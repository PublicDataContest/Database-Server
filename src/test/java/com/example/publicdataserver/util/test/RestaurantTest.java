package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.GoogleApiDto.GooglePlaceDetailsDto;
import com.example.publicdataserver.dto.KakaoApiDto.KakaoPlaceDetailsDto;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
public class RestaurantTest {

    private static final Logger log = LoggerFactory.getLogger(RestaurantTest.class);

    @Autowired
    private GooglePlaceIdApiUtils googlePlaceIdApiUtils;

    @Autowired
    private GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils;

    @Autowired
    private KakaoPlaceApiUtils kakaoApiUtils;

    private static final String KAKAO_API_URL = "https://place.map.kakao.com/main/v/";

    @Test
    @DisplayName("Google과 Kakao API 통합 테스트로 식당 정보, 리뷰 및 메뉴 출력")
    public void testIntegratedApis() throws Exception {
        // given
        String location = "북창갈비(중구 세종대로 14길 17)";
        String placeId = googlePlaceIdApiUtils.getPlaceId(location);

        if (placeId != null) {
            GooglePlaceDetailsDto googleDetails = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);
            logGooglePlaceDetails(googleDetails);

            // when
            KakaoPlaceDetailsDto kakaoDetails = kakaoApiUtils.getKakaoPlaceDetailsDto(
                    googleDetails.getFormattedAddress() + " " + googleDetails.getName()
            );

            logKakaoPlaceDetails(kakaoDetails);

            if (kakaoDetails.getStoreId() != null) {
                fetchAndLogKakaoMenuInfo(kakaoDetails.getStoreId());
            }
        } else {
            log.error("No Google Place ID found for location: {}", location);
        }
    }

    private void logGooglePlaceDetails(GooglePlaceDetailsDto googleDetails) {
        if (googleDetails != null) {
            log.info("Google Place Details: Address: {}, Name: {}, Rating: {}",
                    googleDetails.getFormattedAddress(), googleDetails.getName(), googleDetails.getRating());

            if (googleDetails.getCurrentOpeningHours() != null) {
                GooglePlaceDetailsDto.CurrentOpeningHours hours = googleDetails.getCurrentOpeningHours();
                log.info("Open Now: {}", hours.getOpenNow());
                if (hours.getWeekdayText() != null && !hours.getWeekdayText().isEmpty()) {
                    log.info("Opening Hours:");
                    hours.getWeekdayText().forEach(day -> log.info(day));
                }
            }

            if (googleDetails.getPhotos() != null && !googleDetails.getPhotos().isEmpty()) {
                String photoReference = googleDetails.getPhotos().get(0).getPhotoReference();
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=AIzaSyBTcYS8E3PCQwOyHPDsrk2RM_GcTKsN24c";
                log.info("Photo URL: {}", photoUrl);
            }

            if (googleDetails.getReviews() != null) {
                googleDetails.getReviews().forEach(review ->
                        log.info("Review: Author: {}, Rating: {}, Text: {} ??:{}",
                                review.getAuthorName(), review.getRating(), review.getText(), review.getRelativeTimeDescription()));
            }
        } else {
            log.error("No details found for Google Place");
        }
    }


    private void logKakaoPlaceDetails(KakaoPlaceDetailsDto kakaoDetails) {
        if (kakaoDetails != null) {
            log.info("Kakao Place Details:");
            log.info("Address: {}", kakaoDetails.getAddressName());
            log.info("Category Group Name: {}", kakaoDetails.getCategoryGroupName());
            log.info("Category Name: {}", kakaoDetails.getCategoryName());
            log.info("Phone: {}", kakaoDetails.getPhone());
            log.info("Place Name: {}", kakaoDetails.getPlaceName());
            log.info("Place URL: {}", kakaoDetails.getPlaceUrl());
            log.info("Coordinates: X = {}, Y = {}", kakaoDetails.getX(), kakaoDetails.getY());
            log.info("Store ID: {}", kakaoDetails.getStoreId());
        } else {
            log.error("장소 없음");
        }
    }

    private void fetchAndLogKakaoMenuInfo(String storeId) throws Exception {
        Connection connect = Jsoup.connect(KAKAO_API_URL + storeId)
                .header("Content-Type", "application/json")
                .header("charset", "UTF-8")
                .ignoreContentType(true);
        String json = connect.execute().body();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject menuInfo = jsonObject.getAsJsonObject("menuInfo");
        JsonArray menuList = menuInfo.getAsJsonArray("menuList");

        menuList.forEach(item -> {
            JsonObject menu = (JsonObject) item;
            String menuName = menu.get("menu").getAsString();
            String menuPrice = menu.get("price").getAsString();
            log.info("메뉴: {}, 가격: {}", menuName, menuPrice);
        });
    }
}
