package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.transaction.repository.*;
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
    PreInboundHeaderService preinboundheaderService;
    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;
    @Autowired
    OrderService orderService;
    @Autowired
    PerpetualHeaderService perpetualHeaderService;
    @Autowired
    PeriodicHeaderService periodicHeaderService;
    @Autowired
    StockAdjustmentMiddlewareService stockAdjustmentMiddlewareService;
    @Autowired
    StockAdjustmentService stockAdjustmentService;
    @Autowired
    CycleCountService cycleCountService;
    @Autowired
    MastersService mastersService;
    @Autowired
    OrderManagementLineService orderManagementLineService;
    //-------------------------------------------------------------------------------------------

    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;
    @Autowired
    StockAdjustmentMiddlewareRepository stockAdjustmentRepository;
    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    InboundOrderLinesV2Repository inboundOrderLinesV2Repository;
    @Autowired
    CycleCountHeaderRepository cycleCountHeaderRepository;

    //-------------------------------------------------------------------------------------------

    List<InboundIntegrationHeader> inboundList = null;
    List<OutboundIntegrationHeaderV2> outboundList = null;
    List<CycleCountHeader> stockCountPerpetualList = null;
    List<CycleCountHeader> stockCountPeriodicList = null;
    List<StockAdjustment> stockAdjustmentList = null;
    static CopyOnWriteArrayList<InboundIntegrationHeader> spList = null;            // Inbound List
    static CopyOnWriteArrayList<OutboundIntegrationHeaderV2> spOutboundList = null;    // Outbound List
    static CopyOnWriteArrayList<CycleCountHeader> scPerpetualList = null;    // StockCount List
    static CopyOnWriteArrayList<CycleCountHeader> scPeriodicList = null;    // StockCount List
    static CopyOnWriteArrayList<StockAdjustment> stockAdjustments = null;    // StockAdjustment List


    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    public synchronized WarehouseApiResponse processInboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (inboundList == null || inboundList.isEmpty()) {
            List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
            log.info("ib sql header list: " + sqlInboundList);
            inboundList = new ArrayList<>();
            for (InboundOrderV2 dbOBOrder : sqlInboundList) {
                InboundIntegrationHeader inboundIntegrationHeader = new InboundIntegrationHeader();
                BeanUtils.copyProperties(dbOBOrder, inboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
                inboundIntegrationHeader.setId(dbOBOrder.getOrderId());
                inboundIntegrationHeader.setMiddlewareId(String.valueOf(dbOBOrder.getMiddlewareId()));
                inboundIntegrationHeader.setMiddlewareTable(dbOBOrder.getMiddlewareTable());
                inboundIntegrationHeader.setSourceBranchCode(dbOBOrder.getSourceBranchCode());
                inboundIntegrationHeader.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());
                inboundIntegrationHeader.setCustomerCode(dbOBOrder.getCustomerCode());
                inboundIntegrationHeader.setTransferRequestType(dbOBOrder.getTransferRequestType());

//                List<InboundOrderLinesV2> sqlInboundLineList = inboundOrderLinesV2Repository.getOrderLinesByOrderTypeId(dbOBOrder.getOrderId(), dbOBOrder.getInboundOrderTypeId());
                log.info("ib line list: " + dbOBOrder.getLine().size());
                List<InboundIntegrationLine> inboundIntegrationLineList = new ArrayList<>();
                for (InboundOrderLinesV2 line : dbOBOrder.getLine()) {
                    InboundIntegrationLine inboundIntegrationLine = new InboundIntegrationLine();
                    BeanUtils.copyProperties(line, inboundIntegrationLine, CommonUtils.getNullPropertyNames(line));

                    inboundIntegrationLine.setLineReference(line.getLineReference());
                    inboundIntegrationLine.setItemCode(line.getItemCode());
                    inboundIntegrationLine.setItemText(line.getItemText());
                    inboundIntegrationLine.setInvoiceNumber(line.getInvoiceNumber());
                    inboundIntegrationLine.setContainerNumber(line.getContainerNumber());
                    inboundIntegrationLine.setSupplierCode(line.getSupplierCode());
                    inboundIntegrationLine.setSupplierPartNumber(line.getSupplierPartNumber());
                    inboundIntegrationLine.setManufacturerName(line.getManufacturerName());
                    inboundIntegrationLine.setManufacturerPartNo(line.getManufacturerPartNo());
                    inboundIntegrationLine.setExpectedDate(line.getExpectedDate());
                    inboundIntegrationLine.setOrderedQty(line.getExpectedQty());
                    inboundIntegrationLine.setUom(line.getUom());
                    inboundIntegrationLine.setItemCaseQty(line.getItemCaseQty());
                    inboundIntegrationLine.setSalesOrderReference(line.getSalesOrderReference());
                    inboundIntegrationLine.setManufacturerCode(line.getManufacturerCode());
                    inboundIntegrationLine.setOrigin(line.getOrigin());
                    inboundIntegrationLine.setBrand(line.getBrand());
                    inboundIntegrationLine.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());
                    inboundIntegrationLine.setSourceBranchCode(dbOBOrder.getSourceBranchCode());

                    inboundIntegrationLine.setSupplierName(line.getSupplierName());

                    inboundIntegrationLine.setMiddlewareId(String.valueOf(line.getMiddlewareId()));
                    inboundIntegrationLine.setMiddlewareHeaderId(String.valueOf(line.getMiddlewareHeaderId()));
                    inboundIntegrationLine.setMiddlewareTable(line.getMiddlewareTable());
                    inboundIntegrationLine.setManufacturerFullName(line.getManufacturerFullName());
                    inboundIntegrationLine.setPurchaseOrderNumber(line.getPurchaseOrderNumber());
                    inboundIntegrationLine.setContainerNumber(line.getContainerNumber());
                    inboundIntegrationHeader.setContainerNo(line.getContainerNumber());

                    inboundIntegrationLineList.add(inboundIntegrationLine);
                }
                inboundIntegrationHeader.setInboundIntegrationLine(inboundIntegrationLineList);
                inboundList.add(inboundIntegrationHeader);
            }
            spList = new CopyOnWriteArrayList<InboundIntegrationHeader>(inboundList);
            log.info("There is no record found to process (sql) ...Waiting..");
        }

        if (inboundList != null) {
            log.info("Latest InboundOrder found: " + inboundList);
            for (InboundIntegrationHeader inbound : spList) {
                try {
                    log.info("InboundOrder ID : " + inbound.getRefDocumentNo());
                    InboundHeaderV2 inboundHeader = preinboundheaderService.processInboundReceivedV2(inbound.getRefDocumentNo(), inbound);
                    if (inboundHeader != null) {
                        // Updating the Processed Status
                        orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 10L);
                        inboundList.remove(inbound);
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on inbound processing : " + e.toString());
                    if ((e.toString().contains("SQLState: 40001") && e.toString().contains("SQL Error: 1205")) ||
                            e.toString().contains("was deadlocked on lock") ||
                            e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
                            e.toString().contains("UnexpectedRollbackException")) {
                        // Updating the Processed Status
                        orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 900L);

                       //============================================================================================
                        //Sending Failed Details through Mail
                        InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                        inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCode());
                        inboundOrderCancelInput.setPlantId(inbound.getBranchCode());
                        inboundOrderCancelInput.setRefDocNumber(inbound.getRefDocumentNo());
                        inboundOrderCancelInput.setReferenceField1(getInboundOrderTypeTable(inbound.getInboundOrderTypeId()));
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
                            preinboundheaderService.createInboundIntegrationLogV2(inbound, e.toString());
                            inboundList.remove(inbound);
                        } catch (Exception ex) {
                            inboundList.remove(inbound);
                            throw new RuntimeException(ex);
                        }

                        warehouseApiResponse.setStatusCode("1400");
                        warehouseApiResponse.setMessage("Failure");
                    } else {
                    // Updating the Processed Status
                    orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(),  100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(inbound.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(inbound.getRefDocumentNo());
                    inboundOrderCancelInput.setReferenceField1(getInboundOrderTypeTable(inbound.getInboundOrderTypeId()));
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
                        preinboundheaderService.createInboundIntegrationLogV2(inbound, e.toString());
                        inboundList.remove(inbound);
                    } catch (Exception ex) {
                        inboundList.remove(inbound);
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

    //=====================================================================StockCount=============================================================================
    // PerpetualCount
    public synchronized WarehouseApiResponse processPerpetualStockCountOrder() throws ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (stockCountPerpetualList == null || stockCountPerpetualList.isEmpty()) {
            List<CycleCountHeader> scpList = cycleCountHeaderRepository.findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(0L, "PERPETUAL");
            stockCountPerpetualList = new CopyOnWriteArrayList<CycleCountHeader>(scpList);
            scPerpetualList = new CopyOnWriteArrayList<CycleCountHeader>(stockCountPerpetualList);
            log.info("stockCountPerpetualList : " + stockCountPerpetualList);
            log.info("There is no stock count record found to process (sql) ...Waiting..");
        }

        if (stockCountPerpetualList != null) {
            log.info("Latest Perpetual StockCount found: " + stockCountPerpetualList);
            for (CycleCountHeader stockCount : scPerpetualList) {
                try {
                    log.info("Perpetual StockCount CycleCountNo : " + stockCount.getCycleCountNo());
                    PerpetualHeaderEntityV2 perpetualStockCount = perpetualHeaderService.processStockCountReceived(stockCount);
                    if (perpetualStockCount != null) {
                        // Updating the Processed Status
                        cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 10L);
                        stockCountPerpetualList.remove(stockCount);
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on PerpetualStockCount processing : " + e.toString());
                    // Updating the Processed Status
                    cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(stockCount.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(stockCount.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(stockCount.getCycleCountNo());
                    inboundOrderCancelInput.setReferenceField1("PERPETUALHEADER");
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                        if(e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") || e.toString().contains("SQLServerException")) {
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

//                    preOutboundHeaderService.createOutboundIntegrationLogV2(outbound);
                    stockCountPerpetualList.remove(stockCount);
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        }
        return warehouseApiResponse;
    }

    // PeriodicCount
    public synchronized WarehouseApiResponse processPeriodicStockCountOrder() throws ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (stockCountPeriodicList == null || stockCountPeriodicList.isEmpty()) {
            List<CycleCountHeader> scpList = cycleCountHeaderRepository.findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(0L, "PERIODIC");
            stockCountPeriodicList = new CopyOnWriteArrayList<CycleCountHeader>(scpList);
            scPeriodicList = new CopyOnWriteArrayList<CycleCountHeader>(stockCountPeriodicList);
            log.info("stockCountPeriodicList : " + stockCountPeriodicList);
            log.info("There is no Periodic stock count record found to process (sql) ...Waiting..");
        }

        if (stockCountPeriodicList != null) {
            log.info("Latest Periodic StockCount found: " + stockCountPeriodicList);
            for (CycleCountHeader stockCount : scPeriodicList) {
                try {
                    log.info("Periodic StockCount CycleCountNo : " + stockCount.getCycleCountNo());
                    PeriodicHeaderEntityV2 periodicHeaderV2 = periodicHeaderService.processStockCountReceived(stockCount);
                    if (periodicHeaderV2 != null) {
                        // Updating the Processed Status
                        cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 10L);
                        stockCountPeriodicList.remove(stockCount);
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on PeriodicStockCount processing : " + e.toString());
                    // Updating the Processed Status
                    cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(stockCount.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(stockCount.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(stockCount.getCycleCountNo());
                    inboundOrderCancelInput.setReferenceField1("PERIODICHEADER");
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                        if(e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") || e.toString().contains("SQLServerException")) {
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

//                    preOutboundHeaderService.createOutboundIntegrationLogV2(outbound);
                    stockCountPeriodicList.remove(stockCount);
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        }
        return warehouseApiResponse;
    }

    //=====================================================================StockAdjustment=============================================================================
    // StockAdjustment
    public synchronized WarehouseApiResponse processStockAdjustmentOrder() {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (stockAdjustmentList == null || stockAdjustmentList.isEmpty()) {
            List<StockAdjustment> saList = stockAdjustmentRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
            stockAdjustmentList = new CopyOnWriteArrayList<StockAdjustment>(saList);
            stockAdjustments = new CopyOnWriteArrayList<StockAdjustment>(stockAdjustmentList);
            log.info("stockAdjustmentList : " + stockAdjustmentList);
            log.info("There is no stock adjustment record found to process (sql) ...Waiting..");
        }

        if (stockAdjustmentList != null) {
            log.info("Latest StockAdjustment found: " + stockAdjustmentList);
            for (StockAdjustment stockAdjustment : stockAdjustments) {
                try {
                    log.info("StockAdjustment Id : " + stockAdjustment.getStockAdjustmentId());
                    WarehouseApiResponse dbStockAdjustment = stockAdjustmentService.processStockAdjustment(stockAdjustment);
                    if (dbStockAdjustment != null) {
                        // Updating the Processed Status
                        stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 10L);
                        stockAdjustmentList.remove(stockAdjustment);
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on StockAdjustment processing : " + e.toString());
                    // Updating the Processed Status
                    stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(stockAdjustment.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(stockAdjustment.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(stockAdjustment.getItemCode());
                    inboundOrderCancelInput.setReferenceField2(stockAdjustment.getManufacturerName());
                    inboundOrderCancelInput.setReferenceField1("STOCKADJUSTMENT");
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                        if(e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") || e.toString().contains("SQLServerException")) {
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

//                    preOutboundHeaderService.createOutboundIntegrationLogV2(outbound);
                    stockAdjustmentList.remove(stockAdjustment);
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        }
        return warehouseApiResponse;
    }

    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    public synchronized WarehouseApiResponse processInboundFailedOrder() throws InterruptedException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(900L);
        log.info("ib failedOrders list: " + sqlInboundList);
        if (sqlInboundList != null && !sqlInboundList.isEmpty()) {
            for (InboundOrderV2 dbIBOrder : sqlInboundList) {
                log.info("DeadLock OrderId: " + dbIBOrder.getOrderId() + ", " + dbIBOrder.getInboundOrderTypeId());
                Thread.sleep(10000);
                inboundOrderV2Repository.updateProcessStatusId(dbIBOrder.getInboundOrderHeaderId());
            }
            warehouseApiResponse.setStatusCode("200");
            warehouseApiResponse.setMessage("Success");
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