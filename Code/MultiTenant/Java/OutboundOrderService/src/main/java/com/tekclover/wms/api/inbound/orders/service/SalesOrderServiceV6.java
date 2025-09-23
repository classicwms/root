package com.tekclover.wms.api.inbound.orders.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.*;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesOrderServiceV6 extends BaseService {

    private final RepositoryProvider repo;
    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    protected String MW_AMS = "BP_ADMIN";


    /**
     *
     * @param salesOrders
     * @throws ParseException
     */
    @Async("asyncTaskExecutor")
    public void outboundProcess(List<SalesOrderV2> salesOrders) throws ParseException {
        for (SalesOrderV2 salesOrder : salesOrders){
            createSalesOrderList(salesOrder);
        }
    }

    /**
     * @param salesOrder
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void createSalesOrderList(SalesOrderV2 salesOrder) throws ParseException {

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
                try {
//                    postSalesOrder(salesOrder);
                    SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
                    List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
                    String companyCode = header.getCompanyCode();
                    String plantId = header.getBranchCode();
                    String newPickListNo = header.getPickListNumber();
                    String orderType = lineV2List.get(0).getOrderType();
                    String refDocNumber = header.getSalesOrderNumber();
                    String weight = lineV2List.get(0).getPriceSegment();

                    String warehouseId;
                    log.info("company , plant {} {}",companyCode,plantId);
                    // Get Warehouse
                    if (header.getWarehouseId() != null){
                        warehouseId = header.getWarehouseId();
                    }else {
                        Optional<Warehouse> dbWarehouse =
                                repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                        Warehouse WH = dbWarehouse.get();
                        warehouseId = WH.getWarehouseId();
                    }
                    String languageId = header.getLanguageId();
                    log.info("Warehouse ID: {}", warehouseId);

                    String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);

                    Optional<PreOutboundHeaderV2> orderProcessedStatus =
                            repo.preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                                    refDocNumber,3L, 0L);

                    if (!orderProcessedStatus.isEmpty()) {
                        throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
                    }

                    // PreBoundHeader
                    PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeader(companyCode, plantId, languageId, warehouseId,
                            preOutboundNo, header, orderType, MW_AMS,weight);
                    log.info("preOutboundHeader Created : " + preOutboundHeader);

                    // OrderManagementHeader
                    OrderManagementHeaderV2 orderManagementHeader = createOrderManagementHeader(preOutboundHeader, MW_AMS);
                    log.info("OrderManagementHeader Created : " + orderManagementHeader);

                    // OutboundHeader
                    OutboundHeaderV2 outboundHeader = createOutboundHeader(preOutboundHeader, orderManagementHeader.getStatusId(), header, MW_AMS);
                    log.info("OutboundHeader Created : " + outboundHeader);

                    // Collections for batch saving
                    List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
                    List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
                    List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());
                    List<InventoryV2> inventoryList = Collections.synchronizedList(new ArrayList<>());

                    // Process lines in parallel
                    List<CompletableFuture<Void>> futures = lineV2List.stream()
                            .map(outBoundLine -> CompletableFuture.runAsync(() -> {
                                try {
                                    processSingleSaleOrderLine(outBoundLine,preOutboundHeader,MW_AMS,preOutboundLines,outboundLines,managementLines,inventoryList);
                                } catch (Exception e) {
                                    log.error("Error processing PICK_LIST Line for SalesOrder: {}", header.getSalesOrderNumber(), e);
                                    throw new RuntimeException(e);
                                }
                            }, executorService))
                            .collect(Collectors.toList());

                    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                    try {
                        allFutures.join(); // Wait for all tasks to finish
                    } catch (CompletionException e) {
                        log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
                        throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
                    }

                    // Batch Save All Records
                    repo.preOutboundLineV2Repository.saveAll(preOutboundLines);
                    repo.orderManagementLineV2Repository.saveAll(managementLines);
                    repo.outboundLineV2Repository.saveAll(outboundLines);
//                    repo.inventoryV2Repository.saveAll(inventoryList);

                    if (managementLines != null) {
                        List<OrderManagementLineV2> NoStockLines = managementLines.stream()
                                .filter(line -> Objects.equals(line.getStatusId(), 47L))
                                .collect(Collectors.toList());

                        if (!NoStockLines.isEmpty()) {
                            statusDescription = getStatusDescription(47L, languageId);
                            repo.orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
                            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
                        }

                        List<OrderManagementLineV2> nonNoStockLines = managementLines.stream()
                                .filter(line -> !Objects.equals(line.getStatusId(), 47L))
                                .collect(Collectors.toList());

                        if (!nonNoStockLines.isEmpty()) {
                            createPickupHeaderNoV6(companyCode, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, nonNoStockLines);
                        }
                    }

                        updateStatusId(companyCode,plantId,warehouseId,refDocNumber,10L);
                }catch(Exception e){
                    log.error("Error processing outbound PICK_LIST Lines", e);
                    updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(),salesOrder.getSalesOrderHeader().getBranchCode(),
                            salesOrder.getSalesOrderHeader().getWarehouseId(),salesOrder.getSalesOrderHeader().getSalesOrderNumber(),100L);
                    throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
                }
        finally {
            executorService.shutdown();
        }
        }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param salesOrderHeaderV2
     * @param refField1ForOrderType
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundHeaderV2 createPreOutboundHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                         SalesOrderHeaderV2 salesOrderHeaderV2, String refField1ForOrderType, String loginUserId, String weight) throws ParseException {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(salesOrderHeaderV2, preOutboundHeader, CommonUtils.getNullPropertyNames(salesOrderHeaderV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
        preOutboundHeader.setConsignment(salesOrderHeaderV2.getSalesOrderNumber());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setOutboundOrderTypeId(3L);    // Hardcoded value "0"
        preOutboundHeader.setReferenceDocumentType("SaleOrder");
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);
        preOutboundHeader.setReferenceField3(weight);   //weight

        if (salesOrderHeaderV2.getPickListNumber() != null && salesOrderHeaderV2.getStoreID() != null) {
            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());
            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getStoreID());
            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getPickListNumber());
        } else {
            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getBranchCode());
            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getSalesOrderNumber());
        }

        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
        if (description != null) {
            preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
            preOutboundHeader.setPlantDescription(description.getPlantDesc());
            preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        statusDescription = getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        preOutboundHeader.setReferenceField10(statusDescription);
        preOutboundHeader.setStatusDescription(statusDescription);
        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy(loginUserId);
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = repo.preOutboundHeaderV2Repository.save(preOutboundHeader);
        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
        return createdPreOutboundHeader;
    }

    /**
     * @param createdPreOutboundHeader
     * @return
     */
    public OrderManagementHeaderV2 createOrderManagementHeader(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
        OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"
        statusDescription = getStatusDescription(41L, createdPreOutboundHeader.getLanguageId());
        newOrderManagementHeader.setStatusDescription(statusDescription);
        newOrderManagementHeader.setReferenceField7(statusDescription);
        newOrderManagementHeader.setPickupCreatedBy(loginUserId);
        newOrderManagementHeader.setPickupCreatedOn(new Date());
        return repo.orderManagementHeaderV2Repository.save(newOrderManagementHeader);
    }

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public OutboundHeaderV2 createOutboundHeader(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, SalesOrderHeaderV2 salesOrderHeaderV2, String loginUserId) throws ParseException {

        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        outboundHeader.setRefDocNumber(outboundHeader.getRefDocNumber());
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);
        statusDescription = getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
        outboundHeader.setStatusDescription(statusDescription);
//        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        outboundHeader.setConsignment(salesOrderHeaderV2.getPickListNumber());
        outboundHeader.setInvoiceNumber(salesOrderHeaderV2.getInvoice());
        if (outboundHeader.getOutboundOrderTypeId() == 3L) {
            outboundHeader.setCustomerType("INVOICE");
        }
//        if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
//            outboundHeader.setCustomerType("TRANSVERSE");
//        }
        outboundHeader.setCreatedBy(loginUserId);
        outboundHeader.setCreatedOn(new Date());
        outboundHeader = repo.outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader);
        return outboundHeader;
    }

    @Transactional
    public void processSingleSaleOrderLine(SalesOrderLineV2 salesOrderLineV2, PreOutboundHeaderV2 preOutboundHeaderV2,
                                           String loginUserID, List<PreOutboundLineV2> preOutboundLines,
                                           List<OutboundLineV2> outboundLines, List<OrderManagementLineV2> managementLines,
                                           List<InventoryV2> inventoryList)throws ParseException {

        PreOutboundLineV2 preOutboundLine = createPreOutboundLine(preOutboundHeaderV2, salesOrderLineV2, loginUserID);
        preOutboundLines.add(preOutboundLine);
        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);

        List<OrderManagementLineV2> orderManagementLineV2List = postOrderManagementLine(preOutboundLine, loginUserID,inventoryList);
        managementLines.addAll(orderManagementLineV2List);
        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLineV2List);


        for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {
            OutboundLineV2 outboundLineV2 = createOutboundLine(orderManagementLine);
            outboundLines.add(outboundLineV2);
            log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLineV2);
        }

    }

    /**
     *
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundLineV2 createPreOutboundLine(PreOutboundHeaderV2 preOutboundHeader, SalesOrderLineV2 salesOrderLine,
                                                         String loginUserId) throws ParseException {

            PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
            BeanUtils.copyProperties(salesOrderLine, preOutboundLine, CommonUtils.getNullPropertyNames(salesOrderLine));
            preOutboundLine.setLanguageId(preOutboundHeader.getLanguageId());
            preOutboundLine.setCompanyCodeId(preOutboundHeader.getCompanyCodeId());
            preOutboundLine.setPlantId(preOutboundHeader.getPlantId());
            preOutboundLine.setCustomerId(preOutboundHeader.getCustomerId());
            preOutboundLine.setCustomerName(preOutboundHeader.getCustomerName());
            preOutboundLine.setWarehouseId(preOutboundHeader.getWarehouseId());
            preOutboundLine.setRefDocNumber(preOutboundHeader.getRefDocNumber());
            preOutboundLine.setPreOutboundNo(preOutboundHeader.getPreOutboundNo());
            preOutboundLine.setPartnerCode(preOutboundHeader.getPartnerCode());
            preOutboundLine.setLineNumber(salesOrderLine.getLineReference());
            preOutboundLine.setItemCode(salesOrderLine.getSku());
            preOutboundLine.setOutboundOrderTypeId(3L);
            preOutboundLine.setStatusId(39L);
            preOutboundLine.setStockTypeId(1L);
            preOutboundLine.setSpecialStockIndicatorId(1L);
            preOutboundLine.setManufacturerName(MFR_NAME_V6);
            preOutboundLine.setManufacturerCode(MFR_NAME_V6);
            preOutboundLine.setReferenceField3(salesOrderLine.getPriceSegment());    //weight
            String barcodeId = salesOrderLine.getBarcodeId() != null ? salesOrderLine.getBarcodeId() : salesOrderLine.getSku();

            preOutboundLine.setBarcodeId(barcodeId);
            description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
            if (description != null) {
                preOutboundLine.setCompanyDescription(description.getCompanyDesc());
                preOutboundLine.setPlantDescription(description.getPlantDesc());
                preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());
            }
            statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeader.getLanguageId());
            preOutboundLine.setStatusDescription(statusDescription);
            preOutboundLine.setPickListNumber(salesOrderLine.getPickListNo());
            ImBasicData1V2 imBasicData1 = repo.imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                    preOutboundHeader.getLanguageId(), preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), salesOrderLine.getSku(), salesOrderLine.getManufacturerName(), 0L);
            log.info("imBasicData1 : " + imBasicData1);
            if (imBasicData1 != null) {
                preOutboundLine.setDescription(imBasicData1.getDescription());
                if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                    preOutboundLine.setItemType(getItemTypeDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
                            preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemType()));
                } else {
                    preOutboundLine.setItemType(imBasicData1.getItemTypeDescription());
                }
                if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                    preOutboundLine.setItemGroup(getItemGroupDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
                            preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemGroup()));
                } else {
                    preOutboundLine.setItemGroup(imBasicData1.getItemGroupDescription());
                }
            } else {
                preOutboundLine.setDescription(salesOrderLine.getSkuDescription());
            }
            preOutboundLine.setOrderQty(salesOrderLine.getOrderedQty());
            preOutboundLine.setOrderUom(salesOrderLine.getUom());
//        preOutboundLine.setRequiredDeliveryDate(orderV2.getRequiredDeliveryDate());
            preOutboundLine.setReferenceField1("SalesOrder");
            preOutboundLine.setDeletionIndicator(0L);
            preOutboundLine.setCreatedBy(loginUserId);
            preOutboundLine.setCreatedOn(new Date());

            log.info("preOutboundLine : " + preOutboundLine);

        return preOutboundLine;
    }

    /**
     * @param preOutboundLineV2 preOutboundLine
     * @param loginUserID       userId
     */
    public List<OrderManagementLineV2> postOrderManagementLine(PreOutboundLineV2 preOutboundLineV2, String loginUserID,List<InventoryV2> inventoryList) {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();

        log.info("Started Order Allocation for ItemCode: {}, Warehouse: {}, OrderQty: {}",
                preOutboundLineV2.getItemCode(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getOrderQty());

        String companyCodeId = preOutboundLineV2.getCompanyCodeId();
        String plantId = preOutboundLineV2.getPlantId();
        String warehouseId = preOutboundLineV2.getWarehouseId();
        String languageId = preOutboundLineV2.getLanguageId();
        String itemCode = preOutboundLineV2.getItemCode();
        double balanceOrderQty = preOutboundLineV2.getOrderQty();

        try {
            List<InventoryV2> inventoryV2List = repo.inventoryV2Repository.getInventoryAllocation(companyCodeId, plantId, languageId, warehouseId, itemCode);
            log.info("Inventory List {} in Order Allocation", inventoryV2List.size());


            if (inventoryV2List.isEmpty()) {
                log.warn("No inventory available for allocation for itemCode: {}", itemCode);
            }
            Long STATUS_ID = null;
            for (InventoryV2 inventory : inventoryV2List) {

                if (balanceOrderQty <= 0.0) break; // Stop loop when no quantity left to allocate

                Long count = repo.orderManagementLineV2Repository.getCount(inventory.getCompanyCodeId(), inventory.getPlantId(), inventory.getLanguageId(),
                        inventory.getWarehouseId(), inventory.getBarcodeId(), inventory.getItemCode());

                if (count >0) {
                    continue;
                }

                if (balanceOrderQty <= inventory.getInventoryQuantity()) {

                    double INV_QTY = inventory.getInventoryQuantity();
                    double ALLOC_QTY = balanceOrderQty;

                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(preOutboundLineV2, orderLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                    orderLine.setBarcodeId(inventory.getBarcodeId());
                    orderLine.setInventoryQty(INV_QTY);
                    orderLine.setAllocatedQty(ALLOC_QTY);
//                        orderLine.setNoBags(roundUp(inventory.getNoBags() - (ALLOC_QTY / inventory.getBagSize())));
                    orderLine.setProposedStorageBin(inventory.getStorageBin());

                    STATUS_ID = 43L; // Fully Allocated

                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setProposedPackBarCode("99999");
                    orderManagementLineV2List.add(orderLine);

                    // Inventory_Create
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                    inventoryV2.setInventoryQuantity(INV_QTY - ALLOC_QTY);
//                    inventoryV2.setAllocatedQuantity(ALLOC_QTY);
//                    inventoryV2.setBinClassId(1L);
//                        inventoryV2.setNoBags(roundUp(inventory.getNoBags() - (ALLOC_QTY / inventory.getBagSize())));
//                    inventoryV2.setReferenceField4(inventoryV2.getInventoryQuantity() + ALLOC_QTY);
//                    repo.inventoryV2Repository.save(inventoryV2);
//                    repo.orderManagementLineV2Repository.save(orderLine);
//                    inventoryList.add(inventoryV2);
                    balanceOrderQty -= ALLOC_QTY; // reduce remaining order qty
                    log.debug("Allocated {} qty from inventory {}, Remaining OrderQty: {}", ALLOC_QTY, inventory.getBarcodeId(), balanceOrderQty);
                }
            }

            log.info("Balance OrderQty is {} -------------------> ", balanceOrderQty);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("BP");
            if (balanceOrderQty != 0.0 || inventoryV2List.isEmpty()) {
                OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
                BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                orderManagementLine.setOrderQty(balanceOrderQty);
                orderManagementLine.setBarcodeId(preOutboundLineV2.getBarcodeId());
                orderManagementLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
                orderManagementLine.setStatusDescription(statusDescription);
                orderManagementLine.setReferenceField7(statusDescription);
                orderManagementLine.setProposedStorageBin("");
                orderManagementLine.setProposedPackBarCode("");
                orderManagementLine.setInventoryQty(0D);
                orderManagementLine.setAllocatedQty(0D);
                orderManagementLineV2List.add(orderManagementLine);
                log.info("orderManagementLine created for UnAllocated Order: " + orderManagementLine);
            }

        } catch (Exception ex) {
            log.error("Unexpected error during order allocation: {}", ex.getMessage(), ex);
            throw new RuntimeException("Order allocation process failed", ex);
        }
        return orderManagementLineV2List;
    }

    /**
     *
     * @param orderManagementLineV2List orderManagementLine Input pass outbound Line Create
     */
    public OutboundLineV2 createOutboundLine(OrderManagementLineV2 orderManagementLineV2List) {

        OutboundLineV2 outboundLineV2 = new OutboundLineV2();
        BeanUtils.copyProperties(orderManagementLineV2List, outboundLineV2, CommonUtils.getNullPropertyNames(orderManagementLineV2List));
        outboundLineV2.setDeliveryQty(0D);
        outboundLineV2.setBarcodeId(orderManagementLineV2List.getBarcodeId());
        outboundLineV2.setDeletionIndicator(0L);
        outboundLineV2.setCreatedOn(new Date());
        return  outboundLineV2;
    }

//--------------------------------------------FG---------------------------------------------------------

    /**
     *
     * @param salesOrderV2
     */
    @Async("asyncTaskExecutor")
    public void outboundProcessV6(List<SalesOrderV2> salesOrderV2,Long orderTypeId){
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        for(SalesOrderV2 orderV2 : salesOrderV2){
            createSalesOrderListV6(orderV2,orderTypeId);
        }
    }
    /**
     * BharathPackaging - BP
     *
     * @param salesOrder
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    public void createSalesOrderListV6(SalesOrderV2 salesOrder,Long orderTypeId) {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("BP");
//            postSalesOrderV6(salesOrder,orderTypeId);
            SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
            List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
            String companyCode = header.getCompanyCode();
            String plantId = header.getBranchCode();
            String orderType = lineV2List.get(0).getOrderType();
            String refDocNumber = header.getSalesOrderNumber();
            log.info("company , plant {} {}",companyCode,plantId);
            // Get Warehouse
            String warehouseId;
            if (header.getWarehouseId() != null){
                warehouseId = header.getWarehouseId();
            }else {
                Optional<Warehouse> dbWarehouse =
                        repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                Warehouse WH = dbWarehouse.get();
                warehouseId = WH.getWarehouseId();
            }

            String languageId = header.getLanguageId();
            log.info("Warehouse ID: {}", warehouseId);
            String unitType = lineV2List.get(0).getUnitType();

            String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);

            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    repo.preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            refDocNumber, orderTypeId, 0L);

            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }
            // PreBoundHeader
            PreOutboundHeaderV2 preOutboundHeader = createPreOutboundHeaderV6(companyCode, plantId, languageId, warehouseId,
                    preOutboundNo, header, orderType, MW_AMS, orderTypeId, unitType);
            log.info("preOutboundHeader Created : " + preOutboundHeader);

            OutboundHeaderV2 outboundHeader = createOutboundHeaderV6(preOutboundHeader);
            log.info("OutboundHeader Created : " + outboundHeader);

            // Collections for batch saving
            List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());

            // Process lines in parallel
            List<CompletableFuture<Void>> futures = lineV2List.stream()
                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
                        try {
                            processSingleSaleOrderLineV6(outBoundLine, preOutboundHeader, preOutboundLines);
                        } catch (Exception e) {
                            log.error("Error processing for SalesOrder: {}", header.getSalesOrderNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
            }
            // Batch Save All Records
            repo.preOutboundLineV2Repository.saveAll(preOutboundLines);
            updateStatusId(companyCode, plantId, warehouseId, refDocNumber, 10L);
            log.info("preInbound header and line Processed");

        } catch (Exception e) {
            log.error("Error processing outbound", e);
            updateStatusId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode(),
                    salesOrder.getSalesOrderHeader().getWarehouseId(), salesOrder.getSalesOrderHeader().getSalesOrderNumber(), 100L);

            throw new BadRequestException("PreOutbound Order Processing failed: " + e.getMessage());

        } finally {
            executorService.shutdown();
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param salesOrderHeaderV2
     * @param refField1ForOrderType
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundHeaderV2 createPreOutboundHeaderV6(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                         SalesOrderHeaderV2 salesOrderHeaderV2, String refField1ForOrderType, String loginUserId,
                                                         Long orderTypeId,String unitType) throws ParseException {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(salesOrderHeaderV2, preOutboundHeader, CommonUtils.getNullPropertyNames(salesOrderHeaderV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
        preOutboundHeader.setConsignment(salesOrderHeaderV2.getSalesOrderNumber());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setOutboundOrderTypeId(orderTypeId);    // Hardcoded value "0"
        preOutboundHeader.setReferenceDocumentType("SaleOrder");
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
//        preOutboundHeader.setRequiredDeliveryDate(salesOrderHeaderV2.getRequiredDeliveryDate());
        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);

        if (salesOrderHeaderV2.getPickListNumber() != null && salesOrderHeaderV2.getStoreID() != null) {
            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());
            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getStoreID());
            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getPickListNumber());
        } else {
            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getSalesOrderNumber());
            preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getBranchCode());
            preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getSalesOrderNumber());
        }

        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
        if (description != null) {
            preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
            preOutboundHeader.setPlantDescription(description.getPlantDesc());
            preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        statusDescription = getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        preOutboundHeader.setReferenceField10(statusDescription);
        preOutboundHeader.setStatusDescription(statusDescription);
        preOutboundHeader.setReferenceField6(unitType);
        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy(loginUserId);
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = repo.preOutboundHeaderV2Repository.save(preOutboundHeader);
//        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
        return createdPreOutboundHeader;
    }


    /**
     * BharathPackaging - BP
     *
     * processSingleSaleOrderLine
     * @param salesOrderLineV2
     * @param preOutboundHeaderV2
     * @param preOutboundLineV2
     * @throws Exception
     */
    private void processSingleSaleOrderLineV6(SalesOrderLineV2 salesOrderLineV2, PreOutboundHeaderV2 preOutboundHeaderV2,
                                            List<PreOutboundLineV2> preOutboundLineV2) throws Exception {
        // Collect PreOutboundLine
        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV6(preOutboundHeaderV2, salesOrderLineV2, MW_AMS);
        preOutboundLineV2.add(preOutboundLine);
        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);
    }

    /**
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundLineV2 createPreOutboundLineV6(PreOutboundHeaderV2 preOutboundHeader, SalesOrderLineV2 salesOrderLineV2,
                                                     String loginUserId) throws ParseException {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(salesOrderLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(salesOrderLineV2));
        preOutboundLine.setLanguageId(preOutboundHeader.getLanguageId());
        preOutboundLine.setCompanyCodeId(preOutboundHeader.getCompanyCodeId());
        preOutboundLine.setPlantId(preOutboundHeader.getPlantId());
        preOutboundLine.setCustomerId(preOutboundHeader.getCustomerId());
        preOutboundLine.setCustomerName(preOutboundHeader.getCustomerName());
        preOutboundLine.setWarehouseId(preOutboundHeader.getWarehouseId());
        preOutboundLine.setRefDocNumber(preOutboundHeader.getRefDocNumber());
        preOutboundLine.setPreOutboundNo(preOutboundHeader.getPreOutboundNo());
        preOutboundLine.setPartnerCode(preOutboundHeader.getPartnerCode());
        preOutboundLine.setReferenceField6(salesOrderLineV2.getUnitType());
        preOutboundLine.setLineNumber(salesOrderLineV2.getLineReference());
        preOutboundLine.setItemCode(salesOrderLineV2.getSku());
        preOutboundLine.setOutboundOrderTypeId(preOutboundHeader.getOutboundOrderTypeId());
        preOutboundLine.setStatusId(39L);
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setManufacturerName(MFR_NAME_V6);
        preOutboundLine.setManufacturerCode(MFR_NAME_V6);
        String barcodeId = salesOrderLineV2.getBarcodeId() != null ? salesOrderLineV2.getBarcodeId() : salesOrderLineV2.getSku();
        preOutboundLine.setBarcodeId(barcodeId);
        description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
        if (description != null) {
            preOutboundLine.setCompanyDescription(description.getCompanyDesc());
            preOutboundLine.setPlantDescription(description.getPlantDesc());
            preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());
        }
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeader.getLanguageId());
        preOutboundLine.setStatusDescription(statusDescription);
        preOutboundLine.setPickListNumber(salesOrderLineV2.getPickListNo());
        ImBasicData1V2 imBasicData1 = repo.imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                preOutboundHeader.getLanguageId(), preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(), preOutboundHeader.getWarehouseId(), salesOrderLineV2.getSku(), salesOrderLineV2.getManufacturerName(), 0L);
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
            if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                preOutboundLine.setItemType(getItemTypeDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
                        preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemType()));
            } else {
                preOutboundLine.setItemType(imBasicData1.getItemTypeDescription());
            }
            if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                preOutboundLine.setItemGroup(getItemGroupDesc(preOutboundHeader.getCompanyCodeId(), preOutboundHeader.getPlantId(),
                        preOutboundHeader.getLanguageId(), preOutboundHeader.getWarehouseId(), imBasicData1.getItemGroup()));
            } else {
                preOutboundLine.setItemGroup(imBasicData1.getItemGroupDescription());
            }
        } else {
            preOutboundLine.setDescription(salesOrderLineV2.getSkuDescription());
        }
        preOutboundLine.setOrderQty(salesOrderLineV2.getOrderedQty());
        preOutboundLine.setOrderUom(salesOrderLineV2.getUom());
//        preOutboundLine.setRequiredDeliveryDate(salesOrderLineV2.getRequiredDeliveryDate());
        preOutboundLine.setReferenceField1("SalesOrder");
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy(loginUserId);
        preOutboundLine.setCreatedOn(new Date());
        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }

//-------------------------------------------------ob order-------------------------------------------------------------

    /**
     * @param salesOrder
     */
    public SalesOrderV2 postSalesOrderV6(SalesOrderV2 salesOrder,Long orderTypeId) throws ParseException {
        log.info("SalesOrderHeader received from External: " + salesOrder);
        OutboundOrderV2 savedSoHeader = saveSalesOrderV6(salesOrder,orderTypeId);
        log.info("salesOrderHeader: " + savedSoHeader);
        return salesOrder;
    }

    private OutboundOrderV2 saveSalesOrderV6(@Valid SalesOrderV2 salesOrder,Long orderTypeId) throws ParseException {
        try {
            SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();
            List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();

            String unitType = salesOrderLines.get(0).getUnitType();

            if (orderTypeId.equals(1L) || orderTypeId.equals(4L)){
                if (unitType == null || unitType.isEmpty()){
                    throw new RuntimeException("Unit Type is mandatory for this order");
                }
            }

            OutboundOrderV2 apiHeader = new OutboundOrderV2();

            BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
            if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
            } else {
                Optional<Warehouse> warehouse =
                        repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
                                salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
                                0L);
                apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
            }
            apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
            apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
            apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);

            apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
            apiHeader.setPartnerCode(salesOrderHeader.getCustomerId());
            apiHeader.setPartnerName(salesOrderHeader.getStoreName());
            apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
            apiHeader.setPickListStatus(salesOrderHeader.getStatus());
            apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());

            apiHeader.setOutboundOrderTypeID(orderTypeId);

            apiHeader.setRefDocumentType("PICK LIST");                              // Hardcoded value "SaleOrder"
            apiHeader.setCustomerType("INVOICE");                                //HardCoded
            apiHeader.setRefDocumentType(getOutboundOrderTypeDesc(apiHeader.getOutboundOrderTypeID()));
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
            apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());
            apiHeader.setMiddlewareTable(salesOrderLines.get(0).getUnitType());      // Unit Type

            apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());

            try {
                Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
                apiHeader.setRequiredDeliveryDate(reqDate);
            } catch (Exception e) {
                throw new BadRequestException("Date format should be MM-dd-yyyy");
            }

            IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
                    salesOrderHeader.getCompanyCode(), salesOrderHeader.getStoreID(), apiHeader.getWarehouseID());
            if (iKeyValuePair != null) {
                apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
                apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
            }
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
            Set<OutboundOrderLineV2> orderLines = new HashSet<>();
            String barcodeId = null;
            for (SalesOrderLineV2 soLine : salesOrderLines) {
                OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
                BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));
                apiLine.setBrand(soLine.getBrand());
                apiLine.setOrigin(soLine.getOrigin());
                apiLine.setPackQty(soLine.getPackQty());
                apiLine.setExpectedQty(soLine.getExpectedQty());
                apiLine.setSupplierName(soLine.getSupplierName());
                apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
                apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
                apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
                apiLine.setManufacturerName(soLine.getManufacturerName());        // BRAND_NM
                apiLine.setManufacturerCode(soLine.getManufacturerCode());
                apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
                apiLine.setStoreID(salesOrderHeader.getStoreID());
                apiLine.setRefField1ForOrderType(soLine.getOrderType());
                apiLine.setCustomerType("INVOICE");                                //HardCoded
                apiLine.setOutboundOrderTypeID(orderTypeId);
                apiLine.setMiddlewareTable(soLine.getUnitType());                 // Unit Type
                apiLine.setPriceSegment(soLine.getPriceSegment());                // Weight

                if (orderTypeId.equals(4L)){
                    apiLine.setBarcodeId(soLine.getBarcodeId());
                }else {
                    apiLine.setBarcodeId(barcodeId);
                }
                apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
                apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
                apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
                apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
                apiLine.setPickListNo(soLine.getPickListNo());
                apiLine.setImsSaleTypeCode(salesOrderHeader.getImsSaleTypeCode());

                apiLine.setMiddlewareId(soLine.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
                if (salesOrderHeader.getRequiredDeliveryDate() != null) {
                    if (salesOrderHeader.getRequiredDeliveryDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if (salesOrderHeader.getRequiredDeliveryDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(salesOrderHeader.getRequiredDeliveryDate());
                            }
                            if (salesOrderHeader.getRequiredDeliveryDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
                            }
                            apiHeader.setRequiredDeliveryDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                    if (salesOrderHeader.getRequiredDeliveryDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = salesOrderHeader.getRequiredDeliveryDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiHeader.setRequiredDeliveryDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                }
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
                apiHeader.setProcessedStatusId(1L);
                log.info("apiHeader : " + apiHeader);
                OutboundOrderV2 createdOrder = saveOutboundOrders(apiHeader);
                log.info("SalesOrder Order Success: " + createdOrder);
                return apiHeader;
            } else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                OutboundOrderV2 createdOrder = saveOutboundOrders(apiHeader);
                log.info("SalesOrder Order Failed: " + createdOrder);
                throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

//========================================================Process Outbound FG===============================================================
//FG Order Process

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    @Transactional
    public List<OrderManagementLineV2> createOrderManagementLineV6(String companyCodeId, String plantId, String languageId,
                                                                   String warehouseId, String preOutboundNo, String refDocNumber, String loginUserID) {

        try {
            List<PreOutboundLineV2> preOutboundLineV2 = repo.preOutboundLineV2Repository.getPreOutboundLine(companyCodeId,plantId,warehouseId,preOutboundNo,refDocNumber);

            Optional<PreOutboundHeaderV2> preOutboundHeader =
                    repo.preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, 0L);

            if (preOutboundHeader.isEmpty()) {
                throw new BadRequestException("PreOutboundHeader Doesn't Exist For this Input Values CompanyId - " + companyCodeId +
                        " PlantId - " + plantId + " WarehouseId - " + warehouseId + " PreOutboundNo - " + preOutboundNo + " RefDocNo " + refDocNumber);
            }

            log.info("OrderManagementHeader Creation Started -------------------> ");
            createOrderManagementHeaderV6(preOutboundHeader.get(), loginUserID);
//            log.info("OutboundHeader Creation Started ----------------------> ");
//            createOutboundHeaderV6(preOutboundHeader.get(), loginUserID);

            List<OrderManagementLineV2> orderManagementLineV2List = null;
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineV2) {

                orderManagementLineV2List = postOrderManagementLineV6(preOutboundLine, loginUserID);
            }


            List<OrderManagementLineV2> orderManagementLineV2s =
                    repo.orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdNotInAndDeletionIndicator(
                            companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, Collections.singletonList(47L), 0L);

            if (!orderManagementLineV2s.isEmpty()) {
                log.info("OutboundLine Create BasedOn OrderManagementLine Size {} -- ", orderManagementLineV2s.size());
                createOutboundLineV6(orderManagementLineV2s);
            }

            if (!orderManagementLineV2s.isEmpty()) {
                List<OrderManagementLineV2> NoStockLines = orderManagementLineV2s.stream()
                        .filter(line -> Objects.equals(line.getStatusId(), 47L))
                        .collect(Collectors.toList());

                if (!NoStockLines.isEmpty()) {
                    statusDescription = getStatusDescription(47L, languageId);
                    repo.orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
                    log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
                }

//                List<OrderManagementLineV2> nonNoStockLines = orderManagementLineV2List.stream()
//                        .filter(line -> !Objects.equals(line.getStatusId(), 47L))
//                        .collect(Collectors.toList());


                if (!orderManagementLineV2s.isEmpty()) {
                    log.info("FG PickupHeader Creation Started ----------> OrderManagementLine Size is {} ", orderManagementLineV2s.size());
                    createPickupHeaderNoV6(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, orderManagementLineV2s);
                }
            }
            return orderManagementLineV2List;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader outboundHeaderCreate
     * @param loginUserId              userId
     */
    @Transactional
    public void createOrderManagementHeaderV6(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
        try {
            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
            newOrderManagementHeader.setPickupCreatedOn(new Date());
            repo.orderManagementHeaderV2Repository.save(newOrderManagementHeader);
            log.info("OrderManagement Created Successfully OrderNo is - " + newOrderManagementHeader.getRefDocNumber());
        } catch (Exception e) {
            throw new RuntimeException("Exception Throw in OrderManagementHeader Creation " + e.getMessage());
        }
    }

    /**
     * @param createdPreOutboundHeader outboundHeader Create from PreOutboundHeader Input's
     */
    @Transactional
    public OutboundHeaderV2 createOutboundHeaderV6(PreOutboundHeaderV2 createdPreOutboundHeader) {
        try {
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            outboundHeader.setRefDocDate(new Date());
            outboundHeader.setCustomerType("INVOICE");
            outboundHeader.setCreatedBy(createdPreOutboundHeader.getCreatedBy());
            outboundHeader.setCreatedOn(new Date());
            repo.outboundHeaderV2Repository.save(outboundHeader);
            log.info("Created outboundHeader Successfully OrderNo is -  : " + outboundHeader.getRefDocNumber());
            return outboundHeader;
        } catch (Exception e) {
            throw new RuntimeException("Throw Exception in OutboundHeader Creation" + e.getMessage());
        }
    }

    /**
     * @param preOutboundLineV2 preOutboundLine
     * @param loginUserID       userId
     */
    public List<OrderManagementLineV2> postOrderManagementLineV6(PreOutboundLineV2 preOutboundLineV2, String loginUserID) {
        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();

        log.info("Started Order Allocation for ItemCode: {}, Warehouse: {}, OrderQty: {}",
                preOutboundLineV2.getItemCode(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getOrderQty());

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        String companyCodeId = preOutboundLineV2.getCompanyCodeId();
        String plantId = preOutboundLineV2.getPlantId();
        String warehouseId = preOutboundLineV2.getWarehouseId();
        String languageId = preOutboundLineV2.getLanguageId();
        String itemCode = preOutboundLineV2.getItemCode();
        double balanceOrderQty = preOutboundLineV2.getOrderQty();

        try {
            List<InventoryV2> inventoryV2List = repo.inventoryV2Repository.getInventoryAllocation(companyCodeId, plantId, languageId, warehouseId, itemCode);
            log.info("Inventory List {} in Order Allocation", inventoryV2List.size());

            if (inventoryV2List.isEmpty()) {
                log.warn("No inventory available for allocation for itemCode: {}", itemCode);
            }
            Long STATUS_ID = null;
            for (InventoryV2 inventory : inventoryV2List) {

                if (balanceOrderQty <= 0.0) break; // Stop loop when no quantity left to allocate

                Long count = repo.orderManagementLineV2Repository.getCount(inventory.getCompanyCodeId(), inventory.getPlantId(), inventory.getLanguageId(),
                        inventory.getWarehouseId(), inventory.getBarcodeId(), inventory.getItemCode());

                if (count >0) {
                    continue;
                }

                double INV_QTY = inventory.getInventoryQuantity();
                double ALLOC_QTY;

                log.info("INV_QTY  -----> {}", INV_QTY);
                log.info("balanceOrderQty ----> {}", balanceOrderQty);

                if (INV_QTY == 0.0) {
                    ALLOC_QTY = 0D;
                } else {
                    ALLOC_QTY = Math.min(balanceOrderQty, INV_QTY);
                }

                log.info("ALLOC_QTY  -----> {}", ALLOC_QTY);

                if (ALLOC_QTY > 0.0) {
                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(preOutboundLineV2, orderLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                    orderLine.setBarcodeId(inventory.getBarcodeId());
                    orderLine.setInventoryQty(INV_QTY);
                    orderLine.setAllocatedQty(ALLOC_QTY);
//                    orderLine.setNoBags(roundUp(inventory.getNoBags() - (ALLOC_QTY / inventory.getBagSize())));
                    orderLine.setProposedStorageBin(inventory.getStorageBin());

                    // Determine status
                    if (ALLOC_QTY < balanceOrderQty) {
                        STATUS_ID = 42L; // Partially Allocated
                    }
                    if (ALLOC_QTY == balanceOrderQty) {
                        STATUS_ID = 43L; // Fully Allocated
                    }

                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setProposedPackBarCode("99999");

                    // Inventory_Create
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                    inventoryV2.setInventoryQuantity(INV_QTY - ALLOC_QTY);
//                    inventoryV2.setAllocatedQuantity(ALLOC_QTY);
//                    inventoryV2.setBinClassId(1L);
//                    inventoryV2.setReferenceField4(inventoryV2.getInventoryQuantity() + ALLOC_QTY);

//                    repo.inventoryV2Repository.save(inventoryV2);
                    orderManagementLineV2List.add(repo.orderManagementLineV2Repository.save(orderLine));
                    balanceOrderQty -= ALLOC_QTY; // reduce remaining order qty
                    log.debug("Allocated {} qty from inventory {}, Remaining OrderQty: {}", ALLOC_QTY, inventory.getBarcodeId(), balanceOrderQty);
                }
            }
                log.info("Balance OrderQty is {} -------------------> ", balanceOrderQty);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("BP");
                if (balanceOrderQty != 0.0 || inventoryV2List.isEmpty()) {
                    OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                    orderManagementLine.setOrderQty(balanceOrderQty);
                    orderManagementLine.setBarcodeId(preOutboundLineV2.getBarcodeId());
                    orderManagementLine.setStatusId(47L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
                    orderManagementLine.setStatusDescription(statusDescription);
                    orderManagementLine.setReferenceField7(statusDescription);
                    orderManagementLine.setProposedStorageBin("");
                    orderManagementLine.setProposedPackBarCode("");
                    orderManagementLine.setInventoryQty(0D);
                    orderManagementLine.setAllocatedQty(0D);
                    orderManagementLine = repo.orderManagementLineV2Repository.save(orderManagementLine);
                    log.info("orderManagementLine created for UnAllocated Order: " + orderManagementLine);
                }
        } catch (Exception ex) {
            log.error("Unexpected error during order allocation: {}", ex.getMessage(), ex);
            throw new RuntimeException("Order allocation process failed", ex);
        }
        return orderManagementLineV2List;
    }

    /**
     * @param orderManagementLineV2List orderManagementLine Input pass outbound Line Create
     */
    void createOutboundLineV6(List<OrderManagementLineV2> orderManagementLineV2List) {
        try {
            List<OutboundLineV2> outboundLineV2List = new ArrayList<>();
            orderManagementLineV2List.forEach(line -> {
                OutboundLineV2 outboundLineV2 = new OutboundLineV2();
                BeanUtils.copyProperties(line, outboundLineV2, CommonUtils.getNullPropertyNames(line));
                outboundLineV2.setDeliveryQty(0D);
                outboundLineV2.setDeletionIndicator(0L);
                outboundLineV2.setCreatedOn(new Date());
                outboundLineV2List.add(outboundLineV2);
            });
            repo.outboundLineV2Repository.saveAll(outboundLineV2List);
        } catch (Exception e) {
            log.error("OutboundLine Create Failed Error throw {} -----> ", e.getMessage(), e);
            throw new RuntimeException("Exception throw in OutboundLine Creation");
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderManagementLineV2
     */
    public void createPickupHeaderNoV6(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                     String refDocNumber, List<OrderManagementLineV2> orderManagementLineV2) {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        long NUM_RAN_CODE = 10;
        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
        log.info("----------New PU_NO--------> : " + PU_NO);

        if (orderManagementLineV2 != null && !orderManagementLineV2.isEmpty()) {
            for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2) {
                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                newPickupHeader.setPickupNumber(PU_NO);
                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());
                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);
                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());
                //Setting InventoryQuantity from orderManagementLine
                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());
                //Setting BagSize
                newPickupHeader.setBagSize(orderManagementLine.getInventoryQty());
                newPickupHeader.setNoBags(orderManagementLine.getNoBags());
                // REF_FIELD_1
                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());
                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                newPickupHeader.setSalesOrderNumber(orderManagementLine.getSalesOrderNumber());
                newPickupHeader.setPickListNumber(orderManagementLine.getPickListNumber());
                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                newPickupHeader.setReferenceDocumentType(orderManagementLine.getReferenceDocumentType());
                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                newPickupHeader.setTokenNumber(orderManagementLine.getTokenNumber());
                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());

                pickupHeaderV2Repository.save(newPickupHeader);
                log.info("pickupHeader created: " + newPickupHeader);
                repo.orderManagementLineV2Repository.updateOrderManagementLineV6(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());
            }
            repo.outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
            repo.orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        }
    }

//=======================================================================================================================
    /**
     *
     * @param newOutboundOrder
     * @return
     * @throws ParseException
     */
    public OutboundOrderV2 saveOutboundOrders(OutboundOrderV2 newOutboundOrder) throws ParseException {

        try {
            OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.
                    findByRefDocumentNoAndOutboundOrderTypeID(newOutboundOrder.getOrderId(), newOutboundOrder.getOutboundOrderTypeID());
            if(dbOutboundOrder != null) {
                throw new BadRequestException("Order is getting Duplicated");
            }
            newOutboundOrder.setUpdatedOn(new Date());
            OutboundOrderV2 outboundOrder = outboundOrderV2Repository.save(newOutboundOrder);
            return outboundOrder;
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocumentNo
     * @param processStatusId
     */
    public void updateStatusId(String companyCodeId, String plantId, String warehouseId, String refDocumentNo, Long processStatusId){
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        outboundOrderV2Repository.updateObOrderStatus(companyCodeId,plantId,warehouseId,refDocumentNo,processStatusId);

    }

}
