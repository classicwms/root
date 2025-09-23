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

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV4() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("NAMRATHA");
        return CompletableFuture.completedFuture(inboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV5() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("REEFERON");
        return CompletableFuture.completedFuture(inboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV6() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("BP");
        return CompletableFuture.completedFuture(inboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV7() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("KNOWELL");
        return CompletableFuture.completedFuture(inboundOrder);
    }


    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV8() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{

        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("MMF");
        return CompletableFuture.completedFuture(inboundOrder);
    }

}