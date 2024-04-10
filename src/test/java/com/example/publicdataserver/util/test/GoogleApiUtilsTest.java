package com.example.publicdataserver.util.test;

import com.example.publicdataserver.util.GoogleApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
public class GoogleApiUtilsTest {

    @Autowired
    private GoogleApiUtils googleApiUtils;

    @Test
    @DisplayName("Google Places API 텍스트 검색 테스트")
    public void testSearchPlacesByText() {
        // given
        String query = "서궁(종로구 새문안로 35-20)";

        // when
        Mono<JsonNode> resultMono = googleApiUtils.searchPlacesByText(query);
        JsonNode result = resultMono.block(); // Mono를 동기적으로 처리

        // then
        log.info("Result: {}", result.toString());
    }
}
