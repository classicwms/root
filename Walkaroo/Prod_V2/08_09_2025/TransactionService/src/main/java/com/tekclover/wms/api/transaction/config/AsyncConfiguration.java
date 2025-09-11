package com.tekclover.wms.api.transaction.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);
//        taskExecutor.setQueueCapacity(100);
//        taskExecutor.setMaxPoolSize(5);
//        taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
//        taskExecutor.initialize();
//        return taskExecutor;

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncTaskThread-");
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(30);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new MyTaskExecutionHandlerImpl());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

//    @Bean(name = "asyncExecutor")
//    public Executor asyncExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);   // Minimum threads
//        taskExecutor.setMaxPoolSize(10);   // Maximum threads
//        taskExecutor.setQueueCapacity(100);// Queue size
//        taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
//
//        // Handle rejected tasks (when pool is full)
//        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//
//        taskExecutor.initialize();
//        return taskExecutor;
//    }

}