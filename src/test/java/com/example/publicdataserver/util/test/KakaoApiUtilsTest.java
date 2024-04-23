package com.example.publicdataserver.util.test;

import com.example.publicdataserver.domain.restaurant.Menu;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class KakaoApiUtilsTest {
    @Autowired
    private KakaoPlaceApiUtils kakaoPlaceApiUtils;


    private static final String API_URL = "https://place.map.kakao.com/main/v/";

    @Test
    @DisplayName("카카오_리뷰_크롤링")
    public void 카카오_리뷰_크롤링() throws IOException {
        // given

        // when
        Long storeId = 10553650L;

        Connection connect = Jsoup.connect(API_URL + storeId)
                .header("Content-Type", "application/json")
                .header("charset", "UTF-8")
                .ignoreContentType(true);

        String json = connect.execute().body();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject commentData = jsonObject.getAsJsonObject("comment");

        commentData.getAsJsonArray("list").forEach(item -> {
            JsonObject reviews = item.getAsJsonObject();
            String contents = reviews.get("contents").getAsString();
            String point = reviews.get("point").getAsString();
            String username = reviews.get("username").getAsString();
            String date = reviews.get("date").getAsString();

            JsonArray photoList = reviews.get("photoList").getAsJsonArray();
            String photoUrl = "";
            if (photoList.size() > 0) {
                photoUrl = photoList.get(0).getAsJsonObject().get("url").getAsString();
            }

            log.info("contents = {}", contents);
            log.info("point = {}", point);
            log.info("username = {}", username);
            log.info("photoUrl = {}", photoUrl);
            log.info("date = {}", date);
        });

    }
}
