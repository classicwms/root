package com.tekclover.wms.api.inbound.transaction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutawayHeaderInt;
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

    @Autowired
    BaseService baseService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

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

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV3() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("CHN");
        return CompletableFuture.completedFuture(inboundOrder);
    }

    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundOrderV4() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("VGA");
        return CompletableFuture.completedFuture(inboundOrder);
    }


    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws Exception {

        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
        return CompletableFuture.completedFuture(inboundFailedOrder);

    }

    @Async("asyncExecutorGrLine")
    public void createPutAwayHeaderAsynProcess(List<PutawayHeaderInt> putawayHeaders) {

        try {
            String currentDB = baseService.getDataBase(putawayHeaders.get(0).getSapDocumentNo());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("PutAwayHeader AsynProcess Started ----------------> ");
            putAwayHeaderService.createPutawayHeaderv3(putawayHeaders);
            log.info("PutAwayHeader AsynProcess Completed ----------------> ");
        } catch (Exception e){
            throw e;
        } finally {
            DataBaseContextHolder.clear();
        }
    }


}