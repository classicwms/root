package com.tekclover.wms.api.inbound.transaction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.dashboard.BinOccupancyProjection;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutawayHeaderInt;
import com.tekclover.wms.api.inbound.transaction.repository.OutboundLineRepository;
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
    OutboundLineRepository outboundLineRepository;
    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    @Async("asyncExecutor")
    public void processInboundOrder() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("MDU");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV2() throws Exception{
        WarehouseApiResponse inboundOrder =  transactionService.processInboundOrder("CMP");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV3() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("CHN");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV4() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("VGA");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV5() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("CCL");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV6() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("HYD");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV7() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("AHM");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV8() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("MUB");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV9() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("NGP1");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV10() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("NGP2");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV11() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("MYS");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV12() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("KNP");
    }

    @Async("asyncExecutor")
    public void processInboundOrderV13() throws Exception{
        WarehouseApiResponse inboundOrder = transactionService.processInboundOrder("CTC");
    }

    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    @Async("asyncExecutor")
    public CompletableFuture<WarehouseApiResponse> processInboundFailedOrder() throws Exception {

        WarehouseApiResponse inboundFailedOrder = transactionService.processInboundFailedOrder();
        return CompletableFuture.completedFuture(inboundFailedOrder);

    }

//    @Async("asyncExecutorGrLine")
//    public void createPutAwayHeaderAsynProcess(List<PutawayHeaderInt> putawayHeaders) {
//
//        try {
//            String currentDB = baseService.getDataBase(putawayHeaders.get(0).getSapDocumentNo());
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(currentDB);
//            log.info("Current DB " + currentDB);
//            log.info("PutAwayHeader AsynProcess Started ----------------> ");
//            putAwayHeaderService.createPutawayHeaderv3(putawayHeaders);
//            log.info("PutAwayHeader AsynProcess Completed ----------------> ");
//        } catch (Exception e){
//            throw e;
//        } finally {
//            DataBaseContextHolder.clear();
//        }
//    }

        @Async("asyncExecutor")
        public CompletableFuture<List<BinOccupancyProjection>> getBinOccupancy(String plant) {

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(plant);

            try {
                return CompletableFuture.completedFuture(
                        outboundLineRepository.getBinOccupancyData()
                );
            } finally {
                DataBaseContextHolder.clear();
            }
    }

}