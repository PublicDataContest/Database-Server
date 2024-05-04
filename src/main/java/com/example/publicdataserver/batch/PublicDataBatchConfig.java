package com.example.publicdataserver.batch;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.dto.PriceModelDto;
import com.example.publicdataserver.dto.PublicDataDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.RestaurantRepository;
import com.example.publicdataserver.service.DatabaseService;
import com.example.publicdataserver.util.PublicDataUtils;
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
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PublicDataBatchConfig {
    private final PublicDataUtils publicDataUtils;
    private final DatabaseService databaseService;

    @Bean
    public Job exchangeJob(JobRepository jobRepository,
                           Step step1,
                           Step step2,
                           Step step3) {
        return new JobBuilder("publicDataJob", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
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
        return (contribution, chunkContext) -> {
            int totalRecords = 10;
            int chunkSize = 1000;
            List<Mono<Void>> tasks = new ArrayList<>();

            for (int start = 1; start <= totalRecords; start += chunkSize) {
                int end = Math.min(start + chunkSize - 1, totalRecords);
                tasks.add(processChunk1(start, end, publicDataRepository));
            }

            // 모든 비동기 작업이 완료될 때까지 대기
            Mono.when(tasks).block();
            return RepeatStatus.FINISHED;
        };
    }

    private Mono<Void> processChunk1(int start, int end, PublicDataRepository publicDataRepository) {
        return publicDataUtils.getPublicDataAsDtoListAsync(start, end)
                .flatMapIterable(Function.identity())
                .flatMap(dto -> savePublicData(dto, publicDataRepository))
                .then();
    }

    private Mono<Void> savePublicData(PublicDataDto dto, PublicDataRepository repository) {
        PublicData publicData = PublicData.builder()
                .deptNm(dto.getDeptNm())
                .execDt(dto.getExecDt())
                .execLoc(dto.getExecLoc())
                .targetNm(dto.getTargetNm())
                .execAmount(dto.getExecAmount())
                .execMonth(dto.getExecMonth())
                .build();
        return Mono.fromRunnable(() -> repository.save(publicData));
    }


    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager tm, Tasklet tasklet2) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(tasklet2, tm)
                .build();
    }

    @Bean
    public Tasklet tasklet2(PublicDataRepository publicDataRepository) throws IOException {
        return ((contribution, chunkContext) -> {
            databaseService.saveData();

            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager tm, Tasklet tasklet3) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(tasklet3, tm)
                .build();
    }

    @Bean
    public Tasklet tasklet3(RestaurantRepository restaurantRepository) throws IOException {
        return ((contribution, chunkContext) -> {
            int totalRecords = 2000;
            int chunkSize = 1000;
            List<Mono<Void>> tasks = new ArrayList<>();

            for (int start = 1; start <= totalRecords; start += chunkSize) {
                int end = Math.min(start + chunkSize - 1, totalRecords);
                tasks.add(processChunk3(start, end, restaurantRepository));
            }

            // 모든 비동기 작업이 완료될 때까지 대기
            Mono.when(tasks).block();
            return RepeatStatus.FINISHED;
        });
    }

    private Mono<Void> processChunk3(int start, int end, RestaurantRepository restaurantRepository) {
        return publicDataUtils.getPriceModelDtoListAsync(start, end)
                .flatMapIterable(Function.identity())
                .collectList()
                .flatMap(dtoList -> {
                    List<String> telNos = dtoList.stream()
                            .map(PriceModelDto::getSH_PHONE)
                            .collect(Collectors.toList());
                    return Mono.fromRunnable(() -> databaseService.updateRestaurantPriceModel(telNos));
                })
                .then();
    }
}
