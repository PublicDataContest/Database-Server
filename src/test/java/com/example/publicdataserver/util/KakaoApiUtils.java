package com.example.publicdataserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;

@Component
public class KakaoApiUtils {
    private String authkey = "a892136411c8cdede160a2c3472a706d";

    WebClient webClient;

    public JsonNode getKakaoDataSync(String execLoc) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB로 설정
                .build();

        String responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", execLoc)
                        .build())
                .header("Authorization", "KakaoAK a892136411c8cdede160a2c3472a706d")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기적으로 결과를 얻음
        return parseJson(responseBody);
    }

    private JsonNode parseJson(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
