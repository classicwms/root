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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.tekclover.wms.core.batch.scheduler.entity.Inventory;
import com.tekclover.wms.core.batch.scheduler.entity.PeriodicLine;
import com.tekclover.wms.core.batch.scheduler.entity.PerodicLineItemProcessor;

@EnableBatchProcessing
@Configuration
public class PeriodicRunBatchFetchConfig {

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
    
    @Bean
    public JpaPagingItemReader getJpaPagingItemReader1() {
        return new JpaPagingItemReaderBuilder<Inventory>()
                .name("Inventory")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select i from Inventory i where i.inventoryQuantity > 0 or i.allocatedQuantity > 0")
                .pageSize(1000)
                .saveState(true)
                .build();
    }

    @Bean
    public FlatFileItemWriter<PeriodicLine> writer1() {
    	String headers = "LANGUAGEID00, COMPANYCODEID, PLANTID, WAREHOUSEID, ITEMCODE, STORAGEBIN, STORAGESECTIONID, STOCKTYPEID,"
    			+ " SETSPECIALSTOCKINDICATOR, PACKBARCODES, ITEMDESC, MANUFACTURERPARTNO, INVENTORYQUANTITY, INVENTORYUOM";
    	InventiryFlatFileWriter headeer = new InventiryFlatFileWriter(headers);
        FlatFileItemWriter<PeriodicLine> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("periodicRun.csv"));
        writer.setHeaderCallback(headeer);
        writer.setLineAggregator(getDelimitedLineAggregator());
        return writer;
    }
    
    /**
     * 
     * @return
     */
    private DelimitedLineAggregator<PeriodicLine> getDelimitedLineAggregator() {
        BeanWrapperFieldExtractor<PeriodicLine> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<PeriodicLine>();
        beanWrapperFieldExtractor.setNames(new String[]{"languageId", "companyCodeId", "plantId", "warehouseId", "itemCode", "storageBin", "storageSectionId",
        		"stockTypeId", "specialStockIndicator", "packBarcodes", "itemDesc", "manufacturerPartNo", "inventoryQuantity", "inventoryUom"});
        DelimitedLineAggregator<PeriodicLine> aggregator = new DelimitedLineAggregator<PeriodicLine>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(beanWrapperFieldExtractor);
        return aggregator;
    }

    @Bean
    public Step getPeriodicStep() {
        StepBuilder stepBuilder = stepBuilderFactory.get("getPeriodicStep");
        SimpleStepBuilder<Inventory, PeriodicLine> simpleStepBuilder = stepBuilder.chunk(1000);
        return simpleStepBuilder.reader(getJpaPagingItemReader1()).processor(processor1()).writer(writer1()).build();
    }

    @Bean
    public Job periodicJob() {
        JobBuilder jobBuilder = jobBuilderFactory.get("periodicJob");
        FlowJobBuilder flowJobBuilder = jobBuilder.flow(getPeriodicStep()).end();
        Job job = flowJobBuilder.build();
        return job;
    }

    @Bean
    public PerodicLineItemProcessor processor1() {
        return new PerodicLineItemProcessor();
    }
}
