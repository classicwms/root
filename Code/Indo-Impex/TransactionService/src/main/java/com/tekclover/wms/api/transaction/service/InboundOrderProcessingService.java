package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.enterprise.Warehouse;
import com.tekclover.wms.api.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderProcess;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InboundOrderProcessingService extends BaseService {

    @Autowired
    private MastersService mastersService;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private EnterpriseSetupService enterpriseSetupService;

    @Autowired
    private PreInboundHeaderService preInboundHeaderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    OrderProcessingService orderProcessingService;

    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;
    //========================================================================V2====================================================================

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundOrderProcess.getInboundOrderTypeId();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            // save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
            List<ImPartner> imPartnerList = new ArrayList<>();

            String idMasterAuthToken = getIDMasterAuthToken();
            String masterAuthToken = getMasterAuthToken();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            String partBarCode = generateBarCodeId(refDocNumber);
            long lineNumber = 1;
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId,
                            inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (imBasicData1 != null) {
                    if(inboundIntegrationLine.getItemText() == null) {
                    inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                }
                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
                    inboundIntegrationLine.setSize(imBasicData1.getSize());
                    if(imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                    inboundIntegrationLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemType()));
                    } else {
                        inboundIntegrationLine.setItemType(imBasicData1.getItemTypeDescription());
                    }
                    if(imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                    inboundIntegrationLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemGroup()));
                    } else {
                        inboundIntegrationLine.setItemGroup(imBasicData1.getItemGroupDescription());
                }
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
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                         // UOM_ID
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
                    ImBasicData1 createdImBasicData1 = mastersService.createImBasicData1V2(imBasicData1, MW_AMS, masterAuthToken);
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */
                BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
                                                                  companyCodeId, plantId, languageId, masterAuthToken);
                log.info("bomHeader [BOM] : " + bomHeader);

                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
                for (long i = 1; i <= noOfBags; i++) {
                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    String barcodeId = generateBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
                    newInboundIntegrationLine.setBarcodeId(barcodeId);
                    newInboundIntegrationLine.setLineReference(lineNumber);
                    newInboundIntegrationLine.setNoBags(1D);
                    newInboundIntegrationLine.setOrderedQty(1D);
                    ImPartner imPartner = createImpartner(companyCodeId, plantId, languageId, warehouseId, newInboundIntegrationLine.getItemCode(),
                                                          newInboundIntegrationLine.getManufacturerName(), barcodeId, MW_AMS);
                    imPartnerList.add(imPartner);

                if (bomHeader != null) {
                        BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
                    for (BomLine dbBomLine : bomLine) {
                        PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(companyCodeId, plantId, languageId, warehouseId,
                                                                                                         preInboundNo, inboundIntegrationHeader, newInboundIntegrationLine,
                                                                                                     dbBomLine, MW_AMS, description, statusId, statusDescription);
                        toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                        log.info("preInboundLineEntity [BOM] : " + toBeCreatedPreInboundLineList.size());
                    }

                    // Batch Insert - PreInboundLines
                    if (!toBeCreatedPreInboundLineList.isEmpty()) {
                        log.info("createdPreInboundLine [BOM] : " + toBeCreatedPreInboundLineList);
                        overallCreatedPreInboundLineList.addAll(toBeCreatedPreInboundLineList);
                    }
                }
                    lineNumber++;
                    inboundIntegrationLines.add(newInboundIntegrationLine);
            }
            }

            inboundOrderProcess.setImPartnerList(imPartnerList);
            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
                try {
                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                                                                                  inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
                                                                                  description, statusId, statusDescription));
                } catch (Exception e) {
                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
                }
            });
            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                                                                                       inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);

            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                return inboundLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setInboundLines(inboundLines);

            statusDescription = getStatusDescription(14L, languageId);
            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setStatusId(14L);
                stagingLine.setStatusDescription(statusDescription);

                if(companyCodeId.equalsIgnoreCase(WK_COMPANY_CODE)) {
                    //-----------------PROP_ST_BIN---------------------------------------------
                    StorageBin storageBin = null;
                    try {
                        String referenceField1 = preInboundLine.getArticleNo().substring(0, 2);
                        String referenceField2 = preInboundLine.getGender();
                        log.info("referenceField1, referenceField2, companyCode, plantId, warehouseId, languageId: " + referenceField1 + "," + referenceField2 + "," + companyCodeId + "," + plantId + "," + warehouseId + "," + languageId);
                        storageBin = mastersService.getStorageBinV3(referenceField1, referenceField2, companyCodeId, plantId, warehouseId, languageId, masterAuthToken);
                        log.info("InterimStorageBin: " + storageBin);
                        if (storageBin != null) {
                            stagingLine.setReferenceField5(storageBin.getStorageBin());
                        }
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                }

                return stagingLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setStagingLines(stagingLines);

            /*------------------Insert into Inbound Header----------------------------*/
            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundHeader(createInboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);
            inboundOrderProcess.setStagingHeader(stagingHeader);

            //Gr Header Creation
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, languageId);
            inboundOrderProcess.setGrHeader(createGrHeader);

            if(companyCodeId.equalsIgnoreCase(WK_COMPANY_CODE)) {
                createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader.getParentProductionOrderNo(), createGrHeader, idMasterAuthToken, MW_AMS);
            }

            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e.toString());
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
     * @param MW_AMS
     * @param description
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                                  InboundIntegrationLine inboundIntegrationLine, BomLine bomLine, String MW_AMS,
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
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // STATUS_ID
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrand(inboundIntegrationLine.getBrand());
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

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine [BOM] : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("Exception while create preInboundLine - BOM : " + e.toString());
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
     * @param MW_AMS
     * @param description
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
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
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // EA_DATE
            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
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
            preInboundLine.setBrand(inboundIntegrationLine.getBrand());
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

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
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
     * @param MW_AMS
     * @return
     * @throws Exception
     */
    public PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                              String MW_AMS, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
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
            preInboundHeader.setCreatedBy(MW_AMS);
            preInboundHeader.setCreatedOn(new Date());

            log.info("createdPreInboundHeader : " + preInboundHeader);
            return preInboundHeader;
        } catch (Exception e) {
            log.error("PreInboundHeader Create Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    public InboundHeaderV2 createInboundHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines((long) preInboundLine.size());       //count of lines
//            return inboundHeaderV2Repository.save(inboundHeader);
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e.toString());
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
//            return stagingHeaderV2Repository.save(stagingHeader);
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @param languageId
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV2(StagingHeaderV2 stagingHeader, String caseCode, String grNumber, String languageId) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(16L);
            grHeader.setStatusDescription(getStatusDescription(16L, languageId));
//            GrHeaderV2 createdGrHeader = grHeaderV2Repository.save(grHeader);
            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
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
            Long grCount = grHeaderV2Repository.getGrHeaderHeaderCountV3(companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo);
            Long orderCount = grHeaderV2Repository.getIbOrderHeaderCountV3(companyCodeId, plantId, parentProductionOrderNo);
            log.info("Count-GRHeader, Order: " + grCount + ", " + orderCount);
            if (grCount != null && orderCount != null && grCount.equals(orderCount)) {
                createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader, idMasterAuthToken, loginUserId);
                grLineService.assignBinner(companyCodeId, plantId, languageId, warehouseId, parentProductionOrderNo);
            }
        } catch (Exception e) {
            log.error("1.Exception while Gr Line, PutAway Header create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param grHeader
     * @throws Exception
     */
    public void createGrLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                               GrHeaderV2 grHeader, String idMasterAuthToken, String loginUserId) throws Exception {
        try {
            List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineV3(companyCodeId, plantId, languageId, warehouseId, grHeader.getParentProductionOrderNo());

            String enterpriseAuthToken = getEnterpriseAuthToken();

            /*
             * In GR line, Create a packbarcode for every 10 qty of the selected ST_SEC_ID of the ST_BIN in putawayheader
             */
            Warehouse entWarehouse = enterpriseSetupService.getWarehouse(warehouseId, companyCodeId, plantId, languageId, enterpriseAuthToken);
            log.info("entWarehouse : " + entWarehouse);

            List<GrLineV2> dbGrline = grLineService.getGrLineV3(companyCodeId, plantId, languageId, warehouseId, grHeader.getParentProductionOrderNo());
            log.info("dbGrline : " + dbGrline);

            Long inboundQaCheck = 1L;
            Long counter = 0L;
            if (entWarehouse != null) {
                counter = entWarehouse.getNoAisles() != null ? entWarehouse.getNoAisles() : 0L;
                inboundQaCheck = entWarehouse.getInboundQaCheck();
            }
            if (dbGrline == null || dbGrline.isEmpty()) {
                counter = 0L;
            }

            List<AddGrLineV2> newGrLineList = new ArrayList<>();
            String nextRangeNumber = null;

            // PACKBAR_CODE
            long NUM_RAN_ID = 6;

            for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
                AddGrLineV2 newGrLine = new AddGrLineV2();
                BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));

                if (counter == 0) {
                    PackBarcode newPackBarcode = new PackBarcode();
                    nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
                    newPackBarcode.setBarcode(nextRangeNumber);
                    newPackBarcode.setQuantityType("A");
                    newGrLine.setPackBarcode(newPackBarcode);
                    counter++;
                    log.info("1.counter: " + counter);
                } else if (counter < inboundQaCheck) {
                    PackBarcode newPackBarcode = new PackBarcode();
                    if (dbGrline != null && !dbGrline.isEmpty()) {
                        newPackBarcode.setBarcode(dbGrline.get(0).getPackBarcodes());
                    } else {
                        newPackBarcode.setBarcode(nextRangeNumber);
                    }
                    newPackBarcode.setQuantityType("A");
                    newGrLine.setPackBarcode(newPackBarcode);

                    counter++;
                    log.info("2.counter: " + counter);
                } else if (counter.equals(inboundQaCheck)) {
                    counter = 0L;
                    PackBarcode newPackBarcode = new PackBarcode();
                    nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
                    newPackBarcode.setBarcode(nextRangeNumber);
                    newPackBarcode.setQuantityType("A");
                    newGrLine.setPackBarcode(newPackBarcode);
                    counter++;
                    log.info("3.counter: " + counter);
                }
                Warehouse updateCounterValue = new Warehouse();
                updateCounterValue.setNoAisles(counter);
                Warehouse updatedWarehouse = enterpriseSetupService.patchWarehouse(companyCodeId, plantId, languageId, warehouseId, updateCounterValue, MW_AMS, enterpriseAuthToken);
                log.info("CounterValueUpdated : " + updatedWarehouse);

                newGrLine.setGoodReceiptQty(dbStagingLine.getOrderQty());
                newGrLine.setAcceptedQty(dbStagingLine.getOrderQty());
                newGrLine.setDamageQty(0D);
                newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
                newGrLine.setManufacturerFullName(dbStagingLine.getManufacturerFullName());
                newGrLine.setReferenceDocumentType(dbStagingLine.getReferenceDocumentType());
                newGrLine.setPurchaseOrderNumber(dbStagingLine.getPurchaseOrderNumber());
                newGrLine.setParentProductionOrderNo(dbStagingLine.getParentProductionOrderNo());
                newGrLine.setReferenceField5(dbStagingLine.getReferenceField5());
                log.info("Stbin : " + newGrLine.getReferenceField5());

                if (dbStagingLine.getBarcodeId() != null) {
                    newGrLine.setBarcodeId(dbStagingLine.getBarcodeId());
                }

                if (dbStagingLine.getBarcodeId() == null) {
                    newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
                }

                newGrLine.setAssignedUserId(loginUserId);
                newGrLineList.add(newGrLine);
            }

            createGrLineV3(newGrLineList, loginUserId, idMasterAuthToken);
            log.info("GrLine Created Successfully: " + newGrLineList.size());
        } catch (Exception e) {
            log.error("Exception while Gr Line, PutAway Header create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param newGrLines
     * @param loginUserID
     * @param idMasterAuthToken
     * @throws Exception
     */
    public void createGrLineV3(@Valid List<AddGrLineV2> newGrLines, String loginUserID, String idMasterAuthToken) throws Exception {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        List<PutAwayHeaderV2> createPutAwayHeaderList = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        try {
            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                /*------------Inserting based on the PackBarcodes -----------*/
                GrLineV2 dbGrLine = new GrLineV2();
                log.info("newGrLine : " + newGrLine);
                BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                dbGrLine.setPackBarcodes(newGrLine.getPackBarcode().getBarcode());
                dbGrLine.setQuantityType(newGrLine.getPackBarcode().getQuantityType());
                dbGrLine.setStatusId(14L);

                //12-03-2024 - Ticket No. ALM/2024/006
                if (dbGrLine.getGoodReceiptQty() < 0) {
                    throw new BadRequestException("Gr Quantity Cannot be Negative");
                }
                log.info("StatusId: " + newGrLine.getStatusId());

                if (newGrLine.getStatusId() == 24L) {
                    throw new BadRequestException("GrLine is already Confirmed");
                }

                //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                        newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                        newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(), newGrLine.getPalletCode(),
                        newGrLine.getCaseCode(), newGrLine.getItemCode(), newGrLine.getManufacturerName(), newGrLine.getLineNo());
                log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                if (dbGrQty != null) {
                    Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
                    if (newGrLine.getOrderQty() < totalGrQty) {
                        throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
                    }
                }

                Double recAcceptQty = 0D;
                Double recDamageQty = 0D;
                Double variance = 0D;
                Double invoiceQty = 0D;
                Double acceptQty = 0D;
                Double damageQty = 0D;

                if (newGrLine.getOrderQty() != null) {
                    invoiceQty = newGrLine.getOrderQty();
                }
                if (newGrLine.getAcceptedQty() != null) {
                    acceptQty = newGrLine.getAcceptedQty();
                }
                if (newGrLine.getDamageQty() != null) {
                    damageQty = newGrLine.getDamageQty();
                }

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(newGrLine.getCompanyCode(),
                                                                                                            newGrLine.getPlantId(),
                                                                                                            newGrLine.getLanguageId(),
                                                                                                            newGrLine.getWarehouseId(),
                                                                                                            newGrLine.getPreInboundNo(),
                                                                                                            newGrLine.getRefDocNumber(),
                                                                                                            newGrLine.getLineNo(),
                                                                                                            newGrLine.getItemCode(),
                                                                                                            newGrLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);

                if (dbStagingLineEntity != null) {
                    if (dbStagingLineEntity.getRec_accept_qty() != null) {
                        recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                    }
                    if (dbStagingLineEntity.getRec_damage_qty() != null) {
                        recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                    }
                    dbGrLine.setOrderUom(dbStagingLineEntity.getOrderUom());
                    dbGrLine.setGrUom(dbStagingLineEntity.getOrderUom());
                }

                variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                log.info("Variance: " + variance);

                if (variance == 0D) {
                    dbGrLine.setStatusId(17L);
                }
                statusDescription = getStatusDescription(dbGrLine.getStatusId(), newGrLine.getLanguageId());
                dbGrLine.setStatusDescription(statusDescription);

                if (variance < 0D) {
                    throw new BadRequestException("Variance Qty cannot be Less than 0");
                }

                dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                dbGrLine.setBranchCode(newGrLine.getBranchCode());
                dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                dbGrLine.setDeletionIndicator(0L);
                dbGrLine.setCreatedBy(loginUserID);
                dbGrLine.setUpdatedBy(loginUserID);
                dbGrLine.setConfirmedBy(loginUserID);
                dbGrLine.setCreatedOn(new Date());
                dbGrLine.setUpdatedOn(new Date());
                dbGrLine.setConfirmedOn(new Date());

                companyCode = dbGrLine.getCompanyCode();
                plantId = dbGrLine.getPlantId();
                languageId = dbGrLine.getLanguageId();
                warehouseId = dbGrLine.getWarehouseId();
                refDocNumber = dbGrLine.getRefDocNumber();
                preInboundNo = dbGrLine.getPreInboundNo();
                goodsReceiptNo = dbGrLine.getGoodsReceiptNo();

                GrLineV2 createdGRLine = null;
                boolean createGrLineError = false;

                // Lead Time
                GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(dbGrLine.getLanguageId(), dbGrLine.getCompanyCode(),
                                                                            dbGrLine.getPlantId(), dbGrLine.getWarehouseId(), dbGrLine.getGoodsReceiptNo(), new Date());
                if (implForLeadTime != null) {
                    if (!implForLeadTime.getDiffDays().equals("00")) {
                        String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        dbGrLine.setReferenceField10(leadTime);
                    } else if (!implForLeadTime.getDiffHours().equals("00")) {
                        String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        dbGrLine.setReferenceField10(leadTime);
                    } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                        String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                        dbGrLine.setReferenceField10(leadTime);
                    } else {
                        String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                        dbGrLine.setReferenceField10(leadTime);
                    }

                    try {
                        createdGRLine = grLineV2Repository.save(dbGrLine);
                    } catch (Exception e) {
                        createGrLineError = true;
                        throw e;
                    }

                    log.info("createdGRLine : " + createdGRLine + ", " + createdGRLine.getReferenceField5());
                    createdGRLines.add(createdGRLine);

                    if (createdGRLine != null && !createGrLineError) {
                        // Record Insertion in PUTAWAYHEADER table
                        createPutAwayHeaderList.add(createPutAwayHeaderV3(createdGRLine, loginUserID, idMasterAuthToken));
                    }
                }
                log.info("Records were inserted successfully...");
            }


            //Update GrHeader using stored Procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, createdGRLines.get(0).getLanguageId());
            grHeaderV2Repository.updateGrheaderStatusUpdateProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");

            //Update staging Line using stored Procedure
            stagingLineV2Repository.updateStagingLineUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, new Date());
            log.info("stagingLine Status updated using Stored Procedure ");

            //Update InboundLine using Stored Procedure
            inboundLineV2Repository.updateInboundLineStatusUpdateNewProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 17L, statusDescription, new Date());
            log.info("inboundLine Status updated using Stored Procedure ");

            putAwayHeaderV2Repository.saveAll(createPutAwayHeaderList);
            log.info("PutAwayHeader Saved Successfully: " + createPutAwayHeaderList.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param createdGRLine
     * @param loginUserID
     * @param idMasterAuthToken
     * @throws Exception
     */
    public PutAwayHeaderV2 createPutAwayHeaderV3(GrLineV2 createdGRLine, String loginUserID, String idMasterAuthToken) throws Exception {
        try {
            String itemCode = createdGRLine.getItemCode();
            String companyCode = createdGRLine.getCompanyCode();
            String plantId = createdGRLine.getPlantId();
            String languageId = createdGRLine.getLanguageId();
            String warehouseId = createdGRLine.getWarehouseId();

            // PA_NO
            long NUM_RAN_CODE_PA_NO = 7;
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);

            InboundHeaderV2 dbPreInboundHeader = inboundHeaderService.getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId,
                                                                                                 createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo());
            // Insert record into PutAwayHeader
            PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
            BeanUtils.copyProperties(dbPreInboundHeader, putAwayHeader, CommonUtils.getNullPropertyNames(dbPreInboundHeader));
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setPutAwayNumber(nextPANumber);
            putAwayHeader.setCompanyCodeId(companyCode);
            String quantityType = createdGRLine.getQuantityType() != null ? createdGRLine.getQuantityType() : "A";
            putAwayHeader.setQuantityType(quantityType);
            log.info("PutAwayNumber Generated: " + nextPANumber);

            putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
            putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());
            putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
            putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
            putAwayHeader.setProposedStorageBin(createdGRLine.getReferenceField5());
            log.info("Proposed Bin: " + putAwayHeader.getProposedStorageBin());

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            //PROP_HE_NO	<- PAWAY_HE_NO
            putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
            putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());

            putAwayHeader.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
            putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
            putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
            putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
            putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
            putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
            putAwayHeader.setStatusId(19L);

            statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
            putAwayHeader.setStatusDescription(statusDescription);
            putAwayHeader.setDeletionIndicator(0L);
            putAwayHeader.setCreatedBy(loginUserID);
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeader.setCreatedOn(new Date());
            putAwayHeader.setUpdatedOn(new Date());
            putAwayHeader.setConfirmedOn(new Date());

            log.info("putAwayHeader create---->: " + putAwayHeader);

            /*----------------Inventory tables Create---------------------------------------------*/
            grLineService.createInventoryNonCBMV2(createdGRLine);

            return putAwayHeader;

        } catch (Exception e) {
            log.error("Exception while PutAwayHeader create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param inbound
     * @param MW_AMS
     * @return
     * @throws Exception
     */
    public void createInboundIntegrationLogV2(InboundIntegrationHeader inbound, String MW_AMS) throws Exception {
        try {
            InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
            BeanUtils.copyProperties(inbound, dbInboundIntegrationLog, CommonUtils.getNullPropertyNames(inbound));
            dbInboundIntegrationLog.setCompanyCodeId(inbound.getCompanyCode());
            dbInboundIntegrationLog.setPlantId(inbound.getBranchCode());
            dbInboundIntegrationLog.setLanguageId(inbound.getLanguageId());
            dbInboundIntegrationLog.setWarehouseId(inbound.getWarehouseID());
            dbInboundIntegrationLog.setIntegrationLogNumber(inbound.getRefDocumentNo());
            dbInboundIntegrationLog.setRefDocNumber(inbound.getRefDocumentNo());
            dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
            dbInboundIntegrationLog.setIntegrationStatus("FAILED");
            dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
            dbInboundIntegrationLog.setDeletionIndicator(0L);
            dbInboundIntegrationLog.setCreatedBy(MW_AMS);

            dbInboundIntegrationLog.setCreatedOn(new Date());
            dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
            log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        } catch (Exception e) {
            log.error("Inbound Integration Log Create Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserId
     * @return
     */
    private ImPartner createImpartner(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String itemCode, String manufacturerName, String barcodeId, String loginUserId) {
        ImPartner imPartner = new ImPartner();
        imPartner.setCompanyCodeId(companyCodeId);
        imPartner.setPlantId(plantId);
        imPartner.setLanguageId(languageId);
        imPartner.setWarehouseId(warehouseId);
        imPartner.setItemCode(itemCode);
        imPartner.setManufacturerName(manufacturerName);
        imPartner.setManufacturerCode(manufacturerName);
        imPartner.setBusinessPartnerCode(manufacturerName);
        imPartner.setBusinessPartnerType("1");
        imPartner.setPartnerItemBarcode(barcodeId);
        imPartner.setVendorItemBarcode(barcodeId);
        imPartner.setDeletionIndicator(0L);
        imPartner.setCreatedBy(loginUserId);
        imPartner.setUpdatedBy(loginUserId);
        imPartner.setCreatedOn(new Date());
        imPartner.setUpdatedOn(new Date());
        return imPartner;
    }

}