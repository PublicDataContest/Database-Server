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
        final JsonNode result = target.getKakaoDataSync("진짜무릎도가니탕푸주옥 서울 영등포구 대림로 171");

        // then
        log.info("result = {}", result.toString());
    }
}
