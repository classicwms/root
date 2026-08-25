//package com.tekclover.wms.api.inbound.orders.service;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.microsoft.sqlserver.jdbc.SQLServerException;
//import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
//import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
//import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
//import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
//import com.tekclover.wms.api.inbound.orders.model.common.DbConfig;
//import com.tekclover.wms.api.inbound.orders.model.dto.*;
//import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.InventoryMovement;
//import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
//import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.AddPickupLine;
//import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.PickupLine;
//import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.AddQualityLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
//import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.*;
//import com.tekclover.wms.api.inbound.orders.repository.*;
//import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
//import com.tekclover.wms.api.inbound.orders.util.DateUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.exception.LockAcquisitionException;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.CannotAcquireLockException;
//import org.springframework.data.repository.query.Param;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.UnexpectedRollbackException;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.validation.Valid;
//import java.lang.reflect.InvocationTargetException;
//import java.sql.SQLException;
//import java.text.ParseException;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class SalesOrderServiceV6 extends BaseService {
//
//    private final RepositoryProvider repo;
//    @Autowired
//    PickListHeaderRepository pickListHeaderRepository;
//    @Autowired
//    PickupHeaderV2Repository pickupHeaderV2Repository;
//    @Autowired
//    PickupLineV2Repository pickupLineV2Repository;
//    @Autowired
//    InventoryService inventoryService;
//    @Autowired
//    InventoryV2Repository inventoryV2Repository;
//    @Autowired
//    InventoryTransRepository inventoryTransRepository;
//    @Autowired
//    QualityHeaderV2Repository qualityHeaderV2Repository;
//    @Autowired
//    QualityLineV2Repository qualityLineV2Repository;
//    @Autowired
//    InventoryMovementRepository inventoryMovementRepository;
//    @Autowired
//    OutboundOrderV2Repository outboundOrderV2Repository;
//    @Autowired
//    OutboundLineInterimRepository outboundLineInterimRepository;
//    @Autowired
//    DbConfigRepository dbConfigRepository;
//    protected String MW_AMS = "BP_ADMIN";
//
//
//    /**
//     * @param salesOrders
//     * @throws ParseException
//     */
//    @Async("asyncTaskExecutor")
//    public void outboundProcess(List<SalesOrderV2> salesOrders) throws ParseException {
//
//        for (SalesOrderV2 salesOrder : salesOrders) {
//            createSalesOrderList(salesOrder);
//        }
//    }
//
//
//    /**
//     * @param salesOrder
//     * @return
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
//    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
//            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
//    public void createSalesOrderList(SalesOrderV2 salesOrder) throws ParseException {
//
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//        try {
//
//            postSalesOrderV2(salesOrder);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("BP");
//            SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
//            List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
//            String companyCode = header.getCompanyCode();
//            String plantId = header.getBranchCode();
//            String newPickListNo = header.getPickListNumber();
//            String orderType = lineV2List.get(0).getOrderType();
//            String refDocNumber = header.getSalesOrderNumber();
////                    String warehouseId = header.getWarehouseId();
//
//            log.info("company , plant {} {}", companyCode, plantId);
////                    // Get Warehouse
//            Optional<Warehouse> dbWarehouse =
//                    repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
//            Warehouse WH = dbWarehouse.get();
//            String warehouseId = WH.getWarehouseId();
//            String languageId = header.getLanguageId();
//            log.info("Warehouse ID: {}", warehouseId);
//
//            PickListCancellation createPickListCancellation = null;
//            String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);
//
//            //PickList Cancellation
//            log.info("Executing PickList cancellation scenario pre - checkup process");
//            String salesOrderNumber = header.getSalesOrderNumber();
//
//            Optional<PreOutboundHeaderV2> orderProcessedStatus =
//                    repo.preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
//                            refDocNumber, 3L, 0L);
//
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }
//
//            // PreBoundHeader
//            PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeaderV6(companyCode, plantId, languageId, warehouseId,
//                    preOutboundNo, header, orderType, MW_AMS);
//            log.info("preOutboundHeader Created : " + preOutboundHeader);
//
//            // OrderManagementHeader
//            OrderManagementHeaderV2 orderManagementHeader = createOrderManagementHeaderV6(preOutboundHeader, MW_AMS);
//            log.info("OrderManagementHeader Created : " + orderManagementHeader);
//
//            // OutboundHeader
//            OutboundHeaderV2 outboundHeader = createOutboundHeaderV6(preOutboundHeader, orderManagementHeader.getStatusId(), header, MW_AMS);
//            log.info("OutboundHeader Created : " + outboundHeader);
//
//            // Collections for batch saving
//            List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
//            List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
//            List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());
//
//
//            // Process lines in parallel
//            List<CompletableFuture<Void>> futures = lineV2List.stream()
//                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
//                        try {
//                            processSingleSaleOrderLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
//                                    preOutboundLines, outboundLines, managementLines);
//                        } catch (Exception e) {
//                            log.error("Error processing PICK_LIST Line for SalesOrder: {}", header.getSalesOrderNumber(), e);
//                            throw new RuntimeException(e);
//                        }
//                    }, executorService))
//                    .collect(Collectors.toList());
//
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            try {
//                allFutures.join(); // Wait for all tasks to finish
//            } catch (CompletionException e) {
//                log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
//                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//            }
//
//            // Batch Save All Records
//            repo.preOutboundLineV2Repository.saveAll(preOutboundLines);
//            repo.orderManagementLineV2Repository.saveAll(managementLines);
//            repo.outboundLineV2Repository.saveAll(outboundLines);
//
//            // No_Stock_Items
//            statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
//            repo.orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCode, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
//            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
//
//            createPickupHeaderNo(companyCode, plantId, languageId, warehouseId, refDocNumber, preOutboundNo);
//
//            updateStatusId(companyCode, plantId, warehouseId, refDocNumber, 10L);
//        } catch (Exception e) {
//            log.error("Error processing outbound PICK_LIST Lines", e);
//            updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode(),
//                    salesOrder.getSalesOrderHeader().getWarehouseId(), salesOrder.getSalesOrderHeader().getSalesOrderNumber(), 100L);
//
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("MT");
//
//            updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode(),
//                    salesOrder.getSalesOrderHeader().getWarehouseId(), salesOrder.getSalesOrderHeader().getSalesOrderNumber(), 100L);
//            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//        }
//    }
//
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param salesOrderHeaderV2
//     * @param refField1ForOrderType
//     * @param loginUserId
//     * @return
//     * @throws ParseException
//     */
//    public PreOutboundHeaderV2 createPreOutboundHeaderV6(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
//                                                         SalesOrderHeaderV2 salesOrderHeaderV2, String refField1ForOrderType, String loginUserId) throws ParseException {
//        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
//        BeanUtils.copyProperties(salesOrderHeaderV2, preOutboundHeader, CommonUtils.getNullPropertyNames(salesOrderHeaderV2));
//        preOutboundHeader.setLanguageId(languageId);
//        preOutboundHeader.setCompanyCodeId(companyCodeId);
//        preOutboundHeader.setPlantId(plantId);
//        preOutboundHeader.setWarehouseId(warehouseId);
//        preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
//        preOutboundHeader.setConsignment(salesOrderHeaderV2.getSalesOrderNumber());
//        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
//        preOutboundHeader.setOutboundOrderTypeId(3L);    // Hardcoded value "0"
//        preOutboundHeader.setReferenceDocumentType("SaleOrder");
//        preOutboundHeader.setRefDocDate(new Date());
//        preOutboundHeader.setStatusId(39L);
////        preOutboundHeader.setRequiredDeliveryDate(salesOrderHeaderV2.getRequiredDeliveryDate());
//        // REF_FIELD_1
//        preOutboundHeader.setReferenceField1(refField1ForOrderType);
//
//        if (salesOrderHeaderV2.getPickListNumber() != null && salesOrderHeaderV2.getStoreID() != null) {
//            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());
//            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getStoreID());
//            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getPickListNumber());
//        } else {
//            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
//            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getBranchCode());
//            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getSalesOrderNumber());
//        }
//
//        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//        if (description != null) {
//            preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
//            preOutboundHeader.setPlantDescription(description.getPlantDesc());
//            preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());
//        }
//        statusDescription = getStatusDescription(39L, languageId);
//        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
//        preOutboundHeader.setReferenceField10(statusDescription);
//        preOutboundHeader.setStatusDescription(statusDescription);
//        preOutboundHeader.setDeletionIndicator(0L);
//        preOutboundHeader.setCreatedBy(loginUserId);
//        preOutboundHeader.setCreatedOn(new Date());
//        PreOutboundHeaderV2 createdPreOutboundHeader = repo.preOutboundHeaderV2Repository.save(preOutboundHeader);
////        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
//        return createdPreOutboundHeader;
//    }
//
//    /**
//     * @param createdPreOutboundHeader
//     * @return
//     */
//    public OrderManagementHeaderV2 createOrderManagementHeaderV6(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
//        OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
//        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"
//        statusDescription = getStatusDescription(41L, createdPreOutboundHeader.getLanguageId());
//        newOrderManagementHeader.setStatusDescription(statusDescription);
//        newOrderManagementHeader.setReferenceField7(statusDescription);
//        newOrderManagementHeader.setPickupCreatedBy(loginUserId);
//        newOrderManagementHeader.setPickupCreatedOn(new Date());
//        return repo.orderManagementHeaderV2Repository.save(newOrderManagementHeader);
//    }
//
//    /**
//     * @param createdPreOutboundHeader
//     * @param statusId
//     * @param loginUserId
//     * @return
//     * @throws ParseException
//     */
//    public OutboundHeaderV2 createOutboundHeaderV6(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, SalesOrderHeaderV2 salesOrderHeaderV2, String loginUserId) throws ParseException {
//
//        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
//        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//        outboundHeader.setRefDocNumber(outboundHeader.getRefDocNumber());
//        outboundHeader.setRefDocDate(new Date());
//        outboundHeader.setStatusId(statusId);
//        statusDescription = getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
//        outboundHeader.setStatusDescription(statusDescription);
////        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
//        outboundHeader.setConsignment(salesOrderHeaderV2.getPickListNumber());
//        outboundHeader.setInvoiceNumber(salesOrderHeaderV2.getInvoice());
//        if (outboundHeader.getOutboundOrderTypeId() == 3L) {
//            outboundHeader.setCustomerType("INVOICE");
//        }
//        if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
//            outboundHeader.setCustomerType("TRANSVERSE");
//        }
//        outboundHeader.setCreatedBy(loginUserId);
//        outboundHeader.setCreatedOn(new Date());
//        outboundHeader = repo.outboundHeaderV2Repository.save(outboundHeader);
//        log.info("Created outboundHeader : " + outboundHeader);
//        return outboundHeader;
//    }
//
//    /**
//     * @param createdPreOutboundHeader
//     * @param statusId
//     * @param loginUserId
//     * @return
//     * @throws ParseException
//     */
//    public OutboundHeaderV2 createOutboundHeader(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, String loginUserId) throws ParseException {
//
//        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
//        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//        outboundHeader.setRefDocNumber(outboundHeader.getRefDocNumber());
//        outboundHeader.setRefDocDate(new Date());
//        outboundHeader.setStatusId(statusId);
//        statusDescription = getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
//        outboundHeader.setStatusDescription(statusDescription);
////        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
//        outboundHeader.setConsignment(createdPreOutboundHeader.getPickListNumber());
////        outboundHeader.setInvoiceNumber(salesOrderHeaderV2.getInvoice());
//        if (outboundHeader.getOutboundOrderTypeId() == 3L) {
//            outboundHeader.setCustomerType("INVOICE");
//        }
//        if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
//            outboundHeader.setCustomerType("TRANSVERSE");
//        }
//        outboundHeader.setCreatedBy(loginUserId);
//        outboundHeader.setCreatedOn(new Date());
//        outboundHeader = repo.outboundHeaderV2Repository.save(outboundHeader);
//        log.info("Created outboundHeader : " + outboundHeader);
//        return outboundHeader;
//    }
//
//    /**
//     * @param salesOrderLineV2
//     * @param preOutboundHeaderV2
//     * @param orderManagementHeaderV2
//     * @param outboundHeaderV2
//     * @param preOutboundLineV2
//     * @param outboundLineV2
//     * @param orderManagementLineV2
//     * @throws Exception
//     */
//    private void processSingleSaleOrderLine(SalesOrderLineV2 salesOrderLineV2, PreOutboundHeaderV2 preOutboundHeaderV2,
//                                            OrderManagementHeaderV2 orderManagementHeaderV2, OutboundHeaderV2 outboundHeaderV2,
//                                            List<PreOutboundLineV2> preOutboundLineV2, List<OutboundLineV2> outboundLineV2,
//                                            List<OrderManagementLineV2> orderManagementLineV2) throws Exception {
//
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//        // Collect PreOutboundLine
//        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV6(preOutboundHeaderV2, salesOrderLineV2, MW_AMS);
//        preOutboundLineV2.add(preOutboundLine);
//        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);
//
//        // Collect OrderManagementLine
//        OrderManagementLineV2 orderManagementLine = createOrderManagementLine(orderManagementHeaderV2, preOutboundLine, MW_AMS);
//        orderManagementLineV2.add(orderManagementLine);
//        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);
//
//        // Collect InboundLine
//        OutboundLineV2 outboundLine = createOutboundLineV6(orderManagementLine, outboundHeaderV2);
//        outboundLineV2.add(outboundLine);
//        log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLine);
//
//    }
//
//
//    /**
//     * @param loginUserId
//     * @return
//     * @throws ParseException
//     */
//    public PreOutboundLineV2 createPreOutboundLineV6(PreOutboundHeaderV2 preOutboundHeader, SalesOrderLineV2 salesOrderLineV2,
//                                                     String loginUserId) throws ParseException {
//        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
//        BeanUtils.copyProperties(salesOrderLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(salesOrderLineV2));
//        preOutboundLine.setLanguageId(preOutboundHeader.getLanguageId());
//        preOutboundLine.setCompanyCodeId(preOutboundHeader.getCompanyCodeId());
//        preOutboundLine.setPlantId(preOutboundHeader.getPlantId());
//        preOutboundLine.setCustomerId(preOutboundHeader.getCustomerId());
//        preOutboundLine.setCustomerName(preOutboundHeader.getCustomerName());
//        preOutboundLine.setWarehouseId(preOutboundHeader.getWarehouseId());
//        preOutboundLine.setRefDocNumber(preOutboundHeader.getRefDocNumber());
//        preOutboundLine.setPreOutboundNo(preOutboundHeader.getPreOutboundNo());
//        preOutboundLine.setPartnerCode(preOutboundHeader.getPartnerCode());
//        preOutboundLine.setLineNumber(salesOrderLineV2.getLineReference());
//        preOutboundLine.setItemCode(salesOrderLineV2.getSku());
//        preOutboundLine.setOutboundOrderTypeId(3L);
//        preOutboundLine.setStatusId(39L);
//        preOutboundLine.setStockTypeId(1L);
//        preOutboundLine.setSpecialStockIndicatorId(1L);
//        preOutboundLine.setManufacturerName(MFR_NAME_V6);
//        preOutboundLine.setManufacturerCode(MFR_NAME_V6);
//        String barcodeId = salesOrderLineV2.getBarcodeId() != null ? salesOrderLineV2.getBarcodeId() : salesOrderLineV2.getSku();
//        preOutboundLine.setBarcodeId(barcodeId);
//        description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
//        if (description != null) {
//            preOutboundLine.setCompanyDescription(description.getCompanyDesc());
//            preOutboundLine.setPlantDescription(description.getPlantDesc());
//            preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());
//        }
//        statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeader.getLanguageId());
//        preOutboundLine.setStatusDescription(statusDescription);
//        preOutboundLine.setPickListNumber(salesOrderLineV2.getPickListNo());
//        ImBasicData1V2 imBasicData1 = repo.imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                preOutboundHeader.getLanguageId(), preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), salesOrderLineV2.getSku(), salesOrderLineV2.getManufacturerName(), 0L);
//        log.info("imBasicData1 : " + imBasicData1);
//        if (imBasicData1 != null) {
//            preOutboundLine.setDescription(imBasicData1.getDescription());
//            if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
//                preOutboundLine.setItemType(getItemTypeDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
//                        preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemType()));
//            } else {
//                preOutboundLine.setItemType(imBasicData1.getItemTypeDescription());
//            }
//            if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
//                preOutboundLine.setItemGroup(getItemGroupDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
//                        preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemGroup()));
//            } else {
//                preOutboundLine.setItemGroup(imBasicData1.getItemGroupDescription());
//            }
//        } else {
//            preOutboundLine.setDescription(salesOrderLineV2.getSkuDescription());
//        }
//        preOutboundLine.setOrderQty(salesOrderLineV2.getOrderedQty());
//        preOutboundLine.setOrderUom(salesOrderLineV2.getUom());
////        preOutboundLine.setRequiredDeliveryDate(salesOrderLineV2.getRequiredDeliveryDate());
//        preOutboundLine.setReferenceField1("SalesOrder");
//        preOutboundLine.setDeletionIndicator(0L);
//        preOutboundLine.setCreatedBy(loginUserId);
//        preOutboundLine.setCreatedOn(new Date());
//        log.info("preOutboundLine : " + preOutboundLine);
//        return preOutboundLine;
//    }
//
//    // Create OrderManagementLine
//    public OrderManagementLineV2 createOrderManagementLine(OrderManagementHeaderV2 orderManagementHeaderV2, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
//        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
//        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
//
//        orderManagementLine.setPickupCreatedBy(loginUserId);
//        orderManagementLine.setPickupCreatedOn(new Date());
//        log.info("orderManagementLine : " + orderManagementLine);
//
//        orderManagementLine = createOrderManagementV6(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), 1L, orderManagementLine, preOutboundLine.getWarehouseId(),
//                preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
//        // PROP_ST_BIN
//        return orderManagementLine;
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param binClassId
//     * @param orderManagementLine
//     * @param warehouseId
//     * @param itemCode
//     * @param ORD_QTY
//     * @param loginUserId
//     * @return
//     * @throws Exception
//     */
//    public OrderManagementLineV2 createOrderManagementV6(String companyCodeId, String plantId, String languageId,
//                                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
//                                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
//        log.info("Getting StockType1InventoryList Inpute Values : companyCodeId ------> " + companyCodeId + " plantId -------> " + plantId + " languageId ------> " + languageId + " warehouseId --------> "
//                + warehouseId + " itemCode -----> " + itemCode + " binClassId --------> " + binClassId + " manufacturerName ---------> " + orderManagementLine.getManufacturerName());
//        List<IInventoryImpl> stockType1InventoryList = repo.orderService.getInventoryForOrderManagementV6(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
//        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//        if (stockType1InventoryList.isEmpty()) {
//            return repo.orderService.createEMPTYOrderManagementLineV6(orderManagementLine);
//        }
//        return updateAllocationV6(orderManagementLine, 1L, ORD_QTY, orderManagementLine.getItemCode(), loginUserId);
//    }
//
//    /**
//     * @param itemCode
//     * @param binClassId
//     * @param ORD_QTY
//     * @param orderManagementLine
//     * @param loginUserID
//     * @return
//     * @throws Exception
//     */
//    public OrderManagementLineV2 updateAllocationV6(OrderManagementLineV2 orderManagementLine, Long binClassId, Double ORD_QTY,
//                                                    String itemCode, String loginUserID) throws Exception {
//
//        log.info("Inventory Update Allocation Started ...........");
//
//        String companyCodeId = orderManagementLine.getCompanyCodeId();
//        String warehouseId = orderManagementLine.getWarehouseId();
//        String plantId = orderManagementLine.getPlantId();
//        String languageId = orderManagementLine.getLanguageId();
//        String manufacturerName = orderManagementLine.getManufacturerName();
//
//        String masterToken = getMasterAuthToken();
//        String alternateUom = orderManagementLine.getAlternateUom();
//        Long stockTypeId = 1L;
//        String orderBy = null;
//        String INV_STRATEGY = null;
//
//        log.info("The Alternate UOM ------------------> {}", alternateUom);
//
//        ImBasicData imBasicData = new ImBasicData();
//        imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
//        imBasicData.setPlantId(orderManagementLine.getPlantId());
//        imBasicData.setLanguageId(orderManagementLine.getLanguageId());
//        imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
//        imBasicData.setItemCode(itemCode);
//        ImBatchSerial imBatchSerial = repo.mastersService.getImBatchSerialV2(imBasicData, masterToken);
//
//        if (imBatchSerial != null) {
//            Strategies strategies = repo.strategiesService.getStrategies(companyCodeId, languageId, plantId, warehouseId, 2L, imBatchSerial.getSequenceIndicator());           //Outbound - Strategy type - 2; Inbound - Strategy type - 1
//            if (strategies != null && strategies.getPriority1() != null) {
//                INV_STRATEGY = String.valueOf(strategies.getPriority1());
//            }
//        }
//
//        // Inventory Strategy Choices
//        if (INV_STRATEGY == null) {
//            INV_STRATEGY = repo.propertiesConfig.getOrderAllocationStrategyCoice();
//        }
//
//        boolean shelfLifeIndicator = false;
//        imBasicData.setManufacturerName(manufacturerName);
//        ImBasicData1 dbImBasicData1 = repo.mastersService.getImBasicData1ByItemCodeV2(imBasicData, masterToken);
//        log.info("ImBasicData1: " + dbImBasicData1);
//        if (dbImBasicData1 != null) {
//            if (dbImBasicData1.getShelfLifeIndicator() != null) {
//                shelfLifeIndicator = dbImBasicData1.getShelfLifeIndicator();
//            }
//        }
//        log.info("Allocation Strategy: " + INV_STRATEGY);
//        log.info("shelfLifeIndicator: " + shelfLifeIndicator);
//
//        OrderManagementLineV2 newOrderManagementLine = null;
//        int invQtyByLevelIdCount = 0;
//        int invQtyGroupByLevelIdCount = 0;
//        List<IInventoryImpl> stockType1InventoryList =
//                repo.orderService.getInventoryForOrderManagementV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//
//        if (stockType1InventoryList == null || stockType1InventoryList.isEmpty()) {
//            return repo.orderService.updateOrderManagementLineV6(orderManagementLine);
//        }
//
//        // Getting Inventory GroupBy ST_BIN wise
//        List<IInventoryImpl> finalInventoryList = null;
//        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            log.info("INV_STRATEGY: " + INV_STRATEGY + shelfLifeIndicator);
//            if (!shelfLifeIndicator) {
//                orderBy = "iv.UTD_ON";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByCreatedOnV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            } else {
//                orderBy = "iv.EXP_DATE";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByExpiryDateV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
//            log.info("INV_STRATEGY: " + INV_STRATEGY);
//            orderBy = "iv.LEVEL_ID";
//            finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("1")) { // FIFO
//            log.info("FIFO");
//            List<IInventory> levelIdList = repo.orderService.getInventoryForOrderManagementByBatchV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//            log.info("Group By Batch: " + levelIdList.size());
//            List<String> invQtyByLevelIdList = new ArrayList<>();
//            boolean toBeIncluded = true;
//            for (IInventory iInventory : levelIdList) {
//                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
//                if (ORD_QTY <= iInventory.getInventoryQty()) {
//                    orderBy = "iv.STR_NO";
//                    finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByBatchV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, stockTypeId, binClassId, alternateUom);
//                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
//                    newOrderManagementLine = orderAllocationV6(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
//                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
//                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                    return newOrderManagementLine;
//                }
//                if (ORD_QTY > iInventory.getInventoryQty()) {
//                    toBeIncluded = false;
//                }
//                if (!toBeIncluded) {
//                    invQtyByLevelIdList.add("True");
//                }
//            }
//            invQtyByLevelIdCount = levelIdList.size();
//            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
//            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
//            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
//                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                return newOrderManagementLine;
//            }
//            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
//                orderBy = "iv.LEVEL_ID";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
//            log.info("INV_STRATEGY: " + INV_STRATEGY);
//            List<IInventory> levelIdList = repo.orderService.getInventoryForOrderManagementGroupByLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    stockTypeId, binClassId, manufacturerName, alternateUom);
//
//            log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + companyCodeId + " plantId ----> " + plantId + " languageId ----> " + languageId +
//                    ", warehouseId -----> " + warehouseId + "itemCode -----> " + itemCode + " refDocumentNo -----> " + orderManagementLine.getRefDocNumber() + " barcodeId -------> " + orderManagementLine.getBarcodeId());
//
////            Double INV_QTY = inventoryV2Repository.getCurrentCaseQty1(companyCodeId, plantId, languageId, warehouseId, itemCode);
//
////            Double INV_QTY1 = inventoryV2Repository.getInvCaseQty(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, ORD_QTY);
////            log.info("Queried invQty1 -------------> {}", INV_QTY1);
//
//            Double INV_QTY = repo.inventoryV2Repository.getInvCaseQty2(companyCodeId, plantId, languageId, warehouseId);
//            log.info("Queried invQty2 ----------> {}", INV_QTY);
//
//            log.info("Group By LeveId: " + levelIdList.size());
//            List<String> invQtyByLevelIdList = new ArrayList<>();
//            boolean toBeIncluded = true;
//            for (IInventory iInventory : levelIdList) {
//                log.info("ORD_QTY, INV_QTY_TOTAL : " + ORD_QTY + ", " + iInventory.getInventoryQty());
//
//                log.info("Order Qty --------> {}", ORD_QTY);
//                log.info("BagSize ------------> {}", orderManagementLine.getBagSize());
//                log.info("INV_QTY queired 1 -------------> {}", INV_QTY);
//                if (ORD_QTY == INV_QTY) {
//                    log.info("Closed Case Allocation started !!");
//                    fullQtyAllocation(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
//                } else if (ORD_QTY < iInventory.getInventoryQty()) {
//                    orderBy = "iv.LEVEL_ID";
//                    finalInventoryList = repo.orderService.getInventoryForOrderManagementLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, stockTypeId, binClassId, alternateUom,
//                            iInventory.getLevelId());
//                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
//                    newOrderManagementLine = orderAllocationV6(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
//                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
//                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                    return newOrderManagementLine;
//                }
//                if (ORD_QTY > iInventory.getInventoryQty()) {
//                    toBeIncluded = false;
//                }
//                if (!toBeIncluded) {
//                    invQtyByLevelIdList.add("True");
//                }
//            }
//            invQtyByLevelIdCount = levelIdList.size();
//            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
//            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
//            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
//                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                return newOrderManagementLine;
//            }
//            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
//                orderBy = "iv.LEVEL_ID";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");
//
//        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
//            return updateOrderManagementLineV6(orderManagementLine);
//        }
//
//        newOrderManagementLine = orderAllocationV6(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
//                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
//
//        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//        return newOrderManagementLine;
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param binClassId
//     * @param ORD_QTY
//     * @param orderManagementLine
//     * @param finalInventoryList
//     * @param loginUserID
//     * @return
//     */
//    public OrderManagementLineV2 orderAllocationV6(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
//                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
//                                                   String loginUserID) {
//        OrderManagementLineV2 newOrderManagementLine = null;
//        String alternateUom = orderManagementLine.getAlternateUom();
//        outerloop:
//        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
//            InventoryV2 stBinInventory = repo.orderService.getInventoryV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stBinWiseInventory.getBarcodeId(),
//                    stBinWiseInventory.getStorageBin(), alternateUom);
//            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);
//
//            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
//            if (stBinInventory == null) {
//                return updateOrderManagementLineV6(orderManagementLine);
//            }
//
//            if (stBinInventory != null) {
//
//                Long STATUS_ID = 0L;
//                Double ALLOC_QTY = 0D;
//                String inventoryAlternateUom = stBinInventory.getAlternateUom();
//
//                /*
//                 * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
//                 * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
//                 * ALLOC_QTY=0
//                 */
//                Double INV_QTY = stBinInventory.getInventoryQuantity();
//
//                // INV_QTY
//                orderManagementLine.setInventoryQty(INV_QTY);
//
//                if (ORD_QTY <= INV_QTY) {
//                    ALLOC_QTY = ORD_QTY;
//                } else if (ORD_QTY > INV_QTY) {
//                    ALLOC_QTY = INV_QTY;
//                } else if (INV_QTY == 0) {
//                    ALLOC_QTY = 0D;
//                }
//                log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);
//
//                if (orderManagementLine.getStatusId() == 47L) {
//                    try {
//                        repo.orderManagementLineV2Repository.delete(orderManagementLine);
//                        log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
//                    } catch (Exception e) {
//                        log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
//                        e.printStackTrace();
//                    }
//                }
//
//                orderManagementLine.setAllocatedQty(ALLOC_QTY);
//                orderManagementLine.setReAllocatedQty(ALLOC_QTY);
//
//                // STATUS_ID
//                /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
//                if (ORD_QTY > ALLOC_QTY) {
//                    STATUS_ID = 42L;
//                }
//
//                /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
//                if (ORD_QTY == ALLOC_QTY) {
//                    STATUS_ID = 43L;
//                }
//
//                statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
//                orderManagementLine.setStatusId(STATUS_ID);
//                orderManagementLine.setStatusDescription(statusDescription);
//                orderManagementLine.setReferenceField7(statusDescription);
//                orderManagementLine.setPickupUpdatedBy(loginUserID);
//                orderManagementLine.setPickupUpdatedOn(new Date());
//
//                double allocatedQtyFromOrderMgmt = 0.0;
//
//                /*
//                 * Deleting current record and inserting new record (since UK is not allowing to
//                 * update prop_st_bin and Pack_bar_codes columns
//                 */
//                newOrderManagementLine = new OrderManagementLineV2();
//                BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
//
//                if (newOrderManagementLine.getCompanyDescription() == null) {
//                    description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//                    newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
//                    newOrderManagementLine.setPlantDescription(description.getPlantDesc());
//                    newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
//                }
//
//                newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
//                if (stBinInventory.getBarcodeId() != null) {
//                    newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
//                }
//                if (stBinInventory.getLevelId() != null) {
//                    newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
//                }
//                newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
//                newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
//                newOrderManagementLine.setMrp(stBinInventory.getMrp());
//                OrderManagementLineV2 createdOrderManagementLine = repo.orderManagementLineV2Repository.save(newOrderManagementLine);
//                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
//                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();
//
//                if (ORD_QTY > ALLOC_QTY) {
//                    ORD_QTY = ORD_QTY - ALLOC_QTY;
//                }
//
//
//                if (ORD_QTY == ALLOC_QTY) {
//                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
//                    break outerloop; // If the Inventory satisfied the Ord_qty
//                }
//            }
//        }
//        return newOrderManagementLine;
//    }
//
//    /**
//     * @param orderManagementLine
//     * @return
//     */
//    private OrderManagementLineV2 updateOrderManagementLineV6(OrderManagementLineV2 orderManagementLine) {
//        orderManagementLine.setStatusId(47L);
//        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
//        orderManagementLine.setStatusDescription(statusDescription);
//        orderManagementLine.setReferenceField7(statusDescription);
//        orderManagementLine.setProposedStorageBin("");
//        orderManagementLine.setProposedPackBarCode("");
//        orderManagementLine.setInventoryQty(0D);
//        orderManagementLine.setAllocatedQty(0D);
//        orderManagementLine = repo.orderManagementLineV2Repository.save(orderManagementLine);
//        log.info("orderManagementLine created: " + orderManagementLine);
//        return orderManagementLine;
//    }
//
//    /**
//     * @param iInventory
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @param loginUserID
//     * @param ORD_QTY
//     * @param orderManagementLine
//     */
//    public void fullQtyAllocation(IInventory iInventory, String companyCodeId, String plantId, String languageId, String warehouseId,
//                                  String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom, String loginUserID,
//                                  Double ORD_QTY, OrderManagementLineV2 orderManagementLine) {
//
//        List<IInventoryImpl> finalInventoryList = null;
//        OrderManagementLineV2 newOrderManagementLine = null;
//
//        log.info("Logic according to Closed Case Full ---------------> INV_QTY == ORD_QTY Started");
//        finalInventoryList = repo.orderService.getInventoryForOrderManagementLevelAsscIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                manufacturerName, stockTypeId, binClassId, alternateUom,
//                iInventory.getLevelId());
//
//        log.info("Group By LeveId Inventory Closed Case: " + finalInventoryList.size());
//        newOrderManagementLine = orderAllocationV6(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
//                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
//        log.info("newOrderManagementLine updated Closed Case ---#--->" + newOrderManagementLine);
//    }
//
//
//    /**
//     * @param orderManagementLineV2
//     * @param outboundHeaderV2
//     * @return
//     */
//    private OutboundLineV2 createOutboundLineV6(OrderManagementLineV2 orderManagementLineV2, OutboundHeaderV2 outboundHeaderV2) {
//        OutboundLineV2 outboundLine = new OutboundLineV2();
//        BeanUtils.copyProperties(orderManagementLineV2, outboundLine, CommonUtils.getNullPropertyNames(orderManagementLineV2));
//        outboundLine.setDeliveryQty(0D);
//        outboundLine.setCreatedBy(orderManagementLineV2.getPickupCreatedBy());
//        outboundLine.setCreatedOn(orderManagementLineV2.getPickupCreatedOn());
//        outboundLine.setInvoiceDate(outboundHeaderV2.getRequiredDeliveryDate());
//        outboundLine.setSalesInvoiceNumber(outboundHeaderV2.getSalesInvoiceNumber());
//        outboundLine.setSalesOrderNumber(outboundHeaderV2.getSalesOrderNumber());
//        outboundLine.setPickListNumber(outboundHeaderV2.getPickListNumber());
//        outboundLine.setCustomerType("INVOICE");
//        return outboundLine;
//    }
//
//
//    // PickupHeader Create
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param refDocNumber
//     * @throws Exception
//     */
//    public void createPickupHeaderNo(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
//                                     String refDocNumber) throws Exception {
//
//        List<OrderManagementLineV2> orderManagementLines = repo.orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorAndStatusIdNot(
//                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L, 47L);
//
//        log.info("OrderManagementList for PickupHeader -------------> {}", orderManagementLines.size());
//
//        long NUM_RAN_CODE = 10;
//        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
//        log.info("----------New PU_NO--------> : " + PU_NO);
//
//        if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
//            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {
//                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
//                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
//                newPickupHeader.setPickupNumber(PU_NO);
//                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
//                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
//                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());
//
//                // STATUS_ID
//                newPickupHeader.setStatusId(48L);
//                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
//                newPickupHeader.setStatusDescription(statusDescription);
//
//                // ProposedPackbarcode
//                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());
//
//                //Setting InventoryQuantity from orderManagementLine
//                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());
//
//                //Setting BagSize
//                newPickupHeader.setBagSize(orderManagementLine.getInventoryQty());
//                newPickupHeader.setNoBags(orderManagementLine.getNoBags());
//
//                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
//                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
//                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
//                PickupHeaderV2 createdPickupHeader = repo.orderService.createOutboundOrderProcessingPickupHeaderV6(newPickupHeader, orderManagementLine.getPickupCreatedBy());
//                log.info("pickupHeader created: " + createdPickupHeader);
//
//                repo.orderManagementLineV2Repository.updateOrderManagementLineV6(
//                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
//                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
//                        48L, statusDescription, PU_NO, new Date());
//            }
//
//            repo.outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
//            repo.orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
//        }
//    }
//
////-------------------------------------------------ob order-------------------------------------------------------------
//
//    /**
//     * @param salesOrder
//     * @return
//     */
//    public SalesOrderV2 postSalesOrderV2(SalesOrderV2 salesOrder) throws ParseException {
//        log.info("SalesOrderHeader received from External: " + salesOrder);
//        OutboundOrderV2 savedSoHeader = saveSalesOrderV2(salesOrder);                                // Without Nongo
//        log.info("salesOrderHeader: " + savedSoHeader);
//        return salesOrder;
//    }
//
//    private OutboundOrderV2 saveSalesOrderV2(@Valid SalesOrderV2 salesOrder) throws ParseException {
//        try {
//            SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();
//
//            OutboundOrderV2 apiHeader = new OutboundOrderV2();
//
//            BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
//            if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
//                apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
//            } else {
//                Optional<Warehouse> warehouse =
//                        repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//                                salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
//                                salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
//                                0L);
//                apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
//            }
//            apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
//            apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
//            apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);
//
//            apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
//            apiHeader.setPartnerCode(salesOrderHeader.getCustomerId());
//            apiHeader.setPartnerName(salesOrderHeader.getStoreName());
//            apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
//            apiHeader.setPickListStatus(salesOrderHeader.getStatus());
//            apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
//            apiHeader.setOutboundOrderTypeID(3L);                                   // Hardcoded Value "3"
//
//            apiHeader.setRefDocumentType("PICK LIST");                              // Hardcoded value "SaleOrder"
//            apiHeader.setCustomerType("INVOICE");                                //HardCoded
//            apiHeader.setRefDocumentType(getOutboundOrderTypeDesc(apiHeader.getOutboundOrderTypeID()));
//            apiHeader.setOrderReceivedOn(new Date());
//            apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
//            apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());
//
//            apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
//            apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());
//
//            try {
//                Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
//                apiHeader.setRequiredDeliveryDate(reqDate);
//            } catch (Exception e) {
//                throw new BadRequestException("Date format should be MM-dd-yyyy");
//            }
//
//            IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
//                    salesOrderHeader.getCompanyCode(), salesOrderHeader.getStoreID(), apiHeader.getWarehouseID());
//            if (iKeyValuePair != null) {
//                apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
//                apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
//            }
////			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
//            List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
//            Set<OutboundOrderLineV2> orderLines = new HashSet<>();
//            String barcodeId = null;
//            for (SalesOrderLineV2 soLine : salesOrderLines) {
//                OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
//                BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));
//                apiLine.setBrand(soLine.getBrand());
//                apiLine.setOrigin(soLine.getOrigin());
//                apiLine.setPackQty(soLine.getPackQty());
//                apiLine.setExpectedQty(soLine.getExpectedQty());
//                apiLine.setSupplierName(soLine.getSupplierName());
//                apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
//                apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
//                apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
//                apiLine.setManufacturerName(soLine.getManufacturerName());        // BRAND_NM
//                apiLine.setManufacturerCode(soLine.getManufacturerCode());
//                apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
//                apiLine.setStoreID(salesOrderHeader.getStoreID());
//                apiLine.setRefField1ForOrderType(soLine.getOrderType());
//                apiLine.setCustomerType("INVOICE");                                //HardCoded
//                apiLine.setOutboundOrderTypeID(3L);
//
//                apiLine.setBarcodeId(barcodeId);
//
//                apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
//                apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
//                apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
//                apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
//                apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
//                apiLine.setOrderId(apiHeader.getOrderId());
//                apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
//                apiLine.setPickListNo(soLine.getPickListNo());
//                apiLine.setImsSaleTypeCode(salesOrderHeader.getImsSaleTypeCode());
//
//                apiLine.setMiddlewareId(soLine.getMiddlewareId());
//                apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
//                apiLine.setMiddlewareTable(soLine.getMiddlewareTable());
//                if (salesOrderHeader.getRequiredDeliveryDate() != null) {
//                    if (salesOrderHeader.getRequiredDeliveryDate().contains("-")) {
//                        // EA_DATE
//                        try {
//                            Date reqDelDate = new Date();
//                            if (salesOrderHeader.getRequiredDeliveryDate().length() > 10) {
//                                reqDelDate = DateUtils.convertStringToDateWithTime(salesOrderHeader.getRequiredDeliveryDate());
//                            }
//                            if (salesOrderHeader.getRequiredDeliveryDate().length() == 10) {
//                                reqDelDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
//                            }
//                            apiHeader.setRequiredDeliveryDate(reqDelDate);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            throw new BadRequestException("Date format should be MM-dd-yyyy");
//                        }
//                    }
//                    if (salesOrderHeader.getRequiredDeliveryDate().contains("/")) {
//                        // EA_DATE
//                        try {
//                            ZoneId defaultZoneId = ZoneId.systemDefault();
//                            String sdate = salesOrderHeader.getRequiredDeliveryDate();
//                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
//                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
//                            secondHalf = "/20" + secondHalf;
//                            sdate = firstHalf + secondHalf;
//                            log.info("sdate--------> : " + sdate);
//
//                            LocalDate localDate = DateUtils.dateConv2(sdate);
//                            log.info("localDate--------> : " + localDate);
//                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
//                            apiHeader.setRequiredDeliveryDate(date);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            throw new BadRequestException("Date format should be MM-dd-yyyy");
//                        }
//                    }
//                }
//                orderLines.add(apiLine);
//            }
//            apiHeader.setLine(orderLines);
//            apiHeader.setOrderProcessedOn(new Date());
//
//            if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
//                apiHeader.setProcessedStatusId(1L);
//                log.info("apiHeader : " + apiHeader);
//                OutboundOrderV2 createdOrder = repo.orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Success: " + createdOrder);
//                return apiHeader;
//            } else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
//                // throw the error as Lines are Empty and set the Indicator as '100'
//                apiHeader.setProcessedStatusId(100L);
//                log.info("apiHeader : " + apiHeader);
//                OutboundOrderV2 createdOrder = repo.orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Failed: " + createdOrder);
//                throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//        return null;
//    }
//
//
//    public void updateStatusId(String companyCodeId, String plantId, String warehouseId, String refDocumentNo, Long processStatusId) {
//        outboundOrderV2Repository.updateObOrderStatus(companyCodeId, plantId, warehouseId, refDocumentNo, processStatusId);
//    }
//
////--------------------------------------------FG---------------------------------------------------------
//
//    /**
//     * @param salesOrderV2
//     */
//    @Async("asyncTaskExecutor")
//    public void outboundProcessV6(List<SalesOrderV2> salesOrderV2) {
//        for (SalesOrderV2 orderV2 : salesOrderV2) {
//            createSalesOrderListV6(orderV2);
//        }
//    }
//
//    /**
//     * BharathPackaging - BP
//     *
//     * @param salesOrder
//     * @return
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
//    public SalesOrderV2 createSalesOrderListV6(SalesOrderV2 salesOrder) {
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        try {
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("BP");
//            postSalesOrderV2(salesOrder);
//            SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
//            List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
//            String companyCode = header.getCompanyCode();
//            String plantId = header.getBranchCode();
//            String warehouseId = header.getWarehouseId();
//            String languageId = header.getLanguageId();
//            String orderType = lineV2List.get(0).getOrderType();
//            String refDocNumber = header.getSalesOrderNumber();
//
//            String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);
//
//            Optional<PreOutboundHeaderV2> orderProcessedStatus =
//                    repo.preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
//                            refDocNumber, 3L, 0L);
//
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }
//            // PreBoundHeader
//            PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeaderV6(companyCode, plantId, languageId, warehouseId,
//                    preOutboundNo, header, orderType, MW_AMS);
//            log.info("preOutboundHeader Created : " + preOutboundHeader);
//
//            // Collections for batch saving
//            List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
//
//            // Process lines in parallel
//            List<CompletableFuture<Void>> futures = lineV2List.stream()
//                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
//                        try {
//                            processSingleSaleOrderLine(outBoundLine, preOutboundHeader, preOutboundLines);
//                        } catch (Exception e) {
//                            log.error("Error processing for SalesOrder: {}", header.getSalesOrderNumber(), e);
//                            throw new RuntimeException(e);
//                        }
//                    }, executorService))
//                    .collect(Collectors.toList());
//
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            try {
//                allFutures.join(); // Wait for all tasks to finish
//            } catch (CompletionException e) {
//                log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
//                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//            }
//            // Batch Save All Records
//            repo.preOutboundLineV2Repository.saveAll(preOutboundLines);
//            updateStatusId(companyCode, plantId, warehouseId, refDocNumber, 10L);
//            log.info("preInbound header and line Processed");
//
//        } catch (
//                Exception e) {
//            log.error("Error processing outbound", e);
//            updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode(),
//                    salesOrder.getSalesOrderHeader().getWarehouseId(), salesOrder.getSalesOrderHeader().getSalesOrderNumber(), 100L);
//
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("MT");
//
//            updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode(),
//                    salesOrder.getSalesOrderHeader().getWarehouseId(), salesOrder.getSalesOrderHeader().getSalesOrderNumber(), 100L);
//            throw new BadRequestException("PreOutbound Order Processing failed: " + e.getMessage());
//
//        } finally {
//            executorService.shutdown();
//        }
//        return salesOrder;
//    }
//
//
//    /**
//     * BharathPackaging - BP
//     * <p>
//     * processSingleSaleOrderLine
//     *
//     * @param salesOrderLineV2
//     * @param preOutboundHeaderV2
//     * @param preOutboundLineV2
//     * @throws Exception
//     */
//    private void processSingleSaleOrderLine(SalesOrderLineV2 salesOrderLineV2, PreOutboundHeaderV2 preOutboundHeaderV2,
//                                            List<PreOutboundLineV2> preOutboundLineV2) throws Exception {
//        // Collect PreOutboundLine
//        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV6(preOutboundHeaderV2, salesOrderLineV2, MW_AMS);
//        preOutboundLineV2.add(preOutboundLine);
//        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);
//    }
//
//
////========================================================================================================================================================================
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param perOutboundNo
//     * @param refDocNumber
//     */
//    @Transactional
//    public void processOutbound(String companyCodeId, String plantId, String languageId, String warehouseId, String perOutboundNo, String refDocNumber) {
//
//        PreOutboundHeaderV2 preOutboundHeader = repo.preOutboundHeaderV2Repository.getPreOutboundHeader(companyCodeId, plantId, warehouseId, perOutboundNo, refDocNumber);
//        List<PreOutboundLineV2> preOutboundLine = repo.preOutboundLineV2Repository.getPreOutboundLine(companyCodeId, plantId, warehouseId, perOutboundNo, refDocNumber);
//
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//        try {
//
//            // OrderManagementHeader
//            OrderManagementHeaderV2 orderManagementHeader = createOrderManagementHeaderV6(preOutboundHeader, MW_AMS);
//            log.info("OrderManagementHeader Created : " + orderManagementHeader);
//
//            // OutboundHeader
//            OutboundHeaderV2 outboundHeader = createOutboundHeader(preOutboundHeader, orderManagementHeader.getStatusId(), MW_AMS);
//            log.info("OutboundHeader Created : " + outboundHeader);
//
//            // Collections for batch saving
//            List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
//            List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());
//
//
//            // Process lines in parallel
//            List<CompletableFuture<Void>> futures = preOutboundLine.stream()
//                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
//                        try {
//                            processSaleOrderLine(orderManagementHeader, outboundHeader, outBoundLine,
//                                    outboundLines, managementLines);
//                        } catch (Exception e) {
//                            log.error("Error processing PICK_LIST Line for SalesOrder: {}", preOutboundHeader.getSalesOrderNumber(), e);
//                            throw new RuntimeException(e);
//                        }
//                    }))
//                    .collect(Collectors.toList());
//
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            try {
//                allFutures.join(); // Wait for all tasks to finish
//            } catch (CompletionException e) {
//                log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
//                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//            }
//
//            // Batch Save All Records
//            repo.orderManagementLineV2Repository.saveAll(managementLines);
//            repo.outboundLineV2Repository.saveAll(outboundLines);
//
//            // No_Stock_Items
//            statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
//            repo.orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
//            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
//
//            updateStatusId(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), preOutboundHeader.getRefDocNumber(), 10L);
//        } catch (Exception e) {
//            log.error("Error processing outbound PICK_LIST Lines", e);
//            updateStatusId(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), preOutboundHeader.getRefDocNumber(), 100L);
//
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("MT");
//            updateStatusId(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), preOutboundHeader.getRefDocNumber(), 100L);
//            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
//        }
//
//    }
//
//    /**
//     * @param orderManagementHeaderV2
//     * @param outboundHeaderV2
//     * @param preOutboundLineV2
//     * @param outboundLineV2
//     * @param orderManagementLineV2
//     * @throws Exception
//     */
//    private void processSaleOrderLine(OrderManagementHeaderV2 orderManagementHeaderV2, OutboundHeaderV2 outboundHeaderV2,
//                                      PreOutboundLineV2 preOutboundLineV2, List<OutboundLineV2> outboundLineV2,
//                                      List<OrderManagementLineV2> orderManagementLineV2) throws Exception {
//
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//        // Collect OrderManagementLine
//        OrderManagementLineV2 orderManagementLine = createOrderManagementLine(orderManagementHeaderV2, preOutboundLineV2, MW_AMS);
//        orderManagementLineV2.add(orderManagementLine);
//        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);
//
//        // Collect InboundLine
//        OutboundLineV2 outboundLine = createOutboundLineV6(orderManagementLine, outboundHeaderV2);
//        outboundLineV2.add(outboundLine);
//        log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLine);
//
//    }
//
////=======================================================================================================================
//
//    @Transactional
//    public List<OrderManagementLineV2> createOrderManagementLine(List<PreOutboundLineV2> preOutboundLineV2List, String loginUserID) {
//
//        PreOutboundLineV2 preOutboundLineV2 = preOutboundLineV2List.get(0);
//        String refDocNo = preOutboundLineV2.getRefDocNumber();
//        String companyId = preOutboundLineV2.getCompanyCodeId();
//        String plantId = preOutboundLineV2.getPlantId();
//        String languageId = preOutboundLineV2.getLanguageId();
//        String warehouseId = preOutboundLineV2.getWarehouseId();
//        String preOutboundNo = preOutboundLineV2.getPreOutboundNo();
//
//        Optional<PreOutboundHeaderV2> preOutboundHeader =
//                repo.preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
//                        languageId, companyId, plantId, warehouseId, refDocNo, preOutboundNo, 0L);
//
//        if (preOutboundHeader.isEmpty()) {
//            throw new BadRequestException("PreOutboundHeader Doesn't Exist For this Input Values CompanyId - " + companyId +
//                    " PlantId - " + plantId + " WarehouseId - " + warehouseId + " PreOutboundNo - " + preOutboundNo + " RefDocNo " + refDocNo);
//        }
//
//        log.info("OrderManagementHeader Creation Started -------------------> ");
//        createOrderManagementHeaderV2(preOutboundHeader.get(), loginUserID);
//        log.info("OutboundHeader Creation Started ----------------------> ");
//        createOutboundHeaderV2(preOutboundHeader.get(), loginUserID);
//
//        List<OrderManagementLineV2> orderManagementLineV2List = null;
//        for (PreOutboundLineV2 preOutboundLine : preOutboundLineV2List) {
//
//            orderManagementLineV2List = postOrderManagementLine(preOutboundLine, loginUserID);
//        }
//        if (orderManagementLineV2List != null) {
//            log.info("OutboundLine Create BasedOn OrderManagementLine Size {} -- ", orderManagementLineV2List.size());
//            createOutboundLine(orderManagementLineV2List);
//        }
//
//        if (orderManagementLineV2List != null) {
//            List<OrderManagementLineV2> NoStockLines = orderManagementLineV2List.stream()
//                    .filter(line -> Objects.equals(line.getStatusId(), 47L))
//                    .collect(Collectors.toList());
//
//            if (!NoStockLines.isEmpty()) {
//                statusDescription = getStatusDescription(47L, languageId);
//                repo.orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyId, plantId, languageId, warehouseId, refDocNo, preOutboundNo, 47L, statusDescription);
//                log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
//            }
//
//            List<OrderManagementLineV2> nonNoStockLines = orderManagementLineV2List.stream()
//                    .filter(line -> !Objects.equals(line.getStatusId(), 47L))
//                    .collect(Collectors.toList());
//
//            if (!nonNoStockLines.isEmpty()) {
//                createPickupHeaderNo(companyId, plantId, languageId, warehouseId, preOutboundNo, refDocNo, nonNoStockLines);
//            }
//        }
//        return orderManagementLineV2List;
//    }
//
//    /**
//     * @param createdPreOutboundHeader outboundHeaderCreate
//     * @param loginUserId              userId
//     */
//    @Transactional
//    public void createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
//        try {
//            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
//            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
//            newOrderManagementHeader.setPickupCreatedOn(new Date());
//            repo.orderManagementHeaderV2Repository.save(newOrderManagementHeader);
//            log.info("OrderManagement Created Successfully OrderNo is - " + newOrderManagementHeader.getRefDocNumber());
//        } catch (Exception e) {
//            throw new RuntimeException("Exception Throw in OrderManagementHeader Creation " + e.getMessage());
//        }
//    }
//
//    /**
//     * @param createdPreOutboundHeader outboundHeader Create from PreOutboundHeader Input's
//     * @param loginUserId              userId
//     */
//    @Transactional
//    public void createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
//        try {
//            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
//            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//            outboundHeader.setRefDocDate(new Date());
//            outboundHeader.setCustomerType("INVOICE");
//            outboundHeader.setCreatedBy(loginUserId);
//            outboundHeader.setCreatedOn(new Date());
//            repo.outboundHeaderV2Repository.save(outboundHeader);
//            log.info("Created outboundHeader Successfully OrderNo is -  : " + outboundHeader.getRefDocNumber());
//        } catch (Exception e) {
//            throw new RuntimeException("Throw Exception in OutboundHeader Creation" + e.getMessage());
//        }
//    }
//
//    /**
//     * @param preOutboundLineV2 preOutboundLine
//     * @param loginUserID       userId
//     */
//    public List<OrderManagementLineV2> postOrderManagementLine(PreOutboundLineV2 preOutboundLineV2, String loginUserID) {
//        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();
//
//        log.info("Started Order Allocation for ItemCode: {}, Warehouse: {}, OrderQty: {}",
//                preOutboundLineV2.getItemCode(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getOrderQty());
//
//        String companyCodeId = preOutboundLineV2.getCompanyCodeId();
//        String plantId = preOutboundLineV2.getPlantId();
//        String warehouseId = preOutboundLineV2.getWarehouseId();
//        String languageId = preOutboundLineV2.getLanguageId();
//        String itemCode = preOutboundLineV2.getItemCode();
//        double balanceOrderQty = preOutboundLineV2.getOrderQty();
//
//        try {
//            List<InventoryV2> inventoryV2List = repo.inventoryV2Repository.getInventoryAllocation(companyCodeId, plantId, languageId, warehouseId, itemCode);
//            log.info("Inventory List {} in Order Allocation", inventoryV2List.size());
//
//
//            if (inventoryV2List.isEmpty()) {
//                log.warn("No inventory available for allocation for itemCode: {}", itemCode);
//            }
//            Long STATUS_ID = null;
//            for (InventoryV2 inventory : inventoryV2List) {
//
//                if (balanceOrderQty <= 0.0) break; // Stop loop when no quantity left to allocate
//
//                Long count = repo.orderManagementLineV2Repository.getCount(inventory.getCompanyCodeId(), inventory.getPlantId(), inventory.getLanguageId(),
//                        inventory.getWarehouseId(), inventory.getBarcodeId(), inventory.getItemCode());
//
//                if (count > 0) {
//                    continue;
//                }
//
//                double INV_QTY = inventory.getInventoryQuantity();
//                double ALLOC_QTY;
//
//                log.info("INV_QTY  -----> {}", INV_QTY);
//                log.info("balanceOrderQty ----> {}", balanceOrderQty);
//
//                if (INV_QTY == 0.0) {
//                    ALLOC_QTY = 0D;
//                } else {
//                    ALLOC_QTY = Math.min(balanceOrderQty, INV_QTY);
//                }
//
//                log.info("ALLOC_QTY  -----> {}", ALLOC_QTY);
//
//                if (ALLOC_QTY > 0.0) {
//                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
//                    BeanUtils.copyProperties(preOutboundLineV2, orderLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
//                    orderLine.setBarcodeId(inventory.getBarcodeId());
//                    orderLine.setInventoryQty(INV_QTY);
//                    orderLine.setAllocatedQty(ALLOC_QTY);
//                    orderLine.setNoBags(roundUp(inventory.getNoBags() - (ALLOC_QTY / inventory.getBagSize())));
//                    orderLine.setProposedStorageBin(inventory.getStorageBin());
//
//                    // Determine status
//                    if (ALLOC_QTY < balanceOrderQty) {
//                        STATUS_ID = 42L; // Partially Allocated
//                    }
//                    if (ALLOC_QTY == balanceOrderQty) {
//                        STATUS_ID = 43L; // Fully Allocated
//                    }
//
//                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
//                    orderLine.setStatusId(STATUS_ID);
//                    orderLine.setStatusDescription(statusDescription);
//                    orderLine.setReferenceField7(statusDescription);
//                    orderLine.setPickupUpdatedBy(loginUserID);
//                    orderLine.setPickupUpdatedOn(new Date());
//                    orderLine.setProposedPackBarCode("99999");
//
//                    // Inventory_Create
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                    inventoryV2.setInventoryQuantity(INV_QTY - ALLOC_QTY);
//                    inventoryV2.setAllocatedQuantity(ALLOC_QTY);
//                    inventoryV2.setBinClassId(1L);
//                    inventoryV2.setNoBags(roundUp(inventory.getNoBags() - (ALLOC_QTY / inventory.getBagSize())));
//                    inventoryV2.setReferenceField4(inventoryV2.getInventoryQuantity() + ALLOC_QTY);
//
//                    repo.inventoryV2Repository.save(inventoryV2);
//                    orderManagementLineV2List.add(repo.orderManagementLineV2Repository.save(orderLine));
//                    balanceOrderQty -= ALLOC_QTY; // reduce remaining order qty
//                    log.debug("Allocated {} qty from inventory {}, Remaining OrderQty: {}", ALLOC_QTY, inventory.getBarcodeId(), balanceOrderQty);
//                }
//            }
//
//            log.info("Balance OrderQty is {} -------------------> ", balanceOrderQty);
//            if (balanceOrderQty != 0.0) {
//                OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
//                BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
//                orderManagementLine.setOrderQty(balanceOrderQty);
//                orderManagementLine.setStatusId(47L);
//                statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
//                orderManagementLine.setStatusDescription(statusDescription);
//                orderManagementLine.setReferenceField7(statusDescription);
//                orderManagementLine.setProposedStorageBin("");
//                orderManagementLine.setProposedPackBarCode("");
//                orderManagementLine.setInventoryQty(0D);
//                orderManagementLine.setAllocatedQty(0D);
//                orderManagementLine = repo.orderManagementLineV2Repository.save(orderManagementLine);
//                log.info("orderManagementLine created for UnAllocated Order: " + orderManagementLine);
//            }
//
//
//            if (inventoryV2List.isEmpty()) {
//                OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
//                BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
//                orderManagementLineV2List.add(updateOrderManagementLineV2(orderManagementLine));
//            }
//        } catch (Exception ex) {
//            log.error("Unexpected error during order allocation: {}", ex.getMessage(), ex);
//            throw new RuntimeException("Order allocation process failed", ex);
//        }
//        return orderManagementLineV2List;
//    }
//
//    /**
//     * @param orderManagementLine
//     * @return
//     */
//    public OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
//        orderManagementLine.setStatusId(47L);
//        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
//        orderManagementLine.setStatusDescription(statusDescription);
//        orderManagementLine.setReferenceField7(statusDescription);
//        orderManagementLine.setProposedStorageBin("");
//        orderManagementLine.setProposedPackBarCode("");
//        orderManagementLine.setInventoryQty(0D);
//        orderManagementLine.setAllocatedQty(0D);
//        orderManagementLine = repo.orderManagementLineV2Repository.save(orderManagementLine);
//        log.info("orderManagementLine created: " + orderManagementLine);
//        return orderManagementLine;
//    }
//
//    /**
//     * @param orderManagementLineV2List orderManagementLine Input pass outbound Line Create
//     */
//    void createOutboundLine(List<OrderManagementLineV2> orderManagementLineV2List) {
//        try {
//            List<OutboundLineV2> outboundLineV2List = new ArrayList<>();
//            orderManagementLineV2List.forEach(line -> {
//                OutboundLineV2 outboundLineV2 = new OutboundLineV2();
//                BeanUtils.copyProperties(line, outboundLineV2, CommonUtils.getNullPropertyNames(line));
//                outboundLineV2.setDeliveryQty(0D);
//                outboundLineV2.setDeletionIndicator(0L);
//                outboundLineV2.setCreatedOn(new Date());
//                outboundLineV2List.add(outboundLineV2);
//            });
//            repo.outboundLineV2Repository.saveAll(outboundLineV2List);
//        } catch (Exception e) {
//            log.error("OutboundLine Create Failed Error throw {} -----> ", e.getMessage(), e);
//            throw new RuntimeException("Exception throw in OutboundLine Creation");
//        }
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param refDocNumber
//     * @param orderManagementLineV2
//     */
//    public void createPickupHeaderNo(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
//                                     String refDocNumber, List<OrderManagementLineV2> orderManagementLineV2) {
//
//        long NUM_RAN_CODE = 10;
//        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
//        log.info("----------New PU_NO--------> : " + PU_NO);
//
//        if (orderManagementLineV2 != null && !orderManagementLineV2.isEmpty()) {
//            for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2) {
//                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
//                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
//                newPickupHeader.setPickupNumber(PU_NO);
//                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
//                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
//                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());
//
//                // STATUS_ID
//                newPickupHeader.setStatusId(48L);
//                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
//                newPickupHeader.setStatusDescription(statusDescription);
//
//                // ProposedPackbarcode
//                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());
//
//                //Setting InventoryQuantity from orderManagementLine
//                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());
//
//                //Setting BagSize
//                newPickupHeader.setBagSize(orderManagementLine.getInventoryQty());
//                newPickupHeader.setNoBags(orderManagementLine.getNoBags());
//
//                // REF_FIELD_1
//                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());
//                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
//                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
//                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
//                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
//                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
//                newPickupHeader.setSalesOrderNumber(orderManagementLine.getSalesOrderNumber());
//                newPickupHeader.setPickListNumber(orderManagementLine.getPickListNumber());
//                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
//                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
//                newPickupHeader.setReferenceDocumentType(orderManagementLine.getReferenceDocumentType());
//                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
//                newPickupHeader.setTokenNumber(orderManagementLine.getTokenNumber());
//                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
//                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
//                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
//                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
//
//                pickupHeaderV2Repository.save(newPickupHeader);
////                PickupHeaderV2 createdPickupHeader = repo.pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
//                log.info("pickupHeader created: " + newPickupHeader);
//
//                repo.orderManagementLineV2Repository.updateOrderManagementLineV6(
//                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
//                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
//                        48L, statusDescription, PU_NO, new Date());
//            }
//
//            repo.outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
//            repo.orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
//        }
//    }
//
////=======================================================================================================================
//
//}
