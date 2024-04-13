package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.KakaoApiDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;

@Component
public class KakaoPlaceApiUtils {
    @Value("${kakao.authKey}")
    private String authkey;

    WebClient webClient;

    public KakaoApiDto.KakaoPlaceDetailsDto getKakaoPlaceDetailsDto(String location) {
        return parseKakaoPlaceDetailsDto(getKakaoDataSync(location));
    }

    public String getKakaoDataSync(String location) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB로 설정
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", location)
                        .build())
                .header("Authorization", "KakaoAK" + " " + authkey)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기적으로 결과를 얻음
    }

    public JsonNode parseJson(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private KakaoApiDto.KakaoPlaceDetailsDto parseKakaoPlaceDetailsDto(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode documentsNode = root.path("documents");

            if (documentsNode.isArray() && documentsNode.size() > 0) {
                // "documents" 배열의 첫 번째 요소를 파싱
                return objectMapper.treeToValue(documentsNode.get(0), KakaoApiDto.KakaoPlaceDetailsDto.class);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
