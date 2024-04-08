package com.example.publicdataserver.util;

import com.example.publicdataserver.dto.PublicDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PublicDataUtils {
    private String authkey = "564c555565706a6834327543555661";

    WebClient webClient;

    public JsonNode getPublicDataSync(int start, int end) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB로 설정
                .build();

        String responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("openapi.seoul.go.kr")
                        .port(8088)
                        .path("/{KEY}/json/odExpense/{START_INDEX}/{END_INDEX}")
                        .build(authkey, start, end))
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

    public List<PublicDataDto> getPublicDataAsDtoList(int start, int end) {
        JsonNode jsonNode = getPublicDataSync(start, end);

        if (jsonNode != null && jsonNode.has("odExpense")) {
            JsonNode arrayNode = jsonNode.get("odExpense").get("row");
            if (arrayNode.isArray()) {
                List<PublicDataDto> publicDataDtoList = new ArrayList<>();

                for (JsonNode node : arrayNode) {
                    PublicDataDto publicDataDto = convertJsonToPublicDto(node);
                    publicDataDtoList.add(publicDataDto);
                }

                return publicDataDtoList;
            }
        }

        return Collections.emptyList();
    }

    private PublicDataDto convertJsonToPublicDto(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, PublicDataDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
