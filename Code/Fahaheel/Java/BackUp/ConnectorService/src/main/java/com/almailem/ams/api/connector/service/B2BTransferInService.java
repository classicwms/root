package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
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
import com.almailem.ams.api.connector.util.CommonUtils;
import com.almailem.ams.api.connector.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class B2BTransferInService extends BaseService {

    @Autowired
    TransferInHeaderRepository transferInHeaderRepository;

    @Autowired
    TransferInLineRepository transferInLineRepository;

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
    public WarehouseApiResponse postB2BTransferIn(B2bTransferIn b2bTransferIn) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(b2bTransferIn.getB2bTransferInHeader().getCompanyCode(), b2bTransferIn.getB2bTransferInHeader().getBranchCode());
        UriComponentsBuilder builder = null;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/inbound/b2bTransferIn");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/inbound/b2bTransferIn");
        }
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
     *
     * @param transferInHeaders
     * @return
     */
    public List<TransferInHeader> updateTransferInHeader(List<TransferInHeader> transferInHeaders) {
        List<TransferInHeader> transferInHeadersList = new ArrayList<>();
        for(TransferInHeader transferIn : transferInHeaders) {
            Optional<TransferInHeader> header = transferInHeaderRepository.findByTransferOrderNo(transferIn.getTransferOrderNo());
            if (header.isPresent()) {
                TransferInHeader transfers = header.get();
                BeanUtils.copyProperties(transferIn, transfers, CommonUtils.getNullPropertyNames(transferIn));
                transferInHeaderRepository.save(transfers);
                transferInHeadersList.add(transfers);
            } else {
                throw new BadRequestException("TransferOrderNo Doesn't Exist " + transferIn.getTransferOrderNo());
            }
        }
        return transferInHeadersList;
    }
}
