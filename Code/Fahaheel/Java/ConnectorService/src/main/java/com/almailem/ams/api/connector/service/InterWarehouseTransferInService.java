package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInHeader;
import com.almailem.ams.api.connector.model.transferin.SearchTransferInLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import com.almailem.ams.api.connector.model.wms.InterWarehouseTransferIn;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
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

//import java.sql.Date;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class InterWarehouseTransferInService extends BaseService {

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
     * Get All InterWhTransferInV2 Details
     *
     * @return
     */
    public List<TransferInHeader> getAllInterWhTransferInV2Details() {
        List<TransferInHeader> transferIns = transferInHeaderRepository.findAll();
        return transferIns;
    }

//    public void updateProcessedInboundOrder(String asnNumber) {
//        TransferInHeader dbInboundOrder = transferInHeaderRepository.findTopByTransferOrderNoOrderByOrderReceivedOnDesc(asnNumber);
//        log.info("orderId : " + asnNumber);
//        log.info("dbInboundOrder : " + dbInboundOrder);
//        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
////            TransferInHeader inboundOrder = transferInHeaderRepository.save(dbInboundOrder);
//            transferInHeaderRepository.updateProcessStatusId(asnNumber, new Date());
////            return null;
//        }
////        return dbInboundOrder;
//    }

    public TransferInHeader updateProcessedInboundOrder(Long transferInHeaderId, String companyCode, String branchCode, String transferOrderNo, Long processedStatusId){
//        TransferInHeader dbInboundOrder =
//                transferInHeaderRepository.findTopByTransferInHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNoAndProcessedStatusIdOrderByOrderReceivedOn(
//                        transferInHeaderId, companyCode, branchCode, transferOrderNo, 0L);
        TransferInHeader dbInboundOrder = transferInHeaderRepository.getTransferInHeader(transferInHeaderId);
        log.info("orderId : " + transferOrderNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            SupplierInvoiceHeader inboundOrder = supplierInvoiceHeaderRepository.save(dbInboundOrder);
//            return inboundOrder;
            transferInHeaderRepository.updateProcessStatusId(dbInboundOrder.getTransferInHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }


    /**
     *
     * @param interWarehouseTransferIn
     * @return
     */
    public WarehouseApiResponse postIWTTransferIn(InterWarehouseTransferIn interWarehouseTransferIn) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getToCompanyCode(), interWarehouseTransferIn.getInterWarehouseTransferInHeader().getToBranchCode());
        UriComponentsBuilder builder;
        if (warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/inbound/interWarehouseTransferIn/v2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/inbound/interWarehouseTransferIn/v2");
        }
        HttpEntity<?> entity = new HttpEntity<>(interWarehouseTransferIn, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    public List<TransferInHeader> findInterWareHouseTransferInHeader(SearchTransferInHeader searchTransferInHeader) throws ParseException {

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


    public List<TransferInLine> findInterWareHouseTransferInLine(SearchTransferInLine searchTransferInLine) throws ParseException {


        TransferInLineSpecification spec = new TransferInLineSpecification(searchTransferInLine);
        List<TransferInLine> results = transferInLineRepository.findAll(spec);
        return results;

    }


    /**
     *
     * @param interWarehouseTransferIn interWarehouse
     * @return
     */
    public InterWarehouseTransferIn[] postIWTTransferIn(List<InterWarehouseTransferIn> interWarehouseTransferIn) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhInboundOrderServiceApiUrl() + "inboundorder/inbound/interWarehouseTransferInv2");
        HttpEntity<?> entity = new HttpEntity<>(interWarehouseTransferIn, headers);
        ResponseEntity<InterWarehouseTransferIn[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InterWarehouseTransferIn[].class);
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
        for(TransferInHeader transfer : transferInHeaders) {
            Optional<TransferInHeader> header = transferInHeaderRepository.findByTransferOrderNo(transfer.getTransferOrderNo());
            if (header.isPresent()) {
                TransferInHeader transferIn = header.get();
                BeanUtils.copyProperties(transfer, transferIn, CommonUtils.getNullPropertyNames(transfer));
                transferInHeaderRepository.save(transferIn);
                transferInHeadersList.add(transferIn);
            } else {
                throw new BadRequestException("TransferInHeader OrderNumber Doesn't Exist " + transfer.getTransferOrderNo());
            }
        }
        return transferInHeadersList;
    }

}
