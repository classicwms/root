package com.tekclover.wms.core.service;

import java.util.Collections;

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
import com.tekclover.wms.core.model.masters.AuditLog;
import com.tekclover.wms.core.model.masters.BOM;
import com.tekclover.wms.core.model.masters.BomHeader;
import com.tekclover.wms.core.model.masters.BomLine;
import com.tekclover.wms.core.model.masters.BusinessPartner;
import com.tekclover.wms.core.model.masters.HandlingEquipment;
import com.tekclover.wms.core.model.masters.HandlingUnit;
import com.tekclover.wms.core.model.masters.ImAlternateUom;
import com.tekclover.wms.core.model.masters.ImBasicData1;
import com.tekclover.wms.core.model.masters.ImBasicData2;
import com.tekclover.wms.core.model.masters.ImPacking;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.masters.ImStrategies;
import com.tekclover.wms.core.model.masters.PackingMaterial;
import com.tekclover.wms.core.model.masters.SearchBomHeader;
import com.tekclover.wms.core.model.masters.SearchBusinessPartner;
import com.tekclover.wms.core.model.masters.SearchHandlingEquipment;
import com.tekclover.wms.core.model.masters.SearchHandlingUnit;
import com.tekclover.wms.core.model.masters.SearchImBasicData1;
import com.tekclover.wms.core.model.masters.SearchPackingMaterial;
import com.tekclover.wms.core.model.masters.SearchStorageBin;
import com.tekclover.wms.core.model.masters.StorageBin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MastersService {
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	private String getMastersServiceUrl () {
		return propertiesConfig.getMastersServiceUrl();
	}
	
	public boolean validateUser (String name, String password, String authToken) {
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
	public BomHeader[] getBomHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public BomHeader getBomHeader (String warehouseId, String parentItemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
					.queryParam("warehouseId", warehouseId)
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public BomHeader createBomHeader (BomHeader newBomHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
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
	public BomHeader updateBomHeader (String warehouseId, String parentItemCode, String loginUserID, 
			BomHeader modifiedBomHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedBomHeader, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
					.queryParam("warehouseId", warehouseId)
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
	public boolean deleteBomHeader (String warehouseId, String parentItemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomheader/" + parentItemCode)
					.queryParam("warehouseId", warehouseId)
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
 
//	// GET ALL
//	public BomLine[] getBomLines (String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "MNRClara RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//			
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline");
//			HttpEntity<?> entity = new HttpEntity<>(headers);
//			ResponseEntity<BomLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine[].class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	// GET
//	public BomLine getBomLine ( String warehouseId, Long bomNumber, String childItemCode, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "MNRClara RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//			
//			UriComponentsBuilder builder = 
//					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
//					.queryParam("warehouseId", warehouseId)
//					.queryParam("bomNumber", bomNumber)
//					.queryParam("childItemCode", childItemCode);
//			HttpEntity<?> entity = new HttpEntity<>(headers);	
//			ResponseEntity<BomLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BomLine.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	// POST
//	public BomLine createBomLine (BomLine newBomLine, String loginUserID, String authToken) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("User-Agent", "MNRClara RestTemplate");
//		headers.add("Authorization", "Bearer " + authToken);
//		
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline")
//				.queryParam("loginUserID", loginUserID);
//		HttpEntity<?> entity = new HttpEntity<>(newBomLine, headers);
//		ResponseEntity<BomLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BomLine.class);
//		log.info("result : " + result.getStatusCode());
//		return result.getBody();
//	}
//	
//	// PATCH
//	public BomLine updateBomLine (String warehouseId, Long bomNumber, String childItemCode, 
//			String loginUserID, BomLine modifiedBomLine, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "MNRClara's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//			
//			HttpEntity<?> entity = new HttpEntity<>(modifiedBomLine, headers);
//			
//			HttpClient client = HttpClients.createDefault();
//			RestTemplate restTemplate = getRestTemplate();
//			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
//			
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
//					.queryParam("warehouseId", warehouseId)
//					.queryParam("bomNumber", bomNumber)
//					.queryParam("childItemCode", childItemCode)
//					.queryParam("loginUserID", loginUserID);
//			
//			ResponseEntity<BomLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BomLine.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	// DELETE
//	public boolean deleteBomLine (String warehouseId, Long bomNumber, String childItemCode, String loginUserID, String authToken) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "MNRClara's RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//			
//			HttpEntity<?> entity = new HttpEntity<>(headers);
//			UriComponentsBuilder builder = 
//					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
//					.queryParam("warehouseId", warehouseId)
//					.queryParam("bomNumber", bomNumber)
//					.queryParam("childItemCode", childItemCode)
//					.queryParam("loginUserID", loginUserID);
//			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
		
		
/* -----------------------------MASTERS---BUSINESSPARTNER---------------------------------------------------------------*/
 // Get ALL
	public BusinessPartner[] getBusinessPartners (String authToken) {
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
	public BusinessPartner getBusinessPartner(String partnerCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/" + partnerCode);
			ResponseEntity<BusinessPartner> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner.class);
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public BusinessPartner addBusinessPartner (BusinessPartner businesspartner, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(businesspartner, headers);
			ResponseEntity<BusinessPartner> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BusinessPartner.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch BusinessPartner
	public BusinessPartner updateBusinessPartner (String partnerCode, BusinessPartner modifiedBusinessPartner, 
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<BusinessPartner> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BusinessPartner.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete BusinessPartner
	public boolean deleteBusinessPartner (String partnerCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/" + partnerCode)
					.queryParam("loginUserID", loginUserID);
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
	public HandlingEquipment[] getHandlingEquipments (String authToken) {
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
	public HandlingEquipment getHandlingEquipment(String handlingEquipmentId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId);
			ResponseEntity<HandlingEquipment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
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
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + heBarcode)
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public HandlingEquipment addHandlingEquipment (HandlingEquipment handlingequipment, String loginUserID, 
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
	public HandlingEquipment updateHandlingEquipment (String handlingEquipmentId, 
			HandlingEquipment modifiedHandlingEquipment, String loginUserID, String authToken) {
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
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<HandlingEquipment> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingEquipment.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete HandlingEquipment
	public boolean deleteHandlingEquipment (String handlingEquipmentId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId)
					.queryParam("loginUserID", loginUserID);
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
	public HandlingUnit[] getHandlingUnits (String authToken) {
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
	public HandlingUnit getHandlingUnit(String handlingUnit, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit);
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	
	// POST HandlingUnit
	public HandlingUnit addHandlingUnit (HandlingUnit handlingunit, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(handlingunit, headers);
			ResponseEntity<HandlingUnit> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingUnit.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch HandlingUnit
	public HandlingUnit updateHandlingUnit (String handlingUnit, HandlingUnit modifiedHandlingUnit, 
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<HandlingUnit> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingUnit.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete HandlingUnit
	public boolean deleteHandlingUnit (String handlingUnit, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit)
					.queryParam("loginUserID", loginUserID);
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
	public ImAlternateUom[] getImAlternateUoms (String authToken) {
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
	public ImAlternateUom getImAlternateUom(String alternateUom, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + alternateUom);
			ResponseEntity<ImAlternateUom> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImAlternateUom.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ImAlternateUom
	public ImAlternateUom addImAlternateUom (ImAlternateUom imalternateuom, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(imalternateuom, headers);
			ResponseEntity<ImAlternateUom> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImAlternateUom.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch ImAlternateUom
	public ImAlternateUom updateImAlternateUom (String alternateUom, ImAlternateUom modifiedImAlternateUom, 
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
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + alternateUom)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ImAlternateUom> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImAlternateUom.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete ImAlternateUom
	public boolean deleteImAlternateUom (String alternateUom, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imalternateuom/" + alternateUom)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/* -----------------------------MASTERS---IMBASICDATA1---------------------------------------------------------------*/
	// Get ALL
	public ImBasicData1[] getImBasicData1s (String authToken) {
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
	public ImBasicData1 getImBasicData1(String itemCode, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/" + itemCode)
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<ImBasicData1> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1.class);
			log.info("result : " + result.getStatusCode());
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	
	// POST ImBasicData1
	public ImBasicData1 addImBasicData1 (ImBasicData1 imbasicdata1, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(imbasicdata1, headers);
			ResponseEntity<ImBasicData1> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch ImBasicData1
	public ImBasicData1 updateImBasicData1 (String itemCode, String warehouseId, ImBasicData1 modifiedImBasicData1, 
			String loginUserID, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/" + itemCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ImBasicData1> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImBasicData1.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete ImBasicData1
	public boolean deleteImBasicData1 (String uomId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/" + uomId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
		
/* -----------------------------MASTERS---IMBASICDATA2---------------------------------------------------------------*/
 // Get ALL
	public ImBasicData2[] getImBasicData2s (String authToken) {
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
	public ImBasicData2 getImBasicData2(String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/" + itemCode);
			ResponseEntity<ImBasicData2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData2.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ImBasicData2
	public ImBasicData2 addImBasicData2 (ImBasicData2 imbasicdata2, String loginUserID, String authToken) {
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
	public ImBasicData2 updateImBasicData2 (String itemCode, ImBasicData2 modifiedImBasicData2, 
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ImBasicData2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImBasicData2.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete ImBasicData2
	public boolean deleteImBasicData2 (String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata2/" + itemCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	/* -----------------------------MASTERS---IMPACKING---------------------------------------------------------------*/
	// Get ALL
	public ImPacking[] getImPackings (String authToken) {
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
	public ImPacking getImPacking(String packingMaterialNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo);
			ResponseEntity<ImPacking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPacking.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ImPacking
	public ImPacking addImPacking (ImPacking impacking, String loginUserID, String authToken) {
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
	public ImPacking updateImPacking (String packingMaterialNo, ImPacking modifiedImPacking, 
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
	public boolean deleteImPacking (String packingMaterialNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	/* -----------------------------MASTERS---IMPARTNER---------------------------------------------------------------*/
	// Get ALL
	public ImPartner[] getImPartners (String authToken) {
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
	public ImPartner getImPartner(String businessPartnerCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + businessPartnerCode);
			ResponseEntity<ImPartner> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPartner.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ImPartner
	public ImPartner addImPartner (ImPartner impartner, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner")
					.queryParam("loginUserID", loginUserID);

			HttpEntity<?> entity = new HttpEntity<>(impartner, headers);
			ResponseEntity<ImPartner> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImPartner.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch ImPartner
	public ImPartner updateImPartner (String businessPartnerCode, ImPartner modifiedImPartner, String loginUserID, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + businessPartnerCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ImPartner> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPartner.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete ImPartner
	public boolean deleteImPartner (String businessPartnerCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impartner/" + businessPartnerCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	/* -----------------------------MASTERS---IMSTRATEGIES---------------------------------------------------------------*/
	// Get ALL
	public ImStrategies[] getImStrategiess (String authToken) {
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
	public ImStrategies getImStrategies(String strategeyTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategeyTypeId);
			ResponseEntity<ImStrategies> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImStrategies.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST ImStrategies
	public ImStrategies addImStrategies (ImStrategies imstrategies, String loginUserID, String authToken) {
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
	public ImStrategies updateImStrategies (String strategeyTypeId, ImStrategies modifiedImStrategies, 
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
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategeyTypeId)
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
	public boolean deleteImStrategies (String strategeyTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imstrategies/" + strategeyTypeId)
					.queryParam("loginUserID", loginUserID);
					
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/* -----------------------------MASTERS---PACKINGMATERIAL---------------------------------------------------------------*/
	// Get ALL
	public PackingMaterial[] getPackingMaterials (String authToken) {
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
	public PackingMaterial getPackingMaterial(String packingMaterialNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo);
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public PackingMaterial addPackingMaterial (PackingMaterial packingmaterial, String loginUserID, String authToken) {
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
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Patch PackingMaterial
	public PackingMaterial updatePackingMaterial (String packingMaterialNo, PackingMaterial modifiedPackingMaterial, 
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PackingMaterial> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PackingMaterial.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete PackingMaterial
	public boolean deletePackingMaterial (String packingMaterialNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	/* -----------------------------MASTERS---STORAGEBIN---------------------------------------------------------------*/
	// Get ALL
	public StorageBin[] getStorageBins (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin");
			ResponseEntity<StorageBin[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET StorageBin
	public StorageBin getStorageBin(String storageBin, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin);
			ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
			log.info("result : " + result.getStatusCode());
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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	
	// POST StorageBin
	public StorageBin addStorageBin (StorageBin storagebin, String loginUserID, String authToken) {
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
	
	// Patch StorageBin
	public StorageBin updateStorageBin (String storageBin, StorageBin modifiedStorageBin, String loginUserID, String authToken) {
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
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StorageBin> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBin.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// Delete StorageBin
	public boolean deleteStorageBin (String storageBin, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin)
					.queryParam("loginUserID", loginUserID);
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
	public AuditLog[] getAuditLogs (String authToken) {
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
	public AuditLog addAuditLog (AuditLog auditlog, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "auditlog");

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
	public AuditLog updateAuditLog (String auditFileNumber, AuditLog modifiedAuditLog, String authToken) {
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
	public boolean deleteAuditLog (String auditFileNumber, String authToken) {
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
}
		