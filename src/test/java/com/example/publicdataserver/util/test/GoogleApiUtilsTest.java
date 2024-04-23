package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private GooglePlaceIdApiUtils target;

    @Test
    @DisplayName("API_TEST")
    public void API_TEST() throws JsonProcessingException {
        // given
        String textQuery = "세종원";

        // when
        JsonNode jsonNode = target.getGooglePlaceIdInfoDataSync(textQuery);
        GoogleApiDto dto = target.convertJsonToGoogleApiDto(jsonNode); // DTO 변환
        GoogleApiDto.Place place = dto.getFirstPlace(); // 첫 번째 Place 추출

        // then
        log.info("jsonNode = {}", jsonNode.toString());
        if (place != null) {
            log.info("googleApiDto = {}", place.toString());
        } else {
            log.error("No place found in the response");
        }
    }

}
