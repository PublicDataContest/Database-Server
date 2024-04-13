package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ApiTest {
    @Autowired
    private GooglePlaceIdApiUtils googlePlaceIdApiUtils;

    @Autowired
    private GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils;

    @Autowired
    private KakaoPlaceApiUtils kakaoPlaceApiUtils;

    @Test
    @DisplayName("GooglePlaceAPI_연동_테스트_출력값_확인")
    public void GooglePlaceAPI_연동_테스트_출력값_확인() {
        // given
        String location = "평가옥(종로구 우정국로 2)";

        // when
        JsonNode result = googlePlaceIdApiUtils.getGooglePlaceIdInfoDataSync(location);
        String placeId = googlePlaceIdApiUtils.getPlaceId(location);

        // then
        log.info("result = {}", result.toString());
        log.info("placeId = {}", placeId.toString());
    }

    @Test
    @DisplayName("GooglePlaceDetailsAPI_연동_테스트_출력값_확인")
    public void GooglePlaceDetailsAPI_연동_테스트_출력값_확인() {
        // given
        String placeId = "ChIJL_KvmqyjfDURnjAdGipEauY";

        // when
        GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetailsInfo
                = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);
        JsonNode result
                = googlePlaceDetailsApiUtils.parseJson(googlePlaceDetailsApiUtils.getGooglePlaceInfoDataSync(placeId));

        // then
        log.info("DTO = {}", googlePlaceDetailsInfo.toString());
        log.info("Review List = {}", googlePlaceDetailsInfo.getReviews().size());
        log.info("result = {}", result.toString());
    }

    @Test
    @DisplayName("KakaoPlaceAPI_연동_테스트_출력값_확인")
    public void KakaoPlaceAPI_연동_테스트_출력값_확인() {
        // given
        String location = "대한민국 서울특별시 중구 남대문로1길 11 송옥";

        // when
        JsonNode result = kakaoPlaceApiUtils.parseJson(kakaoPlaceApiUtils.getKakaoDataSync(location));
        KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetailsDto = kakaoPlaceApiUtils.getKakaoPlaceDetailsDto(location);

        // then
        log.info("result = {}", result.toString());
        log.info("DTO = {}", kakaoPlaceDetailsDto.toString());
    }
}
