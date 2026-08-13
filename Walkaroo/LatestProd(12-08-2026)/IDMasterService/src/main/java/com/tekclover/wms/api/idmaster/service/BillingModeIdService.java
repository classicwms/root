package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.BillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.AddBillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.BillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.FindBillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.UpdateBillingModeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.BillingModeIdRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.BillingModeIdSpecification;
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

@Service
@Slf4j
public class BillingModeIdService {
    @Autowired
    private BillingModeIdRepository billingModeIdRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private WarehouseService warehouseService;
    /**
     * getBillingModeIds
     * @return
     */
    public List<BillingModeId> getBillingModeIds(){
        List<BillingModeId>billingModeIdList=billingModeIdRepository.findAll();
        billingModeIdList=billingModeIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<BillingModeId> newBillingModeId=new ArrayList<>();
        for(BillingModeId dbBillingModeId:billingModeIdList) {
            if (dbBillingModeId.getCompanyIdAndDescription() != null&&dbBillingModeId.getPlantIdAndDescription()!=null&&dbBillingModeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBillingModeId.getCompanyCodeId(), dbBillingModeId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBillingModeId.getPlantId(), dbBillingModeId.getLanguageId(), dbBillingModeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBillingModeId.getWarehouseId(), dbBillingModeId.getLanguageId(), dbBillingModeId.getCompanyCodeId(), dbBillingModeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingModeId.add(dbBillingModeId);
        }
        return newBillingModeId;
    }
    /**
     * getBillingModeId
     * @param billModeId
     * @return
     */
    public BillingModeId getBillingModeId(String warehouseId,Long billModeId,String companyCodeId,String languageId,String plantId){
        Optional<BillingModeId> dbBillingModeId=
                billingModeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillModeIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        billModeId,
                        languageId,
                        0l
                );
        if(dbBillingModeId.isEmpty()){
            throw new BadRequestException("The Given Values:"+
                    "warehouseId"+warehouseId+
                    "billModeId"+billModeId+
                    "doesn't exist.");
        }
        BillingModeId newBillingModeId = new BillingModeId();
        BeanUtils.copyProperties(dbBillingModeId.get(),newBillingModeId, CommonUtils.getNullPropertyNames(dbBillingModeId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
       if(iKeyValuePair!=null) {
           newBillingModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
       }
       if(iKeyValuePair1!=null) {
           newBillingModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
       }
       if(iKeyValuePair2!=null) {
           newBillingModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
       }
        return newBillingModeId;
    }
    /**
     * createBillingModeId
     * @param newBillingModeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingModeId createBillingModeId(AddBillingModeId newBillingModeId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingModeId dbBillingModeId = new BillingModeId();
        log.info("newBillingModeId:" + newBillingModeId);
        Optional<BillingModeId> duplicateBillingModeId = billingModeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillModeIdAndLanguageIdAndDeletionIndicator(newBillingModeId.getCompanyCodeId(), newBillingModeId.getPlantId(), newBillingModeId.getWarehouseId(), newBillingModeId.getBillModeId(), newBillingModeId.getLanguageId(), 0L);
        if (!duplicateBillingModeId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newBillingModeId.getWarehouseId(), newBillingModeId.getCompanyCodeId(), newBillingModeId.getPlantId(), newBillingModeId.getLanguageId());
            BeanUtils.copyProperties(newBillingModeId, dbBillingModeId, CommonUtils.getNullPropertyNames(newBillingModeId));
            dbBillingModeId.setDeletionIndicator(0L);
            dbBillingModeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbBillingModeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbBillingModeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbBillingModeId.setCreatedBy(loginUserID);
            dbBillingModeId.setUpdatedBy(loginUserID);
            dbBillingModeId.setCreatedOn(new Date());
            dbBillingModeId.setUpdatedOn(new Date());
            return billingModeIdRepository.save(dbBillingModeId);
        }
    }
    /**
     * updateBillingModeId
     * @param loginUserID
     * @param billModeId
     * @param updateBillingModeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingModeId updateBillingModeId(String warehouseId, Long billModeId,String companyCodeId,String languageId,String plantId, String loginUserID,
                                             UpdateBillingModeId updateBillingModeId) throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingModeId dbBillingModeId=getBillingModeId(warehouseId,billModeId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateBillingModeId,dbBillingModeId,CommonUtils.getNullPropertyNames(updateBillingModeId));
        dbBillingModeId.setUpdatedBy(loginUserID);
        dbBillingModeId.setUpdatedOn(new Date());
        return billingModeIdRepository.save(dbBillingModeId);
    }
    /**
     * deleteBillingModeId
     * @param loginUserID
     * @param billModeId
     */
    public void deleteBillingModeId(String warehouseId,Long billModeId,String companyCodeId,String languageId,String plantId,String loginUserID){
        BillingModeId dbBillingModeId=getBillingModeId(warehouseId,billModeId,companyCodeId,languageId,plantId);
        if(dbBillingModeId !=null){
            dbBillingModeId.setDeletionIndicator(1l);
            dbBillingModeId.setUpdatedBy(loginUserID);
            billingModeIdRepository.save(dbBillingModeId);
        }
        else{
            throw new EntityNotFoundException("Error in deleting Id:"+billModeId);
        }
    }
    //Find BillingModeId

    public List<BillingModeId> findBillingModeId(FindBillingModeId findBillingModeId) throws ParseException {

        BillingModeIdSpecification spec = new BillingModeIdSpecification(findBillingModeId);
        List<BillingModeId> results = billingModeIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<BillingModeId> newBillingModeId=new ArrayList<>();
        for(BillingModeId dbBillingModeId:results) {
            if (dbBillingModeId.getCompanyIdAndDescription() != null&&dbBillingModeId.getPlantIdAndDescription()!=null&&dbBillingModeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBillingModeId.getCompanyCodeId(), dbBillingModeId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBillingModeId.getPlantId(), dbBillingModeId.getLanguageId(), dbBillingModeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBillingModeId.getWarehouseId(), dbBillingModeId.getLanguageId(), dbBillingModeId.getCompanyCodeId(), dbBillingModeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingModeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingModeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingModeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingModeId.add(dbBillingModeId);
        }
        return newBillingModeId;
    }
}
