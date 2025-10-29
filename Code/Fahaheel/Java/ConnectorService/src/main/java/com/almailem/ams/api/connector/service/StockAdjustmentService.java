package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.stockadjustment.FindStockAdjustment;
import com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.StockAdjustmentRepository;
import com.almailem.ams.api.connector.repository.specification.StockAdjustmentSpecification;
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

@Service
@Slf4j
public class StockAdjustmentService extends BaseService {

    @Autowired
    StockAdjustmentRepository stockAdjustmentRepo;

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
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/stockAdjustment");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/stockAdjustment");
        }
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

    public WarehouseApiResponse postStockAdjustmentV2(com.almailem.ams.api.connector.model.wms.StockAdjustment stockAdjustment) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhOutboundServiceApiUrl() + "outboundorder/stockAdjustment");
        HttpEntity<?> entity = new HttpEntity<>(stockAdjustment, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    /**
     *
     * @param stockAdjustments
     * @return
     */
    public List<StockAdjustment> updateStockAdjustment(List<StockAdjustment> stockAdjustments) {
        List<StockAdjustment> stockAdjustmentsList = new ArrayList<>();
        for(StockAdjustment stock : stockAdjustments) {
            Optional<StockAdjustment> header = stockAdjustmentRepo.findByItemCode(stock.getItemCode());
            if (header.isPresent()) {
                StockAdjustment stockAdjust = header.get();
                BeanUtils.copyProperties(stock, stockAdjust, CommonUtils.getNullPropertyNames(stock));
                stockAdjustmentRepo.save(stockAdjust);
                stockAdjustmentsList.add(stockAdjust);
            } else {
                throw new BadRequestException("Stock Adjustment ItemCode Doesn't Exist " + stock.getItemCode());
            }
        }
        return stockAdjustmentsList;
    }

}