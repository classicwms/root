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
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.tekclover.wms.core.batch.dto.Inventory2;
import com.tekclover.wms.core.batch.scheduler.entity.Inventory;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryItemProcessor;

@EnableBatchProcessing
@Configuration
public class InventoryBatchFetchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
//    @Bean
//    public JpaPagingItemReader<Inventory> getJpaPagingItemReader()  {
//        //String sql = "select WH_ID, ITM_CODE, TEXT, ST_BIN,  PACK_BARCODE, INV_UOM, INV_QTY, ALLOC_QTY, STCK_TYP_ID from tblinventory";
//    	String sql = "select wh_id AS warehouseId, itm_code AS itemCode, text AS description, st_bin AS storageBin, \r\n"
//    			+ "pack_barcode AS packBarcodes, inv_uom AS inventoryUom, inv_qty AS inventoryQuantity, alloc_qty AS allocatedQuantity, \r\n"
//    			+ "stck_typ_id AS stockTypeId from tblinventory";
//        JpaNativeQueryProvider<Inventory> queryProvider = new JpaNativeQueryProvider<Inventory>();
//        JpaPagingItemReader<Inventory> reader = new JpaPagingItemReader<>();
//        queryProvider.setSqlQuery(sql);
//        reader.setQueryProvider(queryProvider);
//        queryProvider.setEntityClass(Inventory.class);
////        reader.setParameterValues(Collections.singletonMap("limit", 1000));
//        reader.setEntityManagerFactory(entityManagerFactory);
//        reader.setPageSize(4000);
//        reader.setSaveState(true);
//        return reader;
//    }
    
//  private Double inventoryQuantity;
//	private Double allocatedQuantity;
    
    @Bean
    public JpaPagingItemReader getJpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Inventory>()
                .name("Inventory")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select i from Inventory i where i.inventoryQuantity > 0 or i.allocatedQuantity > 0")
                .pageSize(1000)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Inventory2> writer() {
    	String headers = "WAREHOUSEID, ITEMCODE, DESCRIPTION, STORAGEBIN, PACKBARCODE, MFRPARTNUMBER, STORAGESECTIONID, INVENTORYUOM,INVENTORYQUANTITY, "
    			+ "ALLOCATEDQUANTITY,STOCKTYPEID,TOTALQTY";
    	InventiryFlatFileWriter headeer = new InventiryFlatFileWriter(headers);
        FlatFileItemWriter<Inventory2> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("inventory.csv"));
        writer.setHeaderCallback(headeer);
        writer.setLineAggregator(getDelimitedLineAggregator());
        return writer;
    }

    /**
     * 
     * @return
     */
    private DelimitedLineAggregator<Inventory2> getDelimitedLineAggregator() {
        BeanWrapperFieldExtractor<Inventory2> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<Inventory2>();
        beanWrapperFieldExtractor.setNames(new String[]{"warehouseId", "itemCode", "description", "storageBin", "packBarcodes",
        		"mfrPartNumber", "storageSectionId", "inventoryUom", "inventoryQuantity", "allocatedQuantity", "stockTypeId", "totalQty"});
        DelimitedLineAggregator<Inventory2> aggregator = new DelimitedLineAggregator<Inventory2>();
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
