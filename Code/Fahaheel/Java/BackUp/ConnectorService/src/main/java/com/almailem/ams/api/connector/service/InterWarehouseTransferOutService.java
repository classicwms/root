package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import com.almailem.ams.api.connector.model.wms.InterWarehouseTransferOut;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.TransferOutHeaderRepository;
import com.almailem.ams.api.connector.repository.TransferOutLineRepository;
import com.almailem.ams.api.connector.repository.specification.TransferOutHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.TransferOutLineSpecification;
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
public class InterWarehouseTransferOutService extends BaseService {

    @Autowired
    TransferOutHeaderRepository transferOutHeaderRepository;

    @Autowired
    TransferOutLineRepository transferOutLineRepository;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

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
     * Get All InterWarehouseTransferOutV2 Details
     *
     * @return
     */
    public List<TransferOutHeader> getAllInterWhTransferOutV2Details() {
        List<TransferOutHeader> transferOuts = transferOutHeaderRepository.findAll();
        return transferOuts;
    }

//    /**
//     * @param transferOrderNumber
//     * @return
//     */
//    public TransferOutHeader updateProcessedOutboundOrder(String transferOrderNumber) {
//        TransferOutHeader dbObOrder = transferOutHeaderRepository.findTopByTransferOrderNumberOrderByOrderReceivedOnDesc(transferOrderNumber);
//        log.info("orderId: " + transferOrderNumber);
//        log.info("IWhTransfer Out Order: " + dbObOrder);
//        if (dbObOrder != null) {
//            dbObOrder.setProcessedStatusId(10L);
//            dbObOrder.setOrderProcessedOn(new Date());
////            transferOutHeaderRepository.save(dbObOrder);
//            transferOutHeaderRepository.updateProcessStatusId(transferOrderNumber, new Date());
//        }
//        return dbObOrder;
//    }

    public TransferOutHeader updateProcessedOutboundOrder(Long transferOutHeaderId, String sourceCompanyCode,
                                                          String sourceBranchCode, String transferOrderNumber, Long processedStatusId) {
//        TransferOutHeader dbInboundOrder =
//                transferOutHeaderRepository.findTopByTransferOutHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNumberOrderByOrderReceivedOnDesc(
//                        transferOutHeaderId, sourceCompanyCode, sourceBranchCode, transferOrderNumber);
        TransferOutHeader dbInboundOrder = transferOutHeaderRepository.getTransferOutHeader(transferOutHeaderId);
        log.info("orderId : " + transferOrderNumber);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            transferOutHeaderRepository.updateProcessStatusId(dbInboundOrder.getTransferOutHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

//    public TransferOutHeader updatefailureProcessedOutboundOrder(String transferOrderNumber) {
//        TransferOutHeader dbObOrder = transferOutHeaderRepository.findTopByTransferOrderNumberOrderByOrderReceivedOnDesc(transferOrderNumber);
//        log.info("orderId: " + transferOrderNumber);
//        log.info("IWhTransfer Out Order: " + dbObOrder);
//        if (dbObOrder != null) {
////            dbObOrder.setProcessedStatusId(10L);
////            dbObOrder.setOrderProcessedOn(new Date());
////            transferOutHeaderRepository.save(dbObOrder);
//            transferOutHeaderRepository.updatefailureProcessStatusId(transferOrderNumber);
//        }
//        return dbObOrder;
//    }

    public WarehouseApiResponse postIWhTransferOutV2(InterWarehouseTransferOut iWhTransOutV2) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(iWhTransOutV2.getInterWarehouseTransferOutHeader().getFromCompanyCode(), iWhTransOutV2.getInterWarehouseTransferOutHeader().getFromBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/outbound/interwarehousetransferoutv2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/outbound/interwarehousetransferoutv2");
        }
        HttpEntity<?> entity = new HttpEntity<>(iWhTransOutV2, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    // Find InterWarehouseTransferOut
    public List<TransferOutHeader> findInterWarehouseTransferOut(FindTransferOutHeader findTransferOutHeader) throws ParseException {

        if (findTransferOutHeader.getFromOrderProcessedOn() != null && findTransferOutHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findTransferOutHeader.getFromOrderProcessedOn(), findTransferOutHeader.getToOrderProcessedOn());
            findTransferOutHeader.setFromOrderProcessedOn(dates[0]);
            findTransferOutHeader.setToOrderProcessedOn(dates[1]);
        }
        if (findTransferOutHeader.getFromOrderReceivedOn() != null && findTransferOutHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findTransferOutHeader.getFromOrderReceivedOn(), findTransferOutHeader.getToOrderReceivedOn());
            findTransferOutHeader.setFromOrderReceivedOn(dates[0]);
            findTransferOutHeader.setToOrderReceivedOn(dates[1]);
        }

        TransferOutHeaderSpecification spec = new TransferOutHeaderSpecification(findTransferOutHeader);
        List<TransferOutHeader> results = transferOutHeaderRepository.findAll(spec);
        return results;
    }

    public List<TransferOutLine> findInterWarehouseTransferOutLine(FindTransferOutLine findTransferOutLine) throws ParseException {



        TransferOutLineSpecification spec = new TransferOutLineSpecification(findTransferOutLine);
        List<TransferOutLine> results = transferOutLineRepository.findAll(spec);
        return results;
    }


    public InterWarehouseTransferOut[] postIWhTransferOutV7(List<InterWarehouseTransferOut> iWhTransOutV2) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhOutboundServiceApiUrl() + "outboundorder/outbound/interwarehousetransferout/v2");
        HttpEntity<?> entity = new HttpEntity<>(iWhTransOutV2, headers);
        ResponseEntity<InterWarehouseTransferOut[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InterWarehouseTransferOut[].class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    /**
     *
     * @param transferOutHeaders
     * @return
     */
    public List<TransferOutHeader> updateTransferOutHeader(List<TransferOutHeader> transferOutHeaders) {
        List<TransferOutHeader> transferOutHeadersList = new ArrayList<>();
        for(TransferOutHeader transfer : transferOutHeaders) {
            Optional<TransferOutHeader> header = transferOutHeaderRepository.findByTransferOrderNumber(transfer.getTransferOrderNumber());
            if (header.isPresent()) {
                TransferOutHeader transferOut = header.get();
                BeanUtils.copyProperties(transfer, transferOut, CommonUtils.getNullPropertyNames(transfer));
                transferOutHeaderRepository.save(transferOut);
                transferOutHeadersList.add(transferOut);
            } else {
                throw new BadRequestException("TransferOutHeader OrderNumber Doesn't Exist " + transfer.getTransferOrderNumber());
            }
        }
        return transferOutHeadersList;
    }
}
