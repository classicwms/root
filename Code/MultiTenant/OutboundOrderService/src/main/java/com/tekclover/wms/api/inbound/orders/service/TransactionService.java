package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.orders.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.orders.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.inbound.orders.service.namratha.OrderProcessingService;
import com.tekclover.wms.api.inbound.orders.service.outboundscdeduler.PreOutboundService;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    OrderService orderService;
    @Autowired
    MastersService mastersService;
    @Autowired
    OrderManagementLineService orderManagementLineService;
    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;
    @Autowired
    private PreOutboundService preOutboundService;
    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    DbConfigRepository dbConfigRepository;
    protected static final String WAREHOUSE_ID_100 = "100";

    //-------------------------------------------------------------------Outbound warehouse amghara---------------------------------------------------------------
    public WarehouseApiResponse processAmgharaOutboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        DataBaseContextHolder.setCurrentDb("IMF");
        List<OutboundOrderV2> sqlOutboundList = outboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
        log.info("amghara ob header list: " + sqlOutboundList);

        // Set Process_status_id = 1
        sqlOutboundList.stream().forEach(outbound -> {
            try {
                outboundOrderV2Repository.updateProcessStatusId(outbound.getOutboundOrderHeaderId());
                log.info("Update Process StatusId 1 Successfully");
            } catch (Exception e) {
                log.info("Update Order Process StatusId 1 Failed" + e.getMessage());
                throw new RuntimeException(e);
            }
        });
//            outboundList = new ArrayList<>();
        List<OutboundIntegrationHeaderV2> outboundList = new ArrayList<>();
        for (OutboundOrderV2 dbOBOrder : sqlOutboundList) {
            log.info("amghara OB Process Initiated : " + dbOBOrder.getOrderId());
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
            outboundIntegrationHeader.setCustomerCode(dbOBOrder.getCustomerCode());
            outboundIntegrationHeader.setTransferRequestType(dbOBOrder.getTransferRequestType());
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
                outboundIntegrationLine.setQtyInCase(line.getQtyInCase());
                outboundIntegrationLine.setQtyInPiece(line.getQtyInPiece());
                outboundIntegrationLineList.add(outboundIntegrationLine);
            }
            outboundIntegrationHeader.setOutboundIntegrationLines(outboundIntegrationLineList);
            outboundList.add(outboundIntegrationHeader);
        }
        log.info("Latest amghara OutboundOrder found: " + outboundList);
        for (OutboundIntegrationHeaderV2 outbound : outboundList) {
            try {
                log.info("amghara OutboundOrder ID : " + outbound.getRefDocumentNo());
                String profile = dbConfigRepository.getDbName(outbound.getCompanyCode(), outbound.getBranchCode(), outbound.getWarehouseID());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(profile);
                OutboundHeaderV2 outboundHeader = null;
                if (profile != null) {
                    switch (profile) {
                        case "AUTO_LAP":
                        case "FAHAHEEL":
                            outboundHeader = preOutboundService.processOutboundReceivedV2(outbound);
                            break;
                        case "NAMRATHA":
                            outboundHeader = orderProcessingService.processOutboundReceivedV4(outbound);
                            break;
                        case "REEFERON":
                            outboundHeader = preOutboundService.processOutboundReceivedV5(outbound);
                            break;
                        case "KNOWELL":
                            outboundHeader = orderProcessingService.processOutboundReceivedV7(outbound);
                    }
                }
                if (outboundHeader != null) {
                    // Updating the Processed Status
                    orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(), 10L);
                    outboundList.remove(outbound);
                    warehouseApiResponse.setStatusCode("200");
                    warehouseApiResponse.setMessage("Success");
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error on amghara outbound processing : " + e.toString());
                if ((e.toString().contains("SQLState: 40001") && e.toString().contains("SQL Error: 1205")) ||
                        e.toString().contains("was deadlocked on lock") ||
                        e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
                        e.toString().contains("UnexpectedRollbackException") || e.toString().contains("SqlException")) {
                    // Updating the Processed Status
                    orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(), 900L);
//                        orderManagementLineService.doUnAllocationV2(outbound);
//                        orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID());
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
//                        preOutboundHeaderService.createOutboundIntegrationLogV2(outbound, e.toString());
                        outboundList.remove(outbound);
                    } catch (Exception ex) {
                        outboundList.remove(outbound);
                        throw new RuntimeException(ex);
                    }
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                } else {
                    // Updating the Processed Status
                    orderService.updateProcessedOrderV2(outbound.getRefDocumentNo(), outbound.getOutboundOrderTypeID(), 100L);

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
                        if (e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
                                e.toString().contains("SQLServerException") || e.toString().contains("UnexpectedRollbackException")) {
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
//                        preOutboundHeaderService.createOutboundIntegrationLogV2(outbound, e.toString());
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
//    }
        return warehouseApiResponse;
    }


    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getOutboundOrderTypeTable(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 0 || referenceDocumentTypeId == 1) {
            referenceDocumentType = "TRANSFEROUTHEADER";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "PURCHASERETURNHEADER";
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "PICKLISTHEADER";
        }

        return referenceDocumentType;
    }
}
