package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.transaction.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class TransactionService extends BaseService{

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;
    @Autowired
    OrderService orderService;
    @Autowired
    MastersService mastersService;
    //-------------------------------------------------------------------------------------------

    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;

    //-------------------------------------------------------------------------------------------

    List<OutboundIntegrationHeaderV2> outboundList = null;
    static CopyOnWriteArrayList<OutboundIntegrationHeaderV2> spOutboundList = null;    // Outbound List


    //-------------------------------------------------------------------Outbound---------------------------------------------------------------
    public synchronized WarehouseApiResponse processOutboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (outboundList == null || outboundList.isEmpty()) {
            List<OutboundOrderV2> sqlOutboundList = outboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
            log.info("ob header list: " + sqlOutboundList);
            outboundList = new ArrayList<>();
            for (OutboundOrderV2 dbOBOrder : sqlOutboundList) {
                log.info("OB Process Initiated : " + dbOBOrder.getOrderId());
                OutboundIntegrationHeaderV2 outboundIntegrationHeader = new OutboundIntegrationHeaderV2();
                BeanUtils.copyProperties(dbOBOrder, outboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
                outboundIntegrationHeader.setId(dbOBOrder.getOrderId());
                outboundIntegrationHeader.setCompanyCode(dbOBOrder.getCompanyCode());
                outboundIntegrationHeader.setBranchCode(dbOBOrder.getBranchCode());
                outboundIntegrationHeader.setReferenceDocumentType(dbOBOrder.getRefDocumentType());
                outboundIntegrationHeader.setMiddlewareId(dbOBOrder.getMiddlewareId());
                outboundIntegrationHeader.setMiddlewareTable(dbOBOrder.getMiddlewareTable());
                outboundIntegrationHeader.setReferenceDocumentType(dbOBOrder.getRefDocumentType());
                outboundIntegrationHeader.setSalesOrderNumber(dbOBOrder.getSalesOrderNumber());
                outboundIntegrationHeader.setPickListNumber(dbOBOrder.getPickListNumber());
                outboundIntegrationHeader.setTokenNumber(dbOBOrder.getTokenNumber());
                outboundIntegrationHeader.setTargetCompanyCode(dbOBOrder.getTargetCompanyCode());
                outboundIntegrationHeader.setTargetBranchCode(dbOBOrder.getTargetBranchCode());
                if (dbOBOrder.getOutboundOrderTypeID() == 3L) {
                    outboundIntegrationHeader.setStatus(dbOBOrder.getPickListStatus());
                    outboundIntegrationHeader.setRequiredDeliveryDate(dbOBOrder.getRequiredDeliveryDate());
                }
                if (dbOBOrder.getOutboundOrderTypeID() != 3L) {
                    outboundIntegrationHeader.setStatus(dbOBOrder.getStatus());
                }


                if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4) {
                    outboundIntegrationHeader.setSalesInvoiceNumber(dbOBOrder.getSalesInvoiceNumber());
                    outboundIntegrationHeader.setSalesOrderNumber(dbOBOrder.getSalesOrderNumber());
                    outboundIntegrationHeader.setRequiredDeliveryDate(dbOBOrder.getSalesInvoiceDate());
                    outboundIntegrationHeader.setDeliveryType(dbOBOrder.getDeliveryType());
                    outboundIntegrationHeader.setCustomerId(dbOBOrder.getCustomerId());
                    outboundIntegrationHeader.setCustomerName(dbOBOrder.getCustomerName());
                    outboundIntegrationHeader.setAddress(dbOBOrder.getAddress());
                    outboundIntegrationHeader.setPhoneNumber(dbOBOrder.getPhoneNumber());
                    outboundIntegrationHeader.setAlternateNo(dbOBOrder.getAlternateNo());
                    outboundIntegrationHeader.setStatus(dbOBOrder.getStatus());
                }

                List<OutboundIntegrationLineV2> outboundIntegrationLineList = new ArrayList<>();
//                List<OutboundOrderLineV2> sqlOutboundLineList = outboundOrderLinesV2Repository.findAllByOrderIdAndOutboundOrderTypeID(dbOBOrder.getOrderId(), dbOBOrder.getOutboundOrderTypeID());
                log.info("ob line list: " + dbOBOrder.getLine().size());
                for (OutboundOrderLineV2 line : dbOBOrder.getLine()) {
                    OutboundIntegrationLineV2 outboundIntegrationLine = new OutboundIntegrationLineV2();
                    BeanUtils.copyProperties(line, outboundIntegrationLine, CommonUtils.getNullPropertyNames(line));
                    outboundIntegrationLine.setCompanyCode(line.getFromCompanyCode());
                    outboundIntegrationLine.setBranchCode(line.getSourceBranchCode());
                    outboundIntegrationLine.setManufacturerName(line.getManufacturerName());
                    outboundIntegrationLine.setManufacturerCode(line.getManufacturerName());
                    outboundIntegrationLine.setMiddlewareId(line.getMiddlewareId());
                    outboundIntegrationLine.setMiddlewareHeaderId(line.getMiddlewareHeaderId());
                    outboundIntegrationLine.setMiddlewareTable(line.getMiddlewareTable());
                    outboundIntegrationLine.setSalesInvoiceNo(line.getSalesInvoiceNo());
                    outboundIntegrationLine.setReferenceDocumentType(dbOBOrder.getRefDocumentType());
                    outboundIntegrationLine.setRefField1ForOrderType(line.getRefField1ForOrderType());
                    outboundIntegrationLine.setSalesOrderNumber(line.getSalesOrderNo());
                    outboundIntegrationLine.setSupplierInvoiceNo(line.getSupplierInvoiceNo());
                    outboundIntegrationLine.setPickListNo(line.getPickListNo());
                    outboundIntegrationLine.setManufacturerFullName(line.getManufacturerFullName());
                    outboundIntegrationLineList.add(outboundIntegrationLine);
                }
                outboundIntegrationHeader.setOutboundIntegrationLines(outboundIntegrationLineList);
                outboundList.add(outboundIntegrationHeader);
            }
            spOutboundList = new CopyOnWriteArrayList<OutboundIntegrationHeaderV2>(outboundList);
            log.info("There is no record found to process (sql) ...Waiting..");
        }

        if (outboundList != null) {
            log.info("Latest OutboundOrder found: " + outboundList);
            for (OutboundIntegrationHeaderV2 outbound : spOutboundList) {
                try {
                    log.info("OutboundOrder ID : " + outbound.getRefDocumentNo());
                    OutboundHeaderV2 outboundHeader = preOutboundHeaderService.processOutboundReceivedV2(outbound);
                    if (outboundHeader != null) {
                        // Updating the Processed Status
                        orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(),  10L);
                        outboundList.remove(outbound);
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on outbound processing : " + e.toString());
                    if ((e.toString().contains("SQLState: 40001") && e.toString().contains("SQL Error: 1205")) ||
                            e.toString().contains("was deadlocked on lock") ||
                            e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
                            e.toString().contains("UnexpectedRollbackException")) {
                        // Updating the Processed Status
                        orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(), 900L);

                        //============================================================================================
                        //Sending Failed Details through Mail
                        InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                        inboundOrderCancelInput.setCompanyCodeId(outbound.getCompanyCode());
                        inboundOrderCancelInput.setPlantId(outbound.getBranchCode());
                        inboundOrderCancelInput.setRefDocNumber(outbound.getRefDocumentNo());
                        inboundOrderCancelInput.setReferenceField1(getOutboundOrderTypeTable(outbound.getOutboundOrderTypeID()));
                        String errorDesc = null;
                        try {
                            if (e.toString().contains("message")) {
                                errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                                errorDesc = errorDesc.replaceAll("}]", "");
                            }
                            if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                                errorDesc = "Null Pointer Exception";
                            }
                            if (e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") || e.toString().contains("SQLServerException")) {
                                errorDesc = "SQLServerException";
                            }
                            if (e.toString().contains("BadRequestException")) {
                                errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                            }
                        } catch (Exception ex) {
                            throw new BadRequestException("ErrorDesc Extract Error" + ex);
                        }
                        inboundOrderCancelInput.setRemarks(errorDesc);

                        mastersService.sendMail(inboundOrderCancelInput);
                        //============================================================================================

                        try {
                            preOutboundHeaderService.createOutboundIntegrationLogV2(outbound, e.toString());
                            outboundList.remove(outbound);
                        } catch (Exception ex) {
                            outboundList.remove(outbound);
                            throw new RuntimeException(ex);
                        }
                        warehouseApiResponse.setStatusCode("1400");
                        warehouseApiResponse.setMessage("Failure");
                    } else {
                    // Updating the Processed Status
                    orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(),100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(outbound.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(outbound.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(outbound.getRefDocumentNo());
                    inboundOrderCancelInput.setReferenceField1(getOutboundOrderTypeTable(outbound.getOutboundOrderTypeID()));
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                            if (e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
                                    e.toString().contains("SQLServerException") || e.toString().contains("UnexpectedRollbackException")) {
                                errorDesc = "SQLServerException";
                        }
                        if(e.toString().contains("BadRequestException")){
                            errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                        }
                    } catch (Exception ex) {
                        throw new BadRequestException("ErrorDesc Extract Error" + ex);
                    }
                    inboundOrderCancelInput.setRemarks(errorDesc);

                    mastersService.sendMail(inboundOrderCancelInput);
                    //============================================================================================

                    try {
                        preOutboundHeaderService.createOutboundIntegrationLogV2(outbound, e.toString());
                        outboundList.remove(outbound);
                    } catch (Exception ex) {
                        outboundList.remove(outbound);
                        throw new RuntimeException(ex);
                    }
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        }
        }
        return warehouseApiResponse;
    }

    //-------------------------------------------------------------------Outbound-Failed-Order-------------------------------------------------------------
    public synchronized WarehouseApiResponse processOutboundFailedOrder() throws InterruptedException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        List<OutboundOrderV2> sqlOutboundList = outboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(900L);
        log.info("ob failedOrders list: " + sqlOutboundList);
        if (sqlOutboundList != null && !sqlOutboundList.isEmpty()) {
            for (OutboundOrderV2 dbOBOrder : sqlOutboundList) {
                log.info("DeadLock OrderId: " + dbOBOrder.getOrderId() + ", " + dbOBOrder.getOutboundOrderTypeID());
                Thread.sleep(10000);
                outboundOrderV2Repository.updateProcessStatusId(dbOBOrder.getOutboundOrderHeaderId());
            }
            warehouseApiResponse.setStatusCode("200");
            warehouseApiResponse.setMessage("Success");
        }
        return warehouseApiResponse;
    }
}