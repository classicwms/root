package com.tekclover.wms.api.transaction.service;

import java.util.*;
import java.util.stream.Collectors;

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

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.DocumentNumber;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundOrderProcess;
import com.tekclover.wms.api.transaction.model.outbound.v2.PickListCancellation;
import com.tekclover.wms.api.transaction.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.transaction.model.outbound.v2.PickListLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.transaction.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.transaction.repository.PickListHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineV2Repository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundOrderProcessingFTService extends BaseService {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;

    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    OutboundLineService outboundLineService;

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    PreOutboundLineService preOutboundLineService;

    @Autowired
    QualityHeaderService qualityHeaderService;

    @Autowired
    QualityLineService qualityLineService;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    PickupHeaderService pickupHeaderService;

    @Autowired
    PickListHeaderService pickListHeaderService;

    //========================================================================V2====================================================================

    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
//    public synchronized OutboundHeaderV2 processOutboundReceivedV3(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
//        try {
//            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
//            String plantId = outboundIntegrationHeader.getBranchCode();
//            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
//            String warehouseId = outboundIntegrationHeader.getWarehouseID();
//            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
//            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
//            log.info("Outbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
//            WK = outboundIntegrationHeader.getLoginUserId() != null ? outboundIntegrationHeader.getLoginUserId() : WK;
//
//            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, outboundOrderTypeId);
//            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
//            if (isDuplicateOrder) {
//                log.info("isDuplicateOrder------> : calling doProcessPickListUpdate -----");
//                doProcessPickListUpdate(outboundIntegrationHeader);
//            }
//
//            OutboundOrderProcess outboundOrderProcess = new OutboundOrderProcess();
//            String idMasterAuthToken = getIDMasterAuthToken();
//            /*
//             * Grouping PreOutboundHeader ID based on the Customer ID
//             */
//            String preOutboundNo = getPreOutboundNo(companyCodeId, plantId, languageId, warehouseId,
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
//            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, statusId, statusDescription, description, WK);
//            log.info("preOutboundHeader Created : {}", createdPreOutboundHeader);
//
//            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
//            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
//                log.info("outboundIntegrationLine : " + outboundIntegrationLine);
//                try {
//                    // PreOutboundLine
//                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
//                            outboundIntegrationHeader, outboundIntegrationLine, statusId, statusDescription, description, WK);
//                    log.info("preOutboundLine created---1---> : " + preOutboundLine);
//                    createdPreOutboundLineList.add(preOutboundLine);
//                } catch (Exception e) {
//                    log.error("Error on processing PreOutboundLine : " + e.toString());
//                    e.printStackTrace();
//                }
//                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
//            }
////            preOutboundLineV2Repository.saveAll(createdPreOutboundLineList);
//
//            createOrderManagementLine(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, createdPreOutboundLineList, WK, false);
//
//            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
//            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
//            log.info("createOutboundLine created : " + createOutboundLineList);
//
//            statusId = 41L;
//            statusDescription = getStatusDescription(statusId, languageId);
//            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader, statusId, statusDescription, WK);
//            log.info("OrderMangementHeader Created : {}", createdOrderManagementHeader);
//
//            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, outboundIntegrationHeader, statusId, statusDescription);
//            if (outboundHeader.getPartnerCode() == null) {
//                outboundHeader.setPartnerCode("STO");
//            }
//
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
//            validatePickupHeaderCreation(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, WK);
//
//            return outboundHeader;
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Updating the Processed Status
//            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
//            rollback(outboundIntegrationHeader);
//            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
//            throw e;
//        }
//    }

    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public synchronized OutboundHeaderV2 processOutboundReceivedV3(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
            log.info("Outbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
            MW_AMS = outboundIntegrationHeader.getLoginUserId() != null ? outboundIntegrationHeader.getLoginUserId() : MW_AMS;

            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, outboundOrderTypeId);
            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
            if (isDuplicateOrder) {
                return new OutboundHeaderV2();
            }

            OutboundOrderProcess outboundOrderProcess = new OutboundOrderProcess();
            String idMasterAuthToken = getIDMasterAuthToken();
            /*
             * Grouping PreOutboundHeader ID based on the Customer ID
             */
            String preOutboundNo = getPreOutboundNo(companyCodeId, plantId, languageId, warehouseId,
                    outboundIntegrationHeader.getCustomerId(), outboundIntegrationHeader.getSalesOrderNumber(), outboundOrderTypeId, idMasterAuthToken);
            log.info("PreOutboundNo : {}", preOutboundNo);

            String refField1ForOrderType = null;

            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            Long statusId = 39L;
            statusDescription = getStatusDescription(statusId, languageId);

            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, statusId, statusDescription, description, MW_AMS);
            log.info("preOutboundHeader Created : {}", createdPreOutboundHeader);

            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                log.info("outboundIntegrationLine : " + outboundIntegrationLine);
                try {
                    // PreOutboundLine
                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                            outboundIntegrationHeader, outboundIntegrationLine, statusId, statusDescription, description, MW_AMS);
                    log.info("preOutboundLine created---1---> : " + preOutboundLine);
                    createdPreOutboundLineList.add(preOutboundLine);
                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
            }
//            preOutboundLineV2Repository.saveAll(createdPreOutboundLineList);

            createOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, createdPreOutboundLineList, MW_AMS);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
            log.info("createOutboundLine created : " + createOutboundLineList);

            statusId = 41L;
            statusDescription = getStatusDescription(statusId, languageId);
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV3(createdPreOutboundHeader, statusId, statusDescription, MW_AMS);
            log.info("OrderMangementHeader Created : {}", createdOrderManagementHeader);

            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, outboundIntegrationHeader, statusId, statusDescription);
            log.info("outboundHeader Created : {}", outboundHeader);

            outboundOrderProcess.setOutboundHeader(outboundHeader);
            outboundOrderProcess.setOutboundLines(createOutboundLineList);
            outboundOrderProcess.setOrderManagementHeader(createdOrderManagementHeader);
            outboundOrderProcess.setPreOutboundHeader(createdPreOutboundHeader);
            outboundOrderProcess.setPreOutboundLines(createdPreOutboundLineList);
            outboundOrderProcess.setOutboundIntegrationHeader(outboundIntegrationHeader);
            postOutboundOrderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId, outboundOrderProcess);

            validatePickupHeaderCreationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, MW_AMS);

            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());

            throw e;
        }
    }


    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public synchronized OutboundHeaderV2 processOutboundReceivedFromSAPV3(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
            log.info("Outbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
            WK = outboundIntegrationHeader.getLoginUserId() != null ? outboundIntegrationHeader.getLoginUserId() : WK;

            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, outboundOrderTypeId);
            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder + " <----> " + outboundIntegrationHeader.getOutboundOrderTypeID());

            PreOutboundHeaderV2 existingPreOutboundHeaderV2 =
                    preOutboundHeaderService.getPreOutboundHeaderForFullfillment(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            if (isDuplicateOrder && outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
                log.info("isDuplicateOrder------> : calling doProcessPickListUpdate -----");
                doProcessPickListUpdate(outboundIntegrationHeader);
                boolean recordExists = pickupHeaderV2Repository.existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(companyCodeId,
                        plantId, warehouseId, outboundIntegrationHeader.getRefDocumentNo(), 0L);
                log.info("Exists PickupHeader --------> " + recordExists);
                if (recordExists) {
                    return outboundHeaderV2Repository.findByRefDocNumberAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), 0L);
                }
            }

            OutboundOrderProcess outboundOrderProcess = new OutboundOrderProcess();
            String idMasterAuthToken = getIDMasterAuthToken();

            /*
             * Grouping PreOutboundHeader ID based on the Customer ID
             */
            String preOutboundNo = getPreOutboundNo(companyCodeId, plantId, languageId, warehouseId,
                    outboundIntegrationHeader.getCustomerId(), outboundIntegrationHeader.getSalesOrderNumber(), outboundOrderTypeId, idMasterAuthToken);
            log.info("PreOutboundNo : {}", preOutboundNo);

            String refField1ForOrderType = null;
            Long statusId = 39L;
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            statusDescription = getStatusDescription(statusId, languageId);

            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, statusId, statusDescription, description, WK);
            /*
             * Filling ShipToCode and ShiptToParty
             */
            if (existingPreOutboundHeaderV2 != null) {
                createdPreOutboundHeader.setShipToCode(existingPreOutboundHeaderV2.getShipToCode());
                createdPreOutboundHeader.setShipToParty(existingPreOutboundHeaderV2.getShipToParty());
            }
            log.info("preOutboundHeader Created : {}", createdPreOutboundHeader);

            /*
             * Incase OF Picklistcancellation
             */
            if (outboundIntegrationHeader.getPartnerCode() == null) {
                OutboundOrderV2 objOutboundOrderV2 =
                        orderService.getOBOrderByIdV3(outboundIntegrationHeader.getRefDocumentNo(),
                                outboundIntegrationHeader.getOutboundOrderTypeID());
                log.info("----------objOutboundOrderV3-----------> : " + objOutboundOrderV2);

                if (objOutboundOrderV2 != null) {
                    createdPreOutboundHeader.setPartnerCode(objOutboundOrderV2.getPartnerCode());
                    createdPreOutboundHeader.setCustomerId(objOutboundOrderV2.getCustomerId());
                    createdPreOutboundHeader.setCustomerName(objOutboundOrderV2.getCustomerName());

                    outboundIntegrationHeader.setPartnerCode(objOutboundOrderV2.getPartnerCode());
                    outboundIntegrationHeader.setCustomerId(objOutboundOrderV2.getCustomerId());
                    outboundIntegrationHeader.setCustomerName(objOutboundOrderV2.getCustomerName());
                }
            }

            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                log.info("outboundIntegrationLine : " + outboundIntegrationLine);
                try {
                    // PreOutboundLine
                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                            outboundIntegrationHeader, outboundIntegrationLine, statusId, statusDescription, description, WK);
                    log.info("preOutboundLine created---1---> : " + preOutboundLine);

                    createdPreOutboundHeader.setShipToCode(outboundIntegrationLine.getShipToCode());
                    createdPreOutboundHeader.setShipToParty(outboundIntegrationLine.getShipToParty());
                    createdPreOutboundLineList.add(preOutboundLine);
                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
                refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
            }

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------------------------------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV3(createdPreOutboundLineList,
                    outboundIntegrationHeader);
            log.info("createOutboundLine --initiated--- : " + createOutboundLineList);

            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader,
                    outboundIntegrationHeader, statusId, statusDescription);
            if (outboundHeader.getPartnerCode() == null) {
                outboundHeader.setPartnerCode("STO");
            }
            log.info("outboundHeader Created : {}", outboundHeader);
            log.info("---preOutboundHeader yet to be created : {}", createdPreOutboundHeader);

            outboundOrderProcess.setPreOutboundHeader(createdPreOutboundHeader);
            outboundOrderProcess.setPreOutboundLines(createdPreOutboundLineList);
            outboundOrderProcess.setOutboundHeader(outboundHeader);
            outboundOrderProcess.setOutboundLines(createOutboundLineList);
            outboundOrderProcess.setOutboundIntegrationHeader(outboundIntegrationHeader);
            postOutboundOrderV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId, outboundOrderProcess);
            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
            throw e;
        }
    }

    /**
     * createPickListCancellation
     *
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
                                            PickListCancellation pickListCancellation, String loginUserID) throws Exception {
        List<PickupLineV2> pickupLineV2 = pickListCancellation.getOldPickupLineList();
        List<QualityLineV2> qualityLineV2 = pickListCancellation.getOldQualityLineList();
        OutboundHeaderV2 outboundHeaderV2 = pickListCancellation.getOldOutboundHeader();
        List<OutboundLineV2> outboundLineV2 = pickListCancellation.getOldOutboundLineList();
        List<OrderManagementLineV2> orderManagementLine = pickListCancellation.getOldOrderManagementLineList();

        // PickUpLine Creation
        List<PickupLineV2> createNewPickUpLineList = null;
        if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
            List<PickupHeaderV2> newPickupHeaderList = pickupHeaderService.getPickupHeaderForPickListCancellationV2(
                    companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("newPickupHeaderList: " + newPickupHeaderList);

            List<AddPickupLine> newPickupLineList = new ArrayList<>();
            if (newPickupHeaderList != null && !newPickupHeaderList.isEmpty()) {
                for (PickupHeaderV2 pickupHeaderV2 : newPickupHeaderList) {
                    List<PickupLineV2> pickupLinePresent = pickupLineV2.stream()
                            .filter(n -> n.getItemCode().equalsIgnoreCase(pickupHeaderV2.getItemCode())
                                    && n.getManufacturerName().equalsIgnoreCase(pickupHeaderV2.getManufacturerName()))
                            .collect(Collectors.toList());
                    log.info("Pickup Line present for that itemCode & MFR_Name: " + pickupLinePresent);

                    if (pickupLinePresent != null && !pickupLinePresent.isEmpty()) {
                        AddPickupLine newPickupLine = new AddPickupLine();
                        BeanUtils.copyProperties(pickupHeaderV2, newPickupLine, CommonUtils.getNullPropertyNames(pickupHeaderV2));
                        Double oldPickConfirmQty = pickupLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = pickupHeaderV2.getPickToQty();
                        if (oldPickConfirmQty < newPickConfirmQty) {
                            // Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
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
                //createNewPickUpLineList = pickupLineService.createPickupLineV2(newPickupLineList, loginUserID);
                createNewPickUpLineList = pickupLineService
                        .createPickupLineNonCBMPickListCancellationV2(newPickupLineList, loginUserID);
                log.info("CreatedPickUpLine List : " + createNewPickUpLineList);
            }
        }

        //Quality Line Creation
        if (qualityLineV2 != null && !qualityLineV2.isEmpty()) {
            List<QualityHeaderV2> newQualityHeaderList = qualityHeaderService.getQualityHeaderForPickListCancellationV2(
                    companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("NewQualityHeaderList: " + newQualityHeaderList);

            List<AddQualityLineV2> newQualityLineList = new ArrayList<>();
            if (newQualityHeaderList != null && !newQualityHeaderList.isEmpty()) {
                for (QualityHeaderV2 qualityHeader : newQualityHeaderList) {
                    List<QualityLineV2> qualityLinePresent = qualityLineV2.stream()
                            .filter(n -> n.getItemCode().equalsIgnoreCase(qualityHeader.getReferenceField4())
                                    && n.getManufacturerName().equalsIgnoreCase(qualityHeader.getManufacturerName()))
                            .collect(Collectors.toList());
                    log.info("Quality Line Present for that itemCode and MFR_Name: " + qualityLinePresent);

                    if (qualityLinePresent != null && !qualityLinePresent.isEmpty()) {
                        AddQualityLineV2 newQualityLine = new AddQualityLineV2();
                        BeanUtils.copyProperties(qualityHeader, newQualityLine, CommonUtils.getNullPropertyNames(qualityHeader));

                        Double oldPickConfirmQty = qualityLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = Double.valueOf(qualityHeader.getQcToQty());
                        if (oldPickConfirmQty < newPickConfirmQty) {
                            // Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
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
                List<QualityLineV2> createNewQualityLineList = qualityLineService
                        .createQualityLineV2(newQualityLineList, loginUserID);
                log.info("Created Quality Line List : " + createNewQualityLineList);
            }
        }

        log.info("Pick List Cancellation Completed: " + pickListCancellation.getOldRefDocNumber());
        log.info("Stored procedure call to update cnf_by in pickup line, qc header and line : " + oldPickListNumber
                + ", " + outboundHeaderV2.getPreOutboundNo() + ", " + newPickListNumber + ", " + newPreOutboundNo + ", "
                + outboundHeaderV2.getSalesOrderNumber());

        pickListHeaderRepository.updatePickupLineQualityHeaderLineCnfByUpdateProc(companyCodeId, plantId, languageId,
                warehouseId, pickListCancellation.getOldRefDocNumber(), outboundHeaderV2.getPreOutboundNo(), pickListCancellation.getNewRefDocNumber(),
                newPreOutboundNo, outboundHeaderV2.getSalesOrderNumber());
        log.info("SP update done");

        insertNewPickListCancelRecord(outboundHeaderV2, outboundLineV2, pickupLineV2, createNewPickUpLineList,
                orderManagementLine, companyCodeId, plantId, languageId, warehouseId, oldPickListNumber,
                newPickListNumber);
    }

    /**
     * insertNewPickListCancelRecord
     *
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
        if (outboundLineV2List != null && !outboundLineV2List.isEmpty()) { // old OrderLines Count
            oldObLineCount = outboundLineV2List.stream().count();
        }

        if (oldPickupLineList != null && !oldPickupLineList.isEmpty()) { // old PickedLines Count
            oldPickLineCount = oldPickupLineList.stream().count();
        }

        List<OutboundLineV2> newOutboundLineList = outboundLineService.getOutboundLineV2(companyCodeId, plantId,
                languageId, warehouseId, newPickListNumber); // Fetch New OrderLines
        if (newOutboundLineList != null && !newOutboundLineList.isEmpty()) { // new OrderLines Count
            newObLineCount = newOutboundLineList.stream().count();
        }

        if (newPickupLineList != null && !newPickupLineList.isEmpty()) { // new PickedLines Count
            newPickLineCount = newPickupLineList.stream().count();
        }

        if (outboundHeaderV2 != null) {
            loginUserID = outboundHeaderV2.getCreatedBy();
            PickListHeader pickListHeader = new PickListHeader();
            BeanUtils.copyProperties(outboundHeaderV2, pickListHeader,
                    CommonUtils.getNullPropertyNames(outboundHeaderV2));
            OutboundHeaderV2 newOutboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId,
                    languageId, newPickListNumber, warehouseId); // Fetch New Order Details - Header
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
                            if (pickupLineV2.getItemCode().equalsIgnoreCase(newPickupLineV2.getItemCode())
                                    && pickupLineV2.getManufacturerName()
                                    .equalsIgnoreCase(newPickupLineV2.getManufacturerName())) {

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(pickupLineV2, dbPickListLine,
                                        CommonUtils.getNullPropertyNames(pickupLineV2));

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
                                createdItmMfrNameList
                                        .add(newPickupLineV2.getItemCode() + newPickupLineV2.getManufacturerName());
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
                            boolean newItmPresent = newItmMfrNameList.stream()
                                    .anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
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
                                BeanUtils.copyProperties(newPickupLineV2, dbPickListLine,
                                        CommonUtils.getNullPropertyNames(newPickupLineV2));
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
                        boolean oldItmPresent = oldItmMfrNameList.stream()
                                .anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
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
                        String itmMfrName = oldPickupLineV2.getItemCode() + oldPickupLineV2.getManufacturerName()
                                + oldPickupLineV2.getLineNumber();
                        boolean itmPresent = filterOldList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!itmPresent) {
                            PickListLine dbPickListLine = new PickListLine();
                            BeanUtils.copyProperties(oldPickupLineV2, dbPickListLine,
                                    CommonUtils.getNullPropertyNames(oldPickupLineV2));
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
            //if(oldPickupLineList == null || oldPickupLineList.isEmpty()) {

            List<OrderManagementLineV2> newOrderManagementLineList = orderManagementLineService
                    .getOrderManagementLineForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId,
                            newPickListNumber);
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
                                String newItmMfrName = newOutboundLineV2.getItemCode()
                                        + newOutboundLineV2.getManufacturerName();
                                log.info("ItmMfrName: " + newItmMfrName);

                                boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                                if (!newItmPresent) {
                                    if (outboundLineV2.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode())
                                            && outboundLineV2.getManufacturerName()
                                            .equalsIgnoreCase(newOutboundLineV2.getManufacturerName())) {

                                        // Filter ItemCode, MFR Name and LineNumber - OldPickList
                                        oldOrderManagementLineFilteredList = orderManagementLineList.stream().filter(
                                                        a -> a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode())
                                                                && a.getManufacturerName()
                                                                .equalsIgnoreCase(outboundLineV2.getManufacturerName())
                                                                && a.getLineNumber().equals(outboundLineV2.getLineNumber()))
                                                .collect(Collectors.toList());
                                        // Filter ItemCode, MFR Name and LineNumber - NewPickList
                                        newOrderManagementLineFilteredList = newOrderManagementLineList.stream().filter(
                                                        a -> a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode())
                                                                && a.getManufacturerName().equalsIgnoreCase(
                                                                newOutboundLineV2.getManufacturerName())
                                                                && a.getLineNumber().equals(newOutboundLineV2.getLineNumber()))
                                                .collect(Collectors.toList());

                                        PickListLine dbPickListLine = new PickListLine();
                                        BeanUtils.copyProperties(outboundLineV2, dbPickListLine,
                                                CommonUtils.getNullPropertyNames(outboundLineV2));

                                        dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                                        dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                                        dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                                        dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                                        if (oldOrderManagementLineFilteredList != null
                                                && !oldOrderManagementLineFilteredList.isEmpty()) {
                                            dbPickListLine.setOldPickedStorageBin(
                                                    oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                        }
                                        dbPickListLine.setOldOrderQty(outboundLineV2.getOrderQty());
                                        dbPickListLine.setOldStatusId(outboundLineV2.getStatusId());
                                        dbPickListLine.setOldStatusDescription(outboundLineV2.getStatusDescription());

                                        dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                        dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                        dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                        dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                        if (newOrderManagementLineFilteredList != null
                                                && !newOrderManagementLineFilteredList.isEmpty()) {
                                            dbPickListLine.setNewPickedStorageBin(
                                                    newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                        }
                                        dbPickListLine.setNewOrderQty(newOutboundLineV2.getOrderQty());
                                        dbPickListLine.setNewStatusId(newOutboundLineV2.getStatusId());
                                        dbPickListLine
                                                .setNewStatusDescription(newOutboundLineV2.getStatusDescription());

                                        dbPickListLine.setDeletionIndicator(0L);
                                        dbPickListLine.setCreatedBy(loginUserID);
                                        dbPickListLine.setUpdatedBy(loginUserID);
                                        dbPickListLine.setCreatedOn(new Date());
                                        dbPickListLine.setUpdatedOn(new Date());
                                        dbPickListLine
                                                .setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                        dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());

                                        createPickListLineList.add(dbPickListLine);
                                        createdItmMfrNameList2.add(newOutboundLineV2.getItemCode()
                                                + newOutboundLineV2.getManufacturerName());
                                    }
                                }
                            }
                        }
                    }

                    for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                        String newItmMfrName = newOutboundLineV2.getItemCode()
                                + newOutboundLineV2.getManufacturerName();
                        log.info("ItmMfrName: " + newItmMfrName);
                        boolean newItmPresent = newItmMfrNameList.stream()
                                .anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                        if (!newItmPresent) {
                            newItmMfrNameList2
                                    .add(newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName());
                        }
                    }

                    log.info("OldPickList2 : " + oldItmMfrNameList2);
                    log.info("NewPickList2 : " + newItmMfrNameList2);
                    log.info("CreatedPickList2 : " + createdItmMfrNameList2);
                    if (createdItmMfrNameList2 != null && !createdItmMfrNameList2.isEmpty()) {
                        for (String itmMfrName : createdItmMfrNameList2) {
                            boolean oldItmPresent2 = oldItmMfrNameList2.stream()
                                    .anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            boolean newItmPresent2 = newItmMfrNameList2.stream()
                                    .anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
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

                    log.info("Filtered OldPickList2 : " + filterOldList2);
                    log.info("Filtered NewPickList2 : " + filterNewList2);
                    if (filterNewList2 != null && !filterNewList2.isEmpty()) {
                        for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                            String itmMfrName = newOutboundLineV2.getItemCode()
                                    + newOutboundLineV2.getManufacturerName();
                            boolean itmPresent = filterNewList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!itmPresent) {

                                // Filter ItemCode, MFR Name and LineNumber - NewPickList
                                newOrderManagementLineFilteredList = newOrderManagementLineList.stream()
                                        .filter(a -> a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode())
                                                && a.getManufacturerName()
                                                .equalsIgnoreCase(newOutboundLineV2.getManufacturerName())
                                                && a.getLineNumber().equals(newOutboundLineV2.getLineNumber()))
                                        .collect(Collectors.toList());

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(newOutboundLineV2, dbPickListLine,
                                        CommonUtils.getNullPropertyNames(newOutboundLineV2));
                                dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                if (newOrderManagementLineFilteredList != null
                                        && !newOrderManagementLineFilteredList.isEmpty()) {
                                    dbPickListLine.setNewPickedStorageBin(
                                            newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
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
                            // Filter ItemCode, MFR Name and LineNumber - OldPickList
                            oldOrderManagementLineFilteredList = orderManagementLineList.stream()
                                    .filter(a -> a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode())
                                            && a.getManufacturerName()
                                            .equalsIgnoreCase(outboundLineV2.getManufacturerName())
                                            && a.getLineNumber().equals(outboundLineV2.getLineNumber()))
                                    .collect(Collectors.toList());

                            PickListLine dbPickListLine = new PickListLine();
                            BeanUtils.copyProperties(outboundLineV2, dbPickListLine,
                                    CommonUtils.getNullPropertyNames(outboundLineV2));
                            dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                            dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                            dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                            dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                            if (oldOrderManagementLineFilteredList != null
                                    && !oldOrderManagementLineFilteredList.isEmpty()) {
                                dbPickListLine.setOldPickedStorageBin(
                                        oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
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
            PickListHeader createdPickListHeader = pickListHeaderService.createPickListHeader(pickListHeader, loginUserID);
            log.info("Created PicklistHeader : " + createdPickListHeader);
        }
    }

    /**
     * @param outboundIntegrationHeaderList
     * @return
     * @throws Exception
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Retryable(value = {Exception.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public List<OutboundHeaderV2> sapOutboundOrderFullfillment(List<OutboundIntegrationHeaderV2> outboundIntegrationHeaderList) throws Exception {
        log.info("---------sapOutboundOrderFullfillment----" + outboundIntegrationHeaderList);

        String companyCodeId = outboundIntegrationHeaderList.get(0).getCompanyCode();
        String plantId = outboundIntegrationHeaderList.get(0).getBranchCode();
        String warehouseId = outboundIntegrationHeaderList.get(0).getWarehouseID();
        String languageId = outboundIntegrationHeaderList.get(0).getLanguageId();
        String refDocNumber = outboundIntegrationHeaderList.get(0).getRefDocumentNo();
        String preOutboundNo = null;
        
        List<OutboundHeaderV2> outboundHeaderV2List = new ArrayList<>();
        String salesOrderNo = getNextRangeNumber(9L, outboundIntegrationHeaderList.get(0).getCompanyCode(), outboundIntegrationHeaderList.get(0).getBranchCode(), outboundIntegrationHeaderList.get(0).getLanguageId(),
                outboundIntegrationHeaderList.get(0).getWarehouseID(), getIDMasterAuthToken());
        log.info("SalesOrderNo is -----------------> {} ", salesOrderNo);

        for(OutboundIntegrationHeaderV2 headerList : outboundIntegrationHeaderList) {
            try {
                log.info("---------order----" + headerList);
                headerList.setSalesOrderNumber(salesOrderNo);
                outboundHeaderV2List.add(fullfillOutboundReceivedV4(headerList));
                orderManagementLineV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, headerList.getRefDocumentNo(), salesOrderNo);
                preOutboundHeaderV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, headerList.getRefDocumentNo(), salesOrderNo);
                preOutboundLineV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, headerList.getRefDocumentNo(), salesOrderNo);
                outboundOrderV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, headerList.getRefDocumentNo(), salesOrderNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        outboundIntegrationHeaderList.forEach(order -> {
//            try {
//
//                log.info("---------order----" + order);
//                order.setSalesOrderNumber(salesOrderNo);
//                outboundHeaderV2List.add(fullfillOutboundReceivedV4(order));
//                orderManagementLineV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, order.getRefDocumentNo(), salesOrderNo);
//                preOutboundHeaderV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, order.getRefDocumentNo(), salesOrderNo);
//                preOutboundLineV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, order.getRefDocumentNo(), salesOrderNo);
//                outboundOrderV2Repository.updateSalesOrderNo(companyCodeId, plantId, warehouseId, order.getRefDocumentNo(), salesOrderNo);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
        log.info("PickupHeader Validation Started-------------------->");
        // validatePickupHeaderCreationV2(outboundIntegrationHeaderList.get(0), salesOrderNo, WK);
        validatePickupHeaderCreationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, 
        		outboundHeaderV2List.get(0).getPreOutboundNo(), outboundHeaderV2List.get(0), MW_AMS);
        return outboundHeaderV2List;
    }

    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    @Transactional
    public OutboundHeaderV2 fullfillOutboundReceivedV4(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String salesOrderNumber = null;
        try {
            salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();
            companyCodeId = outboundIntegrationHeader.getCompanyCode();
            plantId = outboundIntegrationHeader.getBranchCode();
            languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId()
                    : LANG_ID;
            warehouseId = outboundIntegrationHeader.getWarehouseID();
            refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
            Long statusId = 39L;
            statusDescription = getStatusDescription(statusId, languageId);

            log.info("Outbound process fullfill Initiated ------> : {}|{}|{}|{}|{}", companyCodeId, plantId, languageId,
                    warehouseId, refDocNumber);
            WK = "SAP-ORDER FULLFILLMENT";

            // ---------------PreOutboundHeader-----------------------------------------------------------------------------------
            log.info("preOutboundHeader inpouts : {},{},{},{},{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderService
                    .getPreOutboundHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            log.info("PreOutboundHeader Retrieved : {}", createdPreOutboundHeader);

            String preOutboundNo = createdPreOutboundHeader.getPreOutboundNo();
            log.info("--------------preOutboundNo--------->{}", preOutboundNo);

            outboundIntegrationHeader.setOutboundOrderTypeID(createdPreOutboundHeader.getOutboundOrderTypeId());
            outboundIntegrationHeader.setCustomerId(createdPreOutboundHeader.getCustomerId());

            OutboundOrderProcess outboundOrderProcess = new OutboundOrderProcess();

            // ----------------PreOutboundLine-------------------------------------------------------------------------------------
            List<PreOutboundLineV2> createdPreOutboundLineList = preOutboundLineService
                    .getPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo);
            log.info("PreOutboundLine Retrieved---1---> : " + createdPreOutboundLineList);
            if (createdPreOutboundLineList == null) {
                createdPreOutboundLineList =
                        preOutboundLineService.getPreOutboundLineForFullfillment(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo);
                log.info("PreOutboundLine Retrieved---2---> : " + createdPreOutboundLineList);
            }

            // ----------------CreateOrderManagementLine----------------------------------------------------------------------------
            boolean fromOrderFullfillment = true;
            createOrderManagementLineFullfillment(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader,
                    createdPreOutboundLineList, WK, fromOrderFullfillment);

            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader,
                    outboundIntegrationHeader, statusId, statusDescription);
            if (outboundHeader.getPartnerCode() == null) {
                outboundHeader.setPartnerCode("STO");
            }
            log.info("outboundHeader Created : {}", outboundHeader);

            statusId = 41L;
            statusDescription = getStatusDescription(statusId, languageId);
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(
                    createdPreOutboundHeader, statusId, statusDescription, WK);
            log.info("OrderMangementHeader Created : {}", createdOrderManagementHeader);

            outboundOrderProcess.setOrderManagementHeader(createdOrderManagementHeader);
            outboundOrderProcess.setPreOutboundHeader(createdPreOutboundHeader);
            outboundOrderProcess.setPreOutboundLines(createdPreOutboundLineList);
            outboundOrderProcess.setOutboundIntegrationHeader(outboundIntegrationHeader);
            postOutboundOrder(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                    outboundIntegrationHeader.getOutboundOrderTypeID(), outboundOrderProcess);

//			validatePickupHeaderCreationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//					outboundHeader, WK);
            preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, 48L, "IN PICKING");
            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
            throw e;
        }
    }


    /**
     * PickList Cancellation
     *
     * @param outboundIntegrationHeader
     * @return
     */
    private void doProcessPickListUpdate(OutboundIntegrationHeaderV2 outboundIntegrationHeader) {
        if (outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
            log.info("Executing PickList cancellation scenario pre - checkup process");
            String salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String languageId = outboundIntegrationHeader.getLanguageId();

            //Check WMS order table
            List<OutboundHeaderV2> outbound =
                    outboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);
            log.info("SalesOrderNumber already Exist: ---> PickList Cancellation to be executed " + salesOrderNumber);
            log.info("--outbound----------> " + outbound);

            String newSalesOrderNo = outboundIntegrationHeader.getSalesOrderNumber();
            if (outbound != null && !outbound.isEmpty()) {
                List<OutboundHeaderV2> oldSalesOrderNumberList =
                        outbound.stream().filter(n -> !n.getSalesOrderNumber().equalsIgnoreCase(newSalesOrderNo)).collect(Collectors.toList());
                log.info("Old SalesOrder Number, New SalesOrderNumber: " + oldSalesOrderNumberList + ", " + newSalesOrderNo);

                if (oldSalesOrderNumberList != null && !oldSalesOrderNumberList.isEmpty()) {
                    for (OutboundHeaderV2 oldSalesOrderNumber : oldSalesOrderNumberList) {
                        OutboundHeaderV2 outboundOrderV2 =
                                outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                                        companyCodeId, languageId, plantId, warehouseId, oldSalesOrderNumber.getPickListNumber(), oldSalesOrderNumber.getPreOutboundNo(), 0L);
                        log.info("Outbound Order status ---> Delivered for old Picklist Number: " + outboundOrderV2 + ", " + oldSalesOrderNumber);

                        if (outboundOrderV2 != null && outboundOrderV2.getInvoiceNumber() != null) {
                            // Update error message for the new PicklistNo
                            throw new BadRequestException("Picklist cannot be cancelled as Sales order associated with picklist - Invoice has been raised");
                        }

                        boolean recordExists = pickupHeaderV2Repository.existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(companyCodeId,
                                plantId, warehouseId, oldSalesOrderNumber.getRefDocNumber(), oldSalesOrderNumber.getPreOutboundNo(), 0L);
                        if (recordExists) {
                            try {
                                outboundIntegrationHeader.setRefDocumentNo(oldSalesOrderNumber.getRefDocNumber());
                                fullfillOutboundReceivedV5(outboundIntegrationHeader);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Outbound OrderReProcess
                            outboundOrderProcessCreation(outboundIntegrationHeader);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param oldPreOutboundNo
     * @param loginUserID
     * @return
     */
    private PickListCancellation pickListCancellation(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, String oldPickListNumber, String newSalesOrderNo, String oldPreOutboundNo,
                                                      String loginUserID) {
        try {
            log.info("PickList Cancellation Initiated---> " + companyCodeId + "," + plantId + "," + languageId + ","
                    + warehouseId + "," + oldPickListNumber + "," + oldPreOutboundNo);
            // Delete OutBoundHeader
            OutboundHeaderV2 outboundHeaderV2 = outboundHeaderService.getPLCOutBoundHeader(companyCodeId, plantId,
                    languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            // Delete OutBoundLine
            List<OutboundLineV2> outboundLineV2 = outboundLineService.getPLCOutBoundLine(companyCodeId, plantId,
                    languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            // Delete PreOutboundLine
            List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineService.getPLCPreOutBoundLine(companyCodeId,
                    plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            // DeleteOrderManagementLine
            List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getPLCOrderManagementLineV2(
                    companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            // Delete PickUpLine
            List<PickupLineV2> pickupLineV2 = pickupLineService.getPLCPickUpLine(companyCodeId, plantId, languageId,
                    warehouseId, oldPickListNumber, oldPreOutboundNo);

            // Quality Line
            List<QualityLineV2> qualityLineV2 = qualityLineService.getPLCQualityLine(companyCodeId, plantId, languageId,
                    warehouseId, oldPickListNumber, oldPreOutboundNo);

            List<String> pickuplineItemCodeMfrNameList = new ArrayList<>();
            List<String> pickuplineLineNoItemCodeMfrNameList = new ArrayList<>();
            if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
                for (PickupLineV2 pickupLine : pickupLineV2) {
                    pickuplineItemCodeMfrNameList.add(pickupLine.getItemCode() + pickupLine.getManufacturerName());
                    InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(),
                            pickupLine.getPlantId(), pickupLine.getLanguageId(), pickupLine.getWarehouseId(),
                            pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(),
                            pickupLine.getManufacturerName());

                    List<PickupLineV2> filteredList = pickupLineV2.stream()
                            .filter(a -> a.getItemCode().equalsIgnoreCase(pickupLine.getItemCode())
                                    && a.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName())
                                    && a.getLineNumber().equals(pickupLine.getLineNumber()))
                            .collect(Collectors.toList());
                    List<PreOutboundLineV2> filteredPreOutboundLineList = preOutboundLineV2List.stream()
                            .filter(n -> n.getItemCode().equalsIgnoreCase(pickupLine.getItemCode())
                                    && n.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName())
                                    && n.getLineNumber().equals(pickupLine.getLineNumber()))
                            .collect(Collectors.toList());

                    Double PICK_CNF_QTY = 0D;
                    Double ORD_QTY = 0D;
                    if (filteredList != null && !filteredList.isEmpty()) {
                        PICK_CNF_QTY = filteredList.stream().mapToDouble(a -> a.getPickConfirmQty()).sum();
                    }
                    if (filteredPreOutboundLineList != null && !filteredPreOutboundLineList.isEmpty()) {
                        ORD_QTY = filteredPreOutboundLineList.stream().mapToDouble(n -> n.getOrderQty()).sum();
                    }

                    boolean equalQty = PICK_CNF_QTY.equals(ORD_QTY);
                    log.info("PICK_CNF_QTY, ORD_TY, EqualQty Condition: " + PICK_CNF_QTY + ", " + ORD_QTY + ", "
                            + equalQty);
                    if (equalQty) {
                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null
                                    ? inventory.getInventoryQuantity()
                                    : 0)
                                    + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
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
                            inventory.setReferenceField4(TOT_QTY); // Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2,
                                    CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 6));
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                    }

                    if (!equalQty) {
                        String itmMfrNameLineNo = pickupLine.getItemCode() + pickupLine.getManufacturerName()
                                + pickupLine.getLineNumber();
                        List<String> filterList = pickuplineLineNoItemCodeMfrNameList.stream()
                                .filter(a -> a.equalsIgnoreCase(itmMfrNameLineNo)).collect(Collectors.toList());
                        if (filterList.size() == 0) {
                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null
                                        ? inventory.getInventoryQuantity()
                                        : 0) + ORD_QTY;
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
                                inventory.setReferenceField4(TOT_QTY); // Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2,
                                        CommonUtils.getNullPropertyNames(inventory));
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
                        if (dbOrderManagementLine.getProposedStorageBin() == null
                                || dbOrderManagementLine.getProposedStorageBin().equalsIgnoreCase("")) {
                            throw new BadRequestException(
                                    "OrderManagementLine ProposedStorageBin is Empty, hence inventory cannot be reversed - PickList Cancellation Aborting");
                        }
                        String itmMfrName = dbOrderManagementLine.getItemCode()
                                + dbOrderManagementLine.getManufacturerName();
                        log.info("ItmMfrName: " + itmMfrName);
                        List<String> itmPresent = pickuplineItemCodeMfrNameList.stream()
                                .filter(a -> a.equalsIgnoreCase(itmMfrName)).collect(Collectors.toList());
                        log.info("itmPresent: " + itmPresent);
                        if (itmPresent.size() == 0) {
                            InventoryV2 inventory = inventoryService.getInventoryV2(
                                    dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(),
                                    dbOrderManagementLine.getLanguageId(), dbOrderManagementLine.getWarehouseId(),
                                    dbOrderManagementLine.getProposedPackBarCode(), dbOrderManagementLine.getItemCode(),
                                    dbOrderManagementLine.getProposedStorageBin(),
                                    dbOrderManagementLine.getManufacturerName());

                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null
                                        ? inventory.getInventoryQuantity()
                                        : 0)
                                        + (dbOrderManagementLine.getAllocatedQty() != null
                                        ? dbOrderManagementLine.getAllocatedQty()
                                        : 0);
                                if (INV_QTY < 0) {
                                    log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                    INV_QTY = 0D;
                                }
                                inventory.setInventoryQuantity(INV_QTY);
                                Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null
                                        ? inventory.getAllocatedQuantity()
                                        : 0)
                                        - (dbOrderManagementLine.getAllocatedQty() != null
                                        ? dbOrderManagementLine.getAllocatedQty()
                                        : 0);
                                if (ALLOC_QTY < 0) {
                                    ALLOC_QTY = 0D;
                                }
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                inventory.setAllocatedQuantity(ALLOC_QTY);
                                inventory.setReferenceField4(TOT_QTY); // Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2,
                                        CommonUtils.getNullPropertyNames(inventory));
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
            pickListCancellation.setNewSalesOrderNumber(newSalesOrderNo);

            //Stored procedure to update deletionIndicator Flag
            pickListHeaderRepository.updateDeletionIndicatorPickListCancellationProc(companyCodeId, plantId, languageId,
                    warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID, new Date());
            log.info("Pick List cancellation - stored procedure update - deletion indicator finished processing");
            return pickListCancellation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param customerId
     * @param salesOrderNumber
     * @param outboundOrderTypeId
     * @param idMasterAuthToken
     * @return
     */
    private String getPreOutboundNo(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String customerId, String salesOrderNumber, Long outboundOrderTypeId, String idMasterAuthToken) {
        Optional<PreOutboundHeaderV2> orderProcessedStatus =
                preOutboundHeaderV2Repository.findTopBySalesOrderNumberAndOutboundOrderTypeIdAndCustomerIdAndDeletionIndicator(salesOrderNumber, outboundOrderTypeId, customerId, 0L);
        if (orderProcessedStatus.isPresent()) {
            log.info("------preOutboundNo---------existing----one---> " + orderProcessedStatus.get().getPreOutboundNo());
            return orderProcessedStatus.get().getPreOutboundNo();
        } else {
            // Getting PreOutboundNo from NumberRangeTable
            return getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    private String getItemDescription(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName) {
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId, itemCode.trim(), manufacturerName, 0L);
        return imBasicData1 != null ? imBasicData1.getDescription() : null;
    }

    /**
     * @param createdPreOutboundHeader
     * @param loginUserId
     * @return
     */
    private OrderManagementHeaderV2 createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, String statusDesc, String loginUserId) throws Exception {
        try {
            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            newOrderManagementHeader.setStatusId(statusId);
            newOrderManagementHeader.setStatusDescription(statusDesc);
            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
            newOrderManagementHeader.setPickupCreatedOn(new Date());
            newOrderManagementHeader.setDeletionIndicator(0L);

            //Deleting orderManagementHeader
            orderManagementHeaderV2Repository.delete(newOrderManagementHeader);
            orderManagementHeaderV2Repository.saveAndFlush(newOrderManagementHeader);
            log.info("OrderManagement Header Saved.");

            // Order_Text_Update
            String text = "OrderManagement Created";
            outboundOrderV2Repository.updateOrderManagementText(newOrderManagementHeader.getOutboundOrderTypeId(), newOrderManagementHeader.getRefDocNumber(), text);
            log.info("OrderManagement Header Status Updated Successfully");
            return newOrderManagementHeader;
        } catch (Exception e) {
            log.error("Exception while creating OrderManagementHeader : " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader
     * @param loginUserId
     * @return
     */
    private OrderManagementHeaderV2 createOrderManagementHeaderV3(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, String statusDesc, String loginUserId) throws Exception {
        try {
            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            newOrderManagementHeader.setStatusId(statusId);
            newOrderManagementHeader.setStatusDescription(statusDesc);
            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
            newOrderManagementHeader.setPickupCreatedOn(new Date());
            // Order_Text_Update
            String text = "OrderManagement Created";
            outboundOrderV2Repository.updateOrderManagementText(newOrderManagementHeader.getOutboundOrderTypeId(), newOrderManagementHeader.getRefDocNumber(), text);
            log.info("OrderManagement Header Status Updated Successfully");
            return newOrderManagementHeader;
        } catch (Exception e) {
            log.error("Exception while creating OrderManagementHeader : " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader
     * @param outboundIntegrationHeader
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                    Long statusId, String statusDesc) throws Exception {
        try {
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            outboundHeader.setRefDocDate(new Date());
            outboundHeader.setStatusId(statusId);
            outboundHeader.setStatusDescription(statusDesc);
            outboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
            outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
            outboundHeader.setDeletionIndicator(0L);

            if (createdPreOutboundHeader.getCustomerId() == null) {
                outboundHeader.setPartnerCode("STO");
            }

            if (outboundHeader.getOutboundOrderTypeId() == 3L) {
                outboundHeader.setCustomerType("INVOICE");
            }
            if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
                outboundHeader.setCustomerType("TRANSVERSE");
            }
            return outboundHeader;
        } catch (Exception e) {
            log.error("Exception While OutboundHeader create: " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader
     * @param outboundIntegrationHeader
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private OutboundHeaderV2 createOutboundHeaderV3(PreOutboundHeaderV2 createdPreOutboundHeader,
                                                    OutboundIntegrationHeaderV2 outboundIntegrationHeader, Long statusId, String statusDesc) throws Exception {
        try {
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader,
                    CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            outboundHeader.setRefDocDate(new Date());
            outboundHeader.setStatusId(statusId);
            outboundHeader.setStatusDescription(statusDesc);
            outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
            outboundHeaderV2Repository.save(outboundHeader);  // OutboundHeader save 28-05-2025
            return outboundHeader;
        } catch (Exception e) {
            log.error("Exception While OutboundHeader create: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param refField1ForOrderType
     * @param statusId
     * @param statusDesc
     * @param desc
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, String refField1ForOrderType,
                                                          Long statusId, String statusDesc, IKeyValuePair desc, String loginUserId) throws Exception {
        try {
            PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
            BeanUtils.copyProperties(outboundIntegrationHeader, preOutboundHeader, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
            preOutboundHeader.setCompanyCodeId(companyCodeId);
            preOutboundHeader.setPlantId(plantId);
            preOutboundHeader.setLanguageId(languageId);
            preOutboundHeader.setWarehouseId(warehouseId);
            preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
            preOutboundHeader.setPreOutboundNo(preOutboundNo);
            preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
            preOutboundHeader.setRefDocDate(new Date());

            // REF_FIELD_1
            preOutboundHeader.setReferenceField1(refField1ForOrderType);
            preOutboundHeader.setStatusId(statusId);
            preOutboundHeader.setStatusDescription(statusDesc);
            preOutboundHeader.setCompanyDescription(desc.getCompanyDesc());
            preOutboundHeader.setPlantDescription(desc.getPlantDesc());
            preOutboundHeader.setWarehouseDescription(desc.getWarehouseDesc());

            preOutboundHeader.setDeletionIndicator(0L);
            preOutboundHeader.setCreatedBy(loginUserId);
            preOutboundHeader.setCreatedOn(new Date());

            // Order_Text_Update
            String text = "PreOutboundHeader Created";
            outboundOrderV2Repository.updatePreOutBoundOrderText(preOutboundHeader.getOutboundOrderTypeId(), preOutboundHeader.getRefDocNumber(), text);
            log.info("PreOutbound Header Status Updated Successfully");
            return preOutboundHeader;
        } catch (Exception e) {
            log.error("Exception While PreoutboundHeader create: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param outboundIntegrationLine
     * @param statusId
     * @param statusDesc
     * @param desc
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreOutboundLineV2 createPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                      OutboundIntegrationHeaderV2 outboundIntegrationHeader, OutboundIntegrationLineV2 outboundIntegrationLine,
                                                      Long statusId, String statusDesc, IKeyValuePair desc, String loginUserId) throws Exception {
        try {
            PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
            BeanUtils.copyProperties(outboundIntegrationLine, preOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationLine));
            preOutboundLine.setCompanyCodeId(companyCodeId);
            preOutboundLine.setPlantId(plantId);
            preOutboundLine.setLanguageId(languageId);
            preOutboundLine.setWarehouseId(warehouseId);
            preOutboundLine.setPreOutboundNo(preOutboundNo);
            preOutboundLine.setCustomerId(outboundIntegrationHeader.getCustomerId());
            preOutboundLine.setCustomerName(outboundIntegrationHeader.getCustomerName());

            // REF DOC Number
            preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

            // PARTNER_CODE
            preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

            // IB__LINE_NO
            preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

            // ITM_CODE
            preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());

            // OB_ORD_TYP_ID
            preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());

            // STATUS_ID
            preOutboundLine.setStatusId(statusId);
            preOutboundLine.setStatusDescription(statusDesc);

            // STCK_TYP_ID
            preOutboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preOutboundLine.setSpecialStockIndicatorId(1L);

            preOutboundLine.setCompanyDescription(desc.getCompanyDesc());
            preOutboundLine.setPlantDescription(desc.getPlantDesc());
            preOutboundLine.setWarehouseDescription(desc.getWarehouseDesc());

            preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
            preOutboundLine.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
            preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
            preOutboundLine.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
            preOutboundLine.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

            String itemText = outboundIntegrationLine.getItemText() != null ? outboundIntegrationLine.getItemText() :
                    getItemDescription(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationLine.getItemCode(), outboundIntegrationLine.getManufacturerName());

            preOutboundLine.setDescription(itemText);

            // ORD_QTY
            preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());

            // ORD_UOM
            preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

            // REQ_DEL_DATE
            preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

            // REF_FIELD_1
            preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

            preOutboundLine.setDeletionIndicator(0L);
            preOutboundLine.setCreatedBy(loginUserId);
            preOutboundLine.setCreatedOn(new Date());

//            log.info("preOutboundLine : " + preOutboundLine);
            return preOutboundLine;
        } catch (Exception e) {
            log.error("Exception While PreoutboundLine create: " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundLine
     * @param outboundIntegrationHeader
     * @return
     */
    private List<OutboundLineV2> createOutboundLineV2(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            List<OutboundLineV2> outboundLines = new ArrayList<>();
            for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
                List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getOrderManagementLineV2(
                        preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(),
                        preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId(),
                        preOutboundLine.getPreOutboundNo(), preOutboundLine.getLineNumber(),
                        preOutboundLine.getItemCode());
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                    OutboundLineV2 outboundLine = new OutboundLineV2();
                    BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
                    outboundLine.setDeliveryQty(0D);
                    outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                    statusDescription = getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                    outboundLine.setStatusDescription(statusDescription);
                    outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
                    outboundLine.setDeletionIndicator(0L);

                    if (outboundLine.getOutboundOrderTypeId() == 3L) {
                        outboundLine.setCustomerType("INVOICE");
                    }
                    if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
                        outboundLine.setCustomerType("TRANSVERSE");
                    }
                    outboundLines.add(outboundLine);
                }
            }
//            outboundLines = outboundLineV2Repository.saveAll(outboundLines);
            log.info("outboundLines created -----2------>: " + outboundLines);
            return outboundLines;
        } catch (Exception e) {
            log.error("Exception While OutboundLine create: " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundLine
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    private List<OutboundLineV2> createOutboundLineV3(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            List<OutboundLineV2> outboundLines = new ArrayList<>();
            for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
                OutboundLineV2 outboundLine = new OutboundLineV2();
                BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
                outboundLine.setDeliveryQty(0D);
                outboundLine.setStatusId(39L);
                statusDescription = getStatusDescription(39L, preOutboundLine.getLanguageId());
                outboundLine.setStatusDescription(statusDescription);
                outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());

                if (outboundLine.getOutboundOrderTypeId() == 3L) {
                    outboundLine.setCustomerType("INVOICE");
                }
                if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
                    outboundLine.setCustomerType("TRANSVERSE");
                }
                outboundLines.add(outboundLine);
            }
            log.info("outboundLines created -----2------>: " + outboundLines);
            return outboundLines;
        } catch (Exception e) {
            log.error("Exception While OutboundLine create: " + e);
            throw e;
        }
    }

    /**
     * 
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundLineList
     * @param loginUserId
     * @param fromOrderFullfillment
     * @throws Exception
     */
    @Transactional
    public void createOrderManagementLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, List<PreOutboundLineV2> preOutboundLineList,
                                          String loginUserId, boolean fromOrderFullfillment) throws Exception {
        log.error("--------1-------fromOrderFullfillment----> " + fromOrderFullfillment);
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                createOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, 
                		preOutboundLine, loginUserId, fromOrderFullfillment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundLineList
     * @param loginUserId
     * @throws Exception
     */
    @Transactional
    public void createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                            List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                createOrderManagementLineV3(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundLine, loginUserId);
//                log.info("orderManagementLine created---1---> : " + orderManagementLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @return
     * @throws Exception
     */
    private void createOrderManagementLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
        try {
            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
            log.info("orderManagementLine : " + orderManagementLine);

            /*
             * 1. If OB_ORD_TYP_ID = 0,1,3
             * Pass WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
             * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZB,ZC,ZG,ZT and
             * PUTAWAY_BLOCK and PICK_BLOCK are false(Null).
             *
             * 2. If OB_ORD_TYP_ID = 2
             * Pass  WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
             * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZD and PUTAWAY_BLOCK
             * and PICK_BLOCK are false(Null).
             *
             * Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch Sum(INV_QTY)"
             */

            Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
            Long BIN_CLASS_ID;

            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
                BIN_CLASS_ID = 1L;
                createOrderManagementV3(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
                        preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                createOrderManagementV3(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
                        preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
            }
//            return orderManagementLine;
        } catch (Exception e) {
            log.error("Exception While OrderManagementLine create: " + e);
            throw e;
        }
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @return
     * @throws Exception
     */
    private void createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine,
                                             String loginUserId, boolean fromOrderFullfillment) throws Exception {
        try {
            log.error("--------2-------fromOrderFullfillment----> " + fromOrderFullfillment);
            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));

            if (preOutboundLine.getCustomerId() == null) {
                orderManagementLine.setPartnerCode("STO");
            }
            log.info("orderManagementLine : " + orderManagementLine);

            /*
             * 1. If OB_ORD_TYP_ID = 0,1,3
             * Pass WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
             * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZB,ZC,ZG,ZT and
             * PUTAWAY_BLOCK and PICK_BLOCK are false(Null).
             *
             * 2. If OB_ORD_TYP_ID = 2
             * Pass  WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
             * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZD and PUTAWAY_BLOCK
             * and PICK_BLOCK are false(Null).
             *
             * Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch Sum(INV_QTY)"
             */

            Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
            Long BIN_CLASS_ID;

            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
                if (preOutboundLine.getMtoNumber() != null && !preOutboundLine.getMtoNumber().isEmpty()) {
                    log.info("MTO Number is ------------------> {} ", preOutboundLine.getMtoNumber());
                    BIN_CLASS_ID = 10L;
                } else {
                    BIN_CLASS_ID = 1L;
                }
                createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
                        preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId, fromOrderFullfillment);
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
                        preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId, fromOrderFullfillment);
            }
        } catch (Exception e) {
            log.error("Exception While OrderManagementLine create: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @return
     */
    private void createOrderManagementV2(String companyCodeId, String plantId, String languageId,
                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId,
                                         boolean fromOrderFullfillment) throws Exception {
        log.error("--------3-------fromOrderFullfillment----> " + fromOrderFullfillment);
        String manufacturerName = orderManagementLine.getManufacturerName();
        orderManagementLineService.updateAllocationV3(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, binClassId, ORD_QTY, orderManagementLine, loginUserId, fromOrderFullfillment);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @return
     */
    private void createOrderManagementV3(String companyCodeId, String plantId, String languageId,
                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
        String manufacturerName = orderManagementLine.getManufacturerName();
//        List<IInventoryImpl> stockType1InventoryList = inventoryService.
//                getInventoryForOrderManagementV3(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, manufacturerName);
//        log.info("Walkaroo---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//        if (stockType1InventoryList.isEmpty()) {
//            return createEMPTYOrderManagementLineV2(orderManagementLine);
//        }

        orderManagementLineService.updateAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, ORD_QTY, orderManagementLine, loginUserId);
    }


    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 createEMPTYOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) throws Exception {
        try {
            orderManagementLine.setStatusId(47L);
            statusDescription = getStatusDescription(47L, orderManagementLine.getLanguageId());
            orderManagementLine.setStatusDescription(statusDescription);
            orderManagementLine.setReferenceField7(statusDescription);
            orderManagementLine.setProposedStorageBin("");
            orderManagementLine.setProposedPackBarCode("");
            orderManagementLine.setBarcodeId(UUID.randomUUID().toString());
            orderManagementLine.setInventoryQty(0D);
            orderManagementLine.setAllocatedQty(0D);

            if (orderManagementLine.getCompanyDescription() == null) {
                description = getDescription(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId());
                orderManagementLine.setCompanyDescription(description.getCompanyDesc());
                orderManagementLine.setPlantDescription(description.getPlantDesc());
                orderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
            }

            orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
            log.info("orderManagementLine created: " + orderManagementLine);
            return orderManagementLine;
        } catch (Exception e) {
            log.error("Exception While EmptyOrderManagementLine create: " + e);
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
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
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
//            if (orderManagementLineV2List != null && !orderManagementLineV2List.isEmpty()) {
//                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
//                    String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
//                    String storageBin = dbOrderManagementLine.getProposedStorageBin();
//                    InventoryV2 inventory =
//                            inventoryService.getOutboundInventoryV3(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
//                                    dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getItemCode(), dbOrderManagementLine.getManufacturerName(),
//                                    dbOrderManagementLine.getBarcodeId(), storageBin);
//                    double[] inventoryQty = calculateInventoryUnAllocate(dbOrderManagementLine.getAllocatedQty(), inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
//                    if (inventoryQty != null && inventoryQty.length > 2) {
//                        inventory.setInventoryQuantity(inventoryQty[0]);
//                        inventory.setAllocatedQuantity(inventoryQty[1]);
//                        inventory.setReferenceField4(inventoryQty[2]);
//                    }
//
//                    // Create new Inventory Record
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
//                    log.info("-----InventoryV2 created-------: " + inventoryV2);
//                }
//                log.info("Rollback---> 1.Inventory restoration Finished ----> " + refDocNo + ", " + outboundOrderTypeId);
//            }

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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param orderManagementLineList
     * @param loginUserId
     */
    private List<DocumentNumber> createPickupHeaderV3(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                      String preOutboundNo, List<OrderManagementLineV2> orderManagementLineList, String loginUserId) {
        try {
            double sumOfAllocatedQty = orderManagementLineList
            		.stream()
            		.filter(n -> n.getAllocatedQty() != null)
            		.mapToDouble(OrderManagementLineV2::getAllocatedQty).sum();
            
            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
            log.info("caseTolerance: " + caseTolerance);

            List<DocumentNumber> documentNumberList = new ArrayList<>();
            List<OrderManagementLineV2> sortedOrderManagementLineList = 
            		orderManagementLineList
            		.stream()
            		.sorted(Comparator.comparing(OrderManagementLineV2::getProposedStorageBin)
            				.reversed()).collect(Collectors.toList());
            
            String PU_NO = null;
            if (caseTolerance != null) {
                double noOfCases = getQuantity(caseTolerance.getNoOfCase());
                double plusTolerance = getQuantity(caseTolerance.getPlusTolerance());
                double totalCases = noOfCases + plusTolerance;
                int PU_NO_COUNT = (int) Math.ceil(sumOfAllocatedQty / totalCases);
                log.info(noOfCases + "|" + plusTolerance + "|" + totalCases + "|" + sumOfAllocatedQty + "|" + PU_NO_COUNT);
                int i = 1;
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                
                for (OrderManagementLineV2 createdOrderManagementLine : sortedOrderManagementLineList) {
                    if (createdOrderManagementLine.getOutboundOrderTypeId() == 3) {
                        if (i <= totalCases) {
                            log.info("Creating PickUpHeader for case {} of {} with PU_NO: {}", i, totalCases, PU_NO);

                            createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, 
                            		createdOrderManagementLine, loginUserId);
                            i++;
                            if (i > totalCases) {
                                log.info("Total cases processed for current PU_NO. Resetting case counter and generating new PU_NO.");
                                i = 1;
                                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                            }
                        }
                    }
                }
            } else {
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                for (OrderManagementLineV2 orderManagementLine : orderManagementLineList) {
                    if (orderManagementLine.getOutboundOrderTypeId() == 3) {
                        createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, 
                        		orderManagementLine, loginUserId);
                    }
                }
            }
            return documentNumberList;

        } catch (Exception e) {
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param orderManagementLineList
     * @param loginUserId
     */
    private List<DocumentNumber> createPickupHeaderV4(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                      String preOutboundNo, List<OrderManagementLineV2> orderManagementLineList, String loginUserId) {
        try {
        	log.info ("-------createPickupHeaderV4--------called-------");
            List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
            double sumOfAllocatedQty = orderManagementLineList.stream().filter(n -> n.getAllocatedQty() != null).mapToDouble(OrderManagementLineV2::getAllocatedQty).sum();
            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
            log.info("caseTolerance: " + caseTolerance);

            List<DocumentNumber> documentNumberList = new ArrayList<>();
            List<OrderManagementLineV2> sortedOrderManagementLineList = orderManagementLineList.stream().sorted(Comparator.comparing(OrderManagementLineV2::getProposedStorageBin).reversed()).collect(Collectors.toList());
            String PU_NO = null;
            if (caseTolerance != null) {
                double noOfCases = getQuantity(caseTolerance.getNoOfCase());
                double plusTolerance = getQuantity(caseTolerance.getPlusTolerance());
                double totalCases = noOfCases + plusTolerance;
                int PU_NO_COUNT = (int) Math.ceil(sumOfAllocatedQty / totalCases);
                log.info(noOfCases + "|" + plusTolerance + "|" + totalCases + "|" + sumOfAllocatedQty+ "|" + PU_NO_COUNT);
                int i = 1;
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                log.info("OrderFullfillment PickupHeader Creation -------------> Sorted OrdermanagementList ---->  {} ", orderManagementLineList.size());

                for (OrderManagementLineV2 createdOrderManagementLine : sortedOrderManagementLineList) {
                    log.info("OutboundOrderType ID is --------------------> {} ", createdOrderManagementLine.getOutboundOrderTypeId() );
                    if (createdOrderManagementLine.getOutboundOrderTypeId() == 3) {
                        if (i <= totalCases) {
                           PickupHeaderV2 pickupHeaderV2 =  createPickUpHeaderV4(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo,
                                    refDocNumber, createdOrderManagementLine, loginUserId);
                            if (pickupHeaderV2 != null) {
                                log.info("PickupHeader is Created -------------------> RefDocNo is {} ", pickupHeaderV2.getRefDocNumber());
                                pickupHeaderV2List.add(pickupHeaderV2);
                            }
                            i++;
                            if (i > totalCases) {
                                i = 1;
                                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                            }
                        }
//                        DocumentNumber documentNumber = new DocumentNumber();
//                        documentNumber.setRefDocNumber(createdOrderManagementLine.getRefDocNumber());
//                        documentNumber.setPreOutboundNo(createdOrderManagementLine.getPreOutboundNo());
//                        documentNumberList.add(documentNumber);
                    }
                }
            } else {
                log.info("CaseTolerance is null ------------------------------------> ");
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                for (OrderManagementLineV2 orderManagementLine : orderManagementLineList) {
                    log.info("OutboundOrderType ID is --------------------> {} ", orderManagementLine.getOutboundOrderTypeId() );
                    if(orderManagementLine.getOutboundOrderTypeId() == 3) {
                        PickupHeaderV2 pickupHeaderV2 = createPickUpHeaderV4(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo,
                                refDocNumber, orderManagementLine, loginUserId);
                        if (pickupHeaderV2 != null) {
                            log.info("PickupHeader is Created -------------------> RefDocNo is {} ", pickupHeaderV2.getRefDocNumber());
                            pickupHeaderV2List.add(pickupHeaderV2);
                        }
//                        DocumentNumber documentNumber = new DocumentNumber();
//                        documentNumber.setRefDocNumber(orderManagementLine.getRefDocNumber());
//                        documentNumber.setPreOutboundNo(orderManagementLine.getPreOutboundNo());
//                        documentNumberList.add(documentNumber);
                    }
                }
            }

            if(!pickupHeaderV2List.isEmpty()) {
                log.info("PickupHeader Values Saved in Orderfullfillment ---------------------> " + pickupHeaderV2List.size());
                pickupHeaderV2Repository.saveAll(pickupHeaderV2List);
            }


//            List<String> distinctRefDocNumbers = pickupHeaderV2List.stream()
//                    .map(PickupHeaderV2::getRefDocNumber)
//                    .filter(Objects::nonNull)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            for(String refDocNo : distinctRefDocNumbers) {
//                log.info("PickupHeader Status Id RefDocNo List -------------> " + refDocNo);
//                // Order_Text_Update
//                String text = "PickupHeader Created";
//                outboundOrderV2Repository.updatePickupHeaderStatus(refDocNo, text);
//                log.info("PickupHeader Status Updated Successfully -----------------> " + refDocNo);
//            }

            return documentNumberList;
        } catch (Exception e) {
            log.error("Exception in createPickupHeaderV4: ", e);
            throw new BadRequestException(e.getLocalizedMessage());
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
     * @param orderManagementLine
     * @param loginUserId
     * @throws Exception
     */
//    private void createPickUpHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                    String preOutboundNo, String refDocNumber, OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {
//
//        String PU_NO = null;
//        if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
//            log.info("orderManagementLine: " + orderManagementLine);
//
//            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
//            log.info("caseTolerance: " + caseTolerance);
//            List<PickupHeaderV2> pickupHeaders = pickupHeaderService.getPickupHeadersV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, orderManagementLine.getCustomerId());
//            log.info("PickupHeader existing---->: " + pickupHeaders.size());
//            if (caseTolerance != null) {
//
//                double noOfCases = getQuantity(caseTolerance.getNoOfCase());
//                double plusTolerance = getQuantity(caseTolerance.getPlusTolerance());
//                double totalCases = noOfCases + plusTolerance;
//                double allocatedQty = getQuantity(orderManagementLine.getAllocatedQty());
//                log.info(noOfCases + "|" + plusTolerance + "|" + totalCases + "|" + allocatedQty);
//
//                if (pickupHeaders != null && !pickupHeaders.isEmpty()) {
//                    double sumOfPickQty = pickupHeaders.stream().filter(n -> n.getPickToQty() != null).mapToDouble(PickupHeaderV2::getPickToQty).sum();
//                    double totalQty = sumOfPickQty + allocatedQty;
//                    log.info("TotalQty : " + totalQty);
//                    if (totalQty < totalCases) {
//                        PU_NO = pickupHeaders.get(0).getPickupNumber();
//                        createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//                    } else {
//                        createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, noOfCases, allocatedQty, totalCases, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//                    }
//                } else if (pickupHeaders == null || pickupHeaders.isEmpty()) {
//                    createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, noOfCases, allocatedQty, totalCases, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//                }
//            } else {
//                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
//                createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//            }
//        }
//    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param noOfCases
     * @param allocatedQty
     * @param totalCases
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderManagementLine
     * @param loginUserId
     * @throws Exception
     */
//    private void createPickUpHeaderV3(String companyCodeId, String plantId, String languageId, String warehouseId, double noOfCases, double allocatedQty,
//                                      double totalCases, String preOutboundNo, String refDocNumber, OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {
//        try {
//            String idMasterToken = getIDMasterAuthToken();
//            long NUM_RAN_CODE = 10;
//            double i = noOfCases;
//            double j = allocatedQty;
//            while (i <= allocatedQty) {
//                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId, idMasterToken);
//                log.info("----------New PU_NO--------> : " + PU_NO);
//                createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);
//                i = i + noOfCases;
//                j = j - noOfCases;
//                if(j > noOfCases && j <= totalCases) {
//                    i = allocatedQty;
//                }
//            }
//        } catch (Exception e) {
//            log.error("Exception while creating PickUpHeader : " + e.getLocalizedMessage());
//            throw e;
//        }
//    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param outboundHeader
     * @param loginUserId
     * @throws Exception
     */
//    private void validatePickupHeaderCreation(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                              String refDocNumber, String preOutboundNo, OutboundHeaderV2 outboundHeader, String loginUserId) throws Exception {
//
//        String salesOrderNumber = outboundHeader.getSalesOrderNumber();
//        log.info("SalesOrderNumber : " + salesOrderNumber);
//        List<DocumentNumber> documentNumberList = new ArrayList<>();
//        try {
//            if (outboundHeader.getOutboundOrderTypeId() == 3L) {
//                List<String> customerIdList = preOutboundHeaderV2Repository.getCustomerIdByOrder(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
//                log.info("CustomerId List : " + customerIdList.size());
//                if (customerIdList != null && !customerIdList.isEmpty()) {
//                    for (String customerId : customerIdList) {
//                        log.info("CustomerId : " + customerId);
//                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, customerId);
//                        documentNumberList = createPickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, orderManagementLineList, loginUserId);
//                    }
//                }
//                assignPicker(companyCodeId, plantId, languageId, warehouseId, outboundHeader, documentNumberList, loginUserId);
//            }
//        } catch (Exception e) {
//            log.error("Exception while validating PickupHeader creation");
//            throw e;
//        }
//    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param outboundHeader
     * @param loginUserId
     * @throws Exception
     */
    private void validatePickupHeaderCreation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String refDocNumber, String preOutboundNo, OutboundHeaderV2 outboundHeader, String loginUserId) throws Exception {

        String salesOrderNumber = outboundHeader.getSalesOrderNumber();
        log.info("SalesOrderNumber : " + salesOrderNumber);
        log.info("CompanyId : " + companyCodeId);
        log.info("PlantId : " + plantId);
        log.info("WarehouseId : " + warehouseId);
        Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
        log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
        try {
            if (pickupHeaderStatusCheck == 1 && outboundHeader.getOutboundOrderTypeId() == 3L) {
                List<String> shipToPartyList = preOutboundHeaderV2Repository.getShipToParty(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("shipToParty List : " + shipToPartyList.size());
                if (shipToPartyList != null && !shipToPartyList.isEmpty()) {
                    for (String shipToParty : shipToPartyList) {
                        log.info("ShipToParty : " + shipToParty);
                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesShipToPartyV3(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty);
                        createPickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, orderManagementLineList, loginUserId);
                    }
                }
                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
            }
        } catch (Exception e) {
            log.error("Exception while validating PickupHeader creation");
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param outboundHeader
     * @param loginUserId
     * @throws Exception
     */
    private void validatePickupHeaderCreationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String refDocNumber, String preOutboundNo, OutboundHeaderV2 outboundHeader, String loginUserId) throws Exception {
    	log.info("---companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, loginUserId : {},{},{},{},{},{},{},{}--->",
    			companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, loginUserId);
        String salesOrderNumber = outboundHeader.getSalesOrderNumber();
        
        log.info("SalesOrderNumber : " + salesOrderNumber);
        Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
        log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
        try {
            if (pickupHeaderStatusCheck == 1 && outboundHeader.getOutboundOrderTypeId() == 3L) {
//            if (outboundHeader.getOutboundOrderTypeId() == 3L) {
                List<String> shipToPartyList = preOutboundHeaderV2Repository.getShipToParty(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
                log.info("shipToParty List : " + shipToPartyList.size());
                if (shipToPartyList != null && !shipToPartyList.isEmpty()) {
                    for (String shipToParty : shipToPartyList) {
                        log.info("ShipToParty : " + shipToParty);
                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesShipToPartyV3(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty);
                        log.info("OrderManagementLine List in Orderfullfillment -----------------------> RefDocNo is {} ", refDocNumber);
                        createPickupHeaderV4(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, orderManagementLineList, loginUserId);
                    }
                }
                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
            }
        } catch (Exception e) {
            log.error("Exception while validating PickupHeader creation");
            throw e;
        }
    }


    /**
     * @param outboundHeader
     * @param loginUserId
     * @throws Exception
     */
    private void validatePickupHeaderCreationV2(OutboundIntegrationHeaderV2 outboundHeader, String salesOrderNumber, String loginUserId) throws Exception {

        String companyCodeId = outboundHeader.getCompanyCode();
        String plantId = outboundHeader.getBranchCode();
        String warehouseId = outboundHeader.getWarehouseID();
        String languageId = outboundHeader.getLanguageId();
        log.info("SalesOrderNumber : " + salesOrderNumber);
        Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
        log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
        try {
            if (pickupHeaderStatusCheck == 1) {
                List<String> shipToPartyList = preOutboundHeaderV2Repository.getShipToParty(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("shipToParty List : " + shipToPartyList.size());
                if (shipToPartyList != null && !shipToPartyList.isEmpty()) {
                    for (String shipToParty : shipToPartyList) {
                        log.info("ShipToParty : " + shipToParty);
                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesShipToPartyV3(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty);
                        
                        log.info("---validatePickupHeaderCreationV2---orderManagementLineList-----> : " + orderManagementLineList);
                        createPickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, orderManagementLineList.get(0).getRefDocNumber(), orderManagementLineList.get(0).getPreOutboundNo(), orderManagementLineList, loginUserId);
                    }
                }
                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
            }
        } catch (Exception e) {
            log.error("Exception while validating PickupHeader creation");
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundHeader
     * @param documentNumberList
     * @throws Exception
     */
    private void assignPicker(String companyCodeId, String plantId, String languageId, String warehouseId,
                              OutboundHeaderV2 outboundHeader, List<DocumentNumber> documentNumberList, String loginUserId) throws Exception {
        try {
            String salesOrderNumber = outboundHeader.getSalesOrderNumber();
            Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
            log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
            if (pickupHeaderStatusCheck == 1 && outboundHeader.getOutboundOrderTypeId() == 3L) {
                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("DocList : " + documentNumberList);
//                if (documentNumberList != null && !documentNumberList.isEmpty()) {
//                    log.info("FireBase Notification Initiated !");
//                    for (DocumentNumber documentNumber : documentNumberList) {
//                        fireBaseNotification(companyCodeId, plantId, languageId, warehouseId, documentNumber.getPreOutboundNo(), documentNumber.getRefDocNumber(), outboundHeader.getReferenceDocumentType(), loginUserId);
//                    }
//                }
            }
        } catch (Exception e) {
            log.error("Exception while validating PickupHeader creation");
            throw e;
        }
    }

//    private void validatePickupHeaderCreation(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                              String refDocNumber, String preOutboundNo, OutboundHeaderV2 outboundHeader, String loginUserId) throws Exception {
//
//        String salesOrderNumber = outboundHeader.getSalesOrderNumber();
//        log.info("SalesOrderNumber : " + salesOrderNumber);
//        List<DocumentNumber> documentNumberList = new ArrayList<>();
//        try {
//            Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//            log.info("pickupHeaderStatusCheck : " + pickupHeaderStatusCheck);
//            if (pickupHeaderStatusCheck == 1 && outboundHeader.getOutboundOrderTypeId() == 3L) {
//                List<String> customerIdList = preOutboundHeaderV2Repository.getCustomerId(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("CustomerId List : " + customerIdList.size());
//                if (customerIdList != null && !customerIdList.isEmpty()) {
//                    for (String customerId : customerIdList) {
//                        log.info("CustomerId : " + customerId);
//                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesV3(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, customerId);
//                        documentNumberList = createPickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, orderManagementLineList, loginUserId);
//                    }
//                }
//                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
//                log.info("DocList : " + documentNumberList);
//                if(documentNumberList != null && !documentNumberList.isEmpty()) {
//                    log.info("FireBase Notification Initiated !");
//                    for(DocumentNumber documentNumber : documentNumberList) {
//                        fireBaseNotification(companyCodeId, plantId, languageId, warehouseId, documentNumber.getPreOutboundNo(), documentNumber.getRefDocNumber(), outboundHeader.getReferenceDocumentType(), loginUserId);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("Exception while validating PickupHeader creation");
//            throw e;
//        }
//    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderManagementLine
     * @param loginUserId
     * @throws Exception
     */
    private void createPickUpHeaderV3(String companyCodeId, String plantId, String languageId, String warehouseId, String pickupNumber,
                                      String preOutboundNo, String refDocNumber, OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {
        preOutboundNo = orderManagementLine.getPreOutboundNo();
        refDocNumber = orderManagementLine.getRefDocNumber();
        PickupHeaderV2 newPickupHeader = new PickupHeaderV2();

//        List<PickupHeaderV2> pickupHeaderV2List = pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndItemCodeAndDeletionIndicator(
//                companyCodeId, plantId,languageId, warehouseId, preOutboundNo, refDocNumber, orderManagementLine.getItemCode(), 0L);
//        log.info("PickupHeader Deleted -------pickupHeaderV2List---------> {} ", pickupHeaderV2List);
//        if(!pickupHeaderV2List.isEmpty()) {
//            log.info("PickupHeader Deleted Successfully ----------------> Size is -----> {} ", pickupHeaderV2List.size());
//            pickupHeaderV2Repository.deleteAll(pickupHeaderV2List);
//        }

        BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
//            newPickupHeader.setAssignedPickerId(assignPickerId);
        newPickupHeader.setPickupNumber(pickupNumber);
        newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
        // PICK_UOM
        newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

        // STATUS_ID
        newPickupHeader.setStatusId(48L);
        statusDescription = getStatusDescription(48L, languageId);
        newPickupHeader.setStatusDescription(statusDescription);

        newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
        if (newPickupHeader.getCompanyDescription() == null) {
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            newPickupHeader.setCompanyDescription(description.getCompanyDesc());
            newPickupHeader.setPlantDescription(description.getPlantDesc());
            newPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        newPickupHeader.setDeletionIndicator(0L);
        newPickupHeader.setPickupCreatedBy(loginUserId);
        newPickupHeader.setPickUpdatedBy(loginUserId);
        newPickupHeader.setPickupCreatedOn(new Date());
        newPickupHeader.setPickUpdatedOn(new Date());

        // Order_Text_Update
        String text = "PickupHeader Created";
        outboundOrderV2Repository.updateOutboundHeaderText(newPickupHeader.getOutboundOrderTypeId(), newPickupHeader.getRefDocNumber(), text);
        log.info("PickupHeader Status Updated Successfully");

        PickupHeaderV2 createdPickupHeader = pickupHeaderV2Repository.save(newPickupHeader);
        log.info("pickupHeader created: " + createdPickupHeader);

        orderManagementLineV2Repository.updateOrderManagementLineV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, pickupNumber,
//                    assignPickerId,
                orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(), 48L, statusDescription, new Date());

        outboundLineV2Repository.updateOutboundLineStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
                48L, statusDescription, orderManagementLine.getLineNumber(), orderManagementLine.getItemCode());
//                    , assignPickerId);

        // OutboundHeader Update
        outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
        log.info("outboundHeader updated");

        // ORDERMANAGEMENTHEADER Update
        orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
        log.info("orderManagementHeader updated");

        // PreOutboundHeader Update for PU_NO
        preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusId(companyCodeId, plantId, languageId, warehouseId, refDocNumber, pickupNumber, 48L, statusDescription);
        log.info("PreOutboundHeader Updated PickupNo");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderManagementLine
     * @param loginUserId
     * @throws Exception
     */
    private PickupHeaderV2 createPickUpHeaderV4(String companyCodeId, String plantId, String languageId, String warehouseId, String pickupNumber,
                                      String preOutboundNo, String refDocNumber, OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {

        preOutboundNo = orderManagementLine.getPreOutboundNo();
        refDocNumber = orderManagementLine.getRefDocNumber();
        PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
        BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));

//            newPickupHeader.setAssignedPickerId(assignPickerId);
        newPickupHeader.setPickupNumber(pickupNumber);
        newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
        // PICK_UOM
        newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

        // STATUS_ID
        newPickupHeader.setStatusId(48L);
        statusDescription = getStatusDescription(48L, languageId);
        newPickupHeader.setStatusDescription(statusDescription);

        newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
        if (newPickupHeader.getCompanyDescription() == null) {
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            newPickupHeader.setCompanyDescription(description.getCompanyDesc());
            newPickupHeader.setPlantDescription(description.getPlantDesc());
            newPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        newPickupHeader.setDeletionIndicator(0L);
        newPickupHeader.setPickupCreatedBy(loginUserId);
        newPickupHeader.setPickUpdatedBy(loginUserId);
        newPickupHeader.setPickupCreatedOn(new Date());
        newPickupHeader.setPickUpdatedOn(new Date());

        try {

            // Order_Text_Update
            String text = "PickupHeader Created";
            outboundOrderV2Repository.updateOutboundHeaderText(newPickupHeader.getOutboundOrderTypeId(), newPickupHeader.getRefDocNumber(), text);
            log.info("PickupHeader Status Updated Successfully -----------------> " + newPickupHeader.getRefDocNumber());

            outboundLineV2Repository.updateOutboundLineStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
                    48L, statusDescription, orderManagementLine.getLineNumber(), orderManagementLine.getItemCode());

            // OutboundHeader Update
            outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L, statusDescription);
            log.info("outboundHeader updated {} ", newPickupHeader.getRefDocNumber());

            // PreOutboundHeader Update for PU_NO
            preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusId(companyCodeId, plantId, languageId, warehouseId, refDocNumber, pickupNumber, 48L, statusDescription);
            log.info("PreOutboundHeader Updated PickupNo {} -------> RefDocNo is -------> {} ", pickupNumber, newPickupHeader.getRefDocNumber());
        } catch (Exception e) {
            log.error("Error creating PickupHeader for RefDocNo {}: {}", refDocNumber, e.getMessage(), e);
            return null; // avoid breaking entire loop
        }
        return newPickupHeader;
        
    }



    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderTypeId
     * @param outboundOrderProcess
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 postOutboundOrder(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String preOutboundNo, String refDocNumber, Long orderTypeId,
                                              OutboundOrderProcess outboundOrderProcess) throws Exception {
        try {
            log.info("Outbound Order Process save Initiated...! " + refDocNumber);
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, orderTypeId);
            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
            if (!isDuplicateOrder) {
                if (outboundOrderProcess.getPreOutboundHeader() != null) {
                    log.info("PreOutboundHeader Deleted Successfully " + outboundOrderProcess.getPreOutboundHeader());
                    preOutboundHeaderV2Repository.delete(outboundOrderProcess.getPreOutboundHeader());
                    preOutboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getPreOutboundHeader());
                }
                if (outboundOrderProcess.getOutboundHeader() != null) {
                    log.info("OutboundHeader Deleted Successfully " + outboundOrderProcess.getOutboundHeader());
                    outboundHeaderV2Repository.delete(outboundOrderProcess.getOutboundHeader());
                    outboundHeader = outboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOutboundHeader());
                }
//                if (outboundOrderProcess.getOrderManagementHeader() != null) {
//                    orderManagementHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOrderManagementHeader());
//                }
                if (outboundOrderProcess.getPreOutboundLines() != null && !outboundOrderProcess.getPreOutboundLines().isEmpty()) {
                    preOutboundLineV2Repository.deleteAll(outboundOrderProcess.getPreOutboundLines());
                    log.info("OutboundLines Deleted Successfully");
                    preOutboundLineV2Repository.saveAll(outboundOrderProcess.getPreOutboundLines());
                }
                if (outboundOrderProcess.getOutboundLines() != null && !outboundOrderProcess.getOutboundLines().isEmpty()) {
                    outboundLineV2Repository.deleteAll(outboundOrderProcess.getOutboundLines());
                    log.info("OutboundLines Deleted Successfully");
                    outboundLineV2Repository.saveAll(outboundOrderProcess.getOutboundLines());
                }
//                if(outboundOrderProcess.getOrderManagementLines() != null && !outboundOrderProcess.getOrderManagementLines().isEmpty()) {
//                    orderManagementLineV2Repository.saveAll(outboundOrderProcess.getOrderManagementLines());
//                }
            }
            log.info("Outbound Order Save Process Completed ------> " + refDocNumber);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            return outboundHeader;
        } catch (Exception e) {
            log.info("Rollback Initiated...!" + refDocNumber);
            e.printStackTrace();
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
     * @param orderTypeId
     * @param outboundOrderProcess
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 postOutboundOrderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String preOutboundNo, String refDocNumber, Long orderTypeId,
                                              OutboundOrderProcess outboundOrderProcess) throws Exception {
        try {
            log.info("Outbound Order Process save Initiated...! " + refDocNumber);
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, orderTypeId);
            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
            if(!isDuplicateOrder) {
                if (outboundOrderProcess.getPreOutboundHeader() != null) {
                    preOutboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getPreOutboundHeader());
                }
                if(outboundOrderProcess.getOutboundHeader() != null) {
                    outboundHeader = outboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOutboundHeader());
                }
                if(outboundOrderProcess.getOrderManagementHeader() != null) {
                    orderManagementHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOrderManagementHeader());
                }
                if (outboundOrderProcess.getPreOutboundLines() != null && !outboundOrderProcess.getPreOutboundLines().isEmpty()) {
                    preOutboundLineV2Repository.saveAll(outboundOrderProcess.getPreOutboundLines());
                }
                if(outboundOrderProcess.getOutboundLines() != null && !outboundOrderProcess.getOutboundLines().isEmpty()) {
                    outboundLineV2Repository.saveAll(outboundOrderProcess.getOutboundLines());
                }
//                if(outboundOrderProcess.getOrderManagementLines() != null && !outboundOrderProcess.getOrderManagementLines().isEmpty()) {
//                    orderManagementLineV2Repository.saveAll(outboundOrderProcess.getOrderManagementLines());
//                }
            }
            log.info("Outbound Order Save Process Completed ------> " + refDocNumber);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Rollback Initiated...!" + refDocNumber);
//            rollback(outboundOrderProcess.getOutboundIntegrationHeader());
//            orderService.updateProcessedOrderV2(refDocNumber, orderTypeId);

            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param orderTypeId
     * @param outboundOrderProcess
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 postOutboundOrderV3(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String preOutboundNo, String refDocNumber, Long orderTypeId,
                                                OutboundOrderProcess outboundOrderProcess) throws Exception {
        try {
            log.info("Outbound Order Process save Initiated...! " + refDocNumber);
            log.info("------outboundOrderProcess.getOutboundLines()-------> " + outboundOrderProcess.getOutboundLines());

            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            boolean isDuplicateOrder = preOutboundHeaderService.isPreOutboundHeaderExist(refDocNumber, orderTypeId);
            log.info("IsDupicate : " + refDocNumber + " |---> " + isDuplicateOrder);
            if (!isDuplicateOrder) {
                if (outboundOrderProcess.getPreOutboundHeader() != null) {
                    preOutboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getPreOutboundHeader());
                }

                if (outboundOrderProcess.getPreOutboundLines() != null
                        && !outboundOrderProcess.getPreOutboundLines().isEmpty()) {
                    preOutboundLineV2Repository.saveAll(outboundOrderProcess.getPreOutboundLines());
                }

                if (outboundOrderProcess.getOutboundHeader() != null) {
                    outboundHeaderV2Repository.saveAndFlush(outboundOrderProcess.getOutboundHeader());
                }

                if (outboundOrderProcess.getOutboundLines() != null && !outboundOrderProcess.getOutboundLines().isEmpty()) {
                    outboundLineV2Repository.saveAll(outboundOrderProcess.getOutboundLines());
                }
            }
            log.info("Outbound Order Save Process Completed ------> " + refDocNumber);
            return outboundHeader;
        } catch (Exception e) {
            log.info("Rollback Initiated...!" + refDocNumber);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param refDocType
     * @param loginUserId
     */
    private void fireBaseNotification(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String preOutboundNo, String refDocNumber, String refDocType, String loginUserId) {
        try {
            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(companyCodeId, plantId, languageId, warehouseId);
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String title = "PICKING";
                String message = refDocType + " ORDER - " + refDocNumber + " - IS RECEIVED";
                NotificationSave notificationInput = new NotificationSave();
                notificationInput.setUserId(Collections.singletonList(loginUserId));
                notificationInput.setUserType(null);
                notificationInput.setMessage(message);
                notificationInput.setTopic(title);
                notificationInput.setReferenceNumber(refDocNumber);
                notificationInput.setDocumentNumber(preOutboundNo);
                notificationInput.setCompanyCodeId(companyCodeId);
                notificationInput.setPlantId(plantId);
                notificationInput.setLanguageId(languageId);
                notificationInput.setWarehouseId(warehouseId);
                notificationInput.setCreatedOn(new Date());
                notificationInput.setCreatedBy(loginUserId);
                String response = pushNotificationService.sendPushNotification(deviceToken, notificationInput);
                if (response.equals("OK")) {
                    pickupHeaderV2Repository.updateNotificationStatus(refDocNumber, warehouseId);
                    log.info("status update successfully");
                }
            }
        } catch (Exception e) {
            log.error("Outbound fireBase notification error " + e.toString());
        }
    }


    @Transactional
    public OutboundHeaderV2 fullfillOutboundReceivedV5(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        try {
            companyCodeId = outboundIntegrationHeader.getCompanyCode();
            plantId = outboundIntegrationHeader.getBranchCode();
            languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            warehouseId = outboundIntegrationHeader.getWarehouseID();
            refDocNumber = outboundIntegrationHeader.getRefDocumentNo();
            Long statusId = 39L;
            statusDescription = getStatusDescription(statusId, languageId);

            log.info("Outbound process fullfill Initiated ------> : {}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            WK = "SAP-ORDER FULLFILLMENT";

            // ---------------PreOutboundHeader-----------------------------------------------------------------------------------
            log.info("preOutboundHeader inpouts : {},{},{},{},{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            PreOutboundHeaderV2 preOutbound = preOutboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
            if (preOutbound == null) {
                throw new BadRequestException("PreOutboundHeader These Values Doesn't Exist");
            }
            // OutboundHeader
            OutboundHeaderV2 outboundHeader = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutbound.getPreOutboundNo(), 0L);

            // OrderManagementHeader
            OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutbound.getPreOutboundNo(), 0L);

            // PreOutboundHeaderV2 Insert
            PreOutboundHeaderV2 newPreOutbound = new PreOutboundHeaderV2();
            BeanUtils.copyProperties(preOutbound, newPreOutbound, CommonUtils.getNullPropertyNames(preOutbound));
            BeanUtils.copyProperties(outboundIntegrationHeader, newPreOutbound, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
            preOutboundHeaderV2Repository.delete(preOutbound);
            log.info("----------PreOutboundHeaderV2------delete-----> : " + preOutbound);

            preOutboundHeaderV2Repository.save(newPreOutbound);
            log.info("----------PreOutboundHeaderV2------newly-created-----> : " + newPreOutbound);

            // OutboundHeaderV2 Insert
            OutboundHeaderV2 newOutboundHeader = new OutboundHeaderV2 ();
            BeanUtils.copyProperties(newPreOutbound, newOutboundHeader, CommonUtils.getNullPropertyNames(newPreOutbound));
            if (outboundHeader.getPartnerCode() == null) {
            	newOutboundHeader.setPartnerCode("STO");
            }
            outboundHeaderV2Repository.delete(outboundHeader);
            log.info("----------OutboundHeaderV2------delete-----> : " + outboundHeader);

            outboundHeaderV2Repository.save(newOutboundHeader);
            log.info("----------OutboundHeaderV2------newly-created-----> : " + newOutboundHeader);

            // OrderManagementHeaderV2 Insert
            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2 ();
            BeanUtils.copyProperties(newPreOutbound, newOrderManagementHeader, CommonUtils.getNullPropertyNames(newPreOutbound));
            statusId = 41L;
            statusDescription = getStatusDescription(statusId, languageId);
            newOrderManagementHeader.setStatusId(statusId);
            newOrderManagementHeader.setStatusDescription(statusDescription);
            orderManagementHeaderV2Repository.delete(orderManagementHeader);
            log.info("----------OrderManagementHeaderV2------delete-----> : " + orderManagementHeader);
            
            /*
             * Delete existing order mgmt lines
             */
            List<OrderManagementLineV2> orderManagementLineV2s = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                  companyCodeId, plantId, languageId, warehouseId, newOrderManagementHeader.getPreOutboundNo(), newOrderManagementHeader.getRefDocNumber(), 0L);
	        if(!orderManagementLineV2s.isEmpty()) {
	        	orderManagementLineV2Repository.deleteAll(orderManagementLineV2s);
	        	log.info("OrderManagementLine is Deleted Successfully ----------------------> Size is ----> {} ", orderManagementLineV2s.size());
	        }

            orderManagementHeaderV2Repository.save(newOrderManagementHeader);
            log.info("----------OrderManagementHeaderV2------newly-created-----> : " + newOrderManagementHeader);

            
            /*
             * Delete existing Pickup header
             */
            List<PickupHeaderV2> pickupHeaderV2List = pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                    companyCodeId, plantId,languageId, warehouseId, refDocNumber, newOrderManagementHeader.getPreOutboundNo(), 0L);
            log.info("PickupHeader Deleted -------pickupHeaderV2List---------> {} ", pickupHeaderV2List);
            if(!pickupHeaderV2List.isEmpty()) {
                pickupHeaderV2Repository.deleteAll(pickupHeaderV2List);
                log.info("PickupHeader Deleted Successfully ----------------> Size is -----> {} ", pickupHeaderV2List.size());
            }
            
            // Lines
            List<PreOutboundLineV2> preOutboundLine = preOutboundLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                    languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutbound.getPreOutboundNo(), 0L);
            if(preOutboundLine.isEmpty()) {
                throw new BadRequestException("PreInboundLine Doesn't Values");
            }
            
            List<PreOutboundLineV2> preOutboundLineV2s = new ArrayList<>();
            for(PreOutboundLineV2 preOutboundLineV2 : preOutboundLine ) {
                for(OutboundIntegrationLineV2 line : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                    if(preOutboundLineV2.getRefDocNumber().equals(line.getPickListNo()) &&
                            preOutboundLineV2.getLineNumber().equals(line.getLineReference())) {
                    	PreOutboundLineV2 newPreOutboundLineV2 = new PreOutboundLineV2 ();
                        BeanUtils.copyProperties(preOutboundLineV2, newPreOutboundLineV2, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                        BeanUtils.copyProperties(line, newPreOutboundLineV2, CommonUtils.getNullPropertyNames(line));
                        newPreOutboundLineV2.setOrderQty(line.getOrderedQty());
                        preOutboundLineV2s.add(newPreOutboundLineV2);
                    }
                }
            }
            preOutboundLineV2Repository.deleteAll(preOutboundLine);
            preOutboundLineV2Repository.saveAll(preOutboundLineV2s);


            // OrderManagementLine - This flow is from SAP Order update, so not from direct API calling
            boolean fromOrderFullfillment = true;

            createOrderManagementLine(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader,
                    preOutboundLineV2s, WK, fromOrderFullfillment);

            validatePickupHeaderCreation(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutbound.getPreOutboundNo(),
                    newOutboundHeader, WK);
            preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber, 48L, "IN PICKING");
            return newOutboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
            throw e;
        }
    }


    /**
     *
     * @param outboundIntegrationHeaderV2 orderReprocess
     */
    public void outboundOrderProcessCreation(OutboundIntegrationHeaderV2 outboundIntegrationHeaderV2) {

        String refDocNo = outboundIntegrationHeaderV2.getRefDocumentNo();
        String companyId = outboundIntegrationHeaderV2.getCompanyCode();
        String plantId = outboundIntegrationHeaderV2.getBranchCode();
        String warehouseId = outboundIntegrationHeaderV2.getWarehouseID();

        log.info("Outbound Re-OrderProcessing CompanyId - {}, PlantId - {}, WarehouseId - {}, RefDocNo - {} ", companyId, plantId, warehouseId, refDocNo);

        // PreOutboundHeader OldOrder Delete Process
        preOutboundHeaderV2Repository.deletePreOutboundHeader(companyId, plantId,warehouseId, refDocNo);
        log.info("PreOutboundHeader Deleted Successfully --> Order No is {} ", refDocNo);

        // PreOutboundHederLine OldOrder Delete Process
        preOutboundLineV2Repository.deletePreOutboundLine(companyId, plantId, warehouseId, refDocNo);
        log.info("PreOutboundLine Deleted Successfully --> Order No is {} ", refDocNo);

        // OutboundHeader OldOrder Delete Process
        outboundHeaderV2Repository.deleteOutboundHeader(companyId, plantId, warehouseId, refDocNo);
        log.info("OutboundHeader Deleted Successfully --> Order No is {} ", refDocNo);

        // OutboundLine OldOrder Delete Process
        outboundLineV2Repository.deleteOutboundLine(companyId, plantId, warehouseId, refDocNo);
        log.info("OutboundLine Deleted Successfully --> Order No is {} ", refDocNo);

    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    private void createOrderManagementLineFullfillment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       OutboundIntegrationHeaderV2 outboundIntegrationHeader, List<PreOutboundLineV2> preOutboundLineList,
                                                       String loginUserId, boolean fromOrderFullfillment) throws Exception {
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                log.error("--------1-------fromOrderFullfillment----> " + fromOrderFullfillment);
                OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
                BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));

                if (preOutboundLine.getCustomerId() == null) {
                    orderManagementLine.setPartnerCode("STO");
                }
                log.info("orderManagementLine : " + orderManagementLine);

                Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
                List<Long> BIN_CLASS_ID  = new ArrayList<>();

                if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
                    if (preOutboundLine.getMtoNumber() != null && !preOutboundLine.getMtoNumber().isEmpty()) {
                        log.info("MTO Number is ------------------> {} ", preOutboundLine.getMtoNumber());
                        BIN_CLASS_ID.add(10L);
                    } else {
                        BIN_CLASS_ID.add(1L);
                        BIN_CLASS_ID.add(10L);
                        BIN_CLASS_ID.add(3L);
                    }

                    OrderManagementLineV2 orderManagementLineV2 = orderManagementLineService.updateAllocationOrderFullfillment(companyCodeId, plantId, languageId, warehouseId, preOutboundLine.getItemCode(),
                            orderManagementLine.getManufacturerName(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId, fromOrderFullfillment);
                    log.info("OrderFullfillment method OrderManagement Line Created Successfully ----------> OrderId is {}", orderManagementLineV2.getRefDocNumber());
                }
                if (OB_ORD_TYP_ID == 2L) {
                    BIN_CLASS_ID.add(7L);
                    OrderManagementLineV2 orderManagement = orderManagementLineService.updateAllocationOrderFullfillment(companyCodeId, plantId, languageId, warehouseId, preOutboundLine.getItemCode(),
                            orderManagementLine.getManufacturerName(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId, fromOrderFullfillment);
                    log.info("OrderFullfillment method OrderManagement Line Created Successfully ----------> OrderId is {}", orderManagement.getRefDocNumber());
                }
            }
        } catch (Exception e) {
            log.error("Exception While OrderManagementLine create: " + e);
            throw e;
        }
    }

}