package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.periodic.FindPeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.FindPeriodicLine;
import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.PeriodicLine;
import com.almailem.ams.api.connector.model.wms.Periodic;
import com.almailem.ams.api.connector.model.wms.UpdateStockCountLine;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.PeriodicHeaderRepository;
import com.almailem.ams.api.connector.repository.PeriodicLineRepository;
import com.almailem.ams.api.connector.repository.specification.PeriodicHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.PeriodicLineSpecification;
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
public class PeriodicService extends BaseService {

    @Autowired
    PeriodicHeaderRepository periodicHeaderRepo;

    @Autowired
    PeriodicLineRepository periodicLineRepository;

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
     * Get All StockCount Periodic Details
     *
     * @return
     */
    public List<PeriodicHeader> getAllPeriodicDetails() {
        List<PeriodicHeader> periodic = periodicHeaderRepo.findAll();
        return periodic;
    }

//    public void updateProcessedPeriodicOrder(String cycleCountNo) {
//        PeriodicHeader dbOrder = periodicHeaderRepo.findTopByCycleCountNoOrderByOrderReceivedOnDesc(cycleCountNo);
//        log.info("orderId: " + cycleCountNo);
//        log.info("dbPeriodicOrder: " + dbOrder);
//        if (dbOrder != null) {
//            dbOrder.setProcessedStatusId(10L);
//            dbOrder.setOrderProcessedOn(new Date());
//            periodicHeaderRepo.updateProcessStatusId(cycleCountNo, new Date());
//        }
//    }

    /**
     * @param periodicHeaderId
     * @param companyCode
     * @param branchCode
     * @param cycleCountNo
     * @param processedStatusId
     * @return
     */
    public PeriodicHeader updateProcessedPeriodicOrder(Long periodicHeaderId, String companyCode,
                                                       String branchCode, String cycleCountNo, Long processedStatusId) {
        PeriodicHeader dbInboundOrder =
                periodicHeaderRepo.getPeriodicHeader(periodicHeaderId);
//                        findTopByPeriodicHeaderIdAndCompanyCodeAndBranchCodeAndCycleCountNoOrderByOrderReceivedOnDesc(
//                        periodicHeaderId, companyCode, branchCode, cycleCountNo);
        log.info("orderId : " + cycleCountNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            periodicHeaderRepo.updateProcessStatusId(dbInboundOrder.getPeriodicHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

    public WarehouseApiResponse postPeriodicOrder(Periodic periodic) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(periodic.getPeriodicHeaderV1().getCompanyCode(), periodic.getPeriodicHeaderV1().getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/stockcount/periodic");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/stockcount/periodic");
        }
        HttpEntity<?> entity = new HttpEntity<>(periodic, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    /**
     * @param updateStockCountLines
     * @return
     */
    public WarehouseApiResponse updatePeriodicStockCount(List<UpdateStockCountLine> updateStockCountLines) {
        if (updateStockCountLines != null) {
            log.info("Perpertual Lines to be Updated:" + updateStockCountLines);
            List<String> statusList = new ArrayList<>();
            for (UpdateStockCountLine dbPeriodicLine : updateStockCountLines) {
                PeriodicLine updatePdlCountedQty = periodicLineRepository.findByCycleCountNoAndItemCodeAndManufacturerCode(
                        dbPeriodicLine.getCycleCountNo(),
                        dbPeriodicLine.getItemCode(),
                        dbPeriodicLine.getManufacturerName());
                if (updatePdlCountedQty != null) {
                    log.info("Periodic Line to be Updated:" + updatePdlCountedQty);

                    updatePdlCountedQty.setCountedQty(dbPeriodicLine.getInventoryQty());
                    updatePdlCountedQty.setIsCompleted("1");
//                    PeriodicLine updateCountedQty = periodicLineRepository.save(updatePdlCountedQty);
                    try {
                        periodicLineRepository.updatePdlLine(dbPeriodicLine.getInventoryQty(),
                                1L,
                                dbPeriodicLine.getCycleCountNo(),
                                dbPeriodicLine.getItemCode(),
                                dbPeriodicLine.getManufacturerName());
                        log.info("Periodic Line CountedQty Updated, CountedQty: " + dbPeriodicLine + ", " + dbPeriodicLine.getInventoryQty());
                        statusList.add("true");
                    } catch (Exception e) {
                        statusList.add("false");
                        throw new RuntimeException(e);
                    }
                }
            }

            WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
            Long status = statusList.stream().filter(a -> a.equalsIgnoreCase("true")).count();
            log.info("True status Count: " + status);

            Long linesCount = updateStockCountLines.stream().count();
            log.info("to be Updated Perpetual Line Count: " + linesCount);

            if (status == linesCount) {
                log.info("Success: status == linesCount: " + status + ", " + linesCount);
                warehouseApiResponse.setStatusCode("200");
                warehouseApiResponse.setMessage("Update Success");
                return warehouseApiResponse;
            } else {
                log.info("Failed: status != linesCount: " + status + ", " + linesCount);
                warehouseApiResponse.setStatusCode("1400");
                warehouseApiResponse.setMessage("Counted Qty Update Failed");
                return warehouseApiResponse;
            }
        }
        return null;
    }

    // Find PeriodicHeader
    public List<PeriodicHeader> findPeriodicHeader(FindPeriodicHeader findPeriodicHeader) throws ParseException {

        if (findPeriodicHeader.getFromOrderProcessedOn() != null && findPeriodicHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPeriodicHeader.getFromOrderProcessedOn(), findPeriodicHeader.getToOrderProcessedOn());
            findPeriodicHeader.setFromOrderProcessedOn(dates[0]);
            findPeriodicHeader.setToOrderProcessedOn(dates[1]);
        }
        if (findPeriodicHeader.getFromOrderReceivedOn() != null && findPeriodicHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPeriodicHeader.getFromOrderReceivedOn(), findPeriodicHeader.getToOrderReceivedOn());
            findPeriodicHeader.setFromOrderReceivedOn(dates[0]);
            findPeriodicHeader.setToOrderReceivedOn(dates[1]);
        }

        PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(findPeriodicHeader);
        List<PeriodicHeader> results = periodicHeaderRepo.findAll(spec);
        return results;
    }

    public List<PeriodicLine> findPeriodicLine(FindPeriodicLine findPeriodicLine) throws ParseException {


        PeriodicLineSpecification spec = new PeriodicLineSpecification(findPeriodicLine);
        List<PeriodicLine> results = periodicLineRepository.findAll(spec);
        return results;
    }


    /**
     *
     * @param periodic periodic
     * @return
     */
    public WarehouseApiResponse postPeriodicOrderV2(Periodic periodic) {
        AuthToken authToken = authTokenService.getFhInboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhInboundOrderServiceApiUrl() + "warehouse/stockcount/periodic");
        HttpEntity<?> entity = new HttpEntity<>(periodic, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

}