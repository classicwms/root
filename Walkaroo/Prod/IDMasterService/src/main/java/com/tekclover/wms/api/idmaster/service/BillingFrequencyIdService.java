package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.AddBillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.BillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.FindBillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.UpdateBillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.BillingFrequencyIdRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.BillingFrequencyIdSpecification;
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
public class BillingFrequencyIdService{
    @Autowired
    private BillingFrequencyIdRepository billingFrequencyIdRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseService warehouseService;
    /**
     * getBillingFrequencyIds
     * @return
     */
    public List<BillingFrequencyId> getBillingFrequencyIds(){
        List<BillingFrequencyId>billingFrequencyIdList=billingFrequencyIdRepository.findAll();
        billingFrequencyIdList=billingFrequencyIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<BillingFrequencyId> newBillingFrequencyId=new ArrayList<>();
        for(BillingFrequencyId dbBillingFrequencyId:billingFrequencyIdList) {
            if (dbBillingFrequencyId.getCompanyIdAndDescription() != null&&dbBillingFrequencyId.getPlantIdAndDescription()!=null&&dbBillingFrequencyId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBillingFrequencyId.getCompanyCodeId(), dbBillingFrequencyId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBillingFrequencyId.getPlantId(), dbBillingFrequencyId.getLanguageId(), dbBillingFrequencyId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBillingFrequencyId.getWarehouseId(), dbBillingFrequencyId.getLanguageId(), dbBillingFrequencyId.getCompanyCodeId(), dbBillingFrequencyId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingFrequencyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingFrequencyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingFrequencyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingFrequencyId.add(dbBillingFrequencyId);
        }
        return newBillingFrequencyId;
    }
    /**
     * getBillingFrequencyId
     * @param billFrequencyId
     * @return
     */
    public BillingFrequencyId getBillingFrequencyId(String warehouseId,Long billFrequencyId,String companyCodeId,String languageId,String plantId){
        Optional<BillingFrequencyId> dbBillingFrequencyId=
                billingFrequencyIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFrequencyIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        billFrequencyId,
                        languageId,
                        0l
                );
        if(dbBillingFrequencyId.isEmpty()){
            throw new BadRequestException("The Given Values:"+
                    "warehouseId"+warehouseId+
                    "billFrequencyId"+billFrequencyId+
                    "doesn't exist.");
        }
        BillingFrequencyId newBillingFrequencyId = new BillingFrequencyId();
        BeanUtils.copyProperties(dbBillingFrequencyId.get(),newBillingFrequencyId, CommonUtils.getNullPropertyNames(dbBillingFrequencyId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
      if(iKeyValuePair!=null) {
          newBillingFrequencyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
      }
      if(iKeyValuePair1!=null) {
          newBillingFrequencyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
      }
      if(iKeyValuePair2!=null) {
          newBillingFrequencyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
      }
        return newBillingFrequencyId;
    }
    /**
     * createBillingFrequencyId
     * @param newBillingFrequencyId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingFrequencyId createBillingFrequencyId(AddBillingFrequencyId newBillingFrequencyId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingFrequencyId dbBillingFrequencyId = new BillingFrequencyId();
        Optional<BillingFrequencyId> duplicateBillingFrequencyId = billingFrequencyIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFrequencyIdAndLanguageIdAndDeletionIndicator(newBillingFrequencyId.getCompanyCodeId(), newBillingFrequencyId.getPlantId(), newBillingFrequencyId.getWarehouseId(), newBillingFrequencyId.getBillFrequencyId(), newBillingFrequencyId.getLanguageId(), 0L);
        if (!duplicateBillingFrequencyId.isEmpty()) {
            throw new EntityNotFoundException("Record id Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newBillingFrequencyId.getWarehouseId(), newBillingFrequencyId.getCompanyCodeId(), newBillingFrequencyId.getPlantId(), newBillingFrequencyId.getLanguageId());
            log.info("newBillingFrequencyId:" + newBillingFrequencyId);
            BeanUtils.copyProperties(newBillingFrequencyId, dbBillingFrequencyId, CommonUtils.getNullPropertyNames(newBillingFrequencyId));
            dbBillingFrequencyId.setDeletionIndicator(0L);
            dbBillingFrequencyId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbBillingFrequencyId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbBillingFrequencyId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbBillingFrequencyId.setCreatedBy(loginUserID);
            dbBillingFrequencyId.setUpdatedBy(loginUserID);
            dbBillingFrequencyId.setCreatedOn(new Date());
            dbBillingFrequencyId.setUpdatedOn(new Date());
            return billingFrequencyIdRepository.save(dbBillingFrequencyId);
        }
    }
    /**
     * updateBillingFrequencyId
     * @param loginUserID
     * @param billFrequencyId
     * @param updateBillingFrequencyId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingFrequencyId updateBillingFrequencyId(String warehouseId, Long billFrequencyId,String companyCodeId,String languageId,String plantId,String loginUserID,
                                             UpdateBillingFrequencyId updateBillingFrequencyId) throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingFrequencyId dbBillingFrequencyId=getBillingFrequencyId(warehouseId,billFrequencyId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateBillingFrequencyId,dbBillingFrequencyId,CommonUtils.getNullPropertyNames(updateBillingFrequencyId));
        dbBillingFrequencyId.setUpdatedBy(loginUserID);
        dbBillingFrequencyId.setUpdatedOn(new Date());
        return billingFrequencyIdRepository.save(dbBillingFrequencyId);
    }
    /**
     * deleteBillingFrequencyId
     * @param loginUserID
     * @param billFrequencyId
     */
    public void deleteBillingFrequencyId(String warehouseId,Long billFrequencyId,String companyCodeId,String languageId,String plantId,String loginUserID){
        BillingFrequencyId dbBillingFrequencyId=getBillingFrequencyId(warehouseId,billFrequencyId,companyCodeId,languageId,plantId);
        if(dbBillingFrequencyId !=null){
            dbBillingFrequencyId.setDeletionIndicator(1l);
            dbBillingFrequencyId.setUpdatedBy(loginUserID);
            billingFrequencyIdRepository.save(dbBillingFrequencyId);
        }
        else{
            throw new EntityNotFoundException("Error in deleting Id:"+billFrequencyId);
        }
    }
    //Find BillingFrequencyId

    public List<BillingFrequencyId> findBillingFrequencyId(FindBillingFrequencyId findBillingFrequencyId) throws ParseException {

        BillingFrequencyIdSpecification spec = new BillingFrequencyIdSpecification(findBillingFrequencyId);
        List<BillingFrequencyId> results = billingFrequencyIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<BillingFrequencyId> newBillingFrequencyId=new ArrayList<>();
        for(BillingFrequencyId dbBillingFrequencyId:results) {
            if (dbBillingFrequencyId.getCompanyIdAndDescription() != null&&dbBillingFrequencyId.getPlantIdAndDescription()!=null&&dbBillingFrequencyId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBillingFrequencyId.getCompanyCodeId(), dbBillingFrequencyId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBillingFrequencyId.getPlantId(), dbBillingFrequencyId.getLanguageId(), dbBillingFrequencyId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBillingFrequencyId.getWarehouseId(), dbBillingFrequencyId.getLanguageId(),dbBillingFrequencyId.getCompanyCodeId(), dbBillingFrequencyId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingFrequencyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingFrequencyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingFrequencyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingFrequencyId.add(dbBillingFrequencyId);
        }
        return newBillingFrequencyId;
    }
}
