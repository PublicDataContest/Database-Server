package com.example.publicdataserver.util.test;

import com.example.publicdataserver.util.KakaoApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KakaoApiUtilsTest {
    @InjectMocks
    private KakaoApiUtils target;

    @Test
    @DisplayName("카카오_API_연동_테스트")
    public void 카카오_API_연동_테스트() {
        // given

        // when
        final JsonNode result = target.getKakaoDataSync("");

        // then
        log.info("result = {}", result.toString());
    }
}
