package com.example.publicdataserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GooglePlaceIdApiUtils {
    @Value("${google.authKey}")
    private String authKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GooglePlaceIdApiUtils(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://places.googleapis.com").build();
        this.objectMapper = objectMapper;
    }

    /**
     * @param location (위치 정보)
     * @return placeId (가게 고유 ID 값)
     */
    public String getPlaceId(String location) {
        return getGooglePlaceIdInfoDataSync(location).get("places").get(0).get("id").asText();
    }

    public JsonNode getGooglePlaceIdInfoDataSync(String location) {
        String requestBody = "{\"textQuery\": \"" + location + "\"}";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/places:searchText")
                        .queryParam("languageCode", "ko")
                        .build())
                .header("X-Goog-Api-Key", authKey)
                .header("X-Goog-FieldMask", "places.id")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorMap(error -> new RuntimeException("Failed to retrieve data from Google Places API", error))
                .block();
    }
}
