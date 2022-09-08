package com.tekclover.wms.core.batch.scheduler.entity;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.tekclover.wms.core.batch.config.singleton.AccountService;
import com.tekclover.wms.core.batch.config.singleton.AppConfig;
import com.tekclover.wms.core.batch.dto.Inventory2;

public class InventoryCustomItemWriter implements ItemWriter<Inventory2>, Closeable {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		AccountService service1 = null;
		
	    public InventoryCustomItemWriter() {
	    	service1 = context.getBean("accountService", AccountService.class);
	    }

	    @Override
	    public void write(final List<? extends Inventory2> items) throws Exception {
	    	service1.getInventoryHolder().addAll(items);
	    }

	    @PreDestroy
	    @Override
	    public void close() throws IOException {
//	        writer.close();
	    }
	}