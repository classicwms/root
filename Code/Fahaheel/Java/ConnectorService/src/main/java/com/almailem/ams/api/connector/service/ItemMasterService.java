package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.master.FindItemMaster;
import com.almailem.ams.api.connector.model.master.ItemMaster;
import com.almailem.ams.api.connector.model.wms.Item;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.ItemMasterRepository;
import com.almailem.ams.api.connector.repository.specification.ItemMasterSpecification;
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
public class ItemMasterService extends BaseService {

    @Autowired
    ItemMasterRepository itemMasterRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;


    private String getMasterServiceApiUrl() {
        return propertiesConfig.getMastersServiceUrl();
    }

    private String getFhMasterServiceApiUrl() {
        return propertiesConfig.getFhMastersServiceUrl();
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
     * Get All ItemMaster Details
     *
     * @return
     */
    public List<ItemMaster> getAllItemMasterDetails() {
        List<ItemMaster> imList = itemMasterRepository.findAll();
        return imList;
    }

    /**
     * @param itemCode
     */
    public void updateProcessedItemMaster(Long itemMasterId, String companyCode, String branchCode, String manufacturerName, String itemCode, Long processedStatusId) {
        ItemMaster dbItemMaster = itemMasterRepository.getItemMaster(itemMasterId);
//                findTopByItemMasterIdAndCompanyCodeAndBranchCodeAndItemCodeAndManufacturerShortNameOrderByOrderReceivedOn(
//                itemMasterId, companyCode, branchCode, itemCode, manufacturerName);
        log.info("ItemCode : " + itemCode);
        log.info("dbItemMaster : " + dbItemMaster);
        if (dbItemMaster != null) {
//            dbItemMaster.setProcessedStatusId(10L);
//            dbItemMaster.setOrderProcessedOn(new Date());
//            ItemMaster itemMaster = itemMasterRepository.save(dbItemMaster);
//            itemMasterRepository.updateProcessStatusId(itemCode, new Date());
            itemMasterRepository.updateProcessStatusId(dbItemMaster.getItemMasterId(), processedStatusId);
        }
    }

    /**
     * @param itemCode
     */
//    public void updateProcessedItemMaster(String companyCode, String branchCode, String manufacturerName, String itemCode, String remark) {
//        ItemMaster dbItemMaster = itemMasterRepository.findTopByCompanyCodeAndBranchCodeAndItemCodeAndManufacturerShortNameOrderByOrderReceivedOnDesc(
//                companyCode, branchCode, itemCode, manufacturerName);
//        log.info("ItemCode : " + itemCode);
//        log.info("dbItemMaster : " + dbItemMaster);
//        if (dbItemMaster != null) {
//            dbItemMaster.setProcessedStatusId(10L);
//            dbItemMaster.setRemarks(remark);
//            dbItemMaster.setOrderProcessedOn(new Date());
//            ItemMaster itemMaster = itemMasterRepository.save(dbItemMaster);
//            itemMasterRepository.updateProcessStatusId(itemCode, new Date());
//        }
//    }

    /**
     * @param item
     * @return
     */
    public WarehouseApiResponse postItemMaster(Item item) {
        AuthToken authToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        String warehouse = validateWarehouse(item.getCompanyCode(), item.getBranchCode());
        UriComponentsBuilder builder;
        if(warehouse != null && warehouse.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            authToken = authTokenService.getMastersServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getMasterServiceApiUrl() + "masterorder/master/item");
        } else {
            authToken = authTokenService.getAmgharaMastersServiceAuthToken();
            headers.add("Authorization", "Bearer " + authToken.getAccess_token());
            builder = UriComponentsBuilder.fromHttpUrl(getAmgharaMasterServiceApiUrl() + "masterorder/master/item");
        }
        HttpEntity<?> entity = new HttpEntity<>(item, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // Find Item Master
    public List<ItemMaster> findItemMaster(FindItemMaster findItemMaster) throws ParseException {

        if (findItemMaster.getFromOrderProcessedOn() != null && findItemMaster.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findItemMaster.getFromOrderProcessedOn(), findItemMaster.getToOrderProcessedOn());
            findItemMaster.setFromOrderProcessedOn(dates[0]);
            findItemMaster.setToOrderProcessedOn(dates[1]);
        }
        if (findItemMaster.getFromOrderReceivedOn() != null && findItemMaster.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findItemMaster.getFromOrderReceivedOn(), findItemMaster.getToOrderReceivedOn());
            findItemMaster.setFromOrderReceivedOn(dates[0]);
            findItemMaster.setToOrderReceivedOn(dates[1]);
        }

        ItemMasterSpecification spec = new ItemMasterSpecification(findItemMaster);
        List<ItemMaster> results = itemMasterRepository.findAll(spec);
        return results;
    }

    /**
     * Fahaheel
     * @param item item
     * @return
     */
    public WarehouseApiResponse postItemMasterV2(Item item) {
        AuthToken authToken = authTokenService.getFHMastersServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getFhMasterServiceApiUrl() + "masterorder/master/item");
        HttpEntity<?> entity = new HttpEntity<>(item, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

}
