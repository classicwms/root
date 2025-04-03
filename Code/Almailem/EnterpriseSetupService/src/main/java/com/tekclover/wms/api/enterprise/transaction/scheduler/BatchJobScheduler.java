package com.tekclover.wms.api.enterprise.transaction.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.enterprise.transaction.service.ScheduleAsyncService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 10000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        CompletableFuture<WarehouseApiResponse> outboundOrder = scheduleAsyncService.processOutboundOrder();
        CompletableFuture<WarehouseApiResponse> outboundFailedOrder = scheduleAsyncService.processOutboundFailedOrder();

//        CompletableFuture.allOf(inboundOrder,outboundOrder,perpetualStockCountOrder,periodicStockCountOrder,stockAdjustmentOrder,inboundFailedOrder,outboundFailedOrder).join();

    }

}