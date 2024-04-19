package com.example.publicdataserver.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicDataBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0 59 23 1 * ?")
    public void runJob() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("publicDataSave", "publicDataJob" + System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, parameters);
    }
}
