package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.PublicDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GooglePlaceIdApiUtils {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GooglePlaceIdApiUtils(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://places.googleapis.com").build();
        this.objectMapper = objectMapper;
    }


    public JsonNode getGooglePlaceIdInfoDataSync(String location) {
        String requestBody = "{\"textQuery\": \"" + location + "\"}";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/places:searchText")
                        .queryParam("languageCode", "ko")
                        .build())
                .header("X-Goog-Api-Key", "AIzaSyAIlAcqTIwnp9YOoygz3VsHYfNR578HsUY")
                .header("X-Goog-FieldMask", "places.formatted_address,places.rating,places.regularOpeningHours.weekdayDescriptions,places.reviews.relativePublishTimeDescription,places.reviews.rating,places.reviews.text.text,places.reviews.authorAttribution.displayName")
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
