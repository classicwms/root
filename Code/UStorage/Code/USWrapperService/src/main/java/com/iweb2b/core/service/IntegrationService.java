package com.iweb2b.core.service;

import com.iweb2b.core.config.PropertiesConfig;
import com.iweb2b.core.model.auth.AuthToken;
import com.iweb2b.core.model.integration.*;
import com.iweb2b.core.model.integration.asyad.Consignment;
import com.iweb2b.core.model.integration.asyad.ConsignmentResponse;

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
public class IntegrationService {

	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	AuthTokenService authTokenService;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	private String getB2bintegrationServiceUrl() {
		return propertiesConfig.getB2bintegrationServiceUrl();
	}

	//--------------------------------------------ConsignmentTracking------------------------------------------------------------------------
	// GET ALL
	public ConsignmentTracking[] getAllConsignmentTracking (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ConsignmentTracking[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ConsignmentTracking[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ConsignmentTracking getConsignmentTracking (String consignmentTrackingId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking/" + consignmentTrackingId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ConsignmentTracking> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ConsignmentTracking.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	public ConsignmentTracking getConsignmentTrackingByRefNumber(String referenceNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking/" + referenceNumber + "/shipment");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ConsignmentTracking> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ConsignmentTracking.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ConsignmentTracking createConsignmentTracking (AddConsignmentTracking newConsignmentTracking, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newConsignmentTracking, headers);
		ResponseEntity<ConsignmentTracking> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ConsignmentTracking.class);
		return result.getBody();
	}

	// PATCH
	public ConsignmentTracking updateConsignmentTracking(String consignmentTrackingId, String loginUserID,
														 @Valid UpdateConsignmentTracking updateConsignmentTracking, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateConsignmentTracking, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking/" + consignmentTrackingId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ConsignmentTracking> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ConsignmentTracking.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteConsignmentTracking (String consignmentTrackingId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "consignmentTracking/" + consignmentTrackingId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------DestinationDetail------------------------------------------------------------------------
	// GET ALL
	public DestinationDetail[] getAllDestinationDetail (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "destinationDetail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DestinationDetail[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DestinationDetail[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public DestinationDetail getDestinationDetail (String destinationDetailId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "destinationDetail/" + destinationDetailId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DestinationDetail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DestinationDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DestinationDetail createDestinationDetail (AddDestinationDetail newDestinationDetail, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "destinationDetail")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newDestinationDetail, headers);
		ResponseEntity<DestinationDetail> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DestinationDetail.class);
		return result.getBody();
	}

	// PATCH
	public DestinationDetail updateDestinationDetail(String destinationDetailId, String loginUserID,
													 @Valid UpdateDestinationDetail updateDestinationDetail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDestinationDetail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "destinationDetail/" + destinationDetailId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<DestinationDetail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DestinationDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteDestinationDetail (String destinationDetailId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "destinationDetail/" + destinationDetailId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Event------------------------------------------------------------------------
	// GET ALL
	public Event[] getAllEvent (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "event");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Event[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Event[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Event getEvent (String eventId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "event/" + eventId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Event> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Event.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Event createEvent (AddEvent newEvent, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "event")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newEvent, headers);
		ResponseEntity<Event> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Event.class);
		return result.getBody();
	}

	// PATCH
	public Event updateEvent(String eventId, String loginUserID, @Valid UpdateEvent updateEvent, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateEvent, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "event/" + eventId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Event> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Event.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteEvent (String eventId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "event/" + eventId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------Node------------------------------------------------------------------------
	// GET ALL
	public Node[] getAllNode (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "node");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Node[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Node[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public Node getNode (String nodeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "node/" + nodeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Node> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Node.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public Node createNode (AddNode newNode, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "node")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newNode, headers);
		ResponseEntity<Node> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Node.class);
		return result.getBody();
	}

	// PATCH
	public Node updateNode(String nodeId, String loginUserID,
						   @Valid UpdateNode updateNode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateNode, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "node/" + nodeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<Node> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Node.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteNode (String nodeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "node/" + nodeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------OriginDetail------------------------------------------------------------------------
	// GET ALL
	public OriginDetail[] getAllOriginDetail (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "originDetail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OriginDetail[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OriginDetail[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public OriginDetail getOriginDetail (String originDetailId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "originDetail/" + originDetailId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OriginDetail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OriginDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public OriginDetail createOriginDetail (AddOriginDetail newOriginDetail, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "originDetail")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newOriginDetail, headers);
		ResponseEntity<OriginDetail> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OriginDetail.class);
		return result.getBody();
	}

	// PATCH
	public OriginDetail updateOriginDetail(String originDetailId, String loginUserID,
										   @Valid UpdateOriginDetail updateOriginDetail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOriginDetail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "originDetail/" + originDetailId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<OriginDetail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OriginDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteOriginDetail (String originDetailId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "originDetail/" + originDetailId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------PiecesDetail------------------------------------------------------------------------
	// GET ALL
	public PiecesDetail[] getAllPiecesDetail (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "piecesDetail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PiecesDetail[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PiecesDetail[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PiecesDetail getPiecesDetail (String piecesDetailId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "piecesDetail/" + piecesDetailId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PiecesDetail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PiecesDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PiecesDetail createPiecesDetail (AddPiecesDetail newPiecesDetail, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "piecesDetail")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newPiecesDetail, headers);
		ResponseEntity<PiecesDetail> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PiecesDetail.class);
		return result.getBody();
	}

	// PATCH
	public PiecesDetail updatePiecesDetail(String piecesDetailId, String loginUserID,
										   @Valid UpdatePiecesDetail updatePiecesDetail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updatePiecesDetail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "piecesDetail/" + piecesDetailId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<PiecesDetail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PiecesDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePiecesDetail (String piecesDetailId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "piecesDetail/" + piecesDetailId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ReturnDetail------------------------------------------------------------------------
	// GET ALL
	public ReturnDetail[] getAllReturnDetail (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "returnDetail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ReturnDetail[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ReturnDetail[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ReturnDetail getReturnDetail (String returnDetailId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "returnDetail/" + returnDetailId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ReturnDetail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ReturnDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ReturnDetail createReturnDetail (AddReturnDetail newReturnDetail, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "returnDetail")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newReturnDetail, headers);
		ResponseEntity<ReturnDetail> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ReturnDetail.class);
		return result.getBody();
	}

	// PATCH
	public ReturnDetail updateReturnDetail(String returnDetailId, String loginUserID,
										   @Valid UpdateReturnDetail updateReturnDetail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateReturnDetail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "returnDetail/" + returnDetailId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<ReturnDetail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ReturnDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteReturnDetail (String returnDetailId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "returnDetail/" + returnDetailId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------SoftDataUpload------------------------------------------------------------------------
	// GET ALL
	public SoftDataUpload[] getAllSoftDataUpload (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SoftDataUpload[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SoftDataUpload[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public SoftDataUpload getSoftDataUpload (String softDataUploadId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload/" + softDataUploadId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SoftDataUpload> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SoftDataUpload.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public String getShippingLabel(String referenceNumber, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "clientLevel/" + referenceNumber + "/shippingLabel");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public SoftDataUpload createSoftDataUpload (AddSoftDataUpload newSoftDataUpload, String loginUserID) {
		AuthToken integAuthToken = authTokenService.getIntegrationServiceAuthToken();
		String authToken = integAuthToken.getAccess_token();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newSoftDataUpload, headers);
		ResponseEntity<SoftDataUpload> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SoftDataUpload.class);
		return result.getBody();
	}
	
	// POST - Asyad
	public ConsignmentResponse createConsignment (Consignment newConsignment, String loginUserID) {
		AuthToken integAuthToken = authTokenService.getIntegrationServiceAuthToken();
		String authToken = integAuthToken.getAccess_token();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload/asyad")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newConsignment, headers);
		ResponseEntity<ConsignmentResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ConsignmentResponse.class);
		return result.getBody();
	}

	// PATCH
	public SoftDataUpload updateSoftDataUpload(String softDataUploadId, String loginUserID,
											   @Valid UpdateSoftDataUpload updateSoftDataUpload, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSoftDataUpload, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload/" + softDataUploadId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<SoftDataUpload> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SoftDataUpload.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteSoftDataUpload (String softDataUploadId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "softDataUpload/" + softDataUploadId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------TaxDetail------------------------------------------------------------------------
	// GET ALL
	public TaxDetail[] getAllTaxDetail (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "taxDetail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<TaxDetail[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, TaxDetail[].class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public TaxDetail getTaxDetail (String taxDetailId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "taxDetail/" + taxDetailId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<TaxDetail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, TaxDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public TaxDetail createTaxDetail (AddTaxDetail newTaxDetail, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "taxDetail")
				.queryParam("loginUserID", loginUserID);

		HttpEntity<?> entity = new HttpEntity<>(newTaxDetail, headers);
		ResponseEntity<TaxDetail> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TaxDetail.class);
		return result.getBody();
	}

	// PATCH
	public TaxDetail updateTaxDetail(String taxDetailId, String loginUserID,
									 @Valid UpdateTaxDetail updateTaxDetail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateTaxDetail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "taxDetail/" + taxDetailId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<TaxDetail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, TaxDetail.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteTaxDetail (String taxDetailId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "UStorage's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getB2bintegrationServiceUrl() + "taxDetail/" + taxDetailId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
