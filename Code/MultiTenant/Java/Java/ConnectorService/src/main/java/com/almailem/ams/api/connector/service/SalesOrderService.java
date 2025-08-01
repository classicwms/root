package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.picklist.FindPickListHeader;
import com.almailem.ams.api.connector.model.picklist.FindPickListLine;
import com.almailem.ams.api.connector.model.picklist.PickListHeader;
import com.almailem.ams.api.connector.model.picklist.PickListLine;
import com.almailem.ams.api.connector.model.wms.SalesOrder;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.PickListHeaderRepository;
import com.almailem.ams.api.connector.repository.PickListLineRepository;
import com.almailem.ams.api.connector.repository.specification.PickListHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.PickListLineSpecification;
import com.almailem.ams.api.connector.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SalesOrderService {

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    PickListLineRepository pickListLineRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;

    // FAHAHEEL
    private String getFhInboundOrderServiceApiUrl() {
        return propertiesConfig.getFhInboundOrderServiceUrl();
    }

    // AUTO_LAP
    private String getAlTransactionServiceApiUrl() {
        return propertiesConfig.getAlTransactionUrl();
    }

    private String getOutboundServiceApiUrl() {
        return propertiesConfig.getFhOutboundOrderUrl();
    }


    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Object Convertor
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter
                .setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    /**
     * Get All PickList Details
     *
     * @return
     */
    public List<PickListHeader> getAllSalesOrderV2Details() {
        List<PickListHeader> pickLists = pickListHeaderRepository.findAll();
        return pickLists;
    }

    /**
     *
     * @param pickListHeaderId
     * @param companyCode
     * @param branchCode
     * @param pickListNo
     * @param processedStatusId
     * @return
     */
    public PickListHeader updateProcessedInboundOrder(Long pickListHeaderId, String companyCode,
                                                      String branchCode, String pickListNo, Long processedStatusId) {
        PickListHeader dbInboundOrder =
                pickListHeaderRepository.getPickListHeader(pickListHeaderId);
//                        findTopByPickListHeaderIdAndCompanyCodeAndBranchCodeAndPickListNoOrderByOrderReceivedOnDesc(
//                pickListHeaderId, companyCode, branchCode, pickListNo);
        log.info("orderId : " + pickListNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            PickListHeader inboundOrder = pickListHeaderRepository.save(dbInboundOrder);
            pickListHeaderRepository.updateProcessStatusId(dbInboundOrder.getPickListHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

    /**
     * @param salesOrder
     * @return
     */
    public WarehouseApiResponse postSalesOrder(SalesOrder salesOrder) {
        AuthToken authToken = authTokenService.getALTransactionServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getAlTransactionServiceApiUrl() + "warehouse/outbound/salesorderv2");
        HttpEntity<?> entity = new HttpEntity<>(salesOrder, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    public SalesOrder[] postSalesOrder(List<SalesOrder> salesOrder) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getOutboundServiceApiUrl() + "outboundorder/outbound/salesorderv2");
        HttpEntity<?> entity = new HttpEntity<>(salesOrder, headers);
        ResponseEntity<SalesOrder[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SalesOrder[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // Find PickListHeader
    public List<PickListHeader> findPickListHeader(FindPickListHeader findPickListHeader) throws ParseException {

        if (findPickListHeader.getFromOrderProcessedOn() != null && findPickListHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPickListHeader.getFromOrderProcessedOn(), findPickListHeader.getToOrderProcessedOn());
            findPickListHeader.setFromOrderProcessedOn(dates[0]);
            findPickListHeader.setToOrderProcessedOn(dates[1]);
        }
        if (findPickListHeader.getFromOrderReceivedOn() != null && findPickListHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPickListHeader.getFromOrderReceivedOn(), findPickListHeader.getToOrderReceivedOn());
            findPickListHeader.setFromOrderReceivedOn(dates[0]);
            findPickListHeader.setToOrderReceivedOn(dates[1]);
        }
        PickListHeaderSpecification spec = new PickListHeaderSpecification(findPickListHeader);
        List<PickListHeader> results = pickListHeaderRepository.findAll(spec);
        return results;
    }


    // Find PickListLine
    public List<PickListLine> findPickListLine(FindPickListLine findPickListLine) throws ParseException {

        PickListLineSpecification spec = new PickListLineSpecification(findPickListLine);
        List<PickListLine> results = pickListLineRepository.findAll(spec);
        return results;
    }

}
