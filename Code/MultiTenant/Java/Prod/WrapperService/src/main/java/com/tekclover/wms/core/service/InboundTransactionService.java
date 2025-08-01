package com.tekclover.wms.core.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.dto.MultiDbInput;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.dto.StagingLineUpdate;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.inbound.ASN;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.FindOutboundOrderLineV2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.FindOutboundOrderV2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.OutboundOrderLineV2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.OutboundOrderV2;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Service
public class InboundTransactionService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcParamTemplate;

    @Autowired
    AuthTokenService authTokenService;

    /**
     * @return
     */
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    /**
     * @return
     */
    private String getInboundTransactionServiceApiUrl() {
        return propertiesConfig.getInboundTransactionServiceUrl();
    }

    /**
     * @return
     */
    private String getInboundOrderServiceApiUrl() {
        return propertiesConfig.getInboundOrderServiceUrl();
    }


    public List<GrHeaderStream> streamGrHeader() {
        jdbcTemplate.setFetchSize(50);
        /**
         * Gr Header
         * String languageId
         * String companyCodeId
         * String plantId
         * String warehouseId
         * String preInboundNo
         * String refDocNumber
         * String stagingNo
         * String goodsReceiptNo
         * String palletCode
         * String caseCode
         * Long inboundOrderTypeId
         * Long statusId
         * String grMethod
         * String containerReceiptNo
         * String dockAllocationNo
         * String containerNo
         * String vechicleNo
         * Date expectedArrivalDate
         * Date goodsReceiptDate
         * Long deletionIndicator
         * String referenceField1
         * String referenceField2
         * String referenceField3
         * String referenceField4
         * String referenceField5
         * String referenceField6
         * String referenceField7
         * String referenceField8
         * String referenceField9
         * String referenceField10
         * String createdBy
         * Date createdOn
         * String updatedBy
         * Date updatedOn
         * String confirmedBy
         * Date confirmedOn
         */

        List<GrHeaderStream> grHeaderStream = jdbcTemplate.query(
//		Stream<GrHeaderStream> grHeaderStream = jdbcTemplate.queryForStream(
                "Select LANG_ID, C_ID, PLANT_ID, WH_ID, PRE_IB_NO, REF_DOC_NO, "
                        + "STG_NO, GR_NO, PAL_CODE, CASE_CODE, IB_ORD_TYP_ID, STATUS_ID, "
                        + "GR_MTD, CONT_REC_NO, DOCK_ALL_NO, CONT_NO, VEH_NO, EA_DATE, "
                        + "GR_DATE, IS_DELETED, REF_FIELD_1, REF_FIELD_2, REF_FIELD_3, REF_FIELD_4, "
                        + "REF_FIELD_5, REF_FIELD_6, REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, "
                        + "GR_CTD_BY, GR_CTD_ON, GR_UTD_BY, GR_UTD_ON, GR_CNF_BY, GR_CNF_ON "
                        + "from tblgrheader "
//						+ "where IS_DELETED = 0 AND WH_ID in (?,?,?,?)",new Object[] {110,111,0,0},
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new GrHeaderStream(
                        resultSet.getString("LANG_ID"),
                        resultSet.getString("C_ID"),
                        resultSet.getString("PLANT_ID"),
                        resultSet.getString("WH_ID"),
                        resultSet.getString("PRE_IB_NO"),
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getString("STG_NO"),
                        resultSet.getString("GR_NO"),
                        resultSet.getString("PAL_CODE"),
                        resultSet.getString("CASE_CODE"),
                        resultSet.getLong("IB_ORD_TYP_ID"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getString("GR_MTD"),
                        resultSet.getString("CONT_REC_NO"),
                        resultSet.getString("DOCK_ALL_NO"),
                        resultSet.getString("CONT_NO"),
                        resultSet.getString("VEH_NO"),
                        resultSet.getDate("EA_DATE"),
                        resultSet.getDate("GR_DATE"),
                        resultSet.getLong("IS_DELETED"),
                        resultSet.getString("REF_FIELD_1"),
                        resultSet.getString("REF_FIELD_2"),
                        resultSet.getString("REF_FIELD_3"),
                        resultSet.getString("REF_FIELD_4"),
                        resultSet.getString("REF_FIELD_5"),
                        resultSet.getString("REF_FIELD_6"),
                        resultSet.getString("REF_FIELD_7"),
                        resultSet.getString("REF_FIELD_8"),
                        resultSet.getString("REF_FIELD_9"),
                        resultSet.getString("REF_FIELD_10"),
                        resultSet.getString("GR_CTD_BY"),
                        resultSet.getDate("GR_CTD_ON"),
                        resultSet.getString("GR_UTD_BY"),
                        resultSet.getDate("GR_UTD_ON"),
                        resultSet.getString("GR_CNF_BY"),
                        resultSet.getDate("GR_CNF_ON")
                ));
        return grHeaderStream;
    }


    /*------------------------------ProcessInboundReceived-----------------------------------------------------------------*/
    // POST
//    public PreInboundHeader processInboundReceived(String authToken) {
//        InboundIntegrationHeader createdInboundIntegrationHeader = mongoTransactionRepository
//                .findTopByOrderByOrderReceivedOnDesc();
//        log.info("Latest InboundIntegrationHeader : " + createdInboundIntegrationHeader);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundOrderServiceApiUrl()
//                + "preinboundheader/" + createdInboundIntegrationHeader.getRefDocumentNo() + "/processInboundReceived");
//        HttpEntity<?> entity = new HttpEntity<>(createdInboundIntegrationHeader, headers);
//        ResponseEntity<PreInboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
//                entity, PreInboundHeader.class);
//        return result.getBody();
//    }

    // --------------------------------------------PreInboundHeader------------------------------------------------------------------------
    // GET ALL
    public PreInboundHeader[] getPreInboundHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, PreInboundHeader[].class);

//			return result.getBody();
            //Adding time to Date
            List<PreInboundHeader> obList = new ArrayList<>();
            for (PreInboundHeader preInboundHeader : result.getBody()) {

                obList.add(addingTimeWithDate(preInboundHeader));

            }
            return obList.toArray(new PreInboundHeader[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PreInboundHeader getPreInboundHeader(String preInboundNo, String warehouseId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PreInboundHeader.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
            return addingTimeWithDate(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public StagingHeader processASN(List<PreInboundLine> newPreInboundLine, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/processASN")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPreInboundLine, headers);
            ResponseEntity<StagingHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, StagingHeader.class);
//			log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PreInboundHeader[] getPreInboundHeaderWithStatusId(String warehouseId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/inboundconfirm")
                    .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, PreInboundHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

            //Adding time to Date
//            List<PreInboundHeader> obList = new ArrayList<>();
//            for (PreInboundHeader preInboundHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDate(preInboundHeader));
//
//            }
//            return obList.toArray(new PreInboundHeader[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PreInboundHeader createPreInboundHeader(PreInboundHeader newPreInboundHeader, String loginUserID,
                                                   String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader").queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPreInboundHeader, headers);
        ResponseEntity<PreInboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, PreInboundHeader.class);
//		log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - findPreInboundHeader
    public PreInboundHeader[] findPreInboundHeader(SearchPreInboundHeader searchPreInboundHeader, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/findPreInboundHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchPreInboundHeader, headers);
            ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PreInboundHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//            List<PreInboundHeader> obList = new ArrayList<>();
//            for (PreInboundHeader preInboundHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDate(preInboundHeader));
//
//            }
//            return obList.toArray(new PreInboundHeader[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findPreInboundHeader - Stream
    public PreInboundHeader[] findPreInboundHeaderNew(SearchPreInboundHeader searchPreInboundHeader, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/findPreInboundHeaderNew");
            HttpEntity<?> entity = new HttpEntity<>(searchPreInboundHeader, headers);
            ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PreInboundHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PreInboundHeader> obList = new ArrayList<>();
//            for (PreInboundHeader preInboundHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDate(preInboundHeader));
//
//            }
//            return obList.toArray(new PreInboundHeader[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PreInboundHeader addingTimeWithDate(PreInboundHeader preInboundHeader) throws ParseException {

        if (preInboundHeader.getRefDocDate() != null) {
            preInboundHeader.setRefDocDate(DateUtils.addTimeToDate(preInboundHeader.getRefDocDate(), 3));
        }

        if (preInboundHeader.getCreatedOn() != null) {
            preInboundHeader.setCreatedOn(DateUtils.addTimeToDate(preInboundHeader.getCreatedOn(), 3));
        }

        if (preInboundHeader.getUpdatedOn() != null) {
            preInboundHeader.setUpdatedOn(DateUtils.addTimeToDate(preInboundHeader.getUpdatedOn(), 3));
        }

        return preInboundHeader;
    }

    // PATCH
    public PreInboundHeader updatePreInboundHeader(String preInboundNo, String warehouseId, String loginUserID,
                                                   PreInboundHeader modifiedPreInboundHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PreInboundHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, PreInboundHeader.class);
//			log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePreInboundHeader(String preInboundNo, String warehouseId, String loginUserID,
                                          String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
//			log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------PreInboundLine------------------------------------------------------------------------
    // GET ALL
    public PreInboundLine[] getPreInboundLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PreInboundLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//            List<PreInboundLine> obList = new ArrayList<>();
//            for (PreInboundLine preInboundLine : result.getBody()) {
//
//                obList.add(addingTimeWithDatePreInboundLine(preInboundLine));
//
//            }
//            return obList.toArray(new PreInboundLine[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PreInboundLine addingTimeWithDatePreInboundLine(PreInboundLine preInboundLine) throws ParseException {

        if (preInboundLine.getExpectedArrivalDate() != null) {
            preInboundLine.setExpectedArrivalDate(DateUtils.addTimeToDate(preInboundLine.getExpectedArrivalDate(), 3));
        }

        if (preInboundLine.getCreatedOn() != null) {
            preInboundLine.setCreatedOn(DateUtils.addTimeToDate(preInboundLine.getCreatedOn(), 3));
        }

        if (preInboundLine.getUpdatedOn() != null) {
            preInboundLine.setUpdatedOn(DateUtils.addTimeToDate(preInboundLine.getUpdatedOn(), 3));
        }

        return preInboundLine;
    }

    // GET
    public PreInboundLine getPreInboundLine(String preInboundNo, String warehouseId, String refDocNumber, Long lineNo,
                                            String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("lineNo", lineNo).queryParam("itemCode", itemCode);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PreInboundLine.class);
//			log.info("result : " + result.getStatusCode());
            return addingTimeWithDatePreInboundLine(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PreInboundLine[] getPreInboundLine(String preInboundNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/" + preInboundNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PreInboundLine[].class);
            log.info("result : " + result.getStatusCode());

            List<PreInboundLine> obList = new ArrayList<>();
            for (PreInboundLine preInboundLine : result.getBody()) {

                obList.add(addingTimeWithDatePreInboundLine(preInboundLine));

            }
            return obList.toArray(new PreInboundLine[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PreInboundLine createPreInboundLine(PreInboundLine newPreInboundLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline").queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPreInboundLine, headers);
        ResponseEntity<PreInboundLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, PreInboundLine.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // CREATE - BOM
    public PreInboundLine[] createPreInboundLineBOM(String preInboundNo, String warehouseId, String refDocNumber,
                                                    String itemCode, Long lineNo, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/bom")
                .queryParam("preInboundNo", preInboundNo).queryParam("warehouseId", warehouseId)
                .queryParam("refDocNumber", refDocNumber).queryParam("itemCode", itemCode).queryParam("lineNo", lineNo)
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<PreInboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, PreInboundLine[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PreInboundLine updatePreInboundLine(String preInboundNo, String warehouseId, String refDocNumber,
                                               Long lineNo, String itemCode, String loginUserID, PreInboundLine modifiedPreInboundLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundLine, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("lineNo", lineNo).queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<PreInboundLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, PreInboundLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePreInboundLine(String preInboundNo, String warehouseId, String refDocNumber, Long lineNo,
                                        String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("lineNo", lineNo).queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------ContainerReceipt------------------------------------------------------------------------
    // GET ALL
    public ContainerReceipt[] getContainerReceipts(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ContainerReceipt[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, ContainerReceipt[].class);
            log.info("result : " + result.getStatusCode());
//			return result.getBody();

            List<ContainerReceipt> obList = new ArrayList<>();
            for (ContainerReceipt containerReceipt : result.getBody()) {

                obList.add(addingTimeWithDateContainerReceipt(containerReceipt));

            }
            return obList.toArray(new ContainerReceipt[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public ContainerReceipt addingTimeWithDateContainerReceipt(ContainerReceipt containerReceipt) throws ParseException {

        if (containerReceipt.getContainerReceivedDate() != null) {
            containerReceipt.setContainerReceivedDate(DateUtils.addTimeToDate(containerReceipt.getContainerReceivedDate(), 3));
        }

        return containerReceipt;
    }

    // GET
    public ContainerReceipt getContainerReceipt(String preInboundNo, String refDocNumber, String containerReceiptNo,
                                                String loginUserID, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
                    .queryParam("preInboundNo", preInboundNo).queryParam("refDocNumber", refDocNumber)
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, ContainerReceipt.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateContainerReceipt(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public ContainerReceipt getContainerReceipt(String containerReceiptNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, ContainerReceipt.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateContainerReceipt(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findContainerReceipt
    public ContainerReceipt[] findContainerReceipt(SearchContainerReceipt searchContainerReceipt, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/findContainerReceipt");
            HttpEntity<?> entity = new HttpEntity<>(searchContainerReceipt, headers);
            ResponseEntity<ContainerReceipt[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, ContainerReceipt[].class);

            List<ContainerReceipt> obList = new ArrayList<>();
            for (ContainerReceipt containerReceipt : result.getBody()) {
//				log.info("Result containerReceipt---getContainerReceivedDate() :" + containerReceipt.getContainerReceivedDate());

                obList.add(addingTimeWithDateContainerReceipt(containerReceipt));
            }
            return obList.toArray(new ContainerReceipt[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findContainerReceipt - Streaming
    public ContainerReceipt[] findContainerReceiptNew(SearchContainerReceipt searchContainerReceipt, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/findContainerReceiptNew");
            HttpEntity<?> entity = new HttpEntity<>(searchContainerReceipt, headers);
            ResponseEntity<ContainerReceipt[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, ContainerReceipt[].class);

            List<ContainerReceipt> obList = new ArrayList<>();
            for (ContainerReceipt containerReceipt : result.getBody()) {
//				log.info("Result containerReceipt---getContainerReceivedDate() :" + containerReceipt.getContainerReceivedDate());

                obList.add(addingTimeWithDateContainerReceipt(containerReceipt));
            }
            return obList.toArray(new ContainerReceipt[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public ContainerReceipt createContainerReceipt(ContainerReceipt newContainerReceipt, String loginUserID,
                                                   String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt").queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newContainerReceipt, headers);
        ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, ContainerReceipt.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public ContainerReceipt updateContainerReceipt(String containerReceiptNo, String loginUserID,
                                                   ContainerReceipt modifiedContainerReceipt, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedContainerReceipt, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<ContainerReceipt> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, ContainerReceipt.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteContainerReceipt(String preInboundNo, String refDocNumber, String containerReceiptNo,
                                          String warehouseId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------InboundHeader------------------------------------------------------------------------
    // GET ALL
    public InboundHeader[] getInboundHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<InboundHeader> obList = new ArrayList<>();
            for (InboundHeader inboundHeader : result.getBody()) {

                obList.add(addingTimeWithDateInboundHeader(inboundHeader));

            }
            return obList.toArray(new InboundHeader[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InboundHeader addingTimeWithDateInboundHeader(InboundHeader inboundHeader) throws ParseException {

        if (inboundHeader.getCreatedOn() != null) {
            inboundHeader.setCreatedOn(DateUtils.addTimeToDate(inboundHeader.getCreatedOn(), 3));
        }

        if (inboundHeader.getConfirmedOn() != null) {
            inboundHeader.setConfirmedOn(DateUtils.addTimeToDate(inboundHeader.getConfirmedOn(), 3));
        }

        return inboundHeader;
    }

    // GET
    public InboundHeader getInboundHeader(String warehouseId, String refDocNumber, String preInboundNo,
                                          String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundHeader.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateInboundHeader(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - Finder
    public InboundHeader[] findInboundHeader(SearchInboundHeader searchInboundHeader, String authToken)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/findInboundHeader");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
        ResponseEntity<InboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InboundHeader[].class);

        List<InboundHeader> inboundHeaderList = new ArrayList<>();
        for (InboundHeader inboundHeader : result.getBody()) {

            inboundHeaderList.add(addingTimeWithDateInboundHeader(inboundHeader));
        }
        return inboundHeaderList.toArray(new InboundHeader[inboundHeaderList.size()]);
    }

    // Find - findInboundHeader - Stream
    public InboundHeader[] findInboundHeaderNew(SearchInboundHeader searchInboundHeader, String authToken)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/findInboundHeaderNew");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
        ResponseEntity<InboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InboundHeader[].class);

        List<InboundHeader> inboundHeaderList = new ArrayList<>();
        for (InboundHeader inboundHeader : result.getBody()) {

            inboundHeaderList.add(addingTimeWithDateInboundHeader(inboundHeader));
        }
        return inboundHeaderList.toArray(new InboundHeader[inboundHeaderList.size()]);
    }

    // GET
    public InboundHeaderEntity[] getInboundHeaderWithStatusId(String warehouseId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/inboundconfirm")
                    .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundHeaderEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InboundHeaderEntity[].class);
            log.info("result : " + result.getStatusCode());
            List<InboundHeaderEntity> inboundHeaderList = new ArrayList<>();
            for (InboundHeaderEntity inboundHeader : result.getBody()) {

                if (inboundHeader.getCreatedOn() != null) {
                    inboundHeader.setCreatedOn(DateUtils.addTimeToDate(inboundHeader.getCreatedOn(), 3));
                }

                if (inboundHeader.getUpdatedOn() != null) {
                    inboundHeader.setUpdatedOn(DateUtils.addTimeToDate(inboundHeader.getUpdatedOn(), 3));
                }
                inboundHeaderList.add(inboundHeader);
            }
            return inboundHeaderList.toArray(new InboundHeaderEntity[inboundHeaderList.size()]);

        } catch (Exception e) {
            throw e;
        }
    }

    // POST
    public InboundHeader createInboundHeader(InboundHeader newInboundHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInboundHeader, headers);
        ResponseEntity<InboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InboundHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - replaceASN
    public Boolean replaceASN(String refDocNumber, String preInboundNo, String asnNumber, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/replaceASN")
                .queryParam("refDocNumber", refDocNumber).queryParam("preInboundNo", preInboundNo)
                .queryParam("asnNumber", asnNumber);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                Boolean.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();

    }

    // PATCH
    public InboundHeader updateInboundHeader(String warehouseId, String refDocNumber, String preInboundNo,
                                             String loginUserID, InboundHeader modifiedInboundHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedInboundHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("loginUserID", loginUserID);
            ResponseEntity<InboundHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, InboundHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH
    public AXApiResponse updateInboundHeaderConfirm(String warehouseId, String preInboundNo, String refDocNumber,
                                                    String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/confirmIndividual")
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("loginUserID", loginUserID);
            ResponseEntity<AXApiResponse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                    AXApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundHeader(String warehouseId, String refDocNumber, String preInboundNo, String loginUserID,
                                       String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------InboundLine------------------------------------------------------------------------
    // GET ALL
    public InboundLine[] getInboundLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundLine[].class);
            log.info("result : " + result.getStatusCode());

            List<InboundLine> obList = new ArrayList<>();
            for (InboundLine inboundLine : result.getBody()) {

                obList.add(addingTimeWithDateInboundLine(inboundLine));

            }
            return obList.toArray(new InboundLine[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InboundLine addingTimeWithDateInboundLine(InboundLine inboundLine) throws ParseException {

        if (inboundLine.getExpectedArrivalDate() != null) {
            inboundLine.setExpectedArrivalDate(DateUtils.addTimeToDate(inboundLine.getExpectedArrivalDate(), 3));
        }

        if (inboundLine.getCreatedOn() != null) {
            inboundLine.setCreatedOn(DateUtils.addTimeToDate(inboundLine.getCreatedOn(), 3));
        }

        if (inboundLine.getConfirmedOn() != null) {
            inboundLine.setConfirmedOn(DateUtils.addTimeToDate(inboundLine.getConfirmedOn(), 3));
        }

        return inboundLine;
    }

    // GET
    public InboundLine getInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                      String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundLine.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateInboundLine(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - Finder
    public InboundLine[] findInboundLine(SearchInboundLine searchInboundLine, String authToken)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/findInboundLine");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundLine, headers);
        ResponseEntity<InboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InboundLine[].class);

        List<InboundLine> obList = new ArrayList<>();
        for (InboundLine inboundLine : result.getBody()) {

            obList.add(addingTimeWithDateInboundLine(inboundLine));

        }
        return obList.toArray(new InboundLine[obList.size()]);
    }

    // POST
    public InboundLine createInboundLine(InboundLine newInboundLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInboundLine, headers);
        ResponseEntity<InboundLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                InboundLine.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InboundLine updateInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                         String itemCode, String loginUserID, InboundLine modifiedInboundLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInboundLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
            ResponseEntity<InboundLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    InboundLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundLine(String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                     String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------StagingHeader------------------------------------------------------------------------
    // GET ALL
    public StagingHeader[] getStagingHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, StagingHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<StagingHeader> obList = new ArrayList<>();
            for (StagingHeader stagingHeader : result.getBody()) {

                obList.add(addingTimeWithDateStagingHeader(stagingHeader));

            }
            return obList.toArray(new StagingHeader[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public StagingHeader addingTimeWithDateStagingHeader(StagingHeader stagingHeader) throws ParseException {

        if (stagingHeader.getConfirmedOn() != null) {
            stagingHeader.setConfirmedOn(DateUtils.addTimeToDate(stagingHeader.getConfirmedOn(), 3));
        }

        if (stagingHeader.getCreatedOn() != null) {
            stagingHeader.setCreatedOn(DateUtils.addTimeToDate(stagingHeader.getCreatedOn(), 3));
        }

        if (stagingHeader.getUpdatedOn() != null) {
            stagingHeader.setUpdatedOn(DateUtils.addTimeToDate(stagingHeader.getUpdatedOn(), 3));
        }

        return stagingHeader;
    }

    // GET
    public StagingHeader getStagingHeader(String warehouseId, String preInboundNo, String refDocNumber,
                                          String stagingNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("stagingNo", stagingNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, StagingHeader.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateStagingHeader(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Find StagingHeader
    public StagingHeader[] findStagingHeader(SearchStagingHeader searchStagingHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/findStagingHeader");
        HttpEntity<?> entity = new HttpEntity<>(searchStagingHeader, headers);
        ResponseEntity<StagingHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, StagingHeader[].class);

        List<StagingHeader> stagingHeaderList = new ArrayList<>();
        for (StagingHeader stagingHeader : result.getBody()) {

            stagingHeaderList.add(addingTimeWithDateStagingHeader(stagingHeader));
        }
        return stagingHeaderList.toArray(new StagingHeader[stagingHeaderList.size()]);
    }

    // POST - Find StagingHeader - Stream
    public StagingHeader[] findStagingHeaderNew(SearchStagingHeader searchStagingHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/findStagingHeaderNew");
        HttpEntity<?> entity = new HttpEntity<>(searchStagingHeader, headers);
        ResponseEntity<StagingHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, StagingHeader[].class);

        List<StagingHeader> stagingHeaderList = new ArrayList<>();
        for (StagingHeader stagingHeader : result.getBody()) {

            stagingHeaderList.add(addingTimeWithDateStagingHeader(stagingHeader));
        }
        return stagingHeaderList.toArray(new StagingHeader[stagingHeaderList.size()]);
    }

    // POST
    public StagingHeader createStagingHeader(StagingHeader newStagingHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newStagingHeader, headers);
        ResponseEntity<StagingHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, StagingHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public StagingHeader updateStagingHeader(String warehouseId, String preInboundNo, String refDocNumber,
                                             String stagingNo, String loginUserID, StagingHeader modifiedStagingHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStagingHeader, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("stagingNo", stagingNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<StagingHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, StagingHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteStagingHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                    .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo).queryParam("stagingNo", stagingNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - /{numberOfCases}/barcode
    public String[] generateNumberRanges(Long numberOfCases, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + numberOfCases + "/barcode")
                    .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    String[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------StagingLine------------------------------------------------------------------------
    // GET ALL
    public StagingLineEntity[] getStagingLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingLineEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, StagingLineEntity[].class);
            log.info("result : " + result.getStatusCode());

            List<StagingLineEntity> stagingLineList = new ArrayList<>();
            for (StagingLineEntity stagingLine : result.getBody()) {

                stagingLineList.add(addingTimeWithDateStagingLineEntity(stagingLine));
            }
            return stagingLineList.toArray(new StagingLineEntity[stagingLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public StagingLineEntity addingTimeWithDateStagingLineEntity(StagingLineEntity stagingLine) throws ParseException {

        if (stagingLine.getCreatedOn() != null) {
            stagingLine.setCreatedOn(DateUtils.addTimeToDate(stagingLine.getCreatedOn(), 3));
        }

        if (stagingLine.getUpdatedOn() != null) {
            stagingLine.setUpdatedOn(DateUtils.addTimeToDate(stagingLine.getUpdatedOn(), 3));
        }

        if (stagingLine.getConfirmedOn() != null) {
            stagingLine.setConfirmedOn(DateUtils.addTimeToDate(stagingLine.getConfirmedOn(), 3));
        }

        return stagingLine;
    }

    // GET
    public StagingLineEntity getStagingLine(String warehouseId, String preInboundNo, String refDocNumber,
                                            String stagingNo, String palletCode, String caseCode, Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingLineEntity> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, StagingLineEntity.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateStagingLineEntity(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findStagingLine
    public StagingLineEntity[] findStagingLine(SearchStagingLine searchStagingLine, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/findStagingLine");
            HttpEntity<?> entity = new HttpEntity<>(searchStagingLine, headers);
            ResponseEntity<StagingLineEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, StagingLineEntity[].class);

            List<StagingLineEntity> stagingLineList = new ArrayList<>();
            for (StagingLineEntity stagingLine : result.getBody()) {

                stagingLineList.add(addingTimeWithDateStagingLineEntity(stagingLine));
            }
            return stagingLineList.toArray(new StagingLineEntity[stagingLineList.size()]);

        } catch (Exception e) {
            throw e;
        }
    }

    // POST
    public StagingLineEntity[] createStagingLine(List<StagingLine> newStagingLine, String loginUserID,
                                                 String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newStagingLine, headers);
        ResponseEntity<StagingLineEntity[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, StagingLineEntity[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public StagingLineEntity updateStagingLine(String warehouseId, String preInboundNo, String refDocNumber,
                                               String stagingNo, String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID,
                                               StagingLineEntity modifiedStagingLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStagingLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntity> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, StagingLineEntity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH
    public StagingLineEntity[] caseConfirmation(List<CaseConfirmation> caseConfirmations, String caseCode,
                                                String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(caseConfirmations, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/caseConfirmation")
                    .queryParam("caseCode", caseCode).queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntity[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, StagingLineEntity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // DELETE
    public boolean deleteStagingLine(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                     String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteCases(String preInboundNo, Long lineNo, String itemCode, String caseCode, String loginUserID,
                               String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/" + lineNo + "/cases")
                    .queryParam("preInboundNo", preInboundNo).queryParam("caseCode", caseCode)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // AssignHHTUser
    public StagingLineEntity[] assignHHTUser(List<AssignHHTUser> assignHHTUsers, String assignedUserId,
                                             String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(assignHHTUsers, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/assignHHTUser")
                    .queryParam("assignedUserId", assignedUserId).queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntity[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, StagingLineEntity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------GrHeader------------------------------------------------------------------------
    // GET ALL
    public GrHeader[] getGrHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, GrHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<GrHeader> grHeaderList = new ArrayList<>();
            for (GrHeader grHeader : result.getBody()) {

                grHeaderList.add(addingTimeWithDateGrHeader(grHeader));
            }
            return grHeaderList.toArray(new GrHeader[grHeaderList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public GrHeader addingTimeWithDateGrHeader(GrHeader grHeader) throws ParseException {

        if (grHeader.getCreatedOn() != null) {
            grHeader.setCreatedOn(DateUtils.addTimeToDate(grHeader.getCreatedOn(), 3));
        }

        if (grHeader.getUpdatedOn() != null) {
            grHeader.setUpdatedOn(DateUtils.addTimeToDate(grHeader.getUpdatedOn(), 3));
        }

        if (grHeader.getConfirmedOn() != null) {
            grHeader.setConfirmedOn(DateUtils.addTimeToDate(grHeader.getConfirmedOn(), 3));
        }

        if (grHeader.getExpectedArrivalDate() != null) {
            grHeader.setExpectedArrivalDate(DateUtils.addTimeToDate(grHeader.getExpectedArrivalDate(), 3));
        }

        if (grHeader.getGoodsReceiptDate() != null) {
            grHeader.setGoodsReceiptDate(DateUtils.addTimeToDate(grHeader.getGoodsReceiptDate(), 3));
        }

        return grHeader;
    }

    // GET
    public GrHeader getGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                String goodsReceiptNo, String palletCode, String caseCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    GrHeader.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateGrHeader(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Finder GrHeader
    public GrHeader[] findGrHeader(SearchGrHeader searchGrHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/findGrHeader");
        HttpEntity<?> entity = new HttpEntity<>(searchGrHeader, headers);
        ResponseEntity<GrHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                GrHeader[].class);

        List<GrHeader> grHeaderList = new ArrayList<>();
        for (GrHeader grHeader : result.getBody()) {

            grHeaderList.add(addingTimeWithDateGrHeader(grHeader));
        }
        return grHeaderList.toArray(new GrHeader[grHeaderList.size()]);
    }

    // POST - Finder GrHeader -Stream JPA
    public GrHeader[] findGrHeaderNew(SearchGrHeader searchGrHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/findGrHeaderNew");
        HttpEntity<?> entity = new HttpEntity<>(searchGrHeader, headers);
        ResponseEntity<GrHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                GrHeader[].class);

        List<GrHeader> grHeaderList = new ArrayList<>();
        for (GrHeader grHeader : result.getBody()) {

            grHeaderList.add(addingTimeWithDateGrHeader(grHeader));
        }
        return grHeaderList.toArray(new GrHeader[grHeaderList.size()]);
    }


    // POST
    public GrHeader createGrHeader(GrHeader newGrHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newGrHeader, headers);
        ResponseEntity<GrHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                GrHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public GrHeader updateGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                   String goodsReceiptNo, String palletCode, String caseCode, String loginUserID, GrHeader modifiedGrHeader,
                                   String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedGrHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<GrHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    GrHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteGrHeader(String warehouseId, String preInboundNo, String refDocNumber, String stagingNo,
                                  String goodsReceiptNo, String palletCode, String caseCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("stagingNo", stagingNo)
                    .queryParam("goodsReceiptNo", goodsReceiptNo).queryParam("palletCode", palletCode)
                    .queryParam("caseCode", caseCode).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------GrLine------------------------------------------------------------------------
    // GET ALL
    public GrLine[] getGrLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    GrLine[].class);
            log.info("result : " + result.getStatusCode());

            List<GrLine> grLineList = new ArrayList<>();
            for (GrLine grLine : result.getBody()) {

                grLineList.add(addingTimeWithDateGrLine(grLine));
            }
            return grLineList.toArray(new GrLine[grLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public GrLine addingTimeWithDateGrLine(GrLine grLine) throws ParseException {

        if (grLine.getCreatedOn() != null) {
            grLine.setCreatedOn(DateUtils.addTimeToDate(grLine.getCreatedOn(), 3));
        }

        if (grLine.getUpdatedOn() != null) {
            grLine.setUpdatedOn(DateUtils.addTimeToDate(grLine.getUpdatedOn(), 3));
        }

        if (grLine.getConfirmedOn() != null) {
            grLine.setConfirmedOn(DateUtils.addTimeToDate(grLine.getConfirmedOn(), 3));
        }

        if (grLine.getExpiryDate() != null) {
            grLine.setExpiryDate(DateUtils.addTimeToDate(grLine.getExpiryDate(), 3));
        }

        if (grLine.getManufacturerDate() != null) {
            grLine.setManufacturerDate(DateUtils.addTimeToDate(grLine.getManufacturerDate(), 3));
        }

        return grLine;
    }

    // GET
    public GrLine getGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                            String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    GrLine.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateGrLine(result.getBody());

        } catch (Exception e) {
            throw e;
        }
    }

    // GET
    public GrLine[] getGrLine(String preInboundNo, String refDocNumber, String packBarcodes, Long lineNo,
                              String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/" + lineNo + "/putawayline")
                    .queryParam("preInboundNo", preInboundNo).queryParam("refDocNumber", refDocNumber)
                    .queryParam("packBarcodes", packBarcodes).queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    GrLine[].class);
            log.info("result : " + result.getStatusCode());

            List<GrLine> grLineList = new ArrayList<>();
            for (GrLine grLine : result.getBody()) {

                grLineList.add(addingTimeWithDateGrLine(grLine));
            }
            return grLineList.toArray(new GrLine[grLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Finder GrLine
    public GrLine[] findGrLine(SearchGrLine searchGrLine, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/findGrLine");
        HttpEntity<?> entity = new HttpEntity<>(searchGrLine, headers);
        ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                GrLine[].class);

        List<GrLine> grLineList = new ArrayList<>();
        for (GrLine grLine : result.getBody()) {

            grLineList.add(addingTimeWithDateGrLine(grLine));
        }
        return grLineList.toArray(new GrLine[grLineList.size()]);
    }

    // POST
    public GrLine[] createGrLine(List<AddGrLine> newGrLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newGrLine, headers);
        ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                GrLine[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public GrLine updateGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                               String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String loginUserID,
                               GrLine modifiedGrLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedGrLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<GrLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    GrLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String loginUserID,
                                String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/" + lineNo)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);

            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET PackBarcode
    public PackBarcode[] generatePackBarcode(Long acceptQty, Long damageQty, String warehouseId, String loginUserID,
                                             String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/packBarcode")
                    .queryParam("acceptQty", acceptQty).queryParam("damageQty", damageQty)
                    .queryParam("warehouseId", warehouseId).queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PackBarcode[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PackBarcode[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // --------------------------------------------PutAwayHeader------------------------------------------------------------------------
    // GET ALL
    public PutAwayHeader[] getPutAwayHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayHeader> putAwayHeaderList = new ArrayList<>();
            for (PutAwayHeader putAwayHeader : result.getBody()) {

                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
            }
            return putAwayHeaderList.toArray(new PutAwayHeader[putAwayHeaderList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PutAwayHeader addingTimeWithDatePutAwayHeader(PutAwayHeader putAwayHeader) throws ParseException {

        if (putAwayHeader.getCreatedOn() != null) {
            putAwayHeader.setCreatedOn(DateUtils.addTimeToDate(putAwayHeader.getCreatedOn(), 3));
        }

        if (putAwayHeader.getUpdatedOn() != null) {
            putAwayHeader.setUpdatedOn(DateUtils.addTimeToDate(putAwayHeader.getUpdatedOn(), 3));
        }

        if (putAwayHeader.getConfirmedOn() != null) {
            putAwayHeader.setConfirmedOn(DateUtils.addTimeToDate(putAwayHeader.getConfirmedOn(), 3));
        }

        return putAwayHeader;
    }

    // GET
    public PutAwayHeader getPutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber,
                                          String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, String putAwayNumber,
                                          String proposedStorageBin, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("proposedStorageBin", proposedStorageBin);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayHeader.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDatePutAwayHeader(result.getBody());

        } catch (Exception e) {
            throw e;
        }
    }

    //Find PutAwayHeader
    public PutAwayHeader[] findPutAwayHeader(SearchPutAwayHeader searchPutAwayHeader, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/findPutAwayHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayHeader, headers);
            ResponseEntity<PutAwayHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PutAwayHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayHeader> putAwayHeaderList = new ArrayList<>();
            for (PutAwayHeader putAwayHeader : result.getBody()) {

                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
            }
            return putAwayHeaderList.toArray(new PutAwayHeader[putAwayHeaderList.size()]);

        } catch (Exception e) {
            throw e;
        }
    }

    //Find PutAwayHeader - Stream
    public PutAwayHeader[] findPutAwayHeaderNew(SearchPutAwayHeader searchPutAwayHeader, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/findPutAwayHeaderNew");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayHeader, headers);
            ResponseEntity<PutAwayHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PutAwayHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayHeader> putAwayHeaderList = new ArrayList<>();
            for (PutAwayHeader putAwayHeader : result.getBody()) {

                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
            }
            return putAwayHeaderList.toArray(new PutAwayHeader[putAwayHeaderList.size()]);

        } catch (Exception e) {
            throw e;
        }
    }

    // GET - /{refDocNumber}/inboundreversal/asn
    public PutAwayHeader[] getPutAwayHeader(String refDocNumber, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                    getInboundTransactionServiceApiUrl() + "putawayheader/" + refDocNumber + "/inboundreversal/asn");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayHeader[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayHeader> putAwayHeaderList = new ArrayList<>();
            for (PutAwayHeader putAwayHeader : result.getBody()) {

                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
            }
            return putAwayHeaderList.toArray(new PutAwayHeader[putAwayHeaderList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PutAwayHeader createPutAwayHeader(PutAwayHeader newPutAwayHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPutAwayHeader, headers);
        ResponseEntity<PutAwayHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, PutAwayHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PutAwayHeader updatePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber,
                                             String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, String putAwayNumber,
                                             String proposedStorageBin, PutAwayHeader modifiedPutAwayHeader, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("proposedStorageBin", proposedStorageBin).queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, PutAwayHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH - /{refDocNumber}/reverse
    public PutAwayHeader[] updatePutAwayHeader(String refDocNumber, String packBarcodes, String loginUserID,
                                               String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + refDocNumber + "/reverse")
                    .queryParam("packBarcodes", packBarcodes).queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeader[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, PutAwayHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber,
                                       String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, String putAwayNumber,
                                       String proposedStorageBin, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode).queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes).queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("proposedStorageBin", proposedStorageBin).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------PutAwayLine------------------------------------------------------------------------
    // GET ALL
    public PutAwayLine[] getPutAwayLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayLine[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayLine> putAwayLineList = new ArrayList<>();
            for (PutAwayLine putAwayLine : result.getBody()) {

                putAwayLineList.add(addingTimeWithDatePutAwayLine(putAwayLine));
            }
            return putAwayLineList.toArray(new PutAwayLine[putAwayLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PutAwayLine addingTimeWithDatePutAwayLine(PutAwayLine putAwayLine) throws ParseException {

        if (putAwayLine.getCreatedOn() != null) {
            putAwayLine.setCreatedOn(DateUtils.addTimeToDate(putAwayLine.getCreatedOn(), 3));
        }
        if (putAwayLine.getConfirmedOn() != null) {
            putAwayLine.setConfirmedOn(DateUtils.addTimeToDate(putAwayLine.getConfirmedOn(), 3));
        }
        if (putAwayLine.getUpdatedOn() != null) {
            putAwayLine.setUpdatedOn(DateUtils.addTimeToDate(putAwayLine.getUpdatedOn(), 3));
        }

        return putAwayLine;
    }

    // GET
    public PutAwayLine getPutAwayLine(String warehouseId, String goodsReceiptNo, String preInboundNo,
                                      String refDocNumber, String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin,
                                      String confirmedStorageBin, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("putAwayNumber", putAwayNumber).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode).queryParam("confirmedStorageBin", confirmedStorageBin)
                    .queryParam("proposedStorageBin", proposedStorageBin);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayLine.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDatePutAwayLine(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - /{refDocNumber}/inboundreversal/palletId
    public PutAwayLine[] getPutAwayLine(String refDocNumber, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                    getInboundTransactionServiceApiUrl() + "putawayline/" + refDocNumber + "/inboundreversal/palletId");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, PutAwayLine[].class);
            log.info("result : " + result.getStatusCode());

            List<PutAwayLine> putAwayLineList = new ArrayList<>();
            for (PutAwayLine putAwayLine : result.getBody()) {

                putAwayLineList.add(addingTimeWithDatePutAwayLine(putAwayLine));
            }
            return putAwayLineList.toArray(new PutAwayLine[putAwayLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ALL
    public PutAwayLine[] findPutAwayLine(SearchPutAwayLine searchPutAwayLine, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/findPutAwayLine");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayLine, headers);
            ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PutAwayLine[].class);

            List<PutAwayLine> putAwayLineList = new ArrayList<>();
            for (PutAwayLine putAwayLine : result.getBody()) {

                putAwayLineList.add(addingTimeWithDatePutAwayLine(putAwayLine));
            }
            return putAwayLineList.toArray(new PutAwayLine[putAwayLineList.size()]);

        } catch (Exception e) {
            throw e;
        }
    }

    // POST
    public PutAwayLine[] createPutAwayLine(List<AddPutAwayLine> newPutAwayLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/confirm")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPutAwayLine, headers);
        ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, PutAwayLine[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PutAwayLine updatePutAwayLine(String warehouseId, String goodsReceiptNo, String preInboundNo,
                                         String refDocNumber, String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin,
                                         String confirmedStorageBin, PutAwayLine modifiedPutAwayLine, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
                    .queryParam("warehouseId", warehouseId).queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber).queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("putAwayNumber", putAwayNumber).queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode).queryParam("proposedStorageBin", proposedStorageBin)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    PutAwayLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePutAwayLine(String languageId, String companyCodeId, String plantId, String warehouseId,
                                     String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo,
                                     String itemCode, String proposedStorageBin, String confirmedStorageBin, String loginUserID,
                                     String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
                    .queryParam("languageId", languageId).queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId).queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo).queryParam("refDocNumber", refDocNumber)
                    .queryParam("goodsReceiptNo", goodsReceiptNo).queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("lineNo", lineNo).queryParam("itemCode", itemCode)
                    .queryParam("confirmedStorageBin", confirmedStorageBin)
                    .queryParam("proposedStorageBin", proposedStorageBin).queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------InventoryMovement------------------------------------------------------------------------
    // GET ALL
    public InventoryMovement[] getInventoryMovements(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InventoryMovement[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InventoryMovement[].class);
            log.info("result : " + result.getStatusCode());

            List<InventoryMovement> inventoryMovementList = new ArrayList<>();
            for (InventoryMovement inventoryMovement : result.getBody()) {

                inventoryMovementList.add(addingTimeWithDateInventoryMovement(inventoryMovement));
            }
            return inventoryMovementList.toArray(new InventoryMovement[inventoryMovementList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InventoryMovement addingTimeWithDateInventoryMovement(InventoryMovement inventoryMovement) throws ParseException {

        if (inventoryMovement.getCreatedOn() != null) {
            inventoryMovement.setCreatedOn(DateUtils.addTimeToDate(inventoryMovement.getCreatedOn(), 3));
        }

        return inventoryMovement;
    }

    // GET
    public InventoryMovement getInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                                  String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("movementType", movementType)
                    .queryParam("submovementType", submovementType)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode)
                    .queryParam("batchSerialNumber", batchSerialNumber)
                    .queryParam("movementDocumentNo", movementDocumentNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InventoryMovement.class);
            return addingTimeWithDateInventoryMovement(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findInventoryMovement
    public InventoryMovement[] findInventoryMovement(SearchInventoryMovement searchInventoryMovement, String authToken)
            throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement/findInventoryMovement");
            HttpEntity<?> entity = new HttpEntity<>(searchInventoryMovement, headers);
            ResponseEntity<InventoryMovement[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InventoryMovement[].class);

            List<InventoryMovement> inventoryMovementList = new ArrayList<>();
            for (InventoryMovement inventoryMovement : result.getBody()) {

                inventoryMovementList.add(addingTimeWithDateInventoryMovement(inventoryMovement));
            }
            return inventoryMovementList.toArray(new InventoryMovement[inventoryMovementList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public InventoryMovement createInventoryMovement(InventoryMovement newInventoryMovement, String loginUserID,
                                                     String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInventoryMovement, headers);
        ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InventoryMovement.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InventoryMovement updateInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                                     String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo,
                                                     InventoryMovement modifiedInventoryMovement, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInventoryMovement, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("movementType", movementType)
                    .queryParam("submovementType", submovementType)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode)
                    .queryParam("batchSerialNumber", batchSerialNumber)
                    .queryParam("movementDocumentNo", movementDocumentNo).queryParam("loginUserID", loginUserID);

            ResponseEntity<InventoryMovement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, InventoryMovement.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                           String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo, String loginUserID,
                                           String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("movementType", movementType)
                    .queryParam("submovementType", submovementType)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode)
                    .queryParam("batchSerialNumber", batchSerialNumber)
                    .queryParam("movementDocumentNo", movementDocumentNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------Inventory------------------------------------------------------------------------
    // GET ALL
    public Inventory[] getInventorys(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, Inventory[].class);
            log.info("result : " + result.getStatusCode());

            List<Inventory> inventoryList = new ArrayList<>();
            for (Inventory inventory : result.getBody()) {

                inventoryList.add(addingTimeWithDateInventory(inventory));
            }
            return inventoryList.toArray(new Inventory[inventoryList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public Inventory addingTimeWithDateInventory(Inventory inventory) throws ParseException {

        if (inventory.getCreatedOn() != null) {
            inventory.setCreatedOn(DateUtils.addTimeToDate(inventory.getCreatedOn(), 3));
        }

        if (inventory.getManufacturerDate() != null) {
            inventory.setManufacturerDate(DateUtils.addTimeToDate(inventory.getManufacturerDate(), 3));
        }

        if (inventory.getExpiryDate() != null) {
            inventory.setExpiryDate(DateUtils.addTimeToDate(inventory.getExpiryDate(), 3));
        }

        return inventory;
    }

    // GET
    public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                  Long stockTypeId, Long specialStockIndicatorId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                    .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                    .queryParam("stockTypeId", stockTypeId)
                    .queryParam("specialStockIndicatorId", specialStockIndicatorId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    Inventory.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateInventory(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                  String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/transfer")
                    .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    Inventory.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateInventory(result.getBody());

        } catch (Exception e) {
            throw e;
        }
    }

    // POST - FinderQuery
    public Inventory[] findInventory(SearchInventory searchInventory, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/findInventory");
            HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
            ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, Inventory[].class);
            log.info("result : " + result.getStatusCode());

            List<Inventory> inventoryList = new ArrayList<>();
            for (Inventory inventory : result.getBody()) {

                inventoryList.add(addingTimeWithDateInventory(inventory));
            }
            return inventoryList.toArray(new Inventory[inventoryList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - FinderQueryNew - SQL Query
    public Inventory[] findInventoryNew(SearchInventory searchInventory, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/findInventoryNew");
            HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
            ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, Inventory[].class);
            return result.getBody();
//			log.info("result : " + result.getStatusCode());

//			List<Inventory> inventoryList = new ArrayList<>();
//			for (Inventory inventory : result.getBody()) {
//
//				inventoryList.add(addingTimeWithDateInventory(inventory));
//			}
//			return inventoryList.toArray(new Inventory[inventoryList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - FinderQuery
    public Inventory[] getQuantityValidatedInventory(SearchInventory searchInventory, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/get-all-validated-inventory");
            HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
            ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, Inventory[].class);
            log.info("result : " + result.getStatusCode());

            List<Inventory> inventoryList = new ArrayList<>();
            for (Inventory inventory : result.getBody()) {

                inventoryList.add(addingTimeWithDateInventory(inventory));
            }
            return inventoryList.toArray(new Inventory[inventoryList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PaginatedResponse<Inventory> findInventory(SearchInventory searchInventory, Integer pageNo, Integer pageSize,
                                                      String sortBy, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/findInventory/pagination")
                    .queryParam("pageNo", pageNo).queryParam("pageSize", pageSize).queryParam("sortBy", sortBy);

            HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);

            ParameterizedTypeReference<PaginatedResponse<Inventory>> responseType = new ParameterizedTypeReference<PaginatedResponse<Inventory>>() {
            };
            ResponseEntity<PaginatedResponse<Inventory>> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, responseType);

            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public Inventory createInventory(Inventory newInventory, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInventory, headers);
        ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                Inventory.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public Inventory updateInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                     Long stockTypeId, Long specialStockIndicatorId, Inventory modifiedInventory, String loginUserID,
                                     String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInventory, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                    .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                    .queryParam("stockTypeId", stockTypeId)
                    .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<Inventory> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    Inventory.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                   Long stockTypeId, Long specialStockIndicatorId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                    .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                    .queryParam("stockTypeId", stockTypeId)
                    .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
    // GET ALL
    public InhouseTransferHeader[] getInhouseTransferHeaders(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InhouseTransferHeader[].class);

            //Start of Comment by V.Senthil on 10-03-2024
//            List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
//            for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {
//
//                inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
//            }
//            return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
            return result.getBody();
            //End of Comment by V.Senthil on 10-03-2024


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InhouseTransferHeader addingTimeWithDateInhouseTransferHeader(InhouseTransferHeader inhouseTransferHeader) throws ParseException {

        if (inhouseTransferHeader.getCreatedOn() != null) {
            inhouseTransferHeader.setCreatedOn(DateUtils.addTimeToDate(inhouseTransferHeader.getCreatedOn(), 3));
        }
        if (inhouseTransferHeader.getUpdatedOn() != null) {
            inhouseTransferHeader.setUpdatedOn(DateUtils.addTimeToDate(inhouseTransferHeader.getUpdatedOn(), 3));
        }

        return inhouseTransferHeader;
    }

    // GET
    public InhouseTransferHeader getInhouseTransferHeader(String warehouseId, String transferNumber,
                                                          Long transferTypeId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader/" + transferNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("transferNumber", transferNumber)
                    .queryParam("transferTypeId", transferTypeId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InhouseTransferHeader.class);

            return addingTimeWithDateInhouseTransferHeader(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Find
    public InhouseTransferHeader[] findInHouseTransferHeader(SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                             String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader/findInHouseTransferHeader");
        HttpEntity<?> entity = new HttpEntity<>(searchInHouseTransferHeader, headers);
        ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, InhouseTransferHeader[].class);

        List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
        for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {

            inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
        }
        return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
    }

    // POST - Find - Stream
    public InhouseTransferHeader[] findInHouseTransferHeaderNew(SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                                String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader/findInHouseTransferHeaderNew");
        HttpEntity<?> entity = new HttpEntity<>(searchInHouseTransferHeader, headers);
        ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, InhouseTransferHeader[].class);

        List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
        for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {

            inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
        }
        return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
    }

    // POST
    public InhouseTransferHeader createInhouseTransferHeader(InhouseTransferHeader newInhouseTransferHeader,
                                                             String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferHeader, headers);
        ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, InhouseTransferHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - V2
    public InhouseTransferHeader createInhouseTransferHeaderV2(InhouseTransferHeader newInhouseTransferHeader,
                                                               String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferHeader, headers);
        ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, InhouseTransferHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - V2
    public WarehouseApiResponse createInhouseTransferUploadV2(List<InhouseTransferUpload> inhouseTransferUploadList,
                                                              String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferheader/v2/upload")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(inhouseTransferUploadList, headers);
        ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // --------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
    // GET ALL
    public InhouseTransferLine[] getInhouseTransferLines(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InhouseTransferLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InhouseTransferLine[].class);
            log.info("result : " + result.getStatusCode());

            List<InhouseTransferLine> inhouseTransferLineList = new ArrayList<>();
            for (InhouseTransferLine inhouseTransferLine : result.getBody()) {

                inhouseTransferLineList.add(addingTimeWithDateInhouseTransferLine(inhouseTransferLine));
            }
            return inhouseTransferLineList.toArray(new InhouseTransferLine[inhouseTransferLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InhouseTransferLine addingTimeWithDateInhouseTransferLine(InhouseTransferLine inhouseTransferLine) throws ParseException {

        if (inhouseTransferLine.getCreatedOn() != null) {
            inhouseTransferLine.setCreatedOn(DateUtils.addTimeToDate(inhouseTransferLine.getCreatedOn(), 3));
        }
        if (inhouseTransferLine.getUpdatedOn() != null) {
            inhouseTransferLine.setUpdatedOn(DateUtils.addTimeToDate(inhouseTransferLine.getUpdatedOn(), 3));
        }
        if (inhouseTransferLine.getConfirmedOn() != null) {
            inhouseTransferLine.setConfirmedOn(DateUtils.addTimeToDate(inhouseTransferLine.getConfirmedOn(), 3));
        }
        return inhouseTransferLine;
    }

    // GET
    public InhouseTransferLine getInhouseTransferLine(String warehouseId, String transferNumber, String sourceItemCode,
                                                      String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferline/" + transferNumber)
                    .queryParam("warehouseId", warehouseId).queryParam("transferNumber", transferNumber)
                    .queryParam("sourceItemCode", sourceItemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InhouseTransferLine> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InhouseTransferLine.class);
            log.info("result : " + result.getStatusCode());

            return addingTimeWithDateInhouseTransferLine(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Find
    public InhouseTransferLine[] findInhouseTransferLine(SearchInhouseTransferLine searchInhouseTransferLine,
                                                         String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferline/findInhouseTransferLine");
        HttpEntity<?> entity = new HttpEntity<>(searchInhouseTransferLine, headers);
        ResponseEntity<InhouseTransferLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, InhouseTransferLine[].class);

        List<InhouseTransferLine> inhouseTransferLineList = new ArrayList<>();
        for (InhouseTransferLine inhouseTransferLine : result.getBody()) {

            inhouseTransferLineList.add(addingTimeWithDateInhouseTransferLine(inhouseTransferLine));
        }
        return inhouseTransferLineList.toArray(new InhouseTransferLine[inhouseTransferLineList.size()]);
    }

    // POST
    public InhouseTransferLine createInhouseTransferLine(InhouseTransferLine newInhouseTransferLine, String loginUserID,
                                                         String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inhousetransferline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferLine, headers);
        ResponseEntity<InhouseTransferLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InhouseTransferLine.class);
        return result.getBody();
    }

    /**
     * @param authToken
     * @return
     */
    public InboundOrder[] getInboundOrders(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "orders/inbound");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundOrder[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundOrder[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-------------------------------------------findStreamInboundHeader-------------------------------------------------------

    /**
     * @return
     */
    public StreamingResponseBody findStreamInboundHeader() {
        Stream<InboundHeaderStream> inboundHeaderStream = streamInboundHeader();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                jsonGenerator.writeStartArray();
                jsonGenerator.setCodec(new ObjectMapper());
                inboundHeaderStream.forEach(im -> {
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
    //	private final Gson gson = new Gson();

    /**
     * Inbound Header
     * refDocNumber
     * statusId
     * inboundOrderTypeId
     * containerNo
     * createdOn
     * confirmedBy
     * confirmedOn
     *
     * @return
     */
    public Stream<InboundHeaderStream> streamInboundHeader() {
        jdbcTemplate.setFetchSize(50);
        /**
         * Inbound Header
         * String refDocNumber
         * Long statusId
         * Long inboundOrderTypeId
         * String containerNo
         * Date createdOn
         * String confirmedBy
         * Date confirmedOn
         */
        Stream<InboundHeaderStream> inboundHeaderStream = jdbcTemplate.queryForStream(
                "Select REF_DOC_NO, STATUS_ID, IB_ORD_TYP_ID, CONT_NO, CTD_ON, IB_CNF_BY, "
                        + "IB_CNF_ON "
                        + "from tblinboundheader "
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new InboundHeaderStream(
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getLong("IB_ORD_TYP_ID"),
                        resultSet.getString("CONT_NO"),
                        resultSet.getDate("CTD_ON"),
                        resultSet.getString("IB_CNF_BY"),
                        resultSet.getDate("IB_CNF_ON")
                ));
        return inboundHeaderStream;
    }
    //-------------------------------------------findStreamStagingHeader-------------------------------------------------------

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
    //-------------------------------------------findStreamOutboundHeader-------------------------------------------------------

    /**
     * @return
     */
    public StreamingResponseBody findStreamOutboundHeader() {
        Stream<OutboundHeaderStream> outboundHeaderStream = streamOutboundHeader();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                jsonGenerator.writeStartArray();
                jsonGenerator.setCodec(new ObjectMapper());
                outboundHeaderStream.forEach(im -> {
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

    public Stream<OutboundHeaderStream> streamOutboundHeader() {
        jdbcTemplate.setFetchSize(50);
        /**
         * Outbound Header
         * String refDocNumber
         * String partnerCode
         * String referenceDocumentType
         * Long statusId
         * Date refDocDate
         * Date requiredDeliveryDate
         * String referenceField1
         * String referenceField7
         * String referenceField8
         * String referenceField9
         * String referenceField10
         * Date deliveryConfirmedOn
         */
        Stream<OutboundHeaderStream> outboundHeaderStream = jdbcTemplate.queryForStream(
                "Select REF_DOC_NO, PARTNER_CODE, REF_DOC_TYP, STATUS_ID, REF_DOC_DATE, REQ_DEL_DATE, REF_FIELD_1, "
                        + "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, DLV_CNF_ON "
                        + "from tbloutboundheader "
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new OutboundHeaderStream(
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getString("PARTNER_CODE"),
                        resultSet.getString("REF_DOC_TYP"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getDate("REF_DOC_DATE"),
                        resultSet.getDate("REQ_DEL_DATE"),
                        resultSet.getString("REF_FIELD_1"),
                        resultSet.getString("REF_FIELD_7"),
                        resultSet.getString("REF_FIELD_8"),
                        resultSet.getString("REF_FIELD_9"),
                        resultSet.getString("REF_FIELD_10"),
                        resultSet.getDate("DLV_CNF_ON")
                ));
        return outboundHeaderStream;
    }

    //-------------------------------------------findStreamPutAwayHeader-------------------------------------------------------

    /**
     * @return
     */
    public StreamingResponseBody findStreamPutAwayHeader() {
        Stream<PutAwayHeaderStream> putAwayHeaderStream = streamPutAwayHeader();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                jsonGenerator.writeStartArray();
                jsonGenerator.setCodec(new ObjectMapper());
                putAwayHeaderStream.forEach(im -> {
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
    //	private final Gson gson = new Gson();

    /**
     * PutAway Header
     * refDocNumber
     * packBarcodes
     * putAwayNumber
     * proposedStorageBin
     * putAwayQuantity
     * proposedHandlingEquipment
     * statusId
     * referenceField5
     * createdBy
     * createdOn
     *
     * @return
     */
    public Stream<PutAwayHeaderStream> streamPutAwayHeader() {
        jdbcTemplate.setFetchSize(50);
        /**
         * PutAway Header
         * String refDocNumber
         * String packBarcodes
         * String putAwayNumber
         * String proposedStorageBin
         * Double putAwayQuantity
         * String proposedHandlingEquipment
         * Long statusId
         * String referenceField5
         * String createdBy
         * Date createdOn
         */
        Stream<PutAwayHeaderStream> putAwayHeaderStream = jdbcTemplate.queryForStream(
                "Select REF_DOC_NO, PACK_BARCODE, PA_NO, PROP_ST_BIN, PA_QTY, PROP_HE_NO, "
                        + "STATUS_ID, REF_FIELD_5, PA_CTD_BY, PA_CTD_ON "
                        + "from tblputawayheader "
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new PutAwayHeaderStream(
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getString("PACK_BARCODE"),
                        resultSet.getString("PA_NO"),
                        resultSet.getString("PROP_ST_BIN"),
                        resultSet.getDouble("PA_QTY"),
                        resultSet.getString("PROP_HE_NO"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getString("REF_FIELD_5"),
                        resultSet.getString("PA_CTD_BY"),
                        resultSet.getDate("PA_CTD_ON")
                ));
        return putAwayHeaderStream;
    }
    //-------------------------------------------findStreamPreInboundHeader-------------------------------------------------------

    /**
     * @return
     */
    public StreamingResponseBody findStreamPreInboundHeader() {
        Stream<PreInboundHeaderStream> preInboundHeaderStream = streamPreInboundHeader();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                jsonGenerator.writeStartArray();
                jsonGenerator.setCodec(new ObjectMapper());
                preInboundHeaderStream.forEach(im -> {
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
    //	private final Gson gson = new Gson();

    /**
     * PreInbound Header
     * preInboundNo
     * refDocNumber
     * inboundOrderTypeId
     * statusId
     * containerNo
     * refDocDate
     * createdBy
     *
     * @return
     */
    public Stream<PreInboundHeaderStream> streamPreInboundHeader() {
        jdbcTemplate.setFetchSize(50);
        /**
         * PreInbound Header
         * String preInboundNo
         * String refDocNumber
         * Long inboundOrderTypeId
         * Long statusId
         * String containerNo
         * Date refDocDate
         * String createdBy
         */
        Stream<PreInboundHeaderStream> preInboundHeaderStream = jdbcTemplate.queryForStream(
                "Select PRE_IB_NO, REF_DOC_NO, IB_ORD_TYP_ID, STATUS_ID, CONT_NO, REF_DOC_DATE, "
                        + "CTD_BY "
                        + "from tblpreinboundheader "
                        + "where IS_DELETED = 0 ",
                (resultSet, rowNum) -> new PreInboundHeaderStream(
                        resultSet.getString("PRE_IB_NO"),
                        resultSet.getString("REF_DOC_NO"),
                        resultSet.getLong("IB_ORD_TYP_ID"),
                        resultSet.getLong("STATUS_ID"),
                        resultSet.getString("CONT_NO"),
                        resultSet.getDate("REF_DOC_DATE"),
                        resultSet.getString("CTD_BY")
                ));
        return preInboundHeaderStream;
    }


    //--------------------------------------------PreInboundHeader-V2-----------------------------------------------------------------------

    // POST - findPreInboundHeader/v2
    public PreInboundHeaderV2[] findPreInboundHeaderV2(SearchPreInboundHeaderV2 searchPreInboundHeader, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/findPreInboundHeader/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchPreInboundHeader, headers);
            ResponseEntity<PreInboundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PreInboundHeaderV2> obList = new ArrayList<>();
//            for (PreInboundHeaderV2 preInboundHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDate(preInboundHeader));
//
//            }
//            return obList.toArray(new PreInboundHeaderV2[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PreInboundHeaderV2 addingTimeWithDate(PreInboundHeaderV2 preInboundHeader) throws ParseException {

        if (preInboundHeader.getRefDocDate() != null) {
            preInboundHeader.setRefDocDate(DateUtils.addTimeToDate(preInboundHeader.getRefDocDate(), 3));
        }

        if (preInboundHeader.getCreatedOn() != null) {
            preInboundHeader.setCreatedOn(DateUtils.addTimeToDate(preInboundHeader.getCreatedOn(), 3));
        }

        if (preInboundHeader.getUpdatedOn() != null) {
            preInboundHeader.setUpdatedOn(DateUtils.addTimeToDate(preInboundHeader.getUpdatedOn(), 3));
        }

        return preInboundHeader;
    }

    // GET
    public PreInboundHeaderV2 getPreInboundHeaderV2(String preInboundNo, String warehouseId,
                                                    String companyCode, String plantId, String languageId,
                                                    String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/v2/" + preInboundNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDate(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PreInboundHeaderV2[] getPreInboundHeaderWithStatusIdV2(String warehouseId, String companyCode,
                                                                  String plantId, String languageId, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/inboundconfirm/v2")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

            //Adding time to Date
//            List<PreInboundHeaderV2> obList = new ArrayList<>();
//            for (PreInboundHeaderV2 preInboundHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDate(preInboundHeader));
//
//            }
//            return obList.toArray(new PreInboundHeaderV2[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PreInboundHeaderV2 createPreInboundHeaderV2(PreInboundHeaderV2 newPreInboundHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPreInboundHeader, headers);
        ResponseEntity<PreInboundHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeaderV2.class);
//		log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PreInboundHeaderV2 updatePreInboundHeaderV2(String preInboundNo, String warehouseId, String companyCode,
                                                       String plantId, String languageId, String loginUserID,
                                                       PreInboundHeaderV2 modifiedPreInboundHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/v2/" + preInboundNo)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PreInboundHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PreInboundHeaderV2.class);
//			log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePreInboundHeaderV2(String preInboundNo, String warehouseId, String companyCode,
                                            String plantId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundheader/v2/" + preInboundNo)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//-----------------------------------------------------PreInboundLine-V2-----------------------------------------------------------------------

    // GET
    public PreInboundLineV2 getPreInboundLineV2(String preInboundNo, String warehouseId, String companyCode,
                                                String plantId, String languageId, String refDocNumber,
                                                Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/" + preInboundNo + "/v2")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundLineV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDatePreInboundLine(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PreInboundLineV2 addingTimeWithDatePreInboundLine(PreInboundLineV2 preInboundLine) throws ParseException {

        if (preInboundLine.getExpectedArrivalDate() != null) {
            preInboundLine.setExpectedArrivalDate(DateUtils.addTimeToDate(preInboundLine.getExpectedArrivalDate(), 3));
        }

        if (preInboundLine.getCreatedOn() != null) {
            preInboundLine.setCreatedOn(DateUtils.addTimeToDate(preInboundLine.getCreatedOn(), 3));
        }

        if (preInboundLine.getUpdatedOn() != null) {
            preInboundLine.setUpdatedOn(DateUtils.addTimeToDate(preInboundLine.getUpdatedOn(), 3));
        }

        return preInboundLine;
    }

    /**
     * @param searchPreInboundLine
     * @param authToken
     * @return
     * @throws ParseException
     */
    public PreInboundLineOutputV2[] findPreInboundLineV2(SearchPreInboundLineV2 searchPreInboundLine, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/findPreInboundLine/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchPreInboundLine, headers);
            ResponseEntity<PreInboundLineOutputV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundLineOutputV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PreInboundLineV2[] getPreInboundLineV2(String preInboundNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/v2/" + preInboundNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PreInboundLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PreInboundLineV2> obList = new ArrayList<>();
//            for (PreInboundLineV2 preInboundLine : result.getBody()) {
//
//                obList.add(addingTimeWithDatePreInboundLine(preInboundLine));
//
//            }
//            return obList.toArray(new PreInboundLineV2[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH
    public PreInboundLineV2 patchPreInboundLineV2(String companyCode, String plantId, String warehouseId, String languageId,
                                                  String preInboundNo, String refDocNumber, Long lineNo, String itemCode,
                                                  String loginUserID, PreInboundLineV2 updatePreInboundLine, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updatePreInboundLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/v2/" + preInboundNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<PreInboundLineV2> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PreInboundLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PreInboundLineV2 createPreInboundLineV2(PreInboundLineV2 newPreInboundLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPreInboundLine, headers);
        ResponseEntity<PreInboundLineV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundLineV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // CREATE - BOM
    public PreInboundLineV2[] createPreInboundLineBOMV2(String preInboundNo, String warehouseId, String refDocNumber,
                                                        String companyCode, String plantId, String languageId,
                                                        String itemCode, Long lineNo, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/bom/v2")
                .queryParam("preInboundNo", preInboundNo)
                .queryParam("warehouseId", warehouseId)
                .queryParam("companyCode", companyCode)
                .queryParam("plantId", plantId)
                .queryParam("languageId", languageId)
                .queryParam("refDocNumber", refDocNumber)
                .queryParam("itemCode", itemCode)
                .queryParam("lineNo", lineNo)
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<PreInboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                PreInboundLineV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PreInboundLineV2 updatePreInboundLineV2(String preInboundNo, String warehouseId,
                                                   String companyCode, String plantId, String languageId,
                                                   String refDocNumber, Long lineNo, String itemCode,
                                                   String loginUserID, PreInboundLineV2 modifiedPreInboundLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundLine, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/v2/" + preInboundNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<PreInboundLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PreInboundLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePreInboundLineV2(String preInboundNo, String warehouseId,
                                          String companyCode, String plantId, String languageId,
                                          String refDocNumber, Long lineNo, String itemCode,
                                          String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "preinboundline/v2/" + preInboundNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//--------------------------------------------ContainerReceipt-V2-----------------------------------------------------------------------

    // GET
    public ContainerReceiptV2 getContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String preInboundNo, String refDocNumber,
                                                    String containerReceiptNo, String loginUserID, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo + "/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ContainerReceiptV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ContainerReceiptV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDateContainerReceipt(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public ContainerReceiptV2 addingTimeWithDateContainerReceipt(ContainerReceiptV2 containerReceipt) throws ParseException {

        if (containerReceipt.getContainerReceivedDate() != null) {
            containerReceipt.setContainerReceivedDate(DateUtils.addTimeToDate(containerReceipt.getContainerReceivedDate(), 3));
        }

        return containerReceipt;
    }

    // GET
    public ContainerReceiptV2 getContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String containerReceiptNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/v2/" + containerReceiptNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ContainerReceiptV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ContainerReceiptV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDateContainerReceipt(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findContainerReceipt/v2
    public ContainerReceiptV2[] findContainerReceiptV2(SearchContainerReceiptV2 searchContainerReceipt, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/findContainerReceipt/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchContainerReceipt, headers);
            ResponseEntity<ContainerReceiptV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContainerReceiptV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<ContainerReceiptV2> obList = new ArrayList<>();
//            for (ContainerReceiptV2 containerReceipt : result.getBody()) {
////				log.info("Result containerReceipt---getContainerReceivedDate() :" + containerReceipt.getContainerReceivedDate());
//
//                obList.add(addingTimeWithDateContainerReceipt(containerReceipt));
//            }
//            return obList.toArray(new ContainerReceiptV2[obList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public ContainerReceiptV2 createContainerReceiptV2(ContainerReceiptV2 newContainerReceipt, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newContainerReceipt, headers);
        ResponseEntity<ContainerReceiptV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContainerReceiptV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public ContainerReceiptV2 updateContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                       String warehouseId, String containerReceiptNo,
                                                       String loginUserID, ContainerReceiptV2 modifiedContainerReceipt, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedContainerReceipt, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/v2/" + containerReceiptNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<ContainerReceiptV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ContainerReceiptV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteContainerReceiptV2(String companyCode, String plantId, String languageId,
                                            String WarehouseId, String preInboundNo, String refDocNumber,
                                            String containerReceiptNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "containerreceipt/v2/" + containerReceiptNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("WarehouseId", WarehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------InboundHeader-V2-----------------------------------------------------------------------
    // GET - findInboundHeader-V2
    public InboundHeaderEntityV2[] findInboundHeaderV2(SearchInboundHeaderV2 searchInboundHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/findInboundHeader/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
        ResponseEntity<InboundHeaderEntityV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeaderEntityV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();

//        List<InboundHeaderEntityV2> inboundHeaderList = new ArrayList<>();
//        for (InboundHeaderEntityV2 inboundHeader : result.getBody()) {
//            if (inboundHeader.getCreatedOn() != null) {
//                inboundHeader.setCreatedOn(DateUtils.addTimeToDate(inboundHeader.getCreatedOn(), 3));
//            }
//            if (inboundHeader.getConfirmedOn() != null) {
//                inboundHeader.setConfirmedOn(DateUtils.addTimeToDate(inboundHeader.getConfirmedOn(), 3));
//            }
//            if (inboundHeader.getUpdatedOn() != null) {
//                inboundHeader.setUpdatedOn(DateUtils.addTimeToDate(inboundHeader.getUpdatedOn(), 3));
//            }
//            inboundHeaderList.add(inboundHeader);
//        }
//        return inboundHeaderList.toArray(new InboundHeaderEntityV2[inboundHeaderList.size()]);
    }

    // GET - findInboundHeader-V2
    public InboundHeaderEntityV2[] findInboundHeaderStreamV2(SearchInboundHeaderV2 searchInboundHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/findInboundHeader/v2/stream");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
        ResponseEntity<InboundHeaderEntityV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeaderEntityV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - replaceASN
    public Boolean replaceASNV2(String refDocNumber, String preInboundNo, String asnNumber, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/replaceASN/v2")
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("preInboundNo", preInboundNo)
                        .queryParam("asnNumber", asnNumber);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Boolean.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();

    }

    // GET
    public InboundHeaderEntityV2 getInboundHeaderV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String refDocNumber, String preInboundNo,
                                                    String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/" + refDocNumber)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundHeaderEntityV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundHeaderEntityV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//            if (result.getBody().getCreatedOn() != null) {
//                result.getBody().setCreatedOn(DateUtils.addTimeToDate(result.getBody().getCreatedOn(), 3));
//            }
//
//            if (result.getBody().getUpdatedOn() != null) {
//                result.getBody().setUpdatedOn(DateUtils.addTimeToDate(result.getBody().getUpdatedOn(), 3));
//            }
//            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH
    public InboundHeaderEntityV2 updateInboundHeaderV2(String companyCode, String plantId, String languageId,
                                                       String warehouseId, String refDocNumber, String preInboundNo,
                                                       String loginUserID, InboundHeaderV2 modifiedInboundHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedInboundHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/" + refDocNumber)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<InboundHeaderEntityV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, InboundHeaderEntityV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH
    public AXApiResponse updateInboundHeaderConfirmV2(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String preInboundNo, String refDocNumber,
                                                      String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/confirmIndividual")
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<AXApiResponse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                    AXApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH - Partial Confirm
    public AXApiResponse updateInboundHeaderPartialConfirmV2(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String preInboundNo, String refDocNumber,
                                                             String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/partialConfirmIndividual")
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<AXApiResponse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                    AXApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH - Partial Confirm - with InboundLines input
    public AXApiResponse updateInboundHeaderWithIbLinePartialConfirmV2(List<InboundLineV2> inboundLineList, String companyCode, String plantId,
                                                                       String languageId, String warehouseId, String preInboundNo, String refDocNumber,
                                                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(inboundLineList, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/confirmIndividual/partial")
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<AXApiResponse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
                    AXApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                         String refDocNumber, String preInboundNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/v2/" + refDocNumber)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --------------------------------------------InboundLine-V2-----------------------------------------------------------------------
    // GET ALL
    public InboundLineV2[] getInboundLinesV2(String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<InboundLineV2> obList = new ArrayList<>();
//            for (InboundLineV2 inboundLine : result.getBody()) {
//
//                obList.add(addingTimeWithDateInboundLine(inboundLine));
//
//            }
//            return obList.toArray(new InboundLineV2[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public InboundLineV2 addingTimeWithDateInboundLine(InboundLineV2 inboundLine) throws ParseException {

        if (inboundLine.getExpectedArrivalDate() != null) {
            inboundLine.setExpectedArrivalDate(DateUtils.addTimeToDate(inboundLine.getExpectedArrivalDate(), 3));
        }

        if (inboundLine.getCreatedOn() != null) {
            inboundLine.setCreatedOn(DateUtils.addTimeToDate(inboundLine.getCreatedOn(), 3));
        }

        if (inboundLine.getConfirmedOn() != null) {
            inboundLine.setConfirmedOn(DateUtils.addTimeToDate(inboundLine.getConfirmedOn(), 3));
        }

        return inboundLine;
    }

    // GET
    public InboundLineV2 getInboundLineV2(String companyCode, String plantId, String languageId,
                                          String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                          String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2/" + lineNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundLineV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//            return addingTimeWithDateInboundLine(result.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - Finder
    public InboundLineV2[] findInboundLineV2(SearchInboundLineV2 searchInboundLine, String authToken)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2/findInboundLine");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundLine, headers);
        ResponseEntity<InboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, InboundLineV2[].class);

        log.info("result : " + result.getStatusCode());
        return result.getBody();


//        List<InboundLineV2> obList = new ArrayList<>();
//        for (InboundLineV2 inboundLine : result.getBody()) {
//
//            obList.add(addingTimeWithDateInboundLine(inboundLine));
//
//        }
//        return obList.toArray(new InboundLineV2[obList.size()]);
    }

    // POST
    public InboundLineV2 createInboundLineV2(InboundLineV2 newInboundLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInboundLine, headers);
        ResponseEntity<InboundLineV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                InboundLineV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InboundLineV2 updateInboundLineV2(String companyCode, String plantId, String languageId,
                                             String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                             String itemCode, String loginUserID, InboundLineV2 modifiedInboundLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInboundLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2/" + lineNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<InboundLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    InboundLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH - Batch Process
    public InboundLineV2[] batchUpdateInboundLineV2(List<InboundLineV2> modifiedInboundLines, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInboundLines, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2/batchUpdateInboundLines")
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<InboundLineV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    InboundLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundLineV2(String companyCode, String plantId, String languageId,
                                       String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
                                       String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundline/v2" + lineNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


//--------------------------------------------StagingHeader-V2-----------------------------------------------------------------------

    // GET
    public StagingHeaderV2 getStagingHeaderV2(String companyCode, String plantId, String languageId,
                                              String warehouseId, String preInboundNo, String refDocNumber,
                                              String stagingNo, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("stagingNo", stagingNo);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingHeaderV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDateStagingHeader(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public StagingHeaderV2 addingTimeWithDateStagingHeader(StagingHeaderV2 stagingHeader) throws ParseException {

        if (stagingHeader.getConfirmedOn() != null) {
            stagingHeader.setConfirmedOn(DateUtils.addTimeToDate(stagingHeader.getConfirmedOn(), 3));
        }

        if (stagingHeader.getCreatedOn() != null) {
            stagingHeader.setCreatedOn(DateUtils.addTimeToDate(stagingHeader.getCreatedOn(), 3));
        }

        if (stagingHeader.getUpdatedOn() != null) {
            stagingHeader.setUpdatedOn(DateUtils.addTimeToDate(stagingHeader.getUpdatedOn(), 3));
        }

        return stagingHeader;
    }

    // POST-V2
    public StagingHeaderV2[] findStagingHeaderV2(SearchStagingHeaderV2 searchStagingHeader, String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/findStagingHeader/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchStagingHeader, headers);
        ResponseEntity<StagingHeaderV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeaderV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
//        List<StagingHeaderV2> stagingHeaderList = new ArrayList<>();
//        for (StagingHeaderV2 stagingHeader : result.getBody()) {
//
//            stagingHeaderList.add(addingTimeWithDateStagingHeader(stagingHeader));
//        }
//        return stagingHeaderList.toArray(new StagingHeaderV2[stagingHeaderList.size()]);
    }

    // POST
    public StagingHeaderV2 createStagingHeaderV2(StagingHeaderV2 newStagingHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newStagingHeader, headers);
        ResponseEntity<StagingHeaderV2> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeaderV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public StagingHeaderV2 updateStagingHeaderV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String preInboundNo, String refDocNumber,
                                                 String stagingNo, String loginUserID,
                                                 StagingHeaderV2 modifiedStagingHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStagingHeader, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("stagingNo", stagingNo)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<StagingHeaderV2> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteStagingHeaderV2(String companyCode, String plantId, String languageId,
                                         String warehouseId, String preInboundNo, String refDocNumber,
                                         String stagingNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("stagingNo", stagingNo)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------StagingLine-V2----------------------------------------------------------------------

    // GET
    public StagingLineEntityV2 getStagingLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String preInboundNo, String refDocNumber,
                                                String stagingNo, String palletCode, String caseCode,
                                                Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/v2" + lineNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("stagingNo", stagingNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StagingLineEntityV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingLineEntityV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDateStagingLineEntity(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public StagingLineEntityV2 addingTimeWithDateStagingLineEntity(StagingLineEntityV2 stagingLine) throws ParseException {

        if (stagingLine.getCreatedOn() != null) {
            stagingLine.setCreatedOn(DateUtils.addTimeToDate(stagingLine.getCreatedOn(), 3));
        }

        if (stagingLine.getUpdatedOn() != null) {
            stagingLine.setUpdatedOn(DateUtils.addTimeToDate(stagingLine.getUpdatedOn(), 3));
        }

        if (stagingLine.getConfirmedOn() != null) {
            stagingLine.setConfirmedOn(DateUtils.addTimeToDate(stagingLine.getConfirmedOn(), 3));
        }

        return stagingLine;
    }

    // POST - findStagingLine-V2
    public StagingLineEntityV2[] findStagingLineV2(SearchStagingLineV2 searchStagingLine, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/findStagingLine");
            HttpEntity<?> entity = new HttpEntity<>(searchStagingLine, headers);
            ResponseEntity<StagingLineEntityV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingLineEntityV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<StagingLineEntityV2> stagingLineList = new ArrayList<>();
//            for (StagingLineEntityV2 stagingLine : result.getBody()) {
//
//                stagingLineList.add(addingTimeWithDateStagingLineEntity(stagingLine));
//            }
//            return stagingLineList.toArray(new StagingLineEntityV2[stagingLineList.size()]);
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - V2
    public StagingLineEntityV2[] createStagingLineV2(List<PreInboundLineV2> newStagingLine, String warehouseId,
                                                     String companyCodeId, String plantId, String languageId,
                                                     String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/v2")
                .queryParam("warehouseId", warehouseId)
                .queryParam("companyCodeId", companyCodeId)
                .queryParam("plantId", plantId)
                .queryParam("languageId", languageId)
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newStagingLine, headers);
        ResponseEntity<StagingLineEntityV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingLineEntityV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public StagingLineEntityV2 updateStagingLineV2(String companyCode, String plantId, String languageId,
                                                   String warehouseId, String preInboundNo, String refDocNumber,
                                                   String stagingNo, String palletCode, String caseCode, Long lineNo,
                                                   String itemCode, String loginUserID,
                                                   StagingLineEntityV2 modifiedStagingLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStagingLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/v2/" + lineNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode)
                    .queryParam("caseCode", caseCode)
                    .queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntityV2> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntityV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH
    public StagingLineEntityV2[] caseConfirmationV2(String companyCode, String plantId, String languageId,
                                                    List<CaseConfirmation> caseConfirmations, String caseCode,
                                                    String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(caseConfirmations, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/caseConfirmation/v2")
                            .queryParam("caseCode", caseCode)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntityV2[]> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntityV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // DELETE
    public boolean deleteStagingLineV2(String companyCode, String plantId, String languageId,
                                       String warehouseId, String preInboundNo, String refDocNumber,
                                       String stagingNo, String palletCode, String caseCode,
                                       Long lineNo, String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/v2/" + lineNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("stagingNo", stagingNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // DELETE
    public boolean deleteCasesV2(String preInboundNo, Long lineNo, String itemCode, String caseCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/v2/" + lineNo + "/cases")
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("caseCode", caseCode)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // AssignHHTUser
    public StagingLineEntityV2[] assignHHTUserV2(String companyCode, String plantId, String languageId,
                                                 List<AssignHHTUser> assignHHTUsers, String assignedUserId,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(assignHHTUsers, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/assignHHTUser/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("assignedUserId", assignedUserId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StagingLineEntityV2[]> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntityV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//--------------------------------------------GrHeader-V2----------------------------------------------------------------------

    // GET
    public GrHeaderV2 getGrHeaderV2(String companyCode, String plantId, String languageId,
                                    String warehouseId, String preInboundNo, String refDocNumber,
                                    String stagingNo, String goodsReceiptNo, String palletCode,
                                    String caseCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/v2/" + goodsReceiptNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("stagingNo", stagingNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrHeaderV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
            //return addingTimeWithDateGrHeader(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public GrHeaderV2 addingTimeWithDateGrHeader(GrHeaderV2 grHeader) throws ParseException {

        if (grHeader.getCreatedOn() != null) {
            grHeader.setCreatedOn(DateUtils.addTimeToDate(grHeader.getCreatedOn(), 3));
        }

        if (grHeader.getUpdatedOn() != null) {
            grHeader.setUpdatedOn(DateUtils.addTimeToDate(grHeader.getUpdatedOn(), 3));
        }

        if (grHeader.getConfirmedOn() != null) {
            grHeader.setConfirmedOn(DateUtils.addTimeToDate(grHeader.getConfirmedOn(), 3));
        }

        if (grHeader.getExpectedArrivalDate() != null) {
            grHeader.setExpectedArrivalDate(DateUtils.addTimeToDate(grHeader.getExpectedArrivalDate(), 3));
        }

        if (grHeader.getGoodsReceiptDate() != null) {
            grHeader.setGoodsReceiptDate(DateUtils.addTimeToDate(grHeader.getGoodsReceiptDate(), 3));
        }

        return grHeader;
    }

    // POST - findGrHeader/V2
    public GrHeaderV2[] findGrHeaderV2(SearchGrHeaderV2 searchGrHeader, String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/findGrHeader/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchGrHeader, headers);
        ResponseEntity<GrHeaderV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrHeaderV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
//        List<GrHeaderV2> grHeaderList = new ArrayList<>();
//        for (GrHeaderV2 grHeader : result.getBody()) {
//
//            grHeaderList.add(addingTimeWithDateGrHeader(grHeader));
//        }
//        return grHeaderList.toArray(new GrHeaderV2[grHeaderList.size()]);
    }

    // POST - V2
    public GrHeaderV2 createGrHeaderV2(GrHeaderV2 newGrHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newGrHeader, headers);
        ResponseEntity<GrHeaderV2> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrHeaderV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public GrHeaderV2 updateGrHeaderV2(String companyCode, String plantId, String languageId,
                                       String warehouseId, String preInboundNo, String refDocNumber,
                                       String stagingNo, String goodsReceiptNo, String palletCode,
                                       String caseCode, String loginUserID, GrHeaderV2 modifiedGrHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedGrHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/v2/" + goodsReceiptNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("stagingNo", stagingNo)
                    .queryParam("palletCode", palletCode)
                    .queryParam("caseCode", caseCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<GrHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, GrHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteGrHeaderV2(String companyCode, String plantId, String languageId,
                                    String warehouseId, String preInboundNo, String refDocNumber,
                                    String stagingNo, String goodsReceiptNo, String palletCode,
                                    String caseCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grheader/v2/" + goodsReceiptNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("stagingNo", stagingNo)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------GrLine-V2----------------------------------------------------------------------
    // GET
    public GrLineV2 getGrLineV2(String companyCode, String plantId, String languageId,
                                String warehouseId, String preInboundNo, String refDocNumber,
                                String goodsReceiptNo, String palletCode, String caseCode,
                                String packBarcodes, Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v2/" + lineNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrLineV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDateGrLine(result.getBody());
        } catch (Exception e) {
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public GrLineV2 addingTimeWithDateGrLine(GrLineV2 grLine) throws ParseException {

        if (grLine.getCreatedOn() != null) {
            grLine.setCreatedOn(DateUtils.addTimeToDate(grLine.getCreatedOn(), 3));
        }

        if (grLine.getUpdatedOn() != null) {
            grLine.setUpdatedOn(DateUtils.addTimeToDate(grLine.getUpdatedOn(), 3));
        }

        if (grLine.getConfirmedOn() != null) {
            grLine.setConfirmedOn(DateUtils.addTimeToDate(grLine.getConfirmedOn(), 3));
        }

        if (grLine.getExpiryDate() != null) {
            grLine.setExpiryDate(DateUtils.addTimeToDate(grLine.getExpiryDate(), 3));
        }

        if (grLine.getManufacturerDate() != null) {
            grLine.setManufacturerDate(DateUtils.addTimeToDate(grLine.getManufacturerDate(), 3));
        }

        return grLine;
    }

    // GET
    public GrLineV2[] getGrLineV2(String companyCode, String plantId, String languageId,
                                  String preInboundNo, String refDocNumber, String packBarcodes,
                                  Long lineNo, String itemCode, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v2/" + lineNo + "/putawayline")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GrLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<GrLineV2> grLineList = new ArrayList<>();
//            for (GrLineV2 grLine : result.getBody()) {
//
//                grLineList.add(addingTimeWithDateGrLine(grLine));
//            }
//            return grLineList.toArray(new GrLineV2[grLineList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findGrLine Method
    public GrLineV2[] findGrLineV2(SearchGrLineV2 searchGrLine, String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/findGrLine/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchGrLine, headers);
        ResponseEntity<GrLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLineV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
//        List<GrLineV2> grLineList = new ArrayList<>();
//        for (GrLineV2 grLine : result.getBody()) {
//
//            grLineList.add(addingTimeWithDateGrLine(grLine));
//        }
//        return grLineList.toArray(new GrLineV2[grLineList.size()]);
    }

    // POST - findGrLine SQL Method
    public GrLineV2[] findGrLineSQLV2(SearchGrLineV2 searchGrLine, String authToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/findGrLineNew/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchGrLine, headers);
        ResponseEntity<GrLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLineV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - V2
    public GrLineV2[] createGrLineV2(List<AddGrLineV2> newGrLine, String loginUserID, String authToken) {
        log.info("AddGrLineV2 ------> {}", newGrLine);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newGrLine, headers);
        ResponseEntity<GrLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLineV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public GrLineV2 updateGrLineV2(String companyCode, String plantId, String languageId,
                                   String warehouseId, String preInboundNo, String refDocNumber,
                                   String goodsReceiptNo, String palletCode, String caseCode,
                                   String packBarcodes, Long lineNo, String itemCode,
                                   String loginUserID, GrLineV2 modifiedGrLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedGrLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v2/" + lineNo)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode)
                    .queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<GrLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, GrLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteGrLineV2(String companyCode, String plantId, String languageId, String warehouseId,
                                  String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                  String palletCode, String caseCode, String packBarcodes,
                                  Long lineNo, String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v2/" + lineNo)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET PackBarcode
    public PackBarcode[] generatePackBarcodeV2(String companyCode, String plantId, String languageId,
                                               Long acceptQty, Long damageQty, String warehouseId,
                                               String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/packBarcode/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("acceptQty", acceptQty)
                            .queryParam("damageQty", damageQty)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PackBarcode[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackBarcode[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

//--------------------------------------------PutAwayHeader-V2----------------------------------------------------------------------

    // GET
    public PutAwayHeaderV2 getPutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                              String warehouseId, String preInboundNo, String refDocNumber,
                                              String goodsReceiptNo, String palletCode, String caseCode,
                                              String packBarcodes, String putAwayNumber, String proposedStorageBin, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/v2/" + putAwayNumber)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("putAwayNumber", putAwayNumber)
                            .queryParam("proposedStorageBin", proposedStorageBin);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDatePutAwayHeader(result.getBody());
        } catch (Exception e) {
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PutAwayHeaderV2 addingTimeWithDatePutAwayHeader(PutAwayHeaderV2 putAwayHeader) throws ParseException {

        if (putAwayHeader.getCreatedOn() != null) {
            putAwayHeader.setCreatedOn(DateUtils.addTimeToDate(putAwayHeader.getCreatedOn(), 3));
        }

        if (putAwayHeader.getUpdatedOn() != null) {
            putAwayHeader.setUpdatedOn(DateUtils.addTimeToDate(putAwayHeader.getUpdatedOn(), 3));
        }

        if (putAwayHeader.getConfirmedOn() != null) {
            putAwayHeader.setConfirmedOn(DateUtils.addTimeToDate(putAwayHeader.getConfirmedOn(), 3));
        }

        return putAwayHeader;
    }

    //V2
    public PutAwayHeaderV2[] findPutAwayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeader, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/findPutAwayHeader/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayHeader, headers);
            ResponseEntity<PutAwayHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PutAwayHeaderV2> putAwayHeaderList = new ArrayList<>();
//            for (PutAwayHeaderV2 putAwayHeader : result.getBody()) {
//
//                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
//            }
//            return putAwayHeaderList.toArray(new PutAwayHeaderV2[putAwayHeaderList.size()]);
        } catch (Exception e) {
            throw e;
        }
    }


    // GET - /{refDocNumber}/inboundreversal/asn
    public PutAwayHeaderV2[] getPutAwayHeaderV2(String companyCode, String plantId, String languageId, String refDocNumber, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + refDocNumber +
                                    "/inboundreversal/asn/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PutAwayHeaderV2> putAwayHeaderList = new ArrayList<>();
//            for (PutAwayHeaderV2 putAwayHeader : result.getBody()) {
//
//                putAwayHeaderList.add(addingTimeWithDatePutAwayHeader(putAwayHeader));
//            }
//            return putAwayHeaderList.toArray(new PutAwayHeaderV2[putAwayHeaderList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PutAwayHeaderV2 createPutAwayHeaderV2(PutAwayHeaderV2 newPutAwayHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPutAwayHeader, headers);
        ResponseEntity<PutAwayHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayHeaderV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PutAwayHeaderV2 updatePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String preInboundNo, String refDocNumber,
                                                 String goodsReceiptNo, String palletCode, String caseCode,
                                                 String packBarcodes, String putAwayNumber, String proposedStorageBin,
                                                 PutAwayHeaderV2 modifiedPutAwayHeader, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/v2/" + putAwayNumber)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("palletCode", palletCode)
                    .queryParam("caseCode", caseCode)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("proposedStorageBin", proposedStorageBin)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH for Bulk Update
    public PutAwayHeaderV2[] updatePutAwayHeaderV2(List<PutAwayHeaderV2> modifiedPutAwayHeader, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/v2/putAway")

                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeaderV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }


    // PATCH - /{refDocNumber}/reverse
    public PutAwayHeaderV2[] updatePutAwayHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                   String refDocNumber, String packBarcodes, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/" + refDocNumber + "/reverse/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeaderV2[]> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH - /inbound reversal - Batch
    public PutAwayHeaderV2[] batchPutAwayHeaderReversalV2(List<InboundReversalInput> inboundReversalInputList, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(inboundReversalInputList, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/reverse/batch/v2")
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayHeaderV2[]> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePutAwayHeaderV2(String companyCode, String plantId, String languageId,
                                         String warehouseId, String preInboundNo, String refDocNumber,
                                         String goodsReceiptNo, String palletCode, String caseCode,
                                         String packBarcodes, String putAwayNumber, String proposedStorageBin,
                                         String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayheader/v2/" + putAwayNumber)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("palletCode", palletCode)
                            .queryParam("caseCode", caseCode)
                            .queryParam("packBarcodes", packBarcodes)
                            .queryParam("putAwayNumber", putAwayNumber)
                            .queryParam("proposedStorageBin", proposedStorageBin)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//--------------------------------------------PutAwayLine-V2----------------------------------------------------------------------

    // GET
    public PutAwayLineV2 getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                          String warehouseId, String goodsReceiptNo, String preInboundNo,
                                          String refDocNumber, String putAwayNumber, Long lineNo,
                                          String itemCode, String proposedStorageBin, List<String> confirmedStorageBin, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/v2/" + confirmedStorageBin)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("putAwayNumber", putAwayNumber)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("confirmedStorageBin", confirmedStorageBin)
                            .queryParam("proposedStorageBin", proposedStorageBin);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayLineV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            return addingTimeWithDatePutAwayLine(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Add Time to Date plus 3 Hours
    public PutAwayLineV2 addingTimeWithDatePutAwayLine(PutAwayLineV2 putAwayLine) throws ParseException {

        if (putAwayLine.getCreatedOn() != null) {
            putAwayLine.setCreatedOn(DateUtils.addTimeToDate(putAwayLine.getCreatedOn(), 3));
        }
        if (putAwayLine.getConfirmedOn() != null) {
            putAwayLine.setConfirmedOn(DateUtils.addTimeToDate(putAwayLine.getConfirmedOn(), 3));
        }
        if (putAwayLine.getUpdatedOn() != null) {
            putAwayLine.setUpdatedOn(DateUtils.addTimeToDate(putAwayLine.getUpdatedOn(), 3));
        }

        return putAwayLine;
    }

    // GET - /{refDocNumber}/inboundreversal/palletId
    public PutAwayLineV2[] getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                            String refDocNumber, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/" + refDocNumber +
                                    "/inboundreversal/palletId/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PutAwayLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
//            List<PutAwayLineV2> putAwayLineList = new ArrayList<>();
//            for (PutAwayLineV2 putAwayLine : result.getBody()) {
//
//                putAwayLineList.add(addingTimeWithDatePutAwayLine(putAwayLine));
//            }
//            return putAwayLineList.toArray(new PutAwayLineV2[putAwayLineList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // findPutAwayLine/v2
    public PutAwayLineV2[] findPutAwayLineV2(SearchPutAwayLineV2 searchPutAwayLine, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/findPutAwayLine/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayLine, headers);
            ResponseEntity<PutAwayLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLineV2[].class);

            log.info("result : " + result.getStatusCode());
            return result.getBody();

//            List<PutAwayLineV2> putAwayLineList = new ArrayList<>();
//            if (result.getBody() != null) {
//                for (PutAwayLineV2 putAwayLine : result.getBody()) {
//                    if (putAwayLine.getCreatedOn() != null) {
//                        putAwayLine.setCreatedOn(DateUtils.addTimeToDate(putAwayLine.getCreatedOn(), 3));
//                    }
//                    if (putAwayLine.getConfirmedOn() != null) {
//                        putAwayLine.setConfirmedOn(DateUtils.addTimeToDate(putAwayLine.getConfirmedOn(), 3));
//                    }
//                    putAwayLineList.add(putAwayLine);
//                }
//            }
//            return putAwayLineList.toArray(new PutAwayLineV2[putAwayLineList.size()]);
        } catch (Exception e) {
            throw e;
        }
    }

    // POST
    public PutAwayLineV2[] createPutAwayLineV2(List<PutAwayLineV2> newPutAwayLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/confirm/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPutAwayLine, headers);
        ResponseEntity<PutAwayLineV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLineV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PutAwayLineV2 updatePutAwayLineV2(String companyCode, String plantId, String languageId,
                                             String warehouseId, String goodsReceiptNo, String preInboundNo,
                                             String refDocNumber, String putAwayNumber, Long lineNo,
                                             String itemCode, String proposedStorageBin, String confirmedStorageBin,
                                             PutAwayLineV2 modifiedPutAwayLine, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/v2/" + confirmedStorageBin)
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("goodsReceiptNo", goodsReceiptNo)
                    .queryParam("putAwayNumber", putAwayNumber)
                    .queryParam("lineNo", lineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("proposedStorageBin", proposedStorageBin)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayLineV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePutAwayLineV2(String companyCode, String plantId, String languageId,
                                       String warehouseId, String goodsReceiptNo, String preInboundNo,
                                       String refDocNumber, String putAwayNumber, Long lineNo,
                                       String itemCode, String proposedStorageBin, String confirmedStorageBin,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "putawayline/v2/" + confirmedStorageBin)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("goodsReceiptNo", goodsReceiptNo)
                            .queryParam("putAwayNumber", putAwayNumber)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("confirmedStorageBin", confirmedStorageBin)
                            .queryParam("proposedStorageBin", proposedStorageBin)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //====================================================Inventory==========================================================================

    //Add Time to Date plus 3 Hours
    public InventoryV2 addingTimeWithDateInventory(InventoryV2 inventory) throws ParseException {

        if (inventory.getCreatedOn() != null) {
            inventory.setCreatedOn(DateUtils.addTimeToDate(inventory.getCreatedOn(), 3));
        }

        if (inventory.getManufacturerDate() != null) {
            inventory.setManufacturerDate(DateUtils.addTimeToDate(inventory.getManufacturerDate(), 3));
        }

        if (inventory.getExpiryDate() != null) {
            inventory.setExpiryDate(DateUtils.addTimeToDate(inventory.getExpiryDate(), 3));
        }

        return inventory;
    }

    // POST - findInventory/v2
    public InventoryV2[] findInventoryV2(SearchInventoryV2 searchInventory, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/findInventoryNew/v2");
            HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
            ResponseEntity<InventoryV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public InventoryV2 createInventoryV2(InventoryV2 newInventory, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/v2")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInventory, headers);
        ResponseEntity<InventoryV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                InventoryV2.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InventoryV2 updateInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes,
                                         String itemCode, String manufacturerName, String storageBin, Long stockTypeId,
                                         Long specialStockIndicatorId, InventoryV2 modifiedInventory, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedInventory, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/v2/" + stockTypeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("manufacturerName", manufacturerName)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                    .queryParam("stockTypeId", stockTypeId)
                    .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<InventoryV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                    InventoryV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                     String manufacturerName, String packBarcodes, String itemCode, String storageBin,
                                     Long stockTypeId, Long specialStockIndicatorId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "inventory/v2/" + stockTypeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("manufacturerName", manufacturerName)
                    .queryParam("packBarcodes", packBarcodes)
                    .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                    .queryParam("stockTypeId", stockTypeId)
                    .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /*-----------------------------------------Delivery Module-------------------------------------------------------*/
    //--------------------------------------------Delivery Header------------------------------------------------------------------------

    // GET ALL
    public DeliveryHeader[] getAllDeliveryHeader(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<DeliveryHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public DeliveryHeader getDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId,
                                            String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<DeliveryHeader> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public DeliveryHeader createDeliveryHeader(AddDeliveryHeader newDeliveryHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newDeliveryHeader, headers);
        ResponseEntity<DeliveryHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public DeliveryHeader updateDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId, String languageId,
                                               String plantId, String loginUserID, UpdateDeliveryHeader updateDeliveryHeader,
                                               String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateDeliveryHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<DeliveryHeader> result = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.PATCH, entity, DeliveryHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId, String languageId,
                                        String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //SEARCH
    public DeliveryHeader[] findDeliveryHeader(SearchDeliveryHeader searchDeliveryHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryheader/findDeliveryHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchDeliveryHeader, headers);
            ResponseEntity<DeliveryHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------Delivery Line------------------------------------------------------------------------

    // GET ALL
    public DeliveryLine[] getAllDeliveryLine(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<DeliveryLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public DeliveryLine getDeliveryLine(String warehouseId, Long deliveryNo, String itemCode, Long lineNumber,
                                        String companyCodeId, String languageId, String plantId, String invoiceNumber,
                                        String refDocNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/" + deliveryNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("lineNumber", lineNumber)
                            .queryParam("invoiceNumber", invoiceNumber)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<DeliveryLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST DeliveryLine
    public DeliveryLine[] createDeliveryLine(List<AddDeliveryLine> newDeliveryLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newDeliveryLine, headers);
        ResponseEntity<DeliveryLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLine[].class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH
    public DeliveryLine[] updateDeliveryLine(String loginUserID, List<UpdateDeliveryLine> updateDeliveryLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateDeliveryLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/deliveryNo/")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<DeliveryLine[]> result = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.PATCH, entity, DeliveryLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteDeliveryLine(String warehouseId, Long deliveryNo, String itemCode, Long lineNumber,
                                      String refDocNumber, String invoiceNumber, String companyCodeId, String languageId,
                                      String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/" + deliveryNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("lineNumber", lineNumber)
                            .queryParam("invoiceNumber", invoiceNumber)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //SEARCH
    public DeliveryLine[] findDeliveryLine(SearchDeliveryLine searchDeliveryLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/findDeliveryLine");
            HttpEntity<?> entity = new HttpEntity<>(searchDeliveryLine, headers);
            ResponseEntity<DeliveryLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // GET - DeliveryLineCount
    public DeliveryLineCount getDeliveryLineCount(String companyCodeId, String plantId, String languageId, String warehouseId, String driverId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/count")
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("driverId", driverId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<DeliveryLineCount> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, DeliveryLineCount.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //SEARCH
    public DeliveryLineCount findDeliveryLineCount(FindDeliveryLineCount findDeliveryLineCount, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "deliveryline/findDeliveryLineCount");
            HttpEntity<?> entity = new HttpEntity<>(findDeliveryLineCount, headers);
            ResponseEntity<DeliveryLineCount> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLineCount.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //----------------------Orders------------------------------------------------------------------

    // POST - ASN V2 upload
    public WarehouseApiResponse[] postASNV2Upload(List<ASNV2> asnv2List, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundOrderServiceApiUrl() + "inboundorder/inbound/asn/upload/v2")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(asnv2List, headers);
        ResponseEntity<WarehouseApiResponse[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse[].class);
        return result.getBody();
    }

    //----------------------Orders------------------------------------------------------------------

    // POST - Empty V2 upload
    public WarehouseApiResponse[] postEmptyUpload(List<ASNV2> asnv2List, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundOrderServiceApiUrl() + "inboundorder/inbound/empty/upload/v2")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(asnv2List, headers);
        ResponseEntity<WarehouseApiResponse[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse[].class);
        return result.getBody();
    }

    // POST - SO
    public WarehouseApiResponse postASNV2(@Valid ASNV2 asnv2, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/asn/v2");
        HttpEntity<?> entity = new HttpEntity<>(asnv2, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - StockReceipt
    public WarehouseApiResponse postStockReceipt(@Valid StockReceiptHeader stockReceipt, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/stockReceipt");
        HttpEntity<?> entity = new HttpEntity<>(stockReceipt, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - SO Return V2
    public WarehouseApiResponse postSOReturnV2(SaleOrderReturnV2 saleOrderReturnV2, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/soreturn/v2");
        HttpEntity<?> entity = new HttpEntity<>(saleOrderReturnV2, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - B2bTransferIn
    public WarehouseApiResponse postB2bTransferIn(@Valid B2bTransferIn b2bTransferIn, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/b2bTransferIn");
        HttpEntity<?> entity = new HttpEntity<>(b2bTransferIn, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - WH2WH Orders via upload
    public WarehouseApiResponse[] postInterWarehouseTransferInUploadV2(List<InterWarehouseTransferInV2> interWarehouseTransferInV2List,
                                                                       String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/interWarehouseTransferIn/upload/v2")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(interWarehouseTransferInV2List, headers);
        ResponseEntity<WarehouseApiResponse[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse[].class);
        return result.getBody();
    }

    // POST - WH2WH Orders via upload
    // For Knowell
    public WarehouseApiResponse[] postInterWarehouseTransferInUploadV7(List<InterWarehouseTransferInV2> interWarehouseTransferInV2List,
                                                                       String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/interWarehouseTransferIn/upload/v2")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(interWarehouseTransferInV2List, headers);
        ResponseEntity<WarehouseApiResponse[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse[].class);
        return result.getBody();
    }

    // POST - InterWarehouseTransferInV2
    public WarehouseApiResponse postInterWarehouseTransferInV2(@Valid InterWarehouseTransferInV2 interWarehouseTransferInV2, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/interWarehouseTransferIn/v2");
        HttpEntity<?> entity = new HttpEntity<>(interWarehouseTransferInV2, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - SO Return V2
    public WarehouseApiResponse[] postSOReturnUploadV2(List<SaleOrderReturnV2> saleOrderReturnV2, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundOrderServiceApiUrl() + "inboundorder/inbound/soreturn/upload/v2");
        HttpEntity<?> entity = new HttpEntity<>(saleOrderReturnV2, headers);
        ResponseEntity<WarehouseApiResponse[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    /*-------------------------------Supplier Invoice Cancellation-------------------------------------*/

    public WarehouseApiResponse replaceInvoice(String companyCode, String plantId, String languageId, String warehouseId,
                                               String newInvoiceNo, String oldInvoiceNo, String newPreInboundNo, String oldPreInboundNo, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "invoice/supplierInvoice/cancellation")
                    .queryParam("companyCode", companyCode)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("oldInvoiceNo", oldInvoiceNo)
                    .queryParam("newInvoiceNo", newInvoiceNo)
                    .queryParam("oldPreInboundNo", oldPreInboundNo)
                    .queryParam("newPreInboundNo", newPreInboundNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("loginUserId", loginUserId);

            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    WarehouseApiResponse.class);
            log.info("result : " + result.getBody());
            return result.getBody();
            //return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Inbound Order Cancellation
    public PreInboundHeaderV2 inboundOrderCancellation(InboundOrderCancelInput inboundOrderCancelInput, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(inboundOrderCancelInput, headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "invoice/inboundOrderCancellation")
                    .queryParam("loginUserId", loginUserId);

            ResponseEntity<PreInboundHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    PreInboundHeaderV2.class);
            log.info("result : " + result.getBody());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //==========================================Get All Exception Log Details==========================================
    public ExceptionLog[] getAllExceptionLogs(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "exceptionlog/all");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ExceptionLog[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ExceptionLog[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Find InBoundOrder
    public InboundOrderV2[] findInboundOrderV2(FindInboundOrderV2 findInboundOrderV2, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/findInboundOrderV2");
            HttpEntity<?> entity = new HttpEntity<>(findInboundOrderV2, headers);
            ResponseEntity<InboundOrderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundOrderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //FindInboundOrderLine
    public InboundOrderLinesV2[] findInboundOrderLinesV2(FindInboundOrderLineV2 findInboundOrderLineV2, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/findInboundOrderLinesV2");
            HttpEntity<?> entity = new HttpEntity<>(findInboundOrderLineV2, headers);
            ResponseEntity<InboundOrderLinesV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundOrderLinesV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //FindOutboundOrder
    public OutboundOrderV2[] findOutboundOrderV2(FindOutboundOrderV2 findOutboundOrderV2, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/findOutboundOrderV2");
            HttpEntity<?> entity = new HttpEntity<>(findOutboundOrderV2, headers);
            ResponseEntity<OutboundOrderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public OutboundOrderLineV2[] findOutboundOrderLineV2(FindOutboundOrderLineV2 findOutboundOrderLineV2, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/findOutboundOrderLineV2");
            HttpEntity<?> entity = new HttpEntity<>(findOutboundOrderLineV2, headers);
            ResponseEntity<OutboundOrderLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findSupplierInvoiceHeader
    public SupplierInvoiceHeader[] findSupplierInvoiceHeader(SearchSupplierInvoiceHeader searchSupplierInvoiceHeader, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "/invoice/findSupplierInvoiceHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchSupplierInvoiceHeader, headers);
            ResponseEntity<SupplierInvoiceHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, SupplierInvoiceHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param outboundOrderCancelInput
     * @param loginUserID
     * @param authToken
     * @return
     * @throws ParseException
     */
    public PreOutboundHeaderV2 orderCancellation(OutboundOrderCancelInput outboundOrderCancelInput, String loginUserID, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "preoutboundheader/v2/orderCancellation")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(outboundOrderCancelInput, headers);
            ResponseEntity<PreOutboundHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PreOutboundHeaderV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - Upload - StockAdjustment V2
    public WarehouseApiResponse createStockAdjustmentUploadV2(List<StockAdjustment> stockAdjustmentList, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/stockAdjustment/upload");
        HttpEntity<?> entity = new HttpEntity<>(stockAdjustmentList, headers);
        ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(),
                HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // -----------------------------------------Orders----------------------------------------------------------------
    // POST - SO
    public WarehouseApiResponse postASN(@Valid ASN asn, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/inbound/asn");
        HttpEntity<?> entity = new HttpEntity<>(asn, headers);
        ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST - SO
    public WarehouseApiResponse postSO(@Valid ShipmentOrder shipmenOrder, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getInboundTransactionServiceApiUrl() + "warehouse/outbound/so");
        HttpEntity<?> entity = new HttpEntity<>(shipmenOrder, headers);
        ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // GET - InboundOrder - OrderByID
    public InboundOrder getInboundOrderById(String orderId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "orders/inbound/orders/" + orderId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundOrder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                    entity, InboundOrder.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - FAILED
    public InboundIntegrationLog[] getFailedInboundOrders(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "orders/inbound/orders/failed");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundIntegrationLog[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, InboundIntegrationLog[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // GET - ReceiptConfimation-V2
    public ReceiptConfimationReport getReceiptConfimationReportV2(String asnNumber, String preInboundNo, String companyCodeId, String plantId,
                                                                  String languageId, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "reports/v2/receiptConfirmation")
                    .queryParam("asnNumber", asnNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ReceiptConfimationReport> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, ReceiptConfimationReport.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param warehouseId
     * @param authToken
     * @return
     */
    public Dashboard getDashboardCount(String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "reports/dashboard/get-count")
                    .queryParam("warehouseId", warehouseId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Dashboard> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                    Dashboard.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //===============================================CrossDock Service ==========================================//

    // Update UnallocatedOrderLine
    public CrossDockEntity crossDockGrLineConfirm(CrossDockGrLineOrderManagementLine crossDock, String authToken, String loginUserID) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/v6/crossDock")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(crossDock, headers);
            ResponseEntity<CrossDockEntity> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, CrossDockEntity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // Cross_Dock
    public CrossDock findCrossDock(MultiDbInput input, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "grline/findCrossDock");
            HttpEntity<?> entity = new HttpEntity<>(input, headers);
            ResponseEntity<CrossDock> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, CrossDock.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // PATCH
    public StagingLineUpdate[] updateExpiryMfr(List<StagingLineUpdate> lineUpdateList, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(lineUpdateList, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "stagingline/update/ExpiryMfr");
            ResponseEntity<StagingLineUpdate[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
                    StagingLineUpdate[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - SO
    public WarehouseApiResponse postASNV4(@Valid ASNV2 asnv2, String oauth) {
        HttpHeaders headers = new HttpHeaders();
        AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundOrderServiceApiUrl() + "inboundorder/inbound/asn/v2");
//                        .queryParam("companyCodeId", companyCodeId)
//                        .queryParam("plantId", plantId)
//                        .queryParam("warehouseId", warehouseId);
        HttpEntity<?> entity = new HttpEntity<>(asnv2, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    //-----------------------------------------------Notification----------------------------------------//

    //SEARCH
    public StorageBinDashBoardImpl[] getStorageBinDashBoard(StorageBinDashBoardInput storageBinDashBoardInput, String authToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "reports/storageBinDashboard");
            HttpEntity<?> entity = new HttpEntity<>(storageBinDashBoardInput, headers);
            ResponseEntity<StorageBinDashBoardImpl[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinDashBoardImpl[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //  ----------------------------------------------KNOWELL - V7-------------------------------------------------

    public PutAwayLine getinboundConfirmValidationV7(String companyCode, String plantId, String languageId, String warehouseId,
                                                     String refDocNumber, String preInboundNo, String loginUserID) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + loginUserID);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/putawayline/inboundManualConfirmation")
                .queryParam("loginUserID", loginUserID)
                .queryParam("compannyCode", companyCode)
                .queryParam("plantId", plantId)
                .queryParam("languageId", languageId)
                .queryParam("warehouseId", warehouseId)
                .queryParam("refDocNumber", refDocNumber)
                .queryParam("preInboundNo", preInboundNo);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<PutAwayLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                entity, PutAwayLine.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    public InboundHeaderEntityV2[] findInboundHeaderWithLinesV2(SearchInboundHeaderV2 searchInboundHeader, String authToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "inboundheader/findInboundHeaderWithLines/v2");
        HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
        ResponseEntity<InboundHeaderEntityV2[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeaderEntityV2[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    //---------------------------------------------Inbound Reversal------------------------------------------------------------

    public WarehouseApiResponse inboundReversal(String companyCodeId, String plantId, String warehouseId,
                                                String refDocNumber, String preInboundNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getInboundTransactionServiceApiUrl() + "/reports/inboundReversal")
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("preInboundNo", preInboundNo);

            ResponseEntity<WarehouseApiResponse> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //=========================================Update=========================================================

    /**
     * @param orderId
     * @param updateInboundOrder
     * @param authToken
     * @return
     */
    public InboundOrder updateInboundOrder(String orderId, UpdateInboundOrder updateInboundOrder, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateInboundOrder, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/update/inbound")
                    .queryParam("orderId", orderId);

            ResponseEntity<InboundOrder> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, InboundOrder.class);
//			log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //==================================================Delete=========================================================

    /**
     * @param orderId
     * @param authToken
     * @return
     */
    public boolean deleteInboundOrder(String orderId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getInboundTransactionServiceApiUrl() + "/orders/delete/inbound")
                    .queryParam("orderId", orderId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class);
//			log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
