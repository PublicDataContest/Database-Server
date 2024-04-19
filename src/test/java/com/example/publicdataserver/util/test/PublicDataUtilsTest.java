package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.PublicDataDto;
import com.example.publicdataserver.util.PublicDataUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class PublicDataUtilsTest {
    @InjectMocks
    private PublicDataUtils target;

    @Test
    @DisplayName("공무원_OpenAPI_연동")
    public void 공무원_OpenAPI_연동() {
        // given

        // when
        final JsonNode result = target.getPublicDataSync(1, 10);

        // then
        log.info("result = {}", result.toString());
    }

    @Test
    @DisplayName("공무원_OpenAPI_DTO변환")
    public void 공무원_OpenAPI_DTO변환() {
        // given

        // when
        final List<PublicDataDto> result = target.getPublicDataAsDtoList(1, 1000);

        // then
        assertThat(result.size()).isEqualTo(1000);
    }
}
