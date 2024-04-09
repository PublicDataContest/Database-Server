package com.example.publicdataserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GoogleApiUtils {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GoogleApiUtils(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://places.googleapis.com").build();
        this.objectMapper = objectMapper;
    }

    public Mono<JsonNode> searchPlacesByText(String query) {
        String requestBody = "{\"textQuery\": \"" + query + "\"}";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/places:searchText")
                        .queryParam("languageCode", "ko")
                        .build())
                .header("X-Goog-Api-Key", "AIzaSyAIlAcqTIwnp9YOoygz3VsHYfNR578HsUY")
                .header("X-Goog-FieldMask", "places.formattedAddress")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorMap(error -> new RuntimeException("Failed to retrieve data from Google Places API", error));
    }
}
