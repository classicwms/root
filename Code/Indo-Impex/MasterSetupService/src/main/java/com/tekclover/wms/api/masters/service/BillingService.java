package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.idmaster.BillingFrequencyId;
import com.tekclover.wms.api.masters.model.idmaster.BillingModeId;
import com.tekclover.wms.api.masters.model.idmaster.PaymentModeId;
import com.tekclover.wms.api.masters.model.idmaster.PaymentTermId;
import com.tekclover.wms.api.masters.model.threepl.billing.AddBilling;
import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
import com.tekclover.wms.api.masters.model.threepl.billing.FindBilling;
import com.tekclover.wms.api.masters.model.threepl.billing.UpdateBilling;
import com.tekclover.wms.api.masters.repository.BillingRepository;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
import com.tekclover.wms.api.masters.repository.specification.BillingSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service

public class BillingService {

    @Autowired
    private IDMasterService idMasterService;

    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private BillingRepository billingRepository;

    /**
     * getBillings
     *
     * @return
     */
    public List<Billing> getBillings() {
        try {
            List<Billing> BillingList = billingRepository.findAll();
            BillingList = BillingList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<Billing> newBilling = new ArrayList<>();
            for (Billing dbBilling : BillingList) {
                if (dbBilling.getCompanyIdAndDescription() != null && dbBilling.getPlantIdAndDescription() != null && dbBilling.getWarehouseIdAndDescription() != null && dbBilling.getPaymentTermIdAndDescription() != null && dbBilling.getPaymentModeIdAndDescription() != null &&
                        dbBilling.getBillFrequencyIdAndDescription() != null && dbBilling.getBillModeIdAndDescription() != null && dbBilling.getModuleIdAndDescription() != null) {

                    IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbBilling.getCompanyCodeId(), dbBilling.getLanguageId());
                    IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbBilling.getPlantId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId());
                    IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbBilling.getWarehouseId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId());
                    IKeyValuePair iKeyValuePair3 = billingRepository.getBillingModeIdAndDescription(dbBilling.getBillModeId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(dbBilling.getModuleId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair5 = billingRepository.getBillFrequencyIdAndDescription(dbBilling.getBillFrequencyId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair6 = billingRepository.getPaymentTermIdAndDescription(dbBilling.getPaymentTermId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair7 = billingRepository.getPaymentModeIdAndDescription(dbBilling.getPaymentModeId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());

                    if (iKeyValuePair != null) {
                        dbBilling.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbBilling.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbBilling.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (iKeyValuePair3 != null) {
                        dbBilling.setBillModeIdAndDescription(iKeyValuePair3.getBillingModeId() + "-" + iKeyValuePair3.getDescription());
                    }
                    if (iKeyValuePair4 != null) {
                        dbBilling.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
                    }
                    if (iKeyValuePair5 != null) {
                        dbBilling.setBillFrequencyIdAndDescription(iKeyValuePair5.getBillFrequencyId() + "-" + iKeyValuePair5.getDescription());
                    }
                    if (iKeyValuePair6 != null) {
                        dbBilling.setPaymentTermIdAndDescription(iKeyValuePair6.getPaymentTermId() + "-" + iKeyValuePair6.getDescription());
                    }
                    if (iKeyValuePair7 != null) {
                        dbBilling.setPaymentModeIdAndDescription(iKeyValuePair7.getPaymentModeId() + "-" + iKeyValuePair7.getDescription());
                    }
                }
                newBilling.add(dbBilling);
            }
            return newBilling;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getBilling
     *
     * @param partnerCode
     * @return
     */
    public Billing getBilling(String warehouseId, String moduleId, String partnerCode, String companyCodeId, String languageId, String plantId) {
        try {
            Optional<Billing> dbBilling =
                    billingRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndModuleIdAndLanguageIdAndDeletionIndicator(
                            companyCodeId,
                            plantId,
                            warehouseId,
                            partnerCode,
                            moduleId,
                            languageId,
                            0L
                    );
            if (dbBilling.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "warehouseId - " + warehouseId +
                        "moduleId - " + moduleId +
                        "partnerCode - " + partnerCode +
                        "doesn't exist.");

            }
            Billing newBilling = new Billing();
            BeanUtils.copyProperties(dbBilling.get(), newBilling, CommonUtils.getNullPropertyNames(dbBilling));

            IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(companyCodeId, languageId);
            IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(plantId, languageId, companyCodeId);
            IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyCodeId, plantId);
            IKeyValuePair iKeyValuePair3 = billingRepository.getBillingModeIdAndDescription(newBilling.getBillModeId(), languageId, companyCodeId, plantId, warehouseId);
            IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(newBilling.getModuleId(), languageId, companyCodeId, plantId, warehouseId);
            IKeyValuePair iKeyValuePair5 = billingRepository.getBillFrequencyIdAndDescription(newBilling.getBillFrequencyId(), languageId, companyCodeId, plantId, warehouseId);
            IKeyValuePair iKeyValuePair6 = billingRepository.getPaymentTermIdAndDescription(newBilling.getPaymentTermId(), languageId, companyCodeId, plantId, warehouseId);
            IKeyValuePair iKeyValuePair7 = billingRepository.getPaymentModeIdAndDescription(newBilling.getPaymentModeId(), languageId, companyCodeId, plantId, warehouseId);

            if (iKeyValuePair != null) {
                newBilling.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
            }
            if (iKeyValuePair1 != null) {
                newBilling.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
            }
            if (iKeyValuePair2 != null) {
                newBilling.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
            }
            if (iKeyValuePair3 != null) {
                newBilling.setBillModeIdAndDescription(iKeyValuePair3.getBillingModeId() + "-" + iKeyValuePair3.getDescription());
            }
            if (iKeyValuePair4 != null) {
                newBilling.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
            }
            if (iKeyValuePair5 != null) {
                newBilling.setBillFrequencyIdAndDescription(iKeyValuePair5.getBillFrequencyId() + "-" + iKeyValuePair5.getDescription());
            }
            if (iKeyValuePair6 != null) {
                newBilling.setPaymentTermIdAndDescription(iKeyValuePair6.getPaymentTermId() + "-" + iKeyValuePair6.getDescription());
            }
            if (iKeyValuePair7 != null) {
                newBilling.setPaymentModeIdAndDescription(iKeyValuePair7.getPaymentModeId() + "-" + iKeyValuePair7.getDescription());
            }

            return newBilling;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createBilling
     *
     * @param newBilling
     * @param loginUserID
     * @return
     */
    public Billing createBilling(AddBilling newBilling, String loginUserID) {
        try {
            Billing dbBilling = new Billing();
            Optional<Billing> duplicateBilling = billingRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndModuleIdAndLanguageIdAndDeletionIndicator(newBilling.getCompanyCodeId(), newBilling.getPlantId(), newBilling.getWarehouseId(), newBilling.getPartnerCode(), newBilling.getModuleId(), newBilling.getLanguageId(), 0L);
            if (!duplicateBilling.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting Duplicated");
            } else {
                AuthToken authTokenForIdMasterService = authTokenService.getIDMasterServiceAuthToken();
                IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(newBilling.getModuleId(), newBilling.getLanguageId(), newBilling.getCompanyCodeId(), newBilling.getPlantId(), newBilling.getWarehouseId());
                //            ModuleId dbModuleId = idMasterService.getModuleId(newBilling.getWarehouseId(), newBilling.getModuleId(), newBilling.getCompanyCodeId(), newBilling.getLanguageId(), newBilling.getPlantId(), authTokenForIdMasterService.getAccess_token());
                BillingModeId dbBillingModeId = idMasterService.getBillingModeId(newBilling.getWarehouseId(), newBilling.getBillModeId(), newBilling.getCompanyCodeId(), newBilling.getLanguageId(), newBilling.getPlantId(), authTokenForIdMasterService.getAccess_token());
                BillingFrequencyId dbBillingFrequencyId = idMasterService.getBillingFrequencyId(newBilling.getWarehouseId(), newBilling.getBillFrequencyId(), newBilling.getCompanyCodeId(), newBilling.getLanguageId(), newBilling.getPlantId(), authTokenForIdMasterService.getAccess_token());
                PaymentTermId dbPaymentTermId = idMasterService.getPaymentTermId(newBilling.getWarehouseId(), newBilling.getPaymentTermId(), newBilling.getCompanyCodeId(), newBilling.getLanguageId(), newBilling.getPlantId(), authTokenForIdMasterService.getAccess_token());
                PaymentModeId dbPaymentModeId = idMasterService.getPaymentModeId(newBilling.getWarehouseId(), newBilling.getPaymentModeId(), newBilling.getCompanyCodeId(), newBilling.getLanguageId(), newBilling.getPlantId(), authTokenForIdMasterService.getAccess_token());

                BeanUtils.copyProperties(newBilling, dbBilling, CommonUtils.getNullPropertyNames(newBilling));

                dbBilling.setCompanyIdAndDescription(dbBillingModeId.getCompanyIdAndDescription());
                dbBilling.setPlantIdAndDescription(dbBillingModeId.getPlantIdAndDescription());
                dbBilling.setWarehouseIdAndDescription(dbBillingModeId.getWarehouseIdAndDescription());
                dbBilling.setBillModeIdAndDescription(dbBillingModeId.getBillModeId() + "-" + dbBillingModeId.getDescription());
                dbBilling.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
                dbBilling.setBillFrequencyIdAndDescription(dbBillingFrequencyId.getBillFrequencyId() + "-" + dbBillingFrequencyId.getDescription());
                dbBilling.setPaymentTermIdAndDescription(dbPaymentTermId.getPaymentTermId() + "-" + dbPaymentTermId.getDescription());
                dbBilling.setPaymentModeIdAndDescription(dbPaymentModeId.getPaymentModeId() + "-" + dbPaymentModeId.getDescription());

                dbBilling.setDeletionIndicator(0L);
                dbBilling.setCreatedBy(loginUserID);
                dbBilling.setUpdatedBy(loginUserID);
                dbBilling.setCreatedOn(new Date());
                dbBilling.setUpdatedOn(new Date());

                return billingRepository.save(dbBilling);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateBilling
     *
     * @param loginUserID
     * @param partnerCode
     * @param updateBilling
     * @return
     */
    public Billing updateBilling(String warehouseId, String moduleId, String partnerCode, String companyCodeId,
                                 String languageId, String plantId, String loginUserID, UpdateBilling updateBilling) {
        try {
            Billing dbBilling = getBilling(warehouseId, moduleId, partnerCode, companyCodeId, languageId, plantId);
            BeanUtils.copyProperties(updateBilling, dbBilling, CommonUtils.getNullPropertyNames(updateBilling));
            dbBilling.setUpdatedBy(loginUserID);
            dbBilling.setUpdatedOn(new Date());
            return billingRepository.save(dbBilling);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteBilling
     *
     * @param loginUserID
     * @param partnerCode
     */
    public void deleteBilling(String warehouseId, String moduleId, String partnerCode, String companyCodeId,
                              String languageId, String plantId, String loginUserID) {
        try {
            Billing dbBilling = getBilling(warehouseId, moduleId, partnerCode, companyCodeId, languageId, plantId);
            if (dbBilling != null) {
                dbBilling.setDeletionIndicator(1L);
                dbBilling.setUpdatedBy(loginUserID);
                billingRepository.save(dbBilling);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + partnerCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //Find Billing
    public List<Billing> findBilling(FindBilling findBilling) {

        try {
            BillingSpecification spec = new BillingSpecification(findBilling);
            List<Billing> results = billingRepository.findAll(spec);
            results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            log.info("results: " + results);
            List<Billing> newBilling = new ArrayList<>();
            for (Billing dbBilling : results) {
                if (dbBilling.getCompanyIdAndDescription() != null && dbBilling.getPlantIdAndDescription() != null && dbBilling.getWarehouseIdAndDescription() != null && dbBilling.getPaymentTermIdAndDescription() != null && dbBilling.getPaymentModeIdAndDescription() != null && dbBilling.getBillFrequencyIdAndDescription() != null && dbBilling.getBillModeIdAndDescription() != null && dbBilling.getModuleIdAndDescription() != null) {
                    IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbBilling.getCompanyCodeId(), dbBilling.getLanguageId());
                    IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbBilling.getPlantId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId());
                    IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbBilling.getWarehouseId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId());
                    IKeyValuePair iKeyValuePair3 = billingRepository.getBillingModeIdAndDescription(dbBilling.getBillModeId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(dbBilling.getModuleId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair5 = billingRepository.getBillFrequencyIdAndDescription(dbBilling.getBillFrequencyId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair6 = billingRepository.getPaymentTermIdAndDescription(dbBilling.getPaymentTermId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    IKeyValuePair iKeyValuePair7 = billingRepository.getPaymentModeIdAndDescription(dbBilling.getPaymentModeId(), dbBilling.getLanguageId(), dbBilling.getCompanyCodeId(), dbBilling.getPlantId(), dbBilling.getWarehouseId());
                    if (iKeyValuePair != null) {
                        dbBilling.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                    }
                    if (iKeyValuePair1 != null) {
                        dbBilling.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                    }
                    if (iKeyValuePair2 != null) {
                        dbBilling.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                    }
                    if (iKeyValuePair3 != null) {
                        dbBilling.setBillModeIdAndDescription(iKeyValuePair3.getBillingModeId() + "-" + iKeyValuePair3.getDescription());
                    }
                    if (iKeyValuePair4 != null) {
                        dbBilling.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
                    }
                    if (iKeyValuePair5 != null) {
                        dbBilling.setBillFrequencyIdAndDescription(iKeyValuePair5.getBillFrequencyId() + "-" + iKeyValuePair5.getDescription());
                    }
                    if (iKeyValuePair6 != null) {
                        dbBilling.setPaymentTermIdAndDescription(iKeyValuePair6.getPaymentTermId() + "-" + iKeyValuePair6.getDescription());
                    }
                    if (iKeyValuePair7 != null) {
                        dbBilling.setPaymentModeIdAndDescription(iKeyValuePair7.getPaymentModeId() + "-" + iKeyValuePair7.getDescription());
                    }
                }
                newBilling.add(dbBilling);
            }
            return newBilling;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}