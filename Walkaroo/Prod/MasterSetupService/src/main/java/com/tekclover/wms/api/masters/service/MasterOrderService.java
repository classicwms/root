package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import com.tekclover.wms.api.masters.model.dto.CustomerImpl;
import com.tekclover.wms.api.masters.model.dto.PlantId;
import com.tekclover.wms.api.masters.model.warehouse.Warehouse;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.masters.Customer;
import com.tekclover.wms.api.masters.model.masters.InboundIntegrationLog;
import com.tekclover.wms.api.masters.model.masters.Item;
import com.tekclover.wms.api.masters.repository.*;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class MasterOrderService {
    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repo;

    @Autowired
    BusinessPartnerV2Repository businessPartnerRepo;

    @Autowired
    BusinessPartnerService businessService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    BusinessPartnerV2Repository businessPartnerV2Repository;

    @Autowired
    ImBasicData1Service imBasicData1Service;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    IDMasterService idMasterService;


    /*---------------------------------------ITEM_MASTER-----------------------------------------------------*/

    /**
     * Post Item
     *
     * @param item
     * @return
     */
    public ImBasicData1V2 postItem(Item item) throws ParseException {
        log.info("Item received: " + item);
        ImBasicData1V2 savedItem = saveItem(item);
        log.info("Saved Item: " + savedItem);
        return savedItem;
    }

    //Save Item
    private ImBasicData1V2 saveItem(Item item) throws ParseException {
        ImBasicData1V2 imBasicData1V2 = new ImBasicData1V2();
        try {
            imBasicData1V2.setCompanyCodeId(item.getCompanyCode());
            imBasicData1V2.setPlantId(item.getBranchCode());
            imBasicData1V2.setItemCode(item.getSku());
            imBasicData1V2.setDescription(item.getSkuDescription());
            imBasicData1V2.setUomId(item.getUom());
            imBasicData1V2.setItemGroup(item.getItemGroupId());
            imBasicData1V2.setSubItemGroup(item.getSubItemGroupId());
            imBasicData1V2.setManufacturerPartNo(item.getManufacturerCode());
            imBasicData1V2.setManufacturerName(item.getManufacturerName());
            imBasicData1V2.setBrand(item.getBrand());
            imBasicData1V2.setSupplierPartNumber(item.getSupplierPartNumber());
            imBasicData1V2.setCreatedBy(item.getCreatedBy());
            imBasicData1V2.setCreatedOn(new Date());
            imBasicData1V2.setIsNew(item.getIsNew());
            imBasicData1V2.setIsUpdate(item.getIsUpdate());
            imBasicData1V2.setIsCompleted(item.getIsCompleted());

            IKeyValuePair iKeyValuePair = imBasicData1V2Repo.getImBasicDataV2Description(
                    item.getCompanyCode(), item.getBranchCode());

            if (iKeyValuePair != null) {
                imBasicData1V2.setLanguageId(iKeyValuePair.getLanguageId());
                imBasicData1V2.setWarehouseId(iKeyValuePair.getWarehouseId());
            }

            imBasicData1V2.setDeletionIndicator(0L);
            imBasicData1V2Repo.save(imBasicData1V2);
        } catch (Exception e) {
            throw e;
        }
        return imBasicData1V2;
    }

    /*-------------------------------------CUSTOMER_MASTER-----------------------------------------------------------*/
    public BusinessPartnerV2 postCustomer(Customer customer) throws ParseException {
        log.info("Customer received: " + customer);
        BusinessPartnerV2 savedCustomer = saveCustomer(customer);
        log.info("Saved Customer: " + savedCustomer);
        return savedCustomer;
    }

    //Save Customer
    private BusinessPartnerV2 saveCustomer(Customer customer) throws ParseException {
        BusinessPartnerV2 businessPartnerV2 = new BusinessPartnerV2();
        try {
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
            businessPartnerV2.setCreatedOn(new Date());
            businessPartnerV2.setIsNew(customer.getIsNew());
            businessPartnerV2.setIsUpdate(customer.getIsUpdate());
            businessPartnerV2.setIsCompleted(customer.getIsCompleted());

            IKeyValuePair iKeyValuePair = imBasicData1V2Repo.getImBasicDataV2Description(
                    customer.getCompanyCode(), customer.getBranchCode());
            if (iKeyValuePair != null) {
                businessPartnerV2.setLanguageId(iKeyValuePair.getLanguageId());
                businessPartnerV2.setWarehouseId(iKeyValuePair.getWarehouseId());
            }

            businessPartnerV2.setDeletionIndicator(0L);
            businessPartnerRepo.save(businessPartnerV2);
        } catch (Exception e) {
            throw e;
        }
        return businessPartnerV2;
    }

    /**
     * Save Item Master
     *
     * @param item
     * @return
     */
    public ImBasicData1V2 processItemMasterReceived(ImBasicData1V2 item) throws ParseException {

        ImBasicData1V2 imBasicData1 = new ImBasicData1V2();
        Boolean updateImbasicData1 = false;

        try {

            BeanUtils.copyProperties(item, imBasicData1, CommonUtils.getNullPropertyNames(item));

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            item.getCompanyCodeId(),
                            item.getPlantId(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);

            Optional<ImBasicData1V2> existingImBasicData = imBasicData1V2Repo.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                    item.getCompanyCodeId(), item.getPlantId(), "EN", dbWarehouse.get().getWarehouseId(), item.getItemCode(), item.getManufacturerName(), 0L);

            Item dbitem = itemMasterRepository.findTopByCompanyCodeAndBranchCodeAndSkuAndManufacturerNameAndProcessedStatusIdOrderByOrderReceivedOn(
                    item.getCompanyCodeId(), item.getPlantId(), item.getItemCode(), item.getManufacturerName(), 0L);

            if (dbWarehouse != null && !dbWarehouse.isEmpty()) {
                imBasicData1.setLanguageId(dbWarehouse.get().getLanguageId());
                imBasicData1.setWarehouseId(dbWarehouse.get().getWarehouseId());
            }
            if (item.getIsNew() != null) {
                if (item.getIsNew().equalsIgnoreCase("Y") && (item.getIsUpdate() == null || item.getIsUpdate().equalsIgnoreCase("N")) ||
                        item.getIsNew().equalsIgnoreCase("1") && (item.getIsUpdate() == null || item.getIsUpdate().equalsIgnoreCase("0")) ||
                        item.getIsNew().equalsIgnoreCase("TRUE") && (item.getIsUpdate() == null || item.getIsUpdate().equalsIgnoreCase("FALSE"))) {

                    if (existingImBasicData != null && !existingImBasicData.isEmpty()) {

                        updateImbasicData1 = imBasicData1Service.deleteImBasicData1V2(item.getItemCode(), item.getCompanyCodeId(),
                                item.getPlantId(), "EN", item.getManufacturerName(), dbWarehouse.get().getWarehouseId());

                        dbitem.setRemarks("item Master Already Exist");
                        dbitem.setOrderProcessedOn(new Date());
                        log.info("item Master Already Exist and tblorderitem updated with remarks");

                        if (updateImbasicData1) {
                            try {

                                imBasicData1.setCreatedBy(existingImBasicData.get().getCreatedBy());
                                imBasicData1.setCreatedOn(existingImBasicData.get().getCreatedOn());
                                imBasicData1.setUpdatedBy(item.getCreatedBy());
                                imBasicData1.setUpdatedOn(new Date());

                                IKeyValuePair description = imBasicData1V2Repo.getDescription(imBasicData1.getCompanyCodeId(),
                                        imBasicData1.getLanguageId(),
                                        imBasicData1.getPlantId(),
                                        imBasicData1.getWarehouseId());

                                if(description != null) {
                                    imBasicData1.setCompanyDescription(description.getCompanyDesc());
                                    imBasicData1.setPlantDescription(description.getPlantDesc());
                                    imBasicData1.setWarehouseDescription(description.getWarehouseDesc());
                                }

                                imBasicData1V2Repo.save(imBasicData1);
                                log.info("Item Master updated Successfully");

                                dbitem.setRemarks("item Master Updated Successfully");
                                dbitem.setOrderProcessedOn(new Date());
                                log.info("item Master updated Successfully");

                                return imBasicData1;

                            } catch (BeansException e) {
                                throw new RuntimeException(e);
                            }
                        }


                    } else {

                        imBasicData1.setCreatedBy(item.getCreatedBy());
                        imBasicData1.setCreatedOn(new Date());
                        imBasicData1.setUpdatedOn(null);

                        IKeyValuePair description = imBasicData1V2Repo.getDescription(imBasicData1.getCompanyCodeId(),
                                imBasicData1.getLanguageId(),
                                imBasicData1.getPlantId(),
                                imBasicData1.getWarehouseId());

                        if(description != null) {
                            imBasicData1.setCompanyDescription(description.getCompanyDesc());
                            imBasicData1.setPlantDescription(description.getPlantDesc());
                            imBasicData1.setWarehouseDescription(description.getWarehouseDesc());
                        }

                        imBasicData1V2Repo.save(imBasicData1);

                        dbitem.setRemarks("item Master Created Successfully");
                        log.info("item Master Created Successfully: " + imBasicData1);

                        return imBasicData1;
                    }
                }
            }
            if (item.getIsUpdate() != null) {
                if (item.getIsUpdate().equalsIgnoreCase("Y") && (item.getIsNew() == null || item.getIsNew().equalsIgnoreCase("N")) ||
                        item.getIsUpdate().equalsIgnoreCase("1") && (item.getIsNew() == null || item.getIsNew().equalsIgnoreCase("0")) ||
                        item.getIsUpdate().equalsIgnoreCase("true") && (item.getIsNew() == null || item.getIsNew().equalsIgnoreCase("FALSE"))) {

                    if (existingImBasicData != null && !existingImBasicData.isEmpty()) {
                        updateImbasicData1 = imBasicData1Service.deleteImBasicData1V2(item.getItemCode(), item.getCompanyCodeId(),
                                item.getPlantId(), "EN", item.getManufacturerName(), dbWarehouse.get().getWarehouseId());
                    }

                    if (updateImbasicData1) {
                        try {

                            imBasicData1.setCreatedBy(existingImBasicData.get().getCreatedBy());
                            imBasicData1.setCreatedOn(existingImBasicData.get().getCreatedOn());
                            imBasicData1.setUpdatedBy(item.getCreatedBy());
                            imBasicData1.setUpdatedOn(new Date());

                            IKeyValuePair description = imBasicData1V2Repo.getDescription(imBasicData1.getCompanyCodeId(),
                                    imBasicData1.getLanguageId(),
                                    imBasicData1.getPlantId(),
                                    imBasicData1.getWarehouseId());

                            if(description != null) {
                                imBasicData1.setCompanyDescription(description.getCompanyDesc());
                                imBasicData1.setPlantDescription(description.getPlantDesc());
                                imBasicData1.setWarehouseDescription(description.getWarehouseDesc());
                            }

                            imBasicData1V2Repo.save(imBasicData1);
                            log.info("Item Master updated Successfully: " + imBasicData1);

                            dbitem.setRemarks("item Master Updated Successfully");
                            dbitem.setOrderProcessedOn(new Date());
                            log.info("item Master updated Successfully");

                            return imBasicData1;

                        } catch (BeansException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (item.getIsNew() == null && item.getIsUpdate() == null) {

                if (existingImBasicData != null && !existingImBasicData.isEmpty()) {
                    dbitem.setRemarks("item Master Already Exist");
                    dbitem.setOrderProcessedOn(new Date());
                    log.info("item Master Already Exist and tblorderitem updated with remarks");

                    throw new BadRequestException("item Master Already Exist");

                } else {

                    imBasicData1.setCreatedBy(item.getCreatedBy());
                    imBasicData1.setCreatedOn(new Date());
                    imBasicData1.setUpdatedOn(null);

                    IKeyValuePair description = imBasicData1V2Repo.getDescription(imBasicData1.getCompanyCodeId(),
                            imBasicData1.getLanguageId(),
                            imBasicData1.getPlantId(),
                            imBasicData1.getWarehouseId());

                    if(description != null) {
                        imBasicData1.setCompanyDescription(description.getCompanyDesc());
                        imBasicData1.setPlantDescription(description.getPlantDesc());
                        imBasicData1.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    imBasicData1V2Repo.save(imBasicData1);
                    log.info("item Master Created Successfully: " + imBasicData1);

                    dbitem.setRemarks("item Master Created Successfully");
                    itemMasterRepository.save(dbitem);
                    log.info("item Master Created Successfully");

                    return imBasicData1;
                }
            }

            if (item.getIsNew().equalsIgnoreCase("N") && item.getIsUpdate().equalsIgnoreCase("N")) {

                if (existingImBasicData != null && !existingImBasicData.isEmpty()) {
                    dbitem.setRemarks("item Master Already Exist");
                    dbitem.setOrderProcessedOn(new Date());

                    log.info("item Master Already Exist and tblorderitem updated with remarks");

                    throw new BadRequestException("item Master Already Exist");
                }
            }

            dbitem.setProcessedStatusId(10L);
            itemMasterRepository.save(dbitem);

        } catch (Exception e) {
            throw e;
        }
        return imBasicData1;
    }

    /**
     * @param sku
     */
    public void updateProcessedItemMaster(String companyCode, String BranchCode, String sku, String manufactureName, Long processStatusId) throws ParseException {
//        Item dbItemMaster = itemMasterRepository.findBySku(sku);
        Item dbItemMaster = itemMasterRepository.findTopByCompanyCodeAndBranchCodeAndSkuAndManufacturerNameAndProcessedStatusIdOrderByOrderReceivedOn(
                companyCode, BranchCode, sku, manufactureName, 0L);
        log.info("ItemCode : " + sku);
        log.info("dbItemMaster : " + dbItemMaster);
        if (dbItemMaster != null) {
            dbItemMaster.setProcessedStatusId(processStatusId);
            dbItemMaster.setOrderProcessedOn(new Date());
            Item itemMaster = itemMasterRepository.save(dbItemMaster);
        }
    }

    /**
     * @param inbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLog(ImBasicData1V2 inbound)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        // Get Warehouse
        Optional<Warehouse> dbWarehouse =
                warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        inbound.getCompanyCodeId(),
                        inbound.getPlantId(),
                        "EN",
                        0L
                );
        if (dbWarehouse == null || dbWarehouse.isEmpty()) {
            throw new BadRequestException("Warehouse not found! ");
        }
        InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
        dbInboundIntegrationLog.setLanguageId("EN");
        dbInboundIntegrationLog.setCompanyCodeId(inbound.getCompanyCodeId());
        dbInboundIntegrationLog.setPlantId(inbound.getPlantId());
        dbInboundIntegrationLog.setWarehouseId(dbWarehouse.get().getWarehouseId());
        dbInboundIntegrationLog.setIntegrationLogNumber(String.valueOf(System.currentTimeMillis()));
        dbInboundIntegrationLog.setRefDocNumber(inbound.getItemCode());
        dbInboundIntegrationLog.setOrderReceiptDate(new Date());
        dbInboundIntegrationLog.setIntegrationStatus("FAILED");
        dbInboundIntegrationLog.setOrderReceiptDate(new Date());
        dbInboundIntegrationLog.setDeletionIndicator(0L);
        dbInboundIntegrationLog.setCreatedBy("MSD_API");
        dbInboundIntegrationLog.setCreatedOn(new Date());
        dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
        log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        return dbInboundIntegrationLog;
    }

    //Save Customer
    public List<BusinessPartnerV2> processCustomerMasterReceived(BusinessPartnerV2 customer) throws ParseException {

        try {

            List<CustomerImpl> plantIdList = new ArrayList<>();
            List<BusinessPartnerV2> businessPartnerV2List = new ArrayList<>();
            Boolean updateBusinessPartner = false;

            if (customer != null) {
                if (customer.getPlantId() == null) {
                    AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
                    PlantId[] branchCodeList = idMasterService.getPlantId(customer.getCompanyCodeId(), "EN", authTokenForIDMasterService.getAccess_token());
                    if (branchCodeList != null) {
                        for (PlantId dbPlant : branchCodeList) {
                            CustomerImpl newCustomerImpl = new CustomerImpl();
                            // Get Warehouse
                            Optional<Warehouse> dbWarehouse =
                                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                            customer.getCompanyCodeId(),
                                            dbPlant.getPlantId(),
                                            "EN",
                                            0L
                                    );
                            log.info("dbWarehouse : " + dbWarehouse);

                            if (dbWarehouse != null && !dbWarehouse.isEmpty()) {
                                newCustomerImpl.setLanguageId(dbPlant.getLanguageId());
                                newCustomerImpl.setCompanyCodeId(customer.getCompanyCodeId());
                                newCustomerImpl.setPlantId(dbPlant.getPlantId());
                                newCustomerImpl.setWarehouseId(dbWarehouse.get().getWarehouseId());
                                plantIdList.add(newCustomerImpl);
                            }
                        }
                    }
                }
                if (customer.getPlantId() != null) {
                    // Get Warehouse
                    Optional<Warehouse> dbWarehouse =
                            warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                    customer.getCompanyCodeId(),
                                    customer.getPlantId(),
                                    "EN",
                                    0L
                            );
                    log.info("dbWarehouse : " + dbWarehouse);

                    if (dbWarehouse != null && !dbWarehouse.isEmpty()) {
                        CustomerImpl newCustomerImpl = new CustomerImpl();
                        newCustomerImpl.setLanguageId(dbWarehouse.get().getLanguageId());
                        newCustomerImpl.setCompanyCodeId(customer.getCompanyCodeId());
                        newCustomerImpl.setPlantId(customer.getPlantId());
                        newCustomerImpl.setWarehouseId(dbWarehouse.get().getWarehouseId());
                        plantIdList.add(newCustomerImpl);
                    }
                }
            }
            Customer dbCustomer = null;
            for (CustomerImpl dbCustomerImpl : plantIdList) {

                BusinessPartnerV2 businessPartnerV2 = new BusinessPartnerV2();
                BeanUtils.copyProperties(customer, businessPartnerV2, CommonUtils.getNullPropertyNames(customer));

                Optional<BusinessPartnerV2> existingCustomerMaster = businessPartnerRepo.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
                        dbCustomerImpl.getCompanyCodeId(), dbCustomerImpl.getPlantId(), dbCustomerImpl.getWarehouseId(), customer.getPartnerCode(), 2L, "EN", 0L);


                if (customer.getPlantId() != null) {
                    dbCustomer = customerMasterRepository.findTopByCompanyCodeAndBranchCodeAndPartnerCodeAndProcessedStatusIdOrderByOrderReceivedOn(
                            dbCustomerImpl.getCompanyCodeId(), dbCustomerImpl.getPlantId(), customer.getPartnerCode(), 0L);
                }

                if (customer.getPlantId() == null) {
                    dbCustomer = customerMasterRepository.findTopByCompanyCodeAndPartnerCodeOrderByOrderReceivedOn(
                            dbCustomerImpl.getCompanyCodeId(), customer.getPartnerCode());
                }

                businessPartnerV2.setLanguageId(dbCustomerImpl.getLanguageId());
                businessPartnerV2.setWarehouseId(dbCustomerImpl.getWarehouseId());
                businessPartnerV2.setPlantId(dbCustomerImpl.getPlantId());

                if (customer.getIsNew() != null) {
                    if (customer.getIsNew().equalsIgnoreCase("Y") && (customer.getIsUpdate() == null || customer.getIsUpdate().equalsIgnoreCase("N") ) ||
                            customer.getIsNew().equalsIgnoreCase("1") && (customer.getIsUpdate() == null || customer.getIsUpdate().equalsIgnoreCase("0") ) ||
                            customer.getIsNew().equalsIgnoreCase("TRUE") && (customer.getIsUpdate() == null || customer.getIsUpdate().equalsIgnoreCase("FALSE") )) {

                        if (existingCustomerMaster != null && !existingCustomerMaster.isEmpty()) {

                            updateBusinessPartner = businessService.deleteBusinessPartnerV2(
                                    customer.getPartnerCode(), dbCustomerImpl.getCompanyCodeId(), dbCustomerImpl.getPlantId(),
                                    dbCustomerImpl.getWarehouseId(), dbCustomerImpl.getLanguageId(), 2L);

                            if (updateBusinessPartner) {

                                try {

                                    businessPartnerV2.setCreatedBy(customer.getCreatedBy());
                                    businessPartnerV2.setCreatedOn(existingCustomerMaster.get().getCreatedOn());
                                    businessPartnerV2.setUpdatedBy(customer.getCreatedBy());
                                    businessPartnerV2.setUpdatedOn(new Date());
                                    businessPartnerRepo.save(businessPartnerV2);
                                    log.info("Business Master updated Successfully");

                                    dbCustomer.setRemarks("Customer Master Updated Successfully");
                                    dbCustomer.setOrderProcessedOn(new Date());
                                    log.info("Customer Master updated Successfully");

                                    businessPartnerV2List.add(businessPartnerV2);

                                } catch (BeansException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            dbCustomer.setRemarks("Customer Master Already Exist");
                            dbCustomer.setOrderProcessedOn(new Date());
                            log.info("Customer Master Already Exist and tblordercustomer updated with remarks");

                        } else {

                            businessPartnerV2.setCreatedBy(customer.getCreatedBy());
                            businessPartnerV2.setCreatedOn(new Date());
                            businessPartnerV2.setUpdatedOn(null);
                            businessPartnerRepo.save(businessPartnerV2);

                            businessPartnerV2List.add(businessPartnerV2);

                            dbCustomer.setRemarks("Customer Master Created Successfully");
                            log.info("Customer Master Created Successfully: " + businessPartnerV2);

                        }
                    }
                }
                if (customer.getIsUpdate() != null) {
                    if (customer.getIsUpdate().equalsIgnoreCase("Y") && (customer.getIsNew() == null || customer.getIsNew().equalsIgnoreCase("N")) ||
                            customer.getIsUpdate().equalsIgnoreCase("1") && (customer.getIsNew() == null || customer.getIsNew().equalsIgnoreCase("0")) ||
                            customer.getIsUpdate().equalsIgnoreCase("true") && (customer.getIsNew() == null || customer.getIsNew().equalsIgnoreCase("FALSE"))) {

                        if (existingCustomerMaster != null && !existingCustomerMaster.isEmpty()) {
                            updateBusinessPartner = businessService.deleteBusinessPartnerV2(
                                    customer.getPartnerCode(), dbCustomerImpl.getCompanyCodeId(), dbCustomerImpl.getPlantId(),
                                    dbCustomerImpl.getWarehouseId(), dbCustomerImpl.getLanguageId(), 2L);
                        }

                        if (updateBusinessPartner) {

                            try {

                                businessPartnerV2.setCreatedBy(customer.getCreatedBy());
                                businessPartnerV2.setCreatedOn(existingCustomerMaster.get().getCreatedOn());
                                businessPartnerV2.setUpdatedBy(customer.getCreatedBy());
                                businessPartnerV2.setUpdatedOn(new Date());
                                businessPartnerRepo.save(businessPartnerV2);
                                log.info("Business Master updated Successfully : " + businessPartnerV2);

                                dbCustomer.setRemarks("Customer Master Updated Successfully");
                                dbCustomer.setOrderProcessedOn(new Date());
                                log.info("Customer Master updated Successfully");

                                businessPartnerV2List.add(businessPartnerV2);

                            } catch (BeansException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                if (customer.getIsNew() == null && customer.getIsUpdate() == null) {

                    if (existingCustomerMaster != null && !existingCustomerMaster.isEmpty()) {
                        dbCustomer.setRemarks("Customer Master Already Exist");
                        dbCustomer.setOrderProcessedOn(new Date());
                        log.info("Customer Master Already Exist and tblordercustomer updated with remarks");
                    } else {

                        businessPartnerV2.setCreatedBy(customer.getCreatedBy());
                        businessPartnerV2.setCreatedOn(new Date());
                        businessPartnerV2.setUpdatedOn(null);
                        businessPartnerRepo.save(businessPartnerV2);

                        dbCustomer.setRemarks("Customer Master Created Successfully");
                        log.info("Customer Master Created Successfully: " + businessPartnerV2);

                        businessPartnerV2List.add(businessPartnerV2);
                    }
                }

                if (customer.getIsNew().equalsIgnoreCase("N") && customer.getIsUpdate().equalsIgnoreCase("N")) {

                    if (existingCustomerMaster != null && !existingCustomerMaster.isEmpty()) {
                        dbCustomer.setRemarks("Customer Master Already Exist");
                        dbCustomer.setOrderProcessedOn(new Date());
                        log.info("Customer Master Already Exist and tblordercustomer updated with remarks");

                    }
                }
            }

            dbCustomer.setProcessedStatusId(10L);
            customerMasterRepository.save(dbCustomer);
            return businessPartnerV2List;

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param partnerCode
     */
    public void updateProcessedCustomerMaster(String companyCode, String branchCode, String partnerCode, Long processedStatusId) throws ParseException {
        Customer dbCustomerMaster = customerMasterRepository.
                findTopByCompanyCodeAndBranchCodeAndPartnerCodeAndProcessedStatusIdOrderByOrderReceivedOn(companyCode, branchCode, partnerCode, 0L);
        log.info("PartnerCode : " + partnerCode);
        log.info("dbCustomerMaster : " + dbCustomerMaster);
        if (dbCustomerMaster != null) {
            dbCustomerMaster.setProcessedStatusId(processedStatusId);
            dbCustomerMaster.setOrderProcessedOn(new Date());
            Customer customerMaster = customerMasterRepository.save(dbCustomerMaster);
        }
    }

    /**
     * @param inbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLog(BusinessPartnerV2 inbound)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        // Get Warehouse
        Optional<Warehouse> dbWarehouse =
                warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        inbound.getCompanyCodeId(),
                        inbound.getPlantId(),
                        "EN",
                        0L
                );
        if (dbWarehouse == null || dbWarehouse.isEmpty()) {
            throw new BadRequestException("Warehouse not found! ");
        }
        InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
        dbInboundIntegrationLog.setLanguageId("EN");
        dbInboundIntegrationLog.setCompanyCodeId(inbound.getCompanyCodeId());
        dbInboundIntegrationLog.setPlantId(inbound.getPlantId());
        dbInboundIntegrationLog.setWarehouseId(dbWarehouse.get().getWarehouseId());
        dbInboundIntegrationLog.setIntegrationLogNumber(String.valueOf(System.currentTimeMillis()));
        dbInboundIntegrationLog.setRefDocNumber(inbound.getPartnerCode());
        dbInboundIntegrationLog.setOrderReceiptDate(new Date());
        dbInboundIntegrationLog.setIntegrationStatus("FAILED");
        dbInboundIntegrationLog.setOrderReceiptDate(new Date());
        dbInboundIntegrationLog.setDeletionIndicator(0L);
        dbInboundIntegrationLog.setCreatedBy("MSD_API");
        dbInboundIntegrationLog.setCreatedOn(new Date());
        dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
        log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        return dbInboundIntegrationLog;
    }

}