package com.almailem.ams.api.connector.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.service.ScheduleAsyncService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException {

        CompletableFuture<WarehouseApiResponse> supplierInvoice = scheduleAsyncService.scheduleSupplierInvoice();
        CompletableFuture<WarehouseApiResponse> stockReceipt = scheduleAsyncService.scheduleStockReceipt();
        CompletableFuture<WarehouseApiResponse> salesReturn = scheduleAsyncService.scheduleSalesReturn();
        CompletableFuture<WarehouseApiResponse> iwtTransfer = scheduleAsyncService.scheduleIWTTransfer();

        CompletableFuture<WarehouseApiResponse> salesInvoice = scheduleAsyncService.scheduleOutboundSalesInvoice();
        CompletableFuture<WarehouseApiResponse> salesOrder = scheduleAsyncService.scheduleOutboundSalesOrder();
        CompletableFuture<WarehouseApiResponse> purchaseReturn = scheduleAsyncService.scheduleOutboundPurchaseReturn();
        CompletableFuture<WarehouseApiResponse> transferOut = scheduleAsyncService.scheduleOutboundIWTTransfer();

        CompletableFuture<WarehouseApiResponse> itemMaster = scheduleAsyncService.scheduleItemMaster();
        CompletableFuture<WarehouseApiResponse> customerMaster = scheduleAsyncService.scheduleCustomerMaster();

        CompletableFuture<WarehouseApiResponse> scPerpetual = scheduleAsyncService.schedulePerpetual();
        CompletableFuture<WarehouseApiResponse> scPeriodic = scheduleAsyncService.schedulePeriodic();

        CompletableFuture<WarehouseApiResponse> stockAdjustment = scheduleAsyncService.scheduleStockAdjustment();
//        CompletableFuture.allOf(supplierInvoice,stockReceipt,salesReturn,b2bTransfer,iwtTransfer).join();


        // FAHAHEEL
        CompletableFuture<WarehouseApiResponse> supplierInvoice_fh = scheduleAsyncService.scheduleSupplierInvoice_fh();
        CompletableFuture<WarehouseApiResponse> stockReceipt_fh = scheduleAsyncService.scheduleStockReceipt_fh();
        CompletableFuture<WarehouseApiResponse> salesReturn_fh = scheduleAsyncService.scheduleSalesReturn_fh();
        CompletableFuture<WarehouseApiResponse> iwtTransfer_fh = scheduleAsyncService.scheduleIWTTransfer_fh();
        CompletableFuture<WarehouseApiResponse> salesInvoice_fh = scheduleAsyncService.scheduleOutboundSalesInvoice_fh();
        CompletableFuture<WarehouseApiResponse> salesOrder_fh = scheduleAsyncService.scheduleOutboundSalesOrder_fh();
        CompletableFuture<WarehouseApiResponse> purchaseReturn_fh = scheduleAsyncService.scheduleOutboundPurchaseReturn_fh();
        CompletableFuture<WarehouseApiResponse> transferOut_fh = scheduleAsyncService.scheduleOutboundIWTTransfer_fh();
        CompletableFuture<WarehouseApiResponse> itemMaster_fh = scheduleAsyncService.scheduleItemMaster_fh();
        CompletableFuture<WarehouseApiResponse> customerMaster_fh = scheduleAsyncService.scheduleCustomerMaster_fh();
        CompletableFuture<WarehouseApiResponse> scPerpetual_fh = scheduleAsyncService.schedulePerpetual_fh();
        CompletableFuture<WarehouseApiResponse> scPeriodic_fh = scheduleAsyncService.schedulePeriodic_fh();
        CompletableFuture<WarehouseApiResponse> stockAdjustment_fh = scheduleAsyncService.scheduleStockAdjustment_fh();


    }

}