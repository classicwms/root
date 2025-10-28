package com.tekclover.wms.api.inbound.orders.service;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.v2.PerpetualHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.v2.PerpetualLineV2;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.v2.PerpetualZeroStockLine;
import com.tekclover.wms.api.inbound.orders.model.dto.*;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.inbound.orders.model.warehouse.cyclecount.perpetual.PerpetualLineV1;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesOrderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.stockAdjustment.StockAdjustment;
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

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import static java.lang.Math.abs;

@Service
@Slf4j
public class SalesOrderService extends BaseService {

    protected static final String LANG_ID = "EN";

    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    DbConfigRepository dbConfigRepository;
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
    PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    protected String statusDescription = null;
    @Autowired
    PropertiesConfig propertiesConfig;
    @Autowired
    MastersService mastersService;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    InventoryV2Repository inventoryV2Repository;
    @Autowired
    OrderService orderService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    PickListHeaderRepository pickListHeaderRepository;
    @Autowired
    PickupLineV2Repository pickupLineV2Repository;
    @Autowired
    InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    QualityLineV2Repository qualityLineV2Repository;
    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;
    @Autowired
    OutboundLineInterimRepository outboundLineInterimRepository;
        @Autowired
    PushNotificationService pushNotificationService;
    @Autowired
    PerpetualHeaderV2Repository perpetualHeaderV2Repository;
    @Autowired
    PerpetualZeroStkLineRepository perpetualZeroStkLineRepository;
    @Autowired
    PerpetualLineV2Repository perpetualLineV2Repository;
    @Autowired
    InventoryTransRepository inventoryTransRepository;


    @Autowired
    StockAdjustmentRepository stockAdjustmentRepository;

    /**
     * @param salesOrderList
     * @return
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<SalesOrderV2> createSalesOrderList(List<SalesOrderV2> salesOrderList) {
        List<SalesOrderV2> salesOrders = Collections.synchronizedList(new ArrayList<>());
        log.info("Outbound Process Start {} PickList", salesOrderList);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            for (SalesOrderV2 salesOrder : salesOrderList) {
                SalesOrderHeaderV2 header = salesOrder.getSalesOrderHeader();
                List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
                String companyCode = header.getCompanyCode();
                String plantId = header.getBranchCode();
                String newPickListNo = header.getPickListNumber();
                String orderType = lineV2List.get(0).getOrderType();

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
                            createPickListCancellation = orderService.pickListCancellationNew(companyCode, plantId, languageId, warehouseId, oldPickListNumber.getPickListNumber(), newPickListNo, oldPickListNumber.getPreOutboundNo(), "MW_AMS");
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
                OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(preOutboundHeader, orderManagementHeader.getStatusId(), header);
                log.info("OutboundHeader Created : " + outboundHeader);

                // Collections for batch saving
                List<PreOutboundLineV2> preOutboundLines = Collections.synchronizedList(new ArrayList<>());
                List<OutboundLineV2> outboundLines = Collections.synchronizedList(new ArrayList<>());
                List<OrderManagementLineV2> managementLines = Collections.synchronizedList(new ArrayList<>());


                // Process lines in parallel
//                List<CompletableFuture<Void>> futures = lineV2List.stream()
//                        .map(outBoundLine -> CompletableFuture.runAsync(() -> {
//                            try {
//                                processSingleSaleOrderLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
//                                        preOutboundLines, outboundLines, managementLines);
//                            } catch (Exception e) {
//                                log.error("Error processing PICK_LIST Line for SalesOrder: {}", header.getSalesOrderNumber(), e);
//                                throw new RuntimeException(e);
//                            }
//                        }, executorService))
//                        .collect(Collectors.toList());
//
//                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//                try {
//                    allFutures.join(); // Wait for all tasks to finish
//                } catch (CompletionException e) {
//                    log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
//                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//                }

                for (SalesOrderLineV2 outBoundLine : lineV2List) {
                    processSingleSaleOrderLine(outBoundLine, preOutboundHeader, orderManagementHeader, outboundHeader,
                            preOutboundLines, outboundLines, managementLines);
                }

                // Batch Save All Records
                preOutboundLineV2Repository.saveAll(preOutboundLines);
                orderManagementLineV2Repository.saveAll(managementLines);
                outboundLineV2Repository.saveAll(outboundLines);

                // No_Stock_Items
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
                orderManagementLineV2Repository.updateAllNoStockStatus(companyCode, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
                log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

                // PickupHeader for only PickList Order Only
                log.info("PickupHeaderAssignPicker initiated...");
                createPickUpHeaderAssignPickerModified(companyCode, plantId, languageId, warehouseId, header,
                        preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
                log.info("PickupHeaderAssignPicker completed...");

                if (createPickListCancellation != null) {
                    createPickListCancellation(companyCode, plantId, languageId, warehouseId,
                            createPickListCancellation.getOldPickListNumber(), createPickListCancellation.getNewPickListNumber(),
                            preOutboundNo, createPickListCancellation, "MW_AMS");
                }
                salesOrders.add(salesOrder);
            }
        } catch (
                Exception e) {
            log.error("Error processing outbound PICK_LIST Lines", e);
            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Outbound Process Completed for {} PickList", salesOrders.size());
        return salesOrders;
    }


    /**
     * @return
     */
    public String getPreOutboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        try {
            String nextRangeNumber = mastersService.getNextNumberRange(9L, warehouseId, companyCodeId, plantId, languageId);
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param headerV2
     * @param refField1ForOrderType
     * @return
     * @throws ParseException
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                          SalesOrderHeaderV2 headerV2, String refField1ForOrderType, String companyText, String plantText, String warehouseText) throws ParseException {
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(headerV2, preOutboundHeader, CommonUtils.getNullPropertyNames(headerV2));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
//        preOutboundHeader.setRefDocNumber(headerV2.getSalesOrderNumber());
        preOutboundHeader.setRefDocNumber(headerV2.getPickListNumber());                        // Changed from salesOrderNumber to PickListNumber
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(headerV2.getBranchCode());
        preOutboundHeader.setOutboundOrderTypeId(3L);
        preOutboundHeader.setCustomerCode(headerV2.getCustomerId());
        preOutboundHeader.setTransferRequestType("PICK LIST");
        preOutboundHeader.setCompanyDescription(companyText);
        preOutboundHeader.setPlantDescription(plantText);
        preOutboundHeader.setWarehouseDescription(warehouseText);
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);
        preOutboundHeader.setMiddlewareId(headerV2.getMiddlewareId());
        preOutboundHeader.setMiddlewareTable(headerV2.getMiddlewareTable());
        preOutboundHeader.setReferenceDocumentType("PICK LIST");
        preOutboundHeader.setSalesOrderNumber(headerV2.getSalesOrderNumber());
        preOutboundHeader.setPickListNumber(headerV2.getPickListNumber());
        preOutboundHeader.setTokenNumber(headerV2.getTokenNumber());
        preOutboundHeader.setTargetBranchCode(headerV2.getBranchCode());
        preOutboundHeader.setFromBranchCode(headerV2.getBranchCode());

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
        newOrderManagementHeader.setReferenceField7(statusDescription);
        return orderManagementHeaderV2Repository.save(newOrderManagementHeader);
    }

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @param salesOrderHeaderV2
     * @return
     * @throws ParseException
     */
    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, SalesOrderHeaderV2 salesOrderHeaderV2) throws ParseException {
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


    // Create_Pc


    // Create_Pc

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param headerV2
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @throws FirebaseMessagingException
     * @throws Exception
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        SalesOrderHeaderV2 headerV2,
                                                        String preOutboundNo, String refDocNumber, String partnerCode) throws FirebaseMessagingException, Exception {
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

                        Long OB_ORD_TYP_ID = 3L;
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
                    if (warehouseId.equalsIgnoreCase("200") || warehouseId.equalsIgnoreCase("300")) {
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

                    String PU_NO = mastersService.getNextNumberRange(10L, warehouseId, companyCodeId, plantId, languageId);
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
                    newPickupHeader.setSalesOrderNumber(headerV2.getSalesOrderNumber());
                    newPickupHeader.setPickListNumber(headerV2.getPickListNumber());
                    newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType("SalesOrder");        // Need to set dynamically
                    newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                    newPickupHeader.setTokenNumber(headerV2.getTokenNumber());
                    newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                    newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                    newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                    newPickupHeader.setImsSaleTypeCode(orderManagementLine.getImsSaleTypeCode());

                    newPickupHeader.setFromBranchCode(headerV2.getBranchCode());
                    newPickupHeader.setCustomerCode(headerV2.getCustomerId());
                    newPickupHeader.setTransferRequestType("SalesOrder");       // Need to set dynamically

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

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param refDocNumber
//     * @return
//     */
//    public List<OrderManagementLineV2> getOrderManagementLineForPickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                             String preOutboundNo, String refDocNumber) {
//        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
//                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
//                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
//        if (orderManagementHeader != null) {
//            return orderManagementHeader;
//        }
//        return null;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param outboundOrderTypeId
//     * @return
//     */
//    public PickupHeaderV2 getPickupHeaderAutomationByOutboundOrderTypeId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long outboundOrderTypeId) {
//        PickupHeaderV2 header =
//                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndOutboundOrderTypeIdAndDeletionIndicatorOrderByPickupCreatedOn(
//                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 48L, outboundOrderTypeId, 0L);
//        return header;
//    }
//
    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @param refDocNumber
     * @return
     * @throws java.text.ParseException
     */
    public PickupHeaderV2 getPickupHeaderAutomateCurrentDateSameOrderNew(String companyCodeId, String plantId, String languageId,
                                                                         String warehouseId, String assignedPickerId, String refDocNumber)
            throws java.text.ParseException {

        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());

        PickupHeaderV2 header =
                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndRefDocNumberAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, refDocNumber, 48L, dates[0], dates[1], 0L);
        return header;
    }

//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param assignedPickerId
//     * @return
//     */
//    public List<PickupHeaderV2> getPickupHeaderAutomationWithOutStatusIdV2(String companyCodeId, String plantId, String languageId,
//                                                                           String warehouseId, String assignedPickerId) {
//        List<PickupHeaderV2> header =
//                pickupHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndDeletionIndicator(
//                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, 0L);
//        return header;
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param assignedPickerId
//     * @return
//     */
//    public List<PickupHeaderV2> getPickupHeaderAutomation(String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId, Long statusId) throws java.text.ParseException {
//        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());
//        List<PickupHeaderV2> header =
//                pickupHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
//                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, statusId, dates[0], dates[1], 0L);
//        return header;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @return
//     */
//    public PickupHeaderV2 getPickupHeaderAutomationByLevelId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String levelId) {
//        PickupHeaderV2 header =
//                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndLevelIdAndDeletionIndicatorOrderByPickupCreatedOn(
//                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 48L, levelId, 0L);
//        return header;
//    }
//
//    /**
//     *
//     * @param newPickupHeader
//     * @param loginUserID
//     * @return
//     */
//    public PickupHeaderV2 createOutboundOrderProcessingPickupHeaderV2(PickupHeaderV2 newPickupHeader, String loginUserID) throws Exception {
//        try {
//            PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
//            log.info("newPickupHeader : " + newPickupHeader);
//            BeanUtils.copyProperties(newPickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(newPickupHeader));
//
//            IKeyValuePair description = stagingLineV2Repository.getDescription(dbPickupHeader.getCompanyCodeId(),
//                    dbPickupHeader.getLanguageId(),
//                    dbPickupHeader.getPlantId(),
//                    dbPickupHeader.getWarehouseId());
//
//            if (dbPickupHeader.getStatusId() != null) {
//                statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupHeader.getStatusId(), dbPickupHeader.getLanguageId());
//                dbPickupHeader.setStatusDescription(statusDescription);
//            }
//
//            dbPickupHeader.setCompanyDescription(description.getCompanyDesc());
//            dbPickupHeader.setPlantDescription(description.getPlantDesc());
//            dbPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
//
//            statusDescription = stagingLineV2Repository.getStatusDescription(48L, dbPickupHeader.getLanguageId());
//            outboundLineV2Repository.updateOutboundLineV2(dbPickupHeader.getCompanyCodeId(),
//                    dbPickupHeader.getPlantId(),
//                    dbPickupHeader.getLanguageId(),
//                    dbPickupHeader.getWarehouseId(),
//                    dbPickupHeader.getPreOutboundNo(),
//                    dbPickupHeader.getRefDocNumber(),
//                    dbPickupHeader.getPartnerCode(),
//                    dbPickupHeader.getLineNumber(),
//                    dbPickupHeader.getItemCode(),
//                    48L,
//                    statusDescription,
//                    dbPickupHeader.getAssignedPickerId(),
//                    dbPickupHeader.getManufacturerName(),
//                    loginUserID,
//                    new Date());
//
//            dbPickupHeader.setDeletionIndicator(0L);
//            dbPickupHeader.setPickupCreatedBy(loginUserID);
//            dbPickupHeader.setPickupCreatedOn(new Date());
//            PickupHeaderV2 pickupHeaderV2 =  pickupHeaderV2Repository.save(dbPickupHeader);
//
//            return pickupHeaderV2;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }


    /**
     * @param salesOrderLineV2
     * @param preOutboundHeaderV2
     * @param orderManagementHeaderV2
     * @param outboundHeaderV2
     * @param preOutboundLineV2
     * @param outboundLineV2
     * @param orderManagementLineV2
     * @throws Exception
     */
    private void processSingleSaleOrderLine(SalesOrderLineV2 salesOrderLineV2, PreOutboundHeaderV2 preOutboundHeaderV2,
                                            OrderManagementHeaderV2 orderManagementHeaderV2, OutboundHeaderV2 outboundHeaderV2,
                                            List<PreOutboundLineV2> preOutboundLineV2, List<OutboundLineV2> outboundLineV2,
                                            List<OrderManagementLineV2> orderManagementLineV2) throws Exception {

        // Collect PreOutboundLine
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(preOutboundHeaderV2, salesOrderLineV2);
        preOutboundLineV2.add(preOutboundLine);
        log.info("PreOutbound Line {} Created Successfully -------------------> ", preOutboundLine);

        // Collect OrderManagementLine
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(orderManagementHeaderV2, preOutboundLine);
        orderManagementLineV2.add(orderManagementLine);
        log.info("OrderManagement Line {} Created Successfully -----------------------> ", orderManagementLine);

        // Collect InboundLine
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        OutboundLineV2 outboundLine = createOutboundLineV2(orderManagementLine, outboundHeaderV2);
        outboundLineV2.add(outboundLine);
        log.info("OutboundLine {} Created Successfully ------------------------------> ", outboundLine);

    }


    /**
     * @param preOutboundHeaderV2
     * @param salesOrderLineV2
     * @return
     * @throws ParseException
     */
    private PreOutboundLineV2 createPreOutboundLineV2(PreOutboundHeaderV2 preOutboundHeaderV2, SalesOrderLineV2 salesOrderLineV2) throws ParseException {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(preOutboundHeaderV2, preOutboundLine, CommonUtils.getNullPropertyNames(preOutboundHeaderV2));
        BeanUtils.copyProperties(salesOrderLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(salesOrderLineV2));
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
        preOutboundLine.setSalesInvoiceNumber(salesOrderLineV2.getSalesOrderNo());
        preOutboundLine.setPickListNumber(salesOrderLineV2.getPickListNo());
        preOutboundLine.setSalesOrderNumber(salesOrderLineV2.getSalesOrderNo());
        preOutboundLine.setSupplierInvoiceNo(salesOrderLineV2.getSalesOrderNo());
        preOutboundLine.setTransferOrderNo(salesOrderLineV2.getSalesOrderNo());
        preOutboundLine.setReturnOrderNo(salesOrderLineV2.getSalesOrderNo());
        preOutboundLine.setOrderQty(salesOrderLineV2.getOrderedQty());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(preOutboundHeaderV2.getCompanyCodeId());
        imBasicData.setPlantId(preOutboundHeaderV2.getPlantId());
        imBasicData.setLanguageId(preOutboundHeaderV2.getLanguageId());
        imBasicData.setWarehouseId(preOutboundHeaderV2.getWarehouseId());
        imBasicData.setItemCode(salesOrderLineV2.getSku());
//        imBasicData.setManufacturerName(salesOrderLineV2.getManufacturerName());
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null && imBasicData1.getDescription() != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
        } else {
            preOutboundLine.setDescription(salesOrderLineV2.getSkuDescription());
        }

        log.info("Barcode input : itemCode ---> " + preOutboundLine.getItemCode() + ", companyCodeId ---> " + preOutboundLine.getCompanyCodeId() +
                ", plantId ---> " + preOutboundLine.getPlantId() + ", warehouseId ---> " + preOutboundLine.getWarehouseId() + ", ManufacturerName ---> " +
                preOutboundLine.getManufacturerName() + ", languageId ---> " + preOutboundLine.getLanguageId());
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
        preOutboundLine.setReferenceField1("PICK LIST");
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());
        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }

    /**
     * @param headerV2
     * @param preOutboundLine
     * @return
     * @throws ParseException
     */
    private OrderManagementLineV2 createOrderManagementLineV2(OrderManagementHeaderV2 headerV2, PreOutboundLineV2 preOutboundLine) throws ParseException {
        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
        orderManagementLine.setPickupCreatedOn(new Date());

        log.info("Barcode input : itemCode ---> " + preOutboundLine.getItemCode() + ", companyCodeId ---> " + preOutboundLine.getCompanyCodeId() +
                ", plantId ---> " + preOutboundLine.getPlantId() + ", warehouseId ---> " + preOutboundLine.getWarehouseId() + ", ManufacturerName ---> " +
                preOutboundLine.getManufacturerName() + ", languageId ---> " + preOutboundLine.getLanguageId());
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
                getInventoryForOrderManagementV2(headerV2.getCompanyCodeId(), headerV2.getPlantId(), headerV2.getLanguageId(), headerV2.getWarehouseId(),
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
            return orderManagementLine;
        }

        orderManagementLine = updateAllocationV2(orderManagementLine, 1L, preOutboundLine.getOrderQty(),
                preOutboundLine.getWarehouseId(), preOutboundLine.getItemCode(), "ORDER_PLACED");

        // PROP_ST_BIN
        return orderManagementLine;
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
     * @param orderManagementLine
     * @param binClassId
     * @param ORD_QTY
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws java.text.ParseException
     */
    public OrderManagementLineV2 updateAllocationV2(OrderManagementLineV2 orderManagementLine, Long binClassId,
                                                    Double ORD_QTY, String warehouseId, String itemCode, String loginUserID) throws java.text.ParseException {
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
                    //outerloop1:
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
                            break; // If the Inventory satisfied the Ord_qty
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

//        outerloop:
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
                break; // If the Inventory satisfied the Ord_qty
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     * @param newPreOutboundNo
     * @param pickListCancellation
     * @param loginUserID
     * @throws Exception
     */
    public void createPickListCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                           String oldPickListNumber, String newPickListNumber, String newPreOutboundNo,
                                           PickListCancellation pickListCancellation,
                                           String loginUserID) throws Exception {
        List<PickupLineV2> pickupLineV2 = pickListCancellation.getOldPickupLineList();
        List<QualityLineV2> qualityLineV2 = pickListCancellation.getOldQualityLineList();
        OutboundHeaderV2 outboundHeaderV2 = pickListCancellation.getOldOutboundHeader();
        List<OutboundLineV2> outboundLineV2 = pickListCancellation.getOldOutboundLineList();
        List<OrderManagementLineV2> orderManagementLine = pickListCancellation.getOldOrderManagementLineList();
        //PickUpLine Creation
        List<PickupLineV2> createNewPickUpLineList = null;
        if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
            List<PickupHeaderV2> newPickupHeaderList = orderService.getPickupHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("newPickupHeaderList: " + newPickupHeaderList);

            List<AddPickupLine> newPickupLineList = new ArrayList<>();
            if (newPickupHeaderList != null && !newPickupHeaderList.isEmpty()) {
                for (PickupHeaderV2 pickupHeaderV2 : newPickupHeaderList) {
                    List<PickupLineV2> pickupLinePresent =
                            pickupLineV2
                                    .stream()
                                    .filter(n -> n.getItemCode().equalsIgnoreCase(pickupHeaderV2.getItemCode()) && n.getManufacturerName().equalsIgnoreCase(pickupHeaderV2.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Pickup Line present for that itemCode & MFR_Name: " + pickupLinePresent);

                    if (pickupLinePresent != null && !pickupLinePresent.isEmpty()) {
                        AddPickupLine newPickupLine = new AddPickupLine();
                        BeanUtils.copyProperties(pickupHeaderV2, newPickupLine, CommonUtils.getNullPropertyNames(pickupHeaderV2));
                        Double oldPickConfirmQty = pickupLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = pickupHeaderV2.getPickToQty();
                        if (oldPickConfirmQty < newPickConfirmQty) {
                            newPickupLine.setPickConfirmQty(oldPickConfirmQty);
                            newPickupLine.setAllocatedQty(oldPickConfirmQty);
                            newPickupLine.setPickQty(String.valueOf(oldPickConfirmQty));
                        }
                        if (oldPickConfirmQty >= newPickConfirmQty) {
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
            if (newPickupLineList != null && !newPickupLineList.isEmpty()) {
                createNewPickUpLineList = createPickupLineNonCBMPickListCancellationV2(newPickupLineList, loginUserID);
                log.info("CreatedPickUpLine List : " + createNewPickUpLineList);
            }
        }

        //Quality Line Creation
        if (qualityLineV2 != null && !qualityLineV2.isEmpty()) {
            List<QualityHeaderV2> newQualityHeaderList = orderService.getQualityHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("NewQualityHeaderList: " + newQualityHeaderList);

            List<AddQualityLineV2> newQualityLineList = new ArrayList<>();
            if (newQualityHeaderList != null && !newQualityHeaderList.isEmpty()) {
                for (QualityHeaderV2 qualityHeader : newQualityHeaderList) {
                    List<QualityLineV2> qualityLinePresent =
                            qualityLineV2
                                    .stream()
                                    .filter(n -> n.getItemCode().equalsIgnoreCase(qualityHeader.getReferenceField4()) && n.getManufacturerName().equalsIgnoreCase(qualityHeader.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Quality Line Present for that itemCode and MFR_Name: " + qualityLinePresent);

                    if (qualityLinePresent != null && !qualityLinePresent.isEmpty()) {
                        AddQualityLineV2 newQualityLine = new AddQualityLineV2();
                        BeanUtils.copyProperties(qualityHeader, newQualityLine, CommonUtils.getNullPropertyNames(qualityHeader));

                        Double oldPickConfirmQty = qualityLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = Double.valueOf(qualityHeader.getQcToQty());
                        if (oldPickConfirmQty < newPickConfirmQty) {
//                            Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
                            newQualityLine.setPickConfirmQty(oldPickConfirmQty);
                            newQualityLine.setQualityQty(Double.valueOf(oldPickConfirmQty));
                        }
                        if (oldPickConfirmQty >= newPickConfirmQty) {
                            newQualityLine.setPickConfirmQty(Double.valueOf(newPickConfirmQty));
                            newQualityLine.setQualityQty(Double.valueOf(newPickConfirmQty));
                        }

                        newQualityLine.setDescription(qualityHeader.getReferenceField3());
                        newQualityLine.setItemCode(qualityHeader.getReferenceField4());
                        newQualityLine.setManufacturerName(qualityHeader.getManufacturerName());
                        newQualityLine.setPickPackBarCode(qualityHeader.getReferenceField2());
                        newQualityLine.setQualityInspectionNo(qualityHeader.getQualityInspectionNo());
                        if (qualityHeader.getReferenceField5() != null) {
                            newQualityLine.setLineNumber(Long.valueOf(qualityHeader.getReferenceField5()));
                        }
                        newQualityLineList.add(newQualityLine);
                    }
                }
            }
            if (newQualityLineList != null && !newQualityLineList.isEmpty()) {
                List<QualityLineV2> createNewQualityLineList = createQualityLineV2(newQualityLineList, loginUserID);
                log.info("Created Quality Line List : " + createNewQualityLineList);
            }
        }
        log.info("Pick List Cancellation Completed");
        log.info("Stored procedure call to update cnf_by in pickup line, qc header and line : " + oldPickListNumber + ", " + outboundHeaderV2.getPreOutboundNo() + ", " + newPickListNumber + ", " + newPreOutboundNo + ", " + outboundHeaderV2.getSalesOrderNumber());
        pickListHeaderRepository.updatePickupLineQualityHeaderLineCnfByUpdateProc(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, outboundHeaderV2.getPreOutboundNo(), newPickListNumber, newPreOutboundNo, outboundHeaderV2.getSalesOrderNumber());
        log.info("SP update done");
        insertNewPickListCancelRecord(outboundHeaderV2, outboundLineV2, pickupLineV2, createNewPickUpLineList, orderManagementLine, companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, newPickListNumber);
    }

    /**
     * @param newPickupLines
     * @return
     */
    public static List<AddPickupLine> getDuplicatesV2(@Valid List<AddPickupLine> newPickupLines) {
        return getDuplicatesMapV2(newPickupLines).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param qualityLineList
     * @return
     */
    public static List<AddQualityLineV2> getDuplicatesQualityLine(List<AddQualityLineV2> qualityLineList) {
        return getDuplicatesMapQuality(qualityLineList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param addQualityLineList
     * @return
     */
    private static Map<String, List<AddQualityLineV2>> getDuplicatesMapQuality(List<AddQualityLineV2> addQualityLineList) {
        return addQualityLineList.stream().collect(Collectors.groupingBy(AddQualityLineV2::uniqueAttributes));
    }

    /**
     * @param newPickupLines
     * @return
     */
    private static Map<String, List<AddPickupLine>> getDuplicatesMapV2(@Valid List<AddPickupLine> newPickupLines) {
        return newPickupLines.stream().collect(Collectors.groupingBy(AddPickupLine::uniqueAttributes));
    }

    /**
     * @param newPickupLines
     * @param loginUserID
     * @return
     */
    @Transactional
    public List<PickupLineV2> createPickupLineNonCBMPickListCancellationV2(@Valid List<AddPickupLine> newPickupLines, String loginUserID) {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Long STATUS_ID = 0L;
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String pickupNumber = null;
        String itemCode = null;
        String manufacturerName = null;
        boolean isQtyAvail = false;

        List<AddPickupLine> dupPickupLines = getDuplicatesV2(newPickupLines);
        log.info("-------dupPickupLines--------> " + dupPickupLines);
        if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
            newPickupLines.removeAll(dupPickupLines);
            newPickupLines.add(dupPickupLines.get(0));
            log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
        }

        // Create PickUpLine
        List<PickupLineV2> createdPickupLineList = new ArrayList<>();
        for (AddPickupLine newPickupLine : newPickupLines) {
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));

            dbPickupLine.setLanguageId(newPickupLine.getLanguageId());
            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));
            dbPickupLine.setPlantId(newPickupLine.getPlantId());

            // STATUS_ID
            if (newPickupLine.getPickConfirmQty() > 0) {
                isQtyAvail = true;
            }

            if (isQtyAvail) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, newPickupLine.getLanguageId());
            dbPickupLine.setStatusDescription(statusDescription);

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getPlantId(),
                    newPickupLine.getWarehouseId());
            if (description != null) {
                dbPickupLine.setCompanyDescription(description.getCompanyDesc());
                dbPickupLine.setPlantDescription(description.getPlantDesc());
                dbPickupLine.setWarehouseDescription(description.getWarehouseDesc());
            }
            OrderManagementLineV2 dbOrderManagementLine = orderService.getOrderManagementLineForQualityLineV2(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getPlantId(),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getWarehouseId(),
                    newPickupLine.getPreOutboundNo(),
                    newPickupLine.getRefDocNumber(),
                    newPickupLine.getLineNumber(),
                    newPickupLine.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                dbPickupLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                dbPickupLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                dbPickupLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                dbPickupLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
                dbPickupLine.setImsSaleTypeCode(dbOrderManagementLine.getImsSaleTypeCode());
            }

            PickupHeaderV2 dbPickupHeader = orderService.getPickupHeaderV2(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber());
            if (dbPickupHeader != null) {
                dbPickupLine.setPickupCreatedOn(dbPickupHeader.getPickupCreatedOn());
                if (dbPickupHeader.getPickupCreatedBy() != null) {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickupCreatedBy());
                } else {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickUpdatedBy());
                }
            }

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setBarcodeId(newPickupLine.getBarcodeId());
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
                    dbPickupLine.getLanguageId(), dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(), dbPickupLine.getPickedPackCode(),
                    0L);

            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                        dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                PickupLineV2 createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        }

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        for (PickupLineV2 dbPickupLine : createdPickupLineList) {

            //------------------------UpdateLock-Applied------------------------------------------------------------
            InventoryV2 inventory = inventoryService.getInventoryV2(dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(), dbPickupLine.getManufacturerName());
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty()) - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);
                        inventory.setReferenceField4(INV_QTY + ALLOC_QTY);
                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
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

                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                                        dbPickupLine.getWarehouseId(), inventory.getStorageBin());
                                if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                    StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(),
                                            dbPickupLine.getCompanyCodeId(),
                                            dbPickupLine.getPlantId(),
                                            dbPickupLine.getLanguageId(),
                                            authTokenForMastersService.getAccess_token());

                                    if (dbStorageBin != null) {

                                        dbStorageBin.setStatusId(0L);
                                        log.info("Bin Emptied");

                                        mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                                authTokenForMastersService.getAccess_token());
                                        log.info("Bin Update Success");
                                    }
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setReferenceField4(INV_QTY);
                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
                        try {
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
                        } catch (Exception e) {
                            log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                            e.printStackTrace();
                            InventoryTrans newInventoryTrans = new InventoryTrans();
                            BeanUtils.copyProperties(newInventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(newInventoryV2));
                            newInventoryTrans.setReRun(0L);
                            InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                            log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                        }
                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {
                                // Setting up statusId = 0
                                StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(),
                                        dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(),
                                        dbPickupLine.getLanguageId(),
                                        authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);

                                mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

            // Inserting record in InventoryMovement
            Long subMvtTypeId;
            String movementDocumentNo;
            String stBin;
            String movementQtyValue;
            InventoryMovement inventoryMovement;
            try {
                subMvtTypeId = 1L;
                movementDocumentNo = dbPickupLine.getPickupNumber();
                stBin = dbPickupLine.getPickedStorageBin();
                movementQtyValue = "N";
                inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);
            } catch (Exception e) {
                log.error("InventoryMovement create Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
                if (dbPickupLine.getAssignedPickerId() == null) {
                    dbPickupLine.setAssignedPickerId("0");
                }
                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, dbPickupLine.getLanguageId());
                outboundLineV2Repository.updateOutboundlineStatusUpdateProc(
                        dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                        dbPickupLine.getWarehouseId(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPreOutboundNo(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
                        dbPickupLine.getLineNumber(), STATUS_ID, statusDescription, new Date());
                log.info("outboundLine updated using Stored Procedure: ");
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

                    Long NUM_RAN_CODE = 11L;
                    QC_NO = mastersService.getNextNumberRange(NUM_RAN_CODE, dbPickupLine.getWarehouseId(), dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                            dbPickupLine.getLanguageId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
                    newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());

                    newQualityHeader.setManufacturerName(dbPickupLine.getManufacturerName());
                    newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setReferenceDocumentType(dbPickupLine.getReferenceDocumentType());
                    newQualityHeader.setPickListNumber(dbPickupLine.getPickListNumber());
                    newQualityHeader.setSalesInvoiceNumber(dbPickupLine.getSalesInvoiceNumber());
                    newQualityHeader.setSalesOrderNumber(dbPickupLine.getSalesOrderNumber());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setSupplierInvoiceNo(dbPickupLine.getSupplierInvoiceNo());
                    newQualityHeader.setTokenNumber(dbPickupLine.getTokenNumber());


                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
//                    StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    statusDescription = stagingLineV2Repository.getStatusDescription(54L, dbPickupLine.getLanguageId());
                    newQualityHeader.setReferenceField10(statusDescription);
                    newQualityHeader.setStatusDescription(statusDescription);

                    QualityHeaderV2 createdQualityHeader = orderService.createQualityHeaderV2(newQualityHeader, loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
            companyCodeId = dbPickupLine.getCompanyCodeId();
            plantId = dbPickupLine.getPlantId();
            languageId = dbPickupLine.getLanguageId();
            itemCode = dbPickupLine.getItemCode();
            manufacturerName = dbPickupLine.getManufacturerName();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 51 only if all OutboundLines are STATUS_ID is 51
         */
        String statusDescription50 = stagingLineV2Repository.getStatusDescription(50L, languageId);
        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
                loginUserID, 47L, 50L, 51L, statusDescription50, statusDescription51);
        log.info("outboundHeader, preOutboundHeader updated as 50 / 51 when respective condition met");

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go to update header-----------*/
        try {
            boolean isStatus51 = false;
            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
                    .collect(Collectors.toList());
            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
            log.info("status count : " + (statusIdCount == statusList.size()));
            isStatus51 = (statusIdCount == statusList.size());
            if (!statusList.isEmpty() && isStatus51) {
                STATUS_ID = 51L;
            } else {
                STATUS_ID = 50L;
            }
            //------------------------UpdateLock-Applied------------------------------------------------------------
            for (PickupLineV2 dbPickupLine : createdPickupLineList) {
                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProc(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        partnerCode, dbPickupLine.getPickupNumber(), STATUS_ID, statusDescription, loginUserID, new Date());
                log.info("PickupNumber: " + dbPickupLine.getPickupNumber());

                PickupHeaderV2 pickupHeader = orderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode, dbPickupLine.getPickupNumber());
                Double headerPickToQty = pickupHeader.getPickToQty();
                Double pickupLineQty = pickupLineV2Repository.getPickupLineSumV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, dbPickupLine.getPickupNumber(), 50L, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName());

                if (pickupLineQty < headerPickToQty) {
                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(pickupHeader, newPickupHeader, CommonUtils.getNullPropertyNames(pickupHeader));
                    long NUM_RAN_CODE = 10;
                    String PU_NO = mastersService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId);
                    log.info("PU_NO : " + PU_NO);

                    newPickupHeader.setAssignedPickerId(pickupHeader.getAssignedPickerId());
                    newPickupHeader.setPickupNumber(PU_NO);

                    Double pickToQty = headerPickToQty - pickupLineQty;
                    newPickupHeader.setPickToQty(pickToQty);
                    // PICK_UOM
                    newPickupHeader.setPickUom(pickupHeader.getPickUom());

                    // STATUS_ID
                    newPickupHeader.setStatusId(48L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                    newPickupHeader.setStatusDescription(statusDescription);

                    // ProposedPackbarcode
                    newPickupHeader.setProposedPackBarCode(pickupHeader.getProposedPackBarCode());

                    // REF_FIELD_1
                    newPickupHeader.setReferenceField1(pickupHeader.getReferenceField1());

                    newPickupHeader.setManufacturerCode(pickupHeader.getManufacturerCode());
                    newPickupHeader.setManufacturerName(pickupHeader.getManufacturerName());
                    newPickupHeader.setManufacturerPartNo(pickupHeader.getManufacturerPartNo());
                    newPickupHeader.setSalesOrderNumber(pickupHeader.getSalesOrderNumber());
                    newPickupHeader.setPickListNumber(pickupHeader.getPickListNumber());
                    newPickupHeader.setSalesInvoiceNumber(pickupHeader.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(pickupHeader.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType(pickupHeader.getReferenceDocumentType());
                    newPickupHeader.setSupplierInvoiceNo(pickupHeader.getSupplierInvoiceNo());
                    newPickupHeader.setTokenNumber(pickupHeader.getTokenNumber());
                    newPickupHeader.setLevelId(pickupHeader.getLevelId());
                    newPickupHeader.setTargetBranchCode(pickupHeader.getTargetBranchCode());
                    newPickupHeader.setLineNumber(pickupHeader.getLineNumber());

                    newPickupHeader.setFromBranchCode(pickupHeader.getFromBranchCode());
                    newPickupHeader.setIsCompleted(pickupHeader.getIsCompleted());
                    newPickupHeader.setIsCancelled(pickupHeader.getIsCancelled());
                    newPickupHeader.setMUpdatedOn(pickupHeader.getMUpdatedOn());

                    PickupHeaderV2 createdPickupHeader = createPickupHeaderV2(newPickupHeader, pickupHeader.getPickupCreatedBy());
                    log.info("pickupHeader created: " + createdPickupHeader);
                }

            }
            log.info("PickUpHeader status updated through stored procedure");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("PickupHeader update error: " + e.toString());
        }
        return createdPickupLineList;
    }

    /**
     * @param newPickupHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     * @throws FirebaseMessagingException
     */
    public PickupHeaderV2 createPickupHeaderV2(PickupHeaderV2 newPickupHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException, FirebaseMessagingException {
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

        OutboundLineV2 updateOutboundLine = new OutboundLineV2();
        updateOutboundLine.setAssignedPickerId(dbPickupHeader.getAssignedPickerId());
        updateOutboundLine.setManufacturerName(dbPickupHeader.getManufacturerName());
        orderService.updateOutboundLineV2(
                dbPickupHeader.getCompanyCodeId(),
                dbPickupHeader.getPlantId(),
                dbPickupHeader.getLanguageId(),
                dbPickupHeader.getWarehouseId(),
                dbPickupHeader.getPreOutboundNo(),
                dbPickupHeader.getRefDocNumber(),
                dbPickupHeader.getPartnerCode(),
                dbPickupHeader.getLineNumber(),
                dbPickupHeader.getItemCode(),
                loginUserID,
                updateOutboundLine);
        String customerName = orderService.getCustomerName(dbPickupHeader.getCompanyCodeId(), dbPickupHeader.getPlantId(),
                dbPickupHeader.getLanguageId(), dbPickupHeader.getWarehouseId(),
                dbPickupHeader.getCustomerCode());
        if (customerName != null) {
            dbPickupHeader.setCustomerName(customerName);
        }
        dbPickupHeader.setDeletionIndicator(0L);
        dbPickupHeader.setPickupCreatedBy(loginUserID);
        dbPickupHeader.setPickupCreatedOn(new Date());
        PickupHeaderV2 pickupHeaderV2 = pickupHeaderV2Repository.save(dbPickupHeader);

        // send Notification
        if (pickupHeaderV2 != null) {
            List<IKeyValuePair> notification =
                    pickupHeaderV2Repository.findByStatusIdAndNotificationStatusAndDeletionIndicatorDistinctRefDocNo();

            if (notification != null) {
                for (IKeyValuePair pickup : notification) {

                    List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                            pickup.getAssignPicker(), pickup.getWarehouseId());

                    if (deviceToken != null && !deviceToken.isEmpty()) {
                        String title = "PICKING";
                        String message = pickup.getRefDocType() + " ORDER - " + pickup.getRefDocNumber() + " - IS RECEIVED ";
//                        String response = pushNotificationService.sendPushNotification(deviceToken, title, message);
//                        if (response.equals("OK")) {
//                            pickupHeaderV2Repository.updateNotificationStatus(
//                                    pickup.getAssignPicker(), pickup.getRefDocNumber(), pickup.getWarehouseId());
//                            log.info("status update successfully");
//                        }
                    }
                }
            }
        }
        return pickupHeaderV2;
    }

    /**
     * @param newQualityLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public List<QualityLineV2> createQualityLineV2(List<AddQualityLineV2> newQualityLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        try {
            log.info("-------createQualityLine--------called-------> " + newQualityLines);

            List<AddQualityLineV2> dupQualityLines = getDuplicatesQualityLine(newQualityLines);
            log.info("-------dupQualityLines--------> " + dupQualityLines);
            if (dupQualityLines != null && !dupQualityLines.isEmpty()) {
                newQualityLines.removeAll(dupQualityLines);
                newQualityLines.add(dupQualityLines.get(0));
                log.info("-------newQualityLines---removed-dupQualityLines-----> " + newQualityLines);
            }
            String DLV_ORD_NO = null;
            /*
             * The below flag helps to avoid duplicate request and updating of outboundline
             * table
             */
            List<QualityLineV2> createdQualityLineList = new ArrayList<>();
            for (AddQualityLineV2 newQualityLine : newQualityLines) {
                log.info("Input from UI:  " + newQualityLine);
                log.info("QualityQty, PickConfirmQty: " + newQualityLine.getQualityQty() + ", " + newQualityLine.getPickConfirmQty());

                QualityLineV2 dbQualityLine = new QualityLineV2();
                BeanUtils.copyProperties(newQualityLine, dbQualityLine, CommonUtils.getNullPropertyNames(newQualityLine));

                // STATUS_ID - HardCoded Value "55"
                dbQualityLine.setStatusId(55L);

                IKeyValuePair description = stagingLineV2Repository.getDescription(dbQualityLine.getCompanyCodeId(),
                        dbQualityLine.getLanguageId(),
                        dbQualityLine.getPlantId(),
                        dbQualityLine.getWarehouseId());

                statusDescription = stagingLineV2Repository.getStatusDescription(55L, dbQualityLine.getLanguageId());
                dbQualityLine.setStatusDescription(statusDescription);

                dbQualityLine.setCompanyDescription(description.getCompanyDesc());
                dbQualityLine.setPlantDescription(description.getPlantDesc());
                dbQualityLine.setWarehouseDescription(description.getWarehouseDesc());

                OrderManagementLineV2 dbOrderManagementLine = orderService.getOrderManagementLineForLineUpdateV2(
                        newQualityLine.getCompanyCodeId(),
                        newQualityLine.getPlantId(),
                        newQualityLine.getLanguageId(),
                        newQualityLine.getWarehouseId(),
                        newQualityLine.getPreOutboundNo(),
                        newQualityLine.getRefDocNumber(),
                        newQualityLine.getLineNumber(),
                        newQualityLine.getItemCode());
                log.info("OrderManagementLine: " + dbOrderManagementLine);

                if (dbOrderManagementLine != null) {
                    dbQualityLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                    dbQualityLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                    dbQualityLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                    dbQualityLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                    dbQualityLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                    dbQualityLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                    dbQualityLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                    dbQualityLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                    dbQualityLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                    dbQualityLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                    dbQualityLine.setDescription(dbOrderManagementLine.getDescription());
                    dbQualityLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                    dbQualityLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                    dbQualityLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
                    dbQualityLine.setImsSaleTypeCode(dbOrderManagementLine.getImsSaleTypeCode());
                }

                dbQualityLine.setBarcodeId(newQualityLine.getBarcodeId());
                dbQualityLine.setDeletionIndicator(0L);
                dbQualityLine.setQualityCreatedBy(loginUserID);
                dbQualityLine.setQualityUpdatedBy(loginUserID);
                dbQualityLine.setQualityCreatedOn(new Date());
                dbQualityLine.setQualityUpdatedOn(new Date());

                /*
                 * String warehouseId, String preOutboundNo, String refDocNumber, String
                 * partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode
                 */
                QualityLineV2 existingQualityLine = orderService.findDuplicateRecordV2(
                        newQualityLine.getCompanyCodeId(), newQualityLine.getPlantId(),
                        newQualityLine.getLanguageId(), newQualityLine.getWarehouseId(),
                        newQualityLine.getPreOutboundNo(), newQualityLine.getRefDocNumber(),
                        newQualityLine.getPartnerCode(), newQualityLine.getLineNumber(),
                        newQualityLine.getQualityInspectionNo(), newQualityLine.getItemCode(), newQualityLine.getManufacturerName());
                log.info("existingQualityLine record status : " + existingQualityLine);

                /*
                 * Checking whether the record already exists (created) or not. If it is not
                 * created then only the rest of the logic has been carry forward
                 */
                if (existingQualityLine == null) {
                    QualityLineV2 createdQualityLine = qualityLineV2Repository.save(dbQualityLine);
                    log.info("createdQualityLine: " + createdQualityLine);
                    log.info("QualityQty, PickConfirmQty: " + createdQualityLine.getQualityQty() + ", " + createdQualityLine.getPickConfirmQty());

                    // createOutboundLineInterim
                    orderService.createOutboundLineInterimV2(createdQualityLine);
                    createdQualityLineList.add(createdQualityLine);

                    statusDescription = stagingLineV2Repository.getStatusDescription(55L, dbQualityLine.getLanguageId());
                    qualityHeaderV2Repository.updateQualityHeaderStatusUpdateProc(
                            newQualityLine.getCompanyCodeId(), newQualityLine.getPlantId(),
                            newQualityLine.getLanguageId(), newQualityLine.getWarehouseId(),
                            dbQualityLine.getQualityInspectionNo(), 55L, statusDescription, dbQualityLine.getQualityCreatedBy());
                }
            }

            for (QualityLineV2 dbQualityLine : createdQualityLineList) {
                Long NUM_RAN_CODE = 12L;
                DLV_ORD_NO = mastersService.getNextNumberRange(NUM_RAN_CODE, dbQualityLine.getCompanyCodeId(),
                        dbQualityLine.getPlantId(), dbQualityLine.getLanguageId(), dbQualityLine.getWarehouseId());

                updateOutboundLineV2(dbQualityLine, DLV_ORD_NO);
                try {
                    /*-------------------OUTBOUNDHEADER------Update---------------------------*/
                    boolean isStatus57 = false;
                    List<OutboundLineV2> outboundLines = orderService.getOutboundLineV2(
                            dbQualityLine.getCompanyCodeId(), dbQualityLine.getPlantId(), dbQualityLine.getLanguageId(),
                            dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                            dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode());
//					log.info("outboundLine re-queried-----> : " + outboundLines);

                    outboundLines = outboundLines.stream().filter(o -> o.getStatusId() == 57L)
                            .collect(Collectors.toList());
                    if (outboundLines != null) {
                        isStatus57 = true;
                    }

                    OutboundHeaderV2 updateOutboundHeader = new OutboundHeaderV2();
                    updateOutboundHeader.setDeliveryOrderNo(DLV_ORD_NO);
                    if (isStatus57) { // If Status if 57 then update OutboundHeader with Status 57.
                        updateOutboundHeader.setStatusId(57L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(57L, dbQualityLine.getLanguageId());
                        updateOutboundHeader.setStatusDescription(statusDescription);
                    }

                    OutboundHeaderV2 outboundHeader = orderService.updateOutboundHeaderV2(
                            dbQualityLine.getCompanyCodeId(), dbQualityLine.getPlantId(), dbQualityLine.getLanguageId(),
                            dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                            dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), updateOutboundHeader,
                            loginUserID);
                    log.info("outboundHeader updated as 57---> : " + outboundHeader);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.info("outboundHeader updated error: " + e1.toString());
                }

                boolean qtyEqual = dbQualityLine.getQualityQty().equals(dbQualityLine.getPickConfirmQty());
                log.info("getQualityQty, getPickConfirmQty: " + dbQualityLine.getQualityQty() + "," + dbQualityLine.getPickConfirmQty());
                log.info("Qty Equal: " + qtyEqual);

                if (!qtyEqual) {
                    throw new BadRequestException("Quality Qty and Picking Confirm Qty Must be same");
                }
            }
            orderService.postDeliveryConfirm(createdQualityLineList, loginUserID);
            return createdQualityLineList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param dbQualityLine
     * @param DLV_ORD_NO
     */
    private void updateOutboundLineV2(QualityLineV2 dbQualityLine, String DLV_ORD_NO) {
        try {
            Double deliveryQty = outboundLineInterimRepository.getSumOfDeliveryLine(dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                    dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
                    dbQualityLine.getItemCode());
            log.info("=======updateOutboundLine==========>: " + deliveryQty);

            statusDescription = stagingLineV2Repository.getStatusDescription(57L, dbQualityLine.getLanguageId());

            // WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);
            updateOutboundLineByQLCreateProc(
                    dbQualityLine.getCompanyCodeId(),
                    dbQualityLine.getPlantId(),
                    dbQualityLine.getLanguageId(),
                    dbQualityLine.getWarehouseId(),
                    dbQualityLine.getPreOutboundNo(),
                    dbQualityLine.getRefDocNumber(),
                    dbQualityLine.getPartnerCode(),
                    dbQualityLine.getLineNumber(),
                    dbQualityLine.getItemCode(),
                    deliveryQty,
                    DLV_ORD_NO,
                    57L,
                    statusDescription);
            log.info("----------updateOutboundLineByQLCreateProc updated as StatusID = 57----------->");

//            }

        } catch (Exception e1) {
            e1.printStackTrace();
            log.info("outboundLine updated error: " + e1.toString());
        }
    }

    /**
     * @param outboundHeaderV2
     * @param outboundLineV2List
     * @param oldPickupLineList
     * @param newPickupLineList
     * @param orderManagementLineList
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     */
    private void insertNewPickListCancelRecord(OutboundHeaderV2 outboundHeaderV2, List<OutboundLineV2> outboundLineV2List, List<PickupLineV2> oldPickupLineList,
                                               List<PickupLineV2> newPickupLineList, List<OrderManagementLineV2> orderManagementLineList,
                                               String companyCodeId, String plantId, String languageId, String warehouseId,
                                               String oldPickListNumber, String newPickListNumber) {
        log.info("Insert Record for PickList Cancellation Started");
        String loginUserID = null;
        Long oldObLineCount = 0L;
        Long newObLineCount = 0L;
        Long oldPickLineCount = 0L;
        Long newPickLineCount = 0L;
        if (outboundLineV2List != null && !outboundLineV2List.isEmpty()) {            //old OrderLines Count
            oldObLineCount = outboundLineV2List.stream().count();
        }
        if (oldPickupLineList != null && !oldPickupLineList.isEmpty()) {             //old PickedLines Count
            oldPickLineCount = oldPickupLineList.stream().count();
        }
        List<OutboundLineV2> newOutboundLineList = orderService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber);               //Fetch New OrderLines
        if (newOutboundLineList != null && !newOutboundLineList.isEmpty()) {         //new OrderLines Count
            newObLineCount = newOutboundLineList.stream().count();
        }
        if (newPickupLineList != null && !newPickupLineList.isEmpty()) {             //new PickedLines Count
            newPickLineCount = newPickupLineList.stream().count();
        }

        if (outboundHeaderV2 != null) {
            loginUserID = outboundHeaderV2.getCreatedBy();
            PickListHeader pickListHeader = new PickListHeader();
            BeanUtils.copyProperties(outboundHeaderV2, pickListHeader, CommonUtils.getNullPropertyNames(outboundHeaderV2));
            OutboundHeaderV2 newOutboundHeader = orderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, newPickListNumber, warehouseId);         //Fetch New Order Details - Header
            if (newOutboundHeader != null) {
                loginUserID = newOutboundHeader.getCreatedBy();
                pickListHeader.setOldPreOutboundNo(outboundHeaderV2.getPreOutboundNo());
                pickListHeader.setOldRefDocNumber(outboundHeaderV2.getRefDocNumber());
                pickListHeader.setOldPickListNumber(oldPickListNumber);
                pickListHeader.setOldCustomerId(outboundHeaderV2.getCustomerId());
                pickListHeader.setOldCustomerName(outboundHeaderV2.getCustomerName());
                pickListHeader.setOldInvoiceDate(outboundHeaderV2.getInvoiceDate());
                pickListHeader.setOldInvoiceNumber(outboundHeaderV2.getInvoiceNumber());
                pickListHeader.setOldSalesInvoiceNumber(outboundHeaderV2.getSalesInvoiceNumber());
                pickListHeader.setOldSupplierInvoiceNo(outboundHeaderV2.getSupplierInvoiceNo());
                pickListHeader.setOldSalesOrderNumber(outboundHeaderV2.getSalesOrderNumber());
                pickListHeader.setOldTokenNumber(outboundHeaderV2.getTokenNumber());
                pickListHeader.setOldCountOfOrderedLine(oldObLineCount);
                pickListHeader.setOldCountOfPickedLine(oldPickLineCount);
                pickListHeader.setOldStatusId(outboundHeaderV2.getStatusId());
                pickListHeader.setOldStatusDescription(outboundHeaderV2.getStatusDescription());

                pickListHeader.setNewPreOutboundNo(newOutboundHeader.getPreOutboundNo());
                pickListHeader.setNewRefDocNumber(newOutboundHeader.getRefDocNumber());
                pickListHeader.setNewPickListNumber(newOutboundHeader.getPickListNumber());
                pickListHeader.setNewCustomerId(newOutboundHeader.getCustomerId());
                pickListHeader.setNewCustomerName(newOutboundHeader.getCustomerName());
                pickListHeader.setNewInvoiceDate(newOutboundHeader.getInvoiceDate());
                pickListHeader.setNewInvoiceNumber(newOutboundHeader.getInvoiceNumber());
                pickListHeader.setNewSalesInvoiceNumber(newOutboundHeader.getSalesInvoiceNumber());
                pickListHeader.setNewSupplierInvoiceNo(newOutboundHeader.getSupplierInvoiceNo());
                pickListHeader.setNewSalesOrderNumber(newOutboundHeader.getSalesOrderNumber());
                pickListHeader.setNewTokenNumber(newOutboundHeader.getTokenNumber());
                pickListHeader.setNewCountOfOrderedLine(newObLineCount);
                pickListHeader.setNewCountOfPickedLine(newPickLineCount);
                pickListHeader.setNewStatusId(newOutboundHeader.getStatusId());
                pickListHeader.setNewStatusDescription(newOutboundHeader.getStatusDescription());
                pickListHeader.setPickListCancelHeaderId(System.currentTimeMillis());
            }
            List<PickListLine> createPickListLineList = new ArrayList<>();
            List<String> createdItmMfrNameList = new ArrayList<>();
            List<String> oldItmMfrNameList = new ArrayList<>();
            List<String> newItmMfrNameList = new ArrayList<>();
            List<String> filterOldList = new ArrayList<>();
            List<String> filterNewList = new ArrayList<>();
            List<String> createdItmMfrNameList2 = new ArrayList<>();
            List<String> oldItmMfrNameList2 = new ArrayList<>();
            List<String> newItmMfrNameList2 = new ArrayList<>();
            List<String> filterOldList2 = new ArrayList<>();
            List<String> filterNewList2 = new ArrayList<>();
            if (oldPickupLineList != null && !oldPickupLineList.isEmpty()) {
                if (newPickupLineList != null && !newPickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLineV2 : oldPickupLineList) {
                        for (PickupLineV2 newPickupLineV2 : newPickupLineList) {
                            if (pickupLineV2.getItemCode().equalsIgnoreCase(newPickupLineV2.getItemCode()) &&
                                    pickupLineV2.getManufacturerName().equalsIgnoreCase(newPickupLineV2.getManufacturerName())) {

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(pickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(pickupLineV2));

                                dbPickListLine.setOldPreOutboundNo(pickupLineV2.getPreOutboundNo());
                                dbPickListLine.setOldRefDocNumber(pickupLineV2.getRefDocNumber());
                                dbPickListLine.setOldLineNo(pickupLineV2.getLineNumber());
                                dbPickListLine.setOldPickConfirmQty(pickupLineV2.getPickConfirmQty());
                                dbPickListLine.setOldPickedStorageBin(pickupLineV2.getPickedStorageBin());
                                dbPickListLine.setOldOrderQty(pickupLineV2.getPickConfirmQty());
                                dbPickListLine.setOldStatusId(pickupLineV2.getStatusId());
                                dbPickListLine.setOldStatusDescription(pickupLineV2.getStatusDescription());

                                dbPickListLine.setNewPreOutboundNo(newPickupLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newPickupLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newPickupLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewPickedStorageBin(newPickupLineV2.getPickedStorageBin());
                                dbPickListLine.setNewOrderQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewStatusId(newPickupLineV2.getStatusId());
                                dbPickListLine.setNewStatusDescription(newPickupLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());

                                createPickListLineList.add(dbPickListLine);
                                createdItmMfrNameList.add(newPickupLineV2.getItemCode() + newPickupLineV2.getManufacturerName());
                            }
                        }
                    }
                    for (PickupLineV2 newPickupLine : newPickupLineList) {
                        newItmMfrNameList.add(newPickupLine.getItemCode() + newPickupLine.getManufacturerName());
                    }
                    log.info("NewPickList : " + newItmMfrNameList);
                    log.info("CreatedPickList : " + createdItmMfrNameList);
                    if (createdItmMfrNameList != null && !createdItmMfrNameList.isEmpty()) {
                        for (String itmMfrName : createdItmMfrNameList) {
                            boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!newItmPresent) {
                                filterNewList.add(itmMfrName);
                            }
                        }
                    }
                    if (createdItmMfrNameList == null || createdItmMfrNameList.isEmpty()) {
                        filterNewList = newItmMfrNameList;
                    }
                    log.info("Filtered NewPickList : " + filterNewList);
                    if (filterNewList != null && !filterNewList.isEmpty()) {
                        for (PickupLineV2 newPickupLineV2 : newPickupLineList) {
                            String itmMfrName = newPickupLineV2.getItemCode() + newPickupLineV2.getManufacturerName();
                            boolean itmPresent = filterNewList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!itmPresent) {
                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(newPickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(newPickupLineV2));
                                dbPickListLine.setNewPreOutboundNo(newPickupLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newPickupLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newPickupLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewPickedStorageBin(newPickupLineV2.getPickedStorageBin());
                                dbPickListLine.setNewOrderQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewStatusId(newPickupLineV2.getStatusId());
                                dbPickListLine.setNewStatusDescription(newPickupLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                                createPickListLineList.add(dbPickListLine);
                            }
                        }
                    }
                }
                for (PickupLineV2 oldPickupLine : oldPickupLineList) {
                    oldItmMfrNameList.add(oldPickupLine.getItemCode() + oldPickupLine.getManufacturerName());
                }
                log.info("OldPickList : " + oldItmMfrNameList);
                if (createdItmMfrNameList != null && !createdItmMfrNameList.isEmpty()) {
                    for (String itmMfrName : createdItmMfrNameList) {
                        boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!oldItmPresent) {
                            filterOldList.add(itmMfrName);
                        }
                    }
                }
                if (createdItmMfrNameList == null || createdItmMfrNameList.isEmpty()) {
                    filterOldList = oldItmMfrNameList;
                }
                log.info("Filtered OldPickList : " + filterOldList);
                if (filterOldList != null && !filterOldList.isEmpty()) {
                    for (PickupLineV2 oldPickupLineV2 : oldPickupLineList) {
                        String itmMfrName = oldPickupLineV2.getItemCode() + oldPickupLineV2.getManufacturerName() + oldPickupLineV2.getLineNumber();
                        boolean itmPresent = filterOldList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!itmPresent) {
                            PickListLine dbPickListLine = new PickListLine();
                            BeanUtils.copyProperties(oldPickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(oldPickupLineV2));
                            dbPickListLine.setOldPreOutboundNo(oldPickupLineV2.getPreOutboundNo());
                            dbPickListLine.setOldRefDocNumber(oldPickupLineV2.getRefDocNumber());
                            dbPickListLine.setOldLineNo(oldPickupLineV2.getLineNumber());
                            dbPickListLine.setOldPickConfirmQty(oldPickupLineV2.getPickConfirmQty());
                            dbPickListLine.setOldPickedStorageBin(oldPickupLineV2.getPickedStorageBin());
                            dbPickListLine.setOldOrderQty(oldPickupLineV2.getPickConfirmQty());
                            dbPickListLine.setOldStatusId(oldPickupLineV2.getStatusId());
                            dbPickListLine.setOldStatusDescription(oldPickupLineV2.getStatusDescription());

                            dbPickListLine.setDeletionIndicator(0L);
                            dbPickListLine.setCreatedBy(loginUserID);
                            dbPickListLine.setUpdatedBy(loginUserID);
                            dbPickListLine.setCreatedOn(new Date());
                            dbPickListLine.setUpdatedOn(new Date());
                            dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                            dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                            createPickListLineList.add(dbPickListLine);
                        }
                    }
                }
            }
            List<OrderManagementLineV2> newOrderManagementLineList = orderService
                    .getOrderManagementLineForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber);
            List<OrderManagementLineV2> oldOrderManagementLineFilteredList = null;
            List<OrderManagementLineV2> newOrderManagementLineFilteredList = null;

            if (outboundLineV2List != null && !outboundLineV2List.isEmpty()) {
                if (newOutboundLineList != null && !newOutboundLineList.isEmpty()) {
                    for (OutboundLineV2 outboundLineV2 : outboundLineV2List) {
                        String oldItmMfrName = outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName();
                        log.info("oldItmMfrName: " + oldItmMfrName);
                        boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(oldItmMfrName));
                        if (!oldItmPresent) {
                            oldItmMfrNameList2.add(outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName());
                            for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                                String newItmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                                log.info("ItmMfrName: " + newItmMfrName);
                                boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                                if (!newItmPresent) {
                                    if (outboundLineV2.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                            outboundLineV2.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName())) {

                                        //Filter ItemCode, MFR Name and LineNumber - OldPickList
                                        oldOrderManagementLineFilteredList = orderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode()) &&
                                                a.getManufacturerName().equalsIgnoreCase(outboundLineV2.getManufacturerName()) &&
                                                a.getLineNumber().equals(outboundLineV2.getLineNumber())).collect(Collectors.toList());
                                        //Filter ItemCode, MFR Name and LineNumber - NewPickList
                                        newOrderManagementLineFilteredList = newOrderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                                a.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName()) &&
                                                a.getLineNumber().equals(newOutboundLineV2.getLineNumber())).collect(Collectors.toList());

                                        PickListLine dbPickListLine = new PickListLine();
                                        BeanUtils.copyProperties(outboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(outboundLineV2));

                                        dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                                        dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                                        dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                                        dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                                        if (oldOrderManagementLineFilteredList != null && !oldOrderManagementLineFilteredList.isEmpty()) {
                                            dbPickListLine.setOldPickedStorageBin(oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                        }
                                        dbPickListLine.setOldOrderQty(outboundLineV2.getOrderQty());
                                        dbPickListLine.setOldStatusId(outboundLineV2.getStatusId());
                                        dbPickListLine.setOldStatusDescription(outboundLineV2.getStatusDescription());

                                        dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                        dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                        dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                        dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                        if (newOrderManagementLineFilteredList != null && !newOrderManagementLineFilteredList.isEmpty()) {
                                            dbPickListLine.setNewPickedStorageBin(newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                        }
                                        dbPickListLine.setNewOrderQty(newOutboundLineV2.getOrderQty());
                                        dbPickListLine.setNewStatusId(newOutboundLineV2.getStatusId());
                                        dbPickListLine.setNewStatusDescription(newOutboundLineV2.getStatusDescription());

                                        dbPickListLine.setDeletionIndicator(0L);
                                        dbPickListLine.setCreatedBy(loginUserID);
                                        dbPickListLine.setUpdatedBy(loginUserID);
                                        dbPickListLine.setCreatedOn(new Date());
                                        dbPickListLine.setUpdatedOn(new Date());
                                        dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                        dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());

                                        createPickListLineList.add(dbPickListLine);
                                        createdItmMfrNameList2.add(newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName());
                                    }
                                }
                            }
                        }
                    }
                    for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                        String newItmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                        log.info("ItmMfrName: " + newItmMfrName);
                        boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                        if (!newItmPresent) {
                            newItmMfrNameList2.add(newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName());
                        }
                    }
                    log.info("OldPickList2 : " + oldItmMfrNameList2);
                    log.info("NewPickList2 : " + newItmMfrNameList2);
                    log.info("CreatedPickList2 : " + createdItmMfrNameList2);
                    if (createdItmMfrNameList2 != null && !createdItmMfrNameList2.isEmpty()) {
                        for (String itmMfrName : createdItmMfrNameList2) {
                            boolean oldItmPresent2 = oldItmMfrNameList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            boolean newItmPresent2 = newItmMfrNameList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!oldItmPresent2) {
                                filterOldList2.add(itmMfrName);
                            }
                            if (!newItmPresent2) {
                                filterNewList2.add(itmMfrName);
                            }
                        }
                    }
                    if (createdItmMfrNameList2 == null || createdItmMfrNameList2.isEmpty()) {
                        filterOldList2 = oldItmMfrNameList2;
                        filterNewList2 = newItmMfrNameList2;
                    }
//                        }
                    log.info("Filtered OldPickList2 : " + filterOldList2);
                    log.info("Filtered NewPickList2 : " + filterNewList2);
                    if (filterNewList2 != null && !filterNewList2.isEmpty()) {
                        for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                            String itmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                            boolean itmPresent = filterNewList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!itmPresent) {

                                //Filter ItemCode, MFR Name and LineNumber - NewPickList
                                newOrderManagementLineFilteredList = newOrderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName()) &&
                                        a.getLineNumber().equals(newOutboundLineV2.getLineNumber())).collect(Collectors.toList());

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(newOutboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(newOutboundLineV2));
                                dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                if (newOrderManagementLineFilteredList != null && !newOrderManagementLineFilteredList.isEmpty()) {
                                    dbPickListLine.setNewPickedStorageBin(newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                }
                                dbPickListLine.setNewOrderQty(newOutboundLineV2.getOrderQty());
                                dbPickListLine.setNewStatusId(newOutboundLineV2.getStatusId());
                                dbPickListLine.setNewStatusDescription(newOutboundLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                                createPickListLineList.add(dbPickListLine);
                            }
                        }
                    }
                }
                if (oldItmMfrNameList2 != null && !oldItmMfrNameList2.isEmpty()) {
                    for (OutboundLineV2 outboundLineV2 : outboundLineV2List) {
                        String itmMfrName = outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName();
                        boolean itmPresent = filterOldList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!itmPresent) {
                            //Filter ItemCode, MFR Name and LineNumber - OldPickList
                            oldOrderManagementLineFilteredList = orderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode()) &&
                                    a.getManufacturerName().equalsIgnoreCase(outboundLineV2.getManufacturerName()) &&
                                    a.getLineNumber().equals(outboundLineV2.getLineNumber())).collect(Collectors.toList());

                            PickListLine dbPickListLine = new PickListLine();
                            BeanUtils.copyProperties(outboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(outboundLineV2));
                            dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                            dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                            dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                            dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                            if (oldOrderManagementLineFilteredList != null && !oldOrderManagementLineFilteredList.isEmpty()) {
                                dbPickListLine.setOldPickedStorageBin(oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                            }
                            dbPickListLine.setOldOrderQty(outboundLineV2.getOrderQty());
                            dbPickListLine.setOldStatusId(outboundLineV2.getStatusId());
                            dbPickListLine.setOldStatusDescription(outboundLineV2.getStatusDescription());

                            dbPickListLine.setDeletionIndicator(0L);
                            dbPickListLine.setCreatedBy(loginUserID);
                            dbPickListLine.setUpdatedBy(loginUserID);
                            dbPickListLine.setCreatedOn(new Date());
                            dbPickListLine.setUpdatedOn(new Date());
                            dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                            dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                            createPickListLineList.add(dbPickListLine);
                        }
                    }
                }
            }
            log.info("PickList Line: " + createPickListLineList);
            pickListHeader.setLine(createPickListLineList);
            PickListHeader createdPickListHeader = createPickListHeader(pickListHeader, loginUserID);
            log.info("Created PicklistHeader : " + createdPickListHeader);
        }
    }


    /**
     * @param dbPickListHeader
     * @param loginUserID
     * @return
     */
    public PickListHeader createPickListHeader(PickListHeader dbPickListHeader, String loginUserID) {
        log.info("newPickListHeader : " + dbPickListHeader);
        dbPickListHeader.setDeletionIndicator(0L);
        dbPickListHeader.setCreatedBy(loginUserID);
        dbPickListHeader.setUpdatedBy(loginUserID);
        dbPickListHeader.setCreatedOn(new Date());
        dbPickListHeader.setUpdatedOn(new Date());
        PickListHeader createdPickListHeader = pickListHeaderRepository.save(dbPickListHeader);
        return createdPickListHeader;
    }

    /**
     * @param dbPickupLine
     * @param subMvtTypeId
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovementV2(PickupLineV2 dbPickupLine, Long subMvtTypeId,
                                                        String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(dbPickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPickupLine));

        inventoryMovement.setCompanyCodeId(dbPickupLine.getCompanyCodeId());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(3L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(subMvtTypeId);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
//        inventoryMovement.setMovementDocumentNo(movementDocumentNo);
        inventoryMovement.setReferenceField10(movementDocumentNo);
        inventoryMovement.setManufacturerName(dbPickupLine.getManufacturerName());
        inventoryMovement.setDescription(dbPickupLine.getDescription());
        inventoryMovement.setBarcodeId(dbPickupLine.getBarcodeId());
        inventoryMovement.setCompanyDescription(dbPickupLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(dbPickupLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(dbPickupLine.getWarehouseDescription());
        inventoryMovement.setRefDocNumber(dbPickupLine.getRefDocNumber());
        inventoryMovement.setBarcodeId(dbPickupLine.getBarcodeId());
        inventoryMovement.setReferenceNumber(dbPickupLine.getPreOutboundNo());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);


        // BAR_CODE
        inventoryMovement.setPackBarcodes(dbPickupLine.getPickedPackCode());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbPickupLine.getPickConfirmQty());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                dbPickupLine.getCompanyCodeId(),
                dbPickupLine.getPlantId(),
                dbPickupLine.getLanguageId(),
                dbPickupLine.getWarehouseId(),
                dbPickupLine.getManufacturerName(),
                dbPickupLine.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if (sumOfInvQty != null) {
            inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = 0D;
            if (movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - dbPickupLine.getPickConfirmQty();
            }
            if (movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + dbPickupLine.getPickConfirmQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : " + sumOfInvQty + ", " + openQty);
        }
        if (sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
            Double openQty = 0D;
            sumOfInvQty = 0D;
            if (movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - dbPickupLine.getPickConfirmQty();
                if (openQty < 0) {
                    openQty = 0D;
                }
            }
            if (movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + dbPickupLine.getPickConfirmQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbPickupLine.getPickUom());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(loginUserID);

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbPickupLine.getPickupCreatedOn());
        inventoryMovement.setMovementDocumentNo(String.valueOf(System.currentTimeMillis()));
        return inventoryMovementRepository.save(inventoryMovement);
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
     * @param deliveryQty
     * @param deliveryOrderNo
     * @param statusId
     * @param statusDescription
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public boolean updateOutboundLineByQLCreateProc(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                    String itemCode, Double deliveryQty, String deliveryOrderNo, Long statusId, String statusDescription)
            throws IllegalAccessException, InvocationTargetException {
        outboundLineV2Repository.updateOBlineByQLCreateProcedure(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode, deliveryQty, deliveryOrderNo, statusDescription, statusId);
        log.info("------updateOutboundLineByProc-------> : " + statusId + " updated...");
        return true;
    }

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
//            String routingDb = dbConfigRepository.getDbNameWithoutWhId(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode());
//            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(routingDb);
//            BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
//            if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
//                apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
//            } else {
//                Optional<Warehouse> warehouse =
//                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
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
////			if (salesOrderHeader.getOrderType() != null) {
////				apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
////			} else {
////				apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
////			}
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

    /// /				if (salesOrderHeader.getOrderType() != null) {
    /// /					apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
    /// /				} else {
    /// /					apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
    /// /				}
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
//                apiHeader.setProcessedStatusId(0L);
//                log.info("apiHeader : " + apiHeader);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb("MT");
//                OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Success: " + createdOrder);
//                return apiHeader;
//            } else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
//                // throw the error as Lines are Empty and set the Indicator as '100'
//                apiHeader.setProcessedStatusId(100L);
//                log.info("apiHeader : " + apiHeader);
//                OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Failed: " + createdOrder);
//                throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//        return null;
//    }
//
//    /**
//     * @param referenceDocumentTypeId
//     * @return
//     */
    public String getOutboundOrderTypeDesc(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 0) {
            referenceDocumentType = "WMS to Non-WMS";
        }
        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "WMS to WMS";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "PURCHASE RETURN";
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "PICK LIST";
        }

        return referenceDocumentType;
    }


//
//    public OutboundOrderV2 postSalesOrderV5(@Valid SalesOrderV2 salesOrder) {
//        try {
//            SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();
//
//            OutboundOrderV2 apiHeader = new OutboundOrderV2();
//            BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
//
//            if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
//                apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
//            } else {
//                Optional<Warehouse> warehouse =
//                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//                                salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
//                                salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
//                                0L);
//                apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
//            }
//
//            apiHeader.setBranchCode(salesOrderHeader.getStoreID());
//            apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
//            apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);
//
//            apiHeader.setCustomerId(salesOrderHeader.getCustomerId());
//            apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
//            apiHeader.setPartnerCode(salesOrderHeader.getCustomerId());
//            apiHeader.setPartnerName(salesOrderHeader.getCustomerName());
//            apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
//            apiHeader.setPickListStatus(salesOrderHeader.getStatus());
//            apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
//            if (salesOrderHeader.getOrderType() != null) {
//                apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
//            } else {
//                apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
//            }
//
//            apiHeader.setRefDocumentType(getOutboundOrderTypeDescV5(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                    apiHeader.getLanguageId(), apiHeader.getWarehouseID(),
//                    apiHeader.getOutboundOrderTypeID()));
//
//            apiHeader.setCustomerType("INVOICE");								//HardCoded
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
//                throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
//            }
//
//            List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
//            Set<OutboundOrderLineV2> orderLines = new HashSet<>();
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
//                apiLine.setManufacturerCode(MFR_NAME);
//                apiLine.setManufacturerName(MFR_NAME);
//                apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
//                apiLine.setStoreID(salesOrderHeader.getStoreID());
//                apiLine.setRefField1ForOrderType(soLine.getOrderType());
//                apiLine.setCustomerType("INVOICE");								//HardCoded
//                if (salesOrderHeader.getOrderType() != null) {
//                    apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
//                } else {
//                    apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
//                }
//
//                apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
//                apiLine.setItemCode(soLine.getSku().trim());                    // ITM_CODE
//                apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
//                apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
//                apiLine.setUom(soLine.getUom());                                // ORD_UOM
//                apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
//                apiLine.setOrderId(apiHeader.getOrderId());
//                apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
//                apiLine.setPickListNo(soLine.getPickListNo());
//
//                apiLine.setMiddlewareId(soLine.getMiddlewareId());
//                apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
//                apiLine.setMiddlewareTable(soLine.getMiddlewareTable());
//
//                orderLines.add(apiLine);
//            }
//            apiHeader.setLine(orderLines);
//            apiHeader.setOrderProcessedOn(new Date());
//
//            if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
//                apiHeader.setProcessedStatusId(0L);
//                apiHeader.setExecuted(0L);
//                log.info("apiHeader : " + apiHeader);
//                OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Success: " + createdOrder);
//                return apiHeader;
//            } else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
//                // throw the error as Lines are Empty and set the Indicator as '100'
//                apiHeader.setProcessedStatusId(100L);
//                log.info("apiHeader : " + apiHeader);
//                OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//                log.info("SalesOrder Order Failed: " + createdOrder);
//                throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
//            }
//        } catch (Exception e) {
//            throw new BadRequestException("Exception while saving sales Order-PickList - " + e.toString());
//        }
//        return null;
//    }

    /**
     * @param perpetual
     * @return
     */
    public PerpetualHeaderEntityV2 processStockCountReceived(Perpetual perpetual) {

        PerpetualHeaderEntityV2 perpetualHeaderEntity = new PerpetualHeaderEntityV2();
        PerpetualHeaderV2 newPerpetualHeader = new PerpetualHeaderV2();
        List<PerpetualLineV2> perpetualLines = new ArrayList<>();
        List<PerpetualZeroStockLine> perpetualZeroStockLineList = new ArrayList<>();

        // Get Warehouse
        Optional<Warehouse> dbWarehouse =
                warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        perpetual.getPerpetualHeaderV1().getCompanyCode(),
                        perpetual.getPerpetualHeaderV1().getBranchCode(),
                        "EN",
                        0L
                );
        log.info("dbWarehouse : " + dbWarehouse);
        if (dbWarehouse == null || dbWarehouse.isEmpty()) {
            throw new BadRequestException("Warehouse can't be null");
        }

        newPerpetualHeader.setCompanyCodeId(perpetual.getPerpetualHeaderV1().getCompanyCode());
        newPerpetualHeader.setPlantId(perpetual.getPerpetualHeaderV1().getBranchCode());
        newPerpetualHeader.setWarehouseId("300");
        newPerpetualHeader.setLanguageId("EN");
        newPerpetualHeader.setMiddlewareId(String.valueOf(perpetual.getPerpetualHeaderV1().getMiddlewareId()));
        newPerpetualHeader.setMiddlewareTable(perpetual.getPerpetualHeaderV1().getMiddlewareTable());
        newPerpetualHeader.setReferenceCycleCountNo(perpetual.getPerpetualHeaderV1().getCycleCountNo());

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 14;
        String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID,
                newPerpetualHeader.getCompanyCodeId(),
                newPerpetualHeader.getPlantId(),
                newPerpetualHeader.getLanguageId(),
                newPerpetualHeader.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        newPerpetualHeader.setCycleCountNo(nextRangeNumber);

        // CC_TYP_ID
        newPerpetualHeader.setCycleCountTypeId(1L);

        newPerpetualHeader.setMovementTypeId(1L);
        newPerpetualHeader.setSubMovementTypeId(2L);

        // STATUS_ID - HardCoded Value "70"
        newPerpetualHeader.setStatusId(70L);
        statusDescription = stagingLineV2Repository.getStatusDescription(70L, newPerpetualHeader.getLanguageId());
        newPerpetualHeader.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(newPerpetualHeader.getCompanyCodeId(),
                newPerpetualHeader.getLanguageId(),
                newPerpetualHeader.getPlantId(),
                newPerpetualHeader.getWarehouseId());
        newPerpetualHeader.setCompanyDescription(description.getCompanyDesc());
        newPerpetualHeader.setPlantDescription(description.getPlantDesc());
        newPerpetualHeader.setWarehouseDescription(description.getWarehouseDesc());

        newPerpetualHeader.setDeletionIndicator(0L);
        newPerpetualHeader.setCreatedBy("MW_AMS");
        newPerpetualHeader.setCountedBy("MW_AMS");
        newPerpetualHeader.setCreatedOn(new Date());
        newPerpetualHeader.setCountedOn(new Date());
        PerpetualHeaderV2 createdPerpetualHeader = perpetualHeaderV2Repository.save(newPerpetualHeader);
        log.info("createdPerpetualHeader : " + createdPerpetualHeader);

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        for (PerpetualLineV1 cycleCountLine : perpetual.getPerpetualLineV1()) {

            List<IInventoryImpl> dbInventoryList = inventoryService.getInventoryForPerpetualCountV2(newPerpetualHeader.getCompanyCodeId(),
                    newPerpetualHeader.getPlantId(),
                    newPerpetualHeader.getLanguageId(),
                    newPerpetualHeader.getWarehouseId(),
                    cycleCountLine.getItemCode(),
                    cycleCountLine.getManufacturerName());

            if (dbInventoryList != null) {
                for (IInventoryImpl dbInventory : dbInventoryList) {

                    PerpetualLineV2 dbPerpetualLine = new PerpetualLineV2();

                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                    storageBinPutAway.setCompanyCodeId(dbInventory.getCompanyCodeId());
                    storageBinPutAway.setPlantId(dbInventory.getPlantId());
                    storageBinPutAway.setLanguageId(dbInventory.getLanguageId());
                    storageBinPutAway.setWarehouseId(dbInventory.getWarehouseId());
                    storageBinPutAway.setBin(dbInventory.getStorageBin());

                    StorageBinV2 dbStorageBin = null;
                    try {
                        dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                    if (dbStorageBin != null) {
                        dbPerpetualLine.setStorageSectionId(dbStorageBin.getStorageSectionId());
                        dbPerpetualLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                        dbPerpetualLine.setVariantCode(dbInventory.getVariantCode());
                        dbPerpetualLine.setVariantSubCode(dbInventory.getVariantSubCode());
                    }

                    dbPerpetualLine.setCompanyCodeId(dbInventory.getCompanyCodeId());
                    dbPerpetualLine.setPlantId(dbInventory.getPlantId());
                    dbPerpetualLine.setWarehouseId(dbInventory.getWarehouseId());
                    dbPerpetualLine.setLanguageId(dbInventory.getLanguageId());
                    dbPerpetualLine.setItemCode(dbInventory.getItemCode());
                    dbPerpetualLine.setManufacturerPartNo(dbInventory.getManufacturerCode());
                    dbPerpetualLine.setManufacturerName(dbInventory.getManufacturerName());
                    dbPerpetualLine.setManufacturerCode(cycleCountLine.getManufacturerCode());
                    dbPerpetualLine.setLineNo(cycleCountLine.getLineNoOfEachItemCode());

                    dbPerpetualLine.setCycleCountNo(nextRangeNumber);
                    dbPerpetualLine.setReferenceNo(cycleCountLine.getCycleCountNo());
                    dbPerpetualLine.setStorageBin(dbInventory.getStorageBin());
                    dbPerpetualLine.setItemCode(dbInventory.getItemCode());
                    dbPerpetualLine.setItemDesc(dbInventory.getReferenceField8());
                    dbPerpetualLine.setPackBarcodes(dbInventory.getPackBarcodes());
                    dbPerpetualLine.setStockTypeId(dbInventory.getStockTypeId());
                    dbPerpetualLine.setSpecialStockIndicator(String.valueOf(dbInventory.getSpecialStockIndicatorId()));
//                    dbPerpetualLine.setInventoryQuantity(dbInventory.getInventoryQuantity());
                    dbPerpetualLine.setInventoryQuantity(dbInventory.getReferenceField4());                              //Total Qty
                    dbPerpetualLine.setInventoryUom(cycleCountLine.getUom());
                    dbPerpetualLine.setFrozenQty(cycleCountLine.getFrozenQty());

                    dbPerpetualLine.setStatusId(70L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(70L, newPerpetualHeader.getLanguageId());
                    dbPerpetualLine.setStatusDescription(statusDescription);

                    dbPerpetualLine.setCompanyDescription(description.getCompanyDesc());
                    dbPerpetualLine.setPlantDescription(description.getPlantDesc());
                    dbPerpetualLine.setWarehouseDescription(description.getWarehouseDesc());

                    if (dbInventory.getBarcodeId() != null) {
                        dbPerpetualLine.setBarcodeId(dbInventory.getBarcodeId());
                    }

                    if (dbInventory.getBarcodeId() == null) {
                        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(dbInventory.getItemCode(),
                                dbInventory.getCompanyCodeId(),
                                dbInventory.getPlantId(),
                                dbInventory.getWarehouseId(),
                                dbInventory.getManufacturerName(),
                                dbInventory.getLanguageId());
                        log.info("Barcode : " + barcode);
                        if (barcode != null && !barcode.isEmpty()) {
                            dbPerpetualLine.setBarcodeId(barcode.get(0));
                        }
                    }

                    dbPerpetualLine.setDeletionIndicator(0L);
                    dbPerpetualLine.setCreatedBy("MW_AMS");
                    dbPerpetualLine.setCreatedOn(new Date());
                    perpetualLines.add(dbPerpetualLine);
                }
            }
            //Item Not present in Inventory ---> Lines Insert as Inv_qty '0'
            if (dbInventoryList == null) {
                PerpetualZeroStockLine dbPerpetualLine = new PerpetualZeroStockLine();

                dbPerpetualLine.setCompanyCodeId(newPerpetualHeader.getCompanyCodeId());
                dbPerpetualLine.setPlantId(newPerpetualHeader.getPlantId());
                dbPerpetualLine.setWarehouseId(newPerpetualHeader.getWarehouseId());
                dbPerpetualLine.setLanguageId(newPerpetualHeader.getLanguageId());
                dbPerpetualLine.setItemCode(cycleCountLine.getItemCode());
                dbPerpetualLine.setManufacturerPartNo(cycleCountLine.getManufacturerName());
                dbPerpetualLine.setManufacturerName(cycleCountLine.getManufacturerName());
                dbPerpetualLine.setManufacturerCode(cycleCountLine.getManufacturerCode());
                dbPerpetualLine.setLineNo(cycleCountLine.getLineNoOfEachItemCode());

                dbPerpetualLine.setCycleCountNo(nextRangeNumber);
                dbPerpetualLine.setReferenceNo(cycleCountLine.getCycleCountNo());

                //Get Item Description
                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(newPerpetualHeader.getCompanyCodeId());
                imBasicData.setPlantId(newPerpetualHeader.getPlantId());
                imBasicData.setLanguageId(newPerpetualHeader.getLanguageId());
                imBasicData.setWarehouseId(newPerpetualHeader.getWarehouseId());
                imBasicData.setItemCode(cycleCountLine.getItemCode());
                imBasicData.setManufacturerName(cycleCountLine.getManufacturerName());
                ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 : " + imBasicData1);

                if (imBasicData1 != null) {
                    dbPerpetualLine.setItemDesc(imBasicData1.getDescription());
                }
                dbPerpetualLine.setInventoryQuantity(0D);                              //Total Qty
                dbPerpetualLine.setInventoryUom(cycleCountLine.getUom());
                dbPerpetualLine.setFrozenQty(cycleCountLine.getFrozenQty());

                dbPerpetualLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, newPerpetualHeader.getLanguageId());
                dbPerpetualLine.setStatusDescription(statusDescription);

                dbPerpetualLine.setCompanyDescription(description.getCompanyDesc());
                dbPerpetualLine.setPlantDescription(description.getPlantDesc());
                dbPerpetualLine.setWarehouseDescription(description.getWarehouseDesc());

                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(cycleCountLine.getItemCode(),
                        newPerpetualHeader.getCompanyCodeId(),
                        newPerpetualHeader.getPlantId(),
                        newPerpetualHeader.getWarehouseId(),
                        cycleCountLine.getManufacturerName(),
                        newPerpetualHeader.getLanguageId());
                log.info("Barcode : " + barcode);
                if (barcode != null && !barcode.isEmpty()) {
                    dbPerpetualLine.setBarcodeId(barcode.get(0));
                }


                dbPerpetualLine.setDeletionIndicator(0L);
                dbPerpetualLine.setCreatedBy("MW_AMS");
                dbPerpetualLine.setCreatedOn(new Date());
                perpetualZeroStockLineList.add(dbPerpetualLine);
            }
        }

        List<PerpetualLineV2> createdPerpetualLines = perpetualLineV2Repository.saveAll(perpetualLines);
        log.info("createdPerpetualLine : " + createdPerpetualLines);
        List<PerpetualZeroStockLine> createdPerpetualZeroStkLines = perpetualZeroStkLineRepository.saveAll(perpetualZeroStockLineList);
        log.info("createdPerpetualZeroStkLines : " + createdPerpetualZeroStkLines);

        BeanUtils.copyProperties(createdPerpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
        perpetualHeaderEntity.setPerpetualLine(perpetualLines);

        return perpetualHeaderEntity;
    }

    /**
     * @param stockAdjustment
     * @return
     */
    public com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment postStockAdjustment(StockAdjustment stockAdjustment) {
        log.info("StockAdjustment received from external: " + stockAdjustment);
        com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = autoUpdateStockAdjustmentNew(stockAdjustment);
        log.info("Saved StockAdjustment: " + createStockAdjustment);
        return createStockAdjustment;
    }

    /**
     * @param stockAdjustment
     * @return
     * @throws java.text.ParseException
     */
    public com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment autoUpdateStockAdjustmentNew(StockAdjustment stockAdjustment) {
        String movementQtyValue = null;
        if (stockAdjustment.getIsCycleCount().equalsIgnoreCase("Y") && stockAdjustment.getIsDamage().equalsIgnoreCase("N")) {
            log.info("IsCycleCount: " + stockAdjustment.getIsCycleCount());
            PerpetualLineV2 perpetualLine = getPerpetualLineForStockAdjustmentV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    "300",
                    stockAdjustment.getItemCode(),
                    stockAdjustment.getManufacturerName());
            log.info("Perpetual Line: " + perpetualLine);

            if (perpetualLine != null) {

                InventoryV2 dbInventory = inventoryService.getInventoryV2(
                        perpetualLine.getCompanyCodeId(),
                        perpetualLine.getPlantId(),
                        perpetualLine.getLanguageId(),
                        perpetualLine.getWarehouseId(),
                        perpetualLine.getPackBarcodes(),
                        perpetualLine.getItemCode(),
                        perpetualLine.getStorageBin(),
                        perpetualLine.getManufacturerName());
                log.info("Inventory: " + dbInventory);

                if (dbInventory != null) {
                    InventoryV2 newInventory = new InventoryV2();
                    BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
                    Double INV_QTY = dbInventory.getInventoryQuantity();
                    Double ADJ_QTY = stockAdjustment.getAdjustmentQty();
                    INV_QTY = INV_QTY + ADJ_QTY;
                    if(INV_QTY < 0){
                        INV_QTY = 0D;
                    }
                    newInventory.setInventoryQuantity(INV_QTY);
                    Double ALLOC_QTY = 0D;
                    if(dbInventory.getAllocatedQuantity() != null) {
                        ALLOC_QTY = dbInventory.getAllocatedQuantity();
                    }
                    Double TOT_QTY = INV_QTY + ALLOC_QTY;
                    newInventory.setReferenceField4(TOT_QTY);
                    newInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                    inventoryV2Repository.save(newInventory);

                    //StockAdjustment Record Insert
                    com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment dbStockAdjustment = new com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment();
                    BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

                    dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
                    dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
                    dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

                    if (stockAdjustment.getItemDescription() != null) {
                        dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() != null) {
                        dbStockAdjustment.setItemDescription(dbInventory.getDescription());
                    }
                    if (stockAdjustment.getItemDescription() == null && dbInventory.getDescription() == null) {
                        if (dbInventory.getReferenceField8() != null) {
                            dbStockAdjustment.setItemDescription(dbInventory.getReferenceField8());
                        }
                    }

                    dbStockAdjustment.setBeforeAdjustment(dbInventory.getReferenceField4());
                    dbStockAdjustment.setAfterAdjustment(newInventory.getReferenceField4());

                    dbStockAdjustment.setPackBarcodes(dbInventory.getPackBarcodes());
                    dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
                    dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
                    dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
                    dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
                    dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
                    dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
                    dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
                    dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
                    dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
                    dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
                    dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
                    dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
                    dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
                    dbStockAdjustment.setStockAdjustmentKey(newInventory.getInventoryId());

                    dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
                    statusDescription = stagingLineV2Repository.getStatusDescription(88L, dbInventory.getLanguageId());
                    dbStockAdjustment.setStatusDescription(statusDescription);

                    dbStockAdjustment.setDeletionIndicator(0L);
                    dbStockAdjustment.setCreatedOn(new Date());
                    dbStockAdjustment.setIsCompleted("Y");
                    dbStockAdjustment.setCreatedBy("MW_AMS");

                    com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
                    log.info("createdStockAdjustment: " + createStockAdjustment);
                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        movementQtyValue = "N";
                    }
                    if(stockAdjustment.getAdjustmentQty() > 0) {
                        movementQtyValue = "P";
                    }
                    createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                    return createStockAdjustment;
                }
            }
            List<IInventoryImpl> inventoryList = null;
            if(perpetualLine == null) {
                inventoryList = inventoryService.getStockAdjustmentInventory(
                        stockAdjustment.getCompanyCode(),
                        stockAdjustment.getBranchCode(),
                        LANG_ID,
                        stockAdjustment.getWarehouseId(),
                        stockAdjustment.getItemCode(),
                        stockAdjustment.getManufacturerName(),
                        1L);
                log.info("Inventory-----> Perpetual Line for this item is not found; ----> BinClassId 1 ---> " + inventoryList.size());

                if (inventoryList != null && !inventoryList.isEmpty()) {
                    //Stock Adjustment - Positive Qty - Have to add Stock Adjustment Qty with Inventory
                    if(stockAdjustment.getAdjustmentQty() > 0) {
                        InventoryV2 stkInventory = new InventoryV2();
                        BeanUtils.copyProperties(inventoryList.get(0), stkInventory, CommonUtils.getNullPropertyNames(inventoryList.get(0)));
                        Double INV_QTY = 0D;
                        if(inventoryList.get(0).getInventoryQuantity() != null) {
                            INV_QTY = inventoryList.get(0).getInventoryQuantity();
                            INV_QTY = INV_QTY + stockAdjustment.getAdjustmentQty();
                        }
                        Double ALLOC_QTY = 0D;
                        if(inventoryList.get(0).getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventoryList.get(0).getAllocatedQuantity();
                        }
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        stkInventory.setInventoryQuantity(INV_QTY);
                        stkInventory.setAllocatedQuantity(ALLOC_QTY);
                        stkInventory.setReferenceField4(TOT_QTY);
                        stkInventory.setDeletionIndicator(0L);
                        stkInventory.setUpdatedOn(new Date());
                        stkInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                        try {
                            stkInventory = inventoryV2Repository.save(stkInventory);
                            log.info("-----Inventory2 created-------: " + stkInventory);
                        } catch (Exception e) {
                            log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
                            e.printStackTrace();

                            // Inventory Error Handling
                            InventoryTrans newInventoryTrans = new InventoryTrans();
                            BeanUtils.copyProperties(stkInventory, newInventoryTrans, CommonUtils.getNullPropertyNames(stkInventory));
                            newInventoryTrans.setInventoryQuantity(INV_QTY);
                            newInventoryTrans.setReRun(0L);
                            InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                            log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                        }

                        //Insert New Record in StockAdjustment
                        com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventoryList.get(0).getReferenceField4(), inventoryList.get(0).getReferenceField8(), stockAdjustment);
                        if(stockAdjustment.getAdjustmentQty() < 0) {
                            movementQtyValue = "N";
                        }
                        if(stockAdjustment.getAdjustmentQty() > 0) {
                            movementQtyValue = "P";
                        }
                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        log.info("createdStockAdjustment: " + createStockAdjustment);
                        return createStockAdjustment;
                    }
                    //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        Double STK_ADJ_QTY = 0D;
                        Double INV_QTY = 0D;
                        Double STK_QTY = 0D;
                        Double ALLOC_QTY = 0D;
                        List<com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment> stockAdjustmentList = new ArrayList<>();
                        if(stockAdjustment.getAdjustmentQty() != null) {
                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                        }
                        outerloop:
                        for (IInventoryImpl inventory : inventoryList) {
                            if (inventory.getInventoryQuantity() != null) {
                                INV_QTY = inventory.getInventoryQuantity();
                            }
                            if (inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            if (STK_ADJ_QTY <= INV_QTY) {
                                STK_QTY = STK_ADJ_QTY;
                            } else if (STK_ADJ_QTY > INV_QTY) {
                                STK_QTY = INV_QTY;
                            } else if (INV_QTY == 0) {
                                STK_QTY = 0D;
                            }
                            log.info("STK_QTY -----1--->: " + STK_QTY);

                            InventoryV2 stkInventory = new InventoryV2();
                            BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                            INV_QTY = INV_QTY - STK_QTY;
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            stkInventory.setInventoryQuantity(INV_QTY);
                            stkInventory.setReferenceField4(TOT_QTY);
                            stkInventory.setDeletionIndicator(0L);
                            stkInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                            try {
                                stkInventory = inventoryV2Repository.save(stkInventory);
                                log.info("-----Inventory2 created-------: " + stkInventory);
                            } catch (Exception e) {
                                log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
                                e.printStackTrace();

                                // Inventory Error Handling
                                InventoryTrans newInventoryTrans = new InventoryTrans();
                                BeanUtils.copyProperties(stkInventory, newInventoryTrans, CommonUtils.getNullPropertyNames(stkInventory));
                                newInventoryTrans.setInventoryQuantity(INV_QTY);
                                newInventoryTrans.setReRun(0L);
                                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                            }

                            //Insert New Record in StockAdjustment
                            com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                            if (stockAdjustment.getAdjustmentQty() < 0) {
                                movementQtyValue = "N";
                            }
                            if (stockAdjustment.getAdjustmentQty() > 0) {
                                movementQtyValue = "P";
                            }
                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                            stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                            STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                            log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                            if (STK_ADJ_QTY < 0) {
                                STK_ADJ_QTY = 0D;
                            }
                            if (STK_ADJ_QTY == 0D) {
                                log.info("STK_ADJ_QTY fully adjusted: ");
                                break outerloop;
                            }
                        }
                        return stockAdjustmentList.get(0);
                    }
                }
                if (inventoryList == null || inventoryList.isEmpty()) {
                    throw new BadRequestException("No Stock found in the Bin Locations");
                }
            }
            if (perpetualLine == null && (inventoryList == null || inventoryList.isEmpty())) {
                throw new BadRequestException("No PerPetual Line & Inventory Found for given ItemCode, ManufacturerName : " + stockAdjustment.getItemCode() + ", " + stockAdjustment.getManufacturerName());
            }
        }

        //Stock Adjustment Damage Code
        if (stockAdjustment.getIsDamage().equalsIgnoreCase("Y") && stockAdjustment.getIsCycleCount().equalsIgnoreCase("N")) {
            log.info("IsDamage: " + stockAdjustment.getIsDamage());

            InventoryV2 dbInventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
                    stockAdjustment.getCompanyCode(),
                    stockAdjustment.getBranchCode(),
                    "EN",
                    stockAdjustment.getWarehouseId(),
                    stockAdjustment.getItemCode(),
                    "99999",
                    7L,
                    stockAdjustment.getManufacturerName());
            log.info("Inventory: " + dbInventory);

            if (dbInventory != null) {
                if (dbInventory.getInventoryQuantity() > 0) {

                    Double STK_ADJ_QTY = 0D;
                    Double INV_QTY = 0D;
                    Double STK_QTY = 0D;
                    Double ALLOC_QTY = 0D;
                    List<com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment> stockAdjustmentList = new ArrayList<>();
                    if(stockAdjustment.getAdjustmentQty() != null) {
                        STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                    }
                    if(dbInventory.getInventoryQuantity() != null){
                        INV_QTY = dbInventory.getInventoryQuantity();
                    }
                    if(dbInventory.getAllocatedQuantity() != null) {
                        ALLOC_QTY = dbInventory.getAllocatedQuantity();
                    }
                    if (STK_ADJ_QTY <= INV_QTY) {
                        STK_QTY = STK_ADJ_QTY;
                    } else if (STK_ADJ_QTY > INV_QTY) {
                        STK_QTY = INV_QTY;
                    } else if (INV_QTY == 0) {
                        STK_QTY = 0D;
                    }
                    log.info("STK_QTY -----1--->: " + STK_QTY);

                    InventoryV2 stkInventory = new InventoryV2();
                    BeanUtils.copyProperties(dbInventory, stkInventory, CommonUtils.getNullPropertyNames(dbInventory));
                    INV_QTY = INV_QTY - STK_QTY;
                    Double TOT_QTY = INV_QTY + ALLOC_QTY;
                    stkInventory.setInventoryQuantity(INV_QTY);
                    stkInventory.setReferenceField4(TOT_QTY);
                    stkInventory.setDeletionIndicator(0L);
                    stkInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                    try {
                        stkInventory = inventoryV2Repository.save(stkInventory);
                        log.info("-----Inventory2 created-------: " + stkInventory);
                    } catch (Exception e) {
                        log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
                        e.printStackTrace();

                        // Inventory Error Handling
                        InventoryTrans newInventoryTrans = new InventoryTrans();
                        BeanUtils.copyProperties(stkInventory, newInventoryTrans, CommonUtils.getNullPropertyNames(stkInventory));
                        newInventoryTrans.setInventoryQuantity(INV_QTY);
                        newInventoryTrans.setReRun(0L);
                        InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                        log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                    }

                    //Insert New Record in StockAdjustment
                    com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, dbInventory.getReferenceField4(), dbInventory.getReferenceField8(), stockAdjustment);
                    if(stockAdjustment.getAdjustmentQty() < 0) {
                        movementQtyValue = "N";
                    }
                    if(stockAdjustment.getAdjustmentQty() > 0) {
                        movementQtyValue = "P";
                    }
                    createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                    stockAdjustmentList.add(createStockAdjustment);

                    STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                    log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);

                    if(STK_ADJ_QTY > 0) {
                        List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                                stockAdjustment.getCompanyCode(),
                                stockAdjustment.getBranchCode(),
                                LANG_ID,
                                stockAdjustment.getWarehouseId(),
                                stockAdjustment.getItemCode(),
                                stockAdjustment.getManufacturerName(),
                                1L);
                        log.info("Inventory-----> BinclassId 7 for this item has been emptied; ----> BinClassId 1 ---> " + inventoryList);
                        if(inventoryList != null && !inventoryList.isEmpty()){
                            //Stock Adjustment - Negative Qty - Have to Subtract Stock Adjustment Qty From Inventory
                            outerloop:
                            for(IInventoryImpl inventory : inventoryList) {
                                if(inventory.getInventoryQuantity() != null){
                                    INV_QTY = inventory.getInventoryQuantity();
                                }
                                if(inventory.getAllocatedQuantity() != null) {
                                    ALLOC_QTY = inventory.getAllocatedQuantity();
                                }
                                if (STK_ADJ_QTY <= INV_QTY) {
                                    STK_QTY = STK_ADJ_QTY;
                                } else if (STK_ADJ_QTY > INV_QTY) {
                                    STK_QTY = INV_QTY;
                                } else if (INV_QTY == 0) {
                                    STK_QTY = 0D;
                                }
                                log.info("STK_QTY -----1--->: " + STK_QTY);

                                InventoryV2 stkInventoryNew = new InventoryV2();
                                BeanUtils.copyProperties(inventory, stkInventoryNew, CommonUtils.getNullPropertyNames(inventory));
                                INV_QTY = INV_QTY - STK_QTY;
                                TOT_QTY = INV_QTY + ALLOC_QTY;
                                stkInventoryNew.setInventoryQuantity(INV_QTY);
                                stkInventoryNew.setReferenceField4(TOT_QTY);
                                stkInventoryNew.setDeletionIndicator(0L);
                                stkInventoryNew.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                                inventoryV2Repository.save(stkInventoryNew);

                                //Insert New Record in StockAdjustment
                                com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustmentNew = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                                if(stockAdjustment.getAdjustmentQty() < 0) {
                                    movementQtyValue = "N";
                                }
                                if(stockAdjustment.getAdjustmentQty() > 0) {
                                    movementQtyValue = "P";
                                }
                                createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                                stockAdjustmentList.add(createStockAdjustmentNew);

                                STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                                log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);

                                if(STK_ADJ_QTY < 0) {
                                    STK_ADJ_QTY = 0D;
                                }
                                if(STK_ADJ_QTY == 0D) {
                                    log.info("STK_ADJ_QTY fully adjusted: ");
                                    break outerloop;
                                }
                            }
                            if(STK_ADJ_QTY > 0){
                                throw new BadRequestException("Stock Adjustment Qty is greater than physically present in inventory");
                            }
                            return stockAdjustmentList.get(0);
                        }
                        if(inventoryList == null || inventoryList.isEmpty()){
                            throw new BadRequestException("No Stock found in the Bin Locations");
                        }
                    }
                    return stockAdjustmentList.get(0);
//                    }
                }
                if (dbInventory.getInventoryQuantity() <= 0) {
                    List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                            stockAdjustment.getCompanyCode(),
                            stockAdjustment.getBranchCode(),
                            LANG_ID,
                            stockAdjustment.getWarehouseId(),
                            stockAdjustment.getItemCode(),
                            stockAdjustment.getManufacturerName(),
                            1L);
                    log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);

                    if(inventoryList != null && !inventoryList.isEmpty()){
                        Double STK_ADJ_QTY = 0D;
                        Double INV_QTY = 0D;
                        Double STK_QTY = 0D;
                        Double ALLOC_QTY = 0D;
                        List<com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment> stockAdjustmentList = new ArrayList<>();
                        if(stockAdjustment.getAdjustmentQty() != null) {
                            STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                        }
                        outerloop:
                        for(IInventoryImpl inventory : inventoryList) {
                            if(inventory.getInventoryQuantity() != null){
                                INV_QTY = inventory.getInventoryQuantity();
                            }
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            if (STK_ADJ_QTY <= INV_QTY) {
                                STK_QTY = STK_ADJ_QTY;
                            } else if (STK_ADJ_QTY > INV_QTY) {
                                STK_QTY = INV_QTY;
                            } else if (INV_QTY == 0) {
                                STK_QTY = 0D;
                            }
                            log.info("STK_QTY -----1--->: " + STK_QTY);

                            InventoryV2 stkInventory = new InventoryV2();
                            BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                            INV_QTY = INV_QTY - STK_QTY;
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            stkInventory.setInventoryQuantity(INV_QTY);
                            stkInventory.setReferenceField4(TOT_QTY);
                            stkInventory.setDeletionIndicator(0L);
                            stkInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                            try {
                                stkInventory = inventoryV2Repository.save(stkInventory);
                                log.info("-----Inventory2 created-------: " + stkInventory);
                            } catch (Exception e) {
                                log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
                                e.printStackTrace();

                                // Inventory Error Handling
                                InventoryTrans newInventoryTrans = new InventoryTrans();
                                BeanUtils.copyProperties(stkInventory, newInventoryTrans, CommonUtils.getNullPropertyNames(stkInventory));
                                newInventoryTrans.setInventoryQuantity(INV_QTY);
                                newInventoryTrans.setReRun(0L);
                                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                            }

                            //Insert New Record in StockAdjustment
                            com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                            if(stockAdjustment.getAdjustmentQty() < 0) {
                                movementQtyValue = "N";
                            }
                            if(stockAdjustment.getAdjustmentQty() > 0) {
                                movementQtyValue = "P";
                            }
                            createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                            stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                            STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                            log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                            if(STK_ADJ_QTY < 0) {
                                STK_ADJ_QTY = 0D;
                            }
                            if(STK_ADJ_QTY == 0D) {
                                log.info("STK_ADJ_QTY fully adjusted: ");
                                break outerloop;
                            }
                        }
                        return stockAdjustmentList.get(0);
//                        }
                    }
                    if(inventoryList == null || inventoryList.isEmpty()){
                        throw new BadRequestException("No Stock found in the Bin Locations");
                    }
                }
            }
            if (dbInventory == null) {
                List<IInventoryImpl> inventoryList = inventoryService.getStockAdjustmentInventory(
                        stockAdjustment.getCompanyCode(),
                        stockAdjustment.getBranchCode(),
                        LANG_ID,
                        stockAdjustment.getWarehouseId(),
                        stockAdjustment.getItemCode(),
                        stockAdjustment.getManufacturerName(),
                        1L);
                log.info("Inventory-----> BinclassId 7 for this item is empty; ----> BinClassId 1 ---> " + inventoryList);

                if(inventoryList != null && !inventoryList.isEmpty()){
                    Double STK_ADJ_QTY = 0D;
                    Double INV_QTY = 0D;
                    Double STK_QTY = 0D;
                    Double ALLOC_QTY = 0D;
                    List<com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment> stockAdjustmentList = new ArrayList<>();
                    if(stockAdjustment.getAdjustmentQty() != null) {
                        STK_ADJ_QTY = abs(stockAdjustment.getAdjustmentQty());
                    }
                    outerloop:
                    for(IInventoryImpl inventory : inventoryList) {
                        if(inventory.getInventoryQuantity() != null){
                            INV_QTY = inventory.getInventoryQuantity();
                        }
                        if(inventory.getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventory.getAllocatedQuantity();
                        }
                        if (STK_ADJ_QTY <= INV_QTY) {
                            STK_QTY = STK_ADJ_QTY;
                        } else if (STK_ADJ_QTY > INV_QTY) {
                            STK_QTY = INV_QTY;
                        } else if (INV_QTY == 0) {
                            STK_QTY = 0D;
                        }
                        log.info("STK_QTY -----1--->: " + STK_QTY);

                        InventoryV2 stkInventory = new InventoryV2();
                        BeanUtils.copyProperties(inventory, stkInventory, CommonUtils.getNullPropertyNames(inventory));
                        INV_QTY = INV_QTY - STK_QTY;
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        stkInventory.setInventoryQuantity(INV_QTY);
                        stkInventory.setReferenceField4(TOT_QTY);
                        stkInventory.setDeletionIndicator(0L);
                        stkInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
                        try {
                            stkInventory = inventoryV2Repository.save(stkInventory);
                            log.info("-----Inventory2 created-------: " + stkInventory);
                        } catch (Exception e) {
                            log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
                            e.printStackTrace();

                            // Inventory Error Handling
                            InventoryTrans newInventoryTrans = new InventoryTrans();
                            BeanUtils.copyProperties(stkInventory, newInventoryTrans, CommonUtils.getNullPropertyNames(stkInventory));
                            newInventoryTrans.setInventoryQuantity(INV_QTY);
                            newInventoryTrans.setReRun(0L);
                            InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                            log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                        }

                        //Insert New Record in StockAdjustment
                        com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = createStockAdjustment(stkInventory, inventory.getReferenceField4(), inventory.getReferenceField8(), stockAdjustment);
                        if(stockAdjustment.getAdjustmentQty() < 0) {
                            movementQtyValue = "N";
                        }
                        if(stockAdjustment.getAdjustmentQty() > 0) {
                            movementQtyValue = "P";
                        }
                        createInventoryMovementV2(createStockAdjustment, movementQtyValue);
                        stockAdjustmentList.add(createStockAdjustment);

//                            if (STK_ADJ_QTY > STK_QTY) {
                        STK_ADJ_QTY = STK_ADJ_QTY - STK_QTY;
                        log.info("STK_ADJ_QTY, STK_QTY: " + STK_ADJ_QTY + ", " + STK_QTY);
//                            }
                        if(STK_ADJ_QTY < 0) {
                            STK_ADJ_QTY = 0D;
                        }
                        if(STK_ADJ_QTY == 0D) {
                            log.info("STK_ADJ_QTY fully adjusted: ");
                            break outerloop;
                        }
                    }
                    return stockAdjustmentList.get(0);
//                    }
                }
                if(inventoryList == null || inventoryList.isEmpty()){
                    throw new BadRequestException("No Stock found in the Bin Locations");
                }
            }
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PerpetualLineV2 getPerpetualLineForStockAdjustmentV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String itemCode, String manufacturerName) {
        PerpetualLineV2 perpetualLine =
                perpetualLineV2Repository.findPerpetualLineByItemCode(
                        itemCode, companyCodeId, plantId, languageId, warehouseId, manufacturerName);
        return perpetualLine;
    }

    /**
     *
     * @param newInventory
     * @param inventoryQty
     * @param itemDesc
     * @param stockAdjustment
     * @return
     */
    public com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment(InventoryV2 newInventory,Double inventoryQty, String itemDesc,
                                                 StockAdjustment stockAdjustment){
        //StockAdjustment Record Insert
        com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment dbStockAdjustment = new com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment();
        BeanUtils.copyProperties(newInventory, dbStockAdjustment, CommonUtils.getNullPropertyNames(newInventory));

        dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
        dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
        dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());

        if (stockAdjustment.getItemDescription() != null) {
            dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
        }
        if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() != null) {
            dbStockAdjustment.setItemDescription(newInventory.getDescription());
        }
        if (stockAdjustment.getItemDescription() == null && newInventory.getDescription() == null) {
            if (itemDesc != null) {
                dbStockAdjustment.setItemDescription(itemDesc);
            }
        }

        if (inventoryQty != null) {
            dbStockAdjustment.setBeforeAdjustment(inventoryQty);
        }
        if (inventoryQty == null) {
            dbStockAdjustment.setBeforeAdjustment(0D);
        }
        dbStockAdjustment.setAfterAdjustment(newInventory.getInventoryQuantity());

        dbStockAdjustment.setPackBarcodes(newInventory.getPackBarcodes());
        dbStockAdjustment.setBranchName(newInventory.getPlantDescription());
        dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
        dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
        dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
        dbStockAdjustment.setReferenceField1(stockAdjustment.getRefDocType());
        dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
        dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
        dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
        dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
        dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
        dbStockAdjustment.setMiddlewareId(stockAdjustment.getMiddlewareId());
        dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
        dbStockAdjustment.setSaUpdatedOn(stockAdjustment.getUpdatedOn());
        dbStockAdjustment.setStockAdjustmentKey(System.currentTimeMillis());

        dbStockAdjustment.setStatusId(88L);                 //Hard Code - StockAdjustment Done/Closed
        statusDescription = stagingLineV2Repository.getStatusDescription(88L, newInventory.getLanguageId());
        dbStockAdjustment.setStatusDescription(statusDescription);

        dbStockAdjustment.setDeletionIndicator(0L);
        dbStockAdjustment.setIsCompleted("Y");
        dbStockAdjustment.setCreatedOn(new Date());
        dbStockAdjustment.setCreatedBy("MW_AMS");

        com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment createStockAdjustment = stockAdjustmentRepository.save(dbStockAdjustment);
        log.info("createdStockAdjustment: " + createStockAdjustment);

        return createStockAdjustment;
    }


    /**
     *
     * @param stockAdjustment
     * @param movementQtyValue
     */
    private void createInventoryMovementV2(com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment stockAdjustment, String movementQtyValue) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(stockAdjustment, inventoryMovement, CommonUtils.getNullPropertyNames(stockAdjustment));
        inventoryMovement.setCompanyCodeId(stockAdjustment.getCompanyCode());
        inventoryMovement.setPlantId(stockAdjustment.getBranchCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(4L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        inventoryMovement.setManufacturerName(stockAdjustment.getManufacturerName());
        inventoryMovement.setRefDocNumber(stockAdjustment.getAmsReferenceNo());
        inventoryMovement.setCompanyDescription(stockAdjustment.getCompanyDescription());
        inventoryMovement.setPlantDescription(stockAdjustment.getBranchName());
        inventoryMovement.setWarehouseDescription(stockAdjustment.getWarehouseDescription());
        inventoryMovement.setBarcodeId(stockAdjustment.getBarcodeId());
        inventoryMovement.setDescription(stockAdjustment.getItemDescription());

        // MVT_DOC_NO
//        inventoryMovement.setMovementDocumentNo(String.valueOf(stockAdjustment.getStockAdjustmentKey()));
        inventoryMovement.setReferenceField10(String.valueOf(stockAdjustment.getStockAdjustmentKey()));

        // ST_BIN
        inventoryMovement.setStorageBin(stockAdjustment.getStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(stockAdjustment.getAdjustmentQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // MVT_UOM
        inventoryMovement.setInventoryUom(stockAdjustment.getInventoryUom());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                stockAdjustment.getCompanyCode(),
                stockAdjustment.getBranchCode(),
                stockAdjustment.getLanguageId(),
                stockAdjustment.getWarehouseId(),
                stockAdjustment.getManufacturerName(),
                stockAdjustment.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if(sumOfInvQty != null) {
            inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - stockAdjustment.getAdjustmentQty();
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + stockAdjustment.getAdjustmentQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }
        if(sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
            Double openQty = 0D;
            sumOfInvQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - stockAdjustment.getAdjustmentQty();
                if(openQty < 0){
                    openQty = 0D;
                }
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + stockAdjustment.getAdjustmentQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }

        inventoryMovement.setVariantCode(1L);
        inventoryMovement.setVariantSubCode("1");

        inventoryMovement.setPackBarcodes(stockAdjustment.getPackBarcodes());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(stockAdjustment.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(new Date());
        inventoryMovement.setMovementDocumentNo(String.valueOf(System.currentTimeMillis()));
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement : " + inventoryMovement);
    }


    //Save StockAdjustment
//    private StockAdjustment saveStockAdjustment(StockAdjustment stockAdjustment) {
//        try {
//
//            StockAdjustment dbStockAdjustment = new StockAdjustment();
//            dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
//            dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());
//            dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
//            dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
//            dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
//            dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
//            dbStockAdjustment.setItemCode(stockAdjustment.getItemCode());
//            dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
//            dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
//            dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
//            dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
//            dbStockAdjustment.setManufacturerName(stockAdjustment.getManufacturerName());
//            dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
//            dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
//            dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
//            dbStockAdjustment.setUpdatedOn(stockAdjustment.getUpdatedOn());
//            dbStockAdjustment.setMiddlewareId(stockAdjustment.getStockAdjustmentId());
//            dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
//
//            // Get Warehouse
//            Optional<Warehouse> dbWarehouse =
//                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//                            stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(),
//                            "EN", 0L);
//            log.info("dbWarehouse: " + dbWarehouse);
//            validateWarehouseId(dbWarehouse.get().getWarehouseId());
//            dbStockAdjustment.setWarehouseId(dbWarehouse.get().getWarehouseId());
//
//            dbStockAdjustment.setRefDocType("Stock Adjustment");
//            dbStockAdjustment.setOrderReceivedOn(new Date());
////			dbStockAdjustment.setOrderProcessedOn(new Date());
//
//            IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
//                    stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), dbWarehouse.get().getWarehouseId());
//            if (iKeyValuePair != null) {
//                dbStockAdjustment.setCompanyDescription(iKeyValuePair.getCompanyDesc());
//                dbStockAdjustment.setPlantDescription(iKeyValuePair.getPlantDesc());
//                dbStockAdjustment.setWarehouseDescription(iKeyValuePair.getWarehouseDesc());
//            }
//
//            if (dbStockAdjustment != null) {
//                try {
//                    dbStockAdjustment.setProcessedStatusId(0L);
//                    log.info("stockAdjustment: " + dbStockAdjustment);
//                    StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
//                    log.info("StockAdjustment Order Success: " + createdOrder);
//                    return createdOrder;
//                } catch (Exception e) {
//                    dbStockAdjustment.setProcessedStatusId(100L);
//                    log.info("StockAdjustment: " + dbStockAdjustment);
//                    StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
//                    log.info("StockAdjustment Order Failed: " + createdOrder);
//                    throw e;
//                }
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//        return null;
//    }
//
////    /**
////     *
////     * @param wareHouseId
////     * @return
////     */
////    private boolean validateWarehouseId(String wareHouseId) {
////        log.info("wareHouseId: " + wareHouseId);
////        if (wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_300)) {
////            log.info("wareHouseId:------------> " + wareHouseId);
////            return true;
////        } else {
////            throw new BadRequestException("Warehouse Id must be 300");
////        }
////    }

}
