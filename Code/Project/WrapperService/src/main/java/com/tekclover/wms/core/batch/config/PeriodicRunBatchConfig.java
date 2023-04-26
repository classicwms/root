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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.tekclover.wms.core.batch.mapper.PeriodicLineMapper;
import com.tekclover.wms.core.model.transaction.PeriodicLine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableBatchProcessing
@Configuration
public class PeriodicRunBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
	public DataSource dataSource;
    
    //---------------------------------------------------------------------------------------------------
  	@Bean
  	public FlatFileItemReader<PeriodicLine> periodicLineItemReader() {
  		FlatFileItemReader<PeriodicLine> reader = new FlatFileItemReader<>();
  		reader.setLinesToSkip(1);
  		reader.setResource(new FileSystemResource("periodicLine.csv"));
  		
  		DefaultLineMapper<PeriodicLine> customerLineMapper = new DefaultLineMapper<>();
  		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
  		tokenizer.setNames(new String[] { "languageId","companyCode","plantId","warehouseId","cycleCountNo","storageBin",
  				"itemCode","packBarcodes","variantCode","variantSubCode","batchSerialNumber","stockTypeId","specialStockIndicator",
  				"inventoryQuantity","inventoryUom","countedQty","varianceQty","cycleCounterId","cycleCounterName","statusId",
  				"cycleCountAction","referenceNo","approvalProcessId","approvalLevel","approverCode","approvalStatus","remarks",
  				"referenceField1","referenceField2","referenceField3","referenceField4","referenceField5","referenceField6",
  				"referenceField7","referenceField8","referenceField9","referenceField10","deletionIndicator","createdBy","createdOn",
  				"confirmedBy","confirmedOn","countedBy","countedOn"});
  		customerLineMapper.setLineTokenizer(tokenizer);
  		customerLineMapper.setFieldSetMapper(new PeriodicLineMapper());
  		customerLineMapper.afterPropertiesSet();
  		reader.setLineMapper(customerLineMapper);
  		return reader;
  	}

  	@SuppressWarnings({ "rawtypes", "unchecked" })
  	@Bean
  	public JdbcBatchItemWriter<PeriodicLine> periodicLineItemWriter() {
  		JdbcBatchItemWriter<PeriodicLine> itemWriter = new JdbcBatchItemWriter<>();
  		itemWriter.setDataSource(this.dataSource);
  		itemWriter.setSql("INSERT INTO tblperiodicline "
  				+ "(LANG_ID,C_ID,PLANT_ID, WH_ID,CC_NO, ST_BIN, ITM_CODE, PACK_BARCODE,VAR_ID, VAR_SUB_ID, STR_NO,STCK_TYP_ID,SP_ST_IND_ID, "
  				+ "INV_QTY, INV_UOM, CTD_QTY, VAR_QTY, COUNTER_ID, COUNTER_NM,STATUS_ID, ACTION, REF_NO,APP_PROCESS_ID, APP_LVL, APP_CODE, "
  				+ "APP_STATUS,REMARK,REF_FIELD_1, REF_FIELD_2,REF_FIELD_3,REF_FIELD_4, REF_FIELD_5,REF_FIELD_6, REF_FIELD_7, REF_FIELD_8,"
  				+ "REF_FIELD_9, REF_FIELD_10,IS_DELETED, CC_CTD_BY, CC_CTD_ON,CC_CNF_BY,CC_CNF_ON,CC_CNT_BY,CC_CNT_ON)"
  				+ " VALUES (:languageId,:companyCode,:plantId,:warehouseId,:cycleCountNo,:storageBin,:itemCode,:packBarcodes,:variantCode,"
  				+ ":variantSubCode,:batchSerialNumber,:stockTypeId,:specialStockIndicator,:inventoryQuantity,:inventoryUom,:countedQty,"
  				+ ":varianceQty,:cycleCounterId,:cycleCounterName,:statusId,:cycleCountAction,:referenceNo,:approvalProcessId,:approvalLevel,"
  				+ ":approverCode,:approvalStatus,:remarks,:referenceField1,:referenceField2,:referenceField3,:referenceField4,:referenceField5,"
  				+ ":referenceField6,:referenceField7,:referenceField8,:referenceField9,:referenceField10,:deletionIndicator,:createdBy,"
  				+ ":createdOn,:confirmedBy,:confirmedOn,:countedBy,:countedOn)");
  		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
  		itemWriter.afterPropertiesSet();
  		return itemWriter;
  	}
    
    @Bean
	public Step periodicLineStep() {
		return stepBuilderFactory.get("periodicLineStep")
				.<PeriodicLine, PeriodicLine>chunk(10)
				.reader(periodicLineItemReader())
				.writer(periodicLineItemWriter())
				.build();
	}

    @Bean
    public Job jobPeriodicJob() {
        JobBuilder jobBuilder = jobBuilderFactory.get("jobPeriodicJob");
        FlowJobBuilder flowJobBuilder = jobBuilder.flow(periodicLineStep()).end();
        Job job = flowJobBuilder.build();
        return job;
    }
}
