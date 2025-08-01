package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.*;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundOrderCancelInput;
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

@Slf4j
@Service
public class MastersService {

    @Autowired
    PropertiesConfig propertiesConfig;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Autowired
    AuthTokenService authTokenService;

    // Master_Service_Url
    private String getMastersServiceApiUrl() {
        return propertiesConfig.getMastersServiceUrl();
    }

    //ID_MasterService_Url
    private String getIDMasterServiceApiUrl () {
        return propertiesConfig.getIdmasterServiceUrl();
    }


    //--------------------------------------------------------------------------------------------------------------------
    /**
     *
     * @param newImBasicData1
     * @param loginUserID
     * @param authToken
     * @return
     */
    public ImBasicData1V2 createImBasicData1V2(ImBasicData1V2 newImBasicData1, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(newImBasicData1, headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/v2")
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<ImBasicData1V2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    ImBasicData1V2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    // GET
    public String getNextNumberRange(Long numberRangeCode, String warehouseId,
                                     String companyCodeId, String plantId,
                                     String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() +
                                    "numberrange/nextNumberRange/" + numberRangeCode + "/v2")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param imBasicData
     * @param authToken
     * @return
     */
    public ImBasicData1 getImBasicData1ByItemCodeV2(ImBasicData imBasicData, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(imBasicData, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/v2/itemMaster");
            ResponseEntity<ImBasicData1> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param itemCode
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param uomId
     * @param authToken
     * @return
     */
    public ImBasicData1 getImBasicData1ByItemCode(String itemCode, String languageId, String companyCodeId, String plantId,
                                                  String warehouseId, String uomId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/" + itemCode)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("uomId", uomId);
            ResponseEntity<ImBasicData1> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    //----------------------BOMHeader---------------------------------------------------------------------------

    // GET BomHeader
    public BomHeader getBomHeader(String parentItemCode, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "bomheader/" + parentItemCode)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<BomHeader> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomHeader.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // GET BomHeader-with companyCode
    public BomHeader getBomHeader(String parentItemCode, String warehouseId,
                                  String companyCode, String plantId, String languageId,
                                  String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "bomheader/" + parentItemCode)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<BomHeader> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomHeader.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // GET BomLine - /{bomNumber}/bomline
    public BomLine[] getBomLine(Long bomLineNo, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "bomline/" + bomLineNo + "/bomline")
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<BomLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
//			throw e;
            return null;
        }
    }

    // GET BomLine - /{bomNumber}/bomline - with CompanyCode,Plant and Language Id's
    public BomLine[] getBomLine(Long bomLineNo, String companyCode, String plantId, String languageId, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "bomline/" + bomLineNo + "/bomline")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<BomLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
//			throw e;
            return null;
        }
    }

    /**
     *
     * @param storageBinPutAway
     * @param authToken
     * @return
     */
    public StorageBin[] getStorageBin(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/putaway");
            ResponseEntity<StorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin - /{warehouseId}/bins/{binClassId} - v2
    public StorageBinV2 getStorageBin(String companyCode, String plantId,
                                      String languageId, String warehouseId, Long binClassId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/v2/" + warehouseId + "/bins/" + binClassId)
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId);
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Get a Bin
    public StorageBinV2 getaStorageBinV2(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/bin/v2");
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH - V2
    public StorageBinV2 updateStorageBinV2(String storageBin, StorageBinV2 modifiedStorageBin,
                                           String companyCodeId, String plantId,
                                           String languageId, String warehouseId,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBin, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/v2/" + storageBin)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageBinV2> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Send EMail
    public String sendMail(InboundOrderCancelInput inboundOrderCancelInput) {
        try {
            AuthToken authTokenForMasterService = authTokenService.getMastersServiceAuthToken();
            String authToken = authTokenForMasterService.getAccess_token();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "email/sendMail");
            HttpEntity<?> entity = new HttpEntity<>(inboundOrderCancelInput, headers);
            ResponseEntity<String> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

    //getStorageBinBinClassId7 - Proposed Bin
    public StorageBinV2 getStorageBinBinClassId7(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/putaway/binClass/v2");
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //NON CBM Proposed Existing Bin
    public StorageBinV2 getExistingStorageBinNonCbm(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/putaway/nonCbm/existing/v2");
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //NON CBM Proposed Bin
    public StorageBinV2 getStorageBinNonCbm(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/putaway/nonCbm/v2");
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //NON CBM Proposed Bin - LastPicked Bin
    public StorageBinV2 getStorageBinNonCbmLastPicked(StorageBinPutAway storageBinPutAway, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(storageBinPutAway, headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/putaway/nonCbm/lastPicked/v2");
            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}