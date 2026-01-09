package com.tekclover.wms.api.transaction.scheduler;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    WarehouseService warehouseService;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws Exception {

        // MDU
        CompletableFuture<WarehouseApiResponse> outboundOrder = scheduleAsyncService.processOutboundOrder();
        // CMP
        CompletableFuture<WarehouseApiResponse> outboundOrderV2 = scheduleAsyncService.processOutboundOrderV2();

    }


    @Scheduled(cron = "*/10 * * * * *")
    public void deliveryConfirmationScheduleJob() throws Exception {

        // MDU
        CompletableFuture<WarehouseApiResponse> outboundOrder = scheduleAsyncService.processDeliveryConfirmationV1();
        // CMP
        CompletableFuture<WarehouseApiResponse> outboundOrderV2 = scheduleAsyncService.processDeliveryConfirmationV2();

    }

}