//package com.tekclover.wms.api.inbound.orders.service.namratha;
//
//
//import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
//import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
//import com.tekclover.wms.api.inbound.orders.model.dto.*;
//import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
//import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundLineV2;
//import com.tekclover.wms.api.inbound.orders.model.outbound.v2.PickListCancellation;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderHeaderV2;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderLineV2;
//import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderV2;
//import com.tekclover.wms.api.inbound.orders.repository.RepositoryProvider;
//import com.tekclover.wms.api.inbound.orders.service.BaseService;
//import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.criterion.Order;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.ParseException;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class SalesOrderServiceV4 extends BaseService {
//
//    private final RepositoryProvider repo;
//    protected String MW_AMS = "WK_ADMIN";
//
//
//    /**
//     * @param salesOrder
//     * @return
//     */
//    @Transactional
//    public SalesOrderV2 createSalesOrderList(SalesOrderV2 salesOrder) {
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        try {
//            SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
//            List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
//            String companyCode = header.getCompanyCode();
//            String plantId = header.getBranchCode();
//            String newPickListNo = header.getPickListNumber();
//            String orderType = lineV2List.get(0).getOrderType();
//            String refDocNumber = header.getSalesOrderNumber();
//
//            // Get Warehouse
//            Optional<Warehouse> dbWarehouse =
//                    repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
//            Warehouse WH = dbWarehouse.get();
//            String warehouseId = WH.getWarehouseId();
//            String languageId = WH.getLanguageId();
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
//            PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeaderV2(companyCode, plantId, languageId, warehouseId,
//                    preOutboundNo, header, orderType, MW_AMS);
//            log.info("preOutboundHeader Created : " + preOutboundHeader);
//
//            // OrderManagementHeader
//            OrderManagementHeaderV2 orderManagementHeader = createOrderManagementHeaderV2(preOutboundHeader, MW_AMS);
//            log.info("OrderManagementHeader Created : " + orderManagementHeader);
//
//            // OutboundHeader
//            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(preOutboundHeader, orderManagementHeader.getStatusId(), header, MW_AMS);
//            log.info("OutboundHeader Created : " + outboundHeader);
//
//            // PickupHeader for only PickList Order Only
////            createPickUpHeaderAssignPickerModified(companyCode, plantId, languageId, warehouseId, header,
////                    preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
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
//        } catch (
//                Exception e) {
//            log.error("Error processing outbound PICK_LIST Lines", e);
//            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//        }
//        return salesOrder;
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
//    public PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
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
//    public OrderManagementHeaderV2 createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
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
//    public OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, SalesOrderHeaderV2 salesOrderHeaderV2, String loginUserId) throws ParseException {
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
//        // Collect PreOutboundLine
//        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(preOutboundHeaderV2, salesOrderLineV2, MW_AMS);
//        preOutboundLineV2.add(preOutboundLine);
//        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);
//
//        // Collect OrderManagementLine
//        OrderManagementLineV2 orderManagementLine = createOrderManagementLine(orderManagementHeaderV2, preOutboundLine, MW_AMS);
//        orderManagementLineV2.add(orderManagementLine);
//        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);
//
//        // Collect InboundLine
//        OutboundLineV2 outboundLine = createOutboundLineV2(orderManagementLine, outboundHeaderV2);
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
//    public PreOutboundLineV2 createPreOutboundLineV2(PreOutboundHeaderV2 preOutboundHeader, SalesOrderLineV2 salesOrderLineV2,
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
//        preOutboundLine.setManufacturerName(MFR_NAME_V4);
//        preOutboundLine.setManufacturerCode(MFR_NAME_V4);
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
//        orderManagementLine = createOrderManagementV4(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), 1L, orderManagementLine, preOutboundLine.getWarehouseId(),
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
//    public OrderManagementLineV2 createOrderManagementV4(String companyCodeId, String plantId, String languageId,
//                                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
//                                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
//        log.info("Getting StockType1InventoryList Inpute Values : companyCodeId ------> " + companyCodeId + " plantId -------> " + plantId + " languageId ------> " + languageId + " warehouseId --------> "
//                + warehouseId + " itemCode -----> " + itemCode + " binClassId --------> " + binClassId + " manufacturerName ---------> " + orderManagementLine.getManufacturerName());
//        List<IInventoryImpl> stockType1InventoryList = repo.orderService.getInventoryForOrderManagementV2(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
//        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//        if (stockType1InventoryList.isEmpty()) {
//            return repo.orderService.createEMPTYOrderManagementLineV2(orderManagementLine);
//        }
//        return updateAllocationV4(orderManagementLine, 1L, ORD_QTY, orderManagementLine.getItemCode(), loginUserId);
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
//    public OrderManagementLineV2 updateAllocationV4(OrderManagementLineV2 orderManagementLine, Long binClassId, Double ORD_QTY,
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
//                repo.orderService.getInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//
//        if (stockType1InventoryList == null || stockType1InventoryList.isEmpty()) {
//            return repo.orderService.updateOrderManagementLineV2(orderManagementLine);
//        }
//
//        // Getting Inventory GroupBy ST_BIN wise
//        List<IInventoryImpl> finalInventoryList = null;
//        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            log.info("INV_STRATEGY: " + INV_STRATEGY + shelfLifeIndicator);
//            if (!shelfLifeIndicator) {
//                orderBy = "iv.UTD_ON";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByCreatedOnV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            } else {
//                orderBy = "iv.EXP_DATE";
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByExpiryDateV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
//            log.info("INV_STRATEGY: " + INV_STRATEGY);
//            orderBy = "iv.LEVEL_ID";
//            finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("1")) { // FIFO
//            log.info("FIFO");
//            List<IInventory> levelIdList = repo.orderService.getInventoryForOrderManagementByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//            log.info("Group By Batch: " + levelIdList.size());
//            List<String> invQtyByLevelIdList = new ArrayList<>();
//            boolean toBeIncluded = true;
//            for (IInventory iInventory : levelIdList) {
//                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
//                if (ORD_QTY <= iInventory.getInventoryQty()) {
//                    orderBy = "iv.STR_NO";
//                    finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, stockTypeId, binClassId, alternateUom);
//                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
//                    newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
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
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
//            log.info("INV_STRATEGY: " + INV_STRATEGY);
//            List<IInventory> levelIdList = repo.orderService.getInventoryForOrderManagementGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
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
//                    newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
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
//                finalInventoryList = repo.orderService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");
//
//        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
//            return updateOrderManagementLineV2(orderManagementLine);
//        }
//
//        newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
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
//    public OrderManagementLineV2 orderAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
//                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
//                                                   String loginUserID) {
//        OrderManagementLineV2 newOrderManagementLine = null;
//        String alternateUom = orderManagementLine.getAlternateUom();
//        outerloop:
//        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
//            InventoryV2 stBinInventory = repo.orderService.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stBinWiseInventory.getBarcodeId(),
//                    stBinWiseInventory.getStorageBin(), alternateUom);
//            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);
//
//            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
//            if (stBinInventory == null) {
//                return updateOrderManagementLineV2(orderManagementLine);
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
////                if (allocatedQtyFromOrderMgmt > 0) {
////
////                    double[] inventoryQty = allocateInventory(allocatedQtyFromOrderMgmt, orderManagementLine.getNoBags(), stBinInventory.getInventoryQuantity(), stBinInventory.getAllocatedQuantity());
////
////                    // Create new Inventory Record
////                    InventoryV2 inventoryV2 = new InventoryV2();
////                    BeanUtils.copyProperties(stBinInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinInventory));
////
////                    if (inventoryQty != null && inventoryQty.length > 2) {
////                        inventoryV2.setInventoryQuantity(inventoryQty[0]);
////                        inventoryV2.setAllocatedQuantity(inventoryQty[1]);
////                        inventoryV2.setReferenceField4(inventoryQty[2]);
////                    }
////
////                    Double bagSize = inventoryV2.getBagSize() != null ? inventoryV2.getBagSize() : 0.0D;
////                    if (bagSize > inventoryV2.getInventoryQuantity()) {
////                        log.info("Loose Pack True");
////                        inventoryV2.setLoosePack(true);
////                    }
////
////                    inventoryV2.setReferenceDocumentNo(orderManagementLine.getRefDocNumber());
////                    inventoryV2.setReferenceOrderNo(orderManagementLine.getRefDocNumber());
////                    inventoryV2.setUpdatedOn(new Date());
////                    inventoryV2 = repo.inventoryV2Repository.save(inventoryV2);
////                    log.info("-----Inventory2 updated-------: " + inventoryV2);
////                }
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
//    private OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
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
//        newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
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
//    private OutboundLineV2 createOutboundLineV2(OrderManagementLineV2 orderManagementLineV2, OutboundHeaderV2 outboundHeaderV2) {
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
//                PickupHeaderV2 createdPickupHeader = repo.orderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
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
//}
