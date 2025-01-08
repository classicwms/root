package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.AddNumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.NumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.SearchNumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.UpdateNumberRangeStorageBin;
import com.tekclover.wms.api.masters.repository.NumberRangeStorageBinRepository;
import com.tekclover.wms.api.masters.repository.specification.NumberRangeStorageBinSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class NumberRangeStorageBinService {

    @Autowired
    private NumberRangeStorageBinRepository numberRangeStorageBinRepository;

    /**
     * getAllNumberRangeStorageBin
     *
     * @return
     */
    public List<NumberRangeStorageBin> getAllNumberRangeStorageBin() {
        try {
            List<NumberRangeStorageBin> numberRangeStorageBin = numberRangeStorageBinRepository.findAll();
            log.info("numberRangeStorageBin : " + numberRangeStorageBin);
            numberRangeStorageBin = numberRangeStorageBin.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return numberRangeStorageBin;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getDock
     *
     * @param rowId
     * @param storageSectionId
     * @param aisleNumber
     * @param floorId
     * @return
     */
    public NumberRangeStorageBin getNumberRangeStorageBin(String warehouseId, String companyCodeId, String languageId,
                                                          String plantId, Long floorId, String rowId, String storageSectionId, String aisleNumber) {
        try {
            Optional<NumberRangeStorageBin> dbNumberRangeStorageBin =
                    numberRangeStorageBinRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndAisleNumberAndDeletionIndicator(
                            companyCodeId,
                            languageId,
                            plantId,
                            warehouseId,
                            floorId,
                            storageSectionId,
                            rowId,
                            aisleNumber,
                            0L);
            if (dbNumberRangeStorageBin.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "warehouseId - " + warehouseId +
                        "companyCodeId - " + companyCodeId +
                        "plantId - " + plantId +
                        "floorId - " + floorId +
                        "storageSectionId - " + storageSectionId +
                        "aisleNumber - " + aisleNumber +
                        "doesn't exist.");

            }
            return dbNumberRangeStorageBin.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchNumberRangeStorageBin
     * @return
     */
    public List<NumberRangeStorageBin> findNumberRangeStorageBin(SearchNumberRangeStorageBin searchNumberRangeStorageBin) {
        try {
            NumberRangeStorageBinSpecification spec = new NumberRangeStorageBinSpecification(searchNumberRangeStorageBin);
            List<NumberRangeStorageBin> results = numberRangeStorageBinRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * createNumberRangeItem
     *
     * @param newNumberRangeStorageBin
     * @return
     */
    public NumberRangeStorageBin createNumberRangeStorageBin(AddNumberRangeStorageBin newNumberRangeStorageBin, String loginUserID) {
        try {
            NumberRangeStorageBin dbNumberRangeStorageBin = new NumberRangeStorageBin();
            Optional<NumberRangeStorageBin> duplicateNumberRangeStorageBin =
                    numberRangeStorageBinRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndAisleNumberAndDeletionIndicator(
                            newNumberRangeStorageBin.getCompanyCodeId(),
                            newNumberRangeStorageBin.getLanguageId(),
                            newNumberRangeStorageBin.getPlantId(),
                            newNumberRangeStorageBin.getWarehouseId(),
                            newNumberRangeStorageBin.getFloorId(),
                            newNumberRangeStorageBin.getStorageSectionId(),
                            newNumberRangeStorageBin.getRowId(),
                            newNumberRangeStorageBin.getAisleNumber(),
                            0L);
            if (!duplicateNumberRangeStorageBin.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newNumberRangeStorageBin, dbNumberRangeStorageBin, CommonUtils.getNullPropertyNames(newNumberRangeStorageBin));
                dbNumberRangeStorageBin.setDeletionIndicator(0L);
                dbNumberRangeStorageBin.setCreatedBy(loginUserID);
                dbNumberRangeStorageBin.setUpdatedBy(loginUserID);
                dbNumberRangeStorageBin.setCreatedOn(new Date());
                dbNumberRangeStorageBin.setUpdatedOn(new Date());
                return numberRangeStorageBinRepository.save(dbNumberRangeStorageBin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateNumberRange
     *
     * @param floorId
     * @param storageSectionId
     * @return
     */
    public NumberRangeStorageBin updateNumberRangeItem(String companyCodeId, String plantId, String warehouseId,
                                                       String languageId, Long floorId, String storageSectionId, String rowId, String aisleNumber,
                                                       UpdateNumberRangeStorageBin updateNumberRangeStorageBin, String loginUserID) {
        try {
            NumberRangeStorageBin dbNumberRangeStorageBin =
                    getNumberRangeStorageBin(warehouseId, companyCodeId, languageId, plantId, floorId, rowId, storageSectionId, aisleNumber);

            BeanUtils.copyProperties(updateNumberRangeStorageBin, dbNumberRangeStorageBin,
                    CommonUtils.getNullPropertyNames(updateNumberRangeStorageBin));
            dbNumberRangeStorageBin.setUpdatedBy(loginUserID);
            dbNumberRangeStorageBin.setUpdatedOn(new Date());
            return numberRangeStorageBinRepository.save(dbNumberRangeStorageBin);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteNumberRangeStorageBin
     *
     * @param floorId
     * @param storageSectionId
     * @param rowId
     */
    public void deleteNumberRangeStorageBin(String companyCodeId, String languageId, String plantId, String warehouseId,
                                            Long floorId, String storageSectionId, String rowId, String aisleNumber, String loginUserID) {
        try {
            NumberRangeStorageBin numberRangeStorageBin =
                    getNumberRangeStorageBin(warehouseId, companyCodeId, languageId, plantId,
                            floorId, rowId, storageSectionId, aisleNumber);

            if (numberRangeStorageBin != null) {
                numberRangeStorageBin.setDeletionIndicator(1L);
                numberRangeStorageBin.setUpdatedBy(loginUserID);
                numberRangeStorageBin.setUpdatedOn(new Date());
                numberRangeStorageBinRepository.save(numberRangeStorageBin);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + storageSectionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}