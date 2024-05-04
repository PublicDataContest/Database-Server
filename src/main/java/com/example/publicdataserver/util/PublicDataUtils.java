package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.PriceModelDto;
import com.example.publicdataserver.dto.PublicDataDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
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
                .flatMap(this::parsePublicDataJsonToDtoList);
    }

    public Mono<List<PriceModelDto>> getPriceModelDtoListAsync(int start, int end) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{key}/json/ListPriceModelStoreService/{START_INDEX}/{END_INDEX}")
                        .build(authkey, start, end))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parsePriceModelJsonToDtoList);
    }

    private Mono<List<PublicDataDto>> parsePublicDataJsonToDtoList(String responseBody) {
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

    private Mono<List<PriceModelDto>> parsePriceModelJsonToDtoList(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if(rootNode != null && rootNode.has("ListPriceModelStoreService")) {
                JsonNode arrayNode = rootNode.get("ListPriceModelStoreService").get("row");
                if(arrayNode.isArray()) {
                    List<PriceModelDto> dtos = new ArrayList<>();
                    for(JsonNode node : arrayNode) {
                        PriceModelDto dto = objectMapper.treeToValue(node, PriceModelDto.class);
                        dtos.add(dto);
                        log.info("tel = {}", dto.getSH_PHONE());
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