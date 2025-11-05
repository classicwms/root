package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.transaction.repository.CycleCountHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InboundOrderV2Repository;
import com.tekclover.wms.api.transaction.repository.StockAdjustmentMiddlewareRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TransactionService extends BaseService {

    @Autowired
    PreInboundHeaderService preinboundheaderService;
    @Autowired
    OrderService orderService;
    @Autowired
    PerpetualHeaderService perpetualHeaderService;
    @Autowired
    StockAdjustmentMiddlewareService stockAdjustmentMiddlewareService;
    @Autowired
    StockAdjustmentService stockAdjustmentService;
    @Autowired
    CycleCountService cycleCountService;
    @Autowired
    MastersService mastersService;
    @Autowired
    StockAdjustmentMiddlewareRepository stockAdjustmentRepository;
    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    CycleCountHeaderRepository cycleCountHeaderRepository;

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
        List<InboundIntegrationHeader> inboundAutoLabList = new ArrayList<>();
            for (InboundOrderV2 dbOBOrder : sqlInboundList) {
                InboundIntegrationHeader inboundIntegrationHeader = new InboundIntegrationHeader();
                BeanUtils.copyProperties(dbOBOrder, inboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
                inboundIntegrationHeader.setId(dbOBOrder.getOrderId());
                inboundIntegrationHeader.setMiddlewareId(String.valueOf(dbOBOrder.getMiddlewareId()));
                inboundIntegrationHeader.setMiddlewareTable(dbOBOrder.getMiddlewareTable());
                inboundIntegrationHeader.setSourceBranchCode(dbOBOrder.getSourceBranchCode());
                inboundIntegrationHeader.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());

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
            inboundAutoLabList.add(inboundIntegrationHeader);
            }
        log.info("Latest InboundOrder found: " + inboundAutoLabList);
        for (InboundIntegrationHeader inbound : inboundAutoLabList) {
                try {
                    log.info("InboundOrder ID : " + inbound.getRefDocumentNo());
                    InboundHeaderV2 inboundHeader = preinboundheaderService.processInboundReceivedV2(inbound.getRefDocumentNo(), inbound);
                    if (inboundHeader != null) {
                        // Updating the Processed Status
                        orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 10L);
                    inboundAutoLabList.remove(inbound);
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
                        inboundAutoLabList.remove(inbound);
                        } catch (Exception ex) {
                        inboundAutoLabList.remove(inbound);
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
                        inboundAutoLabList.remove(inbound);
                    } catch (Exception ex) {
                        inboundAutoLabList.remove(inbound);
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
    public WarehouseApiResponse processPerpetualStockCountOrder() throws ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            List<CycleCountHeader> scpList = cycleCountHeaderRepository.findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(0L, "PERPETUAL");
        log.info("PerpetualStockCount List Size is {}", scpList.size());

        // Update Processed StatusId =1
        scpList.stream().forEach(stockCount -> {
            try {
                cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 1L);
                log.info("PerpetualStockCount Update Process StatusId 1 Successfully");
            } catch (ParseException e) {
                throw new RuntimeException(e);
        }
        });

        for (CycleCountHeader stockCount : scpList) {
                try {
                    log.info("Perpetual StockCount CycleCountNo : " + stockCount.getCycleCountNo());
                    PerpetualHeaderEntityV2 perpetualStockCount = perpetualHeaderService.processStockCountReceived(stockCount);
                    if (perpetualStockCount != null) {
                        // Updating the Processed Status
                        cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 10L);
                    scpList.remove(stockCount);
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
                scpList.remove(stockCount);
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        return warehouseApiResponse;
    }


    //=====================================================================StockAdjustment=============================================================================
    // StockAdjustment
    public WarehouseApiResponse processStockAdjustmentOrder() {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            List<StockAdjustment> saList = stockAdjustmentRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
        log.info("stockAdjustmentList : " + saList);
        // Update ProcessStatusId 1
        saList.stream().forEach(stockAdjustment -> {
            stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 1L);
            log.info("StockAdjustment Update Process StatusId 1 Successfully");
        });

        for (StockAdjustment stockAdjustment : saList) {
                try {
                    log.info("StockAdjustment Id : " + stockAdjustment.getStockAdjustmentId());
                    WarehouseApiResponse dbStockAdjustment = stockAdjustmentService.processStockAdjustment(stockAdjustment);
                    if (dbStockAdjustment != null) {
                        // Updating the Processed Status
                        stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 10L);
                    saList.remove(stockAdjustment);
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
                saList.remove(stockAdjustment);
                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
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

}