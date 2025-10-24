package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.UpdateInboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.FindInboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InboundOrderLineV2Specification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InboundOrderV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    InboundOrderRepository inboundOrderRepository;
    @Autowired
    IntegrationApiResponseRepository integrationApiResponseRepository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;


    @Autowired
    WarehouseService warehouseService;

    //------------------------------------------------------------------------------------------------

    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    private InboundOrderLinesV2Repository inboundOrderLinesV2Repository;

    @Autowired
    private InboundOrderLinesRepository inboundOrderLinesRepository;


    //------------------------------------------------------------------------------------------------
//
//    /**
//     * @return
//     */
//    public List<InboundOrder> getInboundOrders() {
//        return inboundOrderRepository.findAll();
//    }

    /**
     * @param orderId
     * @return
     */
    public InboundOrder getOrderById(String orderId) {
        return inboundOrderRepository.findByRefDocumentNo(orderId);
    }

    /**
     * @return
     */
    public List<InboundIntegrationLog> getFailedInboundOrders() {
        return inboundIntegrationLogRepository.findByIntegrationStatus("FAILED");
    }

    /**
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
     * @param newInboundOrder
     * @return
     */
    public InboundOrder createInboundOrders(InboundOrder newInboundOrder) {
        InboundOrder inboundOrder = inboundOrderRepository.save(newInboundOrder);
        return inboundOrder;
    }

    /**
     * @param orderId
     * @return
     */
    public InboundOrder updateProcessedInboundOrder(String orderId) {
        InboundOrder dbInboundOrder = getOrderById(orderId);
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

    //-----------------------------V2-------------------------------------------

    /**
     * @param orderId
     * @return
     */
    public InboundOrderV2 getOrderByIdV2(String orderId, Long inboundOrderTypeId) {

//		InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNo (orderId);
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(orderId, inboundOrderTypeId);

        if (dbInboundOrder != null) {
            return dbInboundOrder;
        } else {
            return null;
        }
    }

//    /**
//     * @param orderId
//     * @return
//     */
//    public InboundOrderV2 updateProcessedInboundOrderV2(String orderId, Long inboundOrderTypeId, Long processStatusId) throws ParseException {
//        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
//        log.info("orderId : " + orderId);
//        log.info("dbInboundOrder : " + dbInboundOrder);
//        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(processStatusId);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            InboundOrderV2 inboundOrder = inboundOrderV2Repository.save(dbInboundOrder);
//            return inboundOrder;
//        }
//        return dbInboundOrder;
//    }

//    /**
//     * @param newInboundOrderV2
//     * @return
//     */
//    public InboundOrderV2 createInboundOrdersV2(InboundOrderV2 newInboundOrderV2) {
//        String profile = dbConfigRepository.getDbName(newInboundOrderV2.getCompanyCode(), newInboundOrderV2.getBranchCode(), newInboundOrderV2.getWarehouseID());
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
//                findByRefDocumentNoAndInboundOrderTypeId(newInboundOrderV2.getOrderId(), newInboundOrderV2.getInboundOrderTypeId());
//        if (dbInboundOrder != null) {
//            throw new BadRequestException("Order is getting Duplicated");
//        }
//        InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(profile);
//        InboundOrderV2 imdDB = inboundOrderV2Repository.save(newInboundOrderV2);
//        return inboundOrderV2;
//    }

    public InboundOrderV2 createInboundOrdersV2(InboundOrderV2 newInboundOrderV2) {

        try {
            InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                    findByRefDocumentNoAndInboundOrderTypeId(newInboundOrderV2.getOrderId(), newInboundOrderV2.getInboundOrderTypeId());
            if (dbInboundOrder != null) {
                throw new BadRequestException("Order is getting Duplicated");
            }
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newInboundOrderV2.getCompanyCode(), newInboundOrderV2.getBranchCode(), newInboundOrderV2.getWarehouseID());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
            log.info("inboundOrderV2 ----> {}", inboundOrderV2);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");

            InboundOrderV2 imdDB = inboundOrderV2Repository.save(newInboundOrderV2);
            log.info("imdDB ----> {}", imdDB);

            return inboundOrderV2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
        log.info("InboundOrders Results ----> {}", results);
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


    //========================================Update=================================================

    /**
     * Update OutboundOrder
     *
     * @param orderId
     * @return
     */
    public InboundOrder updateInboundOrder(String orderId, UpdateInboundOrder updateInboundOrder) {
        InboundOrder dbInboundOrder = getOrderById(orderId);
        BeanUtils.copyProperties(updateInboundOrder, dbInboundOrder, CommonUtils.getNullPropertyNames(updateInboundOrder));
        dbInboundOrder.setOrderProcessedOn(new Date());
        log.info("record is updated successfully");
        return inboundOrderRepository.save(dbInboundOrder);
    }


    /**
     * Delete InboundOrder
     *
     * @param orderId
     */
    public void deleteInboundOrder(String orderId) {
        InboundOrder delete = getOrderById(orderId);
        if (delete == null) {
            throw new BadRequestException(" Order : " + orderId + " doesn't exist.");
        }
        inboundOrderLinesRepository.deleteAll(delete.getLines());
        inboundOrderRepository.delete(delete);
    }

}