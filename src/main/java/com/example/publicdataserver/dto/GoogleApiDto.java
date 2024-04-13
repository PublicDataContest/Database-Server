package com.example.publicdataserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class GoogleApiDto {

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GooglePlaceDetailsDto {
        @JsonProperty("formatted_address")
        private String formattedAddress;

        @JsonProperty("name")
        private String name;

        @JsonProperty("rating")
        private Integer rating;

        private List<ReviewsInfo> reviews;

        @Getter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ReviewsInfo {
            @JsonProperty("author_name")
            private String authorName;

            @JsonProperty("profile_photo_url")
            private String profilePhotoUrl;

            @JsonProperty("rating")
            private Integer rating;

            @JsonProperty("relative_time_description")
            private String relativeTimeDescription;

            @JsonProperty("text")
            private String text;
        }
    }
}
