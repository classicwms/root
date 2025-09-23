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
//    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
//        CompletableFuture<WarehouseApiResponse> inboundOrder = scheduleAsyncService.processInboundOrder();
//    }

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {


        //NAMRATHA
        CompletableFuture<WarehouseApiResponse> inboundOrderV4 = scheduleAsyncService.processInboundOrderV4();

        //REEFERON
//        CompletableFuture<WarehouseApiResponse> inboundOrderV5 = scheduleAsyncService.processInboundOrderV5();

        //BP
//        CompletableFuture<WarehouseApiResponse> inboundOrderV6 = scheduleAsyncService.processInboundOrderV6();

        //KNOWELL
        CompletableFuture<WarehouseApiResponse> inboundOrderV7 = scheduleAsyncService.processInboundOrderV7();

        //MMF
//        CompletableFuture<WarehouseApiResponse> inboundOrderV8 = scheduleAsyncService.processInboundOrderV8();
    }


}