package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.BillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.AddPaymentModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.FindPaymentModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.PaymentModeId;
import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.UpdatePaymentModeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PaymentModeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.PaymentModeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
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
public class PaymentModeIdService{
    @Autowired
    private PaymentModeIdRepository paymentModeIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private WarehouseService warehouseService;

    /**
     * getPaymentModeIds
     * @return
     */
    public List<PaymentModeId> getPaymentModeIds(){
        List<PaymentModeId>paymentModeIdList=paymentModeIdRepository.findAll();
        paymentModeIdList=paymentModeIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<PaymentModeId> newPaymentModeId=new ArrayList<>();
        for(PaymentModeId dbPaymentModeId:paymentModeIdList) {
            if (dbPaymentModeId.getCompanyIdAndDescription() != null&&dbPaymentModeId.getPlantIdAndDescription()!=null&&dbPaymentModeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbPaymentModeId.getCompanyCodeId(), dbPaymentModeId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbPaymentModeId.getPlantId(), dbPaymentModeId.getLanguageId(), dbPaymentModeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbPaymentModeId.getWarehouseId(), dbPaymentModeId.getLanguageId(), dbPaymentModeId.getCompanyCodeId(), dbPaymentModeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbPaymentModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPaymentModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPaymentModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newPaymentModeId.add(dbPaymentModeId);
        }
        return newPaymentModeId;
    }
    /**
     * getPaymentModeId
     * @param paymentModeId
     * @return
     */
    public PaymentModeId getPaymentModeId(String warehouseId,Long paymentModeId,String companyCodeId,String languageId,String plantId){
        Optional<PaymentModeId> dbPaymentModeId=
                paymentModeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentModeIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        paymentModeId,
                        languageId,
                        0l
                );
        if(dbPaymentModeId.isEmpty()){
            throw new BadRequestException("The Given Values:"+
                    "warehouseId"+warehouseId+
                    "paymentModeId"+paymentModeId+
                    "doesn't exist.");
        }
        PaymentModeId newPaymentModeId = new PaymentModeId();
        BeanUtils.copyProperties(dbPaymentModeId.get(),newPaymentModeId, CommonUtils.getNullPropertyNames(dbPaymentModeId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
        if(iKeyValuePair!=null) {
            newPaymentModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if(iKeyValuePair1!=null) {
            newPaymentModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if(iKeyValuePair2!=null) {
            newPaymentModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        return newPaymentModeId;
    }
    /**
     * createPaymentModeId
     * @param newPaymentModeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PaymentModeId createPaymentModeId(AddPaymentModeId newPaymentModeId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PaymentModeId dbPaymentModeId = new PaymentModeId();
        Optional<PaymentModeId> duplicatePaymentModeId = paymentModeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentModeIdAndLanguageIdAndDeletionIndicator(newPaymentModeId.getCompanyCodeId(), newPaymentModeId.getPlantId(), newPaymentModeId.getWarehouseId(), newPaymentModeId.getPaymentModeId(), newPaymentModeId.getLanguageId(), 0L);
        if (!duplicatePaymentModeId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newPaymentModeId.getWarehouseId(), newPaymentModeId.getCompanyCodeId(), newPaymentModeId.getPlantId(), newPaymentModeId.getLanguageId());
            log.info("newPaymentModeId:" + newPaymentModeId);
            BeanUtils.copyProperties(newPaymentModeId, dbPaymentModeId, CommonUtils.getNullPropertyNames(newPaymentModeId));
            dbPaymentModeId.setDeletionIndicator(0L);
            dbPaymentModeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbPaymentModeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbPaymentModeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbPaymentModeId.setCreatedBy(loginUserID);
            dbPaymentModeId.setUpdatedBy(loginUserID);
            dbPaymentModeId.setCreatedOn(new Date());
            dbPaymentModeId.setUpdatedOn(new Date());
            return paymentModeIdRepository.save(dbPaymentModeId);
        }
    }
    /**
     * updatePaymentModeId
     * @param loginUserID
     * @param paymentModeId
     * @param updatePaymentModeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PaymentModeId updatePaymentModeId(String warehouseId, Long paymentModeId,String companyCodeId,String languageId,String plantId,String loginUserID,
                                             UpdatePaymentModeId updatePaymentModeId) throws IllegalAccessException, InvocationTargetException, ParseException {
        PaymentModeId dbPaymentModeId=getPaymentModeId(warehouseId,paymentModeId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updatePaymentModeId,dbPaymentModeId,CommonUtils.getNullPropertyNames(updatePaymentModeId));
        dbPaymentModeId.setUpdatedBy(loginUserID);
        dbPaymentModeId.setUpdatedOn(new Date());
        return paymentModeIdRepository.save(dbPaymentModeId);
    }
    /**
     * deletePaymentModeId
     * @param loginUserID
     * @param paymentModeId
     */
    public void deletePaymentModeId(String warehouseId,Long paymentModeId,String companyCodeId,String languageId,String plantId,String loginUserID){
        PaymentModeId dbPaymentModeId=getPaymentModeId(warehouseId,paymentModeId,companyCodeId,languageId,plantId);
        if(dbPaymentModeId !=null){
            dbPaymentModeId.setDeletionIndicator(1l);
            dbPaymentModeId.setUpdatedBy(loginUserID);
            paymentModeIdRepository.save(dbPaymentModeId);
        }
        else{
            throw new EntityNotFoundException("Error in deleting Id:"+paymentModeId);
        }
    }
    //Find PaymentModeId

    public List<PaymentModeId> findPaymentModeId(FindPaymentModeId findPaymentModeId) throws ParseException {

        PaymentModeIdSpecification spec = new PaymentModeIdSpecification(findPaymentModeId);
        List<PaymentModeId> results = paymentModeIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<PaymentModeId> newPaymentModeId=new ArrayList<>();
        for(PaymentModeId dbPaymentModeId:results) {
            if (dbPaymentModeId.getCompanyIdAndDescription() != null&&dbPaymentModeId.getPlantIdAndDescription()!=null&&dbPaymentModeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbPaymentModeId.getCompanyCodeId(), dbPaymentModeId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbPaymentModeId.getPlantId(), dbPaymentModeId.getLanguageId(), dbPaymentModeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbPaymentModeId.getWarehouseId(), dbPaymentModeId.getLanguageId(), dbPaymentModeId.getCompanyCodeId(), dbPaymentModeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbPaymentModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPaymentModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPaymentModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newPaymentModeId.add(dbPaymentModeId);
        }
        return newPaymentModeId;
    }
}
