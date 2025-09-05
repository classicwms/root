package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ScheduleAsyncService {

    @Autowired
    TransactionService transactionService;

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder();
        return CompletableFuture.completedFuture(inboundOrder);

    }


    //-------------------------------------------------------------------StockCount---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processPerpetualStockCountOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        WarehouseApiResponse perpetualStockCountOrder = transactionService.processPerpetualStockCountOrder();
        return CompletableFuture.completedFuture(perpetualStockCountOrder);

    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processPeriodicStockCountOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        WarehouseApiResponse periodicStockCountOrder = transactionService.processPeriodicStockCountOrder();
        return CompletableFuture.completedFuture(periodicStockCountOrder);

    }

    //-------------------------------------------------------------------StockAdjustment---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processStockAdjustmentOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        WarehouseApiResponse stockAdjustmentOrder = transactionService.processStockAdjustmentOrder();
        return CompletableFuture.completedFuture(stockAdjustmentOrder);
    }

//    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
//    @Async("asyncTaskExecutor")
//    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws InterruptedException {
//
//        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
//        return CompletableFuture.completedFuture(inboundFailedOrder);
//
//    }


}