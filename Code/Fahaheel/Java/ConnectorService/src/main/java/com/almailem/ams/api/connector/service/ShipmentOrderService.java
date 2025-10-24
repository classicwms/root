package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.FindTransferOutLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import com.almailem.ams.api.connector.model.wms.ShipmentOrder;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.TransferOutHeaderRepository;
import com.almailem.ams.api.connector.repository.TransferOutLineRepository;
import com.almailem.ams.api.connector.repository.specification.TransferOutHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.TransferOutLineSpecification;
import com.almailem.ams.api.connector.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
public class ShipmentOrderService extends BaseService {

    @Autowired
    TransferOutHeaderRepository transferOutHeaderRepository;

    @Autowired
    TransferOutLineRepository transferOutLineRepository;

    @Autowired
    AuthTokenService authTokenService;

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
     * Get All ShipmentOrderV2 Details
     *
     * @return
     */
    public List<TransferOutHeader> getAllSoV2Details() {
        List<TransferOutHeader> transferOuts = transferOutHeaderRepository.findAll();
        return transferOuts;
    }

    public TransferOutHeader updateProcessedOutboundOrder(Long transferOutHeaderId, String companyCode,
                                                          String branchCode, String transferOrderNumber, Long processedStatusId) {
//        TransferOutHeader dbInboundOrder =
//                transferOutHeaderRepository.findTopByTransferOutHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNumberOrderByOrderReceivedOnDesc(
//                        transferOutHeaderId, companyCode, branchCode, transferOrderNumber);
        TransferOutHeader dbInboundOrder = transferOutHeaderRepository.getTransferOutHeader(transferOutHeaderId);
        log.info("orderId : " + transferOrderNumber);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            transferOutHeaderRepository.updateProcessStatusId(dbInboundOrder.getTransferOutHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

//    public void updateFailedProcessedOutboundOrder(String transferOrderNumber) {
//        TransferOutHeader dbObOrder = transferOutHeaderRepository.findTopByTransferOrderNumberOrderByOrderReceivedOnDesc(transferOrderNumber);
//        log.info("orderId: " + transferOrderNumber);
//        log.info("SO Order: " + dbObOrder);
//        if (dbObOrder != null) {
////            dbObOrder.setProcessedStatusId(100L);
////            dbObOrder.setOrderProcessedOn(new Date());
//            transferOutHeaderRepository.updatefailureProcessStatusId(transferOrderNumber);
//        }
//    }

    public WarehouseApiResponse postShipmentOrder(ShipmentOrder shipmentOrder) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(shipmentOrder.getSoHeader().getCompanyCode(), shipmentOrder.getSoHeader().getBranchCode());
        UriComponentsBuilder builder;
        if (warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/outbound/so/v2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/outbound/so/v2");
        }
        HttpEntity<?> entity = new HttpEntity<>(shipmentOrder, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    // Find ShipmentOrder
    public List<TransferOutHeader> findShipmentOrder(FindTransferOutHeader findTransferOutHeader) throws ParseException {

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

    public List<TransferOutLine> findShipmentOrderLine(FindTransferOutLine findTransferOutLine) throws ParseException {



        TransferOutLineSpecification spec = new TransferOutLineSpecification(findTransferOutLine);
        List<TransferOutLine> results = transferOutLineRepository.findAll(spec);
        return results;
    }


    /**
     *
     * @param shipmentOrder shipment_order
     * @return
     */
    public ShipmentOrder[] postShipmentOrder(List<ShipmentOrder> shipmentOrder) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhOutboundServiceApiUrl() + "outboundorder/outbound/shipmentOrder/v2");
        HttpEntity<?> entity = new HttpEntity<>(shipmentOrder, headers);
        ResponseEntity<ShipmentOrder[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ShipmentOrder[].class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }
}
