package com.tekclover.wms.api.inbound.orders.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.CaseConfirmation;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.StagingLineEntity;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StagingLineService extends BaseService {

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;
    @Autowired
    private MastersService mastersService;
    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;
    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    private GrLineService grLineService;
    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;
    String statusDescription = null;


    /**
     * @param inputPreInboundLines
     * @param stagingNo
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public List<StagingLineEntityV2> createStagingLineV2(List<PreInboundLineEntityV2> inputPreInboundLines,
                                                         String stagingNo, String warehouseId,
                                                         String companyCodeId, String plantId, String languageId,
                                                         String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<StagingLineEntityV2> stagingLineEntityList = new ArrayList<>();
        String preInboundNo = null;
        String refDocNumber = null;

        // Casecode needs to be created automatically by calling /({numberOfCases}/barcode) from StagingHeader
        Long numberOfCases = 1L;
        List<String> caseCodeList = generateNumberRanges(numberOfCases, warehouseId, companyCodeId, plantId, languageId);
        if (caseCodeList == null || caseCodeList.isEmpty()) {
            throw new BadRequestException("CaseCode is not generated.");
        }

        String manufactureCode = null;
        String caseCodeForCaseConfirmation = null;
        List<CaseConfirmation> caseConfirmationList = new ArrayList<>();

        for (PreInboundLineEntityV2 newStagingLine : inputPreInboundLines) {
            log.info("newStagingLineEntity : " + newStagingLine);
            // Insert based on the number of casecodes
            for (String caseCode : caseCodeList) {
                StagingLineEntityV2 dbStagingLineEntity = new StagingLineEntityV2();
                BeanUtils.copyProperties(newStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(newStagingLine));
                dbStagingLineEntity.setCaseCode(caseCode);
                dbStagingLineEntity.setPalletCode(caseCode);    //Copy CASE_CODE
                dbStagingLineEntity.setStatusId(13L);
                dbStagingLineEntity.setLanguageId(languageId);
                dbStagingLineEntity.setCompanyCode(companyCodeId);
                dbStagingLineEntity.setPlantId(plantId);
                //Pass ITM_CODE/SUPPLIER_CODE received in integration API into IMPARTNER table and fetch PARTNER_ITEM_BARCODE values. Values can be multiple
                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(newStagingLine.getItemCode(),
                        newStagingLine.getCompanyCode(),
                        newStagingLine.getPlantId(),
                        newStagingLine.getWarehouseId(),
                        newStagingLine.getManufacturerName(),
                        newStagingLine.getLanguageId());
                log.info("Barcode : " + barcode);
                if (barcode != null && !barcode.isEmpty()) {
//                    dbStagingLineEntity.setPartner_item_barcode(barcode.replaceAll("\\s", "").trim());      //to remove white space
                    dbStagingLineEntity.setPartner_item_barcode(barcode.get(0));
                }

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCodeId,
                        languageId,
                        plantId,
                        warehouseId);
                statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
                dbStagingLineEntity.setStatusDescription(statusDescription);

                dbStagingLineEntity.setCompanyDescription(description.getCompanyDesc());
                dbStagingLineEntity.setPlantDescription(description.getPlantDesc());
                dbStagingLineEntity.setWarehouseDescription(description.getWarehouseDesc());
                dbStagingLineEntity.setStagingNo(stagingNo);

                dbStagingLineEntity.setManufacturerCode(newStagingLine.getManufacturerName());
                dbStagingLineEntity.setManufacturerName(newStagingLine.getManufacturerName());
                dbStagingLineEntity.setDeletionIndicator(0L);
                dbStagingLineEntity.setCreatedBy(loginUserID);
                dbStagingLineEntity.setUpdatedBy(loginUserID);
                dbStagingLineEntity.setCreatedOn(new Date());
                dbStagingLineEntity.setUpdatedOn(new Date());
                stagingLineEntityList.add(dbStagingLineEntity);
                // PreInboundNo
                preInboundNo = dbStagingLineEntity.getPreInboundNo();
                // refDocNumber
                refDocNumber = dbStagingLineEntity.getRefDocNumber();
                caseCodeForCaseConfirmation = caseCode;
                //ManufactureCode
                manufactureCode = dbStagingLineEntity.getManufacturerCode();
                // CaseConfirmation preparation for creating caseConfirmation
                CaseConfirmation caseConfirmation = new CaseConfirmation();
                caseConfirmation.setWarehouseId(warehouseId);
                caseConfirmation.setPreInboundNo(preInboundNo);
                caseConfirmation.setRefDocNumber(dbStagingLineEntity.getRefDocNumber());
                caseConfirmation.setStagingNo(dbStagingLineEntity.getStagingNo());
                caseConfirmation.setPalletCode(dbStagingLineEntity.getPalletCode());
                caseConfirmation.setCaseCode(caseCode);
                caseConfirmation.setLineNo(dbStagingLineEntity.getLineNo());
                caseConfirmation.setItemCode(dbStagingLineEntity.getItemCode());
                caseConfirmation.setManufactureCode(manufactureCode);
                caseConfirmationList.add(caseConfirmation);
            }
        }

        // Batch Insert
        if (!stagingLineEntityList.isEmpty()) {
            List<StagingLineEntityV2> createdStagingLineEntityList = stagingLineV2Repository.saveAll(stagingLineEntityList);
            log.info("created StagingLine records." + stagingLineEntityList);
            //update INV_QTY in stagingLine - calling stored procedure
            stagingLineV2Repository.updateStagingLineInvQtyUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

            // Update PreInboundLines
            List<PreInboundLineEntityV2> preInboundLineList = getPreInboundLineV2(preInboundNo);
            statusDescription = stagingLineV2Repository.getStatusDescription(13L, languageId);
            preInboundLineList.stream().forEach(x -> {
                // STATUS_ID - Hard Coded Value "13"
                x.setStatusId(13L);
                x.setStatusDescription(statusDescription);
            });
            List<PreInboundLineEntityV2> updatedList = preInboundLineV2Repository.saveAll(preInboundLineList);
            log.info("updated PreInboundLineEntityV2 : " + updatedList);

            // Create CaseConfirmation
            List<StagingLineEntityV2> responseStagingLineEntityList =
                    caseConfirmationV2(caseConfirmationList, caseCodeForCaseConfirmation,
                            companyCodeId, plantId, languageId, loginUserID);

            return responseStagingLineEntityList;
        }
        return null;
    }

    /**
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderProcess(StagingHeaderV2 stagingHeaderV2, StagingLineEntityV2 stagingLineEntityV2)
            throws Exception {
        GrHeaderV2 newGrHeader = new GrHeaderV2();
        BeanUtils.copyProperties(stagingHeaderV2, newGrHeader, CommonUtils.getNullPropertyNames(stagingHeaderV2));

        // GR_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_CODE = 5;
        String nextGRHeaderNumber = getNextRangeNumber(NUM_RAN_CODE,
                stagingHeaderV2.getCompanyCode(),
                stagingHeaderV2.getPlantId(),
                stagingHeaderV2.getLanguageId(),
                stagingHeaderV2.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        newGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);

        newGrHeader.setPalletCode(stagingLineEntityV2.getPalletCode());
        newGrHeader.setCaseCode(stagingLineEntityV2.getCaseCode());
        newGrHeader.setMiddlewareId(stagingHeaderV2.getMiddlewareId());
        newGrHeader.setMiddlewareTable(stagingHeaderV2.getMiddlewareTable());
        newGrHeader.setReferenceDocumentType(stagingHeaderV2.getReferenceDocumentType());
        newGrHeader.setManufacturerFullName(stagingHeaderV2.getManufacturerFullName());
        newGrHeader.setManufacturerName(stagingHeaderV2.getManufacturerFullName());
        newGrHeader.setTransferOrderDate(stagingHeaderV2.getTransferOrderDate());
        newGrHeader.setIsCompleted(stagingHeaderV2.getIsCompleted());
        newGrHeader.setIsCancelled(stagingHeaderV2.getIsCancelled());
        newGrHeader.setMUpdatedOn(stagingHeaderV2.getMUpdatedOn());
        newGrHeader.setSourceBranchCode(stagingHeaderV2.getSourceBranchCode());
        newGrHeader.setSourceCompanyCode(stagingHeaderV2.getSourceCompanyCode());
        newGrHeader.setDeletionIndicator(0L);
        newGrHeader.setCreatedBy(stagingHeaderV2.getCreatedBy());
        newGrHeader.setUpdatedBy(stagingHeaderV2.getCreatedBy());
        newGrHeader.setCreatedOn(new Date());
        newGrHeader.setUpdatedOn(new Date());

        // STATUS_ID
        newGrHeader.setStatusId(16L);
        statusDescription = stagingLineV2Repository.getStatusDescription(16L, stagingHeaderV2.getLanguageId());
        newGrHeader.setStatusDescription(statusDescription);

        return newGrHeader;
    }


    /**
     *
     * @param companyCodeId c_id
     * @param plantId pl_id
     * @param languageId lang_id
     * @param warehouseId wh_id
     * @param refDocNumber ref_doc
     * @param preInboundNo pre_ib
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public void updateStagingLineInOrderProcess(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String refDocNumber, String preInboundNo) {
        stagingLineV2Repository.updateStagingLineInvQty(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        log.info("Staging Line Inventory Qty Update Completed ------------------------------->");
    }

    /**
     * @param numberOfCases
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public List<String> generateNumberRanges(Long numberOfCases, String warehouseId, String companyCodeId,
                                             String plantId, String languageId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        List<String> numberRanges = new ArrayList<>();
        for (int i = 0; i < numberOfCases; i++) {
            String nextRangeNumber = mastersService.getNextNumberRange(4L, warehouseId, companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
            numberRanges.add(nextRangeNumber);
        }
        return numberRanges;
    }

    /**
     * @param preInboundNo
     * @return
     */
    public List<PreInboundLineEntityV2> getPreInboundLineV2(String preInboundNo) {
        List<PreInboundLineEntityV2> preInboundLines =
                preInboundLineV2Repository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
        if (preInboundLines.isEmpty()) {
            throw new BadRequestException("The given values: preInboundNo:" + preInboundNo +
                    " doesn't exist.");
        }
        preInboundLines.forEach(preInboundLine -> {
            var quantity = inboundLineV2Repository.getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(
                    preInboundLine.getItemCode(), preInboundLine.getRefDocNumber(), preInboundLine.getPreInboundNo(),
                    preInboundLine.getLineNo(), preInboundLine.getWarehouseId());
            preInboundLine.setReferenceField5(quantity != null ? quantity.toString() : null);
        });
        return preInboundLines;
    }

    /**
     * @param caseConfirmations
     * @param caseCode
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public List<StagingLineEntityV2> caseConfirmationV2(List<CaseConfirmation> caseConfirmations,
                                                        String caseCode, String companyCodeId, String plantId,
                                                        String languageId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        log.info("caseConfirmation--called----> : " + caseConfirmations);

        List<StagingLineEntityV2> updatedStagingLineEntityList = new ArrayList<>();

        for (CaseConfirmation caseConfirmation : caseConfirmations) {
            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(caseConfirmation.getWarehouseId(),
                    companyCodeId, plantId, languageId,
                    caseConfirmation.getPreInboundNo(),
                    caseConfirmation.getRefDocNumber(),
                    caseConfirmation.getStagingNo(),
                    caseConfirmation.getPalletCode(),
                    caseConfirmation.getCaseCode(),
                    caseConfirmation.getLineNo(),
                    caseConfirmation.getItemCode(),
                    caseConfirmation.getManufactureCode());

            // update STATUS_ID value as 14
            dbStagingLineEntity.setStatusId(14L);
            statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
            dbStagingLineEntity.setStatusDescription(statusDescription);
            dbStagingLineEntity.setCaseCode(caseCode);
            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            StagingLineEntityV2 updatedStagingLineEntity = stagingLineV2Repository.save(dbStagingLineEntity);

            log.info("updatedStagingLineEntity------> : " + updatedStagingLineEntity);
            updatedStagingLineEntityList.add(updatedStagingLineEntity);

            if (updatedStagingLineEntity != null) {
                // STATUS updates
                List<StagingLineEntityV2> stagingLineEntity =
                        stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
                                languageId, companyCodeId, plantId, caseConfirmation.getWarehouseId(), caseConfirmation.getPreInboundNo(),
                                caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), caseConfirmation.getLineNo(),
                                caseConfirmation.getItemCode(), caseCode, 0L);

                boolean isStatus14 = false;

                List<Long> statusList = stagingLineEntity.stream().map(StagingLineEntity::getStatusId).collect(Collectors.toList());
                long statusIdCount = statusList.stream().filter(a -> a == 14L).count();

                log.info("status count : " + (statusIdCount == statusList.size()));

                isStatus14 = (statusIdCount == statusList.size());

                //-----logic for checking all records as 14 then only it should go to update header---issue--#5-------------
                if (!stagingLineEntity.isEmpty() && isStatus14) {
                    StagingHeaderV2 updateStagingHeader = new StagingHeaderV2();
                    updateStagingHeader.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    updateStagingHeader.setStatusDescription(statusDescription);
                    StagingHeaderV2 stagingHeader = updateStagingHeaderV2(companyCodeId, plantId, languageId,
                                    caseConfirmation.getWarehouseId(),
                                    caseConfirmation.getPreInboundNo(),
                                    caseConfirmation.getRefDocNumber(),
                                    caseConfirmation.getStagingNo(),
                                    loginUserID,
                                    updateStagingHeader);

                    log.info("stagingHeader : " + stagingHeader);

                    InboundLineV2 updateInboundLine = new InboundLineV2();
                    updateInboundLine.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    updateInboundLine.setStatusDescription(statusDescription);
                    InboundLineV2 inboundLine = updateInboundLineV2(companyCodeId, plantId, languageId,
                            caseConfirmation.getWarehouseId(),
                            caseConfirmation.getRefDocNumber(),
                            caseConfirmation.getPreInboundNo(),
                            caseConfirmation.getLineNo(),
                            caseConfirmation.getItemCode(),
                            loginUserID, updateInboundLine);
                    log.info("inboundLine : " + inboundLine);
                }
            }
        }

        // Record Insertion in GRHEADER table
        if (updatedStagingLineEntityList != null && !updatedStagingLineEntityList.isEmpty()) {

            StagingLineEntityV2 updatedStagingLineEntity = updatedStagingLineEntityList.get(0);

            log.info("updatedStagingLineEntity-----IN---GRHEADER---CREATION---> : " + updatedStagingLineEntity);

            GrHeaderV2 addGrHeader = new GrHeaderV2();
            BeanUtils.copyProperties(updatedStagingLineEntity, addGrHeader, CommonUtils.getNullPropertyNames(updatedStagingLineEntity));

            // GR_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_CODE = 5;
            String nextGRHeaderNumber = mastersService.getNextNumberRange(NUM_RAN_CODE,
                    updatedStagingLineEntity.getWarehouseId(),
                    updatedStagingLineEntity.getCompanyCode(),
                    updatedStagingLineEntity.getPlantId(),
                    updatedStagingLineEntity.getLanguageId(),
                    authTokenForIDMasterService.getAccess_token());
            addGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);

            List<StagingHeaderV2> stagingHeaderList = getStagingHeaderV2(
                    addGrHeader.getCompanyCode(), addGrHeader.getPlantId(), addGrHeader.getLanguageId(),
                    addGrHeader.getWarehouseId(), addGrHeader.getPreInboundNo(), addGrHeader.getRefDocNumber());

            for (StagingHeaderV2 stagingHeaderV2 : stagingHeaderList) {
                addGrHeader.setTransferOrderDate(stagingHeaderV2.getTransferOrderDate());
                addGrHeader.setIsCompleted(stagingHeaderV2.getIsCompleted());
                addGrHeader.setIsCancelled(stagingHeaderV2.getIsCancelled());
                addGrHeader.setMUpdatedOn(stagingHeaderV2.getMUpdatedOn());
                addGrHeader.setSourceBranchCode(stagingHeaderV2.getSourceBranchCode());
                addGrHeader.setSourceCompanyCode(stagingHeaderV2.getSourceCompanyCode());
                addGrHeader.setCustomerCode(stagingHeaderV2.getCustomerCode());
                addGrHeader.setTransferRequestType(stagingHeaderV2.getTransferRequestType());
            }

            // STATUS_ID
            addGrHeader.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            addGrHeader.setStatusDescription(statusDescription);
            GrHeaderV2 createdGrHeader = createGrHeaderV2(addGrHeader, loginUserID);
            log.info("createdGrHeader : " + createdGrHeader);
            if (createdGrHeader != null && createdGrHeader.getInboundOrderTypeId() == 5) {  //Direct Stock Receipt Condition
                createGrLine(createdGrHeader);
            }
        }

        return updatedStagingLineEntityList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @param itemCode
     * @return
     */
    public StagingLineEntityV2 getStagingLineV2(String warehouseId, String companyCodeId,
                                                String plantId, String languageId, String preInboundNo,
                                                String refDocNumber, String stagingNo,
                                                String palletCode, String caseCode,
                                                Long lineNo, String itemCode, String manufacturerCode) {
        Optional<StagingLineEntityV2> stagingLineV2 = stagingLineV2Repository
                .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndManufacturerCodeAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        stagingNo,
                        palletCode,
                        caseCode,
                        lineNo,
                        itemCode,
                        manufacturerCode,
                        0L);
        if (stagingLineV2.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",stagingNo: " + stagingNo +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    ",manufacturerCode: " + manufacturerCode +
                    " doesn't exist.");
        }

        return stagingLineV2.get();
    }

    /**
     * @param newGrHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public GrHeaderV2 createGrHeaderV2(GrHeaderV2 newGrHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<GrHeaderV2> grheader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
                        newGrHeader.getLanguageId(),
                        newGrHeader.getCompanyCode(),
                        newGrHeader.getPlantId(),
                        newGrHeader.getWarehouseId(),
                        newGrHeader.getPreInboundNo(),
                        newGrHeader.getRefDocNumber(),
                        newGrHeader.getStagingNo(),
                        newGrHeader.getGoodsReceiptNo(),
                        newGrHeader.getPalletCode(),
                        newGrHeader.getCaseCode(),
                        0L);
        if (!grheader.isEmpty()) {
            // Exception Log
            throw new BadRequestException("Record is getting duplicated with the given values");
        }

        IKeyValuePair description = stagingLineV2Repository.getDescription(newGrHeader.getCompanyCode(),
                newGrHeader.getLanguageId(),
                newGrHeader.getPlantId(),
                newGrHeader.getWarehouseId());

        newGrHeader.setCompanyCode(newGrHeader.getCompanyCode());
        newGrHeader.setPlantId(newGrHeader.getPlantId());

        newGrHeader.setCompanyDescription(description.getCompanyDesc());
        newGrHeader.setPlantDescription(description.getPlantDesc());
        newGrHeader.setWarehouseDescription(description.getWarehouseDesc());

        newGrHeader.setMiddlewareId(newGrHeader.getMiddlewareId());
        newGrHeader.setMiddlewareTable(newGrHeader.getMiddlewareTable());
        newGrHeader.setManufacturerFullName(newGrHeader.getManufacturerFullName());
        newGrHeader.setReferenceDocumentType(newGrHeader.getReferenceDocumentType());

        newGrHeader.setDeletionIndicator(0L);
        newGrHeader.setCreatedBy(loginUserID);
        newGrHeader.setUpdatedBy(loginUserID);
        newGrHeader.setCreatedOn(new Date());
        newGrHeader.setUpdatedOn(new Date());
        return grHeaderV2Repository.save(newGrHeader);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param loginUserID
     * @param updateStagingHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public StagingHeaderV2 updateStagingHeaderV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                                 String loginUserID, StagingHeaderV2 updateStagingHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StagingHeaderV2 dbStagingHeader = getStagingHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo);
        BeanUtils.copyProperties(updateStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(updateStagingHeader));
        dbStagingHeader.setUpdatedBy(loginUserID);
        dbStagingHeader.setUpdatedOn(new Date());
        return stagingHeaderV2Repository.save(dbStagingHeader);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 getStagingHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                              String preInboundNo, String refDocNumber, String stagingNo) {
        log.info("Staging Header value : " + languageId + "," + companyCode
                + "," + plantId + "," + warehouseId + "," + refDocNumber + "," + preInboundNo + "," + stagingNo);

        Optional<StagingHeaderV2> stagingHeader =
                stagingHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        stagingNo,
                        0L);
        log.info("stagingHeader : " + stagingHeader);
        if (stagingHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",stagingNo: " + stagingNo + " doesn't exist.");
        }

        return stagingHeader.get();
    }

    /**
     * @param grHeader
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws java.text.ParseException
     */
    public void createGrLine(GrHeaderV2 grHeader) throws InvocationTargetException, IllegalAccessException, java.text.ParseException {

        List<StagingLineEntityV2> stagingLineEntityList = getStagingLineForGrLine(grHeader.getCompanyCode(),
                grHeader.getPlantId(), grHeader.getLanguageId(), grHeader.getWarehouseId(),
                grHeader.getStagingNo(), grHeader.getCaseCode());

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        Double itemLength = 0D;
        Double itemWidth = 0D;
        Double itemHeight = 0D;
        Double orderQty = 0D;
        Double cbm = 0D;
        Double cbmPerQty = 0D;

        String hhtUser = "DirecStockReceipt";

        List<AddGrLineV2> newGrLineList = new ArrayList<>();
        for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
            List<PackBarcode> packBarcodeList = new ArrayList<>();
            AddGrLineV2 newGrLine = new AddGrLineV2();
            PackBarcode newPackBarcode = new PackBarcode();
            BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));
            long NUM_RAN_ID = 6;
            String nextRangeNumber = mastersService.getNextNumberRange(NUM_RAN_ID, dbStagingLine.getWarehouseId(), dbStagingLine.getCompanyCode(),
                    dbStagingLine.getPlantId(), dbStagingLine.getLanguageId(), authTokenForIDMasterService.getAccess_token());

            boolean capacityCheck = false;
            boolean storageBinCapacityCheck = false;

            ImBasicData imBasicData = new ImBasicData();
            imBasicData.setCompanyCodeId(dbStagingLine.getCompanyCode());
            imBasicData.setPlantId(dbStagingLine.getPlantId());
            imBasicData.setLanguageId(dbStagingLine.getLanguageId());
            imBasicData.setWarehouseId(dbStagingLine.getWarehouseId());
            imBasicData.setItemCode(dbStagingLine.getItemCode());
            imBasicData.setManufacturerName(dbStagingLine.getManufacturerName());
            ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
            log.info("ImbasicData1 : " + itemCodeCapacityCheck);
            if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                log.info("capacity Check: " + capacityCheck);
            }

            newPackBarcode.setQuantityType("A");
            newPackBarcode.setBarcode(nextRangeNumber);

            if (capacityCheck) {

                if (dbStagingLine.getOrderQty() != null) {
                    orderQty = dbStagingLine.getOrderQty();
                }
                if (itemCodeCapacityCheck.getLength() != null) {
                    itemLength = itemCodeCapacityCheck.getLength();
                }
                if (itemCodeCapacityCheck.getWidth() != null) {
                    itemWidth = itemCodeCapacityCheck.getWidth();
                }
                if (itemCodeCapacityCheck.getHeight() != null) {
                    itemHeight = itemCodeCapacityCheck.getHeight();
                }

                cbmPerQty = itemLength * itemWidth * itemHeight;
                cbm = orderQty * cbmPerQty;

                newPackBarcode.setCbmQuantity(cbmPerQty);
                newPackBarcode.setCbm(cbm);

                log.info("item Length, Width, Height, Volume[CbmPerQty], CBM: " + itemLength + ", " + itemWidth + "," + itemHeight + ", " + cbmPerQty + ", " + cbm);
            }
            if (!capacityCheck) {

                newPackBarcode.setCbmQuantity(0D);
                newPackBarcode.setCbm(0D);
            }

            packBarcodeList.add(newPackBarcode);
            newGrLine.setGoodReceiptQty(dbStagingLine.getOrderQty());
            newGrLine.setAcceptedQty(dbStagingLine.getOrderQty());
            newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
            newGrLine.setManufacturerFullName(dbStagingLine.getManufacturerFullName());
            newGrLine.setReferenceDocumentType(dbStagingLine.getReferenceDocumentType());
            newGrLine.setPurchaseOrderNumber(dbStagingLine.getPurchaseOrderNumber());
            if (dbStagingLine.getPartner_item_barcode() != null) {
                newGrLine.setBarcodeId(dbStagingLine.getPartner_item_barcode());
            }
            if (dbStagingLine.getPartner_item_barcode() == null) {
                newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
            }

            List<String> hhtUserOutputList = stagingLineV2Repository.getHhtUserByOrderType(
                    grHeader.getCompanyCode(), grHeader.getLanguageId(), grHeader.getPlantId(), grHeader.getWarehouseId(),
                    grHeader.getInboundOrderTypeId());
            if (hhtUserOutputList != null && !hhtUserOutputList.isEmpty()) {
                hhtUser = hhtUserOutputList.get(0);
            }
            if (hhtUserOutputList == null || hhtUserOutputList.isEmpty() || hhtUserOutputList.size() == 0) {
                List<String> hhtUserList = stagingLineV2Repository.getHhtUser(
                        grHeader.getCompanyCode(), grHeader.getLanguageId(), grHeader.getPlantId(), grHeader.getWarehouseId());
                if (hhtUserList != null && !hhtUserList.isEmpty()) {
                    hhtUser = hhtUserList.get(0);
                }
            }
            newGrLine.setAssignedUserId(hhtUser);

            newGrLine.setPackBarcodes(packBarcodeList);
            newGrLine.setInterimStorageBin("REC-AL-B2");

            newGrLineList.add(newGrLine);
        }
        List<GrLineV2> createGrLine = grLineService.createGrLineNonCBMV2(newGrLineList, grHeader.getCreatedBy());
        log.info("GrLine Created Successfully: " + createGrLine);

        List<PutAwayLineV2> createdPutawayLine = null;
        //putaway Confirm
        if (createGrLine != null && !createGrLine.isEmpty()) {
            log.info("Putaway line Confirm Initiated");
            List<PutAwayLineV2> createPutawayLine = new ArrayList<>();
//            for (GrLineV2 grLine : createGrLine) {

            List<PutAwayHeaderV2> dbPutawayHeaderList = getPutAwayHeaderV2(
                    createGrLine.get(0).getCompanyCode(),
                    createGrLine.get(0).getPlantId(),
                    createGrLine.get(0).getLanguageId(),
                    createGrLine.get(0).getWarehouseId(),
                    createGrLine.get(0).getRefDocNumber());
            log.info("Putaway header: " + dbPutawayHeaderList);

            if (dbPutawayHeaderList != null && !dbPutawayHeaderList.isEmpty()) {
                for (PutAwayHeaderV2 dbPutawayHeader : dbPutawayHeaderList) {
                    PutAwayLineV2 putAwayLine = new PutAwayLineV2();

                    List<GrLineV2> grLine = createGrLine.stream().filter(n -> n.getPackBarcodes().equalsIgnoreCase(dbPutawayHeader.getPackBarcodes())).collect(Collectors.toList());

                    BeanUtils.copyProperties(grLine.get(0), putAwayLine, CommonUtils.getNullPropertyNames(grLine.get(0)));
                    putAwayLine.setProposedStorageBin(dbPutawayHeader.getProposedStorageBin());
                    putAwayLine.setConfirmedStorageBin(dbPutawayHeader.getProposedStorageBin());
                    putAwayLine.setPutawayConfirmedQty(dbPutawayHeader.getPutAwayQuantity());
                    putAwayLine.setPutAwayNumber(dbPutawayHeader.getPutAwayNumber());
                    createPutawayLine.add(putAwayLine);
                }
            }
            createdPutawayLine = grLineService.putAwayLineConfirmV2(createPutawayLine, grHeader.getCreatedBy());
            log.info("PutawayLine Confirmed: " + createdPutawayLine);
        }
        if (createdPutawayLine != null && !createdPutawayLine.isEmpty()) {
            log.info("Direct StockReceipt - Inbound Confirmation Process Initiated");
            grLineService.updateInboundHeaderPartialConfirmV2(
                    grHeader.getCompanyCode(),
                    grHeader.getPlantId(),
                    grHeader.getLanguageId(),
                    grHeader.getWarehouseId(),
                    grHeader.getPreInboundNo(),
                    grHeader.getRefDocNumber(),
                    grHeader.getCreatedBy());
            log.info("Direct Stock Receipt - Inbound Order Confirmed Successfully");
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param stagingNo
     * @param caseCode
     * @return
     */
    public List<StagingLineEntityV2> getStagingLineForGrLine(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String stagingNo, String caseCode) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndStagingNoAndCaseCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        stagingNo,
                        caseCode,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            return null;
        }
        return StagingLineEntity;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param loginUserID
     * @param updateInboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public InboundLineV2 updateInboundLineV2(String companyCode, String plantId,
                                             String languageId, String warehouseId,
                                             String refDocNumber, String preInboundNo,
                                             Long lineNo, String itemCode,
                                             String loginUserID, InboundLineV2 updateInboundLine)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        InboundLineV2 dbInboundLine = getInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        BeanUtils.copyProperties(updateInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(updateInboundLine));
        dbInboundLine.setUpdatedBy(loginUserID);
        dbInboundLine.setUpdatedOn(new Date());
        return inboundLineV2Repository.save(dbInboundLine);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @return
     */
    public InboundLineV2 getInboundLineV2(String companyCode, String plantId,
                                          String languageId, String warehouseId, String refDocNumber,
                                          String preInboundNo, Long lineNo, String itemCode) {
        Optional<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",temCode: " + itemCode + " doesn't exist.");
        }

        return inboundLine.get();
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<StagingHeaderV2> getStagingHeaderV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String preInboundNo, String refDocNumber) {
        List<StagingHeaderV2> stagingHeader =
                stagingHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L);
        log.info("stagingHeader : " + stagingHeader);
        if (stagingHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    " doesn't exist.");
        }
        return stagingHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<Long> statusIds = Arrays.asList(19L, 20L);
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        statusIds,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public StagingLineEntityV2 getStagingLineForPutAwayLineV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preInboundNo,
                                                              String refDocNumber, Long lineNo, String itemCode, String manufacturerName) {
        Optional<StagingLineEntityV2> stagingLineV2 = stagingLineV2Repository
                .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        manufacturerName,
                        0L);
        if (stagingLineV2 == null || stagingLineV2.isEmpty()) {
            return null;
        }
        log.info("dbStagingLine: " + stagingLineV2.get());
        return stagingLineV2.get();
    }

}
