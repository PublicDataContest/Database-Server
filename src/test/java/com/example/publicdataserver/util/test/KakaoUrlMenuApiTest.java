package com.example.publicdataserver.util.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class KakaoUrlMenuApiTest {

    private static final Logger log = LoggerFactory.getLogger(KakaoUrlMenuApiTest.class);
    private static final String API_URL = "https://place.map.kakao.com/main/v/";

    @Test
    @DisplayName("카카오 메뉴 정보 추출")
    public void fetchMenuInfo() {
        try {
            Long storeId = 10553650L; // 예시 상점 ID
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

        } catch (Exception e) {
            log.error("Error during API data fetching", e);
        }
    }
}