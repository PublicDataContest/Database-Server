package com.example.publicdataserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleApiDto {

    @JsonProperty("places")
    private List<Place> places;

    public Place getFirstPlace() {
        if (places != null && !places.isEmpty()) {
            return places.get(0);
        }
        return null;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Place {
        @JsonProperty("formattedAddress")
        private String formattedAddress;

        @JsonProperty("rating")
        private Double rating;

        @JsonProperty("regularOpeningHours")
        private CurrentOpeningHours CurrentOpeningHours;

        @JsonProperty("reviews")
        private List<ReviewsInfo> reviews;

        @JsonProperty("displayName")
        private DisplayName displayName;

        @JsonProperty("photos")
        private Photo photo;

        @JsonSetter("photos")
        public void setFirstPhoto(List<Photo> photos) {
            if (photos != null && !photos.isEmpty()) {
                this.photo = photos.get(0);
            }
        }
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentOpeningHours {
        @JsonProperty("weekdayDescriptions")
        private List<String> weekdayText;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReviewsInfo {
        @JsonProperty("relativePublishTimeDescription")
        private String relativeTimeDescription;

        @JsonProperty("rating")
        private Integer rating;

        @JsonProperty("text")
        private Text text;

        @JsonProperty("authorAttribution")
        private AuthorAttribution authorAttribution;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Text {
        @JsonProperty("text")
        private String text;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorAttribution {
        @JsonProperty("displayName")
        private String authorName;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DisplayName {
        @JsonProperty("text")
        private String name;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        @JsonProperty("name")
        private String photoName;
    }
}
