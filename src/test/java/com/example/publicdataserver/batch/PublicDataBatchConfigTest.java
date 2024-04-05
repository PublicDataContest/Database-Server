package com.example.publicdataserver.batch;

import com.example.publicdataserver.dto.PublicDataDto;
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
public class PublicDataBatchConfigTest {
    private final PublicDataUtils publicDataUtils;

    @Bean
    public Job exchangeJob(JobRepository jobRepository,
                           Step step) {
        return new JobBuilder("publicDataJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager tm, Tasklet tasklet) {
        return new StepBuilder("step", jobRepository)
                .tasklet(tasklet, tm)
                .build();
    }

    @Bean
    public Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            List<PublicDataDto> publicDataDtoList = publicDataUtils.getPublicDataAsDtoList();

            for (PublicDataDto publicDataDto : publicDataDtoList) {
                log.info("제목 : " + publicDataDto.getTitle());
                log.info("목적 : " + publicDataDto.getExecPurpose());
                log.info("목적 : " + publicDataDto.getExecAmount());
            }
            return RepeatStatus.FINISHED;
        });
    }
}
