package com.tekclover.wms.api.inbound.transaction.scheduler;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 30000)
    public void scheduleJob() throws Exception {

        // MDU
        CompletableFuture<WarehouseApiResponse> inboundOrder = scheduleAsyncService.processInboundOrder();
        // CMP
        CompletableFuture<WarehouseApiResponse> inboundOrderV2 = scheduleAsyncService.processInboundOrderV2();
        // CHN
        CompletableFuture<WarehouseApiResponse> inboundOrderV3 = scheduleAsyncService.processInboundOrderV3();
        //VGA
        CompletableFuture<WarehouseApiResponse> inboundOrderV4 = scheduleAsyncService.processInboundOrderV4();

//        CompletableFuture<WarehouseApiResponse> inboundFailedOrder = scheduleAsyncService.processInboundFailedOrder();

    }

//    private final AtomicBoolean running = new AtomicBoolean(false);
//
//    @Scheduled(fixedDelay = 20000)
//    public void scheduleJob() {
//
//        if (!running.compareAndSet(false, true)) {
//            log.info("Previous inbound job still running, skipping...");
//            return;
//        }
//
//        try {
//            CompletableFuture<WarehouseApiResponse> mdu =
//                    scheduleAsyncService.processInboundOrder();
//
//            CompletableFuture<WarehouseApiResponse> cmp =
//                    scheduleAsyncService.processInboundOrderV2();
//
//            CompletableFuture.allOf(mdu, cmp).join(); //  wait for completion
//
//        } catch (Exception e) {
//            log.error("Scheduler exception", e);
//        } finally {
//            running.set(false); //  only after async completes
//        }
//    }

}