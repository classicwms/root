package com.tekclover.wms.api.inbound.transaction.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InboundOrderLineV2Specification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InboundOrderV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderService extends BaseService {

    @Autowired
    InboundOrderRepository inboundOrderRepository;

    @Autowired
    CycleCountHeaderRepository cycleCountHeaderRepository;

    @Autowired
    IntegrationApiResponseRepository integrationApiResponseRepository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;


    @Autowired
    private InboundOrderLinesV2Repository inboundOrderLinesV2Repository;

    @Autowired
    private DbConfigRepository dbConfigRepository;

    //------------------------------------------------------------------------------------------------

    /**
     *
     * @return
     */
    public List<InboundOrder> getInboundOrders() {
        return inboundOrderRepository.findAll();
    }

    /**
     *
     * @param orderId
     * @return
     */
    public InboundOrder getOrderById(String orderId) {
        return inboundOrderRepository.findByRefDocumentNo (orderId);
    }

    /**
     *
     * @return
     */
    public List<InboundIntegrationLog> getFailedInboundOrders() {
        return inboundIntegrationLogRepository.findByIntegrationStatus("FAILED");
    }

    /**
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public List<InboundOrder> getOrderByDate(String sdate) throws ParseException {
        Date date1 = DateUtils.convertStringToDate_start(sdate);
        Date date2 = DateUtils.convertStringToDate_end(sdate);
        return inboundOrderRepository.findByOrderReceivedOnBetween(date1, date2);
    }

    /**
     *
     * @param newInboundOrder
     * @return
     */
    public InboundOrder createInboundOrders(InboundOrder newInboundOrder) {
        InboundOrder inboundOrder = inboundOrderRepository.save(newInboundOrder);
        return inboundOrder;
    }

    /**
     *
     * @param orderId
     * @return
     */
    public InboundOrder updateProcessedInboundOrder(String orderId) {
        InboundOrder dbInboundOrder = getOrderById (orderId);
        log.info("orderId : " + orderId);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            dbInboundOrder.setProcessedStatusId(10L);
            dbInboundOrder.setOrderProcessedOn(new Date());
            InboundOrder inboundOrder = inboundOrderRepository.save(dbInboundOrder);
            return inboundOrder;
        }
        return dbInboundOrder;
    }

    /**
     *
     * @param orderId
     * @param inboundOrderTypeId
     */
    public void updateProcessedIbOrderV2(String orderId, Long inboundOrderTypeId) {
        log.info("rollback - rerun - orderId : " + orderId + "," + inboundOrderTypeId);
        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
        if (dbInboundOrder != null) {
            Long numberOfAttemts = 0L;
            Long attempted = 0L;
            Long processStatusId = 0L;
            if(dbInboundOrder.getNumberOfAttempts() != null){
                if(dbInboundOrder.getNumberOfAttempts().equals(0L)){
                    numberOfAttemts = 1L;
                    processStatusId = 0L;
                }
                if(dbInboundOrder.getNumberOfAttempts().equals(1L)){
                    numberOfAttemts = 2L;
                    processStatusId = 0L;
                }
                if(dbInboundOrder.getNumberOfAttempts().equals(2L)){
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
                if(dbInboundOrder.getNumberOfAttempts().equals(3L)){
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
            } else {
                numberOfAttemts = 1L;
                processStatusId = 0L;
            }
            dbInboundOrder.setProcessedStatusId(processStatusId);
            dbInboundOrder.setNumberOfAttempts(numberOfAttemts);
            dbInboundOrder.setOrderProcessedOn(new Date());
            InboundOrderV2 updatedInboundOrder = inboundOrderV2Repository.save(dbInboundOrder);
            log.info("rollback rerun - updatedInboundOrder : " + updatedInboundOrder);
        }
    }

    //-----------------------------V2-------------------------------------------

    /**
     *
     * @param orderId
     * @return
     */
    public InboundOrderV2 getOrderByIdV2(String orderId, Long inboundOrderTypeId) {

//		InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNo (orderId);
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId (orderId, inboundOrderTypeId);

        if (dbInboundOrder != null) {
            return dbInboundOrder;
        } else {
            return null;
        }
    }

    /**
     *
     * @param orderId
     * @return
     */
    public InboundOrderV2 updateProcessedInboundOrderV2(String orderId, Long inboundOrderTypeId, Long processStatusId) throws Exception {
        InboundOrderV2 dbInboundOrder = getOrderByIdV2 (orderId, inboundOrderTypeId);
        log.info("orderId : " + orderId);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            dbInboundOrder.setProcessedStatusId(processStatusId);
            dbInboundOrder.setOrderProcessedOn(new Date());
            InboundOrderV2 inboundOrder = inboundOrderV2Repository.save(dbInboundOrder);
            return inboundOrder;
        }
        return dbInboundOrder;
    }

    /**
     *
     * @param newInboundOrderV2
     * @return
     */
    @Transactional
    public InboundOrderV2 createInboundOrdersV2(InboundOrderV2 newInboundOrderV2) {
//		InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(newInboundOrderV2.getOrderId(),0L);
//		InboundOrderV2 dbInboundOrder = getOrderByIdV2(newInboundOrderV2.getOrderId());
//        String routingDb = dbConfigRepository.getDbName(newInboundOrderV2.getCompanyCode(),
//                newInboundOrderV2.getBranchCode(),newInboundOrderV2.getWarehouseID());
//        log.info("Routing DataBase ------->  {}",routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);
//        String db = getDataBase(newInboundOrderV2.getBranchCode());
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(db);
//        log.info("Inbound Order Current DB -------------> "  + db);

        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                findByRefDocumentNoAndInboundOrderTypeId(newInboundOrderV2.getOrderId(), newInboundOrderV2.getInboundOrderTypeId());
        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
        DataBaseContextHolder.clear();
        return inboundOrderV2;
    }

    @Transactional
    public InboundOrderV2 createInboundOrdersTransactional(InboundOrderV2 newInboundOrderV2) {

        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository
                .findByRefDocumentNoAndInboundOrderTypeId(
                        newInboundOrderV2.getOrderId(),
                        newInboundOrderV2.getInboundOrderTypeId()
                );

        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }

        InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
        return inboundOrderV2;
    }

    /**
     *
     * @param orderId
     * @return
     */
    public List<IntegrationApiResponse> getConfirmationOrder(String orderId) {
        return integrationApiResponseRepository.findByOrderNumber (orderId);
    }

    /**
     *
     * @param cycleCountHeader
     * @return
     */
    public CycleCountHeader createCycleCountOrder(CycleCountHeader cycleCountHeader) {
        CycleCountHeader dbCycleCountHeader = cycleCountHeaderRepository.save(cycleCountHeader);
        return dbCycleCountHeader;
    }

    //Find InboundOrder
    public List<InboundOrderV2> findInboundOrderV2(FindInboundOrderV2 findInboundOrder) throws ParseException {
        if (findInboundOrder.getFromOrderProcessedOn() != null && findInboundOrder.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findInboundOrder.getFromOrderProcessedOn(), findInboundOrder.getToOrderProcessedOn());
            findInboundOrder.setFromOrderProcessedOn(dates[0]);
            findInboundOrder.setToOrderProcessedOn(dates[1]);
        }
        if (findInboundOrder.getFromOrderReceivedOn() != null && findInboundOrder.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findInboundOrder.getFromOrderReceivedOn(), findInboundOrder.getToOrderReceivedOn());
            findInboundOrder.setFromOrderReceivedOn(dates[0]);
            findInboundOrder.setToOrderReceivedOn(dates[1]);
        }

        InboundOrderV2Specification spec = new InboundOrderV2Specification(findInboundOrder);
        List<InboundOrderV2> results = inboundOrderV2Repository.findAll(spec);
        return results;
    }

    //Find InboundOrderLine
    public List<InboundOrderLinesV2> findInboundOrderLineV2(FindInboundOrderLineV2 findInboundOrderLineV2) throws ParseException {
        if (findInboundOrderLineV2.getFromExpectedDate() != null && findInboundOrderLineV2.getToExpectedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findInboundOrderLineV2.getFromExpectedDate(), findInboundOrderLineV2.getToExpectedDate());
            findInboundOrderLineV2.setFromExpectedDate(dates[0]);
            findInboundOrderLineV2.setToExpectedDate(dates[1]);
        }
        if (findInboundOrderLineV2.getFromReceivedDate() != null && findInboundOrderLineV2.getToReceivedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findInboundOrderLineV2.getFromReceivedDate(), findInboundOrderLineV2.getToReceivedDate());
            findInboundOrderLineV2.setFromReceivedDate(dates[0]);
            findInboundOrderLineV2.setToReceivedDate(dates[1]);
        }

        InboundOrderLineV2Specification spec = new InboundOrderLineV2Specification(findInboundOrderLineV2);
        List<InboundOrderLinesV2> results = inboundOrderLinesV2Repository.findAll(spec);
        return results;
    }
}