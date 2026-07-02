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

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV3() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("CHN");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV4() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("VGA");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV5() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("CCL");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV6() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("HYD");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV7() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("AHM");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV8() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("MUB");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV9() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("NGP1");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV10() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("NGP2");
        return CompletableFuture.completedFuture(outboundOrder);
    }

//    //-------------------------------------------------------------------DeliveryConfirmation---------------------------------------------------------------
//    @Async("asyncExecutor")
//    public CompletableFuture<WarehouseApiResponse> processDeliveryConfirmationV1() throws Exception {
//
//        WarehouseApiResponse deliveryConfirm = warehouseService.postSAPDeliveryConfirmationScheduleProcess("MDU");
//        return CompletableFuture.completedFuture(deliveryConfirm);
//    }
//
//
//    @Async("asyncExecutor")
//    public CompletableFuture<WarehouseApiResponse> processDeliveryConfirmationV2() throws Exception {
//        WarehouseApiResponse deliveryConfirm = warehouseService.postSAPDeliveryConfirmationScheduleProcess("CMP");
//        return CompletableFuture.completedFuture(deliveryConfirm);
//    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV1() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("MDU");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV2() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("CMP");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV3() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("CHN");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV4() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("VGA");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV5() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("CCL");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV6() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("HYD");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV7() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("AHM");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV8() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("MUB");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV9() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("NGP1");
    }

    @Async("asyncExecutor")
    public void processDeliveryConfirmationV10() throws Exception {
        warehouseService.postSAPDeliveryConfirmationScheduleProcess("NGP2");
    }
}