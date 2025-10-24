package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imbatchserial.AddImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.SearchImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.UpdateImBatchSerial;
import com.tekclover.wms.api.masters.model.imcapacity.AddImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.ImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.SearchImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.UpdateImCapacity;
import com.tekclover.wms.api.masters.repository.ImBatchSerialRepository;
import com.tekclover.wms.api.masters.repository.ImCapacityRepository;
import com.tekclover.wms.api.masters.repository.specification.ImBatchSerialSpecification;
import com.tekclover.wms.api.masters.repository.specification.ImCapacitySpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImCapacityService {

    @Autowired
    private ImCapacityRepository imCapacityRepository;

    /**
     * getHandlingEquipment
     * @param companyCodeId
     * @param itemCode
     * @return
     */
    public ImCapacity getImCapacity (String warehouseId, String companyCodeId, String languageId, String plantId,String itemCode) {
        Optional<ImCapacity> dbImCapacity =
                imCapacityRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        itemCode,
                        0L
                );
        if (dbImCapacity.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "companyCodeId - "+companyCodeId+
                    "plantId - " +plantId+
                    "itemCode - " +itemCode+
                    "doesn't exist.");

        }
        return dbImCapacity.get();
    }



    /**
     *
     * @param searchImCapacity
     * @return
     * @throws Exception
     */
    public List<ImCapacity> findImCapacity(SearchImCapacity searchImCapacity)
            throws Exception {
        ImCapacitySpecification spec = new ImCapacitySpecification(searchImCapacity);
        List<ImCapacity> results = imCapacityRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createImCapacity
     * @param newImCapacity
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ImCapacity createImCapacity (AddImCapacity newImCapacity, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImCapacity dbImCapacity = new ImCapacity();
        Optional<ImCapacity> duplicateImCapacity = imCapacityRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                newImCapacity.getCompanyCodeId(),
                newImCapacity.getLanguageId(),
                newImCapacity.getPlantId(),
                newImCapacity.getWarehouseId(),
                newImCapacity.getItemCode(),
                0L);
        if (!duplicateImCapacity.isEmpty()) {
            throw new BadRequestException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newImCapacity, dbImCapacity, CommonUtils.getNullPropertyNames(newImCapacity));
            dbImCapacity.setDeletionIndicator(0L);
            dbImCapacity.setCreatedBy(loginUserID);
            dbImCapacity.setUpdatedBy(loginUserID);
            dbImCapacity.setCreatedOn(new Date());
            dbImCapacity.setUpdatedOn(new Date());
            return imCapacityRepository.save(dbImCapacity);
        }
    }

    /**
     * updateImCapacity
     * @param itemCode
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ImCapacity updateImCapacity (String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, UpdateImCapacity updateImCapacity, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImCapacity dbImCapacity = getImCapacity(warehouseId,companyCodeId,languageId,plantId,itemCode);
        BeanUtils.copyProperties(updateImCapacity, dbImCapacity, CommonUtils.getNullPropertyNames(updateImCapacity));
        dbImCapacity.setUpdatedBy(loginUserID);
        dbImCapacity.setUpdatedOn(new Date());
        return imCapacityRepository.save(dbImCapacity);
    }

    /**
     * deleteImCapacity
     * @param itemCode
     */
    public void deleteImCapacity (String companyCodeId,String languageId,String plantId,String warehouseId,String itemCode,String loginUserID) throws ParseException {
        ImCapacity imCapacity = getImCapacity(warehouseId,companyCodeId,languageId,plantId,itemCode);
        if ( imCapacity != null) {
            imCapacity.setDeletionIndicator (1L);
            imCapacity.setUpdatedBy(loginUserID);
            imCapacity.setUpdatedOn(new Date());
            imCapacityRepository.save(imCapacity);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + imCapacity);
        }
    }
}
