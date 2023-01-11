package com.ustorage.core.service;

import java.util.Collections;

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
import com.ustorage.core.model.masters.*;

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

	private String getUstorageServiceUrl() {
		return propertiesConfig.getUstorageServiceUrl();
	}

	//--------------------------------------------DocumentStorage------------------------------------------------------------------------

	// GET
	public DocumentStorage getDocumentStorage (String documentNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStorage/" + documentNumber);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DocumentStorage> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DocumentStorage.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DocumentStorage createDocumentStorage (AddDocumentStorage newDocumentStorage, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStorage")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newDocumentStorage, headers);
		ResponseEntity<DocumentStorage> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DocumentStorage.class);
		return result.getBody();
	}


	// DELETE
	public boolean deleteDocumentStorage (String documentNumber, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStorage/" + documentNumber)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//SEARCH
	public DocumentStorage[] findDocumentStorage(FindDocumentStorage findDocumentStorage, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStorage/find");
			HttpEntity<?> entity = new HttpEntity<>(findDocumentStorage, headers);
			ResponseEntity<DocumentStorage[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DocumentStorage[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------AccountStatus------------------------------------------------------------------------
	// GET ALL
	public AccountStatus[] getAllAccountStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "accountStatus");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AccountStatus[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AccountStatus[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public AccountStatus getAccountStatus (String accountStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "accountStatus/" + accountStatusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AccountStatus> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AccountStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public AccountStatus createAccountStatus (AddAccountStatus newAccountStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "accountStatus")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newAccountStatus, headers);
		ResponseEntity<AccountStatus> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AccountStatus.class);
		return result.getBody();
	}
	
	// PATCH
	public AccountStatus updateAccountStatus(String accountStatusId, String loginUserID,
			@Valid UpdateAccountStatus updateAccountStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateAccountStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "accountStatus/" + accountStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<AccountStatus> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, AccountStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteAccountStatus (String accountStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "accountStatus/" + accountStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Bin------------------------------------------------------------------------
	// GET ALL
	public Bin[] getAllBin (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "bin");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Bin[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Bin[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Bin getBin (String binId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "bin/" + binId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Bin> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Bin.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Bin createBin (AddBin newBin, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "bin")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newBin, headers);
		ResponseEntity<Bin> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Bin.class);
		return result.getBody();
	}

	// PATCH
	public Bin updateBin(String binId, String loginUserID,
						 @Valid UpdateBin updateBin, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateBin, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "bin/" + binId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Bin> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Bin.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteBin (String binId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "bin/" + binId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------BusinessPartnerType------------------------------------------------------------------------
	// GET ALL
	public BusinessPartnerType[] getAllBusinessPartnerType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "businessPartnerType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BusinessPartnerType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartnerType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public BusinessPartnerType getBusinessPartnerType (String businessPartnerTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "businessPartnerType/" + businessPartnerTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BusinessPartnerType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BusinessPartnerType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public BusinessPartnerType createBusinessPartnerType (AddBusinessPartnerType newBusinessPartnerType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "businessPartnerType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newBusinessPartnerType, headers);
		ResponseEntity<BusinessPartnerType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BusinessPartnerType.class);
		return result.getBody();
	}

	// PATCH
	public BusinessPartnerType updateBusinessPartnerType(String businessPartnerTypeId, String loginUserID,
														 @Valid UpdateBusinessPartnerType updateBusinessPartnerType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateBusinessPartnerType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "businessPartnerType/" + businessPartnerTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<BusinessPartnerType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BusinessPartnerType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteBusinessPartnerType (String businessPartnerTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "businessPartnerType/" + businessPartnerTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Currency------------------------------------------------------------------------
	// GET ALL
	public Currency[] getAllCurrency (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "currency");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Currency[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Currency[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Currency getCurrency (String currencyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "currency/" + currencyId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Currency> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Currency.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Currency createCurrency (AddCurrency newCurrency, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "currency")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newCurrency, headers);
		ResponseEntity<Currency> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Currency.class);
		return result.getBody();
	}

	// PATCH
	public Currency updateCurrency(String currencyId, String loginUserID,
								   @Valid UpdateCurrency updateCurrency, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCurrency, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "currency/" + currencyId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Currency> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Currency.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteCurrency (String currencyId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "currency/" + currencyId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------CustomerGroup------------------------------------------------------------------------
	// GET ALL
	public CustomerGroup[] getAllCustomerGroup (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerGroup");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CustomerGroup[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CustomerGroup[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public CustomerGroup getCustomerGroup (String customerGroupId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerGroup/" + customerGroupId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CustomerGroup> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CustomerGroup.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public CustomerGroup createCustomerGroup (AddCustomerGroup newCustomerGroup, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerGroup")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newCustomerGroup, headers);
		ResponseEntity<CustomerGroup> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CustomerGroup.class);
		return result.getBody();
	}

	// PATCH
	public CustomerGroup updateCustomerGroup(String customerGroupId, String loginUserID,
											 @Valid UpdateCustomerGroup updateCustomerGroup, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCustomerGroup, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerGroup/" + customerGroupId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<CustomerGroup> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CustomerGroup.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteCustomerGroup (String customerGroupId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerGroup/" + customerGroupId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------CustomerType------------------------------------------------------------------------
	// GET ALL
	public CustomerType[] getAllCustomerType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CustomerType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CustomerType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public CustomerType getCustomerType (String customerTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerType/" + customerTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CustomerType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CustomerType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public CustomerType createCustomerType (AddCustomerType newCustomerType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newCustomerType, headers);
		ResponseEntity<CustomerType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CustomerType.class);
		return result.getBody();
	}

	// PATCH
	public CustomerType updateCustomerType(String customerTypeId, String loginUserID,
										   @Valid UpdateCustomerType updateCustomerType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCustomerType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerType/" + customerTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<CustomerType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CustomerType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteCustomerType (String customerTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "customerType/" + customerTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------DocumentStatus------------------------------------------------------------------------
	// GET ALL
	public DocumentStatus[] getAllDocumentStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStatus");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DocumentStatus[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DocumentStatus[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public DocumentStatus getDocumentStatus (String documentStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStatus/" + documentStatusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DocumentStatus> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DocumentStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DocumentStatus createDocumentStatus (AddDocumentStatus newDocumentStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStatus")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newDocumentStatus, headers);
		ResponseEntity<DocumentStatus> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DocumentStatus.class);
		return result.getBody();
	}

	// PATCH
	public DocumentStatus updateDocumentStatus(String documentStatusId, String loginUserID,
											   @Valid UpdateDocumentStatus updateDocumentStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDocumentStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStatus/" + documentStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<DocumentStatus> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DocumentStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteDocumentStatus (String documentStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "documentStatus/" + documentStatusId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------DoorType------------------------------------------------------------------------
	// GET ALL
	public DoorType[] getAllDoorType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "doorType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DoorType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DoorType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public DoorType getDoorType (String doorTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "doorType/" + doorTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DoorType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DoorType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DoorType createDoorType (AddDoorType newDoorType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "doorType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newDoorType, headers);
		ResponseEntity<DoorType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DoorType.class);
		return result.getBody();
	}

	// PATCH
	public DoorType updateDoorType(String doorTypeId, String loginUserID,
								   @Valid UpdateDoorType updateDoorType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDoorType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "doorType/" + doorTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<DoorType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DoorType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteDoorType (String doorTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "doorType/" + doorTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------EnquiryStatus------------------------------------------------------------------------
	// GET ALL
	public EnquiryStatus[] getAllEnquiryStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "enquiryStatus");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EnquiryStatus[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EnquiryStatus[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public EnquiryStatus getEnquiryStatus (String enquiryStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "enquiryStatus/" + enquiryStatusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EnquiryStatus> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EnquiryStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public EnquiryStatus createEnquiryStatus (AddEnquiryStatus newEnquiryStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "enquiryStatus")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newEnquiryStatus, headers);
		ResponseEntity<EnquiryStatus> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EnquiryStatus.class);
		return result.getBody();
	}

	// PATCH
	public EnquiryStatus updateEnquiryStatus(String enquiryStatusId, String loginUserID,
											 @Valid UpdateEnquiryStatus updateEnquiryStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateEnquiryStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "enquiryStatus/" + enquiryStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<EnquiryStatus> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, EnquiryStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteEnquiryStatus (String enquiryStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "enquiryStatus/" + enquiryStatusId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------InvoiceCurrency------------------------------------------------------------------------
	// GET ALL
	public InvoiceCurrency[] getAllInvoiceCurrency (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceCurrency");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InvoiceCurrency[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceCurrency[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public InvoiceCurrency getInvoiceCurrency (String invoiceCurrencyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceCurrency/" + invoiceCurrencyId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InvoiceCurrency> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceCurrency.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public InvoiceCurrency createInvoiceCurrency (AddInvoiceCurrency newInvoiceCurrency, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceCurrency")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newInvoiceCurrency, headers);
		ResponseEntity<InvoiceCurrency> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceCurrency.class);
		return result.getBody();
	}

	// PATCH
	public InvoiceCurrency updateInvoiceCurrency(String invoiceCurrencyId, String loginUserID,
												 @Valid UpdateInvoiceCurrency updateInvoiceCurrency, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateInvoiceCurrency, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceCurrency/" + invoiceCurrencyId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<InvoiceCurrency> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InvoiceCurrency.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteInvoiceCurrency (String invoiceCurrencyId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceCurrency/" + invoiceCurrencyId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------InvoiceDocumentStatus------------------------------------------------------------------------
	// GET ALL
	public InvoiceDocumentStatus[] getAllInvoiceDocumentStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceDocumentStatus");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InvoiceDocumentStatus[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceDocumentStatus[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public InvoiceDocumentStatus getInvoiceDocumentStatus (String invoiceDocumentStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceDocumentStatus/" + invoiceDocumentStatusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InvoiceDocumentStatus> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceDocumentStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public InvoiceDocumentStatus createInvoiceDocumentStatus (AddInvoiceDocumentStatus newInvoiceDocumentStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceDocumentStatus")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newInvoiceDocumentStatus, headers);
		ResponseEntity<InvoiceDocumentStatus> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceDocumentStatus.class);
		return result.getBody();
	}

	// PATCH
	public InvoiceDocumentStatus updateInvoiceDocumentStatus(String invoiceDocumentStatusId, String loginUserID,
															 @Valid UpdateInvoiceDocumentStatus updateInvoiceDocumentStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateInvoiceDocumentStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceDocumentStatus/" + invoiceDocumentStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<InvoiceDocumentStatus> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InvoiceDocumentStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteInvoiceDocumentStatus (String invoiceDocumentStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "invoiceDocumentStatus/" + invoiceDocumentStatusId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ItemGroup------------------------------------------------------------------------
	// GET ALL
	public ItemGroup[] getAllItemGroup (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemGroup");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemGroup[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ItemGroup getItemGroup (String itemGroupId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemGroup/" + itemGroupId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemGroup> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroup.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ItemGroup createItemGroup (AddItemGroup newItemGroup, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemGroup")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newItemGroup, headers);
		ResponseEntity<ItemGroup> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroup.class);
		return result.getBody();
	}

	// PATCH
	public ItemGroup updateItemGroup(String itemGroupId, String loginUserID,
									 @Valid UpdateItemGroup updateItemGroup, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateItemGroup, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemGroup/" + itemGroupId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ItemGroup> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemGroup.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteItemGroup (String itemGroupId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemGroup/" + itemGroupId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ItemType------------------------------------------------------------------------
	// GET ALL
	public ItemType[] getAllItemType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ItemType getItemType (String itemTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemType/" + itemTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ItemType createItemType (AddItemType newItemType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newItemType, headers);
		ResponseEntity<ItemType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemType.class);
		return result.getBody();
	}

	// PATCH
	public ItemType updateItemType(String itemTypeId, String loginUserID,
								   @Valid UpdateItemType updateItemType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateItemType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemType/" + itemTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ItemType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteItemType (String itemTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "itemType/" + itemTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ModeOfPayment------------------------------------------------------------------------
	// GET ALL
	public ModeOfPayment[] getAllModeOfPayment (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "modeOfPayment");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ModeOfPayment[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ModeOfPayment[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ModeOfPayment getModeOfPayment (String modeOfPaymentId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "modeOfPayment/" + modeOfPaymentId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ModeOfPayment> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ModeOfPayment.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ModeOfPayment createModeOfPayment (AddModeOfPayment newModeOfPayment, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "modeOfPayment")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newModeOfPayment, headers);
		ResponseEntity<ModeOfPayment> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ModeOfPayment.class);
		return result.getBody();
	}

	// PATCH
	public ModeOfPayment updateModeOfPayment(String modeOfPaymentId, String loginUserID,
											 @Valid UpdateModeOfPayment updateModeOfPayment, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateModeOfPayment, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "modeOfPayment/" + modeOfPaymentId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ModeOfPayment> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ModeOfPayment.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteModeOfPayment (String modeOfPaymentId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "modeOfPayment/" + modeOfPaymentId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Nationality------------------------------------------------------------------------
	// GET ALL
	public Nationality[] getAllNationality (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "nationality");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Nationality[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Nationality[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Nationality getNationality (String nationalityId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "nationality/" + nationalityId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Nationality> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Nationality.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Nationality createNationality (AddNationality newNationality, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "nationality")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newNationality, headers);
		ResponseEntity<Nationality> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Nationality.class);
		return result.getBody();
	}

	// PATCH
	public Nationality updateNationality(String nationalityId, String loginUserID,
										 @Valid UpdateNationality updateNationality, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateNationality, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "nationality/" + nationalityId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Nationality> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Nationality.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteNationality (String nationalityId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "nationality/" + nationalityId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------PaymentMode------------------------------------------------------------------------
	// GET ALL
	public PaymentMode[] getAllPaymentMode (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentMode");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentMode[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentMode[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PaymentMode getPaymentMode (String paymentModeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentMode/" + paymentModeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentMode> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentMode.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PaymentMode createPaymentMode (AddPaymentMode newPaymentMode, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentMode")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPaymentMode, headers);
		ResponseEntity<PaymentMode> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentMode.class);
		return result.getBody();
	}

	// PATCH
	public PaymentMode updatePaymentMode(String paymentModeId, String loginUserID,
										 @Valid UpdatePaymentMode updatePaymentMode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePaymentMode, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentMode/" + paymentModeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PaymentMode> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PaymentMode.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePaymentMode (String paymentModeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentMode/" + paymentModeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------PaymentPeriod------------------------------------------------------------------------
	// GET ALL
	public PaymentPeriod[] getAllPaymentPeriod (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentPeriod");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentPeriod[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentPeriod[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PaymentPeriod getPaymentPeriod (String paymentPeriodId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentPeriod/" + paymentPeriodId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentPeriod> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentPeriod.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PaymentPeriod createPaymentPeriod (AddPaymentPeriod newPaymentPeriod, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentPeriod")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPaymentPeriod, headers);
		ResponseEntity<PaymentPeriod> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentPeriod.class);
		return result.getBody();
	}

	// PATCH
	public PaymentPeriod updatePaymentPeriod(String paymentPeriodId, String loginUserID,
											 @Valid UpdatePaymentPeriod updatePaymentPeriod, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePaymentPeriod, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentPeriod/" + paymentPeriodId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PaymentPeriod> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PaymentPeriod.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePaymentPeriod (String paymentPeriodId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentPeriod/" + paymentPeriodId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------PaymentTerm------------------------------------------------------------------------
	// GET ALL
	public PaymentTerm[] getAllPaymentTerm (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentTerm");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentTerm[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentTerm[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PaymentTerm getPaymentTerm (String paymentTermId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentTerm/" + paymentTermId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentTerm> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentTerm.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PaymentTerm createPaymentTerm (AddPaymentTerm newPaymentTerm, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentTerm")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPaymentTerm, headers);
		ResponseEntity<PaymentTerm> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentTerm.class);
		return result.getBody();
	}

	// PATCH
	public PaymentTerm updatePaymentTerm(String paymentTermId, String loginUserID,
										 @Valid UpdatePaymentTerm updatePaymentTerm, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePaymentTerm, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentTerm/" + paymentTermId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PaymentTerm> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PaymentTerm.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePaymentTerm (String paymentTermId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentTerm/" + paymentTermId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------PaymentType------------------------------------------------------------------------
	// GET ALL
	public PaymentType[] getAllPaymentType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PaymentType getPaymentType (String paymentTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentType/" + paymentTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PaymentType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PaymentType createPaymentType (AddPaymentType newPaymentType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPaymentType, headers);
		ResponseEntity<PaymentType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PaymentType.class);
		return result.getBody();
	}

	// PATCH
	public PaymentType updatePaymentType(String paymentTypeId, String loginUserID,
										 @Valid UpdatePaymentType updatePaymentType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePaymentType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentType/" + paymentTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PaymentType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PaymentType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePaymentType (String paymentTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "paymentType/" + paymentTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Phase------------------------------------------------------------------------
	// GET ALL
	public Phase[] getAllPhase (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "phase");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Phase[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Phase[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Phase getPhase (String phaseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "phase/" + phaseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Phase> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Phase.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Phase createPhase (AddPhase newPhase, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "phase")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPhase, headers);
		ResponseEntity<Phase> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Phase.class);
		return result.getBody();
	}

	// PATCH
	public Phase updatePhase(String phaseId, String loginUserID,
							 @Valid UpdatePhase updatePhase, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePhase, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "phase/" + phaseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Phase> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Phase.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePhase (String phaseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "phase/" + phaseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Rack------------------------------------------------------------------------
	// GET ALL
	public Rack[] getAllRack (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rack");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Rack[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Rack[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Rack getRack (String rackId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rack/" + rackId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Rack> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Rack.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Rack createRack (AddRack newRack, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rack")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newRack, headers);
		ResponseEntity<Rack> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Rack.class);
		return result.getBody();
	}

	// PATCH
	public Rack updateRack(String rackId, String loginUserID,
						   @Valid UpdateRack updateRack, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateRack, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rack/" + rackId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Rack> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Rack.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteRack (String rackId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rack/" + rackId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------RentPeriod------------------------------------------------------------------------
	// GET ALL
	public RentPeriod[] getAllRentPeriod (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rentPeriod");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RentPeriod[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RentPeriod[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public RentPeriod getRentPeriod (String rentPeriodId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rentPeriod/" + rentPeriodId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RentPeriod> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RentPeriod.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public RentPeriod createRentPeriod (AddRentPeriod newRentPeriod, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rentPeriod")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newRentPeriod, headers);
		ResponseEntity<RentPeriod> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RentPeriod.class);
		return result.getBody();
	}

	// PATCH
	public RentPeriod updateRentPeriod(String rentPeriodId, String loginUserID,
									   @Valid UpdateRentPeriod updateRentPeriod, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateRentPeriod, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rentPeriod/" + rentPeriodId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<RentPeriod> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, RentPeriod.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteRentPeriod (String rentPeriodId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "rentPeriod/" + rentPeriodId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------RequirementType------------------------------------------------------------------------
	// GET ALL
	public RequirementType[] getAllRequirementType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "requirementType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RequirementType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RequirementType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public RequirementType getRequirementType (String requirementTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "requirementType/" + requirementTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RequirementType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RequirementType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public RequirementType createRequirementType (AddRequirementType newRequirementType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "requirementType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newRequirementType, headers);
		ResponseEntity<RequirementType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RequirementType.class);
		return result.getBody();
	}

	// PATCH
	public RequirementType updateRequirementType(String requirementTypeId, String loginUserID,
												 @Valid UpdateRequirementType updateRequirementType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateRequirementType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "requirementType/" + requirementTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<RequirementType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, RequirementType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteRequirementType (String requirementTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "requirementType/" + requirementTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Room------------------------------------------------------------------------
	// GET ALL
	public Room[] getAllRoom (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "room");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Room[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Room[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Room getRoom (String roomId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "room/" + roomId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Room> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Room.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Room createRoom (AddRoom newRoom, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "room")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newRoom, headers);
		ResponseEntity<Room> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Room.class);
		return result.getBody();
	}

	// PATCH
	public Room updateRoom(String roomId, String loginUserID,
						   @Valid UpdateRoom updateRoom, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateRoom, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "room/" + roomId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Room> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Room.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteRoom (String roomId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "room/" + roomId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ServiceRendered------------------------------------------------------------------------
	// GET ALL
	public ServiceRendered[] getAllServiceRendered (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "serviceRendered");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ServiceRendered[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ServiceRendered[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ServiceRendered getServiceRendered (String serviceRenderedId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "serviceRendered/" + serviceRenderedId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ServiceRendered> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ServiceRendered.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ServiceRendered createServiceRendered (AddServiceRendered newServiceRendered, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "serviceRendered")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newServiceRendered, headers);
		ResponseEntity<ServiceRendered> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ServiceRendered.class);
		return result.getBody();
	}

	// PATCH
	public ServiceRendered updateServiceRendered(String serviceRenderedId, String loginUserID,
												 @Valid UpdateServiceRendered updateServiceRendered, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateServiceRendered, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "serviceRendered/" + serviceRenderedId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ServiceRendered> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ServiceRendered.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteServiceRendered (String serviceRenderedId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "serviceRendered/" + serviceRenderedId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Status------------------------------------------------------------------------
	// GET ALL
	public Status[] getAllStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "status");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Status[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Status[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Status getStatus (String statusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "status/" + statusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Status> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Status.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Status createStatus (AddStatus newStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "status")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newStatus, headers);
		ResponseEntity<Status> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Status.class);
		return result.getBody();
	}

	// PATCH
	public Status updateStatus(String statusId, String loginUserID,
							   @Valid UpdateStatus updateStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "status/" + statusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Status> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Status.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteStatus (String statusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "status/" + statusId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------StorageType------------------------------------------------------------------------
	// GET ALL
	public StorageType[] getAllStorageType (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storageType");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageType[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageType[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public StorageType getStorageType (String storageTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storageType/" + storageTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageType> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public StorageType createStorageType (AddStorageType newStorageType, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storageType")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newStorageType, headers);
		ResponseEntity<StorageType> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageType.class);
		return result.getBody();
	}

	// PATCH
	public StorageType updateStorageType(String storageTypeId, String loginUserID,
										 @Valid UpdateStorageType updateStorageType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStorageType, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storageType/" + storageTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<StorageType> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageType.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteStorageType (String storageTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storageType/" + storageTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------StoreNumberSize------------------------------------------------------------------------
	// GET ALL
	public StoreNumberSize[] getAllStoreNumberSize (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeNumberSize");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StoreNumberSize[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StoreNumberSize[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public StoreNumberSize getStoreNumberSize (String storeNumberSizeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeNumberSize/" + storeNumberSizeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StoreNumberSize> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StoreNumberSize.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public StoreNumberSize createStoreNumberSize (AddStoreNumberSize newStoreNumberSize, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeNumberSize")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newStoreNumberSize, headers);
		ResponseEntity<StoreNumberSize> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StoreNumberSize.class);
		return result.getBody();
	}

	// PATCH
	public StoreNumberSize updateStoreNumberSize(String storeNumberSizeId, String loginUserID,
												 @Valid UpdateStoreNumberSize updateStoreNumberSize, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStoreNumberSize, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeNumberSize/" + storeNumberSizeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<StoreNumberSize> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StoreNumberSize.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteStoreNumberSize (String storeNumberSizeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeNumberSize/" + storeNumberSizeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------StoreSize------------------------------------------------------------------------
	// GET ALL
	public StoreSize[] getAllStoreSize (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeSize");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StoreSize[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StoreSize[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public StoreSize getStoreSize (String storeSizeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeSize/" + storeSizeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StoreSize> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StoreSize.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public StoreSize createStoreSize (AddStoreSize newStoreSize, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeSize")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newStoreSize, headers);
		ResponseEntity<StoreSize> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StoreSize.class);
		return result.getBody();
	}

	// PATCH
	public StoreSize updateStoreSize(String storeSizeId, String loginUserID,
									 @Valid UpdateStoreSize updateStoreSize, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStoreSize, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeSize/" + storeSizeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<StoreSize> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StoreSize.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteStoreSize (String storeSizeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "storeSize/" + storeSizeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------UnitOfMeasure------------------------------------------------------------------------
	// GET ALL
	public UnitOfMeasure[] getAllUnitOfMeasure (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "unitOfMeasure");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UnitOfMeasure[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UnitOfMeasure[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public UnitOfMeasure getUnitOfMeasure (String unitOfMeasureId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "unitOfMeasure/" + unitOfMeasureId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UnitOfMeasure> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UnitOfMeasure.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public UnitOfMeasure createUnitOfMeasure (AddUnitOfMeasure newUnitOfMeasure, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "unitOfMeasure")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newUnitOfMeasure, headers);
		ResponseEntity<UnitOfMeasure> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UnitOfMeasure.class);
		return result.getBody();
	}

	// PATCH
	public UnitOfMeasure updateUnitOfMeasure(String unitOfMeasureId, String loginUserID,
											 @Valid UpdateUnitOfMeasure updateUnitOfMeasure, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateUnitOfMeasure, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "unitOfMeasure/" + unitOfMeasureId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<UnitOfMeasure> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UnitOfMeasure.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteUnitOfMeasure (String unitOfMeasureId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "unitOfMeasure/" + unitOfMeasureId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Warehouse------------------------------------------------------------------------
	// GET ALL
	public Warehouse[] getAllWarehouse (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "warehouse");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Warehouse[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Warehouse getWarehouse (String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "warehouse/" + warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Warehouse> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Warehouse createWarehouse (AddWarehouse newWarehouse, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "warehouse")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newWarehouse, headers);
		ResponseEntity<Warehouse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Warehouse.class);
		return result.getBody();
	}

	// PATCH
	public Warehouse updateWarehouse(String warehouseId, String loginUserID,
									 @Valid UpdateWarehouse updateWarehouse, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWarehouse, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "warehouse/" + warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Warehouse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Warehouse.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteWarehouse (String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "warehouse/" + warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------WorkOrderCreatedBy------------------------------------------------------------------------
	// GET ALL
	public WorkOrderCreatedBy[] getAllWorkOrderCreatedBy (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderCreatedBy");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderCreatedBy[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderCreatedBy[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public WorkOrderCreatedBy getWorkOrderCreatedBy (String workOrderCreatedById, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderCreatedBy/" + workOrderCreatedById);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderCreatedBy> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderCreatedBy.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public WorkOrderCreatedBy createWorkOrderCreatedBy (AddWorkOrderCreatedBy newWorkOrderCreatedBy, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderCreatedBy")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newWorkOrderCreatedBy, headers);
		ResponseEntity<WorkOrderCreatedBy> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkOrderCreatedBy.class);
		return result.getBody();
	}

	// PATCH
	public WorkOrderCreatedBy updateWorkOrderCreatedBy(String workOrderCreatedById, String loginUserID,
													   @Valid UpdateWorkOrderCreatedBy updateWorkOrderCreatedBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWorkOrderCreatedBy, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderCreatedBy/" + workOrderCreatedById)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<WorkOrderCreatedBy> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkOrderCreatedBy.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteWorkOrderCreatedBy (String workOrderCreatedById, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderCreatedBy/" + workOrderCreatedById)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------WorkOrderProcessedBy------------------------------------------------------------------------
	// GET ALL
	public WorkOrderProcessedBy[] getAllWorkOrderProcessedBy (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderProcessedBy");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderProcessedBy[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderProcessedBy[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public WorkOrderProcessedBy getWorkOrderProcessedBy (String workOrderProcessedById, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderProcessedBy/" + workOrderProcessedById);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderProcessedBy> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderProcessedBy.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public WorkOrderProcessedBy createWorkOrderProcessedBy (AddWorkOrderProcessedBy newWorkOrderProcessedBy, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderProcessedBy")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newWorkOrderProcessedBy, headers);
		ResponseEntity<WorkOrderProcessedBy> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkOrderProcessedBy.class);
		return result.getBody();
	}

	// PATCH
	public WorkOrderProcessedBy updateWorkOrderProcessedBy(String workOrderProcessedById, String loginUserID,
														   @Valid UpdateWorkOrderProcessedBy updateWorkOrderProcessedBy, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWorkOrderProcessedBy, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderProcessedBy/" + workOrderProcessedById)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<WorkOrderProcessedBy> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkOrderProcessedBy.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteWorkOrderProcessedBy (String workOrderProcessedById, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderProcessedBy/" + workOrderProcessedById)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------WorkOrderStatus------------------------------------------------------------------------
	// GET ALL
	public WorkOrderStatus[] getAllWorkOrderStatus (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderStatus");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderStatus[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderStatus[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public WorkOrderStatus getWorkOrderStatus (String workOrderStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderStatus/" + workOrderStatusId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkOrderStatus> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkOrderStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public WorkOrderStatus createWorkOrderStatus (AddWorkOrderStatus newWorkOrderStatus, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderStatus")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newWorkOrderStatus, headers);
		ResponseEntity<WorkOrderStatus> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkOrderStatus.class);
		return result.getBody();
	}

	// PATCH
	public WorkOrderStatus updateWorkOrderStatus(String workOrderStatusId, String loginUserID,
												 @Valid UpdateWorkOrderStatus updateWorkOrderStatus, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWorkOrderStatus, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderStatus/" + workOrderStatusId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<WorkOrderStatus> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkOrderStatus.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteWorkOrderStatus (String workOrderStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "workOrderStatus/" + workOrderStatusId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Zone------------------------------------------------------------------------
	// GET ALL
	public Zone[] getAllZone (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "zone");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Zone[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Zone[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Zone getZone (String zoneId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "zone/" + zoneId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Zone> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Zone.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Zone createZone (AddZone newZone, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "zone")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newZone, headers);
		ResponseEntity<Zone> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Zone.class);
		return result.getBody();
	}

	// PATCH
	public Zone updateZone(String zoneId, String loginUserID,
						   @Valid UpdateZone updateZone, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateZone, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "zone/" + zoneId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Zone> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Zone.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteZone (String zoneId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "zone/" + zoneId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Country------------------------------------------------------------------------
	// GET ALL
	public Country[] getAllCountry (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "country");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Country[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Country[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Country getCountry (String countryId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "country/" + countryId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Country> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Country.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Country createCountry (AddCountry newCountry, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "country")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newCountry, headers);
		ResponseEntity<Country> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Country.class);
		return result.getBody();
	}

	// PATCH
	public Country updateCountry(String countryId, String loginUserID,
								 @Valid UpdateCountry updateCountry, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCountry, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "country/" + countryId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Country> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Country.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteCountry (String countryId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "country/" + countryId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Employee------------------------------------------------------------------------
	// GET ALL
	public Employee[] getAllEmployee (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "employee");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Employee[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Employee[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Employee getEmployee (String employeeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "employee/" + employeeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Employee> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Employee.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Employee createEmployee (AddEmployee newEmployee, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "employee")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newEmployee, headers);
		ResponseEntity<Employee> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Employee.class);
		return result.getBody();
	}

	// PATCH
	public Employee updateEmployee(String employeeId, String loginUserID,
								   @Valid UpdateEmployee updateEmployee, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateEmployee, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "employee/" + employeeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Employee> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Employee.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteEmployee (String employeeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "employee/" + employeeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Sbu------------------------------------------------------------------------
	// GET ALL
	public Sbu[] getAllSbu (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "sbu");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Sbu[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Sbu[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Sbu getSbu (String sbuId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "sbu/" + sbuId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Sbu> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Sbu.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Sbu createSbu (AddSbu newSbu, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "sbu")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newSbu, headers);
		ResponseEntity<Sbu> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Sbu.class);
		return result.getBody();
	}

	// PATCH
	public Sbu updateSbu(String sbuId, String loginUserID,
						 @Valid UpdateSbu updateSbu, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSbu, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "sbu/" + sbuId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Sbu> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Sbu.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteSbu (String sbuId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "sbu/" + sbuId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//-------------------------------NumberRange----------------------------------------------------------------------------------
	// GET ALL
	public NumberRange[] getNumberRanges (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<NumberRange[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRange[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public NumberRange getNumberRange (Long numberRangeCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange/" + numberRangeCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<NumberRange> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, NumberRange.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public String getNextNumberRange (Long numberRangeCode,String description, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange/nextNumberRange")
					.queryParam("numberRangeCode", numberRangeCode)
					.queryParam("description", description);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public NumberRange createNumberRange (NumberRange NumberRange, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange")
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(NumberRange, headers);
			ResponseEntity<NumberRange> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, NumberRange.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// UPDATE
	public NumberRange updateNumberRange (Long numberRangeCode, String loginUserID, NumberRange modifiedNumberRange, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedNumberRange, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();

			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange/" + numberRangeCode)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<NumberRange> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, NumberRange.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteNumberRange (Long numberRangeCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/numberRange/" + numberRangeCode)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------User------------------------------------------------------------------------
	//Validate user
	/**
	 *
	 * @param userID
	 * @param password
	 * @param authToken
	 * @return
	 */
	public User validateUser (String userID, String password, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/validate")
					.queryParam("userID", userID)
					.queryParam("password", password);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<User> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, User.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	// GET ALL
	public User[] getAllUser (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/users");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<User[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, User[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public User getUser (String id, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/user/" + id);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<User> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, User.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public User createUser (AddUser newUser, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/user")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newUser, headers);
		ResponseEntity<User> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, User.class);
		return result.getBody();
	}

	// PATCH
	public User updateUser(String id, String loginUserID,
						   @Valid UpdateUser updateUser, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateUser, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/user/" + id)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<User> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, User.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteUser (String id, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getUstorageServiceUrl() + "/login/user/" + id)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
