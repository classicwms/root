package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.AddBillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.FindBillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.UpdateBillingFormatId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.BillingFormatIdRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.BillingFormatIdSpecification;
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
public class BillingFormatIdService{
    @Autowired
    private CompanyIdRepository companyIdRepository;

    @Autowired
    private PlantIdRepository plantIdRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private BillingFormatIdRepository billingFormatIdRepository;

    @Autowired
    private WarehouseService warehouseService;
    /**
     * getBillingFormatIds
     * @return
     */
    public List<BillingFormatId> getBillingFormatIds(){
        List<BillingFormatId>billingFormatIdList=billingFormatIdRepository.findAll();
        billingFormatIdList=billingFormatIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<BillingFormatId> newBillingFormatId=new ArrayList<>();
        for(BillingFormatId dbBillingFormatId:billingFormatIdList) {
            if (dbBillingFormatId.getCompanyIdAndDescription() != null&&dbBillingFormatId.getPlantIdAndDescription()!=null&&dbBillingFormatId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBillingFormatId.getCompanyCodeId(), dbBillingFormatId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBillingFormatId.getPlantId(), dbBillingFormatId.getLanguageId(), dbBillingFormatId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBillingFormatId.getWarehouseId(), dbBillingFormatId.getLanguageId(), dbBillingFormatId.getCompanyCodeId(), dbBillingFormatId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingFormatId.add(dbBillingFormatId);
        }
        return newBillingFormatId;
    }
    /**
     * getBillingFormatId
     * @param billFormatId
     * @return
     */
        public BillingFormatId getBillingFormatId(String warehouseId,Long billFormatId,String companyCodeId,String languageId,String plantId){
        Optional<BillingFormatId> dbBillingFormatId=
                billingFormatIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFormatIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        billFormatId,
                        languageId,
                        0l
                );
        if(dbBillingFormatId.isEmpty()){
            throw new BadRequestException("The Given Values:"+
                    "warehouseId"+warehouseId+
                    "billFormatId"+billFormatId+
                    "doesn't exist.");
        }
            BillingFormatId newBillingFormatId = new BillingFormatId();
            BeanUtils.copyProperties(dbBillingFormatId.get(),newBillingFormatId, CommonUtils.getNullPropertyNames(dbBillingFormatId));
            IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
            IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
            IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
            newBillingFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId()+"-"+iKeyValuePair.getDescription());
            newBillingFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId()+"-"+iKeyValuePair1.getDescription());
            newBillingFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId()+"-"+iKeyValuePair2.getDescription());
            return newBillingFormatId;
    }
    /**
     * createBillingFormatId
     * @param newBillingFormatId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingFormatId createBillingFormatId(AddBillingFormatId newBillingFormatId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingFormatId dbBillingFormatId = new BillingFormatId();
        Optional<BillingFormatId> duplicateBillingFormatId = billingFormatIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFormatIdAndLanguageIdAndDeletionIndicator(newBillingFormatId.getCompanyCodeId(), newBillingFormatId.getPlantId(), newBillingFormatId.getWarehouseId(), newBillingFormatId.getBillFormatId(), newBillingFormatId.getLanguageId(), 0L);
        if (!duplicateBillingFormatId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newBillingFormatId.getWarehouseId(), newBillingFormatId.getCompanyCodeId(), newBillingFormatId.getPlantId(), newBillingFormatId.getLanguageId());
            log.info("newBillingFormatId:" + newBillingFormatId);
            BeanUtils.copyProperties(newBillingFormatId, dbBillingFormatId, CommonUtils.getNullPropertyNames(newBillingFormatId));
            dbBillingFormatId.setDeletionIndicator(0L);
            dbBillingFormatId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbBillingFormatId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbBillingFormatId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbBillingFormatId.setCreatedBy(loginUserID);
            dbBillingFormatId.setUpdatedBy(loginUserID);
            dbBillingFormatId.setCreatedOn(new Date());
            dbBillingFormatId.setUpdatedOn(new Date());
            return billingFormatIdRepository.save(dbBillingFormatId);
        }
    }
    /**
     * updateBillingFormatId
     * @param loginUserID
     * @param billFormatId
     * @param updateBillingFormatId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public BillingFormatId updateBillingFormatId(String warehouseId, Long billFormatId,String companyCodeId,String languageId,
                                                 String plantId,String loginUserID,
                                             UpdateBillingFormatId updateBillingFormatId) throws IllegalAccessException, InvocationTargetException, ParseException {
        BillingFormatId dbBillingFormatId=getBillingFormatId(warehouseId,billFormatId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateBillingFormatId,dbBillingFormatId,CommonUtils.getNullPropertyNames(updateBillingFormatId));
        dbBillingFormatId.setUpdatedBy(loginUserID);
        dbBillingFormatId.setUpdatedOn(new Date());
        return billingFormatIdRepository.save(dbBillingFormatId);
    }
    /**
     * deleteBillingFormatId
     * @param loginUserID
     * @param billFormatId
     */
    public void deleteBillingFormatId(String warehouseId,Long billFormatId,String companyCodeId,
                                      String languageId,String plantId,String loginUserID){
        BillingFormatId dbBillingFormatId=getBillingFormatId(warehouseId,billFormatId,companyCodeId,languageId,plantId);
        if(dbBillingFormatId !=null){
            dbBillingFormatId.setDeletionIndicator(1l);
            dbBillingFormatId.setUpdatedBy(loginUserID);
            billingFormatIdRepository.save(dbBillingFormatId);
        }
        else{
            throw new EntityNotFoundException("Error in deleting Id:"+billFormatId);
        }
    }
    //Find BillingFormatId

    public List<BillingFormatId> findBillingFormatId(FindBillingFormatId findBillingFormatId) throws ParseException {

        BillingFormatIdSpecification spec = new BillingFormatIdSpecification(findBillingFormatId);
        List<BillingFormatId> results = billingFormatIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<BillingFormatId> newBillingFormatId=new ArrayList<>();
        for(BillingFormatId dbBillingFormatId:results) {
            if (dbBillingFormatId.getCompanyIdAndDescription() != null&&dbBillingFormatId.getPlantIdAndDescription()!=null&&dbBillingFormatId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBillingFormatId.getCompanyCodeId(), dbBillingFormatId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBillingFormatId.getPlantId(), dbBillingFormatId.getLanguageId(), dbBillingFormatId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBillingFormatId.getWarehouseId(), dbBillingFormatId.getLanguageId(), dbBillingFormatId.getCompanyCodeId(), dbBillingFormatId.getPlantId());
                if (iKeyValuePair != null) {
                    dbBillingFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbBillingFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbBillingFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newBillingFormatId.add(dbBillingFormatId);
        }
        return newBillingFormatId;
    }
}
