package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.mfg.Process;
import com.tekclover.wms.core.model.mfg.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MfgService {

    @Autowired
    PropertiesConfig propertiesConfig;

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
    private String getMfgServiceApiUrl() {
        return propertiesConfig.getMfgServiceUrl();
    }


    //--------------------------------------------MasterOperation------------------------------------------------------------------------
    // GET
    public MasterOperation getMasterOperation(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber,
                                              String phaseNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/" + operationNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("phaseNumber", phaseNumber)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<MasterOperation> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MasterOperation.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public MasterOperation[] getMasterOperation(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String operationNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/v2/" + operationNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<MasterOperation[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MasterOperation[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public MasterOperation[] createMasterOperation(List<MasterOperation> newMasterOperation, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newMasterOperation, headers);
        ResponseEntity<MasterOperation[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterOperation[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public MasterOperation[] updateMasterOperation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String loginUserID, List<MasterOperation> updateMasterOperation, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateMasterOperation, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/batch")
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<MasterOperation[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MasterOperation[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteMasterOperation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String operationNumber, String phaseNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/" + operationNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("phaseNumber", phaseNumber)
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
    public MasterOperation[] findMasterOperation(SearchMasterOperation searchMasterOperation, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masteroperation/findMasterOperation");
            HttpEntity<?> entity = new HttpEntity<>(searchMasterOperation, headers);
            ResponseEntity<MasterOperation[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterOperation[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------MasterReceipe------------------------------------------------------------------------
    // GET
    public MasterReceipe getMasterReceipe(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String operationNumber, String receipeId, String itemCode, String bomNumber,
                                          String phaseNumber, String childItemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/" + receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("phaseNumber", phaseNumber)
                            .queryParam("childItemCode", childItemCode)
                            .queryParam("itemCode", itemCode)
                            .queryParam("bomNumber", bomNumber)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<MasterReceipe> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MasterReceipe.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public MasterReceipe[] getMasterReceipe(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String operationNumber, String receipeId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/v2/" + receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<MasterReceipe[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MasterReceipe[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public MasterReceipe[] createMasterReceipe(List<MasterReceipe> newMasterReceipe, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newMasterReceipe, headers);
        ResponseEntity<MasterReceipe[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterReceipe[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public MasterReceipe[] updateMasterReceipe(String companyCodeId, String plantId, String languageId, String warehouseId,
                                               String loginUserID, List<MasterReceipe> modifyMasterReceipe, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyMasterReceipe, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/batch")
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<MasterReceipe[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MasterReceipe[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteMasterReceipe(String companyCodeId, String plantId, String languageId, String warehouseId,
                                       String operationNumber, String receipeId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/" + receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
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
    public MasterReceipe[] findMasterReceipe(SearchMasterReceipe searchMasterReceipe, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterreceipe/findMasterReceipe");
            HttpEntity<?> entity = new HttpEntity<>(searchMasterReceipe, headers);
            ResponseEntity<MasterReceipe[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterReceipe[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------InboundQualityHeader------------------------------------------------------------------------
    // GET
    public InboundQualityHeader getInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                        String preInboundNo, String inboundQualityNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader/" + inboundQualityNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("itemCode", itemCode)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundQualityHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundQualityHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public InboundQualityHeader[] createInboundQualityHeader(List<InboundQualityHeader> newInboundQualityHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newInboundQualityHeader, headers);
        ResponseEntity<InboundQualityHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundQualityHeader[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InboundQualityHeader[] updateInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String refDocNumber, String preInboundNo, String inboundQualityNumber,
                                                             String loginUserID, List<InboundQualityHeader> modifyInboundQualityHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyInboundQualityHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader/batch")
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("inboundQualityNumber", inboundQualityNumber)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<InboundQualityHeader[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundQualityHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                              String preInboundNo, String inboundQualityNumber, String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader/" + inboundQualityNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("itemCode", itemCode)
                            .queryParam("preInboundNo", preInboundNo)
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
    public InboundQualityHeader[] findInboundQualityHeader(SearchInboundQualityHeader searchInboundQualityHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader/findInboundQualityHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchInboundQualityHeader, headers);
            ResponseEntity<InboundQualityHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundQualityHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //--------------------------------------------InboundQualityLine------------------------------------------------------------------------
    // GET ALL
    public InboundQualityLine[] getInboundQualityLines(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                       String preInboundNo, String inboundQualityNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline/v2/" + inboundQualityNumber)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundQualityLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundQualityLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public InboundQualityLine getInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                    String preInboundNo, String inboundQualityNumber, Long lineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline/" + inboundQualityNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("preInboundNo", preInboundNo)
                            .queryParam("lineNo", lineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InboundQualityLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundQualityLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public InboundQualityLine[] createInboundQualityLine(List<InboundQualityLine> addInboundQualityLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(addInboundQualityLine, headers);
        ResponseEntity<InboundQualityLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundQualityLine[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public InboundQualityLine[] updateInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String refDocNumber, String preInboundNo, String inboundQualityNumber,
                                                         String loginUserID, List<InboundQualityLine> modifyInboundQualityLines, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyInboundQualityLines, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline/" + inboundQualityNumber)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("refDocNumber", refDocNumber)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("preInboundNo", preInboundNo)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<InboundQualityLine[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundQualityLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteInboundQualityLine(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                            String preInboundNo, String inboundQualityNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline/" + inboundQualityNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("refDocNumber", refDocNumber)
                            .queryParam("preInboundNo", preInboundNo)
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
    public InboundQualityLine[] findInboundQualityLine(SearchInboundQualityLine searchInboundQualityLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityline/findInboundQualityLine");
            HttpEntity<?> entity = new HttpEntity<>(searchInboundQualityLine, headers);
            ResponseEntity<InboundQualityLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundQualityLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //--------------------------------------------OperationHeader------------------------------------------------------------------------
    // GET
    public OperationHeader getOperationHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationheader/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OperationHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OperationHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public OperationHeader[] createOperationHeader(List<OperationHeader> newOperationHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationheader/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newOperationHeader, headers);
        ResponseEntity<OperationHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationHeader[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public OperationHeader[] updateOperationHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String loginUserID, List<OperationHeader> modifyOperationHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyOperationHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationheader/batch")
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<OperationHeader[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OperationHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteOperationHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationheader/" + productionOrderNo)
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
    public OperationHeader[] findOperationHeader(SearchOperationHeader searchOperationHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationheader/findOperationHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationHeader, headers);
            ResponseEntity<OperationHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------OperationHeader------------------------------------------------------------------------
    // GET
    public ProductionOrderOutput getProductionOrder(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ProductionOrderOutput> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProductionOrderOutput.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ProductionOrder getProductionOrder(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/v2/" + productionOrderNo)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ProductionOrder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProductionOrder.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public ProductionOrderOutput[] createProductionOrder(List<ProductionOrder> newProductionOrder, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newProductionOrder, headers);
        ResponseEntity<ProductionOrderOutput[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProductionOrderOutput[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST
    public Boolean createSFGProductionOrder(List<ProductionOrder> newProductionOrder, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/sfgProductionOrder")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newProductionOrder, headers);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            log.info("result : " + result.getStatusCode());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    // POST
    public ProductionOrderOutput[] updateProductionOrder(List<ProductionOrder> newProductionOrder, String companyCodeId, String plantId,
                                                         String languageId, String warehouseId, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/patch")
                .queryParam("warehouseId", warehouseId)
                .queryParam("companyCodeId", companyCodeId)
                .queryParam("languageId", languageId)
                .queryParam("plantId", plantId)
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newProductionOrder, headers);
        ResponseEntity<ProductionOrderOutput[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.PATCH, entity, ProductionOrderOutput[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // DELETE
    public boolean deleteProductionOrder(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/" + productionOrderNo)
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
    public ProductionOrderOutput[] findProductionOrder(SearchOperationHeader searchOperationHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "productionheader/findProductionOrder");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationHeader, headers);
            ResponseEntity<ProductionOrderOutput[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProductionOrderOutput[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------OperationConsumption------------------------------------------------------------------------
    // GET
    public OperationConsumption getOperationConsumption(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OperationConsumption> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OperationConsumption.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ALL
    public OperationConsumption[] getOperationConsumptions(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OperationConsumption[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OperationConsumption[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public OperationConsumption[] createOperationConsumption(List<OperationConsumption> newOperationConsumptions, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newOperationConsumptions, headers);
        ResponseEntity<OperationConsumption[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationConsumption[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public OperationConsumption[] updateOperationConsumption(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                                             String loginUserID, List<OperationConsumption> modifyOperationConsumptions, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyOperationConsumptions, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<OperationConsumption[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OperationConsumption[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteOperationConsumption(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/" + productionOrderNo)
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
    public OperationConsumption[] findOperationConsumption(SearchOperationConsumption searchOperationConsumption, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/findOperationConsumption");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationConsumption, headers);
            ResponseEntity<OperationConsumption[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationConsumption[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------OperationLine------------------------------------------------------------------------
    // GET
    public OperationLine getOperationLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OperationLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OperationLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ALL
    public OperationLine[] getOperationLines(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OperationLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OperationLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public OperationLine[] createOperationLine(List<OperationLine> newOperationLines, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newOperationLines, headers);
        ResponseEntity<OperationLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationLine[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public OperationLine[] updateOperationLine(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                               String loginUserID, List<OperationLine> modifyOperationLines, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyOperationLines, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<OperationLine[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OperationLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteOperationLine(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline/" + productionOrderNo)
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
    public OperationLine[] findOperationLine(SearchOperationLine searchOperationLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationline/findOperationLine");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationLine, headers);
            ResponseEntity<OperationLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------------SORTING---------------------------------------------------------
    // GET SORTING
    public Sorting getSorting(String companyCodeId, String plantId, String languageId, String warehouseId,
                              String productionOrderNo, Long productionOrderLineNo, String receipeId,
                              String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Sorting> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Sorting.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL SORTING
    public Sorting[] getBulkSorting(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Sorting[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Sorting[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST SORTING
    public Sorting[] createSorting(List<Sorting> newSorting, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newSorting, headers);
        ResponseEntity<Sorting[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Sorting[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH SORTING
    public Sorting[] updateSorting(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                   String loginUserID, List<Sorting> modifySorting, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifySorting, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Sorting[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Sorting[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteSorting(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting/" + productionOrderNo)
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
    public Sorting[] findSorting(SearchSorting searchSorting, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sorting/findSorting");
            HttpEntity<?> entity = new HttpEntity<>(searchSorting, headers);
            ResponseEntity<Sorting[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Sorting[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------Soaking------------------------------------------------------------

    // GET SOAKING
    public Soaking getSoaking(String companyCodeId, String plantId, String languageId, String warehouseId,
                              String productionOrderNo, Long productionOrderLineNo, String receipeId,
                              String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Soaking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Soaking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL SOAKING
    public Soaking[] getBulkSoaking(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Soaking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Soaking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST SOAKING
    public Soaking[] createSoaking(List<Soaking> newSoaking, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newSoaking, headers);
        ResponseEntity<Soaking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Soaking[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH SOAKING
    public Soaking[] updateSoaking(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                   String loginUserID, List<Soaking> modifySoaking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifySoaking, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Soaking[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Soaking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE SOAKING
    public boolean deleteSoaking(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking/" + productionOrderNo)
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

    //SEARCH SOAKING
    public Soaking[] findSoaking(SearchSoaking searchSoaking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "soaking/findSoaking");
            HttpEntity<?> entity = new HttpEntity<>(searchSoaking, headers);
            ResponseEntity<Soaking[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Soaking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------Peeling------------------------------------------------------------

    // GET PEELING
    public Peeling getPeeling(String companyCodeId, String plantId, String languageId, String warehouseId,
                              String productionOrderNo, Long productionOrderLineNo, String receipeId,
                              String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Peeling> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Peeling.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL PEELING
    public Peeling[] getBulkPeeling(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Peeling[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Peeling[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST PEELING
    public Peeling[] createPeeling(List<Peeling> newPeeling, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPeeling, headers);
        ResponseEntity<Peeling[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Peeling[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH PEELING
    public Peeling[] updatePeeling(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                   String loginUserID, List<Peeling> modifyPeeling, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyPeeling, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Peeling[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Peeling[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE PEELING
    public boolean deletePeeling(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling/" + productionOrderNo)
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

    //SEARCH PEELING
    public Peeling[] findPeeling(SearchPeeling searchPeeling, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "peeling/findPeeling");
            HttpEntity<?> entity = new HttpEntity<>(searchPeeling, headers);
            ResponseEntity<Peeling[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Peeling[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------GingerPaste------------------------------------------------------------

    // GET GINGERPASTE
    public GingerPaste getGingerPaste(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String productionOrderNo, Long productionOrderLineNo, String receipeId,
                                      String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GingerPaste> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GingerPaste.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL GINGERPASTE
    public GingerPaste[] getBulkGingerPaste(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GingerPaste[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GingerPaste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST GINGERPASTE
    public GingerPaste[] createGingerPaste(List<GingerPaste> newGingerPaste, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newGingerPaste, headers);
        ResponseEntity<GingerPaste[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GingerPaste[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH GINGERPASTE
    public GingerPaste[] updateGingerPaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                           String loginUserID, List<GingerPaste> modifyGingerPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyGingerPaste, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<GingerPaste[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, GingerPaste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE GINGERPASTE
    public boolean deleteGingerPaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste/" + productionOrderNo)
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

    //SEARCH GINGERPASTE
    public GingerPaste[] findGingerPaste(SearchGingerPaste searchGingerPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "gingerPaste/findGingerPaste");
            HttpEntity<?> entity = new HttpEntity<>(searchGingerPaste, headers);
            ResponseEntity<GingerPaste[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GingerPaste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------Paste------------------------------------------------------------

    // GET PASTE
    public Paste getPaste(String companyCodeId, String plantId, String languageId, String warehouseId,
                          String productionOrderNo, Long productionOrderLineNo, String receipeId,
                          String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Paste> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Paste.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL PASTE
    public Paste[] getBulkPaste(String companyCodeId, String plantId, String languageId, String warehouseId,
                                String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Paste[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Paste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST PASTE
    public Paste[] createPaste(List<Paste> newPaste, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPaste, headers);
        ResponseEntity<Paste[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Paste[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH PASTE
    public Paste[] updatePaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                               String loginUserID, List<Paste> modifyPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyPaste, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Paste[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Paste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE PASTE
    public boolean deletePaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste/" + productionOrderNo)
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

    //SEARCH PASTE
    public Paste[] findPaste(SearchPaste searchPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "paste/findPaste");
            HttpEntity<?> entity = new HttpEntity<>(searchPaste, headers);
            ResponseEntity<Paste[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Paste[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------FgReceiving------------------------------------------------------------

    // GET FGRECEIVING
    public FgReceiving getFgReceiving(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String productionOrderNo, Long productionOrderLineNo, String receipeId,
                                      String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<FgReceiving> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FgReceiving.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL FGRECEIVING
    public FgReceiving[] getBulkFgReceiving(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<FgReceiving[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FgReceiving[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST FGRECEIVING
    public FgReceiving[] createFgReceiving(List<FgReceiving> newFgReceiving, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newFgReceiving, headers);
        ResponseEntity<FgReceiving[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgReceiving[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH FGRECEIVING
    public FgReceiving[] updateFgReceiving(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                           String loginUserID, List<FgReceiving> modifyFgReceiving, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyFgReceiving, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<FgReceiving[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, FgReceiving[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE FGRECEIVING
    public boolean deleteFgReceiving(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving/" + productionOrderNo)
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

    //SEARCH FGRECEIVING
    public FgReceiving[] findFgReceiving(SearchFgReceiving searchFgReceiving, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgReceiving/findFgReceiving");
            HttpEntity<?> entity = new HttpEntity<>(searchFgReceiving, headers);
            ResponseEntity<FgReceiving[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgReceiving[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //-----------------------------------------------FgPacking------------------------------------------------------------

    // GET FG PACKING
    public FgPacking getFgPacking(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String productionOrderNo, Long productionOrderLineNo, String receipeId,
                                  String operationNumber, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("receipeId", receipeId)
                            .queryParam("plantId", plantId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("itemCode", itemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<FgPacking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FgPacking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //GET ALL FG PACKING
    public FgPacking[] getBulkFgPacking(String companyCodeId, String plantId, String languageId, String warehouseId,
                                        String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<FgPacking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FgPacking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST FG PACKING
    public FgPacking[] createFgPacking(List<FgPacking> newFgPacking, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newFgPacking, headers);
        ResponseEntity<FgPacking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgPacking[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    // PATCH FG PACKING
    public FgPacking[] updateFgPacking(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                                       String loginUserID, List<FgPacking> modifyFgReceiving, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyFgReceiving, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking/" + productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<FgPacking[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, FgPacking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE FG PACKING
    public boolean deleteFgPacking(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking/" + productionOrderNo)
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

    //SEARCH FG PACKING
    public FgPacking[] findFgPacking(SearchFgPacking searchFgPacking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgPacking/findFgPacking");
            HttpEntity<?> entity = new HttpEntity<>(searchFgPacking, headers);
            ResponseEntity<FgPacking[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgPacking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------Powder------------------------------------------------------------------------
    // GET
    public Powder getPowder(String companyCodeId, String plantId, String languageId, String warehouseId,
                            String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "powder/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Powder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Powder.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public Powder[] createPowder(List<Powder> newPowder, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "powder/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPowder, headers);
        ResponseEntity<Powder[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Powder[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public Powder[] updatePowder(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                 String loginUserID, List<Powder> modifyPowder, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyPowder, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "powder/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Powder[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Powder[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePowder(String companyCodeId, String plantId, String languageId, String warehouseId,
                                String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "powder/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public Powder[] findPowder(SearchPowder searchPowder, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "powder/findPowder");
            HttpEntity<?> entity = new HttpEntity<>(searchPowder, headers);
            ResponseEntity<Powder[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Powder[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //--------------------------------------------DiceSliceChop------------------------------------------------------------------------
    // GET
    public DiceSliceChop getDiceSliceChop(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "diceSliceChop/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<DiceSliceChop> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DiceSliceChop.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public DiceSliceChop[] createDiceSliceChop(List<DiceSliceChop> newDiceSliceChop, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "diceSliceChop/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newDiceSliceChop, headers);
        ResponseEntity<DiceSliceChop[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DiceSliceChop[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public DiceSliceChop[] updateDiceSliceChop(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                               String loginUserID, List<DiceSliceChop> modifyDiceSliceChop, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyDiceSliceChop, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "diceSliceChop/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<DiceSliceChop[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DiceSliceChop[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteDiceSliceChop(String companyCodeId, String plantId, String languageId, String warehouseId,
                                       String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "diceSliceChop/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public DiceSliceChop[] findDiceSliceChop(SearchDiceSliceChop searchDiceSliceChop, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "diceSliceChop/findDiceSliceChop");
            HttpEntity<?> entity = new HttpEntity<>(searchDiceSliceChop, headers);
            ResponseEntity<DiceSliceChop[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DiceSliceChop[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------SFGQI------------------------------------------------------------------------
    // GET
    public SFGQI getSFGQI(String companyCodeId, String plantId, String languageId, String warehouseId,
                          String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sfgqi/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<SFGQI> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SFGQI.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public SFGQI[] createSFGQI(List<SFGQI> newSFGQI, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sfgqi/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newSFGQI, headers);
        ResponseEntity<SFGQI[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SFGQI[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public SFGQI[] updateSFGQI(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                               String loginUserID, List<SFGQI> modifySFGQI, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifySFGQI, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sfgqi/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<SFGQI[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SFGQI[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteSFGQI(String companyCodeId, String plantId, String languageId, String warehouseId,
                               String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                               String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sfgqi/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public SFGQI[] findSFGQI(SearchSFGQI searchSFGQI, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "sfgqi/findSFGQI");
            HttpEntity<?> entity = new HttpEntity<>(searchSFGQI, headers);
            ResponseEntity<SFGQI[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SFGQI[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------Cooking------------------------------------------------------------------------
    // GET
    public Cooking getCooking(String companyCodeId, String plantId, String languageId, String warehouseId,
                              String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "cooking/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Cooking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Cooking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public Cooking[] createCooking(List<Cooking> newCooking, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "cooking/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newCooking, headers);
        ResponseEntity<Cooking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Cooking[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public Cooking[] updateCooking(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                   String loginUserID, List<Cooking> modifyCooking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyCooking, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "cooking/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<Cooking[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Cooking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteCooking(String companyCodeId, String plantId, String languageId, String warehouseId,
                                 String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "cooking/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public Cooking[] findCooking(SearchCooking searchCooking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "cooking/findCooking");
            HttpEntity<?> entity = new HttpEntity<>(searchCooking, headers);
            ResponseEntity<Cooking[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Cooking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------PackingQc------------------------------------------------------------------------
    // GET
    public PackingQc getPackingQc(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "packingQc/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<PackingQc> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackingQc.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public PackingQc[] createPackingQc(List<PackingQc> newPackingQc, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "packingQc/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newPackingQc, headers);
        ResponseEntity<PackingQc[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PackingQc[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public PackingQc[] updatePackingQc(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                       String loginUserID, List<PackingQc> modifyPackingQc, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyPackingQc, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "packingQc/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<PackingQc[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PackingQc[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePackingQc(String companyCodeId, String plantId, String languageId, String warehouseId,
                                   String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                   String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "packingQc/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public PackingQc[] findPackingQc(SearchPackingQc searchPackingQc, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "packingQc/findPackingQc");
            HttpEntity<?> entity = new HttpEntity<>(searchPackingQc, headers);
            ResponseEntity<PackingQc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PackingQc[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------FgDelivery------------------------------------------------------------------------
    // GET
    public FgDelivery getFgDelivery(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgDelivery/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<FgDelivery> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FgDelivery.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public FgDelivery[] createFgDelivery(List<FgDelivery> newFgDelivery, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgDelivery/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newFgDelivery, headers);
        ResponseEntity<FgDelivery[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgDelivery[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public FgDelivery[] updateFgDelivery(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                         String loginUserID, List<FgDelivery> modifyFgDelivery, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyFgDelivery, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgDelivery/batch")
                    .queryParam("receipeId", receipeId)
                    .queryParam("operationNumber", operationNumber)
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("productionOrderLineNo", productionOrderLineNo)
                    .queryParam("itemCode", itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<FgDelivery[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, FgDelivery[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteFgDelivery(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String operationNumber, String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                                    String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgDelivery/")
                            .queryParam("receipeId", receipeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
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

    //SEARCH
    public FgDelivery[] findFgDelivery(SearchFgDelivery searchFgDelivery, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "fgDelivery/findFgDelivery");
            HttpEntity<?> entity = new HttpEntity<>(searchFgDelivery, headers);
            ResponseEntity<FgDelivery[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FgDelivery[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------OrderConfirmation------------------------------------------------------------------------

    //Update BlackDal
    public void updateBlackDal(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                               String loginUserID, BlackDal modifyBlackDal, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyBlackDal, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/blackDal")
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Update BlackDal
    public void createBlackDal(String loginUserID, BlackDal blackDal, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(blackDal, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/blackDal/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Get BlackDal
    public BlackDal getBlackDal(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/blackDal/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<BlackDal> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BlackDal.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Update ChanaDal
    public void updateChanaDal(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo,
                               String loginUserID, ChanaDal modifyChanaDal, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifyChanaDal, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/")
                    .queryParam("productionOrderNo", productionOrderNo)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Update ChanaDal
    public void createChanaDal(String loginUserID, ChanaDal chanaDal, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(chanaDal, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/chanaDal/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get ChanaDal
    public ChanaDal getChanaDal(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/chanaDal/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ChanaDal> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ChanaDal.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Update ChitraRajma
    public void createChitraRajma(String loginUserID, ChitraRajma chitraRajma, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(chitraRajma, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/chitraRajma/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get ChanaDal
    public ChitraRajma getChitraRajma(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/chitraRajma/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ChitraRajma> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ChitraRajma.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create TomatoPuree
    public void createTomatoPuree(String loginUserID, TomatoPuree tomatoPuree, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(tomatoPuree, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/tomatoPuree/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get TomatoPuree
    public TomatoPuree getTomatoPuree(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/tomatoPuree/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<TomatoPuree> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, TomatoPuree.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create GingerPaste
    public void createSfgGingerPaste(String loginUserID, SfgGingerPaste sfgGingerPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(sfgGingerPaste, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/sfgGingerPaste/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get GingerPaste
    public SfgGingerPaste getSfgGingerPaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/sfgGingerPaste/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<SfgGingerPaste> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SfgGingerPaste.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create GarlicPaste
    public void createGarlicPaste(String loginUserID, GarlicPaste garlicPaste, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(garlicPaste, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/garlicPaste/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get GarlicPaste
    public GarlicPaste getGarlicPaste(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/garlicPaste/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GarlicPaste> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GarlicPaste.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create GarlicChop
    public void createGarlicChop(String loginUserID, GarlicChop garlicChop, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(garlicChop, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/garlicChop/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get GarlicChop
    public GarlicChop getGarlicChop(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/garlicChop/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<GarlicChop> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GarlicChop.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create RoastedKasturiMethi
    public void createRoastedKasturiMethi(String loginUserID, RoastedKasturiMethi roastedKasturiMethi, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(roastedKasturiMethi, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/roastedKasturiMethi/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get RoastedKasturiMethi
    public RoastedKasturiMethi getRoastedKasturiMethi(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/roastedKasturiMethi/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<RoastedKasturiMethi> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RoastedKasturiMethi.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create RoastedCuminPowder
    public void createRoastedCuminPowder(String loginUserID, RoastedCuminPowder roastedCuminPowder, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(roastedCuminPowder, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/roastedCuminPowder/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get RoastedCuminPowder
    public RoastedCuminPowder getRoastedCuminPowder(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/roastedCuminPowder/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<RoastedCuminPowder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RoastedCuminPowder.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Create OrderConfirmation
    public void createOrderConfirmation(String loginUserID, OrderConfirmation orderConfirmation, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(orderConfirmation, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/create")
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<String[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String[].class);
            log.info("result : " + result.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get OrderConfirmation
    public OrderConfirmation getOrderConfirmation(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "orderconfirmation/get")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("productionOrderNo", productionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OrderConfirmation> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OrderConfirmation.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //--------------------------------------OrderLineReport-------------------------------------

    public OperationLineReport[] findOperationLineReport(SearchOperationLineReport searchOperationLineReport, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationLineReport/findOperationLineReport");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationLineReport, headers);
            ResponseEntity<OperationLineReport[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationLineReport[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public OperationLineReport[] findOperationLineV2Report(SearchOperationLineReport searchOperationLineReport, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationLineReport/findOperationLineV2Report");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationLineReport, headers);
            ResponseEntity<OperationLineReport[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OperationLineReport[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public LineReportProcess findOperationLineProcessReport(SearchOperationLineReportProcess searchOperationLineReport, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationLineReport/findOperationLineProcessReport");
            HttpEntity<?> entity = new HttpEntity<>(searchOperationLineReport, headers);
            ResponseEntity<LineReportProcess> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, LineReportProcess.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------Process------------------------------------------------------------------------
    // GET
    public Process[] getProcess(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/v2/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Process[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Process[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Process[] getProcess(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/batch/" + productionOrderNo)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Process[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Process[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Process getProcess(String companyCodeId, String plantId, String languageId, String warehouseId, String operationNumber,
                              String receipeId, String productionOrderNo, Long productionOrderLineNo, String itemCode,
                              String phaseNumber, String bomItem, String batchNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/" + productionOrderNo)
                            .queryParam("operationNumber", operationNumber)
                            .queryParam("receipeId", receipeId)
                            .queryParam("productionOrderLineNo", productionOrderLineNo)
                            .queryParam("itemCode", itemCode)
                            .queryParam("phaseNumber", phaseNumber)
                            .queryParam("bomItem", bomItem)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Process> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Process.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public Process[] createProcess(List<Process> newProcess, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/create")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newProcess, headers);
        ResponseEntity<Process[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Process[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // POST
    public Process[] updateProcess(List<Process> updateProcess, String companyCodeId, String plantId, String languageId,
                                   String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/" + productionOrderNo)
                .queryParam("warehouseId", warehouseId)
                .queryParam("companyCodeId", companyCodeId)
                .queryParam("languageId", languageId)
                .queryParam("plantId", plantId)
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(updateProcess, headers);
        ResponseEntity<Process[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.PATCH, entity, Process[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    public Process[] findProcess(SearchProcess searchProcess, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/findProcess");
            HttpEntity<?> entity = new HttpEntity<>(searchProcess, headers);
            ResponseEntity<Process[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Process[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public void deleteProcess(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/" + productionOrderNo)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE by batch
    public boolean deleteProcess(String companyCodeId, String plantId, String languageId, String warehouseId, String productionOrderNo, String batchNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "process/batch/" + productionOrderNo)
                            .queryParam("batchNumber", batchNumber)
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

    //--------------------------------------------MasterPhase------------------------------------------------------------------------
    // GET
    public MasterPhase getMasterPhase(String companyCodeId, String plantId, String languageId,
                                      String warehouseId, String phaseNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterphase/" + phaseNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<MasterPhase> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MasterPhase.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public MasterPhase[] createMasterPhase(List<MasterPhase> newMasterPhase, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterphase/batch")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newMasterPhase, headers);
        ResponseEntity<MasterPhase[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterPhase[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public MasterPhase[] updateMasterPhase(String companyCodeId, String plantId, String languageId, String warehouseId,
                                           String loginUserID, List<MasterPhase> updateMasterPhase, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateMasterPhase, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterphase/batch")
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<MasterPhase[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MasterPhase[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteMasterPhase(String companyCodeId, String plantId, String languageId, String warehouseId,
                                     String phaseNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterphase/" + phaseNumber)
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
    public MasterPhase[] findMasterPhase(SearchMasterPhase searchMasterPhase, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "masterphase/findMasterPhase");
            HttpEntity<?> entity = new HttpEntity<>(searchMasterPhase, headers);
            ResponseEntity<MasterPhase[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MasterPhase[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}