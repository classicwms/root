package com.almailem.ams.api.connector.service;


import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.FindSalesReturnLine;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnLine;
import com.almailem.ams.api.connector.model.wms.SaleOrderReturn;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.SalesReturnHeaderRepository;
import com.almailem.ams.api.connector.repository.SalesReturnLineRepository;
import com.almailem.ams.api.connector.repository.specification.SalesReturnHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.SalesReturnLineSpecification;
import com.almailem.ams.api.connector.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class SalesReturnService extends BaseService {

    @Autowired
    private SalesReturnHeaderRepository salesReturnHeaderRepository;

    @Autowired
    private SalesReturnLineRepository salesReturnLineRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;

    private String getTransactionServiceApiUrl() {
        return propertiesConfig.getTransactionServiceUrl();
    }

    private String getFhInboundOrderServiceApiUrl() {
        return propertiesConfig.getFhInboundOrderServiceUrl();
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

    //GET ALL

    public List<SalesReturnHeader>  getAllSalesReturnHeader(){
        List<SalesReturnHeader> salesReturnHeader = salesReturnHeaderRepository.findAll();
        return salesReturnHeader;
    }

    public SalesReturnHeader updateProcessedInboundOrder(Long salesReturnHeaderId, String companyCode,
                                                         String branchCode, String returnOrderNo, Long processedStatusId) {
        SalesReturnHeader dbInboundOrder =
                salesReturnHeaderRepository.getSalesReturnHeader(salesReturnHeaderId);
//                        findTopBySalesReturnHeaderIdAndCompanyCodeAndBranchCodeAndReturnOrderNoOrderByOrderReceivedOnDesc(
//                salesReturnHeaderId, companyCode, branchCode, returnOrderNo);
        log.info("orderId : " + returnOrderNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            SalesReturnHeader inboundOrder = salesReturnHeaderRepository.save(dbInboundOrder);
//            return inboundOrder;
            salesReturnHeaderRepository.updateProcessStatusId(dbInboundOrder.getSalesReturnHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

    /**
     *
     * @param salesReturn
     * @return
     */
    public WarehouseApiResponse postSaleOrderReturn(SaleOrderReturn salesReturn) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(salesReturn.getSoReturnHeader().getCompanyCode(), salesReturn.getSoReturnHeader().getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/inbound/soreturn/v2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/inbound/soreturn/v2");
        }
        HttpEntity<?> entity = new HttpEntity<>(salesReturn, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // Find SalesReturnHeader
    public List<SalesReturnHeader> findSalesReturnHeader(FindSalesReturnHeader findSalesReturnHeader) throws ParseException {

        if (findSalesReturnHeader.getFromOrderProcessedOn() != null && findSalesReturnHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findSalesReturnHeader.getFromOrderProcessedOn(), findSalesReturnHeader.getToOrderProcessedOn());
            findSalesReturnHeader.setFromOrderProcessedOn(dates[0]);
            findSalesReturnHeader.setToOrderProcessedOn(dates[1]);
        }
        if (findSalesReturnHeader.getFromOrderReceivedOn() != null && findSalesReturnHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findSalesReturnHeader.getFromOrderReceivedOn(), findSalesReturnHeader.getToOrderReceivedOn());
            findSalesReturnHeader.setFromOrderReceivedOn(dates[0]);
            findSalesReturnHeader.setToOrderReceivedOn(dates[1]);
        }


        SalesReturnHeaderSpecification spec = new SalesReturnHeaderSpecification(findSalesReturnHeader);
        List<SalesReturnHeader> results = salesReturnHeaderRepository.findAll(spec);
        return results;
    }

    public List<SalesReturnLine> findSalesReturnLine(FindSalesReturnLine findSalesReturnLine) throws ParseException {

        if (findSalesReturnLine.getFromReturnOrderDate() != null && findSalesReturnLine.getToReturnOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findSalesReturnLine.getFromReturnOrderDate(), findSalesReturnLine.getToReturnOrderDate());
            findSalesReturnLine.setFromReturnOrderDate(dates[0]);
            findSalesReturnLine.setToReturnOrderDate(dates[1]);
        }

        SalesReturnLineSpecification spec = new SalesReturnLineSpecification(findSalesReturnLine);
        List<SalesReturnLine> results = salesReturnLineRepository.findAll(spec);
        return results;
    }


    /**
     *
     * @param salesReturn
     * @return
     */
    public SaleOrderReturn[] postSaleOrderReturn(List<SaleOrderReturn> salesReturn) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhInboundOrderServiceApiUrl() + "inboundorder/inbound/salesReturnv2");
        HttpEntity<?> entity = new HttpEntity<>(salesReturn, headers);
        ResponseEntity<SaleOrderReturn[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SaleOrderReturn[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }
}
