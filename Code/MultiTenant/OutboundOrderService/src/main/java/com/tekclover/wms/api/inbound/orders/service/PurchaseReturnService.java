package com.tekclover.wms.api.inbound.orders.service;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.HHTUser;
import com.tekclover.wms.api.inbound.orders.model.dto.IInventory;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.PickListCancellation;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.ReturnPOHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.ReturnPOLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.ReturnPOV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PurchaseReturnService {

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderRepository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;
    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    protected AuthTokenService authTokenService;
    @Autowired
    StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
    @Autowired
    OrderManagementLineService orderManagementLineService;
    @Autowired
    PushNotificationService pushNotificationService;
    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    PropertiesConfig propertiesConfig;
    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    protected String statusDescription = null;
    @Autowired
    MastersService mastersService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    SalesOrderService salesOrderService;
    @Autowired
    OrderService orderService;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    InventoryV2Repository inventoryV2Repository;
    @Autowired
    InventoryTransRepository inventoryTransRepository;


    /**
     * @param returnPO purchase_return
     * @return return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<ReturnPOV2> createPurchaseRetrunList(List<ReturnPOV2> returnPO) {
        List<ReturnPOV2> purchaseReturns = Collections.synchronizedList(new ArrayList<>());
        log.info("Outbound Process Start {} Purchase Retrun", returnPO);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            for (ReturnPOV2 returnOrder : returnPO) {
                ReturnPOHeaderV2 header = returnOrder.getReturnPOHeader();
                List<ReturnPOLineV2> lineV2List = returnOrder.getReturnPOLine();
                String companyCode = header.getCompanyCode();
                String plantId = header.getBranchCode();
                String newPickListNo = header.getPoNumber();
                String orderType = lineV2List.get(0).getOrderType();

                Optional<PreOutboundHeaderV2> orderProcessedStatus =
                        preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                                newPickListNo, 2L, 0L);

                if (!orderProcessedStatus.isEmpty()) {
                    throw new BadRequestException("Order :" + newPickListNo +
                            " already processed. Reprocessing can't be allowed.");
                }

                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                Warehouse WH = dbWarehouse.get();
                String warehouseId = WH.getWarehouseId();
                String languageId = WH.getLanguageId();
                log.info("Warehouse ID: {}", warehouseId);

                PickListCancellation createPickListCancellation = null;
                String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);

                // Description_Set
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
                String companyText = description.getCompanyDesc();
                String plantText = description.getPlantDesc();
                String warehouseText = description.getWarehouseDesc();

                // PreBoundHeader
                PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeaderV2(companyCode, plantId, languageId, warehouseId,
                        preOutboundNo, header, orderType, companyText, plantText, warehouseText);
                log.info("preOutboundHeader Created : " + preOutboundHeader);

                // OrderManagementHeader
                OrderManagementHeaderV2 orderManagementHeader = createOrderManagementHeaderV2(preOutboundHeader);
                log.info("OrderManagementHeader Created : " + orderManagementHeader);

                // OutboundHeader
                OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(preOutboundHeader, orderManagementHeader.getStatusId(), header);
                log.info("OutboundHeader Created : " + outboundHeader);

                // Collections for batch saving
                List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
                List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
                List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());

                // Process lines in parallel
                List<CompletableFuture<Void>> futures = lineV2List.stream()
                        .map(outBoundLine -> CompletableFuture.runAsync(() -> {
                            try {
                                processSinglePurchaseReturnLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
                                        preOutboundLines, outboundLines, managementLines);
                            } catch (Exception e) {
                                log.error("Error processing Purchase Retrun Line for SalesOrder: {}", header.getPoNumber(), e);
                                throw new RuntimeException(e);
                            }
                        }, executorService))
                        .collect(Collectors.toList());

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                try {
                    allFutures.join(); // Wait for all tasks to finish
                } catch (CompletionException e) {
                    log.error("Exception during Purchase Retrun line processing: {}", e.getCause().getMessage());
                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
                }
                // Batch Save All Records
                preOutboundLineV2Repository.saveAll(preOutboundLines);
                orderManagementLineV2Repository.saveAll(managementLines);
                outboundLineV2Repository.saveAll(outboundLines);

                // No_Stock_Items
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
                orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCode, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
                log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

                purchaseReturns.add(returnOrder);
            }
        } catch (
                Exception e) {
            log.error("Error processing outbound purchase return Lines", e);
            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Outbound Process Completed for {} Purchase Return", purchaseReturns.size());
        return purchaseReturns;
    }

    /**
     * @param outBoundLine
     * @param preOutboundHeaderV2
     * @param orderManagementHeaderV2
     * @param outboundHeader
     * @param preOutboundLines
     * @param outboundLines
     * @param managementLines
     */
    private void processSinglePurchaseReturnLine(ReturnPOLineV2 outBoundLine, PreOutboundHeaderV2 preOutboundHeaderV2,
                                                 OrderManagementHeaderV2 orderManagementHeaderV2, OutboundHeaderV2 outboundHeader,
                                                 List<PreOutboundLineV2> preOutboundLines, List<OutboundLineV2> outboundLines,
                                                 List<OrderManagementLineV2> managementLines) {
        // Collect PreOutboundLine
        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(preOutboundHeaderV2, outBoundLine);
        preOutboundLines.add(preOutboundLine);
        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);

        // Collect OrderManagementLine
        OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(orderManagementHeaderV2, preOutboundLine);
        managementLines.add(orderManagementLine);
        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);

        // Collect InboundLine
        OutboundLineV2 outboundLine = createOutboundLineV2(orderManagementLine, outboundHeader);
        outboundLines.add(outboundLine);
        log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLine);
    }

    /**
     * @param orderManagementLineV2
     * @param outboundHeaderV2
     * @return
     */
    private OutboundLineV2 createOutboundLineV2(OrderManagementLineV2 orderManagementLineV2, OutboundHeaderV2 outboundHeaderV2) {
        OutboundLineV2 outboundLine = new OutboundLineV2();
        BeanUtils.copyProperties(orderManagementLineV2, outboundLine, CommonUtils.getNullPropertyNames(orderManagementLineV2));
        outboundLine.setDeliveryQty(0D);
        outboundLine.setCreatedBy(orderManagementLineV2.getPickupCreatedBy());
        outboundLine.setCreatedOn(orderManagementLineV2.getPickupCreatedOn());
        outboundLine.setInvoiceDate(outboundHeaderV2.getRequiredDeliveryDate());
        outboundLine.setSalesInvoiceNumber(outboundHeaderV2.getSalesInvoiceNumber());
        outboundLine.setSalesOrderNumber(outboundHeaderV2.getSalesOrderNumber());
        outboundLine.setPickListNumber(outboundHeaderV2.getPickListNumber());
        outboundLine.setCustomerType("INVOICE");
        return outboundLine;
    }

    /**
     * @param orderManagementHeaderV2
     * @param preOutboundLine
     * @return
     */
    private OrderManagementLineV2 createOrderManagementLineV2(OrderManagementHeaderV2 orderManagementHeaderV2, PreOutboundLineV2 preOutboundLine) {
        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
        orderManagementLine.setPickupCreatedOn(new Date());

        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(preOutboundLine.getItemCode(),
                preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId(),
                preOutboundLine.getManufacturerName(),
                preOutboundLine.getLanguageId());
        log.info("Barcode : " + barcode);

        if (barcode != null && !barcode.isEmpty()) {
            orderManagementLine.setBarcodeId(barcode.get(0));
            orderManagementLine.setItemBarcode(barcode.get(0));
        }

        List<IInventoryImpl> stockType1InventoryList = inventoryService.
                getInventoryForOrderManagementV2(orderManagementHeaderV2.getCompanyCodeId(), orderManagementHeaderV2.getPlantId(), orderManagementHeaderV2.getLanguageId(), orderManagementHeaderV2.getWarehouseId(),
                        preOutboundLine.getItemCode(), 1L, 7L, orderManagementLine.getManufacturerName());
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            orderManagementLine.setStatusId(47L);
            statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
            orderManagementLine.setReferenceField7(statusDescription);
            orderManagementLine.setProposedStorageBin("");
            orderManagementLine.setProposedPackBarCode("");
            orderManagementLine.setInventoryQty(0D);
            orderManagementLine.setAllocatedQty(0D);
            orderManagementLine.setStatusDescription(statusDescription);
            return orderManagementLine;
        }

        orderManagementLine = updateAllocationV2(orderManagementLine, 7L, preOutboundLine.getOrderQty(),
                preOutboundLine.getWarehouseId(), preOutboundLine.getItemCode(), "ORDER_PLACED");

        // PROP_ST_BIN
        return orderManagementLine;
    }

    /**
     * @param orderManagementLine
     * @param binClassId
     * @param ORD_QTY
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     * @return
     */
    private OrderManagementLineV2 updateAllocationV2(OrderManagementLineV2 orderManagementLine, Long binClassId, Double ORD_QTY, String warehouseId, String itemCode, String loginUserID) {
        // Inventory Strategy Choices
        String INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        log.info("Allocation Strategy: " + INV_STRATEGY);
        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        List<IInventoryImpl> stockType1InventoryList =
                inventoryService.getInventoryForOrderManagementV2(orderManagementLine.getCompanyCodeId(),
                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                        warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        // -----------------------------------------------------------------------------------------------------------------------------------------
        // Getting Inventory GroupBy ST_BIN wise

        List<IInventoryImpl> finalInventoryList = null;
        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
            log.info("SB_CTD_ON");
            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByCtdOnV2(orderManagementLine.getCompanyCodeId(),
                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
            log.info("SB_LEVEL_ID");
            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("SB_BEST_FIT");
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
            log.info("Group By LeveId: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
                if (ORD_QTY <= iInventory.getInventoryQty()) {
                    finalInventoryList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, 1L, binClassId, iInventory.getLevelId(), orderManagementLine.getManufacturerName());
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    if (finalInventoryList != null && finalInventoryList.isEmpty()) {
                        return updateOrderManagementLineV2(orderManagementLine);
                    }
                    outerloop1:
                    for (IInventoryImpl stBinInventory : finalInventoryList) {
                        Long STATUS_ID = 0L;
                        Double ALLOC_QTY = 0D;

                        /*
                         * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
                         * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                         * ALLOC_QTY=0
                         */
                        Double INV_QTY = stBinInventory.getInventoryQuantity();

                        // INV_QTY
                        orderManagementLine.setInventoryQty(INV_QTY);

                        if (ORD_QTY <= INV_QTY) {
                            ALLOC_QTY = ORD_QTY;
                        } else if (ORD_QTY > INV_QTY) {
                            ALLOC_QTY = INV_QTY;
                        } else if (INV_QTY == 0) {
                            ALLOC_QTY = 0D;
                        }
                        log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

                        if (orderManagementLine.getStatusId() == 47L) {
                            try {
                                orderManagementLine = null;
//                                orderManagementLineV2Repository.delete(orderManagementLine);
                                log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                            } catch (Exception e) {
                                log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                                e.printStackTrace();
                            }
                        }

                        orderManagementLine.setAllocatedQty(ALLOC_QTY);
                        orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                        // STATUS_ID
                        /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                        if (ORD_QTY > ALLOC_QTY) {
                            STATUS_ID = 42L;
                        }

                        /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                        if (ORD_QTY == ALLOC_QTY) {
                            STATUS_ID = 43L;
                        }

                        statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                        orderManagementLine.setStatusId(STATUS_ID);
                        orderManagementLine.setStatusDescription(statusDescription);
                        orderManagementLine.setReferenceField7(statusDescription);
                        orderManagementLine.setPickupUpdatedBy(loginUserID);
                        orderManagementLine.setPickupUpdatedOn(new Date());

                        double allocatedQtyFromOrderMgmt = 0.0;

                        /*
                         * Deleting current record and inserting new record (since UK is not allowing to
                         * update prop_st_bin and Pack_bar_codes columns
                         */
                        newOrderManagementLine = new OrderManagementLineV2();
                        BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine,
                                CommonUtils.getNullPropertyNames(orderManagementLine));

                        //V2 Code
                        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                                orderManagementLine.getLanguageId(),
                                orderManagementLine.getPlantId(),
                                orderManagementLine.getWarehouseId());

                        newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                        newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                        newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());

                        newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                        if (stBinInventory.getBarcodeId() != null) {
                            newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
                        }
                        if (stBinInventory.getLevelId() != null) {
                            newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
                        }
                        newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
//                        OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);

                        allocatedQtyFromOrderMgmt = newOrderManagementLine.getAllocatedQty();
                        log.info("allocatedQtyFromOrderMgmt------: " + newOrderManagementLine);

                        if (ORD_QTY > ALLOC_QTY) {
                            ORD_QTY = ORD_QTY - ALLOC_QTY;
                        }

                        if (allocatedQtyFromOrderMgmt > 0) {
                            // Update Inventory table
                            InventoryV2 inventoryForUpdate = inventoryService.getInventoryForAllocationV2(orderManagementLine.getCompanyCodeId(),
                                    orderManagementLine.getPlantId(),
                                    orderManagementLine.getLanguageId(), warehouseId,
                                    stBinInventory.getPackBarcodes(), itemCode,
                                    orderManagementLine.getManufacturerName(),
                                    stBinInventory.getStorageBin());

                            double dbInventoryQty = 0;
                            double dbInvAllocatedQty = 0;

                            if (inventoryForUpdate.getInventoryQuantity() != null) {
                                dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
                            }

                            if (inventoryForUpdate.getAllocatedQuantity() != null) {
                                dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
                            }

                            double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
                            double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;

                            /*
                             * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                             */
                            // Start
                            if (inventoryQty < 0) {
                                inventoryQty = 0;
                            }
                            // End
                            inventoryForUpdate.setInventoryQuantity(inventoryQty);
                            inventoryForUpdate.setAllocatedQuantity(allocatedQty);
                            inventoryForUpdate.setReferenceField4(inventoryQty + allocatedQty);
                            // Create new Inventory Record
                            InventoryV2 inventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, CommonUtils.getNullPropertyNames(inventoryForUpdate));
                            inventoryV2.setUpdatedOn(new Date());
                            try {
                                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                                log.info("-----Inventory2 updated-------: " + inventoryV2);
                            } catch (Exception e) {
                                log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                e.printStackTrace();
                                InventoryTrans newInventoryTrans = new InventoryTrans();
                                BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                                newInventoryTrans.setReRun(0L);
                                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                            }
                        }

                        if (ORD_QTY == ALLOC_QTY) {
                            log.info("ORD_QTY fully allocated: " + ORD_QTY);
                            break outerloop1; // If the Inventory satisfied the Ord_qty
                        }
//                        }
                    }
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (ORD_QTY > iInventory.getInventoryQty()) {
                    toBeIncluded = false;
                }
                if (!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                        warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
            }
        }
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

        ImBasicData1 dbImBasicData1 = null;
        boolean shelfLifeIndicator = false;
        if (finalInventoryList != null && !finalInventoryList.isEmpty()) {

            ImBasicData imBasicData = new ImBasicData();
            imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
            imBasicData.setPlantId(orderManagementLine.getPlantId());
            imBasicData.setLanguageId(orderManagementLine.getLanguageId());
            imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
            imBasicData.setItemCode(itemCode);
            imBasicData.setManufacturerName(orderManagementLine.getManufacturerName());
            dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());

//            dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(itemCode,
//                    orderManagementLine.getLanguageId(), orderManagementLine.getCompanyCodeId(),
//                    orderManagementLine.getPlantId(), orderManagementLine.getWarehouseId(),
//                    orderManagementLine.getManufacturerName(), authTokenForMastersService.getAccess_token());

            log.info("ImBasicData1: " + dbImBasicData1);
            if (dbImBasicData1 != null) {
                if (dbImBasicData1.getShelfLifeIndicator() != null) {
                    shelfLifeIndicator = dbImBasicData1.getShelfLifeIndicator();
                }
            }
        }

        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
            //ascending sort - expiryDate
            if (shelfLifeIndicator) {
                finalInventoryList = finalInventoryList.stream().filter(n -> n.getExpiryDate() != null).sorted(Comparator.comparing(IInventoryImpl::getExpiryDate)).collect(Collectors.toList());
            }
            //ascending sort - created on
            if (!shelfLifeIndicator) {
                finalInventoryList = finalInventoryList.stream().filter(n -> n.getCreatedOn() != null).sorted(Comparator.comparing(IInventoryImpl::getCreatedOn)).collect(Collectors.toList());
            }
        }

        outerloop:
        for (IInventoryImpl stBinInventory : finalInventoryList) {

            Long STATUS_ID = 0L;
            Double ALLOC_QTY = 0D;

            /*
             * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
             * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
             * ALLOC_QTY=0
             */
            Double INV_QTY = stBinInventory.getInventoryQuantity();

            // INV_QTY
            orderManagementLine.setInventoryQty(INV_QTY);

            if (ORD_QTY <= INV_QTY) {
                ALLOC_QTY = ORD_QTY;
            } else if (ORD_QTY > INV_QTY) {
                ALLOC_QTY = INV_QTY;
            } else if (INV_QTY == 0) {
                ALLOC_QTY = 0D;
            }
            log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

            if (orderManagementLine.getStatusId() == 47L) {
                try {
                    orderManagementLine = null;
//                    orderManagementLineV2Repository.delete(orderManagementLine);
                    log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                } catch (Exception e) {
                    log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                    e.printStackTrace();
                }
            }

            orderManagementLine.setAllocatedQty(ALLOC_QTY);
            orderManagementLine.setReAllocatedQty(ALLOC_QTY);

            // STATUS_ID
            /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
            if (ORD_QTY > ALLOC_QTY) {
                STATUS_ID = 42L;
            }

            /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
            if (ORD_QTY == ALLOC_QTY) {
                STATUS_ID = 43L;
            }

//                StatusId idStatus = idmasterService.getStatus(STATUS_ID, orderManagementLine.getWarehouseId(), idmasterAuthToken.getAccess_token());
            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
            orderManagementLine.setStatusId(STATUS_ID);
            orderManagementLine.setStatusDescription(statusDescription);
            orderManagementLine.setReferenceField7(statusDescription);
            orderManagementLine.setPickupUpdatedBy(loginUserID);
            orderManagementLine.setPickupUpdatedOn(new Date());

            double allocatedQtyFromOrderMgmt = 0.0;

            /*
             * Deleting current record and inserting new record (since UK is not allowing to
             * update prop_st_bin and Pack_bar_codes columns
             */
            newOrderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine,
                    CommonUtils.getNullPropertyNames(orderManagementLine));

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                    orderManagementLine.getLanguageId(),
                    orderManagementLine.getPlantId(),
                    orderManagementLine.getWarehouseId());

            newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
            newOrderManagementLine.setPlantDescription(description.getPlantDesc());
            newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());

            newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
            if (stBinInventory.getBarcodeId() != null) {
                newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
            }
            if (stBinInventory.getLevelId() != null) {
                newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
            }
            newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
//            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
            allocatedQtyFromOrderMgmt = newOrderManagementLine.getAllocatedQty();
            log.info("allocatedQtyFromOrderMgmt------: " + newOrderManagementLine.getAllocatedQty());

            if (ORD_QTY > ALLOC_QTY) {
                ORD_QTY = ORD_QTY - ALLOC_QTY;
            }

            if (allocatedQtyFromOrderMgmt > 0) {
                // Update Inventory table
                InventoryV2 inventoryForUpdate = inventoryService.getInventoryForAllocationV2(orderManagementLine.getCompanyCodeId(),
                        orderManagementLine.getPlantId(),
                        orderManagementLine.getLanguageId(), warehouseId,
                        stBinInventory.getPackBarcodes(), itemCode,
                        orderManagementLine.getManufacturerName(),
                        stBinInventory.getStorageBin());

                double dbInventoryQty = 0;
                double dbInvAllocatedQty = 0;

                if (inventoryForUpdate.getInventoryQuantity() != null) {
                    dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
                }

                if (inventoryForUpdate.getAllocatedQuantity() != null) {
                    dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
                }

                double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
                double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;

                /*
                 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                 */
                // Start
                if (inventoryQty < 0) {
                    inventoryQty = 0;
                }
                // End
                inventoryForUpdate.setInventoryQuantity(inventoryQty);
                inventoryForUpdate.setAllocatedQuantity(allocatedQty);
                inventoryForUpdate.setReferenceField4(inventoryQty + allocatedQty);
                // Create new Inventory Record
                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, CommonUtils.getNullPropertyNames(inventoryForUpdate));
                inventoryV2.setUpdatedOn(new Date());
                try {
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----Inventory2 updated-------: " + inventoryV2);
                } catch (Exception e) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                    e.printStackTrace();
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }

            if (ORD_QTY == ALLOC_QTY) {
                log.info("ORD_QTY fully allocated: " + ORD_QTY);
                break outerloop; // If the Inventory satisfied the Ord_qty
            }
//            }
        }
        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
//        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param preOutboundHeaderV2
     * @param returnPOLineV2
     * @return
     */
    private PreOutboundLineV2 createPreOutboundLineV2(PreOutboundHeaderV2 preOutboundHeaderV2, ReturnPOLineV2 returnPOLineV2) {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(preOutboundHeaderV2, preOutboundLine, CommonUtils.getNullPropertyNames(preOutboundHeaderV2));
        BeanUtils.copyProperties(returnPOLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(returnPOLineV2));
        preOutboundLine.setLineNumber(returnPOLineV2.getLineReference());
        preOutboundLine.setItemCode(returnPOLineV2.getSku());
        preOutboundLine.setDescription(returnPOLineV2.getSkuDescription());
        preOutboundLine.setOutboundOrderTypeId(2L);
        preOutboundLine.setStatusId(39L);
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setManufacturerName(returnPOLineV2.getManufacturerName());
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeaderV2.getLanguageId());
        preOutboundLine.setStatusDescription(statusDescription);
        preOutboundLine.setReferenceDocumentType("Purchase Return");
        preOutboundLine.setSupplierInvoiceNo(returnPOLineV2.getSupplierInvoiceNo());
        preOutboundLine.setReturnOrderNo(returnPOLineV2.getReturnOrderNo());
        preOutboundLine.setOrderQty(returnPOLineV2.getReturnQty());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(preOutboundHeaderV2.getCompanyCodeId());
        imBasicData.setPlantId(preOutboundHeaderV2.getPlantId());
        imBasicData.setLanguageId(preOutboundHeaderV2.getLanguageId());
        imBasicData.setWarehouseId(preOutboundHeaderV2.getWarehouseId());
        imBasicData.setItemCode(returnPOLineV2.getSku());
//        imBasicData.setManufacturerName(salesOrderLineV2.getManufacturerName());
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null && imBasicData1.getDescription() != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
        } else {
            preOutboundLine.setDescription(returnPOLineV2.getSkuDescription());
        }

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(preOutboundLine.getItemCode(),
                preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId(),
                preOutboundLine.getManufacturerName(),
                preOutboundLine.getLanguageId());
        log.info("Barcode : " + barcode);

        if (barcode != null && !barcode.isEmpty()) {
            preOutboundLine.setBarcodeId(barcode.get(0));
            preOutboundLine.setItemBarcode(barcode.get(0));
        }
        preOutboundLine.setRequiredDeliveryDate(preOutboundHeaderV2.getRequiredDeliveryDate());
        preOutboundLine.setReferenceField1("Purchase Return");
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());
        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param headerV2
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @throws Exception
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId, ReturnPOHeaderV2 headerV2, String preOutboundNo, String refDocNumber, String partnerCode) throws Exception {
        try {
            List<OrderManagementLineV2> orderManagementLineV2List = orderService.
                    getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

            //pickup header create
            Long assignPickerListCount = 0L;
            Long hhtUserCount = 0L;
            Long lineCount = 0L;

            lineCount = orderManagementLineV2List.stream().count();
            log.info("total Order Lines Count: " + lineCount);

            List<String> assignPickerList = new ArrayList<>();
            List<OrderManagementLineV2> orderManagementLineOrderedList = new ArrayList<>();
            List<OrderManagementLineV2> orderManagementLineRemainingList = new ArrayList<>();
            List<OrderManagementLineV2> orderManagementLineFilterList = new ArrayList<>();
            Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());

            List<String> assignPickerPickUpHeaderList = pickupHeaderV2Repository.getPickupHeaderAssignPickerList(
                    companyCodeId, plantId, languageId, warehouseId, 48L, dates[0], dates[1]);
            log.info("assignPickerPickUpHeaderList : " + assignPickerPickUpHeaderList.size());
            if (assignPickerPickUpHeaderList != null && !assignPickerPickUpHeaderList.isEmpty()) {
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
                    String itmMfrName = dbOrderManagementLine.getItemCode() + dbOrderManagementLine.getManufacturerName();
                    boolean itemPresent = assignPickerPickUpHeaderList.stream().allMatch(a -> a.equalsIgnoreCase(itmMfrName));
                    if (itemPresent) {
                        orderManagementLineFilterList.add(dbOrderManagementLine);
                    }
                    if (!itemPresent) {
                        orderManagementLineRemainingList.add(dbOrderManagementLine);
                    }
                }
                log.info("OrderManagementLine sameItem Present : " + orderManagementLineFilterList.size());
                log.info("OrderManagementLine No sameItem Present : " + orderManagementLineRemainingList.size());
            }
            if (orderManagementLineFilterList != null && !orderManagementLineFilterList.isEmpty()) {
                orderManagementLineOrderedList.addAll(orderManagementLineFilterList);
            }
            if (orderManagementLineRemainingList != null && !orderManagementLineRemainingList.isEmpty()) {
                orderManagementLineOrderedList.addAll(orderManagementLineRemainingList);
            }
            if ((orderManagementLineFilterList == null || orderManagementLineFilterList.isEmpty())
                    && (orderManagementLineRemainingList == null || orderManagementLineRemainingList.isEmpty())) {
                orderManagementLineOrderedList = orderManagementLineV2List;
            }

            for (OrderManagementLineV2 orderManagementLine : orderManagementLineOrderedList) {
                String assignPickerId = null;

                if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                    log.info("orderManagementLine: " + orderManagementLine);

                    int currentTime = DateUtils.getCurrentTime();
                    log.info("CurrentTime: " + currentTime);

//                if (currentTime < 15) {

                    if (warehouseId.equalsIgnoreCase("100")) {
                        log.info("warehouseId: " + warehouseId);

                        Long OB_ORD_TYP_ID = 2L;
                        log.info("OutboundOrderTypeId: " + OB_ORD_TYP_ID);

                        List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListNew(OB_ORD_TYP_ID, companyCodeId, plantId, languageId, warehouseId);
                        log.info("hhtUserList: " + hhtUserIdList);

                        List<String> hhtUserList = new ArrayList<>();
                        List<String> absentHhtUserList = new ArrayList<>();
                        if (hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                            for (HHTUser dbHhtUser : hhtUserIdList) {
                                if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                    List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                            dbHhtUser.getCompanyCodeId(),
                                            dbHhtUser.getLanguageId(),
                                            dbHhtUser.getPlantId(),
                                            dbHhtUser.getWarehouseId(),
                                            dbHhtUser.getUserId(),
                                            dbHhtUser.getStartDate(),
                                            dbHhtUser.getEndDate());
                                    log.info("HHt User Absent: " + userPresent);
                                    if (userPresent != null && !userPresent.isEmpty()) {
                                        absentHhtUserList.add(dbHhtUser.getUserId());
                                    } else {
                                        hhtUserList.add(dbHhtUser.getUserId());
                                    }
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            }
                        }
                        log.info("Present HHtUser List: " + hhtUserList);
                        log.info("Absent HHtUser List: " + absentHhtUserList);

                        if (hhtUserList != null && !hhtUserList.isEmpty()) {

                            hhtUserCount = hhtUserList.stream().count();
                            log.info("hhtUserList count: " + hhtUserCount);

                            PickupHeaderV2 assignPickerPickUpHeader = orderService.getPickupHeaderAutomationByOutboundOrderTypeId(companyCodeId, plantId, languageId, warehouseId,
                                    orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName(), OB_ORD_TYP_ID);
                            log.info("pickupHeader--> Status48---> assignPicker---> SameItem ---> same level: " + assignPickerPickUpHeader);
                            if (assignPickerPickUpHeader != null) {
                                List<String> userPresentInSelectedLevel = hhtUserList.stream().filter(n -> n.equalsIgnoreCase(assignPickerPickUpHeader.getAssignedPickerId())).collect(Collectors.toList());
                                log.info("userPresentInSelectedLevel: " + userPresentInSelectedLevel);
                                if (userPresentInSelectedLevel != null && !userPresentInSelectedLevel.isEmpty()) {
                                    log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                                    assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                                }
                            }

                            if (assignPickerId == null) {
                                log.info("assignPickerId: " + assignPickerId);
                                assignPickerList = new ArrayList<>();
                                outerLoop2:
                                for (String hhtUser : hhtUserList) {
                                    PickupHeaderV2 pickerPickupHeaderSameList = orderService.getPickupHeaderAutomateCurrentDateSameOrderNew(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderSameList);
                                    if (pickerPickupHeaderSameList != null) {
                                        assignPickerList.add(hhtUser);
                                        if (assignPickerList.size() > 0) {
                                            break outerLoop2;
                                        }
                                    }
                                }
                                outerLoop1:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = orderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
                                outerLoop3:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = orderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 50L);
                                        List<PickupHeaderV2> pickupHeader48List = orderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 48L);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                        if ((pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
                                                (pickupHeader48List == null || pickupHeader48List.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop3;
                                            }
                                        }
                                    }
                                }
                                outerLoop:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {

                                    List<String> pickerCountList_50 = pickupHeaderV2Repository
                                            .getPickUpheader50AssignPickerObTypeList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 50L, dates[0], dates[1]);
                                    Set<String> pickerCountList_50_filtered = pickerCountList_50.stream().collect(Collectors.toSet());
                                    log.info("assigned Picker status_50L: " + pickerCountList_50);
                                    log.info("assigned Picker status_50L_filtered: " + pickerCountList_50_filtered);
                                    if (pickerCountList_50_filtered != null && !pickerCountList_50_filtered.isEmpty()) {
                                        for (String dbAssignpickerId : pickerCountList_50_filtered) {
                                            List<String> pickerCountList_48 = pickupHeaderV2Repository
                                                    .getPickUpheaderAssignPickerAmgharaListNew(companyCodeId, plantId, languageId, warehouseId, dbAssignpickerId, OB_ORD_TYP_ID, 48L, dates[0], dates[1]);
                                            if (pickerCountList_48 == null || pickerCountList_48.isEmpty()) {
                                                IKeyValuePair pickupHeaderPickerByCount = pickupHeaderV2Repository.getAssignPickerNew(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 50L, dates[0], dates[1]);
                                                if (pickupHeaderPickerByCount != null) {
                                                    boolean isPickerSame = dbAssignpickerId.equalsIgnoreCase(pickupHeaderPickerByCount.getAssignPicker());
                                                    if (isPickerSame) {
                                                        assignPickerList.add(dbAssignpickerId);
                                                        log.info("assigned Picker: " + assignPickerList.get(0));
                                                        if (assignPickerList.size() > 0) {
                                                            break outerLoop;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    List<String> pickerCountList = pickupHeaderV2Repository
                                            .getPickUpheaderAssignPickerAmgharaList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 48L, dates[0], dates[1]);
                                    log.info("assigned Picker status_48L: " + pickerCountList);
                                    if (pickerCountList != null && !pickerCountList.isEmpty()) {
                                        assignPickerList.add(pickerCountList.get(0));
                                        log.info("assigned Picker: " + assignPickerList.get(0));
                                        if (assignPickerList.size() > 0) {
                                            break outerLoop;
                                        }
                                    }
                                }
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                                if (assignPickerList != null && !assignPickerList.isEmpty()) {
                                    assignPickerId = assignPickerList.get(0);
                                }
                                log.info("assignPickerId: " + assignPickerId);
                            }
                        }
                    }
//            }
                    if (warehouseId.equalsIgnoreCase("200")) {
                        log.info("warehouseId: " + warehouseId);

                        Long LEVEL_ID = Long.valueOf(orderManagementLine.getLevelId());
                        log.info("levelId: " + LEVEL_ID);

                        List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListByLevelIdNew(LEVEL_ID, companyCodeId, plantId, languageId, warehouseId);
                        log.info("hhtUserList: " + hhtUserIdList);

                        List<String> hhtUserList = new ArrayList<>();
                        List<String> absentHhtUserList = new ArrayList<>();
                        if (hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                            for (HHTUser dbHhtUser : hhtUserIdList) {
                                if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                    List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                            dbHhtUser.getCompanyCodeId(),
                                            dbHhtUser.getLanguageId(),
                                            dbHhtUser.getPlantId(),
                                            dbHhtUser.getWarehouseId(),
                                            dbHhtUser.getUserId(),
                                            dbHhtUser.getStartDate(),
                                            dbHhtUser.getEndDate());
                                    log.info("HHt User Absent: " + userPresent);
                                    if (userPresent != null && !userPresent.isEmpty()) {
                                        absentHhtUserList.add(dbHhtUser.getUserId());
                                    } else {
                                        hhtUserList.add(dbHhtUser.getUserId());
                                    }
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            }
                        }
                        log.info("Present HHtUser List: " + hhtUserList);
                        log.info("Absent HHtUser List: " + absentHhtUserList);

                        if (hhtUserList != null && !hhtUserList.isEmpty()) {

                            hhtUserCount = hhtUserList.stream().count();
                            log.info("hhtUserList count: " + hhtUserCount);

                            PickupHeaderV2 assignPickerPickUpHeader = orderService.getPickupHeaderAutomationByLevelId(companyCodeId, plantId, languageId, warehouseId,
                                    orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName(), String.valueOf(LEVEL_ID));
                            log.info("pickupHeader--> Status48---> assignPicker---> SameItem ---> same level: " + assignPickerPickUpHeader);
                            if (assignPickerPickUpHeader != null) {
                                List<String> userPresentInSelectedLevel = hhtUserList.stream().filter(n -> n.equalsIgnoreCase(assignPickerPickUpHeader.getAssignedPickerId())).collect(Collectors.toList());
                                log.info("userPresentInSelectedLevel: " + userPresentInSelectedLevel);
                                if (userPresentInSelectedLevel != null && !userPresentInSelectedLevel.isEmpty()) {
                                    log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                                    assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                                }
                            }

                            if (assignPickerId == null) {
                                log.info("assignPickerId: " + assignPickerId);
                                assignPickerList = new ArrayList<>();
                                outerLoop2:
                                for (String hhtUser : hhtUserList) {
                                    PickupHeaderV2 pickerPickupHeaderSameList = orderService.getPickupHeaderAutomateCurrentDateSameOrderNew(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderSameList);
                                    if (pickerPickupHeaderSameList != null) {
                                        assignPickerList.add(hhtUser);
                                        if (assignPickerList.size() > 0) {
                                            break outerLoop2;
                                        }
                                    }
                                }
                                outerLoop1:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = orderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
                                outerLoop3:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = orderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 50L);
                                        List<PickupHeaderV2> pickupHeader48List = orderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 48L);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                        if ((pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
                                                (pickupHeader48List == null || pickupHeader48List.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop3;
                                            }
                                        }
                                    }
                                }
                                outerLoop:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {

                                    List<String> pickerCountList_50 = pickupHeaderV2Repository
                                            .getPickUpheader50AssignPickerList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 50L, dates[0], dates[1]);
                                    Set<String> pickerCountList_50_filtered = pickerCountList_50.stream().collect(Collectors.toSet());
                                    log.info("assigned Picker status_50L: " + pickerCountList_50);
                                    log.info("assigned Picker status_50L_filtered: " + pickerCountList_50_filtered);
                                    if (pickerCountList_50_filtered != null && !pickerCountList_50_filtered.isEmpty()) {
                                        for (String dbAssignpickerId : pickerCountList_50_filtered) {
                                            List<String> pickerCountList_48 = pickupHeaderV2Repository
                                                    .getPickUpheaderAssignPickerListNew(companyCodeId, plantId, languageId, warehouseId, dbAssignpickerId, LEVEL_ID, 48L, dates[0], dates[1]);
                                            if (pickerCountList_48 == null || pickerCountList_48.isEmpty()) {
                                                IKeyValuePair pickupHeaderPickerByCount = pickupHeaderV2Repository.getAssignPickerNew(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 50L, dates[0], dates[1]);
                                                if (pickupHeaderPickerByCount != null) {
                                                    boolean isPickerSame = dbAssignpickerId.equalsIgnoreCase(pickupHeaderPickerByCount.getAssignPicker());
                                                    if (isPickerSame) {
                                                        assignPickerList.add(dbAssignpickerId);
                                                        log.info("assigned Picker: " + assignPickerList.get(0));
                                                        if (assignPickerList.size() > 0) {
                                                            break outerLoop;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    List<String> pickerCountList = pickupHeaderV2Repository
                                            .getPickUpheaderAssignPickerList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 48L, dates[0], dates[1]);
                                    log.info("assigned Picker status_48L: " + pickerCountList);
                                    if (pickerCountList != null && !pickerCountList.isEmpty()) {
                                        assignPickerList.add(pickerCountList.get(0));
                                        log.info("assigned Picker: " + assignPickerList.get(0));
                                        if (assignPickerList.size() > 0) {
                                            break outerLoop;
                                        }
                                    }
                                }
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                                if (assignPickerList != null && !assignPickerList.isEmpty()) {
                                    assignPickerId = assignPickerList.get(0);
                                }
                                log.info("assignPickerId: " + assignPickerId);
                            }
                        }
                    }

                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                    log.info("PU_NO : " + PU_NO);

                    newPickupHeader.setAssignedPickerId(assignPickerId);
                    newPickupHeader.setPickupNumber(PU_NO);

                    newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                    // PICK_UOM
                    newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

                    // STATUS_ID
                    newPickupHeader.setStatusId(48L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                    newPickupHeader.setStatusDescription(statusDescription);

                    // ProposedPackbarcode
                    newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                    // REF_FIELD_1
                    newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());

                    newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                    newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                    newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                    newPickupHeader.setSalesOrderNumber(headerV2.getPoNumber());
                    newPickupHeader.setPickListNumber(headerV2.getPoNumber());
                    newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType("Purchase Return");
                    newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
//                    newPickupHeader.setTokenNumber(headerV2.getTokenNumber());
                    newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                    newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                    newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                    newPickupHeader.setImsSaleTypeCode(orderManagementLine.getImsSaleTypeCode());

                    newPickupHeader.setFromBranchCode(headerV2.getBranchCode());
//                    newPickupHeader.setIsCompleted(headerV2.getIsCompleted());
//                    newPickupHeader.setIsCancelled(headerV2.getIsCancelled());
//                    newPickupHeader.setMUpdatedOn(headerV2.getMUpdatedOn());
                    newPickupHeader.setCustomerCode(headerV2.getBranchCode());
                    newPickupHeader.setTransferRequestType("Purchase Return");

                    PickupHeaderV2 createdPickupHeader = orderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                    log.info("pickupHeader created: " + createdPickupHeader);
                    // Update_OrderManement_Line
                    orderManagementLineV2Repository.updateOrderManagementLineV2(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                            48L, statusDescription, assignPickerId, PU_NO, new Date());
                }
            }

            //push notification separated from pickup header and consolidated notification sent
            pushNotificationService.sendPushNotification(preOutboundNo, warehouseId);

            outboundHeaderV2Repository.updateOutboundHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
            orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
        } catch (Exception e) {
            log.error("create PickupHeader error : " + e);
            throw e;
        }
    }

    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, ReturnPOHeaderV2 header) {
        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeader.setCustomerType("INVOICE");
//        outboundHeader.setInvoiceDate(salesOrderHeaderV2.getRequiredDeliveryDate());
        outboundHeader.setCreatedOn(new Date());
        outboundHeader = outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader + "---------------> " + new Date());
        return outboundHeader;
    }

    /**
     * @param createdPreOutboundHeader
     * @return
     */
    private OrderManagementHeaderV2 createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader) {
        OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));

        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"
        statusDescription = stagingLineV2Repository.getStatusDescription(41L, createdPreOutboundHeader.getLanguageId());
        newOrderManagementHeader.setStatusDescription(statusDescription);
        newOrderManagementHeader.setCompanyDescription(createdPreOutboundHeader.getCompanyDescription());
        newOrderManagementHeader.setPlantDescription(createdPreOutboundHeader.getPlantDescription());
        newOrderManagementHeader.setWarehouseDescription(createdPreOutboundHeader.getWarehouseDescription());
        newOrderManagementHeader.setMiddlewareId(createdPreOutboundHeader.getMiddlewareId());
        newOrderManagementHeader.setMiddlewareTable(createdPreOutboundHeader.getMiddlewareTable());
        newOrderManagementHeader.setReferenceDocumentType(createdPreOutboundHeader.getReferenceDocumentType());
        newOrderManagementHeader.setSalesOrderNumber(createdPreOutboundHeader.getSalesOrderNumber());
        newOrderManagementHeader.setPickListNumber(createdPreOutboundHeader.getPickListNumber());
        newOrderManagementHeader.setTokenNumber(createdPreOutboundHeader.getTokenNumber());
        newOrderManagementHeader.setTargetBranchCode(createdPreOutboundHeader.getTargetBranchCode());
        newOrderManagementHeader.setFromBranchCode(createdPreOutboundHeader.getFromBranchCode());
        newOrderManagementHeader.setIsCompleted(createdPreOutboundHeader.getIsCompleted());
        newOrderManagementHeader.setIsCancelled(createdPreOutboundHeader.getIsCancelled());
        newOrderManagementHeader.setMUpdatedOn(createdPreOutboundHeader.getMUpdatedOn());

        newOrderManagementHeader.setReferenceField7(statusDescription);
        return orderManagementHeaderV2Repository.save(newOrderManagementHeader);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param headerV2
     * @param orderType
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String preOutboundNo, ReturnPOHeaderV2 headerV2, String orderType, String companyText, String plantText, String warehouseText) {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(headerV2, preOutboundHeader, CommonUtils.getNullPropertyNames(headerV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCode);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(headerV2.getPoNumber());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(headerV2.getBranchCode());
        preOutboundHeader.setOutboundOrderTypeId(2L);
//        preOutboundHeader.setReferenceDocumentType("SO");                                                // Hardcoded value "SO"
//        preOutboundHeader.setCustomerCode(headerV2.getC());
        preOutboundHeader.setTransferRequestType("Purchase Return");
        preOutboundHeader.setCompanyDescription(companyText);
        preOutboundHeader.setPlantDescription(plantText);
        preOutboundHeader.setWarehouseDescription(warehouseText);

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
//        preOutboundHeader.setRequiredDeliveryDate(headerV2.getRequiredDeliveryDate());  //test
        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(orderType);
        preOutboundHeader.setMiddlewareId(headerV2.getMiddlewareId());
        preOutboundHeader.setMiddlewareTable(headerV2.getMiddlewareTable());
        preOutboundHeader.setReferenceDocumentType("Purchase Return");
        preOutboundHeader.setSalesOrderNumber(headerV2.getPoNumber());
        preOutboundHeader.setPickListNumber(headerV2.getPoNumber());
        preOutboundHeader.setTargetBranchCode(headerV2.getBranchCode());

        preOutboundHeader.setFromBranchCode(headerV2.getBranchCode());
//        preOutboundHeader.setIsCompleted(headerV2.getIsCompleted());
//        preOutboundHeader.setIsCancelled(headerV2.getIsCancelled());
//        preOutboundHeader.setMUpdatedOn(headerV2.getMUpdatedOn());

        // Status Description
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        // REF_FIELD_10
        preOutboundHeader.setReferenceField10(statusDescription);
        preOutboundHeader.setStatusDescription(statusDescription);
        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy("MW_AMS");
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderV2Repository.save(preOutboundHeader);
        log.info("Finished createdPreOutboundHeader : " + createdPreOutboundHeader + " ---------> " + new Date());
        return createdPreOutboundHeader;
    }

    /**
     * @param warehouseId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    private String getPreOutboundNo(String warehouseId, String companyCode, String plantId, String languageId) {
        try {
            String nextRangeNumber = getNextRangeNumber(9L, companyCode, plantId, languageId, warehouseId);
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param NUM_RAN_CODE
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
                                        String languageId, String warehouseId) {
        String nextNumberRange = mastersService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId);
        return nextNumberRange;
    }
}
