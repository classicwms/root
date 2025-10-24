package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.perpetual.FindPerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.FindPerpetualLine;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.PerpetualLine;
import com.almailem.ams.api.connector.model.wms.Perpetual;
import com.almailem.ams.api.connector.model.wms.UpdateStockCountLine;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.PerpetualHeaderRepository;
import com.almailem.ams.api.connector.repository.PerpetualLineRepository;
import com.almailem.ams.api.connector.repository.specification.PerpetualHeaderSpecification;
import com.almailem.ams.api.connector.repository.specification.PerpetualLineSpecification;
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
public class PerpetualService extends BaseService {

    @Autowired
    PerpetualHeaderRepository perpetualHeaderRepo;

    @Autowired
    PerpetualLineRepository perpetualLineRepository;

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

    /**
     * Get All StockCount Perpetual Details
     *
     * @return
     */
    public List<PerpetualHeader> getAllPerpetualDetails() {
        List<PerpetualHeader> perpetuals = perpetualHeaderRepo.findAll();
        return perpetuals;
    }

    //    public void updateProcessedPerpetualOrder(String cycleCountNo) {
//        PerpetualHeader dbOrder = perpetualHeaderRepo.findTopByCycleCountNoOrderByOrderReceivedOnDesc(cycleCountNo);
//        log.info("orderId: " + cycleCountNo);
//        log.info("dbPerpetualOrder: " + dbOrder);
//        if (dbOrder != null) {
//            dbOrder.setProcessedStatusId(10L);
//            dbOrder.setOrderProcessedOn(new Date());
//            perpetualHeaderRepo.updateProcessStatusId(cycleCountNo, new Date());
//        }
//    }

    /**
     *
     * @param perpetualHeaderId
     * @param companyCode
     * @param branchCode
     * @param cycleCountNo
     * @param processedStatusId
     * @return
     */
    public PerpetualHeader updateProcessedPerpetualOrder(Long perpetualHeaderId, String companyCode,
                                                      String branchCode, String cycleCountNo, Long processedStatusId) {
        PerpetualHeader dbInboundOrder =
                perpetualHeaderRepo.getPerpetualHeader(perpetualHeaderId);
//                        findTopByPerpetualHeaderIdAndCompanyCodeAndBranchCodeAndCycleCountNoOrderByOrderReceivedOnDesc(
//                        perpetualHeaderId, companyCode, branchCode, cycleCountNo);
        log.info("orderId : " + cycleCountNo);
        log.info("dbInboundOrder : " + dbInboundOrder);
        if (dbInboundOrder != null) {
            perpetualHeaderRepo.updateProcessStatusId(dbInboundOrder.getPerpetualHeaderId(), processedStatusId);
        }
        return dbInboundOrder;
    }

    /**
     * @param updateStockCountLines
     * @return
     */
    public WarehouseApiResponse updatePerpetualStockCount(List<UpdateStockCountLine> updateStockCountLines) {
        if (updateStockCountLines != null) {
            log.info("Perpertual Lines to be Updated:" + updateStockCountLines);
            List<String> statusList = new ArrayList<>();
            for (UpdateStockCountLine dbPerpetualLine : updateStockCountLines) {
                PerpetualLine updatePplCountedQty = perpetualLineRepository.findByCycleCountNoAndItemCodeAndManufacturerName(
                        dbPerpetualLine.getCycleCountNo(),
                        dbPerpetualLine.getItemCode(),
                        dbPerpetualLine.getManufacturerName());
                if (updatePplCountedQty != null) {
                    log.info("Perpertual Line to be Updated:" + updatePplCountedQty);

                    updatePplCountedQty.setCountedQty(dbPerpetualLine.getInventoryQty());
                    updatePplCountedQty.setIsCompleted("1");
//                    PerpetualLine updateCountedQty = perpetualLineRepository.save(updatePplCountedQty);
                    try {
                        perpetualLineRepository.updatePplLine(dbPerpetualLine.getInventoryQty(),
                                1L,
                                dbPerpetualLine.getCycleCountNo(),
                                dbPerpetualLine.getItemCode(),
                                dbPerpetualLine.getManufacturerName());
                        log.info("Perpertual Line CountedQty Updated, CountedQty: " + dbPerpetualLine + ", " + dbPerpetualLine.getInventoryQty());
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

    public WarehouseApiResponse postPerpetualOrder(Perpetual perpetual) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(perpetual.getPerpetualHeaderV1().getCompanyCode(), perpetual.getPerpetualHeaderV1().getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "warehouse/stockcount/perpetual");
        } else {
            authToken = authTokenService.getAmgharaTransactionServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaTransactionServiceApiUrl() + "warehouse/stockcount/perpetual");
        }
        HttpEntity<?> entity = new HttpEntity<>(perpetual, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

    // Find PerpetualHeader
    public List<PerpetualHeader> findPerpetualHeader(FindPerpetualHeader findPerpetualHeader) throws ParseException {

        if (findPerpetualHeader.getFromOrderProcessedOn() != null && findPerpetualHeader.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPerpetualHeader.getFromOrderProcessedOn(), findPerpetualHeader.getToOrderProcessedOn());
            findPerpetualHeader.setFromOrderProcessedOn(dates[0]);
            findPerpetualHeader.setToOrderProcessedOn(dates[1]);
        }
        if (findPerpetualHeader.getFromOrderReceivedOn() != null && findPerpetualHeader.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findPerpetualHeader.getFromOrderReceivedOn(), findPerpetualHeader.getToOrderReceivedOn());
            findPerpetualHeader.setFromOrderReceivedOn(dates[0]);
            findPerpetualHeader.setToOrderReceivedOn(dates[1]);
        }

        PerpetualHeaderSpecification spec = new PerpetualHeaderSpecification(findPerpetualHeader);
        List<PerpetualHeader> results = perpetualHeaderRepo.findAll(spec);
        return results;
    }

    // Find PerpetualLine
    public List<PerpetualLine> findPerpetualLine(FindPerpetualLine findPerpetualLine) throws ParseException {

        PerpetualLineSpecification spec = new PerpetualLineSpecification(findPerpetualLine);
        List<PerpetualLine> results = perpetualLineRepository.findAll(spec);
        return results;
    }


    /**
     *
     * @param perpetual perpetual
     * @return
     */
    public WarehouseApiResponse postPerpetualOrderV2(Perpetual perpetual) {
        AuthToken authToken = authTokenService.getFhOutboundOrderServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhOutboundServiceApiUrl() + "outboundorder/stockcount/perpetual");
        HttpEntity<?> entity = new HttpEntity<>(perpetual, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result: " + result.getStatusCode());
        return result.getBody();
    }

}
