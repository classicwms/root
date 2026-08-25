package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.PaymentModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.AddPaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.FindPaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.PaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.UpdatePaymentTermId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.PaymentTermIdSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentTermIdService{
    @Autowired
    private PaymentTermIdRepository paymentTermIdRepository;

    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private WarehouseService warehouseService;
    /**
     * getPaymentTermIds
     * @return
     */
    public List<PaymentTermId> getPaymentTermIds(){
        List<PaymentTermId>paymentTermIdList=paymentTermIdRepository.findAll();
        paymentTermIdList=paymentTermIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<PaymentTermId> newPaymentTermId=new ArrayList<>();
        for(PaymentTermId dbPaymentTermId:paymentTermIdList) {
            if (dbPaymentTermId.getCompanyIdAndDescription() != null&&dbPaymentTermId.getPlantIdAndDescription()!=null&&dbPaymentTermId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbPaymentTermId.getCompanyCodeId(), dbPaymentTermId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbPaymentTermId.getPlantId(), dbPaymentTermId.getLanguageId(), dbPaymentTermId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbPaymentTermId.getWarehouseId(), dbPaymentTermId.getLanguageId(), dbPaymentTermId.getCompanyCodeId(), dbPaymentTermId.getPlantId());
                if (iKeyValuePair != null) {
                    dbPaymentTermId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPaymentTermId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPaymentTermId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newPaymentTermId.add(dbPaymentTermId);
        }
        return newPaymentTermId;
    }
    /**
     * getPaymentTermId
     * @param paymentTermId
     * @return
     */
    public PaymentTermId getPaymentTermId(String warehouseId,Long paymentTermId,String companyCodeId,String languageId,String plantId){
        Optional<PaymentTermId> dbPaymentTermId=
                paymentTermIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentTermIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        paymentTermId,
                        languageId,
                        0l
                );
        if(dbPaymentTermId.isEmpty()){
            throw new BadRequestException("The Given Values:"+
                    "WarehouseId"+warehouseId+
                    "PaymentTermId"+paymentTermId+
                    "doesn't exist.");
        }
        PaymentTermId newPaymentTermId = new PaymentTermId();
        BeanUtils.copyProperties(dbPaymentTermId.get(),newPaymentTermId, CommonUtils.getNullPropertyNames(dbPaymentTermId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
        if(iKeyValuePair!=null) {
            newPaymentTermId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if(iKeyValuePair1!=null) {
            newPaymentTermId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if(iKeyValuePair2!=null) {
            newPaymentTermId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        return newPaymentTermId;
    }
    /**
     * createPaymentTermId
     * @param newPaymentTermId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PaymentTermId createPaymentTermId(AddPaymentTermId newPaymentTermId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PaymentTermId dbPaymentTermId = new PaymentTermId();
        Optional<PaymentTermId> duplicatePaymentTermId = paymentTermIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentTermIdAndLanguageIdAndDeletionIndicator(newPaymentTermId.getCompanyCodeId(), newPaymentTermId.getPlantId(), newPaymentTermId.getWarehouseId(), newPaymentTermId.getPaymentTermId(), newPaymentTermId.getLanguageId(), 0L);
        if (!duplicatePaymentTermId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newPaymentTermId.getWarehouseId(), newPaymentTermId.getCompanyCodeId(), newPaymentTermId.getPlantId(), newPaymentTermId.getLanguageId());
            log.info("newPaymentTermId:" + newPaymentTermId);
            BeanUtils.copyProperties(newPaymentTermId, dbPaymentTermId, CommonUtils.getNullPropertyNames(newPaymentTermId));
            dbPaymentTermId.setDeletionIndicator(0L);
            dbPaymentTermId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbPaymentTermId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbPaymentTermId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbPaymentTermId.setCreatedBy(loginUserID);
            dbPaymentTermId.setUpdatedBy(loginUserID);
            dbPaymentTermId.setCreatedOn(new Date());
            dbPaymentTermId.setUpdatedOn(new Date());
            return paymentTermIdRepository.save(dbPaymentTermId);
        }
    }
    /**
     * updatePaymentTermId
     * @param loginUserID
     * @param paymentTermId
     * @param updatePaymentTermId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PaymentTermId updatePaymentTermId(String warehouseId, Long paymentTermId,String companyCodeId,String languageId,String plantId,String loginUserID,
                                     UpdatePaymentTermId updatePaymentTermId) throws IllegalAccessException, InvocationTargetException, ParseException {
        PaymentTermId dbPaymentTermId=getPaymentTermId(warehouseId,paymentTermId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updatePaymentTermId,dbPaymentTermId,CommonUtils.getNullPropertyNames(updatePaymentTermId));
        dbPaymentTermId.setUpdatedBy(loginUserID);
        dbPaymentTermId.setUpdatedOn(new Date());
        return paymentTermIdRepository.save(dbPaymentTermId);
    }
    /**
     * deletePaymentTermId
     * @param loginUserID
     * @param paymentTermId
     */
    public void deletePaymentTermId(String warehouseId,Long paymentTermId,String companyCodeId,String languageId,String plantId,String loginUserID){
        PaymentTermId dbPaymentTermId=getPaymentTermId(warehouseId,paymentTermId,companyCodeId,languageId,plantId);
        if(dbPaymentTermId !=null){
            dbPaymentTermId.setDeletionIndicator(1l);
            dbPaymentTermId.setUpdatedBy(loginUserID);
            paymentTermIdRepository.save(dbPaymentTermId);
        }
        else{
            throw new EntityNotFoundException("Error in deleting Id:"+paymentTermId);
        }
    }
    //Find PaymentTermId

    public List<PaymentTermId> findPaymentTermId(FindPaymentTermId findPaymentTermId) throws ParseException {

        PaymentTermIdSpecification spec = new PaymentTermIdSpecification(findPaymentTermId);
        List<PaymentTermId> results = paymentTermIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<PaymentTermId> newPaymentTermId=new ArrayList<>();
        for(PaymentTermId dbPaymentTermId:results) {
            if (dbPaymentTermId.getCompanyIdAndDescription() != null&&dbPaymentTermId.getPlantIdAndDescription()!=null&&dbPaymentTermId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbPaymentTermId.getCompanyCodeId(), dbPaymentTermId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbPaymentTermId.getPlantId(), dbPaymentTermId.getLanguageId(), dbPaymentTermId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbPaymentTermId.getWarehouseId(), dbPaymentTermId.getLanguageId(), dbPaymentTermId.getCompanyCodeId(), dbPaymentTermId.getPlantId());
                if (iKeyValuePair != null) {
                    dbPaymentTermId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPaymentTermId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPaymentTermId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newPaymentTermId.add(dbPaymentTermId);
        }
        return newPaymentTermId;
    }
}
