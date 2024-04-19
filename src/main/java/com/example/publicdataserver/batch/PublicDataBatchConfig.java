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

import java.util.List;

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
        return ((contribution, chunkContext) -> {
//            for(int start = 1; start <= 56_000; start += 1000) {
//                int end = start + 999;
//                end = Math.min(end, 57_000);
//
//                List<PublicDataDto> publicDataDtos
//                        = publicDataUtils.getPublicDataAsDtoList(start, end);
//
//                for(PublicDataDto publicDataDto : publicDataDtos) {
//                    PublicData publicData = PublicData.builder()
//                            .deptNm(publicDataDto.getDeptNm())
//                            .execDt(publicDataDto.getExecDt())
//                            .execLoc(publicDataDto.getExecLoc())
//                            .targetNm(publicDataDto.getTargetNm())
//                            .execAmount(publicDataDto.getExecAmount())
//                            .execMonth(publicDataDto.getExecMonth())
//                            .build();
//
//                    publicDataRepository.save(publicData);
//                }
//            }

            int start = 1;
            int end = 10;

                List<PublicDataDto> publicDataDtos
                        = publicDataUtils.getPublicDataAsDtoList(start, end);

                for(PublicDataDto publicDataDto : publicDataDtos) {
                    PublicData publicData = PublicData.builder()
                            .deptNm(publicDataDto.getDeptNm())
                            .execDt(publicDataDto.getExecDt())
                            .execLoc(publicDataDto.getExecLoc())
                            .targetNm(publicDataDto.getTargetNm())
                            .execAmount(publicDataDto.getExecAmount())
                            .execMonth(publicDataDto.getExecMonth())
                            .build();

                    publicDataRepository.save(publicData);
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
    public Tasklet tasklet2(PublicDataRepository publicDataRepository) {
        return ((contribution, chunkContext) -> {
            databaseService.saveData();

            return RepeatStatus.FINISHED;
        });
    }
}
