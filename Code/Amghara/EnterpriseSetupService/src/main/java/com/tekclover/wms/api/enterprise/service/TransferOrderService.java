package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.enterprise.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.enterprise.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.enterprise.transaction.model.dto.IInventory;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.WarehouseId;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.SOHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.SOLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.ShipmentOrderV2;
import com.tekclover.wms.api.enterprise.transaction.repository.*;
import com.tekclover.wms.api.enterprise.transaction.service.BaseService;
import com.tekclover.wms.api.enterprise.transaction.service.*;
import com.tekclover.wms.api.enterprise.transaction.util.DateUtils;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TransferOrderService extends BaseService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    WarehouseIdRepository warehouseRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    String statusDescription = null;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Autowired
    PickupHeaderService pickupHeaderService;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    OrderService orderService;

    @Autowired
    MastersService mastersService;

    @Async("asyncTaskExecutor")
    public ShipmentOrderV2 createShipmentOrderList(ShipmentOrderV2 shipmentOrderV2)  {
//        List<ShipmentOrderV2> shipmentOrderV2List = Collections.synchronizedList(new ArrayList<>());
        log.info("Outbound Process Start {} Shipment Order", shipmentOrderV2);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
//            for (ShipmentOrderV2 shipmentOrderV2 : shipmenOrder) {
            SOHeaderV2 header = shipmentOrderV2.getSoHeader();
            List<SOLineV2> lineV2List = shipmentOrderV2.getSoLine();
            String companyCode = header.getCompanyCode();
            String plantId = header.getBranchCode();
            String orderType = lineV2List.get(0).getOrderType();

            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            header.getTransferOrderNumber(), 0L, 0L);

            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + header.getTransferOrderNumber() +
                        " already processed. Reprocessing can't be allowed.");
            }

            // Get Warehouse
            Optional<WarehouseId> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
            WarehouseId WH = dbWarehouse.get();
            String warehouseId = WH.getWarehouseId();
            String languageId = WH.getLanguageId();
            log.info("Warehouse ID: {}", warehouseId);

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
            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(preOutboundHeader, orderManagementHeader.getStatusId());
            log.info("OutboundHeader Created : " + outboundHeader);

            // Collections for batch saving
            List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
            List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
            List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());


            // Process lines in parallel
            List<CompletableFuture<Void>> futures = lineV2List.stream()
                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
                        try {
                            processSingleShipmentLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
                                    preOutboundLines, outboundLines, managementLines);
                        } catch (Exception e) {
                            log.error("Error processing Purchase Retrun Line for SalesOrder: {}", header.getTransferOrderNumber(), e);
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
            preOutboundLineV2Repository.saveAll(preOutboundLines);
//                orderManagementLineV2Repository.saveAll(managementLines);
            outboundLineV2Repository.saveAll(outboundLines);

            log.info("PickupHeader Creation Process Started -----------> RefDocNo is {} ", outboundHeader.getRefDocNumber());
            createPickUpHeaderAssignPickerModified(companyCode, plantId, languageId, warehouseId, header,
                    preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
            log.info("PickupHeader Creation Process Completed -----------> RefDocNo is {} ", outboundHeader.getRefDocNumber());


            // No_Stock_Items
            statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCode, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
            orderService.updateProcessedOrderV2(outboundHeader.getRefDocNumber(), outboundHeader.getOutboundOrderTypeId(),  10L);
//                shipmentOrderV2List.add(shipmentOrderV2);
//            }
        } catch (
                Exception e) {
            log.error("Error processing outbound purchase return Lines", e);
            InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
            inboundOrderCancelInput.setCompanyCodeId(shipmentOrderV2.getSoHeader().getCompanyCode());
            inboundOrderCancelInput.setPlantId(shipmentOrderV2.getSoHeader().getBranchCode());
            inboundOrderCancelInput.setRefDocNumber(shipmentOrderV2.getSoHeader().getTransferOrderNumber());
            inboundOrderCancelInput.setReferenceField1(getOutboundOrderTypeTable(4L));
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
            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Outbound Process Completed for {} Shipment order", shipmentOrderV2);
        return shipmentOrderV2;
    }

    /**
     * @return
     */
    private String getPreOutboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
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
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                          String preOutboundNo, SOHeaderV2 headerV2, String orderType, String companyText,
                                                          String plantText, String warehouseText) {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(headerV2, preOutboundHeader, CommonUtils.getNullPropertyNames(headerV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCode);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(headerV2.getTransferOrderNumber());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(headerV2.getBranchCode());
        preOutboundHeader.setOutboundOrderTypeId(0L);
        preOutboundHeader.setReferenceDocumentType("WMS to NON-WMS");                                                // Hardcoded value "SO"
        preOutboundHeader.setCustomerCode("TRANSVERSE");
        preOutboundHeader.setTransferRequestType("Shipment Order");
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
        preOutboundHeader.setReferenceDocumentType("WMS to NON-WMS");
        preOutboundHeader.setSalesOrderNumber(headerV2.getTransferOrderNumber());
        preOutboundHeader.setPickListNumber(headerV2.getTransferOrderNumber());
        preOutboundHeader.setTargetBranchCode(headerV2.getTargetBranchCode());

        preOutboundHeader.setFromBranchCode(plantId);

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
     *
     * @param createdPreOutboundHeader createPreOutboundHeader
     * @param statusId statusId
     * @return
     */
    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId) {
        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeader.setCustomerType("INVOICE");
        outboundHeader.setCreatedOn(new Date());
        outboundHeader = outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader + "---------------> " + new Date());
        return outboundHeader;
    }

    /**
     * @param outBoundLine outboundLine
     * @param preOutboundHeaderV2 PreOutboundHeader
     * @param orderManagementHeaderV2 OrderManagementHeader
     * @param outboundHeader outboundHeader
     * @param preOutboundLines preOutboundLines
     * @param outboundLines outboundLines
     * @param managementLines managementLines
     */
    private void processSingleShipmentLine(SOLineV2 outBoundLine, PreOutboundHeaderV2 preOutboundHeaderV2, OrderManagementHeaderV2 orderManagementHeaderV2,
                                           OutboundHeaderV2 outboundHeader, List<PreOutboundLineV2> preOutboundLines, List<OutboundLineV2> outboundLines,
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

    // PreOutboundLine Creation Process
    private PreOutboundLineV2 createPreOutboundLineV2(PreOutboundHeaderV2 preOutboundHeaderV2, SOLineV2 returnPOLineV2) {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(preOutboundHeaderV2, preOutboundLine, CommonUtils.getNullPropertyNames(preOutboundHeaderV2));
        BeanUtils.copyProperties(returnPOLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(returnPOLineV2));
        preOutboundLine.setLineNumber(returnPOLineV2.getLineReference());
        preOutboundLine.setItemCode(returnPOLineV2.getSku());
        preOutboundLine.setDescription(returnPOLineV2.getSkuDescription());
        preOutboundLine.setOutboundOrderTypeId(0L);
        preOutboundLine.setStatusId(39L);
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setManufacturerName(returnPOLineV2.getManufacturerName());
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeaderV2.getLanguageId());
        preOutboundLine.setStatusDescription(statusDescription);
        preOutboundLine.setReferenceDocumentType("WMS to NON-WMS");
        preOutboundLine.setSupplierInvoiceNo(returnPOLineV2.getTransferOrderNumber());
        preOutboundLine.setReturnOrderNo(returnPOLineV2.getTransferOrderNumber());
        preOutboundLine.setOrderQty(returnPOLineV2.getExpectedQty());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert

        String itemText = imBasicData1Repository.findItemDescription(returnPOLineV2.getSku(), preOutboundHeaderV2.getCompanyCodeId(), preOutboundHeaderV2.getPlantId(), preOutboundHeaderV2.getLanguageId());
        if (itemText != null && !itemText.isEmpty()) {
            preOutboundLine.setDescription(itemText);
        } else {
            preOutboundLine.setDescription(returnPOLineV2.getSkuDescription());
        }

        preOutboundLine.setRequiredDeliveryDate(preOutboundHeaderV2.getRequiredDeliveryDate());
        preOutboundLine.setReferenceField1("WMS to NON-WMS");
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());
        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }

    /**
     *
     * @param orderManagementHeaderV2 Header Values
     * @param preOutboundLine One by One Line Values
     * @return
     */
    private OrderManagementLineV2 createOrderManagementLineV2(OrderManagementHeaderV2 orderManagementHeaderV2, PreOutboundLineV2 preOutboundLine) {
        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
        orderManagementLine.setPickupCreatedOn(new Date());

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
                        preOutboundLine.getItemCode(), 1L, 1L, orderManagementLine.getManufacturerName());
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
            orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
            return orderManagementLine;
        }

        orderManagementLine = updateAllocationV2(orderManagementLine, 1L, preOutboundLine.getOrderQty(),
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
    public OrderManagementLineV2 updateAllocationV2(OrderManagementLineV2 orderManagementLine, Long binClassId,
                                                    Double ORD_QTY, String warehouseId, String itemCode, String loginUserID)  {
        // Inventory Strategy Choices
        String INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        log.info("Allocation Strategy: " + INV_STRATEGY);
        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;

        // -----------------------------------------------------------------------------------------------------------------------------------------
        // Getting Inventory GroupBy ST_BIN wise

        List<IInventoryImpl> finalInventoryList = null;
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("SB_BEST_FIT");
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
            log.info("Group By LeveId: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for(IInventory iInventory : levelIdList){
                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
                if(ORD_QTY <= iInventory.getInventoryQty()){
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
                                orderManagementLineV2Repository.delete(orderManagementLine);
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
                                com.tekclover.wms.api.enterprise.transaction.util.CommonUtils.getNullPropertyNames(orderManagementLine));

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
                        OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                        log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                        allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

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
                            BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, com.tekclover.wms.api.enterprise.transaction.util.CommonUtils.getNullPropertyNames(inventoryForUpdate));
                            inventoryV2.setUpdatedOn(new Date());
                            inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
                            inventoryV2 = inventoryV2Repository.save(inventoryV2);
                            log.info("-----Inventory2 updated-------: " + inventoryV2);
                        }

                        if (ORD_QTY == ALLOC_QTY) {
                            log.info("ORD_QTY fully allocated: " + ORD_QTY);
                            break outerloop1; // If the Inventory satisfied the Ord_qty
                        }
                    }
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if(ORD_QTY > iInventory.getInventoryQty()){
                    toBeIncluded = false;
                }
                if(!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if(invQtyByLevelIdCount != invQtyGroupByLevelIdCount){
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if(invQtyByLevelIdCount == invQtyGroupByLevelIdCount){
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV2(orderManagementLine.getCompanyCodeId(),
                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                        warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
            }
        }
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

        outerloop:
        for (IInventoryImpl stBinInventory : finalInventoryList) {
            // Getting PackBarCode by passing ST_BIN to Inventory
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
                    orderManagementLineV2Repository.delete(orderManagementLine);
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
                    com.tekclover.wms.api.enterprise.transaction.util.CommonUtils.getNullPropertyNames(orderManagementLine));

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
            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
            log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
            allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

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
                inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----Inventory2 updated-------: " + inventoryV2);
            }

            if (ORD_QTY == ALLOC_QTY) {
                log.info("ORD_QTY fully allocated: " + ORD_QTY);
                break outerloop; // If the Inventory satisfied the Ord_qty
            }
        }
        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
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
     * @param orderManagementLine orderManagementLine
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
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     *
     * PickupHeader Creation
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        SOHeaderV2 outboundIntegrationHeader,
                                                        String preOutboundNo, String refDocNumber, String partnerCode) throws Exception {

        try {
            List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                    getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

            log.info("OrderManagementLine Values in WMS to Non WMS ---------> " + orderManagementLineV2List);
            //pickup header create
//            Long lineCount = 0L;
//
//            lineCount = orderManagementLineV2List.stream().count();

            List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
            for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {
//                String assignPickerId = null;

                if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                    log.info("orderManagementLine: " + orderManagementLine);

                    int currentTime = DateUtils.getCurrentTime();
                    log.info("CurrentTime: " + currentTime);

                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(orderManagementLine, newPickupHeader, com.tekclover.wms.api.enterprise.transaction.util.CommonUtils.getNullPropertyNames(orderManagementLine));
                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                    log.info("PU_NO : " + PU_NO);

//                newPickupHeader.setAssignedPickerId(assignPickerId);
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


                    newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerName());
                    newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                    newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                    newPickupHeader.setSalesOrderNumber(null);
                    newPickupHeader.setPickListNumber(outboundIntegrationHeader.getTransferOrderNumber());
//                    newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType("WMS to Non-WMS");
                    newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
//                    newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                    newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                    newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                    newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                    newPickupHeader.setImsSaleTypeCode(orderManagementLine.getImsSaleTypeCode());

                    newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getBranchCode());
                    newPickupHeader.setIsCompleted(orderManagementLine.getIsCompleted());
                    newPickupHeader.setIsCancelled(orderManagementLine.getIsCancelled());
//                    newPickupHeader.setMUpdatedOn(orderManagementLine.getMUpdatedOn());
//                newPickupHeader.setCustomerCode(outboundIntegrationHeader.getCustomerCode());
//                newPickupHeader.setTransferRequestType(outboundIntegrationHeader.getTransferRequestType());

                    log.info("PickupHeader Creation Process Started --->");
                    PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                    log.info("PickupHeader Creation Process Completed ---> Values is {} ", createdPickupHeader);
                    pickupHeaderV2List.add(createdPickupHeader);
                    orderManagementLineV2Repository.updateOrderManagementLineV2(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                            48L, statusDescription, PU_NO, new Date());
                }
            }

            log.info("PickupHeader Batch All Save Process Started----> " + pickupHeaderV2List.size());
            pickupHeaderV2Repository.saveAll(pickupHeaderV2List);
            log.info("PickupHeader Batch All Save Process Completed----> " + pickupHeaderV2List.size());

            //push notification separated from pickup header and consolidated notification sent
            orderManagementLineService.sendPushNotification(preOutboundNo, warehouseId);
        } catch (Exception e) {
            log.error("create PickupHeader error : " + e);
            throw e;
        }
    }

}
