package com.almailem.ams.api.connector.service;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.dto.AuditLog;
import com.almailem.ams.api.connector.model.master.CustomerMaster;
import com.almailem.ams.api.connector.model.master.ItemMaster;
import com.almailem.ams.api.connector.model.wms.Customer;
import com.almailem.ams.api.connector.model.wms.Item;
import com.almailem.ams.api.connector.model.wms.OrderCancelInput;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.repository.CustomerMasterRepository;
import com.almailem.ams.api.connector.repository.ItemMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class MastersService {

    @Autowired
    ItemMasterService itemMasterService;

    @Autowired
    CustomerMasterService customerMasterService;

    @Autowired
    ItemMasterRepository itemMasterRepository;

    @Autowired
    CustomerMasterRepository customerMasterRepository;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    MastersService mastersService;


    //-------------------------------------------------------------------------------------------
    List<Item> itemMasterList = null;
    List<Customer> customerMasterList = null;
    //=================================================================================================================
    static CopyOnWriteArrayList<Item> spIMList = null;                     // Item Master List
    static CopyOnWriteArrayList<Customer> spCMList = null;                // Customer Master List

    //==========================================================================================================================

    private String getMasterServiceApiUrl() {
        return propertiesConfig.getMastersServiceUrl();
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getIDMasterServiceApiUrl() {
        return propertiesConfig.getIdmasterServiceUrl();
    }

    /**
     * @param auditLog
     * @param loginUserID
     * @param authToken
     */
    private void createAuditLog(AuditLog auditLog, String loginUserID, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<?> entity = new HttpEntity<>(auditLog, headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "auditLog")
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<AuditLog> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    AuditLog.class);
            log.info("result : " + result.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //==========================================================================================================================
    public WarehouseApiResponse processItemMasterOrder() throws IllegalAccessException, InvocationTargetException {
        if (itemMasterList == null || itemMasterList.isEmpty()) {

            final List<String> branchCode = List.of("219", "213");
            List<ItemMaster> itemMasterOrderList = itemMasterRepository.findByItemMaster(0L,branchCode);
            log.info("Latest Item Master found: " + itemMasterOrderList);
            itemMasterList = new ArrayList<>();

            for (ItemMaster dbIBOrder : itemMasterOrderList) {
                log.info("Item Master: " + dbIBOrder);
                Item item = new Item();

                item.setCompanyCode(dbIBOrder.getCompanyCode());
                item.setBranchCode(dbIBOrder.getBranchCode());
                item.setSku(dbIBOrder.getItemCode());
                item.setSkuDescription(dbIBOrder.getItemDescription());
                item.setUom(dbIBOrder.getUnitOfMeasure());
                item.setItemGroupId(dbIBOrder.getItemGroupId());
                item.setSubItemGroupId(dbIBOrder.getSecondaryItemGroupId());
                item.setManufacturerName(dbIBOrder.getManufacturerShortName());
                item.setManufacturerCode(dbIBOrder.getManufacturerCode());
                item.setManufacturerFullName(dbIBOrder.getManufacturerFullName());
                item.setCreatedBy(dbIBOrder.getCreatedUsername());
                item.setCreatedOn(String.valueOf(dbIBOrder.getItemCreationDate()));
                item.setIsNew(dbIBOrder.getIsNew());
                item.setIsUpdate(dbIBOrder.getIsUpdate());
                item.setIsCompleted(dbIBOrder.getIsCompleted());

                item.setMiddlewareId(dbIBOrder.getItemMasterId());
                item.setMiddlewareTable("Item_Master");

                itemMasterList.add(item);
            }

            spIMList = new CopyOnWriteArrayList<Item>(itemMasterList);
            log.info("There is no Item Master record found to process (sql) ...Waiting..");
        }

        if (itemMasterList != null) {
            log.info("Latest Item Master found: " + itemMasterList);
            for (Item inbound : spIMList) {
                try {
                    log.info("Item Code : " + inbound.getSku());
                    WarehouseApiResponse inboundHeader = itemMasterService.postItemMaster(inbound);
                    if (inboundHeader != null) {
                        // Updating the Processed Status
                        itemMasterService.updateProcessedItemMaster(inbound.getMiddlewareId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getManufacturerName(), inbound.getSku(), 10L);
                        itemMasterList.remove(inbound);
                        return inboundHeader;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on item Master processing : " + e.toString());
                    // Updating the Processed Status
                    itemMasterService.updateProcessedItemMaster(inbound.getMiddlewareId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getManufacturerName(), inbound.getSku(), 100L);
                    itemMasterList.remove(inbound);
                    //============================================================================================
                    //Sending Failed Details through Mail
                    OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(inbound.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(inbound.getSku());
                    inboundOrderCancelInput.setReferenceField2(inbound.getManufacturerName());
                    inboundOrderCancelInput.setReferenceField1("ITEMMASTER");
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                        if(e.toString().contains("BadRequestException")){
                            errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                        }
                    } catch (Exception ex) {
                        throw new BadRequestException("ErrorDesc Extract Error" + ex);
                    }
                    inboundOrderCancelInput.setRemarks(errorDesc);

                    mastersService.sendMail(inboundOrderCancelInput);
                    //============================================================================================
                    throw new BadRequestException("Exception :" + e);
                }
            }
        }
        return null;
    }

    //==========================================================================================================================
    public WarehouseApiResponse processCustomerMasterOrder() throws IllegalAccessException, InvocationTargetException {
        if (customerMasterList == null || customerMasterList.isEmpty()) {

            final List<String> branchCode = List.of("219", "213");
            List<CustomerMaster> customerMasterOrderList = customerMasterRepository.findByCustomerMaster(0L,branchCode);
            log.info("Latest Customer Master found: " + customerMasterOrderList);
            customerMasterList = new ArrayList<>();

            for (CustomerMaster dbIBOrder : customerMasterOrderList) {
                log.info("Customer Master: " + dbIBOrder);
                Customer customer = new Customer();

                customer.setCompanyCode(dbIBOrder.getCompanyCode());
                customer.setBranchCode(dbIBOrder.getBranchCode());
                customer.setPartnerCode(dbIBOrder.getCustomerCode());
                customer.setPartnerName(dbIBOrder.getCustomerName());
                customer.setAddress1(dbIBOrder.getHomeAddress1());
                customer.setAddress2(dbIBOrder.getHomeAddress2());
                customer.setPhoneNumber(dbIBOrder.getMobileNumber());
                customer.setCivilId(dbIBOrder.getCivilIdNumber());
                customer.setAlternatePhoneNumber(dbIBOrder.getHomeTelNumber());
                customer.setCreatedBy(dbIBOrder.getCreatedUsername());
                customer.setCreatedOn(String.valueOf(dbIBOrder.getCustomerCreationDate()));
                customer.setIsNew(dbIBOrder.getIsNew());
                customer.setIsUpdate(dbIBOrder.getIsUpdate());
                customer.setIsCompleted(dbIBOrder.getIsCompleted());

                customer.setMiddlewareId(dbIBOrder.getCustomerMasterId());
                customer.setMiddlewareTable("Customer_Master");

                customerMasterList.add(customer);
            }

            spCMList = new CopyOnWriteArrayList<Customer>(customerMasterList);
            log.info("There is no Customer Master record found to process (sql) ...Waiting..");
        }

        if (customerMasterList != null) {
            log.info("Latest Customer Master found: " + customerMasterList);
            for (Customer inbound : spCMList) {
                try {
                    log.info("Partner Code : " + inbound.getPartnerCode());
                    WarehouseApiResponse inboundHeader = customerMasterService.postCustomerMaster(inbound);
                    if (inboundHeader != null) {
                        // Updating the Processed Status
                        customerMasterService.updateProcessedCustomMaster(inbound.getMiddlewareId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getPartnerCode(), 10L);
                        customerMasterList.remove(inbound);
                        return inboundHeader;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on customer Master processing : " + e.toString());
                    // Updating the Processed Status
                    customerMasterService.updateProcessedCustomMaster(inbound.getMiddlewareId(), inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getPartnerCode(), 100L);
//                    supplierInvoiceService.createInboundIntegrationLog(inbound);
                    customerMasterList.remove(inbound);
                    //============================================================================================
                    //Sending Failed Details through Mail
                    OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCode());
                    inboundOrderCancelInput.setPlantId(inbound.getBranchCode());
                    inboundOrderCancelInput.setRefDocNumber(inbound.getPartnerCode());
                    inboundOrderCancelInput.setReferenceField1("CUSTOMERMASTER");
                    String errorDesc = null;
                    try {
                        if(e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                            errorDesc = errorDesc.replaceAll("}]", "");
                        }
                        if(e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        }
                        if(e.toString().contains("BadRequestException")){
                            errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                        }
                    } catch (Exception ex) {
                        throw new BadRequestException("ErrorDesc Extract Error" + ex);
                    }
                    inboundOrderCancelInput.setRemarks(errorDesc);

                    mastersService.sendMail(inboundOrderCancelInput);
                    //============================================================================================
                    throw new BadRequestException("Exception :" + e);
                }
            }
        }
        return null;
    }

    //==========================================================Email===================================================
    // Send EMail
    public String sendMail(OrderCancelInput inboundOrderCancelInput) {
        try {
            AuthToken authTokenForMasterService = authTokenService.getMastersServiceAuthToken();
            String authToken = authTokenForMasterService.getAccess_token();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "Classic WMS's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getMasterServiceApiUrl() + "email/sendMail");
            HttpEntity<?> entity = new HttpEntity<>(inboundOrderCancelInput, headers);
            ResponseEntity<String> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }


}