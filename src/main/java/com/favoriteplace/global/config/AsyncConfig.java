package com.favoriteplace.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     corePoolSize = CPU 코어수 * CPU 사용률 * (1 + I/O입력시간대기효율)
     */
    @Bean(name = "S3imageUploadExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int numOfCores = Runtime.getRuntime().availableProcessors();
        float targetCpuUtilization = 0.3f; // CPU 사용률
        float blockingCoefficient = 0.1f;   // I/O 입력시간 대기효율
        int corePoolSize = (int) (numOfCores * targetCpuUtilization * (1 + blockingCoefficient));
        executor.setCorePoolSize(corePoolSize);
        executor.initialize();
        return executor;
    }
}
