package com.tekclover.wms.api.transaction.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.tng.Fetch;
import com.tekclover.wms.api.transaction.model.tng.FetchStock;
import com.tekclover.wms.api.transaction.model.tng.PurchaseOrder;
import com.tekclover.wms.api.transaction.model.tng.Sku;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class TransactionService extends BaseService{
    @Autowired
    private OutboundOrderLinesV2Repository outboundOrderLinesV2Repository;

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
    DirectStockReceiptService directStockReceiptService;
    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    InventoryV2Repository inventoryV2Repository;
    //-------------------------------------------------------------------------------------------

    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;
    @Autowired
    StockAdjustmentMiddlewareRepository stockAdjustmentRepository;
    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    CycleCountHeaderRepository cycleCountHeaderRepository;

    //-------------------------------------------------------------------------------------------

    List<CycleCountHeader> stockCountPerpetualList = null;
    List<CycleCountHeader> stockCountPeriodicList = null;
    List<StockAdjustment> stockAdjustmentList = null;
    static CopyOnWriteArrayList<CycleCountHeader> scPerpetualList = null;    // StockCount List
    static CopyOnWriteArrayList<CycleCountHeader> scPeriodicList = null;    // StockCount List
    static CopyOnWriteArrayList<StockAdjustment> stockAdjustments = null;    // StockAdjustment List


    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    public WarehouseApiResponse processInboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
        log.info("ib sql header list: " + sqlInboundList);

        // Set Process_status_id = 1
        sqlInboundList.stream().forEach(inbound -> {
            try {
                orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 1L);
                log.info("Update Process StatusId 1 Successfully");
            } catch (Exception e) {
                log.info("Update Order Process StatusId 1 Failed" + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        List<InboundIntegrationHeader> inboundList = new ArrayList<>();
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
            inboundIntegrationHeader.setInboundOrderTypeId(dbOBOrder.getInboundOrderTypeId());

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
                inboundIntegrationHeader.setCustomerCode(line.getSupplierCode());
                inboundIntegrationHeader.setContainerNo(line.getContainerNumber());

                inboundIntegrationLineList.add(inboundIntegrationLine);
            }
            inboundIntegrationHeader.setInboundIntegrationLine(inboundIntegrationLineList);
            inboundList.add(inboundIntegrationHeader);
        }
        log.info("There is no record found to process (sql) ...Waiting..");

        log.info("Latest InboundOrder found: " + inboundList);
        for (InboundIntegrationHeader inbound : inboundList) {
            try {
                log.info("InboundOrder ID : " + inbound.getRefDocumentNo());
                InboundHeaderV2 inboundHeader = null;
                if (inbound.getInboundOrderTypeId().equals(5L)) {
                    inboundHeader = directStockReceiptService.processInboundReceivedV2(inbound.getRefDocumentNo(), inbound);
                } else {
                    inboundHeader = preinboundheaderService.processInboundReceivedV2(inbound.getRefDocumentNo(), inbound);
                }
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
                    orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 100L);

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

//    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
//    public synchronized WarehouseApiResponse processInboundFailedOrder() throws InterruptedException {
//        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
//        List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(900L);
//        log.info("ib failedOrders list: " + sqlInboundList);
//        if (sqlInboundList != null && !sqlInboundList.isEmpty()) {
//            for (InboundOrderV2 dbIBOrder : sqlInboundList) {
//                log.info("DeadLock OrderId: " + dbIBOrder.getOrderId() + ", " + dbIBOrder.getInboundOrderTypeId());
//                Thread.sleep(10000);
//                inboundOrderV2Repository.updateProcessStatusId(dbIBOrder.getInboundOrderHeaderId());
//            }
//            warehouseApiResponse.setStatusCode("200");
//            warehouseApiResponse.setMessage("Success");
//        }
//        return warehouseApiResponse;
//    }

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