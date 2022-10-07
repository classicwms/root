package com.tekclover.wms.core.batch.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tekclover.wms.core.batch.scheduler.entity.Inventory;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryCustomItemWriter;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryItemProcessor2;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryMovement;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryMovement2;

@EnableBatchProcessing
@Configuration
public class InventoryBatchFetchConfig2 {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaPagingItemReader getJpaPagingItemReader2() {
		return new JpaPagingItemReaderBuilder<InventoryMovement>()
				.name("InventoryMovement")
				.entityManagerFactory(entityManagerFactory)
				.queryString("select i from InventoryMovement i ")
				.pageSize(10000)
				.saveState(false)
				.build();
	}
	
	@Bean
	public InventoryCustomItemWriter writer2() {
		InventoryCustomItemWriter writer = new InventoryCustomItemWriter();
		return writer;
	}

	@Bean
	public Step getDbToCsvStep2() {
		StepBuilder stepBuilder = stepBuilderFactory.get("getDbToCsvStep2");
		SimpleStepBuilder<InventoryMovement, InventoryMovement> simpleStepBuilder = stepBuilder.chunk(10000);
		return simpleStepBuilder.reader(getJpaPagingItemReader2()).processor(processor2()).writer(writer2()).build();
	}

	@Bean
	public Job dbToCsvJob2() {
		JobBuilder jobBuilder = jobBuilderFactory.get("dbToCsvJob2");
		FlowJobBuilder flowJobBuilder = jobBuilder.flow(getDbToCsvStep2()).end();
		Job job = flowJobBuilder.build();
		return job;
	}

	@Bean
	public InventoryItemProcessor2 processor2() {
		return new InventoryItemProcessor2();
	}
}
