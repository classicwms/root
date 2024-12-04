package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.enterprise.*;
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
public class EnterpriseSetupService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    /**
     * @return
     */
    private String getEnterpriseSetupServiceApiUrl() {
        return propertiesConfig.getEnterpriseServiceUrl();
    }

    /**
     * @param name
     * @param password
     * @param authToken
     * @return
     */
    public boolean validateUser(String name, String password, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "login")
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

    /* -----------------------------ENTERPRISE---BARCODE---------------------------------------------------------------*/
    // Get ALL
    public Barcode[] getBarcodes(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode");
            ResponseEntity<Barcode[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Barcode[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Barcode
    public Barcode getBarcode(String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId,
                              Long levelId, String levelReference, Long processId,
                              String companyId, String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode/" + barcodeTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("method", method)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("barcodeSubTypeId", barcodeSubTypeId)
                            .queryParam("levelId", levelId)
                            .queryParam("levelReference", levelReference)
                            .queryParam("processId", processId);

            ResponseEntity<Barcode> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Barcode.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // FIND - Barcode
    public Barcode[] findBarcode(SearchBarcode searchBarcode, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode/findBarcode");
            HttpEntity<?> entity = new HttpEntity<>(searchBarcode, headers);
            ResponseEntity<Barcode[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Barcode[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Barcode
    public Barcode addBarcode(Barcode barcode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(barcode, headers);
            ResponseEntity<Barcode> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Barcode.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Barcode
    public Barcode updateBarcode(String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId,
                                 String companyId, String plantId, String languageId,
                                 Long levelId, String levelReference, Long processId,
                                 Barcode modifiedBarcode, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBarcode, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode/" + barcodeTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("method", method)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("companyId", companyId)
                            .queryParam("barcodeSubTypeId", barcodeSubTypeId)
                            .queryParam("levelId", levelId)
                            .queryParam("levelReference", levelReference)
                            .queryParam("processId", processId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Barcode> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Barcode.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Barcode
    public boolean deleteBarcode(String warehouseId, String method, Long barcodeTypeId,
                                 Long barcodeSubTypeId, String companyId, String languageId, String plantId,
                                 Long levelId, String levelReference, Long processId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode/" + barcodeTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("method", method)
                            .queryParam("barcodeSubTypeId", barcodeSubTypeId)
                            .queryParam("levelId", levelId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("levelReference", levelReference)
                            .queryParam("processId", processId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---BATCHSERIAL---------------------------------------------------------------*/
    // Get ALL
    public BatchSerial[] getBatchSerials(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial");
            ResponseEntity<BatchSerial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET BatchSerial
    public BatchSerial[] getBatchSerial(String storageMethod, String companyId, String languageId,
                                        String plantId, String warehouseId, Long levelId,
                                        String maintenance, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod)
                    .queryParam("companyId", companyId)
                    .queryParam("languageId", languageId)
                    .queryParam("plantId", plantId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("levelId", levelId)
                    .queryParam("maintenance", maintenance);
            ResponseEntity<BatchSerial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find - BatchSerial
    public BatchSerial[] findBatchSerial(SearchBatchSerial searchBatchSerial, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/findBatchSerial");
            HttpEntity<?> entity = new HttpEntity<>(searchBatchSerial, headers);
            ResponseEntity<BatchSerial[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST BatchSerial
    public BatchSerial[] addBatchSerial(List<AddBatchSerial> batchserial, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(batchserial, headers);
            ResponseEntity<BatchSerial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
//	// POST BatchSerial
//	public BatchSerial addBatchSerial (AddBatchSerial batchserial, String loginUserID, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "Classic WMS's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial")
//					.queryParam("loginUserID", loginUserID);
//
//			HttpEntity<?> entity = new HttpEntity<>(batchserial, headers);
//			ResponseEntity<BatchSerial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BatchSerial.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

    // Patch BatchSerial
    public BatchSerial[] updateBatchSerial(String storageMethod, String companyId, String languageId, String maintenance,
                                           String plantId, String warehouseId, Long levelId, List<UpdateBatchSerial> modifiedBatchSerial,
                                           String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedBatchSerial, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("levelId", levelId)
                            .queryParam("maintenance", maintenance)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<BatchSerial[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BatchSerial[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //	// Patch BatchSerial
//	public BatchSerial updateBatchSerial (String storageMethod,BatchSerial modifiedBatchSerial, String loginUserID, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "Classic WMS's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			HttpEntity<?> entity = new HttpEntity<>(modifiedBatchSerial, headers);
//
//			HttpClient client = HttpClients.createDefault();
//			RestTemplate restTemplate = getRestTemplate();
//			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
//
//			UriComponentsBuilder builder =
//					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod)
//							.queryParam("loginUserID", loginUserID);
//
//			ResponseEntity<BatchSerial> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BatchSerial.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
// Delete BatchSerial
    public boolean deleteBatchSerial(String storageMethod, String companyId, String plantId,
                                     String warehouseId, String maintenance,
                                     Long levelId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("levelId", levelId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("maintenance", maintenance);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /* -----------------------------ENTERPRISE---COMPANY---------------------------------------------------------------*/
    // Get ALL
    public Company[] getCompanies(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company");
            ResponseEntity<Company[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Company[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Company
    public Company getCompany(String companyId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyId)
                    .queryParam("languageId", languageId);
            ResponseEntity<Company> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Company.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findCompany
    public Company[] findCompany(SearchCompany searchCompany, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/findCompany");
            HttpEntity<?> entity = new HttpEntity<>(searchCompany, headers);
            ResponseEntity<Company[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Company[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Company
    public Company addCompany(Company companyMaster, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(companyMaster, headers);
            ResponseEntity<Company> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Company.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Company
    public Company updateCompany(String companyId, String languageId, String loginUserID,
                                 Company modifiedCompany, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedCompany, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Company> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Company.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Company
    public boolean deleteCompany(String companyId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---FLOOR---------------------------------------------------------------*/
    // Get ALL
    public Floor[] getFloors(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor");
            ResponseEntity<Floor[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Floor[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Floor
    public Floor getFloor(Long floorId, String warehouseId, String companyId,
                          String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/" + floorId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId);
            ResponseEntity<Floor> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Floor.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findFloor
    public Floor[] findFloor(SearchFloor searchFloor, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/findFloor");
            HttpEntity<?> entity = new HttpEntity<>(searchFloor, headers);
            ResponseEntity<Floor[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Floor[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Floor
    public Floor addFloor(Floor floor, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(floor, headers);
            ResponseEntity<Floor> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Floor.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Floor
    public Floor updateFloor(Long floorId, String warehouseId, String companyId,
                             String plantId, String languageId, Floor modifiedFloor,
                             String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedFloor, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/" + floorId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Floor> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Floor.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Floor
    public boolean deleteFloor(Long floorId, String warehouseId, String companyId,
                               String plantId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/" + floorId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyId", companyId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---ITEMGROUP---------------------------------------------------------------*/

    // Get ALL
    public ItemGroup[] getItemGroups(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup");
            ResponseEntity<ItemGroup[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ItemGroup
    public ItemGroup[] getItemGroup(String companyId, String languageId, String plantId,
                                    String warehouseId, Long itemTypeId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("itemTypeId", itemTypeId);

            ResponseEntity<ItemGroup[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findItemGroup
    public ItemGroup[] findItemGroup(SearchItemGroup searchItemGroup, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/findItemGroup");
            HttpEntity<?> entity = new HttpEntity<>(searchItemGroup, headers);
            ResponseEntity<ItemGroup[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroup[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ItemGroup
    public ItemGroup[] addItemGroup(List<ItemGroup> itemgroup, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(itemgroup, headers);
            ResponseEntity<ItemGroup[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroup[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ItemGroup
    public ItemGroup[] updateItemGroup(String warehouseId, Long itemTypeId, List<ItemGroup> modifiedItemGroup,
                                       String companyId, String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedItemGroup, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemTypeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("itemTypeId", itemTypeId);

            ResponseEntity<ItemGroup[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemGroup[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ItemGroup
    public boolean deleteItemGroup(String warehouseId, Long itemTypeId, String companyId, String languageId,
                                   String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemTypeId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("itemTypeId", itemTypeId)
                            .queryParam("companyId", companyId)
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


    /* -----------------------------ENTERPRISE---EMPLOYEE---------------------------------------------------------------*/

    // Get ALL
    public Employee[] getEMployees(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee");
            ResponseEntity<Employee[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Employee[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Employee
    public Employee getEmployee(String employee, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee/" + employee);

            ResponseEntity<Employee> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Employee.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
//
//	// GET - findEmployee
//	public Employee[] findEmployee(SearchItemGroup searchItemGroup, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "Classic WMS's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			HttpEntity<?> entity = new HttpEntity<>(searchItemGroup, headers);
//			UriComponentsBuilder builder =
//					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee/findEmployee");
//			ResponseEntity<Employee[]> result =
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Employee[].class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

    // POST Employee
    public Employee addEmployee(Employee employee, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(employee, headers);
            ResponseEntity<Employee> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Employee.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ItemGroup
    public Employee updateEmployee(String employee, Employee modifiedEmployee, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedEmployee, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee/" + employee)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Employee> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Employee.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ItemGroup
    public boolean deleteEmployee(String employee, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "employee/" + employee)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---ITEMTYPE---------------------------------------------------------------*/

    // Get ALL
    public ItemType[] getItemTypes(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype");
            ResponseEntity<ItemType[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET ItemType
    public ItemType getItemType(String warehouseId, Long itemTypeId, String companyId,
                                String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/" + itemTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);

            ResponseEntity<ItemType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findItemType
    public ItemType[] findItemType(SearchItemType searchItemType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/findItemType");
            HttpEntity<?> entity = new HttpEntity<>(searchItemType, headers);
            ResponseEntity<ItemType[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST ItemType
    public ItemType addItemType(ItemType itemtype, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(itemtype, headers);
            ResponseEntity<ItemType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch ItemType
    public ItemType updateItemType(String warehouseId, Long itemTypeId, String companyId,
                                   String plantId, String languageId, ItemType modifiedItemType,
                                   String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedItemType, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/" + itemTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<ItemType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete ItemType
    public boolean deleteItemType(String warehouseId, Long itemTypeId, String companyId,
                                  String plantId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/" + itemTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---PLANT---------------------------------------------------------------*/

    // Get ALL
    public Plant[] getPlants(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant");
            ResponseEntity<Plant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Plant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Plant
    public Plant getPlant(String plantId, String companyId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/" + plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("companyId", companyId);
            ResponseEntity<Plant> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Plant.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findPlant
    public Plant[] findPlant(SearchPlant searchPlant, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/findPlant");
            HttpEntity<?> entity = new HttpEntity<>(searchPlant, headers);
            ResponseEntity<Plant[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Plant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // POST Plant
    public Plant addPlant(Plant plant, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(plant, headers);
            ResponseEntity<Plant> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Plant.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Plant
    public Plant updatePlant(String plantId, Plant modifiedPlant, String companyId,
                             String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedPlant, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/" + plantId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Plant> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Plant.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Plant
    public boolean deletePlant(String plantId, String companyId,
                               String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/" + plantId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("languageId", languageId)
                            .queryParam("companyId", companyId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---STORAGEBINTYPE---------------------------------------------------------------*/

    // Get ALL
    public StorageBinType[] getStorageBinTypes(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype");
            ResponseEntity<StorageBinType[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageBinType
    public StorageBinType getStorageBinType(String warehouseId, Long storageTypeId, Long storageClassId,
                                            Long storageBinTypeId, String companyId, String languageId,
                                            String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/" + storageBinTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("storageClassId", storageClassId)
                            .queryParam("storageTypeId", storageTypeId);

            ResponseEntity<StorageBinType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findStorageBinType
    public StorageBinType[] findStorageBinType(SearchStorageBinType searchStorageBinType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/findStorageBinType");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageBinType, headers);
            ResponseEntity<StorageBinType[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // POST StorageBinType
    public StorageBinType addStorageBinType(StorageBinType storagebintype, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storagebintype, headers);
            ResponseEntity<StorageBinType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageBinType
    public StorageBinType updateStorageBinType(String warehouseId, Long storageTypeId, Long storageClassId,
                                               Long storageBinTypeId, String companyId, String languageId,
                                               String plantId, StorageBinType modifiedStorageBinType, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBinType, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/" + storageBinTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("storageClassId", storageClassId)
                            .queryParam("storageTypeId", storageTypeId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageBinType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBinType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageBinType
    public boolean deleteStorageBinType(String warehouseId, Long storageTypeId, Long storageClassId,
                                        Long storageBinTypeId, String companyId, String languageId,
                                        String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/" + storageBinTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("storageTypeId", storageTypeId)
                            .queryParam("storageClassId", storageClassId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---STORAGECLASS---------------------------------------------------------------*/

    // Get ALL
    public StorageClass[] getStorageClasss(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass");
            ResponseEntity<StorageClass[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageClass[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageClass
    public StorageClass getStorageClass(String warehouseId, Long storageClassId, String companyId,
                                        String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/" + storageClassId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("companyId", companyId)
                            .queryParam("warehouseId", warehouseId);

            ResponseEntity<StorageClass> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageClass.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findStorageClass
    public StorageClass[] findStorageClass(SearchStorageClass searchStorageClass, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/findStorageClass");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageClass, headers);
            ResponseEntity<StorageClass[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageClass[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST StorageClass
    public StorageClass addStorageClass(StorageClass storageclass, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storageclass, headers);
            ResponseEntity<StorageClass> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageClass.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageClass
    public StorageClass updateStorageClass(String warehouseId, Long storageClassId,
                                           String companyId, String languageId, String plantId,
                                           StorageClass modifiedStorageClass, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageClass, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/" + storageClassId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageClass> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageClass.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageClass
    public boolean deleteStorageClass(String warehouseId, Long storageClassId, String companyId,
                                      String plantId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/" + storageClassId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---STORAGESECTION---------------------------------------------------------------*/

    // Get ALL
    public StorageSection[] getStorageSections(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection");
            ResponseEntity<StorageSection[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageSection[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageSection
    public StorageSection getStorageSection(String warehouseId, Long floorId, String storageSectionId,
                                            String companyId, String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/" + storageSectionId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("floorId", floorId);

            ResponseEntity<StorageSection> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageSection.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findStorageSection
    public StorageSection[] findStorageSection(SearchStorageSection searchStorageSection, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/findStorageSection");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageSection, headers);
            ResponseEntity<StorageSection[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageSection[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST StorageSection
    public StorageSection addStorageSection(StorageSection storagesection, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storagesection, headers);
            ResponseEntity<StorageSection> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageSection.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageSection
    public StorageSection updateStorageSection(String warehouseId, Long floorId, String storageSectionId,
                                               String companyId, String languageId, String plantId,
                                               StorageSection modifiedStorageSection, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageSection, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/" + storageSectionId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("floorId", floorId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageSection> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageSection.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageSection//warehouseId, floorId, storageSectionId,companyId,plantId,languageId,loginUserID, authToken
    public boolean deleteStorageSection(String warehouseId, Long floorId, String storageSectionId,
                                        String companyId, String plantId, String languageId,
                                        String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/" + storageSectionId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("floorId", floorId)
                            .queryParam("companyId", companyId)
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

    /* -----------------------------ENTERPRISE---STORAGETYPE---------------------------------------------------------------*/

    // Get ALL
    public StorageType[] getStorageTypes(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype");
            ResponseEntity<StorageType[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET StorageType
    public StorageType getStorageType(String warehouseId, Long storageClassId, Long storageTypeId,
                                      String companyId, String languageId, String plantId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/" + storageTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("storageClassId", storageClassId);

            ResponseEntity<StorageType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findStorageType
    public StorageType[] findStorageType(SearchStorageType searchStorageType, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/findStorageType");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageType, headers);
            ResponseEntity<StorageType[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageType[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST StorageType
    public StorageType addStorageType(StorageType storagetype, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(storagetype, headers);
            ResponseEntity<StorageType> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch StorageType
    public StorageType updateStorageType(String warehouseId, Long storageClassId, Long storageTypeId,
                                         StorageType modifiedStorageType, String companyId,
                                         String languageId, String plantId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStorageType, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/" + storageTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("storageClassId", storageClassId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<StorageType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageType.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete StorageType
    public boolean deleteStorageType(String warehouseId, Long storageClassId, Long storageTypeId,
                                     String companyId, String languageId, String plantId,
                                     String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/" + storageTypeId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("plantId", plantId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("storageClassId", storageClassId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---STRATEGIES---------------------------------------------------------------*/

    // Get ALL
    public Strategies[] getStrategiess(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies");
            ResponseEntity<Strategies[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Strategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Strategies
    public Strategies[] getStrategies(String companyId, String languageId,
                                      String plantId, String warehouseId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/strategies")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId);

            ResponseEntity<Strategies[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Strategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findStrategies
    public Strategies[] findStrategies(SearchStrategies searchStrategies, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/findStrategies");
            HttpEntity<?> entity = new HttpEntity<>(searchStrategies, headers);
            ResponseEntity<Strategies[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Strategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Strategies
    public Strategies[] addStrategies(List<Strategies> strategies, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies")
                            .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(strategies, headers);
            ResponseEntity<Strategies[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Strategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Strategies
    public Strategies[] updateStrategies(String companyId, String languageId, String plantId, String warehouseId,
                                         List<Strategies> modifiedStrategies,
                                         String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedStrategies, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/strategies")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Strategies[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Strategies[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Strategies
    public boolean deleteStrategies(String companyId, String languageId, String plantId,
                                    String warehouseId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/strategies")
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("companyId", companyId)
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
    /* -----------------------------ENTERPRISE---VARIANT---------------------------------------------------------------*/

    // Get ALL
    public Variant[] getVariants(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant");
            ResponseEntity<Variant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Variant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Variant
    public Variant[] getVariant(String variantId, String companyId, String languageId, String plantId,
                                Long levelId, String warehouseId, String variantSubId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantId)
                    .queryParam("companyId", companyId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("levelId", levelId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("variantSubId", variantSubId);
            ResponseEntity<Variant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Variant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findVariant
    public Variant[] findVariant(SearchVariant searchVariant, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/findVariant");
            HttpEntity<?> entity = new HttpEntity<>(searchVariant, headers);
            ResponseEntity<Variant[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Variant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Variant
    public Variant[] addVariant(List<AddVariant> variant, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(variant, headers);
            ResponseEntity<Variant[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Variant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Variant
    public Variant[] updateVariant(String variantId, List<UpdateVariant> modifiedVariant, String companyId,
                                   String plantId, String warehouseId, String languageId, Long levelId,
                                   String variantSubId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedVariant, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("levelId", levelId)
                            .queryParam("variantSubId", variantSubId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Variant[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Variant[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Variant
    public boolean deleteVariant(String variantId, String companyId, String plantId, String warehouseId, String variantSubId,
                                 String languageId, Long levelId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId)
                            .queryParam("variantSubId", variantSubId)
                            .queryParam("levelId", levelId);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* -----------------------------ENTERPRISE---WAREHOUSE---------------------------------------------------------------*/

    // Get ALL
    public Warehouse[] getWarehouses(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse");
            ResponseEntity<Warehouse[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET Warehouse
    public Warehouse getWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId,
                                  String companyId, String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/" + warehouseId)
                            .queryParam("modeOfImplementation", modeOfImplementation)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseTypeId", warehouseTypeId);
            ResponseEntity<Warehouse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - findWarehouse
    public Warehouse[] findWarehouse(SearchWarehouse searchWarehouse, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/findWarehouse");
            HttpEntity<?> entity = new HttpEntity<>(searchWarehouse, headers);
            ResponseEntity<Warehouse[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Warehouse[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // POST Warehouse
    public Warehouse addWarehouse(Warehouse warehouse, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse")
                    .queryParam("loginUserID", loginUserID);

            HttpEntity<?> entity = new HttpEntity<>(warehouse, headers);
            ResponseEntity<Warehouse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Warehouse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Patch Warehouse
    public Warehouse updateWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId,
                                     String companyId, String plantId, String languageId, Warehouse modifiedWarehouse,
                                     String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(modifiedWarehouse, headers);

            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/" + warehouseId)
                            .queryParam("modeOfImplementation", modeOfImplementation)
                            .queryParam("warehouseTypeId", warehouseTypeId)
                            .queryParam("companyId", companyId)
                            .queryParam("languageId", languageId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID);

            ResponseEntity<Warehouse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Warehouse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Delete Warehouse
    public boolean deleteWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId,
                                   String companyId, String plantId, String languageId, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/" + warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("modeOfImplementation", modeOfImplementation)
                            .queryParam("warehouseTypeId", warehouseTypeId)
                            .queryParam("loginUserID", loginUserID);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
            log.info("result : " + result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //======================================================================================================================

    /**
     *
     * @param fileName
     * @param loginUserID
     * @throws Exception
     */
    public void extractPdf(String companyCodeId, String plantId, String languageId, String warehouseId,
                           String preOutboundNo, String fileName, String loginUserID) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            AuthToken enterPriseAuthToken = authTokenService.getEnterpriseServiceAuthToken();
            String authToken = enterPriseAuthToken.getAccess_token();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "pdf/extract/v2")
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("preOutboundNo", preOutboundNo)
                    .queryParam("fileName", fileName)
                    .queryParam("loginUserId", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            log.info(result.getBody());
        } catch (Exception e) {
            log.error("Exception while pdf Extract : " + e.toString());
            throw e;
        }
    }
}