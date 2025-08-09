package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.master.CustomerMaster;
import com.almailem.ams.api.connector.model.master.FindCustomerMaster;
import com.almailem.ams.api.connector.model.wms.Customer;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.CustomerMasterRepository;
import com.almailem.ams.api.connector.repository.specification.CustomerMasterSpecification;
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
public class CustomerMasterService {

    @Autowired
    CustomerMasterRepository customerMasterRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    PropertiesConfig propertiesConfig;


    private String getMasterServiceApiUrl() {
        return propertiesConfig.getMastersServiceUrl();
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
     * Get All CustomerMaster Details
     *
     * @return
     */
    public List<CustomerMaster> getAllCustomerMasterDetails() {
        List<CustomerMaster> customerMasters = customerMasterRepository.findAll();
        return customerMasters;
    }

    /**
     * @param partnerCode
     */
    public void updateProcessedCustomMaster(Long customerMasterId, String companyCode, String branchCode, String partnerCode,  Long processedStatusId) {
        CustomerMaster dbCustomerMaster = customerMasterRepository.findTopByCustomerMasterIdAndCompanyCodeAndBranchCodeAndCustomerCodeOrderByOrderReceivedOn(
                customerMasterId, companyCode, branchCode, partnerCode);
        log.info("PartnerCode : " + partnerCode);
        log.info("dbCustomerMaster : " + dbCustomerMaster);
        if (dbCustomerMaster != null) {
//            dbCustomerMaster.setProcessedStatusId(10L);
//            dbCustomerMaster.setOrderProcessedOn(new Date());
//            CustomerMaster customerMaster = customerMasterRepository.save(dbCustomerMaster);
//            customerMasterRepository.updateProcessStatusId(partnerCode, new Date());
            customerMasterRepository.updateProcessStatusId(dbCustomerMaster.getCustomerMasterId(), processedStatusId);
        }
    }

//    public void updateProcessedCustomMaster(String companyCode, String branchCode, String partnerCode, String remark) {
//        CustomerMaster dbCustomerMaster = customerMasterRepository.findTopByCompanyCodeAndBranchCodeAndCustomerCodeOrderByOrderReceivedOnDesc(
//                companyCode, branchCode, partnerCode);
//        log.info("PartnerCode : " + partnerCode);
//        log.info("dbCustomerMaster : " + dbCustomerMaster);
//        if (dbCustomerMaster != null) {
//            dbCustomerMaster.setProcessedStatusId(10L);
//            dbCustomerMaster.setRemarks(remark);
//            dbCustomerMaster.setOrderProcessedOn(new Date());
//            CustomerMaster customerMaster = customerMasterRepository.save(dbCustomerMaster);
//            customerMasterRepository.updateProcessStatusId(partnerCode, new Date());
//            customerMasterRepository.updateProcessStatusId(dbCustomerMaster.getCustomerMasterId(), 10L);
//        }
//    }

    /**
     * @param customer
     * @return
     */
    public WarehouseApiResponse postCustomerMaster(Customer customer) {
        AuthToken authToken = authTokenService.getMastersServiceAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "ClassicWMS RestTemplate");
        headers.add("Authorization", "Bearer " + authToken.getAccess_token());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(getMasterServiceApiUrl() + "masterorder/master/customer");
        HttpEntity<?> entity = new HttpEntity<>(customer, headers);
        ResponseEntity<WarehouseApiResponse> result =
                getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
        log.info("result : " + result.getStatusCode());
        return result.getBody();
    }

    // Find Customer Master
    public List<CustomerMaster> findCustomerMaster(FindCustomerMaster findCustomerMaster) throws ParseException {

        if (findCustomerMaster.getFromOrderProcessedOn() != null && findCustomerMaster.getToOrderProcessedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findCustomerMaster.getFromOrderProcessedOn(), findCustomerMaster.getToOrderProcessedOn());
            findCustomerMaster.setFromOrderProcessedOn(dates[0]);
            findCustomerMaster.setToOrderProcessedOn(dates[1]);
        }
        if (findCustomerMaster.getFromOrderReceivedOn() != null && findCustomerMaster.getToOrderReceivedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findCustomerMaster.getFromOrderReceivedOn(), findCustomerMaster.getToOrderReceivedOn());
            findCustomerMaster.setFromOrderReceivedOn(dates[0]);
            findCustomerMaster.setToOrderReceivedOn(dates[1]);
        }

        CustomerMasterSpecification spec = new CustomerMasterSpecification(findCustomerMaster);
        List<CustomerMaster> results = customerMasterRepository.findAll(spec);
        return results;
    }

}
