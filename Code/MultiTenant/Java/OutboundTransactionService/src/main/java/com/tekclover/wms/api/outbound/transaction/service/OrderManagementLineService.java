package com.tekclover.wms.api.outbound.transaction.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tekclover.wms.api.outbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.outbound.transaction.model.dto.*;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.*;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.*;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.outbound.transaction.repository.*;
import com.tekclover.wms.api.outbound.transaction.repository.specification.OrderManagementLineSpecification;
import com.tekclover.wms.api.outbound.transaction.repository.specification.OrderManagementLineV2Specification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderManagementLineService extends BaseService {
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private ImBasicData1Repository imBasicData1Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PickupLineRepository pickupLineRepository;

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
    private OrderManagementLineService orderManagementLineService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    PickupHeaderService pickupHeaderService;

    @Autowired
    InventoryTransRepository inventoryTransRepository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    StorageBinRepository storageBinRepository;

    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

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
//        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
//        StatusId idStatus = idmasterService.getStatus(47L, warehouseId, idmasterAuthToken.getAccess_token());

        String idStatus = orderManagementLineV2Repository.getStatusDesc(47L);

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
            dbOrderManagementLine.setReferenceField7(idStatus);
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
    public OrderManagementLineV2 getOrderManagementLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
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
    public OrderManagementLineV2 getOrderManagementLineForLineUpdate(String companyCodeId, String plantId, String languageId, String warehouseId,
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
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateV5(String companyCodeId, String plantId, String languageId, String warehouseId,
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
     * Modified for Knowell JPA Query to Native Query
     * Aakash Vinayak - 03/07/2025
     *
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
    public List<OrderManagementLineV2> getListOrderManagementLineV7(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                    String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository
                .findAllOrderManagementLine(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
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

                log.info("Delete dbOrderManagementLine barcodeId started....");
                orderManagementLineV2Repository.deleteOrderManagementBarcodeId(companyCodeId, plantId, warehouseId, itemCode, manufacturerName, statusIdList, dbOrderManagementLine.getPreOutboundNo());

                orderManagementLineV2Repository.save(dbOrderManagementLine);
                log.info("dbOrderManagementLine -----> {}", dbOrderManagementLine);
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

    //Streaming For Fahaheel
    public List<OrderManagementLineV2> findOrderManagementLineFahaheelV2(SearchOrderManagementLineV2 searchOrderManagementLine)
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
        List<OrderManagementLineV2> searchResults = orderManagementLineV2Repository.findAll(spec);

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

        IKeyValuePair description = pickupLineRepository.getDescription(dbOrderManagementLine.getCompanyCodeId(),
                dbOrderManagementLine.getLanguageId(),
                dbOrderManagementLine.getPlantId(),
                dbOrderManagementLine.getWarehouseId());

        if (dbOrderManagementLine.getStatusId() != null) {
            statusDescription = pickupLineRepository.getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
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
                    try {
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        log.info("-----InventoryV2 created-------: " + inventoryV2);
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
            statusDescription = pickupLineRepository.getStatusDescription(47L, lineV2.getLanguageId());

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
                try {
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----InventoryV2 created-------: " + inventoryV2);
                } catch (Exception e) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                    e.printStackTrace();
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }

                /*
                 * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
                 */
                dbOrderManagementLine.setAllocatedQty(0D);
                dbOrderManagementLine.setStatusId(47L);
//            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
                dbOrderManagementLine.setReferenceField7(statusDescription);
                dbOrderManagementLine.setStatusDescription(statusDescription);
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

        statusDescription = pickupLineRepository.getStatusDescription(47L, languageId);

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
            // Create new Inventory Record
            InventoryV2 inventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
            try {
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----InventoryV2 created-------: " + inventoryV2);
            } catch (Exception e) {
                log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                e.printStackTrace();
                InventoryTrans newInventoryTrans = new InventoryTrans();
                BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                newInventoryTrans.setReRun(0L);
                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
            }
            /*
             * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
             */
            dbOrderManagementLine.setAllocatedQty(0D);
            dbOrderManagementLine.setStatusId(47L);
//            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
            dbOrderManagementLine.setReferenceField7(statusDescription);
            dbOrderManagementLine.setStatusDescription(statusDescription);
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            if (i != 0) {
                dbOrderManagementLine.setDeletionIndicator(1L);
            }
            orderManagementLineV2Repository.delete(dbOrderManagementLine);  // deleting since duplicate error throws in MT
            OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
            i++;
        }
        return !orderManagementLineList.isEmpty() ? orderManagementLineList.get(0) : null;
    }

    /**
     * Modified for Knowell - PickupHeader should be deleted
     * 03/07/2025 - Aakash Vinayak
     *
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
    public OrderManagementLineV2 doUnAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                  String itemCode, String proposedStorageBin, String proposedPackBarCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        // HAREESH - 2022-10-01- Validate multiple ordermanagement lines
        List<OrderManagementLineV2> orderManagementLineList =
                getListOrderManagementLineV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + orderManagementLineList);

        /*
         * Update Inventory table -------------------------- Pass the
         * WH_ID/ITM_CODE/PACK_BARCODE(PROP_PACK_BARCODE)/ST_BIN(PROP_ST_BIN) values in
         * INVENTORY table update INV_QTY as (INV_QTY+ALLOC_QTY) and change ALLOC_QTY as
         * 0
         */
        int i = 0;

        statusDescription = pickupLineRepository.getStatusDescription(47L, languageId);

        OrderManagementLineV2 updatedOrderManagementLine = new OrderManagementLineV2();

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
            // Create new Inventory Record
            InventoryV2 inventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
            try {
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----InventoryV2 created-------: " + inventoryV2);
            } catch (Exception e) {
                log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                e.printStackTrace();
                InventoryTrans newInventoryTrans = new InventoryTrans();
                BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                newInventoryTrans.setReRun(0L);
                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
            }
            /*
             * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
             */
            dbOrderManagementLine.setAllocatedQty(0D);
            dbOrderManagementLine.setStatusId(47L);
//            dbOrderManagementLine.setReferenceField7(idStatus.getStatus());
            dbOrderManagementLine.setReferenceField7(statusDescription);
            dbOrderManagementLine.setStatusDescription(statusDescription);
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            if (i != 0) {
                dbOrderManagementLine.setDeletionIndicator(1L);
            }
            orderManagementLineV2Repository.delete(dbOrderManagementLine);  // deleting since duplicate error throws in MT
            updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);

            // PickupHeader deletion for unallocated orders - 03/07/2025
//            PickupHeaderV2 dbPickupHeader = pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndBarcodeIdAndDeletionIndicator(
//                    companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, dbOrderManagementLine.getBarcodeId(), 0L
//            );
//            log.info("PickupHeader for Deleting ------> {}", dbPickupHeader);
//
//            if (dbPickupHeader != null) {
//                pickupHeaderV2Repository.delete(dbPickupHeader);
//                log.warn("PickupHeader deletion completed...");
//            }

            i++;
        }
        return updatedOrderManagementLine;
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

//                for(OrderManagementLineV2 orderManagementLineToDelete : orderManagementLineV2) {
//                    deleteOrderManagementLineV2(orderManagementLineToDelete.getCompanyCodeId(),
//                            orderManagementLineToDelete.getPlantId(), orderManagementLineToDelete.getLanguageId(),
//                            orderManagementLineToDelete.getWarehouseId(),
//                            orderManagementLineToDelete.getPreOutboundNo(), orderManagementLineToDelete.getRefDocNumber(),
//                            orderManagementLineToDelete.getPartnerCode(), orderManagementLineToDelete.getLineNumber(),
//                            orderManagementLineToDelete.getItemCode(), orderManagementLineToDelete.getProposedStorageBin(),
//                            orderManagementLineToDelete.getProposedPackBarCode(), loginUserID);
//                    log.info("Deleted the other orderManagementLine : " + orderManagementLineToDelete);
////                }
//                }

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
            orderManagementLineV2Repository.delete(dbOrderManagementLine);      // // deleting since duplicate error throws in MT
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
        orderManagementLineV2Repository.delete(dbOrderManagementLine);  // delete and update for Fahaheel MT
        OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
        log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
        return updatedOrderManagementLine;
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
    public OrderManagementLineV2 doAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                String itemCode, String loginUserID) throws Exception {
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
            //companyCodeId, plantId, languageId, warehouseId, itemCode,
            //                orderManagementLine.getManufacturerName(), binClassId, ORD_QTY,
            //                orderManagementLine, loginUserId
            dbOrderManagementLine = updateAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    dbOrderManagementLine.getManufacturerName(), BIN_CLASS_ID, ORD_QTY,
                    dbOrderManagementLine, loginUserID);
        }

        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
            BIN_CLASS_ID = 7L;
            dbOrderManagementLine = updateAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    dbOrderManagementLine.getManufacturerName(), BIN_CLASS_ID, ORD_QTY,
                    dbOrderManagementLine, loginUserID);

        }
        dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
        dbOrderManagementLine.setPickupUpdatedOn(new Date());
        orderManagementLineV2Repository.delete(dbOrderManagementLine);  // delete and update for Fahaheel MT
        OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
        log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
        return updatedOrderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {

        log.info("Inventory Update Allocation Started ...........");

        String masterToken = getMasterAuthToken();
        String alternateUom = orderManagementLine.getAlternateUom();
        Long stockTypeId = 1L;
        String orderBy = null;
        String INV_STRATEGY = null;

        log.info("The Alternate UOM ------------------> {}", alternateUom);

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
        imBasicData.setPlantId(orderManagementLine.getPlantId());
        imBasicData.setLanguageId(orderManagementLine.getLanguageId());
        imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
        imBasicData.setItemCode(itemCode);
//        ImBatchSerial imBatchSerial = mastersService.getImBatchSerialV2(imBasicData, masterToken);

        // Inventory Strategy Choices
        if (INV_STRATEGY == null) {
            INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        }

        String MFR_PART = orderManagementLine.getManufacturerName();

        boolean shelfLifeIndicator = false;
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId,
                itemCode, MFR_PART, 0L);
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null) {
            if (imBasicData1.getShelfLifeIndicator() != null) {
                shelfLifeIndicator = imBasicData1.getShelfLifeIndicator();
            }
        }

        log.info("Allocation Strategy: " + INV_STRATEGY);
        log.info("shelfLifeIndicator: " + shelfLifeIndicator);

        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        List<IInventoryImpl> stockType1InventoryList =
                inventoryService.getInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        MFR_PART, stockTypeId, binClassId, alternateUom);
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());

        if (stockType1InventoryList == null || stockType1InventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        // Getting Inventory GroupBy ST_BIN wise
        List<IInventoryImpl> finalInventoryList = null;
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    stockTypeId, binClassId, MFR_PART, alternateUom);

            log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + companyCodeId + " plantId ----> " + plantId + " languageId ----> " + languageId +
                    ", warehouseId -----> " + warehouseId + "itemCode -----> " + itemCode + " refDocumentNo -----> " + orderManagementLine.getRefDocNumber() + " barcodeId -------> " + orderManagementLine.getBarcodeId());

            Double INV_QTY = inventoryV2Repository.getInvCaseQty2(companyCodeId, plantId, languageId, warehouseId);
            log.info("Queried invQty2 ----------> {}", INV_QTY);
            if (INV_QTY == null) {
                INV_QTY = 0.0;
            }
            log.info("Group By LeveId: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY_TOTAL : " + ORD_QTY + ", " + iInventory.getInventoryQty());

                log.info("Order Qty --------> {}", ORD_QTY);
                log.info("BagSize ------------> {}", orderManagementLine.getBagSize());
                log.info("INV_QTY queired 1 -------------> {}", INV_QTY);
                if (Objects.equals(ORD_QTY, INV_QTY)) {
                    log.info("Closed Case Allocation started !!");
                    newOrderManagementLine = fullQtyAllocationV7(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                    return newOrderManagementLine;
                } else if (Objects.equals(ORD_QTY, iInventory.getInventoryQty())) {
                    log.info("InventoryQty {}, OrderQty {} is equal ", iInventory.getInventoryQty(), ORD_QTY);
                    newOrderManagementLine = fullQtyAllocationV7(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                    return newOrderManagementLine;
                } else if (ORD_QTY < iInventory.getInventoryQty()) {
                    orderBy = "iv.LEVEL_ID";
                    finalInventoryList = inventoryService.getInventoryForOrderManagementLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom,
                            iInventory.getLevelId());
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, MFR_PART,
                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
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
                orderBy = "iv.LEVEL_ID";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        MFR_PART, stockTypeId, binClassId, alternateUom);
            }

        }
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
        if (finalInventoryList == null || finalInventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, MFR_PART,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);

        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param iInventory
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param loginUserID
     * @param ORD_QTY
     * @param orderManagementLine
     */
    public OrderManagementLineV2 fullQtyAllocationV7(IInventory iInventory, String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom, String loginUserID,
                                                     Double ORD_QTY, OrderManagementLineV2 orderManagementLine) {

        List<IInventoryImpl> finalInventoryList = null;
        OrderManagementLineV2 newOrderManagementLine = null;

        log.info("Logic according to Closed Case Full ---------------> INV_QTY == ORD_QTY Started");
        finalInventoryList = inventoryService.getInventoryForOrderManagementLevelAsscIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom,
                iInventory.getLevelId());

        log.info("Group By LeveId Inventory Closed Case: " + finalInventoryList.size());
        newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
        log.info("newOrderManagementLine updated Closed Case ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) {
        OrderManagementLineV2 newOrderManagementLine = null;
        String alternateUom = orderManagementLine.getAlternateUom();
//        outerloop:
        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
            InventoryV2 stBinInventory = inventoryService.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stBinWiseInventory.getBarcodeId(),
                    stBinWiseInventory.getStorageBin(), alternateUom);
            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);

            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
            if (stBinInventory == null) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

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

            log.info("ORD_QTY -----> {}", ORD_QTY);
            log.info("INV_QTY -----> {}", INV_QTY);

            // Temp variable for setting ORD_QTY
            Double INCOMING_ORD_QTY = ORD_QTY;

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

            orderManagementLine.setNoBags(stBinInventory.getNoBags() != null ? stBinInventory.getNoBags() : 0.0);
            orderManagementLine.setBagSize(stBinInventory.getBagSize() != null ? stBinInventory.getBagSize() : 0.0);
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

            statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
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

            if (newOrderManagementLine.getCompanyDescription() == null) {
                description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
            }

            newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
            if (stBinInventory.getBarcodeId() != null) {
                newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
            }
            if (stBinInventory.getLevelId() != null) {
                newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
            }
            log.info("LoosePack is inventory ---------> " + stBinInventory.getLoosePack());
//            if (Boolean.TRUE.equals(stBinInventory.getLoosePack())) {
//                newOrderManagementLine.setLoosePack(1L);
//            } else {
//                newOrderManagementLine.setLoosePack(0L);
//            }
            newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
            newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
            newOrderManagementLine.setMrp(stBinInventory.getMrp());

            // Logic for checking ordermanagementline partner_item_barcode duplicates
            List<Long> statusIds = Arrays.asList(42L, 43L, 48L);  //42,43,48
            boolean existingOrderManagementLine = orderManagementLineV2Repository.existsByBarcodeIdAndStatusIdInAndDeletionIndicator(newOrderManagementLine.getBarcodeId(), statusIds, 0L);

            OrderManagementLineV2 createdOrderManagementLine = null;
            if (existingOrderManagementLine) {
                log.warn("OrderManagementLine with same barcodeId is existing ---> {}", newOrderManagementLine.getBarcodeId());
            } else {
                createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                BigDecimal ordQty = BigDecimal.valueOf(ORD_QTY);
                BigDecimal allocQty = BigDecimal.valueOf(ALLOC_QTY);

                ordQty = ordQty.setScale(2, RoundingMode.HALF_UP);

                if (ordQty.compareTo(allocQty) > 0) {
//                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                    ORD_QTY = ordQty.doubleValue() - ALLOC_QTY; // convert back if needed
                }

                log.info("ORD_QTY After --else---createdOrderManagementLine newly created------: {}", ORD_QTY);
                log.info("allocatedQtyFromOrderMgmt ----> {}", allocatedQtyFromOrderMgmt);
                log.info("INCOMING_ORD_QTY == ALLOC_QTY Check for Breaking Loop | " + INCOMING_ORD_QTY + " | " + ALLOC_QTY);
                if (INCOMING_ORD_QTY.equals(ALLOC_QTY)) {   // Changed coz ord_qty and alloc_qty will always be same if there is excess inv_qty, in that case this condition fails, so instead check ord_qty = inv_qty then the condition is true.
                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
                    return newOrderManagementLine;
//                    break outerloop; // If the Inventory satisfied the Ord_qty
                }
            }
        }
        return newOrderManagementLine;
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

            //push Notification
            Set<String> preOutboundNoList = new HashSet<>();
            Set<String> warehouseIdList = new HashSet<>();
            String notificationPreOutboundNo = null;
            String notificationWarehouseId = null;
            List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
            List<PickupHeaderV2> pickupHeaders = new ArrayList<>();
            List<AssignPickerV2> sortedList = assignPickers.stream().sorted(Comparator.comparing(AssignPickerV2::getPreOutboundNo)).collect(Collectors.toList());
            AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();

            // Iterating over AssignPicker
            for (AssignPickerV2 assignPicker : sortedList) {
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

                //push notification
                preOutboundNoList.add(assignPicker.getPreOutboundNo());
                warehouseIdList.add(assignPicker.getWarehouseId());

                /**
                 * Check for duplicates
                 */
                PickupHeaderV2 dupPickupHeader = pickupHeaderV2Repository
                        .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                                lineNumber, itemCode, proposedStorageBin, proposedPackCode, 0L);
                log.info("duplicatePickUpHeader: " + dupPickupHeader);

                if (dupPickupHeader == null) {
                    OrderManagementLineV2 dbOrderManagementLine = getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                            partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);

                    log.info("orderManagementLine: " + dbOrderManagementLine);

                    //                AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
                    //                StatusId idStatus = idmasterService.getStatus(48L, warehouseId, idmasterAuthToken.getAccess_token());
                    statusDescription = pickupLineRepository.getStatusDescription(48L, languageId);

//                    dbOrderManagementLine.setAssignedPickerId(assignedPickerId);
//                    dbOrderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                    //                dbOrderManagementLine.setReferenceField7(statusDescription);
//                    dbOrderManagementLine.setStatusDescription(statusDescription);
//                    dbOrderManagementLine.setPickupUpdatedBy(loginUserID);            // Ref_field_7
//                    dbOrderManagementLine.setPickupUpdatedOn(new Date());
//                    dbOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
//                    log.info("dbOrderManagementLine updated : " + dbOrderManagementLine);

                    /*
                     * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                     * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                     * OUTBOUNDLINE table and update SATATU_ID as 48
                     */
                    //                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                    //                        partnerCode, lineNumber, itemCode);
                    //                outboundLine.setStatusId(48L);
                    //                outboundLine.setStatusDescription(statusDescription);
                    //                outboundLine.setAssignedPickerId(assignedPickerId);
                    //                outboundLine = outboundLineV2Repository.save(outboundLine);
                    //                log.info("outboundLine updated : " + outboundLine);
                    //
                    //                // OutboundHeader Update
                    OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                            refDocNumber, partnerCode);
                    //                outboundHeader.setStatusId(48L);
                    //                outboundHeader.setStatusDescription(statusDescription);
                    //                outboundHeaderV2Repository.save(outboundHeader);
                    //                log.info("outboundHeader updated : " + outboundHeader);
                    //
                    //                // ORDERMANAGEMENTHEADER Update
                    //                OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                    //                        .getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                    //                orderManagementHeader.setStatusId(48L);
                    //                orderManagementHeader.setStatusDescription(statusDescription);
                    //                orderManagementHeaderV2Repository.save(orderManagementHeader);
                    //                log.info("orderManagementHeader updated : " + orderManagementHeader);

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
                        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(),
                                dbOrderManagementLine.getLanguageId(), dbOrderManagementLine.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
                        log.info("PU_NO : " + PU_NO);

                        // Insertion of Record in PICKUPHEADER tables
                        PickupHeaderV2 pickupHeader = new PickupHeaderV2();
                        BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));

                        pickupHeader.setAssignedPickerId(assignedPickerId);
                        // PU_NO
                        pickupHeader.setPickupNumber(PU_NO);

                        // PICK_TO_QTY
                        pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());

                        // PICK_UOM
                        pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());

                        // STATUS_ID
                        pickupHeader.setStatusId(48L);
                        //                    pickupHeader.setReferenceField7(idStatus.getStatus());
                        pickupHeader.setStatusDescription(statusDescription);

                        // ProposedPackbarcode
                        pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());

                        pickupHeader.setPickupCreatedBy(loginUserID);
                        pickupHeader.setPickupCreatedOn(new Date());

                        //customerName
                        if (outboundHeader.getCustomerCode() != null) {
                            String customerName = getCustomerName(pickupHeader.getCompanyCodeId(), pickupHeader.getPlantId(),
                                    pickupHeader.getLanguageId(), pickupHeader.getWarehouseId(), outboundHeader.getCustomerCode());
                            if (customerName != null) {
                                pickupHeader.setCustomerName(customerName);
                            }
                        }
                        pickupHeader.setCustomerCode(outboundHeader.getCustomerCode());
                        pickupHeader.setIsPickupHeaderCreated(0l);

                        // REF_FIELD_1
                        pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
                        PickupHeaderV2 pickup = pickupHeaderV2Repository.save(pickupHeader);
                        pickupHeaders.add(pickup);
                        log.info("pickupHeader created : " + pickup);

                        if (notificationPreOutboundNo == null) {
                            notificationPreOutboundNo = assignPicker.getPreOutboundNo();
                            notificationWarehouseId = assignPicker.getWarehouseId();
                            log.info("1.Send Push Notification Initiated..! ---> " + notificationPreOutboundNo);
                            sendPushNotification(notificationPreOutboundNo, notificationWarehouseId);
                        }
                        boolean pass = notificationPreOutboundNo != null && notificationPreOutboundNo.equalsIgnoreCase(assignPicker.getPreOutboundNo());
                        if (!pass) {
                            log.info("2.Send Push Notification Initiated..! ---> " + notificationPreOutboundNo);
                            notificationPreOutboundNo = assignPicker.getPreOutboundNo();
                            notificationWarehouseId = assignPicker.getWarehouseId();
                            sendPushNotification(notificationPreOutboundNo, notificationWarehouseId);
                        }

//                        dbOrderManagementLine.setPickupNumber(PU_NO);
//                        dbOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
                        orderManagementLineV2Repository.updateOrderManagementLineV2(
                                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                                lineNumber, itemCode, 48L, statusDescription, assignedPickerId, PU_NO, loginUserID, proposedStorageBin, new Date());//26_02_2025_update uniqueOrderManagementLine stBin added
                        log.info("OrderManagementLine updated..! ");
                    }
                    orderManagementLineList.add(dbOrderManagementLine);
                }
            }
            //push notification separated from pickup header and consolidated notification sent
        if(preOutboundNoList != null && !preOutboundNoList.isEmpty() && warehouseIdList != null && !warehouseIdList.isEmpty()) {
            sendPushNotification(preOutboundNoList, warehouseIdList);
            sendPushNotification(pickupHeaders);
        } else {
            sendPushNotification();
        }
            return orderManagementLineList;
        } catch (Exception e) {
            log.info("Exception while PickupHeader Create : " + e.getMessage());
            throw e;
        }
    }

    /**
     * update outbound header, line and order management header post pickup header creation
     */
//    @Scheduled(fixedDelay = 5000)
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
                statusDescription = pickupLineRepository.getStatusDescription(STATUS_ID, languageId);

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
     * @param preOutboundNo
     * @param warehouseId
     */
    public void sendPushNotificationV5(Set<String> preOutboundNo, Set<String> warehouseId) {
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

                        statusDescription = pickupLineRepository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
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
                        IKeyValuePair description = pickupLineRepository.getDescription(orderManagementLine.getCompanyCodeId(),
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
            statusDescription = pickupLineRepository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
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
            IKeyValuePair description = pickupLineRepository.getDescription(orderManagementLine.getCompanyCodeId(),
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
        statusDescription = pickupLineRepository.getStatusDescription(47L, orderManagementLine.getLanguageId());
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                                       String warehouseId, String refDocNumber) {
        List<OrderManagementLineV2> orderManagementLineList =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLineList);
        return orderManagementLineList;
    }

    /**
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
                                                                   String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) throws Exception {

        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
        List<OrderManagementLineV2> orderManagementLine =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
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
     * Pick List cancel
     *
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

        if (searchOrderManagementLine.getCompanyCodeId() != null && searchOrderManagementLine.getCompanyCodeId().isEmpty()) {
            searchOrderManagementLine.setCompanyCodeId(null);
        }
        if (searchOrderManagementLine.getPlantId() != null && searchOrderManagementLine.getPlantId().isEmpty()) {
            searchOrderManagementLine.setPlantId(null);
        }
        if (searchOrderManagementLine.getLanguageId() != null && searchOrderManagementLine.getLanguageId().isEmpty()) {
            searchOrderManagementLine.setLanguageId(null);
        }
        if (searchOrderManagementLine.getWarehouseId() != null && searchOrderManagementLine.getWarehouseId().isEmpty()) {
            searchOrderManagementLine.setWarehouseId(null);
        }
        if (searchOrderManagementLine.getRefDocNumber() != null && searchOrderManagementLine.getRefDocNumber().isEmpty()) {
            searchOrderManagementLine.setRefDocNumber(null);
        }
        if (searchOrderManagementLine.getPreOutboundNo() != null && searchOrderManagementLine.getPreOutboundNo().isEmpty()) {
            searchOrderManagementLine.setPreOutboundNo(null);
        }
        if (searchOrderManagementLine.getPartnerCode() != null && searchOrderManagementLine.getPartnerCode().isEmpty()) {
            searchOrderManagementLine.setPartnerCode(null);
        }
        if (searchOrderManagementLine.getItemCode() != null && searchOrderManagementLine.getItemCode().isEmpty()) {
            searchOrderManagementLine.setItemCode(null);
        }
        if (searchOrderManagementLine.getManufacturerName() != null && searchOrderManagementLine.getManufacturerName().isEmpty()) {
            searchOrderManagementLine.setManufacturerName(null);
        }
        if (searchOrderManagementLine.getOutboundOrderTypeId() != null && searchOrderManagementLine.getOutboundOrderTypeId().isEmpty()) {
            searchOrderManagementLine.setOutboundOrderTypeId(null);
        }
        if (searchOrderManagementLine.getSoType() != null && searchOrderManagementLine.getSoType().isEmpty()) {
            searchOrderManagementLine.setSoType(null);
        }
        if (searchOrderManagementLine.getStatusId() != null && searchOrderManagementLine.getStatusId().isEmpty()) {
            searchOrderManagementLine.setStatusId(null);
        }
        if (searchOrderManagementLine.getDescription() != null && searchOrderManagementLine.getDescription().isEmpty()) {
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
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateV6(String companyCodeId, String plantId, String languageId, String warehouseId,
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param barcodeId
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateNamratha(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String preOutboundNo, String refDocNumber, String barcodeId, String itemCode) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndBarcodeIdAndItemCodeAndDeletionIndicator(
                        plantId, companyCodeId, languageId, warehouseId, preOutboundNo, refDocNumber, barcodeId, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        } else {
            return null;
        }

    }


    /**
     * @param assignPickers
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> doAssignPickerV5(List<AssignPickerV2> assignPickers, String assignedPickerId,
                                                        String loginUserID) throws java.text.ParseException, FirebaseMessagingException {
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
        String barcodeId = null;

        //push Notification
        Set<String> preOutboundNoList = new HashSet<>();
        Set<String> warehouseIdList = new HashSet<>();
        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();

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
            barcodeId = assignPicker.getBarcodeId();

            //push notification
            preOutboundNoList.add(assignPicker.getPreOutboundNo());
            warehouseIdList.add(assignPicker.getWarehouseId());

            /**
             * Check for duplicates
             */
            PickupHeaderV2 dupPickupHeader = pickupHeaderV2Repository
                    .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId, 0L);
            log.info("duplicatePickUpHeader: " + dupPickupHeader);

            if (dupPickupHeader == null) {
                // OrderManagementLine Update Process
                OrderManagementLineV2 dbOrderManagementLine = orderManagementLineV2Repository
                        .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId, 0L);
                log.info("orderManagementLine: " + dbOrderManagementLine);

                if (dbOrderManagementLine == null) {
                    throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                            + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                            + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                            + ",barcodeId:" + barcodeId  + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
                }

//                if (dbOrderManagementLine != null) {
//                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
//                    orderManagementLineV2Repository.updateOrderManagementLine(48L, statusDescription, new Date(), assignedPickerId,
//                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
//                            proposedPackCode, 0L);
//                    log.info("Successfully updated Order Management Line ");
//                } else {
//                    throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
//                            + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
//                            + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
//                            + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
//                }

                //OutboundLine Update Process
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                outboundLineV2Repository.updateOutboundLineV5(48L, statusDescription, assignedPickerId, loginUserID, new Date(),
                        companyCodeId, plantId, warehouseId, languageId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
                log.info("outboundLine updated : " + outboundLine);

                // OutboundHeader Update
                OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode);
                outboundHeaderV2Repository.updateOutboundHeaderV5(48L, statusDescription, companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                log.info("outboundHeader updated : " + outboundHeader);

                // ORDERMANAGEMENTHEADER Update
                OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                        .getOrderManagementHeaderV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                // OrderManagementHeaderUpdate
                orderManagementHeader.setStatusId(48L);
                orderManagementHeader.setStatusDescription(statusDescription);
                orderManagementHeaderV2Repository.save(orderManagementHeader);

                log.info("orderManagementHeader updated : " + orderManagementHeader);
                log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber());
                if (dbOrderManagementLine.getPickupNumber() == null) {
                    AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();

                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(),
                            dbOrderManagementLine.getLanguageId(), dbOrderManagementLine.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
                    log.info("PU_NO : " + PU_NO);


                    // Insertion of Record in PICKUPHEADER tables
                    PickupHeaderV2 pickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                    pickupHeader.setPickupNumber(PU_NO);
                    pickupHeader.setAssignedPickerId(assignedPickerId);
                    pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());
                    pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());
                    pickupHeader.setStatusId(48L);
                    pickupHeader.setStatusDescription(statusDescription);
                    pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());
                    pickupHeader.setPickupCreatedBy(loginUserID);
                    pickupHeader.setPickupCreatedOn(new Date());
                    pickupHeader.setReferenceField4(dbOrderManagementLine.getDescription());
                    pickupHeader.setQtyInCase(dbOrderManagementLine.getQtyInCase());
                    pickupHeader.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
                    pickupHeader.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
                    pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
                    pickupHeader.setReferenceField3(dbOrderManagementLine.getReferenceField3());
                    pickupHeader.setManufacturerDate(dbOrderManagementLine.getManufacturerDate());
                    pickupHeader.setExpiryDate(dbOrderManagementLine.getExpiryDate());
                    if (pickupHeader.getExpiryDate() != null) {
                        Date currentDate = new Date();
                        Date expiryDate = pickupHeader.getExpiryDate();

                        // Convert both dates to LocalDate
                        LocalDate localCurrentDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        // Calculate remaining days including today (+1)
                        long remainingDays = ChronoUnit.DAYS.between(localCurrentDate, localExpiryDate) + 1;
                        // Prevent negative remaining days (optional)
                        remainingDays = Math.max(remainingDays, 0);

                        pickupHeader.setRemainingDays(String.valueOf(remainingDays));
                    }

                    PickupHeaderV2 pickup = pickupHeaderV2Repository.save(pickupHeader);
                    log.info("pickupHeader created : " + pickup);
//                    dbOrderManagementLine.setPickupNumber(PU_NO);
//                    dbOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
                    orderManagementLineV2Repository.updateOrderManagementLine(48L, statusDescription, new Date(), assignedPickerId, PU_NO,
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                            proposedPackCode, 0L);
                    log.info("OrderManagementLine updated : " + dbOrderManagementLine);
                }
                orderManagementLineList.add(dbOrderManagementLine);
            }
        }
        //push notification separated from pickup header and consolidated notification sent
        if (preOutboundNoList != null && !preOutboundNoList.isEmpty() && warehouseIdList != null && !warehouseIdList.isEmpty()) {
            sendPushNotificationV5(preOutboundNoList, warehouseIdList);
        } else {
            sendPushNotification();
        }
        return orderManagementLineList;
    }

    /**
     * @param assignPickers
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> doAssignPickerV7(List<AssignPickerV2> assignPickers, String assignedPickerId,
                                                        String loginUserID) throws java.text.ParseException, FirebaseMessagingException {
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
        String barcodeId = null;

        //push Notification
        Set<String> preOutboundNoList = new HashSet<>();
        Set<String> warehouseIdList = new HashSet<>();
        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();

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
            barcodeId = assignPicker.getBarcodeId();

            //push notification
            preOutboundNoList.add(assignPicker.getPreOutboundNo());
            warehouseIdList.add(assignPicker.getWarehouseId());

            /**
             * Check for duplicates
             */
            PickupHeaderV2 dupPickupHeader = pickupHeaderV2Repository
                    .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId, 0L);
            log.info("duplicatePickUpHeader: " + dupPickupHeader);

            if (dupPickupHeader == null) {
                // OrderManagementLine Update Process
                OrderManagementLineV2 dbOrderManagementLine = orderManagementLineV2Repository
                        .findOrderManagementLineV7(
                                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId);
                log.info("orderManagementLine: " + dbOrderManagementLine);

                if (dbOrderManagementLine == null) {
                    throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                            + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                            + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                            + ",barcodeId:" + barcodeId  + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
                }

                //OutboundLine Update Process
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                outboundLineV2Repository.updateOutboundLineV7(48L, statusDescription, assignedPickerId, loginUserID, new Date(),
                        companyCodeId, plantId, warehouseId, languageId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
                log.info("outboundLine updated : " + outboundLine);

                // OutboundHeader Update
                OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode);
                outboundHeaderV2Repository.updateOutboundHeaderV7(48L, statusDescription, companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                log.info("outboundHeader updated : " + outboundHeader);

                // ORDERMANAGEMENTHEADER Update
                OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                        .getOrderManagementHeaderV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                // OrderManagementHeaderUpdate
                orderManagementHeader.setStatusId(48L);
                orderManagementHeader.setStatusDescription(statusDescription);
                orderManagementHeaderV2Repository.save(orderManagementHeader);

                log.info("orderManagementHeader updated : " + orderManagementHeader);
                log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber());
                if (dbOrderManagementLine.getPickupNumber() == null) {
                    AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();

                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(),
                            dbOrderManagementLine.getLanguageId(), dbOrderManagementLine.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
                    log.info("PU_NO : " + PU_NO);


                    // Insertion of Record in PICKUPHEADER tables
                    PickupHeaderV2 pickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                    pickupHeader.setPickupNumber(PU_NO);
                    pickupHeader.setAssignedPickerId(assignedPickerId);
                    pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());
                    pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());
                    pickupHeader.setStatusId(48L);
                    pickupHeader.setStatusDescription(statusDescription);
                    pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());
                    pickupHeader.setPickupCreatedBy(loginUserID);
                    pickupHeader.setPickupCreatedOn(new Date());
                    pickupHeader.setQtyInCase(dbOrderManagementLine.getQtyInCase());
                    pickupHeader.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
                    pickupHeader.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
                    pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
                    pickupHeader.setManufacturerDate(dbOrderManagementLine.getManufacturerDate());
                    pickupHeader.setExpiryDate(dbOrderManagementLine.getExpiryDate());
                    if (pickupHeader.getExpiryDate() != null) {
                        Date currentDate = new Date();
                        Date expiryDate = pickupHeader.getExpiryDate();

                        // Convert both dates to LocalDate
                        LocalDate localCurrentDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        // Calculate remaining days including today (+1)
                        long remainingDays = ChronoUnit.DAYS.between(localCurrentDate, localExpiryDate) + 1;
                        // Prevent negative remaining days (optional)
                        remainingDays = Math.max(remainingDays, 0);

                        pickupHeader.setRemainingDays(String.valueOf(remainingDays));
                    }

                    // Setting item_text in ref_field_5 of pickupheader
                    pickupHeader.setReferenceField5(dbOrderManagementLine.getDescription());

                    PickupHeaderV2 pickup = pickupHeaderV2Repository.save(pickupHeader);
                    log.info("pickupHeader created : " + pickup);
                    fireBaseNotificationV7(assignPickers.get(0), loginUserID);
//                    dbOrderManagementLine.setPickupNumber(PU_NO);
//                    dbOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
                    orderManagementLineV2Repository.updateOrderManagementLineV7(48L, statusDescription, new Date(), assignedPickerId, PU_NO,
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                            proposedPackCode, 0L, barcodeId);
                    log.info("OrderManagementLine updated : " + dbOrderManagementLine);
                    outboundHeaderV2Repository.updateOutboundHeaderStatusV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
                    orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV7(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
                }
                orderManagementLineList.add(dbOrderManagementLine);
            }
        }
        //push notification separated from pickup header and consolidated notification sent
        if (preOutboundNoList != null && !preOutboundNoList.isEmpty() && warehouseIdList != null && !warehouseIdList.isEmpty()) {
            sendPushNotificationV5(preOutboundNoList, warehouseIdList);
        } else {
            sendPushNotification();
        }
        return orderManagementLineList;
    }

    /**
     *
     * @param assignPickerV2
     * @param loginUserID
     */
    private void fireBaseNotificationV7(AssignPickerV2 assignPickerV2,String loginUserID) {
        try {
//            try {
//                DataBaseContextHolder.setCurrentDb("MT");
//                String profile = dbConfigRepository.getDbName(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(profile);

            log.info("Notification Input ----> | " + assignPickerV2.getCompanyCodeId() + " | " + assignPickerV2.getPlantId() + " | " + assignPickerV2.getLanguageId() + " | " + assignPickerV2.getWarehouseId());
            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(assignPickerV2.getCompanyCodeId(), assignPickerV2.getPlantId(), assignPickerV2.getLanguageId(), assignPickerV2.getWarehouseId(), loginUserID);
            log.info("deviceToken ------> {}", deviceToken);
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String title = "Inbound Create";
                String message = "PickupHeader Created Sucessfully ";

                NotificationSave notificationInput = new NotificationSave();
                notificationInput.setUserId(Collections.singletonList(loginUserID));
                notificationInput.setUserType(null);
                notificationInput.setMessage(message);
                notificationInput.setTopic(title);
                notificationInput.setReferenceNumber(assignPickerV2.getRefDocNumber());
                notificationInput.setDocumentNumber(assignPickerV2.getPreOutboundNo());
                notificationInput.setCompanyCodeId(assignPickerV2.getCompanyCodeId());
                notificationInput.setPlantId(assignPickerV2.getPlantId());
                notificationInput.setLanguageId(assignPickerV2.getLanguageId());
                notificationInput.setWarehouseId(assignPickerV2.getWarehouseId());
                notificationInput.setCreatedBy(loginUserID);

                log.info("pushNotification started");
                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
                log.info("pushNotification completed");
            }
//            } finally {
//                DataBaseContextHolder.clear();
//            }
        } catch (Exception e) {
            log.error("Inbound firebase notification error", e); // This logs the full stack trace
        }
    }

    //==========SPAREX=============================
    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param barcodeId
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForLineV10(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String preOutboundNo, String refDocNumber, String barcodeId, String itemCode, Long lineNo) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndBarcodeIdAndItemCodeAndLineNumberAndDeletionIndicator(
                        plantId, companyCodeId, languageId, warehouseId, preOutboundNo, refDocNumber, barcodeId, itemCode, lineNo,0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        } else {
            return null;
        }

    }

    // ============== BF && KKF =================
    /**
     *
     * @param assignPicker
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<AssignPickerId> assignPickerIdV9(List<AssignPickerId> assignPicker,String assignedPickerId,String loginUserID){

        //push Notification
        Set<String> preOutboundNoList = new HashSet<>();
        Set<String> warehouseIdList = new HashSet<>();

        log.info("Assign Picker ----------->V9<---------- {}",assignPicker);
        for (AssignPickerId pickerId : assignPicker){
            String companyId = pickerId.getCompanyId();
            String plantId = pickerId.getPlantId();
            String languageId = pickerId.getLanguageId();
            String warehouseId = pickerId.getWarehouseId();
            String refDocNo = pickerId.getRefDocNumber();
            String preOutboundNo = pickerId.getPreOutboundNo();
            String itemCode = pickerId.getItemCode();
            String barcode = pickerId.getBarcode();
            Long lineNo = pickerId.getLineNo();
            String storageBin = pickerId.getProposedStorageBin();
            String palletCode = pickerId.getPalletCode();
            Double orderQty = pickerId.getOrderQty();
            String partnerCode = pickerId.getPartnerCode();
            String proposedPackCode = pickerId.getProposedPackCode();

            preOutboundNoList.add(pickerId.getPreOutboundNo());
            warehouseIdList.add(pickerId.getWarehouseId());

            IKeyValuePair ikey = orderManagementLineRepository.getOrigin(palletCode);

            String customerPallet = null;
            Date mfrDate = null;
            Date expDate = null;

            if(ikey != null) {
                customerPallet = ikey.getOrigin() != null ? ikey.getOrigin() : null;
                mfrDate = ikey.getMfrDate() != null ? ikey.getMfrDate() : null;
                expDate = ikey.getExpiryDate() != null ? ikey.getExpiryDate() : null;
            }

            outboundLineV2Repository.updateOutboundLineAssignPickerV9(companyId,plantId,languageId,warehouseId,refDocNo,preOutboundNo,
                    itemCode,lineNo,partnerCode,assignedPickerId,barcode,orderQty,palletCode, customerPallet, mfrDate, expDate);
            log.info("Outbound Line updated");
            outboundLineV2Repository.updateOrderManagementAssignPickerV9(companyId,plantId,languageId,warehouseId,refDocNo,preOutboundNo,
                    itemCode,lineNo,partnerCode,assignedPickerId,barcode,proposedPackCode,orderQty,palletCode,storageBin, customerPallet, mfrDate, expDate);
            log.info("Order management Line updated");
            outboundLineV2Repository.updatePickupHeaderAssignPickerV9(companyId,plantId,languageId,warehouseId,refDocNo,preOutboundNo,
                    itemCode,lineNo,partnerCode,assignedPickerId,barcode,proposedPackCode,orderQty,palletCode,storageBin, customerPallet, mfrDate, expDate);
            log.info("Pickup header updated");
        }

        try {
            String orderText = "PickupHeader Created Successfully";
            outboundOrderV2Repository.updatePickupHeaderProcessStatusId(assignPicker.get(0).getRefDocNumber(), orderText);
        } catch (Exception e) {
            log.error("Error while updating tblOborder2...");
        }

        //push notification separated from pickup header and consolidated notification sent
        if (!preOutboundNoList.isEmpty()) {
            sendPushNotificationV5(preOutboundNoList, warehouseIdList);
        } else {
            sendPushNotification();
        }
        return assignPicker;
    }


    /**
     *
     * @param outboundLineV2List
     * @return
     */
    public List<OutboundLineV2> orderReAllocation(List<OutboundLineV2> outboundLineV2List, String loginUserID) throws Exception {


        log.info("OrderReAllocation's  Size of Value {} ---------> Inputs ---> {} ",outboundLineV2List.size(), outboundLineV2List);

        OutboundLineV2 outboundLineV2 = outboundLineV2List.get(0);
        String companyCodeId = outboundLineV2.getCompanyCodeId();
        String plantId = outboundLineV2.getPlantId();
        String warehouseId = outboundLineV2.getWarehouseId();
        String refDocNumber = outboundLineV2.getRefDocNumber();

        log.info("GroupBy ItemCode & Sum Of Qty  ------> ");

//        Map<String, Double> groupByItemCode = outboundLineV2List.stream().collect(Collectors.groupingBy(OutboundLineV2 :: getItemCode,
//                Collectors.summingDouble(OutboundLineV2::getOrderQty)));

//        for(Map.Entry<String, Double> entryKeyValue : groupByItemCode.entrySet()) {
//
//            String itemCode = entryKeyValue.getKey();
//            Double orderQty = entryKeyValue.getValue();
//
//            log.info("ItemCode -> {} , OrderQty -> {} ", itemCode, orderQty);
//
//            if(orderQty > 0) {
////                int deletePreOutboundLine = preOutboundLineV2Repository.deleteLine(companyCodeId, plantId, warehouseId, refDocNumber, itemCode);
////                log.info("Deleted PreOutboundLine Affected Rows ---> {} ", deletePreOutboundLine);
//                int updateQty = preOutboundLineV2Repository.updateOrderQty(companyCodeId, plantId, warehouseId, refDocNumber, itemCode, orderQty, new Date(), 101L, "ReAllocation");
//                log.info("PreOutboundLine Qty Updated Affected Rows ---> {} ", updateQty);
//            } else {
//            int deletePreOutboundLine = preOutboundLineV2Repository.deleteLine(companyCodeId, plantId, warehouseId, refDocNumber, itemCode);
//            log.info("Deleted PreOutboundLine Affected Rows ---> {} ", deletePreOutboundLine);
//            }
//
//        }

        for(OutboundLineV2 outboundLine : outboundLineV2List) {

//            Long lineNumber = preOutboundLineV2Repository.getMaxLineNumberV9(outboundLine.getCompanyCodeId(),outboundLine.getPlantId(),
//                    outboundLine.getLanguageId(),outboundLine.getWarehouseId(),outboundLine.getRefDocNumber(),outboundLine.getPreOutboundNo());
//            if (lineNumber == null){
//                lineNumber = outboundLine.getLineNumber();
//            }

            if(outboundLine.getOrderQty() > 0) {

                log.info("OrderAllocation Process --------------> {} ", outboundLine);
                orderAllocationV9(outboundLine, loginUserID);
                log.info("OutboundLine Creation Logic Started --------> ");
                List<OrderManagementLineV2> orderLineList = orderManagementLineV2Repository.findOrderLinesV9(outboundLine.getCompanyCodeId(),
                        outboundLine.getPlantId(), outboundLine.getWarehouseId(), outboundLine.getRefDocNumber(), outboundLine.getItemCode(), "5");

                long NUM_RAN_CODE = 10;
                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, "EN", warehouseId);
                log.info("----------New PU_NO--------> : " + PU_NO);
                List<OutboundLineV2> outboundLineV2s = new ArrayList<>();
                List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
                for(OrderManagementLineV2 dbOrderManagementLine : orderLineList) {
                    OutboundLineV2 dbOutboundLine = new OutboundLineV2();
                    BeanUtils.copyProperties(dbOrderManagementLine, dbOutboundLine, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                    dbOutboundLine.setDeliveryQty(0D);
                    dbOutboundLine.setLineNumber(dbOrderManagementLine.getLineNumber());
                    dbOutboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                    dbOutboundLine.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
                    dbOutboundLine.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
                    dbOutboundLine.setQtyInCase(dbOrderManagementLine.getQtyInCase());
                    dbOutboundLine.setDescription(dbOrderManagementLine.getDescription());
                    statusDescription = getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                    dbOutboundLine.setStatusDescription(statusDescription);
//                    dbOutboundLine.setInvoiceDate(preOutboundHeaderV2.getRequiredDeliveryDate());
                    dbOutboundLine.setReferenceField1(dbOrderManagementLine.getPalletId());
                    dbOutboundLine.setReferenceField6(dbOrderManagementLine.getReferenceField6());     //GrossWeight
                    dbOutboundLine.setReferenceField10(dbOrderManagementLine.getReferenceField10());  //NetWeight
                    dbOutboundLine.setMrp(dbOrderManagementLine.getMrp());                              //MRP
                    dbOutboundLine.setReferenceField5(dbOrderManagementLine.getReferenceField5());       //totalWeight

                    dbOutboundLine.setReferenceField2(String.valueOf(dbOrderManagementLine.getManufacturerDate()));
                    dbOutboundLine.setReferenceField8(String.valueOf(dbOrderManagementLine.getExpiryDate()));
                    dbOutboundLine.setReferenceField4(dbOrderManagementLine.getPalletId());
                    dbOutboundLine.setLineNumber(dbOrderManagementLine.getLineNumber());
                    dbOutboundLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());


                    if (dbOutboundLine.getOutboundOrderTypeId() == 3L) {
                        dbOutboundLine.setCustomerType("INVOICE");
                    }
                    if (dbOutboundLine.getOutboundOrderTypeId() == 1L) {
                        dbOutboundLine.setCustomerType("Transfer Out");
                    }
                    if (dbOutboundLine.getOutboundOrderTypeId() == 0L) {
                        dbOutboundLine.setCustomerType("TRANSVERSE");
                    }


                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, dbOrderManagementLine.getLanguageId());

                    int updateOrderLine = orderManagementLineV2Repository.updateOrderLinesV9(
                            dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getRefDocNumber(),
                            dbOrderManagementLine.getItemCode(), "0", 48L, statusDescription);
                    log.info("OrderLines count update ------> " + updateOrderLine);
                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(dbOrderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                    newPickupHeader.setPickupNumber(PU_NO);
                    newPickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());
                    newPickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());
                    newPickupHeader.setBarcodeId(dbOrderManagementLine.getBarcodeId());

                    newPickupHeader.setStatusId(48L);
                    // STATUS_ID
                    newPickupHeader.setStatusDescription(statusDescription);

                    // ProposedPackbarcode
                    newPickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());

                    //Setting InventoryQuantity from orderManagementLine
                    newPickupHeader.setInventoryQuantity(dbOrderManagementLine.getInventoryQty());

                    //Setting BagSize
                    newPickupHeader.setBagSize(dbOrderManagementLine.getInventoryQty());
                    newPickupHeader.setNoBags(dbOrderManagementLine.getNoBags());

                    newPickupHeader.setReferenceField5(dbOrderManagementLine.getDescription());
                    newPickupHeader.setBatchSerialNumber(dbOrderManagementLine.getProposedBatchSerialNumber());
                    newPickupHeader.setStorageSectionId(dbOrderManagementLine.getStorageSectionId());
                    newPickupHeader.setReferenceField2(dbOrderManagementLine.getPalletId());
                    newPickupHeader.setManufacturerDate(dbOrderManagementLine.getManufacturerDate());
                    newPickupHeader.setExpiryDate(dbOrderManagementLine.getExpiryDate());
                    newPickupHeader.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                    newPickupHeader.setManufacturerName(dbOutboundLine.getManufacturerName());

                    outboundLineV2s.add(dbOutboundLine);
                    pickupHeaderV2List.add(newPickupHeader);
                }
                log.info("OutboundLine's Saved List ------>" + outboundLineV2s.size());
                outboundLineV2Repository.saveAll(outboundLineV2s);
                log.info("PickupHeader Saved List ----> " + pickupHeaderV2List.size());
                pickupHeaderV2Repository.saveAll(pickupHeaderV2List);

            } else {
                int updateQty = preOutboundLineV2Repository.updateOrderQty(companyCodeId, plantId, warehouseId, refDocNumber, outboundLine.getItemCode(), outboundLine.getDeliveryQty(), new Date());
                log.info("PreOutboundLine Qty Updated Affected Rows ---> {} ", updateQty);

                log.info("OrderManagementLine Deleted Process -------> ");
                int orderManagementLine = orderManagementLineV2Repository.deleteOrderManagementLine(companyCodeId, plantId, outboundLine.getLanguageId(), warehouseId,
                        refDocNumber, outboundLine.getItemCode(), outboundLine.getReferenceField1());
                log.info("OrderManagementLine Deleted Affected Row's -----> {} ", orderManagementLine);

                int obLine = outboundLineV2Repository.deleteOutboundLine(companyCodeId, plantId, warehouseId, refDocNumber, outboundLine.getItemCode(), outboundLine.getReferenceField1());
                log.info("OutboundLine Deleted Affected Row's -----> {} ", obLine);

                int pickupHeader = pickupHeaderV2Repository.deletePickupHeader(companyCodeId, plantId, warehouseId, refDocNumber, outboundLine.getItemCode(), outboundLine.getReferenceField1());
                log.info("PickUpHeader Deleted Affected Row's -----> {} ", pickupHeader);

                List<PickupLineV2> pickUpList = pickupLineV2Repository.getPickupLineForDenial(outboundLine.getReferenceField1(), outboundLine.getBarcodeId(), outboundLine.getItemCode(), refDocNumber);
                log.info("PickupLine Values  -----------------> {} ", pickUpList);
                for (PickupLineV2 pickUp : pickUpList) {
                    if (pickUp != null) {
                        log.info("Processing dbPickup -----> {}", pickUp);

                        log.info("Barcode Id is --> {} ", pickUp.getBarcodeId());
                        log.info("ItemCode is --> {} ", pickUp.getItemCode());
                        log.info("ConfirmedStorageBin --> {} ", pickUp.getPickedStorageBin());
                        log.info("Qty is --> {} ", pickUp.getPickConfirmQty());

                        InventoryV2 inv = inventoryV2Repository.getInventoryListV9(pickUp.getCompanyCodeId(), pickUp.getLanguageId(),
                                pickUp.getPlantId(), pickUp.getWarehouseId(), pickUp.getBarcodeId(), pickUp.getItemCode(), pickUp.getManufacturerName(),
                                pickUp.getPickedStorageBin(), pickUp.getReferenceField2());

                        log.info("InventoryV2 data -------> {}", inv);

                        if (inv != null) {
                            Double pickedQty = pickUp.getPickConfirmQty();
                            Double inventoryQty = inv.getInventoryQuantity();
                            Double allocateQty;
                            if (inv.getAllocatedQuantity() == null) {
                                allocateQty = 0D;
                            } else {
                                allocateQty = inv.getAllocatedQuantity();
                            }

                            Double INV_QTY = inventoryQty;
                            Double ALL_QTY = allocateQty - pickedQty;

                            log.info("Inventory Qty ------------V9 -------> " + INV_QTY);
                            log.info("Allocated Qty ------------V9 --------> " + ALL_QTY);
                            InventoryV2 newInventory = new InventoryV2();
                            BeanUtils.copyProperties(inv, newInventory, CommonUtils.getNullPropertyNames(inv));
                            newInventory.setReferenceDocumentNo(pickUp.getRefDocNumber());
                            newInventory.setInventoryQuantity(INV_QTY);
                            newInventory.setAllocatedQuantity(ALL_QTY);
                            newInventory.setReferenceField4(INV_QTY + ALL_QTY);
                            Long statusId = null;
                            if (INV_QTY == 0) {
                                newInventory.setReferenceField7("1");
                                statusId = 0L;
                            } else {
                                statusId = 10L;
                                newInventory.setReferenceField7("0");
                            }

                            // Explicitly setting inv_id null
                            newInventory.setInventoryId(null);

                            InventoryV2 createdBinCls1Inventory = inventoryV2Repository.save(newInventory);

                            int stBinCount = storageBinRepository.updateStorageBinStatus(statusId, newInventory.getStorageBin(), newInventory.getCompanyCodeId(), newInventory.getPlantId(), newInventory.getWarehouseId());
                            log.info("StorageBin StatusUpdated Count --------> " + stBinCount);

                            // BinClsId 5 inventory Record
                            InventoryV2 bin5Inv = new InventoryV2();
                            BeanUtils.copyProperties(createdBinCls1Inventory, bin5Inv, CommonUtils.getNullPropertyNames(createdBinCls1Inventory));
                            Double INV_BIN5_QTY = pickedQty;
                            if (INV_BIN5_QTY < 0) {
                                INV_BIN5_QTY = 0D;
                            }

                            log.info("INV_BIN5_QTY -------> {}", INV_BIN5_QTY);
                            bin5Inv.setInventoryId(null);
                            bin5Inv.setBinClassId(5L);
                            String binDesc = inventoryV2Repository.getBinClassIdDecription(bin5Inv.getBinClassId(), bin5Inv.getCompanyCodeId(), bin5Inv.getPlantId(), bin5Inv.getLanguageId());
                            bin5Inv.setStorageBin(binDesc);
                            bin5Inv.setInventoryQuantity(INV_BIN5_QTY);
                            bin5Inv.setReferenceField4(INV_BIN5_QTY);
                            inventoryV2Repository.save(bin5Inv);
                            log.info("InvQty Set as PICK_CNF_QTY in Inventory BIN_CL_ID 5 Saved -----> " + bin5Inv);

                            int deletePickup = pickupLineV2Repository.deletePickupLineByPallet(refDocNumber, pickUp.getReferenceField2());
                            log.info("PickupLine Deleted -----------> Affected Row's --> {} ", deletePickup);

                            int qualityHeader = qualityHeaderV2Repository.deleteQualityHeader(pickUp.getBarcodeId(), refDocNumber, pickUp.getItemCode(), companyCodeId, plantId, warehouseId);
                            log.info("QualityHeader Deleted -----------> Affected Row's --> {} ", qualityHeader);
                        }
                    }
                }
            }
        }

        return outboundLineV2List;
    }


    void orderAllocationV9(OutboundLineV2 outboundLine, String loginUserID) throws Exception {

        Long lineNumber = preOutboundLineV2Repository.getMaxLineNumberV9(outboundLine.getCompanyCodeId(),outboundLine.getPlantId(),
                outboundLine.getLanguageId(),outboundLine.getWarehouseId(),outboundLine.getRefDocNumber(),outboundLine.getPreOutboundNo());

        PreOutboundLineV2 newPreOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(outboundLine, newPreOutboundLine, CommonUtils.getNullPropertyNames(outboundLine));
        newPreOutboundLine.setStatusId(101L);
        newPreOutboundLine.setLineNumber(lineNumber);
        newPreOutboundLine.setStatusDescription("Order ReAllocation");
        newPreOutboundLine.setCreatedOn(new Date());
        newPreOutboundLine.setCreatedBy(loginUserID);
        preOutboundLineV2Repository.save(newPreOutboundLine);

        OrderManagementLineV2 orderLine = updateAllocationV9(outboundLine, loginUserID);

        log.info("OrderManagementAllocation Process Started ---------------> ");
    }


    public OrderManagementLineV2 updateAllocationV9(OutboundLineV2 outboundLineV2, String loginUserID) throws Exception {
        try {
            String manufacturerName = outboundLineV2.getManufacturerName();
            OrderManagementLineV2 orderLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(outboundLineV2, orderLine, CommonUtils.getNullPropertyNames(outboundLineV2));
            orderLine.setCompanyCodeId(outboundLineV2.getCompanyCodeId());
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantitiesV9(orderLine);
            log.info("Quantity Logic started ----------> ");
            Double ORD_QTY = orderLine.getOrderQty();
            log.info("ORD_QTY is ------------------> {} ", ORD_QTY);
            String INV_STRATEGY = null;
            String obStrategy = null;
            String fifoMethod = null;
            if (orderLine.getBarcodeId() != null && !orderLine.getBarcodeId().isEmpty()) {
                log.info("BarcodeId is ------> " + orderLine.getBarcodeId());
                INV_STRATEGY = "FIFO";

            } else {
                log.info("Starting Warehouse table call for Strategies");
                List<Object[]> strategyList = stagingLineV2Repository.getStrategy(orderLine.getCompanyCodeId(), orderLine.getPlantId(), orderLine.getWarehouseId());
                if (strategyList != null && !strategyList.isEmpty()) {
                    Object[] strategy = strategyList.get(0); // GET_FIRST_RECORD
                    if (strategy.length > 0 && strategy[0] != null) {
                        obStrategy = strategy[0].toString();
                    }
                    if (strategy.length > 0 && strategy[1] != null) {
                        fifoMethod = strategy[1].toString();
                    }
                }
                log.info("OB_STRATEGY: {}, FIFO_MD: {}", obStrategy, fifoMethod);
                INV_STRATEGY = obStrategy;
            }

            // Inventory Strategy Choices
            if (INV_STRATEGY == null) {
                INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
            }

            log.info("Allocation Strategy: " + INV_STRATEGY);
            OrderManagementLineV2 newOrderManagementLine = null;
            // Getting Inventory GroupBy ST_BIN wise
            List<IInventoryImpl> finalInventoryList = null;
            List<InventoryV2> inventoryV2List = null;
            double balanceOrderQty = orderLine.getOrderQty();
            if (INV_STRATEGY.equalsIgnoreCase("FIFO")) {
                if (orderLine.getOutboundOrderTypeId().equals(3L) || orderLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9(orderLine.getCompanyCodeId(),
                            orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode(), orderLine.getBarcodeId());
                }
                if (orderLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9Bin7(orderLine.getCompanyCodeId(),
                            orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode(), orderLine.getBarcodeId());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", orderLine.getItemCode());
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    log.info("BarcodeId----->"+barcodeId);
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            orderLine.getCompanyCodeId(), orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode(), inventory.getPalletCode(), barcodeId);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
//                    double availableQty = invQty - alreadyAllocated;
                    double availableQty = Math.abs(invQty - alreadyAllocated);
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLineV2 = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderLine, orderLineV2, CommonUtils.getNullPropertyNames(orderLine));
                    orderLineV2.setBarcodeId(barcodeId);
                    orderLineV2.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLineV2.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderLine.getOutboundOrderTypeId() == 11) {
                        orderLineV2.setBarcodeId("Empty Crate");
                    }
                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(),orderLine.getPlantId(),
                            orderLine.getLanguageId(),orderLine.getWarehouseId(),orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(),orderLine.getItemCode());
                    if (lineNumber == null){
                        lineNumber = orderLine.getLineNumber();
                    }
                    orderLineV2.setLineNumber(lineNumber);
                    orderLineV2.setManufacturerDate(inventory.getManufacturerDate());
                    orderLineV2.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLineV2.setExpiryDate(inventory.getExpiryDate());
                    orderLineV2.setInventoryQty(balanceOrderQty);
                    orderLineV2.setAllocatedQty(allocatedQty);
                    orderLineV2.setProposedStorageBin(inventory.getStorageBin());
                    orderLineV2.setItemCode(orderLine.getItemCode());
                    orderLineV2.setDescription(inventory.getDescription());
                    orderLineV2.setOrderQty(orderLine.getOrderQty());
                    orderLineV2.setPalletId(inventory.getPalletCode());


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, orderLine.getLanguageId());
                    orderLineV2.setStatusId(STATUS_ID);
                    orderLineV2.setStatusDescription(statusDescription);
                    orderLineV2.setReferenceField7(statusDescription);
                    orderLineV2.setPickupUpdatedBy(loginUserID);
                    orderLineV2.setPickupUpdatedOn(new Date());
                    orderLineV2.setManufacturerCode(inventory.getManufacturerCode());
                    orderLineV2.setManufacturerName(inventory.getManufacturerName());
                    orderLineV2.setManufacturerFullName(inventory.getManufacturerName());
                    orderLineV2.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLineV2.setMrp(inventory.getMrp());                                    //MRP
                    orderLineV2.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLineV2.setReferenceField10(inventory.getBrand());                        //totalWeight
                    orderLineV2.setLevelId("5");

                    orderManagementLineV2Repository.save(orderLineV2);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLineV2;
                    }
                }
            }
            if (INV_STRATEGY.equalsIgnoreCase("FEFO")) {

                if (orderLine.getOutboundOrderTypeId().equals(3L) || orderLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationV9(orderLine.getCompanyCodeId(),
                            orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode());
                }
                if (orderLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin7V9(orderLine.getCompanyCodeId(),
                            orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", orderLine.getItemCode());
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            orderLine.getCompanyCodeId(), orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getItemCode(), inventory.getPalletCode(), barcodeId);

                    log.info("Sum Of Qty----->" + sumOfQty);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
                    double availableQty = Math.abs(invQty - alreadyAllocated);
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }
                    log.info("BalanceOrderQty---->" + balanceOrderQty);

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLineV2 = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderLine, orderLineV2, CommonUtils.getNullPropertyNames(orderLine));
                    orderLineV2.setBarcodeId(barcodeId);
                    orderLineV2.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLineV2.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderLine.getOutboundOrderTypeId() == 11) {
                        orderLineV2.setBarcodeId("Empty Crate");
                    }

                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
//                    if (lineNumber == null){
//                        lineNumber = orderManagementLine.getLineNumber();
//                    }
                    orderLineV2.setLineNumber(lineNumber);
                    orderLineV2.setManufacturerDate(inventory.getManufacturerDate());
                    orderLineV2.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLineV2.setExpiryDate(inventory.getExpiryDate());
                    orderLineV2.setInventoryQty(balanceOrderQty);
                    orderLineV2.setAllocatedQty(allocatedQty);
                    orderLineV2.setProposedStorageBin(inventory.getStorageBin());
                    orderLineV2.setItemCode(orderLine.getItemCode());
                    orderLineV2.setDescription(inventory.getDescription());
                    orderLineV2.setOrderQty(orderLine.getOrderQty());
                    orderLineV2.setPalletId(inventory.getPalletCode());
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    log.info("AllocatedQty---->" + allocatedQty);


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, orderLine.getLanguageId());
                    orderLineV2.setStatusId(STATUS_ID);
                    orderLineV2.setStatusDescription(statusDescription);
                    orderLineV2.setReferenceField7(statusDescription);
                    orderLineV2.setPickupUpdatedBy(loginUserID);
                    orderLineV2.setPickupUpdatedOn(new Date());
                    orderLineV2.setManufacturerCode(inventory.getManufacturerCode());
                    orderLineV2.setManufacturerName(inventory.getManufacturerName());
                    orderLineV2.setManufacturerFullName(inventory.getManufacturerName());
                    orderLineV2.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLineV2.setMrp(inventory.getMrp());                                    //MRP
                    orderLineV2.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLineV2.setReferenceField5(inventory.getBrand());                        //totalWeight

                    log.info("OrderManagementLine------>" + orderLineV2);
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    orderLineV2.setLevelId("5");
                    orderManagementLineV2Repository.save(orderLineV2);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLineV2;
                    }
                }
            }
//            log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

            // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//            if (finalInventoryList == null || (finalInventoryList != null && finalInventoryList.isEmpty())) {
//                return updateOrderManagementLineV2(orderManagementLine);
//            }

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(outboundLineV2.getCompanyCodeId(), outboundLineV2.getPlantId(), outboundLineV2.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            if (inventoryV2List == null || inventoryV2List.isEmpty() || (finalInventoryList == null || finalInventoryList.isEmpty()) || balanceOrderQty >=0) {
                return updateOrderManagementLineV2(orderLine);
            }

            newOrderManagementLine = orderAllocationV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(), orderLine.getLanguageId(), orderLine.getWarehouseId(),
                    ORD_QTY, orderLine, finalInventoryList, loginUserID);

            log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while updateAllocation V3: " + e);
            throw e;
        }
    }


    private void setAlternateUomQuantitiesV9(OrderManagementLineV2 orderManagementLineV2) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = orderManagementLineV2.getOrderUom();
            String companyCodeId = orderManagementLineV2.getCompanyCodeId();
            String plantId = orderManagementLineV2.getPlantId();
            String warehouseId = orderManagementLineV2.getWarehouseId();
            String itemCode = orderManagementLineV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = orderManagementLineV2.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLineV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = orderManagementLineV2.getOrderQty() / caseQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = orderManagementLineV2.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = orderManagementLineV2.getOrderQty();

                log.info("Case Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is Crate");
                qtyInCreate = orderManagementLineV2.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQy = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("Crate Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", caseQy);

                if (orderManagementLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && caseQy != null && caseQy.getUomQty() != null) {
                    qtyInCase = qtyInPiece / caseQy.getUomQty();
                }
            }

            orderManagementLineV2.setQtyInPiece(qtyInPiece);
            orderManagementLineV2.setQtyInCase(qtyInCase);
            orderManagementLineV2.setQtyInCrate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }


    public OrderManagementLineV2 orderAllocationV9(String companyCodeId, String plantId, String languageId, String warehouseId, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) throws Exception {
        try {
            if (finalInventoryList == null || finalInventoryList.isEmpty()) {
                return updateOrderManagementLineV2(orderManagementLine);
            }
            OrderManagementLineV2 newOrderManagementLine = null;
            outerloop:
            for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
                if (stBinWiseInventory == null) {
                    return updateOrderManagementLineV2(orderManagementLine);
                }

                if (stBinWiseInventory != null) {

                    Long STATUS_ID = 0L;
                    Double ALLOC_QTY = 0D;
                    Double INV_QTY = stBinWiseInventory.getInventoryQuantity();
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

                    statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                    orderManagementLine.setStatusId(STATUS_ID);
                    orderManagementLine.setStatusDescription(statusDescription);
                    orderManagementLine.setReferenceField7(statusDescription);
                    orderManagementLine.setPickupUpdatedBy(loginUserID);
                    orderManagementLine.setPickupUpdatedOn(new Date());
                    double allocatedQtyFromOrderMgmt = 0.0;
                    newOrderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    if (newOrderManagementLine.getCompanyDescription() == null) {
                        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                        newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                        newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                        newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    newOrderManagementLine.setProposedStorageBin(stBinWiseInventory.getStorageBin());
                    newOrderManagementLine.setBarcodeId(stBinWiseInventory.getBarcodeId());
                    newOrderManagementLine.setLevelId(stBinWiseInventory.getLevelId());
                    newOrderManagementLine.setStorageSectionId(stBinWiseInventory.getStorageSectionId());
                    newOrderManagementLine.setProposedPackBarCode(stBinWiseInventory.getPackBarcodes());
                    newOrderManagementLine.setProposedBatchSerialNumber(stBinWiseInventory.getBatchSerialNumber());
                    newOrderManagementLine.setArticleNo(stBinWiseInventory.getArticleNo());
                    newOrderManagementLine.setManufacturerDate(stBinWiseInventory.getManufacturerDate());
                    newOrderManagementLine.setExpiryDate(stBinWiseInventory.getExpiryDate());
                    String totQty = String.valueOf(stBinWiseInventory.getReferenceField4());
                    newOrderManagementLine.setPalletId(stBinWiseInventory.getPalletCode());
                    newOrderManagementLine.setReferenceField3(totQty);

                    // Logic for checking ordermanagementline partner_item_barcode duplicates
                    List<Long> statusIds = Arrays.asList(42L, 43L, 48L);  //42,43,48
                    boolean existingOrderManagementLine = orderManagementLineV2Repository.existsByBarcodeIdAndStatusIdInAndDeletionIndicator(newOrderManagementLine.getBarcodeId(), statusIds, 0L);

                    OrderManagementLineV2 createdOrderManagementLine = null;
                    if (existingOrderManagementLine) {
                        log.warn("OrderManagementLine with same barcodeId is existing ---> {}", newOrderManagementLine.getBarcodeId());
                    } else {
                        newOrderManagementLine.setLevelId("5");
                        createdOrderManagementLine = orderManagementLineV2Repository.saveAndFlush(newOrderManagementLine);
                        log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);

                        if (ORD_QTY > ALLOC_QTY) {
                            ORD_QTY = ORD_QTY - ALLOC_QTY;
                        }

                        if (ORD_QTY.equals(ALLOC_QTY)) {
                            log.info("ORD_QTY fully allocated: " + ORD_QTY);
                            break outerloop; // If the Inventory satisfied the Ord_qty
                        }
                    }
                }
            }
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while orderAllocation V3: " + e);
            throw e;
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
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws Exception
     */
    @Transactional
    public OrderManagementLineV2 doAllocationV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                String itemCode, String loginUserID) throws Exception {
        log.info("UnAllocation Process started ---------------------> V9");

        List<OrderManagementLineV2> orderManagementLineV2s = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndStatusIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 47L, 0L);

        log.info("OrderManagementLine 47 Status ID Values ---------> " + orderManagementLineV2s);
        orderManagementLineV2Repository.deleteAll(orderManagementLineV2s);

        log.info("OutboundLine Delete Process ");
        int outboundLine = outboundLineV2Repository.deleteOutboundLineV9(companyCodeId, plantId, warehouseId, refDocNumber, itemCode, lineNumber);

        List<OrderManagementLineV2> orderList = new ArrayList<>();
        for(OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2s) {
            Long OB_ORD_TYP_ID = dbOrderManagementLine.getOutboundOrderTypeId();
            Double ORD_QTY = dbOrderManagementLine.getInventoryQty();
            Long BIN_CLASS_ID;
            List<OrderManagementLineV2> orderManagementLineV2List = null;
            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
                BIN_CLASS_ID = 1L;
                orderManagementLineV2List = updateAllocationV9(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        dbOrderManagementLine.getManufacturerName(), BIN_CLASS_ID, ORD_QTY,
                        dbOrderManagementLine, loginUserID);
            }

            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                orderManagementLineV2List = updateAllocationV9(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        dbOrderManagementLine.getManufacturerName(), BIN_CLASS_ID, ORD_QTY,
                        dbOrderManagementLine, loginUserID);
            }
            log.info("OrderAllocation Process Completed ------------------------------");
            log.info("PickupHeader Creation Process Started ------> ");
            if(orderManagementLineV2List != null) {
                orderList.addAll(orderManagementLineV2List);

                createPickupHeaderV9(orderManagementLineV2List);
            }
            log.info("PickupHeader Creation Process Completed ------> ");
        }
        if(orderList.isEmpty()) {
            OrderManagementLineV2 newOrder = new OrderManagementLineV2();
            newOrder.setRefDocNumber(refDocNumber);
            newOrder.setCompanyCodeId(companyCodeId);
            newOrder.setStatusId(47L);
            return newOrder;
        }
        return orderList.get(0);
    }


    // OrderAllocation Process
    public List<OrderManagementLineV2> updateAllocationV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                          OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {
        try {
            List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantitiesV9(orderManagementLine);
            log.info("Quantity Logic completed ----------> ");
            Double INCOMING_ORD_QTY = orderManagementLine.getOrderQty();
            Double RECEIVING_ORD_QTY = 0D;
            log.info("ORD_QTY is ------------------> {} ", ORD_QTY);
            Long stockTypeId = 1L;
            String INV_STRATEGY = null;
            String obStrategy = null;
            String fifoMethod = null;
            if (orderManagementLine.getBarcodeId() != null && !orderManagementLine.getBarcodeId().isEmpty()) {
                log.info("BarcodeId is ------> " + orderManagementLine.getBarcodeId());
                log.info("PalletId is -------->" + orderManagementLine.getReferenceField1());

//                INV_STRATEGY = "SB_BEST_FIT";
                INV_STRATEGY = "FIFO";

            } else {
                log.info("Starting Warehouse table call for Strategies");
                List<Object[]> strategyList = stagingLineV2Repository.getStrategy(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), warehouseId);
                if (strategyList != null && !strategyList.isEmpty()) {
                    Object[] strategy = strategyList.get(0); // GET_FIRST_RECORD
                    if (strategy.length > 0 && strategy[0] != null) {
                        obStrategy = strategy[0].toString();
                    }
                    if (strategy.length > 0 && strategy[1] != null) {
                        fifoMethod = strategy[1].toString();
                    }
                }
                log.info("OB_STRATEGY: {}, FIFO_MD: {}", obStrategy, fifoMethod);
                INV_STRATEGY = obStrategy;
            }

            // Inventory Strategy Choices
            if (INV_STRATEGY == null) {
                INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
            }

            ImBasicData1 imBasicData1V2 = imBasicData1V2Repository.getImBasicData1WeightV9(orderManagementLine.getItemCode(),
                    orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                    orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId());

            log.info("ImbasicData1 -----> {}", imBasicData1V2);

            Double weight = null;
            if (imBasicData1V2 != null && imBasicData1V2.getReferenceField1() != null) {
                weight = Double.valueOf(imBasicData1V2.getReferenceField1());
            }

            log.info("Weight ----> {}", weight);


            log.info("Allocation Strategy: " + INV_STRATEGY);
            int invQtyByLevelIdCount = 0;
            int invQtyGroupByLevelIdCount = 0;
            // Getting Inventory GroupBy ST_BIN wise
            List<IInventoryImpl> finalInventoryList = null;
            List<InventoryV2> inventoryV2List = null;
            double balanceOrderQty = orderManagementLine.getInventoryQty();
            if (INV_STRATEGY.equalsIgnoreCase("FIFO")) {
                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeNewV9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getBarcodeId(), orderManagementLine.getReferenceField1());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeNewV9Bin7(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getBarcodeId(), orderManagementLine.getReferenceField1());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    log.info("BarcodeId----->" + barcodeId);
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            companyCodeId, plantId, languageId, warehouseId, itemCode, inventory.getPalletCode(), barcodeId);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
                    double availableQty = Math.abs(invQty - alreadyAllocated);
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, orderLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    orderLine.setBarcodeId(barcodeId);
                    orderLine.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLine.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderManagementLine.getOutboundOrderTypeId() == 11) {
                        orderLine.setBarcodeId("Empty Crate");
                    }
                    orderLine.setManufacturerDate(inventory.getManufacturerDate());
                    orderLine.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLine.setExpiryDate(inventory.getExpiryDate());
                    orderLine.setInventoryQty(balanceOrderQty);
                    orderLine.setAllocatedQty(allocatedQty);
                    orderLine.setProposedStorageBin(inventory.getStorageBin());
                    orderLine.setItemCode(itemCode);
                    orderLine.setDescription(inventory.getDescription());
                    orderLine.setOrderQty(orderManagementLine.getOrderQty());
                    orderLine.setPalletId(inventory.getPalletCode());


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setManufacturerCode(orderManagementLine.getManufacturerCode());
                    orderLine.setManufacturerName(orderManagementLine.getManufacturerName());
                    orderLine.setManufacturerFullName(orderManagementLine.getManufacturerName());
                    orderLine.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLine.setMrp(inventory.getMrp());                                    //MRP
                    orderLine.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLine.setReferenceField10(inventory.getBrand());                        //totalWeight

                    orderLine.setOrigin(inventory.getOrigin());                                 //Origin

                    String inventoryOwner = imBasicData1V2Repository.getInventoryOwnerV9(orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                    if (inventoryOwner != null) {
                        orderLine.setMaterialNo(inventoryOwner);
                    }

                    // Net Weight ---> Price Segment
                    log.info("Weight -------> {}", weight);
                    Double roundWeight = weight != null ? weight : 0D;
                    Double allocQty = orderLine.getAllocatedQty() != null ? orderLine.getAllocatedQty() : 0D;

                    log.info("roundWeight  -------> {}", roundWeight);
                    log.info("allocQty -------> {}", allocQty);

                    Double priceSegment = roundWeight * allocQty;
                    orderLine.setPriceSegment(String.valueOf(priceSegment));

                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(),
                            orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
                    orderLine.setLineNumber(lineNumber);

                    orderManagementLineV2Repository.save(orderLine);
                    orderManagementLineV2List.add(orderLine);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderManagementLineV2List;
                    }
                }
            }
            if (INV_STRATEGY.equalsIgnoreCase("FEFO")) {

                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationNewV9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getReferenceField1());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBinNew7V9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getReferenceField1());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            companyCodeId, plantId, languageId, warehouseId, itemCode, inventory.getPalletCode(), barcodeId);

                    log.info("Sum Of Qty----->" + sumOfQty);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;

                    double availableQty = invQty - alreadyAllocated;
                    if(availableQty <= 0) {
                        availableQty = 0;
                    }
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }
                    log.info("BalanceOrderQty---->" + balanceOrderQty);

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, orderLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    orderLine.setBarcodeId(barcodeId);
                    orderLine.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLine.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderManagementLine.getOutboundOrderTypeId() == 11) {
                        orderLine.setBarcodeId("Empty Crate");
                    }

                    orderLine.setManufacturerDate(inventory.getManufacturerDate());
                    orderLine.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLine.setExpiryDate(inventory.getExpiryDate());
                    orderLine.setInventoryQty(balanceOrderQty);
                    orderLine.setAllocatedQty(allocatedQty);
                    orderLine.setProposedStorageBin(inventory.getStorageBin());
                    orderLine.setItemCode(itemCode);
                    orderLine.setDescription(inventory.getDescription());
                    orderLine.setOrderQty(orderManagementLine.getOrderQty());
                    orderLine.setPalletId(inventory.getPalletCode());
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    log.info("AllocatedQty---->" + allocatedQty);


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setManufacturerCode(orderManagementLine.getManufacturerCode());
                    orderLine.setManufacturerName(orderManagementLine.getManufacturerName());
                    orderLine.setManufacturerFullName(orderManagementLine.getManufacturerName());
                    orderLine.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLine.setMrp(inventory.getMrp());                                    //MRP
                    orderLine.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLine.setReferenceField5(inventory.getBrand());                        //totalWeight

                    orderLine.setOrigin(inventory.getOrigin());                                 //Origin

                    String inventoryOwner = imBasicData1V2Repository.getInventoryOwnerV9(orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                    if (inventoryOwner != null) {
                        orderLine.setMaterialNo(inventoryOwner);
                    }

                    // Net Weight ---> Price Segment
                    log.info("Weight -------> {}", weight);
                    Double roundWeight = weight != null ? weight : 0D;
                    Double allocQty = orderLine.getAllocatedQty() != null ? orderLine.getAllocatedQty() : 0D;

                    log.info("roundWeight  -------> {}", roundWeight);
                    log.info("allocQty -------> {}", allocQty);

                    Double priceSegment = roundWeight * allocQty;
                    orderLine.setPriceSegment(String.valueOf(priceSegment));

                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(),
                            orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
                    orderLine.setLineNumber(lineNumber);

                    log.info("OrderManagementLine------>" + orderLine);
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    orderManagementLineV2Repository.save(orderLine);
                    orderManagementLineV2List.add(orderLine);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderManagementLineV2List;
                    }
                }
            }

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            if (inventoryV2List == null || inventoryV2List.isEmpty() || (finalInventoryList == null || finalInventoryList.isEmpty()) || balanceOrderQty >= 0) {
                updateOrderManagementLineV2(orderManagementLine);
                return orderManagementLineV2List;
            }

            log.info("newOrderManagementLine updated ---#--->" + orderManagementLineV2List);
            return orderManagementLineV2List;
        } catch (Exception e) {
            log.error("Exception while updateAllocation V3: " + e);
            throw e;
        }
    }

    /**
     *
     * @param orderManagementLines orderLine
     */
    public void createPickupHeaderV9(List<OrderManagementLineV2> orderManagementLines) {

        log.info("OrderManagementList for PickupHeader -------------> {}", orderManagementLines.size());

        if (!orderManagementLines.isEmpty()) {

            long NUM_RAN_CODE = 10;
            String PU_NO = getNextRangeNumber(NUM_RAN_CODE, orderManagementLines.get(0).getCompanyCodeId(), orderManagementLines.get(0).getPlantId(),
                    orderManagementLines.get(0).getLanguageId(), orderManagementLines.get(0).getWarehouseId());
            log.info("----------New PU_NO--------> : " + PU_NO);

            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {

                OutboundLineV2 outboundLineV2 = new OutboundLineV2();
                BeanUtils.copyProperties(orderManagementLine, outboundLineV2, CommonUtils.getNullPropertyNames(orderManagementLine));
                outboundLineV2.setReferenceField1(orderManagementLine.getPalletId());
                outboundLineV2Repository.save(outboundLineV2);
                log.info("OutboundLine Creation Completed --------------> " + outboundLineV2);
                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                newPickupHeader.setPickupNumber(PU_NO);
                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, orderManagementLine.getLanguageId());
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                //Setting InventoryQuantity from orderManagementLine
                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());

                //Setting BagSize
                newPickupHeader.setBagSize(orderManagementLine.getInventoryQty());
                newPickupHeader.setNoBags(orderManagementLine.getNoBags());

                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
                newPickupHeader.setReferenceField2(orderManagementLine.getPalletId());
                newPickupHeader.setManufacturerDate(orderManagementLine.getManufacturerDate());
                newPickupHeader.setExpiryDate(orderManagementLine.getExpiryDate());
                newPickupHeader.setOrigin(orderManagementLine.getOrigin());                       //customerPallet
                PickupHeaderV2 createdPickupHeader = createOutboundOrderProcessingPickupHeaderV9(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLineV2Repository.updateOrderManagementLineV9(
                        orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), orderManagementLine.getPreOutboundNo(),
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());

                outboundHeaderV2Repository.updateOutboundHeaderStatusV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(),
                        orderManagementLine.getPreOutboundNo(), 48L, statusDescription);
                orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                        orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), orderManagementLine.getPreOutboundNo(), 48L, statusDescription);
            }
        }
    }

    /**
     *
     * @param newPickupHeader
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PickupHeaderV2 createOutboundOrderProcessingPickupHeaderV9(PickupHeaderV2 newPickupHeader, String loginUserID) {
        try {
            PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
            log.info("newPickupHeader : " + newPickupHeader);
            BeanUtils.copyProperties(newPickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(newPickupHeader));

            IKeyValuePair description = stagingLineV2Repository.getDescription(dbPickupHeader.getCompanyCodeId(),
                    dbPickupHeader.getLanguageId(),
                    dbPickupHeader.getPlantId(),
                    dbPickupHeader.getWarehouseId());

            if (dbPickupHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupHeader.getStatusId(), dbPickupHeader.getLanguageId());
                dbPickupHeader.setStatusDescription(statusDescription);
            }

            dbPickupHeader.setCompanyDescription(description.getCompanyDesc());
            dbPickupHeader.setPlantDescription(description.getPlantDesc());
            dbPickupHeader.setWarehouseDescription(description.getWarehouseDesc());

            statusDescription = stagingLineV2Repository.getStatusDescription(48L, dbPickupHeader.getLanguageId());
            outboundLineV2Repository.updateOutboundLineV2(dbPickupHeader.getCompanyCodeId(),
                    dbPickupHeader.getPlantId(),
                    dbPickupHeader.getLanguageId(),
                    dbPickupHeader.getWarehouseId(),
                    dbPickupHeader.getPreOutboundNo(),
                    dbPickupHeader.getRefDocNumber(),
                    dbPickupHeader.getPartnerCode(),
                    dbPickupHeader.getLineNumber(),
                    dbPickupHeader.getItemCode(),
                    48L,
                    statusDescription,
                    dbPickupHeader.getAssignedPickerId(),
                    dbPickupHeader.getManufacturerName(),
                    loginUserID,
                    new Date());

            String customerName = getCustomerName(dbPickupHeader.getCompanyCodeId(), dbPickupHeader.getPlantId(),
                    dbPickupHeader.getLanguageId(), dbPickupHeader.getWarehouseId(),
                    dbPickupHeader.getCustomerCode());
            if (customerName != null) {
                dbPickupHeader.setCustomerName(customerName);
            }
            dbPickupHeader.setDeletionIndicator(0L);
            dbPickupHeader.setPickupCreatedBy(loginUserID);
            dbPickupHeader.setPickupCreatedOn(new Date());
            PickupHeaderV2 pickupHeaderV2 = pickupHeaderV2Repository.save(dbPickupHeader);

            return pickupHeaderV2;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<OrderManagementLineV2> reAllocationOrderV9(List<OrderManagementLineV2> reAllocationLines, String loginUserID) {

        try {
            OrderManagementLineV2 orderLine = reAllocationLines.get(0);
            String companyId = orderLine.getCompanyCodeId();
            String plantId = orderLine.getPlantId();
            String warehouseId = orderLine.getWarehouseId();
            String refDocNo = orderLine.getRefDocNumber();
            String itemCode = orderLine.getItemCode();
            String languageId = orderLine.getLanguageId();

            List<String> pickupPallets = pickupLineV2Repository.findPickupLinePallet(companyId, plantId, warehouseId, refDocNo, itemCode);

            List<String> allPallets = orderManagementLineV2Repository.findAllPallets(companyId, plantId, warehouseId, refDocNo, itemCode);

            allPallets = allPallets.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            pickupPallets = pickupPallets.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("Pickup pallets: {}", pickupPallets);
            log.info("All pallets: {}", allPallets);

            if (pickupPallets == null || pickupPallets.isEmpty()) {
                log.info("No pickup pallets ---> deleting ALL pallets for RefDoc: {}, ItemCode: {}", refDocNo, itemCode);

                int orderDeleted = orderManagementLineV2Repository.deleteOrderMangementLine(companyId, plantId, warehouseId, refDocNo, itemCode);
                log.info("OrderManagementLine Deleted Rows: {}", orderDeleted);

                int outboundDeleted = outboundLineRepository.deleteOutboundLineV9(companyId, plantId, warehouseId, refDocNo, itemCode);
                log.info("OutboundLine Deleted Rows: {}", outboundDeleted);

                int pickupDeleted = pickupHeaderV2Repository.deletePickupHeaders(companyId, plantId, warehouseId, refDocNo, itemCode);
                log.info("PickupHeader Deleted Rows: {}", pickupDeleted);

            } else {
                //Delete only Non Pickup pallets
                for (String palletId : allPallets) {
                    if (!pickupPallets.contains(palletId)) {

                        log.info("OrderManagementLine Delete Process ------------> RefDocNo: {}, ItemCode: {}, PalletId: {}", refDocNo, itemCode, palletId);
                        int orderDeleted = orderManagementLineV2Repository.deleteOrderMangementLineByPallet(companyId, plantId, warehouseId, refDocNo, itemCode, palletId);
                        log.info("OrderManagementLine Delete Completed ------------> Affected Rows: {}", orderDeleted);

                        log.info("OutboundLine Delete Process ------------> RefDocNo: {}, ItemCode: {}, PalletId: {}", refDocNo, itemCode, palletId);
                        int outboundDeleted = outboundLineRepository.deleteOutboundLineByPallet(companyId, plantId, warehouseId, refDocNo, itemCode, palletId);
                        log.info("OutboundLine Delete Completed ------------> Affected Rows: {}", outboundDeleted);

                        log.info("PickupHeader Delete Process ------------> RefDocNo: {}, ItemCode: {}, PalletId: {}", refDocNo, itemCode, palletId);
                        int pickupHeaderDeleted = pickupHeaderV2Repository.deletePickupHeadersByPallet(companyId, plantId, warehouseId, refDocNo, itemCode, palletId);
                        log.info("PickupHeader Delete Completed ------------> Affected Rows: {}", pickupHeaderDeleted);

                        log.info("Deleted pallet: {}", palletId);

                    } else {
                        log.info("Skipping pickup pallet: {}", palletId);
                    }
                }
            }

            long NUM_RAN_CODE = 10;
            String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyId, plantId, "EN", warehouseId);
            log.info("----------New PU_NO--------> : " + PU_NO);

            log.info("reAllocationLines ---> " + reAllocationLines);

            log.info("reAllocationLines size----------> {}", reAllocationLines.size());

            List<IInventoryImpl> finalInventoryList = null;
            List<InventoryV2> inventoryV2List = null;


            for (OrderManagementLineV2 line : reAllocationLines) {

                double balanceOrderQty = line.getAllocatedQty();

                log.info("Inserting New pallet: {}", line.getPalletId());

                if (line.getOutboundOrderTypeId().equals(3L) || line.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9New(line.getCompanyCodeId(), line.getPlantId(), line.getLanguageId(),
                            warehouseId, itemCode, line.getBarcodeId(), line.getPalletId(), line.getManufacturerName());
                }
                if (line.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9Bin7New(line.getCompanyCodeId(), line.getPlantId(), line.getLanguageId(),
                            warehouseId, itemCode, line.getBarcodeId(), line.getPalletId(), line.getManufacturerName());
                }
                if (inventoryV2List == null || inventoryV2List.isEmpty()) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin2(line.getCompanyCodeId(), line.getPlantId(), line.getLanguageId(),
                            warehouseId, itemCode, line.getBarcodeId(), line.getPalletId(), line.getManufacturerName());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    log.info("companyId {}, plantId {}, languageId {}, warehouseId {}, itemCode {}, PalletId {}, BarcodeId {} ",
                            line.getCompanyCodeId(), line.getPlantId(), line.getLanguageId(), line.getWarehouseId(), line.getItemCode(), inventory.getPalletCode(), line.getBarcodeId());

                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            line.getCompanyCodeId(), line.getPlantId(), line.getLanguageId(), line.getWarehouseId(), line.getItemCode(), inventory.getPalletCode(), line.getBarcodeId());
                    log.info("Sum Of Qty----->" + sumOfQty);

                    double invAllocQty = inventory.getReferenceField4();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
                    double availableQty = invAllocQty - alreadyAllocated;
                    double allocatedQty = 0D;

                    if (availableQty <= 0) {
                        availableQty = 0;
                    }

                    if (alreadyAllocated > inventory.getInventoryQuantity()) {
                        log.info("AlreadyAllocated {} InventoryQuantity {} ", alreadyAllocated, inventory.getInventoryQuantity());
                        alreadyAllocated = inventory.getInventoryQuantity();
                    }

                    if (availableQty <= 0) {
                        availableQty = 0;
                    }

                    log.info("PalletId {}, InvAllocQty {}, AlreadyAllocated {}, Available {}",
                            line.getPalletId(), invAllocQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("PalletId {} has no available stock", line.getPalletId());
                        continue;
                    }

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }

                    OrderManagementLineV2 dbOrderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(line, dbOrderManagementLine, CommonUtils.getNullPropertyNames(line));
                    dbOrderManagementLine.setBarcodeId(barcodeId);
                    dbOrderManagementLine.setProposedPackBarCode(inventory.getPackBarcodes());
                    dbOrderManagementLine.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    dbOrderManagementLine.setManufacturerDate(inventory.getManufacturerDate());
                    dbOrderManagementLine.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    dbOrderManagementLine.setExpiryDate(inventory.getExpiryDate());
                    dbOrderManagementLine.setInventoryQty(balanceOrderQty);
                    dbOrderManagementLine.setAllocatedQty(allocatedQty);
                    dbOrderManagementLine.setProposedStorageBin(inventory.getStorageBin());
                    dbOrderManagementLine.setItemCode(itemCode);
                    dbOrderManagementLine.setDescription(inventory.getDescription());
                    dbOrderManagementLine.setOrderQty(line.getOrderQty());
                    dbOrderManagementLine.setPalletId(inventory.getPalletCode());
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    log.info("AllocatedQty---->" + allocatedQty);


                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(),
                            orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
                    if (lineNumber == null) {
                        lineNumber = orderLine.getLineNumber();
                    }
                    line.setLineNumber(lineNumber);

                    IKeyValuePair ikey = orderManagementLineRepository.getOrigin(line.getPalletId());

                    dbOrderManagementLine.setCompanyCodeId(line.getCompanyCodeId());
                    dbOrderManagementLine.setPlantId(line.getPlantId());
                    dbOrderManagementLine.setWarehouseId(line.getWarehouseId());
                    dbOrderManagementLine.setRefDocNumber(line.getRefDocNumber());
                    dbOrderManagementLine.setPreOutboundNo(line.getPreOutboundNo());
                    dbOrderManagementLine.setLineNumber(line.getLineNumber());
                    dbOrderManagementLine.setItemCode(line.getItemCode());
                    dbOrderManagementLine.setPartnerCode(line.getPartnerCode());
                    dbOrderManagementLine.setProposedStorageBin(line.getProposedStorageBin());
                    dbOrderManagementLine.setProposedPackBarCode(line.getProposedPackBarCode());
                    dbOrderManagementLine.setBarcodeId(line.getBarcodeId());
                    dbOrderManagementLine.setReferenceField1(line.getPalletId());

                    if (ikey != null) {
                        dbOrderManagementLine.setMaterialNo(ikey.getMaterialNo() != null ? ikey.getMaterialNo() : null);
                        dbOrderManagementLine.setOrigin(ikey.getOrigin() != null ? ikey.getOrigin() : null);
                        dbOrderManagementLine.setManufacturerDate(ikey.getMfrDate() != null ? ikey.getMfrDate() : null);
                        dbOrderManagementLine.setExpiryDate(ikey.getExpiryDate() != null ? ikey.getExpiryDate() : null);
                    }

                    log.info("Inserting OrderManagementLine pallet: {}", line.getPalletId());
                    OrderManagementLineV2 lineV2 = orderManagementLineV2Repository.save(dbOrderManagementLine);

                    OutboundLineV2 dbOutboundLine = new OutboundLineV2();
                    BeanUtils.copyProperties(lineV2, dbOutboundLine, CommonUtils.getNullPropertyNames(lineV2));
                    dbOutboundLine.setCompanyCodeId(lineV2.getCompanyCodeId());
                    dbOutboundLine.setPlantId(lineV2.getPlantId());
                    dbOutboundLine.setWarehouseId(lineV2.getWarehouseId());
                    dbOutboundLine.setRefDocNumber(lineV2.getRefDocNumber());
                    dbOutboundLine.setPreOutboundNo(lineV2.getPreOutboundNo());
                    dbOutboundLine.setLineNumber(lineV2.getLineNumber());
                    dbOutboundLine.setItemCode(lineV2.getItemCode());
                    dbOutboundLine.setPartnerCode(lineV2.getPartnerCode());
                    dbOutboundLine.setBarcodeId(lineV2.getBarcodeId());
                    dbOutboundLine.setDeliveryQty(0D);
                    dbOutboundLine.setLineNumber(lineV2.getLineNumber());
                    dbOutboundLine.setStatusId(lineV2.getStatusId());
                    dbOutboundLine.setQtyInCrate(lineV2.getQtyInCrate());
                    dbOutboundLine.setQtyInPiece(lineV2.getQtyInPiece());
                    dbOutboundLine.setQtyInCase(lineV2.getQtyInCase());
                    dbOutboundLine.setDescription(lineV2.getDescription());
                    statusDescription = getStatusDescription(lineV2.getStatusId(), lineV2.getLanguageId());
                    dbOutboundLine.setStatusDescription(statusDescription);
                    //            dbOutboundLine.setInvoiceDate(preOutboundHeaderV2.getRequiredDeliveryDate());
                    dbOutboundLine.setReferenceField1(lineV2.getPalletId());
                    dbOutboundLine.setReferenceField6(lineV2.getReferenceField6());     //GrossWeight
                    dbOutboundLine.setReferenceField10(lineV2.getReferenceField10());  //NetWeight
                    dbOutboundLine.setMrp(lineV2.getMrp());                              //MRP
                    dbOutboundLine.setReferenceField5(lineV2.getReferenceField5());       //totalWeight
                    if (ikey != null) {
                        dbOutboundLine.setTracking(ikey.getOrigin() != null ? ikey.getOrigin() : null);
                        dbOutboundLine.setReferenceField2(ikey.getMfrDate() != null ? String.valueOf(ikey.getMfrDate()) : null);
                        dbOutboundLine.setReferenceField8(ikey.getExpiryDate() != null ? String.valueOf(ikey.getExpiryDate()) : null);
                        dbOutboundLine.setBrand(ikey.getOrigin() != null ? String.valueOf(ikey.getOrigin()) : null);                         //CustomerPallet Inventory Origin
                        dbOutboundLine.setMaterialNo(ikey.getMaterialNo() != null ? String.valueOf(ikey.getMaterialNo()) : null);
                    }
                    dbOutboundLine.setReferenceField4(lineV2.getPalletId());
                    //PriceSegment
                    if (lineV2.getPriceSegment() != null) {
                        dbOutboundLine.setPriceSegment(lineV2.getPriceSegment());
                    }
                    if (dbOutboundLine.getOutboundOrderTypeId() == 3L) {
                        dbOutboundLine.setCustomerType("INVOICE");
                    }
                    if (dbOutboundLine.getOutboundOrderTypeId() == 1L) {
                        dbOutboundLine.setCustomerType("Transfer Out");
                    }
                    if (dbOutboundLine.getOutboundOrderTypeId() == 0L) {
                        dbOutboundLine.setCustomerType("TRANSVERSE");
                    }
                    log.info("Inserting OutboundLine pallet: {}", lineV2.getPalletId());

                    outboundLineRepository.save(dbOutboundLine);

                    PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(lineV2, dbPickupHeader, CommonUtils.getNullPropertyNames(lineV2));
                    dbPickupHeader.setCompanyCodeId(lineV2.getCompanyCodeId());
                    dbPickupHeader.setPlantId(lineV2.getPlantId());
                    dbPickupHeader.setWarehouseId(lineV2.getWarehouseId());
                    dbPickupHeader.setRefDocNumber(lineV2.getRefDocNumber());
                    dbPickupHeader.setPreOutboundNo(lineV2.getPreOutboundNo());
                    dbPickupHeader.setLineNumber(lineV2.getLineNumber());
                    dbPickupHeader.setItemCode(lineV2.getItemCode());
                    dbPickupHeader.setPartnerCode(lineV2.getPartnerCode());
                    dbPickupHeader.setBarcodeId(lineV2.getBarcodeId());
                    dbPickupHeader.setPickupNumber(PU_NO);
                    dbPickupHeader.setPickToQty(lineV2.getAllocatedQty());
                    dbPickupHeader.setPickUom(lineV2.getOrderUom());
                    dbPickupHeader.setBarcodeId(lineV2.getBarcodeId());

                    // STATUS_ID
                    dbPickupHeader.setStatusId(48L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, lineV2.getLanguageId());
                    dbPickupHeader.setStatusDescription(statusDescription);
                    // ProposedPackbarcode
                    dbPickupHeader.setProposedPackBarCode(lineV2.getProposedPackBarCode());
                    //Setting InventoryQuantity from lineV2
                    dbPickupHeader.setInventoryQuantity(lineV2.getInventoryQty());
                    //Setting BagSize
                    dbPickupHeader.setBagSize(lineV2.getInventoryQty());
                    dbPickupHeader.setNoBags(lineV2.getNoBags());
                    dbPickupHeader.setReferenceField5(lineV2.getDescription());
                    dbPickupHeader.setBatchSerialNumber(lineV2.getProposedBatchSerialNumber());
                    dbPickupHeader.setStorageSectionId(lineV2.getStorageSectionId());
                    dbPickupHeader.setReferenceField2(lineV2.getPalletId());
                    if (ikey != null) {
                        dbPickupHeader.setOrigin(ikey.getOrigin() != null ? ikey.getOrigin() : null);
                        dbPickupHeader.setManufacturerDate(ikey.getMfrDate() != null ? ikey.getMfrDate() : null);
                        dbPickupHeader.setExpiryDate(ikey.getExpiryDate() != null ? ikey.getExpiryDate() : null);
                        dbPickupHeader.setMaterialNo(ikey.getMaterialNo() != null ? ikey.getMaterialNo() : null);
                    }
                    dbPickupHeader.setPriceSegment(lineV2.getPriceSegment());

                    log.info("Inserting PickupHeader pallet: {}", lineV2.getPalletId());
                    pickupHeaderV2Repository.save(dbPickupHeader);

                    if (balanceOrderQty <= 0) {
                        break;
                    }
                }
                if (inventoryV2List == null || inventoryV2List.isEmpty() || (finalInventoryList == null || finalInventoryList.isEmpty()) || balanceOrderQty >= 0) {
                    updateOrderManagementLineV9(line, balanceOrderQty);
                }
            }

            log.info("ReAllocation Lines: {}", reAllocationLines.size());
            return reAllocationLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReAllocation failed: " + e.getMessage());
        }
    }

    /**
     * @param orderManagementLine
     * @return x
     */
    private OrderManagementLineV2 updateOrderManagementLineV9(OrderManagementLineV2 orderManagementLine, double balanceOrderQty) {
        log.info("UnAllocated Order :" + orderManagementLine.getRefDocNumber());
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setBarcodeId("");
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(balanceOrderQty);
        orderManagementLine.setAllocatedQty(0D);
        Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), orderManagementLine.getRefDocNumber(),
                orderManagementLine.getPreOutboundNo(), orderManagementLine.getItemCode());
        orderManagementLine.setLineNumber(lineNumber);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("UnAllocated orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param assignPickers
     * @param assignedPickerId
     * @param loginUserID
     * @return
     */
    public List<OrderManagementLineV2> doAssignPickerV9(List<AssignPickerV2> assignPickers, String assignedPickerId,
                                                        String loginUserID) throws java.text.ParseException, FirebaseMessagingException {
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
        String barcodeId = null;

        //push Notification
        Set<String> preOutboundNoList = new HashSet<>();
        Set<String> warehouseIdList = new HashSet<>();
        List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();

        Long STATUS_ID = 46L;
        String STATUS_TEXT = "PICKING INPROGRESS";
        pickupHeaderV2Repository.updatePartialPickupHeaderStatusV9(assignPickers.get(0).getCompanyCodeId(),
                assignPickers.get(0).getPlantId(), assignPickers.get(0).getWarehouseId(), assignPickers.get(0).getRefDocNumber(),
                STATUS_ID, STATUS_TEXT);

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
            barcodeId = assignPicker.getBarcodeId();

            //push notification
            preOutboundNoList.add(assignPicker.getPreOutboundNo());
            warehouseIdList.add(assignPicker.getWarehouseId());

            /**
             * Check for duplicates
             */
            PickupHeaderV2 dupPickupHeader = pickupHeaderV2Repository
                    .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId, 0L);
            log.info("duplicatePickUpHeader: " + dupPickupHeader);

            if (dupPickupHeader == null) {
                // OrderManagementLine Update Process
                OrderManagementLineV2 dbOrderManagementLine = orderManagementLineV2Repository
                        .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                                partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, barcodeId, 0L);
                log.info("orderManagementLine: " + dbOrderManagementLine);

                if (dbOrderManagementLine == null) {
                    throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                            + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                            + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                            + ",barcodeId:" + barcodeId  + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
                }

                //OutboundLine Update Process
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                outboundLineV2Repository.updateOutboundLineV5(48L, statusDescription, assignedPickerId, loginUserID, new Date(),
                        companyCodeId, plantId, warehouseId, languageId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
                log.info("outboundLine updated : " + outboundLine);

                // OutboundHeader Update
                OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode);
                outboundHeaderV2Repository.updateOutboundHeaderV5(48L, statusDescription, companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                log.info("outboundHeader updated : " + outboundHeader);

                // ORDERMANAGEMENTHEADER Update
                OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                        .getOrderManagementHeaderV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
                // OrderManagementHeaderUpdate
                orderManagementHeader.setStatusId(48L);
                orderManagementHeader.setStatusDescription(statusDescription);
                orderManagementHeaderV2Repository.save(orderManagementHeader);

                log.info("orderManagementHeader updated : " + orderManagementHeader);
                log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber());
                if (dbOrderManagementLine.getPickupNumber() == null) {
                    AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();

                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(),
                            dbOrderManagementLine.getLanguageId(), dbOrderManagementLine.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
                    log.info("PU_NO : " + PU_NO);


                    // Insertion of Record in PICKUPHEADER tables
                    PickupHeaderV2 pickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                    pickupHeader.setPickupNumber(PU_NO);
                    pickupHeader.setAssignedPickerId(assignedPickerId);
                    pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());
                    pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());
                    pickupHeader.setStatusId(46L);
                    pickupHeader.setStatusDescription("PICKING INPROGRESS");
                    pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());
                    pickupHeader.setPickupCreatedBy(loginUserID);
                    pickupHeader.setPickupCreatedOn(new Date());
                    pickupHeader.setReferenceField4(dbOrderManagementLine.getDescription());
                    pickupHeader.setQtyInCase(dbOrderManagementLine.getQtyInCase());
                    pickupHeader.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
                    pickupHeader.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
                    pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
                    pickupHeader.setReferenceField3(dbOrderManagementLine.getReferenceField3());
                    pickupHeader.setManufacturerDate(dbOrderManagementLine.getManufacturerDate());
                    pickupHeader.setExpiryDate(dbOrderManagementLine.getExpiryDate());
                    if (pickupHeader.getExpiryDate() != null) {
                        Date currentDate = new Date();
                        Date expiryDate = pickupHeader.getExpiryDate();

                        // Convert both dates to LocalDate
                        LocalDate localCurrentDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        // Calculate remaining days including today (+1)
                        long remainingDays = ChronoUnit.DAYS.between(localCurrentDate, localExpiryDate) + 1;
                        // Prevent negative remaining days (optional)
                        remainingDays = Math.max(remainingDays, 0);

                        pickupHeader.setRemainingDays(String.valueOf(remainingDays));
                    }

                    PickupHeaderV2 pickup = pickupHeaderV2Repository.save(pickupHeader);
                    log.info("pickupHeader created : " + pickup);
                    orderManagementLineV2Repository.updateOrderManagementLine(48L, statusDescription, new Date(), assignedPickerId, PU_NO,
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                            proposedPackCode, 0L);
                    log.info("OrderManagementLine updated : " + dbOrderManagementLine);
                }
                orderManagementLineList.add(dbOrderManagementLine);
            }
        }

        // Update statusId
        pickupHeaderV2Repository.updatePartialPickupHeaderStatusV9(assignPickers.get(0).getCompanyCodeId(),
                assignPickers.get(0).getPlantId(), assignPickers.get(0).getWarehouseId(), assignPickers.get(0).getRefDocNumber(),
                48L, statusDescription);

        try {
            String orderText = "PickupHeader Created Successfully";
            outboundOrderV2Repository.updatePickupHeaderProcessStatusId(assignPickers.get(0).getRefDocNumber(), orderText);
        } catch (Exception e) {
            log.error("Error while updating tblOborder2...");
        }

        //push notification separated from pickup header and consolidated notification sent
        if (preOutboundNoList != null && !preOutboundNoList.isEmpty() && warehouseIdList != null && !warehouseIdList.isEmpty()) {
            sendPushNotificationV5(preOutboundNoList, warehouseIdList);
        } else {
            sendPushNotification();
        }
        return orderManagementLineList;
    }

    // BF
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
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateV9(String companyCodeId, String plantId, String languageId, String warehouseId,
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
}