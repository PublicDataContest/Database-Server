package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class GooglePlaceDetailsApiUtils {
    @Value("${google.authKey}")
    private String authKey;

    /**
     * @param placeId (가게 고유 ID 값)
     * @return GoogleApiDto.GooglePlaceDetailsDto(이름, 주소, 평점, 리뷰 반환)
     */
    public GoogleApiDto.GooglePlaceDetailsDto getGooglePlaceDetailsInfo(String placeId) {
        try {
            String response = getGooglePlaceInfoDataSync(placeId);
            return parseGooglePlaceDetailsDto(response);
        } catch (Exception e) {
            return null;  // 예외 처리
        }
    }

    public String getGooglePlaceInfoDataSync(String placeId) {
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/details/json")
                .queryParam("place_id", placeId)
                .queryParam("language", "ko")
                .queryParam("fields", "name,formatted_address,rating,reviews,current_opening_hours")
                .queryParam("key", authKey)
                .build()
                .encode();

        return webClient.get()
                .uri(uriComponents.toUri())
                .retrieve()
                .bodyToMono(String.class)
                .block();
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

    private GoogleApiDto.GooglePlaceDetailsDto parseGooglePlaceDetailsDto(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode resultNode = root.path("result");
            if (resultNode.isMissingNode()) {
                log.error("Result node is missing in the response");
                return null;
            }
            return objectMapper.treeToValue(resultNode, GoogleApiDto.GooglePlaceDetailsDto.class);
        } catch (IOException e) {
            log.error("Error parsing Google Place Details: {}", e.getMessage());
            return null;
        }
    }
}