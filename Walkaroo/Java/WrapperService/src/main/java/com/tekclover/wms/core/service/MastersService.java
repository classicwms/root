package com.tekclover.wms.core.service;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.tekclover.wms.core.model.masters.*;
import com.tekclover.wms.core.model.threepl.*;
import com.tekclover.wms.core.model.transaction.ImPartnerInput;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.mastersorder.Customer;
import com.tekclover.wms.core.model.warehouse.mastersorder.ImBasicData1V2;
import com.tekclover.wms.core.model.warehouse.mastersorder.Item;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.transaction.PaginatedResponse;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@Slf4j
@Service
public class MastersService {

    @Autowired
    PropertiesConfig propertiesConfig;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getMastersServiceUrl() {
        return propertiesConfig.getMastersServiceUrl();
    }

    public boolean validateUser(String name, String password, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "login")
                    .queryParam("name", name)
                    .queryParam("password", password);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            log.info("result : " + result.getStatusCode());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---BomHeader---------------------------------------------------------------*/
    // GET ALL
    public BomHeader[] getBomHeaders(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<BomHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public BomHeader getBomHeader(String warehouseId, String parentItemCode, String languageId,
                                  String companyCode, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("parentItemCode", parentItemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<BomHeader> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findBomHeader
    public BomHeader[] findBomHeader(SearchBomHeader searchBomHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/findBomHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchBomHeader, headers);
            ResponseEntity<BomHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BomHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public BomHeader createBomHeader(BomHeader newBomHeader, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader")
                        .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newBomHeader, headers);
        ResponseEntity<BomHeader> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BomHeader.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public BomHeader updateBomHeader(String warehouseId, String parentItemCode, String languageId,
                                     String companyCode, String plantId, String loginUserID,
                                     BomHeader modifiedBomHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "'s RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBomHeader, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("parentItemCode", parentItemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<BomHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BomHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteBomHeader(String warehouseId, String parentItemCode, String languageId,
                                   String companyCode, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "'s RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("parentItemCode", parentItemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---BomLine-----------------------------------------------------------------------------*/

    // GET ALL
    public BomLine[] getBomLines(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<BomLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public BomLine getBomLine(Long bomNumber, String warehouseId, String childItemCode, String languageId,
                              String companyCode, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("childItemCode", childItemCode);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<BomLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST
    public BomLine createBomLine(BomLine newBomLine, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", " RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newBomLine, headers);
        ResponseEntity<BomLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BomLine.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // PATCH
    public BomLine updateBomLine(String warehouseId, String companyCode, String languageId,
                                 String plantId, Long bomNumber, String childItemCode,
                                 String loginUserID, BomLine modifiedBomLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "'s RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBomLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("bomNumber", bomNumber)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("companyCode", companyCode)
                    .queryParam("childItemCode", childItemCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<BomLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BomLine.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH - BatchBomLines
    public BomLine[] updateBomLines(String companyCode, String plantId, String languageId, String warehouseId,
                                    Long bomNumber, String loginUserID, List<BomLine> modifiedBomLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBomLine, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/batch/" + bomNumber)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("bomNumber", bomNumber)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("companyCode", companyCode)
                    .queryParam("loginUserID", loginUserID);

            ResponseEntity<BomLine[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BomLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteBomLine(Long bomNumber, String warehouseId, String languageId, String companyCode,
                                 String plantId, String childItemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "'s RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("childItemCode", childItemCode)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findBomLine
    public BomLine[] findBomLine(SearchBomLine searchBomLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/findBomLine");
            HttpEntity<?> entity = new HttpEntity<>(searchBomLine, headers);
            ResponseEntity<BomLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BomLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---BUSINESSPARTNER---------------------------------------------------------------*/
    // Get ALL
    public BusinessPartner[] getBusinessPartners(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner");
            ResponseEntity<BusinessPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET BusinessPartner
    public BusinessPartnerV2 getBusinessPartner(String partnerCode, String companyCodeId, String plantId,
                                              String warehouseId, String languageId, Long businessPartnerType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/v2/" + partnerCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("languageId", languageId)
                    .queryParam("businessPartnerType", businessPartnerType);
            ResponseEntity<BusinessPartnerV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartnerV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findBusinessPartner
    public BusinessPartner[] findBusinessPartner(SearchBusinessPartner searchBusinessPartner, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/findBusinessPartner");
            HttpEntity<?> entity = new HttpEntity<>(searchBusinessPartner, headers);
            ResponseEntity<BusinessPartner[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BusinessPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST BusinessPartner
    public BusinessPartnerV2 addBusinessPartner(BusinessPartnerV2 businesspartner, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/v2")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(businesspartner, headers);
            ResponseEntity<BusinessPartnerV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BusinessPartnerV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch BusinessPartner
    public BusinessPartner updateBusinessPartner(String partnerCode, String companyCodeId, String plantId,
                                                 String warehouseId, String languageId, Long businessPartnerType,
                                                 BusinessPartner modifiedBusinessPartner,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBusinessPartner, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/" + partnerCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("businessPartnerType", businessPartnerType);

            ResponseEntity<BusinessPartner> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BusinessPartner.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch BusinessPartner
    public BusinessPartnerV2 updateBusinessPartnerV2(String partnerCode, String companyCodeId, String plantId,
                                                 String warehouseId, String languageId, Long businessPartnerType,
                                                 BusinessPartnerV2 modifiedBusinessPartner,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBusinessPartner, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/v2/" + partnerCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("businessPartnerType", businessPartnerType);

            ResponseEntity<BusinessPartnerV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BusinessPartnerV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete BusinessPartner
    public boolean deleteBusinessPartner(String partnerCode, String companyCodeId, String plantId,
                                         String warehouseId, String languageId, Long businessPartnerType,
                                         String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/" + partnerCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("businessPartnerType", businessPartnerType);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---HANDLINGEQUIPMENT---------------------------------------------------------------*/
    // Get ALL
    public HandlingEquipment[] getHandlingEquipments(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment");
            ResponseEntity<HandlingEquipment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET HandlingEquipment
    public HandlingEquipment getHandlingEquipment(String warehouseId, String handlingEquipmentId, String companyCodeId,
                                                  String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId);
            ResponseEntity<HandlingEquipment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public HandlingEquipment getHandlingEquipmentV2(String warehouseId, String heBarcode, String companyCodeId,
                                                  String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + heBarcode + "/v2/barCode")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<HandlingEquipment> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public HandlingEquipment getHandlingEquipment(String warehouseId, String heBarcode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + heBarcode + "/barCode")
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<HandlingEquipment> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findHandlingEquipment
    public HandlingEquipment[] findHandlingEquipment(SearchHandlingEquipment searchHandlingEquipment, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/findHandlingEquipment");
            HttpEntity<?> entity = new HttpEntity<>(searchHandlingEquipment, headers);
            ResponseEntity<HandlingEquipment[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingEquipment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST HandlingEquipment
    public HandlingEquipment addHandlingEquipment(HandlingEquipment handlingequipment, String loginUserID,
                                                  String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment")
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(handlingequipment, headers);
            ResponseEntity<HandlingEquipment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingEquipment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch HandlingEquipment
    public HandlingEquipment updateHandlingEquipment(String warehouseId, String handlingEquipmentId, String companyCodeId,
                                                     String languageId, String plantId,
                                                     HandlingEquipment modifiedHandlingEquipment,
                                                     String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedHandlingEquipment, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            ResponseEntity<HandlingEquipment> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingEquipment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete HandlingEquipment
    public boolean deleteHandlingEquipment(String warehouseId, String handlingEquipmentId,
                                           String companyCodeId, String languageId, String plantId,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---HANDLINGUNIT---------------------------------------------------------------*/
    // Get ALL
    public HandlingUnit[] getHandlingUnits(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit");
            ResponseEntity<HandlingUnit[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnit[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET HandlingUnit
    public HandlingUnit getHandlingUnit(String handlingUnit, String companyCodeId, String plantId,
                                        String warehouseId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<HandlingUnit> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnit.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findHandlingUnit
    public HandlingUnit[] findHandlingUnit(SearchHandlingUnit searchHandlingUnit, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/findHandlingUnit");
            HttpEntity<?> entity = new HttpEntity<>(searchHandlingUnit, headers);
            ResponseEntity<HandlingUnit[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingUnit[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // CREATE
    public HandlingUnit createHandlingUnit(AddHandlingUnit newHandlingUnit, String loginUserID, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit")
                .queryParam("loginUserID", loginUserID);
        HttpEntity<?> entity = new HttpEntity<>(newHandlingUnit, headers);
        ResponseEntity<HandlingUnit> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingUnit.class);
        //		log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // Patch HandlingUnit
    public HandlingUnit updateHandlingUnit(String handlingUnit, String warehouseId, String companyCodeId,
                                           String languageId, String plantId, HandlingUnit modifiedHandlingUnit,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedHandlingUnit, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId);

            ResponseEntity<HandlingUnit> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingUnit.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete HandlingUnit
    public boolean deleteHandlingUnit(String handlingUnit, String warehouseId, String companyCodeId,
                                      String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMALTERNATEUOM---------------------------------------------------------------*/
    // Get ALL
    public ImAlternateUom[] getImAlternateUoms(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom");
            ResponseEntity<ImAlternateUom[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImAlternateUom[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImAlternateUom
    public ImAlternateUom[] getImAlternateUom(String alternateUom, String companyCodeId, String plantId,
                                              String warehouseId, String itemCode, String uomId,
                                              String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + uomId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("itemCode", itemCode)
                    .queryParam("alternateUom", alternateUom)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImAlternateUom[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImAlternateUom[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImAlternateUom
    public ImAlternateUom[] addImAlternateUom(List<AddImAlternateUom> imalternateuom, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(imalternateuom, headers);
            ResponseEntity<ImAlternateUom[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImAlternateUom[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImAlternateUom
    public ImAlternateUom[] updateImAlternateUom(String alternateUom, String companyCodeId, String plantId,
                                                 String warehouseId, String itemCode, String uomId, String languageId,
                                                 List<UpdateImAlternateUom> modifiedImAlternateUom,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImAlternateUom, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + uomId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("alternateUom", alternateUom);

            ResponseEntity<ImAlternateUom[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImAlternateUom[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImAlternateUom
    public boolean deleteImAlternateUom(String alternateUom, String companyCodeId, String plantId,
                                        String warehouseId, String itemCode, String uomId,
                                        String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + uomId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("alternateUom", alternateUom);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImAlternateUom
    public ImAlternateUom[] findImAlternateUom(SearchImAlternateUom searchImAlternateUom, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/find");
            HttpEntity<?> entity = new HttpEntity<>(searchImAlternateUom, headers);
            ResponseEntity<ImAlternateUom[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImAlternateUom[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---IMBASICDATA1---------------------------------------------------------------*/
    // Get ALL
    public ImBasicData1[] getImBasicData1s(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1");
            ResponseEntity<ImBasicData1[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImBasicData1
    public ImBasicData1V2 getImBasicData1(String itemCode, String warehouseId, String companyCodeId, String plantId,
                                        String uomId, String languageId, String manufacturerPartNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2/" + itemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("uomId", uomId)
                            .queryParam("manufacturerPartNo", manufacturerPartNo)
                            .queryParam("languageId", languageId);
            ResponseEntity<ImBasicData1V2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1V2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData1
    public PaginatedResponse<ImBasicData1> findImBasicData11(SearchImBasicData1 searchImBasicData1, Integer pageNo,
                                                             Integer pageSize, String sortBy, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findImBasicData1/pagination")
                            .queryParam("pageNo", pageNo)
                            .queryParam("pageSize", pageSize)
                            .queryParam("sortBy", sortBy);
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);

            ParameterizedTypeReference<PaginatedResponse<ImBasicData1>> responseType =
                    new ParameterizedTypeReference<PaginatedResponse<ImBasicData1>>() {
                    };
            ResponseEntity<PaginatedResponse<ImBasicData1>> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);

            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData1
    public ImBasicData1[] findImBasicData1(SearchImBasicData1 searchImBasicData1, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findImBasicData1");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData1Stream
    public ImBasicData1[] findImBasicData1New(SearchImBasicData1 searchImBasicData1, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findImBasicData1Stream");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//			List<ImBasicData1> obList = new ArrayList<>();
//			for (ImBasicData1 imBasicData1 : result.getBody()) {
//
//				obList.add(addingTimeWithDateImBasicData1(imBasicData1));
//
//			}
//			return obList.toArray(new ImBasicData1[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearch
    public ItemCodeDesc[] findImBasicData1LikeSearch(String likeSearchByItemCodeNDesc, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponents builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findItemCodeByLike")
                            .queryParam("likeSearchByItemCodeNDesc", likeSearchByItemCodeNDesc).build();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ItemCodeDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemCodeDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearch
    public ItemCodeDesc[] findImBasicData1LikeSearchNew(String likeSearchByItemCodeNDesc,
                                                        String companyCodeId,
                                                        String plantId,
                                                        String languageId,
                                                        String warehouseId,
                                                        String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponents builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findItemCodeByLikeNew")
                            .queryParam("likeSearchByItemCodeNDesc", likeSearchByItemCodeNDesc)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId).build();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ItemCodeDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemCodeDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearch
    public ItemCodeDesc[] findImBasicData1LikeSearchNewV2(LikeSearchInput likeSearchInput,
                                                        String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2/findItemCodeByLikeNew");
            HttpEntity<?> entity = new HttpEntity<>(likeSearchInput, headers);
            ResponseEntity<ItemCodeDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemCodeDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST ImBasicData1
    public ImBasicData1V2 addImBasicData1(ImBasicData1V2 imbasicdata1, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(imbasicdata1, headers);
            ResponseEntity<ImBasicData1V2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1V2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImBasicData1
    public ImBasicData1V2 updateImBasicData1(String itemCode, String warehouseId, String companyCodeId, String plantId, String manufacturerPartNo,
                                           String uomId, String languageId, ImBasicData1V2 modifiedImBasicData1, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImBasicData1, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2/" + itemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("uomId", uomId)
                            .queryParam("languageId", languageId)
                            .queryParam("manufacturerPartNo", manufacturerPartNo)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImBasicData1V2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImBasicData1V2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImBasicData1
    public boolean deleteImBasicData1(String itemCode, String warehouseId, String companyCodeId,
                                      String plantId, String uomId, String languageId,
                                      String manufacturerPartNo, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2/" + itemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("uomId", uomId)
                            .queryParam("manufacturerPartNo", manufacturerPartNo)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    //===============================walkaroo-v3==================================================

     // POST - findImBasicData1Stream
    public ImBasicData1[] findImBasicData1V3(SearchImBasicData1 searchImBasicData1, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/v2/findImBasicData1Stream");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMBASICDATA2---------------------------------------------------------------*/
    // Get ALL
    public ImBasicData2[] getImBasicData2s(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2");
            ResponseEntity<ImBasicData2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImBasicData2
    public ImBasicData2 getImBasicData2(String itemCode, String companyCodeId, String plantId,
                                        String warehouseId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/" + itemCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("languageId", languageId);
            ResponseEntity<ImBasicData2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImBasicData2
    public ImBasicData2 addImBasicData2(ImBasicData2 imbasicdata2, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(imbasicdata2, headers);
            ResponseEntity<ImBasicData2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImBasicData2
    public ImBasicData2 updateImBasicData2(String itemCode, String companyCodeId, String plantId,
                                           String warehouseId, String languageId, ImBasicData2 modifiedImBasicData2,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImBasicData2, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId);

            ResponseEntity<ImBasicData2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImBasicData2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImBasicData2
    public boolean deleteImBasicData2(String itemCode, String companyCodeId, String plantId,
                                      String warehouseId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData2
    public ImBasicData2[] findImBasicData2(SearchImBasicData2 searchImBasicData2, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/findImBasicData2");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData2, headers);
            ResponseEntity<ImBasicData2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMPACKING---------------------------------------------------------------*/
    // Get ALL
    public ImPacking[] getImPackings(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking");
            ResponseEntity<ImPacking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPacking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImPacking
    public ImPacking getImPacking(String packingMaterialNo, String companyCodeId, String plantId,
                                  String languageId, String warehouseId, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("itemCode", itemCode);
            ResponseEntity<ImPacking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPacking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImPacking
    public ImPacking addImPacking(ImPacking impacking, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(impacking, headers);
            ResponseEntity<ImPacking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPacking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImPacking
    public ImPacking updateImPacking(String packingMaterialNo, String companyCodeId, String plantId,
                                     String languageId, String warehouseId, String itemCode,
                                     ImPacking modifiedImPacking,
                                     String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImPacking, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImPacking> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPacking.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImPacking
    public boolean deleteImPacking(String packingMaterialNo, String companyCodeId, String plantId,
                                   String languageId, String warehouseId, String itemCode,
                                   String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImPacking
    public ImPacking[] findImPacking(SearchImPacking searchImPacking, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/findImPacking");
            HttpEntity<?> entity = new HttpEntity<>(searchImPacking, headers);
            ResponseEntity<ImPacking[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPacking[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---IMPARTNER---------------------------------------------------------------*/
    // Get ALL
    public ImPartner[] getImPartners(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner");
            ResponseEntity<ImPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImPartner
    public ImPartner[] getImPartner(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, String itemCode, String manufacturerName, String partnerItemBarcode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + itemCode)
                    .queryParam("manufacturerName", manufacturerName)
                    .queryParam("partnerItemBarcode", partnerItemBarcode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImPartner
    public ImPartner[] addImPartner(List<ImPartner> impartner, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(impartner, headers);
            ResponseEntity<ImPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImPartner
    public ImPartner[] updateImPartner(String companyCodeId, String plantId, String languageId,
                                       String warehouseId, String itemCode, String manufacturerName,
                                       List<ImPartner> modifiedImPartner, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImPartner, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("manufacturerName", manufacturerName)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("itemCode", itemCode);

            ResponseEntity<ImPartner[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImPartner
    public boolean deleteImPartner(String companyCodeId, String plantId, String languageId, String warehouseId,
                                   String itemCode, String manufacturerName, String partnerItemBarcode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("manufacturerName", manufacturerName)
                            .queryParam("partnerItemBarcode", partnerItemBarcode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // GET ImPartner
    public ImPartner[] getImPartnerV2(ImPartnerInput imPartnerInput, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(imPartnerInput, headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/v2/get" );
            ResponseEntity<ImPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImPartner
    public ImPartner[] updateImPartnerV2(List<ImPartner> modifiedImPartner, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImPartner, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/v2/update")
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImPartner[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImPartner
    public boolean deleteImPartnerV2(List<ImPartnerInput> imPartnerInput, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(imPartnerInput, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/v2/delete")
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // POST - findImPartner
    public ImPartner[] findImPartner(SearchImPartner searchImPartner, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/findImPartner");
            HttpEntity<?> entity = new HttpEntity<>(searchImPartner, headers);
            ResponseEntity<ImPartner[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPartner[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---IMSTRATEGIES---------------------------------------------------------------*/
    // Get ALL
    public ImStrategies[] getImStrategiess(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies");
            ResponseEntity<ImStrategies[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImStrategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImStrategies
    public ImStrategies getImStrategies(Long strategyTypeId, String companyCodeId, String plantId, String warehouseId,
                                        String itemCode, Long sequenceIndicator, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategyTypeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("languageId", languageId)
                    .queryParam("itemCode", itemCode)
                    .queryParam("sequenceIndicator", sequenceIndicator);
            ResponseEntity<ImStrategies> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImStrategies.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImStrategies
    public ImStrategies addImStrategies(ImStrategies imstrategies, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(imstrategies, headers);
            ResponseEntity<ImStrategies> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImStrategies.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImStrategies
    public ImStrategies updateImStrategies(Long strategyTypeId, String companyCodeId, String plantId, String warehouseId,
                                           String itemCode, Long sequenceIndicator, String languageId, ImStrategies modifiedImStrategies,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedImStrategies, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategyTypeId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("sequenceIndicator", sequenceIndicator)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImStrategies> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImStrategies.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImStrategies
    public boolean deleteImStrategies(Long strategyTypeId, String companyCodeId, String plantId,
                                      String warehouseId, String itemCode, Long sequenceIndicator,
                                      String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategyTypeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("sequenceIndicator", sequenceIndicator);

            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImStrategies
    public ImStrategies[] findImStrategies(SearchImStrategies searchImStrategies, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/findImStrategies");
            HttpEntity<?> entity = new HttpEntity<>(searchImStrategies, headers);
            ResponseEntity<ImStrategies[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImStrategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---PACKINGMATERIAL---------------------------------------------------------------*/
    // Get ALL
    public PackingMaterial[] getPackingMaterials(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial");
            ResponseEntity<PackingMaterial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackingMaterial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET PackingMaterial
    public PackingMaterial getPackingMaterial(String packingMaterialNo, String companyCodeId, String plantId,
                                              String languageId, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<PackingMaterial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackingMaterial.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findPackingMaterial
    public PackingMaterial[] findPackingMaterial(SearchPackingMaterial searchPackingMaterial, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/findPackingMaterial");
            HttpEntity<?> entity = new HttpEntity<>(searchPackingMaterial, headers);
            ResponseEntity<PackingMaterial[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PackingMaterial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST PackingMaterial
    public PackingMaterial addPackingMaterial(PackingMaterial packingmaterial, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(packingmaterial, headers);
            ResponseEntity<PackingMaterial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PackingMaterial.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch PackingMaterial
    public PackingMaterial updatePackingMaterial(String packingMaterialNo, String companyCodeId, String languageId,
                                                 String plantId, String warehouseId, PackingMaterial modifiedPackingMaterial,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedPackingMaterial, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<PackingMaterial> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PackingMaterial.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete PackingMaterial
    public boolean deletePackingMaterial(String packingMaterialNo, String companyCodeId, String plantId,
                                         String languageId, String warehouseId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMALTERNATEPART---------------------------------------------------------------*/
    // Get ALL
    public ImAlternatePart[] getImAlternateParts(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart");
            ResponseEntity<ImAlternatePart[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImAlternatePart[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImAlternatePart
    public ImAlternatePart[] getImAlternatePart(String companyCodeId, String languageId, String warehouseId,
                                                String plantId, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart/" + itemCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImAlternatePart[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImAlternatePart[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImAlternatePart
    public ImAlternatePart[] findImAlternatePart(SearchImAlternateParts searchImAlternateParts, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart/find");
            HttpEntity<?> entity = new HttpEntity<>(searchImAlternateParts, headers);
            ResponseEntity<ImAlternatePart[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImAlternatePart[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImalternatePart
    public ImAlternatePart[] addImalternatePart(List<AddImAlternatePart> addImAlternatePart, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addImAlternatePart, headers);
            ResponseEntity<ImAlternatePart[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImAlternatePart[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImAlternatePart
    public ImAlternatePart[] updateImAlternatePart(String companyCodeId, String languageId, String warehouseId, String plantId,
                                                   String itemCode, List<AddImAlternatePart> updateImAlternatePart,
                                                   String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(updateImAlternatePart, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<ImAlternatePart[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImAlternatePart[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImAlternatePart
    public boolean deleteImAlternateCode(String companyCodeId, String languageId, String warehouseId, String plantId,
                                         String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternatepart/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---STORAGEBIN---------------------------------------------------------------*/
    // Get ALL
    public StorageBin[] getStorageBins(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin");
            ResponseEntity<StorageBin[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin
    public StorageBin getStorageBin(String storageBin, String companyCodeId, String plantId,
                                    String warehouseId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin
    public StorageBin getStorageBin(String warehouseId, String storageBin, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin + "/warehouseId")
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findStorageBin
    public StorageBin[] findStorageBin(SearchStorageBin searchStorageBin, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/findStorageBin");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageBin, headers);
            ResponseEntity<StorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findStorageBinStream
    public StorageBin[] findStorageBinNew(SearchStorageBin searchStorageBin, String authToken) throws ParseException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/findStorageBinStream");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageBin, headers);
            ResponseEntity<StorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();

//			List<StorageBin> obList = new ArrayList<>();
//			for (StorageBin storageBin : result.getBody()) {
//
//				obList.add(addingTimeWithDateStorageBin(storageBin));
//
//			}
//			return obList.toArray(new StorageBin[obList.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearch
    public StorageBinDesc[] findStorageBinLikeSearch(String likeSearchByStorageBinNDesc, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponents builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/findStorageBinByLike")
                            .queryParam("likeSearchByStorageBinNDesc", likeSearchByStorageBinNDesc).build();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StorageBinDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearchNew
    public StorageBinDesc[] findStorageBinLikeSearchNew(String likeSearchByStorageBinNDesc,
                                                        String companyCodeId,
                                                        String plantId,
                                                        String languageId,
                                                        String warehouseId,
                                                        String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "WMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponents builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/findStorageBinByLikeNew")
                            .queryParam("likeSearchByStorageBinNDesc", likeSearchByStorageBinNDesc)
                            .queryParam("companyCodeId",companyCodeId)
                            .queryParam("plantId",plantId)
                            .queryParam("languageId",languageId)
                            .queryParam("warehouseId",warehouseId).build();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<StorageBinDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - findImBasicData1LikeSearchNew
    public StorageBinDesc[] findStorageBinLikeSearchNewV2(LikeSearchInput likeSearchInput, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "WMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/v2/findStorageBinByLikeNew");
            HttpEntity<?> entity = new HttpEntity<>(likeSearchInput, headers);
            ResponseEntity<StorageBinDesc[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinDesc[].class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    // POST StorageBin
    public StorageBin addStorageBin(StorageBin storagebin, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storagebin, headers);
            ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST StorageBin - V2
    public StorageBinV2 addStorageBinV2(StorageBinV2 storagebin, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/v2")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storagebin, headers);
            ResponseEntity<StorageBinV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageBin
    public StorageBin updateStorageBin(String storageBin, String companyCodeId, String plantId,
                                       String languageId, String warehouseId, StorageBin modifiedStorageBin,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBin, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageBin> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBin.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageBin
    public boolean deleteStorageBin(String storageBin, String warehouseId, String companyCodeId,
                                    String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // GET StorageBinV2
    public StorageBinV2 getStorageBinV2(String storageBin, String companyCodeId, String plantId,
                                        String warehouseId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/v2/" + storageBin)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<StorageBinV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageBinV2
    public StorageBinV2 updateStorageBinV2(String storageBin, String companyCodeId, String plantId,
                                           String languageId, String warehouseId, StorageBinV2 modifiedStorageBinV2,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBinV2, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/storageBinV2/" + storageBin)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageBinV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBinV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageBinV2
    public boolean deleteStorageBinV2(String storageBin, String warehouseId, String companyCodeId,
                                      String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/v2/" + storageBin)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---AUDITLOG---------------------------------------------------------------*/
    // Get ALL
    public AuditLog[] getAuditLogs(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog");
            ResponseEntity<AuditLog[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AuditLog[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET AuditLog
    public AuditLog getAuditLog(String auditFileNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog/" + auditFileNumber);
            ResponseEntity<AuditLog> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AuditLog.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST AuditLog
    public AuditLog addAuditLog(AuditLog auditlog, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(auditlog, headers);
            ResponseEntity<AuditLog> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AuditLog.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch AuditLog
    public AuditLog updateAuditLog(String auditFileNumber, AuditLog modifiedAuditLog, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedAuditLog, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog/" + auditFileNumber);

            ResponseEntity<AuditLog> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, AuditLog.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete AuditLog
    public boolean deleteAuditLog(String auditFileNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog/" + auditFileNumber);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findAuditLog
    public AuditLog[] findAuditLog(SearchAuditLog searchAuditLog, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog/findAuditLog");
            HttpEntity<?> entity = new HttpEntity<>(searchAuditLog, headers);
            ResponseEntity<AuditLog[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AuditLog[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //-----------------------------------Notification------------------------------------------------
    public Notification[] getAllNotifications(String userId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Inquiry-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "/notification-message")
                    .queryParam("userId", userId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Notification[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Notification[].class);
            log.info("result : " + result.getBody());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // UPDATE
    public Boolean updateNotificationAsRead(String loginUserID, Long id, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();

            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "/notification-message/mark-read/" + id)
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<Boolean> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Boolean.class);
            log.info("result : " + result.getBody());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // UPDATE
    public Boolean updateNotificationAsReadAll(String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();

            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "/notification-message/mark-read-all")
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<Boolean> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Boolean.class);
            log.info("result : " + result.getBody());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMBATCHSERIAL---------------------------------------------------------------*/
    // Get ALL
    public ImBatchSerial[] getImBatchSerials(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial");
            ResponseEntity<ImBatchSerial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImBatchSerial
    public ImBatchSerial getImBatchSerial(String storageMethod, String companyCodeId, String plantId,
                                          String languageId, String warehouseId, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial/" + storageMethod)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("itemCode", itemCode);
            ResponseEntity<ImBatchSerial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBatchSerial.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImBatchSerial
    public ImBatchSerial addImBatchSerial(AddImBatchSerial addImBatchSerial, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addImBatchSerial, headers);
            ResponseEntity<ImBatchSerial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBatchSerial.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImPacking
    public ImBatchSerial updateImBatchSerial(String storageMethod, String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String itemCode,
                                             UpdateImBatchSerial updateImBatchSerial,
                                             String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateImBatchSerial, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial/" + storageMethod)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImBatchSerial> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImBatchSerial.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImPacking
    public boolean deleteImBatchSerial(String storageMethod, String companyCodeId, String plantId,
                                       String languageId, String warehouseId, String itemCode,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial/" + storageMethod)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemCode", itemCode);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImBatchSerial
    public ImBatchSerial[] findImBatchSerial(SearchImBatchSerial searchImBatchSerial, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbatchserial/findImBatchSerial");
            HttpEntity<?> entity = new HttpEntity<>(searchImBatchSerial, headers);
            ResponseEntity<ImBatchSerial[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---IMCAPACITY---------------------------------------------------------------*/
    // Get ALL
    public ImCapacity[] getAllImCapacity(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity");
            ResponseEntity<ImCapacity[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImCapacity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImCapacity
    public ImCapacity getImCapacity(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity/" + itemCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImCapacity> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImCapacity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImCapacity
    public ImCapacity addImCapacity(AddImCapacity addImCapacity, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addImCapacity, headers);
            ResponseEntity<ImCapacity> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImCapacity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImCapacity
    public ImCapacity updateImCapacity(String companyCodeId, String plantId, String languageId,
                                       String warehouseId, String itemCode, UpdateImCapacity updateImCapacity,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateImCapacity, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImCapacity> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImCapacity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImCapacity
    public boolean deleteImCapacity(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImCapacity
    public ImCapacity[] findImCapacity(SearchImCapacity searchImCapacity, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imcapacity/findImCapacity");
            HttpEntity<?> entity = new HttpEntity<>(searchImCapacity, headers);
            ResponseEntity<ImCapacity[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImCapacity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---IMVARIANT---------------------------------------------------------------*/
    // Get ALL
    public ImVariant[] getAllImVariant(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant");
            ResponseEntity<ImVariant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImVariant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImCapacity
    public ImVariant[] getImVariant(String companyCodeId, String plantId, String languageId, String warehouseId,
                                    String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant/" + itemCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("itemCode", itemCode)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImVariant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImVariant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImVariant
    public ImVariant[] addImVariant(List<AddImVariant> addImVariant, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addImVariant, headers);
            ResponseEntity<ImVariant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImVariant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImVariant
    public ImVariant[] updateImVariant(String companyCodeId, String plantId, String languageId, String warehouseId,
                                       String itemCode, List<UpdateImVariant> updateImVariant,
                                       String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateImVariant, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImVariant[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImVariant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImVariant
    public boolean deleteImVariant(String companyCodeId, String plantId,
                                   String languageId, String warehouseId, String itemCode,
                                   String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("itemCode", itemCode)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImVariant
    public ImVariant[] findImVariant(SearchImVariant searchImVariant, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imvariant/findImVariant");
            HttpEntity<?> entity = new HttpEntity<>(searchImVariant, headers);
            ResponseEntity<ImVariant[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImVariant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---IMPALLETIZATION---------------------------------------------------------------*/
    // Get ALL
    public ImPalletization[] getAllPalletization(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization");
            ResponseEntity<ImPalletization[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPalletization[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ImPalletization
    public ImPalletization[] getImPalletization(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String itemCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization/" + itemCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<ImPalletization[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPalletization[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ImPalletization
    public ImPalletization[] addImPalletization(List<AddImPalletization> addImPalletization, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addImPalletization, headers);
            ResponseEntity<ImPalletization[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPalletization[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ImPalletization
    public ImPalletization[] updatePalletization(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String itemCode, List<AddImPalletization> updateImPalletization,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateImPalletization, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ImPalletization[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPalletization[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ImPalletization
    public boolean deleteImPalletization(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String itemCode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findImPalletization
    public ImPalletization[] findImPalletization(SearchImPalletization searchImPalletization, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impalletization/findImPalletization");
            HttpEntity<?> entity = new HttpEntity<>(searchImPalletization, headers);
            ResponseEntity<ImPalletization[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPalletization[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---DOCK---------------------------------------------------------------*/
    // Get ALL
    public Dock[] getAllDock(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock");
            ResponseEntity<Dock[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dock[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Dock
    public Dock getDock(String companyCodeId, String plantId, String languageId,
                        String warehouseId, String dockId, String dockType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock/" + dockId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("dockType", dockType)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<Dock> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dock.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Dock
    public Dock addDock(AddDock addDock, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addDock, headers);
            ResponseEntity<Dock> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Dock.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Dock
    public Dock updateDock(String companyCodeId, String plantId, String languageId,
                           String warehouseId, String dockId, String dockType, UpdateDock updateDock,
                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateDock, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock/" + dockId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("dockType", dockType)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Dock> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Dock.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Dock
    public boolean deleteDock(String companyCodeId, String plantId, String languageId,
                              String warehouseId, String dockId, String dockType,
                              String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock/" + dockId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("dockType", dockType)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findDock
    public Dock[] findDock(SearchDock searchDock, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "dock/findDock");
            HttpEntity<?> entity = new HttpEntity<>(searchDock, headers);
            ResponseEntity<Dock[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Dock[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---CYCLECOUNTSCHEDULER---------------------------------------------------------------*/
    // Get ALL
    public CycleCountScheduler[] getAllCycleCountScheduler(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler");
            ResponseEntity<CycleCountScheduler[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CycleCountScheduler[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET CycleCountScheduler
    public CycleCountScheduler getCycleCountScheduler(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, Long cycleCountTypeId, Long levelId,
                                                      String schedulerNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler/" + cycleCountTypeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("levelId", levelId)
                    .queryParam("schedulerNumber", schedulerNumber)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<CycleCountScheduler> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CycleCountScheduler.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST CycleCountScheduler
    public CycleCountScheduler addCycleCountScheduler(AddCycleCountScheduler addCycleCountScheduler, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addCycleCountScheduler, headers);
            ResponseEntity<CycleCountScheduler> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CycleCountScheduler.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch CycleCountScheduler
    public CycleCountScheduler updateCycleCountScheduler(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, Long cycleCountTypeId, Long levelId,
                                                         String schedulerNumber, UpdateCycleCountScheduler updateCycleCountScheduler,
                                                         String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateCycleCountScheduler, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler/" + cycleCountTypeId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("levelId", levelId)
                            .queryParam("schedulerNumber", schedulerNumber)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<CycleCountScheduler> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CycleCountScheduler.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete CycleCountScheduler
    public boolean deleteCycleCountScheduler(String companyCodeId, String plantId, String languageId,
                                             String warehouseId, Long levelId, String schedulerNumber,
                                             Long cycleCountTypeId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler/" + cycleCountTypeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("levelId", levelId)
                            .queryParam("schedulerNumber", schedulerNumber)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findCycleCountScheduler
    public CycleCountScheduler[] findCycleCountScheduler(SearchCycleCountScheduler searchCycleCountScheduler, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cyclecountscheduler/find");
            HttpEntity<?> entity = new HttpEntity<>(searchCycleCountScheduler, headers);
            ResponseEntity<CycleCountScheduler[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CycleCountScheduler[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---NUMBERRANGEITEM---------------------------------------------------------------*/
    // Get ALL
    public NumberRangeItem[] getAllNumberRangeItem(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem");
            ResponseEntity<NumberRangeItem[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRangeItem[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET NumberRangeItem
    public NumberRangeItem getNumberRange(String companyCodeId, String plantId,
                                          String languageId, String warehouseId,
                                          Long itemTypeId, Long sequenceNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem/" + itemTypeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("sequenceNo", sequenceNo)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<NumberRangeItem> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRangeItem.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST NumberRangeItem
    public NumberRangeItem addNumberRangeItem(AddNumberRangeItem addNumberRangeItem, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addNumberRangeItem, headers);
            ResponseEntity<NumberRangeItem> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, NumberRangeItem.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch NumberRangeItem
    public NumberRangeItem updateNumberRangeItem(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, Long itemTypeId, Long sequenceNo,
                                                 UpdateNumberRangeItem updateNumberRangeItem,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateNumberRangeItem, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem/" + itemTypeId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("sequenceNo", sequenceNo)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<NumberRangeItem> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, NumberRangeItem.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete NumberRangeItem
    public boolean deleteNumberRangeItem(String companyCodeId, String plantId, String languageId,
                                         String warehouseId, Long itemTypeId, Long sequenceNo,
                                         String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem/" + itemTypeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("sequenceNo", sequenceNo)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findNumberRangeItem
    public NumberRangeItem[] findNumberRangeItem(SearchNumberRangeItem searchNumberRangeItem, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangeitem/find");
            HttpEntity<?> entity = new HttpEntity<>(searchNumberRangeItem, headers);
            ResponseEntity<NumberRangeItem[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, NumberRangeItem[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---NUMBERRANGESTORAGEBIN---------------------------------------------------------------*/
    // Get ALL
    public NumberRangeStorageBin[] getAllNumberRangeStorageBin(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin");
            ResponseEntity<NumberRangeStorageBin[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRangeStorageBin[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET NumberRangeStorageBin
    public NumberRangeStorageBin getNumberRangeStorageBin(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          Long floorId, String storageSectionId, String rowId, String aisleNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin/" + storageSectionId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("aisleNumber", aisleNumber)
                    .queryParam("floorId", floorId)
                    .queryParam("rowId", rowId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<NumberRangeStorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRangeStorageBin.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST NumberRangeStorageBin
    public NumberRangeStorageBin addNumberRangeStorageBin(AddNumberRangeStorageBin addNumberRangeStorageBin, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(addNumberRangeStorageBin, headers);

            ResponseEntity<NumberRangeStorageBin> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, NumberRangeStorageBin.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch NumberRangeStorageBin
    public NumberRangeStorageBin updateNumberRangeStorageBin(String companyCodeId, String plantId, String languageId,
                                                             String warehouseId, String storageSectionId, Long floorId, String rowId,
                                                             String aisleNumber, UpdateNumberRangeStorageBin updateNumberRangeStorageBin,
                                                             String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateNumberRangeStorageBin, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin/" + storageSectionId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("floorId", floorId)
                            .queryParam("rowId", rowId)
                            .queryParam("aisleNumber", aisleNumber)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<NumberRangeStorageBin> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, NumberRangeStorageBin.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete NumberRangeStorageBin
    public boolean deleteNumberRangeStorageBin(String companyCodeId, String plantId, String languageId, String warehouseId,
                                               Long floorId, String storageSectionId, String rowId, String aisleNumber,
                                               String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin/" + storageSectionId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("floorId", floorId)
                            .queryParam("rowId", rowId)
                            .queryParam("aisleNumber", aisleNumber)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findNumberRangeStorageBin
    public NumberRangeStorageBin[] findNumberRangeStorageBin(SearchNumberRangeStorageBin searchNumberRangeStorageBin, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "numberrangestoragebin/findNumberRangeStorageBin");
            HttpEntity<?> entity = new HttpEntity<>(searchNumberRangeStorageBin, headers);
            ResponseEntity<NumberRangeStorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, NumberRangeStorageBin[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---WorkCenter---------------------------------------------------------------*/
    // Get ALL
    public WorkCenter[] getAllWorkCenter(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter");
            ResponseEntity<WorkCenter[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkCenter[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//	// GET WorkCenter
//	public WorkCenter getWorkCenter(String companyCodeId,String plantId,String languageId,String warehouseId,Long workCenterId,String workCenterType,String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "Classic WMS's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			HttpEntity<?> entity = new HttpEntity<>(headers);
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter/" + workCenterId)
//					.queryParam("companyCodeId",companyCodeId)
//					.queryParam("languageId",languageId)
//					.queryParam("workCenterType ",workCenterType)
//					.queryParam("plantId",plantId)
//					.queryParam("warehouseId",warehouseId);
//			ResponseEntity<WorkCenter> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkCenter.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

    public WorkCenter getWorkCenter(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, Long workCenterId, String workCenterType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter/" + workCenterId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("workCenterType", workCenterType)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<WorkCenter> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkCenter.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST NumberRangeItem
    public WorkCenter addWorkCenter(AddWorkCenter addWorkCenter, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addWorkCenter, headers);
            ResponseEntity<WorkCenter> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkCenter.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch WOrkCenterId
    public WorkCenter updateWorkCenter(String companyCodeId, String plantId, String languageId,
                                       String warehouseId, Long workCenterId, UpdateWorkCenter updateWorkCenter,
                                       String workCenterType, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateWorkCenter, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter/" + workCenterId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("workCenterType", workCenterType)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<WorkCenter> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkCenter.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete WorkCenter
    public boolean deleteWorkCenter(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, Long workCenterId, String workCenterType,
                                    String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter/" + workCenterId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("workCenterType", workCenterType)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findWorkCenter
    public WorkCenter[] findWorkCenter(SearchWorkCenter searchWorkCenter, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "workcenter/findWorkCenter");
            HttpEntity<?> entity = new HttpEntity<>(searchWorkCenter, headers);
            ResponseEntity<WorkCenter[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkCenter[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


//THREEPL

    /*--------------------------------------Billing---------------------------------------------------------------------------------*/
    // GET ALL
    public Billing[] getBillings(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing");
            ResponseEntity<Billing[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Billing[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public Billing getBilling(String warehouseId, String moduleId, String partnerCode,
                              String companyCodeId, String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing/" + partnerCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("moduleId", moduleId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId);
            ResponseEntity<Billing> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Billing.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // CREATE
    public Billing addBilling(AddBilling newBilling, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(newBilling, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing")
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<Billing> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Billing.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //UPDATE
    public Billing updateBilling(String warehouseId, String moduleId, String partnerCode,
                                 String companyCodeId, String languageId, String plantId, String loginUserID,
                                 UpdateBilling modifiedBilling, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedBilling, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing/" + partnerCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("moduleId", moduleId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<Billing> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Billing.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteBilling(String warehouseId, String moduleId, String partnerCode, String companyCodeId,
                                 String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing/" + partnerCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("moduleId", moduleId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    //SEARCH
    public Billing[] findBilling(FindBilling findBilling, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "billing/find");
            HttpEntity<?> entity = new HttpEntity<>(findBilling, headers);
            ResponseEntity<Billing[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Billing[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*--------------------------------------CbmInbound---------------------------------------------------------------------------------*/
    // GET ALL
    public CbmInbound[] getCbmInbounds(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound");
            ResponseEntity<CbmInbound[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CbmInbound[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public CbmInbound getCbmInbound(String warehouseId, String itemCode, String companyCodeId,
                                    String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound/" + itemCode)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId);
            ResponseEntity<CbmInbound> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CbmInbound.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // CREATE
    public CbmInbound addCbmInbound(AddCbmInbound newCbmInbound, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(newCbmInbound, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound")
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<CbmInbound> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CbmInbound.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //UPDATE
    public CbmInbound updateCbmInbound(String warehouseId, String itemCode, String companyCodeId,
                                       String languageId, String plantId, String loginUserID,
                                       UpdateCbmInbound modifiedCbmInbound, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedCbmInbound, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<CbmInbound> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CbmInbound.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deleteCbmInbound(String warehouseId, String itemCode, String companyCodeId,
                                    String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound/" + itemCode)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    //SEARCH
    public CbmInbound[] findCbmInbound(FindCbmInbound findCbmInbound, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "cbminbound/find");
            HttpEntity<?> entity = new HttpEntity<>(findCbmInbound, headers);
            ResponseEntity<CbmInbound[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CbmInbound[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*--------------------------------------PriceList---------------------------------------------------------------------------------*/
    // GET ALL
    public PriceList[] getPriceLists(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist");
            ResponseEntity<PriceList[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PriceList[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PriceList getPriceList(String warehouseId, String moduleId, Long priceListId,
                                  Long serviceTypeId, Long chargeRangeId, String companyCodeId,
                                  String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist/" + priceListId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("moduleId", moduleId)
                    .queryParam("serviceTypeId", serviceTypeId)
                    .queryParam("chargeRangeId", chargeRangeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId);
            ResponseEntity<PriceList> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PriceList.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // CREATE
    public PriceList addPriceList(AddPriceList newPriceListId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(newPriceListId, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist")
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<PriceList> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PriceList.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //UPDATE
    public PriceList updatePriceList(String warehouseId, String moduleId, Long priceListId,
                                     Long serviceTypeId, Long chargeRangeId, String companyCodeId,
                                     String languageId, String plantId, String loginUserID,
                                     UpdatePriceList modifiedPriceList, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedPriceList, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist/" + priceListId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("moduleId", moduleId)
                            .queryParam("serviceTypeId", serviceTypeId)
                            .queryParam("chargeRangeId", chargeRangeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<PriceList> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PriceList.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePriceList(String warehouseId, String moduleId, Long priceListId,
                                   Long serviceTypeId, Long chargeRangeId, String companyCodeId,
                                   String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist/" + priceListId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("moduleId", moduleId)
                            .queryParam("serviceTypeId", serviceTypeId)
                            .queryParam("chargeRangeId", chargeRangeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    //SEARCH
    public PriceList[] findPriceList(FindPriceList findPriceList, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelist/find");
            HttpEntity<?> entity = new HttpEntity<>(findPriceList, headers);
            ResponseEntity<PriceList[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PriceList[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*--------------------------------------PriceListAssignment---------------------------------------------------------------------------------*/
    // GET ALL
    public PriceListAssignment[] getPriceListAssignments(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment");
            ResponseEntity<PriceListAssignment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PriceListAssignment[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET
    public PriceListAssignment getPriceListAssignment(String warehouseId, String partnerCode, Long priceListId,
                                                      String companyCodeId, String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment/" + priceListId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("partnerCode", partnerCode)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId);
            ResponseEntity<PriceListAssignment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PriceListAssignment.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // CREATE
    public PriceListAssignment addPriceListAssignment(AddPriceListAssignment newPriceListAssignment, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(newPriceListAssignment, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment")
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<PriceListAssignment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PriceListAssignment.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //UPDATE
    public PriceListAssignment updatePriceListAssignment(String warehouseId, String partnerCode, Long priceListId,
                                                         String companyCodeId, String languageId, String plantId, String loginUserID,
                                                         UpdatePriceListAssignment modifiedPriceListAssignment, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(modifiedPriceListAssignment, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment/" + priceListId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("partnerCode", partnerCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<PriceListAssignment> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PriceListAssignment.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // DELETE
    public boolean deletePriceListAssignment(String warehouseId, Long priceListId, String partnerCode, String companyCodeId,
                                             String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Threepl RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment/" + priceListId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("partnerCode", partnerCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    //SEARCH
    public PriceListAssignment[] findPriceListAssignment(FindPriceListAssignment findPriceListAssignment, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "pricelistassignment/find");
            HttpEntity<?> entity = new HttpEntity<>(findPriceListAssignment, headers);
            ResponseEntity<PriceListAssignment[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PriceListAssignment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---DRIVER---------------------------------------------------------------*/

    // Get ALL
    public Driver[] getAllDriver(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver");
            ResponseEntity<Driver[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Driver[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Driver
    public Driver getDriver(String companyCodeId, String plantId, String languageId,
                            String warehouseId, Long driverId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver/" + driverId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<Driver> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Driver.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Driver

    public Driver addDriver(AddDriver addDriver, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addDriver, headers);
            ResponseEntity<Driver> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Driver.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Driver

    public Driver updateDriver(String companyCodeId, String plantId, String languageId, String warehouseId,
                               Long driverId, UpdateDriver updateDriver, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateDriver, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver/" + driverId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Driver> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Driver.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Driver

    public boolean deleteDriver(String companyCodeId, String plantId, String languageId, String warehouseId,
                                Long driverId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver/" + driverId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findDriver
    public Driver[] findDriver(SearchDriver searchDriver, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "driver/findDriver");
            HttpEntity<?> entity = new HttpEntity<>(searchDriver, headers);
            ResponseEntity<Driver[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Driver[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---VEHICLE---------------------------------------------------------------*/

    // Get ALL
    public Vehicle[] getAllVehicle(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle");
            ResponseEntity<Vehicle[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Vehicle[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Vehicle
    public Vehicle getVehicle(String companyCodeId, String plantId, String languageId,
                              String warehouseId, String vehicleNumber, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle/" + vehicleNumber)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);
            ResponseEntity<Vehicle> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Vehicle.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Vehicle

    public Vehicle addVehicle(AddVehicle addVehicle, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addVehicle, headers);
            ResponseEntity<Vehicle> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Vehicle.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Driver

    public Vehicle updateVehicle(String companyCodeId, String plantId, String languageId,
                                 String warehouseId, String vehicleNumber, UpdateVehicle updateVehicle,
                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateVehicle, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle/" + vehicleNumber)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Vehicle> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Vehicle.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Vehicle

    public boolean deleteVehicle(String companyCodeId, String plantId, String languageId, String warehouseId,
                                 String vehicleNumber, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle/" + vehicleNumber)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findVehicle
    public Vehicle[] findVehicle(SearchVehicle searchVehicle, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "vehicle/findVehicle");
            HttpEntity<?> entity = new HttpEntity<>(searchVehicle, headers);
            ResponseEntity<Vehicle[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Vehicle[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---ROUTE---------------------------------------------------------------*/

    // Get ALL
    public Route[] getAllRoute(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route");
            ResponseEntity<Route[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Route[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Route
    public Route getRoute(String companyCodeId, String plantId, String languageId,
                          String warehouseId, Long routeId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route/" + routeId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId);

            ResponseEntity<Route> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Route.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Route

    public Route addRoute(AddRoute addRoute, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addRoute, headers);
            ResponseEntity<Route> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Route.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Route

    public Route updateRoute(String companyCodeId, String plantId, String languageId, String warehouseId,
                             Long routeId, UpdateRoute updateRoute, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateRoute, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route/" + routeId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Route> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Route.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Route

    public boolean deleteRoute(String companyCodeId, String plantId, String languageId, String warehouseId,
                               Long routeId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route/" + routeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findRoute
    public Route[] findRoute(SearchRoute searchRoute, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "route/findRoute");
            HttpEntity<?> entity = new HttpEntity<>(searchRoute, headers);

            ResponseEntity<Route[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Route[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------MASTERS---DRIVERVEHICLEASSIGNMENT---------------------------------------------------------------*/

    // Get ALL
    public DriverVehicleAssignment[] getAllDriverVehicleAssignment(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment");
            ResponseEntity<DriverVehicleAssignment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DriverVehicleAssignment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET DriverVehicleAssignment
    public DriverVehicleAssignment getDriverVehicleAssignment(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, Long routeId, Long driverId, String vehicleNumber,
                                                              String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment/" + driverId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("vehicleNumber", vehicleNumber)
                    .queryParam("routeId", routeId)
                    .queryParam("warehouseId", warehouseId);

            ResponseEntity<DriverVehicleAssignment> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, DriverVehicleAssignment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST DriverVehicleAssignment
    public DriverVehicleAssignment addDriverVehicleAssignment(AddDriverVehicleAssignment addDriverVehicleAssignment,
                                                              String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addDriverVehicleAssignment, headers);
            ResponseEntity<DriverVehicleAssignment> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, DriverVehicleAssignment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch DriverVehicleAssignment
    public DriverVehicleAssignment updateDriverVehicleAssignment(String companyCodeId, String plantId, String languageId,
                                                                 String warehouseId, Long routeId, Long driverId,
                                                                 String vehicleNumber, UpdateDriverVehicleAssignment updateDriverVehicleAssignment,
                                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateDriverVehicleAssignment, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment/" + driverId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("vehicleNumber", vehicleNumber)
                            .queryParam("routeId", routeId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<DriverVehicleAssignment> result = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.PATCH, entity, DriverVehicleAssignment.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete DriverVehicleAssignment

    public boolean deleteDriverVehicleAssignment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 Long routeId, String vehicleNumber, Long driverId,
                                                 String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment/" + driverId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("vehicleNumber", vehicleNumber)
                            .queryParam("routeId", routeId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findDriverVehicleAssignment
    public DriverVehicleAssignment[] findDriverVehicleAssignment(SearchDriverVehicleAssignment searchDriverVehicleAssignment,
                                                                 String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "drivervehicleassignment/findDriverVehicleAssignment");
            HttpEntity<?> entity = new HttpEntity<>(searchDriverVehicleAssignment, headers);

            ResponseEntity<DriverVehicleAssignment[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DriverVehicleAssignment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------Item Master -------------------------------------

    //Post Item
    public WarehouseApiResponse postItem(@Valid Item item, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "masterorder/master/item");
        HttpEntity<?> entity = new HttpEntity<>(item, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    //Post Customer
    public WarehouseApiResponse postCustomer(@Valid Customer customer, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken);
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "masterorder/master/customer");
        HttpEntity<?> entity = new HttpEntity<>(customer, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }
    //==============================================================Email=====================================================

    // GET EmailDetails
    public EMailDetails getEMailDetails(Long emailId , String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/" + emailId );
            ResponseEntity<EMailDetails> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EMailDetails.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    // POST EMailDetails
    public EMailDetails createEMailDetails(EMailDetails eMailDetails, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/")
                    .queryParam("loginUserId", loginUserId);

            HttpEntity<?> entity = new HttpEntity<>(eMailDetails, headers);
            ResponseEntity<EMailDetails> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EMailDetails.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // Patch EmailDetails
    public EMailDetails updateEMailDetails(Long emailId, EMailDetails eMailDetails, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(eMailDetails, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/" + emailId)
                            .queryParam("loginUserId", loginUserId);

            ResponseEntity<EMailDetails> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, EMailDetails.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete EMailDetails
    public boolean deleteEMailDetails(Long emailId, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/" + emailId)
                            .queryParam("loginUserId", loginUserId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // UnDelete EMailDetails
    public boolean unDeleteEMailDetails(String emailId, String loginUserId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/undelete/" + emailId)
                            .queryParam("loginUserId", loginUserId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findEMailDetails
    public EMailDetails[] findEMailDetails(FindEmailDetails findEmailDetails, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "email/findEmail");
            HttpEntity<?> entity = new HttpEntity<>(findEmailDetails, headers);
            ResponseEntity<EMailDetails[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EMailDetails[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------MASTERS---PUTAWAYSTRATEGY---------------------------------------------------------------*/

    // GET PutAwayStrategy
    public PutAwayStrategy getPutAwayStrategy(String languageId, String companyCodeId, String plantId, String warehouseId,
                                      String brand, String article, String category, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "putAwayStrategy/get")
                    .queryParam("languageId", languageId)
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("brand", brand)
                    .queryParam("article", article)
                    .queryParam("category", category);

            ResponseEntity<PutAwayStrategy> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayStrategy.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST PutAwayStrategy

    public PutAwayStrategy addPutAwayStrategy(AddPutAwayStrategy addPutAwayStrategy, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "putAwayStrategy")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(addPutAwayStrategy, headers);
            ResponseEntity<PutAwayStrategy> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayStrategy.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch PutAwayStrategy

    public PutAwayStrategy updatePutAwayStrategy(String languageId, String companyCodeId, String plantId, String warehouseId,
                                         String brand, String article, String category, UpdatePutAwayStrategy updatePutAwayStrategy, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updatePutAwayStrategy, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "putAwayStrategy/update")
                            .queryParam("languageId", languageId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("brand", brand)
                            .queryParam("article", article)
                            .queryParam("category", category)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<PutAwayStrategy> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayStrategy.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete PutAwayStrategy

    public boolean deletePutAwayStrategy(String languageId, String companyCodeId, String plantId, String warehouseId,
                                 String brand, String article, String category, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "putAwayStrategy/delete")
                            .queryParam("languageId", languageId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("brand", brand)
                            .queryParam("article", article)
                            .queryParam("category", category)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST - findPutAwayStrategy
    public PutAwayStrategy[] findPutAwayStrategy(FindPutAwayStrategy findPutAwayStrategy, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "putAwayStrategy/findPutAwayStrategy");
            HttpEntity<?> entity = new HttpEntity<>(findPutAwayStrategy, headers);

            ResponseEntity<PutAwayStrategy[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayStrategy[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
