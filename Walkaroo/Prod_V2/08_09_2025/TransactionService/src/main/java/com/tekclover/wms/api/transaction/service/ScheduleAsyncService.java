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

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrder() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder();
        return CompletableFuture.completedFuture(inboundOrder);
    }

    //-------------------------------------------------------------------Outbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrder() throws Exception {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder();
        return CompletableFuture.completedFuture(outboundOrder);

    }

    //-------------------------------------------------------------------DeliveryConfirmation---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processDeliveryTemplate() throws Exception {
        WarehouseApiResponse deliveryTemplate = transactionService.processDeliveryTemplate();
        return CompletableFuture.completedFuture(deliveryTemplate);
    }
    
    //-------------------------------------------------------------------StockCount---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processPerpetualStockCountOrder() throws Exception {

        WarehouseApiResponse perpetualStockCountOrder = transactionService.processPerpetualStockCountOrder();
        return CompletableFuture.completedFuture(perpetualStockCountOrder);

    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processPeriodicStockCountOrder() throws Exception {

        WarehouseApiResponse periodicStockCountOrder = transactionService.processPeriodicStockCountOrder();
        return CompletableFuture.completedFuture(periodicStockCountOrder);

    }

    //-------------------------------------------------------------------StockAdjustment---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processStockAdjustmentOrder() throws Exception {

        WarehouseApiResponse stockAdjustmentOrder = transactionService.processStockAdjustmentOrder();
        return CompletableFuture.completedFuture(stockAdjustmentOrder);
    }

    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws Exception {

        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
        return CompletableFuture.completedFuture(inboundFailedOrder);

    }

    //-------------------------------------------------------------------Outbound-Failed-Order---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundFailedOrder() throws Exception {

        WarehouseApiResponse outboundFailedOrder = transactionService.processOutboundFailedOrder();
        return CompletableFuture.completedFuture(outboundFailedOrder);

    }
}