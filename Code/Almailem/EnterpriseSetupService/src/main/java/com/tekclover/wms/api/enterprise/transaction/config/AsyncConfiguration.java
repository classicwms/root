package com.tekclover.wms.api.enterprise.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {
    @Bean(name = "asyncTaskExecutor")
	public TaskExecutor asyncTaskExecutor() {
//	        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//	        taskExecutor.setCorePoolSize(7);
//	        taskExecutor.setQueueCapacity(150);
//	        taskExecutor.setMaxPoolSize(7);
//	        taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
//	        taskExecutor.initialize();

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
}