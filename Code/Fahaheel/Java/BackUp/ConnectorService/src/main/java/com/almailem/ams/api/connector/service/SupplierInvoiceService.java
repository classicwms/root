package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.supplierinvoice.*;
import com.almailem.ams.api.connector.model.wms.*;
import com.almailem.ams.api.connector.repository.SupplierInvoiceHeaderRepository;
import com.almailem.ams.api.connector.repository.SupplierInvoiceLineRepository;
import com.almailem.ams.api.connector.repository.specification.SupplierInvoiceHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.SupplierInvoiceLineSpecification;
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

@Service
@Slf4j
public class SupplierInvoiceService extends BaseService {

    @Autowired
    private SupplierInvoiceHeaderRepository supplierInvoiceHeaderRepository;

    @Autowired
    private SupplierInvoiceLineRepository supplierInvoiceLineRepository;

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
     * GET ALL
     */
    public List<SupplierInvoiceHeader> getAllSupplierInvoiceHeader() {
        List<SupplierInvoiceHeader> supplierInvoiceHeader = supplierInvoiceHeaderRepository.findAll();
        return supplierInvoiceHeader;
    }

    /**
     *
     * @param supplierInvoiceNo
     * @return
     */
    public SupplierInvoiceHeader updateProcessedInboundOrder(Long supplierInvoiceHeaderId, String companyCode, String branchCode, String supplierInvoiceNo,Long processedStatusId) {
        SupplierInvoiceHeader dbInboundOrder =
                supplierInvoiceHeaderRepository.getSupplierInvoiceHeader(supplierInvoiceHeaderId);
//                        findTopBySupplierInvoiceHeaderIdAndCompanyCodeAndBranchCodeAndSupplierInvoiceNoOrderByOrderReceivedOnDesc(
//                        supplierInvoiceHeaderId, companyCode, branchCode, supplierInvoiceNo);
        log.info("orderId : " + supplierInvoiceNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
//            dbInboundOrder.setProcessedStatusId(10L);
//            dbInboundOrder.setOrderProcessedOn(new Date());
//            SupplierInvoiceHeader inboundOrder = supplierInvoiceHeaderRepository.save(dbInboundOrder);
//            return inboundOrder;
            supplierInvoiceHeaderRepository.updateProcessStatusId(dbInboundOrder.getSupplierInvoiceHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

    /**
     * @param ASN
     * @return
     */
    public WarehouseApiResponse postASNV2(ASN ASN) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(ASN.getAsnHeader().getCompanyCode(), ASN.getAsnHeader().getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/inbound/asn/v2");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/inbound/asn/v2");
        }
        HttpEntity<?> entity = new HttpEntity<>(ASN, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    //Find
    public List<SupplierInvoiceHeader> findSupplierInvoiceHeader(SearchSupplierInvoiceHeader searchSupplierInvoiceHeader) throws ParseException {


        if (searchSupplierInvoiceHeader.getFromOrderProcessedOn() != null && searchSupplierInvoiceHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchSupplierInvoiceHeader.getFromOrderProcessedOn(), searchSupplierInvoiceHeader.getToOrderProcessedOn());
            searchSupplierInvoiceHeader.setFromOrderProcessedOn(dates[0]);
            searchSupplierInvoiceHeader.setToOrderProcessedOn(dates[1]);
        }
        if (searchSupplierInvoiceHeader.getFromOrderReceivedOn() != null && searchSupplierInvoiceHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchSupplierInvoiceHeader.getFromOrderReceivedOn(), searchSupplierInvoiceHeader.getToOrderReceivedOn());
            searchSupplierInvoiceHeader.setFromOrderReceivedOn(dates[0]);
            searchSupplierInvoiceHeader.setToOrderReceivedOn(dates[1]);
        }

        SupplierInvoiceHeaderSpecification spec = new SupplierInvoiceHeaderSpecification(searchSupplierInvoiceHeader);
        List<SupplierInvoiceHeader> results = supplierInvoiceHeaderRepository.findAll(spec);
        return results;

    }
    public List<SupplierInvoiceLine> findSupplierInvoiceLine(SearchSupplierInvoiceLine searchSupplierInvoiceLine) throws ParseException {

        if (searchSupplierInvoiceLine.getFromInvoiceDate() != null && searchSupplierInvoiceLine.getToInvoiceDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchSupplierInvoiceLine.getFromInvoiceDate(), searchSupplierInvoiceLine.getToInvoiceDate());
            searchSupplierInvoiceLine.setFromInvoiceDate(dates[0]);
            searchSupplierInvoiceLine.setToInvoiceDate(dates[1]);
        }


        SupplierInvoiceLineSpecification spec = new SupplierInvoiceLineSpecification(searchSupplierInvoiceLine);
        List<SupplierInvoiceLine> results = supplierInvoiceLineRepository.findAll(spec);
        return results;

    }


    /**
     * @param ASN asn
     * @return
     */
    public ASN[] postASNV2(List<ASN> ASN) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhInboundOrderServiceApiUrl() + "inboundorder/inbound/asn/v8");
        HttpEntity<?> entity = new HttpEntity<>(ASN, headers);
        ResponseEntity<ASN[]> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ASN[].class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }


    /**
     *
     * @param supplierInvoiceHeaders
     * @return
     */
    public List<SupplierInvoiceHeader> updateSupplierInvoiceHeader(List<SupplierInvoiceHeader> supplierInvoiceHeaders) {
        List<SupplierInvoiceHeader> supplierInvoiceHeadersList = new ArrayList<>();
        for(SupplierInvoiceHeader invoice : supplierInvoiceHeaders) {
            Optional<SupplierInvoiceHeader> header = supplierInvoiceHeaderRepository.findBySupplierInvoiceNo(invoice.getSupplierInvoiceNo());
            if (header.isPresent()) {
                SupplierInvoiceHeader supplierInvoice = header.get();
                BeanUtils.copyProperties(invoice, supplierInvoice, CommonUtils.getNullPropertyNames(invoice));
                supplierInvoiceHeaderRepository.save(supplierInvoice);
                supplierInvoiceHeadersList.add(supplierInvoice);
            } else {
                throw new BadRequestException("SupplierInvoiceNo Doesn't Exist " + invoice.getSupplierInvoiceNo());
            }
        }
        return supplierInvoiceHeadersList;
    }

}
