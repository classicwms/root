package com.ustorage.core.service;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

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

import com.ustorage.core.config.PropertiesConfig;
import com.ustorage.core.model.trans.*;
import com.ustorage.core.model.reports.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransService {

	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	AuthTokenService authTokenService;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	private String getTransServiceUrl() {
		return propertiesConfig.getTransServiceUrl();
	}

	//--------------------------------------------Agreement------------------------------------------------------------------------
	// GET ALL
	public Agreement[] getAllAgreement (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Agreement[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Agreement[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public GAgreement getAgreement (String agreementNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement/" + agreementNumber);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<GAgreement> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GAgreement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Agreement createAgreement (AddAgreement newAgreement, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newAgreement, headers);
		ResponseEntity<Agreement> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Agreement.class);
		return result.getBody();
	}

	// PATCH
	public Agreement updateAgreement(String agreementId, String loginUserID,
									 @Valid UpdateAgreement updateAgreement, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateAgreement, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement/" + agreementId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Agreement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Agreement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteAgreement (String agreementId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement/" + agreementId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public GAgreement[] findAgreement(FindAgreement findAgreement, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement/find");
		HttpEntity<?> entity = new HttpEntity<>(findAgreement, headers);
		ResponseEntity<GAgreement[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GAgreement[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//SEARCH-StoreNumber
	public GAgreement[] findStoreNumber(FindStoreNumber findStoreNumber, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "agreement/findStoreNumber");
		HttpEntity<?> entity = new HttpEntity<>(findStoreNumber, headers);
		ResponseEntity<GAgreement[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GAgreement[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------Consumables------------------------------------------------------------------------
	// GET ALL
	public Consumables[] getAllConsumables (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Consumables[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Consumables[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Consumables getConsumables (String consumablesId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables/" + consumablesId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Consumables> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Consumables.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Consumables createConsumables (AddConsumables newConsumables, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newConsumables, headers);
		ResponseEntity<Consumables> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Consumables.class);
		return result.getBody();
	}

	// PATCH
	public Consumables updateConsumables(String consumablesId, String loginUserID,
										 @Valid UpdateConsumables updateConsumables, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateConsumables, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables/" + consumablesId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Consumables> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Consumables.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteConsumables (String consumablesId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables/" + consumablesId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public Consumables[] findConsumables(FindConsumables findConsumables, String authToken) {
	try {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumables/find");
		HttpEntity<?> entity = new HttpEntity<>(findConsumables, headers);
		ResponseEntity<Consumables[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Consumables[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Enquiry------------------------------------------------------------------------
	// GET ALL
	public Enquiry[] getAllEnquiry (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Enquiry[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Enquiry[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Enquiry getEnquiry (String enquiryId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry/" + enquiryId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Enquiry> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Enquiry.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Enquiry createEnquiry (AddEnquiry newEnquiry, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newEnquiry, headers);
		ResponseEntity<Enquiry> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Enquiry.class);
		return result.getBody();
	}

	// PATCH
	public Enquiry updateEnquiry(String enquiryId, String loginUserID,
								 @Valid UpdateEnquiry updateEnquiry, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateEnquiry, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry/" + enquiryId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Enquiry> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Enquiry.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteEnquiry (String enquiryId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry/" + enquiryId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public Enquiry[] findEnquiry(FindEnquiry findEnquiry, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "enquiry/find");
		HttpEntity<?> entity = new HttpEntity<>(findEnquiry, headers);
		ResponseEntity<Enquiry[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Enquiry[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------Invoice------------------------------------------------------------------------
	// GET ALL
	public Invoice[] getAllInvoice (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Invoice[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Invoice[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Invoice getInvoice (String invoiceId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice/" + invoiceId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Invoice> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Invoice.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Invoice createInvoice (AddInvoice newInvoice, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newInvoice, headers);
		ResponseEntity<Invoice> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Invoice.class);
		return result.getBody();
	}

	// PATCH
	public Invoice updateInvoice(String invoiceId, String loginUserID,
								 @Valid UpdateInvoice updateInvoice, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateInvoice, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice/" + invoiceId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Invoice> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Invoice.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteInvoice (String invoiceId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice/" + invoiceId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public Invoice[] findInvoice(FindInvoice findInvoice, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "invoice/find");
		HttpEntity<?> entity = new HttpEntity<>(findInvoice, headers);
		ResponseEntity<Invoice[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Invoice[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------LeadCustomer------------------------------------------------------------------------
	// GET ALL
	public LeadCustomer[] getAllLeadCustomer (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LeadCustomer[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LeadCustomer[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public LeadCustomer getLeadCustomer (String leadCustomerId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer/" + leadCustomerId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LeadCustomer> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LeadCustomer.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public LeadCustomer createLeadCustomer (AddLeadCustomer newLeadCustomer, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newLeadCustomer, headers);
		ResponseEntity<LeadCustomer> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, LeadCustomer.class);
		return result.getBody();
	}

	// PATCH
	public LeadCustomer updateLeadCustomer(String leadCustomerId, String loginUserID,
										   @Valid UpdateLeadCustomer updateLeadCustomer, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateLeadCustomer, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer/" + leadCustomerId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<LeadCustomer> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, LeadCustomer.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteLeadCustomer (String leadCustomerId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer/" + leadCustomerId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//SEARCH
	public LeadCustomer[] findLeadCustomer(FindLeadCustomer findLeadCustomer, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "leadCustomer/find");
		HttpEntity<?> entity = new HttpEntity<>(findLeadCustomer, headers);
		ResponseEntity<LeadCustomer[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, LeadCustomer[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	//--------------------------------------------ConsumablePurchase------------------------------------------------------------------------
	// GET ALL
	public ConsumablePurchase[] getAllConsumablePurchase (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumablePurchase");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ConsumablePurchase[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ConsumablePurchase[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ConsumablePurchase[] getConsumablePurchase (String consumablePurchaseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumablePurchase/" + consumablePurchaseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ConsumablePurchase[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ConsumablePurchase[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ConsumablePurchase[] createConsumablePurchase (List<AddConsumablePurchase> newConsumablePurchase, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumablePurchase")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newConsumablePurchase, headers);
		ResponseEntity<ConsumablePurchase[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ConsumablePurchase[].class);
		return result.getBody();
	}

	// PATCH
	public ConsumablePurchase[] updateConsumablePurchase( List<UpdateConsumablePurchase> updateConsumablePurchase,
														  String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateConsumablePurchase, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumablePurchase/" )
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ConsumablePurchase[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ConsumablePurchase[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteConsumablePurchase (String consumablePurchaseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "consumablePurchase/" + consumablePurchaseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------PaymentVoucher------------------------------------------------------------------------
	// GET ALL
	public PaymentVoucher[] getAllPaymentVoucher (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentVoucher[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentVoucher[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PaymentVoucher getPaymentVoucher (String paymentVoucherId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher/" + paymentVoucherId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentVoucher> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentVoucher.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PaymentVoucher createPaymentVoucher (AddPaymentVoucher newPaymentVoucher, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPaymentVoucher, headers);
		ResponseEntity<PaymentVoucher> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentVoucher.class);
		return result.getBody();
	}

	// PATCH
	public PaymentVoucher updatePaymentVoucher(String paymentVoucherId, String loginUserID,
											   @Valid UpdatePaymentVoucher updatePaymentVoucher, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePaymentVoucher, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher/" + paymentVoucherId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PaymentVoucher> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PaymentVoucher.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePaymentVoucher (String paymentVoucherId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher/" + paymentVoucherId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public PaymentVoucher[] findPaymentVoucher(FindPaymentVoucher findPaymentVoucher, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "paymentVoucher/find");
		HttpEntity<?> entity = new HttpEntity<>(findPaymentVoucher, headers);
		ResponseEntity<PaymentVoucher[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentVoucher[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	//--------------------------------------------Quote------------------------------------------------------------------------
	// GET ALL
	public Quote[] getAllQuote (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Quote[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Quote[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Quote getQuote (String quoteId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote/" + quoteId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Quote> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Quote.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Quote createQuote (AddQuote newQuote, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newQuote, headers);
		ResponseEntity<Quote> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Quote.class);
		return result.getBody();
	}

	// PATCH
	public Quote updateQuote(String quoteId, String loginUserID,
							 @Valid UpdateQuote updateQuote, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateQuote, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote/" + quoteId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Quote> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Quote.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteQuote (String quoteId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote/" + quoteId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public Quote[] findQuote(FindQuote findQuote, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "quote/find");
		HttpEntity<?> entity = new HttpEntity<>(findQuote, headers);
		ResponseEntity<Quote[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Quote[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------StorageUnit------------------------------------------------------------------------
	// GET ALL
	public StorageUnit[] getAllStorageUnit (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageUnit[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageUnit[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public StorageUnit getStorageUnit (String storageUnitId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit/" + storageUnitId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageUnit> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageUnit.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public StorageUnit createStorageUnit (AddStorageUnit newStorageUnit, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newStorageUnit, headers);
		ResponseEntity<StorageUnit> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageUnit.class);
		return result.getBody();
	}

	// PATCH
	public StorageUnit updateStorageUnit(String storageUnitId, String loginUserID,
										 @Valid UpdateStorageUnit updateStorageUnit, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStorageUnit, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit/" + storageUnitId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<StorageUnit> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageUnit.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteStorageUnit (String storageUnitId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit/" + storageUnitId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH

	public StorageUnit[] findStorageUnit(FindStorageUnit findStorageUnit, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "storageUnit/find");
		HttpEntity<?> entity = new HttpEntity<>(findStorageUnit, headers);
		ResponseEntity<StorageUnit[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageUnit[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------WorkOrder------------------------------------------------------------------------
	// GET ALL
	public WorkOrder[] getAllWorkOrder (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrder[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrder[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public GWorkOrder getWorkOrder (String workOrderId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder/" + workOrderId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<GWorkOrder> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, GWorkOrder.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public WorkOrder createWorkOrder (AddWorkOrder newWorkOrder, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newWorkOrder, headers);
		ResponseEntity<WorkOrder> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkOrder.class);
		return result.getBody();
	}

	// PATCH
	public WorkOrder updateWorkOrder(String workOrderId, String loginUserID,
									 @Valid UpdateWorkOrder updateWorkOrder, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWorkOrder, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder/" + workOrderId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<WorkOrder> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkOrder.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteWorkOrder (String workOrderId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder/" + workOrderId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public GWorkOrder[] findWorkOrder(FindWorkOrder findWorkOrder, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "workOrder/find");
		HttpEntity<?> entity = new HttpEntity<>(findWorkOrder, headers);
		ResponseEntity<GWorkOrder[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GWorkOrder[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------FlRent------------------------------------------------------------------------
	// GET ALL
	public FlRent[] getAllFlRent (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FlRent[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FlRent[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public FlRent getFlRent (String flRentId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent/" + flRentId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FlRent> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FlRent.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public FlRent createFlRent (AddFlRent newFlRent, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newFlRent, headers);
		ResponseEntity<FlRent> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FlRent.class);
		return result.getBody();
	}

	// PATCH
	public FlRent updateFlRent(String flRentId, String loginUserID,
							   @Valid UpdateFlRent updateFlRent, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateFlRent, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent/" + flRentId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<FlRent> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, FlRent.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteFlRent (String flRentId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent/" + flRentId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public FlRent[] findFlRent(FindFlRent findFlRent, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "flRent/find");
		HttpEntity<?> entity = new HttpEntity<>(findFlRent, headers);
		ResponseEntity<FlRent[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FlRent[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------HandlingCharge------------------------------------------------------------------------
	// GET ALL
	public HandlingCharge[] getAllHandlingCharge (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingCharge[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingCharge[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public HandlingCharge getHandlingCharge (String handlingChargeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge/" + handlingChargeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingCharge> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingCharge.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public HandlingCharge createHandlingCharge (AddHandlingCharge newHandlingCharge, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newHandlingCharge, headers);
		ResponseEntity<HandlingCharge> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingCharge.class);
		return result.getBody();
	}

	// PATCH
	public HandlingCharge updateHandlingCharge(String handlingChargeId, String loginUserID,
											   @Valid UpdateHandlingCharge updateHandlingCharge, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateHandlingCharge, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge/" + handlingChargeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<HandlingCharge> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingCharge.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteHandlingCharge (String handlingChargeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge/" + handlingChargeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public HandlingCharge[] findHandlingCharge(FindHandlingCharge findHandlingCharge, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "handlingCharge/find");
		HttpEntity<?> entity = new HttpEntity<>(findHandlingCharge, headers);
		ResponseEntity<HandlingCharge[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingCharge[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//--------------------------------------------Trip------------------------------------------------------------------------
	// GET ALL
	public Trip[] getAllTrip (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Trip[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Trip[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Trip getTrip (String tripId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip/" + tripId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Trip> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Trip.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Trip createTrip (AddTrip newTrip, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newTrip, headers);
		ResponseEntity<Trip> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Trip.class);
		return result.getBody();
	}

	// PATCH
	public Trip updateTrip(String tripId, String loginUserID,
						   @Valid UpdateTrip updateTrip, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateTrip, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip/" + tripId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Trip> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Trip.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteTrip (String tripId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip/" + tripId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public Trip[] findTrip(FindTrip findTrip, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "trip/find");
		HttpEntity<?> entity = new HttpEntity<>(findTrip, headers);
		ResponseEntity<Trip[]> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Trip[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
//--------------------------------------------Reports------------------------------------------------------------------------

	// WorkOrderStatus SEARCH
	public WorkOrderStatusReport[] getWorkOrderStatus (WorkOrderStatusModel workOrderStatusModel, String authToken) throws ParseException {
		try {
			/*WorkOrderStatusModel requestDataForService = new WorkOrderStatusModel();
			BeanUtils.copyProperties(requestData, requestDataForService,
					com.ustorage.core.util.CommonUtils.getNullPropertyNames(requestData));
			if (requestData.getStartDate() != null) {
				requestDataForService.setStartDate(requestData.getStartDate());
			}
			if (requestData.getEndDate() != null) {
				requestDataForService.setEndDate(requestData.getEndDate());
			}*/
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/work-order-status-report");
			HttpEntity<?> entity = new HttpEntity<>(workOrderStatusModel, headers);
			ResponseEntity<WorkOrderStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkOrderStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// EfficeiencyRecord SEARCH
	public EfficiencyRecordReport[] getEfficiencyRecord (EfficiencyRecord efficiencyRecord, String authToken) throws ParseException {
		try {
			/*EfficiencyRecord requestDataForService = new EfficiencyRecord();
			BeanUtils.copyProperties(requestData, requestDataForService,
					com.ustorage.core.util.CommonUtils.getNullPropertyNames(requestData));
			if (requestData.getStartDate() != null) {
				requestDataForService.setStartDate(requestData.getStartDate());
			}
			if (requestData.getEndDate() != null) {
				requestDataForService.setEndDate(requestData.getEndDate());
			}*/
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/efficiency-record-report");
			HttpEntity<?> entity = new HttpEntity<>(efficiencyRecord, headers);
			ResponseEntity<EfficiencyRecordReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EfficiencyRecordReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// QuotationStatus SEARCH
	public QuotationStatusReport[] getQuotationStatus (QuotationStatus quotationStatus, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/quotation-status-report");
			HttpEntity<?> entity = new HttpEntity<>(quotationStatus, headers);
			ResponseEntity<QuotationStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QuotationStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// EnquiryStatus SEARCH
	public EnquiryStatusReport[] getEnquiryStatus (EnquiryStatusModel enquiryStatus, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/enquiry-status-report");
			HttpEntity<?> entity = new HttpEntity<>(enquiryStatus, headers);
			ResponseEntity<EnquiryStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EnquiryStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// FillrateStatus SEARCH
	public FillrateStatusReport[] getFillrateStatus (FillrateStatus fillrateStatus, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/fillrate-status-report");
			HttpEntity<?> entity = new HttpEntity<>(fillrateStatus, headers);
			ResponseEntity<FillrateStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FillrateStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// ContractRenewalStatus SEARCH
	public ContractRenewalStatusReport[] getContractRenewalStatus (ContractRenewalStatus contractRenewalStatus, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/contract-renewal-status-report");
			HttpEntity<?> entity = new HttpEntity<>(contractRenewalStatus, headers);
			ResponseEntity<ContractRenewalStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContractRenewalStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// PaymentDueStatus SEARCH
	public PaymentDueStatusReport[] getPaymentDueStatus (PaymentDueStatus paymentDueStatus, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/payment-due-status-report");
			HttpEntity<?> entity = new HttpEntity<>(paymentDueStatus, headers);
			ResponseEntity<PaymentDueStatusReport[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentDueStatusReport[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DocumentStatus SEARCH
	public DocumentStatusReport getDocumentStatus (DocumentStatusInput documentStatusInput, String authToken) throws ParseException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/document-status-report");
			HttpEntity<?> entity = new HttpEntity<>(documentStatusInput, headers);
			ResponseEntity<DocumentStatusReport> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DocumentStatusReport.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET-customer dropdown
	public CustomerDropdownList getCustomerDropdownList (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("UStorage-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getTransServiceUrl() + "/report/dropdown/customerName");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CustomerDropdownList> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
					entity, CustomerDropdownList.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET-storage unit dropdown
	public StorageDropdownList getStorageDropdownList (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("UStorage-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getTransServiceUrl() + "/report/dropdown/storageUnit");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageDropdownList> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
					entity, StorageDropdownList.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST -
	public Dropdown createCustomerDetail(@Valid CustomerDetailInput customerDetailInput,
																  String authToken) {
		try {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(getTransServiceUrl() + "/report/customerDetail");
		HttpEntity<?> entity = new HttpEntity<>(customerDetailInput, headers);
		ResponseEntity<Dropdown> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
				entity, Dropdown.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// dashboard-invoiceAmount
	public BilledPaid getBilledPaid (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("UStorage-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getTransServiceUrl() + "/report/dashboard/billedAndPaidAmount");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BilledPaid> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
					entity, BilledPaid.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// dashboard-lead and customer
	public LeadAndCustomer getLeadAndCustomer (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("UStorage-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getTransServiceUrl() + "/report/dashboard/leadAndCustomer");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LeadAndCustomer> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
					entity, LeadAndCustomer.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
