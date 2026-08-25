package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.InboundOrderStatusId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.AddInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.FindInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.UpdateInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.InboundOrderTypeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.InboundOrderTypeIdSpecification;
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
public class InboundOrderTypeIdService {

    @Autowired
    private CompanyIdRepository companyIdRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private InboundOrderTypeIdRepository inboundOrderTypeIdRepository;
    /**
     * getInboundOrderTypeIds
     * @return
     */
    public List<InboundOrderTypeId> getInboundOrderTypeIds () {
        List<InboundOrderTypeId> inboundOrderTypeIdList =  inboundOrderTypeIdRepository.findAll();
        inboundOrderTypeIdList = inboundOrderTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<InboundOrderTypeId> newInboundOrderTypeId=new ArrayList<>();
        for(InboundOrderTypeId dbInboundOrderTypeId:inboundOrderTypeIdList) {
            if (dbInboundOrderTypeId.getCompanyIdAndDescription() != null&&dbInboundOrderTypeId.getPlantIdAndDescription()!=null&&dbInboundOrderTypeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbInboundOrderTypeId.getCompanyCodeId(), dbInboundOrderTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbInboundOrderTypeId.getPlantId(), dbInboundOrderTypeId.getLanguageId(), dbInboundOrderTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbInboundOrderTypeId.getWarehouseId(), dbInboundOrderTypeId.getLanguageId(), dbInboundOrderTypeId.getCompanyCodeId(), dbInboundOrderTypeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbInboundOrderTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbInboundOrderTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbInboundOrderTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newInboundOrderTypeId.add(dbInboundOrderTypeId);
        }
        return newInboundOrderTypeId;
    }
    /**
     * getInboundOrderTypeId
     * @param inboundOrderTypeId
     * @return
     */
    public InboundOrderTypeId getInboundOrderTypeId(String warehouseId, String inboundOrderTypeId,String companyCodeId,String languageId,String plantId) {
        Optional<InboundOrderTypeId> dbInboundOrderTypeId =
                inboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        inboundOrderTypeId,
                        languageId,
                        0L
                );
        if (dbInboundOrderTypeId.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "inboundOrderTypeId - " + inboundOrderTypeId +
                    " doesn't exist.");

        }
        InboundOrderTypeId newInboundOrderTypeId = new InboundOrderTypeId();
        BeanUtils.copyProperties(dbInboundOrderTypeId.get(),newInboundOrderTypeId, CommonUtils.getNullPropertyNames(dbInboundOrderTypeId));
        IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
        IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
        if(iKeyValuePair!=null) {
            newInboundOrderTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if(iKeyValuePair1!=null) {
            newInboundOrderTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if(iKeyValuePair2!=null) {
            newInboundOrderTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        return newInboundOrderTypeId;
    }
    /**
     * createInboundOrderStatusId
     * @param newInboundOrderTypeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundOrderTypeId createInboundOrderTypeId (AddInboundOrderTypeId newInboundOrderTypeId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        InboundOrderTypeId dbnewInboundOrderTypeId = new InboundOrderTypeId();
        Optional<InboundOrderTypeId> duplicateInboundOrderTypeId = inboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderTypeIdAndLanguageIdAndDeletionIndicator(newInboundOrderTypeId.getCompanyCodeId(), newInboundOrderTypeId.getPlantId(), newInboundOrderTypeId.getWarehouseId(), newInboundOrderTypeId.getInboundOrderTypeId(), newInboundOrderTypeId.getLanguageId(), 0L);
        if (!duplicateInboundOrderTypeId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            Warehouse dbWarehouse=warehouseService.getWarehouse(newInboundOrderTypeId.getWarehouseId(), newInboundOrderTypeId.getCompanyCodeId(), newInboundOrderTypeId.getPlantId(), newInboundOrderTypeId.getLanguageId());
            log.info("newInboundOrderStatusId : " + newInboundOrderTypeId);
            BeanUtils.copyProperties(newInboundOrderTypeId, dbnewInboundOrderTypeId, CommonUtils.getNullPropertyNames(newInboundOrderTypeId));
            dbnewInboundOrderTypeId.setDeletionIndicator(0L);
            dbnewInboundOrderTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
            dbnewInboundOrderTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
            dbnewInboundOrderTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
            dbnewInboundOrderTypeId.setCreatedBy(loginUserID);
            dbnewInboundOrderTypeId.setUpdatedBy(loginUserID);
            dbnewInboundOrderTypeId.setCreatedOn(new Date());
            dbnewInboundOrderTypeId.setUpdatedOn(new Date());
            return inboundOrderTypeIdRepository.save(dbnewInboundOrderTypeId);
        }
    }
    /**
     * updateInboundOrderStatusId
     * @param loginUserID
     * @param inboundOrderTypeId
     * @param updateInboundOrderTypeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundOrderTypeId updateInboundOrderTypeId (String warehouseId, String inboundOrderTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
                                                            UpdateInboundOrderTypeId updateInboundOrderTypeId)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        InboundOrderTypeId dbInboundOrderTypeId = getInboundOrderTypeId( warehouseId, inboundOrderTypeId,companyCodeId,languageId,plantId);
        BeanUtils.copyProperties(updateInboundOrderTypeId, dbInboundOrderTypeId, CommonUtils.getNullPropertyNames(updateInboundOrderTypeId));
        dbInboundOrderTypeId.setUpdatedBy(loginUserID);
        dbInboundOrderTypeId.setUpdatedOn(new Date());
        return inboundOrderTypeIdRepository.save(dbInboundOrderTypeId);
    }

    /**
     * deleteInboundOrderStatusId
     * @param loginUserID
     * @param inboundOrderTypeId
     */
    public void deleteInboundOrderTypeId (String warehouseId, String inboundOrderTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
        InboundOrderTypeId dbinboundOrderTypeId=getInboundOrderTypeId( warehouseId,inboundOrderTypeId,companyCodeId,languageId,plantId);
        if ( dbinboundOrderTypeId != null) {
            dbinboundOrderTypeId.setDeletionIndicator(1L);
            dbinboundOrderTypeId.setUpdatedBy(loginUserID);
            inboundOrderTypeIdRepository.save(dbinboundOrderTypeId);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + inboundOrderTypeId);
        }
    }
    //Find InboundOrderTypeId
    public List<InboundOrderTypeId> findInboundOrderTypeId(FindInboundOrderTypeId findInboundOrderTypeId) throws ParseException {

        InboundOrderTypeIdSpecification spec = new InboundOrderTypeIdSpecification(findInboundOrderTypeId);
        List<InboundOrderTypeId> results = inboundOrderTypeIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<InboundOrderTypeId> newInboundOrderTypeId=new ArrayList<>();
        for(InboundOrderTypeId dbInboundOrderTypeId:results) {
            if (dbInboundOrderTypeId.getCompanyIdAndDescription() != null&&dbInboundOrderTypeId.getPlantIdAndDescription()!=null&&dbInboundOrderTypeId.getWarehouseIdAndDescription()!=null) {
                IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbInboundOrderTypeId.getCompanyCodeId(), dbInboundOrderTypeId.getLanguageId());
                IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbInboundOrderTypeId.getPlantId(), dbInboundOrderTypeId.getLanguageId(), dbInboundOrderTypeId.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbInboundOrderTypeId.getWarehouseId(), dbInboundOrderTypeId.getLanguageId(), dbInboundOrderTypeId.getCompanyCodeId(), dbInboundOrderTypeId.getPlantId());
                if (iKeyValuePair != null) {
                    dbInboundOrderTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbInboundOrderTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbInboundOrderTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
            }
            newInboundOrderTypeId.add(dbInboundOrderTypeId);
        }
        return newInboundOrderTypeId;
    }




}
