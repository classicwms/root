package com.iweb2b.core.service;

import com.iweb2b.core.config.PropertiesConfig;

import com.iweb2b.core.model.play.*;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PlayService {

	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	AuthTokenService authTokenService;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	private String getB2bmasterServiceUrl() {
		return propertiesConfig.getB2bmasterServiceUrl();
	}

	//--------------------------------------------User------------------------------------------------------------------------
	//Validate user
	public User validateUser (String userID, String password, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/validate")
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
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/users");
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
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/user/" + id);
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
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/user")
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
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/user/" + id)
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
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "/login/user/" + id)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ArchiveLogs------------------------------------------------------------------------
	// GET ALL
	public ArchiveLogs[] getAllArchiveLogs (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "archiveLogs");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ArchiveLogs[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ArchiveLogs[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ArchiveLogs getArchiveLogs (String archiveLogsId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "archiveLogs/" + archiveLogsId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ArchiveLogs> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ArchiveLogs.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ArchiveLogs createArchiveLogs (AddArchiveLogs newArchiveLogs, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "archiveLogs")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newArchiveLogs, headers);
		ResponseEntity<ArchiveLogs> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ArchiveLogs.class);
		return result.getBody();
	}

	// PATCH
	public ArchiveLogs updateArchiveLogs(String archiveLogsId, String loginUserID,
										 @Valid UpdateArchiveLogs updateArchiveLogs, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateArchiveLogs, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "archiveLogs/" + archiveLogsId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ArchiveLogs> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ArchiveLogs.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteArchiveLogs (String archiveLogsId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "archiveLogs/" + archiveLogsId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------ClientAssignment------------------------------------------------------------------------
	// GET ALL
	public ClientAssignment[] getAllClientAssignment (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "clientAssignment");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ClientAssignment[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ClientAssignment[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ClientAssignment getClientAssignment (String clientAssignmentId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "clientAssignment/" + clientAssignmentId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ClientAssignment> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ClientAssignment.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ClientAssignment createClientAssignment (AddClientAssignment newClientAssignment, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "clientAssignment")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newClientAssignment, headers);
		ResponseEntity<ClientAssignment> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ClientAssignment.class);
		return result.getBody();
	}

	// PATCH
	public ClientAssignment updateClientAssignment(String clientAssignmentId, String loginUserID,
												   @Valid UpdateClientAssignment updateClientAssignment, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateClientAssignment, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "clientAssignment/" + clientAssignmentId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ClientAssignment> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ClientAssignment.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteClientAssignment (String clientAssignmentId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "clientAssignment/" + clientAssignmentId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------CreateNewSystem------------------------------------------------------------------------
	// GET ALL
	public CreateNewSystem[] getAllCreateNewSystem (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "createNewSystem");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CreateNewSystem[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CreateNewSystem[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public CreateNewSystem getCreateNewSystem (String createNewSystemId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "createNewSystem/" + createNewSystemId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CreateNewSystem> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CreateNewSystem.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public CreateNewSystem createCreateNewSystem (AddCreateNewSystem newCreateNewSystem, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "createNewSystem")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newCreateNewSystem, headers);
		ResponseEntity<CreateNewSystem> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CreateNewSystem.class);
		return result.getBody();
	}

	// PATCH
	public CreateNewSystem updateCreateNewSystem(String createNewSystem, String loginUserID,
												   @Valid UpdateCreateNewSystem updateCreateNewSystem, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCreateNewSystem, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "createNewSystem/" + createNewSystem)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<CreateNewSystem> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CreateNewSystem.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteCreateNewSystem (String createNewSystem, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "createNewSystem/" + createNewSystem)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------DataTrafficAnalytics------------------------------------------------------------------------
	// GET ALL
	public DataTrafficAnalytics[] getAllDataTrafficAnalytics (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "dataTrafficAnalytics");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DataTrafficAnalytics[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DataTrafficAnalytics[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public DataTrafficAnalytics getDataTrafficAnalytics (String dataTrafficAnalyticsId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "dataTrafficAnalytics/" + dataTrafficAnalyticsId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DataTrafficAnalytics> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DataTrafficAnalytics.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DataTrafficAnalytics createDataTrafficAnalytics (AddDataTrafficAnalytics newDataTrafficAnalytics, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "dataTrafficAnalytics")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newDataTrafficAnalytics, headers);
		ResponseEntity<DataTrafficAnalytics> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DataTrafficAnalytics.class);
		return result.getBody();
	}

	// PATCH
	public DataTrafficAnalytics updateDataTrafficAnalytics(String dataTrafficAnalyticsId, String loginUserID,
														   @Valid UpdateDataTrafficAnalytics updateDataTrafficAnalytics, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDataTrafficAnalytics, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "dataTrafficAnalytics/" + dataTrafficAnalyticsId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<DataTrafficAnalytics> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DataTrafficAnalytics.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteDataTrafficAnalytics (String dataTrafficAnalyticsId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "dataTrafficAnalytics/" + dataTrafficAnalyticsId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------HealthCheck------------------------------------------------------------------------
	// GET ALL
	public HealthCheck[] getAllHealthCheck (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "healthCheck");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HealthCheck[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HealthCheck[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public HealthCheck getHealthCheck (String healthCheckId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "healthCheck/" + healthCheckId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HealthCheck> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HealthCheck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public HealthCheck createHealthCheck (AddHealthCheck newHealthCheck, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "healthCheck")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newHealthCheck, headers);
		ResponseEntity<HealthCheck> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HealthCheck.class);
		return result.getBody();
	}

	// PATCH
	public HealthCheck updateHealthCheck(String healthCheckId, String loginUserID,
										 @Valid UpdateHealthCheck updateHealthCheck, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateHealthCheck, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "healthCheck/" + healthCheckId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<HealthCheck> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HealthCheck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteHealthCheck (String healthCheckId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "healthCheck/" + healthCheckId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------MonitoringControl------------------------------------------------------------------------
	// GET ALL
	public MonitoringControl[] getAllMonitoringControl (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "monitoringControl");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MonitoringControl[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MonitoringControl[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public MonitoringControl getMonitoringControl (String monitoringControlId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "monitoringControl/" + monitoringControlId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MonitoringControl> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MonitoringControl.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public MonitoringControl createMonitoringControl (AddMonitoringControl newMonitoringControl, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "monitoringControl")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newMonitoringControl, headers);
		ResponseEntity<MonitoringControl> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MonitoringControl.class);
		return result.getBody();
	}

	// PATCH
	public MonitoringControl updateMonitoringControl(String monitoringControlId, String loginUserID,
													 @Valid UpdateMonitoringControl updateMonitoringControl, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateMonitoringControl, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "monitoringControl/" + monitoringControlId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<MonitoringControl> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MonitoringControl.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteMonitoringControl (String monitoringControlId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "monitoringControl/" + monitoringControlId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------Notification------------------------------------------------------------------------
	// GET ALL
	public Notification[] getAllNotification (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "notification");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Notification[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Notification[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Notification getNotification (String notificationId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "notification/" + notificationId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Notification> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Notification.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Notification createNotification (AddNotification newNotification, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "notification")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newNotification, headers);
		ResponseEntity<Notification> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Notification.class);
		return result.getBody();
	}

	// PATCH
	public Notification updateNotification(String notificationId, String loginUserID,
										   @Valid UpdateNotification updateNotification, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateNotification, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "notification/" + notificationId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Notification> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Notification.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteNotification (String notificationId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "notification/" + notificationId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------PartnerMaster------------------------------------------------------------------------
	// GET ALL
	public PartnerMaster[] getAllPartnerMaster (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnerMaster");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PartnerMaster[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PartnerMaster[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PartnerMaster getPartnerMaster (String partnerMasterId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnerMaster/" + partnerMasterId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PartnerMaster> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PartnerMaster.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PartnerMaster createPartnerMaster (AddPartnerMaster newPartnerMaster, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnerMaster")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPartnerMaster, headers);
		ResponseEntity<PartnerMaster> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PartnerMaster.class);
		return result.getBody();
	}

	// PATCH
	public PartnerMaster updatePartnerMaster(String partnerMasterId, String loginUserID,
											 @Valid UpdatePartnerMaster updatePartnerMaster, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePartnerMaster, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnerMaster/" + partnerMasterId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PartnerMaster> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PartnerMaster.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePartnerMaster (String partnerMasterId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnerMaster/" + partnerMasterId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------PartnersActivityDeck------------------------------------------------------------------------
	// GET ALL
	public PartnersActivityDeck[] getAllPartnersActivityDeck (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnersActivityDeck");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PartnersActivityDeck[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PartnersActivityDeck[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PartnersActivityDeck getPartnersActivityDeck (String partnersActivityDeckId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnersActivityDeck/" + partnersActivityDeckId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PartnersActivityDeck> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PartnersActivityDeck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PartnersActivityDeck createPartnersActivityDeck (AddPartnersActivityDeck newPartnersActivityDeck, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnersActivityDeck")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPartnersActivityDeck, headers);
		ResponseEntity<PartnersActivityDeck> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PartnersActivityDeck.class);
		return result.getBody();
	}

	// PATCH
	public PartnersActivityDeck updatePartnersActivityDeck(String partnersActivityDeckId, String loginUserID,
														   @Valid UpdatePartnersActivityDeck updatePartnersActivityDeck, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePartnersActivityDeck, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnersActivityDeck/" + partnersActivityDeckId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PartnersActivityDeck> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PartnersActivityDeck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePartnersActivityDeck (String partnersActivityDeckId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "partnersActivityDeck/" + partnersActivityDeckId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------SetupConfiguration------------------------------------------------------------------------
	// GET ALL
	public SetupConfiguration[] getAllSetupConfiguration (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "setupConfiguration");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SetupConfiguration[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SetupConfiguration[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public SetupConfiguration getSetupConfiguration (String setupConfigurationId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "setupConfiguration/" + setupConfigurationId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SetupConfiguration> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SetupConfiguration.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public SetupConfiguration createSetupConfiguration (AddSetupConfiguration newSetupConfiguration, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "setupConfiguration")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newSetupConfiguration, headers);
		ResponseEntity<SetupConfiguration> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SetupConfiguration.class);
		return result.getBody();
	}

	// PATCH
	public SetupConfiguration updateSetupConfiguration(String setupConfigurationId, String loginUserID,
													   @Valid UpdateSetupConfiguration updateSetupConfiguration, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSetupConfiguration, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "setupConfiguration/" + setupConfigurationId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<SetupConfiguration> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SetupConfiguration.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteSetupConfiguration (String setupConfigurationId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "setupConfiguration/" + setupConfigurationId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------SubscriptionManagement------------------------------------------------------------------------
	// GET ALL
	public SubscriptionManagement[] getAllSubscriptionManagement (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "subscriptionManagement");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubscriptionManagement[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubscriptionManagement[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public SubscriptionManagement getSubscriptionManagement (String subscriptionManagementId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "subscriptionManagement/" + subscriptionManagementId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubscriptionManagement> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubscriptionManagement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public SubscriptionManagement createSubscriptionManagement (AddSubscriptionManagement newSubscriptionManagement, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "subscriptionManagement")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newSubscriptionManagement, headers);
		ResponseEntity<SubscriptionManagement> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SubscriptionManagement.class);
		return result.getBody();
	}

	// PATCH
	public SubscriptionManagement updateSubscriptionManagement(String subscriptionManagementId, String loginUserID,
															   @Valid UpdateSubscriptionManagement updateSubscriptionManagement, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSubscriptionManagement, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "subscriptionManagement/" + subscriptionManagementId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<SubscriptionManagement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SubscriptionManagement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteSubscriptionManagement (String subscriptionManagementId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "subscriptionManagement/" + subscriptionManagementId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------SystemMonitoringDeck------------------------------------------------------------------------
	// GET ALL
	public SystemMonitoringDeck[] getAllSystemMonitoringDeck (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "systemMonitoringDeck");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SystemMonitoringDeck[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SystemMonitoringDeck[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public SystemMonitoringDeck getSystemMonitoringDeck (String systemMonitoringDeckId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "systemMonitoringDeck/" + systemMonitoringDeckId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SystemMonitoringDeck> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SystemMonitoringDeck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public SystemMonitoringDeck createSystemMonitoringDeck (AddSystemMonitoringDeck newSystemMonitoringDeck, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "systemMonitoringDeck")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newSystemMonitoringDeck, headers);
		ResponseEntity<SystemMonitoringDeck> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SystemMonitoringDeck.class);
		return result.getBody();
	}

	// PATCH
	public SystemMonitoringDeck updateSystemMonitoringDeck(String systemMonitoringDeckId, String loginUserID,
														   @Valid UpdateSystemMonitoringDeck updateSystemMonitoringDeck, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSystemMonitoringDeck, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "systemMonitoringDeck/" + systemMonitoringDeckId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<SystemMonitoringDeck> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SystemMonitoringDeck.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteSystemMonitoringDeck (String systemMonitoringDeckId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "systemMonitoringDeck/" + systemMonitoringDeckId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------UserCreation------------------------------------------------------------------------
	// GET ALL
	public UserCreation[] getAllUserCreation (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userCreation");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserCreation[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserCreation[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public UserCreation getUserCreation (String userCreationId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userCreation/" + userCreationId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserCreation> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserCreation.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public UserCreation createUserCreation (AddUserCreation newUserCreation, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userCreation")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newUserCreation, headers);
		ResponseEntity<UserCreation> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UserCreation.class);
		return result.getBody();
	}

	// PATCH
	public UserCreation updateUserCreation(String userCreationId, String loginUserID,
										   @Valid UpdateUserCreation updateUserCreation, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateUserCreation, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userCreation/" + userCreationId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<UserCreation> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UserCreation.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteUserCreation (String userCreationId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userCreation/" + userCreationId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------UserManagement------------------------------------------------------------------------
	// GET ALL
	public UserManagement[] getAllUserManagement (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userManagement");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public UserManagement getUserManagement (String userManagementId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userManagement/" + userManagementId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public UserManagement createUserManagement (AddUserManagement newUserManagement, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userManagement")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newUserManagement, headers);
		ResponseEntity<UserManagement> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UserManagement.class);
		return result.getBody();
	}

	// PATCH
	public UserManagement updateUserManagement(String userManagementId, String loginUserID,
											   @Valid UpdateUserManagement updateUserManagement, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateUserManagement, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userManagement/" + userManagementId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<UserManagement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UserManagement.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteUserManagement (String userManagementId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userManagement/" + userManagementId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------UserProfiles------------------------------------------------------------------------
	// GET ALL
	public UserProfiles[] getAllUserProfiles (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userProfiles");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserProfiles[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserProfiles[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public UserProfiles getUserProfiles (String userProfilesId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userProfiles/" + userProfilesId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserProfiles> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserProfiles.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public UserProfiles createUserProfiles (AddUserProfiles newUserProfiles, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userProfiles")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newUserProfiles, headers);
		ResponseEntity<UserProfiles> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UserProfiles.class);
		return result.getBody();
	}

	// PATCH
	public UserProfiles updateUserProfiles(String userProfilesId, String loginUserID,
										   @Valid UpdateUserProfiles updateUserProfiles, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateUserProfiles, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userProfiles/" + userProfilesId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<UserProfiles> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UserProfiles.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteUserProfiles (String userProfilesId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bmasterServiceUrl() + "userProfiles/" + userProfilesId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


}
