package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imvariant.AddImVariant;
import com.tekclover.wms.api.masters.model.imvariant.ImVariant;
import com.tekclover.wms.api.masters.model.imvariant.SearchImVariant;
import com.tekclover.wms.api.masters.repository.ImVariantRepository;
import com.tekclover.wms.api.masters.repository.specification.ImVariantSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImVariantService {

    @Autowired
    private ImVariantRepository imVariantRepository;

    /**
     * @param warehouseId
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param itemCode
     * @return
     */
    public List<ImVariant> getImVariant(String warehouseId, String companyCodeId, String languageId, String plantId,
                                        String itemCode) {
        List<ImVariant> dbImVariant =
                imVariantRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        itemCode,
                        0L
                );
        if (dbImVariant.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "companyCodeId - " + companyCodeId +
                    "plantId - " + plantId +
                    "itemCode - " + itemCode +
                    "doesn't exist.");

        }
        return dbImVariant;
    }


    /**
     * @param searchImVariant
     * @return
     * @throws Exception
     */
    public List<ImVariant> findImVariant(SearchImVariant searchImVariant)
            throws Exception {
        ImVariantSpecification spec = new ImVariantSpecification(searchImVariant);
        List<ImVariant> results = imVariantRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createImCapacity
     *
     * @param newImVariant
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<ImVariant> createImvariant(List<AddImVariant> newImVariant, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImVariant dbImVariant = new ImVariant();
        List<ImVariant> imVariantList = new ArrayList<>();
        for (AddImVariant addImVariant : newImVariant) {
            List<ImVariant> duplicateImVariant = imVariantRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndVariantCodeAndVariantTypeAndAndVariantSubCodeAndDeletionIndicator(
                    addImVariant.getCompanyCodeId(),
                    addImVariant.getLanguageId(),
                    addImVariant.getPlantId(),
                    addImVariant.getWarehouseId(),
                    addImVariant.getItemCode(),
                    addImVariant.getVariantCode(),
                    addImVariant.getVariantType(),
                    addImVariant.getVariantSubCode(),
                    0L);
            if (!duplicateImVariant.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(addImVariant, dbImVariant, CommonUtils.getNullPropertyNames(addImVariant));
                dbImVariant.setDeletionIndicator(0L);
                dbImVariant.setCreatedBy(loginUserID);
                dbImVariant.setUpdatedBy(loginUserID);
                dbImVariant.setCreatedOn(new Date());
                dbImVariant.setUpdatedOn(new Date());
                ImVariant savedImVariant = imVariantRepository.save(dbImVariant);
                imVariantList.add(savedImVariant);
            }
        }
        return imVariantList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param itemCode
     * @param updateImVariant
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<ImVariant> updateImVariant(String companyCodeId, String plantId, String warehouseId, String languageId,
                                           String itemCode, List<AddImVariant> updateImVariant, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<ImVariant> imVariantList = imVariantRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                companyCodeId,
                languageId,
                plantId,
                warehouseId,
                itemCode,
                0L
        );
        if (imVariantList != null) {
            for (ImVariant imVariant : imVariantList) {
                imVariant.setUpdatedBy(loginUserID);
                imVariant.setUpdatedOn(new Date());
                imVariant.setDeletionIndicator(1L);
                imVariantRepository.save(imVariant);
            }
        } else {
            throw new EntityNotFoundException("The given values of companyCodeId " + companyCodeId +
                    " plantId " + plantId +
                    " languageId " + languageId +
                    " warehouseId " + warehouseId +
                    " itemCode " + itemCode + " doesn't exists");
        }
            List<ImVariant> createImVariant = createImvariant(updateImVariant, loginUserID);
            return createImVariant;

        }

    /**
     *
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     */
    public void deleteImVariant (String companyCodeId,String languageId,String plantId,String warehouseId,
                                 String itemCode,String loginUserID) throws ParseException {

      List<ImVariant> imVariant = imVariantRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
              companyCodeId,
              languageId,
              plantId,
              warehouseId,
              itemCode,
              0L
      );
        if ( imVariant != null) {
            for(ImVariant variant :imVariant) {
                variant.setDeletionIndicator(1L);
                variant.setUpdatedBy(loginUserID);
                variant.setUpdatedOn(new Date());
                imVariantRepository.save(variant);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + imVariant);
        }
    }
}
