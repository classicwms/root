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

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processAmgharaOutboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        WarehouseApiResponse outboundOrder = transactionService.processAmgharaOutboundOrder();
        return CompletableFuture.completedFuture(outboundOrder);
    }

}