package com.tekclover.wms.core.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tekclover.wms.core.model.masters.*;
import com.tekclover.wms.core.util.DateUtils;
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
	public BomHeader[] getBomHeaders (String authToken) throws ParseException {
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
//			return result.getBody();

			List<BomHeader> obList = new ArrayList<>();
			for (BomHeader bomHeader : result.getBody()) {

				obList.add(addingTimeWithDateBomHeader(bomHeader));

			}
			return obList.toArray(new BomHeader[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//Add Time to Date plus 3 Hours
	public BomHeader addingTimeWithDateBomHeader(BomHeader bomHeader) throws ParseException {

		if (bomHeader.getCreatedOn() != null) {
			bomHeader.setCreatedOn(DateUtils.addTimeToDate(bomHeader.getCreatedOn(), 3));
		}

		if (bomHeader.getUpdatedOn() != null) {
			bomHeader.setUpdatedOn(DateUtils.addTimeToDate(bomHeader.getUpdatedOn(), 3));
		}

		return bomHeader;
	}
	// GET
	public BomHeader getBomHeader (String warehouseId, String parentItemCode, String authToken) throws ParseException {
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
//			return result.getBody();

			return addingTimeWithDateBomHeader(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findBomHeader
	public BomHeader[] findBomHeader(SearchBomHeader searchBomHeader, String authToken) throws ParseException {
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
//			return result.getBody();

			List<BomHeader> obList = new ArrayList<>();
			for (BomHeader bomHeader : result.getBody()) {

				obList.add(addingTimeWithDateBomHeader(bomHeader));

			}
			return obList.toArray(new BomHeader[obList.size()]);

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

	// GET ALL
	public BomLine[] getBomLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public BomLine getBomLine ( Long bomNumber, String warehouseId, String childItemCode, String languageId,
								String companycode, String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "bomline/" + bomNumber)
							.queryParam("warehouseId", warehouseId)
							.queryParam("languageId", languageId)
							.queryParam("companycode", companycode)
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
	// DELETE
	public boolean deleteBomLine (Long bomNumber, String warehouseId, String languageId, String companyCode,
								  String plantId, String childItemCode,   String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
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
		
		
/* -----------------------------MASTERS---BUSINESSPARTNER---------------------------------------------------------------*/
 // Get ALL
	public BusinessPartner[] getBusinessPartners (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner");
			ResponseEntity<BusinessPartner[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<BusinessPartner> obList = new ArrayList<>();
			for (BusinessPartner businessPartner : result.getBody()) {

				obList.add(addingTimeWithDateBusinessPartner(businessPartner));

			}
			return obList.toArray(new BusinessPartner[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public BusinessPartner addingTimeWithDateBusinessPartner(BusinessPartner businessPartner) throws ParseException {

		if (businessPartner.getCreatedon() != null) {
			businessPartner.setCreatedon(DateUtils.addTimeToDate(businessPartner.getCreatedon(), 3));
		}

		if (businessPartner.getUpdatedon() != null) {
			businessPartner.setUpdatedon(DateUtils.addTimeToDate(businessPartner.getUpdatedon(), 3));
		}

		return businessPartner;
	}

	// GET BusinessPartner
	public BusinessPartner getBusinessPartner(String partnerCode, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "businesspartner/" + partnerCode);
			ResponseEntity<BusinessPartner> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartner.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDateBusinessPartner(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findBusinessPartner
	public BusinessPartner[] findBusinessPartner(SearchBusinessPartner searchBusinessPartner, String authToken) throws ParseException {
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
//			return result.getBody();

			List<BusinessPartner> obList = new ArrayList<>();
			for (BusinessPartner businessPartner : result.getBody()) {

				obList.add(addingTimeWithDateBusinessPartner(businessPartner));

			}
			return obList.toArray(new BusinessPartner[obList.size()]);

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
	public HandlingEquipment[] getHandlingEquipments (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment");
			ResponseEntity<HandlingEquipment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<HandlingEquipment> obList = new ArrayList<>();
			for (HandlingEquipment handlingEquipment : result.getBody()) {

				obList.add(addingTimeWithDateHandlingEquipment(handlingEquipment));

			}
			return obList.toArray(new HandlingEquipment[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public HandlingEquipment addingTimeWithDateHandlingEquipment(HandlingEquipment handlingEquipment) throws ParseException {

		if (handlingEquipment.getAcquistionDate() != null) {
			handlingEquipment.setAcquistionDate(DateUtils.addTimeToDate(handlingEquipment.getAcquistionDate(), 3));
		}

		if (handlingEquipment.getCreatedon() != null) {
			handlingEquipment.setCreatedon(DateUtils.addTimeToDate(handlingEquipment.getCreatedon(), 3));
		}

		if (handlingEquipment.getUpdatedon() != null) {
			handlingEquipment.setUpdatedon(DateUtils.addTimeToDate(handlingEquipment.getUpdatedon(), 3));
		}

		return handlingEquipment;
	}

	// GET HandlingEquipment
	public HandlingEquipment getHandlingEquipment(String handlingEquipmentId, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + handlingEquipmentId);
			ResponseEntity<HandlingEquipment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDateHandlingEquipment(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public HandlingEquipment getHandlingEquipment(String warehouseId, String heId, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingequipment/" + heId)
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<HandlingEquipment> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipment.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDateHandlingEquipment(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findHandlingEquipment
	public HandlingEquipment[] findHandlingEquipment(SearchHandlingEquipment searchHandlingEquipment, String authToken) throws ParseException {
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
//			return result.getBody();

			List<HandlingEquipment> obList = new ArrayList<>();
			for (HandlingEquipment handlingEquipment : result.getBody()) {

				obList.add(addingTimeWithDateHandlingEquipment(handlingEquipment));

			}
			return obList.toArray(new HandlingEquipment[obList.size()]);

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
	public HandlingUnit[] getHandlingUnits (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit");
			ResponseEntity<HandlingUnit[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnit[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<HandlingUnit> obList = new ArrayList<>();
			for (HandlingUnit handlingUnit : result.getBody()) {

				obList.add(addingTimeWithDateHandlingUnit(handlingUnit));

			}
			return obList.toArray(new HandlingUnit[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public HandlingUnit addingTimeWithDateHandlingUnit(HandlingUnit handlingUnit) throws ParseException {

		if (handlingUnit.getCreatedon() != null) {
			handlingUnit.setCreatedon(DateUtils.addTimeToDate(handlingUnit.getCreatedon(), 3));
		}

		if (handlingUnit.getUpdatedon() != null) {
			handlingUnit.setUpdatedon(DateUtils.addTimeToDate(handlingUnit.getUpdatedon(), 3));
		}

		return handlingUnit;
	}
	
	// GET HandlingUnit
	public HandlingUnit getHandlingUnit(String handlingUnit, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "handlingunit/" + handlingUnit);
			ResponseEntity<HandlingUnit> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnit.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDateHandlingUnit(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findHandlingUnit
	public HandlingUnit[] findHandlingUnit(SearchHandlingUnit searchHandlingUnit, String authToken) throws ParseException {
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
//			return result.getBody();

			List<HandlingUnit> obList = new ArrayList<>();
			for (HandlingUnit handlingUnit : result.getBody()) {

				obList.add(addingTimeWithDateHandlingUnit(handlingUnit));

			}
			return obList.toArray(new HandlingUnit[obList.size()]);

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
	public ImBasicData1[] getImBasicData1s (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1");
			ResponseEntity<ImBasicData1[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImBasicData1[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<ImBasicData1> obList = new ArrayList<>();
			for (ImBasicData1 imBasicData1 : result.getBody()) {

				obList.add(addingTimeWithDateImBasicData1(imBasicData1));

			}
			return obList.toArray(new ImBasicData1[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public ImBasicData1 addingTimeWithDateImBasicData1(ImBasicData1 imBasicData1) throws ParseException {

		if (imBasicData1.getCreatedOn() != null) {
			imBasicData1.setCreatedOn(DateUtils.addTimeToDate(imBasicData1.getCreatedOn(), 3));
		}

		if (imBasicData1.getUpdatedOn() != null) {
			imBasicData1.setUpdatedOn(DateUtils.addTimeToDate(imBasicData1.getUpdatedOn(), 3));
		}

		return imBasicData1;
	}

	// GET ImBasicData1
	public ImBasicData1 getImBasicData1(String itemCode, String warehouseId, String authToken) throws ParseException {
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
//			return result.getBody();

			return addingTimeWithDateImBasicData1(result.getBody());

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
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findImBasicData1/pagination")
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy);
			HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);	
			
			ParameterizedTypeReference<PaginatedResponse<ImBasicData1>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<ImBasicData1>>() {};
			ResponseEntity<PaginatedResponse<ImBasicData1>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
			
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findImBasicData1
	public ImBasicData1[] findImBasicData1(SearchImBasicData1 searchImBasicData1, String authToken) throws ParseException {
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
//			return result.getBody();

			List<ImBasicData1> obList = new ArrayList<>();
			for (ImBasicData1 imBasicData1 : result.getBody()) {

				obList.add(addingTimeWithDateImBasicData1(imBasicData1));

			}
			return obList.toArray(new ImBasicData1[obList.size()]);

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
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "imbasicdata1/findImBasicData1Stream");
			HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
			ResponseEntity<ImBasicData1[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<ImBasicData1> obList = new ArrayList<>();
			for (ImBasicData1 imBasicData1 : result.getBody()) {

				obList.add(addingTimeWithDateImBasicData1(imBasicData1));

			}
			return obList.toArray(new ImBasicData1[obList.size()]);

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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
	public ImPacking[] getImPackings (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking");
			ResponseEntity<ImPacking[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPacking[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<ImPacking> obList = new ArrayList<>();
			for (ImPacking imPacking : result.getBody()) {

				obList.add(addingTimeWithDateImPacking(imPacking));

			}
			return obList.toArray(new ImPacking[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public ImPacking addingTimeWithDateImPacking(ImPacking imPacking) throws ParseException {

		if (imPacking.getCreatedon() != null) {
			imPacking.setCreatedon(DateUtils.addTimeToDate(imPacking.getCreatedon(), 3));
		}

		if (imPacking.getUpdatedon() != null) {
			imPacking.setUpdatedon(DateUtils.addTimeToDate(imPacking.getUpdatedon(), 3));
		}

		return imPacking;
	}

	// GET ImPacking
	public ImPacking getImPacking(String packingMaterialNo, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "impacking/" + packingMaterialNo);
			ResponseEntity<ImPacking> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ImPacking.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDateImPacking(result.getBody());

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
	public PackingMaterial[] getPackingMaterials (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial");
			ResponseEntity<PackingMaterial[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackingMaterial[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<PackingMaterial> obList = new ArrayList<>();
			for (PackingMaterial packingMaterial : result.getBody()) {

				obList.add(addingTimeWithDatePackingMaterial(packingMaterial));

			}
			return obList.toArray(new PackingMaterial[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public PackingMaterial addingTimeWithDatePackingMaterial(PackingMaterial packingMaterial) throws ParseException {

		if (packingMaterial.getCreatedOn() != null) {
			packingMaterial.setCreatedOn(DateUtils.addTimeToDate(packingMaterial.getCreatedOn(), 3));
		}

		if (packingMaterial.getUpdatedOn() != null) {
			packingMaterial.setUpdatedOn(DateUtils.addTimeToDate(packingMaterial.getUpdatedOn(), 3));
		}

		return packingMaterial;
	}

	// GET PackingMaterial
	public PackingMaterial getPackingMaterial(String packingMaterialNo, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "packingmaterial/" + packingMaterialNo);
			ResponseEntity<PackingMaterial> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PackingMaterial.class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			return addingTimeWithDatePackingMaterial(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findPackingMaterial
	public PackingMaterial[] findPackingMaterial(SearchPackingMaterial searchPackingMaterial, String authToken) throws ParseException {
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
//			return result.getBody();

			List<PackingMaterial> obList = new ArrayList<>();
			for (PackingMaterial packingMaterial : result.getBody()) {

				obList.add(addingTimeWithDatePackingMaterial(packingMaterial));

			}
			return obList.toArray(new PackingMaterial[obList.size()]);

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
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	/* -----------------------------MASTERS---STORAGEBIN---------------------------------------------------------------*/
	// Get ALL
	public StorageBin[] getStorageBins (String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin");
			ResponseEntity<StorageBin[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin[].class);
//			return result.getBody();

			List<StorageBin> obList = new ArrayList<>();
			for (StorageBin storageBin : result.getBody()) {

				obList.add(addingTimeWithDateStorageBin(storageBin));

			}
			return obList.toArray(new StorageBin[obList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Add Time to Date plus 3 Hours
	public StorageBin addingTimeWithDateStorageBin(StorageBin storageBin) throws ParseException {

		if (storageBin.getCreatedOn() != null) {
			storageBin.setCreatedOn(DateUtils.addTimeToDate(storageBin.getCreatedOn(), 3));
		}

		if (storageBin.getUpdatedOn() != null) {
			storageBin.setUpdatedOn(DateUtils.addTimeToDate(storageBin.getUpdatedOn(), 3));
		}

		return storageBin;
	}
	// GET StorageBin
	public StorageBin getStorageBin(String storageBin, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin);
			ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
//			return result.getBody();

			return addingTimeWithDateStorageBin(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET StorageBin
	public StorageBin getStorageBin(String warehouseId, String storageBin, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/" + storageBin + "/warehouseId")
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<StorageBin> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBin.class);
//			return result.getBody();

			return addingTimeWithDateStorageBin(result.getBody());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findStorageBin
	public StorageBin[] findStorageBin(SearchStorageBin searchStorageBin, String authToken) throws ParseException {
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
//			return result.getBody();

			List<StorageBin> obList = new ArrayList<>();
			for (StorageBin storageBin : result.getBody()) {

				obList.add(addingTimeWithDateStorageBin(storageBin));

			}
			return obList.toArray(new StorageBin[obList.size()]);

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
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getMastersServiceUrl() + "storagebin/findStorageBinStream");
			HttpEntity<?> entity = new HttpEntity<>(searchStorageBin, headers);
			ResponseEntity<StorageBin[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin[].class);
			log.info("result : " + result.getStatusCode());
//			return result.getBody();

			List<StorageBin> obList = new ArrayList<>();
			for (StorageBin storageBin : result.getBody()) {

				obList.add(addingTimeWithDateStorageBin(storageBin));

			}
			return obList.toArray(new StorageBin[obList.size()]);

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
			headers.add("User-Agent", "MNRClara RestTemplate");
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
		