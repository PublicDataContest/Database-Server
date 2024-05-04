package com.example.publicdataserver.service;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.restaurant.Category;
import com.example.publicdataserver.domain.restaurant.Menu;
import com.example.publicdataserver.domain.restaurant.Restaurant;
import com.example.publicdataserver.domain.review.KakaoReviews;
import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.repository.*;
import com.example.publicdataserver.service.statistics.CostStatisticsService;
import com.example.publicdataserver.service.statistics.PeopleStatisticsService;
import com.example.publicdataserver.service.statistics.SeasonStatisticsService;
import com.example.publicdataserver.service.statistics.TimeStatisticsService;
import com.example.publicdataserver.util.GooglePlaceApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final GooglePlaceApiUtils googlePlaceApiUtils;
    private final KakaoPlaceApiUtils kakaoPlaceApiUtils;

    private final PublicDataRepository publicDataRepository;
    private final RestaurantRepository restaurantRepository;
    private final KakaoReviewsRepository kakaoReviewsRepository;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    private final SeasonStatisticsService seasonStatisticsService;
    private final PeopleStatisticsService peopleStatisticsService;
    private final CostStatisticsService costStatisticsService;
    private final TimeStatisticsService timeStatisticsService;

    @Value("${google.authKey}")
    private String authkey;

    private static final String API_URL = "https://place.map.kakao.com/main/v/";
    private static final String PHOTO_URL = "https://places.googleapis.com/v1/";

    public void saveData() throws IOException {
        List<PublicData> publicDatas = publicDataRepository.findAll();
        Set<String> locations = new HashSet<>();

        for (PublicData publicData : publicDatas) locations.add(publicData.getExecLoc());

        for (String textQuery : locations) {
            try {
                // Google 데이터 추출
                JsonNode googlePlaceJsonNode = googlePlaceApiUtils.getGooglePlaceIdInfoDataSync(textQuery);
                GoogleApiDto googleApiDto = googlePlaceApiUtils.convertJsonToGoogleApiDto(googlePlaceJsonNode);
                GoogleApiDto.Place googlePlaceDetails = googleApiDto.getFirstPlace();
                if (googlePlaceDetails == null) {
                    log.error("No place found in the response");
                    continue;
                }
                if (googlePlaceDetails.getRating() == null) {
                    log.error("Rating is NULL");
                    continue;
                }

                // Kakao 데이터 추출
                KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails = kakaoPlaceApiUtils.getKakaoPlaceDetailsDto(
                        googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getDisplayName().getName()
                );
                if (kakaoPlaceDetails == null) {
                    log.error("Kakao Place Details Load Fail for location: " + googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getDisplayName().getName());
                    continue;
                }

                // Restaurant 저장
                Restaurant restaurant = saveRestaurant(textQuery, kakaoPlaceDetails, googlePlaceDetails);

                // Google Review 저장
                saveKakaoReviews(kakaoPlaceDetails, restaurant);

                // Menu, Category 저장
                saveMenuAndCategory(kakaoPlaceDetails, restaurant);

                // Statistics 저장
                seasonStatisticsService.updateSeasonStatistics(textQuery, restaurant.getId());
                log.info("+++++++++++++++++++");

                peopleStatisticsService.updatePeopleStatistics(textQuery, restaurant.getId());
                log.info("+++++++++++++++++++");

                costStatisticsService.updateCostStatistics(textQuery, restaurant.getId());

                timeStatisticsService.updateTimeStatistics(textQuery, restaurant.getId());
            } catch (Exception e) {
                log.error("Last Exception Occurs");
            }
        }
    }

    @Transactional
    public Restaurant saveRestaurant(String location,
                                     KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails,
                                     GoogleApiDto.Place googlePlaceDetails) {
        StringBuilder openingHours = new StringBuilder();

        // getCurrentOpeningHours() 생성
        if (googlePlaceDetails.getCurrentOpeningHours() != null && googlePlaceDetails.getCurrentOpeningHours().getWeekdayText() != null) {
            for (String openingHour : googlePlaceDetails.getCurrentOpeningHours().getWeekdayText()) {
                if (openingHours.length() > 0) openingHours.append("\n"); // 줄바꿈 추가
                openingHours.append(openingHour);
            }
        } else {
            openingHours.append("영업 시간 정보가 없습니다.");
        }

        // 식당 이미지 URL 생성
        String photoUrl = "";
        if (googlePlaceDetails.getPhoto() != null) {
            photoUrl = PHOTO_URL + googlePlaceDetails.getPhoto().getPhotoName() +
                    "/media?key=" + authkey + "&maxHeightPx=1000";
        }

        // 총 매출 수 계산
        Long execAmount = publicDataRepository.sumExecAmountByExecLoc(location).longValue();
        // 총 방문 횟수 계산
        Long numberOfVisit = publicDataRepository.countByExecLoc(location);

        Restaurant restaurant = Restaurant.builder()
                .execLoc(location)
                .addressName(kakaoPlaceDetails.getAddressName())
                .phone(kakaoPlaceDetails.getPhone())
                .placeName(kakaoPlaceDetails.getPlaceName())
                .placeUrl(kakaoPlaceDetails.getPlaceUrl())
                .x(kakaoPlaceDetails.getX())
                .y(kakaoPlaceDetails.getY())
                .storeId(kakaoPlaceDetails.getStoreId())
                .photoUrl(photoUrl)
                .rating(googlePlaceDetails.getRating())
                .currentOpeningHours(openingHours.toString())
                .longText(googlePlaceDetails.getLongText())
                .totalExecAmounts(execAmount)
                .numberOfVisit(numberOfVisit)
                .priceModel(false)
                .build();

        return restaurantRepository.save(restaurant);
    }

    // 방문 순서 List 반환 Method
    private List<Restaurant> getRestaurantsWithMatchingLocation() {
        List<PublicData> publicDataList = publicDataRepository.findAllByOrderByExecDtDesc();
        List<Restaurant> matchedRestaurants = new ArrayList<>();

        publicDataList.forEach(pd -> {
            List<Restaurant> restaurants = restaurantRepository.findRestaurantsByExecLoc(pd.getExecLoc());
            matchedRestaurants.addAll(restaurants);
        });

        return matchedRestaurants.stream().distinct().collect(Collectors.toList());
    }

    @Transactional
    public void saveKakaoReviews(KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails,
                                 Restaurant restaurant) throws IOException {
        Long storeId = Long.valueOf(kakaoPlaceDetails.getStoreId());

        Connection connect = Jsoup.connect(API_URL + storeId)
                .header("Content-Type", "application/json")
                .header("charset", "UTF-8")
                .ignoreContentType(true);

        String json = connect.execute().body();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject commentData = jsonObject.getAsJsonObject("comment");

        commentData.getAsJsonArray("list").forEach(item -> {
            JsonObject reviews = item.getAsJsonObject();
            String contents = reviews.get("contents").getAsString();
            String point = reviews.get("point").getAsString();
            String username = reviews.get("username").getAsString();
            String date = reviews.get("date").getAsString();

            JsonArray photoList = reviews.get("photoList").getAsJsonArray();
            String photoUrl = "";
            if (photoList.size() > 0) {
                photoUrl = photoList.get(0).getAsJsonObject().get("url").getAsString();
            }

            KakaoReviews kakaoReviews = KakaoReviews.builder()
                    .authorName(username)
                    .relativeTimeDescription(date)
                    .rating(Double.parseDouble(point))
                    .text(contents)
                    .photoUrl(photoUrl)
                    .restaurant(restaurant)
                    .build();

            kakaoReviewsRepository.save(kakaoReviews);
        });
    }

    @Transactional
    public void saveMenuAndCategory(KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetailsDto,
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
        for (char categoryArray : categoryName.toCharArray()) {
            if (categoryArray == '>') sb.append("#");
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

    @Transactional
    public void updateRestaurantPriceModel(List<String> telNos) {
        restaurantRepository.updateRestaurantPriceModelInBatch(telNos);

        // 모든 레스토랑 가져오기
        List<Restaurant> restaurants = restaurantRepository.findAll();

        // 전체 레스토랑 중 1/3 랜덤하게 선택하기
        Collections.shuffle(restaurants);
        int selectedCount = restaurants.size() / 5;
        List<Restaurant> selectedRestaurants = restaurants.subList(0, selectedCount);

        // 선택된 레스토랑들의 전화번호 가져오기
        List<String> phoneNumbers = selectedRestaurants.stream()
                .map(Restaurant::getPhone)
                .collect(Collectors.toList());

        // 선택된 레스토랑들의 priceModel 값을 true로 업데이트
        restaurantRepository.updateRestaurantPriceModelInBatch(phoneNumbers);
    }
}