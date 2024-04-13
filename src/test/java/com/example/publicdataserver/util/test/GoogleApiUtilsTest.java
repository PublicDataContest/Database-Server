package com.example.publicdataserver.util.test;

import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class GoogleApiUtilsTest {

    @Autowired
    private GooglePlaceIdApiUtils googleApiUtils;

    @Test
    @DisplayName("Google Places API 텍스트 검색 테스트")
    public void testSearchPlacesByText() {
        // given
        String query = "서궁(종로구 새문안로 35-20)";

        // when
        JsonNode result = googleApiUtils.getGooglePlaceIdInfoDataSync(query);

        // then
        String placeId = result.get("places").get(0).get("id").asText();
        log.info("Places = {}", placeId);
    }

    @Autowired
    private GooglePlaceDetailsApiUtils googlePlaceInfoUtils;

    @Test
    @DisplayName("Google_장소_ID_기반_장소_검색")
    public void Google_장소_ID_기반_장소_검색() {
        // given
        String query = "송옥(중구 남대문로1길 11)";
        JsonNode placeIdNode = googleApiUtils.getGooglePlaceIdInfoDataSync(query);
        log.info("PlaceIdNode = {}", placeIdNode.toString());
        String placeId = placeIdNode.get("places").get(0).get("id").asText();
        log.info("Places = {}", placeId);

        // when
        JsonNode result = googlePlaceInfoUtils.parseJson(googlePlaceInfoUtils.getGooglePlaceInfoDataSync(placeId));

        // then
        log.info("Result = {}", result.toString());
    }
}
