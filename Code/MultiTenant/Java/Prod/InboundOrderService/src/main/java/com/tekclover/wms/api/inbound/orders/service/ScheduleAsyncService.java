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

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder();
        return CompletableFuture.completedFuture(inboundOrder);
    }

//    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
//    @Async("asyncTaskExecutor")
//    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws InterruptedException {
//        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
//        return CompletableFuture.completedFuture(inboundFailedOrder);
//    }

}