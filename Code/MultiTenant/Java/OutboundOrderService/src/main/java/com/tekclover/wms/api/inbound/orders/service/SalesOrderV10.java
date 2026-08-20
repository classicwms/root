package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.service.namratha.OrderProcessingService;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesOrderV10 extends BaseService {

    private final RepositoryProvider repo;

    @Autowired
    OrderProcessingService orderProcessingService;
    @Autowired
    SalesOrderServiceV6 salesOrderServiceV6;
    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    protected String MW_AMS = "SPAREX";

    @Autowired
    OrderService orderService;


    //=========SPAREX================
    public OutboundHeaderV2 processOutboundReceivedV10(OutboundIntegrationHeaderV2 outbound) throws Exception {


        String warehouseId = outbound.getWarehouseID();
        String companyCodeId = outbound.getCompanyCode();
        String plantId = outbound.getBranchCode();
        String languageId = outbound.getLanguageId() != null ? outbound.getLanguageId() : LANG_ID;
        String refDocNumber = outbound.getRefDocumentNo();

        Optional<PreOutboundHeaderV2> orderProcessedStatus =
                preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                        refDocNumber, outbound.getOutboundOrderTypeID(), 0L);


        if (orderProcessedStatus.isPresent()) {
            log.info("PickListCancellation V10 Starting -----------> companyId {}, PlantId {}, WarehouseId {}, RefDocNo {} ", companyCodeId, plantId, warehouseId, refDocNumber);
            List<Long> statusIdList = Arrays.asList(57L, 50L);
            boolean pickUpConfirm = pickupHeaderV2Repository.existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
                    companyCodeId, plantId, warehouseId, refDocNumber, statusIdList, 0L);
            log.info("PickupHeader V10 Status Checking " + pickUpConfirm);
            if (pickUpConfirm) {
                throw new BadRequestException("This Order Already PickList Confirm V10 --------> RefDocNo is " + refDocNumber);
            }
            // PickListCancellation Delete all tables
            orderProcessingService.pickListCancellationV10(companyCodeId, plantId, warehouseId, refDocNumber);
        }

        // Getting PreOutboundNo from NumberRangeTable
        String preOutboundNo = orderProcessingService.getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
        String refField1ForOrderType = null;

        PreOutboundHeaderV2 createdPreOutboundHeader = orderProcessingService.createPreOutboundHeaderV10(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, outbound, refField1ForOrderType, MW_AMS);
        log.info("preOutboundHeader Created V10 : " + createdPreOutboundHeader);

        /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
        OrderManagementHeaderV2 createdOrderManagementHeader = orderProcessingService.createOrderManagementHeaderV10(createdPreOutboundHeader, MW_AMS);
        log.info("OrderMangementHeader Created V10 : " + createdOrderManagementHeader);

        /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
        OutboundHeaderV2 outboundHeader = orderProcessingService.createOutboundHeaderV10(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(),
                outbound, MW_AMS);

        List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
        for (OutboundIntegrationLineV2 outboundIntegrationLine : outbound.getOutboundIntegrationLines()) {
            // PreOutboundLine
            try {
                PreOutboundLineV2 preOutboundLine = orderProcessingService.createPreOutboundLineV10(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, outbound, outboundIntegrationLine, MW_AMS);
                PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
                log.info("preOutboundLine created V10 ---1---> : " + createdPreOutboundLine);
                createdPreOutboundLineList.add(createdPreOutboundLine);

            } catch (Exception e) {
                log.error("Error on processing PreOutboundLine V10 : " + e.toString());
                e.printStackTrace();
            }
        }

        List<OrderManagementLineV2> orderLine = createOrderManagementLineV10(createdPreOutboundLineList, MW_AMS);

        // OutboundLines Created
        createOutboundLine(orderLine);

        if (!orderLine.isEmpty()) {
            List<OrderManagementLineV2> NoStockLines = orderLine.stream()
                    .filter(line -> Objects.equals(line.getStatusId(), 47L))
                    .collect(Collectors.toList());

            if (!NoStockLines.isEmpty()) {
                log.info("Stated updating un allocated order");
                updateUnAllocatedOrders(companyCodeId,plantId,languageId,warehouseId,refDocNumber,preOutboundNo,NoStockLines);
                log.info("Un allocated order updated");
            }
            List<OrderManagementLineV2> nonNoStockLines = orderLine.stream()
                    .filter(line -> !Objects.equals(line.getStatusId(), 47L))
                    .collect(Collectors.toList());

            if (!nonNoStockLines.isEmpty()) {
                log.info("PickupHeader Creation Started V10 ----------> OrderManagementLine Size is {} ", nonNoStockLines.size());
                createPickupHeaderNoV10(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, nonNoStockLines, outbound.getTokenNumber());
            }
        }

        return outboundHeader;
    }

    //========SPAREX==================
    public void createPickupHeaderNoV10(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                        String refDocNumber, List<OrderManagementLineV2> orderManagementLines, String deliveryTo) throws Exception {

        long NUM_RAN_CODE = 10;
        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
        log.info("----------New PU_NO--------> : " + PU_NO);

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("SPAREX");
        if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {
                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                newPickupHeader.setPickupNumber(PU_NO);
                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());

                // Setting delivery Details
                newPickupHeader.setOrigin(deliveryTo);

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());

                newPickupHeader.setRefDocNumber(refDocNumber);
                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
                newPickupHeader.setCustomerName(orderManagementLine.getCustomerName());
                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
                PickupHeaderV2 createdPickupHeader = orderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                repo.orderManagementLineV2Repository.updateOrderManagementLineV10(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());
            }

            repo.outboundHeaderV2Repository.updateOutboundHeaderStatusV10(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
            repo.orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV10(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        }
    }


    public void createOutboundLine(List<OrderManagementLineV2> orderManagementLinList) {

        for (OrderManagementLineV2 orderManagementLine : orderManagementLinList) {
            OutboundLineV2 newOutboundLine = new OutboundLineV2();
            BeanUtils.copyProperties(orderManagementLine, newOutboundLine, CommonUtils.getNullPropertyNames(orderManagementLine));
            newOutboundLine.setLineNumber(orderManagementLine.getLineNumber());
            repo.outboundLineV2Repository.delete(newOutboundLine);
            repo.outboundLineV2Repository.save(newOutboundLine);
        }
    }

    public List<OrderManagementLineV2> createOrderManagementLineV10(List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {

        log.info("Total PreOutboundLines received : {}", preOutboundLineList.size());

        List<OrderManagementLineV2> orderList = new ArrayList<>();

        Map<String, Double> itemCodeToTotalQty = preOutboundLineList.stream()
                .collect(Collectors.groupingBy(PreOutboundLineV2::getItemCode,
                        Collectors.summingDouble(line -> line.getOrderQty() == null ? 0.0 : line.getOrderQty())));

        log.info("Grouped item codes found: {}", itemCodeToTotalQty.keySet());

        for (Map.Entry<String, Double> entry : itemCodeToTotalQty.entrySet()) {
            String itemCode = entry.getKey();
            Double totalQty = entry.getValue();

            try {
                PreOutboundLineV2 preOutboundLineV2 = preOutboundLineList.stream()
                        .filter(p -> p.getItemCode().equals(itemCode))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("ItemCode not found in list: " + itemCode));

                PreOutboundLineV2 newPreOutboundLine = new PreOutboundLineV2();
                BeanUtils.copyProperties(preOutboundLineV2, newPreOutboundLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                newPreOutboundLine.setItemCode(itemCode);
                newPreOutboundLine.setOrderQty(totalQty);

                log.info("Creating OrderManagementLine for ItemCode: {} | TotalQty: {}", itemCode, totalQty);

                List<OrderManagementLineV2> orderManagementLine = createOderManagementLineV10(newPreOutboundLine, loginUserId);
                if (!orderManagementLine.isEmpty()) {
                    orderList.addAll(orderManagementLine);
                }

                log.debug("Created OrderManagementLine for ItemCode: {}", itemCode);
            } catch (Exception ex) {
                log.error("Failed to create OrderManagementLine for ItemCode: {} | Error: {}", itemCode, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }

        log.info("All OrderManagementLines created successfully  {}", orderList);
        return orderList;
    }

    public List<OrderManagementLineV2> createOderManagementLineV10(PreOutboundLineV2 preOutboundLineV2, String loginUserID) {

        log.info("Started Order Allocation V10 for ItemCode: {}, Warehouse: {}, OrderQty: {}",
                preOutboundLineV2.getItemCode(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getOrderQty());

        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("SPAREX");
        String companyCodeId = preOutboundLineV2.getCompanyCodeId();
        String plantId = preOutboundLineV2.getPlantId();
        String warehouseId = preOutboundLineV2.getWarehouseId();
        String languageId = preOutboundLineV2.getLanguageId();
        String itemCode = preOutboundLineV2.getItemCode();
        String itemDescription = preOutboundLineV2.getDescription();
        double balanceOrderQty = preOutboundLineV2.getOrderQty();

        try {

            List<InventoryV2> inventoryV2List;
            inventoryV2List = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L);
            log.info("Inventory List {} in Order Allocation", inventoryV2List.size());

            if (inventoryV2List.isEmpty()) {
                inventoryV2List = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 10L);
                log.info("Inventory List {} in Order Allocation", inventoryV2List.size());
            }

            Long STATUS_ID = null;

            if(!inventoryV2List.isEmpty()) {
                
            Long binClassId = inventoryV2List.get(0).getBinClassId();

                for (InventoryV2 inventory : inventoryV2List) {

                    Double allocatableQty = 0D;
                    Double INV_QTY = inventory.getInventoryQuantity();

                    if (balanceOrderQty <= inventory.getInventoryQuantity()) {
                        allocatableQty = balanceOrderQty;
                    } else if (balanceOrderQty > INV_QTY) {
                        allocatableQty = INV_QTY;
                    } else if (INV_QTY == 0) {
                        allocatableQty = 0D;
                    }

                    if (allocatableQty > 0) {
                        OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                        BeanUtils.copyProperties(preOutboundLineV2, orderLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                        orderLine.setItemCode(companyCodeId);
                        orderLine.setItemCode(plantId);
                        orderLine.setItemCode(warehouseId);
                        orderLine.setItemCode(languageId);

                        orderLine.setBarcodeId(inventory.getBarcodeId());
                        orderLine.setInventoryQty(inventory.getInventoryQuantity());
                        orderLine.setAllocatedQty(allocatableQty);
                        orderLine.setProposedStorageBin(inventory.getStorageBin());
                        orderLine.setItemCode(itemCode);
                        orderLine.setDescription(itemDescription);
                        orderLine.setOrderQty(preOutboundLineV2.getOrderQty());

                        STATUS_ID = (allocatableQty.equals(balanceOrderQty)) ? 43L : 42L;
                        String statusDescription = getStatusDescription(STATUS_ID, languageId);
                        orderLine.setStatusId(STATUS_ID);
                        orderLine.setStatusDescription(statusDescription);
                        orderLine.setReferenceField7(statusDescription);
                        Long lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
                                warehouseId, preOutboundLineV2.getRefDocNumber());
                        orderLine.setLineNumber(lineNo);
                        orderLine.setPickupUpdatedBy(loginUserID);
                        orderLine.setPickupUpdatedOn(new Date());
                        orderLine.setProposedPackBarCode("99999");
                        orderLine.setManufacturerCode(inventory.getManufacturerCode());
                        orderLine.setManufacturerName(inventory.getManufacturerName());
                        orderLine.setManufacturerFullName(inventory.getManufacturerName());
                        orderLine.setReferenceField5(preOutboundLineV2.getReferenceField5());
                        orderLine.setCustomerName(preOutboundLineV2.getCustomerName());
                        orderLine.setOrigin(preOutboundLineV2.getOrigin());

                        repo.orderManagementLineV2Repository.save(orderLine);
                        orderManagementLineList.add(orderLine);
                    }

                    balanceOrderQty -= allocatableQty;
                    log.info("Allocated {}, Remaining: {}", allocatableQty, balanceOrderQty);
//                lineNo++;
                    if (balanceOrderQty <= 0) break;
                }

                if (balanceOrderQty > 0 && binClassId.equals(1L)) {

                    List<InventoryV2> inventoryV2ForBinCl10 = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 10L);
                    log.info("Inventory List {} in Order Allocation", inventoryV2ForBinCl10.size());

                    if (!inventoryV2ForBinCl10.isEmpty()) {
                        for (InventoryV2 inventory : inventoryV2ForBinCl10) {

                            Double allocatableQty = 0D;
                            Double INV_QTY = inventory.getInventoryQuantity();

                            if (balanceOrderQty <= inventory.getInventoryQuantity()) {
                                allocatableQty = balanceOrderQty;
                            } else if (balanceOrderQty > INV_QTY) {
                                allocatableQty = INV_QTY;
                            } else if (INV_QTY == 0) {
                                allocatableQty = 0D;
                            }

                            if (allocatableQty > 0) {
                                OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                                BeanUtils.copyProperties(preOutboundLineV2, orderLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                                orderLine.setItemCode(companyCodeId);
                                orderLine.setItemCode(plantId);
                                orderLine.setItemCode(warehouseId);
                                orderLine.setItemCode(languageId);

                                orderLine.setBarcodeId(inventory.getBarcodeId());
                                orderLine.setInventoryQty(inventory.getInventoryQuantity());
                                orderLine.setAllocatedQty(allocatableQty);
                                orderLine.setProposedStorageBin(inventory.getStorageBin());
                                orderLine.setItemCode(itemCode);
                                orderLine.setDescription(itemDescription);
                                orderLine.setOrderQty(preOutboundLineV2.getOrderQty());

                                STATUS_ID = (allocatableQty.equals(balanceOrderQty)) ? 43L : 42L;
                                String statusDescription = getStatusDescription(STATUS_ID, languageId);
                                orderLine.setStatusId(STATUS_ID);
                                orderLine.setStatusDescription(statusDescription);
                                orderLine.setReferenceField7(statusDescription);
                                Long lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
                                        warehouseId, preOutboundLineV2.getRefDocNumber());
                                orderLine.setLineNumber(lineNo);
                                orderLine.setPickupUpdatedBy(loginUserID);
                                orderLine.setPickupUpdatedOn(new Date());
                                orderLine.setProposedPackBarCode("99999");
                                orderLine.setManufacturerCode(inventory.getManufacturerCode());
                                orderLine.setManufacturerName(inventory.getManufacturerName());
                                orderLine.setManufacturerFullName(inventory.getManufacturerName());
                                orderLine.setReferenceField5(preOutboundLineV2.getReferenceField5());
                                orderLine.setCustomerName(preOutboundLineV2.getCustomerName());

                                repo.orderManagementLineV2Repository.save(orderLine);
                                orderManagementLineList.add(orderLine);
                            }

                            balanceOrderQty -= allocatableQty;
                            log.info("Allocated {}, Remaining: {}", allocatableQty, balanceOrderQty);
                            if (balanceOrderQty <= 0) break;
                        }
                    }
                }
            }
            if (balanceOrderQty > 0 || inventoryV2List.isEmpty()) {

                OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
                BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                Long  lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
                        warehouseId, preOutboundLineV2.getRefDocNumber());
                orderManagementLine.setLineNumber(lineNo);
                orderManagementLine.setOrderQty(preOutboundLineV2.getOrderQty());
                orderManagementLine.setBarcodeId(preOutboundLineV2.getBarcodeId());
                orderManagementLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
                orderManagementLine.setStatusDescription(statusDescription);
                orderManagementLine.setReferenceField7(statusDescription);
                orderManagementLine.setProposedStorageBin("");
                orderManagementLine.setProposedPackBarCode("");
                orderManagementLine.setInventoryQty(0D);
                orderManagementLine.setAllocatedQty(0D);
                repo.orderManagementLineV2Repository.save(orderManagementLine);
                orderManagementLineList.add(orderManagementLine);
                log.info("orderManagementLine created for UnAllocated Order V10" + orderManagementLine);
            }

            return orderManagementLineList;

        } catch (Exception ex) {
            log.error("Unexpected error during order allocation V10 : {}", ex.getMessage(), ex);
            throw new RuntimeException("Order allocation process failed V10", ex);
        }
    }

    //=========SPAREX======================
    public void updateUnAllocatedOrders(String companyId, String plantId, String languageId, String warehouseId,
                                        String refDocNo, String preOutboundNo, List<OrderManagementLineV2> lines){

        Long statusId = 47L;
        statusDescription = getStatusDescription(statusId, languageId);

        Long orderCount = repo.orderManagementLineV2Repository.getLineCountV10(companyId,plantId,languageId,warehouseId,refDocNo,preOutboundNo);

        if (orderCount.equals(1L)){
            repo.preOutboundHeaderV2Repository.updatePreOutboundHeaderV10(companyId,plantId,languageId,warehouseId,refDocNo,
                    preOutboundNo,statusId,statusDescription);
            repo.preOutboundLineV2Repository.updatePreOutboundLineV10(companyId,plantId,languageId,warehouseId,refDocNo,
                    preOutboundNo,statusId,statusDescription);
            repo.outboundHeaderV2Repository.updateOutboundHeaderV10(companyId,plantId,languageId,warehouseId,refDocNo,
                    preOutboundNo,statusId,statusDescription);

        }else {
            Map<String, List<OrderManagementLineV2>> groupByItemCode =
                    lines.stream().collect(Collectors.groupingBy(OrderManagementLineV2::getItemCode));

            for (Map.Entry<String, List<OrderManagementLineV2>> orderLine : groupByItemCode.entrySet()) {
                String itemCode = orderLine.getKey();

                Long orderCountItemWise = repo.orderManagementLineV2Repository.getLineCountItemWiseV10(companyId,plantId,
                        languageId,warehouseId,refDocNo,preOutboundNo,itemCode);

                if (orderCountItemWise.equals(1L)){
                    repo.preOutboundLineV2Repository.updatePreOutboundLineItemWiseV10(companyId,plantId,languageId,warehouseId,refDocNo,
                            preOutboundNo,statusId,statusDescription,itemCode);
                }
            }
        }
    }

    /**
     *  Manual Allocation of unallocated ordermanagementLines
     *
     * @param orderManagementLineV2List
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> createOderManagementLineManualV10(List<OrderManagementLineV2> orderManagementLineV2List, String loginUserID) {

        try {
            List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();


            String companyCodeId = orderManagementLineV2List.get(0).getCompanyCodeId();
            String plantId = orderManagementLineV2List.get(0).getPlantId();
            String warehouseId = orderManagementLineV2List.get(0).getWarehouseId();
            String languageId = orderManagementLineV2List.get(0).getLanguageId();
            String itemCode = orderManagementLineV2List.get(0).getItemCode();
            String refDocNumber = orderManagementLineV2List.get(0).getRefDocNumber();
            String preOutboundNo = orderManagementLineV2List.get(0).getPreOutboundNo();
            String deliveryTo = orderManagementLineV2List.get(0).getOrigin();

            for (OrderManagementLineV2 orderManagementLineV2 : orderManagementLineV2List) {

                log.info("Deleting the Unallocated OrderManagementLine");
                repo.orderManagementLineV2Repository.delete(orderManagementLineV2);

                log.info("Started Order Allocation V10 for ItemCode: {}, Warehouse: {}, OrderQty: {}",
                        orderManagementLineV2.getItemCode(), orderManagementLineV2.getWarehouseId(), orderManagementLineV2.getOrderQty());

                String itemDescription = orderManagementLineV2.getDescription();
                double balanceOrderQty = orderManagementLineV2.getOrderQty();

                List<InventoryV2> inventoryV2List;
                inventoryV2List = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L);
                log.info("Inventory List {} in Order Allocation", inventoryV2List.size());

                if (inventoryV2List.isEmpty()) {
                    inventoryV2List = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 10L);
                    log.info("Inventory List {} in Order Allocation", inventoryV2List.size());
                }

                Long STATUS_ID = null;

                if(!inventoryV2List.isEmpty()) {

                    Long binClassId = inventoryV2List.get(0).getBinClassId();

                    for (InventoryV2 inventory : inventoryV2List) {

                        Double allocatableQty = 0D;
                        Double INV_QTY = inventory.getInventoryQuantity();

                        if (balanceOrderQty <= inventory.getInventoryQuantity()) {
                            allocatableQty = balanceOrderQty;
                        } else if (balanceOrderQty > INV_QTY) {
                            allocatableQty = INV_QTY;
                        } else if (INV_QTY == 0) {
                            allocatableQty = 0D;
                        }

                        if (allocatableQty > 0) {
                            OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                            BeanUtils.copyProperties(orderManagementLineV2, orderLine, CommonUtils.getNullPropertyNames(orderManagementLineV2));
                            orderLine.setItemCode(companyCodeId);
                            orderLine.setItemCode(plantId);
                            orderLine.setItemCode(warehouseId);
                            orderLine.setItemCode(languageId);

                            orderLine.setBarcodeId(inventory.getBarcodeId());
                            orderLine.setInventoryQty(inventory.getInventoryQuantity());
                            orderLine.setAllocatedQty(allocatableQty);
                            orderLine.setProposedStorageBin(inventory.getStorageBin());
                            orderLine.setItemCode(itemCode);
                            orderLine.setDescription(itemDescription);
                            orderLine.setOrderQty(orderManagementLineV2.getOrderQty());

                            STATUS_ID = (allocatableQty.equals(balanceOrderQty)) ? 43L : 42L;
                            String statusDescription = getStatusDescription(STATUS_ID, languageId);
                            orderLine.setStatusId(STATUS_ID);
                            orderLine.setStatusDescription(statusDescription);
                            orderLine.setReferenceField7(statusDescription);
//                            Long lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
//                                    warehouseId, orderManagementLineV2.getRefDocNumber());
//                            orderLine.setLineNumber(lineNo);
                            orderLine.setPickupUpdatedBy(loginUserID);
                            orderLine.setPickupUpdatedOn(new Date());
                            orderLine.setProposedPackBarCode("99999");
                            orderLine.setManufacturerCode(inventory.getManufacturerCode());
                            orderLine.setManufacturerName(inventory.getManufacturerName());
                            orderLine.setManufacturerFullName(inventory.getManufacturerName());
                            orderLine.setReferenceField5(orderManagementLineV2.getReferenceField5());
                            orderLine.setCustomerName(orderManagementLineV2.getCustomerName());
                            orderLine.setOrigin(orderManagementLineV2.getOrigin());

                            repo.orderManagementLineV2Repository.save(orderLine);
                            orderManagementLineList.add(orderLine);
                        }

                        balanceOrderQty -= allocatableQty;
                        log.info("Allocated {}, Remaining: {}", allocatableQty, balanceOrderQty);
                        if (balanceOrderQty <= 0) break;
                    }

                    if (balanceOrderQty > 0 && binClassId.equals(1L)) {

                        List<InventoryV2> inventoryV2ForBinCl10 = repo.inventoryV2Repository.getInventoryV10New(companyCodeId, plantId, languageId, warehouseId, itemCode, 10L);
                        log.info("Inventory List {} in Order Allocation", inventoryV2ForBinCl10.size());

                        if (!inventoryV2ForBinCl10.isEmpty()) {
                            for (InventoryV2 inventory : inventoryV2ForBinCl10) {

                                Double allocatableQty = 0D;
                                Double INV_QTY = inventory.getInventoryQuantity();

                                if (balanceOrderQty <= inventory.getInventoryQuantity()) {
                                    allocatableQty = balanceOrderQty;
                                } else if (balanceOrderQty > INV_QTY) {
                                    allocatableQty = INV_QTY;
                                } else if (INV_QTY == 0) {
                                    allocatableQty = 0D;
                                }

                                if (allocatableQty > 0) {
                                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                                    BeanUtils.copyProperties(orderManagementLineV2, orderLine, CommonUtils.getNullPropertyNames(orderManagementLineV2));
                                    orderLine.setItemCode(companyCodeId);
                                    orderLine.setItemCode(plantId);
                                    orderLine.setItemCode(warehouseId);
                                    orderLine.setItemCode(languageId);

                                    orderLine.setBarcodeId(inventory.getBarcodeId());
                                    orderLine.setInventoryQty(inventory.getInventoryQuantity());
                                    orderLine.setAllocatedQty(allocatableQty);
                                    orderLine.setProposedStorageBin(inventory.getStorageBin());
                                    orderLine.setItemCode(itemCode);
                                    orderLine.setDescription(itemDescription);
                                    orderLine.setOrderQty(orderManagementLineV2.getOrderQty());

                                    STATUS_ID = (allocatableQty.equals(balanceOrderQty)) ? 43L : 42L;
                                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                                    orderLine.setStatusId(STATUS_ID);
                                    orderLine.setStatusDescription(statusDescription);
                                    orderLine.setReferenceField7(statusDescription);
//                                    Long lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
//                                            warehouseId, orderManagementLineV2.getRefDocNumber());
//                                    orderLine.setLineNumber(lineNo);
                                    orderLine.setPickupUpdatedBy(loginUserID);
                                    orderLine.setPickupUpdatedOn(new Date());
                                    orderLine.setProposedPackBarCode("99999");
                                    orderLine.setManufacturerCode(inventory.getManufacturerCode());
                                    orderLine.setManufacturerName(inventory.getManufacturerName());
                                    orderLine.setManufacturerFullName(inventory.getManufacturerName());
                                    orderLine.setReferenceField5(orderManagementLineV2.getReferenceField5());
                                    orderLine.setCustomerName(orderManagementLineV2.getCustomerName());

                                    repo.orderManagementLineV2Repository.save(orderLine);
                                    orderManagementLineList.add(orderLine);
                                }

                                balanceOrderQty -= allocatableQty;
                                log.info("Allocated {}, Remaining: {}", allocatableQty, balanceOrderQty);
                                if (balanceOrderQty <= 0) break;
                            }
                        }
                    }
                }
                if (balanceOrderQty > 0 || inventoryV2List.isEmpty()) {

                    OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLineV2));
                    Long  lineNo = repo.orderManagementLineV2Repository.getLineNoV10(companyCodeId, plantId, languageId,
                            warehouseId, orderManagementLineV2.getRefDocNumber());
                    orderManagementLine.setLineNumber(lineNo);
                    orderManagementLine.setOrderQty(orderManagementLineV2.getOrderQty());
                    orderManagementLine.setBarcodeId(orderManagementLineV2.getBarcodeId());
                    orderManagementLine.setStatusId(47L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
                    orderManagementLine.setStatusDescription(statusDescription);
                    orderManagementLine.setReferenceField7(statusDescription);
                    orderManagementLine.setProposedStorageBin("");
                    orderManagementLine.setProposedPackBarCode("");
                    orderManagementLine.setInventoryQty(0D);
                    orderManagementLine.setAllocatedQty(0D);
                    repo.orderManagementLineV2Repository.save(orderManagementLine);
                    orderManagementLineList.add(orderManagementLine);
                    log.info("orderManagementLine created for UnAllocated Order V10" + orderManagementLine);
                }

            }

            // OutboundLines Created
            createOutboundLine(orderManagementLineList);

            if (!orderManagementLineList.isEmpty()) {
                List<OrderManagementLineV2> NoStockLines = orderManagementLineList.stream()
                        .filter(line -> Objects.equals(line.getStatusId(), 47L))
                        .collect(Collectors.toList());

                if (!NoStockLines.isEmpty()) {
                    log.info("Stated updating un allocated order");
                    updateUnAllocatedOrders(companyCodeId,plantId,languageId,warehouseId,refDocNumber,preOutboundNo,NoStockLines);
                    log.info("Un allocated order updated");
                }
                List<OrderManagementLineV2> nonNoStockLines = orderManagementLineList.stream()
                        .filter(line -> !Objects.equals(line.getStatusId(), 47L))
                        .collect(Collectors.toList());

                if (!nonNoStockLines.isEmpty()) {
                    log.info("PickupHeader Creation Started V10 ----------> OrderManagementLine Size is {} ", nonNoStockLines.size());
                    createPickupHeaderNoV10(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, nonNoStockLines, deliveryTo);
                }
            }

            return orderManagementLineList;
        } catch (Exception ex) {
            log.error("Unexpected error during order allocation V10 : {}", ex.getMessage(), ex);
            throw new RuntimeException("Order allocation process failed V10", ex);
        }
    }

}
