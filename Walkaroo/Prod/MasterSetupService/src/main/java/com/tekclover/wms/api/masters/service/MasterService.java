package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.masters.Customer;
import com.tekclover.wms.api.masters.model.masters.Item;
import com.tekclover.wms.api.masters.repository.CustomerMasterRepository;
import com.tekclover.wms.api.masters.repository.ItemMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Service
public class MasterService {
    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private ItemMasterRepository itemMasterRepository;
    //-------------------------------------------------------------------------------------------

    public Item processItemMaster(Item item) throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("Item Master Received : " + item);
            Item inboundItemMaster = itemMasterRepository.save(item);
            if (inboundItemMaster != null) {
                // Updating the Processed Status
//                masterOrderService.updateProcessedItemMaster(item.getSku());
                return inboundItemMaster;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error on item master processing : " + e.toString());
            // Updating the Processed Status
//            masterOrderService.updateProcessedItemMaster(item.getSku());
//            masterOrderService.createInboundIntegrationLog(item);
        }
        return null;
    }

    //=====================================================================Customer Master=============================================================================

    // CustomerMaster
    public Customer processCustomerMaster(Customer customer) throws IllegalAccessException, InvocationTargetException {

        try {
            log.info("Customer Master received : " + customer);
            Customer inboundCustomerMaster = customerMasterRepository.save(customer);
            if (inboundCustomerMaster != null) {
                // Updating the Processed Status
//                masterOrderService.updateProcessedCustomerMaster(customer.getPartnerCode());
                return inboundCustomerMaster;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error on customer master processing : " + e.toString());
            // Updating the Processed Status
//            masterOrderService.updateProcessedCustomerMaster(customer.getPartnerCode());
//            masterOrderService.createInboundIntegrationLog(businessPartnerV2);
        }
        return null;
    }
//    public ImBasicData1V2 processItemMaster(Item item) throws IllegalAccessException, InvocationTargetException {
//
//        ImBasicData1V2 imBasicData1 = new ImBasicData1V2();
//
//        imBasicData1.setCompanyCodeId(item.getCompanyCode());
//        imBasicData1.setPlantId(item.getBranchCode());
//        imBasicData1.setItemCode(item.getSku());
//        imBasicData1.setDescription(item.getSkuDescription());
//        imBasicData1.setUomId(item.getUom());
//        imBasicData1.setItemGroup(item.getItemGroupId());
//        imBasicData1.setSubItemGroup(item.getSubItemGroupId());
//        imBasicData1.setManufacturerPartNo(item.getManufacturerName());
//        imBasicData1.setManufacturerName(item.getManufacturerName());
//        imBasicData1.setManufacturerCode(item.getManufacturerCode());
//        imBasicData1.setManufacturerFullName(item.getManufacturerFullName());
//        imBasicData1.setBrand(item.getBrand());
//        imBasicData1.setSupplierPartNumber(item.getSupplierPartNumber());
//        imBasicData1.setCreatedBy(item.getCreatedBy());
//
//        try {
//            Date reqDelDate = DateUtils.convertStringToDateWithTime(item.getCreatedOn());
//            imBasicData1.setCreatedOn(reqDelDate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Date format should be MM-dd-yyyy");
//        }
//
//        imBasicData1.setDeletionIndicator(0L);
//        imBasicData1.setMiddlewareId(item.getMiddlewareId());
//        imBasicData1.setMiddlewareTable(item.getMiddlewareTable());
//
//        try {
//            log.info("Imbasic Data 1 sku : " + item.getSku());
//            ImBasicData1V2 inboundItemMaster = masterOrderService.processItemMasterReceived(imBasicData1);
//            if (inboundItemMaster != null) {
//                // Updating the Processed Status
//                masterOrderService.updateProcessedItemMaster(imBasicData1.getItemCode());
//                return inboundItemMaster;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Error on item master processing : " + e.toString());
//            // Updating the Processed Status
//            masterOrderService.updateProcessedItemMaster(imBasicData1.getItemCode());
//            masterOrderService.createInboundIntegrationLog(imBasicData1);
//        }
//        return null;
//    }
//
//    //=====================================================================Customer Master=============================================================================
//
//    // CustomerMaster
//    public BusinessPartnerV2 processCustomerMaster(Customer customer) throws IllegalAccessException, InvocationTargetException {
//        BusinessPartnerV2 businessPartnerV2 = new BusinessPartnerV2();
//
//        businessPartnerV2.setCompanyCodeId(customer.getCompanyCode());
//        businessPartnerV2.setPlantId(customer.getBranchCode());
//        businessPartnerV2.setPartnerCode(customer.getPartnerCode());
//        businessPartnerV2.setPartnerName(customer.getPartnerName());
//        businessPartnerV2.setBusinessPartnerType(2L);
//        businessPartnerV2.setAddress1(customer.getAddress1());
//        businessPartnerV2.setAddress2(customer.getAddress2());
//        businessPartnerV2.setPhoneNumber(customer.getPhoneNumber());
//        businessPartnerV2.setCivilId(customer.getCivilId());
//        businessPartnerV2.setCountry(customer.getCountry());
//        businessPartnerV2.setAlternatePhoneNumber(customer.getAlternatePhoneNumber());
//        businessPartnerV2.setCreatedBy(customer.getCreatedBy());
//        try {
//            Date reqDelDate = DateUtils.convertStringToDateWithTime(customer.getCreatedOn());
//            businessPartnerV2.setCreatedOn(reqDelDate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Date format should be MM-dd-yyyy");
//        }
//        businessPartnerV2.setDeletionIndicator(0L);
//        businessPartnerV2.setMiddlewareId(customer.getMiddlewareId());
//        businessPartnerV2.setMiddlewareTable(customer.getMiddlewareTable());
//
//        try {
//            log.info("Customer Code : " + customer.getPartnerCode());
//            BusinessPartnerV2 inboundCustomerMaster = masterOrderService.processCustomerMasterReceived(businessPartnerV2);
//            if (inboundCustomerMaster != null) {
//                // Updating the Processed Status
//                masterOrderService.updateProcessedCustomerMaster(customer.getPartnerCode());
//                return inboundCustomerMaster;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Error on customer master processing : " + e.toString());
//            // Updating the Processed Status
//            masterOrderService.updateProcessedCustomerMaster(businessPartnerV2.getPartnerCode());
//            masterOrderService.createInboundIntegrationLog(businessPartnerV2);
//        }
//        return null;
//    }
}