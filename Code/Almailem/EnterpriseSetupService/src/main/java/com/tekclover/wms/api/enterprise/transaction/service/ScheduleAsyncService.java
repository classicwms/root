package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.enterprise.transaction.repository.OutboundOrderV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.tekclover.wms.api.enterprise.transaction.service.BaseService.WAREHOUSE_ID_200;

@Slf4j
@Service
public class ScheduleAsyncService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    //-------------------------------------------------------------------Outbound---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processNonPickListOutboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        log.info("Non PickList Order Schedule Processing");
        List<OutboundOrderV2> sqlOutboundList = outboundOrderV2Repository.findOutboundOrderNonPickList(0L, WAREHOUSE_ID_200);
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder(sqlOutboundList);
        return CompletableFuture.completedFuture(outboundOrder);

    }

    //-------------------------------------------------------------------Outbound-Failed-Order---------------------------------------------------------------
    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processOutboundFailedOrder() throws InterruptedException {

        WarehouseApiResponse outboundFailedOrder = transactionService.processOutboundFailedOrder();
        return CompletableFuture.completedFuture(outboundFailedOrder);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<WarehouseApiResponse> processPickListOutboundOrder() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        log.info("PickList Order Schedule Processing ------------> ");
        List<OutboundOrderV2> sqlOutboundList = outboundOrderV2Repository.findOutboundOrderPickList(0L, WAREHOUSE_ID_200);
        WarehouseApiResponse outboundOrder = transactionService.processOutboundOrder(sqlOutboundList);
        return CompletableFuture.completedFuture(outboundOrder);
    }
}