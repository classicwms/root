package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleAsyncService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    WarehouseService warehouseService;


    //-------------------------------------------------------------------Outbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrder() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("MDU");
        return CompletableFuture.completedFuture(outboundOrder);

    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV2() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("CMP");
        return CompletableFuture.completedFuture(outboundOrder);

    }

    //-------------------------------------------------------------------DeliveryConfirmation---------------------------------------------------------------
//    @Async("asyncExecutor")
//    public CompletableFuture<WarehouseApiResponse> processDeliveryConfirmationV1() throws Exception {
//
//        WarehouseApiResponse deliveryConfirm = warehouseService.postSAPDeliveryConfirmationScheduleProcess("MDU");
//        return CompletableFuture.completedFuture(deliveryConfirm);
//    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV1() {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("MDU");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV2() {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("CMP");
    }


//    @Async("asyncExecutor")
//    public CompletableFuture<WarehouseApiResponse> processDeliveryConfirmationV2() throws Exception {
//        WarehouseApiResponse deliveryConfirm = warehouseService.postSAPDeliveryConfirmationScheduleProcess("CMP");
//        return CompletableFuture.completedFuture(deliveryConfirm);
//    }

}