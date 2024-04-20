package com.example.publicdataserver.service;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.restaurant.Category;
import com.example.publicdataserver.domain.restaurant.Menu;
import com.example.publicdataserver.domain.restaurant.Restaurant;
import com.example.publicdataserver.domain.review.GoogleReviews;
import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.repository.*;
import com.example.publicdataserver.service.Statistics.CostStatisticsService;
import com.example.publicdataserver.service.Statistics.PeopleStatisticsService;
import com.example.publicdataserver.service.Statistics.SeasonStatisticsService;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final GooglePlaceIdApiUtils googlePlaceIdApiUtils;
    private final GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils;
    private final KakaoPlaceApiUtils kakaoPlaceApiUtils;

    private final PublicDataRepository publicDataRepository;
    private final RestaurantRepository restaurantRepository;
    private final GoogleReviewsRepository googleReviewsRepository;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    private final SeasonStatisticsService seasonStatisticsService;
    private final PeopleStatisticsService peopleStatisticsService;
    private final CostStatisticsService costStatisticsService;

    private static final String API_URL = "https://place.map.kakao.com/main/v/";

    public void saveData() throws IOException {
        List<PublicData> publicDatas = publicDataRepository.findAll();
        Set<String> locations = new HashSet<>();

        for(PublicData publicData : publicDatas) locations.add(publicData.getExecLoc());

        for(String location : locations) {
            try {
                // PlaceId 값 추출
                String placeId = googlePlaceIdApiUtils.getPlaceId(location);
                if (placeId == null) {
                    log.info("Google Place Id Load Fail: " + location);
                    continue;
                }

                // Google Data 값 추출
                GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails
                        = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);
                if (googlePlaceDetails == null) {
                    log.info("Google Place Details Load Fail for location: " + placeId);
                    continue;
                }

                // Kakao 데이터 추출
                KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails = kakaoPlaceApiUtils.getKakaoPlaceDetailsDto(
                        googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getName()
                );
                if (kakaoPlaceDetails == null) {
                    log.info("Kakao Place Details Load Fail for location: " + googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getName());
                    continue;
                }

                // Restaurant 저장
                Restaurant restaurant = saveRestaurant(location, kakaoPlaceDetails, googlePlaceDetails);

                // Google Review 저장
                saveGoogleReviews(googlePlaceDetails, restaurant);

                // Menu, Category 저장
                saveMenuAndCategory(kakaoPlaceDetails, restaurant);

                // Statistics 저장
                seasonStatisticsService.updateSeasonStatistics(location, restaurant.getId());
                log.info("+++++++++++++++++++");

                peopleStatisticsService.updatePeopleStatistics(location, restaurant.getId());
                log.info("+++++++++++++++++++");

                costStatisticsService.updateCostStatistics(location, restaurant.getId());

            } catch (Exception e) {
                log.info("Last Exception Occurs");
            }
        }
    }


    private Restaurant saveRestaurant(String location, KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails,
                                      GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails) {
        StringBuilder openingHours = new StringBuilder();

        // getCurrentOpeningHours()의 결과가 null인지 확인
        if (googlePlaceDetails.getCurrentOpeningHours() != null && googlePlaceDetails.getCurrentOpeningHours().getWeekdayText() != null) {
            for (String openingHour : googlePlaceDetails.getCurrentOpeningHours().getWeekdayText()) {
                if (openingHours.length() > 0) openingHours.append("\n"); // 줄바꿈 추가
                openingHours.append(openingHour);
            }
        } else {
            openingHours.append("영업 시간 정보가 없습니다.");
        }

        Restaurant restaurant = Restaurant.builder()
                .execLoc(location)
                .addressName(kakaoPlaceDetails.getAddressName())
                .phone(kakaoPlaceDetails.getPhone())
                .placeName(kakaoPlaceDetails.getPlaceName())
                .placeUrl(kakaoPlaceDetails.getPlaceUrl())
                .x(kakaoPlaceDetails.getX())
                .y(kakaoPlaceDetails.getY())
                .storeId(kakaoPlaceDetails.getStoreId())
                .rating(googlePlaceDetails.getRating())
                .currentOpeningHours(openingHours.toString())
                .build();

        return restaurantRepository.save(restaurant);
    }

    private void saveGoogleReviews(GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails,
                                   Restaurant restaurant) {
        List<GoogleApiDto.GooglePlaceDetailsDto.ReviewsInfo> reviews = googlePlaceDetails.getReviews();
        if (reviews != null) {
            for (GoogleApiDto.GooglePlaceDetailsDto.ReviewsInfo review : reviews) {
                GoogleReviews reviewEntity = GoogleReviews.builder()
                        .authorName(review.getAuthorName())
                        .rating(review.getRating())
                        .relativeTimeDescription(review.getRelativeTimeDescription())
                        .text(review.getText())
                        .restaurant(restaurant)
                        .build();
                googleReviewsRepository.save(reviewEntity);
            }
        }
    }

    private void saveMenuAndCategory(KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetailsDto,
                                     Restaurant restaurant) throws IOException {
        Long storeId = Long.parseLong(kakaoPlaceDetailsDto.getStoreId());

        Connection connect = Jsoup.connect(API_URL + storeId)
                .header("Content-Type", "application/json")
                .header("charset", "UTF-8")
                .ignoreContentType(true);

        String json = connect.execute().body();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject menuInfo = jsonObject.getAsJsonObject("menuInfo");

        menuInfo.getAsJsonArray("menuList").forEach(item -> {
            JsonObject menuList = item.getAsJsonObject();
            String menuName = menuList.get("menu").getAsString();
            String menuPrice = menuList.get("price").getAsString();

            Menu menu = Menu.builder()
                    .menu(menuName)
                    .price(menuPrice)
                    .restaurant(restaurant)
                    .build();

            menuRepository.save(menu);
        });

        JsonObject categoryInfo = jsonObject.getAsJsonObject("basicInfo");
        JsonArray tags = categoryInfo.getAsJsonArray("tags");

        StringBuilder sb = new StringBuilder();
        String categoryName = kakaoPlaceDetailsDto.getCategoryName();
        sb.append("# ");
        for(char categoryArray : categoryName.toCharArray()) {
            if(categoryArray == '>') sb.append("#");
            else sb.append(categoryArray);
        }

        tags.forEach(tag -> {
            Category category = Category.builder()
                    .categoryGroupName(kakaoPlaceDetailsDto.getCategoryGroupName())
                    .categoryName(String.valueOf(tag))
                    .hashTags(sb.toString())
                    .restaurant(restaurant)
                    .build();

            categoryRepository.save(category);
        });
    }
}