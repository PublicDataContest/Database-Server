package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GooglePlaceApiUtils {
    @Value("${google.authKey}")
    private String authkey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GooglePlaceApiUtils(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://places.googleapis.com").build();
        this.objectMapper = objectMapper;
    }


    public JsonNode getGooglePlaceIdInfoDataSync(String textQuery) {
        String requestBody = "{\"textQuery\": \"" + textQuery + "\"}";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/places:searchText")
                        .queryParam("languageCode", "ko")
                        .build())
                .header("X-Goog-Api-Key", authkey)
                .header("X-Goog-FieldMask", "places.formatted_address,places.rating,places.regularOpeningHours.weekdayDescriptions,places.reviews.relativePublishTimeDescription,places.reviews.rating,places.reviews.text.text,places.reviews.authorAttribution.displayName,places.reviews.name,places.displayName.text,places.photos.name,places.addressComponents.longText,places.addressComponents.types")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorMap(error -> new RuntimeException("Failed to retrieve data from Google Places API", error))
                .block();
    }

    public GoogleApiDto convertJsonToGoogleApiDto(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, GoogleApiDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
