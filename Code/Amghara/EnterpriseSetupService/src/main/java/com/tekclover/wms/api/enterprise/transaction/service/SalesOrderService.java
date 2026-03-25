package com.tekclover.wms.api.enterprise.transaction.service;


import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.enterprise.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.enterprise.transaction.model.dto.IInventory;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.PickListCancellation;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.WarehouseId;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.enterprise.transaction.repository.*;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import com.tekclover.wms.api.enterprise.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
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
public class SalesOrderService extends BaseService {


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

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    OutboundLineService outboundLineService;

    @Autowired
    PreOutboundLineService preOutboundLineService;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    QualityLineService qualityLineService;

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;

    @Autowired
    QualityHeaderService qualityHeaderService;

    /**
     * @param salesOrder sales_order_list
     * @return
     */
    @Async("asyncTaskExecutor")
    public SalesOrderV2 createSalesOrderList(SalesOrderV2 salesOrder) {
        log.info("Outbound Process Start {} PickList", salesOrder);

//        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
            List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
            String companyCode = header.getCompanyCode();
            String plantId = header.getBranchCode();
            String newPickListNo = header.getPickListNumber();
            String orderType = lineV2List.get(0).getOrderType();

            // Get Warehouse
            Optional<WarehouseId> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
            WarehouseId WH = dbWarehouse.get();
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

            Optional<PreOutboundHeaderV2> duplicateCheck = preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                    languageId, companyCode, plantId, warehouseId, newPickListNo, 0L);
            if(duplicateCheck.isPresent()) {
                return  salesOrder;
            }

            //PickList Cancellation
            log.info("Executing PickList cancellation scenario pre - checkup process");
            String salesOrderNumber = header.getSalesOrderNumber();

            //Check WMS order table
            List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndOutboundOrderTypeIdAndDeletionIndicator(salesOrderNumber, 3L, 0L);
            log.info("SalesOrderNumber already Exist: ---> PickList Cancellation to be executed " + salesOrderNumber);

            if (outbound != null && !outbound.isEmpty()) {
                List<OutboundHeaderV2> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).collect(Collectors.toList());
                log.info("Old PickList Number, New PickList Number: " + oldPickListNo + ", " + newPickListNo);

                if (oldPickListNo != null && !oldPickListNo.isEmpty()) {
                    for (OutboundHeaderV2 oldPickListNumber : oldPickListNo) {
                        OutboundHeaderV2 outboundOrderV2 =
                                outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                                        companyCode, languageId, plantId, warehouseId, oldPickListNumber.getPickListNumber(), oldPickListNumber.getPreOutboundNo(), 0L);
                        log.info("Outbound Order status ---> Delivered for old Picklist Number: " + outboundOrderV2 + ", " + oldPickListNumber);

                        if (outboundOrderV2 != null && outboundOrderV2.getInvoiceNumber() != null) {
                            // Update error message for the new PicklistNo
                            throw new BadRequestException("Picklist cannot be cancelled as Sales order associated with picklist - Invoice has been raised");
                        }

                        log.info("Old PickList Number: " + oldPickListNumber.getPickListNumber() + ", " +
                                oldPickListNumber.getPreOutboundNo() + " Cancellation Initiated and followed by New PickList " + newPickListNo + " creation started");

                        //Delete old PickListData
                        createPickListCancellation = pickListCancellationNew(companyCode, plantId, languageId, warehouseId, oldPickListNumber.getPickListNumber(), newPickListNo, oldPickListNumber.getPreOutboundNo(), "MW_AMS");
                    }
                }
            }

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

            // Process lines in parallel
//            List<CompletableFuture<Void>> futures = lineV2List.stream()
//                    .map(outBoundLine -> CompletableFuture.runAsync(() -> {
//                        try {
//                            processSingleShipmentLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
//                                    preOutboundLines, outboundLines);
//                        } catch (Exception e) {
//                            log.error("Error processing Purchase Retrun Line for SalesOrder: {}", header.getPickListNumber(), e);
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

            for(SalesOrderLineV2 outBoundLine : lineV2List) {
                processSingleShipmentLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
                        preOutboundLines, outboundLines);
            }

            // Batch Save All Records
            preOutboundLineV2Repository.saveAll(preOutboundLines);
            outboundLineV2Repository.saveAll(outboundLines);

            // PickupHeader for only PickList Order Only
            log.info("PickupHeader Creation Process Started -----------> RefDocNo is {} ", outboundHeader.getRefDocNumber());
            createPickUpHeaderAssignPickerModified(companyCode, plantId, languageId, warehouseId, header,
                    preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
            log.info("PickupHeader Creation Process Completed -----------> RefDocNo is {} ", outboundHeader.getRefDocNumber());

            if (createPickListCancellation != null) {
                createPickListCancellation(companyCode, plantId, languageId, warehouseId,
                        createPickListCancellation.getOldPickListNumber(), createPickListCancellation.getNewPickListNumber(),
                        preOutboundNo, createPickListCancellation, "MW_AMS");
            }

            statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCode, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
            orderService.updateProcessedOrderV2(outboundHeader.getRefDocNumber(), outboundHeader.getOutboundOrderTypeId(),  10L);
        } catch (
                Exception e) {
            log.error("Error processing outbound PICK_LIST Lines", e);
            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
        }
        log.info("Outbound Process Completed for SaleOrderNumber {} &&& PickListNo {} ", salesOrder.getSalesOrderHeader().getSalesOrderNumber(), salesOrder.getSalesOrderHeader().getPickListNumber());
        return salesOrder;
    }

    /**
     *
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
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     * @param oldPreOutboundNo
     * @param loginUserID
     * @return
     */
    //Delete Old PickList records and insert new picklist data
    private PickListCancellation pickListCancellationNew(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String oldPickListNumber, String newPickListNumber, String oldPreOutboundNo, String loginUserID) {

        try {
            log.info("PickList Cancellation Initiated---> " + companyCodeId + "," + plantId + "," + languageId + "," + warehouseId + "," + oldPickListNumber + "," + oldPreOutboundNo);
            //Delete OutBoundHeader
            OutboundHeaderV2 outboundHeaderV2 = outboundHeaderService.getPLCOutBoundHeader(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete OutBoundLine
            List<OutboundLineV2> outboundLineV2 = outboundLineService.getPLCOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete PreOutboundLine
            List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineService.getPLCPreOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //DeleteOrderManagementLine
            List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getPLCOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete PickUpLine
            List<PickupLineV2> pickupLineV2 = pickupLineService.getPLCPickUpLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Quality Line
            List<QualityLineV2> qualityLineV2 = qualityLineService.getPLCQualityLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            List<String> pickuplineItemCodeMfrNameList = new ArrayList<>();
            List<String> pickuplineLineNoItemCodeMfrNameList = new ArrayList<>();
            if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
                for (PickupLineV2 pickupLine : pickupLineV2) {
                    pickuplineItemCodeMfrNameList.add(pickupLine.getItemCode() + pickupLine.getManufacturerName());
                    InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                            pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                    List<PickupLineV2> filteredList = pickupLineV2.stream().filter(a -> a.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                            a.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                            a.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                    List<PreOutboundLineV2> filteredPreOutboundLineList = preOutboundLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                            n.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                            n.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                    Double PICK_CNF_QTY = 0D;
                    Double ORD_QTY = 0D;
                    if (filteredList != null && !filteredList.isEmpty()) {
                        PICK_CNF_QTY = filteredList.stream().mapToDouble(a -> a.getPickConfirmQty()).sum();
                    }
                    if (filteredPreOutboundLineList != null && !filteredPreOutboundLineList.isEmpty()) {
                        ORD_QTY = filteredPreOutboundLineList.stream().mapToDouble(n -> n.getOrderQty()).sum();
                    }
                    boolean equalQty = PICK_CNF_QTY.equals(ORD_QTY);
                    log.info("PICK_CNF_QTY, ORD_TY, EqualQty Condition: " + PICK_CNF_QTY + ", " + ORD_QTY + ", " + equalQty);
                    if (equalQty) {
                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if (inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 6));
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                    }
                    if (!equalQty) {
                        String itmMfrNameLineNo = pickupLine.getItemCode() + pickupLine.getManufacturerName() + pickupLine.getLineNumber();
                        List<String> filterList = pickuplineLineNoItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrNameLineNo)).collect(Collectors.toList());
                        if (filterList.size() == 0) {
                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + ORD_QTY;
                                if (INV_QTY < 0) {
                                    log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                    INV_QTY = 0D;
                                }
                                inventory.setInventoryQuantity(INV_QTY);
                                Double ALLOC_QTY = 0D;
                                if (inventory.getAllocatedQuantity() != null) {
                                    ALLOC_QTY = inventory.getAllocatedQuantity() - (ORD_QTY - PICK_CNF_QTY);
                                }
                                if (ALLOC_QTY < 0) {
                                    log.info("allocated qty calculated is less than 0: " + ALLOC_QTY);
                                    ALLOC_QTY = 0D;
                                }
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                inventory.setReferenceField4(TOT_QTY);              //Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                                newInventoryV2.setUpdatedOn(new Date());
                                newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 6));
                                InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                                log.info("InventoryV2 created : " + updateInventoryV2);
                            }
                            pickuplineLineNoItemCodeMfrNameList.add(itmMfrNameLineNo);
                        }
                    }
                }
            }

            if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
                log.info("Inventory update for OrderManagementLine");
                log.info("Pickupline ItmCodeMfrName: " + pickuplineItemCodeMfrNameList);
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                    if (dbOrderManagementLine.getStatusId() != 47L) {
                        if (dbOrderManagementLine.getProposedStorageBin() == null || dbOrderManagementLine.getProposedStorageBin().equalsIgnoreCase("")) {
                            throw new BadRequestException("OrderManagementLine ProposedStorageBin is Empty, hence inventory cannot be reversed - PickList Cancellation Aborting");
                        }
                        String itmMfrName = dbOrderManagementLine.getItemCode() + dbOrderManagementLine.getManufacturerName();
                        log.info("ItmMfrName: " + itmMfrName);
                        List<String> itmPresent = pickuplineItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrName)).collect(Collectors.toList());
                        log.info("itmPresent: " + itmPresent);
                        if (itmPresent.size() == 0) {
                            InventoryV2 inventory = inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                    dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getProposedPackBarCode(), dbOrderManagementLine.getItemCode(),
                                    dbOrderManagementLine.getProposedStorageBin(), dbOrderManagementLine.getManufacturerName());

                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                                if (INV_QTY < 0) {
                                    log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                    INV_QTY = 0D;
                                }
                                inventory.setInventoryQuantity(INV_QTY);
                                Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) - (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                                if (ALLOC_QTY < 0) {
                                    ALLOC_QTY = 0D;
                                }
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                inventory.setAllocatedQuantity(ALLOC_QTY);
                                inventory.setReferenceField4(TOT_QTY);              //Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                                newInventoryV2.setUpdatedOn(new Date());
                                newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 6));
                                InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                                log.info("InventoryV2 created : " + updateInventoryV2);
                            }
                        }
                    }
                }
            }

            PickListCancellation pickListCancellation = new PickListCancellation();
            if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
                pickListCancellation.setOldPickupLineList(pickupLineV2);
            }
            if (qualityLineV2 != null && !qualityLineV2.isEmpty()) {
                pickListCancellation.setOldQualityLineList(qualityLineV2);
            }
            if (outboundHeaderV2 != null) {
                pickListCancellation.setOldOutboundHeader(outboundHeaderV2);
            }
            if (outboundLineV2 != null && !outboundLineV2.isEmpty()) {
                pickListCancellation.setOldOutboundLineList(outboundLineV2);
            }
            if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
                pickListCancellation.setOldOrderManagementLineList(orderManagementLine);
            }
            pickListCancellation.setOldPickListNumber(oldPickListNumber);
            pickListCancellation.setNewPickListNumber(newPickListNumber);

            //stored procedure to update deletionIndicator Flag
            pickListHeaderRepository.updateDeletionIndicatorPickListCancellationProc(
                    companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID, new Date());
            log.info("Pick List cancellation - stored procedure update - deletion indicator finished processing");

            return pickListCancellation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    private void createPickListCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String oldPickListNumber, String newPickListNumber, String newPreOutboundNo,
                                            PickListCancellation pickListCancellation,
                                            String loginUserID) throws Exception {
        List<PickupLineV2> pickupLineV2 = pickListCancellation.getOldPickupLineList();
        List<QualityLineV2> qualityLineV2 = pickListCancellation.getOldQualityLineList();
        OutboundHeaderV2 outboundHeaderV2 = pickListCancellation.getOldOutboundHeader();
        //PickUpLine Creation
        List<PickupLineV2> createNewPickUpLineList = null;
        if(pickupLineV2 != null && !pickupLineV2.isEmpty()) {
            List<PickupHeaderV2> newPickupHeaderList = pickupHeaderService.getPickupHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("newPickupHeaderList: " + newPickupHeaderList);

            List<AddPickupLine> newPickupLineList = new ArrayList<>();
            if(newPickupHeaderList != null && !newPickupHeaderList.isEmpty()){
                for(PickupHeaderV2 pickupHeaderV2 : newPickupHeaderList) {
                    List<PickupLineV2> pickupLinePresent =
                            pickupLineV2
                                    .stream()
                                    .filter(n->n.getItemCode().equalsIgnoreCase(pickupHeaderV2.getItemCode()) && n.getManufacturerName().equalsIgnoreCase(pickupHeaderV2.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Pickup Line present for that itemCode & MFR_Name: " + pickupLinePresent);

                    if(pickupLinePresent != null && !pickupLinePresent.isEmpty()){
                        AddPickupLine newPickupLine = new AddPickupLine();
                        BeanUtils.copyProperties(pickupHeaderV2, newPickupLine, CommonUtils.getNullPropertyNames(pickupHeaderV2));
                        Double oldPickConfirmQty = pickupLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = pickupHeaderV2.getPickToQty();
                        if(oldPickConfirmQty < newPickConfirmQty){
//                        Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
                            newPickupLine.setPickConfirmQty(oldPickConfirmQty);
                            newPickupLine.setAllocatedQty(oldPickConfirmQty);
                            newPickupLine.setPickQty(String.valueOf(oldPickConfirmQty));
                        }
                        if(oldPickConfirmQty >= newPickConfirmQty){
                            newPickupLine.setPickConfirmQty(newPickConfirmQty);
                            newPickupLine.setAllocatedQty(newPickConfirmQty);
                            newPickupLine.setPickQty(String.valueOf(newPickConfirmQty));
                        }
                        newPickupLine.setCompanyCodeId(Long.valueOf(pickupHeaderV2.getCompanyCodeId()));
                        newPickupLine.setPickedStorageBin(pickupHeaderV2.getProposedStorageBin());
                        newPickupLine.setPickupNumber(pickupHeaderV2.getPickupNumber());
                        newPickupLine.setActualHeNo(pickupLinePresent.get(0).getActualHeNo());
                        newPickupLine.setPickedPackCode(pickupHeaderV2.getProposedPackBarCode());
                        newPickupLineList.add(newPickupLine);
                    }
                }
            }
            if(newPickupLineList != null && !newPickupLineList.isEmpty()){
//                createNewPickUpLineList = pickupLineService.createPickupLineV2(newPickupLineList, loginUserID);
                createNewPickUpLineList = pickupLineService.createPickupLineNonCBMPickListCancellationV2(newPickupLineList, loginUserID);
                log.info("CreatedPickUpLine List : " + createNewPickUpLineList);
            }
        }

        //Quality Line Creation
        if(qualityLineV2 != null && !qualityLineV2.isEmpty()) {
            List<QualityHeaderV2> newQualityHeaderList = qualityHeaderService.getQualityHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("NewQualityHeaderList: " + newQualityHeaderList);

            List<AddQualityLineV2> newQualityLineList = new ArrayList<>();
            if(newQualityHeaderList != null && !newQualityHeaderList.isEmpty()){
                for(QualityHeaderV2 qualityHeader : newQualityHeaderList) {
                    List<QualityLineV2> qualityLinePresent =
                            qualityLineV2
                                    .stream()
                                    .filter(n->n.getItemCode().equalsIgnoreCase(qualityHeader.getReferenceField4()) && n.getManufacturerName().equalsIgnoreCase(qualityHeader.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Quality Line Present for that itemCode and MFR_Name: " + qualityLinePresent);

                    if(qualityLinePresent != null && !qualityLinePresent.isEmpty()){
                        AddQualityLineV2 newQualityLine = new AddQualityLineV2();
                        BeanUtils.copyProperties(qualityHeader, newQualityLine, CommonUtils.getNullPropertyNames(qualityHeader));

                        Double oldPickConfirmQty = qualityLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = Double.valueOf(qualityHeader.getQcToQty());
                        if(oldPickConfirmQty < newPickConfirmQty){
//                            Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
                            newQualityLine.setPickConfirmQty(oldPickConfirmQty);
                            newQualityLine.setQualityQty(Double.valueOf(oldPickConfirmQty));
                        }
                        if(oldPickConfirmQty >= newPickConfirmQty){
                            newQualityLine.setPickConfirmQty(Double.valueOf(newPickConfirmQty));
                            newQualityLine.setQualityQty(Double.valueOf(newPickConfirmQty));
                        }

                        newQualityLine.setDescription(qualityHeader.getReferenceField3());
                        newQualityLine.setItemCode(qualityHeader.getReferenceField4());
                        newQualityLine.setManufacturerName(qualityHeader.getManufacturerName());
                        newQualityLine.setPickPackBarCode(qualityHeader.getReferenceField2());
                        newQualityLine.setQualityInspectionNo(qualityHeader.getQualityInspectionNo());
                        if(qualityHeader.getReferenceField5() != null) {
                            newQualityLine.setLineNumber(Long.valueOf(qualityHeader.getReferenceField5()));
                        }
                        newQualityLineList.add(newQualityLine);
                    }
                }
            }
            if(newQualityLineList != null && !newQualityLineList.isEmpty()) {
                List<QualityLineV2> createNewQualityLineList = qualityLineService.createQualityLineV2(newQualityLineList, loginUserID);
                log.info("Created Quality Line List : " + createNewQualityLineList);
            }
        }
        log.info("Pick List Cancellation Completed");

        try {
            String preOutboundNo = outboundHeaderV2.getPreOutboundNo() != null ? outboundHeaderV2.getPreOutboundNo() : null;
            String salesOrderNo = outboundHeaderV2.getSalesOrderNumber() != null ? outboundHeaderV2.getSalesOrderNumber() : null;
            log.info("Stored procedure call to update cnf_by in pickup line, qc header and line : " + oldPickListNumber + ", " + preOutboundNo + ", " + newPickListNumber + ", " + newPreOutboundNo + ", " + salesOrderNo);
            pickListHeaderRepository.updatePickupLineQualityHeaderLineCnfByUpdateProc(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, outboundHeaderV2.getPreOutboundNo(), newPickListNumber, newPreOutboundNo, outboundHeaderV2.getSalesOrderNumber());
            log.info("SP update done");
        } catch (Exception e) {
            log.info("PickupLine & QualityLine Update Exception Throwing ----> " + e.getMessage());
        }
    }


    /**
     *
     * @param companyCodeId company
     * @param plantId plant
     * @param languageId language
     * @param warehouseId warehouse
     * @param salesOrderHeaderV2 header
     * @param preOutboundNo preOutBondNo
     * @param refDocNumber refDocNo
     * @param partnerCode partnerCode
     * @throws Exception exception
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        SalesOrderHeaderV2 salesOrderHeaderV2,
                                                        String preOutboundNo, String refDocNumber, String partnerCode) throws Exception {

        try {
            List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                    getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

            log.info("OrderManagementLine Values in WMS to Non WMS ---------> " + orderManagementLineV2List);

            List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
            for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {

                if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                    log.info("orderManagementLine: " + orderManagementLine);

                    int currentTime = DateUtils.getCurrentTime();
                    log.info("CurrentTime: " + currentTime);

                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(orderManagementLine, newPickupHeader, com.tekclover.wms.api.enterprise.transaction.util.CommonUtils.getNullPropertyNames(orderManagementLine));
                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                    log.info("PU_NO : " + PU_NO);

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
                    newPickupHeader.setSalesOrderNumber(orderManagementLine.getSalesOrderNumber());

                    newPickupHeader.setPickListNumber(salesOrderHeaderV2.getPickListNumber());
                    newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType("PICK LIST");
                    newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
//                    newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                    newPickupHeader.setLevelId(orderManagementLine.getLevelId());
//                    newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                    newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                    newPickupHeader.setImsSaleTypeCode(orderManagementLine.getImsSaleTypeCode());

                    newPickupHeader.setFromBranchCode(salesOrderHeaderV2.getBranchCode());
                    newPickupHeader.setIsCompleted(orderManagementLine.getIsCompleted());
                    newPickupHeader.setIsCancelled(orderManagementLine.getIsCancelled());
//                    newPickupHeader.setMUpdatedOn(orderManagementLine.getMUpdatedOn());
                    newPickupHeader.setCustomerCode(salesOrderHeaderV2.getCustomerCode());
//                newPickupHeader.setTransferRequestType(outboundIntegrationHeader.getTransferRequestType());

                    log.info("PickupHeader Creation Process Started --->");
                    PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                    log.info("PickupHeader Creation Process Completed ---> Values is {} ", createdPickupHeader);
                    pickupHeaderV2List.add(createdPickupHeader);

                    log.info("Update OrderManagementLine Process PickupNo: {} ", PU_NO);
                    updateOrderManagementLine(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            orderManagementLine, statusDescription, PU_NO);

//                    orderManagementLineV2Repository.updateOrderManagementLineV2(
//                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
//                            orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
//                            48L, statusDescription, PU_NO, new Date());
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

    /**
     *
     * @param companyCode c_id
     * @param plantId plant_id
     * @param languageId lang_id
     * @param warehouseId wh_id
     * @param preOutboundNo pre_ob_no
     * @param salesOrderHeaderV2
     * @param orderType
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                          String preOutboundNo, SalesOrderHeaderV2 salesOrderHeaderV2, String orderType,
                                                          String companyText, String plantText, String warehouseText) {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(salesOrderHeaderV2, preOutboundHeader, com.tekclover.wms.api.enterprise.util.CommonUtils.getNullPropertyNames(salesOrderHeaderV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCode);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(salesOrderHeaderV2.getBranchCode());
        preOutboundHeader.setOutboundOrderTypeId(3L);
        preOutboundHeader.setReferenceDocumentType("PICK LIST");
        preOutboundHeader.setCustomerCode("INVOICE");
        preOutboundHeader.setTransferRequestType("PICK LIST");
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
        preOutboundHeader.setMiddlewareId(salesOrderHeaderV2.getMiddlewareId());
        preOutboundHeader.setMiddlewareTable(salesOrderHeaderV2.getMiddlewareTable());
        preOutboundHeader.setReferenceDocumentType("PICK LIST");
        preOutboundHeader.setSalesOrderNumber(salesOrderHeaderV2.getSalesOrderNumber());
        preOutboundHeader.setPickListNumber(salesOrderHeaderV2.getPickListNumber());
//        preOutboundHeader.setTargetBranchCode(salesOrderHeaderV2.getBranchCode());

        preOutboundHeader.setFromBranchCode(salesOrderHeaderV2.getBranchCode());
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
     *
     * @param createdPreOutboundHeader preOutbound
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
//        newOrderManagementHeader.setTargetBranchCode(createdPreOutboundHeader.getTargetBranchCode());
        newOrderManagementHeader.setFromBranchCode(createdPreOutboundHeader.getFromBranchCode());
        newOrderManagementHeader.setIsCompleted(createdPreOutboundHeader.getIsCompleted());
        newOrderManagementHeader.setIsCancelled(createdPreOutboundHeader.getIsCancelled());
        newOrderManagementHeader.setMUpdatedOn(createdPreOutboundHeader.getMUpdatedOn());

        newOrderManagementHeader.setReferenceField7(statusDescription);
        return orderManagementHeaderV2Repository.save(newOrderManagementHeader);
    }

    /**
     *
     * @param createdPreOutboundHeader preoutboundHeader
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
     * @param outLineV2 outboundLine
     * @param preOutboundHeaderV2 PreOutboundHeader
     * @param orderManagementHeaderV2 OrderManagementHeader
     * @param outboundHeader outboundHeader
     * @param preOutboundLines preOutboundLines
     * @param outboundLines outboundLines
     */
    private void processSingleShipmentLine(SalesOrderLineV2 outLineV2, PreOutboundHeaderV2 preOutboundHeaderV2, OrderManagementHeaderV2 orderManagementHeaderV2,
                                           OutboundHeaderV2 outboundHeader, List<PreOutboundLineV2> preOutboundLines, List<OutboundLineV2> outboundLines) {
        // Collect PreOutboundLine
        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(preOutboundHeaderV2, outLineV2);
        preOutboundLines.add(preOutboundLine);
        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);

        // Collect OrderManagementLine
        OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(orderManagementHeaderV2, preOutboundLine);
        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);

        // Collect InboundLine
        OutboundLineV2 outboundLine = createOutboundLineV2(orderManagementLine, outboundHeader);
        outboundLines.add(outboundLine);
        log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLine);
    }

    /**
     *
     * @param preOutboundHeaderV2 preOutboundHeader
     * @return
     */
    private PreOutboundLineV2 createPreOutboundLineV2(PreOutboundHeaderV2 preOutboundHeaderV2, SalesOrderLineV2 salesOrderLineV2) {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(preOutboundHeaderV2, preOutboundLine, CommonUtils.getNullPropertyNames(preOutboundHeaderV2));
        BeanUtils.copyProperties(salesOrderLineV2, preOutboundLine, com.tekclover.wms.api.enterprise.util.CommonUtils.getNullPropertyNames(salesOrderLineV2));
        preOutboundLine.setLineNumber(salesOrderLineV2.getLineReference());
        preOutboundLine.setItemCode(salesOrderLineV2.getSku());
        preOutboundLine.setDescription(salesOrderLineV2.getSkuDescription());
        preOutboundLine.setOutboundOrderTypeId(3L);
        preOutboundLine.setStatusId(39L);
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setManufacturerName(salesOrderLineV2.getManufacturerName());
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, preOutboundHeaderV2.getLanguageId());
        preOutboundLine.setStatusDescription(statusDescription);
        preOutboundLine.setReferenceDocumentType("PICK LIST");
        preOutboundLine.setOrderQty(salesOrderLineV2.getOrderedQty());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert

        String itemText = imBasicData1Repository.findItemDescription(salesOrderLineV2.getSku(), preOutboundHeaderV2.getCompanyCodeId(), preOutboundHeaderV2.getPlantId(), preOutboundHeaderV2.getLanguageId());
        if (itemText != null && !itemText.isEmpty()) {
            preOutboundLine.setDescription(itemText);
        } else {
            preOutboundLine.setDescription(salesOrderLineV2.getSkuDescription());
        }

        preOutboundLine.setOrderUom(salesOrderLineV2.getUom());
        preOutboundLine.setRequiredDeliveryDate(preOutboundHeaderV2.getRequiredDeliveryDate());
        preOutboundLine.setReferenceField1("PICK LIST");
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
     * @param orderManagementLine line
     * @param binClassId bin_cl_id
     * @param ORD_QTY ord_qty
     * @param warehouseId wh_id
     * @param itemCode itm_code
     * @param loginUserID user_id
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
                        BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

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
            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.saveAndFlush(newOrderManagementLine);
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
                BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, com.tekclover.wms.api.enterprise.util.CommonUtils.getNullPropertyNames(inventoryForUpdate));
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
     * @param orderManagementLine orderManagementLine
     * @return return
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
     * @param orderManagementLineV2 order_line
     * @param outboundHeaderV2 header
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

    // OrderManagementLine Update Process
    public void updateOrderManagementLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String preOutboundNo, String refDocNumber, String partnerCode, OrderManagementLineV2 orderManagementLine,
                                          String statusDescription, String PU_NO) {
        int maxRetry = 3;
        int attempt = 0;
        boolean success = false;
        while(!success && attempt < maxRetry) {
            try {
                attempt ++;
                orderManagementLineV2Repository.updateOrderManagementLineV2(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());

                success = true; // success
            } catch (CannotAcquireLockException ex) {
                log.warn("Retry {} failed for preOutboundNo: {} line: {}", attempt, preOutboundNo, orderManagementLine.getLineNumber());

                try {
                    Thread.sleep(1000); // wait 1 sec
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!success) {
            log.info("Failed after retries for preOutboundNo: " + preOutboundNo);
        }
    }

}
