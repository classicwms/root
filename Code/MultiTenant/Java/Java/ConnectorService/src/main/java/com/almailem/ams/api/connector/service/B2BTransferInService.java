package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import com.almailem.ams.api.connector.model.wms.*;
import com.almailem.ams.api.connector.repository.TransferInHeaderRepository;
import com.almailem.ams.api.connector.repository.TransferInLineRepository;
import com.almailem.ams.api.connector.repository.specification.TransferInHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.TransferInLineSpecification;
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

@Slf4j
@Service
public class B2BTransferInService {

    @Autowired
    TransferInHeaderRepository transferInHeaderRepository;

    @Autowired
    TransferInLineRepository transferInLineRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;

    // FAHAHELL
    private String getFhInboundOrderServiceApiUrl() {
        return propertiesConfig.getFhInboundOrderServiceUrl();
    }

    private String getAlTransactionServiceApiUrl() {
        return propertiesConfig.getAlTransactionUrl();
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
     * Get All B2BTransferIn Details
     *
     * @return
     */
    public List<TransferInHeader> getAllB2BTransferInDetails() {
        List<TransferInHeader> headerRepoAll = transferInHeaderRepository.findAll();
        return headerRepoAll;
    }

//    public void updateProcessedInboundOrder(String asnNumber) {
//        TransferInHeader dbInboundOrder = transferInHeaderRepository.findTopByTransferOrderNoOrderByOrderReceivedOnDesc(asnNumber);
//        log.info("orderId : " + asnNumber);
//        log.info("dbInboundOrder : " + dbInboundOrder);
//        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            transferInHeaderRepository.updateProcessStatusId(asnNumber,new Date());
////            TransferInHeader inboundOrder = transferInHeaderRepository.save(dbInboundOrder);
////            return inboundOrder;
//        }
////        return dbInboundOrder;
//    }

    public TransferInHeader updateProcessedInboundOrder(Long transferInHeaderId, String sourceCompanyCode,
                                                        String sourceBranchCode, String transferOrderNo, Long processedStatusId) {
//        TransferInHeader dbInboundOrder =
//                transferInHeaderRepository.findTopByTransferInHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNoAndProcessedStatusIdOrderByOrderReceivedOn(
//                        transferInHeaderId, sourceCompanyCode, sourceBranchCode, transferOrderNo, 0L);
        TransferInHeader dbInboundOrder = transferInHeaderRepository.getTransferInHeader(transferInHeaderId);
        log.info("orderId : " + transferOrderNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            transferInHeaderRepository.updateProcessStatusId(dbInboundOrder.getTransferInHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }


//    public void updatefailureProcessedInboundOrder(String asnNumber) {
//        TransferInHeader dbInboundOrder = transferInHeaderRepository.findTopByTransferOrderNoOrderByOrderReceivedOnDesc(asnNumber);
//        log.info("orderId : " + asnNumber);
//        log.info("dbInboundOrder : " + dbInboundOrder);
//        if (dbInboundOrder != null) {
////            dbInboundOrder.setProcessedStatusId(100L);
////            dbInboundOrder.setOrderProcessedOn(new Date());
//            transferInHeaderRepository.updatefailureProcessStatusId(asnNumber);
////            TransferInHeader inboundOrder = transferInHeaderRepository.save(dbInboundOrder);
////            return inboundOrder;
//        }
////        return dbInboundOrder;
//    }

    /**
     * @param b2bTransferIn
     * @return
     */
    public B2bTransferIn[] postB2BTransferIn(List<B2bTransferIn> b2bTransferIn) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhInboundOrderServiceApiUrl() + "inboundorder/inbound/b2bTransferInv2");
        HttpEntity<?> entity = new HttpEntity<>(b2bTransferIn, headers);
        ResponseEntity<B2bTransferIn[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, B2bTransferIn[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    /**
     * @param b2bTransferIn
     * @return
     */
    public WarehouseApiResponse postB2BTransferIn(B2bTransferIn b2bTransferIn) {
        AuthToken authToken = authTokenService.getALTransactionServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getAlTransactionServiceApiUrl() + "warehouse/inbound/b2bTransferIn");
        HttpEntity<?> entity = new HttpEntity<>(b2bTransferIn, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    public List<TransferInHeader> findB2BTransferInHeader(SearchTransferInHeader searchTransferInHeader) throws ParseException {


        if (searchTransferInHeader.getFromTransferOrderDate() != null && searchTransferInHeader.getFromTransferOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchTransferInHeader.getFromTransferOrderDate(), searchTransferInHeader.getToTransferOrderDate());
            searchTransferInHeader.setFromTransferOrderDate(dates[0]);
            searchTransferInHeader.setToTransferOrderDate(dates[1]);
        }
        if (searchTransferInHeader.getFromOrderProcessedOn() != null && searchTransferInHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchTransferInHeader.getFromOrderProcessedOn(), searchTransferInHeader.getToOrderProcessedOn());
            searchTransferInHeader.setFromOrderProcessedOn(dates[0]);
            searchTransferInHeader.setToOrderProcessedOn(dates[1]);
        }
        if (searchTransferInHeader.getFromOrderReceivedOn() != null && searchTransferInHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchTransferInHeader.getFromOrderReceivedOn(), searchTransferInHeader.getToOrderReceivedOn());
            searchTransferInHeader.setFromOrderReceivedOn(dates[0]);
            searchTransferInHeader.setToOrderReceivedOn(dates[1]);
        }

        TransferInHeaderSpecification spec = new TransferInHeaderSpecification(searchTransferInHeader);
        List<TransferInHeader> results = transferInHeaderRepository.findAll(spec);
        return results;
    }

    public List<TransferInLine> findB2BTransferInLine(SearchTransferInLine searchTransferInLine) throws ParseException {

        TransferInLineSpecification spec = new TransferInLineSpecification(searchTransferInLine);
        List<TransferInLine> results = transferInLineRepository.findAll(spec);
        return results;
        // findB2BTransferInLine
    }

}
