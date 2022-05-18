package com.tekclover.wms.core.service;

import java.util.Collections;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.enterprise.Barcode;
import com.tekclover.wms.core.model.enterprise.BatchSerial;
import com.tekclover.wms.core.model.enterprise.Company;
import com.tekclover.wms.core.model.enterprise.Floor;
import com.tekclover.wms.core.model.enterprise.ItemGroup;
import com.tekclover.wms.core.model.enterprise.ItemType;
import com.tekclover.wms.core.model.enterprise.Plant;
import com.tekclover.wms.core.model.enterprise.SearchBarcode;
import com.tekclover.wms.core.model.enterprise.SearchBatchSerial;
import com.tekclover.wms.core.model.enterprise.SearchCompany;
import com.tekclover.wms.core.model.enterprise.SearchFloor;
import com.tekclover.wms.core.model.enterprise.SearchItemGroup;
import com.tekclover.wms.core.model.enterprise.SearchItemType;
import com.tekclover.wms.core.model.enterprise.SearchPlant;
import com.tekclover.wms.core.model.enterprise.SearchStorageBinType;
import com.tekclover.wms.core.model.enterprise.SearchStorageClass;
import com.tekclover.wms.core.model.enterprise.SearchStorageSection;
import com.tekclover.wms.core.model.enterprise.SearchStorageType;
import com.tekclover.wms.core.model.enterprise.SearchStrategies;
import com.tekclover.wms.core.model.enterprise.SearchVariant;
import com.tekclover.wms.core.model.enterprise.SearchWarehouse;
import com.tekclover.wms.core.model.enterprise.StorageBinType;
import com.tekclover.wms.core.model.enterprise.StorageClass;
import com.tekclover.wms.core.model.enterprise.StorageSection;
import com.tekclover.wms.core.model.enterprise.StorageType;
import com.tekclover.wms.core.model.enterprise.Strategies;
import com.tekclover.wms.core.model.enterprise.Variant;
import com.tekclover.wms.core.model.enterprise.Warehouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnterpriseSetupService {
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getEnterpriseSetupServiceApiUrl () {
		return propertiesConfig.getEnterpriseServiceUrl();
	}
	
	/**
	 * 
	 * @param name
	 * @param password
	 * @param authToken
	 * @return
	 */
	public boolean validateUser (String name, String password, String authToken) {
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
	public Barcode[] getBarcodes (String authToken) {
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
			Long levelId, String levelReference, Long processId, String authToken) {
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchBarcode, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "barcode/findBarcode");
			ResponseEntity<Barcode[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Barcode[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Barcode
	public Barcode addBarcode (Barcode barcode, String loginUserID, String authToken) {
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
	public Barcode updateBarcode (String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId, 
			Long levelId, String levelReference, Long processId, Barcode modifiedBarcode, String loginUserID, String authToken) {
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
	public boolean deleteBarcode (String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId, 
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
	public BatchSerial[] getBatchSerials (String authToken) {
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
	public BatchSerial getBatchSerial(String storageMethod, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod);
			ResponseEntity<BatchSerial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BatchSerial.class);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchBatchSerial, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/findBatchSerial");
			ResponseEntity<BatchSerial[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BatchSerial[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST BatchSerial
	public BatchSerial addBatchSerial (BatchSerial batchserial, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(batchserial, headers);
			ResponseEntity<BatchSerial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BatchSerial.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch BatchSerial
	public BatchSerial updateBatchSerial (String storageMethod, BatchSerial modifiedBatchSerial, String loginUserID, String authToken) {
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<BatchSerial> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BatchSerial.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete BatchSerial
	public boolean deleteBatchSerial (String storageMethod, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "batchserial/" + storageMethod)
					.queryParam("loginUserID", loginUserID);
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
	public Company[] getCompanies (String authToken) {
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
	public Company getCompany(String companyMasterId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyMasterId);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchCompany, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/findCompany");
			ResponseEntity<Company[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Company[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Company
	public Company addCompany (Company companyMaster, String loginUserID, String authToken) {
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
	public Company updateCompany (String companyMasterId,  String loginUserID, Company modifiedCompany, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyMasterId)
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
	public boolean deleteCompany (String companyMasterId,  String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "company/" + companyMasterId)
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
	public Floor[] getFloors (String authToken) {
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
	public Floor getFloor(Long floorId, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/" + floorId)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchFloor, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/findFloor");
			ResponseEntity<Floor[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Floor[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Floor
	public Floor addFloor (Floor floor, String loginUserID, String authToken) {
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
	public Floor updateFloor (Long floorId, String warehouseId, Floor modifiedFloor, String loginUserID, String authToken) {
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
	public boolean deleteFloor (Long floorId, String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "floor/" + floorId)
					.queryParam("warehouseId", warehouseId)	
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
	public ItemGroup[] getItemGroups (String authToken) {
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
	public ItemGroup getItemGroup(String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroup, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroupId", itemGroupId)
					.queryParam("subItemGroup", subItemGroup);
					
			ResponseEntity<ItemGroup> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup.class);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchItemGroup, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/findItemGroup");
			ResponseEntity<ItemGroup[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ItemGroup
	public ItemGroup addItemGroup (ItemGroup itemgroup, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(itemgroup, headers);
			ResponseEntity<ItemGroup> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroup.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch ItemGroup
	public ItemGroup updateItemGroup (Long itemGroupId, String warehouseId, Long itemTypeId, Long subItemGroup, 
			ItemGroup modifiedItemGroup, String loginUserID, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemGroupId)
					.queryParam("loginUserID", loginUserID)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("subItemGroup", subItemGroup);
			
			ResponseEntity<ItemGroup> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemGroup.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete ItemGroup
	public boolean deleteItemGroup (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroup,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemgroup/" + itemGroupId)
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
	public ItemType[] getItemTypes (String authToken) {
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
	public ItemType getItemType(String warehouseId, Long itemTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/" + itemTypeId)
					.queryParam("warehouseId", warehouseId);
						
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchItemType, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemType/findItemType");
			ResponseEntity<ItemType[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemType[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ItemType
	public ItemType addItemType (ItemType itemtype, String loginUserID, String authToken) {
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
	public ItemType updateItemType (String warehouseId, Long itemTypeId, ItemType modifiedItemType, 
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
	public boolean deleteItemType (String warehouseId, Long itemTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "itemtype/" + itemTypeId)
					.queryParam("warehouseId", warehouseId)
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
	public Plant[] getPlants (String authToken) {
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
	public Plant getPlant(String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/" + plantId);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchPlant, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/findPlant");
			ResponseEntity<Plant[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Plant[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Plant
	public Plant addPlant (Plant plant, String loginUserID, String authToken) {
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
	public Plant updatePlant (String plantId, Plant modifiedPlant, String loginUserID, String authToken) {
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
	public boolean deletePlant (String plantId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "plant/" + plantId)
					.queryParam("loginUserID", loginUserID);
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
	public StorageBinType[] getStorageBinTypes (String authToken) {
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
	public StorageBinType getStorageBinType(String warehouseId, Long storageTypeId, Long storageBinTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/" + storageBinTypeId)
					.queryParam("warehouseId", warehouseId)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchStorageBinType, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageBinType/findStorageBinType");
			ResponseEntity<StorageBinType[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinType[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST StorageBinType
	public StorageBinType addStorageBinType (StorageBinType storagebintype, String loginUserID, String authToken) {
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
	public StorageBinType updateStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId, StorageBinType modifiedStorageBinType, String loginUserID, String authToken) {
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
	public boolean deleteStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagebintype/" + storageBinTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageTypeId", storageTypeId)
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
	public StorageClass[] getStorageClasss (String authToken) {
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
	public StorageClass getStorageClass(String warehouseId, Long storageClassId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/" + storageClassId)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchStorageClass, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageClass/findStorageClass");
			ResponseEntity<StorageClass[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageClass[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST StorageClass
	public StorageClass addStorageClass (StorageClass storageclass, String loginUserID, String authToken) {
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
	public StorageClass updateStorageClass (String warehouseId, Long storageClassId, StorageClass modifiedStorageClass, String loginUserID, String authToken) {
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
	public boolean deleteStorageClass (String warehouseId, Long storageClassId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageclass/" + storageClassId)
					.queryParam("warehouseId", warehouseId)
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
	public StorageSection[] getStorageSections (String authToken) {
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
	public StorageSection getStorageSection(String warehouseId, Long floorId, Long storageSectionId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/" + storageSectionId)
					.queryParam("warehouseId", warehouseId)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchStorageSection, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageSection/findStorageSection");
			ResponseEntity<StorageSection[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageSection[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST StorageSection
	public StorageSection addStorageSection (StorageSection storagesection, String loginUserID, String authToken) {
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
	public StorageSection updateStorageSection (String warehouseId, Long floorId, Long storageSectionId, StorageSection modifiedStorageSection, String loginUserID, String authToken) {
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StorageSection> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageSection.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete StorageSection
	public boolean deleteStorageSection (String warehouseId, Long floorId, Long storageSectionId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagesection/" + storageSectionId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
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
	public StorageType[] getStorageTypes (String authToken) {
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
	public StorageType getStorageType(String warehouseId, Long storageClassId, Long storageTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/" + storageTypeId)
					.queryParam("warehouseId", warehouseId)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchStorageType, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storageType/findStorageType");
			ResponseEntity<StorageType[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageType[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST StorageType
	public StorageType addStorageType (StorageType storagetype, String loginUserID, String authToken) {
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
	public StorageType updateStorageType (String warehouseId, Long storageClassId, Long storageTypeId, StorageType modifiedStorageType, String loginUserID, String authToken) {
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
	public boolean deleteStorageType (String warehouseId, Long storageClassId, Long storageTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "storagetype/" + storageTypeId)
					.queryParam("warehouseId", warehouseId)
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
	public Strategies[] getStrategiess (String authToken) {
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
	public Strategies getStrategies( String warehouseId, Long strategyTypeId, Long sequenceIndicator, String strategyNo, Long priority, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/" + strategyTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("sequenceIndicator", sequenceIndicator)
					.queryParam("strategyNo", strategyNo)
					.queryParam("priority", priority);
					
			ResponseEntity<Strategies> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Strategies.class);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchStrategies, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/findStrategies");
			ResponseEntity<Strategies[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Strategies[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Strategies
	public Strategies addStrategies (Strategies strategies, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(strategies, headers);
			ResponseEntity<Strategies> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Strategies.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch Strategies
	public Strategies updateStrategies ( String warehouseId, Long strategyTypeId, Long sequenceIndicator, String strategyNo, Long priority, Strategies modifiedStrategies, String loginUserID, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/" + strategyTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("sequenceIndicator", sequenceIndicator)
					.queryParam("strategyNo", strategyNo)
					.queryParam("priority", priority)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<Strategies> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Strategies.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete Strategies
	public boolean deleteStrategies ( String warehouseId, Long strategyTypeId, Long sequenceIndicator, String strategyNo, Long priority, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "strategies/" + strategyTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("sequenceIndicator", sequenceIndicator)
					.queryParam("strategyNo", strategyNo)
					.queryParam("priority", priority)
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
	public Variant[] getVariants (String authToken) {
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
	public Variant getVariant(String variantCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantCode);
			ResponseEntity<Variant> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Variant.class);
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchVariant, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/findVariant");
			ResponseEntity<Variant[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Variant[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST Variant
	public Variant addVariant (Variant variant, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(variant, headers);
			ResponseEntity<Variant> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Variant.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch Variant
	public Variant updateVariant (String variantCode, Variant modifiedVariant, String loginUserID, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<Variant> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Variant.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete Variant
	public boolean deleteVariant (String variantCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "variant/" + variantCode)
						.queryParam("loginUserID", loginUserID);
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
	public Warehouse[] getWarehouses (String authToken) {
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
	public Warehouse getWarehouse(String warehouseId, String modeOfImplementation, Long warehouseTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/" + warehouseId)
					.queryParam("modeOfImplementation", modeOfImplementation)
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
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(searchWarehouse, headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/findWarehouse");
			ResponseEntity<Warehouse[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST Warehouse
	public Warehouse addWarehouse (Warehouse warehouse, String loginUserID, String authToken) {
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
	public Warehouse updateWarehouse (String warehouseId, Warehouse modifiedWarehouse, String loginUserID, String authToken) {
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
	public boolean deleteWarehouse (String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getEnterpriseSetupServiceApiUrl() + "warehouse/" + warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
	
	