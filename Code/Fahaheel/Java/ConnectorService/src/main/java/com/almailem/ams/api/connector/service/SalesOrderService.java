package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
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
import com.almailem.ams.api.connector.util.CommonUtils;
import com.almailem.ams.api.connector.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.*;

@Slf4j
@Service
public class SalesOrderService extends BaseService {

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    PickListLineRepository pickListLineRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;

    private String getTransactionServiceApiUrl() {
        return propertiesConfig.getTransactionServiceUrl();
    }

    private String getFhOutboundServiceApiUrl() {
        return propertiesConfig.getFhOutboundOrderServiceUrl();
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
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(salesOrder.getSalesOrderHeader().getCompanyCode(), salesOrder.getSalesOrderHeader().getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/outbound/salesorderv2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/outbound/salesorderv2");
        }
        HttpEntity<?> entity = new HttpEntity<>(salesOrder, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
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


    public SalesOrder postSalesOrderV2(SalesOrder salesOrder) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhOutboundServiceApiUrl() + "outboundorder/outbound/salesorderv2");
        HttpEntity<?> entity = new HttpEntity<>(salesOrder, headers);
        ResponseEntity<SalesOrder> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SalesOrder.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    /**
     *
     * @param pickListHeaders
     * @return
     */
    public List<PickListHeader> updatePickListHeader(List<PickListHeader> pickListHeaders) {
        List<PickListHeader> pickListHeadersList = new ArrayList<>();
        for(PickListHeader picklists : pickListHeaders) {
            Optional<PickListHeader> header = pickListHeaderRepository.findByPickListNo(picklists.getPickListNo());
            if (header.isPresent()) {
                PickListHeader pickListed = header.get();
                BeanUtils.copyProperties(picklists, pickListed, CommonUtils.getNullPropertyNames(picklists));
                pickListHeaderRepository.save(pickListed);
                pickListHeadersList.add(pickListed);
            } else {
                throw new BadRequestException("PickList No  Doesn't Exist " + picklists.getPickListNo());
            }
        }
        return pickListHeadersList;
    }
}
