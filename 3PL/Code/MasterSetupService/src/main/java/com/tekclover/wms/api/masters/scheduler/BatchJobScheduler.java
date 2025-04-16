package com.tekclover.wms.api.masters.scheduler;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;

import com.tekclover.wms.api.masters.model.email.OrderCancelInput;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.masters.Customer;
import com.tekclover.wms.api.masters.model.masters.Item;
import com.tekclover.wms.api.masters.repository.BusinessPartnerV2Repository;
import com.tekclover.wms.api.masters.repository.CustomerMasterRepository;
import com.tekclover.wms.api.masters.repository.ItemMasterRepository;
import com.tekclover.wms.api.masters.repository.WarehouseRepository;
import com.tekclover.wms.api.masters.service.*;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class BatchJobScheduler {

    //-------------------------------------------------------------------------------------------

    @Autowired
    CustomerMasterRepository customerMasterRepository;

    @Autowired
    ItemMasterRepository itemMasterRepository;

    @Autowired
    MasterOrderService masterOrderService;

    @Autowired
    BusinessPartnerV2Repository businessPartnerRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    BusinessPartnerService businessService;

    @Autowired
    SendMailService sendMailService;

    //-------------------------------------------------------------------------------------------

    List<ImBasicData1V2> inboundItemList = null;
    List<BusinessPartnerV2> inboundCustomerList = null;
    static CopyOnWriteArrayList<ImBasicData1V2> spList = null;            // Inbound List
    static CopyOnWriteArrayList<BusinessPartnerV2> spCustomerList = null;    // Customer List

//    @Scheduled(fixedDelay = 30000)
    public void processInboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException, MessagingException, IOException {
        if (inboundItemList == null || inboundItemList.isEmpty()) {
            List<Item> sqlInboundList = itemMasterRepository.findByProcessedStatusIdOrderByOrderReceivedOn(0L);
            inboundItemList = new ArrayList<>();
            for (Item item : sqlInboundList) {
                ImBasicData1V2 imBasicData1 = new ImBasicData1V2();

                imBasicData1.setCompanyCodeId(item.getCompanyCode());
                imBasicData1.setPlantId(item.getBranchCode());
                imBasicData1.setItemCode(item.getSku());
                imBasicData1.setDescription(item.getSkuDescription());
                imBasicData1.setUomId(item.getUom());
                imBasicData1.setItemGroup(item.getItemGroupId());
                imBasicData1.setItemType(1L);                                   //HardCode
                imBasicData1.setCapacityCheck(false);                           //HardCode
                imBasicData1.setSubItemGroup(item.getSubItemGroupId());
                imBasicData1.setManufacturerPartNo(item.getManufacturerName());
                imBasicData1.setManufacturerName(item.getManufacturerName());
                imBasicData1.setManufacturerCode(item.getManufacturerCode());
                imBasicData1.setManufacturerFullName(item.getManufacturerFullName());
                imBasicData1.setBrand(item.getBrand());
                imBasicData1.setSupplierPartNumber(item.getSupplierPartNumber());
                imBasicData1.setCreatedBy(item.getCreatedBy());
                imBasicData1.setIsNew(item.getIsNew());
                imBasicData1.setIsUpdate(item.getIsUpdate());
                imBasicData1.setIsCompleted(item.getIsCompleted());

                try {
                    Date reqDelDate = new Date();
//                    if (item.getCreatedOn().length() > 10) {
//                        reqDelDate = DateUtils.convertStringToDateWithTime(item.getCreatedOn());
//                    }
                    if (item.getCreatedOn() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(item.getCreatedOn());
                        imBasicData1.setCreatedOn(reqDelDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }

                imBasicData1.setDeletionIndicator(0L);
                imBasicData1.setMiddlewareId(item.getMiddlewareId());
                imBasicData1.setMiddlewareTable(item.getMiddlewareTable());

                inboundItemList.add(imBasicData1);
            }
            spList = new CopyOnWriteArrayList<ImBasicData1V2>(inboundItemList);
            log.info("There is no Item Master record found to process (sql) ...Waiting..");
        }

        if (inboundItemList != null) {
            log.info("Latest Item Master found: " + inboundItemList);
            for (ImBasicData1V2 inbound : spList) {
                try {
                    log.info("Imbasic Data 1 sku : " + inbound.getItemCode());
                    ImBasicData1V2 inboundItemMaster = masterOrderService.processItemMasterReceived(inbound);
                    if (inboundItemMaster != null) {
                        // Updating the Processed Status
                        masterOrderService.updateProcessedItemMaster(inbound.getCompanyCodeId(), inbound.getPlantId(), inbound.getItemCode(), inbound.getManufacturerName(), 10L);
                        inboundItemList.remove(inbound);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on item master processing : " + e.toString());
                    // Updating the Processed Status
                    masterOrderService.updateProcessedItemMaster(inbound.getCompanyCodeId(), inbound.getPlantId(), inbound.getItemCode(), inbound.getManufacturerName(), 100L);

                    //============================================================================================
                    //Sending Failed Details through Mail
                    OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCodeId());
                    inboundOrderCancelInput.setPlantId(inbound.getPlantId());
                    inboundOrderCancelInput.setRefDocNumber(inbound.getItemCode());
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

                    sendMailService.sendMail(inboundOrderCancelInput);
                    //============================================================================================

                    masterOrderService.createInboundIntegrationLog(inbound);
                    inboundItemList.remove(inbound);
                }
            }
        }
    }

    //=====================================================================V2=============================================================================

    // CustomerMaster
//    @Scheduled(fixedDelay = 50000)
    public void processCustomerMaster() throws IllegalAccessException, InvocationTargetException, ParseException, MessagingException, IOException {
        if (inboundCustomerList == null || inboundCustomerList.isEmpty()) {
            List<Customer> sqlInboundList = customerMasterRepository.findByProcessedStatusIdOrderByOrderReceivedOn(0L);
            inboundCustomerList = new ArrayList<>();

            for (Customer customer : sqlInboundList) {

                        BusinessPartnerV2 businessPartnerV2 = new BusinessPartnerV2();

                        businessPartnerV2.setCompanyCodeId(customer.getCompanyCode());
                        businessPartnerV2.setPlantId(customer.getBranchCode());
                        businessPartnerV2.setPartnerCode(customer.getPartnerCode());
                        businessPartnerV2.setPartnerName(customer.getPartnerName());
                        businessPartnerV2.setBusinessPartnerType(2L);
                        businessPartnerV2.setAddress1(customer.getAddress1());
                        businessPartnerV2.setAddress2(customer.getAddress2());
                        businessPartnerV2.setPhoneNumber(customer.getPhoneNumber());
                        businessPartnerV2.setCivilId(customer.getCivilId());
                        businessPartnerV2.setCountry(customer.getCountry());
                        businessPartnerV2.setAlternatePhoneNumber(customer.getAlternatePhoneNumber());
                        businessPartnerV2.setCreatedBy(customer.getCreatedBy());
                        try {
                            Date reqDelDate = new Date();
//                            if (customer.getCreatedOn().length() > 10) {
//                                reqDelDate = DateUtils.convertStringToDateWithTime(customer.getCreatedOn());
//                            }
                            if (customer.getCreatedOn() != null) {
                                reqDelDate = DateUtils.convertStringToDate2(customer.getCreatedOn());
                            }
                            businessPartnerV2.setCreatedOn(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be MM-dd-yyyy");
                        }
                        businessPartnerV2.setDeletionIndicator(0L);
                        businessPartnerV2.setMiddlewareId(customer.getMiddlewareId());
                        businessPartnerV2.setMiddlewareTable(customer.getMiddlewareTable());
                        businessPartnerV2.setIsNew(customer.getIsNew());
                        businessPartnerV2.setIsUpdate(customer.getIsUpdate());
                        businessPartnerV2.setIsCompleted(customer.getIsCompleted());

                        inboundCustomerList.add(businessPartnerV2);
                    }
                    spCustomerList = new CopyOnWriteArrayList<BusinessPartnerV2>(inboundCustomerList);
                    log.info("There is no Customer Master record found to process (sql) ...Waiting..");
//                }
//            }
        }
        if (inboundCustomerList != null) {
            log.info("Latest Customer Master found: " + inboundCustomerList);
            for (BusinessPartnerV2 inbound : spCustomerList) {
                try {
                    log.info("Customer Code : " + inbound.getPartnerCode());
                    List<BusinessPartnerV2> inboundCustomerMaster = masterOrderService.processCustomerMasterReceived(inbound);
                    if (inboundCustomerMaster != null) {
                        // Updating the Processed Status
                        masterOrderService.updateProcessedCustomerMaster(inbound.getCompanyCodeId(), inbound.getPlantId(), inbound.getPartnerCode(), 10L);
                        inboundCustomerList.remove(inbound);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on customer master processing : " + e.toString());
                    // Updating the Processed Status
                    masterOrderService.updateProcessedCustomerMaster(inbound.getCompanyCodeId(), inbound.getPlantId(), inbound.getPartnerCode(), 100L);
                    //============================================================================================
                    //Sending Failed Details through Mail
                    OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                    inboundOrderCancelInput.setCompanyCodeId(inbound.getCompanyCodeId());
                    inboundOrderCancelInput.setPlantId(inbound.getPlantId());
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

                    sendMailService.sendMail(inboundOrderCancelInput);
                    //============================================================================================
                    masterOrderService.createInboundIntegrationLog(inbound);
                    inboundCustomerList.remove(inbound);
                }
            }
        }
    }
}