package com.example.publicdataserver.util.test;

import com.example.publicdataserver.dto.KakaoApiDto.KakaoPlaceDetailsDto;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class KakaoUrlMenuApiTest {

    private static final Logger log = LoggerFactory.getLogger(KakaoUrlMenuApiTest.class);

    @InjectMocks
    private KakaoPlaceApiUtils kakaoPlaceApiUtils;

    private static final String API_URL = "https://place.map.kakao.com/main/v/";

    @Test
    @DisplayName("카카오 메뉴 정보 추출")
    public void fetchMenuInfo() throws Exception {
        // kakaoplaceapiutils에서 storeid가져오도록
        KakaoPlaceDetailsDto detailsDto = kakaoPlaceApiUtils.getKakaoPlaceDetailsDto("세종원");
        Long storeId = Long.parseLong(detailsDto.getStoreId());
        //Long storeId = 10553650L; // 예시 상점 ID
        Connection connect = Jsoup.connect(API_URL + storeId)
                .header("Content-Type", "application/json")
                .header("charset", "UTF-8")
                .ignoreContentType(true);

        String json = connect.execute().body();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject menuInfo = jsonObject.getAsJsonObject("menuInfo");

        menuInfo.getAsJsonArray("menuList").forEach(item -> {
            JsonObject menu = item.getAsJsonObject();
            String menuName = menu.get("menu").getAsString();
            String menuPrice = menu.get("price").getAsString();

            log.info("Menu: {}, Price: {}", menuName, menuPrice);
        });

        // aInfo 객체에서 tags 정보만 로그 찍기
        JsonObject aInfo = jsonObject.getAsJsonObject("basicInfo");
        JsonArray tags = aInfo.getAsJsonArray("tags");  // 태그 배열 가져오기
        tags.forEach(tag -> {
            log.info("Tag: {}", tag.getAsString());  // 태그 로그 찍기
        });
    }
}
