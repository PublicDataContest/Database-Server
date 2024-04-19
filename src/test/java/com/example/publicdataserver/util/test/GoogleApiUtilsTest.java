package com.example.publicdataserver.util.test;

import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class GoogleApiUtilsTest {

    @Autowired
    private GooglePlaceIdApiUtils googleApiUtils;

    @Test
    @DisplayName("Google Places API 텍스트 검색 테스트")
    public void testSearchPlacesByText() {
        // given
        String query = "라그릴리아광화문점(종로구 청계천로 11)";

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
        String query = "라그릴리아광화문점(종로구 청계천로 11)";
        JsonNode placeIdNode = googleApiUtils.getGooglePlaceIdInfoDataSync(query);
        log.info("PlaceIdNode = {}", placeIdNode.toString());
        String placeId = placeIdNode.get("places").get(0).get("id").asText();
        log.info("PlacesId = {}", placeId);

        // when
        JsonNode result = googlePlaceInfoUtils.parseJson(googlePlaceInfoUtils.getGooglePlaceInfoDataSync(placeId));
        JsonNode ratingNode = result.path("result").path("rating");
        if (!ratingNode.isMissingNode()) {
            int rating = ratingNode.asInt();
            log.info("rating = {}", rating);
        }

        // then
        log.info("Result = {}", result.toString());
    }
}
