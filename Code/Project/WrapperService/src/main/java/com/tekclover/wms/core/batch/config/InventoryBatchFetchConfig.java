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
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.tekclover.wms.core.batch.scheduler.entity.Inventory;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryItemProcessor;

@EnableBatchProcessing
@Configuration
public class InventoryBatchFetchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Bean
    public JpaPagingItemReader<Inventory> getJpaPagingItemReader()  {
        //String sql = "select WH_ID, ITM_CODE, TEXT, ST_BIN,  PACK_BARCODE, INV_UOM, INV_QTY, ALLOC_QTY, STCK_TYP_ID from tblinventory";
    	String sql = "select wh_id, itm_code, text, st_bin,  pack_barcode, inv_uom, inv_qty, alloc_qty, stck_typ_id from tblinventory";
        JpaNativeQueryProvider<Inventory> queryProvider = new JpaNativeQueryProvider<Inventory>();
        JpaPagingItemReader<Inventory> reader = new JpaPagingItemReader<>();
        queryProvider.setSqlQuery(sql);
        reader.setQueryProvider(queryProvider);
        queryProvider.setEntityClass(Inventory.class);
//        reader.setParameterValues(Collections.singletonMap("limit", 1000));
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(4000);
        reader.setSaveState(true);
        return reader;
    }
    
//    @Bean
//    public JpaPagingItemReader getJpaPagingItemReader() {
//        return new JpaPagingItemReaderBuilder<Inventory>()
//                .name("Inventory")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select LANG_ID, CLASS_ID, MATTER_NO from tblmattergenaccid s")
//                .pageSize(1000)
//                .build();
//    }

    @Bean
    public FlatFileItemWriter<Inventory> writer() {
        FlatFileItemWriter<Inventory> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("data.csv"));
        writer.setLineAggregator(getDelimitedLineAggregator());
        return writer;
    }

    private DelimitedLineAggregator<Inventory> getDelimitedLineAggregator() {
        BeanWrapperFieldExtractor<Inventory> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<Inventory>();
        beanWrapperFieldExtractor.setNames(new String[]{"warehouseId", "itemCode", "description", "storageBin", "packBarcodes", "inventoryUom",
        		"inventoryQuantity", "allocatedQuantity", "stockTypeId"});
        DelimitedLineAggregator<Inventory> aggregator = new DelimitedLineAggregator<Inventory>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(beanWrapperFieldExtractor);
        return aggregator;
    }

    @Bean
    public Step getDbToCsvStep() {
        StepBuilder stepBuilder = stepBuilderFactory.get("getDbToCsvStep");
        SimpleStepBuilder<Inventory, Inventory> simpleStepBuilder = stepBuilder.chunk(1000);
        return simpleStepBuilder.reader(getJpaPagingItemReader()).processor(processor()).writer(writer()).build();
    }

    @Bean
    public Job dbToCsvJob() {
        JobBuilder jobBuilder = jobBuilderFactory.get("dbToCsvJob");
        jobBuilder.incrementer(new RunIdIncrementer());
        FlowJobBuilder flowJobBuilder = jobBuilder.flow(getDbToCsvStep()).end();
        Job job = flowJobBuilder.build();
        return job;
    }

    @Bean
    public InventoryItemProcessor processor() {
        return new InventoryItemProcessor();
    }
}