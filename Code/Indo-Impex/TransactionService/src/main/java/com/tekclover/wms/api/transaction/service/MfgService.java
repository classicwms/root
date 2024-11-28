package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.inboundquality.InboundQualityHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Slf4j
@Service
public class MfgService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getMfgServiceApiUrl() {
        return propertiesConfig.getMfgServiceUrl();
    }

    private String getMfgServiceAuthToken() {
        return authTokenService.getMfgServiceAuthToken().getAccess_token();
    }

    //--------------------------------------------------------------------------------------------------------------------
    // Create
    public InboundQualityHeader postInboundQualityHeader(InboundQualityHeader inboundQualityHeader, String loginUserID) {
        try {
            String authToken = getMfgServiceAuthToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "inboundqualityheader")
                            .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(inboundQualityHeader, headers);
            ResponseEntity<InboundQualityHeader> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundQualityHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while creating Inbound quality header: " + e);
        }
    }

    // Patch
    public Boolean patchOperationConsumption(String companyCodeId, String plantId, String languageId,
                                             String warehouseId, String batchNumber, String productionOrderNo,
                                             String parentProductionOrderNo, String orderType) {
        try {
            String authToken = getMfgServiceAuthToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMfgServiceApiUrl() + "operationconsumption/update/" + productionOrderNo)
                            .queryParam("orderType", orderType)
                            .queryParam("batchNumber", batchNumber)
                            .queryParam("parentProductionOrderNo", parentProductionOrderNo)
                            .queryParam("companyCodeId", companyCodeId)
                            .queryParam("plantId", plantId)
                            .queryParam("languageId", languageId)
                            .queryParam("warehouseId", warehouseId);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            log.info("result : " + result.getStatusCode());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while updating operation consumption: " + e);
        }
    }
}