package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CombinedApiUtilsTest {

    @Autowired
    private GooglePlaceIdApiUtils googlePlaceIdApiUtils;

    @Autowired
    private GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils;

    @Autowired
    private KakaoPlaceApiUtils kakaoApiUtils;

    @Test
    @DisplayName("Google Places API와 카카오 API 연동 테스트")
    public void testCombinedApis() {
        // Google Places API 사용
        String googleQuery = "평가옥(종로구 우정국로 2)";
        JsonNode googleResult = googlePlaceIdApiUtils.getGooglePlaceIdInfoDataSync(googleQuery);
        log.info("Google Result: {}", googleResult.toString());

        // Google 결과에서 formattedAddress 추출
        String formattedAddress = googleResult.get("places").get(0).get("formatted_address").asText();

        // 추출된 주소를 카카오 API에 사용
        JsonNode kakaoResult = kakaoApiUtils.parseJson(kakaoApiUtils.getKakaoDataSync(formattedAddress));
        log.info("Kakao Result: {}", kakaoResult.toString());
    }

    @Test
    @DisplayName("Google_Place_카카오_API_연동")
    public void Google_Place_카카오_API_연동() {
        // given
        String query = "평가옥(종로구 우정국로 2)";
        JsonNode placeIdNode = googlePlaceIdApiUtils.getGooglePlaceIdInfoDataSync(query);
        String placeId = placeIdNode.get("places").get(0).get("id").asText();
        JsonNode result = googlePlaceDetailsApiUtils.parseJson(googlePlaceDetailsApiUtils.getGooglePlaceInfoDataSync(placeId));
        String formattedAddress = result.get("result").get("formatted_address").asText();
        String name = result.get("result").get("name").asText();

        // when
        JsonNode kakaoDataSync = kakaoApiUtils.parseJson(kakaoApiUtils.getKakaoDataSync(formattedAddress + " " + name));

        // then
        log.info("result = {}", kakaoDataSync.toString());
    }

    @Test
    @DisplayName("Google_Id_Place_Details_API_각각_출력")
    public void Google_Id_Place_Details_API_각각_출력() {
        // given
        String location = "송옥(중구 남대문로1길 11)";

        // when
        String placeId = googlePlaceIdApiUtils.getPlaceId(location);
        GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);

        // then
        log.info("Place Details = {}", googlePlaceDetails.toString());
    }

    @Test
    @DisplayName("Google_Kakao_전체_연동_테스트")
    public void Google_Kakao_전체_연동_테스트() {
        // given
        String location = "송옥(중구 남대문로1길 11)";
        String placeId = googlePlaceIdApiUtils.getPlaceId(location);
        GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetailsInfo = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);

        // when
        KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetailsDto = kakaoApiUtils.getKakaoPlaceDetailsDto(
                googlePlaceDetailsInfo.getFormattedAddress() + " " + googlePlaceDetailsInfo.getName()
        );

        // then
        log.info("KakaoPlaceDetailsDto = {}", kakaoPlaceDetailsDto.toString());
    }
}

