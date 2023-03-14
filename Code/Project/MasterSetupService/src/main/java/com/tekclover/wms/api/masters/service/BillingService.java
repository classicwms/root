package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.threepl.billing.*;
import com.tekclover.wms.api.masters.repository.BillingRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service

public class BillingService extends BaseService {

    @Autowired
    private BillingRepository billingRepository;

    /**
     * getBillings
     * @return
     */
    public List<Billing> getBillings () {
        List<Billing> BillingList =  billingRepository.findAll();
        BillingList = BillingList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return BillingList;
    }

    /**
     * getBilling
     * @param partnerCode
     * @return
     */
    public Billing getBilling (String warehouseId, Long module, String partnerCode) {
        Optional<Billing> dbBilling =
                billingRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndModuleAndLanguageIdAndDeletionIndicator(
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        partnerCode,
                        module,
                        getLanguageId(),
                        0L
                );
        if (dbBilling.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "module - " + module +
                    "partnerCode - " + partnerCode +
                    "doesn't exist.");

        }
        return dbBilling.get();
    }

    /**
     * createBilling
     * @param newBilling
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Billing createBilling (AddBilling newBilling, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Billing dbBilling = new Billing();
        log.info("newBilling : " + newBilling);
        BeanUtils.copyProperties(newBilling, dbBilling, CommonUtils.getNullPropertyNames(newBilling));
        dbBilling.setCompanyCodeId(getCompanyCode());
        dbBilling.setPlantId(getPlantId());
        dbBilling.setDeletionIndicator(0L);
        dbBilling.setCreatedBy(loginUserID);
        dbBilling.setUpdatedBy(loginUserID);
        dbBilling.setCreatedOn(new Date());
        dbBilling.setUpdatedOn(new Date());
        return billingRepository.save(dbBilling);
    }

    /**
     * updateBilling
     * @param loginUserID
     * @param partnerCode
     * @param updateBilling
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Billing updateBilling(String warehouseId, Long module,String partnerCode, String loginUserID,
                                      UpdateBilling updateBilling)
            throws IllegalAccessException, InvocationTargetException {
        Billing dbBilling = getBilling(warehouseId, module, partnerCode);
        BeanUtils.copyProperties(updateBilling, dbBilling, CommonUtils.getNullPropertyNames(updateBilling));
        dbBilling.setUpdatedBy(loginUserID);
        dbBilling.setUpdatedOn(new Date());
        return billingRepository.save(dbBilling);
    }

    /**
     * deleteBilling
     * @param loginUserID
     * @param partnerCode
     */
    public void deleteBilling(String warehouseId,Long module,String partnerCode, String loginUserID) {
        Billing dbBilling = getBilling(warehouseId, module, partnerCode);
        if ( dbBilling != null) {
            dbBilling.setDeletionIndicator(1L);
            dbBilling.setUpdatedBy(loginUserID);
            billingRepository.save(dbBilling);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + partnerCode);
        }
    }
}
