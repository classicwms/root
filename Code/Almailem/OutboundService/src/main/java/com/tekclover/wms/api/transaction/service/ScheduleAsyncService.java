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

    //-------------------------------------------------------------------Outbound---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder();
        return CompletableFuture.completedFuture(outboundOrder);

    }

    //-------------------------------------------------------------------Outbound-Failed-Order---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundFailedOrder() throws InterruptedException {

        WarehouseApiResponse outboundFailedOrder = transactionService.processOutboundFailedOrder();
        return CompletableFuture.completedFuture(outboundFailedOrder);

    }
}