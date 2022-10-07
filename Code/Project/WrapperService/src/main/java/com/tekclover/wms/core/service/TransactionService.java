package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.repository.MongoTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TransactionService {
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	MongoTransactionRepository mongoTransactionRepository;
	
	/**
	 * 
	 * @return
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getTransactionServiceApiUrl () {
		return propertiesConfig.getTransactionServiceUrl();
	} 
	
	/*------------------------------ProcessInboundReceived-----------------------------------------------------------------*/
	// POST
	public PreInboundHeader processInboundReceived (String authToken) {
		InboundIntegrationHeader createdInboundIntegrationHeader = mongoTransactionRepository.findTopByOrderByOrderReceivedOnDesc();
		log.info("Latest InboundIntegrationHeader : " + createdInboundIntegrationHeader);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() 
				+ "preinboundheader/" + createdInboundIntegrationHeader.getRefDocumentNo() + "/processInboundReceived");
		HttpEntity<?> entity = new HttpEntity<>(createdInboundIntegrationHeader, headers);
		ResponseEntity<PreInboundHeader> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeader.class);
		return result.getBody();
	}

	//--------------------------------------------PreInboundHeader------------------------------------------------------------------------
	// GET ALL
	public PreInboundHeader[] getPreInboundHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundHeader[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PreInboundHeader getPreInboundHeader ( String preInboundNo, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PreInboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundHeader.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StagingHeader processASN (List<PreInboundLine> newPreInboundLine, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/processASN")
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(newPreInboundLine, headers);	
			ResponseEntity<StagingHeader> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeader.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PreInboundHeader[] getPreInboundHeaderWithStatusId(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/inboundconfirm")
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PreInboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundHeader[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public PreInboundHeader createPreInboundHeader (PreInboundHeader newPreInboundHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPreInboundHeader, headers);
		ResponseEntity<PreInboundHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeader.class);
//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST - findContainerReceipt
	public PreInboundHeader[] findPreInboundHeader (SearchPreInboundHeader searchPreInboundHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/findPreInboundHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchPreInboundHeader, headers);	
			ResponseEntity<PreInboundHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeader[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	// PATCH
	public PreInboundHeader updatePreInboundHeader ( String preInboundNo, String warehouseId, 
			String loginUserID, PreInboundHeader modifiedPreInboundHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundHeader, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PreInboundHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PreInboundHeader.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePreInboundHeader (String preInboundNo, String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundheader/" + preInboundNo)
					.queryParam("warehouseId", warehouseId)
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
	
	//--------------------------------------------PreInboundLine------------------------------------------------------------------------
	// GET ALL
	public PreInboundLine[] getPreInboundLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PreInboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundLine[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PreInboundLine getPreInboundLine (String preInboundNo, String warehouseId, 
			String refDocNumber, Long lineNo, String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("lineNo", lineNo)
					.queryParam("itemCode", itemCode);
					
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PreInboundLine> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundLine.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PreInboundLine[] getPreInboundLine (String preInboundNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline/" + preInboundNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PreInboundLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PreInboundLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public PreInboundLine createPreInboundLine (PreInboundLine newPreInboundLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPreInboundLine, headers);
		ResponseEntity<PreInboundLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundLine.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// CREATE - BOM
	public PreInboundLine[] createPreInboundLineBOM(String preInboundNo, String warehouseId, String refDocNumber,
			String itemCode, Long lineNo, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline/bom")
				.queryParam("preInboundNo", preInboundNo)
				.queryParam("warehouseId", warehouseId)
				.queryParam("refDocNumber", refDocNumber)
				.queryParam("itemCode", itemCode)
				.queryParam("lineNo", lineNo)
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<PreInboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, 
				PreInboundLine[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public PreInboundLine updatePreInboundLine (String preInboundNo, String warehouseId, 
			String refDocNumber, Long lineNo, String itemCode, String loginUserID, PreInboundLine modifiedPreInboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedPreInboundLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("lineNo", lineNo)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PreInboundLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PreInboundLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePreInboundLine (String preInboundNo, String warehouseId, 
			String refDocNumber, Long lineNo, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preinboundline/" + preInboundNo)
					.queryParam("warehouseId", warehouseId)
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
	
	//--------------------------------------------ContainerReceipt------------------------------------------------------------------------
	// GET ALL
	public ContainerReceipt[] getContainerReceipts (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ContainerReceipt[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ContainerReceipt[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public ContainerReceipt getContainerReceipt (String preInboundNo, String refDocNumber, String containerReceiptNo, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ContainerReceipt.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public ContainerReceipt getContainerReceipt (String containerReceiptNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ContainerReceipt.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST - findContainerReceipt
	public ContainerReceipt[] findContainerReceipt(SearchContainerReceipt searchContainerReceipt, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt/findContainerReceipt");
			HttpEntity<?> entity = new HttpEntity<>(searchContainerReceipt, headers);	
			ResponseEntity<ContainerReceipt[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContainerReceipt[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public ContainerReceipt createContainerReceipt (ContainerReceipt newContainerReceipt, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newContainerReceipt, headers);
		ResponseEntity<ContainerReceipt> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContainerReceipt.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ContainerReceipt updateContainerReceipt (String containerReceiptNo, 
			String loginUserID, ContainerReceipt modifiedContainerReceipt, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedContainerReceipt, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ContainerReceipt> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ContainerReceipt.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteContainerReceipt (String preInboundNo, String refDocNumber, String containerReceiptNo, String warehouseId, 
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "containerreceipt/" + containerReceiptNo)
					.queryParam("warehouseId", warehouseId)					
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
	
	//--------------------------------------------InboundHeader------------------------------------------------------------------------
	// GET ALL
	public InboundHeader[] getInboundHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public InboundHeader getInboundHeader (String warehouseId, String refDocNumber, String preInboundNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<InboundHeader> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET - Finder
	public InboundHeader[] findInboundHeader(SearchInboundHeader searchInboundHeader, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/findInboundHeader");
		HttpEntity<?> entity = new HttpEntity<>(searchInboundHeader, headers);
		ResponseEntity<InboundHeader[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeader[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// GET
	public InboundHeaderEntity[] getInboundHeaderWithStatusId(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/inboundconfirm")
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundHeaderEntity[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundHeaderEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST
	public InboundHeader createInboundHeader (InboundHeader newInboundHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInboundHeader, headers);
		ResponseEntity<InboundHeader> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST - replaceASN
	public Boolean replaceASN(String refDocNumber, String preInboundNo, String asnNumber, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/replaceASN")
				.queryParam("refDocNumber", refDocNumber)
				.queryParam("preInboundNo", preInboundNo)
				.queryParam("asnNumber", asnNumber);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<Boolean> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Boolean.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
		
	}
	
	// PATCH
	public InboundHeader updateInboundHeader (String warehouseId, String refDocNumber, String preInboundNo, 
			String loginUserID, InboundHeader modifiedInboundHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(modifiedInboundHeader, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<InboundHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundHeader.class);
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
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/confirmIndividual")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<AXApiResponse> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, AXApiResponse.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteInboundHeader (String warehouseId, String refDocNumber, String preInboundNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundheader/" + refDocNumber)
					.queryParam("warehouseId", warehouseId)
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
	
	//--------------------------------------------InboundLine------------------------------------------------------------------------
	// GET ALL
	public InboundLine[] getInboundLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public InboundLine getInboundLine (String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, 
			String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("lineNo", lineNo)
					.queryParam("itemCode", itemCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<InboundLine> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public InboundLine createInboundLine (InboundLine newInboundLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInboundLine, headers);
		ResponseEntity<InboundLine> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundLine.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public InboundLine updateInboundLine (String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, String itemCode, 
			String loginUserID, InboundLine modifiedInboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedInboundLine, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("lineNo", lineNo)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<InboundLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteInboundLine (String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, 
			String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inboundline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
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

	//--------------------------------------------StagingHeader------------------------------------------------------------------------
	// GET ALL
	public StagingHeader[] getStagingHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StagingHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StagingHeader getStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("stagingNo", stagingNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StagingHeader> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StagingHeader[] findStagingHeader (SearchStagingHeader searchStagingHeader, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader/findStagingHeader");
		HttpEntity<?> entity = new HttpEntity<>(searchStagingHeader, headers);
		ResponseEntity<StagingHeader[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeader[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST
	public StagingHeader createStagingHeader (StagingHeader newStagingHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStagingHeader, headers);
		ResponseEntity<StagingHeader> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StagingHeader updateStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String loginUserID, StagingHeader modifiedStagingHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStagingHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("stagingNo", stagingNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<StagingHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader/" + stagingNo)
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
	
	// GET - /{numberOfCases}/barcode
	public String[] generateNumberRanges(Long numberOfCases, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingheader/" + numberOfCases + "/barcode")
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<String[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	//--------------------------------------------StagingLine------------------------------------------------------------------------
	// GET ALL
	public StagingLineEntity[] getStagingLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StagingLineEntity[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingLineEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StagingLineEntity getStagingLine (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String palletCode, String caseCode, Long lineNo, String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("stagingNo", stagingNo)					
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("itemCode", itemCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StagingLineEntity> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StagingLineEntity.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findStagingLine
	public StagingLineEntity[] findStagingLine(SearchStagingLine searchStagingLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/findStagingLine");
			HttpEntity<?> entity = new HttpEntity<>(searchStagingLine, headers);	
			ResponseEntity<StagingLineEntity[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingLineEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST
	public StagingLineEntity[] createStagingLine (List<StagingLine> newStagingLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStagingLine, headers);
		ResponseEntity<StagingLineEntity[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingLineEntity[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StagingLineEntity updateStagingLine (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String palletCode, 
			String caseCode, Long lineNo, String itemCode, String loginUserID, StagingLineEntity modifiedStagingLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStagingLine, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("stagingNo", stagingNo)					
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StagingLineEntity> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntity.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// PATCH
	public StagingLineEntity[] caseConfirmation (List<CaseConfirmation> caseConfirmations, String caseCode, 
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(caseConfirmations, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/caseConfirmation")
					.queryParam("caseCode", caseCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StagingLineEntity[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStagingLine (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/" + lineNo)
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
	public boolean deleteCases (String preInboundNo, Long lineNo, String itemCode, String caseCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/" + lineNo + "/cases")
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
	public StagingLineEntity[] assignHHTUser(List<AssignHHTUser> assignHHTUsers, String assignedUserId,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(assignHHTUsers, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "stagingline/assignHHTUser")
					.queryParam("assignedUserId", assignedUserId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StagingLineEntity[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StagingLineEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------GrHeader------------------------------------------------------------------------
	// GET ALL
	public GrHeader[] getGrHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<GrHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrHeader[].class);
				log.info("result : " + result.getStatusCode());
				return result.getBody();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}
	
	// GET
	public GrHeader getGrHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String goodsReceiptNo, String palletCode, String caseCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("stagingNo", stagingNo)
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<GrHeader> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - Finder
	public GrHeader[] findGrHeader(SearchGrHeader searchGrHeader, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader/findGrHeader");
		HttpEntity<?> entity = new HttpEntity<>(searchGrHeader, headers);
		ResponseEntity<GrHeader[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrHeader[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST
	public GrHeader createGrHeader (GrHeader newGrHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newGrHeader, headers);
		ResponseEntity<GrHeader> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public GrHeader updateGrHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String goodsReceiptNo, String palletCode, String caseCode, String loginUserID, GrHeader modifiedGrHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedGrHeader, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("stagingNo", stagingNo)
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<GrHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, GrHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	// DELETE
	public boolean deleteGrHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String goodsReceiptNo, String palletCode, String caseCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grheader/" + goodsReceiptNo)
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
		
	//--------------------------------------------GrLine------------------------------------------------------------------------
	// GET ALL
	public GrLine[] getGrLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<GrLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public GrLine getGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, 
			String caseCode, String packBarcodes, Long lineNo, String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("goodsReceiptNo", goodsReceiptNo)					
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<GrLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// GET
	public GrLine[] getGrLine(String preInboundNo, String refDocNumber, String packBarcodes, Long lineNo,
			String itemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/" + lineNo + "/putawayline")
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GrLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - Finder method
	public GrLine[] findGrLine(SearchGrLine searchGrLine, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/findGrLine");
		HttpEntity<?> entity = new HttpEntity<>(searchGrLine, headers);
		ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLine[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST
	public GrLine[] createGrLine (List<AddGrLine> newGrLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newGrLine, headers);
		ResponseEntity<GrLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLine[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public GrLine updateGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, 
			String loginUserID, GrLine modifiedGrLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedGrLine, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/" + lineNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("goodsReceiptNo", goodsReceiptNo)					
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<GrLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, GrLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
	// DELETE
	public boolean deleteGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/" + lineNo)
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
	public PackBarcode[] generatePackBarcode(Long acceptQty, Long damageQty, String warehouseId, 
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "grline/packBarcode")
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
	
	//--------------------------------------------PutAwayHeader------------------------------------------------------------------------
	// GET ALL
	public PutAwayHeader[] getPutAwayHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PutAwayHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PutAwayHeader getPutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, 
			String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
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
			ResponseEntity<PutAwayHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public PutAwayHeader[] findPutAwayHeader(SearchPutAwayHeader searchPutAwayHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/findPutAwayHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchPutAwayHeader, headers);	
			ResponseEntity<PutAwayHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// GET - /{refDocNumber}/inboundreversal/asn
	public PutAwayHeader[] getPutAwayHeader(String refDocNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/" + refDocNumber +
							"/inboundreversal/asn");
					
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PutAwayHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public PutAwayHeader createPutAwayHeader (PutAwayHeader newPutAwayHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPutAwayHeader, headers);
		ResponseEntity<PutAwayHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public PutAwayHeader updatePutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, 
			String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, PutAwayHeader modifiedPutAwayHeader, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayHeader, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
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
			
			ResponseEntity<PutAwayHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH - /{refDocNumber}/reverse
	public PutAwayHeader[] updatePutAwayHeader(String refDocNumber, String packBarcodes, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/" + refDocNumber + "/reverse")
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PutAwayHeader[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, 
			String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayheader/" + putAwayNumber)
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
	
	//--------------------------------------------PutAwayLine------------------------------------------------------------------------
	// GET ALL
	public PutAwayLine[] getPutAwayLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PutAwayLine getPutAwayLine (String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, 
			Long lineNo, String itemCode, String proposedStorageBin, String confirmedStorageBin, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
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
			ResponseEntity<PutAwayLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET - /{refDocNumber}/inboundreversal/palletId
	public PutAwayLine[] getPutAwayLine(String refDocNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/" + refDocNumber +
							"/inboundreversal/palletId");
					
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PutAwayLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PutAwayLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public PutAwayLine[] findPutAwayLine (SearchPutAwayLine searchPutAwayLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/findPutAwayLine");
			HttpEntity<?> entity = new HttpEntity<>(searchPutAwayLine, headers);
			ResponseEntity<PutAwayLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST
	public PutAwayLine[] createPutAwayLine (List<AddPutAwayLine> newPutAwayLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/confirm")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPutAwayLine, headers);
		ResponseEntity<PutAwayLine[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLine[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public PutAwayLine updatePutAwayLine (String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, 
			Long lineNo, String itemCode, String proposedStorageBin, String confirmedStorageBin, PutAwayLine modifiedPutAwayLine, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedPutAwayLine, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preInboundNo", preInboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("goodsReceiptNo", goodsReceiptNo)
					.queryParam("putAwayNumber", putAwayNumber)
					.queryParam("lineNo", lineNo)
					.queryParam("itemCode", itemCode)
					.queryParam("proposedStorageBin", proposedStorageBin)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PutAwayLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PutAwayLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePutAwayLine (String languageId,String companyCodeId, String plantId,String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin, String confirmedStorageBin, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "putawayline/" + confirmedStorageBin)
					.queryParam("languageId", languageId)
					.queryParam("companyCodeId", companyCodeId)
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
	
	//--------------------------------------------InventoryMovement------------------------------------------------------------------------
	// GET ALL
	public InventoryMovement[] getInventoryMovements (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InventoryMovement[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InventoryMovement[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public InventoryMovement getInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, String caseCode, 
			String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, String movementDocumentNo, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement/" + movementType)
					.queryParam("warehouseId", warehouseId)
					.queryParam("movementType", movementType)
					.queryParam("submovementType", submovementType)
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("variantCode", variantCode)
					.queryParam("variantSubCode", variantSubCode)
					.queryParam("batchSerialNumber", batchSerialNumber)
					.queryParam("movementDocumentNo", movementDocumentNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InventoryMovement.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - findInventoryMovement
	public InventoryMovement[] findInventoryMovement(SearchInventoryMovement searchInventoryMovement, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement/findInventoryMovement");
			HttpEntity<?> entity = new HttpEntity<>(searchInventoryMovement, headers);	
			ResponseEntity<InventoryMovement[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryMovement[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public InventoryMovement createInventoryMovement (InventoryMovement newInventoryMovement, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInventoryMovement, headers);
		ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryMovement.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public InventoryMovement updateInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, String caseCode, 
			String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, String movementDocumentNo, 
			InventoryMovement modifiedInventoryMovement, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedInventoryMovement, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement/" + movementType)
					.queryParam("warehouseId", warehouseId)
					.queryParam("movementType", movementType)
					.queryParam("submovementType", submovementType)
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("variantCode", variantCode)
					.queryParam("variantSubCode", variantSubCode)
					.queryParam("batchSerialNumber", batchSerialNumber)
					.queryParam("movementDocumentNo", movementDocumentNo)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<InventoryMovement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InventoryMovement.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, String caseCode, 
			String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, String movementDocumentNo, 
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventorymovement/" + movementType)
					.queryParam("warehouseId", warehouseId)
					.queryParam("movementType", movementType)
					.queryParam("submovementType", submovementType)
					.queryParam("palletCode", palletCode)
					.queryParam("caseCode", caseCode)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("variantCode", variantCode)
					.queryParam("variantSubCode", variantSubCode)
					.queryParam("batchSerialNumber", batchSerialNumber)
					.queryParam("movementDocumentNo", movementDocumentNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------Inventory------------------------------------------------------------------------
	// GET ALL
	public Inventory[] getInventorys (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Inventory[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public Inventory getInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId, Long specialStockIndicatorId, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/" + stockTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("storageBin", storageBin)
					.queryParam("stockTypeId", stockTypeId)
					.queryParam("specialStockIndicatorId", specialStockIndicatorId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Inventory.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/transfer")
					.queryParam("warehouseId", warehouseId)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("storageBin", storageBin);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Inventory.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST - FinderQuery
	public Inventory[] findInventory(SearchInventory searchInventory, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/findInventory");
			HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);	
			ResponseEntity<Inventory[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Inventory[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST - FinderQuery
	public Inventory[] getQuantityValidatedInventory(SearchInventory searchInventory, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/get-all-validated-inventory");
			HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
			ResponseEntity<Inventory[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Inventory[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public PaginatedResponse<Inventory> findInventory(SearchInventory searchInventory, Integer pageNo, Integer pageSize, String sortBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/findInventory/pagination")
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy);
			
			HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);	
			
			ParameterizedTypeReference<PaginatedResponse<Inventory>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<Inventory>>() {};
			ResponseEntity<PaginatedResponse<Inventory>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
			
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public Inventory createInventory (Inventory newInventory, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInventory, headers);
		ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Inventory.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public Inventory updateInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId, 
			Long specialStockIndicatorId, Inventory modifiedInventory, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedInventory, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/" + stockTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("storageBin", storageBin)
					.queryParam("stockTypeId", stockTypeId)
					.queryParam("specialStockIndicatorId", specialStockIndicatorId)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<Inventory> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Inventory.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId, Long specialStockIndicatorId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inventory/" + stockTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("packBarcodes", packBarcodes)
					.queryParam("itemCode", itemCode)
					.queryParam("storageBin", storageBin)
					.queryParam("stockTypeId", stockTypeId)
					.queryParam("specialStockIndicatorId", specialStockIndicatorId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
	// GET ALL
	public InhouseTransferHeader[] getInhouseTransferHeaders (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InhouseTransferHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public InhouseTransferHeader getInhouseTransferHeader (String warehouseId, String transferNumber, Long transferTypeId, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferheader/" + transferNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("transferNumber", transferNumber)
					.queryParam("transferTypeId", transferTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InhouseTransferHeader.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - Find
	public InhouseTransferHeader[] findInHouseTransferHeader(SearchInhouseTransferHeader searchInHouseTransferHeader, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferheader/findInHouseTransferHeader");
		HttpEntity<?> entity = new HttpEntity<>(searchInHouseTransferHeader, headers);
		ResponseEntity<InhouseTransferHeader[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferHeader[].class);
		return result.getBody();
	}
	
	// POST
	public InhouseTransferHeader createInhouseTransferHeader (InhouseTransferHeader newInhouseTransferHeader, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferHeader, headers);
		ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	//--------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
	// GET ALL
	public InhouseTransferLine[] getInhouseTransferLines (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferline");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InhouseTransferLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InhouseTransferLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public InhouseTransferLine getInhouseTransferLine (String warehouseId, String transferNumber, String sourceItemCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferline/" + transferNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("transferNumber", transferNumber)
					.queryParam("sourceItemCode", sourceItemCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<InhouseTransferLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InhouseTransferLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST - Find
	public InhouseTransferLine[] findInhouseTransferLine(SearchInhouseTransferLine searchInhouseTransferLine, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferline/findInhouseTransferLine");
		HttpEntity<?> entity = new HttpEntity<>(searchInhouseTransferLine, headers);
		ResponseEntity<InhouseTransferLine[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferLine[].class);
		return result.getBody();
	}
	
	// POST
	public InhouseTransferLine createInhouseTransferLine (InhouseTransferLine newInhouseTransferLine, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "inhousetransferline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferLine, headers);
		ResponseEntity<InhouseTransferLine> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferLine.class);
		return result.getBody();
	}
	
	/*
	 * -------------PreOutboundHeader----------------------------------------
	 */
	// POST - findPreOutboundHeader
	public PreOutboundHeader[] findPreOutboundHeader(SearchPreOutboundHeader searchPreOutboundHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preoutboundheader/findPreOutboundHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundHeader, headers);	
			ResponseEntity<PreOutboundHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreOutboundHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	//-------------------------PreOutboundLine------------------------------------------------
	public PreOutboundLine[] findPreOutboundLine(SearchPreOutboundLine searchPreOutboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "preoutboundline/findPreOutboundLine");
			HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundLine, headers);	
			ResponseEntity<PreOutboundLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreOutboundLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	//--------------------------OrderManagementLine----------------------------------------------------

	// POST - findOrderManagementLine
	public OrderManagementLine[] findOrderManagementLine(SearchOrderManagementLine searchOrderManagementLine, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/findOrderManagementLine");
			HttpEntity<?> entity = new HttpEntity<>(searchOrderManagementLine, headers);	
			ResponseEntity<OrderManagementLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OrderManagementLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// PATCH 
	public OrderManagementLine doUnAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackBarCode,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/unallocate")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("proposedStorageBin", proposedStorageBin)
					.queryParam("proposedPackBarCode", proposedPackBarCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OrderManagementLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OrderManagementLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// PATCH
	public OrderManagementLine doAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			HttpClient client = HttpClients.createDefault();
			
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/allocate")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OrderManagementLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OrderManagementLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// PATCH
	public OrderManagementLine[] doAssignPicker(List<AssignPicker> assignPicker, String assignedPickerId, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(assignPicker, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/assignPicker")
					.queryParam("assignedPickerId", assignedPickerId)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OrderManagementLine[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OrderManagementLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH
	public OrderManagementLine updateOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
			String loginUserID, @Valid OrderManagementLine updateOrderMangementLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOrderMangementLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/" + refDocNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("proposedStorageBin", proposedStorageBin)
					.queryParam("proposedPackCode", proposedPackCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OrderManagementLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OrderManagementLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "ordermanagementline/" + refDocNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("proposedStorageBin", proposedStorageBin)
					.queryParam("proposedPackCode", proposedPackCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*--------------------------PickupHeader----------------------------------------------------*/
	// POST - Finder
	public PickupHeader[] findPickupHeader(SearchPickupHeader searchPickupHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupheader/findPickupHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchPickupHeader, headers);	
			ResponseEntity<PickupHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickupHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH
	public PickupHeader updatePickupHeader(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID,
			@Valid PickupHeader updatePickupHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePickupHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupheader/" + pickupNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<PickupHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PickupHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH
	public PickupHeader[] patchAssignedPickerIdInPickupHeader(String loginUserID,
															  @Valid List<UpdatePickupHeader> updatePickupHeaderList, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePickupHeaderList, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupheader/update-assigned-picker")
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<PickupHeader[]> result =
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PickupHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePickupHeader(String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String proposedStorageBin, 
			String proposedPackCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupheader/" + pickupNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("proposedStorageBin", proposedStorageBin)
					.queryParam("proposedPackCode", proposedPackCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*--------------------------PickupLine----------------------------------------------------*/
	// GET
	public Inventory[] getAdditionalBins(String warehouseId, String itemCode, Long obOrdertypeId, 
			String proposedPackBarCode, String proposedStorageBin, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupline/additionalBins")
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemCode", itemCode)
					.queryParam("obOrdertypeId", obOrdertypeId)
					.queryParam("proposedPackBarCode", proposedPackBarCode)
					.queryParam("proposedStorageBin", proposedStorageBin);
					
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Inventory[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Inventory[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PickupLine[] createPickupLine(@Valid List<AddPickupLine> newPickupLine, String loginUserID,
			String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupline")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPickupLine, headers);	
		ResponseEntity<PickupLine[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickupLine[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// POST - findPickupLine
	public PickupLine[] findPickupLine(SearchPickupLine searchPickupLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupline/findPickupLine");
			HttpEntity<?> entity = new HttpEntity<>(searchPickupLine, headers);	
			ResponseEntity<PickupLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickupLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// PATCH
	public PickupLine updatePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String pickupNumber, String itemCode, 
			String pickedStorageBin, String pickedPackCode, String actualHeNo, String loginUserID, 
			@Valid PickupLine updatePickupLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePickupLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupline/" + actualHeNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("pickupNumber", pickupNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("pickedStorageBin", pickedStorageBin)
					.queryParam("pickedPackCode", pickedPackCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<PickupLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PickupLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String pickupNumber, String itemCode, String actualHeNo, String pickedStorageBin,
			String pickedPackCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "pickupline/" + actualHeNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("pickupNumber", pickupNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("pickedStorageBin", pickedStorageBin)
					.queryParam("pickedPackCode", pickedPackCode)
					.queryParam("loginUserID", loginUserID);	
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*-----------------------------QualityHeader---------------------------------------------------------*/
	
	//POST - CREATE QUALITY HEADER
	public QualityHeader createQualityHeader(@Valid QualityHeader newQualityHeader, String loginUserID,
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityheader")
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(newQualityHeader, headers);	
			ResponseEntity<QualityHeader> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST - findQualityHeader
	public QualityHeader[] findQualityHeader(SearchQualityHeader searchQualityHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityheader/findQualityHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchQualityHeader, headers);	
			ResponseEntity<QualityHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH
	public QualityHeader updateQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID,
			@Valid QualityHeader updateQualityHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateQualityHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityheader/" + qualityInspectionNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("pickupNumber", pickupNumber)
					.queryParam("actualHeNo", actualHeNo)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<QualityHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, QualityHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityheader/" + qualityInspectionNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("pickupNumber", pickupNumber)
					.queryParam("actualHeNo", actualHeNo)
					.queryParam("loginUserID", loginUserID);	
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*-----------------------------QualityLine------------------------------------------------------------*/
	// POST - findQualityLine
	public QualityLine[] findQualityLine(SearchQualityLine searchQualityLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityline/findQualityLine");
			HttpEntity<?> entity = new HttpEntity<>(searchQualityLine, headers);	
			ResponseEntity<QualityLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public QualityLine[] createQualityLine(@Valid List<AddQualityLine> newQualityLine, String loginUserID,
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityline")
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(newQualityLine, headers);	
			ResponseEntity<QualityLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH
	public QualityLine updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
		String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID,
		@Valid QualityLine updateQualityLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateQualityLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityline/" + partnerCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("qualityInspectionNo", qualityInspectionNo)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<QualityLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, QualityLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "qualityline/" + partnerCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("lineNumber", lineNumber)
					.queryParam("qualityInspectionNo", qualityInspectionNo)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);	
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*
	 * ----------------------OutboundHeader-----------------------------------------------------------------
	 */
	// POST - findOutboundHeader
	public OutboundHeader[] findOutboundHeader(SearchOutboundHeader searchOutboundHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundheader/findOutboundHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchOutboundHeader, headers);	
			ResponseEntity<OutboundHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// PATCH
	public OutboundHeader updateOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, @Valid OutboundHeader updateOutboundHeader, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOutboundHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundheader/" + preOutboundNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OutboundHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OutboundHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundheader/" + preOutboundNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*
	 * ----------------------OutboundLine----------------------------------------------------------
	 */
	// GET - /outboundline/delivery/orderedLines
	public Long getCountofOrderedLines(String warehouseId, String preOutboundNo, String refDocNumber,
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/delivery/orderedLines")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Long> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Long.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// GET - /outboundline/delivery/totalQuantity
	public Long getSumOfOrderedQty(String warehouseId, String preOutboundNo, String refDocNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/delivery/totalQuantity")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Long> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Long.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// GET - /outboundline/delivery/deliveryLines
	public Long getDeliveryLines(String warehouseId, String preOutboundNo, String refDocNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/delivery/deliveryLines")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<Long> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Long.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// GET - /outboundline/delivery/confirmation
	public OutboundLine[] deliveryConfirmation(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/delivery/confirmation")
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<OutboundLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// POST - findOutboundLine
	public OutboundLine[] findOutboundLine(SearchOutboundLine searchOutboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/findOutboundLine");
			HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);	
			ResponseEntity<OutboundLine[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST - stock-movement-report-findOutboundLine
	public StockMovementReport[] findOutboundLineForStockMovement(SearchOutboundLine searchOutboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/stock-movement-report/findOutboundLine");
			HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
			ResponseEntity<StockMovementReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockMovementReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH ----
	public OutboundLine updateOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID,
			@Valid OutboundLine updateOutboundLine, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOutboundLine, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/" + lineNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<OutboundLine> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OutboundLine.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/" + lineNumber)
					.queryParam("warehouseId", warehouseId)
					.queryParam("preOutboundNo", preOutboundNo)
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("partnerCode", partnerCode)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*
	 * ---------------------------------OutboundReversal---------------------------------------------------
	 */
	// POST - findOutboundReversal
	public OutboundReversal[] findOutboundReversal(SearchOutboundReversal searchOutboundReversal, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundreversal/findOutboundReversal");
			HttpEntity<?> entity = new HttpEntity<>(searchOutboundReversal, headers);	
			ResponseEntity<OutboundReversal[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundReversal[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	// GET
	public OutboundReversal[] doReversal(String refDocNumber, String itemCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundline/reversal/new")
					.queryParam("refDocNumber", refDocNumber)
					.queryParam("itemCode", itemCode)
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundReversal[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundReversal[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*----------------------------------REPORTS----------------------------------------------------------*/
	
	// GET - STOCK REPORT
	public PaginatedResponse<StockReport> getStockReports(List<String> warehouseId, List<String> itemCode, String itemText,
			String stockTypeText, Integer pageNo, Integer pageSize, String sortBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/stockReport")
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemCode", itemCode)
					.queryParam("itemText", itemText)
					.queryParam("stockTypeText",stockTypeText)
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
//			ResponseEntity<StockReport[]> result = 
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockReport[].class);
			
			ParameterizedTypeReference<PaginatedResponse<StockReport>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<StockReport>>() {};
			ResponseEntity<PaginatedResponse<StockReport>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, responseType);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public StockReport[] getAllStockReports(List<String> warehouseId, List<String> itemCode, String itemText,
														  String stockTypeText,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/stockReport-all")
							.queryParam("warehouseId", warehouseId)
							.queryParam("itemCode", itemCode)
							.queryParam("itemText", itemText)
							.queryParam("stockTypeText",stockTypeText);
			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<StockReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - INVENTORY REPORT
	public PaginatedResponse<InventoryReport> getInventoryReport(List<String> warehouseId, List<String> itemCode, String storageBin,
			String stockTypeText, List<String> stSectionIds, Integer pageNo, Integer pageSize, String sortBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/inventoryReport")
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemCode", itemCode)
					.queryParam("storageBin", storageBin)
					.queryParam("stockTypeText", stockTypeText)
					.queryParam("stSectionIds", stSectionIds)
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ParameterizedTypeReference<PaginatedResponse<InventoryReport>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<InventoryReport>>() {};
			ResponseEntity<PaginatedResponse<InventoryReport>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, responseType);
			
//			ResponseEntity<InventoryReport[]> result = 
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InventoryReport[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - StockMovementReport
	public StockMovementReport[] getStockMovementReport(String warehouseId, String itemCode, String fromCreatedOn,
			String toCreatedOn, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/stockMovementReport")
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemCode", itemCode)
					.queryParam("fromCreatedOn", fromCreatedOn)
					.queryParam("toCreatedOn", toCreatedOn);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StockMovementReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockMovementReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - OrderStatusReport
	public OrderStatusReport[] getOrderStatusReport(String warehouseId, String fromDeliveryDate, String toDeliveryDate,
			List<String> customerCode, List<String> orderNumber, List<String> orderType, List<Long> statusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/orderStatusReport")
					.queryParam("warehouseId", warehouseId)
					.queryParam("fromDeliveryDate", fromDeliveryDate)
					.queryParam("toDeliveryDate", toDeliveryDate)
					.queryParam("customerCode", customerCode)
					.queryParam("orderNumber", orderNumber)
					.queryParam("orderType", orderType)
					.queryParam("statusId", statusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OrderStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OrderStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - ShipmentDelivery
	public ShipmentDeliveryReport[] getShipmentDeliveryReport(String warehouseId, String fromDeliveryDate, String toDeliveryDate,
			String storeCode, List<String> soType, String orderNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/shipmentDelivery")
					.queryParam("warehouseId", warehouseId)
					.queryParam("fromDeliveryDate", fromDeliveryDate)
					.queryParam("toDeliveryDate", toDeliveryDate)
					.queryParam("storeCode", storeCode)
					.queryParam("orderNumber", orderNumber)
					.queryParam("soType", soType);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ShipmentDeliveryReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ShipmentDeliveryReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - ShipmentDeliverySummary
	public ShipmentDeliverySummaryReport getShipmentDeliverySummaryReport(String fromDeliveryDate, String toDeliveryDate,
			List<String> customerCode,String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/shipmentDeliverySummary")
					.queryParam("fromDeliveryDate", fromDeliveryDate)
					.queryParam("toDeliveryDate", toDeliveryDate)
					.queryParam("customerCode", customerCode)
					.queryParam("warehouseId", warehouseId);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ShipmentDeliverySummaryReport> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, 
							ShipmentDeliverySummaryReport.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - ShipmentDispatchSummary
	public ShipmentDispatchSummaryReport getShipmentDispatchSummaryReport(String fromDeliveryDate, String toDeliveryDate,
			List<String> customerCode,String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/shipmentDispatchSummary")
					.queryParam("fromDeliveryDate", fromDeliveryDate)
					.queryParam("toDeliveryDate", toDeliveryDate)
					.queryParam("customerCode", customerCode)
					.queryParam("warehouseId", warehouseId);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ShipmentDispatchSummaryReport> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ShipmentDispatchSummaryReport.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - ReceiptConfimation
	public ReceiptConfimationReport getReceiptConfimationReport(String asnNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/receiptConfirmation")
					.queryParam("asnNumber", asnNumber);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ReceiptConfimationReport> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ReceiptConfimationReport.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - Dashboard
	public Dashboard getDashboard(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard")
					.queryParam("warehouseId", warehouseId);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardAwaitingASN(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/awaiting-asn")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardContainerReceived(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/container-received")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardItemReceived(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/item-received")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardShippedLine(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/shipped-line")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardNormalCount(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/normal-count")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardSpecialCount(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/special-count")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Dashboard getDashboardBinStatus(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/bin-status")
							.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Dashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Dashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET - MpbileDashboard
	public MobileDashboard getMobileDashboard(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/dashboard/mobile")
					.queryParam("warehouseId", warehouseId);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MobileDashboard> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MobileDashboard.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//---------------------------------PerpetualHeader----------------------------------------------------
	// GET ALL
	public PerpetualHeader[] getPerpetualHeaders(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PerpetualHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PerpetualHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public PerpetualHeader[] getPerpetualHeader(String warehouseId, Long cycleCountTypeId,
			String cycleCountNo, Long movementTypeId, Long subMovementTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("movementTypeId", movementTypeId)
					.queryParam("subMovementTypeId", subMovementTypeId);
					
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PerpetualHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PerpetualHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// FIND ALL - findPerpetualHeader
	public PerpetualHeaderEntity[] findPerpetualHeader (SearchPerpetualHeader searchPerpetualHeader,
		String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader/findPerpetualHeader");
			HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);	
			ResponseEntity<PerpetualHeaderEntity[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualHeaderEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST - CREATE
	public PerpetualHeader createPerpetualHeader(@Valid AddPerpetualHeader newPerpetualHeader, String loginUserID,
			String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPerpetualHeader, headers);
		ResponseEntity<PerpetualHeader> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualHeader.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// POST - RUN
	public PerpetualLineEntity[] runPerpetualHeader(@Valid RunPerpetualHeader runPerpetualHeader,
			String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader/run");
		HttpEntity<?> entity = new HttpEntity<>(runPerpetualHeader, headers);
		ResponseEntity<PerpetualLineEntity[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualLineEntity[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH 
	public PerpetualHeader updatePerpetualHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			Long movementTypeId, Long subMovementTypeId, String loginUserID,
			@Valid UpdatePerpetualHeader updatePerpetualHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePerpetualHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("movementTypeId", movementTypeId)
					.queryParam("subMovementTypeId", subMovementTypeId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PerpetualHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PerpetualHeader.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePerpetualHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			Long movementTypeId, Long subMovementTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("movementTypeId", movementTypeId)
					.queryParam("subMovementTypeId", subMovementTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH 
	public PerpetualLine[] updateAssingHHTUser(List<AssignHHTUserCC> assignHHTUser, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualline/assigingHHTUser")
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PerpetualLine[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PerpetualLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH
	public PerpetualLine[] updatePerpetualLine(String cycleCountNo, List<UpdatePerpetualLine> updatePerpetualLine,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "perpetualline/" + cycleCountNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PerpetualLine[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PerpetualLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//---------------------------------PeriodicHeader----------------------------------------------------
	// GET ALL
	public PeriodicHeaderEntity[] getPeriodicHeaders(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PeriodicHeaderEntity[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PeriodicHeaderEntity[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PeriodicHeader[] getPeriodicHeader(String warehouseId, Long cycleCountTypeId,
			String cycleCountNo, Long movementTypeId, Long subMovementTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("movementTypeId", movementTypeId)
					.queryParam("subMovementTypeId", subMovementTypeId);
					
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PeriodicHeader[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PeriodicHeader[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// FIND ALL - findPeriodicHeader
	public Page<?> findPeriodicHeader (SearchPeriodicHeader searchPeriodicHeader, Integer pageNo, 
			Integer pageSize, String sortBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader/findPeriodicHeader")
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy);
			HttpEntity<?> entity = new HttpEntity<>(searchPeriodicHeader, headers);	
			ParameterizedTypeReference<PaginatedResponse<PeriodicHeaderEntity>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<PeriodicHeaderEntity>>() {};
			ResponseEntity<PaginatedResponse<PeriodicHeaderEntity>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST - CREATE
	public PeriodicHeaderEntity createPeriodicHeader(@Valid AddPeriodicHeader newPeriodicHeader, String loginUserID,
			String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPeriodicHeader, headers);
		ResponseEntity<PeriodicHeaderEntity> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeaderEntity.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// GET ALL
	public Page<?> runPeriodicHeader(String warehouseId, Integer pageNo, Integer pageSize, String sortBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("ClientGeneral-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader/run/pagination")
					.queryParam("pageNo", pageNo)
					.queryParam("pageSize", pageSize)
					.queryParam("sortBy", sortBy)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>> responseType = 
					new ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>>() {};
			ResponseEntity<PaginatedResponse<PeriodicLineEntity>> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH 
	public PeriodicHeader updatePeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			String loginUserID, UpdatePeriodicHeader updatePeriodicHeader, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(updatePeriodicHeader, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PeriodicHeader> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PeriodicHeader.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("cycleCountTypeId", cycleCountTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH 
	public PeriodicLine[] updatePeriodicLineAssingHHTUser(List<AssignHHTUserCC> assignHHTUser, String loginUserID, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicline/assigingHHTUser")
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PeriodicLine[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PeriodicLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PATCH
	public PeriodicLine[] updatePeriodicLine(String cycleCountNo, List<UpdatePeriodicLine> updatePeriodicLine,
			String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(updatePeriodicLine, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "periodicline/" + cycleCountNo)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PeriodicLine[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PeriodicLine[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param authToken
	 */
	public void getInventoryReport(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/inventoryReport/schedule");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public InventoryReport[] generateInventoryReport(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "reports/inventoryReport/all");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InventoryReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InventoryReport[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

//	/**
//	 * sendEmail
//	 * @param email
//	 * @param authToken
//	 * @return
//	 */
//	public String sendEmail (MultipartFile file,String authToken) throws Exception {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "ClassicWMS RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			MultiValueMap<String, Object> body
//					= new LinkedMultiValueMap<>();
//			body.add("file", file);
//
//			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body,headers);
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "/sendmail/attachment").queryParam("file", file);
//			ResponseEntity<String> result = getRestTemplate().postForEntity(builder.toUriString(), entity, String.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

	// POST - SO
	public WarehouseApiResponse postSO(@Valid ShipmentOrder shipmenOrder,
													 String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/outbound/so");
		HttpEntity<?> entity = new HttpEntity<>(shipmenOrder, headers);
		ResponseEntity<WarehouseApiResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// GET
	public InboundOrder getInboundOrderById (String orderId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "orders/inbound/orders/" + orderId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundOrder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundOrder.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public OutboundOrder getOutboundOrdersById (String orderId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "orders/outbound/orders/" + orderId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundOrder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundOrder.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}	