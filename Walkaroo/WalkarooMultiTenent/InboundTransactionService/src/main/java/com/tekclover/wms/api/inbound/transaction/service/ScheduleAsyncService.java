package com.tekclover.wms.api.inbound.transaction.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleAsyncService {

    @Autowired
    TransactionService transactionService;

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrder() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("MDU");
        return CompletableFuture.completedFuture(inboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV2() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("CMP");
        return CompletableFuture.completedFuture(inboundOrder);
    }


    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws Exception {

        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
        return CompletableFuture.completedFuture(inboundFailedOrder);

    }
}