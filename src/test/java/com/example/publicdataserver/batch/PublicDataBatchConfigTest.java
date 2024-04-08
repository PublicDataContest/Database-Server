package com.example.publicdataserver.batch;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.dto.PublicDataDto;
import com.example.publicdataserver.repository.PublicDataRepository;
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
    public Tasklet tasklet(PublicDataRepository publicDataRepository) {
        return ((contribution, chunkContext) -> {
            for(int start = 1; start <= 56_000; start += 1000) {
                int end = start + 999;
                end = Math.min(end, 57_000);

                saveData(publicDataUtils.getPublicDataAsDtoList(start, end), publicDataRepository);
            }

            return RepeatStatus.FINISHED;
        });
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
