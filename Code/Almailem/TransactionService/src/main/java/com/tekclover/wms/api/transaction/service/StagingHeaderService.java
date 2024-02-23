package com.tekclover.wms.api.transaction.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.inbound.staging.*;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.SearchStagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.repository.StagingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.StagingHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.StagingHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
public class StagingHeaderService extends BaseService {

    @Autowired
    private StagingHeaderRepository stagingHeaderRepository;

    @Autowired
    private StagingLineRepository stagingLineRepository;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //---------------------------------------------------------------------------------------------------------------------------
    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;
    //---------------------------------------------------------------------------------------------------------------------------

    /**
     * getStagingHeaders
     *
     * @return
     */
    public List<StagingHeader> getStagingHeaders() {
        List<StagingHeader> stagingHeaderList = stagingHeaderRepository.findAll();
        stagingHeaderList = stagingHeaderList.stream()
                .filter(n -> n.getDeletionIndicator() != null &&
                        n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stagingHeaderList;
    }

    /**
     * getStagingHeader
     *
     * @param stagingNo
     * @return
     */
    public StagingHeader getStagingHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo) {
        log.info("Staging Header value : " + getLanguageId() + "," + getCompanyCode()
                + "," + getPlantId() + "," + warehouseId + "," + refDocNumber + "," + preInboundNo + "," + stagingNo);

        Optional<StagingHeader> stagingHeader =
                stagingHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
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
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<StagingHeader> getStagingHeader(String warehouseId, String preInboundNo, String refDocNumber) {
        List<StagingHeader> stagingHeader =
                stagingHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
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
     * @param numberOfCases
     * @param warehouseId
     * @return
     */
    public List<String> generateNumberRanges(Long numberOfCases, String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        List<String> numberRanges = new ArrayList<>();
        for (int i = 0; i < numberOfCases; i++) {
            String nextRangeNumber = getNextRangeNumber(4, warehouseId, authTokenForIDMasterService.getAccess_token());
            numberRanges.add(nextRangeNumber);
        }
        return numberRanges;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<StagingHeader> getStagingHeaderCount(String warehouseId) {
        return stagingHeaderRepository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 12L, 0L);
    }

    /**
     * @param searchStagingHeader
     * @return
     */
    public List<StagingHeader> findStagingHeader(SearchStagingHeader searchStagingHeader)
            throws Exception {
        if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn());
            searchStagingHeader.setStartCreatedOn(dates[0]);
            searchStagingHeader.setEndCreatedOn(dates[1]);
        }

        StagingHeaderSpecification spec = new StagingHeaderSpecification(searchStagingHeader);
        List<StagingHeader> results = stagingHeaderRepository.findAll(spec);
//		log.info("results: " + results);
        return results;
    }

    /**
     * @param searchStagingHeader
     * @return
     * @throws Exception
     */
    //Streaming
    public Stream<StagingHeader> findStagingHeaderNew(SearchStagingHeader searchStagingHeader)
            throws Exception {
        if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn());
            searchStagingHeader.setStartCreatedOn(dates[0]);
            searchStagingHeader.setEndCreatedOn(dates[1]);
        }

        StagingHeaderSpecification spec = new StagingHeaderSpecification(searchStagingHeader);
        Stream<StagingHeader> results = stagingHeaderRepository.stream(spec, StagingHeader.class);

        return results;
    }

    /**
     * createStagingHeader
     *
     * @param newStagingHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingHeader createStagingHeader(AddStagingHeader newStagingHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeader dbStagingHeader = new StagingHeader();
        log.info("newStagingHeader : " + newStagingHeader);
        BeanUtils.copyProperties(newStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(newStagingHeader));
        dbStagingHeader.setDeletionIndicator(0L);
        dbStagingHeader.setCreatedBy(loginUserID);
        dbStagingHeader.setUpdatedBy(loginUserID);
        dbStagingHeader.setCreatedOn(new Date());
        dbStagingHeader.setUpdatedOn(new Date());
        return stagingHeaderRepository.save(dbStagingHeader);
    }

    /**
     * updateStagingHeader
     *
     * @param loginUserID
     * @param stagingNo
     * @param updateStagingHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingHeader updateStagingHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                             String loginUserID, UpdateStagingHeader updateStagingHeader)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeader dbStagingHeader = getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo);
        BeanUtils.copyProperties(updateStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(updateStagingHeader));
        dbStagingHeader.setUpdatedBy(loginUserID);
        dbStagingHeader.setUpdatedOn(new Date());
        return stagingHeaderRepository.save(dbStagingHeader);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void updateStagingHeader(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
                                    String itemCode, Long statusId, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<StagingHeader> dbStagingHeaderList = getStagingHeader(warehouseId, preInboundNo, refDocNumber);
        for (StagingHeader dbStagingHeader : dbStagingHeaderList) {
            dbStagingHeader.setStatusId(statusId);
            dbStagingHeader.setUpdatedBy(loginUserID);
            dbStagingHeader.setUpdatedOn(new Date());
            dbStagingHeader = stagingHeaderRepository.save(dbStagingHeader);
            log.info("dbStagingHeader : " + dbStagingHeader);
        }

        // Line
        List<StagingLineEntity> stagingLineEntityList = stagingLineService.getStagingLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        for (StagingLineEntity stagingLineEntity : stagingLineEntityList) {
            stagingLineEntity.setStatusId(statusId);
            stagingLineEntity.setUpdatedBy(loginUserID);
            stagingLineEntity.setUpdatedOn(new Date());
            stagingLineEntity = stagingLineRepository.save(stagingLineEntity);
            log.info("stagingLineEntity : " + stagingLineEntity);
        }
        log.info("GRHeader & Line updated..");
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<StagingHeader> stagingHeaders = getStagingHeaders();
        stagingHeaders.forEach(stagHeaders -> stagHeaders.setReferenceField1(asnNumber));
        stagingHeaderRepository.saveAll(stagingHeaders);
    }

    /**
     * deleteStagingHeader
     *
     * @param loginUserID
     * @param stagingNo
     */
    public void deleteStagingHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String loginUserID) {
        StagingHeader stagingHeader = getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo);
        if (stagingHeader != null) {
            stagingHeader.setDeletionIndicator(1L);
            stagingHeader.setUpdatedBy(loginUserID);
            stagingHeaderRepository.save(stagingHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: warehouseId: " + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",stagingNo: " + stagingNo + " doesn't exist.");
        }
    }
    //-------------------------------------------Streaming-------------------------------------------------------

    /**
     * @return
     */
    public StreamingResponseBody findStreamStagingHeader() {
        Stream<StagingHeaderStream> stagingHeaderStream = streamStagingHeader();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                jsonGenerator.writeStartArray();
                jsonGenerator.setCodec(new ObjectMapper());
                stagingHeaderStream.forEach(im -> {
                    try {
                        jsonGenerator.writeObject(im);
                    } catch (IOException exception) {
                        log.error("exception occurred while writing object to stream", exception);
                    }
                });
                jsonGenerator.writeEndArray();
                jsonGenerator.close();
            } catch (Exception e) {
                log.info("Exception occurred while publishing data", e);
                e.printStackTrace();
            }
            log.info("finished streaming records");
        };
        return responseBody;
    }
    // =======================================JDBCTemplate=======================================================

//	private final Gson gson = new Gson();

    /**
     * preInboundNo
     * refDocNumber
     * stagingNo
     * inboundOrderTypeId
     * statusId
     * createdBy
     * createdOn
     *
     * @return
     */
    public Stream<StagingHeaderStream> streamStagingHeader() {
        jdbcTemplate.setFetchSize(50);
		/*

		 * ----------------
		 * 	String preInboundNo;
			String refDocNumber;
			String stagingNo;
			Long inboundOrderTypeId;
			Long statusId;
			String createdBy;
			Date createdOn;
		 */
        Stream<StagingHeaderStream> stagingHeaderStream = jdbcTemplate.queryForStream(
                "Select PRE_IB_NO, REF_DOC_NO, STG_NO, IB_ORD_TYP_ID, STATUS_ID, ST_CTD_BY, "
                        + "ST_CTD_ON "
                        + "from tblstagingheader "
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new StagingHeaderStream(
                        resultSet.getString("PRE_IB_NO"),
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getString("STG_NO"),
                        resultSet.getLong("IB_ORD_TYP_ID"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getString("ST_CTD_BY"),
                        resultSet.getDate("ST_CTD_ON")
                ));
        return stagingHeaderStream;
    }

    //===================================================================V2=========================================================

    /**
     * getStagingHeaders
     *
     * @return
     */
    public List<StagingHeaderV2> getStagingHeadersV2() {
        List<StagingHeaderV2> stagingHeaderList = stagingHeaderV2Repository.findAll();
        stagingHeaderList = stagingHeaderList.stream()
                .filter(n -> n.getDeletionIndicator() != null &&
                        n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return stagingHeaderList;
    }

    /**
     * getStagingHeader
     *
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
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public StagingHeaderV2 getStagingHeaderForReversalV2(String companyCode, String plantId, String languageId,
                                                         String warehouseId, String preInboundNo, String refDocNumber) {
        Optional<StagingHeaderV2> stagingHeader =
                stagingHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCode,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L);
        log.info("stagingHeader : " + stagingHeader);
        if (stagingHeader.isEmpty()) {
            return null;
        }
        return stagingHeader.get();
    }

    /**
     * @param numberOfCases
     * @param warehouseId
     * @return
     */
    public List<String> generateNumberRanges(Long numberOfCases, String warehouseId, String companyCodeId,
                                             String plantId, String languageId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        List<String> numberRanges = new ArrayList<>();
        for (int i = 0; i < numberOfCases; i++) {
            String nextRangeNumber = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            numberRanges.add(nextRangeNumber);
        }
        return numberRanges;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<StagingHeaderV2> getStagingHeaderCountV2(String warehouseId) {
        return stagingHeaderV2Repository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 12L, 0L);
    }

    /**
     * @param searchStagingHeader
     * @return
     * @throws Exception
     */
    //Streaming
    public Stream<StagingHeaderV2> findStagingHeaderV2(SearchStagingHeaderV2 searchStagingHeader)
            throws Exception {
        if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn());
            searchStagingHeader.setStartCreatedOn(dates[0]);
            searchStagingHeader.setEndCreatedOn(dates[1]);
        }

        StagingHeaderV2Specification spec = new StagingHeaderV2Specification(searchStagingHeader);
        Stream<StagingHeaderV2> results = stagingHeaderV2Repository.stream(spec, StagingHeaderV2.class);

        return results;
    }

    /**
     * createStagingHeader
     *
     * @param newStagingHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingHeaderV2 createStagingHeaderV2(StagingHeaderV2 newStagingHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StagingHeaderV2 dbStagingHeader = new StagingHeaderV2();
        log.info("newStagingHeader : " + newStagingHeader);
        BeanUtils.copyProperties(newStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(newStagingHeader));
        dbStagingHeader.setDeletionIndicator(0L);
        dbStagingHeader.setCreatedBy(loginUserID);
        dbStagingHeader.setUpdatedBy(loginUserID);
        dbStagingHeader.setCreatedOn(new Date());
        dbStagingHeader.setUpdatedOn(new Date());
        return stagingHeaderV2Repository.save(dbStagingHeader);
    }

    /**
     * updateStagingHeader
     *
     * @param loginUserID
     * @param stagingNo
     * @param updateStagingHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
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
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void updateStagingHeaderV2(String companyCode, String plantId, String languageId,
                                      String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
                                      String itemCode, Long statusId, String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        List<StagingHeaderV2> dbStagingHeaderList = getStagingHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
        for (StagingHeaderV2 dbStagingHeader : dbStagingHeaderList) {
            dbStagingHeader.setStatusId(statusId);
            statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
            dbStagingHeader.setStatusDescription(statusDescription);
            dbStagingHeader.setUpdatedBy(loginUserID);
            dbStagingHeader.setUpdatedOn(new Date());
            dbStagingHeader = stagingHeaderV2Repository.save(dbStagingHeader);
            log.info("dbStagingHeader : " + dbStagingHeader);
        }

        // Line
        List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineV2(companyCode, plantId, languageId,
                warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
        for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityList) {
            stagingLineEntity.setStatusId(statusId);
            stagingLineEntity.setStatusDescription(statusDescription);
            stagingLineEntity.setUpdatedBy(loginUserID);
            stagingLineEntity.setUpdatedOn(new Date());
            stagingLineEntity = stagingLineV2Repository.save(stagingLineEntity);
            log.info("stagingLineEntity : " + stagingLineEntity);
        }
        log.info("GRHeader & Line updated..");
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<StagingHeaderV2> stagingHeaders = getStagingHeadersV2();
        stagingHeaders.stream().forEach(stagHeaders -> stagHeaders.setReferenceField1(asnNumber));
        stagingHeaderV2Repository.saveAll(stagingHeaders);
    }

    /**
     * deleteStagingHeader
     *
     * @param loginUserID
     * @param stagingNo
     */
    public void deleteStagingHeaderV2(String companyCode, String plantId, String languageId,
                                      String warehouseId, String preInboundNo, String refDocNumber,
                                      String stagingNo, String loginUserID) throws ParseException {
        StagingHeaderV2 stagingHeader = getStagingHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo);
        if (stagingHeader != null) {
            stagingHeader.setDeletionIndicator(1L);
            stagingHeader.setUpdatedBy(loginUserID);
            stagingHeader.setUpdatedOn(new Date());
            stagingHeaderV2Repository.save(stagingHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: warehouseId: " + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",stagingNo: " + stagingNo + " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    // Delete StagingHeader
    public StagingHeaderV2 deleteStagingHeaderV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String loginUserID) throws ParseException {
        StagingHeaderV2 stagingHeader = stagingHeaderV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCode, languageId, plantId, warehouseId, refDocNumber, 0L);
        log.info("stagingHeader - cancellation : " + stagingHeader);
        if (stagingHeader != null) {
            stagingHeader.setDeletionIndicator(1L);
            stagingHeader.setUpdatedBy(loginUserID);
            stagingHeader.setUpdatedOn(new Date());
            stagingHeaderV2Repository.save(stagingHeader);
        }
        return stagingHeader;
    }
}
