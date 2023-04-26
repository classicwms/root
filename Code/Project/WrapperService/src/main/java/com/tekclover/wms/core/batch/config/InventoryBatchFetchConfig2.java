package com.tekclover.wms.core.batch.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.tekclover.wms.core.batch.mapper.UserFieldSetMapper;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryCustomItemWriter;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryItemProcessor2;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryMovement;
import com.tekclover.wms.core.util.User1;

@EnableBatchProcessing
@Configuration
public class InventoryBatchFetchConfig2 {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	DataSource dataSource;
	
	//------------------------------------------------------------------------------------------------------
	
	@Bean
	public FlatFileItemReader<User1> userItemReader() {
		FlatFileItemReader<User1> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource("data.csv"));
		
		DefaultLineMapper<User1> customerLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "id", "name", "dob"});

		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new UserFieldSetMapper());
		customerLineMapper.afterPropertiesSet();
		reader.setLineMapper(customerLineMapper);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public JdbcBatchItemWriter<User1> userItemWriter() {
		JdbcBatchItemWriter<User1> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
//		itemWriter.setSql("Update testuser set name = :name where id=:id");
		itemWriter.setSql("INSERT INTO testuser (ID, NAME, DOB) VALUES (:id, :name, :dob)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}
	
	//--------------------------------------------------------------------------------------------------------

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

//	@Bean
//	public Step getInventoryMovement() {
//		StepBuilder stepBuilder = stepBuilderFactory.get("getInventoryMovement");
//		SimpleStepBuilder<InventoryMovement, InventoryMovement> simpleStepBuilder = stepBuilder.chunk(10000);
//		return simpleStepBuilder.reader(getJpaPagingItemReader2()).processor(processor2()).writer(writer2()).build();
//	}
	
	@Bean
	public Step getInventoryMovement() {
		return stepBuilderFactory.get("getInventoryMovement").<User1, User1>chunk(10).reader(userItemReader())
				.writer(userItemWriter()).build();
	}

	@Bean
	public Job jobInventoryMovement() {
		JobBuilder jobBuilder = jobBuilderFactory.get("jobInventoryMovement");
		FlowJobBuilder flowJobBuilder = jobBuilder.flow(getInventoryMovement()).end();
		Job job = flowJobBuilder.build();
		return job;
	}

	@Bean
	public InventoryItemProcessor2 processor2() {
		return new InventoryItemProcessor2();
	}
}
