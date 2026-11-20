package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.PaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.AddServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.FindServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.ServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.UpdateServiceTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ServiceTypeIdSpecification;
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
public class ServiceTypeIdService{
    @Autowired
    private ServiceTypeIdRepository serviceTypeIdRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;

    @Autowired
    private ModuleIdRepository moduleIdRepository;

    @Autowired
    private AdhocModuleIdRepository adhocModuleIdRepository;

    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private ModuleIdService moduleIdService;
    /**
     * getServiceTypeIds
     * @return
     */
    public List<ServiceTypeId> getServiceTypeIds(){
    List<ServiceTypeId> serviceTypeIdList = serviceTypeIdRepository.findAll();
    serviceTypeIdList = serviceTypeIdList.stream().filter(n->n.getDeletionIndicator()==0).collect(Collectors.toList());
        List<ServiceTypeId> newServiceTypeId=new ArrayList<>();
        for(ServiceTypeId dbServiceTypeId:serviceTypeIdList) {
            if (dbServiceTypeId.getCompanyIdAndDescription() != null && dbServiceTypeId.getPlantIdAndDescription()!=null && dbServiceTypeId.getWarehouseIdAndDescription()!=null && dbServiceTypeId.getModuleIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbServiceTypeId.getPlantId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbServiceTypeId.getWarehouseId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getPlantId());
                IKeyValuePair iKeyValuePair3 = moduleIdRepository.getModuleIdAndDescription(dbServiceTypeId.getModuleId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getPlantId(), dbServiceTypeId.getWarehouseId());
                if (iKeyValuePair != null) {
                    dbServiceTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbServiceTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbServiceTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbServiceTypeId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
                }
            }
            newServiceTypeId.add(dbServiceTypeId);
        }
        return newServiceTypeId;
    }
    /**
     * getServiceTypeId
     * @param serviceTypeId
     * @return
     */
    public ServiceTypeId getServiceTypeId(String warehouseId, String moduleId,Long serviceTypeId,String companyCodeId,String languageId,String plantId){
        Optional<ServiceTypeId> dbServiceTypeId=
                serviceTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndServiceTypeIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        moduleId,
                        serviceTypeId,
                        languageId,
                        0L
                );
    if(dbServiceTypeId.isEmpty()){
         throw new BadRequestException("The Given Values:"+
                 "warehouseId"+warehouseId+
                 "moduleId"+moduleId+
                 "serviceTypeId"+serviceTypeId+
                 "doesn't exist.");
    }
        ServiceTypeId newServiceTypeId = new ServiceTypeId();
        BeanUtils.copyProperties(dbServiceTypeId.get(),newServiceTypeId, CommonUtils.getNullPropertyNames(dbServiceTypeId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
        IKeyValuePair iKeyValuePair3=moduleIdRepository.getModuleIdAndDescription(moduleId,languageId,companyCodeId,plantId,warehouseId);
        if(iKeyValuePair!=null) {
            newServiceTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if(iKeyValuePair1!=null) {
            newServiceTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if(iKeyValuePair2!=null) {
            newServiceTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        if(iKeyValuePair3!=null) {
            newServiceTypeId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
        }
        return newServiceTypeId;
    }
    /**
     * createServiceId
     * @param newServiceTypeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   public ServiceTypeId createServiceTypeId(AddServiceTypeId newServiceTypeId, String loginUserID)
           throws IllegalAccessException, InvocationTargetException, ParseException {
       ServiceTypeId dbServiceTypeId = new ServiceTypeId();
       Optional<ServiceTypeId> duplicateServiceTypeId = serviceTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndServiceTypeIdAndLanguageIdAndDeletionIndicator(newServiceTypeId.getCompanyCodeId(), newServiceTypeId.getPlantId(), newServiceTypeId.getWarehouseId(), newServiceTypeId.getModuleId(), newServiceTypeId.getServiceTypeId(), newServiceTypeId.getLanguageId(), 0L);
       if (!duplicateServiceTypeId.isEmpty()) {
           throw new EntityNotFoundException("Record is Getting Duplicated");
       } else {
//           ModuleId dbModuleId=moduleIdService.getModuleId(newServiceTypeId.getWarehouseId(), newServiceTypeId.getModuleId(), newServiceTypeId.getCompanyCodeId(), newServiceTypeId.getLanguageId(), newServiceTypeId.getPlantId());
//           ModuleId dbModuleId=adhocModuleIdRepository.
//                   getModuleIdAndDescription(newServiceTypeId.getWarehouseId(), newServiceTypeId.getModuleId(), newServiceTypeId.getCompanyCodeId(), newServiceTypeId.getLanguageId(), newServiceTypeId.getPlantId());
           Optional<ModuleId> dbModuleId=moduleIdRepository.
                   findTop1ByModuleIdAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
                           newServiceTypeId.getModuleId(),
                           newServiceTypeId.getLanguageId(),
                           newServiceTypeId.getCompanyCodeId(),
                           newServiceTypeId.getPlantId(),
                           newServiceTypeId.getWarehouseId(),0L);
           log.info("newServiceTypeId:" + newServiceTypeId);
           BeanUtils.copyProperties(newServiceTypeId, dbServiceTypeId, CommonUtils.getNullPropertyNames(newServiceTypeId));
           dbServiceTypeId.setDeletionIndicator(0L);
           dbServiceTypeId.setCompanyIdAndDescription(dbModuleId.get().getCompanyIdAndDescription());
           dbServiceTypeId.setPlantIdAndDescription(dbModuleId.get().getPlantIdAndDescription());
           dbServiceTypeId.setWarehouseIdAndDescription(dbModuleId.get().getWarehouseIdAndDescription());
           dbServiceTypeId.setModuleIdAndDescription(dbModuleId.get().getModuleId()+"-"+dbModuleId.get().getModuleDescription());
           dbServiceTypeId.setCreatedBy(loginUserID);
           dbServiceTypeId.setUpdatedBy(loginUserID);
           dbServiceTypeId.setCreatedOn(new Date());
           dbServiceTypeId.setUpdatedOn(new Date());
           return serviceTypeIdRepository.save(dbServiceTypeId);
       }
   }
    /**
     * updateServiceTypeId
     * @param loginUserID
     * @param serviceTypeId
     * @param updateServiceTypeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
   public ServiceTypeId updateServiceTypeId(String warehouseId, String moduleId, Long serviceTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
                                        UpdateServiceTypeId updateServiceTypeId) throws IllegalAccessException, InvocationTargetException, ParseException {
   ServiceTypeId dbServiceTypeId =getServiceTypeId(warehouseId,moduleId,serviceTypeId,companyCodeId,languageId,plantId);
   BeanUtils.copyProperties(updateServiceTypeId, dbServiceTypeId,CommonUtils.getNullPropertyNames(updateServiceTypeId));
   dbServiceTypeId.setUpdatedBy(loginUserID);
   dbServiceTypeId.setUpdatedOn(new Date());
   return serviceTypeIdRepository.save(dbServiceTypeId);
   }
    /**
     * deleteServiceTypeId
     * @param loginUserID
     * @param serviceTypeId
     */
   public void deleteServiceTypeId(String warehouseId,String moduleId,Long serviceTypeId,String companyCodeId,String languageId,String plantId,String loginUserID){
       ServiceTypeId dbserviceTypeId =getServiceTypeId(warehouseId,moduleId,serviceTypeId,companyCodeId,languageId,plantId);
       if(dbserviceTypeId !=null){
          dbserviceTypeId.setDeletionIndicator(1l);
          dbserviceTypeId.setUpdatedBy(loginUserID);
          serviceTypeIdRepository.save(dbserviceTypeId);
       }
       else{
      throw new EntityNotFoundException("Error in deleting Id:"+serviceTypeId);
       }
   }
    //Find ServiceTypeId

    public List<ServiceTypeId> findServiceTypeId(FindServiceTypeId findServiceTypeId) throws ParseException {

        ServiceTypeIdSpecification spec = new ServiceTypeIdSpecification(findServiceTypeId);
        List<ServiceTypeId> results = serviceTypeIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<ServiceTypeId> newServiceTypeId=new ArrayList<>();
        for(ServiceTypeId dbServiceTypeId:results) {
            if (dbServiceTypeId.getCompanyIdAndDescription() != null&&dbServiceTypeId.getPlantIdAndDescription()!=null&&dbServiceTypeId.getWarehouseIdAndDescription()!=null&&dbServiceTypeId.getModuleIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbServiceTypeId.getPlantId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbServiceTypeId.getWarehouseId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getPlantId());
                IKeyValuePair iKeyValuePair3=moduleIdRepository.getModuleIdAndDescription(dbServiceTypeId.getModuleId(), dbServiceTypeId.getLanguageId(), dbServiceTypeId.getCompanyCodeId(), dbServiceTypeId.getPlantId(), dbServiceTypeId.getWarehouseId());
                if (iKeyValuePair != null) {
                    dbServiceTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbServiceTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbServiceTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbServiceTypeId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
                }
            }
            newServiceTypeId.add(dbServiceTypeId);
        }
        return newServiceTypeId;
    }
}
