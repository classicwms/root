package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AddOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AssignPicker;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.AssignPickerV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineImpl;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.SearchOrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.OrderManagementLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.OrderManagementLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderManagementLineService extends BaseService {
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private ImBasicData1Repository imBasicData1Repository;

    @Autowired
    OrderManagementHeaderRepository orderManagementHeaderRepository;

    @Autowired
    OrderManagementLineRepository orderManagementLineRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    PickupHeaderRepository pickupHeaderRepository;

    @Autowired
    OutboundLineRepository outboundLineRepository;

    @Autowired
    OrderManagementHeaderService orderManagementHeaderService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    OutboundLineService outboundLineService;

    @Autowired
    MastersService mastersService;

    @Autowired
    OrderService orderService;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    private OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private OrderManagementLineService orderManagementLineService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    PickupHeaderService pickupHeaderService;

    String statusDescription = null;
    //------------------------------------------------------------------------------------------------------

    /**
     * getOrderManagementLines
     *
     * @return
     */
    public List<OrderManagementLine> getOrderManagementLines() {
        List<OrderManagementLine> orderManagementHeaderList = orderManagementLineRepository.findAll();
        orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return orderManagementHeaderList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @return Pass the Selected
     * WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE/PROP_ST_BIN/PROP_PACK_BARCODE
     * in ORDERMANAGEMENTLINE table
     */
    public OrderManagementLine getOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                      String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode) {
        OrderManagementLine orderManagementHeader = orderManagementLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                        proposedPackCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @return
     */
    public List<OrderManagementLine> getListOrderManagementLine(String warehouseId, String preOutboundNo,
                                                                String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin,
                                                                String proposedPackCode) {
        List<OrderManagementLine> orderManagementLineList = orderManagementLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                        proposedPackCode, 0L);
        if (orderManagementLineList != null && !orderManagementLineList.isEmpty()) {
            return orderManagementLineList;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
    }

    /**
     * Used by Allocation
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLine> getOrderManagementLine(String warehouseId, String preOutboundNo,
                                                            String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLine> orderManagementHeader = orderManagementLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLine> getListOrderManagementLine(String warehouseId, String preOutboundNo,
                                                                String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLine> orderManagementLine = orderManagementLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            return orderManagementLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param preOutboundNo
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLine> getOrderManagementLine(String preOutboundNo, Long lineNumber, String itemCode) {
        List<OrderManagementLine> orderManagementHeader = orderManagementLineRepository
                .findByPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(preOutboundNo, lineNumber, itemCode,
                        0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "preOutboundNo" + preOutboundNo
                + ",lineNumber" + lineNumber + ",itemCode" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     * @return
     */
    public long getOrderManagementLine(String warehouseId, String refDocNumber, String preOutboundNo, List<Long> statusId) {
        long orderManagementLineCount = orderManagementLineRepository
                .getByWarehouseIdAndAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return orderManagementLineCount;
    }

    /**
     * @param searchOrderManagementLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OrderManagementLine> findOrderManagementLine(SearchOrderManagementLine searchOrderManagementLine)
            throws ParseException, java.text.ParseException {

        if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null
                && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(),
                    searchOrderManagementLine.getEndRequiredDeliveryDate());
            searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
            searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchOrderManagementLine.getStartOrderDate() != null
                && searchOrderManagementLine.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(),
                    searchOrderManagementLine.getEndOrderDate());
            searchOrderManagementLine.setStartOrderDate(dates[0]);
            searchOrderManagementLine.setEndOrderDate(dates[1]);
        }
        OrderManagementLineSpecification spec = new OrderManagementLineSpecification(searchOrderManagementLine);
        List<OrderManagementLine> searchResults = orderManagementLineRepository.findAll(spec);
        return searchResults;
    }

    //Streaming
    public Stream<OrderManagementLine> findOrderManagementLineNew(SearchOrderManagementLine searchOrderManagementLine)
            throws ParseException, java.text.ParseException {

        if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null
                && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(),
                    searchOrderManagementLine.getEndRequiredDeliveryDate());
            searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
            searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchOrderManagementLine.getStartOrderDate() != null
                && searchOrderManagementLine.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(),
                    searchOrderManagementLine.getEndOrderDate());
            searchOrderManagementLine.setStartOrderDate(dates[0]);
            searchOrderManagementLine.setEndOrderDate(dates[1]);
        }
        OrderManagementLineSpecification spec = new OrderManagementLineSpecification(searchOrderManagementLine);
        Stream<OrderManagementLine> searchResults = orderManagementLineRepository.stream(spec, OrderManagementLine.class);

        return searchResults;
    }

    /**
     *
     */
    public void updateRef9ANDRef10() {
        List<OrderManagementLine> searchResults = orderManagementLineRepository
                .findByWarehouseIdAndStatusIdIn(WAREHOUSE_ID_110, Arrays.asList(42L, 43L, 47L));
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        for (OrderManagementLine orderManagementLine : searchResults) {
            if (orderManagementLine.getProposedStorageBin() != null
                    && orderManagementLine.getProposedStorageBin().trim().length() > 0) {
                // Getting StorageBin by WarehouseId
                StorageBin storageBin = mastersService.getStorageBin(orderManagementLine.getProposedStorageBin(),
                        orderManagementLine.getWarehouseId(), authTokenForMastersService.getAccess_token());

                // Ref_Field_9 for storing ST_SEC_ID
                orderManagementLine.setReferenceField9(storageBin.getStorageSectionId());

                // Ref_Field_10 for storing SPAN_ID
                orderManagementLine.setReferenceField10(storageBin.getSpanId());
                orderManagementLineRepository.save(orderManagementLine);
            }
        }
    }

    /**
     * createOrderManagementLine
     *
     * @param newOrderManagementLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLine createOrderManagementLine(AddOrderManagementLine newOrderManagementLine,
                                                         String loginUserID) throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine dbOrderManagementLine = new OrderManagementLine();
        log.info("newOrderManagementLine : " + newOrderManagementLine);
        BeanUtils.copyProperties(newOrderManagementLine, dbOrderManagementLine);
        dbOrderManagementLine.setDeletionIndicator(0L);
        return orderManagementLineRepository.save(dbOrderManagementLine);
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackBarCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLine doUnAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
                                              String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackBarCode,
                                              String loginUserID) throws IllegalAccessException, InvocationTargetException {

        // HAREESH - 2022-10-01- Validate multiple ordermanagement lines
        List<OrderManagementLine> orderManagementLineList = getListOrderManagementLine(warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + orderManagementLineList);
        /*
         * Update Inventory table -------------------------- Pass the
         * WH_ID/ITM_CODE/PACK_BARCODE(PROP_PACK_BARCODE)/ST_BIN(PROP_ST_BIN) values in
         * INVENTORY table update INV_QTY as (INV_QTY+ALLOC_QTY) and change ALLOC_QTY as
         * 0
         */
        int i = 0;
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(47L, warehouseId, idmasterAuthToken.getAccess_token());

        for (OrderManagementLine dbOrderManagementLine : orderManagementLineList) {
            String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
            String storageBin = dbOrderManagementLine.getProposedStorageBin();
            Inventory inventory = inventoryService.getInventory(warehouseId, packBarcodes, itemCode, storageBin);
            Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();

            /*
             * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
             */
            // Start
            if (invQty < 0D) {
                invQty = 0D;
            }
            // End

            inventory.setInventoryQuantity(invQty);
            log.info("Inventory invQty: " + invQty);

            Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
            if (allocQty < 0D) {
                allocQty = 0D;
            }
            inventory.setAllocatedQuantity(allocQty);
            log.info("Inventory allocQty: " + allocQty);

            inventory = inventoryRepository.save(inventory);
            log.info("Inventory updated: " + inventory);

            /*
             * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
             */
            dbOrderManagementLine.setAllocatedQty(0D);
            dbOrderManagementLine.setStatusId(47L);
            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            if (i != 0) {
                dbOrderManagementLine.setDeletionIndicator(1L);
            }
            OrderManagementLine updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
            i++;
        }
        return !orderManagementLineList.isEmpty() ? orderManagementLineList.get(0) : null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     */
    public OrderManagementLine doAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
                                            String partnerCode, Long lineNumber, String itemCode, String loginUserID) {
        List<OrderManagementLine> dbOrderManagementLines = getOrderManagementLine(warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + dbOrderManagementLines);
        OrderManagementLine dbOrderManagementLine = null;

        // If results is multiple reords then keeping one record and deleting rest of
        // them
        if (dbOrderManagementLines != null && !dbOrderManagementLines.isEmpty()) {
            dbOrderManagementLine = dbOrderManagementLines.get(0); // Keeping the first record

            // Deleting the rest
            for (int i = 1; i < dbOrderManagementLines.size(); i++) {
                // warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
                // proposedStorageBin, proposedPackCode
                OrderManagementLine orderManagementLineToDelete = dbOrderManagementLines.get(i);
                deleteOrderManagementLine(orderManagementLineToDelete.getWarehouseId(),
                        orderManagementLineToDelete.getPreOutboundNo(), orderManagementLineToDelete.getRefDocNumber(),
                        orderManagementLineToDelete.getPartnerCode(), orderManagementLineToDelete.getLineNumber(),
                        orderManagementLineToDelete.getItemCode(), orderManagementLineToDelete.getProposedStorageBin(),
                        orderManagementLineToDelete.getProposedPackBarCode(), loginUserID);
                log.info("Deleted the other orderManagementLine : " + orderManagementLineToDelete);
            }
        }

        Long OB_ORD_TYP_ID = dbOrderManagementLine.getOutboundOrderTypeId();
        Double ORD_QTY = dbOrderManagementLine.getOrderQty();

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
            dbOrderManagementLine = updateAllocation(dbOrderManagementLine, storageSectionIds, ORD_QTY, warehouseId,
                    itemCode, loginUserID);
        }

        if (OB_ORD_TYP_ID == 2L) {
            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
            dbOrderManagementLine = updateAllocation(dbOrderManagementLine, storageSectionIds, ORD_QTY, warehouseId,
                    itemCode, loginUserID);

        }
        dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
        dbOrderManagementLine.setPickupUpdatedOn(new Date());
        OrderManagementLine updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
        log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
        return updatedOrderManagementLine;
    }

    /**
     * @param assignPickers
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLine> doAssignPicker(List<AssignPicker> assignPickers, String assignedPickerId,
                                                    String loginUserID) {
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        Long lineNumber = null;
        String itemCode = null;
        String proposedStorageBin = null;
        String proposedPackCode = null;
        List<OrderManagementLine> orderManagementLineList = new ArrayList<>();

        // Iterating over AssignPicker
        for (AssignPicker assignPicker : assignPickers) {
            warehouseId = assignPicker.getWarehouseId();
            preOutboundNo = assignPicker.getPreOutboundNo();
            refDocNumber = assignPicker.getRefDocNumber();
            partnerCode = assignPicker.getPartnerCode();
            lineNumber = assignPicker.getLineNumber();
            itemCode = assignPicker.getItemCode();
            proposedStorageBin = assignPicker.getProposedStorageBin();
            proposedPackCode = assignPicker.getProposedPackCode();

            /**
             * Check for duplicates
             */
            PickupHeader dupPickupHeader = pickupHeaderRepository
                    .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                            warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
                            proposedStorageBin, proposedPackCode, 0L);

            if (dupPickupHeader == null) {
                OrderManagementLine dbOrderManagementLine = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);

                AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
                StatusId idStatus = idmasterService.getStatus(48L, warehouseId, idmasterAuthToken.getAccess_token());

                dbOrderManagementLine.setAssignedPickerId(assignedPickerId);
                dbOrderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
                dbOrderManagementLine.setPickupUpdatedBy(loginUserID);            // Ref_field_7
                dbOrderManagementLine.setPickupUpdatedOn(new Date());
                dbOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
                log.info("dbOrderManagementLine updated : " + dbOrderManagementLine);

                /*
                 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                 * OUTBOUNDLINE table and update SATATU_ID as 48
                 */
                OutboundLine outboundLine = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode);
                outboundLine.setStatusId(48L);
                outboundLine = outboundLineRepository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                // OutboundHeader Update
                OutboundHeader outboundHeader = outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo,
                        refDocNumber, partnerCode);
                outboundHeader.setStatusId(48L);
                outboundHeaderRepository.save(outboundHeader);

                // ORDERMANAGEMENTHEADER Update
                OrderManagementHeader orderManagementHeader = orderManagementHeaderService
                        .getOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
                orderManagementHeader.setStatusId(48L);
                orderManagementHeaderRepository.save(orderManagementHeader);

                // Create Pickup TO Number
                /*
                 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITM_CODE/OBLINE_NO
                 * and validate PU_NO is Null in ORDERMANAGEMENTLINE table , If yes
                 *
                 * Create New PU_NO by Pass WH_ID - Userlogged in WH_ID and NUM_RAN_CODE = 10 in
                 * NUMBERRANGE table and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR
                 * and add +1 and then update in ORDERMANAGEMENTLINE table by passing
                 * WH_ID/PRE_OB_NO/OB_LINE_NO/REF_DOC_NO/ITM_CODE
                 */
                log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber());
                if (dbOrderManagementLine.getPickupNumber() == null) {
                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getWarehouseId());
                    log.info("PU_NO : " + PU_NO);

                    // Insertion of Record in PICKUPHEADER tables
                    PickupHeader pickupHeader = new PickupHeader();
                    BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader,
                            CommonUtils.getNullPropertyNames(dbOrderManagementLine));

                    // PU_NO
                    pickupHeader.setPickupNumber(PU_NO);

                    // PICK_TO_QTY
                    pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());

                    // PICK_UOM
                    pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());

                    // STATUS_ID
                    pickupHeader.setStatusId(48L);
                    pickupHeader.setReferenceField7(idStatus.getStatus());

                    // ProposedPackbarcode
                    pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());

                    pickupHeader.setPickupCreatedBy(loginUserID);
                    pickupHeader.setPickupCreatedOn(new Date());

                    // REF_FIELD_1
                    pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
                    pickupHeader = pickupHeaderRepository.save(pickupHeader);
                    log.info("pickupHeader created : " + pickupHeader);

                    // Updating Ordermanagementline
                    dbOrderManagementLine.setPickupNumber(PU_NO);
                    dbOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
                    log.info("OrderManagementLine updated : " + dbOrderManagementLine);
                }
                orderManagementLineList.add(dbOrderManagementLine);
            }
        }
        return orderManagementLineList;
    }

//		
//		/* To obtain the SumOfInvQty */
//		List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
//		log.info("---Filtered---stBins -----> : " + stBins);
//		
//		List<Inventory> finalInventoryList = new ArrayList<>();
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//		storageBinPutAway.setStorageBin(stBins);
//		storageBinPutAway.setStorageSectionIds(storageSectionIds);
//		storageBinPutAway.setWarehouseId(warehouseId);
//		
//		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//		log.info("---1----selected----storageBins---from---masters-----> : " + storageBin);
//		
//		// If the StorageBin returns null, then creating OrderManagementLine table with Zero Alloc_qty and Inv_Qty
//		if (storageBin == null) {
//			return updateOrderManagementLine(orderManagementLine);
//		}
//		
//		if (storageBin != null && storageBin.length > 0) {
//			log.info("----2----selected----storageBins---from---masters-----> : " + Arrays.asList(storageBin));
//			
//			// Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch SUM (INV_QTY)
//			for (StorageBin dbStorageBin : storageBin) {
//				List<Inventory> listInventory = inventoryService.getInventoryForOrderMgmt (warehouseId, itemCode, 1L, dbStorageBin.getStorageBin(), 1L);
//				log.info("----Selected--Inventory--by--stBin--wise----> : " + listInventory);
//				if (listInventory != null) {
//					finalInventoryList.addAll(listInventory);
//				}
//			}
//			List<StorageBin> stBinList = Arrays.asList(storageBin);
//			List<String> storageBinListToQueryInventory = stBinList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
//			List<Inventory> listInventory = inventoryService.getInventoryForOrderMgmt (warehouseId, itemCode, 1L, storageBinListToQueryInventory, 1L);
//			if (listInventory != null) {
//				finalInventoryList.addAll(listInventory);
//			}
//		}
//		log.info("Final inventory list###########---> : " + finalInventoryList);

//		
//		List<IInventory> finalInventoryList = inventoryService.getInventoryGroupByStorageBin(warehouseId, itemCode, storageSectionIds);
//		log.info("---Global---finalInventoryList-------> : " + finalInventoryList);
//		
//		/*
//		 * If the Inventory doesn't exists in the Table then inserting 0th record in Ordermanagementline table
//		 */
//		if (finalInventoryList.isEmpty()) {
//			return updateOrderManagementLine(orderManagementLine);
//		}

//		Inventory maxQtyHoldsInventory = new Inventory();

//		Double tempMaxQty = 0D;
//		for (Inventory inventory : finalInventoryList) {
//			if (tempMaxQty < inventory.getInventoryQuantity()) {
//				tempMaxQty = inventory.getInventoryQuantity();
//			}
//		}
//		
//		for (Inventory inventory : finalInventoryList) {
//			if (inventory.getInventoryQuantity() == tempMaxQty) {
//				BeanUtils.copyProperties(inventory, maxQtyHoldsInventory, CommonUtils.getNullPropertyNames(inventory));
//			}
//		}
//		log.info("Found ------tempMaxQty-----> : " + tempMaxQty);
//		log.info("Found ------tempMaxQty--Inventory---> : " + maxQtyHoldsInventory);
//		
//		/*
//		 * Sorting the list
//		 */
//		Collections.sort(finalInventoryList, new Comparator<Inventory>() {
//            public int compare(Inventory s1, Inventory s2) {
//                return ((Double)s2.getInventoryQuantity()).compareTo(s1.getInventoryQuantity());
//            }
//        });

//		log.info("Collections------sort-----> : " + finalInventoryList);
//		if (ORD_QTY < maxQtyHoldsInventory.getInventoryQuantity()) {
//			Long STATUS_ID = 0L;
//			Double ALLOC_QTY = 0D;			
//			Double INV_QTY = maxQtyHoldsInventory.getInventoryQuantity();
//			
//			// INV_QTY
//			orderManagementLine.setInventoryQty(INV_QTY);
//			
//			if (ORD_QTY <= INV_QTY) {
//				ALLOC_QTY = ORD_QTY;
//			} else if (ORD_QTY > INV_QTY) {
//				ALLOC_QTY = INV_QTY;
//			} else if (INV_QTY == 0) {
//				ALLOC_QTY = 0D;
//			}
//			log.info ("ALLOC_QTY -----@@--->: " + ALLOC_QTY);
//			
//			orderManagementLine.setAllocatedQty(ALLOC_QTY);
//			orderManagementLine.setReAllocatedQty(ALLOC_QTY);
//						
//			// STATUS_ID 
//			/* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
//			if (ORD_QTY > ALLOC_QTY) {
//				STATUS_ID = 42L;
//			}
//			
//			/* if ORD_QTY=ALLOC_QTY,  then STATUS_ID is hardcoded as "43" */
//			if (ORD_QTY == ALLOC_QTY) {
//				STATUS_ID = 43L;
//			}
//			
//			orderManagementLine.setStatusId(STATUS_ID);
//			orderManagementLine.setPickupUpdatedBy(loginUserID);
//			orderManagementLine.setPickupUpdatedOn(new Date());
//			
//			/*
//			 * Deleting current record and inserting new record (since UK is allowing to update 
//			 * prop_st_bin and Pack_bar_codes columns)
//			 */
//			try {
//				orderManagementLineRepository.delete(orderManagementLine);
//				log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
//			} catch (Exception e) {
//				log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
//				e.printStackTrace();
//			}
//			
//			OrderManagementLine newOrderManagementLine = new OrderManagementLine();
//			BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
//			newOrderManagementLine.setProposedStorageBin(maxQtyHoldsInventory.getStorageBin());
//			newOrderManagementLine.setProposedPackBarCode(maxQtyHoldsInventory.getPackBarcodes());
//			OrderManagementLine createdOrderManagementLine = orderManagementLineRepository.save(newOrderManagementLine);
//			log.info("--1---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
//			
//			if (createdOrderManagementLine.getAllocatedQty() > 0) {
//				// Update Inventory table
//				Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId, createdOrderManagementLine.getProposedPackBarCode(), 
//						itemCode, createdOrderManagementLine.getProposedStorageBin());
//				log.info("-----inventoryForUpdate------> : " + inventoryForUpdate);
//				if (inventoryForUpdate == null) {
//					throw new BadRequestException("Inventory found as null.");
//				}
//				
//				double dbInventoryQty = 0;
//				double dbInvAllocatedQty = 0;
//				
//				if (inventoryForUpdate.getAllocatedQuantity() != null) {
//					dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
//				}
//				
//				if (inventoryForUpdate.getInventoryQuantity() != null) {
//					dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
//				}
//				
//				double inventoryQty = dbInventoryQty - createdOrderManagementLine.getAllocatedQty();
//				double allocatedQty = dbInvAllocatedQty + createdOrderManagementLine.getAllocatedQty();
//				
//				/*
//				 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
//				 */
//				// Start
//				if (inventoryQty < 0) {
//					inventoryQty = 0;
//				}
//				// End
//				inventoryForUpdate.setInventoryQuantity(inventoryQty);
//				inventoryForUpdate.setAllocatedQuantity(allocatedQty);
//				inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
//				log.info("inventoryForUpdate updated: " + inventoryForUpdate);
//			}
//			return createdOrderManagementLine;
//		} else {
//		for (Inventory stBinInventory : finalInventoryList) {

    /**
     * @param orderManagementLine
     * @param storageSectionIds
     * @param ORD_QTY
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     * @return
     */
    public OrderManagementLine updateAllocation(OrderManagementLine orderManagementLine, List<String> storageSectionIds,
                                                Double ORD_QTY, String warehouseId, String itemCode, String loginUserID) {
        List<Inventory> stockType1InventoryList = inventoryService.getInventoryForOrderManagement(warehouseId, itemCode,
                1L, 1L);
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList);
        if (stockType1InventoryList.isEmpty()) {
            return updateOrderManagementLine(orderManagementLine);
        }

        // -----------------------------------------------------------------------------------------------------------------------------------------
        // Getting Inventory GroupBy ST_BIN wise
        List<IInventory> finalInventoryList = inventoryService.getInventoryGroupByStorageBin(warehouseId, itemCode,
                storageSectionIds);
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList + "\n");

        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
            return updateOrderManagementLine(orderManagementLine);
        }

        OrderManagementLine newOrderManagementLine = null;
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        outerloop:
        for (IInventory stBinWiseInventory : finalInventoryList) {
            log.info("\nstBinWiseInventory---->: " + stBinWiseInventory.getStorageBin() + "::"
                    + stBinWiseInventory.getInventoryQty());

            // Getting PackBarCode by passing ST_BIN to Inventory
            List<Inventory> listInventoryForAlloc = inventoryService.getInventoryForOrderMgmt(warehouseId, itemCode, 1L,
                    stBinWiseInventory.getStorageBin(), 1L);
            log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc + "\n");

            // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
            // created.
            if (listInventoryForAlloc != null && listInventoryForAlloc.isEmpty()) {
                return updateOrderManagementLine(orderManagementLine);
            }

            for (Inventory stBinInventory : listInventoryForAlloc) {
                log.info("\nBin-wise Inventory : " + stBinInventory + "\n");

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
                        orderManagementLineRepository.delete(orderManagementLine);
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

                StatusId idStatus = idmasterService.getStatus(STATUS_ID, orderManagementLine.getWarehouseId(), idmasterAuthToken.getAccess_token());
                orderManagementLine.setStatusId(STATUS_ID);
                orderManagementLine.setReferenceField7(idStatus.getStatus());
                orderManagementLine.setPickupUpdatedBy(loginUserID);
                orderManagementLine.setPickupUpdatedOn(new Date());

                double allocatedQtyFromOrderMgmt = 0.0;

                /*
                 * Deleting current record and inserting new record (since UK is not allowing to
                 * update prop_st_bin and Pack_bar_codes columns
                 */
                newOrderManagementLine = new OrderManagementLine();
                BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine,
                        CommonUtils.getNullPropertyNames(orderManagementLine));
                newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
                OrderManagementLine createdOrderManagementLine = orderManagementLineRepository
                        .save(newOrderManagementLine);
                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                if (ORD_QTY > ALLOC_QTY) {
                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                }

                if (allocatedQtyFromOrderMgmt > 0) {
                    // Update Inventory table
                    Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId,
                            stBinInventory.getPackBarcodes(), itemCode, stBinInventory.getStorageBin());

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
                    inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
                    log.info("inventoryForUpdate updated: " + inventoryForUpdate);
                }

                if (ORD_QTY == ALLOC_QTY) {
                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
                    break outerloop; // If the Inventory satisfied the Ord_qty
                }
            }
        }
        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLine updateOrderManagementLine(OrderManagementLine orderManagementLine) {
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(47L, orderManagementLine.getWarehouseId(), idmasterAuthToken.getAccess_token());

        orderManagementLine.setStatusId(47L);
        orderManagementLine.setReferenceField7(idStatus.getStatus());
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineRepository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updateOrderManagementLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLine updateOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                         String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                                         UpdateOrderManagementLine updateOrderManagementLine)
            throws IllegalAccessException, InvocationTargetException {
        List<OrderManagementLine> dbOrderManagementLines = getOrderManagementLine(warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        for (OrderManagementLine dbOrderManagementLine : dbOrderManagementLines) {
            BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine,
                    CommonUtils.getNullPropertyNames(updateOrderManagementLine));
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            return orderManagementLineRepository.save(dbOrderManagementLine);
        }
        return null;
    }

    /**
     * updateOrderManagementLine
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @param loginUserID
     * @param updateOrderMangementLine
     * @return
     */
    public OrderManagementLine updateOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                         String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                                         String loginUserID, @Valid UpdateOrderManagementLine updateOrderMangementLine) {
        OrderManagementLine dbOrderManagementLine = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
        if (dbOrderManagementLine != null) {
            BeanUtils.copyProperties(updateOrderMangementLine, dbOrderManagementLine,
                    CommonUtils.getNullPropertyNames(updateOrderMangementLine));
            if (updateOrderMangementLine.getPickupNumber() == null) {
                dbOrderManagementLine.setPickupNumber(null);
            }
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            return orderManagementLineRepository.save(dbOrderManagementLine);
        }
        return null;
    }

    /**
     * deleteOrderManagementLine
     *
     * @param loginUserID
     * @param refDocNumber
     */
    public void deleteOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                          String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                          String loginUserID) {
        OrderManagementLine orderManagementHeader = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
        if (orderManagementHeader != null) {
            orderManagementHeader.setDeletionIndicator(1L);
            orderManagementLineRepository.save(orderManagementHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

//=====================================================================V2===================================================================================

    /**
     * getOrderManagementLines
     *
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLinesV2() {
        List<OrderManagementLineV2> orderManagementHeaderList = orderManagementLineV2Repository.findAll();
        orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return orderManagementHeaderList;
    }

    /**
     * getOrderManagementLine
     *
     * @param proposedPackCode
     * @param proposedStorageBin
     * @param itemCode
     * @param lineNumber
     * @param partnerCode
     * @param preOutboundNo
     * @param warehouseId
     * @param plantId
     * @param companyCodeId
     * @return Pass the Selected
     * WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE/PROP_ST_BIN/PROP_PACK_BARCODE
     * in ORDERMANAGEMENTLINE table
     */
    public OrderManagementLineV2 getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                          String itemCode, String proposedStorageBin, String proposedPackCode) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineForPickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String preOutboundNo, String refDocNumber) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        } else {
            return null;
        }

    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                       String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
                         plantId, companyCodeId, languageId, warehouseId, preOutboundNo, refDocNumber, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null && !orderManagementHeader.isEmpty()) {
            return orderManagementHeader.get(0);
        } else {
            return null;
        }

    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode,
                                                                        String manufacturerName, String storageBin) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndManufacturerNameAndProposedStorageBinAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, lineNumber, itemCode, manufacturerName, storageBin, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        } else {
            return null;
        }

    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @return
     */
    public List<OrderManagementLineV2> getListOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                    String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin,
                                                                    String proposedPackCode) {
        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, 0L);
        if (orderManagementLineList != null && !orderManagementLineList.isEmpty()) {
            return orderManagementLineList;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId +
                "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
    }

    /**
     * Used by Allocation
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getListOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                    String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            return orderManagementLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param preOutboundNo
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String preOutboundNo, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + "preOutboundNo" + preOutboundNo
                + ",lineNumber" + lineNumber + ",itemCode" + itemCode + " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param statusId
     * @return
     */
    public long getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String refDocNumber, String preOutboundNo, List<Long> statusId) {
        long orderManagementLineCount = orderManagementLineV2Repository
                .getByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return orderManagementLineCount;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserID
     */
    public void updateOrderManagementLineForBarcodeV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String itemCode, String manufacturerName, String barcodeId, String loginUserID) {
        List<Long> statusIdList = new ArrayList<>();
        statusIdList.add(41L);      //Order Allocation
        statusIdList.add(42L);      //Partial Allocation
        statusIdList.add(43L);      //Allocated
        statusIdList.add(48L);      //InPicking

        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdInAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, statusIdList, 0L);
        log.info("OrderManagementLine: " + orderManagementLineList);
        if (orderManagementLineList != null && !orderManagementLineList.isEmpty()) {
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineList) {
                dbOrderManagementLine.setBarcodeId(barcodeId);
                dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
                dbOrderManagementLine.setPickupUpdatedOn(new Date());
                orderManagementLineV2Repository.save(dbOrderManagementLine);
            }
        }
    }

    //Streaming
    public Stream<OrderManagementLineV2> findOrderManagementLineV2(SearchOrderManagementLineV2 searchOrderManagementLine)
            throws ParseException, java.text.ParseException {

        if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null
                && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(),
                    searchOrderManagementLine.getEndRequiredDeliveryDate());
            searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
            searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchOrderManagementLine.getStartOrderDate() != null
                && searchOrderManagementLine.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(),
                    searchOrderManagementLine.getEndOrderDate());
            searchOrderManagementLine.setStartOrderDate(dates[0]);
            searchOrderManagementLine.setEndOrderDate(dates[1]);
        }
        OrderManagementLineV2Specification spec = new OrderManagementLineV2Specification(searchOrderManagementLine);
        Stream<OrderManagementLineV2> searchResults = orderManagementLineV2Repository.stream(spec, OrderManagementLineV2.class);

        return searchResults;
    }

    /**
     *
     */
    public void updateV2Ref9ANDRef10(String companyCodeId, String plantId, String languageId, String warehouseId) {
        List<OrderManagementLineV2> searchResults = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdIn(
                        companyCodeId, plantId, languageId, warehouseId, Arrays.asList(42L, 43L, 47L));
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        for (OrderManagementLineV2 orderManagementLine : searchResults) {
            if (orderManagementLine.getProposedStorageBin() != null
                    && orderManagementLine.getProposedStorageBin().trim().length() > 0) {
                // Getting StorageBin by WarehouseId
                StorageBinV2 storageBin = mastersService.getStorageBinV2(orderManagementLine.getProposedStorageBin(),
                        orderManagementLine.getWarehouseId(), authTokenForMastersService.getAccess_token());

                // Ref_Field_9 for storing ST_SEC_ID
                orderManagementLine.setReferenceField9(storageBin.getStorageSectionId());

                // Ref_Field_10 for storing SPAN_ID
                orderManagementLine.setReferenceField10(storageBin.getSpanId());
                orderManagementLineV2Repository.save(orderManagementLine);
            }
        }
    }

    /**
     * createOrderManagementLine
     *
     * @param newOrderManagementLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLineV2 createOrderManagementLineV2(OrderManagementLineV2 newOrderManagementLine,
                                                             String loginUserID) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        OrderManagementLineV2 dbOrderManagementLine = new OrderManagementLineV2();
        log.info("newOrderManagementLine : " + newOrderManagementLine);

        BeanUtils.copyProperties(newOrderManagementLine, dbOrderManagementLine, CommonUtils.getNullPropertyNames(newOrderManagementLine));

        IKeyValuePair description = stagingLineV2Repository.getDescription(dbOrderManagementLine.getCompanyCodeId(),
                dbOrderManagementLine.getLanguageId(),
                dbOrderManagementLine.getPlantId(),
                dbOrderManagementLine.getWarehouseId());

        if (dbOrderManagementLine.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
            dbOrderManagementLine.setStatusDescription(statusDescription);
        }

        dbOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
        dbOrderManagementLine.setPlantDescription(description.getPlantDesc());
        dbOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());

        dbOrderManagementLine.setDeletionIndicator(0L);
        dbOrderManagementLine.setPickupCreatedBy(loginUserID);
        dbOrderManagementLine.setPickupCreatedOn(new Date());
        dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
        dbOrderManagementLine.setPickupUpdatedOn(new Date());

        return orderManagementLineV2Repository.save(dbOrderManagementLine);
    }

    /**
     *
     * @param outboundIntegrationHeader
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void doUnAllocationV2(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : "EN";
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
            String refDocNo = outboundIntegrationHeader.getRefDocumentNo();

            List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId, 0L);

            log.info("Rollback---> 1. unAllocation ----> " + refDocNo + ", " + outboundOrderTypeId);
            //if order management line present do un allocation
            if (orderManagementLineV2List != null && !orderManagementLineV2List.isEmpty()) {
                doUnAllocationV2(orderManagementLineV2List, "MW_AMS");
                log.info("Rollback---> 1.Unallocation Finished ----> " + refDocNo + ", " + outboundOrderTypeId);
            }

            //delete all records from respective tables
            log.info("Rollback---> 2. delete all record initiated ----> " + refDocNo + ", " + outboundOrderTypeId);
            orderManagementLineV2Repository.deleteOutboundProcessingProc(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
            log.info("Rollback---> 2. delete all record finished ----> " + refDocNo + ", " + outboundOrderTypeId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param outboundIntegrationHeader
     * @throws Exception
     */
    public void rollback(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : "EN";
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
            String refDocNo = outboundIntegrationHeader.getRefDocumentNo();
            initiateRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
        } catch (Exception e) {
            log.error("Exception occurred : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNo
     * @param outboundOrderTypeId
     * @throws Exception
     */
    public void rollback(String companyCodeId, String plantId, String languageId, String warehouseId,
                         String refDocNo, Long outboundOrderTypeId) throws Exception {
        try {
            initiateRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
            log.info("Rollback---> 3. rerun the order ----> " + refDocNo + ", " + outboundOrderTypeId);
            orderService.reRunProcessedOrderV2(refDocNo, outboundOrderTypeId);
        } catch (Exception e) {
            log.error("Exception occurred during Rollback : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNo
     * @param outboundOrderTypeId
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    public void initiateRollBack(String companyCodeId, String plantId, String languageId, String warehouseId,
                                 String refDocNo, Long outboundOrderTypeId) throws Exception {
        try {

            List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId, 0L);

            log.info("Rollback---> 1. Inventory restore ----> " + refDocNo + ", " + outboundOrderTypeId);
            //if order management line present do un allocation
            if (orderManagementLineV2List != null && !orderManagementLineV2List.isEmpty()) {
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
                    String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
                    String storageBin = dbOrderManagementLine.getProposedStorageBin();
                    InventoryV2 inventory =
                            inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                                            dbOrderManagementLine.getWarehouseId(), packBarcodes, dbOrderManagementLine.getItemCode(), storageBin,
                                                            dbOrderManagementLine.getManufacturerName());
                    Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();

                    /*
                     * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                     */
                    if (invQty < 0D) {
                        invQty = 0D;
                    }

                    inventory.setInventoryQuantity(invQty);
                    log.info("Inventory invQty: " + invQty);

                    Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
                    if (allocQty < 0D) {
                        allocQty = 0D;
                    }
                    inventory.setAllocatedQuantity(allocQty);
                    log.info("Inventory allocQty: " + allocQty);
                    Double totQty = invQty + allocQty;
                    inventory.setReferenceField4(totQty);
                    log.info("Inventory totQty: " + totQty);

                    // Create new Inventory Record
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                    inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----InventoryV2 created-------: " + inventoryV2);
                }
                log.info("Rollback---> 1.Inventory restoration Finished ----> " + refDocNo + ", " + outboundOrderTypeId);
            }

            //delete all records from respective tables
            log.info("Rollback---> 2. delete all record initiated ----> " + refDocNo + ", " + outboundOrderTypeId);
            orderManagementLineV2Repository.deleteOutboundProcessingProc(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
            log.info("Rollback---> 2. delete all record finished ----> " + refDocNo + ", " + outboundOrderTypeId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * @param orderManagementLineV2
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     */
    public List<OrderManagementLineV2> doUnAllocationV2(List<OrderManagementLineV2> orderManagementLineV2, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        List<OrderManagementLineV2> orderManagementLineV2s = new ArrayList<>();

        for (OrderManagementLineV2 lineV2 : orderManagementLineV2) {

            List<OrderManagementLineV2> orderManagementLineV2List = getListOrderManagementLineV2(lineV2.getCompanyCodeId(), lineV2.getPlantId(), lineV2.getLanguageId(), lineV2.getWarehouseId(), lineV2.getPreOutboundNo(),
                    lineV2.getRefDocNumber(), lineV2.getPartnerCode(), lineV2.getLineNumber(), lineV2.getItemCode());
            log.info("Processing Order management Line : " + orderManagementLineV2List);

            int i = 0;
            statusDescription = stagingLineV2Repository.getStatusDescription(47L, lineV2.getLanguageId());

            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
                String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
                String storageBin = dbOrderManagementLine.getProposedStorageBin();
                InventoryV2 inventory =
                        inventoryService.getInventoryV2(lineV2.getCompanyCodeId(), lineV2.getPlantId(), lineV2.getLanguageId(),
                                lineV2.getWarehouseId(), packBarcodes, lineV2.getItemCode(), storageBin);
                Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();

                /*
                 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                 */
                // Start
                if (invQty < 0D) {
                    invQty = 0D;
                }
                // End

                inventory.setInventoryQuantity(invQty);
                log.info("Inventory invQty: " + invQty);

                Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
                if (allocQty < 0D) {
                    allocQty = 0D;
                }
                inventory.setAllocatedQuantity(allocQty);
                log.info("Inventory allocQty: " + allocQty);
                Double totQty = invQty + allocQty;
                inventory.setReferenceField4(totQty);
                log.info("Inventory totQty: " + totQty);

//            inventory = inventoryRepository.save(inventory);
//            log.info("Inventory updated: " + inventory);
                // Create new Inventory Record
                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----InventoryV2 created-------: " + inventoryV2);


                /*
                 * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
                 */
                dbOrderManagementLine.setAllocatedQty(0D);
                dbOrderManagementLine.setStatusId(47L);
//            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
                dbOrderManagementLine.setReferenceField7(statusDescription);
                dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
                dbOrderManagementLine.setPickupUpdatedOn(new Date());
                Long deletionIndicator = 0L;
                if (i != 0) {
//                    dbOrderManagementLine.setDeletionIndicator(1L);
                    deletionIndicator = 1L;
                }
                orderManagementLineV2Repository.updateOrderManagementLineUnAllocateV2(
                        lineV2.getCompanyCodeId(), lineV2.getPlantId(), lineV2.getLanguageId(),
                        lineV2.getWarehouseId(), lineV2.getPreOutboundNo(), lineV2.getRefDocNumber(),
                        lineV2.getPartnerCode(), lineV2.getLineNumber(), lineV2.getItemCode(),
                        47L, statusDescription, deletionIndicator, loginUserID, new Date());
//                OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
//                log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
                i++;
                orderManagementLineV2s.add(dbOrderManagementLine);
            }
        }
        return orderManagementLineV2s;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackBarCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLineV2 doUnAllocationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                  String itemCode, String proposedStorageBin, String proposedPackBarCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        // HAREESH - 2022-10-01- Validate multiple ordermanagement lines
        List<OrderManagementLineV2> orderManagementLineList =
                getListOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + orderManagementLineList);

        /*
         * Update Inventory table -------------------------- Pass the
         * WH_ID/ITM_CODE/PACK_BARCODE(PROP_PACK_BARCODE)/ST_BIN(PROP_ST_BIN) values in
         * INVENTORY table update INV_QTY as (INV_QTY+ALLOC_QTY) and change ALLOC_QTY as
         * 0
         */
        int i = 0;
//        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
//        StatusId idStatus = idmasterService.getStatus(47L, warehouseId, idmasterAuthToken.getAccess_token());
//        StatusId idStatus = idmasterService.getStatus(47L, warehouseId, languageId, idmasterAuthToken.getAccess_token());

        statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);

        for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineList) {
            String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
            String storageBin = dbOrderManagementLine.getProposedStorageBin();
            InventoryV2 inventory =
                    inventoryService.getInventoryV2(companyCodeId, plantId, languageId, warehouseId, packBarcodes, itemCode, storageBin);
            Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();

            /*
             * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
             */
            // Start
            if (invQty < 0D) {
                invQty = 0D;
            }
            // End

            inventory.setInventoryQuantity(invQty);
            log.info("Inventory invQty: " + invQty);

            Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
            if (allocQty < 0D) {
                allocQty = 0D;
            }
            inventory.setAllocatedQuantity(allocQty);
            log.info("Inventory allocQty: " + allocQty);
            Double totQty = invQty + allocQty;
            inventory.setReferenceField4(totQty);
            log.info("Inventory totQty: " + totQty);
//            inventory = inventoryRepository.save(inventory);
//            log.info("Inventory updated: " + inventory);
            // Create new Inventory Record
            InventoryV2 inventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
            inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
            inventoryV2 = inventoryV2Repository.save(inventoryV2);
            log.info("-----InventoryV2 created-------: " + inventoryV2);

            /*
             * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
             */
            dbOrderManagementLine.setAllocatedQty(0D);
            dbOrderManagementLine.setStatusId(47L);
//            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
            dbOrderManagementLine.setReferenceField7(statusDescription);
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            if (i != 0) {
                dbOrderManagementLine.setDeletionIndicator(1L);
            }
            OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
            i++;
        }
        return !orderManagementLineList.isEmpty() ? orderManagementLineList.get(0) : null;
    }

    /**
     * @param orderManagementLineV2s
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> doAllocationV2(List<OrderManagementLineV2> orderManagementLineV2s, String loginUserID) throws java.text.ParseException {

        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();

        OrderManagementLineV2 dbOrderManagementLine = null;

        for (OrderManagementLineV2 lineV2 : orderManagementLineV2s) {

            List<OrderManagementLineV2> orderManagementLineV2 =
                    getOrderManagementLineV2(lineV2.getCompanyCodeId(), lineV2.getPlantId(),
                            lineV2.getLanguageId(), lineV2.getWarehouseId(), lineV2.getPreOutboundNo(),
                            lineV2.getRefDocNumber(), lineV2.getPartnerCode(), lineV2.getLineNumber(), lineV2.getItemCode());
            log.info("Processing Order management Line : " + orderManagementLineV2);


            // If results is multiple reords then keeping one record and deleting rest of them
            if (orderManagementLineV2 != null && !orderManagementLineV2.isEmpty()) {
                dbOrderManagementLine = orderManagementLineV2.get(0); // Keeping the first record

                // Deleting the rest
                for (int i = 1; i < orderManagementLineV2.size(); i++) {
                    // warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
                    // proposedStorageBin, proposedPackCode
                    OrderManagementLineV2 orderManagementLineToDelete = orderManagementLineV2.get(i);
                    deleteOrderManagementLineV2(orderManagementLineToDelete.getCompanyCodeId(),
                            orderManagementLineToDelete.getPlantId(), orderManagementLineToDelete.getLanguageId(),
                            orderManagementLineToDelete.getWarehouseId(),
                            orderManagementLineToDelete.getPreOutboundNo(), orderManagementLineToDelete.getRefDocNumber(),
                            orderManagementLineToDelete.getPartnerCode(), orderManagementLineToDelete.getLineNumber(),
                            orderManagementLineToDelete.getItemCode(), orderManagementLineToDelete.getProposedStorageBin(),
                            orderManagementLineToDelete.getProposedPackBarCode(), loginUserID);
                    log.info("Deleted the other orderManagementLine : " + orderManagementLineToDelete);
                }
            }

            assert dbOrderManagementLine != null;
            Long OB_ORD_TYP_ID = dbOrderManagementLine.getOutboundOrderTypeId();
            Double ORD_QTY = dbOrderManagementLine.getOrderQty();

            Long BIN_CLASS_ID;
            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
                BIN_CLASS_ID = 1L;
                dbOrderManagementLine = updateAllocationV2(dbOrderManagementLine, BIN_CLASS_ID, ORD_QTY, dbOrderManagementLine.getWarehouseId(),
                        dbOrderManagementLine.getItemCode(), loginUserID);
            }

            if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
                BIN_CLASS_ID = 7L;
                dbOrderManagementLine = updateAllocationV2(dbOrderManagementLine, BIN_CLASS_ID, ORD_QTY, dbOrderManagementLine.getWarehouseId(),
                        dbOrderManagementLine.getItemCode(), loginUserID);

                dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
                dbOrderManagementLine.setPickupUpdatedOn(new Date());

            }
            OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);

            orderManagementLineV2List.add(updatedOrderManagementLine);
        }

        return orderManagementLineV2List;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 doAllocationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                String itemCode, String loginUserID) throws java.text.ParseException {
        List<OrderManagementLineV2> dbOrderManagementLines =
                getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + dbOrderManagementLines);

        OrderManagementLineV2 dbOrderManagementLine = null;

        // If results is multiple reords then keeping one record and deleting rest of them
        if (dbOrderManagementLines != null && !dbOrderManagementLines.isEmpty()) {
            dbOrderManagementLine = dbOrderManagementLines.get(0); // Keeping the first record

            // Deleting the rest
            for (int i = 1; i < dbOrderManagementLines.size(); i++) {
                // warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
                // proposedStorageBin, proposedPackCode
                OrderManagementLineV2 orderManagementLineToDelete = dbOrderManagementLines.get(i);
                deleteOrderManagementLineV2(orderManagementLineToDelete.getCompanyCodeId(),
                        orderManagementLineToDelete.getPlantId(), orderManagementLineToDelete.getLanguageId(),
                        orderManagementLineToDelete.getWarehouseId(),
                        orderManagementLineToDelete.getPreOutboundNo(), orderManagementLineToDelete.getRefDocNumber(),
                        orderManagementLineToDelete.getPartnerCode(), orderManagementLineToDelete.getLineNumber(),
                        orderManagementLineToDelete.getItemCode(), orderManagementLineToDelete.getProposedStorageBin(),
                        orderManagementLineToDelete.getProposedPackBarCode(), loginUserID);
                log.info("Deleted the other orderManagementLine : " + orderManagementLineToDelete);
            }
        }

        Long OB_ORD_TYP_ID = dbOrderManagementLine.getOutboundOrderTypeId();
        Double ORD_QTY = dbOrderManagementLine.getOrderQty();
        Long BIN_CLASS_ID;
        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
            BIN_CLASS_ID = 1L;
            dbOrderManagementLine = updateAllocationV2(dbOrderManagementLine, BIN_CLASS_ID, ORD_QTY, warehouseId,
                    itemCode, loginUserID);
        }

        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
            BIN_CLASS_ID = 7L;
            dbOrderManagementLine = updateAllocationV2(dbOrderManagementLine, BIN_CLASS_ID, ORD_QTY, warehouseId,
                    itemCode, loginUserID);

        }
        dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
        dbOrderManagementLine.setPickupUpdatedOn(new Date());
        OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
        log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
        return updatedOrderManagementLine;
    }


    /**
     * @param assignPickers
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> doAssignPickerV2(List<AssignPickerV2> assignPickers, String assignedPickerId,
			String loginUserID) throws Exception {
		try {
			log.info("PickupHeader Create Initiated : ---> " + assignPickers.size());
			String companyCodeId = null;
			String plantId = null;
			String languageId = null;
			String warehouseId = null;
			String preOutboundNo = null;
			String refDocNumber = null;
			String partnerCode = null;
			Long lineNumber = null;
			String itemCode = null;
			String proposedStorageBin = null;
			String proposedPackCode = null;
			
			// push Notification
//			Set<String> preOutboundNoList = new HashSet<>();
//			Set<String> warehouseIdList = new HashSet<>();
			String notificationPreOutboundNo = null;
			String notificationWarehouseId = null;
			List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
			List<PickupHeaderV2> pickupHeaders = new ArrayList<>();
//			List<AssignPickerV2> sortedList = assignPickers.stream()
//					.sorted(Comparator.comparing(AssignPickerV2::getPreOutboundNo)).collect(Collectors.toList());
			AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();
			statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
			
			long NUM_RAN_CODE = 10;
			
			// Iterating over AssignPicker
			for (AssignPickerV2 assignPicker : assignPickers) {
				companyCodeId = assignPicker.getCompanyCodeId();
				plantId = assignPicker.getPlantId();
				languageId = assignPicker.getLanguageId();
				warehouseId = assignPicker.getWarehouseId();
				preOutboundNo = assignPicker.getPreOutboundNo();
				refDocNumber = assignPicker.getRefDocNumber();
				partnerCode = assignPicker.getPartnerCode();
				lineNumber = assignPicker.getLineNumber();
				itemCode = assignPicker.getItemCode();
				proposedStorageBin = assignPicker.getProposedStorageBin();
				proposedPackCode = assignPicker.getProposedPackCode();
				preOutboundNo = assignPicker.getPreOutboundNo();
				warehouseId = assignPicker.getWarehouseId();
				
				// push notification
//				preOutboundNoList.add(assignPicker.getPreOutboundNo());
//				warehouseIdList.add(assignPicker.getWarehouseId());

				/**
				 * Check for duplicates
				 */
				PickupHeaderV2 dupPickupHeader = pickupHeaderV2Repository
						.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
								companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
								partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, 0L);
				log.info("duplicatePickUpHeader: " + dupPickupHeader);

				if (dupPickupHeader == null) {
					OrderManagementLineV2 dbOrderManagementLine = getOrderManagementLineV2(companyCodeId, plantId,
							languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
							proposedStorageBin, proposedPackCode);
					log.info("orderManagementLine: " + dbOrderManagementLine);

					/*
					 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
					 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
					 * OUTBOUNDLINE table and update SATATU_ID as 48
					 */
					// // OutboundHeader Update
					OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId,
							languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);

					// Create Pickup TO Number
					/*
					 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITM_CODE/OBLINE_NO
					 * and validate PU_NO is Null in ORDERMANAGEMENTLINE table , If yes
					 *
					 * Create New PU_NO by Pass WH_ID - Userlogged in WH_ID and NUM_RAN_CODE = 10 in
					 * NUMBERRANGE table and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR
					 * and add +1 and then update in ORDERMANAGEMENTLINE table by passing
					 * WH_ID/PRE_OB_NO/OB_LINE_NO/REF_DOC_NO/ITM_CODE
					 */
					log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber());
					if (dbOrderManagementLine.getPickupNumber() == null) {
						String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getCompanyCodeId(),
								dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
								dbOrderManagementLine.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
						log.info("PU_NO : " + PU_NO);

						// Insertion of Record in PICKUPHEADER tables
						PickupHeaderV2 pickupHeader = new PickupHeaderV2();
						BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader,
								CommonUtils.getNullPropertyNames(dbOrderManagementLine));

						pickupHeader.setAssignedPickerId(assignedPickerId);
						// PU_NO
						pickupHeader.setPickupNumber(PU_NO);

						// PICK_TO_QTY
						pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());

						// PICK_UOM
						pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());

						// STATUS_ID
						pickupHeader.setStatusId(48L);
						// pickupHeader.setReferenceField7(idStatus.getStatus());
						pickupHeader.setStatusDescription(statusDescription);

						// ProposedPackbarcode
						pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());
						pickupHeader.setTransferRequestType(outboundHeader.getTransferRequestType());
						pickupHeader.setPickupCreatedBy(loginUserID);
						pickupHeader.setPickupCreatedOn(new Date());

						// customerName
						if (outboundHeader.getCustomerCode() != null) {
							String customerName = getCustomerName(pickupHeader.getCompanyCodeId(),
									pickupHeader.getPlantId(), pickupHeader.getLanguageId(),
									pickupHeader.getWarehouseId(), outboundHeader.getCustomerCode());
							if (customerName != null) {
								pickupHeader.setCustomerName(customerName);
							}
						}
						pickupHeader.setCustomerCode(outboundHeader.getCustomerCode());
						pickupHeader.setIsPickupHeaderCreated(0l);

						// REF_FIELD_1
						pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
//						PickupHeaderV2 pickup = pickupHeaderV2Repository.save(pickupHeader);
						pickupHeaders.add(pickupHeader);
						log.info("-----PickupHeader b4 create----->: " + pickupHeader);
					}
					orderManagementLineList.add(dbOrderManagementLine);
				}
			} // end of for
			
			if (pickupHeaders != null && pickupHeaders.size() > 0) {
				List<PickupHeaderV2> pickupHeaderList = pickupHeaderV2Repository.saveAll(pickupHeaders);
				log.info("-----PickupHeader create----->: " + pickupHeaderList.size());
				
				pickupHeaderList.stream().forEach(ph -> {
					orderManagementLineV2Repository.updateOrderManagementLineV2(ph.getCompanyCodeId(), ph.getPlantId(), ph.getLanguageId(),
							ph.getWarehouseId(), ph.getPreOutboundNo(), ph.getRefDocNumber(), ph.getPartnerCode(), ph.getLineNumber(), ph.getItemCode(), 48L,
							statusDescription, assignedPickerId, ph.getPickupNumber(), loginUserID, ph.getProposedStorageBin(), new Date());
				});	
				
				// 26_02_2025_update uniqueOrderManagementLine stBin added
				log.info("OrderManagementLine updated..! ");
				
				if (notificationPreOutboundNo == null) {
					notificationPreOutboundNo = preOutboundNo;
					notificationWarehouseId = warehouseId;
					log.info("---1---Sending Push Notification---> " + notificationPreOutboundNo);
					sendPushNotification(notificationPreOutboundNo, notificationWarehouseId);
				}
				boolean pass = notificationPreOutboundNo != null && notificationPreOutboundNo.equalsIgnoreCase(preOutboundNo);
				if (!pass) {
					log.info("-----2----Sending Push Notification---> " + notificationPreOutboundNo);
					notificationPreOutboundNo = preOutboundNo;
					notificationWarehouseId = warehouseId;
					sendPushNotification(notificationPreOutboundNo, notificationWarehouseId);
				}
			}
			
			// push notification separated from pickup header and consolidated notification
			// sent
//        if(preOutboundNoList != null && !preOutboundNoList.isEmpty() && warehouseIdList != null && !warehouseIdList.isEmpty()) {
//            sendPushNotification(preOutboundNoList, warehouseIdList);
//            sendPushNotification(pickupHeaders);
//        } else {
//            sendPushNotification();
//        }
			return orderManagementLineList;
		} catch (Exception e) {
			log.info("Exception while PickupHeader Create : " + e.getMessage());
			throw e;
		}
	}

    /**
     * update outbound header, line and order management header post pickup header creation
     */
    //@Scheduled(fixedDelay = 5000)
    private void postPickupHeaderStatusUpdateHeaderLine() {
        PickupHeaderV2 dbPickupHeader = pickupHeaderService.getPickupHeaderV2();
        log.info("PickupHeader create status update Initiated ---> " + dbPickupHeader);
        if (dbPickupHeader != null) {
            String companyCodeId = dbPickupHeader.getCompanyCodeId();
            String plantId = dbPickupHeader.getPlantId();
            String languageId = dbPickupHeader.getLanguageId();
            String warehouseId = dbPickupHeader.getWarehouseId();
            String preOutboundNo = dbPickupHeader.getPreOutboundNo();
            String refDocNumber = dbPickupHeader.getRefDocNumber();
            String partnerCode = dbPickupHeader.getPartnerCode();
            Long lineNumber = dbPickupHeader.getLineNumber();
            String itemCode = dbPickupHeader.getItemCode();
            String assignedPickerId = dbPickupHeader.getAssignedPickerId();
            String pickupNumber = dbPickupHeader.getPickupNumber();
            Long STATUS_ID = 48l;

            try {
                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);

                outboundLineV2Repository.updateOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, STATUS_ID, statusDescription, assignedPickerId);
                log.info("outboundLine updated..! ");

                outboundHeaderV2Repository.updateOutboundHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId,
                        refDocNumber, preOutboundNo, STATUS_ID, statusDescription);
                log.info("outboundHeader updated..! ");

                orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId,
                        refDocNumber, preOutboundNo, STATUS_ID, statusDescription);
                log.info("orderManagementHeader updated..! ");

                //update pickupheader status
                pickupHeaderV2Repository.updatePickupHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, pickupNumber,
                        lineNumber, itemCode, dbPickupHeader.getProposedStorageBin(), dbPickupHeader.getProposedPackBarCode(), 10l);
                log.info("post PickupHeader create status updated successfully...!");

            } catch (Exception e) {
                log.error("Exception while create pickupheader status update : " + e.getMessage());
            }
        }
    }

    /**
     * send Push Notification
     */
    public void sendPushNotification() {
        try {
                        List<IKeyValuePair> notification =
                                pickupHeaderV2Repository.findByStatusIdAndNotificationStatusAndDeletionIndicatorDistinctRefDocNo();

                        if (notification != null) {
                            for (IKeyValuePair pickupHeaderV2 : notification) {

                                List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                                        pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getWarehouseId());

                                if (deviceToken != null && !deviceToken.isEmpty()) {
                                    String title = "PICKING";
                                    String message =  pickupHeaderV2.getRefDocType() + " ORDER - " + pickupHeaderV2.getRefDocNumber() + " - IS RECEIVED ";
                                    String response = pushNotificationService.sendPushNotification(deviceToken, title, message);
                                    if (response.equals("OK")) {
                                        pickupHeaderV2Repository.updateNotificationStatus(
                                                pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getRefDocNumber(), pickupHeaderV2.getWarehouseId());
                                        log.info("status update successfully");
                                    }
                                }
                            }
                        }
        } catch (Exception e) {
//            e.printStackTrace();
                    }
                }

    /**
     *
     * @param preOutboundNo
     * @param warehouseId
     */
    public void sendPushNotification(String preOutboundNo, String warehouseId) {
        try {
            List<IKeyValuePair> notification =
                    pickupHeaderV2Repository.findPushNotificationStatusByPreOutboundNo(preOutboundNo, warehouseId);

            if (notification != null) {
                for (IKeyValuePair pickupHeaderV2 : notification) {

                    List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                            pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getWarehouseId());

                    if (deviceToken != null && !deviceToken.isEmpty()) {
                        String title = "PICKING";
                        String message = pickupHeaderV2.getRefDocType() + " ORDER - " + pickupHeaderV2.getRefDocNumber() + " - IS RECEIVED ";
                        String response = pushNotificationService.sendPushNotification(deviceToken, title, message);
                        if (response.equals("OK")) {
                            pickupHeaderV2Repository.updateNotificationStatus(
                                    pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getRefDocNumber(), pickupHeaderV2.getWarehouseId());
                            log.info("status update successfully");
            }
            }
        }
    }
        } catch (Exception e) {
//            e.printStackTrace();
            }
    }

    /**
     *
     * @param pickupHeaders
     */
    public void sendPushNotification(List<PickupHeaderV2> pickupHeaders) {
        try {
            for (PickupHeaderV2 pickupHeaderV2 : pickupHeaders) {
                List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                        pickupHeaderV2.getAssignedPickerId(), pickupHeaderV2.getWarehouseId());
                if (deviceToken != null && !deviceToken.isEmpty()) {
                    String title = "PICKING";
                    String message = pickupHeaderV2.getReferenceDocumentType() + " ORDER - " + pickupHeaderV2.getRefDocNumber() + " - IS RECEIVED ";
                    String response = pushNotificationService.sendPushNotification(deviceToken, title, message);
                    if (response.equals("OK")) {
                        pickupHeaderV2Repository.updateNotificationStatus(
                                pickupHeaderV2.getAssignedPickerId(), pickupHeaderV2.getRefDocNumber(), pickupHeaderV2.getWarehouseId());
                        log.info("status update successfully");
                    }
                }
            }
        } catch (Exception e) {
            log.info("Push Notification pickupheader create exception : " + e.toString());
        }
    }

    /**
     *
     * @param preOutboundNo
     * @param warehouseId
     */
    public void sendPushNotification(Set<String> preOutboundNo, Set<String> warehouseId) {
        try {
            List<IKeyValuePair> notification = null;
            if (preOutboundNo != null && !preOutboundNo.isEmpty() &&
                    warehouseId != null && !warehouseId.isEmpty()) {
                List<String> preOutboundList = new ArrayList<>(preOutboundNo);
                List<String> warehouseIdList = new ArrayList<>(warehouseId);

                notification = pickupHeaderV2Repository.findPushNotificationStatusByPreOutboundNo(preOutboundList, warehouseIdList);
    }

            if (notification != null) {
                for (IKeyValuePair pickupHeaderV2 : notification) {

                    List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                            pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getWarehouseId());

                    if (deviceToken != null && !deviceToken.isEmpty()) {
                        String title = "PICKING";
                        String message = pickupHeaderV2.getRefDocType() + " ORDER - " + pickupHeaderV2.getRefDocNumber() + " - IS RECEIVED ";
                        String response = pushNotificationService.sendPushNotification(deviceToken, title, message);
                        if (response.equals("OK")) {
                            pickupHeaderV2Repository.updateNotificationStatus(
                                    pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getRefDocNumber(), pickupHeaderV2.getWarehouseId());
                            log.info("status update successfully");
                        }
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
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
//    public OrderManagementLineV2 updateAllocationV2(OrderManagementLineV2 orderManagementLine, Long binClassId,
//                                                    Double ORD_QTY, String warehouseId, String itemCode, String loginUserID) throws java.text.ParseException {
//        // Inventory Strategy Choices
//        String INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
//        log.info("Allocation Strategy: " + INV_STRATEGY);
//
//        List<IInventoryImpl> stockType1InventoryList =
//                inventoryService.getInventoryForOrderManagementV2(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
//                        warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
//        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//        if (stockType1InventoryList.isEmpty()) {
//            return updateOrderManagementLineV2(orderManagementLine);
//        }
//
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//
//        // -----------------------------------------------------------------------------------------------------------------------------------------
//        // Getting Inventory GroupBy ST_BIN wise
//
//        List<IInventoryImpl> finalInventoryList = null;
//        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByCtdOnV2(orderManagementLine.getCompanyCodeId(),
//                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
//                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
//            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV2(orderManagementLine.getCompanyCodeId(),
//                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
//                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
//        }
//        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");
//
//        ImBasicData1 dbImBasicData1 = null;
//        boolean shelfLifeIndicator = false;
//        if (finalInventoryList != null && !finalInventoryList.isEmpty()) {
//
//            ImBasicData imBasicData = new ImBasicData();
//            imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
//            imBasicData.setPlantId(orderManagementLine.getPlantId());
//            imBasicData.setLanguageId(orderManagementLine.getLanguageId());
//            imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
//            imBasicData.setItemCode(itemCode);
//            imBasicData.setManufacturerName(orderManagementLine.getManufacturerName());
//            dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
//
////            dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(itemCode,
////                    orderManagementLine.getLanguageId(), orderManagementLine.getCompanyCodeId(),
////                    orderManagementLine.getPlantId(), orderManagementLine.getWarehouseId(),
////                    orderManagementLine.getManufacturerName(), authTokenForMastersService.getAccess_token());
//
//            log.info("ImBasicData1: " + dbImBasicData1);
//            if(dbImBasicData1 != null) {
//                if (dbImBasicData1.getShelfLifeIndicator() != null) {
//                    shelfLifeIndicator = dbImBasicData1.getShelfLifeIndicator();
//                }
//            }
//        }
//
//        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
//            return updateOrderManagementLineV2(orderManagementLine);
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            //ascending sort - expiryDate
//            if (shelfLifeIndicator) {
//                finalInventoryList.stream().sorted(Comparator.comparing(IInventoryImpl::getExpiryDate)).collect(Collectors.toList());
//            }
//            //ascending sort - created on
//            if (!shelfLifeIndicator) {
//                finalInventoryList.stream().sorted(Comparator.comparing(IInventoryImpl::getCreatedOn)).collect(Collectors.toList());
//            }
//        }
//        OrderManagementLineV2 newOrderManagementLine = null;
//
//        outerloop:
//        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
//            // Getting PackBarCode by passing ST_BIN to Inventory
//            List<IInventoryImpl> listInventoryForAlloc = null;
//            if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//                listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
//                        orderManagementLine.getManufacturerName(), binClassId,
//                        stBinWiseInventory.getStorageBin(), 1L);
//            }
//            if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
//                listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2OrderByLevelId(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
//                        orderManagementLine.getManufacturerName(), binClassId,
//                        stBinWiseInventory.getStorageBin(), 1L);
//            }
//            log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc.size() + "\n");
//
//            // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
//            // created.
//            if (listInventoryForAlloc != null && listInventoryForAlloc.isEmpty()) {
//                return updateOrderManagementLineV2(orderManagementLine);
//            }
//            if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//                //ascending sort - expiryDate
//                if (shelfLifeIndicator) {
//                    listInventoryForAlloc.stream().sorted(Comparator.comparing(IInventoryImpl::getExpiryDate)).collect(Collectors.toList());
//                }
//                //ascending sort - created on
//                if (!shelfLifeIndicator) {
//                    listInventoryForAlloc.stream().sorted(Comparator.comparing(IInventoryImpl::getCreatedOn)).collect(Collectors.toList());
//                }
//            }
//            for (IInventoryImpl stBinInventory : listInventoryForAlloc) {
//                log.info("\nBin-wise Inventory : " + stBinInventory + "\n");
//
//                Long STATUS_ID = 0L;
//                Double ALLOC_QTY = 0D;
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
//                        orderManagementLineV2Repository.delete(orderManagementLine);
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
////                StatusId idStatus = idmasterService.getStatus(STATUS_ID, orderManagementLine.getWarehouseId(), idmasterAuthToken.getAccess_token());
//                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
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
//                BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine,
//                        CommonUtils.getNullPropertyNames(orderManagementLine));
//
//                //V2 Code
//                IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getLanguageId(),
//                        orderManagementLine.getPlantId(),
//                        orderManagementLine.getWarehouseId());
//
//                newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
//                newOrderManagementLine.setPlantDescription(description.getPlantDesc());
//                newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
//
//                newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
//                if (stBinInventory.getBarcodeId() != null) {
//                    newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
//                }
//                if (stBinInventory.getLevelId() != null) {
//                    newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
//                }
//                newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
//                OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
//                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
//                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();
//
//                if (ORD_QTY > ALLOC_QTY) {
//                    ORD_QTY = ORD_QTY - ALLOC_QTY;
//                }
//
//                if (allocatedQtyFromOrderMgmt > 0) {
//                    // Update Inventory table
//                    InventoryV2 inventoryForUpdate = inventoryService.getInventoryForAllocationV2(orderManagementLine.getCompanyCodeId(),
//                            orderManagementLine.getPlantId(),
//                            orderManagementLine.getLanguageId(), warehouseId,
//                            stBinInventory.getPackBarcodes(), itemCode,
//                            orderManagementLine.getManufacturerName(),
//                            stBinInventory.getStorageBin());
//
//                    double dbInventoryQty = 0;
//                    double dbInvAllocatedQty = 0;
//
//                    if (inventoryForUpdate.getInventoryQuantity() != null) {
//                        dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
//                    }
//
//                    if (inventoryForUpdate.getAllocatedQuantity() != null) {
//                        dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
//                    }
//
//                    double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
//                    double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;
//
//                    /*
//                     * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
//                     */
//                    // Start
//                    if (inventoryQty < 0) {
//                        inventoryQty = 0;
//                    }
//                    // End
//                    inventoryForUpdate.setInventoryQuantity(inventoryQty);
//                    inventoryForUpdate.setAllocatedQuantity(allocatedQty);
//                    inventoryForUpdate.setReferenceField4(inventoryQty + allocatedQty);
////                    inventoryForUpdate = inventoryV2Repository.save(inventoryForUpdate);
////                    log.info("inventoryForUpdate updated: " + inventoryForUpdate);
//                    // Create new Inventory Record
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, CommonUtils.getNullPropertyNames(inventoryForUpdate));
//                    inventoryV2.setUpdatedOn(new Date());
//                    inventoryV2.setInventoryId(System.currentTimeMillis());
//                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
//                    log.info("-----Inventory2 updated-------: " + inventoryV2);
//                }
//
//                if (ORD_QTY == ALLOC_QTY) {
//                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
//                    break outerloop; // If the Inventory satisfied the Ord_qty
//                }
//            }
//        }
//        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//        return newOrderManagementLine;
//    }

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
                                                    Double ORD_QTY, String warehouseId, String itemCode, String loginUserID) throws java.text.ParseException {
        // Inventory Strategy Choices
        String INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        log.info("Allocation Strategy: " + INV_STRATEGY);
        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        double actualOrderQty = ORD_QTY;                    //26_02_2025_partial allocation in different bin bugFix
        double actualAllocatedQty = 0;                      //26_02_2025_partial allocation in different bin bugFix
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
//                        Long LEVEL_ID = 1L;                                     //Default level - Hard Code
//                        if(stBinWiseInventory.getLevelId() != null) {
//                            LEVEL_ID = Long.valueOf(stBinWiseInventory.getLevelId());
//                        }
//                        log.info("LEVEL_ID: " + LEVEL_ID);
//                        // Getting PackBarCode by passing ST_BIN to Inventory
//                        List<IInventoryImpl> listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2GroupByLevelId(orderManagementLine.getCompanyCodeId(),
//                                    orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
//                                    orderManagementLine.getManufacturerName(), binClassId, LEVEL_ID,
//                                    stBinWiseInventory.getStorageBin(), 1L);
//
//                        log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc.size() + "\n");
//
//                        // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
//                        // created.
//                        if (listInventoryForAlloc != null && listInventoryForAlloc.isEmpty()) {
//                            return updateOrderManagementLineV2(orderManagementLine);
//                        }
//
//                        for (IInventoryImpl stBinInventory : listInventoryForAlloc) {
//                            log.info("\nBin-wise Inventory : " + stBinInventory + "\n");

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
                            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                            log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                            allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                            if (ORD_QTY > ALLOC_QTY) {
                                ORD_QTY = ORD_QTY - ALLOC_QTY;
                                actualAllocatedQty = actualAllocatedQty + ALLOC_QTY;        //26_02_2025_partial allocation in different bin bugFix
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
//                            if (ORD_QTY == ALLOC_QTY) {
                            if (actualOrderQty == actualAllocatedQty) {         //26_02_2025_partial allocation in different bin bugFix
                                log.info("ORD_QTY fully allocated: " + actualAllocatedQty);
                                break outerloop1; // If the Inventory satisfied the Ord_qty
                            }
//                        }
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
            if(dbImBasicData1 != null) {
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
            // Getting PackBarCode by passing ST_BIN to Inventory
//            List<IInventoryImpl> listInventoryForAlloc = null;
//            if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//                listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
//                        orderManagementLine.getManufacturerName(), binClassId,
//                        stBinWiseInventory.getStorageBin(), 1L);
//            }
//            if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID") || INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_LEVEL_ID
//                listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2OrderByLevelId(orderManagementLine.getCompanyCodeId(),
//                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
//                            orderManagementLine.getManufacturerName(), binClassId,
//                            stBinWiseInventory.getStorageBin(), 1L);
//            }
//            log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc.size() + "\n");
//
//            // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
//            // created.
//            if (listInventoryForAlloc != null && listInventoryForAlloc.isEmpty()) {
//                return updateOrderManagementLineV2(orderManagementLine);
//            }
//            if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            //ascending sort - expiryDate
//            if (shelfLifeIndicator) {
//                listInventoryForAlloc.stream().sorted(Comparator.comparing(IInventoryImpl::getExpiryDate)).collect(Collectors.toList());
//            }
//            //ascending sort - created on
//            if (!shelfLifeIndicator) {
//                listInventoryForAlloc.stream().sorted(Comparator.comparing(IInventoryImpl::getCreatedOn)).collect(Collectors.toList());
//            }
//            }
//            for (IInventoryImpl stBinInventory : listInventoryForAlloc) {
//                log.info("\nBin-wise Inventory : " + stBinInventory + "\n");

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
                OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                if (ORD_QTY > ALLOC_QTY) {
                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                    actualAllocatedQty = actualAllocatedQty + ALLOC_QTY;            //26_02_2025_partial allocation in different bin bugFix
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
//                    inventoryForUpdate = inventoryV2Repository.save(inventoryForUpdate);
//                    log.info("inventoryForUpdate updated: " + inventoryForUpdate);
                    // Create new Inventory Record
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventoryForUpdate, inventoryV2, CommonUtils.getNullPropertyNames(inventoryForUpdate));
                    inventoryV2.setUpdatedOn(new Date());
                    inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 2));
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----Inventory2 updated-------: " + inventoryV2);
                }

//                if (ORD_QTY == ALLOC_QTY) {
                if (actualOrderQty == actualAllocatedQty) {                     //26_02_2025_partial allocation in different bin bugFix
                    log.info("ORD_QTY fully allocated: " + actualOrderQty);
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
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updateOrderManagementLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementLineV2 updateOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                             String itemCode, String loginUserID, OrderManagementLineV2 updateOrderManagementLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<OrderManagementLineV2> dbOrderManagementLines = getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        for (OrderManagementLineV2 dbOrderManagementLine : dbOrderManagementLines) {
            BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine,
                    CommonUtils.getNullPropertyNames(updateOrderManagementLine));
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            return orderManagementLineV2Repository.save(dbOrderManagementLine);
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @param loginUserID
     * @param updateOrderMangementLine
     * @return
     */
    public OrderManagementLineV2 updateOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                             String itemCode, String proposedStorageBin, String proposedPackCode,
                                                             String loginUserID, @Valid OrderManagementLineV2 updateOrderMangementLine) throws java.text.ParseException {
        OrderManagementLineV2 dbOrderManagementLine = getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
        if (dbOrderManagementLine != null) {
            BeanUtils.copyProperties(updateOrderMangementLine, dbOrderManagementLine,
                    CommonUtils.getNullPropertyNames(updateOrderMangementLine));
            if (updateOrderMangementLine.getPickupNumber() == null) {
                dbOrderManagementLine.setPickupNumber(null);
            }
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            return orderManagementLineV2Repository.save(dbOrderManagementLine);
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackCode
     * @param loginUserID
     */
    public void deleteOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                            String itemCode, String proposedStorageBin, String proposedPackCode, String loginUserID) throws java.text.ParseException {
        OrderManagementLineV2 orderManagementHeader = getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
        if (orderManagementHeader != null) {
            orderManagementHeader.setDeletionIndicator(1L);
            orderManagementHeader.setPickupUpdatedBy(loginUserID);
            orderManagementHeader.setPickupUpdatedOn(new Date());
            orderManagementLineV2Repository.save(orderManagementHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                                       String warehouseId, String refDocNumber){
        List<OrderManagementLineV2> orderManagementLineList =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLineList);
        return orderManagementLineList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //Delete OrderManagementLine
    public List<OrderManagementLineV2> deleteOrderManagementLineV2(String companyCodeId, String plantId, String languageId,
                                                                   String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) throws Exception{

        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
        List<OrderManagementLineV2> orderManagementLine =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLine);

        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            for (OrderManagementLineV2 orderManagementLineV2 : orderManagementLine) {
                orderManagementLineV2.setDeletionIndicator(1L);
                orderManagementLineV2.setPickupUpdatedBy(loginUserID);
                orderManagementLineV2.setPickupUpdatedOn(new Date());
                OrderManagementLineV2 dbOrderManagementLine = orderManagementLineV2Repository.save(orderManagementLineV2);
                orderManagementLineList.add(dbOrderManagementLine);
            }
        }
        return orderManagementLineList;
    }

    /**
     *  Pick List cancel
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getPLCOrderManagementLineV2(String companyCodeId, String plantId, String languageId,
                                                                   String warehouseId, String refDocNumber, String preOutboundNo) {
        List<OrderManagementLineV2> orderManagementLine =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLine);
        return orderManagementLine;
    }
/**
     *
     * @param searchOrderManagementLine
     * @return
     * @throws Exception
     */
    public List<OrderManagementLineImpl> findOrderManagementLinesV2(SearchOrderManagementLineV2 searchOrderManagementLine) throws Exception {

        if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null
                && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(),
                    searchOrderManagementLine.getEndRequiredDeliveryDate());
            searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
            searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchOrderManagementLine.getStartOrderDate() != null
                && searchOrderManagementLine.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(),
                    searchOrderManagementLine.getEndOrderDate());
            searchOrderManagementLine.setStartOrderDate(dates[0]);
            searchOrderManagementLine.setEndOrderDate(dates[1]);
        }

        if(searchOrderManagementLine.getCompanyCodeId() != null && searchOrderManagementLine.getCompanyCodeId().isEmpty()) {
            searchOrderManagementLine.setCompanyCodeId(null);
        }
        if(searchOrderManagementLine.getPlantId() != null && searchOrderManagementLine.getPlantId().isEmpty()) {
            searchOrderManagementLine.setPlantId(null);
        }
        if(searchOrderManagementLine.getLanguageId() != null && searchOrderManagementLine.getLanguageId().isEmpty()) {
            searchOrderManagementLine.setLanguageId(null);
        }
        if(searchOrderManagementLine.getWarehouseId() != null && searchOrderManagementLine.getWarehouseId().isEmpty()) {
            searchOrderManagementLine.setWarehouseId(null);
        }
        if(searchOrderManagementLine.getRefDocNumber() != null && searchOrderManagementLine.getRefDocNumber().isEmpty()) {
            searchOrderManagementLine.setRefDocNumber(null);
        }
        if(searchOrderManagementLine.getPreOutboundNo() != null && searchOrderManagementLine.getPreOutboundNo().isEmpty()) {
            searchOrderManagementLine.setPreOutboundNo(null);
        }
        if(searchOrderManagementLine.getPartnerCode() != null && searchOrderManagementLine.getPartnerCode().isEmpty()) {
            searchOrderManagementLine.setPartnerCode(null);
        }
        if(searchOrderManagementLine.getItemCode() != null && searchOrderManagementLine.getItemCode().isEmpty()) {
            searchOrderManagementLine.setItemCode(null);
        }
        if(searchOrderManagementLine.getManufacturerName() != null && searchOrderManagementLine.getManufacturerName().isEmpty()) {
            searchOrderManagementLine.setManufacturerName(null);
        }
        if(searchOrderManagementLine.getOutboundOrderTypeId() != null && searchOrderManagementLine.getOutboundOrderTypeId().isEmpty()) {
            searchOrderManagementLine.setOutboundOrderTypeId(null);
        }
        if(searchOrderManagementLine.getSoType() != null && searchOrderManagementLine.getSoType().isEmpty()) {
            searchOrderManagementLine.setSoType(null);
        }
        if(searchOrderManagementLine.getStatusId() != null && searchOrderManagementLine.getStatusId().isEmpty()) {
            searchOrderManagementLine.setStatusId(null);
        }
        if(searchOrderManagementLine.getDescription() != null && searchOrderManagementLine.getDescription().isEmpty()) {
            searchOrderManagementLine.setDescription(null);
        }

        log.info("Assignment Tab searchOrderManagementLine Input: " + searchOrderManagementLine);
        return orderManagementLineV2Repository.findOrderManagementLine(searchOrderManagementLine.getCompanyCodeId(),
                searchOrderManagementLine.getPlantId(), searchOrderManagementLine.getLanguageId(), searchOrderManagementLine.getWarehouseId(),
                searchOrderManagementLine.getRefDocNumber(), searchOrderManagementLine.getPreOutboundNo(), searchOrderManagementLine.getPartnerCode(),
                searchOrderManagementLine.getItemCode(), searchOrderManagementLine.getManufacturerName(), searchOrderManagementLine.getDescription(),
                searchOrderManagementLine.getOutboundOrderTypeId(), searchOrderManagementLine.getStatusId(), searchOrderManagementLine.getSoType(),
                searchOrderManagementLine.getStartRequiredDeliveryDate(), searchOrderManagementLine.getEndRequiredDeliveryDate(),
                searchOrderManagementLine.getStartOrderDate(), searchOrderManagementLine.getEndOrderDate());
    }
}