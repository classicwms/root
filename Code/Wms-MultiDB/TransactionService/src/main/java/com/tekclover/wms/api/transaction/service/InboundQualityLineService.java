package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.FetchImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inboundquality.InboundQualityLine;
import com.tekclover.wms.api.transaction.model.inbound.inboundquality.SearchInboundQualityLine;
import com.tekclover.wms.api.transaction.repository.InboundQualityLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.InboundQualityLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class InboundQualityLineService extends BaseService {

    @Autowired
    InboundQualityLineRepository inboundQualityLineRepository;

    @Autowired
    InboundQualityHeaderService inboundQualityHeaderService;

    @Autowired
    GrLineService grLineService;
    //--------------------------------------------------------------------------

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param inboundQualityNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public InboundQualityLine getInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                    String preInboundNo, String inboundQualityNumber, Long lineNo, String itemCode) {
        Optional<InboundQualityLine> inboundQualityLine =
                inboundQualityLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndLineNoAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        inboundQualityNumber,
                        itemCode,
                        lineNo,
                        0L);
        if (inboundQualityLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",companyCodeId: " + companyCodeId +
                    ",languageId: " + languageId +
                    ",plantId: " + plantId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",inboundQualityNumber: " + inboundQualityNumber +
                    ",itemCode: " + itemCode +
                    ",lineNo: " + lineNo +
                    " doesn't exist.");
        }
        return inboundQualityLine.get();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param inboundQualityNumber
     * @return
     */
    public List<InboundQualityLine> getInboundQualityLines(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                           String preInboundNo, String inboundQualityNumber) {
        List<InboundQualityLine> inboundQualityLine =
                inboundQualityLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        inboundQualityNumber,
                        0L);
        if (inboundQualityLine == null || inboundQualityLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",companyCodeId: " + companyCodeId +
                    ",languageId: " + languageId +
                    ",plantId: " + plantId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",inboundQualityNumber: " + inboundQualityNumber +
                    " doesn't exist.");
        }
        return inboundQualityLine;
    }

    /**
     * @param searchInboundQualityLine
     * @return
     * @throws Exception
     */
    public Stream<InboundQualityLine> findInboundQualityLine(SearchInboundQualityLine searchInboundQualityLine) throws Exception {
        if (searchInboundQualityLine.getStartCreatedOn() != null && searchInboundQualityLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundQualityLine.getStartCreatedOn(), searchInboundQualityLine.getEndCreatedOn());
            searchInboundQualityLine.setStartCreatedOn(dates[0]);
            searchInboundQualityLine.setEndCreatedOn(dates[1]);
        }
        log.info("searchInboundQualityLine Input: {}", searchInboundQualityLine);
        InboundQualityLineSpecification spec = new InboundQualityLineSpecification(searchInboundQualityLine);
        Stream<InboundQualityLine> results = inboundQualityLineRepository.stream(spec, InboundQualityLine.class);
        return results;
    }

    /**
     * @param newInboundQualityLines
     * @param loginUserID
     * @return
     */
    @Transactional
    public List<InboundQualityLine> createInboundQualityLine(List<InboundQualityLine> newInboundQualityLines, String loginUserID) {
        try {
            List<InboundQualityLine> createdInboundQualityLines = new ArrayList<>();
            if (newInboundQualityLines != null && !newInboundQualityLines.isEmpty()) {
                for (InboundQualityLine newInboundQualityLine : newInboundQualityLines) {
                    if (newInboundQualityLine.getInboundQualityNumber() == null || newInboundQualityLine.getLineNo() == null) {
                        throw new BadRequestException("InboundQualityNumber & Line No is must to create quality line");
                    }
                    Optional<InboundQualityLine> inboundQualityLine =
                            inboundQualityLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndLineNoAndDeletionIndicator(
                                    newInboundQualityLine.getLanguageId(),
                                    newInboundQualityLine.getCompanyCodeId(),
                                    newInboundQualityLine.getPlantId(),
                                    newInboundQualityLine.getWarehouseId(),
                                    newInboundQualityLine.getRefDocNumber(),
                                    newInboundQualityLine.getPreInboundNo(),
                                    newInboundQualityLine.getInboundQualityNumber(),
                                    newInboundQualityLine.getItemCode(),
                                    newInboundQualityLine.getLineNo(),
                                    0L);
//                    if (inboundQualityLine.isPresent()) {
//                        throw new BadRequestException("Record is getting duplicated with the given values");
//                    }
                    log.info("create InboundQualityLine Initiated: {}", newInboundQualityLine);
                    InboundQualityLine dbInboundQualityLine = new InboundQualityLine();
                    BeanUtils.copyProperties(newInboundQualityLine, dbInboundQualityLine, CommonUtils.getNullPropertyNames(newInboundQualityLine));
                    if (dbInboundQualityLine.getCompanyCodeId() != null && dbInboundQualityLine.getPlantId() != null &&
                            dbInboundQualityLine.getLanguageId() != null && dbInboundQualityLine.getWarehouseId() != null) {
                        description = getDescription(dbInboundQualityLine.getCompanyCodeId(), dbInboundQualityLine.getPlantId(),
                                dbInboundQualityLine.getLanguageId(), dbInboundQualityLine.getWarehouseId());
                        if (description != null) {
                            dbInboundQualityLine.setCompanyDescription(description.getCompanyDesc());
                            dbInboundQualityLine.setPlantDescription(description.getPlantDesc());
                            dbInboundQualityLine.setWarehouseDescription(description.getWarehouseDesc());
                        }
                    }
                    if (dbInboundQualityLine.getStatusId() != null && dbInboundQualityLine.getLanguageId() != null) {
                        statusDescription = getStatusDescription(dbInboundQualityLine.getStatusId(), dbInboundQualityLine.getLanguageId());
                        if (statusDescription != null) {
                            dbInboundQualityLine.setStatusDescription(statusDescription);
                        }
                    }
                    dbInboundQualityLine.setDeletionIndicator(0L);
                    dbInboundQualityLine.setCreatedBy(loginUserID);
                    dbInboundQualityLine.setCreatedOn(new Date());
                    inboundQualityLineRepository.save(dbInboundQualityLine);
                    createPutAwayHeader(dbInboundQualityLine, loginUserID);
                    createdInboundQualityLines.add(dbInboundQualityLine);

                    //update quality header status after putaway header create using stored procedure
                    inboundQualityHeaderService.updateInboundQualityHeaderStatus(dbInboundQualityLine.getCompanyCodeId(), dbInboundQualityLine.getPlantId(),
                            dbInboundQualityLine.getLanguageId(), dbInboundQualityLine.getWarehouseId(), dbInboundQualityLine.getRefDocNumber(),
                            dbInboundQualityLine.getPreInboundNo(), dbInboundQualityLine.getInboundQualityNumber(),dbInboundQualityLine.getItemCode(),
                            dbInboundQualityLine.getLineNo(), loginUserID);
                }
            }
            return createdInboundQualityLines;
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
     * @param refDocNumber
     * @param preInboundNo
     * @param inboundQualityNumber
     * @param loginUserID
     * @param modifyInboundQualityLines
     * @return
     */
    public List<InboundQualityLine> updateInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String refDocNumber, String preInboundNo, String inboundQualityNumber,
                                                             String loginUserID, List<InboundQualityLine> modifyInboundQualityLines) {
        try {
            List<InboundQualityLine> updatedInboundQualityLines = new ArrayList<>();
            for (InboundQualityLine modifyInboundQualityLine : modifyInboundQualityLines) {
                InboundQualityLine dbInboundQualityLine = getInboundQualityLine(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber,
                        modifyInboundQualityLine.getLineNo(), modifyInboundQualityLine.getItemCode());
                log.info("Update InboundQualityLine Initiated : {}", dbInboundQualityLine);
                BeanUtils.copyProperties(modifyInboundQualityLine, dbInboundQualityLine, CommonUtils.getNullPropertyNames(modifyInboundQualityLine));
                if (dbInboundQualityLine.getStatusId() != null && dbInboundQualityLine.getLanguageId() != null) {
                    statusDescription = getStatusDescription(dbInboundQualityLine.getStatusId(), dbInboundQualityLine.getLanguageId());
                    if (statusDescription != null) {
                        dbInboundQualityLine.setStatusDescription(statusDescription);
                    }
                }
                dbInboundQualityLine.setUpdatedBy(loginUserID);
                dbInboundQualityLine.setUpdatedOn(new Date());
                inboundQualityLineRepository.save(dbInboundQualityLine);
                updatedInboundQualityLines.add(dbInboundQualityLine);
            }
            return updatedInboundQualityLines;
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
     * @param refDocNumber
     * @param preInboundNo
     * @param inboundQualityNumber
     * @param loginUserID
     */
    public void deleteInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                         String preInboundNo, String inboundQualityNumber, String loginUserID) {
        try {
            List<InboundQualityLine> dbInboundQualityLines = getInboundQualityLines(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber);
            log.info("Delete InboundQualityLine Initiated : {}", dbInboundQualityLines);
            for (InboundQualityLine dbInboundQualityLine : dbInboundQualityLines) {
                dbInboundQualityLine.setDeletionIndicator(1L);
                dbInboundQualityLine.setUpdatedBy(loginUserID);
                dbInboundQualityLine.setUpdatedOn(new Date());
                inboundQualityLineRepository.save(dbInboundQualityLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
    /**
     * @param inboundQualityLine
     * @param loginUserID
     * @return
     */
    public void createPutAwayHeader(InboundQualityLine inboundQualityLine, String loginUserID) {
        try {
            FetchImpl dbFetchImpl = new FetchImpl();
            BeanUtils.copyProperties(inboundQualityLine, dbFetchImpl, CommonUtils.getNullPropertyNames(inboundQualityLine));
            dbFetchImpl.setLoginUserID(loginUserID);
            dbFetchImpl.setBatchSerialNumber(inboundQualityLine.getBatchSerialNumber());
            grLineService.createPutAwayHeaderFromInboundQualityLine(dbFetchImpl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}