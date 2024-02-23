package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.transaction.model.inbound.gr.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.SearchGrHeaderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.GrHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.GrHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class GrHeaderService extends BaseService {
    @Autowired
    private GrHeaderRepository grHeaderRepository;

    @Autowired
    private GrLineRepository grLineRepository;

    @Autowired
    private GrLineService grLineService;

    //--------------------------------------------------------------------------
    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    private GrLineV2Repository grLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    ErrorLogRepository exceptionLogRepo;

    String statusDescription = null;
    //--------------------------------------------------------------------------

    /**
     * getGrHeaders
     *
     * @return
     */
    public List<GrHeader> getGrHeaders() {
        List<GrHeader> grHeaderList = grHeaderRepository.findAll();
        grHeaderList =
                grHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return grHeaderList;
    }

    /**
     * getGrHeader
     *
     * @param goodsReceiptNo
     * @return
     */
    public GrHeader getGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                String goodsReceiptNo, String palletCode, String caseCode) {
        Optional<GrHeader> grHeader =
                grHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        stagingNo,
                        goodsReceiptNo,
                        palletCode,
                        caseCode,
                        0L);
        if (grHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",stagingNo: " + stagingNo +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    " doesn't exist.");
        }

        return grHeader.get();
    }

    /**
     * @param warehouseId
     * @param goodsReceiptNo
     * @param caseCode
     * @param refDocNumber
     * @return
     */
    public List<GrHeader> getGrHeader(String warehouseId, String goodsReceiptNo, String caseCode, String refDocNumber) {
        List<GrHeader> grHeader =
                grHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndCaseCodeAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        goodsReceiptNo,
                        caseCode,
                        refDocNumber,
                        0L);
        if (grHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",goodsReceiptNo: " + goodsReceiptNo + "," +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }
        return grHeader;
    }

    /**
     * @param refDocNumber
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @return
     */
    public List<GrHeader> getGrHeaderForReverse(String refDocNumber, String warehouseId, String preInboundNo, String caseCode) {
        List<GrHeader> grHeader =
                grHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        warehouseId,
                        preInboundNo,
                        caseCode,
                        0L);
        if (grHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }
        return grHeader;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<GrHeader> getGrHeader(String warehouseId, String preInboundNo, String refDocNumber) {
        List<GrHeader> grHeader =
                grHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L);
        if (grHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ", refDocNumber: " + refDocNumber +
                    ", preInboundNo: " + preInboundNo +
                    " doesn't exist.");
        }
        return grHeader;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    public List<GrHeader> getGrHeader(String companyCode, String plantId, String languageId, String warehouseId, List<Long> status) {
        return grHeaderRepository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndStatusIdInAndDeletionIndicator(
                companyCode, languageId, plantId, warehouseId, status, 0L);
    }

    /**
     * @param searchGrHeader
     * @return
     * @throws ParseException
     */
    public List<GrHeader> findGrHeader(SearchGrHeader searchGrHeader) throws Exception {
        searchGrHeader.setDeletionIndicator(0L);
        if (searchGrHeader.getStartCreatedOn() != null && searchGrHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn());
            searchGrHeader.setStartCreatedOn(dates[0]);
            searchGrHeader.setEndCreatedOn(dates[1]);
        }
        GrHeaderSpecification spec = new GrHeaderSpecification(searchGrHeader);
        List<GrHeader> results = grHeaderRepository.findAll(spec);
//		log.info("results: " + results);
        return results;
    }

    /**
     * @param searchGrHeader
     * @return
     * @throws Exception
     */
    public Stream<GrHeader> findGrHeaderNew(SearchGrHeader searchGrHeader) throws Exception {
        searchGrHeader.setDeletionIndicator(0L);
        if (searchGrHeader.getStartCreatedOn() != null && searchGrHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn());
            searchGrHeader.setStartCreatedOn(dates[0]);
            searchGrHeader.setEndCreatedOn(dates[1]);
        }
        GrHeaderSpecification spec = new GrHeaderSpecification(searchGrHeader);
        Stream<GrHeader> results = grHeaderRepository.stream(spec, GrHeader.class);
        return results;
    }

    /**
     * createGrHeader
     *
     * @param newGrHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrHeader createGrHeader(AddGrHeader newGrHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Optional<GrHeader> grheader =
                grHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        newGrHeader.getWarehouseId(),
                        newGrHeader.getPreInboundNo(),
                        newGrHeader.getRefDocNumber(),
                        newGrHeader.getStagingNo(),
                        newGrHeader.getGoodsReceiptNo(),
                        newGrHeader.getPalletCode(),
                        newGrHeader.getCaseCode(),
                        0L);
        if (!grheader.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        GrHeader dbGrHeader = new GrHeader();
        log.info("newGrHeader : " + newGrHeader);
        BeanUtils.copyProperties(newGrHeader, dbGrHeader, CommonUtils.getNullPropertyNames(newGrHeader));

        dbGrHeader.setCompanyCode(getCompanyCode());
        dbGrHeader.setPlantId(getPlantId());
        dbGrHeader.setDeletionIndicator(0L);
        dbGrHeader.setCreatedBy(loginUserID);
        dbGrHeader.setUpdatedBy(loginUserID);
        dbGrHeader.setCreatedOn(new Date());
        dbGrHeader.setUpdatedOn(new Date());
        return grHeaderRepository.save(dbGrHeader);
    }

    /**
     * updateGrHeader
     *
     * @param goodsReceiptNo
     * @param updateGrHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrHeader updateGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String goodsReceiptNo, String palletCode, String caseCode,
                                   String loginUserID, UpdateGrHeader updateGrHeader)
            throws IllegalAccessException, InvocationTargetException {
        GrHeader dbGrHeader = getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                palletCode, caseCode);
        BeanUtils.copyProperties(updateGrHeader, dbGrHeader, CommonUtils.getNullPropertyNames(updateGrHeader));
        dbGrHeader.setUpdatedBy(loginUserID);
        dbGrHeader.setUpdatedOn(new Date());
        return grHeaderRepository.save(dbGrHeader);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     */
    public void updateGrHeader(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
                               String itemCode, Long statusId, String loginUserID) {
        List<GrHeader> grHeaderList = getGrHeader(warehouseId, preInboundNo, refDocNumber);
        for (GrHeader dbGrHeader : grHeaderList) {
            dbGrHeader.setStatusId(statusId);
            dbGrHeader.setUpdatedBy(loginUserID);
            dbGrHeader.setUpdatedOn(new Date());
            grHeaderRepository.save(dbGrHeader);
        }

        // Line
        List<GrLine> grLineList = grLineService.getGrLineForUpdate(warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
        for (GrLine grLine : grLineList) {
            grLine.setStatusId(statusId);
            grLine.setUpdatedBy(loginUserID);
            grLine.setUpdatedOn(new Date());
            grLineRepository.save(grLine);
        }
        log.info("GRHeader & Line updated..");
    }

    /**
     * Pass the selected REF_DOC_NO/PACK_BARCODE along with REF_DOC_NO/PRE_IB_NO/WH_ID/CASE_CODE values in GRLINE and GRHEADER tables
     * and update Status ID as 16
     *
     * @param refDocNumber
     * @param packBarcodes
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @param loginUserID
     */
    public void updateGrHeader(String refDocNumber, String packBarcodes, String warehouseId, String preInboundNo,
                               String caseCode, String loginUserID) {
        List<GrHeader> grHeaderList = getGrHeaderForReverse(refDocNumber, warehouseId, preInboundNo, caseCode);
        for (GrHeader dbGrHeader : grHeaderList) {
            dbGrHeader.setStatusId(16L);
            dbGrHeader.setUpdatedBy(loginUserID);
            dbGrHeader.setUpdatedOn(new Date());
            dbGrHeader = grHeaderRepository.save(dbGrHeader);
            log.info("dbGrHeader updated : " + dbGrHeader);
        }

        List<GrLine> grLineList = grLineService.getGrLine(refDocNumber, packBarcodes, warehouseId, preInboundNo, caseCode);
        for (GrLine dbGrLine : grLineList) {
            dbGrLine.setStatusId(16L);
            dbGrLine.setUpdatedBy(loginUserID);
            dbGrLine.setUpdatedOn(new Date());
            dbGrLine = grLineRepository.save(dbGrLine);
            log.info("dbGrLine updated : " + dbGrLine);
        }
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<GrHeader> grHeaders = getGrHeaders();
        grHeaders.forEach(g -> g.setReferenceField1(asnNumber));
        grHeaderRepository.saveAll(grHeaders);
    }

    /**
     * deleteGrHeader
     *
     * @param loginUserID
     * @param goodsReceiptNo
     */
    public void deleteGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String goodsReceiptNo, String palletCode, String caseCode, String loginUserID) {
        GrHeader grHeader = getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                palletCode, caseCode);
        if (grHeader != null) {
            grHeader.setDeletionIndicator(1L);
            grHeader.setUpdatedBy(loginUserID);
            grHeaderRepository.save(grHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + goodsReceiptNo);
        }
    }

    //=======================================================================V2===========================================================================

    /**
     * getGrHeaders
     *
     * @return
     */
    public List<GrHeaderV2> getGrHeadersV2() {
        List<GrHeaderV2> grHeaderList = grHeaderV2Repository.findAll();
        grHeaderList =
                grHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return grHeaderList;
    }

    /**
     *
     * @param warehouseId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param refDocNumber
     * @return
     */
//    public List<GrHeaderV2> getGrHeaderV2(String warehouseId, String companyCode, String plantId,
//                                    String languageId, String refDocNumber) {
//        List<GrHeaderV2> grHeader =
//                grHeaderV2Repository.getGrHeaderV2(
//                        warehouseId, companyCode, plantId, languageId, refDocNumber);
//        if (grHeader.isEmpty()) {
//            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
//                    ",refDocNumber: " + refDocNumber + "," +
//                    ", companyCode: " + companyCode + "," +
//                    ", plantId: " + plantId +
//                    " doesn't exist.");
//        }
//
//        return grHeader;
//    }

    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @return
     */
    public GrHeaderV2 getGrHeaderV2(String companyCode, String languageId, String plantId,
                                    String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                    String goodsReceiptNo, String palletCode, String caseCode) {
        Optional<GrHeaderV2> grHeader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        stagingNo,
                        goodsReceiptNo,
                        palletCode,
                        caseCode,
                        0L);
        if (grHeader.isEmpty()) {
            // Exception Log
            createGrHeaderLog(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, stagingNo, goodsReceiptNo,
                    palletCode, caseCode, "GrHeaderV2 with goodsReceiptNo - " + goodsReceiptNo + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",stagingNo: " + stagingNo +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    " doesn't exist.");
        }

        return grHeader.get();
    }

    /**
     * @param warehouseId
     * @param goodsReceiptNo
     * @param caseCode
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param refDocNumber
     * @return
     */
    public GrHeaderV2 getGrHeaderV2(String warehouseId, String goodsReceiptNo, String caseCode, String companyCode,
                                    String languageId, String plantId, String refDocNumber) {
        GrHeaderV2 grHeader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndCaseCodeAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        goodsReceiptNo,
                        caseCode,
                        refDocNumber,
                        0L);
        if (grHeader == null) {
            // Exception Log
            createGrHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, goodsReceiptNo,
                    caseCode, "The given values of GrheaderV2 with goodsReceiptNo - " + goodsReceiptNo + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",goodsReceiptNo: " + goodsReceiptNo + "," +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }
        return grHeader;
    }

    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<GrHeaderV2> getGrHeaderV2(String companyCode, String languageId, String plantId,
                                          String warehouseId, String preInboundNo, String refDocNumber) {
        List<GrHeaderV2> grHeader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L);
        if (grHeader.isEmpty()) {
            // Exception Log
            createGrHeaderLog2(languageId, companyCode, plantId, warehouseId, preInboundNo, refDocNumber,
                    "The given values of GrHeaderV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ", refDocNumber: " + refDocNumber +
                    ", preInboundNo: " + preInboundNo +
                    " doesn't exist.");
        }
        return grHeader;
    }

    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public GrHeaderV2 getGrHeaderForReversalV2(String companyCode, String plantId, String languageId,
                                               String warehouseId, String refDocNumber) {
        GrHeaderV2 grHeader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        0L);
        if (grHeader == null) {
            return null;
        }
        return grHeader;
    }

    /**
     * @param refDocNumber
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @return
     */
    public List<GrHeaderV2> getGrHeaderForReverseV2(String companyCode, String languageId, String plantId,
                                                    String refDocNumber, String warehouseId, String preInboundNo, String caseCode) {
        List<GrHeaderV2> grHeader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        warehouseId,
                        preInboundNo,
                        caseCode,
                        0L);
        if (grHeader.isEmpty()) {
            // Exception Log
            createGrHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
                    caseCode, "The given values of GrHeaderV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }
        return grHeader;
    }

    /**
     * @param searchGrHeader
     * @return
     * @throws Exception
     */
    public Stream<GrHeaderV2> findGrHeaderV2(SearchGrHeaderV2 searchGrHeader) throws Exception {
        searchGrHeader.setDeletionIndicator(0L);
        if (searchGrHeader.getStartCreatedOn() != null && searchGrHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn());
            searchGrHeader.setStartCreatedOn(dates[0]);
            searchGrHeader.setEndCreatedOn(dates[1]);
        }
        GrHeaderV2Specification spec = new GrHeaderV2Specification(searchGrHeader);
        Stream<GrHeaderV2> results = grHeaderV2Repository.stream(spec, GrHeaderV2.class);
        return results;
    }

    /**
     * @param newGrHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrHeaderV2 createGrHeaderV2(GrHeaderV2 newGrHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<GrHeaderV2> grheader =
                grHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
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
            createGrHeaderLog4(newGrHeader, "Record is getting duplicated with the given values.");

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
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param loginUserID
     * @param updateGrHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrHeaderV2 updateGrHeaderV2(String companyCode, String languageId, String plantId,
                                       String warehouseId, String preInboundNo, String refDocNumber,
                                       String stagingNo, String goodsReceiptNo, String palletCode, String caseCode,
                                       String loginUserID, GrHeaderV2 updateGrHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        GrHeaderV2 dbGrHeader = getGrHeaderV2(companyCode, plantId, languageId,
                warehouseId, preInboundNo, refDocNumber,
                stagingNo, goodsReceiptNo,
                palletCode, caseCode);
        BeanUtils.copyProperties(updateGrHeader, dbGrHeader, CommonUtils.getNullPropertyNames(updateGrHeader));
        dbGrHeader.setUpdatedBy(loginUserID);
        dbGrHeader.setUpdatedOn(new Date());
        return grHeaderV2Repository.save(dbGrHeader);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     */
    public void updateGrHeaderV2(String companyCode, String languageId, String plantId,
                                 String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
                                 String itemCode, Long statusId, String loginUserID) throws ParseException {
        List<GrHeaderV2> grHeaderList = getGrHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
        for (GrHeaderV2 dbGrHeader : grHeaderList) {
            dbGrHeader.setStatusId(statusId);

            statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
            dbGrHeader.setStatusDescription(statusDescription);

            dbGrHeader.setUpdatedBy(loginUserID);
            dbGrHeader.setUpdatedOn(new Date());
            grHeaderV2Repository.save(dbGrHeader);
        }

        // Line
        List<GrLineV2> grLineList = grLineService.getGrLineForUpdateV2(companyCode, languageId, plantId, warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
        for (GrLineV2 grLine : grLineList) {
            grLine.setStatusId(statusId);
            statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
            grLine.setStatusDescription(statusDescription);
            grLine.setUpdatedBy(loginUserID);
            grLine.setUpdatedOn(new Date());
            grLineV2Repository.save(grLine);
        }
        log.info("GRHeader & Line updated..");
    }

    /**
     * Pass the selected REF_DOC_NO/PACK_BARCODE along with REF_DOC_NO/PRE_IB_NO/WH_ID/CASE_CODE values in GRLINE and GRHEADER tables
     * and update Status ID as 16
     *
     * @param refDocNumber
     * @param packBarcodes
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @param loginUserID
     */
    public void updateGrHeaderV2(String companyCode, String languageId, String plantId,
                                 String refDocNumber, String packBarcodes, String warehouseId, String preInboundNo,
                                 String caseCode, String loginUserID) throws ParseException {
        List<GrHeaderV2> grHeaderList = getGrHeaderForReverseV2(companyCode, languageId, plantId, refDocNumber, warehouseId, preInboundNo, caseCode);
        for (GrHeaderV2 dbGrHeader : grHeaderList) {
            dbGrHeader.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            dbGrHeader.setStatusDescription(statusDescription);
            dbGrHeader.setUpdatedBy(loginUserID);
            dbGrHeader.setUpdatedOn(new Date());
            dbGrHeader = grHeaderV2Repository.save(dbGrHeader);
            log.info("dbGrHeader updated : " + dbGrHeader);
        }

        List<GrLineV2> grLineList = grLineService.getGrLineV2(companyCode, languageId, plantId, refDocNumber, packBarcodes, warehouseId, preInboundNo, caseCode);
        for (GrLineV2 dbGrLine : grLineList) {
            dbGrLine.setStatusId(16L);
            statusDescription = stagingLineV2Repository.getStatusDescription(16L, languageId);
            dbGrLine.setStatusDescription(statusDescription);
            dbGrLine.setUpdatedBy(loginUserID);
            dbGrLine.setUpdatedOn(new Date());
            dbGrLine = grLineV2Repository.save(dbGrLine);
            log.info("dbGrLine updated : " + dbGrLine);
        }
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<GrHeader> grHeaders = getGrHeaders();
        grHeaders.forEach(g -> g.setReferenceField1(asnNumber));
        grHeaderRepository.saveAll(grHeaders);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param loginUserID
     */
    public void deleteGrHeaderV2(String companyCode, String languageId, String plantId,
                                 String warehouseId, String preInboundNo, String refDocNumber,
                                 String stagingNo, String goodsReceiptNo, String palletCode,
                                 String caseCode, String loginUserID) throws ParseException {
        GrHeaderV2 grHeader = getGrHeaderV2(companyCode, languageId, plantId, warehouseId,
                preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                palletCode, caseCode);
        if (grHeader != null) {
            grHeader.setDeletionIndicator(1L);
            grHeader.setUpdatedBy(loginUserID);
            grHeader.setUpdatedOn(new Date());
            grHeaderV2Repository.save(grHeader);
        } else {
            // Exception Log
            createGrHeaderLog(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, stagingNo, goodsReceiptNo,
                    palletCode, caseCode, "Error in deleting GrHeaderV2 with goodsReceiptNo - " + goodsReceiptNo);

            throw new EntityNotFoundException("Error in deleting Id: " + goodsReceiptNo);
        }
    }

    //Delete GrHeader
    public GrHeaderV2 deleteGrHeaderV2(String companyCode, String languageId, String plantId,
                                       String warehouseId, String refDocNumber, String loginUserID) throws ParseException {

        GrHeaderV2 grHeader = grHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCode, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("GrHeader - Cancellation : " + grHeader);
        if (grHeader != null) {
            grHeader.setDeletionIndicator(1L);
            grHeader.setUpdatedBy(loginUserID);
            grHeader.setUpdatedOn(new Date());
            grHeaderV2Repository.save(grHeader);
        }
        return grHeader;
    }

    //===========================================GrHeader_ExceptionLog=================================================
    private void createGrHeaderLog(String languageId, String companyCode, String plantId, String warehouseId,
                                   String refDocNumber, String preInboundNo, String stagingNo,
                                   String goodsReceiptNo, String palletCode, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(goodsReceiptNo);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setReferenceField2(stagingNo);
        dbErrorLog.setReferenceField3(palletCode);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    private void createGrHeaderLog1(String languageId, String companyCode, String plantId, String warehouseId,
                                    String refDocNumber, String goodsReceiptNo, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(goodsReceiptNo);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    private void createGrHeaderLog2(String languageId, String companyCode, String plantId, String warehouseId,
                                    String preInboundNo, String refDocNumber, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    private void createGrHeaderLog3(String languageId, String companyCode, String plantId,
                                    String warehouseId, String refDocNumber, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    private void createGrHeaderLog4(GrHeaderV2 grHeaderV2, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(grHeaderV2.getRefDocNumber());
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(grHeaderV2.getLanguageId());
        dbErrorLog.setCompanyCodeId(grHeaderV2.getCompanyCode());
        dbErrorLog.setPlantId(grHeaderV2.getPlantId());
        dbErrorLog.setWarehouseId(grHeaderV2.getWarehouseId());
        dbErrorLog.setRefDocNumber(grHeaderV2.getRefDocNumber());
        dbErrorLog.setReferenceField1(grHeaderV2.getPreInboundNo());
        dbErrorLog.setReferenceField2(grHeaderV2.getGoodsReceiptNo());
        dbErrorLog.setReferenceField3(grHeaderV2.getStagingNo());
        dbErrorLog.setReferenceField4(grHeaderV2.getPalletCode());
        dbErrorLog.setReferenceField5(grHeaderV2.getCaseCode());
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

}
