package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.model.auth.AuthToken;
import com.tekclover.wms.api.idmaster.model.outboundheader.PreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.outboundheader.SearchPreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.pdfreport.ShipmentDeliverySummary;
import com.tekclover.wms.api.idmaster.model.pdfreport.ShipmentDeliverySummaryReport;
import com.tekclover.wms.api.idmaster.model.pickerdenial.PickerDenialReport;
import com.tekclover.wms.api.idmaster.model.pickerdenial.SearchPickupLine;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;

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
    private String getTransactionServiceApiUrl() {
        return propertiesConfig.getTransactionServiceUrl();
    }

    /*------------------------------------------- find PreOutboundHeader for PDF -------------------------------*/
    // Pdf PreOutBoundHeader
    // POST - findPreOutboundHeader - Stream
    public PreOutboundHeader[] findPreOutboundHeaderPdf(SearchPreOutboundHeader searchPreOutboundHeader) throws Exception {
        try {
            AuthToken interAuthToken = authTokenService.getTransactionServiceAuthToken();
            String authToken = interAuthToken.getAccess_token();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getTransactionServiceApiUrl() + "preoutboundheader/findPreOutboundHeaderNew");
            HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundHeader, headers);
            ResponseEntity<PreOutboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PreOutboundHeader[].class);
            log.info("result : " + result.getBody());

            List<PreOutboundHeader> obList = new ArrayList<>();
            for (PreOutboundHeader obHeader : result.getBody()) {
                log.info("Result RefDocDate :" + obHeader.getRefDocDate());
                if(obHeader.getRefDocDate() != null) {
                    obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 2, 30));
                }
                if(obHeader.getRequiredDeliveryDate() != null) {
                    obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 2, 30));
                }
                if(obHeader.getCreatedOn() != null) {
                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 2, 30));
                }
                if(obHeader.getUpdatedOn() != null) {
                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 2, 30));
                }
                obList.add(obHeader);
            }
            return obList.toArray(new PreOutboundHeader[obList.size()]);
        } catch (Exception e) {
            throw e;
        }
    }

    // POST - PickerDenialReport
    public PickerDenialReport pickerDenialReport(SearchPickupLine searchPickupLine) throws Exception {
        try {
            AuthToken interAuthToken = authTokenService.getTransactionServiceAuthToken();
            String authToken = interAuthToken.getAccess_token();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getTransactionServiceApiUrl() + "pickupline/pickerDenialReport");
            HttpEntity<?> entity = new HttpEntity<>(searchPickupLine, headers);
            ResponseEntity<PickerDenialReport> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickerDenialReport.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GET - ShipmentDeliverySummary
    public ShipmentDeliverySummaryReport getShipmentDeliverySummaryReportPdf(String fromDeliveryDate,
                                                                             String toDeliveryDate, List<String> customerCode, String warehouseId) throws Exception {
        try {
            AuthToken interAuthToken = authTokenService.getTransactionServiceAuthToken();
            String authToken = interAuthToken.getAccess_token();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getTransactionServiceApiUrl() + "reports/shipmentDeliverySummary")
                    .queryParam("fromDeliveryDate", fromDeliveryDate).queryParam("toDeliveryDate", toDeliveryDate)
                    .queryParam("customerCode", customerCode).queryParam("warehouseId", warehouseId);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ShipmentDeliverySummaryReport> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.GET, entity, ShipmentDeliverySummaryReport.class);
            ShipmentDeliverySummaryReport summaryReport = result.getBody();

            for (ShipmentDeliverySummary shipmentDeliverySummary : summaryReport.getShipmentDeliverySummary()) {
                if (shipmentDeliverySummary.getExpectedDeliveryDate() != null) {
                    shipmentDeliverySummary.setExpectedDeliveryDate(
                            DateUtils.addTimeToDate(shipmentDeliverySummary.getExpectedDeliveryDate(), 3));
                }

                if (shipmentDeliverySummary.getDeliveryDateTime() != null) {
                    shipmentDeliverySummary.setDeliveryDateTime(
                            DateUtils.addTimeToDate(shipmentDeliverySummary.getDeliveryDateTime(), 3));
                }
            }
            return summaryReport;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
