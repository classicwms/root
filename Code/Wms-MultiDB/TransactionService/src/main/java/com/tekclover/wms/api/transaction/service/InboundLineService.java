package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.AddInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.SearchInboundLineV2;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.InboundLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.InboundLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class InboundLineService extends BaseService {

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private PutAwayLineService putAwayLineService;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    @Autowired
    private PreInboundHeaderService preInboundHeaderService;

    @Autowired
    private PreInboundLineService preInboundLineService;

    //----------------------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;
    //----------------------------------------------------------------------------------------------------

    /**
     * getInboundLines
     * @return
     */
    public List<InboundLine> getInboundLines() {
        List<InboundLine> inboundLineList = inboundLineRepository.findAll();
        inboundLineList = inboundLineList
                .stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return inboundLineList;
    }

    /**
     * getInboundLine
     * @param lineNo
     * @return
     */
    public InboundLine getInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, String itemCode) {
        Optional<InboundLine> inboundLine =
                inboundLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
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
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<InboundLine> getInboundLine(String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLine> inboundLine =
                inboundLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }

        return inboundLine;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<InboundLine> getInboundLinebyRefDocNoISNULL(String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLine> inboundLine =
                inboundLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndReferenceField1AndStatusIdAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        null,
                        20L,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values in getInboundLinebyRefDocNoISNULL: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }

        return inboundLine;
    }

    /**
     * getInboundLine
     * @param refDocNumber
     * @return
     */
    public List<InboundLine> getInboundLine(String refDocNumber) {
        List<InboundLine> inboundLine = inboundLineRepository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0L);
        log.info("inboundLine : " + inboundLine);
        return inboundLine;
    }

    /**
     * @param refDocNumber
     * @param warehouseId
     * @return
     */
    public List<InboundLine> getInboundLine(String refDocNumber, String warehouseId) {
        List<InboundLine> inboundLine = inboundLineRepository.findByRefDocNumberAndWarehouseIdAndDeletionIndicator(refDocNumber, warehouseId, 0L);
        log.info("inboundLine : " + inboundLine);
        return inboundLine;
    }

    /**
     * @param newInboundLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundLine confirmInboundLine(List<AddInboundLine> newInboundLines, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        for (AddInboundLine addInboundLine : newInboundLines) {
            /*
             * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in INBOUNDLINE tables and
             * Validate STATUS_ID of all the Values are 20
             */
            InboundLine inboundLine =
                    getInboundLine(addInboundLine.getWarehouseId(), addInboundLine.getRefDocNumber(),
                                   addInboundLine.getPreInboundNo(), addInboundLine.getLineNo(), addInboundLine.getItemCode());
            if (inboundLine.getStatusId() == 20L) {
                addInboundLine.setStatusId(24L);
                inboundLine = createInboundLine(addInboundLine, loginUserID);
                log.info("inboundLine : " + inboundLine);

                // PUTAWAY table STATUS Updates
                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE tables and  update STATUS_ID as 24
                List<PutAwayLine> putAwayLines = putAwayLineService.getPutAwayLine(inboundLine.getWarehouseId(),
                                                                                   inboundLine.getPreInboundNo(), inboundLine.getRefDocNumber(), inboundLine.getLineNo(), inboundLine.getItemCode());
                for (PutAwayLine putAwayLine : putAwayLines) {
                    putAwayLine.setStatusId(24L);
                    UpdatePutAwayLine updatePutAwayLine = new UpdatePutAwayLine();
                    BeanUtils.copyProperties(putAwayLine, updatePutAwayLine, CommonUtils.getNullPropertyNames(putAwayLine));
                    putAwayLine = putAwayLineService.updatePutAwayLine(updatePutAwayLine, loginUserID);
                    log.info("putAwayLine---------> " + putAwayLine);
                }

                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PUTAWAYHEADER tables and  update STATUS_ID as 24
                putAwayHeaderService.updatePutAwayHeader(inboundLine.getWarehouseId(), inboundLine.getPreInboundNo(),
                                                         inboundLine.getRefDocNumber(), 24L, loginUserID);

                // PREINBOUND table updates
                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PREINBOUNDHEADER tables and  update STATUS_ID as 24
                preInboundHeaderService.updatePreInboundHeader(inboundLine.getWarehouseId(), inboundLine.getPreInboundNo(),
                                                               inboundLine.getRefDocNumber(), 24L, loginUserID);

                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PREINBOUNDLINE tables and  update STATUS_ID as 24
                preInboundLineService.updatePreInboundLine(inboundLine.getPreInboundNo(), inboundLine.getWarehouseId(),
                                                           inboundLine.getRefDocNumber(), inboundLine.getLineNo(), inboundLine.getItemCode(), 24L, loginUserID);
            }
        }
        return null;
    }

    /**
     * @param searchInboundLine
     * @return
     * @throws Exception
     */
    public List<InboundLine> findInboundLine(SearchInboundLine searchInboundLine) throws Exception {
        if (searchInboundLine.getStartConfirmedOn() != null && searchInboundLine.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundLine.getStartConfirmedOn(),
                                                             searchInboundLine.getEndConfirmedOn());
            searchInboundLine.setStartConfirmedOn(dates[0]);
            searchInboundLine.setEndConfirmedOn(dates[1]);
        }

        InboundLineSpecification spec = new InboundLineSpecification(searchInboundLine);
        List<InboundLine> results = inboundLineRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createInboundLine
     * @param newInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundLine createInboundLine(AddInboundLine newInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InboundLine dbInboundLine = new InboundLine();
        log.info("newInboundLine : " + newInboundLine);
        BeanUtils.copyProperties(newInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(newInboundLine));
        dbInboundLine.setDeletionIndicator(0L);
        dbInboundLine.setCreatedBy(loginUserID);
        dbInboundLine.setUpdatedBy(loginUserID);
        dbInboundLine.setCreatedOn(new Date());
        dbInboundLine.setUpdatedOn(new Date());
        return inboundLineRepository.save(dbInboundLine);
    }

    /**
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
     */
    public InboundLine updateInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, String itemCode,
                                         String loginUserID, UpdateInboundLine updateInboundLine)
            throws IllegalAccessException, InvocationTargetException {
        InboundLine dbInboundLine = getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        BeanUtils.copyProperties(updateInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(updateInboundLine));
        dbInboundLine.setUpdatedBy(loginUserID);
        dbInboundLine.setUpdatedOn(new Date());
        return inboundLineRepository.save(dbInboundLine);
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<InboundLine> inboundLines = getInboundLines();
        inboundLines.forEach(p -> p.setReferenceField1(asnNumber));
        inboundLineRepository.saveAll(inboundLines);
    }

    /**
     * deleteInboundLine
     * @param loginUserID
     * @param lineNo
     */
    public void deleteInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, String itemCode, String loginUserID) {
        InboundLine inboundLine = getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        if (inboundLine != null) {
            inboundLine.setDeletionIndicator(1L);
            inboundLine.setUpdatedBy(loginUserID);
            inboundLineRepository.save(inboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNo);
        }
    }

    //----------------------------------------------------------------------V2--------------------------------------------------------------------

    /**
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineV2(String refDocNumber) {
        List<InboundLineV2> inboundLine = inboundLineV2Repository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0L);
        log.info("inboundLine : " + inboundLine);
        return inboundLine;
    }

    public List<InboundLineV2> getInboundLineForReportV2(String refDocNumber, String preInboundNo, String companyCodeId,
                                                         String plantId, String languageId, String warehouseId) {
        List<InboundLineV2> inboundLine = inboundLineV2Repository.
                findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        companyCodeId, languageId, plantId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("inboundLine : " + inboundLine);
        return inboundLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLineV2> inboundLines = inboundLineV2Repository.
                findByRefDocNumberAndPreInboundNoAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
                        refDocNumber, preInboundNo, companyCode, plantId, languageId, warehouseId, 20L, 0L);
        log.info("inboundLine : " + inboundLines.size());
        return inboundLines;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineForInboundConfirmWithStatusIdV2(String companyCode, String plantId, String languageId,
                                                                             String warehouseId, String refDocNumber) {
        List<InboundLineV2> inboundLines = inboundLineV2Repository.
                findByRefDocNumberAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
                        refDocNumber, companyCode, plantId, languageId, warehouseId, 20L, 0L);
        log.info("inboundLine : " + inboundLines);
        return inboundLines;
    }

    /**
     * PartialAllocation
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineForInboundConfirmPartialAllocationV2(String companyCode, String plantId, String languageId,
                                                                                  String warehouseId, String refDocNumber, String preInboundNo) {
//        List<InboundLineV2> inboundLines = inboundLineV2Repository.getInboundLinesV2ForInboundConfirm(
//                        companyCode, plantId, languageId, warehouseId, refDocNumber,  20L, 24L);
        List<InboundLineV2> inboundLines = inboundLineV2Repository.getInboundLinesV2ForInboundConfirm(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 20L);
        log.info("inboundLine : " + inboundLines.size());
        return inboundLines;
    }

    //V2
    public Stream<InboundLineV2> findInboundLineV2(SearchInboundLineV2 searchInboundLine) throws Exception {
        if (searchInboundLine.getStartConfirmedOn() != null && searchInboundLine.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundLine.getStartConfirmedOn(),
                                                             searchInboundLine.getEndConfirmedOn());
            searchInboundLine.setStartConfirmedOn(dates[0]);
            searchInboundLine.setEndConfirmedOn(dates[1]);
        }

        InboundLineV2Specification spec = new InboundLineV2Specification(searchInboundLine);
        Stream<InboundLineV2> results = inboundLineV2Repository.stream(spec, InboundLineV2.class);
        return results;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<InboundLineV2> getInboundLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }

        return inboundLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineForInvoiceCancellationV2(String companyCode, String plantId, String languageId,
                                                                      String warehouseId, String refDocNumber, Long statusId) {
        List<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByRefDocNumberAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        statusId,
                        0L);
        log.info("inboundLine : " + inboundLine);
        return inboundLine;
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
     * @param refDocNumber
     * @param preInboundNo
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @return
     */
    public InboundLineV2 getInboundLineForInboundConfirmPartialConfirmV2(String companyCode, String plantId, String languageId,
                                                                         String warehouseId, String refDocNumber, String preInboundNo,
                                                                         String itemCode, String manufacturerName, Long lineNumber) {
        if (companyCode != null && manufacturerName == null) {
            manufacturerName = getMfrName(companyCode);
        }
        InboundLineV2 inboundLines = inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, lineNumber, itemCode, manufacturerName, 20L, 0L);
        log.info("db inboundLine for partial Confirm: " + inboundLines);
        return inboundLines;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param itemCode
     * @param lineNumber
     * @return
     */
    public InboundLineV2 getInboundLineForInboundConfirmPartialConfirmV2(String companyCode, String plantId, String languageId,
                                                                         String warehouseId, String refDocNumber, String preInboundNo,
                                                                         String itemCode, Long lineNumber) {
        InboundLineV2 inboundLines = inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndStatusIdAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, lineNumber, itemCode, 20L, 0L);
        log.info("db inboundLine for partial Confirm: " + inboundLines);
        return inboundLines;
    }

    /**
     * @param newInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundLineV2 createInboundLineV2(InboundLineV2 newInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        InboundLineV2 dbInboundLine = new InboundLineV2();
        log.info("newInboundLine : " + newInboundLine);
        BeanUtils.copyProperties(newInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(newInboundLine));

        //===========================================Handle MfrName==============================================================//
        if (dbInboundLine.getCompanyCode() != null && dbInboundLine.getManufacturerName() == null) {
            dbInboundLine.setManufacturerName(getMfrName(dbInboundLine.getCompanyCode()));
            if (dbInboundLine.getManufacturerPartNo() == null) {
                dbInboundLine.setManufacturerPartNo(getMfrName(dbInboundLine.getCompanyCode()));
            }
            if (dbInboundLine.getManufacturerCode() == null) {
                dbInboundLine.setManufacturerCode(getMfrName(dbInboundLine.getCompanyCode()));
            }
        }
        //=========================================================================================================//

        dbInboundLine.setDeletionIndicator(0L);
        dbInboundLine.setCreatedBy(loginUserID);
        dbInboundLine.setUpdatedBy(loginUserID);
        dbInboundLine.setCreatedOn(new Date());
        dbInboundLine.setUpdatedOn(new Date());
        return inboundLineV2Repository.save(dbInboundLine);
    }

    /**
     * @param newInboundLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundLineV2 confirmInboundLineV2(List<InboundLineV2> newInboundLines, String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        for (InboundLineV2 addInboundLine : newInboundLines) {
            /*
             * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in INBOUNDLINE tables and
             * Validate STATUS_ID of all the Values are 20
             */
            InboundLineV2 inboundLine =
                    getInboundLineV2(
                            addInboundLine.getCompanyCode(), addInboundLine.getPlantId(),
                            addInboundLine.getLanguageId(),
                            addInboundLine.getWarehouseId(), addInboundLine.getRefDocNumber(),
                            addInboundLine.getPreInboundNo(), addInboundLine.getLineNo(), addInboundLine.getItemCode());
            if (inboundLine.getStatusId() == 20L) {
                addInboundLine.setStatusId(24L);
                statusDescription = stagingLineV2Repository.getStatusDescription(24L, addInboundLine.getLanguageId());
                addInboundLine.setStatusDescription(statusDescription);
                inboundLine = createInboundLineV2(addInboundLine, loginUserID);
                log.info("inboundLine : " + inboundLine);

                // PUTAWAY table STATUS Updates
                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE tables and  update STATUS_ID as 24
                List<PutAwayLineV2> putAwayLines = putAwayLineService.getPutAwayLineV2(
                        inboundLine.getCompanyCode(), inboundLine.getPlantId(), inboundLine.getLanguageId(), inboundLine.getWarehouseId(),
                        inboundLine.getPreInboundNo(), inboundLine.getRefDocNumber(), inboundLine.getLineNo(), inboundLine.getItemCode());
                for (PutAwayLineV2 putAwayLine : putAwayLines) {
                    putAwayLine.setStatusId(24L);
                    putAwayLine.setStatusDescription(statusDescription);
                    PutAwayLineV2 updatePutAwayLine = new PutAwayLineV2();
                    BeanUtils.copyProperties(putAwayLine, updatePutAwayLine, CommonUtils.getNullPropertyNames(putAwayLine));
                    putAwayLine = putAwayLineService.updatePutAwayLineV2(updatePutAwayLine, loginUserID);
                    log.info("putAwayLine---------> " + putAwayLine);
                }

                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PUTAWAYHEADER tables and  update STATUS_ID as 24
                putAwayHeaderService.updatePutAwayHeaderV2(
                        inboundLine.getCompanyCode(), inboundLine.getPlantId(),
                        inboundLine.getLanguageId(),
                        inboundLine.getWarehouseId(), inboundLine.getPreInboundNo(),
                        inboundLine.getRefDocNumber(), 24L, loginUserID);

                // PREINBOUND table updates
                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PREINBOUNDHEADER tables and  update STATUS_ID as 24
                preInboundHeaderService.updatePreInboundHeaderV2(
                        inboundLine.getCompanyCode(), inboundLine.getPlantId(),
                        inboundLine.getLanguageId(), inboundLine.getPreInboundNo(),
                        inboundLine.getWarehouseId(),
                        inboundLine.getRefDocNumber(), 24L, loginUserID);

                // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PREINBOUNDLINE tables and  update STATUS_ID as 24
                preInboundLineService.updatePreInboundLineV2(
                        inboundLine.getCompanyCode(), inboundLine.getPlantId(),
                        inboundLine.getLanguageId(),
                        inboundLine.getPreInboundNo(), inboundLine.getWarehouseId(),
                        inboundLine.getRefDocNumber(), inboundLine.getLineNo(), inboundLine.getItemCode(), 24L, loginUserID);
            }
        }
        return null;
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
     * Batch Update for Inbound Confirm
     * @param loginUserID
     * @param updateInboundLine
     * @return
     */
    public List<InboundLineV2> updateBatchInboundLineV2(List<InboundLineV2> updateInboundLine, String loginUserID) {
        log.info("InboundLines to Update: " + updateInboundLine.size());
        List<InboundLineV2> updatedInboundLines = new ArrayList<>();
        if (updateInboundLine != null && !updateInboundLine.isEmpty()) {
            for (InboundLineV2 inboundLine : updateInboundLine) {
                //===========================================Handle MfrName==============================================================//
                if (inboundLine.getCompanyCode() != null && inboundLine.getManufacturerName() == null) {
                    inboundLine.setManufacturerName(getMfrName(inboundLine.getCompanyCode()));
                    if (inboundLine.getManufacturerPartNo() == null) {
                        inboundLine.setManufacturerPartNo(getMfrName(inboundLine.getCompanyCode()));
                    }
                    if (inboundLine.getManufacturerCode() == null) {
                        inboundLine.setManufacturerCode(getMfrName(inboundLine.getCompanyCode()));
                    }
                }
                //=========================================================================================================//
                InboundLineV2 dbInboundLine = inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        inboundLine.getLanguageId(),
                        inboundLine.getCompanyCode(),
                        inboundLine.getPlantId(),
                        inboundLine.getWarehouseId(),
                        inboundLine.getRefDocNumber(),
                        inboundLine.getPreInboundNo(),
                        inboundLine.getLineNo(),
                        inboundLine.getItemCode(),
                        inboundLine.getManufacturerName(),
                        0L);
                if (dbInboundLine != null) {
                    BeanUtils.copyProperties(inboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(inboundLine));
                    dbInboundLine.setUpdatedBy(loginUserID);
                    dbInboundLine.setUpdatedOn(new Date());
                    inboundLineV2Repository.save(dbInboundLine);
                    updatedInboundLines.add(dbInboundLine);
                }
            }
        }
        log.info("InboundLines Updated : " + updatedInboundLines.size());
        return updatedInboundLines;
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
     */
    public void deleteInboundLineV2(String companyCode, String plantId, String languageId, String warehouseId,
                                    String refDocNumber, String preInboundNo, Long lineNo, String itemCode, String loginUserID) throws ParseException {
        InboundLineV2 inboundLine = getInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        if (inboundLine != null) {
            inboundLine.setDeletionIndicator(1L);
            inboundLine.setUpdatedBy(loginUserID);
            inboundLine.setUpdatedOn(new Date());
            inboundLineV2Repository.save(inboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNo);
        }
    }

    /**
     * getInboundLines
     * @return
     */
    public List<InboundLineV2> getInboundLinesV2() {
        List<InboundLineV2> inboundLineList = inboundLineV2Repository.findAll();
        inboundLineList = inboundLineList
                .stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return inboundLineList;
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<InboundLineV2> inboundLines = getInboundLinesV2();
        inboundLines.forEach(p -> p.setReferenceField1(asnNumber));
        inboundLineV2Repository.saveAll(inboundLines);
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
    //Delete InboundLine
    public List<InboundLineV2> cancelInboundLineV2(String companyCode, String plantId, String languageId,
                                                   String warehouseId, String refDocNumber, String preInboundNo) {

        List<InboundLineV2> inboundLineList = inboundLineV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdAndDeletionIndicator(
                companyCode, languageId, plantId, warehouseId, refDocNumber, preInboundNo, 24L, 0L);
        log.info("InboundLine - order cancellation : " + inboundLineList);
        return inboundLineList;
    }

    public List<InboundLineV2> deleteInboundLineV2(String companyCode, String plantId, String languageId,
                                                   String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) throws ParseException {

        List<InboundLineV2> inboundLineV2List = new ArrayList<>();
        List<InboundLineV2> inboundLineList = inboundLineV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                companyCode, languageId, plantId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("InboundLine - cancellation : " + inboundLineList);
        if (inboundLineList != null && !inboundLineList.isEmpty()) {
            for (InboundLineV2 inboundLineV2 : inboundLineList) {
                inboundLineV2.setUpdatedBy(loginUserID);
                inboundLineV2.setDeletionIndicator(1L);
                inboundLineV2.setUpdatedOn(new Date());
                InboundLineV2 dbInboundLine = inboundLineV2Repository.save(inboundLineV2);
                inboundLineV2List.add(dbInboundLine);
            }
        }
        return inboundLineV2List;
    }
}