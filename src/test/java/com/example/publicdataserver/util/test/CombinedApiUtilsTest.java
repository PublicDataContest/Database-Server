package com.example.publicdataserver.util.test;

import com.example.publicdataserver.util.GoogleApiUtils;
import com.example.publicdataserver.util.KakaoApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
public class CombinedApiUtilsTest {

    @Autowired
    private GoogleApiUtils googleApiUtils;

    @Autowired
    private KakaoApiUtils kakaoApiUtils;

    @Test
    @DisplayName("Google Places API와 카카오 API 연동 테스트")
    public void testCombinedApis() {
        // Google Places API 사용
        String googleQuery = "서궁(종로구 새문안로 35-20)";
        Mono<JsonNode> googleResultMono = googleApiUtils.searchPlacesByText(googleQuery);
        JsonNode googleResult = googleResultMono.block();
        log.info("Google Result: {}", googleResult.toString());

        // Google 결과에서 formattedAddress 추출
        String formattedAddress = googleResult.get("places").get(0).get("formattedAddress").asText();

        // 추출된 주소를 카카오 API에 사용
        JsonNode kakaoResult = kakaoApiUtils.getKakaoDataSync(formattedAddress);
        log.info("Kakao Result: {}", kakaoResult.toString());
    }
}

