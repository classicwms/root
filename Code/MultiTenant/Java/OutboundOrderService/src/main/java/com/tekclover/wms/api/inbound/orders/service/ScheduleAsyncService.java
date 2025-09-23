package com.tekclover.wms.api.inbound.orders.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleAsyncService {

    @Autowired
    TransactionService transactionService;

//    @Async("asyncTaskExecutor")
//    public CompletableFuture<WarehouseApiResponse> processAmgharaOutboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
//        WarehouseApiResponse outboundOrder = transactionService.processAmgharaOutboundOrder();
//        return CompletableFuture.completedFuture(outboundOrder);
//    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV2() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("FAHAHEEL");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV4() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("NAMRATHA");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV5() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("REEFERON");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV6() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("BP");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV7() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("KNOWELL");
        return CompletableFuture.completedFuture(outboundOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrderV8() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder("MMF");
        return CompletableFuture.completedFuture(outboundOrder);
    }

}