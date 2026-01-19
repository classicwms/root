package com.tekclover.wms.api.inbound.transaction.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.PeriodicHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderCancelInput;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService extends BaseService {


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
    StockAdjustmentMiddlewareRepository stockAdjustmentRepository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    CycleCountHeaderRepository cycleCountHeaderRepository;

    @Autowired
    InboundOrderProcessingService inboundOrderProcessingService;

    @Autowired
    DeliveryConfirmationRepository deliveryConfirmationRepository;

    @Autowired
    OrderReversalService orderReversalService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    //-------------------------------------------------------------------------------------------


    List<CycleCountHeader> stockCountPerpetualList = null;
    List<CycleCountHeader> stockCountPeriodicList = null;
    List<StockAdjustment> stockAdjustmentList = null;
    static CopyOnWriteArrayList<CycleCountHeader> scPerpetualList = null;    // StockCount List
    static CopyOnWriteArrayList<CycleCountHeader> scPeriodicList = null;    // StockCount List
    static CopyOnWriteArrayList<StockAdjustment> stockAdjustments = null;    // StockAdjustment List


    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    public WarehouseApiResponse processInboundOrder(String profile) throws Exception {
        try {
            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("Current DB --------> " + profile);
            List<InboundIntegrationHeader> inboundList = new ArrayList<>();
//            if (inboundList == null || inboundList.isEmpty()) {
//                List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findByProcessedStatusIdOrderByOrderReceivedOn(0L);
                List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopInboundOrder();
                log.info("ib sql header list: " + sqlInboundList.size());

                for(InboundOrderV2 ibOrder : sqlInboundList) {
                    log.info("InboundOrder StatusId 1 Updated --------> " + ibOrder);
                    inboundOrderV2Repository.updateProcessStatus(ibOrder.getInboundOrderHeaderId(), 1L);
                }

                for (InboundOrderV2 dbOBOrder : sqlInboundList) {
                    InboundIntegrationHeader inboundIntegrationHeader = new InboundIntegrationHeader();
                    BeanUtils.copyProperties(dbOBOrder, inboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
                    inboundIntegrationHeader.setId(dbOBOrder.getOrderId());
                    inboundIntegrationHeader.setMiddlewareId(String.valueOf(dbOBOrder.getMiddlewareId()));
                    inboundIntegrationHeader.setMiddlewareTable(dbOBOrder.getMiddlewareTable());
                    inboundIntegrationHeader.setSourceBranchCode(dbOBOrder.getSourceBranchCode());
                    inboundIntegrationHeader.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());
                    inboundIntegrationHeader.setInboundOrderTypeId(dbOBOrder.getInboundOrderTypeId());

                    //-------InboundUpload Vs SAP API -------Differentiator
                    inboundIntegrationHeader.setIsSapOrder(dbOBOrder.getIsSapOrder());

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
                        inboundIntegrationLine.setManufacturerPartNo(line.getManufacturerName());
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
                        inboundIntegrationLine.setMtoNumber(line.getMtoNumber());

                        //Setting reversalFlag
                        inboundIntegrationLine.setReversalFlag(line.getReversalFlag());

                        inboundIntegrationLineList.add(inboundIntegrationLine);
                    }
                    inboundIntegrationHeader.setInboundIntegrationLine(inboundIntegrationLineList);
                    inboundList.add(inboundIntegrationHeader);
                }
//            }

            if (!inboundList.isEmpty()) {
                log.info("Latest InboundOrder found: " + inboundList);
                for (InboundIntegrationHeader inbound : inboundList) {
                    log.info("------------@@@@@@InboundIntegrationHeader: " + inbound);

                    String refDocNumber = inbound.getRefDocumentNo();
                    Long inboundOrderTypeId = inbound.getInboundOrderTypeId();
                    boolean isDuplicateOrder = inboundOrderProcessingService.isPreInboundOrderExist(refDocNumber, inboundOrderTypeId);
                    log.info("Duplicate_IB_Order : " + refDocNumber + " |--->" + isDuplicateOrder);
                    if(!isDuplicateOrder) {
                        try {
                            log.info("InboundOrder ID : " + refDocNumber);
                            InboundHeaderV2 inboundHeader = null;
                            if (inbound.getInboundOrderTypeId().equals(5L)) {
                                inboundHeader = directStockReceiptService.processInboundReceivedV2(refDocNumber, inbound);
                            } else if (inbound.getInboundOrderTypeId().equals(10L)) {
                                log.info("PGI Reversal process started...");
                                orderReversalService.pgiOrderReversal(inbound);
                                log.info("PGI Reversal process Completed...");
                                return warehouseApiResponse;
                            } else if (inbound.getIsSapOrder()) { // Is from order SAP?
                                inboundHeader = inboundOrderProcessingService.processSAPInboundReceivedV3(refDocNumber, inbound);
                            } else {
                                inboundHeader = inboundOrderProcessingService.processInboundReceivedV3(refDocNumber, inbound);
                            }
                            if (inboundHeader != null) {
                                // Updating the Processed Status
                                updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, 10L);
                                warehouseApiResponse = successResponse(warehouseApiResponse);
                            } else {
                                updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, 100L);
                                sendMail(inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getLanguageId(), inbound.getWarehouseID(),
                                        refDocNumber, getInboundOrderTypeTable(inboundOrderTypeId), "Failed");
                                throw new RuntimeException("Exception while Inbound Processing! " + refDocNumber);
                            }
                        } catch (Exception e) {
                            log.error("Error on inbound Order processing : " + refDocNumber);
                            e.printStackTrace();
                            boolean deadlock = deadLockException(e.toString());
                            if (deadlock) {
                                updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, 900L);
                            } else {
                                updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, 100L);
                            }
                            inboundList.remove(inbound);
                            sendMail(inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getLanguageId(), inbound.getWarehouseID(),
                                    refDocNumber, getInboundOrderTypeTable(inboundOrderTypeId), e.toString());
                            log.error("Exception while Inbound Processing! " + refDocNumber);
                            throw e;
                        }
                    } else {
                        inboundList.remove(inbound);
                    }
                }
            }
            return warehouseApiResponse;
        } catch (Exception e) {
            throw e;
        }
    }

    //=====================================================================StockCount=============================================================================
    // PerpetualCount
    public synchronized WarehouseApiResponse processPerpetualStockCountOrder() throws Exception {
        try {
            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            if (stockCountPerpetualList == null || stockCountPerpetualList.isEmpty()) {
                List<CycleCountHeader> scpList = cycleCountHeaderRepository.findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(0L, "PERPETUAL");
                stockCountPerpetualList = new CopyOnWriteArrayList<CycleCountHeader>(scpList);
                scPerpetualList = new CopyOnWriteArrayList<CycleCountHeader>(stockCountPerpetualList);
                log.info("stockCountPerpetualList : " + stockCountPerpetualList);
                log.info("PPL-There is no stock count record found to process (sql) ...Waiting..");
            }

            if (stockCountPerpetualList != null && !stockCountPerpetualList.isEmpty()) {
                log.info("Latest Perpetual StockCount found: " + stockCountPerpetualList);
                for (CycleCountHeader stockCount : scPerpetualList) {
                    try {
                        log.info("Perpetual StockCount CycleCountNo : " + stockCount.getCycleCountNo());
                        PerpetualHeaderEntityV2 perpetualStockCount = perpetualHeaderService.processStockCountReceived(stockCount);
                        if (perpetualStockCount != null) {
                            // Updating the Processed Status
                            cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 10L);
                            stockCountPerpetualList.remove(stockCount);
                            warehouseApiResponse = successResponse(warehouseApiResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error on PerpetualStockCount processing : " + e.toString());
                        // Updating the Processed Status
                        cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 100L);
                        stockCountPerpetualList.remove(stockCount);
                        sendMail(stockCount.getCompanyCode(), stockCount.getBranchCode(), stockCount.getLanguageId(),
                                stockCount.getWarehouseId(), stockCount.getCycleCountNo(),
                                "PERPETUALHEADER", e.toString());
                        log.error("Exception while Perpetual Stock count Processing! " + stockCount.getCycleCountNo());
                        throw e;
                    }
                }
            }
            return warehouseApiResponse;
        } catch (Exception e) {
            throw e;
        }
    }

    // PeriodicCount
    public synchronized WarehouseApiResponse processPeriodicStockCountOrder() throws Exception {
        try {
            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            if (stockCountPeriodicList == null || stockCountPeriodicList.isEmpty()) {
                List<CycleCountHeader> scpList = cycleCountHeaderRepository.findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(0L, "PERIODIC");
                stockCountPeriodicList = new CopyOnWriteArrayList<CycleCountHeader>(scpList);
                scPeriodicList = new CopyOnWriteArrayList<CycleCountHeader>(stockCountPeriodicList);
                log.info("stockCountPeriodicList : " + stockCountPeriodicList);
                log.info("PDL-There is no Periodic stock count record found to process (sql) ...Waiting..");
            }

            if (stockCountPeriodicList != null && !stockCountPeriodicList.isEmpty()) {
                log.info("Latest Periodic StockCount found: " + stockCountPeriodicList);
                for (CycleCountHeader stockCount : scPeriodicList) {
                    try {
                        log.info("Periodic StockCount CycleCountNo : " + stockCount.getCycleCountNo());
                        PeriodicHeaderEntityV2 periodicHeaderV2 = periodicHeaderService.processStockCountReceived(stockCount);
                        if (periodicHeaderV2 != null) {
                            // Updating the Processed Status
                            cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 10L);
                            stockCountPeriodicList.remove(stockCount);
                            warehouseApiResponse = successResponse(warehouseApiResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error on PeriodicStockCount processing : " + e.toString());
                        // Updating the Processed Status
                        cycleCountService.updateProcessedOrderV2(stockCount.getCycleCountNo(), 100L);
                        sendMail(stockCount.getCompanyCode(), stockCount.getBranchCode(),
                                stockCount.getLanguageId(), stockCount.getWarehouseId(),
                                stockCount.getCycleCountNo(), "PERIODICHEADER", e.toString());
                        stockCountPeriodicList.remove(stockCount);
                        log.error("Exception while Periodic Stock Count Processing! " + stockCount.getCycleCountNo());
                        throw e;
                    }
                }
            }
            return warehouseApiResponse;
        } catch (Exception e) {
            throw e;
        }
    }

    //=====================================================================StockAdjustment=============================================================================
    // StockAdjustment
    public synchronized WarehouseApiResponse processStockAdjustmentOrder() throws Exception {
        try {
            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            if (stockAdjustmentList == null || stockAdjustmentList.isEmpty()) {
                List<StockAdjustment> saList = stockAdjustmentRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
                stockAdjustmentList = new CopyOnWriteArrayList<StockAdjustment>(saList);
                stockAdjustments = new CopyOnWriteArrayList<StockAdjustment>(stockAdjustmentList);
                log.info("stockAdjustmentList : " + stockAdjustmentList);
                log.info("SA-There is no stock adjustment record found to process (sql) ...Waiting..");
            }

            if (stockAdjustmentList != null && !stockAdjustmentList.isEmpty()) {
                log.info("Latest StockAdjustment found: " + stockAdjustmentList);
                for (StockAdjustment stockAdjustment : stockAdjustments) {
                    try {
                        log.info("StockAdjustment Id : " + stockAdjustment.getStockAdjustmentId());
                        WarehouseApiResponse dbStockAdjustment = stockAdjustmentService.processStockAdjustment(stockAdjustment);
                        if (dbStockAdjustment != null) {
                            // Updating the Processed Status
                            stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 10L);
                            stockAdjustmentList.remove(stockAdjustment);
                            warehouseApiResponse = successResponse(warehouseApiResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error on StockAdjustment processing : " + e.toString());
                        // Updating the Processed Status
                        stockAdjustmentMiddlewareService.updateProcessedOrderV2(stockAdjustment.getStockAdjustmentId(), 100L);
                        sendMail(stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(),
                                stockAdjustment.getLanguageId(), stockAdjustment.getWarehouseId(),
                                stockAdjustment.getItemCode(), "STOCKADJUSTMENT", e.toString());
                        stockAdjustmentList.remove(stockAdjustment);
                        log.error("Exception while Stock Adjustment Processing! " + stockAdjustment.getItemCode());
                        throw e;
                    }
                }
            }
            return warehouseApiResponse;
        } catch (Exception e) {
            throw e;
        }
    }

    //-------------------------------------------------------------------Inbound-Failed-Order-------------------------------------------------------------
    public synchronized WarehouseApiResponse processInboundFailedOrder() throws Exception {
        try {
            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(900L);
            log.info("ib failedOrders list: " + sqlInboundList);
            if (sqlInboundList != null && !sqlInboundList.isEmpty()) {
                for (InboundOrderV2 dbIBOrder : sqlInboundList) {
                    log.info("DeadLock OrderId: " + dbIBOrder.getOrderId() + ", " + dbIBOrder.getInboundOrderTypeId());
                    Thread.sleep(10000);
                    inboundOrderV2Repository.updateProcessStatusId(dbIBOrder.getInboundOrderHeaderId());
                }
                warehouseApiResponse = successResponse(warehouseApiResponse);
            }
            return warehouseApiResponse;
        } catch (Exception e) {
            throw e;
        }
    }
    //=========================================================================================================================================================

    /**
     * @param refDocNumber
     * @param inboundOrderTypeId
     * @param processStatusId
     */
    private void updateProcessedInboundOrderV2(String refDocNumber, Long inboundOrderTypeId, Long processStatusId) throws Exception {
        orderService.updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, processStatusId);
    }
    /**
     * @param successWarehouseApiResponse
     * @return
     */
    private WarehouseApiResponse successResponse(WarehouseApiResponse successWarehouseApiResponse) {
        successWarehouseApiResponse.setStatusCode("200");
        successWarehouseApiResponse.setMessage("Success");

        return successWarehouseApiResponse;
    }

    /**
     * @param failureWarehouseApiResponse
     * @return
     */
    private WarehouseApiResponse failureResponse(WarehouseApiResponse failureWarehouseApiResponse) {
        failureWarehouseApiResponse.setStatusCode("1400");
        failureWarehouseApiResponse.setMessage("Failed");

        return failureWarehouseApiResponse;
    }

    /**
     * deadlockErrorMessage
     *
     * @param errorDesc
     * @return
     */
    private boolean deadLockException(String errorDesc) {
        if ((errorDesc.contains("SQLState: 40001") || errorDesc.contains("SQL Error: 1205")) ||
                errorDesc.contains("was deadlocked on lock") || errorDesc.contains("CannotAcquireLockException") ||
                errorDesc.contains("LockAcquisitionException") || errorDesc.contains("UnexpectedRollbackException")) {
            return true;
        }
        return false;
    }

    /**
     * @param error
     * @return
     */
    private String errorMessageExtraction(String error) throws Exception {
        String errorDesc = error;
        try {
            if (error.contains("message")) {
                errorDesc = error.substring(error.indexOf("message") + 9);
                errorDesc = errorDesc.replaceAll("}]", "");
            }
            if (error.contains("DataIntegrityViolationException") || error.contains("ConstraintViolationException")) {
                errorDesc = "Null Pointer Exception";
            }
            if (error.contains("CannotAcquireLockException") || error.contains("LockAcquisitionException") ||
                    error.contains("SQLServerException") || error.contains("UnexpectedRollbackException")) {
                errorDesc = "SQLServerException";
            }
            if (error.contains("BadRequestException")) {
                errorDesc = error.substring(error.indexOf("BadRequestException:") + 20);
            }
            return errorDesc;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param referenceField
     * @param error
     * @throws Exception
     */
    private void sendMail(String companyCodeId, String plantId, String languageId, String warehouseId,
                          String refDocNumber, String referenceField, String error) throws Exception {
        try {
            InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
            inboundOrderCancelInput.setCompanyCodeId(companyCodeId);
            inboundOrderCancelInput.setPlantId(plantId);
            inboundOrderCancelInput.setLanguageId(languageId);
            inboundOrderCancelInput.setWarehouseId(warehouseId);
            inboundOrderCancelInput.setRefDocNumber(refDocNumber);
            inboundOrderCancelInput.setReferenceField1(referenceField);
            inboundOrderCancelInput.setRemarks(errorMessageExtraction(error));
            mastersService.sendMail(inboundOrderCancelInput);
        } catch (Exception ex) {
            log.error("Exception occurred while Sending Mail " + ex.toString());
            throw ex;
        }
    }
}