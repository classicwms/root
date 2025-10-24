package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.dto.*;
import com.tekclover.wms.api.outbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.outbound.transaction.model.outbound.StorageBinPutAway;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.InboundOrderCancelInput;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MastersService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getMastersServiceApiUrl() {
        return propertiesConfig.getMastersServiceUrl();
    }


    //--------------------------------------------------------------------------------------------------------------------
    // GET ImBasicData1
    public ImBasicData1 getImBasicData1ByItemCode(String itemCode, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            String url = getMastersServiceApiUrl() + "imbasicdata1/" + itemCode + "?warehouseId=" + warehouseId;
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            URI uri = builder.build(false).toUri();
            log.info("uri-----------> : " + uri);
            ResponseEntity<ImBasicData1> result = getRestTemplate().exchange(uri, HttpMethod.GET, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
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

    /**
     * @param itemCode
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param uomId
     * @param manufacturerPartNo
     * @param authToken
     * @return
     */
    public ImBasicData1 getImBasicData1ByItemCode(String itemCode, String languageId, String companyCodeId, String plantId,
                                                  String warehouseId, String uomId, String manufacturerPartNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("manufacturerPartNo", manufacturerPartNo)
                            .queryParam("uomId", uomId);
            ResponseEntity<ImBasicData1> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param itemCode
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param manufacturerPartNo
     * @param authToken
     * @return
     */
    public ImBasicData1 getImBasicData1ByItemCodeV2(String itemCode, String languageId, String companyCodeId, String plantId,
                                                    String warehouseId, String manufacturerPartNo, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/v2/" + itemCode)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("manufacturerPartNo", manufacturerPartNo);
            ResponseEntity<ImBasicData1> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
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
     * @param itemCode
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param authToken
     * @return
     */
    public ImBasicData1 getImBasicData1ByItemCode(String itemCode, String languageId, String companyCodeId, String plantId,
                                                  String warehouseId, String authToken) {
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
                            .queryParam("plantId", plantId);
            ResponseEntity<ImBasicData1> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
            return result.getBody();
        } catch (Exception e) {
            return null;
        }
    }

//    // POST - /imbasicdata1
//    public ImBasicData1 createImBasicData1(ImBasicData1 newImBasicData1, String loginUserID, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//            HttpEntity<?> entity = new HttpEntity<>(newImBasicData1, headers);
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1")
//                    .queryParam("loginUserID", loginUserID);
//            ResponseEntity<ImBasicData1> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
//                    ImBasicData1.class);
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

//    /**
//     *
//     * @param newImBasicData1
//     * @param loginUserID
//     * @param authToken
//     * @return
//     */
//    public ImBasicData1V2 createImBasicData1V2(ImBasicData1V2 newImBasicData1, String loginUserID, String authToken) throws Exception {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//            HttpEntity<?> entity = new HttpEntity<>(newImBasicData1, headers);
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/v2")
//                    .queryParam("loginUserID", loginUserID);
//            ResponseEntity<ImBasicData1V2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
//                    ImBasicData1V2.class);
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

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

    //------------------------------StorageBin--------------------------------------------------------------
    // /{storageBin}
    // GET StorageBin - /{warehouseId}/bins/{binClassId}
    public StorageBin getStorageBin(String storageBin, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + storageBin);
            ResponseEntity<StorageBin> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin - /{storageBin}/warehouseId
    public StorageBin getStorageBin(String storageBin, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + storageBin + "/warehouseId")
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageBin> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin - /{warehouseId}/bins/{binClassId}
    public StorageBin getStorageBin(String warehouseId, Long binClassId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/bins/" + binClassId);
            ResponseEntity<StorageBin> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
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


    // GET StorageBin - /{storageBin}/warehouseId - V2
    public StorageBinV2 getStorageBinV2(String storageBin, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/v2/" + storageBin + "/warehouseId")
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBin - /{storageBin}/warehouseId - V2
    public StorageBinV2 getStorageBinV2(String storageBin, String warehouseId,
                                        String companyCode, String plantId,
                                        String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + storageBin + "/warehouseId/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageBinV2> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinV2.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//     GET StorageBin - /{warehouseId}/status
//    public StorageBin[] getStorageBinByStatus(String warehouseId, Long statusId, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            UriComponentsBuilder builder =
//                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/status")
//                            .queryParam("statusId", statusId);
//            ResponseEntity<StorageBin[]> result =
//                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    // GET StorageBin - /{warehouseId}/status - V2
    public StorageBinV2[] getStorageBinByStatusV2(String companyCode, String plantId, String languageId, String warehouseId, Long statusId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/status/v2")
                            .queryParam("companyCode", companyCode)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("statusId", statusId);
            ResponseEntity<StorageBinV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinV2[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//    // GET StorageBin - /{warehouseId}/status
//    public StorageBin[] getStorageBinByStatusNotEqual(String warehouseId, Long statusId, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            UriComponentsBuilder builder =
//                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/status-not-equal")
//                            .queryParam("statusId", statusId);
//            ResponseEntity<StorageBin[]> result =
//                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
//

    // GET By Storage Section Id - /sectionId
    public StorageBin[] getStorageBinBySectionId(String warehouseId, List<String> stSectionIds, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/sectionId")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("stSectionIds", stSectionIds);
            ResponseEntity<StorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PATCH
    public StorageBin updateStorageBin(String storageBin, StorageBin modifiedStorageBin, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBin, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + storageBin)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageBin> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBin.class);
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

    //------------------------------HandlingEquipment--------------------------------------------------------------
    // GET HandlingEquipment - /{heBarcode}/barCode
//    public HandlingEquipment getHandlingEquipment(String warehouseId, String heBarcode, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            UriComponentsBuilder builder =
//                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "handlingequipment/" + heBarcode + "/barCode")
//                            .queryParam("warehouseId", warehouseId);
//            ResponseEntity<HandlingEquipment> result =
//                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    //------------------------------Business Partner----------------------------------------------------------------
    public BusinessPartner getBusinessPartner(String partnerCode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "businesspartner/" + partnerCode);
            ResponseEntity<BusinessPartner> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner.class);
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

//    //------------------------------Business Partner----------------------------------------------------------------
//    public BusinessPartner getBusinessPartnerV5(String partnerCode, String companyCodeId, String plantId, String warehouseId, String languageId, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            UriComponentsBuilder builder =
//                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "businesspartner/" + partnerCode)
//                            .queryParam("companyCodeId", companyCodeId)
//                            .queryParam("plantId", plantId)
//                            .queryParam("warehouseId", warehouseId)
//                            .queryParam("languageId", languageId);
//            ResponseEntity<BusinessPartner> result =
//                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner.class);
//            return result.getBody();
//        } catch (Exception e) {
//            throw e;
//        }
//    }

//    public BusinessPartnerV2 getBusinessPartnerV2(String companyCodeId, String plantId, String languageId,
//                                                  String warehouseId, Long businessPartnerType, String partnerCode, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "Classic WMS's RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//            UriComponentsBuilder builder =
//                    UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "businesspartner/" + partnerCode)
//                            .queryParam("companyCodeId", companyCodeId)
//                            .queryParam("plantId", plantId)
//                            .queryParam("languageId", languageId)
//                            .queryParam("warehouseId", warehouseId)
//                            .queryParam("businessPartnerType", businessPartnerType);
//            ResponseEntity<BusinessPartnerV2> result =
//                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartnerV2.class);
//            return result.getBody();
//        } catch (Exception e) {
//            throw e;
//        }
//    }
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
}