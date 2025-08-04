package com.tekclover.wms.api.transaction.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.DocumentNumber;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundOrderProcess;
import com.tekclover.wms.api.transaction.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderV2Repository;
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

    //========================================================================V2====================================================================

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

            createOrderManagementLine(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, createdPreOutboundLineList, MW_AMS);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
            log.info("createOutboundLine created : " + createOutboundLineList);

            statusId = 41L;
            statusDescription = getStatusDescription(statusId, languageId);
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader, statusId, statusDescription, MW_AMS);
            log.info("OrderMangementHeader Created : {}", createdOrderManagementHeader);

            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, outboundIntegrationHeader, statusId, statusDescription);
            log.info("outboundHeader Created : {}", outboundHeader);

            outboundOrderProcess.setOutboundHeader(outboundHeader);
            outboundOrderProcess.setOutboundLines(createOutboundLineList);
            outboundOrderProcess.setOrderManagementHeader(createdOrderManagementHeader);
            outboundOrderProcess.setPreOutboundHeader(createdPreOutboundHeader);
            outboundOrderProcess.setPreOutboundLines(createdPreOutboundLineList);
            outboundOrderProcess.setOutboundIntegrationHeader(outboundIntegrationHeader);
            postOutboundOrder(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId, outboundOrderProcess);

            validatePickupHeaderCreation(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, outboundHeader, MW_AMS);

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
     *
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
    private String getItemDescription (String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName) {
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
            outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());

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
                                                      Long statusId, String statusDesc, IKeyValuePair desc,  String loginUserId) throws Exception {
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

    @Transactional
    public void createOrderManagementLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                          List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                createOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundLine, loginUserId);
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
    private void createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
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
                createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
                        preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, warehouseId,
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
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @return
     */
    private void createOrderManagementV2(String companyCodeId, String plantId, String languageId,
                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
        String manufacturerName = orderManagementLine.getManufacturerName();
//        List<IInventoryImpl> stockType1InventoryList = inventoryService.
//                getInventoryForOrderManagementV3(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, manufacturerName);
//        log.info("Walkaroo---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
//        if (stockType1InventoryList.isEmpty()) {
//            return createEMPTYOrderManagementLineV2(orderManagementLine);
//        }

        orderManagementLineService.updateAllocationV3(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, ORD_QTY, orderManagementLine, loginUserId);
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

            if(orderManagementLine.getCompanyDescription() == null) {
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
            if (orderManagementLineV2List != null && !orderManagementLineV2List.isEmpty()) {
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
                    String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
                    String storageBin = dbOrderManagementLine.getProposedStorageBin();
                    InventoryV2 inventory =
                            inventoryService.getOutboundInventoryV3(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                    dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getItemCode(), dbOrderManagementLine.getManufacturerName(),
                                    dbOrderManagementLine.getBarcodeId(), storageBin);
                    double[] inventoryQty = calculateInventoryUnAllocate(dbOrderManagementLine.getAllocatedQty(), inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                    if (inventoryQty != null && inventoryQty.length > 2) {
                        inventory.setInventoryQuantity(inventoryQty[0]);
                        inventory.setAllocatedQuantity(inventoryQty[1]);
                        inventory.setReferenceField4(inventoryQty[2]);
                    }

                    // Create new Inventory Record
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
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
                for (OrderManagementLineV2 createdOrderManagementLine : sortedOrderManagementLineList) {
                    if (createdOrderManagementLine.getOutboundOrderTypeId() == 3) {
                        if (i <= totalCases) {
                            createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, 
                            		refDocNumber, createdOrderManagementLine, loginUserId);
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
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                for (OrderManagementLineV2 orderManagementLine : orderManagementLineList) {
                    if(orderManagementLine.getOutboundOrderTypeId() == 3) {
                        createPickUpHeaderV3(companyCodeId, plantId, languageId, warehouseId, PU_NO, preOutboundNo, refDocNumber, orderManagementLine, loginUserId);

//                        DocumentNumber documentNumber = new DocumentNumber();
//                        documentNumber.setRefDocNumber(orderManagementLine.getRefDocNumber());
//                        documentNumber.setPreOutboundNo(orderManagementLine.getPreOutboundNo());
//                        documentNumberList.add(documentNumber);
                    }
                }
            }
            return documentNumberList;

        } catch (Exception e) {
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
    public OutboundHeaderV2 postOutboundOrder(String companyCodeId, String plantId, String languageId, String warehouseId,
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

}