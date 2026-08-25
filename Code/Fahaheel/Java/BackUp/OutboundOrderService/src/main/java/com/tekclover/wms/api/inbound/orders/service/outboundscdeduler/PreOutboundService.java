package com.tekclover.wms.api.inbound.orders.service.outboundscdeduler;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.HHTUser;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.service.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PreOutboundService extends BaseService {

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PreOutboundHeaderRepository preOutboundHeaderRepository;
    @Autowired
    private PreOutboundLineRepository preOutboundLineRepository;
    @Autowired
    private OrderManagementHeaderRepository orderManagementHeaderRepository;
    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;
    @Autowired
    private OutboundLineRepository outboundLineRepository;
    @Autowired
    private OrderManagementLineRepository orderManagementLineRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    private OrderManagementLineService orderManagementLineService;
    @Autowired
    private OutboundIntegrationLogRepository outboundIntegrationLogRepository;
    @Autowired
    private MastersService mastersService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OutboundService outboundService;
    @Autowired
    SalesOrderService salesOrderService;
    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    private PushNotificationService pushNotificationService;

    //------------------------------------------------------------------------------------------------------

    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    private OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    protected AuthTokenService authTokenService;
    @Autowired
    private PickListHeaderRepository pickListHeaderRepository;
    @Autowired
    InventoryTransRepository inventoryTransRepository;
    @Autowired
    WarehouseRepository warehouseIdRepository;
    String statusDescription = null;

    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws BadRequestException
     * @throws SQLException
     * @throws SQLServerException
     * @throws CannotAcquireLockException
     * @throws LockAcquisitionException
     * @throws Exception
     */
    public OutboundHeaderV2 processOutboundReceivedV2(OutboundIntegrationHeaderV2 outboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException,
            SQLException, SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
            /*
             * Checking whether received refDocNumber processed already.
             */
            log.info("Outbound Process Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + ", " + outboundIntegrationHeader.getOutboundOrderTypeID());
//        Optional<PreOutboundHeaderV2> orderProcessedStatus =
//                preOutboundHeaderV2Repository.findByRefDocNumberAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), 0L);
            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);

            if (!orderProcessedStatus.isEmpty()) {
//            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), 100L);
                throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() +
                        " already processed. Reprocessing can't be allowed.");
            }

            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            PickListCancellation createPickListCancellation = null;
            Optional<Warehouse> warehouse = null;
            String warehouseId = null;
            String companyCodeId = null;
            String plantId = null;
            String languageId = null;

            try {
                warehouse = warehouseIdRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN", 0L);
                log.info("warehouse : " + warehouse.get().getWarehouseId());

                warehouseId = warehouse.get().getWarehouseId();
                companyCodeId = outboundIntegrationHeader.getCompanyCode();
                plantId = outboundIntegrationHeader.getBranchCode();
                languageId = warehouse.get().getLanguageId();
                log.info("warehouseId : " + warehouseId);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            if (warehouse == null) {
                log.info("warehouse not found.");
                throw new BadRequestException("Warehouse cannot be null.");
            }

            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4 || outboundIntegrationHeader.getReferenceDocumentType().equalsIgnoreCase("Sales Invoice")) {
                try {
//            OutboundHeaderV2 updateOutboundHeaderAndLine = outboundHeaderService.updateOutboundHeaderForSalesInvoice(outboundIntegrationHeader, warehouseId);
                    OutboundHeaderV2 updateOutboundHeaderAndLine = outboundService.updateOutboundHeaderForSalesInvoiceV2(outboundIntegrationHeader, warehouseId);
                    log.info("SalesInvoice Updated in OutboundHeader and Line");
                    if (updateOutboundHeaderAndLine == null) {
                        updateOutboundHeaderAndLine = new OutboundHeaderV2();
                    }
                    return updateOutboundHeaderAndLine;
                } catch (Exception e) {
                    throw e;
                }
            }

            //PickList Cancellation
            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
                log.info("Executing PickList cancellation scenario pre - checkup process");
                String salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();
                companyCodeId = outboundIntegrationHeader.getCompanyCode();
                plantId = outboundIntegrationHeader.getBranchCode();
                warehouseId = outboundIntegrationHeader.getWarehouseID();
                languageId = outboundIntegrationHeader.getLanguageId();
//            String loginUserID = MW_AMS;                                     //Hard Coded

                //Check WMS order table
//            List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndDeletionIndicator(salesOrderNumber, 0L);
                List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndOutboundOrderTypeIdAndDeletionIndicator(salesOrderNumber, outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);
                log.info("SalesOrderNumber already Exist: ---> PickList Cancellation to be executed " + salesOrderNumber);
                String newPickListNo = outboundIntegrationHeader.getPickListNumber();
                if (outbound != null && !outbound.isEmpty()) {
//                List<String> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).map(OutboundHeaderV2::getPickListNumber).collect(Collectors.toList());
                    List<OutboundHeaderV2> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).collect(Collectors.toList());
                    log.info("Old PickList Number, New PickList Number: " + oldPickListNo + ", " + newPickListNo);

                    if (oldPickListNo != null && !oldPickListNo.isEmpty()) {
                        for (OutboundHeaderV2 oldPickListNumber : oldPickListNo) {
                            OutboundHeaderV2 outboundOrderV2 =
                                    outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                                            companyCodeId, languageId, plantId, warehouseId, oldPickListNumber.getPickListNumber(), oldPickListNumber.getPreOutboundNo(), 0L);
                            log.info("Outbound Order status ---> Delivered for old Picklist Number: " + outboundOrderV2 + ", " + oldPickListNumber);

                            if (outboundOrderV2 != null && outboundOrderV2.getInvoiceNumber() != null) {
                                // Update error message for the new PicklistNo
                                throw new BadRequestException("Picklist cannot be cancelled as Sales order associated with picklist - Invoice has been raised");
                            }

                            log.info("Old PickList Number: " + oldPickListNumber.getPickListNumber() + ", " +
                                    oldPickListNumber.getPreOutboundNo() + " Cancellation Initiated and followed by New PickList " + newPickListNo + " creation started");

                            //Delete old PickListData
                            createPickListCancellation = orderService.pickListCancellationNew(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber.getPickListNumber(), newPickListNo, oldPickListNumber.getPreOutboundNo(), "MW_AMS");
                        }
                    }
                }
            }

            // Getting PreOutboundNo from NumberRangeTable
            String preOutboundNo = getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
            String refField1ForOrderType = null;

            List<PreOutboundLineV2> overallCreatedPreoutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                log.info("outboundIntegrationLine : " + outboundIntegrationLine);

                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
            }


            /*
             * Append PREOUTBOUNDLINE table through below logic
             */
            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                // PreOutboundLine
                try {
                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId,
                            plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine);
                    PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
                    log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
                    createdPreOutboundLineList.add(createdPreOutboundLine);

                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
            }

            createOrderManagementLine(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, createdPreOutboundLineList);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
            log.info("createOutboundLine created : " + createOutboundLineList);

            /*------------------Insert into PreOutboundHeader table-----------------------------*/
            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId,
                    plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType);
            log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

            /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader);
            log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

            /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(), outboundIntegrationHeader);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            //Pickup Header Automation only for Picklist Header - OutboundOrderTypeId - 3L --> wh_id = 200 referenceDocumentType - Pick List
            //for wh_id = 200 ---> ob_type_id=3 && wh_id = 100 ---> ob_type_id=0,1,3
            if ((warehouseId.equalsIgnoreCase("200") &&warehouseId.equalsIgnoreCase("300") && outboundIntegrationHeader.getOutboundOrderTypeID() == 3L)) {
                createPickUpHeaderAssignPickerModified(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader,
                        preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
            }

            //PickList Cancellation
            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
                if (createPickListCancellation != null) {
                    log.info("Executing Post PickList cancellation scenario");
                    String salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();
                    companyCodeId = outboundIntegrationHeader.getCompanyCode();
                    plantId = outboundIntegrationHeader.getBranchCode();
                    warehouseId = outboundIntegrationHeader.getWarehouseID();
                    languageId = outboundIntegrationHeader.getLanguageId();
                    //String loginUserID = "MW_AMS";                                     //Hard Coded

                    //Create PickListData
                    createPickListCancellation(companyCodeId, plantId, languageId, warehouseId,
                            createPickListCancellation.getOldPickListNumber(), createPickListCancellation.getNewPickListNumber(),
                            preOutboundNo, createPickListCancellation, "MW_AMS");
                }
            }
            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            rollback(outboundIntegrationHeader);
            updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());

            throw e;
        }
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLineList
     * @throws Exception
     */
//    @Transactional
    private void createOrderManagementLine(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                           OutboundIntegrationHeaderV2 outboundIntegrationHeader, List<PreOutboundLineV2> preOutboundLineList) throws Exception {
        OrderManagementLineV2 orderManagementLine = null;
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                orderManagementLine = createOrderManagementLineV2(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, preOutboundLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        log.info("orderManagementLine created---1---> : " + orderManagementLine);
    }

    /**
     * @param createdPreOutboundLine
     * @return
     */
    private List<OutboundLineV2> createOutboundLineV2(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader) {
        List<OutboundLineV2> outboundLines = new ArrayList<>();
        for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
            List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getOrderManagementLineV2(
                    preOutboundLine.getCompanyCodeId(),
                    preOutboundLine.getPlantId(),
                    preOutboundLine.getLanguageId(),
                    preOutboundLine.getWarehouseId(),
                    preOutboundLine.getPreOutboundNo(),
                    preOutboundLine.getLineNumber(),
                    preOutboundLine.getItemCode());
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                OutboundLineV2 outboundLine = new OutboundLineV2();
                BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
                outboundLine.setDeliveryQty(0D);
                outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                outboundLine.setCreatedBy(preOutboundLine.getCreatedBy());
                outboundLine.setCreatedOn(preOutboundLine.getCreatedOn());

                IKeyValuePair description = stagingLineV2Repository.getDescription(preOutboundLine.getCompanyCodeId(),
                        preOutboundLine.getLanguageId(),
                        preOutboundLine.getPlantId(),
                        preOutboundLine.getWarehouseId());

                if (preOutboundLine.getStatusId() != null) {
                    statusDescription = stagingLineV2Repository.getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                    outboundLine.setStatusDescription(statusDescription);
                }
                outboundLine.setCompanyDescription(description.getCompanyDesc());
                outboundLine.setPlantDescription(description.getPlantDesc());
                outboundLine.setWarehouseDescription(description.getWarehouseDesc());

                outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());

                if (outboundLine.getOutboundOrderTypeId() == 3L) {
                    outboundLine.setCustomerType("INVOICE");
                }
                if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
                    outboundLine.setCustomerType("TRANSVERSE");
                }
                outboundLines.add(outboundLine);
            }
        }
        outboundLines = outboundLineV2Repository.saveAll(outboundLines);
        log.info("outboundLines created -----2------>: " + outboundLines);
        return outboundLines;
    }


//    /**
//     * @param warehouseId
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @return
//     */
//    public String getPreOutboundNo(String warehouseId, String companyCode, String plantId, String languageId) {
//        try {
//            String nextRangeNumber = mastersService.getNextNumberRange(9L, warehouseId, companyCode, plantId, languageId);
//            return nextRangeNumber;
//        } catch (Exception e) {
//            throw new BadRequestException("Error on Number generation." + e.toString());
//        }
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param customerId
//     * @param salesOrderNumber
//     * @param outboundOrderTypeId
//     * @param idMasterAuthToken
//     * @return
//     */
//    private String getPreOutboundNoV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                    String customerId, String salesOrderNumber, Long outboundOrderTypeId, String idMasterAuthToken) {
//        Optional<PreOutboundHeaderV2> orderProcessedStatus =
//                preOutboundHeaderV2Repository.findTopBySalesOrderNumberAndOutboundOrderTypeIdAndCustomerIdAndDeletionIndicator(salesOrderNumber, outboundOrderTypeId, customerId, 0L);
//        if (orderProcessedStatus.isPresent()) {
//            log.info("------preOutboundNo---------existing----one---> " + orderProcessedStatus.get().getPreOutboundNo());
//            return orderProcessedStatus.get().getPreOutboundNo();
//        } else {
//            // Getting PreOutboundNo from NumberRangeTable
//            return getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//        }
//    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param refField1ForOrderType
     * @return
     * @throws ParseException
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, String refField1ForOrderType) throws ParseException {
       AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(outboundIntegrationHeader, preOutboundHeader, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
        preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());    // Hardcoded value "0"
//        preOutboundHeader.setReferenceDocumentType("SO");                                                // Hardcoded value "SO"

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        preOutboundHeader.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCodeId,
                languageId,
                plantId,
                warehouseId);

        preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
        preOutboundHeader.setPlantDescription(description.getPlantDesc());
        preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());

        preOutboundHeader.setMiddlewareId(outboundIntegrationHeader.getMiddlewareId());
        preOutboundHeader.setMiddlewareTable(outboundIntegrationHeader.getMiddlewareTable());
        preOutboundHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
        preOutboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
        preOutboundHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
        preOutboundHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        preOutboundHeader.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        preOutboundHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
        preOutboundHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
        preOutboundHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
        preOutboundHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

        // Status Description
//		StatusId idStatus = idmasterService.getStatus(39L, outboundIntegrationHeader.getWarehouseID(), authTokenForIDService.getAccess_token());
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        // REF_FIELD_10
        preOutboundHeader.setReferenceField10(statusDescription);

        preOutboundHeader.setStatusDescription(statusDescription);

        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy("MW_AMS");
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderV2Repository.save(preOutboundHeader);
        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
        return createdPreOutboundHeader;
    }



    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @return
     * @throws ParseException
     */
    private OrderManagementLineV2 createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                              OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine) throws ParseException {
        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));

        orderManagementLine.setMiddlewareId(preOutboundLine.getMiddlewareId());
        orderManagementLine.setMiddlewareHeaderId(preOutboundLine.getMiddlewareHeaderId());
        orderManagementLine.setMiddlewareTable(preOutboundLine.getMiddlewareTable());
        orderManagementLine.setReferenceDocumentType(preOutboundLine.getReferenceDocumentType());
        orderManagementLine.setSalesInvoiceNumber(preOutboundLine.getSalesInvoiceNumber());
        orderManagementLine.setManufacturerName(preOutboundLine.getManufacturerName());
        orderManagementLine.setItemCode(preOutboundLine.getItemCode());
        orderManagementLine.setReferenceField1(preOutboundLine.getReferenceField1());
        orderManagementLine.setManufacturerFullName(preOutboundLine.getManufacturerFullName());
        orderManagementLine.setPickListNumber(preOutboundLine.getPickListNumber());
        orderManagementLine.setSupplierInvoiceNo(preOutboundLine.getSupplierInvoiceNo());
        orderManagementLine.setTokenNumber(preOutboundLine.getTokenNumber());
        orderManagementLine.setTargetBranchCode(preOutboundLine.getTargetBranchCode());
        orderManagementLine.setPickupCreatedBy(preOutboundLine.getCreatedBy());
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

        orderManagementLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
        orderManagementLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
        orderManagementLine.setIsCompleted(preOutboundLine.getIsCompleted());
        orderManagementLine.setIsCancelled(preOutboundLine.getIsCancelled());

        log.info("orderManagementLine : " + orderManagementLine);

        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
        Long BIN_CLASS_ID;

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); //ZB,ZC,ZG,ZT
            BIN_CLASS_ID = 1L;
            orderManagementLine = outboundService.createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
        }

        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
            BIN_CLASS_ID = 7L;
            orderManagementLine = outboundService.createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
        }

        // PROP_ST_BIN
        return orderManagementLine;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param outboundIntegrationLine
     * @return
     * @throws ParseException
     */
    private PreOutboundLineV2 createPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                      OutboundIntegrationHeaderV2 outboundIntegrationHeader, OutboundIntegrationLineV2 outboundIntegrationLine) throws ParseException {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(outboundIntegrationLine, preOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationLine));
        preOutboundLine.setLanguageId(languageId);
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);

        // WH_ID
        preOutboundLine.setWarehouseId(warehouseId);

        // REF DOC Number
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

        // PRE_IB_NO
        preOutboundLine.setPreOutboundNo(preOutboundNo);

        // PARTNER_CODE
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

        // IB__LINE_NO
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

        // ITM_CODE
        preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());

        // OB_ORD_TYP_ID
        preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());

        // STATUS_ID
        preOutboundLine.setStatusId(39L);

        // STCK_TYP_ID
        preOutboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preOutboundLine.setSpecialStockIndicatorId(1L);

        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());

        IKeyValuePair description = stagingLineV2Repository.getDescription(preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getLanguageId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId());

        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        preOutboundLine.setStatusDescription(statusDescription);

        preOutboundLine.setCompanyDescription(description.getCompanyDesc());
        preOutboundLine.setPlantDescription(description.getPlantDesc());
        preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());

        preOutboundLine.setMiddlewareId(outboundIntegrationLine.getMiddlewareId());
        preOutboundLine.setMiddlewareHeaderId(outboundIntegrationLine.getMiddlewareHeaderId());
        preOutboundLine.setMiddlewareTable(outboundIntegrationLine.getMiddlewareTable());
        preOutboundLine.setReferenceDocumentType(outboundIntegrationLine.getReferenceDocumentType());
        preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
        preOutboundLine.setManufacturerFullName(outboundIntegrationLine.getManufacturerFullName());
        preOutboundLine.setManufacturerCode(outboundIntegrationLine.getManufacturerCode());
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
        preOutboundLine.setSalesOrderNumber(outboundIntegrationLine.getSalesOrderNumber());
        preOutboundLine.setSupplierInvoiceNo(outboundIntegrationLine.getSupplierInvoiceNo());
        preOutboundLine.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        preOutboundLine.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        preOutboundLine.setTransferOrderNo(outboundIntegrationLine.getTransferOrderNo());
        preOutboundLine.setReturnOrderNo(outboundIntegrationLine.getReturnOrderNo());
        preOutboundLine.setIsCompleted(outboundIntegrationLine.getIsCompleted());
        preOutboundLine.setIsCancelled(outboundIntegrationLine.getIsCancelled());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(companyCodeId);
        imBasicData.setPlantId(plantId);
        imBasicData.setLanguageId(languageId);
        imBasicData.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
        imBasicData.setItemCode(outboundIntegrationLine.getItemCode());
        imBasicData.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
//        ImBasicData1 imBasicData1 =
//                mastersService.getImBasicData1ByItemCodeV2(outboundIntegrationLine.getItemCode(),
//                        languageId,
//                        companyCodeId,
//                        plantId,
//                        outboundIntegrationHeader.getWarehouseID(),
//                        outboundIntegrationLine.getManufacturerName(),
//                        authTokenForMastersService.getAccess_token());
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null && imBasicData1.getDescription() != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
        } else {
            preOutboundLine.setDescription(outboundIntegrationLine.getItemText());
        }

        // ORD_QTY
        preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());

        // ORD_UOM
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

        // REQ_DEL_DATE
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

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
     * @param outboundIntegrationHeader
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @throws Exception
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                        String preOutboundNo, String refDocNumber, String partnerCode) throws Exception {

        try {
            List<OrderManagementLineV2> orderManagementLineV2List = orderService.getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

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

                        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
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
                    String PU_NO = mastersService.getNextNumberRange(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
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
                    newPickupHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                    newPickupHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                    newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
                    newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                    newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                    newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                    newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                    newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());
                    newPickupHeader.setImsSaleTypeCode(orderManagementLine.getImsSaleTypeCode());

                    newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
                    newPickupHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
                    newPickupHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
                    newPickupHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());
                    newPickupHeader.setCustomerCode(outboundIntegrationHeader.getCustomerCode());
                    newPickupHeader.setTransferRequestType(outboundIntegrationHeader.getTransferRequestType());

                    PickupHeaderV2 createdPickupHeader = orderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                    log.info("pickupHeader created: " + createdPickupHeader);
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

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @param outboundIntegrationHeader
     * @return
     * @throws ParseException
     */
    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId,
                                                    OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws ParseException {

        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeader.setCompanyDescription(createdPreOutboundHeader.getCompanyDescription());
        outboundHeader.setPlantDescription(createdPreOutboundHeader.getPlantDescription());
        outboundHeader.setWarehouseDescription(createdPreOutboundHeader.getWarehouseDescription());
        outboundHeader.setMiddlewareId(createdPreOutboundHeader.getMiddlewareId());
        outboundHeader.setMiddlewareTable(createdPreOutboundHeader.getMiddlewareTable());
        outboundHeader.setReferenceDocumentType(createdPreOutboundHeader.getReferenceDocumentType());

        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        outboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
        outboundHeader.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
        outboundHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
        outboundHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        outboundHeader.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        outboundHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
        outboundHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
        outboundHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
        outboundHeader.setMUpdatedOn(outboundHeader.getMUpdatedOn());

        if (outboundHeader.getOutboundOrderTypeId() == 3L) {
            outboundHeader.setCustomerType("INVOICE");
        }
        if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
            outboundHeader.setCustomerType("TRANSVERSE");
        }

        outboundHeader.setCreatedBy(createdPreOutboundHeader.getCreatedBy());
        outboundHeader.setCreatedOn(createdPreOutboundHeader.getCreatedOn());
        outboundHeader = outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader);
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
    private void createPickListCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
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
//                        Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
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
//                createNewPickUpLineList = pickupLineService.createPickupLineV2(newPickupLineList, loginUserID);
                createNewPickUpLineList = salesOrderService.createPickupLineNonCBMPickListCancellationV2(newPickupLineList, loginUserID);
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
                List<QualityLineV2> createNewQualityLineList = salesOrderService.createQualityLineV2(newQualityLineList, loginUserID);
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
     * @param orderId
     * @param outboundOrderTypeID
     */
    public void updateProcessedOrderV2(String orderId, Long outboundOrderTypeID) {
        log.info("rollback - rerun - orderId : " + orderId + "," + outboundOrderTypeID);
        OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
        if (dbOutboundOrder != null) {
            Long numberOfAttemts = 0L;
            Long attempted = 0L;
            Long processStatusId = 0L;
            if (dbOutboundOrder.getNumberOfAttempts() != null) {
                if (dbOutboundOrder.getNumberOfAttempts().equals(0L)) {
                    numberOfAttemts = 1L;
                    processStatusId = 0L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(1L)) {
                    numberOfAttemts = 2L;
                    processStatusId = 0L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(2L)) {
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(3L)) {
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
            } else {
                numberOfAttemts = 1L;
                processStatusId = 0L;
            }
            dbOutboundOrder.setProcessedStatusId(processStatusId);
            dbOutboundOrder.setNumberOfAttempts(numberOfAttemts);
            dbOutboundOrder.setOrderProcessedOn(new Date());
            OutboundOrderV2 updatedOutboundOrder = outboundOrderV2Repository.save(dbOutboundOrder);
            log.info("rollback rerun - updatedOutboundOrder : " + updatedOutboundOrder);
        }
    }

    /**
     * @param orderId
     * @param outboundOrderTypeID
     * @return
     */
    public OutboundOrderV2 getOBOrderByIdV2(String orderId, Long outboundOrderTypeID) {
        OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNoAndOutboundOrderTypeID(orderId, outboundOrderTypeID);
        if (dbOutboundOrder != null) {
            return dbOutboundOrder;
        } else {
            return null;
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
            //If PickupLine is Empty fill the line details from outboundLine and bin from OrderManagementLine
//            if(oldPickupLineList == null || oldPickupLineList.isEmpty()) {

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
//            }
            log.info("PickList Line: " + createPickListLineList);
            pickListHeader.setLine(createPickListLineList);
            PickListHeader createdPickListHeader = orderService.createPickListHeader(pickListHeader, loginUserID);
            log.info("Created PicklistHeader : " + createdPickListHeader);
        }
    }

//    /**
//     * @param outboundIntegrationHeader
//     * @return
//     * @throws Exception
//     */
//    public synchronized OutboundHeaderV2 processOutboundReceivedV5(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
//        try {
//            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
//            String plantId = outboundIntegrationHeader.getBranchCode();
//            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
//            String warehouseId = outboundIntegrationHeader.getWarehouseID();
//            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
//            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
//            log.info("Outbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
//            MW_AMS = outboundIntegrationHeader.getLoginUserId() != null ? outboundIntegrationHeader.getLoginUserId() : MW_AMS;
//
//            boolean isDuplicateOrder = isPreOutboundHeaderExist(refDocNumber, outboundOrderTypeId);
//            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
//            if (isDuplicateOrder) {
//                return new OutboundHeaderV2();
//            }
//
//            OutboundOrderProcess outboundOrderProcess = new OutboundOrderProcess();
//            String idMasterAuthToken = getIDMasterAuthToken();
//            /*
//             * Grouping PreOutboundHeader ID based on the Customer ID
//             */
//            String preOutboundNo = getPreOutboundNoV5(companyCodeId, plantId, languageId, warehouseId,
//                    outboundIntegrationHeader.getCustomerId(), outboundIntegrationHeader.getSalesOrderNumber(), outboundOrderTypeId, idMasterAuthToken);
//            log.info("PreOutboundNo : {}", preOutboundNo);
//
//            String refField1ForOrderType = null;
//
//            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//
//            Long statusId = 39L;
//            statusDescription = getStatusDescription(statusId, languageId);
//
//            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, statusId, statusDescription, description, MW_AMS);
//            log.info("preOutboundHeader Created : {}", createdPreOutboundHeader);
//
//            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
//            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
//                log.info("outboundIntegrationLine : " + outboundIntegrationLine);
//                try {
//                    // PreOutboundLine
//                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV5(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
//                            outboundIntegrationHeader, outboundIntegrationLine, statusId, statusDescription, description, MW_AMS);
//                    log.info("preOutboundLine created---1---> : " + preOutboundLine);
//                    createdPreOutboundLineList.add(preOutboundLine);
//                } catch (Exception e) {
//                    log.error("Error on processing PreOutboundLine : " + e.toString());
//                    e.printStackTrace();
//                }
//                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
//            }
//
//            createdPreOutboundLineList.stream().forEach(preOutboundLine -> {
//                try {
//                    log.info("OrderManagementLineCreate and Inventory Allocation Started ----------------------------> ");
//                    createOrderManagementLineV5(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundLine, MW_AMS);
//                    log.info("OrderManagementLineCreate and Inventory Allocation Completed ----------------------------> ");
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
////            createOrderManagementLineV5(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, createdPreOutboundLineList, MW_AMS);
//
//            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
//            List<OutboundLineV2> createOutboundLineList = createOutboundLineV5(createdPreOutboundLineList, outboundIntegrationHeader);
//            log.info("createOutboundLine created : " + createOutboundLineList);
//
//            statusId = 41L;
//            statusDescription = getStatusDescription(statusId, languageId);
//            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV5(createdPreOutboundHeader, statusId, statusDescription, MW_AMS);
//            log.info("OrderMangementHeader Created : {}", createdOrderManagementHeader);
//
//            OutboundHeaderV2 outboundHeader = createOutboundHeaderV5(createdPreOutboundHeader, outboundIntegrationHeader, statusId, statusDescription);
//            log.info("outboundHeader Created : {}", outboundHeader);
//
//            outboundOrderProcess.setOutboundHeader(outboundHeader);
//            outboundOrderProcess.setOutboundLines(createOutboundLineList);
//            outboundOrderProcess.setOrderManagementHeader(createdOrderManagementHeader);
//            outboundOrderProcess.setPreOutboundHeader(createdPreOutboundHeader);
//            outboundOrderProcess.setPreOutboundLines(createdPreOutboundLineList);
//            outboundOrderProcess.setOutboundIntegrationHeader(outboundIntegrationHeader);
//            postOutboundOrder(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId, outboundOrderProcess);
//
//            validatePickupHeaderCreation(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, MW_AMS);
//
//            return outboundHeader;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            // Updating the Processed Status
//            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
//            rollback(outboundIntegrationHeader);
//            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
//
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param refDocNumber
//     * @param orderTypeId
//     * @return
//     */
//    public boolean isPreOutboundHeaderExist(String refDocNumber, Long orderTypeId) {
//        return preOutboundHeaderV2Repository.existsByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(refDocNumber, orderTypeId, 0L);
//    }
//
//    /**
//     *
//     * @param refDocNumber
//     * @param orderTypeId
//     * @return
//     */
//    public boolean isPreOutboundHeaderExistV5(String refDocNumber, Long orderTypeId) {
//        return preOutboundHeaderV2Repository.existsByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(refDocNumber, orderTypeId, 0L);
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param outboundIntegrationHeader
//     * @param refField1ForOrderType
//     * @param statusId
//     * @param statusDesc
//     * @param desc
//     * @param loginUserId
//     * @return
//     * @throws Exception
//     */
//    private PreOutboundHeaderV2 createPreOutboundHeaderV5(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
//                                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, String refField1ForOrderType,
//                                                          Long statusId, String statusDesc, IKeyValuePair desc, String loginUserId) throws Exception {
//        try {
//            PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
//            BeanUtils.copyProperties(outboundIntegrationHeader, preOutboundHeader, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
//            preOutboundHeader.setCompanyCodeId(companyCodeId);
//            preOutboundHeader.setPlantId(plantId);
//            preOutboundHeader.setLanguageId(languageId);
//            preOutboundHeader.setWarehouseId(warehouseId);
//            preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
//            preOutboundHeader.setPreOutboundNo(preOutboundNo);
//            preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
//            preOutboundHeader.setRefDocDate(new Date());
//
//            // REF_FIELD_1
//            preOutboundHeader.setReferenceField1(refField1ForOrderType);
//            preOutboundHeader.setStatusId(statusId);
//            preOutboundHeader.setStatusDescription(statusDesc);
//            preOutboundHeader.setCompanyDescription(desc.getCompanyDesc());
//            preOutboundHeader.setPlantDescription(desc.getPlantDesc());
//            preOutboundHeader.setWarehouseDescription(desc.getWarehouseDesc());
//
//            preOutboundHeader.setDeletionIndicator(0L);
//            preOutboundHeader.setCreatedBy(loginUserId);
//            preOutboundHeader.setCreatedOn(new Date());
//            // Order_Text_Update
//            String text = "PreOutboundHeader Created";
//            outboundOrderV2Repository.updatePreOutBoundOrderText(preOutboundHeader.getOutboundOrderTypeId(), preOutboundHeader.getRefDocNumber(), text);
//            log.info("PreOutbound Header Status Updated Successfully");
//            return preOutboundHeader;
//        } catch (Exception e) {
//            log.error("Exception While PreoutboundHeader create: " + e);
//            throw e;
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
//     * @param outboundIntegrationHeader
//     * @param outboundIntegrationLine
//     * @param statusId
//     * @param statusDesc
//     * @param desc
//     * @param loginUserId
//     * @return
//     * @throws Exception
//     */
//    private PreOutboundLineV2 createPreOutboundLineV5(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
//                                                      OutboundIntegrationHeaderV2 outboundIntegrationHeader, OutboundIntegrationLineV2 outboundIntegrationLine,
//                                                      Long statusId, String statusDesc, IKeyValuePair desc,  String loginUserId) throws Exception {
//        try {
//             PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
//            BeanUtils.copyProperties(outboundIntegrationLine, preOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationLine));
//            preOutboundLine.setCompanyCodeId(companyCodeId);
//            preOutboundLine.setPlantId(plantId);
//            preOutboundLine.setLanguageId(languageId);
//            preOutboundLine.setWarehouseId(warehouseId);
//            preOutboundLine.setPreOutboundNo(preOutboundNo);
//            preOutboundLine.setCustomerId(outboundIntegrationHeader.getCustomerId());
//            preOutboundLine.setCustomerName(outboundIntegrationHeader.getCustomerName());
//
//            // REF DOC Number
//            preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
//
//            // PARTNER_CODE
//            preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
//
//            // IB__LINE_NO
//            preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());
//
//            // ITM_CODE
//            preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());
//
//            // OB_ORD_TYP_ID
//            preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
//
//            // STATUS_ID
//            preOutboundLine.setStatusId(statusId);
//            preOutboundLine.setStatusDescription(statusDesc);
//
//            // STCK_TYP_ID
//            preOutboundLine.setStockTypeId(1L);
//
//            // SP_ST_IND_ID
//            preOutboundLine.setSpecialStockIndicatorId(1L);
//
//            preOutboundLine.setCompanyDescription(desc.getCompanyDesc());
//            preOutboundLine.setPlantDescription(desc.getPlantDesc());
//            preOutboundLine.setWarehouseDescription(desc.getWarehouseDesc());
//
//            preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
//            preOutboundLine.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
//            preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
//            preOutboundLine.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
//            preOutboundLine.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());
//
//            String itemText = outboundIntegrationLine.getItemText() != null ? outboundIntegrationLine.getItemText() :
//                    getItemDescription(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationLine.getItemCode(), outboundIntegrationLine.getManufacturerName());
//
//            preOutboundLine.setDescription(itemText);
//
//            // ORD_QTY
//            preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());
//
//            // ORD_UOM
//            preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());
//
//            // REQ_DEL_DATE
//            preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
//
//            // REF_FIELD_1
//            preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());
//
//            preOutboundLine.setDeletionIndicator(0L);
//            preOutboundLine.setCreatedBy(loginUserId);
//            preOutboundLine.setCreatedOn(new Date());
//            log.info("Quantity Logic started ----------> ");
//            setAlternateUomQuantities(preOutboundLine);
//            log.info("Quantity Logic started ----------> ");
////            log.info("preOutboundLine : " + preOutboundLine);
//            return preOutboundLine;
//        } catch (Exception e) {
//            log.error("Exception While PreoutboundLine create: " + e);
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param preOutboundLineV2
//     */
//    private void setAlternateUomQuantities(PreOutboundLineV2 preOutboundLineV2) {
//        try {
//            Double qtyInPiece = null;
//            Double qtyInCase = null;
//            Double qtyInCreate = null;
//
//            String orderUom = preOutboundLineV2.getOrderUom();
//            String companyCodeId = preOutboundLineV2.getCompanyCodeId();
//            String plantId = preOutboundLineV2.getPlantId();
//            String warehouseId = preOutboundLineV2.getWarehouseId();
//            String itemCode = preOutboundLineV2.getItemCode();
//
//            if ("piece".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is PIECE");
//
//                qtyInPiece = preOutboundLineV2.getOrderQty();
//                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//
//                log.info("Piece Qty --- {}", preOutboundLineV2.getOrderQty());
//                log.info("Case Qty ALT_UOM: {}", caseQty);
//                log.info("Create Qty ALT_UOM: {}", createQty);
//
//                if (preOutboundLineV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
//                    qtyInCase = preOutboundLineV2.getOrderQty() / caseQty.getUomQty();
//                }
//
//                if (preOutboundLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
//                    qtyInCreate = preOutboundLineV2.getOrderQty() / createQty.getUomQty();
//                }
//
//            } else if ("case".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is CASE");
//
//                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//
//                qtyInCase = preOutboundLineV2.getOrderQty();
//
//                log.info("Case Qty --- {}", preOutboundLineV2.getOrderQty());
//                log.info("Piece Qty ALT_UOM: {}", pieceQty);
//                log.info("Create Qty ALT_UOM: {}", createQty);
//
//                if (preOutboundLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
//                    qtyInPiece = preOutboundLineV2.getOrderQty() * pieceQty.getUomQty();
//                }
//
//                if (preOutboundLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
//                    qtyInCreate = qtyInPiece / createQty.getUomQty();
//                }
//            } else if("crate".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is Crate");
//                qtyInCreate = preOutboundLineV2.getOrderQty();
//
//                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//                IKeyValuePair caseQy = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//
//                log.info("Crate Qty --- {}", preOutboundLineV2.getOrderQty());
//                log.info("Piece Qty ALT_UOM: {}", pieceQty);
//                log.info("Create Qty ALT_UOM: {}", caseQy);
//
//                if (preOutboundLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
//                    qtyInPiece = preOutboundLineV2.getOrderQty() * pieceQty.getUomQty();
//                }
//
//                if (preOutboundLineV2.getOrderQty() != null && caseQy != null && caseQy.getUomQty() != null) {
//                    qtyInCase = qtyInPiece / caseQy.getUomQty();
//                }
//            }
//
//            preOutboundLineV2.setQtyInPiece(qtyInPiece);
//            preOutboundLineV2.setQtyInCase(qtyInCase);
//            preOutboundLineV2.setQtyInCrate(qtyInCreate);
//        } catch (Exception e) {
//            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
//        }
//    }
//
////    @Transactional
////    public void createOrderManagementLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
////                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader,
////                                          List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {
////        try {
////            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
////                createOrderManagementLineV5(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundLine, loginUserId);
//////                log.info("orderManagementLine created---1---> : " + orderManagementLine);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            throw e;
////        }
////    }
//
//    /**
//     * @param createdPreOutboundLine
//     * @param outboundIntegrationHeader
//     * @return
//     */
//    private List<OutboundLineV2> createOutboundLineV5(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
//        try {
//            List<OutboundLineV2> outboundLines = new ArrayList<>();
//            for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
//                List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getOrderManagementLineV5(
//                        preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(),
//                        preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId(),
//                        preOutboundLine.getPreOutboundNo(), preOutboundLine.getLineNumber(),
//                        preOutboundLine.getItemCode());
//                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
//                    OutboundLineV2 outboundLine = new OutboundLineV2();
//                    BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
//                    outboundLine.setDeliveryQty(0D);
//                    outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
//                    outboundLine.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
//                    outboundLine.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
//                    outboundLine.setQtyInCase(dbOrderManagementLine.getQtyInCase());
//                    outboundLine.setDescription(dbOrderManagementLine.getDescription());
//                    statusDescription = getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
//                    outboundLine.setStatusDescription(statusDescription);
//                    outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
//
//                    if (outboundLine.getOutboundOrderTypeId() == 3L) {
//                        outboundLine.setCustomerType("INVOICE");
//                    }
//                    if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
//                        outboundLine.setCustomerType("TRANSVERSE");
//                    }
//                    outboundLines.add(outboundLine);
//                }
//            }
////            outboundLines = outboundLineV2Repository.saveAll(outboundLines);
//            log.info("outboundLines created -----2------>: " + outboundLines);
//            return outboundLines;
//        } catch (Exception e) {
//            log.error("Exception While OutboundLine create: " + e);
//            throw e;
//        }
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param outboundIntegrationHeader
//     * @param preOutboundLine
//     * @return
//     * @throws Exception
//     */
//    private void createOrderManagementLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
//        try {
//            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
//            BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
//            log.info("orderManagementLine : " + orderManagementLine);
//
//            Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
//            Long BIN_CLASS_ID;
//
//            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L|| OB_ORD_TYP_ID == 11L) {
//                BIN_CLASS_ID = 1L;
//                orderManagementLineService.updateAllocationV5(companyCodeId, plantId, languageId, warehouseId,
//                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId);
//            }
//            if (OB_ORD_TYP_ID == 2L) {
//                BIN_CLASS_ID = 7L;
//                orderManagementLineService.updateAllocationV5(companyCodeId, plantId, languageId, warehouseId,
//                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId);
//            }
//        } catch (Exception e) {
//            log.error("Exception While OrderManagementLine create: " + e);
//            throw e;
//        }
//    }
//
////    /**
////     * @param companyCodeId
////     * @param plantId
////     * @param languageId
////     * @param binClassId
////     * @param orderManagementLine
////     * @param warehouseId
////     * @param itemCode
////     * @param ORD_QTY
////     * @return
////     */
////    private void createOrderManagementV5(String companyCodeId, String plantId, String languageId,
////                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
////                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
////        String manufacturerName = orderManagementLine.getManufacturerName();
//////        List<IInventoryImpl> stockType1InventoryList = inventoryService.
//////                getInventoryForOrderManagementV3(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, manufacturerName);
//////        log.info("Walkaroo---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//////        if (stockType1InventoryList.isEmpty()) {
//////            return createEMPTYOrderManagementLineV2(orderManagementLine);
//////        }
////
////        orderManagementLineService.updateAllocationV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, ORD_QTY, orderManagementLine, loginUserId);
////    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @return
//     */
//    private String getItemDescription (String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName) {
//        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                languageId, companyCodeId, plantId, warehouseId, itemCode.trim(), manufacturerName, 0L);
//        return imBasicData1 != null ? imBasicData1.getDescription() : null;
//    }
//
//    /**
//     * @param createdPreOutboundHeader
//     * @param loginUserId
//     * @return
//     */
//    private OrderManagementHeaderV2 createOrderManagementHeaderV5(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, String statusDesc, String loginUserId) throws Exception {
//        try {
//            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
//            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//            newOrderManagementHeader.setStatusId(statusId);
//            newOrderManagementHeader.setStatusDescription(statusDesc);
//            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
//            newOrderManagementHeader.setPickupCreatedOn(new Date());
//            // Order_Text_Update
//            String text = "OrderManagement Created";
//            outboundOrderV2Repository.updateOrderManagementText(newOrderManagementHeader.getOutboundOrderTypeId(), newOrderManagementHeader.getRefDocNumber(), text);
//            log.info("OrderManagement Header Status Updated Successfully");
//            return newOrderManagementHeader;
//        } catch (Exception e) {
//            log.error("Exception while creating OrderManagementHeader : " + e);
//            throw e;
//        }
//    }
//
//    /**
//     * @param createdPreOutboundHeader
//     * @param outboundIntegrationHeader
//     * @param statusId
//     * @param statusDesc
//     * @return
//     * @throws Exception
//     */
//    private OutboundHeaderV2 createOutboundHeaderV5(PreOutboundHeaderV2 createdPreOutboundHeader, OutboundIntegrationHeaderV2 outboundIntegrationHeader,
//                                                    Long statusId, String statusDesc) throws Exception {
//        try {
//            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
//            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//            outboundHeader.setRefDocDate(new Date());
//            outboundHeader.setStatusId(statusId);
//            outboundHeader.setStatusDescription(statusDesc);
//            outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
//
//            if (outboundHeader.getOutboundOrderTypeId() == 3L) {
//                outboundHeader.setCustomerType("INVOICE");
//            }
//            if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
//                outboundHeader.setCustomerType("TRANSVERSE");
//            }
//            return outboundHeader;
//        } catch (Exception e) {
//            log.error("Exception While OutboundHeader create: " + e);
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preOutboundNo
//     * @param refDocNumber
//     * @param orderTypeId
//     * @param outboundOrderProcess
//     * @return
//     * @throws Exception
//     */
//    public OutboundHeaderV2 postOutboundOrder(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                              String preOutboundNo, String refDocNumber, Long orderTypeId,
//                                              OutboundOrderProcess outboundOrderProcess) throws Exception {
//        try {
//            log.info("Outbound Order Process save Initiated...! " + refDocNumber);
//            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
//            boolean isDuplicateOrder = isPreOutboundHeaderExistV5(refDocNumber, orderTypeId);
//            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
//            if(!isDuplicateOrder) {
//                if (outboundOrderProcess.getPreOutboundHeader() != null) {
//                    preOutboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getPreOutboundHeader());
//                }
//                if(outboundOrderProcess.getOutboundHeader() != null) {
//                    outboundHeader = outboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOutboundHeader());
//                }
//                if(outboundOrderProcess.getOrderManagementHeader() != null) {
//                    orderManagementHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOrderManagementHeader());
//                }
//                if (outboundOrderProcess.getPreOutboundLines() != null && !outboundOrderProcess.getPreOutboundLines().isEmpty()) {
//                    preOutboundLineV2Repository.saveAll(outboundOrderProcess.getPreOutboundLines());
//                }
//                if(outboundOrderProcess.getOutboundLines() != null && !outboundOrderProcess.getOutboundLines().isEmpty()) {
//                    outboundLineV2Repository.saveAll(outboundOrderProcess.getOutboundLines());
//                }
////                if(outboundOrderProcess.getOrderManagementLines() != null && !outboundOrderProcess.getOrderManagementLines().isEmpty()) {
////                    orderManagementLineV2Repository.saveAll(outboundOrderProcess.getOrderManagementLines());
////                }
//            }
//            log.info("Outbound Order Save Process Completed ------> " + refDocNumber);
//
//            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
//            statusDescription = getStatusDescription(47L, languageId);
//            orderManagementLineV2Repository.updateNostockStatusUpdateProcV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
//            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");
//
//            return outboundHeader;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            // Updating the Processed Status
//            log.info("Rollback Initiated...!" + refDocNumber);
////            rollback(outboundOrderProcess.getOutboundIntegrationHeader());
////            orderService.updateProcessedOrderV2(refDocNumber, orderTypeId);
//
//            throw e;
//        }
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preOutboundNo
//     * @param outboundHeader
//     * @param loginUserId
//     * @throws Exception
//     */
//    private void validatePickupHeaderCreation(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                              String refDocNumber, String preOutboundNo, OutboundHeaderV2 outboundHeader, String loginUserId) throws Exception {
//
//        String salesOrderNumber = outboundHeader.getSalesOrderNumber();
//        log.info("SalesOrderNumber : " + salesOrderNumber);
//        Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//        log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
//        try {
//            if (pickupHeaderStatusCheck == 1 && outboundHeader.getOutboundOrderTypeId() == 3L) {
//                List<String> shipToPartyList = preOutboundHeaderV2Repository.getShipToParty(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("shipToParty List : " + shipToPartyList.size());
//                if (shipToPartyList != null && !shipToPartyList.isEmpty()) {
//                    for (String shipToParty : shipToPartyList) {
//                        log.info("ShipToParty : " + shipToParty);
//                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesShipToPartyV5(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty);
//                        createPickupHeaderV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, orderManagementLineList, loginUserId);
//                    }
//                }
//                assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//            }
//        } catch (Exception e) {
//            log.error("Exception while validating PickupHeader creation");
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param salesOrderNumber
//     * @throws Exception
//     */
//    public void assignPicker (String companyCode, String plantId, String languageId, String warehouseId, String salesOrderNumber) throws Exception {
//        try {
//
//            List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListV5(companyCode, plantId, languageId, warehouseId, 6L);
//            log.info("hhtUserList: " + hhtUserIdList.size());
//            log.info("assign Picker for: " + salesOrderNumber);
//            List<String> hhtUserList = new ArrayList<>();
//            List<String> absentHhtUserList = new ArrayList<>();
//            if (hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
//                for (HHTUser dbHhtUser : hhtUserIdList) {
//                    if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
//                        List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendanceV5(
//                                companyCode, languageId, plantId, warehouseId, dbHhtUser.getUserId(), dbHhtUser.getStartDate(), dbHhtUser.getEndDate());
//                        log.info("HHt User Absent: " + userPresent);
//
//                        if (userPresent != null && !userPresent.isEmpty()) {
//                            absentHhtUserList.add(dbHhtUser.getUserId());
//                        } else {
//                            hhtUserList.add(dbHhtUser.getUserId());
//                        }
//                    } else {
//                        hhtUserList.add(dbHhtUser.getUserId());
//                    }
//                }
//            }
//            log.info("Present HHtUser List: " + hhtUserList);
//            log.info("Absent HHtUser List: " + absentHhtUserList);
//
//            List<PickupHeaderV2> pickupHeaderV2List = getPickupHeaderV5(companyCode, plantId, languageId, warehouseId, salesOrderNumber);
//            log.info("pickupHeaderV2List : " + pickupHeaderV2List.size());
//            List<String> pickupNumbers = new ArrayList<>();
//            if(pickupHeaderV2List != null && !pickupHeaderV2List.isEmpty()) {
//                pickupNumbers = pickupHeaderV2List.stream().map(PickupHeaderV2::getPickupNumber).distinct().sorted().collect(Collectors.toList());
//            }
//
//            int i = 0;
//            if (pickupNumbers != null && !pickupNumbers.isEmpty()) {
//                for (String pickupNumber : pickupNumbers) {
//                    if (hhtUserList != null && !hhtUserList.isEmpty()) {
//                        log.info("hhtUserList count: " + hhtUserList.size());
//                        pickupHeaderV2Repository.updateAssignPicker(pickupNumber, hhtUserList.get(i));
//                        log.info("AssignPicker : " + hhtUserList.get(i));
//                        i = i + 1;
//                        if (i >= hhtUserList.size()) {
//                            i = 0;
//                        }
//                        log.info("ob-i: " + i);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("Exception while assign binner : " + e.toString());
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param salesOrderNumber
//     * @return
//     */
//    public List<PickupHeaderV2> getPickupHeaderV5(String companyCodeId, String plantId, String languageId,
//                                                  String warehouseId, String salesOrderNumber) {
//        List<PickupHeaderV2> pickupHeaderV2List =
//                pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndDeletionIndicatorOrderByPickupNumber(
//                        companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, 0L);
//        return pickupHeaderV2List;
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preOutboundNo
//     * @param orderManagementLineList
//     * @param loginUserId
//     */
//    private List<DocumentNumber> createPickupHeaderV5(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
//                                                      String preOutboundNo, List<OrderManagementLineV2> orderManagementLineList, String loginUserId) {
//        try {
//            double sumOfAllocatedQty = orderManagementLineList.stream().filter(n -> n.getAllocatedQty() != null).mapToDouble(OrderManagementLineV2::getAllocatedQty).sum();
//            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
//            log.info("caseTolerance: " + caseTolerance);
//
//            List<DocumentNumber> documentNumberList = new ArrayList<>();
//            List<OrderManagementLineV2> sortedOrderManagementLineList = orderManagementLineList.stream().sorted(Comparator.comparing(OrderManagementLineV2::getProposedStorageBin).reversed()).collect(Collectors.toList());
//            String PU_NO = null;
//            if (caseTolerance != null) {
//
//                double noOfCases = getQuantity(caseTolerance.getNoOfCase());
//                double plusTolerance = getQuantity(caseTolerance.getPlusTolerance());
//                double totalCases = noOfCases + plusTolerance;
//                int PU_NO_COUNT = (int) Math.ceil(sumOfAllocatedQty / totalCases);
//                log.info(noOfCases + "|" + plusTolerance + "|" + totalCases + "|" + sumOfAllocatedQty+ "|" + PU_NO_COUNT);
//                int i = 1;
//                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
//                for (OrderManagementLineV2 createdOrderManagementLine : sortedOrderManagementLineList) {
//                    if (createdOrderManagementLine.getOutboundOrderTypeId() == 3) {
//                        if (i <= totalCases) {
//                            createPickUpHeaderV5(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, createdOrderManagementLine, loginUserId);
//                            i++;
//                            if (i > totalCases) {
//                                i = 1;
//                                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
//                            }
//                        }
////                        DocumentNumber documentNumber = new DocumentNumber();
////                        documentNumber.setRefDocNumber(createdOrderManagementLine.getRefDocNumber());
////                        documentNumber.setPreOutboundNo(createdOrderManagementLine.getPreOutboundNo());
////                        documentNumberList.add(documentNumber);
//                    }
//                }
//            } else {
//                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
//                for (OrderManagementLineV2 orderManagementLine : orderManagementLineList) {
//                    if(orderManagementLine.getOutboundOrderTypeId() == 3) {
//                        createPickUpHeaderV5(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//
////                        DocumentNumber documentNumber = new DocumentNumber();
////                        documentNumber.setRefDocNumber(orderManagementLine.getRefDocNumber());
////                        documentNumber.setPreOutboundNo(orderManagementLine.getPreOutboundNo());
////                        documentNumberList.add(documentNumber);
//                    }
//                }
//            }
//            return documentNumberList;
//
//        } catch (Exception e) {
//            throw new BadRequestException(e.getLocalizedMessage());
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
//     * @param orderManagementLine
//     * @param loginUserId
//     * @throws Exception
//     */
//    private void createPickUpHeaderV5(String companyCodeId, String plantId, String languageId, String warehouseId, String pickupNumber,
//                                      String preOutboundNo, String refDocNumber, OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {
//
//        preOutboundNo = orderManagementLine.getPreOutboundNo();
//        refDocNumber = orderManagementLine.getRefDocNumber();
//        PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
//        BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
//
////            newPickupHeader.setAssignedPickerId(assignPickerId);
//        newPickupHeader.setPickupNumber(pickupNumber);
//        newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
//        // PICK_UOM
//        newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
//
//        // STATUS_ID
//        newPickupHeader.setStatusId(48L);
//        statusDescription = getStatusDescriptionV5(48L, languageId);
//        newPickupHeader.setStatusDescription(statusDescription);
//
//        newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
//        if (newPickupHeader.getCompanyDescription() == null) {
//            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//            newPickupHeader.setCompanyDescription(description.getCompanyDesc());
//            newPickupHeader.setPlantDescription(description.getPlantDesc());
//            newPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
//        }
//        newPickupHeader.setDeletionIndicator(0L);
//        newPickupHeader.setPickupCreatedBy(loginUserId);
//        newPickupHeader.setPickUpdatedBy(loginUserId);
//        newPickupHeader.setPickupCreatedOn(new Date());
//        newPickupHeader.setPickUpdatedOn(new Date());
//
//        // Order_Text_Update
//        String text = "PickupHeader Created";
//        outboundOrderV2Repository.updateOutboundHeaderText(newPickupHeader.getOutboundOrderTypeId(), newPickupHeader.getRefDocNumber(), text);
//        log.info("PickupHeader Status Updated Successfully");
//
//        PickupHeaderV2 createdPickupHeader = pickupHeaderV2Repository.save(newPickupHeader);
//        log.info("pickupHeader created: " + createdPickupHeader);
//
//        orderManagementLineV2Repository.updateOrderManagementLineV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, pickupNumber,
////                    assignPickerId,
//                orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(), 48L, statusDescription, new Date());
//
//        outboundLineV2Repository.updateOutboundLineStatusV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                48L, statusDescription, orderManagementLine.getLineNumber(), orderManagementLine.getItemCode());
////                    , assignPickerId);
//
//        // OutboundHeader Update
//        outboundHeaderV2Repository.updateOutboundHeaderStatusV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
//        log.info("outboundHeader updated");
//
//        // ORDERMANAGEMENTHEADER Update
//        orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
//        log.info("orderManagementHeader updated");
//    }

}
