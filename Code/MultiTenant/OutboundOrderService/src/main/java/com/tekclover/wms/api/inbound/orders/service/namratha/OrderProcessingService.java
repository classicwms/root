package com.tekclover.wms.api.inbound.orders.service.namratha;

import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.dto.BomLine;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.service.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class OrderProcessingService extends BaseService {

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    MastersService mastersService;
    @Autowired
    OrderService orderService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    OrderManagementLineService orderManagementLineService;
    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    protected static final Long OB_PL_ORD_TYP_ID = 3L;          //Production Order creation
    protected static final Long OB_IPL_ORD_TYP_ID_SFG = 5L;          //Production Order create - semi-finished goods Picklist
    protected static final Long OB_IPL_ORD_TYP_ID_FG = 6L;          //Production Order create-finished goods picklist


    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 processOutboundReceivedV4(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            /*
             * Checking whether received refDocNumber processed already.
             */
            log.info("Outbound Process Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + ", " + outboundIntegrationHeader.getOutboundOrderTypeID());
            if (outboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = outboundIntegrationHeader.getLoginUserId();
            }

            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();

            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            refDocNumber, outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);

            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() + " already processed. Reprocessing can't be allowed.");
            }

            String masterAuthToken = getMasterAuthToken();

            if (warehouseId == null) {
                try {
                    Optional<Warehouse> warehouse =
                            warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                    outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), LANG_ID, 0L);
                    log.info("warehouse : " + warehouse);
                    if (warehouse != null && !warehouse.isEmpty()) {
                        log.info("warehouse : " + warehouse.get().getWarehouseId());
                        warehouseId = warehouse.get().getWarehouseId();
                    } else {
                        log.info("warehouse not found.");
                        throw new BadRequestException("Warehouse cannot be null.");
                    }
                } catch (Exception e) {
                    log.error("Warehouse fetch exception : " + e.toString());
                    throw e;
                }
            }

            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4 ||
                    (outboundIntegrationHeader.getReferenceDocumentType() != null && outboundIntegrationHeader.getReferenceDocumentType().equalsIgnoreCase("Sales Invoice"))) {
                OutboundHeaderV2 updateOutboundHeaderAndLine = updateOutboundHeaderForSalesInvoice(outboundIntegrationHeader, warehouseId);
                log.info("SalesInvoice Updated in OutboundHeader and Line");
                if (updateOutboundHeaderAndLine == null) {
                    updateOutboundHeaderAndLine = new OutboundHeaderV2();
                }
                return updateOutboundHeaderAndLine;
            }

            // Getting PreOutboundNo from NumberRangeTable
            String preOutboundNo = getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
            String refField1ForOrderType = null;

            /*
             * Append PREOUTBOUNDLINE table through below logic
             */
            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                // PreOutboundLine
                try {
                    //=========================================================================================================//

                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                            preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine, MW_AMS);
                    PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
                    log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
                    createdPreOutboundLineList.add(createdPreOutboundLine);

                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
            }

            createOrderManagementLine(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, createdPreOutboundLineList, MW_AMS);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader, MW_AMS);
            log.info("createOutboundLine created : " + createOutboundLineList);

            /*------------------Insert into PreOutboundHeader table-----------------------------*/
            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                    preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, MW_AMS);
            log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

            /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader, MW_AMS);
            log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

            /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(),
                    outboundIntegrationHeader, MW_AMS);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            // Creating pickupheader number with respect to the incoming orders!!
            createPickupHeaderNo(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundHeader.getRefDocNumber(), outboundIntegrationHeader);

            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
            orderManagementLineService.rollback(outboundIntegrationHeader);
            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());

            throw e;
        }
    }

    /**
     * @param outboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 processOutboundReceivedV7(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            /*
             * Checking whether received refDocNumber processed already.
             */
            log.info("Outbound Process Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + ", " + outboundIntegrationHeader.getOutboundOrderTypeID());
            if (outboundIntegrationHeader.getLoginUserId() != null) {
                MFR_NAME_V7 = outboundIntegrationHeader.getLoginUserId();
            }

            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String refDocNumber = outboundIntegrationHeader.getRefDocumentNo();

            Optional<PreOutboundHeaderV2> orderProcessedStatus =
                    preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                            refDocNumber, outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);

            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() + " already processed. Reprocessing can't be allowed.");
            }

            String masterAuthToken = getMasterAuthToken();

            if (warehouseId == null) {
                try {
                    Optional<Warehouse> warehouse =
                            warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                    outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), LANG_ID, 0L);
                    log.info("warehouse : " + warehouse);
                    if (warehouse != null && !warehouse.isEmpty()) {
                        log.info("warehouse : " + warehouse.get().getWarehouseId());
                        warehouseId = warehouse.get().getWarehouseId();
                    } else {
                        log.info("warehouse not found.");
                        throw new BadRequestException("Warehouse cannot be null.");
                    }
                } catch (Exception e) {
                    log.error("Warehouse fetch exception : " + e.toString());
                    throw e;
                }
            }

            if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4 ||
                    (outboundIntegrationHeader.getReferenceDocumentType() != null && outboundIntegrationHeader.getReferenceDocumentType().equalsIgnoreCase("Sales Invoice"))) {
                OutboundHeaderV2 updateOutboundHeaderAndLine = updateOutboundHeaderForSalesInvoice(outboundIntegrationHeader, warehouseId);
                log.info("SalesInvoice Updated in OutboundHeader and Line");
                if (updateOutboundHeaderAndLine == null) {
                    updateOutboundHeaderAndLine = new OutboundHeaderV2();
                }
                return updateOutboundHeaderAndLine;
            }

            // Getting PreOutboundNo from NumberRangeTable
            String preOutboundNo = getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
            String refField1ForOrderType = null;

            /*
             * Append PREOUTBOUNDLINE table through below logic
             */
            List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
                // PreOutboundLine
                try {
                    //=========================================================================================================//

                    PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                            preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine, MFR_NAME_V7);
                    PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
                    log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
                    createdPreOutboundLineList.add(createdPreOutboundLine);

                } catch (Exception e) {
                    log.error("Error on processing PreOutboundLine : " + e.toString());
                    e.printStackTrace();
                }
            }

            createOrderManagementLineV7(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, createdPreOutboundLineList, MFR_NAME_V7);

            /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader, MFR_NAME_V7);
            log.info("createOutboundLine created : " + createOutboundLineList);

            /*------------------Insert into PreOutboundHeader table-----------------------------*/
            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                    preOutboundNo, outboundIntegrationHeader, refField1ForOrderType, MFR_NAME_V7);
            log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

            /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
            OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader, MFR_NAME_V7);
            log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

            /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
            OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(),
                    outboundIntegrationHeader, MW_AMS);

            //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
            statusDescription = getStatusDescription(47L, languageId);
            orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 47L, statusDescription);
            log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

            // Creating pickupheader number with respect to the incoming orders!!
            //createPickupHeaderNo(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundHeader.getRefDocNumber(), outboundIntegrationHeader);

            return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
//            log.info("Rollback Initiated...!" + outboundIntegrationHeader.getRefDocumentNo());
//            orderManagementLineService.rollback(outboundIntegrationHeader);
//            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID());
            throw e;
        }
    }



    /**
     * @param outboundIntegrationHeader
     * @param warehouseId
     * @return
     */
    public OutboundHeaderV2 updateOutboundHeaderForSalesInvoice(OutboundIntegrationHeaderV2 outboundIntegrationHeader, String warehouseId) throws java.text.ParseException {
        OutboundHeaderV2 dbOutboundHeader = getOutboundHeaderForSalesInvoiceUpdateV2(
                outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), LANG_ID,
                warehouseId, outboundIntegrationHeader.getPickListNumber());
        log.info("OutboundHeader: " + dbOutboundHeader);
        if (dbOutboundHeader != null) {
            BeanUtils.copyProperties(outboundIntegrationHeader, dbOutboundHeader, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
            dbOutboundHeader.setUpdatedOn(new Date());
            outboundHeaderV2Repository.save(dbOutboundHeader);
            log.info("OutboundHeader updated with salesInvoice: " + outboundIntegrationHeader.getSalesInvoiceNumber());

            List<OutboundLineV2> dbOutboundLineList = updateOutboundLineForSalesInvoice(outboundIntegrationHeader,
                    dbOutboundHeader.getPreOutboundNo(), dbOutboundHeader.getRefDocNumber(), warehouseId);
        }
        return dbOutboundHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param dbBomLine
     * @param outboundIntegrationLine
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundLineV2 createPreOutboundLineBOMBasedV2(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, BomLine dbBomLine,
                                                             OutboundIntegrationLineV2 outboundIntegrationLine, String loginUserId) throws ParseException {
//        Warehouse warehouse = getWarehouse(outboundIntegrationHeader.getWarehouseID());
        String warehouseId = outboundIntegrationHeader.getWarehouseID();
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(outboundIntegrationLine, preOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationLine));
        preOutboundLine.setLanguageId(languageId);
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);
        preOutboundLine.setCustomerId(outboundIntegrationHeader.getCustomerId());
        preOutboundLine.setCustomerName(outboundIntegrationHeader.getCustomerName());
        preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
        preOutboundLine.setPreOutboundNo(preOutboundNo);
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());
        preOutboundLine.setItemCode(dbBomLine.getChildItemCode());
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId, dbBomLine.getChildItemCode(), outboundIntegrationLine.getManufacturerName(), 0L);

        if (imBasicData1 != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
            // MFR
            preOutboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
            if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                preOutboundLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, outboundIntegrationHeader.getWarehouseID(), imBasicData1.getItemType()));
            } else {
                preOutboundLine.setItemType(imBasicData1.getItemTypeDescription());
            }
            if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                preOutboundLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, outboundIntegrationHeader.getWarehouseID(), imBasicData1.getItemGroup()));
            } else {
                preOutboundLine.setItemGroup(imBasicData1.getItemGroupDescription());
            }
        }
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
        double orderQuantity = outboundIntegrationLine.getOrderedQty() * dbBomLine.getChildItemQuantity();
        preOutboundLine.setOrderQty(orderQuantity);
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setStatusId(39L);
        description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
        if (description != null) {
            preOutboundLine.setCompanyDescription(description.getCompanyDesc());
            preOutboundLine.setPlantDescription(description.getPlantDesc());
            preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());
        }
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        preOutboundLine.setStatusDescription(statusDescription);

        preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
        preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());
        preOutboundLine.setReferenceField2("BOM");
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy(loginUserId);
        preOutboundLine.setCreatedOn(new Date());
        return preOutboundLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
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
        orderManagementLine.setPickupCreatedBy(loginUserId);
        orderManagementLine.setPickupCreatedOn(new Date());
        orderManagementLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
        orderManagementLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
        orderManagementLine.setIsCompleted(preOutboundLine.getIsCompleted());
        orderManagementLine.setIsCancelled(preOutboundLine.getIsCancelled());
        log.info("orderManagementLine : " + orderManagementLine);
        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
        Long BIN_CLASS_ID;

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L ||
                OB_ORD_TYP_ID.equals(OB_IPL_ORD_TYP_ID_SFG) || OB_ORD_TYP_ID.equals(OB_IPL_ORD_TYP_ID_FG)) {

            log.info("OutboundOrderType is either ---------> 0, 1, 3, 5, 6");
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); //ZB,ZC,ZG,ZT
            BIN_CLASS_ID = 1L;
            orderManagementLine = orderManagementLineService.createOrderManagementV4(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
        }

        if (OB_ORD_TYP_ID == 2L) {
            log.info("OutboundOrderType is ----------> 2");
//            List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
            BIN_CLASS_ID = 7L;
            orderManagementLine = orderManagementLineService.createOrderManagementV4(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
        }

        // PROP_ST_BIN
        return orderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 createOrderManagementLineV7(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                             OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
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
        orderManagementLine.setPickupCreatedBy(loginUserId);
        orderManagementLine.setPickupCreatedOn(new Date());
        orderManagementLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
        orderManagementLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
        orderManagementLine.setIsCompleted(preOutboundLine.getIsCompleted());
        orderManagementLine.setIsCancelled(preOutboundLine.getIsCancelled());
        log.info("orderManagementLine : " + orderManagementLine);
        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
        Long BIN_CLASS_ID;

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L ||
                OB_ORD_TYP_ID.equals(OB_IPL_ORD_TYP_ID_SFG) || OB_ORD_TYP_ID.equals(OB_IPL_ORD_TYP_ID_FG)) {

            log.info("OutboundOrderType is either ---------> 0, 1, 3, 5, 6");
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); //ZB,ZC,ZG,ZT
            BIN_CLASS_ID = 1L;
            orderManagementLine = orderManagementLineService.createOrderManagementV7(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
        }

        if (OB_ORD_TYP_ID == 2L) {
            log.info("OutboundOrderType is ----------> 2");
//            List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
            BIN_CLASS_ID = 7L;
            orderManagementLine = orderManagementLineService.createOrderManagementV7(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), loginUserId);
        }

        // PROP_ST_BIN
        return orderManagementLine;
    }


    /**
     * @param createdPreOutboundLine
     * @return
     */
    public List<OutboundLineV2> createOutboundLineV2(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader, String loginUserId) {
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
                outboundLine.setCreatedBy(loginUserId);
                outboundLine.setCreatedOn(new Date());
                outboundLine.setCompanyDescription(dbOrderManagementLine.getCompanyDescription());
                outboundLine.setPlantDescription(dbOrderManagementLine.getPlantDescription());
                outboundLine.setWarehouseDescription(dbOrderManagementLine.getWarehouseDescription());

                if (preOutboundLine.getStatusId() != null) {
                    statusDescription = getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                    outboundLine.setStatusDescription(statusDescription);
                }

                if (outboundLine.getCompanyDescription() == null) {
                    description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
                    outboundLine.setCompanyDescription(description.getCompanyDesc());
                    outboundLine.setPlantDescription(description.getPlantDesc());
                    outboundLine.setWarehouseDescription(description.getWarehouseDesc());
                }

                outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
                outboundLine.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
                outboundLine.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                outboundLine.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                outboundLine.setSupplierInvoiceNo(preOutboundLine.getSupplierInvoiceNo());
                outboundLine.setTokenNumber(preOutboundLine.getTokenNumber());

                outboundLine.setMiddlewareId(preOutboundLine.getMiddlewareId());
                outboundLine.setMiddlewareHeaderId(preOutboundLine.getMiddlewareHeaderId());
                outboundLine.setMiddlewareTable(preOutboundLine.getMiddlewareTable());
                outboundLine.setSalesInvoiceNumber(preOutboundLine.getSalesInvoiceNumber());
                outboundLine.setManufacturerFullName(preOutboundLine.getManufacturerFullName());
                outboundLine.setManufacturerName(preOutboundLine.getManufacturerName());
                outboundLine.setOutboundOrderTypeId(preOutboundLine.getOutboundOrderTypeId());
                outboundLine.setReferenceDocumentType(preOutboundLine.getReferenceDocumentType());
                outboundLine.setTargetBranchCode(preOutboundLine.getTargetBranchCode());
                outboundLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                if (outboundLine.getOutboundOrderTypeId() == 3L) {
                    outboundLine.setCustomerType("INVOICE");
                }
                if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
                    outboundLine.setCustomerType("TRANSVERSE");
                }

                outboundLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
                outboundLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
                outboundLine.setIsCompleted(preOutboundLine.getIsCompleted());
                outboundLine.setIsCancelled(preOutboundLine.getIsCancelled());

                outboundLines.add(outboundLine);
            }
        }

        outboundLines = outboundLineV2Repository.saveAll(outboundLines);
        log.info("outboundLines created -----2------>: " + outboundLines);
        return outboundLines;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param outboundIntegrationLine
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundLineV2 createPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                     OutboundIntegrationHeaderV2 outboundIntegrationHeader, OutboundIntegrationLineV2 outboundIntegrationLine, String loginUserId) throws ParseException {
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        BeanUtils.copyProperties(outboundIntegrationLine, preOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationLine));
        preOutboundLine.setLanguageId(languageId);
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);
        preOutboundLine.setCustomerId(outboundIntegrationHeader.getCustomerId());
        preOutboundLine.setCustomerName(outboundIntegrationHeader.getCustomerName());
        preOutboundLine.setWarehouseId(warehouseId);
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundLine.setPreOutboundNo(preOutboundNo);
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());
        preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());
        preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
        preOutboundLine.setStatusId(39L);
        preOutboundLine.setStockTypeId(1L);
        preOutboundLine.setSpecialStockIndicatorId(1L);
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        description = getDescription(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId());
        if (description != null) {
            preOutboundLine.setCompanyDescription(description.getCompanyDesc());
            preOutboundLine.setPlantDescription(description.getPlantDesc());
            preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());
        }
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        preOutboundLine.setStatusDescription(statusDescription);
        preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId,
                outboundIntegrationLine.getItemCode().trim(), 0L);
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
            if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                preOutboundLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, outboundIntegrationHeader.getWarehouseID(), imBasicData1.getItemType()));
            } else {
                preOutboundLine.setItemType(imBasicData1.getItemTypeDescription());
            }
            if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                preOutboundLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, outboundIntegrationHeader.getWarehouseID(), imBasicData1.getItemGroup()));
            } else {
                preOutboundLine.setItemGroup(imBasicData1.getItemGroupDescription());
            }

            preOutboundLine.setManufacturerCode(imBasicData1.getManufacturerCode());
            preOutboundLine.setManufacturerName(imBasicData1.getManufacturerName());
            preOutboundLine.setManufacturerFullName(imBasicData1.getManufacturerFullName());
        } else {
            preOutboundLine.setDescription(outboundIntegrationLine.getItemText());
        }
        preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());
        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy(loginUserId);
        preOutboundLine.setCreatedOn(new Date());
        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLineList
     * @param loginUserId
     * @throws Exception
     */
    @Transactional
    public void createOrderManagementLine(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {
        OrderManagementLineV2 orderManagementLine = null;
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                orderManagementLine = createOrderManagementLineV2(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, preOutboundLine, loginUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        log.info("orderManagementLine created---1---> : " + orderManagementLine);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLineList
     * @param loginUserId
     * @throws Exception
     */
    @Transactional
    public void createOrderManagementLineV7(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, List<PreOutboundLineV2> preOutboundLineList, String loginUserId) throws Exception {
        OrderManagementLineV2 orderManagementLine = null;
        try {
            for (PreOutboundLineV2 preOutboundLine : preOutboundLineList) {
                orderManagementLine = createOrderManagementLineV7(companyCodeId, plantId, languageId, preOutboundNo, outboundIntegrationHeader, preOutboundLine, loginUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        log.info("orderManagementLine created---1---> : " + orderManagementLine);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param refField1ForOrderType
     * @param loginUserId
     * @return
     * @throws ParseException
     */
    public PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                         OutboundIntegrationHeaderV2 outboundIntegrationHeader, String refField1ForOrderType, String loginUserId) throws ParseException {
//        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        BeanUtils.copyProperties(outboundIntegrationHeader, preOutboundHeader, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundHeader.setConsignment(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());    // Hardcoded value "0"
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        preOutboundHeader.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        preOutboundHeader.setCustomerId(outboundIntegrationHeader.getCustomerId());
        preOutboundHeader.setCustomerName(outboundIntegrationHeader.getCustomerName());

        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);

        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
        if (description != null) {
            preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
            preOutboundHeader.setPlantDescription(description.getPlantDesc());
            preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        // Status Description
//		StatusId idStatus = idmasterService.getStatus(39L, outboundIntegrationHeader.getWarehouseID(), authTokenForIDService.getAccess_token());
        statusDescription = getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        // REF_FIELD_10
        preOutboundHeader.setReferenceField10(statusDescription);
        preOutboundHeader.setStatusDescription(statusDescription);
        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy(loginUserId);
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderV2Repository.save(preOutboundHeader);
        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
        return createdPreOutboundHeader;
    }

    /**
     * @param createdPreOutboundHeader
     * @return
     */
    public OrderManagementHeaderV2 createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, String loginUserId) {
        OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"
        statusDescription = getStatusDescription(41L, createdPreOutboundHeader.getLanguageId());
        newOrderManagementHeader.setStatusDescription(statusDescription);
        newOrderManagementHeader.setReferenceField7(statusDescription);
        newOrderManagementHeader.setPickupCreatedBy(loginUserId);
        newOrderManagementHeader.setPickupCreatedOn(new Date());
        return orderManagementHeaderV2Repository.save(newOrderManagementHeader);
    }

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @return
     * @throws ParseException
     */
    public OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, OutboundIntegrationHeaderV2 outboundIntegrationHeader, String loginUserId) throws ParseException {

        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        outboundHeader.setRefDocNumber(outboundHeader.getRefDocNumber());
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);
        statusDescription = getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        outboundHeader.setConsignment(outboundIntegrationHeader.getPickListNumber());
        outboundHeader.setInvoiceNumber(outboundIntegrationHeader.getInvoice());
        if (outboundHeader.getOutboundOrderTypeId() == 3L) {
            outboundHeader.setCustomerType("INVOICE");
        }
        if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
            outboundHeader.setCustomerType("TRANSVERSE");
        }
        outboundHeader.setCreatedBy(loginUserId);
        outboundHeader.setCreatedOn(new Date());
        outboundHeader = outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader);
        return outboundHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param outboundIntegrationHeader
     * @throws Exception
     */
    public void createPickupHeaderNo(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                     String refDocNumber, OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {

        List<OrderManagementLineV2> orderManagementLines = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorAndStatusIdNot(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L, 47L);

        log.info("OrderManagementList for PickupHeader -------------> {}", orderManagementLines.size());

        long NUM_RAN_CODE = 10;
        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
        log.info("----------New PU_NO--------> : " + PU_NO);

        if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {
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

                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
                PickupHeaderV2 createdPickupHeader = orderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLineV2Repository.updateOrderManagementLineV6(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());
            }

            outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
            orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param pickListNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderForSalesInvoiceUpdateV2(String companyCodeId, String plantId, String languageId,
                                                                     String warehouseId, String pickListNumber) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPickListNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, pickListNumber, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        } else {
            return null;
        }
    }

    /**
     * @param outboundIntegrationHeader
     * @param preOutboundNo
     * @param refDocNumber
     * @param warehouseId
     * @return
     * @throws java.text.ParseException
     */
    public List<OutboundLineV2> updateOutboundLineForSalesInvoice(OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                                  String preOutboundNo, String refDocNumber, String warehouseId) throws java.text.ParseException {

        List<OutboundLineV2> dbOutboundLineList = getOutboundLineV2(outboundIntegrationHeader.getCompanyCode(),
                outboundIntegrationHeader.getBranchCode(), LANG_ID, warehouseId, preOutboundNo, refDocNumber);
        if (dbOutboundLineList != null) {
            List<OutboundLineV2> updateOutboundLineList = new ArrayList<>();
            for (OutboundLineV2 dbOutboundLine : dbOutboundLineList) {
                BeanUtils.copyProperties(outboundIntegrationHeader, dbOutboundLine, CommonUtils.getNullPropertyNames(outboundIntegrationHeader));
                dbOutboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
                dbOutboundLine.setUpdatedOn(new Date());
                updateOutboundLineList.add(dbOutboundLine);
            }
            outboundLineV2Repository.saveAll(updateOutboundLineList);
        }
        return dbOutboundLineList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String preOutboundNo, String refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (!outboundLine.isEmpty()) {
            return outboundLine;
        } else {
            return null;
        }
    }
}
