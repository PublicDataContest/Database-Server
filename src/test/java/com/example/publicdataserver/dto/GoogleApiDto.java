package com.example.publicdataserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

        @JsonProperty("current_opening_hours")
        private CurrentOpeningHours currentOpeningHours;

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

        @Getter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CurrentOpeningHours {
            @JsonProperty("open_now")
            private Boolean openNow;

            @JsonProperty("weekday_text")
            private List<String> weekdayText;
        }
    }
}
