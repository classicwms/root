package com.tekclover.wms.api.inbound.transaction.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.dto.BomLine;
import com.tekclover.wms.api.inbound.transaction.model.dto.HHTUser;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.enterprise.Warehouse;
import com.tekclover.wms.api.inbound.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderProcess;
import com.tekclover.wms.api.inbound.transaction.repository.GrHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.GrLineV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundIntegrationLogRepository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundOrderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PreInboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PreOutboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PutAwayHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.StagingHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InboundOrderProcessingService extends BaseService {

    @Autowired
    MastersService mastersService;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    GrLineService grLineService;

    @Autowired
    EnterpriseSetupService enterpriseSetupService;

    @Autowired
    InboundHeaderService inboundHeaderService;

    @Autowired
    OrderProcessingService orderProcessingService;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    OrderService orderService;
    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    //========================================================================V2====================================================================

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public synchronized InboundHeaderV2 processInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
        log.info("----------processInboundReceivedV3------------");
        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("Inbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);

        try {
            MW_AMS = inboundIntegrationHeader.getLoginUserId() != null ? inboundIntegrationHeader.getLoginUserId() : MW_AMS;

            //Checking whether received refDocNumber processed already.
            boolean orderProcessedStatus = preInboundHeaderV2Repository.existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            log.error("IsDupicate : " + refDocNumber + " |---> " + orderProcessedStatus);
            if (orderProcessedStatus) {
                return new InboundHeaderV2();
            }

            String idMasterAuthToken = getIDMasterAuthToken();
            String masterAuthToken = getMasterAuthToken();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : {}|{}|{}|{}", preInboundNo, stagingNo, caseCode, grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            inboundOrderProcess = createBOMPreInboundLines(companyCodeId, plantId, languageId, warehouseId, preInboundNo, description, masterAuthToken, statusId, statusDescription, inboundIntegrationHeader, MW_AMS);

            List<InboundIntegrationLine> inboundIntegrationLines = inboundOrderProcess.getInboundIntegrationLines();
            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = inboundOrderProcess.getPreInboundLines();

            //Append PREINBOUNDLINE table through below logic
            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
                try {
                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
                            description, statusId, statusDescription));
                } catch (Exception e) {
                    throw new BadRequestException("Exception While PreInboundLine Create" + e);
                }
            });
            log.info("toBeCreatedPreInboundLineList [API] : {}", finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }

            //Header
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo, inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);

            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, (long) overallCreatedPreInboundLineList.size());

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);

            statusDescription = getStatusDescription(17L, languageId);
            String grStatusDes = getStatusDescription(16L,languageId);
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, 16L, grStatusDes);

            //Lines
            List<InboundLineV2> inboundLines = createInboundLines(17L, statusDescription, overallCreatedPreInboundLineList);
            String stagingStatusDes = getStatusDescription(14L,languageId);
            List<StagingLineEntityV2> stagingLines = createStagingLines(stagingNo, caseCode, 14L, stagingStatusDes, overallCreatedPreInboundLineList);

            //Log
            InboundIntegrationLog inboundIntegrationLog = createInboundIntegrationLogV2(createPreInboundHeader);
            inboundIntegrationLogRepository.save(inboundIntegrationLog);

            // db save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);
            inboundOrderProcess.setInboundHeader(createInboundHeader);
            inboundOrderProcess.setStagingHeader(stagingHeader);
            inboundOrderProcess.setGrHeader(createGrHeader);
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundLines(inboundLines);
            inboundOrderProcess.setStagingLines(stagingLines);
            inboundOrderProcess.setInboundIntegrationLog(inboundIntegrationLog);
            InboundHeaderV2 createdInboundHeader = orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

            // GR Line create and Putawayheader create
            createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader.getParentProductionOrderNo(), createGrHeader, idMasterAuthToken, MW_AMS);
            return createdInboundHeader;
        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e);
            throw e;
        }
    }

    /**
     *
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public synchronized InboundHeaderV2 processSAPInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
        log.info("----SAP------processSAPInboundReceivedV3------------");
        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("Inbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);

        try {
            MW_AMS = inboundIntegrationHeader.getLoginUserId() != null ? inboundIntegrationHeader.getLoginUserId() : MW_AMS;

            //Checking whether received refDocNumber processed already.
            boolean orderProcessedStatus = preInboundHeaderV2Repository.existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            log.error("IsDupicate : " + refDocNumber + " |---> " + orderProcessedStatus);
            if (orderProcessedStatus) {
                return new InboundHeaderV2();
            }

            String idMasterAuthToken = getIDMasterAuthToken();
            String masterAuthToken = getMasterAuthToken();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : {}|{}|{}|{}", preInboundNo, stagingNo, caseCode, grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            inboundOrderProcess = createBOMPreInboundLines(companyCodeId, plantId, languageId, warehouseId, preInboundNo, description, masterAuthToken, statusId, statusDescription, inboundIntegrationHeader, MW_AMS);

            List<InboundIntegrationLine> inboundIntegrationLines = inboundOrderProcess.getInboundIntegrationLines();
            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = inboundOrderProcess.getPreInboundLines();

            //Append PREINBOUNDLINE table through below logic
            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
                try {
                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
                            description, statusId, statusDescription));
                } catch (Exception e) {
                    throw new BadRequestException("Exception While PreInboundLine Create" + e);
                }
            });
            log.info("toBeCreatedPreInboundLineList [API] : {}", finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }

            //Header
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo, inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);

            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, (long) overallCreatedPreInboundLineList.size());

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);

            statusDescription = getStatusDescription(17L, languageId);
            String grStatusDes = getStatusDescription(16L,languageId);
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, 16L, grStatusDes);

            //Lines
            List<InboundLineV2> inboundLines = createInboundLines(17L, statusDescription, overallCreatedPreInboundLineList);
            String stagingStatusDes = getStatusDescription(14L,languageId);
            List<StagingLineEntityV2> stagingLines = createStagingLines(stagingNo, caseCode, 14L, stagingStatusDes, overallCreatedPreInboundLineList);

            //Log
            InboundIntegrationLog inboundIntegrationLog = createInboundIntegrationLogV2(createPreInboundHeader);
            inboundIntegrationLogRepository.save(inboundIntegrationLog);

            // db save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);
            inboundOrderProcess.setInboundHeader(createInboundHeader);
            inboundOrderProcess.setStagingHeader(stagingHeader);
            inboundOrderProcess.setGrHeader(createGrHeader);
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundLines(inboundLines);
            inboundOrderProcess.setStagingLines(stagingLines);
            inboundOrderProcess.setInboundIntegrationLog(inboundIntegrationLog);
            InboundHeaderV2 createdInboundHeader = orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

            // GR Line create and Putawayheader create
//            createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader.getParentProductionOrderNo(), createGrHeader, idMasterAuthToken, MW_AMS);
            return createdInboundHeader;
        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param bomLine
     * @param loginUserId
     * @param description
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                                  InboundIntegrationLine inboundIntegrationLine, BomLine bomLine, String loginUserId,
                                                                  IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());

            // ITM_CODE
            preInboundLine.setItemCode(bomLine.getChildItemCode());

            // ITM_TEXT
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());

            // MFR
            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());

            // ORD_QTY
            double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
            preInboundLine.setOrderQty(orderQuantity);

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // EA_DATE
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // STATUS_ID
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // REF_FIELD_1
            preInboundLine.setReferenceField1("CHILD ITEM");

            // REF_FIELD_2
            preInboundLine.setReferenceField2("BOM ITEM");

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

//            if (preInboundLine.getBarcodeId() == null) {
//                //Barcode
//                List<String> barcode = getBarCodeId(companyCodeId, plantId, languageId, warehouseId,
//                        preInboundLine.getItemCode(), preInboundLine.getManufacturerName());
//                log.info("Barcode : " + barcode);
//                if (barcode != null && !barcode.isEmpty()) {
//                    preInboundLine.setBarcodeId(barcode.get(0));
//                }
//            }

            //-----------------PROP_ST_BIN---------------------------------------------
//            String storageBin = validateStorageBin(companyCodeId, plantId, languageId, warehouseId, inboundIntegrationLine.getBinLocation(), preInboundLine);
//            preInboundLine.setReferenceField5(storageBin);
            preInboundLine.setReferenceField5(inboundIntegrationLine.getBinLocation());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine [BOM] : {}", preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("Exception while create preInboundLine - BOM : " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param loginUserId
     * @param description
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine, String loginUserId,
                                                          IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());

            // ITM_CODE
            preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

            // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());

            // MFR_PART
            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());

            // PARTNER_CODE
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // ORD_QTY
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // EA_DATE
            log.info("inboundIntegrationLine.getExpectedDate() : {}", inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // ITM_CASE_QTY
            preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());

//            if (preInboundLine.getBarcodeId() == null) {
//                //Barcode
//                List<String> barcode = getBarCodeId(companyCodeId, plantId, languageId, warehouseId,
//                        preInboundLine.getItemCode(), preInboundLine.getManufacturerName());
//                log.info("Barcode : " + barcode);
//                if (barcode != null && !barcode.isEmpty()) {
//                    preInboundLine.setBarcodeId(barcode.get(0));
//                }
//            }

            //-----------------PROP_ST_BIN---------------------------------------------
//            String storageBin = validateStorageBin(companyCodeId, plantId, languageId, warehouseId, inboundIntegrationLine.getBinLocation(), preInboundLine);
//            preInboundLine.setReferenceField5(storageBin);
            preInboundLine.setReferenceField5(inboundIntegrationLine.getBinLocation());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                              String loginUserId, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setCompanyCode(companyCodeId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundHeader.setStatusId(statusId);
            preInboundHeader.setStatusDescription(statusDesc);
            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
            preInboundHeader.setPlantDescription(description.getPlantDesc());
            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy(loginUserId);
            preInboundHeader.setCreatedOn(new Date());
            log.info("createdPreInboundHeader : " + preInboundHeader);

            // PreInbound_Header
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update PreInbound Header Update Successfully");

            return preInboundHeader;
        } catch (Exception e) {
            log.error("PreInboundHeader Create Exception : " + e);
            throw e;
        }
    }

    /**
     *
     * @param preInboundHeader
     * @param orderLinesCount
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 createInboundHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, Long orderLinesCount) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines(orderLinesCount);       //count of lines
            // Inbound_Header
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");
            //inboundHeaderV2Repository.save(inboundHeader);
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e);
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            // GR_MTD
            stagingHeader.setGrMtd("INTEGRATION");

            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");
            //stagingHeaderV2Repository.save(stagingHeader);
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e);
            throw e;
        }
    }

    /**
     *
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV2(StagingHeaderV2 stagingHeader, String caseCode, String grNumber, Long statusId, String statusDesc) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(statusId);
            grHeader.setStatusDescription(statusDesc);
            // Gr_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update Gr_Header Update Successfully");
            //grHeaderV2Repository.save(grHeader);
            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param parentProductionOrderNo
     * @param createGrHeader
     * @param idMasterAuthToken
     * @param loginUserId
     */
    public void createGrLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                               String parentProductionOrderNo, GrHeaderV2 createGrHeader,
                               String idMasterAuthToken, String loginUserId) throws Exception {
        try {
            log.info("Inbound Process Gr Line condition Check Initiated ------> " + new Date());
//            Long grCount = grHeaderV2Repository.getGrHeaderHeaderCountV3(companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo);
//            Long orderCount = grHeaderV2Repository.getIbOrderHeaderCountV3(companyCodeId, plantId, parentProductionOrderNo);
//            log.info("Count-GRHeader, Order: " + grCount + ", " + orderCount);
//            if (grCount != null && orderCount != null && grCount.equals(orderCount)) {
            Long putAwayHeaderCreateValidateCheck = grHeaderV2Repository.getPutAwayHeaderCreateValidationV3(companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo);
            log.info("putAwayHeaderCreateValidateCheck : " + putAwayHeaderCreateValidateCheck);
            if(putAwayHeaderCreateValidateCheck == 1) {
                createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader, idMasterAuthToken, loginUserId);
            }
        } catch (Exception e) {
            log.error("1.Exception while Gr Line, PutAway Header create : " + e);
            throw e;
        }
    }

    /**
     * @param grHeader
     * @throws Exception
     */
//    public void createGrLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
//                               GrHeaderV2 grHeader, String idMasterAuthToken, String loginUserId) throws Exception {
//        try {
//            log.info("Inbound Process Gr Line Initiated ------> " + new Date());
//            List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineV3(companyCodeId, plantId, languageId, warehouseId, grHeader.getParentProductionOrderNo());
//            String enterpriseAuthToken = getEnterpriseAuthToken();
//
//            Warehouse entWarehouse = enterpriseSetupService.getWarehouse(warehouseId, companyCodeId, plantId, languageId, enterpriseAuthToken);
//            log.info("entWarehouse : " + entWarehouse);
//
//            List<GrLineV2> dbGrline = grLineService.getGrLineV3(companyCodeId, plantId, languageId, warehouseId, grHeader.getParentProductionOrderNo());
//            log.info("dbGrline : " + dbGrline);
//
//            Long inboundQaCheck = 1L;
//            Long counter = 0L;
//            if (entWarehouse != null) {
//                counter = entWarehouse.getNoAisles() != null ? entWarehouse.getNoAisles() : 0L;
//                inboundQaCheck = entWarehouse.getInboundQaCheck();
//            }
//            if (dbGrline == null || dbGrline.isEmpty()) {
//                counter = 0L;
//            }
//
//            List<GrLineV2> newGrLineList = new ArrayList<>();
//            String nextRangeNumber = null;
//
//            // PACKBAR_CODE
//            long NUM_RAN_ID = 6;
//
//            for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
//                GrLineV2 newGrLine = new GrLineV2();
//                BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));
//
//                if (counter == 0) {
//                    nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//                    newGrLine.setPackBarcodes(nextRangeNumber);
//                    newGrLine.setQuantityType("A");
//                    counter++;
//                    log.info("1.counter: {}", counter);
//                } else if (counter < inboundQaCheck) {
//                    if (dbGrline != null && !dbGrline.isEmpty()) {
//                        newGrLine.setPackBarcodes(dbGrline.get(0).getPackBarcodes());
//                    } else {
//                        newGrLine.setPackBarcodes(nextRangeNumber);
//                    }
//                    newGrLine.setQuantityType("A");
//
//                    counter++;
//                    log.info("2.counter: {}", counter);
//                } else if (counter.equals(inboundQaCheck)) {
//                    counter = 0L;
//                    nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//                    newGrLine.setPackBarcodes(nextRangeNumber);
//                    newGrLine.setQuantityType("A");
//                    counter++;
//                    log.info("3.counter: {}", counter);
//                }
//                Warehouse updateCounterValue = new Warehouse();
//                updateCounterValue.setNoAisles(counter);
//                Warehouse updatedWarehouse = enterpriseSetupService.patchWarehouse(companyCodeId, plantId, languageId, warehouseId, updateCounterValue, MW_AMS, enterpriseAuthToken);
//                log.info("CounterValueUpdated : {}", updatedWarehouse);
//
//                newGrLine.setCompanyCode(companyCodeId);
//                newGrLine.setGoodReceiptQty(dbStagingLine.getOrderQty());
//                newGrLine.setAcceptedQty(dbStagingLine.getOrderQty());
//                newGrLine.setOrderUom(dbStagingLine.getOrderUom());
//                newGrLine.setGrUom(dbStagingLine.getOrderUom());
//                newGrLine.setDamageQty(0D);
//                newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
//                log.info("Stbin : {}", newGrLine.getReferenceField5());
//
//                if (dbStagingLine.getBarcodeId() != null) {
//                    newGrLine.setBarcodeId(dbStagingLine.getBarcodeId());
//                } else {
//                    newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
//                }
//
//                newGrLine.setAssignedUserId(loginUserId);
//                newGrLine.setConfirmedQty(dbStagingLine.getOrderQty());
//                newGrLine.setBranchCode(dbStagingLine.getBranchCode());
//                newGrLine.setTransferOrderNo(dbStagingLine.getTransferOrderNo());
//                newGrLine.setDeletionIndicator(0L);
//                newGrLine.setCreatedBy(loginUserId);
//                newGrLine.setUpdatedBy(loginUserId);
//                newGrLine.setConfirmedBy(loginUserId);
//                newGrLine.setCreatedOn(new Date());
//                newGrLine.setUpdatedOn(new Date());
//                newGrLine.setConfirmedOn(new Date());
//
//                // Lead Time
//                GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(languageId, companyCodeId, plantId, warehouseId, grHeader.getGoodsReceiptNo(), new Date());
//                if (implForLeadTime != null) {
//                    if (!implForLeadTime.getDiffDays().equals("00")) {
//                        String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
//                                + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
//                        newGrLine.setReferenceField10(leadTime);
//                    } else if (!implForLeadTime.getDiffHours().equals("00")) {
//                        String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
//                                + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
//                        newGrLine.setReferenceField10(leadTime);
//                    } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
//                        String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
//                        newGrLine.setReferenceField10(leadTime);
//                    } else {
//                        String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
//                        newGrLine.setReferenceField10(leadTime);
//                    }
//                }
//
//                newGrLineList.add(newGrLine);
//            }
//            List<PutAwayHeaderV2> createPutAwayHeaderList = createPutAwayHeaderV3(companyCodeId, plantId, languageId, warehouseId, newGrLineList, grHeader.getParentProductionOrderNo(), loginUserId, idMasterAuthToken);
////            List<PutAwayHeaderV2> putAwayHeaderSortedList = createPutAwayHeaderList.stream().sorted(Comparator.comparing(PutAwayHeaderV2::getPackBarcodes)).collect(toList());
////            List<PutAwayHeaderV2> putAwayHeaderListWithBinner = assignBinner(companyCodeId, plantId, languageId, warehouseId, putAwayHeaderSortedList);
//            log.info(" <---- GrLine & PutAwayHeader Save Process Initiated ------> ");
//            grLineV2Repository.saveAll(newGrLineList);
//            putAwayHeaderV2Repository.saveAll(createPutAwayHeaderList);
//            log.info("PutAwayHeader Saved Successfully: {}", createPutAwayHeaderList.size());
//            log.info("GrLine Created Successfully: {}", newGrLineList.size());
//        } catch (Exception e) {
//            log.error("Exception while Gr Line, PutAway Header create : " + e);
//            throw e;
//        }
//    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param grHeader
     * @param idMasterAuthToken
     * @param loginUserId
     * @throws Exception
     */
    public void createGrLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                               GrHeaderV2 grHeader, String idMasterAuthToken, String loginUserId) throws Exception {
        String refDocNumber = grHeader.getRefDocNumber();
        Long inboundOrderTypeId = grHeader.getInboundOrderTypeId();
        try {
            log.info("Inbound Process Gr Line Initiated ------> " + new Date());
            List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineV3(companyCodeId, plantId, languageId, warehouseId, grHeader.getParentProductionOrderNo());

            List<GrLineV2> newGrLineList = new ArrayList<>();

            for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
                GrLineV2 newGrLine = new GrLineV2();
                BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));

                String packBarCode = getNextRangeNumber(6L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);

                newGrLine.setPackBarcodes(packBarCode);
                newGrLine.setQuantityType("A");
                newGrLine.setCompanyCode(companyCodeId);
                newGrLine.setGoodReceiptQty(dbStagingLine.getOrderQty());
                newGrLine.setAcceptedQty(dbStagingLine.getOrderQty());
                newGrLine.setOrderUom(dbStagingLine.getOrderUom());
                newGrLine.setGrUom(dbStagingLine.getOrderUom());
                newGrLine.setDamageQty(0D);
                newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
//                log.info("Stbin : {}", newGrLine.getReferenceField5());

                if (dbStagingLine.getBarcodeId() != null) {
                    newGrLine.setBarcodeId(dbStagingLine.getBarcodeId());
                } else {
                    newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
                }

//                newGrLine.setAssignedUserId(loginUserId);
                newGrLine.setConfirmedQty(dbStagingLine.getOrderQty());
                newGrLine.setBranchCode(dbStagingLine.getBranchCode());
                newGrLine.setTransferOrderNo(dbStagingLine.getTransferOrderNo());
                newGrLine.setDeletionIndicator(0L);
                newGrLine.setCreatedBy(loginUserId);
                newGrLine.setUpdatedBy(loginUserId);
                newGrLine.setConfirmedBy(loginUserId);
                newGrLine.setCreatedOn(new Date());
                newGrLine.setUpdatedOn(new Date());
                newGrLine.setConfirmedOn(new Date());

                // Lead Time
                GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(languageId, companyCodeId, plantId, warehouseId, grHeader.getGoodsReceiptNo(), new Date());
                if (implForLeadTime != null) {
                    if (!implForLeadTime.getDiffDays().equals("00")) {
                        String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        newGrLine.setReferenceField10(leadTime);
                    } else if (!implForLeadTime.getDiffHours().equals("00")) {
                        String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        newGrLine.setReferenceField10(leadTime);
                    } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                        String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        newGrLine.setReferenceField10(leadTime);
                    } else {
                        String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                        newGrLine.setReferenceField10(leadTime);
                    }
                }

                newGrLineList.add(newGrLine);
            }

            long NUM_RAN_CODE_PA_NO = 7;
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            createPutAwayHeaderV3(companyCodeId, plantId, languageId, warehouseId, newGrLineList, nextPANumber, loginUserId);

            log.info(" <---- GrLine Save Process Initiated ------> ");
            grLineV2Repository.saveAll(newGrLineList);
//            putAwayHeaderV2Repository.saveAll(createPutAwayHeaderList);
//            log.info("PutAwayHeader Saved Successfully: {}", createPutAwayHeaderList.size());
            log.info("GrLine Created Successfully: {}", newGrLineList.size());
        } catch (Exception e) {
            log.error("Exception while Gr Line, PutAway Header create : " + e);

            // Updating the Processed Status
            log.info("Inbound Rollback Initiated...!" + refDocNumber);
            preInboundHeaderService.initiateInboundRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);
            orderService.updateProcessedIbOrderV2(refDocNumber, inboundOrderTypeId);
            log.info("Inbound Rollback Finished...!" + refDocNumber);

            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param newGrLineList
     * @param loginUserID
     * @return
     * @throws Exception
     */
    private void createPutAwayHeaderV3(String companyCode, String plantId, String languageId, String warehouseId,
                                       List<GrLineV2> newGrLineList, String putAwayNumber, String loginUserID) throws Exception {
        log.info("Inbound Process PutAwayHeader Initiated ------> {}", new Date());
        statusDescription = getStatusDescription(19L, languageId);
        /*----------------Inventory tables Create---------------------------------------------*/
        newGrLineList.stream().forEach(createdGRLine -> {
            try {
                InboundHeaderV2 dbPreInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId,
                        createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo());
                // Insert record into PutAwayHeader
                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(dbPreInboundHeader, putAwayHeader, CommonUtils.getNullPropertyNames(dbPreInboundHeader));
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));

//                String putAwayNumber = putAwayHeaderService.getPutAwayNumberV3(companyCode, plantId, languageId, warehouseId, parentProductionOrderNo);
//                log.info("PutAwayNumber : " + putAwayNumber);

//                if(putAwayNumber != null) {
                putAwayHeader.setPutAwayNumber(putAwayNumber);
//                }
//                else {
                // PA_NO
//                    long NUM_RAN_CODE_PA_NO = 7;
//                    String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
//                    putAwayHeader.setPutAwayNumber(nextPANumber);
//                    log.info("PutAwayNumber Generated: {}", nextPANumber);
//                }
                putAwayHeader.setCompanyCodeId(companyCode);
                String quantityType = createdGRLine.getQuantityType() != null ? createdGRLine.getQuantityType() : "A";
                putAwayHeader.setQuantityType(quantityType);

                String priceSegment = createdGRLine.getPriceSegment() != null ? createdGRLine.getPriceSegment().length() > 1 ? createdGRLine.getPriceSegment().substring(0,2) : createdGRLine.getPriceSegment() : createdGRLine.getPriceSegment();
                StringBuilder concatField = new StringBuilder()
                        .append(createdGRLine.getArticleNo()).append("_")
                        .append(createdGRLine.getGender()).append("_")
                        .append(createdGRLine.getColor()).append("_")
                        .append(createdGRLine.getSize()).append("_")
                        .append(priceSegment);
                putAwayHeader.setReferenceField3(concatField.toString());
                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                String storageBin = getStorageBin(companyCode, plantId, languageId, warehouseId,
                        createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), createdGRLine.getArticleNo(),
                        createdGRLine.getGender(), createdGRLine.getReferenceField5());
                putAwayHeader.setProposedStorageBin(storageBin);
                log.info("Proposed Bin: {}", putAwayHeader.getProposedStorageBin());

                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
                putAwayHeader.setStatusId(19L);
                putAwayHeader.setStatusDescription(statusDescription);

                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setCreatedBy(loginUserID);
                putAwayHeader.setUpdatedBy(loginUserID);
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());

                log.info("putAwayHeader create---->: {}", putAwayHeader);
                /*----------------Inventory tables Create---------------------------------------------*/
                createInventoryNonCBMV3(companyCode, plantId, languageId, warehouseId, createdGRLine, loginUserID);

                // Update_Order
                String orderText = "PutAwayHeader Created";
                inboundOrderV2Repository.updatePutawayHeader(putAwayHeader.getInboundOrderTypeId(), putAwayHeader.getRefDocNumber(), orderText);
                log.info("Update PutAwayHeader Update Successfully");

                putAwayHeaderV2Repository.save(putAwayHeader);

            } catch (Exception e) {
                log.error("Exception while PutAwayHeader create : " + e);
                throw new BadRequestException(e.getLocalizedMessage());
            }
        });
    }

    @Transactional
    public void createInventoryNonCBMV3(String companyCode, String plantId, String languageId, String warehouseId, GrLineV2 createdGRLine, String loginUserId) {
        try {
            InventoryV2 createdinventory = null;
            InventoryV2 dbInventory = getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(), createdGRLine.getBarcodeId());
            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

                Double INV_QTY = round(dbInventory.getInventoryQuantity());
                Double ALLOC_QTY = round(dbInventory.getAllocatedQuantity());
                Double GR_QTY = round(createdGRLine.getGoodReceiptQty());

                log.info("Before - Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,GrQty: {}, {}, {}, {}", INV_QTY, ALLOC_QTY, dbInventory.getReferenceField4(), GR_QTY);
                INV_QTY = INV_QTY + GR_QTY;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;

                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(ALLOC_QTY);
                inventory.setReferenceField4(TOT_QTY);

                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                inventory.setUpdatedBy(loginUserId);
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory[Existing] : {}", createdinventory);
            }

            if (dbInventory == null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));
                inventory.setCompanyCodeId(companyCode);

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setPackBarcodes(PACK_BARCODE);
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setReferenceField8(createdGRLine.getItemDescription());
                inventory.setReferenceField9(createdGRLine.getManufacturerName());
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setDescription(createdGRLine.getItemDescription());
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                StorageBinV2 storageBin = storageBinService.getStorageBinByBinClassIdV2(warehouseId, 3L, companyCode, plantId, languageId);
                log.info("storageBin: {}", storageBin);

                if (storageBin != null) {
                    inventory.setStorageBin(storageBin.getStorageBin());
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setStorageSectionId(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                double INV_QTY = round(createdGRLine.getGoodReceiptQty());
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for bin Class Id 3
                log.info("New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(loginUserId);

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }

                inventory.setCreatedOn(new Date());
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory : {}", createdinventory);
            }
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barCodeId
     * @return
     */
    public InventoryV2 getInventoryBinClassId3V4(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barCodeId) {
        InventoryV2 dbInventoryV2 = new InventoryV2();
        IInventoryImpl inventory = inventoryV2Repository.getInboundInventoryV3(companyCode, plantId, languageId, warehouseId, barCodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, null, 3L, 1L);
        if (inventory != null) {
            BeanUtils.copyProperties(inventory, dbInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            return dbInventoryV2;
        }
        return null;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param putAwayHeaderV2List
     * @throws Exception
     */
    public List<PutAwayHeaderV2> assignBinner(String companyCode, String plantId, String languageId, String warehouseId, List<PutAwayHeaderV2> putAwayHeaderV2List) throws Exception {
        try {
            List<PutAwayHeaderV2> putAwayHeaderListWithAssignUser = new ArrayList<>();
            List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListV3(companyCode, plantId, languageId, warehouseId, 5L);
            log.info("hhtUserList: " + hhtUserIdList.size());
            List<String> hhtUserList = new ArrayList<>();
            List<String> absentHhtUserList = new ArrayList<>();
            if (hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                for (HHTUser dbHhtUser : hhtUserIdList) {
                    if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                        List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                companyCode, languageId, plantId, warehouseId, dbHhtUser.getUserId(), dbHhtUser.getStartDate(), dbHhtUser.getEndDate());
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
            log.info("Present HHtUser List: {}", hhtUserList);
            log.info("Absent HHtUser List: {}", absentHhtUserList);

            Warehouse entWarehouse = enterpriseSetupService.getWarehouse(warehouseId, companyCode, plantId, languageId, getEnterpriseAuthToken());
            log.info("entWarehouse : {}", entWarehouse);
            Long inboundQaCheck = 0L;
            if (entWarehouse != null) {
                inboundQaCheck = entWarehouse.getInboundQaCheck();
            }

            int i = 0;
            int j = 0;
            if (putAwayHeaderV2List != null && !putAwayHeaderV2List.isEmpty()) {
                for (PutAwayHeaderV2 putAwayHeader : putAwayHeaderV2List) {
                    if (hhtUserList != null && !hhtUserList.isEmpty()) {
                        log.info("hhtUserList count: {}", hhtUserList.size());
                        putAwayHeader.setAssignedUserId(hhtUserList.get(i));
                        putAwayHeaderListWithAssignUser.add(putAwayHeader);
                        j = j + 1;
                        if (j == inboundQaCheck) {
                            j = 0;
                            i = i + 1;
                            if (i == hhtUserList.size()) {
                                i = 0;
                            }
                        }
                        log.info("i,j: {}|{}", i,j);
                    }
                }
            }
            return putAwayHeaderListWithAssignUser;
        } catch (Exception e) {
            log.error("Exception while assign binner : " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param description
     * @param masterAuthToken
     * @param statusId
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundOrderProcess createBOMPreInboundLines(String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
                                                        IKeyValuePair description, String masterAuthToken, Long statusId, String statusDescription,
                                                        InboundIntegrationHeader inboundIntegrationHeader, String loginUserId) throws Exception {
        List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
        List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
            ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                    languageId, companyCodeId, plantId, warehouseId,
                    inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
            log.info("imBasicData1 exists: {}", imBasicData1);

            if (inboundIntegrationLine.getItemText() == null && imBasicData1 != null) {
                inboundIntegrationLine.setItemText(imBasicData1.getDescription());
            }

            // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
            if (imBasicData1 == null) {
                imBasicData1 = new ImBasicData1V2();
                BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                imBasicData1.setLanguageId(languageId);                                         // LANG_ID
                imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
                imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
                imBasicData1.setPlantId(plantId);                                               // PLANT_ID
                imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
                String uomId = inboundIntegrationLine.getUom() != null ? inboundIntegrationLine.getUom() : getUomId(companyCodeId, plantId, languageId, warehouseId);
                imBasicData1.setUomId(uomId);                         // UOM_ID
                imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
                imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                imBasicData1.setManufacturerCode(inboundIntegrationLine.getManufacturerCode());
                imBasicData1.setCapacityCheck(false);
                imBasicData1.setDeletionIndicator(0L);
                imBasicData1.setCompanyDescription(description.getCompanyDesc());
                imBasicData1.setPlantDescription(description.getPlantDesc());
                imBasicData1.setWarehouseDescription(description.getWarehouseDesc());

                imBasicData1.setStatusId(1L);                                                // STATUS_ID
                ImBasicData1 createdImBasicData1 = mastersService.createImBasicData1V2(imBasicData1, loginUserId, masterAuthToken);
                log.info("ImBasicData1 created: {}", createdImBasicData1);
            }

            /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
            /*
             * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader table and
             * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
             */
//            BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId, companyCodeId, plantId, languageId, masterAuthToken);
//            log.info("bomHeader [BOM] : {}", bomHeader);
//            if (bomHeader != null) {
//                BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
//                for (BomLine dbBomLine : bomLine) {
//                    PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(companyCodeId, plantId, languageId, warehouseId,
//                            preInboundNo, inboundIntegrationHeader, inboundIntegrationLine,
//                            dbBomLine, loginUserId, description, statusId, statusDescription);
//                    overallCreatedPreInboundLineList.add(preInboundLineEntity);
//                }
//            }
            inboundIntegrationLines.add(inboundIntegrationLine);
        }
        log.info("preInboundLineEntity [BOM] : {}", overallCreatedPreInboundLineList.size());
        inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
        inboundOrderProcess.setInboundIntegrationLines(inboundIntegrationLines);
        return inboundOrderProcess;
    }

    /**
     *
     * @param stagingNo
     * @param caseCode
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     */
    public List<StagingLineEntityV2> createStagingLines(String stagingNo, String caseCode, Long statusId, String statusDesc,
                                                        List<PreInboundLineEntityV2> preInboundLines) {
        try {
            return preInboundLines.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));

                String priceSegment = preInboundLine.getPriceSegment() != null ? preInboundLine.getPriceSegment().length() > 1 ? preInboundLine.getPriceSegment().substring(0,2) : preInboundLine.getPriceSegment() : preInboundLine.getPriceSegment();
                StringBuilder concatField = new StringBuilder()
                        .append(preInboundLine.getArticleNo()).append("_")
                        .append(preInboundLine.getGender()).append("_")
                        .append(preInboundLine.getColor()).append("_")
                        .append(preInboundLine.getSize()).append("_")
                        .append(priceSegment);
                stagingLine.setReferenceField3(concatField.toString());

                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setStatusId(statusId);
                stagingLine.setRec_accept_qty(preInboundLine.getOrderQty());
                stagingLine.setRec_damage_qty(0D);
                stagingLine.setStatusDescription(statusDesc);
                log.info("stagingLine : " +stagingLine);
                return stagingLine;
            }).collect(toList());
        } catch (Exception e) {
            log.error("Exception while StagingLines Create : " + e);
            throw e;
        }
    }

    /**
     *
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     * @throws Exception
     */
    public List<InboundLineV2> createInboundLines(Long statusId, String statusDesc, List<PreInboundLineEntityV2> preInboundLines) throws Exception {
        try {
            return preInboundLines.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setStatusId(statusId);
                inboundLine.setStatusDescription(statusDesc);
                return inboundLine;
            }).collect(toList());
        }  catch (Exception e) {
            log.error("Exception while InboundLines Create : " + e);
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundLine
     * @return
     */
    private String getStorageBin (String companyCodeId, String plantId, String languageId, String warehouseId, String location, PreInboundLineEntityV2 preInboundLine) {
        //-----------------PROP_ST_BIN---------------------------------------------
        try {
//            String referenceField1 = preInboundLine.getArticleNo().substring(0, 2);
//            String referenceField2 = preInboundLine.getGender();
//            log.info("referenceField1, referenceField2, companyCode, plantId, warehouseId, languageId: {},{},{},{},{},{}", referenceField1, referenceField2, companyCodeId, plantId, warehouseId, languageId);
            return grLineService.getProposedStorageBin(companyCodeId, plantId, languageId, warehouseId,
                    preInboundLine.getItemCode(), preInboundLine.getManufacturerName(), preInboundLine.getArticleNo(), preInboundLine.getGender(), location);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param articleNo
     * @param gender
     * @param location
     * @return
     */
    private String getStorageBin(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String articleNo, String gender, String location) {
        try {
            return grLineService.getProposedStorageBin(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, articleNo, gender, location);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageBinLocation
     * @return
     */
    private String validateStorageBin(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBinLocation, PreInboundLineEntityV2 preInboundLine) throws Exception {
        try {
            log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + storageBinLocation);
//            if(storageBin != null) {
//                boolean isValidBin = storageBinService.isStorageBinExists(companyCodeId, plantId, languageId, warehouseId, storageBinLocation);
//                log.info("Valid StorageBin : " + isValidBin);
//                if (isValidBin) {
//                    return storageBin;
//                }
//            }
            return getStorageBin(companyCodeId, plantId, languageId, warehouseId, storageBinLocation, preInboundLine);
        } catch (Exception e) {
            log.error("Exception while fetching StorageBin");
            throw e;
        }
    }

    /**
     *
     * @param refDocNumber
     * @param orderTypeId
     * @return
     */
    public boolean isPreInboundOrderExist(String refDocNumber, Long orderTypeId) {
        return preInboundHeaderV2Repository.existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, orderTypeId, 0L);
    }

    /**
     * @param createdPreInboundHeader
     * @return
     * @throws Exception
     */
    public InboundIntegrationLog createInboundIntegrationLogV2(PreInboundHeaderEntityV2 createdPreInboundHeader) throws Exception {
        try {
            InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
            BeanUtils.copyProperties(createdPreInboundHeader, dbInboundIntegrationLog, CommonUtils.getNullPropertyNames(createdPreInboundHeader));
            dbInboundIntegrationLog.setLanguageId(createdPreInboundHeader.getLanguageId());
            dbInboundIntegrationLog.setCompanyCodeId(createdPreInboundHeader.getCompanyCode());
            dbInboundIntegrationLog.setPlantId(createdPreInboundHeader.getPlantId());
            dbInboundIntegrationLog.setWarehouseId(createdPreInboundHeader.getWarehouseId());
            dbInboundIntegrationLog.setIntegrationLogNumber(createdPreInboundHeader.getPreInboundNo());
            dbInboundIntegrationLog.setRefDocNumber(createdPreInboundHeader.getRefDocNumber());
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setIntegrationStatus("SUCCESS");
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setDeletionIndicator(0L);
            dbInboundIntegrationLog.setCreatedBy(createdPreInboundHeader.getCreatedBy());
            dbInboundIntegrationLog.setCreatedOn(new Date());
            log.info("dbInboundIntegrationLog : {}", dbInboundIntegrationLog);
            return dbInboundIntegrationLog;
        } catch (Exception e) {
            log.error("InboundIntegrationLog Create[Success] Exception : " + e);
            throw e;
        }
    }
}
