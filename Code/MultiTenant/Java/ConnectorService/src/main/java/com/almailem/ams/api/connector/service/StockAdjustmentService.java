package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.stockadjustment.FindStockAdjustment;
import com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.StockAdjustmentRepository;
import com.almailem.ams.api.connector.repository.specification.StockAdjustmentSpecification;
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

@Service
@Slf4j
public class StockAdjustmentService {

    @Autowired
    StockAdjustmentRepository stockAdjustmentRepo;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;


    // FAHAHELL
    private String getTransactionServiceApiUrl() {
        return propertiesConfig.getFhInboundOrderServiceUrl();
    }

    // AUTO_LAP
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

    public List<StockAdjustment> getAllStockAdjustment() {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepo.findAll();
        return stockAdjustments;
    }

    /**
     *
     * @param stockAdjustmentId
     * @param companyCode
     * @param branchCode
     * @param itemCode
     * @param processStatusId
     * @return
     */
    public StockAdjustment updateProcessedStockAdjustment(Long stockAdjustmentId, String companyCode, String branchCode,
                                                          String itemCode, Long processStatusId) {
        StockAdjustment dbSA =
                stockAdjustmentRepo.getStockAdjustment(stockAdjustmentId);
//                        findTopByStockAdjustmentIdAndCompanyCodeAndBranchCodeAndItemCodeOrderByDateOfAdjustmentDesc(
//                        stockAdjustmentId, companyCode, branchCode, itemCode);
        log.info("Item Code: " + itemCode);
        log.info("dbStockAdjustment: " + dbSA);
        if (dbSA != null) {
//            dbSA.setProcessedStatusId(10L);
//            dbSA.setOrderProcessedOn(new Date());
            stockAdjustmentRepo.updateProcessStatusId(stockAdjustmentId, processStatusId);
        }
        return dbSA;
    }

    public WarehouseApiResponse postStockAdjustment(com.almailem.ams.api.connector.model.wms.StockAdjustment stockAdjustment) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/stockAdjustment");
        HttpEntity<?> entity = new HttpEntity<>(stockAdjustment, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    public WarehouseApiResponse postStockAdjustmentAl(com.almailem.ams.api.connector.model.wms.StockAdjustment stockAdjustment) {
        AuthToken authToken = authTokenService.getALTransactionServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getAlTransactionServiceApiUrl() + "warehouse/stockAdjustment");
        HttpEntity<?> entity = new HttpEntity<>(stockAdjustment, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    // Find StockAdjustment
    public List<StockAdjustment> findStockAdjustment(FindStockAdjustment findStockAdjustment) throws ParseException {


        if (findStockAdjustment.getFromOrderProcessedOn() != null && findStockAdjustment.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findStockAdjustment.getFromOrderProcessedOn(), findStockAdjustment.getToOrderProcessedOn());
            findStockAdjustment.setFromOrderProcessedOn(dates[0]);
            findStockAdjustment.setToOrderProcessedOn(dates[1]);
        }
        if (findStockAdjustment.getFromOrderReceivedOn() != null && findStockAdjustment.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findStockAdjustment.getFromOrderReceivedOn(), findStockAdjustment.getToOrderReceivedOn());
            findStockAdjustment.setFromOrderReceivedOn(dates[0]);
            findStockAdjustment.setToOrderReceivedOn(dates[1]);
        }


        StockAdjustmentSpecification spec = new StockAdjustmentSpecification(findStockAdjustment);
        List<StockAdjustment> results = stockAdjustmentRepo.findAll(spec);
        return results;
    }

}