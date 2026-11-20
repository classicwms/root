package com.tekclover.wms.api.idmaster.service;


import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.AddOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.FindOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.UpdateOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.OutboundOrderTypeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.OutboundOrderTypeIdSpecification;
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
public class OutboundOrderTypeIdService {


    @Autowired
    private  WarehouseService warehouseService;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private OutboundOrderTypeIdRepository outboundOrderTypeIdRepository;

    /**
     * getOutboundOrderTypeIds
     * @return
     */
    public List<OutboundOrderTypeId> getOutboundOrderTypeIds () {
        List<OutboundOrderTypeId> OutboundOrderTypeIdList = outboundOrderTypeIdRepository.findAll();
        OutboundOrderTypeIdList = OutboundOrderTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<OutboundOrderTypeId> newOutboundOrderTypeId=new ArrayList<>();
        for(OutboundOrderTypeId dbOutboundOrderTypeId:OutboundOrderTypeIdList) {
            if (dbOutboundOrderTypeId.getCompanyIdAndDescription() != null&&dbOutboundOrderTypeId.getPlantIdAndDescription()!=null&&dbOutboundOrderTypeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbOutboundOrderTypeId.getCompanyCodeId(),dbOutboundOrderTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbOutboundOrderTypeId.getPlantId(),dbOutboundOrderTypeId.getLanguageId(), dbOutboundOrderTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbOutboundOrderTypeId.getWarehouseId(),dbOutboundOrderTypeId.getLanguageId(), dbOutboundOrderTypeId.getCompanyCodeId(), dbOutboundOrderTypeId.getPlantId());
               if(iKeyValuePair!=null) {
                   dbOutboundOrderTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
               }
               if(iKeyValuePair1!=null) {
                   dbOutboundOrderTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
               }
               if(iKeyValuePair2!=null){
                dbOutboundOrderTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId()+"-"+iKeyValuePair2.getDescription());
            }
            }

            newOutboundOrderTypeId.add(dbOutboundOrderTypeId);
        }
        return newOutboundOrderTypeId;
    }

    /**
     * getOutboundOrderTypeId
     * @param outboundOrderTypeId
     * @return
     */
    public OutboundOrderTypeId getOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId,String companyCodeId,String languageId,String plantId) {
        Optional<OutboundOrderTypeId> dbOutboundOrderTypeId = outboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
                companyCodeId,
                plantId,
                warehouseId,
                outboundOrderTypeId,
                languageId,
                0L
        );
        if (dbOutboundOrderTypeId.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "dbOutboundOrderTypeId - " + outboundOrderTypeId +
                    " doesn't exist.");
        }
        OutboundOrderTypeId newOutboundOrdertypeId = new OutboundOrderTypeId();
        BeanUtils.copyProperties(dbOutboundOrderTypeId.get(),newOutboundOrdertypeId, CommonUtils.getNullPropertyNames(dbOutboundOrderTypeId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
       if(iKeyValuePair!=null) {
           newOutboundOrdertypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
       }
       if(iKeyValuePair1!=null) {
           newOutboundOrdertypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
       }
       if(iKeyValuePair2!=null) {
           newOutboundOrdertypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
       }
       return newOutboundOrdertypeId;
    }
    /**
     * CreateOutboundOrderTypeId
     * @param newOutboundOrderTypeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public OutboundOrderTypeId CreateOutboundOrderTypeId(AddOutboundOrderTypeId newOutboundOrderTypeId, String loginUserID) throws ParseException {
        OutboundOrderTypeId dbOutboundOrderTypeIdId = new OutboundOrderTypeId();
        Optional<OutboundOrderTypeId> duplicateOrderOrderTypeId = outboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderTypeIdAndLanguageIdAndDeletionIndicator(newOutboundOrderTypeId.getCompanyCodeId(),
                newOutboundOrderTypeId.getPlantId(), newOutboundOrderTypeId.getWarehouseId(), newOutboundOrderTypeId.getOutboundOrderTypeId(), newOutboundOrderTypeId.getLanguageId(),0L);
        if (!duplicateOrderOrderTypeId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newOutboundOrderTypeId.getWarehouseId(), newOutboundOrderTypeId.getCompanyCodeId(), newOutboundOrderTypeId.getPlantId(), newOutboundOrderTypeId.getLanguageId());
            log.info("newOutboundOrderTypeId : " + newOutboundOrderTypeId);
            BeanUtils.copyProperties(newOutboundOrderTypeId, dbOutboundOrderTypeIdId, CommonUtils.getNullPropertyNames(newOutboundOrderTypeId));
            dbOutboundOrderTypeIdId.setDeletionIndicator(0L);
            dbOutboundOrderTypeIdId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbOutboundOrderTypeIdId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbOutboundOrderTypeIdId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbOutboundOrderTypeIdId.setCreatedBy(loginUserID);
            dbOutboundOrderTypeIdId.setUpdatedBy(loginUserID);
            dbOutboundOrderTypeIdId.setCreatedOn(new Date());
            dbOutboundOrderTypeIdId.setUpdatedOn(new Date());
            return outboundOrderTypeIdRepository.save(dbOutboundOrderTypeIdId);
        }
    }

    /**
     * updateOutboundOrderTypeId
     * @param loginUserID
     * @param outboundOrderTypeId
     * @param updateOutboundOrderTypeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundOrderTypeId updateOutboundOrderTypeId (String warehouseId, String outboundOrderTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
                                                              UpdateOutboundOrderTypeId updateOutboundOrderTypeId)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OutboundOrderTypeId dbOutboundOrderTypeId = getOutboundOrderTypeId( warehouseId, outboundOrderTypeId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateOutboundOrderTypeId, dbOutboundOrderTypeId, CommonUtils.getNullPropertyNames(updateOutboundOrderTypeId));
        dbOutboundOrderTypeId.setUpdatedBy(loginUserID);
        dbOutboundOrderTypeId.setUpdatedOn(new Date());
        return outboundOrderTypeIdRepository.save(dbOutboundOrderTypeId);
    }
    /**
     * deleteOutboundOrderTypeId
     * @param loginUserID
     * @param outboundOrderTypeId
     */
    public void deleteOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId,String companyCodeId,String languageId,String plantId,String loginUserID){
        OutboundOrderTypeId dbOutboundOrderTypeId=getOutboundOrderTypeId(warehouseId,outboundOrderTypeId,companyCodeId,languageId,plantId);
            if(dbOutboundOrderTypeId !=null) {
                dbOutboundOrderTypeId.setDeletionIndicator(1l);
                dbOutboundOrderTypeId.setUpdatedBy(loginUserID);
                outboundOrderTypeIdRepository.save(dbOutboundOrderTypeId);
            }
            else{
                throw new EntityNotFoundException("Error in deleting Id: " + outboundOrderTypeId);
            }

    }
    //Find OutboundOrderTypeId
    public List<OutboundOrderTypeId> findOutboundOrderTypeId(FindOutboundOrderTypeId findOutboundOrderTypeId) throws ParseException {

        OutboundOrderTypeIdSpecification spec = new OutboundOrderTypeIdSpecification(findOutboundOrderTypeId);
        List<OutboundOrderTypeId> results = outboundOrderTypeIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<OutboundOrderTypeId> newOutboundOrderTypeId=new ArrayList<>();
        for(OutboundOrderTypeId dbOutboundOrderTypeId:results) {
            if (dbOutboundOrderTypeId.getCompanyIdAndDescription() != null&&dbOutboundOrderTypeId.getPlantIdAndDescription()!=null&&dbOutboundOrderTypeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbOutboundOrderTypeId.getCompanyCodeId(), dbOutboundOrderTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbOutboundOrderTypeId.getPlantId(),dbOutboundOrderTypeId.getLanguageId(), dbOutboundOrderTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbOutboundOrderTypeId.getWarehouseId(),dbOutboundOrderTypeId.getLanguageId(), dbOutboundOrderTypeId.getCompanyCodeId(), dbOutboundOrderTypeId.getPlantId());
                if(iKeyValuePair!=null) {
                    dbOutboundOrderTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if(iKeyValuePair1!=null) {
                    dbOutboundOrderTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if(iKeyValuePair2!=null){
                    dbOutboundOrderTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId()+"-"+iKeyValuePair2.getDescription());
                }
            }
            newOutboundOrderTypeId.add(dbOutboundOrderTypeId);
        }
        return newOutboundOrderTypeId;
    }

}




