package com.tekclover.wms.api.transaction.service;

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

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.model.dto.BomHeader;
import com.tekclover.wms.api.transaction.model.dto.BomLine;
import com.tekclover.wms.api.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.transaction.model.dto.HandlingEquipment;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;

import lombok.extern.slf4j.Slf4j;

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
	
	private String getMastersServiceApiUrl () {
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
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1/" + itemCode)
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<ImBasicData1> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
			return result.getBody();
		} catch (Exception e) {
			return null;
		}
	}
	
	// POST - /imbasicdata1
	public ImBasicData1 createImBasicData1(ImBasicData1 newImBasicData1, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(newImBasicData1, headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "imbasicdata1")
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ImBasicData1> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
					ImBasicData1.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
	
	// GET BomLine - /{bomNumber}/bomline
	public BomLine[] getBomLine (Long bomLineNo, String warehouseId, String authToken) {
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
	
	//------------------------------StorageBin--------------------------------------------------------------
	// /{storageBin}
	// GET StorageBin - /{warehouseId}/bins/{binClassId}
	public StorageBin getStorageBin (String storageBin, String authToken) {
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
	public StorageBin getStorageBin (String storageBin, String warehouseId, String authToken) {
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
	public StorageBin getStorageBin (String warehouseId, Long binClassId, String authToken) {
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
	
	// GET StorageBin - /{storageBin}/storageSections/{storageSectionId}
	public StorageBin[] getStorageBin ( StorageBinPutAway storageBinPutAway, String authToken) {
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
	
	// GET StorageBin - /{warehouseId}/status
	public StorageBin[] getStorageBinByStatus ( String warehouseId, Long statusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/status")
						.queryParam("statusId", statusId);
			ResponseEntity<StorageBin[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET StorageBin - /{warehouseId}/status
	public StorageBin[] getStorageBinByStatusNotEqual ( String warehouseId, Long statusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "storagebin/" + warehouseId + "/status-not-equal")
						.queryParam("statusId", statusId);
			ResponseEntity<StorageBin[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	
	// GET By Storage Section Id - /sectionId
	public StorageBin[] getStorageBinBySectionId ( String warehouseId, List<String> stSectionIds, String authToken) {
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
	public StorageBin updateStorageBin ( String storageBin, StorageBin modifiedStorageBin, String loginUserID, String authToken) {
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
		
	//------------------------------HandlingEquipment--------------------------------------------------------------
	// GET HandlingEquipment - /{heBarcode}/barCode
	public HandlingEquipment getHandlingEquipment (String warehouseId, String heBarcode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "handlingequipment/" + heBarcode + "/barCode")
						.queryParam("warehouseId", warehouseId);
			ResponseEntity<HandlingEquipment> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//------------------------------Business Partner----------------------------------------------------------------
	public BusinessPartner getBusinessPartner (String partnerCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceApiUrl() + "businesspartner/" + partnerCode );
			ResponseEntity<BusinessPartner> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner.class);
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
}