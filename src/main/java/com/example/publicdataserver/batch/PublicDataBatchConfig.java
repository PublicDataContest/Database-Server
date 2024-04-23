package com.example.publicdataserver.batch;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.dto.PublicDataDto;
import com.example.publicdataserver.repository.PublicDataRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PublicDataBatchConfig {
    private final PublicDataUtils publicDataUtils;
    private final DatabaseService databaseService;

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
        return (contribution, chunkContext) -> {
            int totalRecords = 10;
            int chunkSize = 1000;
            List<Mono<Void>> tasks = new ArrayList<>();

            for (int start = 1; start <= totalRecords; start += chunkSize) {
                int end = Math.min(start + chunkSize - 1, totalRecords);
                tasks.add(processChunk(start, end, publicDataRepository));
            }

            // 모든 비동기 작업이 완료될 때까지 대기
            Mono.when(tasks).block();
            return RepeatStatus.FINISHED;
        };
    }

    private Mono<Void> processChunk(int start, int end, PublicDataRepository publicDataRepository) {
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
}
