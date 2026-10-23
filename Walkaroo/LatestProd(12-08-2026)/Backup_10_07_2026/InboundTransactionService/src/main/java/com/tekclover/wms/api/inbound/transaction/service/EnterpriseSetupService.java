package com.tekclover.wms.api.inbound.transaction.service;

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

import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.model.enterprise.Warehouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnterpriseSetupService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

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
    private String getEnterpriseServiceApiUrl () {
        return propertiesConfig.getEnterpriseServiceUrl();
    }

    /**
     *
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param authToken
     * @return
     */
    public Warehouse getWarehouse (String warehouseId, String companyCodeId, String plantId, String languageId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseServiceApiUrl() + "warehouse/" + warehouseId)
                            .queryParam("companyId",companyCodeId)
                            .queryParam("plantId",plantId)
                            .queryParam("languageId", languageId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Warehouse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * @param companyId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param updateWarehouse
     * @param loginUserID
     * @param authToken
     * @return
     */
    public Warehouse patchWarehouse(String companyId, String plantId, String languageId, String warehouseId,
                                    Warehouse updateWarehouse, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateWarehouse, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getEnterpriseServiceApiUrl() + "warehouse/v3/" + warehouseId)
                            .queryParam("companyId", companyId)
                            .queryParam("plantId", plantId)
                            .queryParam("loginUserID", loginUserID)
                            .queryParam("languageId", languageId);
            ResponseEntity<Warehouse> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Warehouse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}