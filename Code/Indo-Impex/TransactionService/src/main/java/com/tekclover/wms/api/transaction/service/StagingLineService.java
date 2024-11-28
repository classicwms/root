package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.*;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.SearchStagingLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.StagingLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.StagingLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class StagingLineService extends BaseService {

    @Autowired
    private StagingLineRepository stagingLineEntityRepository;

    @Autowired
    private StagingHeaderService stagingHeaderService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private PreInboundLineService preInboundLineService;

    @Autowired
    private PreInboundLineRepository preInboundLineRepository;

    @Autowired
    private GrHeaderService grHeaderService;

    @Autowired
    private IDMasterService idmasterService;

    @Autowired
    private AuthTokenService authTokenService;

    //----------------------------------------------------------------------------------------
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    @Autowired
    private PutAwayLineService putAwayLineService;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    private ImPartnerService imPartnerService;

    @Autowired
    private ErrorLogRepository exceptionLogRepo;
    //----------------------------------------------------------------------------------------

    /**
     * getStagingLineEntitys
     * @return
     */
    public List<StagingLineEntity> getStagingLines() {
        List<StagingLineEntity> stagingLineEntityList = stagingLineEntityRepository.findAll();
        stagingLineEntityList = stagingLineEntityList.stream().filter(n -> n.getDeletionIndicator() != null &&
                n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stagingLineEntityList;
    }

    /**
     * getStagingLineEntity
     * @param palletCode
     * @return
     */
    public StagingLineEntity getStagingLine(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                            String palletCode, String caseCode, Long lineNo, String itemCode) {
        Optional<StagingLineEntity> StagingLineEntity = stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
                getLanguageId(),
                getCompanyCode(),
                getPlantId(),
                warehouseId,
                preInboundNo,
                refDocNumber,
                stagingNo,
                palletCode,
                caseCode,
                lineNo,
                itemCode,
                0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  ",stagingNo: " + stagingNo +
                                                  ",palletCode: " + palletCode +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }

        return StagingLineEntity.get();
    }

    /**
     * getStagingLineEntity
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<StagingLineEntity> getStagingLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                                  String itemCode) {
        List<StagingLineEntity> StagingLineEntity =
                stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }

        return StagingLineEntity;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getStagingLineByStatusId(String warehouseId, String preInboundNo, String refDocNumber) {
        long putAwayLineStatusIdCount = stagingLineEntityRepository.getStagingLineCountByStatusId(getCompanyCode(), getPlantId(), warehouseId, preInboundNo, refDocNumber);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param caseCode
     * @return
     */
    public List<StagingLineEntity> getStagingLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                                  String itemCode, String caseCode) {
        List<StagingLineEntity> StagingLineEntity =
                stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        caseCode,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  ",caseCode: " + caseCode +
                                                  " doesn't exist.");
        }

        return StagingLineEntity;
    }

    /**
     * @param searchStagingLine
     * @return
     * @throws ParseException
     */
    public List<StagingLineEntity> findStagingLine(SearchStagingLine searchStagingLine)
            throws ParseException {
        StagingLineSpecification spec = new StagingLineSpecification(searchStagingLine);
        List<StagingLineEntity> searchResults = stagingLineEntityRepository.findAll(spec);
//		log.info("searchResults: " + searchResults);

        List<StagingLineEntity> stagingLineEntityList =
                searchResults.stream().filter(n -> n.getDeletionIndicator() != null &&
                        n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stagingLineEntityList;
    }

    /**
     * @param newStagingLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StagingLine> createStagingLine(List<AddStagingLine> newStagingLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<StagingLineEntity> stagingLineEntityList = new ArrayList<>();
        String preInboundNo = null;

        for (AddStagingLine newStagingLine : newStagingLines) {
            log.info("newStagingLineEntity : " + newStagingLine);
            if (newStagingLine.getCaseCode() == null) {
                throw new BadRequestException("CaseCode is not presented.");
            }

            // Warehouse
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            Warehouse warehouse = idmasterService.getWarehouse(newStagingLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
            // Insert based on the number of casecodes

            for (String caseCode : newStagingLine.getCaseCode()) {
                StagingLineEntity dbStagingLineEntity = new StagingLineEntity();
                BeanUtils.copyProperties(newStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(newStagingLine));
                dbStagingLineEntity.setCaseCode(caseCode);
                dbStagingLineEntity.setPalletCode(caseCode);    //Copy CASE_CODE

                // STATUS_ID - Hard Coded Value "13"
                dbStagingLineEntity.setStatusId(13L);

                // C_ID
                dbStagingLineEntity.setCompanyCode(warehouse.getCompanyCode());

                // PLANT_ID
                dbStagingLineEntity.setPlantId(warehouse.getPlantId());

                // LANG_ID
                dbStagingLineEntity.setLanguageId(LANG_ID);

                dbStagingLineEntity.setDeletionIndicator(0L);
                dbStagingLineEntity.setCreatedBy(loginUserID);
                dbStagingLineEntity.setUpdatedBy(loginUserID);
                dbStagingLineEntity.setCreatedOn(new Date());
                dbStagingLineEntity.setUpdatedOn(new Date());
                stagingLineEntityList.add(dbStagingLineEntity);

                // PreInboundNo
                preInboundNo = dbStagingLineEntity.getPreInboundNo();
            }
        }

        // Batch Insert
        if (!stagingLineEntityList.isEmpty()) {
            List<StagingLineEntity> createdStagingLineEntityList =
                    stagingLineEntityRepository.saveAll(stagingLineEntityList);
            log.info("created StagingLine records." + createdStagingLineEntityList);

            List<StagingLine> responseStagingLineList = new ArrayList<>();
            for (StagingLineEntity stagingLineEntity : createdStagingLineEntityList) {
                responseStagingLineList.add(copyLineEntityToBean(stagingLineEntity));
            }

            // Update PreInboundLines
            List<PreInboundLineEntity> preInboundLineList = preInboundLineService.getPreInboundLine(preInboundNo);
            for (PreInboundLineEntity preInboundLineEntity : preInboundLineList) {
                // STATUS_ID - Hard Coded Value "13"
                preInboundLineEntity.setStatusId(13L);
            }
            List<PreInboundLineEntity> updatedList = preInboundLineRepository.saveAll(preInboundLineList);
            log.info("updated PreInboundLineEntity : " + updatedList);
            return responseStagingLineList;
        }

        return null;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @param itemCode
     * @param loginUserID
     * @param updateStagingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingLineEntity updateStagingLine(String warehouseId,
                                               String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo,
                                               String itemCode, String loginUserID, UpdateStagingLine updateStagingLine)
            throws IllegalAccessException, InvocationTargetException {
        StagingLineEntity dbStagingLineEntity = getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                                                               caseCode, lineNo, itemCode);
        BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
        dbStagingLineEntity.setUpdatedBy(loginUserID);
        dbStagingLineEntity.setUpdatedOn(new Date());
        return stagingLineEntityRepository.save(dbStagingLineEntity);
    }

    /**
     * @param assignHHTUsers
     * @param assignedUserId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StagingLineEntity> assignHHTUser(List<AssignHHTUser> assignHHTUsers, String assignedUserId,
                                                 String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<StagingLineEntity> updatedStagingLineEntityList = new ArrayList<>();
        for (AssignHHTUser assignHHTUser : assignHHTUsers) {
            StagingLineEntity dbStagingLineEntity = getStagingLine(assignHHTUser.getWarehouseId(), assignHHTUser.getPreInboundNo(),
                                                                   assignHHTUser.getRefDocNumber(), assignHHTUser.getStagingNo(), assignHHTUser.getPalletCode(),
                                                                   assignHHTUser.getCaseCode(), assignHHTUser.getLineNo(), assignHHTUser.getItemCode());
            dbStagingLineEntity.setAssignedUserId(assignedUserId);
            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            StagingLineEntity updatedStagingLineEntity = stagingLineEntityRepository.save(dbStagingLineEntity);
            updatedStagingLineEntityList.add(updatedStagingLineEntity);
        }
        return updatedStagingLineEntityList;
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<StagingLineEntity> StagingLineEntitys = getStagingLines();
        StagingLineEntitys.forEach(stagLines -> stagLines.setReferenceField1(asnNumber));
        stagingLineEntityRepository.saveAll(StagingLineEntitys);
    }

    /**
     * @param caseConfirmations
     * @param caseCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StagingLineEntity> caseConfirmation(List<CaseConfirmation> caseConfirmations, String caseCode,
                                                    String loginUserID) throws IllegalAccessException, InvocationTargetException {
        log.info("caseConfirmation--called----> : " + caseConfirmations);

        List<StagingLineEntity> updatedStagingLineEntityList = new ArrayList<>();
        for (CaseConfirmation caseConfirmation : caseConfirmations) {
            StagingLineEntity dbStagingLineEntity = getStagingLine(caseConfirmation.getWarehouseId(),
                                                                   caseConfirmation.getPreInboundNo(), caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(),
                                                                   caseConfirmation.getPalletCode(), caseConfirmation.getCaseCode(), caseConfirmation.getLineNo(), caseConfirmation.getItemCode());

            // update STATUS_ID value as 14
            dbStagingLineEntity.setStatusId(14L);
            dbStagingLineEntity.setCaseCode(caseCode);
            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            StagingLineEntity updatedStagingLineEntity = stagingLineEntityRepository.save(dbStagingLineEntity);
            log.info("updatedStagingLineEntity------> : " + updatedStagingLineEntity);
            updatedStagingLineEntityList.add(updatedStagingLineEntity);

            if (updatedStagingLineEntity != null) {
                // STATUS updates
                /*
                 * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO/IB_LINE_NO/ITM_CODE/CASE_CODE values in StagingLineEntity table and
                 * validate STATUS_ID , if all the STATUS_ID values of the CASE_CODE are 14
                 */
                List<StagingLineEntity> stagingLineEntity =
                        stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
                                getLanguageId(), getCompanyCode(), getPlantId(), caseConfirmation.getWarehouseId(), caseConfirmation.getPreInboundNo(),
                                caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), caseConfirmation.getLineNo(),
                                caseConfirmation.getItemCode(), caseCode, 0L);

                boolean isStatus14 = false;
                List<Long> statusList = stagingLineEntity.stream().map(StagingLineEntity::getStatusId).collect(Collectors.toList());
                long statusIdCount = statusList.stream().filter(a -> a == 14L).count();
                log.info("status count : " + (statusIdCount == statusList.size()));

                isStatus14 = (statusIdCount == statusList.size());

                //-----logic for checking all records as 14 then only it should go to update header---issue--#5-------------
                if (!stagingLineEntity.isEmpty() && isStatus14) {
                    /*
                     * 1. Then pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO in STAGINGHEADER table and
                     * update the STATUS_ID as 14,ST_CNF_BY and ST_CNF_ON fields
                     */
                    UpdateStagingHeader updateStagingHeader = new UpdateStagingHeader();
                    updateStagingHeader.setStatusId(14L);
                    StagingHeader stagingHeader =
                            stagingHeaderService.updateStagingHeader(caseConfirmation.getWarehouseId(), caseConfirmation.getPreInboundNo(),
                                                                     caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), loginUserID,
                                                                     updateStagingHeader);
                    log.info("stagingHeader : " + stagingHeader);

                    /*
                     * 2. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
                     * updated STATUS_ID as 14
                     */
                    UpdateInboundLine updateInboundLine = new UpdateInboundLine();
                    updateInboundLine.setStatusId(14L);
                    InboundLine inboundLine = inboundLineService.updateInboundLine(caseConfirmation.getWarehouseId(),
                                                                                   caseConfirmation.getRefDocNumber(), caseConfirmation.getPreInboundNo(), caseConfirmation.getLineNo(),
                                                                                   caseConfirmation.getItemCode(), loginUserID, updateInboundLine);
                    log.info("inboundLine : " + inboundLine);
                }
            }
        }

        // Record Insertion in GRHEADER table
        if (!updatedStagingLineEntityList.isEmpty()) {
            StagingLineEntity updatedStagingLineEntity = updatedStagingLineEntityList.get(0);
            log.info("updatedStagingLineEntity-----IN---GRHEADER---CREATION---> : " + updatedStagingLineEntity);

            AddGrHeader addGrHeader = new AddGrHeader();
            BeanUtils.copyProperties(updatedStagingLineEntity, addGrHeader, CommonUtils.getNullPropertyNames(updatedStagingLineEntity));

            // GR_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_CODE = 5;
            String nextGRHeaderNumber = getNextRangeNumber(NUM_RAN_CODE, updatedStagingLineEntity.getWarehouseId(),
                                                           authTokenForIDMasterService.getAccess_token());
            addGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);

            // STATUS_ID
            addGrHeader.setStatusId(16L);

            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
            StatusId idStatus = idmasterService.getStatus(16L, updatedStagingLineEntity.getWarehouseId(), authTokenForIDService.getAccess_token());
            addGrHeader.setReferenceField10(idStatus.getStatus());
            GrHeader createdGrHeader = grHeaderService.createGrHeader(addGrHeader, loginUserID);
            log.info("createdGrHeader : " + createdGrHeader);
        }

        return updatedStagingLineEntityList;
    }

    /**
     * deleteStagingLineEntity
     * @param loginUserID
     * @param palletCode
     */
    public void deleteStagingLine(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                  String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID) {
        StagingLineEntity StagingLineEntity = getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                                                             caseCode, lineNo, itemCode);
        if (StagingLineEntity != null) {
            StagingLineEntity.setDeletionIndicator(1L);
            StagingLineEntity.setUpdatedBy(loginUserID);
            stagingLineEntityRepository.save(StagingLineEntity);
        } else {
            throw new BadRequestException("Error in deleting Id:  warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",stagingNo: " + stagingNo +
                                                  ",palletCode: " + palletCode +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }
    }

    /**
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param caseCode
     * @param loginUserID
     */
    public void deleteCases(String preInboundNo, Long lineNo, String itemCode, String caseCode, String loginUserID) {
        List<Long> statusIds = new ArrayList<>();
        statusIds.add(13L);
        statusIds.add(14L);
        List<StagingLineEntity> StagingLineEntitys =
                stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        preInboundNo,
                        lineNo,
                        itemCode,
                        statusIds,
                        0L
                );
        if (StagingLineEntitys == null) {
            throw new BadRequestException("Error in deleting Id: " +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }

        for (StagingLineEntity StagingLineEntity : StagingLineEntitys) {
            StagingLineEntity.setDeletionIndicator(1L);
            StagingLineEntity.setUpdatedBy(loginUserID);
            StagingLineEntity deletedStagingLineEntity = stagingLineEntityRepository.save(StagingLineEntity);
            log.info("deletedStagingLineEntity : " + deletedStagingLineEntity);
        }
    }

    /**
     * @param stagingLineEntity
     * @return
     */
    private StagingLine copyLineEntityToBean(StagingLineEntity stagingLineEntity) {
        StagingLine stagingLine = new StagingLine();
        BeanUtils.copyProperties(stagingLineEntity, stagingLine, CommonUtils.getNullPropertyNames(stagingLineEntity));
        return stagingLine;
    }

    //=========================================================V2================================================================================

    /**
     * getStagingLineEntitys
     * @return
     */
    public List<StagingLineEntityV2> getStagingLinesV2() {
        List<StagingLineEntityV2> stagingLineEntityList = stagingLineV2Repository.findAll();
        stagingLineEntityList = stagingLineEntityList.stream().filter(n -> n.getDeletionIndicator() != null &&
                n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stagingLineEntityList;
    }

    /**
     * getStagingLineEntity
     * @param palletCode
     * @return
     */
    public StagingLineEntityV2 getStagingLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String preInboundNo, String refDocNumber,
                                                String stagingNo, String palletCode, String caseCode, Long lineNo, String itemCode) {
        Optional<StagingLineEntityV2> StagingLineEntity = stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
                languageId,
                companyCode,
                plantId,
                warehouseId,
                preInboundNo,
                refDocNumber,
                stagingNo,
                palletCode,
                caseCode,
                lineNo,
                itemCode,
                0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  ",stagingNo: " + stagingNo +
                                                  ",palletCode: " + palletCode +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }

        return StagingLineEntity.get();
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @return
     */
    public StagingLineEntityV2 getStagingLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String preInboundNo, String refDocNumber,
                                                String stagingNo, String palletCode, String caseCode, Long lineNo) {
        Optional<StagingLineEntityV2> StagingLineEntity = stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndDeletionIndicator(
                languageId,
                companyCode,
                plantId,
                warehouseId,
                preInboundNo,
                refDocNumber,
                stagingNo,
                palletCode,
                caseCode,
                lineNo,
                0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  ",stagingNo: " + stagingNo +
                                                  ",palletCode: " + palletCode +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  " doesn't exist.");
        }

        return StagingLineEntity.get();
    }

    /**
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @param itemCode
     * @param manufacturerCode
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
        if (stagingLineV2.isEmpty()) {
            return null;
        }
        log.info("dbStagingLine: " + stagingLineV2.get());
        return stagingLineV2.get();
    }

    /**
     * getStagingLineEntity
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<StagingLineEntityV2> getStagingLineV2(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                                      String itemCode) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
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
     * @return
     */
    public List<StagingLineEntityV2> getStagingLineForGrConfirmV2(String companyCode, String plantId, String languageId,
                                                                  String warehouseId, String refDocNumber, String preInboundNo) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            return null;
        }

        return StagingLineEntity;
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
    public List<StagingLineEntityV2> getStagingLineForBarcodeUpdateV2(String companyCodeId, String plantId, String languageId,
                                                                      String warehouseId, String itemCode, String manufacturerName) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.getStagingLineList(
                        itemCode, manufacturerName, companyCodeId, plantId, languageId, warehouseId);
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
     * @return
     */
    public StagingLineEntityV2 getStagingLineForReversalV2(String companyCode, String plantId, String languageId,
                                                           String warehouseId, String refDocNumber, String preInboundNo,
                                                           String itemCode, String manufacturerName, Long lineNo) {
        StagingLineEntityV2 StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        manufacturerName,
                        0L);
        if (StagingLineEntity == null) {
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
     * @param itemCode
     * @param manufacturerName
     * @param caseCode
     * @param palletCode
     * @return
     */
    public StagingLineEntityV2 getStagingLineForReversalV2(String companyCode, String plantId, String languageId,
                                                           String warehouseId, String refDocNumber, String preInboundNo,
                                                           String itemCode, String manufacturerName, String caseCode, String palletCode) {
        StagingLineEntityV2 StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndCaseCodeAndPalletCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        caseCode,
                        palletCode,
                        0L);
        if (StagingLineEntity == null) {
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
     * @param itemCode
     * @param manufacturerName
     * @param caseCode
     * @param palletCode
     * @param lineNo
     * @return
     */
    public StagingLineEntityV2 getStagingLineForReversalV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                           String refDocNumber, String preInboundNo, String itemCode, String manufacturerName,
                                                           String caseCode, String palletCode, Long lineNo) {
        StagingLineEntityV2 StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndCaseCodeAndPalletCodeAndLineNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        caseCode,
                        palletCode,
                        lineNo,
                        0L);
        if (StagingLineEntity == null) {
            return null;
        }

        return StagingLineEntity;
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

    public List<StagingLineEntityV2> getStagingLineForGrLine(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String stagingNo, String refDocNumber, String preInboundNo) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndStagingNoAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        stagingNo,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            return null;
        }

        return StagingLineEntity;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getStagingLineByStatusIdV2(String companyId, String plantId, String warehouseId, String preInboundNo, String refDocNumber) {
        long putAwayLineStatusIdCount = stagingLineEntityRepository.getStagingLineCountByStatusId(companyId, plantId, warehouseId, preInboundNo, refDocNumber);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param caseCode
     * @return
     */
    public List<StagingLineEntityV2> getStagingLineV2(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                                      String itemCode, String caseCode) {
        List<StagingLineEntityV2> StagingLineEntity =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        caseCode,
                        0L);
        if (StagingLineEntity.isEmpty()) {
            // Exception Log
            createStagingLineLog3(languageId, companyCode, plantId, warehouseId, preInboundNo, refDocNumber, lineNo, itemCode,
                                  caseCode, "StagingLine with given values and refDocNumber-" + refDocNumber + " doesn't exists.");
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  ",caseCode: " + caseCode +
                                                  " doesn't exist.");
        }

        return StagingLineEntity;
    }

    /**
     * @param searchStagingLine
     * @return
     * @throws ParseException
     */
    public Stream<StagingLineEntityV2> findStagingLineV2(SearchStagingLineV2 searchStagingLine)
            throws ParseException {
        log.info("Search StagingLine Input: " + searchStagingLine);
        StagingLineV2Specification spec = new StagingLineV2Specification(searchStagingLine);
        Stream<StagingLineEntityV2> searchResults = stagingLineV2Repository.stream(spec, StagingLineEntityV2.class);
//		log.info("searchResults: " + searchResults);

        return searchResults;
    }

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
     */
    public List<StagingLineEntityV2> createStagingLineV2(List<PreInboundLineEntityV2> inputPreInboundLines,
                                                         String stagingNo, String warehouseId,
                                                         String companyCodeId, String plantId, String languageId,
                                                         String loginUserID) throws Exception {
        List<StagingLineEntityV2> stagingLineEntityList = new ArrayList<>();
        String preInboundNo = null;
        String refDocNumber = null;

        // Casecode needs to be created automatically by calling /({numberOfCases}/barcode) from StagingHeader
        Long numberOfCases = 1L;
        List<String> caseCodeList = stagingHeaderService.generateNumberRanges(numberOfCases, warehouseId, companyCodeId, plantId, languageId);
        if (caseCodeList == null || caseCodeList.isEmpty()) {
            throw new BadRequestException("CaseCode is not generated.");
        }

        String manufactureCode = null;
        String caseCodeForCaseConfirmation = null;
        List<CaseConfirmation> caseConfirmationList = new ArrayList<>();

        for (PreInboundLineEntityV2 newStagingLine : inputPreInboundLines) {
            log.info("newStagingLineEntity : " + newStagingLine);

            // Warehouse
//            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
//            Warehouse warehouse = idmasterService.getWarehouse(newStagingLine.getWarehouseId(),
//                    companyCodeId, plantId, languageId,
//                    authTokenForIDMasterService.getAccess_token());

            // Insert based on the number of casecodes
            for (String caseCode : caseCodeList) {
                StagingLineEntityV2 dbStagingLineEntity = new StagingLineEntityV2();
                BeanUtils.copyProperties(newStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(newStagingLine));
                dbStagingLineEntity.setCaseCode(caseCode);
                dbStagingLineEntity.setPalletCode(caseCode);    //Copy CASE_CODE

                // STATUS_ID - Hard Coded Value "13"
                dbStagingLineEntity.setStatusId(13L);

                // LANG_ID
                dbStagingLineEntity.setLanguageId(languageId);

                // C_ID
                dbStagingLineEntity.setCompanyCode(companyCodeId);

                // PLANT_ID
                dbStagingLineEntity.setPlantId(plantId);

                dbStagingLineEntity.setOrderQty(newStagingLine.getOrderQty());
                dbStagingLineEntity.setPartner_item_barcode(newStagingLine.getBarcodeId());

                /*
                 * Pass the C_ID,PLANT_ID,WH_ID,LANG_ID and ITM_CODE for each record of staging line table into inventory table and
                 * fetch the sum of INV_QTY+ ALLOC_QTY values and append in this field. If results are null append as Zero
                 */
                //code commented and approach changed to avoid deadlock, after save call update stagingline inventory
//                dbStagingLineEntity.setInventoryQuantity(
//                        stagingLineV2Repository.getTotalQuantityNew(newStagingLine.getItemCode(),
//                                warehouseId,
//                                languageId,
//                                plantId,
//                                companyCodeId));
                if(dbStagingLineEntity.getBarcodeId() == null) {
                //Pass ITM_CODE/SUPPLIER_CODE received in integration API into IMPARTNER table and fetch PARTNER_ITEM_BARCODE values. Values can be multiple
                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(newStagingLine.getItemCode(),
                                                                                     newStagingLine.getCompanyCode(),
                                                                                     newStagingLine.getPlantId(),
                                                                                     newStagingLine.getWarehouseId(),
                                                                                     newStagingLine.getManufacturerName(),
                                                                                     newStagingLine.getLanguageId());
                log.info("Barcode : " + barcode);
                //for interim only the following condition is used
//                if (barcode == null) {
//                    barcode = stagingLineV2Repository.getPartnerItemBarcode(newStagingLine.getItemCode(),
//                            newStagingLine.getManufacturerCode());
//                }
                if (barcode != null && !barcode.isEmpty()) {
//                    dbStagingLineEntity.setBarcodeId(barcode.replaceAll("\\s", "").trim());      //to remove white space
                    dbStagingLineEntity.setBarcodeId(barcode.get(0));
                }
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

                dbStagingLineEntity.setContainerNo(newStagingLine.getContainerNo());
                dbStagingLineEntity.setMiddlewareId(newStagingLine.getMiddlewareId());
                dbStagingLineEntity.setMiddlewareHeaderId(newStagingLine.getMiddlewareHeaderId());
                dbStagingLineEntity.setMiddlewareTable(newStagingLine.getMiddlewareTable());
                dbStagingLineEntity.setPurchaseOrderNumber(newStagingLine.getPurchaseOrderNumber());
                dbStagingLineEntity.setReferenceDocumentType(newStagingLine.getReferenceDocumentType());
                dbStagingLineEntity.setManufacturerFullName(newStagingLine.getManufacturerFullName());

                dbStagingLineEntity.setManufacturerCode(newStagingLine.getManufacturerName());
                dbStagingLineEntity.setManufacturerName(newStagingLine.getManufacturerName());
                dbStagingLineEntity.setManufacturerPartNo(newStagingLine.getManufacturerPartNo());

                dbStagingLineEntity.setBranchCode(newStagingLine.getBranchCode());
                dbStagingLineEntity.setTransferOrderNo(newStagingLine.getTransferOrderNo());
                dbStagingLineEntity.setIsCompleted(newStagingLine.getIsCompleted());
                dbStagingLineEntity.setBusinessPartnerCode(newStagingLine.getBusinessPartnerCode());

                dbStagingLineEntity.setDeletionIndicator(0L);
                dbStagingLineEntity.setCreatedBy(loginUserID);
                dbStagingLineEntity.setUpdatedBy(loginUserID);
                dbStagingLineEntity.setCreatedOn(new Date());
                dbStagingLineEntity.setUpdatedOn(new Date());
                stagingLineEntityList.add(dbStagingLineEntity);
//                stagingLineV2Repository.save(dbStagingLineEntity);

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
            List<PreInboundLineEntityV2> preInboundLineList = preInboundLineService.getPreInboundLineV2(preInboundNo);
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
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @param itemCode
     * @param loginUserID
     * @param updateStagingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
//    public StagingLineEntityV2 updateStagingLineV2(String companyCode, String plantId, String languageId, String warehouseId,
//                                                   String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo,
//                                                   String itemCode, String loginUserID, StagingLineEntityV2 updateStagingLine)
//            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
//        StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
//                caseCode, lineNo, itemCode);
//        List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForGrConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//        log.info("Staging Lines List for respective ref_doc_no: " + stagingLineEntityV2List);
//
//        //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
//        if(updateStagingLine.getBarcodeId() != null) {
//            List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
//            if (imPartnerList != null && !imPartnerList.isEmpty()) {
//                for(ImPartner imPartner : imPartnerList) {
//                    String itemCodeMfrName = imPartner.getItemCode()+imPartner.getManufacturerName();
//                    String updateItemCodeMfrName = itemCode+dbStagingLineEntity.getManufacturerName();
//                    if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
//                        log.info("Barcode Already Assigned");
//                        throw new BadRequestException("Barcode already Assigned for : "
//                                + updateStagingLine.getBarcodeId()
//                                + ", " + imPartner.getItemCode()
//                                + ", " + imPartner.getManufacturerName()
//                        );
//                    }
//                }
//            }
//        }
//
//        //Throw exception updating stagingLine - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
////        if(stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()){
////            if(updateStagingLine.getBarcodeId() != null) {
////                List<StagingLineEntityV2> stagingLineBarcodeFilteredList = stagingLineEntityV2List.stream()
////                        .filter(a -> a.getBarcodeId() != null && a.getBarcodeId().equalsIgnoreCase(updateStagingLine.getBarcodeId())).collect(Collectors.toList());
////                log.info("Staging Line same BarcodeId: " + stagingLineBarcodeFilteredList);
////                if(stagingLineBarcodeFilteredList != null && !stagingLineBarcodeFilteredList.isEmpty()){
////                    for(StagingLineEntityV2 stagingLineEntityV2 : stagingLineBarcodeFilteredList) {
////                        String itemCodeMfrName = stagingLineEntityV2.getItemCode()+stagingLineEntityV2.getManufacturerName();
////                        String updateItemCodeMfrName = itemCode+dbStagingLineEntity.getManufacturerName();
////                        if(!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
////                                throw new BadRequestException("Barcode Assigned for Another ItemCode - Manufacturer Name: "
////                                        + updateStagingLine.getBarcodeId()
////                                        + ", " + stagingLineEntityV2.getItemCode()
////                                        + ", " + stagingLineEntityV2.getManufacturerName());
////                        }
////                    }
////                }
////            }
////        }
//
//        BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
//
//        //Update Barcode if more than one same item & mfr_name present in same order
//        if(stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()){
//            if(updateStagingLine.getBarcodeId() != null) {
//                log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                        .filter(a -> a.getItemCode().equalsIgnoreCase(itemCode) &&
//                                     a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                        .collect(Collectors.toList());
//                log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
//                    for(StagingLineEntityV2 stagingLineEntity : stagingLineFilteredList){
//                        log.info("StagingLine: " + stagingLineEntity);
//                        stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
//                        stagingLineEntity.setUpdatedBy(loginUserID);
//                        stagingLineEntity.setUpdatedOn(new Date());
//                        stagingLineV2Repository.save(stagingLineEntity);
//                        log.info("Staging Line Barcode Updated: " + stagingLineEntity);
//                    }
//                }
//            }
//        }
//
//        dbStagingLineEntity.setUpdatedBy(loginUserID);
//        dbStagingLineEntity.setUpdatedOn(new Date());
//        return stagingLineV2Repository.save(dbStagingLineEntity);
//    }

    /**
     *
     * @param companyCode
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
     * @param loginUserID
     * @param updateStagingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
//    public StagingLineEntityV2 updateStagingLineV2(String companyCode, String plantId, String languageId, String warehouseId,
//                                                   String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo,
//                                                   String itemCode, String loginUserID, StagingLineEntityV2 updateStagingLine)
//            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
//        log.info("ItemCode, updateStagingline.getItemCode, Mfr_name : " + itemCode +", " + updateStagingLine.getItemCode() +", " + updateStagingLine.getManufacturerName());
//        if(itemCode.contains("%20") && updateStagingLine.getItemCode() == null){
//            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo,
//                    refDocNumber, stagingNo, palletCode, caseCode, lineNo);
//            List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForGrConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//            log.info("Staging Lines List for respective ref_doc_no: " + stagingLineEntityV2List);
//
//            //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
//            if(updateStagingLine.getBarcodeId() != null) {
//                List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
//                if (imPartnerList != null && !imPartnerList.isEmpty()) {
//                    for(ImPartner imPartner : imPartnerList) {
//                        String itemCodeMfrName = imPartner.getItemCode()+imPartner.getManufacturerName();
//                        String updateItemCodeMfrName = dbStagingLineEntity.getItemCode()+dbStagingLineEntity.getManufacturerName();
//                        if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
//                            log.info("Barcode Already Assigned");
//                            throw new BadRequestException("Barcode already Assigned for : "
//                                    + updateStagingLine.getBarcodeId()
//                                    + ", " + imPartner.getItemCode()
//                                    + ", " + imPartner.getManufacturerName()
//                            );
//                        }
//                    }
//                }
//            }
//
//            BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
//
//            //Update Barcode if more than one same item & mfr_name present in same order
//            if(stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()){
//                if(updateStagingLine.getBarcodeId() != null) {
//                    log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                    List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                    a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                            .collect(Collectors.toList());
//                    log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                    if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
//                        for(StagingLineEntityV2 stagingLineEntity : stagingLineFilteredList){
//                            log.info("StagingLine: " + stagingLineEntity);
//                            stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
//                            stagingLineEntity.setUpdatedBy(loginUserID);
//                            stagingLineEntity.setUpdatedOn(new Date());
//                            stagingLineV2Repository.save(stagingLineEntity);
//                            log.info("Staging Line Barcode Updated: " + stagingLineEntity);
//                        }
//                    }
//                }
//            }
//
//            dbStagingLineEntity.setUpdatedBy(loginUserID);
//            dbStagingLineEntity.setUpdatedOn(new Date());
//            return stagingLineV2Repository.save(dbStagingLineEntity);
//        } else if(itemCode.contains("%20") && updateStagingLine.getItemCode() != null){
//            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo,
//                    refDocNumber, stagingNo, palletCode, caseCode, lineNo, updateStagingLine.getItemCode());
//            List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForGrConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//            log.info("Staging Lines List for respective ref_doc_no: " + stagingLineEntityV2List);
//
//            //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
//            if(updateStagingLine.getBarcodeId() != null) {
//                List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
//                if (imPartnerList != null && !imPartnerList.isEmpty()) {
//                    for(ImPartner imPartner : imPartnerList) {
//                        String itemCodeMfrName = imPartner.getItemCode()+imPartner.getManufacturerName();
//                        String updateItemCodeMfrName = dbStagingLineEntity.getItemCode()+dbStagingLineEntity.getManufacturerName();
//                        if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
//                            log.info("Barcode Already Assigned");
//                            throw new BadRequestException("Barcode already Assigned for : "
//                                    + updateStagingLine.getBarcodeId()
//                                    + ", " + imPartner.getItemCode()
//                                    + ", " + imPartner.getManufacturerName()
//                            );
//                        }
//                    }
//                }
//            }
//
//            BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
//
//            //Update Barcode if more than one same item & mfr_name present in same order
//            if(stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()){
//                if(updateStagingLine.getBarcodeId() != null) {
//                    log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                    List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                    a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                            .collect(Collectors.toList());
//                    log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                    if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
//                        for(StagingLineEntityV2 stagingLineEntity : stagingLineFilteredList){
//                            log.info("StagingLine: " + stagingLineEntity);
//                            stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
//                            stagingLineEntity.setUpdatedBy(loginUserID);
//                            stagingLineEntity.setUpdatedOn(new Date());
//                            stagingLineV2Repository.save(stagingLineEntity);
//                            log.info("Staging Line Barcode Updated: " + stagingLineEntity);
//                        }
//                    }
//                }
//            }
//
//            dbStagingLineEntity.setUpdatedBy(loginUserID);
//            dbStagingLineEntity.setUpdatedOn(new Date());
//            return stagingLineV2Repository.save(dbStagingLineEntity);
//        } else {
//
//        StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
//                caseCode, lineNo, itemCode);
//        List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForGrConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//        log.info("Staging Lines List for respective ref_doc_no: " + stagingLineEntityV2List);
//
//        //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
//        if(updateStagingLine.getBarcodeId() != null) {
//            List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
//            if (imPartnerList != null && !imPartnerList.isEmpty()) {
//                for(ImPartner imPartner : imPartnerList) {
//                    String itemCodeMfrName = imPartner.getItemCode()+imPartner.getManufacturerName();
//                    String updateItemCodeMfrName = itemCode+dbStagingLineEntity.getManufacturerName();
//                    if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
//                        log.info("Barcode Already Assigned");
//                        throw new BadRequestException("Barcode already Assigned for : "
//                                + updateStagingLine.getBarcodeId()
//                                + ", " + imPartner.getItemCode()
//                                + ", " + imPartner.getManufacturerName()
//                        );
//                    }
//                }
//            }
//        }
//
//        BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
//
//        //Update Barcode if more than one same item & mfr_name present in same order
//        if(stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()){
//            if(updateStagingLine.getBarcodeId() != null) {
//                log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                     a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                        .collect(Collectors.toList());
//                log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
//                    for(StagingLineEntityV2 stagingLineEntity : stagingLineFilteredList){
//                        log.info("StagingLine: " + stagingLineEntity);
//                        stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
//                        stagingLineEntity.setUpdatedBy(loginUserID);
//                        stagingLineEntity.setUpdatedOn(new Date());
//                        stagingLineV2Repository.save(stagingLineEntity);
//                        log.info("Staging Line Barcode Updated: " + stagingLineEntity);
//                    }
//                }
//            }
//        }
//
//        dbStagingLineEntity.setUpdatedBy(loginUserID);
//        dbStagingLineEntity.setUpdatedOn(new Date());
//        return stagingLineV2Repository.save(dbStagingLineEntity);
//        }
//    }

    /**
     * @param companyCode
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
     * @param loginUserID
     * @param updateStagingLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public StagingLineEntityV2 updateStagingLineV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                   String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo,
                                                   String itemCode, String loginUserID, StagingLineEntityV2 updateStagingLine) {
        log.info("ItemCode, updateStagingline.getItemCode, Mfr_name : " + itemCode + ", " + updateStagingLine.getItemCode() + ", " + updateStagingLine.getManufacturerName());
        if (itemCode.contains("%20") && updateStagingLine.getItemCode() == null) {
            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo,
                                                                       refDocNumber, stagingNo, palletCode, caseCode, lineNo);
            List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForBarcodeUpdateV2(companyCode, plantId, languageId, warehouseId, itemCode, updateStagingLine.getManufacturerName());
            log.info("Staging Lines List for respective itemCode: " + stagingLineEntityV2List);

            //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
            if (updateStagingLine.getBarcodeId() != null) {
                List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
                if (imPartnerList != null && !imPartnerList.isEmpty()) {
                    for (ImPartner imPartner : imPartnerList) {
                        String itemCodeMfrName = imPartner.getItemCode() + imPartner.getManufacturerName();
                        String updateItemCodeMfrName = dbStagingLineEntity.getItemCode() + dbStagingLineEntity.getManufacturerName();
                        if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
                            log.info("Barcode Already Assigned");
                            throw new BadRequestException("Barcode already Assigned for : "
                                                                  + updateStagingLine.getBarcodeId()
                                                                  + ", " + imPartner.getItemCode()
                                                                  + ", " + imPartner.getManufacturerName()
                            );
                        }
                    }
                }
            }

            BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));

            //Update Barcode if more than one same item & mfr_name present in same order
            if (stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()) {
                if (updateStagingLine.getBarcodeId() != null) {
                    log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                    List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                    a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                            .collect(Collectors.toList());
//                    log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                    if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
                    for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityV2List) {
                        log.info("StagingLine: " + stagingLineEntity);
                        stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setPartner_item_barcode(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setUpdatedBy(loginUserID);
                        stagingLineEntity.setUpdatedOn(new Date());
                        stagingLineV2Repository.save(stagingLineEntity);
                        log.info("Staging Line Barcode Updated: " + stagingLineEntity);
                    }
//                    }
                }
            }

            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            return stagingLineV2Repository.save(dbStagingLineEntity);
        } else if (itemCode.contains("%20") && updateStagingLine.getItemCode() != null) {
            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo,
                                                                       refDocNumber, stagingNo, palletCode, caseCode, lineNo, updateStagingLine.getItemCode());
            List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForBarcodeUpdateV2(companyCode, plantId, languageId, warehouseId, itemCode, updateStagingLine.getManufacturerName());
            log.info("Staging Lines List for respective itemCode: " + stagingLineEntityV2List);

            //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
            if (updateStagingLine.getBarcodeId() != null) {
                List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
                if (imPartnerList != null && !imPartnerList.isEmpty()) {
                    for (ImPartner imPartner : imPartnerList) {
                        String itemCodeMfrName = imPartner.getItemCode() + imPartner.getManufacturerName();
                        String updateItemCodeMfrName = dbStagingLineEntity.getItemCode() + dbStagingLineEntity.getManufacturerName();
                        if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
                            log.info("Barcode Already Assigned");
                            throw new BadRequestException("Barcode already Assigned for : "
                                                                  + updateStagingLine.getBarcodeId()
                                                                  + ", " + imPartner.getItemCode()
                                                                  + ", " + imPartner.getManufacturerName()
                            );
                        }
                    }
                }
            }

            BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));

            //Update Barcode if more than one same item & mfr_name present in same order
            if (stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()) {
                if (updateStagingLine.getBarcodeId() != null) {
                    log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                    List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                    a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                            .collect(Collectors.toList());
//                    log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                    if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
                    for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityV2List) {
                        log.info("StagingLine: " + stagingLineEntity);
                        stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setPartner_item_barcode(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setUpdatedBy(loginUserID);
                        stagingLineEntity.setUpdatedOn(new Date());
                        stagingLineV2Repository.save(stagingLineEntity);
                        log.info("Staging Line Barcode Updated: " + stagingLineEntity);
                    }
//                    }
                }
            }

            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            return stagingLineV2Repository.save(dbStagingLineEntity);
        } else {

            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode,
                                                                       caseCode, lineNo, itemCode);
            List<StagingLineEntityV2> stagingLineEntityV2List = getStagingLineForBarcodeUpdateV2(companyCode, plantId, languageId, warehouseId, itemCode, updateStagingLine.getManufacturerName());
            log.info("Staging Lines List for respective itemCode: " + stagingLineEntityV2List);

            //Throw exception updating ImPartner - barcode with itemCode and ManufacturerName when barcode associated with another ItemCode
            if (updateStagingLine.getBarcodeId() != null) {
                List<ImPartner> imPartnerList = imPartnerService.getImpartnerBarcodeList(companyCode, plantId, languageId, warehouseId, updateStagingLine.getBarcodeId());
                if (imPartnerList != null && !imPartnerList.isEmpty()) {
                    for (ImPartner imPartner : imPartnerList) {
                        String itemCodeMfrName = imPartner.getItemCode() + imPartner.getManufacturerName();
                        String updateItemCodeMfrName = itemCode + dbStagingLineEntity.getManufacturerName();
                        if (!itemCodeMfrName.equalsIgnoreCase(updateItemCodeMfrName)) {
                            log.info("Barcode Already Assigned");
                            throw new BadRequestException("Barcode already Assigned for : "
                                                                  + updateStagingLine.getBarcodeId()
                                                                  + ", " + imPartner.getItemCode()
                                                                  + ", " + imPartner.getManufacturerName()
                            );
                        }
                    }
                }
            }

            BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));

            //Update Barcode if more than one same item & mfr_name present in same order
            if (stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()) {
                if (updateStagingLine.getBarcodeId() != null) {
                    log.info("Update Barcode: " + updateStagingLine.getBarcodeId());
//                List<StagingLineEntityV2> stagingLineFilteredList = stagingLineEntityV2List.stream()
//                            .filter(a -> a.getItemCode().equalsIgnoreCase(dbStagingLineEntity.getItemCode()) &&
//                                     a.getManufacturerName().equalsIgnoreCase(dbStagingLineEntity.getManufacturerName()))
//                        .collect(Collectors.toList());
//                log.info("Staging Line same ItemCode and MfrName: " + stagingLineFilteredList);
//                if(stagingLineFilteredList != null && !stagingLineFilteredList.isEmpty()){
                    for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityV2List) {
                        log.info("StagingLine: " + stagingLineEntity);
                        stagingLineEntity.setBarcodeId(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setPartner_item_barcode(updateStagingLine.getBarcodeId());
                        stagingLineEntity.setUpdatedBy(loginUserID);
                        stagingLineEntity.setUpdatedOn(new Date());
                        stagingLineV2Repository.save(stagingLineEntity);
                        log.info("Staging Line Barcode Updated: " + stagingLineEntity);
                    }
//                }
                }
            }

            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            return stagingLineV2Repository.save(dbStagingLineEntity);
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserID
     * @return
     */
    public List<StagingLineEntityV2> updateStagingLineForBarcodeV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                   String itemCode, String manufacturerName, String barcodeId, String loginUserID) {
        List<StagingLineEntityV2> barcodeUpdatedList = new ArrayList<>();
        List<StagingLineEntityV2> stagingLineEntityV2List =
                stagingLineV2Repository.findAllByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, itemCode, manufacturerName, 0L);
        log.info("Staging Lines List for respective itemCode, ManufacturerName: " + stagingLineEntityV2List);

        if (stagingLineEntityV2List != null && !stagingLineEntityV2List.isEmpty()) {
            for (StagingLineEntityV2 dbStagingLineEntity : stagingLineEntityV2List) {
                dbStagingLineEntity.setBarcodeId(barcodeId);
                dbStagingLineEntity.setPartner_item_barcode(barcodeId);
                dbStagingLineEntity.setUpdatedBy(loginUserID);
                dbStagingLineEntity.setUpdatedOn(new Date());
                stagingLineV2Repository.save(dbStagingLineEntity);
                barcodeUpdatedList.add(dbStagingLineEntity);
            }
        }
        return barcodeUpdatedList;
    }

    /**
     * @param assignHHTUsers
     * @param assignedUserId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StagingLineEntityV2> assignHHTUserV2(List<AssignHHTUser> assignHHTUsers, String companyCodeId, String plantId,
                                                     String languageId, String assignedUserId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        List<StagingLineEntityV2> updatedStagingLineEntityList = new ArrayList<>();

        for (AssignHHTUser assignHHTUser : assignHHTUsers) {

            log.info("Assign HHt User: " + assignedUserId);

            StagingLineEntityV2 dbStagingLineEntity = getStagingLineV2(
                    companyCodeId,
                    plantId,
                    languageId,
                    assignHHTUser.getWarehouseId(),
                    assignHHTUser.getPreInboundNo(),
                    assignHHTUser.getRefDocNumber(),
                    assignHHTUser.getStagingNo(),
                    assignHHTUser.getPalletCode(),
                    assignHHTUser.getCaseCode(),
                    assignHHTUser.getLineNo(),
                    assignHHTUser.getItemCode());
            dbStagingLineEntity.setAssignedUserId(assignedUserId);
            dbStagingLineEntity.setUpdatedBy(loginUserID);
            dbStagingLineEntity.setUpdatedOn(new Date());
            log.info("StagingLine: " + dbStagingLineEntity);

            StagingLineEntityV2 updatedStagingLineEntity = stagingLineV2Repository.save(dbStagingLineEntity);
            log.info("StagingLine Updated: " + updatedStagingLineEntity);
            if (updatedStagingLineEntity != null) {
                log.info("Updated StagingLine --> statusId, assignedUserId: " + updatedStagingLineEntity.getStatusId() + ", " + updatedStagingLineEntity.getAssignedUserId());
            }

            updatedStagingLineEntityList.add(updatedStagingLineEntity);
        }
        return updatedStagingLineEntityList;
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<StagingLineEntityV2> StagingLineEntitys = getStagingLinesV2();
        StagingLineEntitys.stream().forEach(stagLines -> stagLines.setReferenceField1(asnNumber));
        stagingLineV2Repository.saveAll(StagingLineEntitys);
    }

    /**
     * @param caseConfirmations
     * @param caseCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<StagingLineEntityV2> caseConfirmationV2(List<CaseConfirmation> caseConfirmations,
                                                        String caseCode, String companyCodeId, String plantId,
                                                        String languageId, String loginUserID) throws Exception {

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

				 /* Pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO/IB_LINE_NO/ITM_CODE/CASE_CODE values in StagingLineEntity table and
				  validate STATUS_ID , if all the STATUS_ID values of the CASE_CODE are 14*/

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

					 /* 1. Then pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO in STAGINGHEADER table and
					  update the STATUS_ID as 14,ST_CNF_BY and ST_CNF_ON fields*/

                    StagingHeaderV2 updateStagingHeader = new StagingHeaderV2();
                    updateStagingHeader.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    updateStagingHeader.setStatusDescription(statusDescription);
                    StagingHeaderV2 stagingHeader =
                            stagingHeaderService.updateStagingHeaderV2(
                                    companyCodeId,
                                    plantId,
                                    languageId,
                                    caseConfirmation.getWarehouseId(),
                                    caseConfirmation.getPreInboundNo(),
                                    caseConfirmation.getRefDocNumber(),
                                    caseConfirmation.getStagingNo(),
                                    loginUserID,
                                    updateStagingHeader);

                    log.info("stagingHeader : " + stagingHeader);


					 /* 2. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
					  updated STATUS_ID as 14*/

                    InboundLineV2 updateInboundLine = new InboundLineV2();
                    updateInboundLine.setStatusId(14L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(14L, languageId);
                    updateInboundLine.setStatusDescription(statusDescription);
                    InboundLineV2 inboundLine = inboundLineService.updateInboundLineV2(companyCodeId, plantId, languageId,
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
        if (!updatedStagingLineEntityList.isEmpty()) {

            StagingLineEntityV2 updatedStagingLineEntity = updatedStagingLineEntityList.get(0);

            log.info("updatedStagingLineEntity-----IN---GRHEADER---CREATION---> : " + updatedStagingLineEntity);

            GrHeaderV2 addGrHeader = new GrHeaderV2();
            BeanUtils.copyProperties(updatedStagingLineEntity, addGrHeader, CommonUtils.getNullPropertyNames(updatedStagingLineEntity));

            // GR_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_CODE = 5;
            String nextGRHeaderNumber = getNextRangeNumber(NUM_RAN_CODE,
                                                           updatedStagingLineEntity.getCompanyCode(),
                                                           updatedStagingLineEntity.getPlantId(),
                                                           updatedStagingLineEntity.getLanguageId(),
                                                           updatedStagingLineEntity.getWarehouseId(),
                                                           authTokenForIDMasterService.getAccess_token());
            addGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);

            addGrHeader.setMiddlewareId(updatedStagingLineEntity.getMiddlewareId());
            addGrHeader.setMiddlewareTable(updatedStagingLineEntity.getMiddlewareTable());
            addGrHeader.setReferenceDocumentType(updatedStagingLineEntity.getReferenceDocumentType());
            addGrHeader.setManufacturerFullName(updatedStagingLineEntity.getManufacturerFullName());
            addGrHeader.setManufacturerName(updatedStagingLineEntity.getManufacturerName());

            List<StagingHeaderV2> stagingHeaderList = stagingHeaderService.getStagingHeaderV2(
                    addGrHeader.getCompanyCode(), addGrHeader.getPlantId(), addGrHeader.getLanguageId(),
                    addGrHeader.getWarehouseId(), addGrHeader.getPreInboundNo(), addGrHeader.getRefDocNumber());

            for (StagingHeaderV2 stagingHeaderV2 : stagingHeaderList) {
                addGrHeader.setTransferOrderDate(stagingHeaderV2.getTransferOrderDate());
                addGrHeader.setIsCompleted(stagingHeaderV2.getIsCompleted());
                addGrHeader.setIsCancelled(stagingHeaderV2.getIsCancelled());
                addGrHeader.setMUpdatedOn(stagingHeaderV2.getMUpdatedOn());
                addGrHeader.setSourceBranchCode(stagingHeaderV2.getSourceBranchCode());
                addGrHeader.setSourceCompanyCode(stagingHeaderV2.getSourceCompanyCode());
            }

            // STATUS_ID
            addGrHeader.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            addGrHeader.setStatusDescription(statusDescription);

            GrHeaderV2 createdGrHeader = grHeaderService.createGrHeaderV2(addGrHeader, loginUserID);
            log.info("createdGrHeader : " + createdGrHeader);

            if (createdGrHeader != null &&
                    createdGrHeader.getInboundOrderTypeId() != null &&
                    (createdGrHeader.getInboundOrderTypeId() == 5 ||
                            (createdGrHeader.getInboundOrderTypeId() == 6 && !createdGrHeader.getCompanyCode().equalsIgnoreCase(COMPANY_CODE)))) {  //Direct Stock Receipt Condition
                createGrLine(createdGrHeader);
            }
        }

        return updatedStagingLineEntityList;
    }

    /**
     * @param grHeader
     */
    public void createGrLine(GrHeaderV2 grHeader) throws Exception {

        List<StagingLineEntityV2> stagingLineEntityList = getStagingLineForGrLine(grHeader.getCompanyCode(),
                                                                                  grHeader.getPlantId(), grHeader.getLanguageId(), grHeader.getWarehouseId(),
                                                                                  grHeader.getStagingNo(), grHeader.getCaseCode());

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

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

            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_ID = 6;
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, dbStagingLine.getCompanyCode(),
                                                        dbStagingLine.getPlantId(), dbStagingLine.getLanguageId(), dbStagingLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());

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
            if (dbStagingLine.getBarcodeId() != null) {
                newGrLine.setBarcodeId(dbStagingLine.getBarcodeId());
            }
            if (dbStagingLine.getBarcodeId() == null) {
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
            if (grHeader.getInboundOrderTypeId() == 5) {
                newGrLine.setInterimStorageBin("REC-AL-B2");
            }

            newGrLineList.add(newGrLine);
        }
//        List<GrLineV2> createGrLine = grLineService.createGrLineV2(newGrLineList, grHeader.getCreatedBy());
//        List<GrLineV2> createGrLine = grLineService.createGrLineNonCBMV2(newGrLineList, grHeader.getCreatedBy());
        List<GrLineV2> createGrLine = grLineService.createGrLineNonCBMV4(newGrLineList, grHeader.getCreatedBy());
        log.info("GrLine Created Successfully: " + createGrLine);

        if (grHeader.getInboundOrderTypeId() == 5) {
            List<PutAwayLineV2> createdPutawayLine = null;
            //putaway Confirm
            if (createGrLine != null && !createGrLine.isEmpty()) {
                log.info("Putaway line Confirm Initiated");
                List<PutAwayLineV2> createPutawayLine = new ArrayList<>();
//            for (GrLineV2 grLine : createGrLine) {

                List<PutAwayHeaderV2> dbPutawayHeaderList = putAwayHeaderService.getPutAwayHeaderV2(
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
//            }
                createdPutawayLine = putAwayLineService.putAwayLineConfirmV2(createPutawayLine, grHeader.getCreatedBy());
                log.info("PutawayLine Confirmed: " + createdPutawayLine);
            }
            if (createdPutawayLine != null && !createdPutawayLine.isEmpty()) {
                log.info("Direct StockReceipt - Inbound Confirmation Process Initiated");
                inboundHeaderService.updateInboundHeaderPartialConfirmV2(
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
    }


    /**
     * deleteStagingLineEntity
     * @param loginUserID
     * @param palletCode
     */
    public void deleteStagingLineV2(String companyCode, String plantId, String languageId,
                                    String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                    String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID) throws java.text.ParseException {
        StagingLineEntityV2 StagingLineEntity = getStagingLineV2(companyCode, plantId, languageId, warehouseId,
                                                                 preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo, itemCode);
        if (StagingLineEntity != null) {
            StagingLineEntity.setDeletionIndicator(1L);
            StagingLineEntity.setUpdatedBy(loginUserID);
            StagingLineEntity.setUpdatedOn(new Date());
            stagingLineV2Repository.save(StagingLineEntity);
        } else {
            throw new BadRequestException("Error in deleting Id:  warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber + "," +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",stagingNo: " + stagingNo +
                                                  ",palletCode: " + palletCode +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }
    }

    /**
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param caseCode
     * @param loginUserID
     */
    public void deleteCasesV2(String companyCode, String plantId, String languageId,
                              String preInboundNo, Long lineNo, String itemCode,
                              String caseCode, String loginUserID) {
        List<Long> statusIds = new ArrayList<>();
        statusIds.add(13L);
        statusIds.add(14L);
        List<StagingLineEntityV2> StagingLineEntitys =
                stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        statusIds,
                        0L
                );
        if (StagingLineEntitys == null) {
            throw new BadRequestException("Error in deleting Id: " +
                                                  ",preInboundNo: " + preInboundNo + "," +
                                                  ",caseCode: " + caseCode +
                                                  ",lineNo: " + lineNo +
                                                  ",itemCode: " + itemCode +
                                                  " doesn't exist.");
        }

        for (StagingLineEntityV2 StagingLineEntity : StagingLineEntitys) {
            StagingLineEntity.setDeletionIndicator(1L);
            StagingLineEntity.setUpdatedBy(loginUserID);
            StagingLineEntity deletedStagingLineEntity = stagingLineV2Repository.save(StagingLineEntity);
            log.info("deletedStagingLineEntity : " + deletedStagingLineEntity);
        }
    }

    /**
     * @param stagingLineEntity
     * @return
     */
    private StagingLine copyLineEntityToBean(StagingLineEntityV2 stagingLineEntity) {
        StagingLine stagingLine = new StagingLine();
        BeanUtils.copyProperties(stagingLineEntity, stagingLine, CommonUtils.getNullPropertyNames(stagingLineEntity));
        return stagingLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     */
    public List<StagingLineEntityV2> deleteStagingLineV2(String companyCode, String plantId, String languageId,
                                                         String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) throws java.text.ParseException {
        List<StagingLineEntityV2> stagingLineEntityV2s = new ArrayList<>();
        List<StagingLineEntityV2> dbStagingLineList =
                stagingLineV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("StagingLineList - cancellation : " + dbStagingLineList);
        if (dbStagingLineList != null && !dbStagingLineList.isEmpty()) {
            for (StagingLineEntityV2 stagingLineEntityV2 : dbStagingLineList) {
                stagingLineEntityV2.setDeletionIndicator(1L);
                stagingLineEntityV2.setUpdatedBy(loginUserID);
                stagingLineEntityV2.setUpdatedOn(new Date());
                StagingLineEntityV2 stagingLineEntityV21 = stagingLineV2Repository.save(stagingLineEntityV2);
                stagingLineEntityV2s.add(stagingLineEntityV21);
            }
        }
        return stagingLineEntityV2s;
    }

    //===========================================StagingLine_ExceptionLog==============================================
    private void createStagingLineLog3(String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
                                       String refDocNumber, Long lineNo, String itemCode, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setReferenceField2(String.valueOf(lineNo));
        dbErrorLog.setReferenceField3(itemCode);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }
    //=======================================walkaroo-v3=============================================================

    public List<StagingLineEntityV2> getStagingLineV3(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String parentProductionOrderNo) {
        return stagingLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentProductionOrderNoAndDeletionIndicatorOrderByReferenceField5(
                languageId,
                companyCode,
                plantId,
                warehouseId,
                parentProductionOrderNo,
                0L);
    }
}