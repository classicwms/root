package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData;
import com.tekclover.wms.api.masters.model.imbatchserial.AddImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.SearchImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.UpdateImBatchSerial;
import com.tekclover.wms.api.masters.repository.ImBatchSerialRepository;
import com.tekclover.wms.api.masters.repository.specification.ImBatchSerialSpecification;
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

@Service
@Slf4j
public class ImBatchSerialService {

    @Autowired
    private ImBatchSerialRepository imBatchSerialRepository;

    /**
     * ImBatchSerial
     *
     * @return
     */
    public List<ImBatchSerial> getImBatchSerials() {
        try {
            List<ImBatchSerial> imBatchSerialList = imBatchSerialRepository.findAll();
            log.info("imBatchSerialList : " + imBatchSerialList);
            imBatchSerialList = imBatchSerialList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return imBatchSerialList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getHandlingEquipment
     *
     * @param storageMethod
     * @param itemCode
     * @return
     */
    public ImBatchSerial getImBatchSerial(String warehouseId, String companyCodeId, String languageId, String plantId, String itemCode, String storageMethod) {
        try {
            Optional<ImBatchSerial> dbImBatchSerial =
                    imBatchSerialRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageMethodAndDeletionIndicator(
                            languageId,
                            companyCodeId,
                            plantId,
                            warehouseId,
                            itemCode,
                            storageMethod,
                            0L
                    );
            if (dbImBatchSerial.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "warehouseId - " + warehouseId +
                        "companyCodeId - " + companyCodeId +
                        "plantId - " + plantId +
                        "itemCode - " + itemCode +
                        "storageMethod - " + storageMethod +
                        "doesn't exist.");

            }
            return dbImBatchSerial.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param imBasicData
     * @return
     */
    public ImBatchSerial getImBatchSerialV2(ImBasicData imBasicData) {
        try {
            Optional<ImBatchSerial> dbImBatchSerial =
                    imBatchSerialRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                            imBasicData.getLanguageId(),
                            imBasicData.getCompanyCodeId(),
                            imBasicData.getPlantId(),
                            imBasicData.getWarehouseId(),
                            imBasicData.getItemCode(),
                            0L);
            if (dbImBatchSerial.isEmpty()) {
                return null;
            }
            return dbImBatchSerial.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchImBatchSerial
     * @return
     */
    public List<ImBatchSerial> findImBatchSerial(SearchImBatchSerial searchImBatchSerial) {
        try {
            ImBatchSerialSpecification spec = new ImBatchSerialSpecification(searchImBatchSerial);
            List<ImBatchSerial> results = imBatchSerialRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createImBatchSerial
     *
     * @param newImBatchSerial
     * @return
     */
    public ImBatchSerial createImBatchSerial(AddImBatchSerial newImBatchSerial, String loginUserID) {
        try {
            ImBatchSerial dbImBatchSerial = new ImBatchSerial();
            Optional<ImBatchSerial> duplicateImBatchSerial = imBatchSerialRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageMethodAndDeletionIndicator(
                    newImBatchSerial.getLanguageId(),
                    newImBatchSerial.getCompanyCodeId(),
                    newImBatchSerial.getPlantId(),
                    newImBatchSerial.getWarehouseId(),
                    newImBatchSerial.getItemCode(),
                    newImBatchSerial.getStorageMethod(),
                    0L);
            if (!duplicateImBatchSerial.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newImBatchSerial, dbImBatchSerial, CommonUtils.getNullPropertyNames(newImBatchSerial));
                dbImBatchSerial.setDeletionIndicator(0L);
                dbImBatchSerial.setCreatedBy(loginUserID);
                dbImBatchSerial.setUpdatedBy(loginUserID);
                dbImBatchSerial.setCreatedOn(new Date());
                dbImBatchSerial.setUpdatedOn(new Date());
                return imBatchSerialRepository.save(dbImBatchSerial);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateBatchSerial
     *
     * @param itemCode
     * @param storageMethod
     * @return
     */
    public ImBatchSerial updateBatchSerial(String companyCodeId, String plantId, String warehouseId, String languageId,
                                           String itemCode, String storageMethod, UpdateImBatchSerial updateImBatchSerial, String loginUserID) {
        try {
            ImBatchSerial dbImBatchSerial = getImBatchSerial(warehouseId, companyCodeId, languageId, plantId, itemCode, storageMethod);
            BeanUtils.copyProperties(updateImBatchSerial, dbImBatchSerial, CommonUtils.getNullPropertyNames(updateImBatchSerial));
            dbImBatchSerial.setUpdatedBy(loginUserID);
            dbImBatchSerial.setUpdatedOn(new Date());
            return imBatchSerialRepository.save(dbImBatchSerial);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteImBatchSerial
     *
     * @param storageMethod
     * @param itemCode
     */
    public void deleteImBatchSerial(String companyCodeId, String languageId, String plantId, String warehouseId,
                                    String itemCode, String storageMethod, String loginUserID) {
        try {
            ImBatchSerial imBatchSerial = getImBatchSerial(warehouseId, companyCodeId, languageId, plantId, itemCode, storageMethod);
            if (imBatchSerial != null) {
                imBatchSerial.setDeletionIndicator(1L);
                imBatchSerial.setUpdatedBy(loginUserID);
                imBatchSerial.setUpdatedOn(new Date());
                imBatchSerialRepository.save(imBatchSerial);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + storageMethod);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}