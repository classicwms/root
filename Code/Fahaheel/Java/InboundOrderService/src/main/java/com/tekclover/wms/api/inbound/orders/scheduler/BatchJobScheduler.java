package com.tekclover.wms.api.inbound.orders.scheduler;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.service.ScheduleAsyncService;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    //-------------------------------------------------------------------------------------------

//    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        CompletableFuture<WarehouseApiResponse> inboundOrder = scheduleAsyncService.processInboundOrder();
    }

//    @Scheduled(fixedDelay = 10000)
//    public void setScheduleAsyncService() {
//
//        namratha();
//        reeferon();
//        common();
//
//    }
//
//    @Async("asyncExecutor")
//    public void namratha() {
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("NAMRATHA");
//        String currentDb = DataBaseContextHolder.getCurrentDb();
//        log.info(currentDb);
//    }
//
//    @Async("asyncExecutor")
//    public void reeferon() {
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("REEFERON");
//        String currentDb = DataBaseContextHolder.getCurrentDb();
//        log.info(currentDb);
//    }
//
//    @Async("asyncExecutor")
//    public void common() {
//        DataBaseContextHolder.clear();
//        String currentDb = DataBaseContextHolder.getCurrentDb();
//        log.info(currentDb);
//    }
//


}