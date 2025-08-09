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

        // AUTO_LAP
        CompletableFuture<WarehouseApiResponse> supplierInvoice_al = scheduleAsyncService.scheduleSupplierInvoice_al();
        CompletableFuture<WarehouseApiResponse> stockReceipt_al = scheduleAsyncService.scheduleStockReceipt_al();
        CompletableFuture<WarehouseApiResponse> salesReturn_al = scheduleAsyncService.scheduleSalesReturn_al();
        CompletableFuture<WarehouseApiResponse> iwtTransfer_al = scheduleAsyncService.scheduleIWTTransfer_al();
        CompletableFuture<WarehouseApiResponse> salesInvoice_al = scheduleAsyncService.scheduleOutboundSalesInvoice_al();
        CompletableFuture<WarehouseApiResponse> salesOrder_al = scheduleAsyncService.scheduleOutboundSalesOrder_al();
        CompletableFuture<WarehouseApiResponse> purchaseReturn_al = scheduleAsyncService.scheduleOutboundPurchaseReturn_al();
        CompletableFuture<WarehouseApiResponse> transferOut_al = scheduleAsyncService.scheduleOutboundIWTTransfer_al();
        CompletableFuture<WarehouseApiResponse> itemMaster_al = scheduleAsyncService.scheduleItemMaster_al();
        CompletableFuture<WarehouseApiResponse> customerMaster_al = scheduleAsyncService.scheduleCustomerMaster_al();
        CompletableFuture<WarehouseApiResponse> scPerpetual_al = scheduleAsyncService.schedulePerpetual_al();
        CompletableFuture<WarehouseApiResponse> scPeriodic_al = scheduleAsyncService.schedulePeriodic_al();
        CompletableFuture<WarehouseApiResponse> stockAdjustment_al = scheduleAsyncService.scheduleStockAdjustment_al();

    }

}