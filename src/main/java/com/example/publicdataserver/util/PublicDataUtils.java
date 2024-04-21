package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.PublicDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PublicDataUtils {
    @Value("${public.official.authKey}")
    private String authkey;

    private final WebClient webClient;

    public PublicDataUtils(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .build();
    }

    public Mono<List<PublicDataDto>> getPublicDataAsDtoListAsync(int start, int end) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{KEY}/json/odExpense/{START_INDEX}/{END_INDEX}")
                        .build(authkey, start, end))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseJsonToDtoList);
    }

    private Mono<List<PublicDataDto>> parseJsonToDtoList(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode != null && rootNode.has("odExpense")) {
                JsonNode arrayNode = rootNode.get("odExpense").get("row");
                if (arrayNode.isArray()) {
                    List<PublicDataDto> dtos = new ArrayList<>();
                    for (JsonNode node : arrayNode) {
                        PublicDataDto dto = objectMapper.treeToValue(node, PublicDataDto.class);
                        dtos.add(dto);
                    }
                    return Mono.just(dtos);
                }
            }
            return Mono.just(Collections.emptyList());
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
