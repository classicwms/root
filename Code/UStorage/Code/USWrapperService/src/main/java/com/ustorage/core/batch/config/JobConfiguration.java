package com.ustorage.core.batch.config;

import com.ustorage.core.batch.dto.*;

import com.ustorage.core.batch.mapper.AgreementFieldSetMapper;
import com.ustorage.core.batch.mapper.StorageUnitFieldSetMapper;
import com.ustorage.core.batch.mapper.StoreNumberFieldSetMapper;
import com.ustorage.core.config.PropertiesConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class JobConfiguration extends DefaultBatchConfigurer {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;
	
	@Autowired
	PropertiesConfig propertiesConfig;

	@Bean
	public JobListener ustorageListener() throws Exception {
		JobListener listener = new JobListener();
		return listener;
	}

	/*-------------------------agreement----------------------------------------------------------------*/
	@Bean
	public FlatFileItemReader<Agreement> agreementItemReader() {
		FlatFileItemReader<Agreement> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(propertiesConfig.getFileUploadDir() + propertiesConfig.getAgreementFile()));
		DefaultLineMapper<Agreement> customerLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "agreementNumber", "codeId", "quoteNumber", "customerName", "civilIDNumber",  "nationality",
				"address", "phoneNumber", "secondaryNumber", "faxNumber", "itemsToBeStored", "startDate",
				"endDate", "location", "size", "insurance", "rent", "rentPeriod",
				"totalRent", "paymentTerms", "agreementDiscount", "totalAfterDiscount", "notes", "email",  "agreementType",
				"deposit", "others", "itemsToBeStored1", "itemsToBeStored2", "itemsToBeStored3", "itemsToBeStored4",
				"itemsToBeStored5", "itemsToBeStored6", "itemsToBeStored7", "itemsToBeStored8", "itemsToBeStored9", "itemsToBeStored10",  "itemsToBeStored11",
				"itemsToBeStored12", "status", "deletionIndicator", "referenceField1", "referenceField2", "referenceField3",  "referenceField4",
				"referenceField5", "referenceField6", "referenceField7", "referenceField8", "referenceField9", "referenceField10",
				"createdBy", "createdOn", "updatedBy", "updatedOn"});
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new AgreementFieldSetMapper());
		customerLineMapper.afterPropertiesSet();
		reader.setLineMapper(customerLineMapper);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public JdbcBatchItemWriter<Agreement> agreementItemWriter() {
		JdbcBatchItemWriter<Agreement> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO tblagreement (AGREEMENT_NUMBER, CODE_ID, QUOTE_NUMBER, CUSTOMER_NAME, CIVIL_ID_NUMBER, NATIONALITY, \r\n"
				+ "ADDRESS, PHONE_NUMBER, SECONDARY_NUMBER, FAX_NUMBER, ITEMS_TO_BE_STORED, START_DATE, \r\n"
				+ "END_DATE, LOCATION, SIZE, INSURANCE, RENT, RENT_PERIOD, \r\n"
				+ "TOTAL_RENT, PAYMENT_TERMS, AGREEMENT_DISCOUNT, TOTAL_AFTER_DISCOUNT, NOTES, EMAIL,AGREEMENT_TYPE, \r\n"
				+ "DEPOSIT, OTHERS, ITEMS_TO_BE_STORED1,  ITEMS_TO_BE_STORED2, ITEMS_TO_BE_STORED3, \r\n"
				+ "ITEMS_TO_BE_STORED4, ITEMS_TO_BE_STORED5, ITEMS_TO_BE_STORED6, ITEMS_TO_BE_STORED7, ITEMS_TO_BE_STORED8, ITEMS_TO_BE_STORED9, \r\n"
				+ "ITEMS_TO_BE_STORED10, ITEMS_TO_BE_STORED11,ITEMS_TO_BE_STORED12, STATUS, IS_DELETED, REF_FIELD_1, REF_FIELD_2,REF_FIELD_3, \r\n"
				+ "REF_FIELD_4, REF_FIELD_5,REF_FIELD_6, REF_FIELD_7, REF_FIELD_8,REF_FIELD_9,REF_FIELD_10, \r\n"
				+ "CTD_BY, CTD_ON, UTD_BY, UTD_ON) \r\n"
				+ "VALUES (:agreementNumber, :codeId, :quoteNumber, :customerName, :civilIDNumber, :nationality, \r\n"
				+ ":address, :phoneNumber, :secondaryNumber,  :faxNumber, :itemsToBeStored, CAST(:startDate AS DATETIME),CAST(:endDate AS DATETIME), \r\n"
				+ ":location, :size, :insurance, :rent, :rentPeriod, :totalRent, :paymentTerms,:agreementDiscount, \r\n"
				+ ":totalAfterDiscount, :notes,  :email, :agreementType, :deposit,:others, :itemsToBeStored1, :itemsToBeStored2,\r\n"
		        + ":itemsToBeStored3, :itemsToBeStored4, :itemsToBeStored5,  :itemsToBeStored6, :itemsToBeStored7, :itemsToBeStored8,:itemsToBeStored9, \r\n"
				+ ":itemsToBeStored10, :itemsToBeStored11, :itemsToBeStored12, :status, :deletionIndicator, :referenceField1, :referenceField2,:referenceField3, \r\n"
				+ ":referenceField4, :referenceField5,  :referenceField6, :referenceField7, :referenceField8,:referenceField9, :referenceField10, :createdBy, \r\n"
		        + ":createdOn, :updatedBy, :updatedOn)\r\n"
				+ "SELECT NEXT VALUE FOR [dbo].[seq_agreement];");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Agreement, Agreement>chunk(10).reader(agreementItemReader())
				.writer(agreementItemWriter()).build();
	}

	@Bean
	public Job jobAgreement() throws Exception {
		return jobBuilderFactory.get("jobAgreement")
				.listener(ustorageListener())
				.start(step1())
				.build();
	}


	/*-------------------------storageUnit----------------------------------------------------------------*/
	@Bean
	public FlatFileItemReader<StorageUnit> storageUnitItemReader() {
		FlatFileItemReader<StorageUnit> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(propertiesConfig.getFileUploadDir() + propertiesConfig.getStorageUnitFile()));
		DefaultLineMapper<StorageUnit> customerLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "itemCode","codeId","description","itemType","itemGroup","doorType",
				"storageType","phase","zone","room","rack","bin","priceMeterSquare","weekly","monthly","quarterly",
				"halfYearly","yearly","length","width","originalDimention","roundedDimention","availability",
				"occupiedFrom","availableAfter","linkedAgreement","status","storeSizeMeterSquare","aisle","deletionIndicator",
				"referenceField1","referenceField2","referenceField3","referenceField4","referenceField5",
				"referenceField6","referenceField7","referenceField8","referenceField9","referenceField10",
				"createdBy","createdOn","updatedBy","updatedOn"});
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new StorageUnitFieldSetMapper());
		customerLineMapper.afterPropertiesSet();
		reader.setLineMapper(customerLineMapper);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public JdbcBatchItemWriter<StorageUnit> storageUnitItemWriter() {
		JdbcBatchItemWriter<StorageUnit> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO tblstorageunit (ITEM_CODE,CODE_ID,DESCRIPTION,ITEM_TYPE,ITEM_GROUP,DOOR_TYPE,STORAGE_TYPE,PHASE,ZONE,ROOM,RACK,BIN,PRICE_METER_SQAURE,WEEKLY, \r\n"
				+ "MONTHLY,QUARTERLY,HALF_YEARLY,YEARLY,LENGTH,WIDTH,ORIGINAL_DIMENTION,ROUNDED_DIMENTION,AVAILABILITY,OCCUPIED_FROM,AVAILABLE_AFTER, \r\n"
				+ "LINKED_AGREEMENT,STATUS,STORE_SIZE_METER_SQAURE,AISLE,IS_DELETED,REF_FIELD_1,REF_FIELD_2,REF_FIELD_3,REF_FIELD_4,REF_FIELD_5, \r\n"
				+ "REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,CTD_BY,CTD_ON,UTD_BY,UTD_ON) \r\n"
				+ "VALUES (:itemCode,:codeId,:description,:itemType,:itemGroup,:doorType,\r\n"
				+ ":storageType,:phase,:zone,:room,:rack,:bin,:priceMeterSquare,:weekly,:monthly,:quarterly,:halfYearly,:yearly,:length,:width,:originalDimention, \r\n"
				+ ":roundedDimention,:availability,:occupiedFrom,:availableAfter,:linkedAgreement,:status,:storeSizeMeterSquare,:aisle,:deletionIndicator, \r\n"
				+ ":referenceField1,:referenceField2,:referenceField3,:referenceField4,:referenceField5,:referenceField6,:referenceField7,:referenceField8,:referenceField9,:referenceField10, \r\n"
				+ ":createdBy,:createdOn,:updatedBy,:updatedOn)\r\n"
				+ "SELECT NEXT VALUE FOR [dbo].[seq_storage_unit]");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}


	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<StorageUnit, StorageUnit>chunk(10).reader(storageUnitItemReader())
				.writer(storageUnitItemWriter()).build();
	}

	@Bean
	public Job jobStorageUnit() throws Exception {
		return jobBuilderFactory.get("jobStorageUnit")
				.listener(ustorageListener())
				.start(step2())
				.build();
	}

	/*-------------------------storeNumber----------------------------------------------------------------*/
	@Bean
	public FlatFileItemReader<StoreNumber> storeNumberItemReader() {
		FlatFileItemReader<StoreNumber> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(propertiesConfig.getFileUploadDir() + propertiesConfig.getStoreNumberFile()));
		DefaultLineMapper<StoreNumber> customerLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "storeNumber","agreementNumber","description","deletionIndicator",
				"createdBy","createdOn","updatedBy","updatedOn"});
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new StoreNumberFieldSetMapper());
		customerLineMapper.afterPropertiesSet();
		reader.setLineMapper(customerLineMapper);
		return reader;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public JdbcBatchItemWriter<StoreNumber> storeNumberItemWriter() {
		JdbcBatchItemWriter<StoreNumber> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO tblstorenumber (STORE_NUMBER,AGREEMENT_NUMBER,DESCRIPTION,IS_DELETED, \r\n"
				+ "CTD_BY,CTD_ON,UTD_BY,UTD_ON) \r\n"
				+ "VALUES (:storeNumber,:agreementNumber,:description,:deletionIndicator,\r\n"
				+ ":createdBy,:createdOn,:updatedBy,:updatedOn)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").<StoreNumber, StoreNumber>chunk(10).reader(storeNumberItemReader())
				.writer(storeNumberItemWriter()).build();
	}

	@Bean
	public Job jobStoreNumber() throws Exception {
		return jobBuilderFactory.get("jobStoreNumber")
				.listener(ustorageListener())
				.start(step3())
				.build();
	}

}