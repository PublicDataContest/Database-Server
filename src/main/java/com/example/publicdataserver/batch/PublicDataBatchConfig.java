package com.example.publicdataserver.batch;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.Restaurant;
import com.example.publicdataserver.domain.Reviews;
import com.example.publicdataserver.dto.GoogleApiDto;
import com.example.publicdataserver.dto.KakaoApiDto;
import com.example.publicdataserver.dto.PublicDataDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.RestaurantRepository;
import com.example.publicdataserver.repository.ReviewsRepository;
import com.example.publicdataserver.util.GooglePlaceDetailsApiUtils;
import com.example.publicdataserver.util.GooglePlaceIdApiUtils;
import com.example.publicdataserver.util.KakaoPlaceApiUtils;
import com.example.publicdataserver.util.PublicDataUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PublicDataBatchConfig {
    private final PublicDataUtils publicDataUtils;

    @Bean
    public Job exchangeJob(JobRepository jobRepository,
                           Step step1,
                           Step step2) {
        return new JobBuilder("publicDataJob", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager tm, Tasklet tasklet1) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet1, tm)
                .build();
    }

    @Bean
    public Tasklet tasklet1(PublicDataRepository publicDataRepository) {
        return ((contribution, chunkContext) -> {
            for(int start = 1; start <= 56_000; start += 1000) {
                int end = start + 999;
                end = Math.min(end, 57_000);

                saveData(publicDataUtils.getPublicDataAsDtoList(start, end), publicDataRepository);
            }

            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager tm, Tasklet tasklet2) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(tasklet2, tm)
                .build();
    }

    @Bean
    public Tasklet tasklet2(PublicDataRepository publicDataRepository,
                            RestaurantRepository restaurantRepository,
                            ReviewsRepository reviewsRepository,
                            GooglePlaceIdApiUtils googlePlaceIdApiUtils,
                            GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils,
                            KakaoPlaceApiUtils kakaoPlaceApiUtils) {
        return ((contribution, chunkContext) -> {
            List<PublicData> publicDatas = publicDataRepository.findAll();
            Set<String> locations = new HashSet<>();
            for(PublicData publicData : publicDatas) locations.add(publicData.getExecLoc());
            log.info("locations Size = {}", locations.size());

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for(String location : locations) {
                CompletableFuture<Void> future = processLocationAsync(location, googlePlaceIdApiUtils,
                        googlePlaceDetailsApiUtils, kakaoPlaceApiUtils, restaurantRepository, reviewsRepository);
                futures.add(future);
            }

            // 모든 비동기 작업이 완료될 때까지 기다립니다.
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return RepeatStatus.FINISHED;
        });
    }

    @Async
    public CompletableFuture<Void> processLocationAsync(String location,
                                                        GooglePlaceIdApiUtils googlePlaceIdApiUtils,
                                                        GooglePlaceDetailsApiUtils googlePlaceDetailsApiUtils,
                                                        KakaoPlaceApiUtils kakaoPlaceApiUtils,
                                                        RestaurantRepository restaurantRepository,
                                                        ReviewsRepository reviewsRepository) {
        try {
            String placeId = googlePlaceIdApiUtils.getPlaceId(location);
            if (placeId == null) {
                log.info("Google Place Id Load Fail: " + location);
                return CompletableFuture.completedFuture(null);
            }

            GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails = googlePlaceDetailsApiUtils.getGooglePlaceDetailsInfo(placeId);
            if (googlePlaceDetails == null) {
                log.info("Google Place Details Load Fail for location: " + placeId);
                return CompletableFuture.completedFuture(null);
            }

            KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails = kakaoPlaceApiUtils.getKakaoPlaceDetailsDto(
                    googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getName()
            );

            if (kakaoPlaceDetails == null) {
                log.info("Kakao Place Details Load Fail for location: " + googlePlaceDetails.getFormattedAddress() + " " + googlePlaceDetails.getName());
                return CompletableFuture.completedFuture(null);
            }

            Restaurant restaurant = buildRestaurant(location, kakaoPlaceDetails, googlePlaceDetails);
            restaurantRepository.save(restaurant);

            saveReviewsIfAvailable(googlePlaceDetails, restaurant, reviewsRepository, location);
        } catch (Exception e) {
            log.error("Failed processing for location: " + location, e);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void saveReviewsIfAvailable(GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails,
                                        Restaurant restaurant,
                                        ReviewsRepository reviewsRepository,
                                        String location) {
        List<GoogleApiDto.GooglePlaceDetailsDto.ReviewsInfo> reviews = googlePlaceDetails.getReviews();
        if (reviews != null) {
            for (GoogleApiDto.GooglePlaceDetailsDto.ReviewsInfo review : reviews) {
                Reviews reviewEntity = Reviews.builder()
                        .authorName(review.getAuthorName())
                        .profilePhotoUrl(review.getProfilePhotoUrl())
                        .rating(review.getRating())
                        .relativeTimeDescription(review.getRelativeTimeDescription())
                        .text(review.getText())
                        .restaurant(restaurant)
                        .build();
                reviewsRepository.save(reviewEntity);
            }
        } else {
            log.info("No reviews available for location: " + location);
        }
    }

    private Restaurant buildRestaurant(String location, KakaoApiDto.KakaoPlaceDetailsDto kakaoPlaceDetails,
                                       GoogleApiDto.GooglePlaceDetailsDto googlePlaceDetails) {
        StringBuilder openingHours = new StringBuilder();

        // getCurrentOpeningHours()의 결과가 null인지 확인
        if (googlePlaceDetails.getCurrentOpeningHours() != null && googlePlaceDetails.getCurrentOpeningHours().getWeekdayText() != null) {
            for (String openingHour : googlePlaceDetails.getCurrentOpeningHours().getWeekdayText()) {
                if (openingHours.length() > 0) openingHours.append("\n"); // 줄바꿈 추가
                openingHours.append(openingHour);
            }
        } else {
            openingHours.append("영업 시간 정보가 없습니다.");
        }

        return Restaurant.builder()
                .execLoc(location)
                .addressName(kakaoPlaceDetails.getAddressName())
                .categoryGroupName(kakaoPlaceDetails.getCategoryGroupName())
                .categoryName(kakaoPlaceDetails.getCategoryName())
                .phone(kakaoPlaceDetails.getPhone())
                .placeName(kakaoPlaceDetails.getPlaceName())
                .placeUrl(kakaoPlaceDetails.getPlaceUrl())
                .x(kakaoPlaceDetails.getX())
                .y(kakaoPlaceDetails.getY())
                .rating(googlePlaceDetails.getRating())
                .currentOpeningHours(openingHours.toString())
                .build();
    }


    private void saveData(List<PublicDataDto> publicDataDtoList, PublicDataRepository publicDataRepository) {
        for (PublicDataDto publicDataDto : publicDataDtoList) {
            PublicData publicData = PublicData.builder()
                    .title(publicDataDto.getTitle())
                    .deptNm(publicDataDto.getDeptNm())
                    .url(publicDataDto.getUrl())
                    .execDt(publicDataDto.getExecDt())
                    .execLoc(publicDataDto.getExecLoc())
                    .execPurpose(publicDataDto.getExecPurpose())
                    .execAmount(publicDataDto.getExecAmount())
                    .build();

            publicDataRepository.save(publicData);
        }
    }
}
