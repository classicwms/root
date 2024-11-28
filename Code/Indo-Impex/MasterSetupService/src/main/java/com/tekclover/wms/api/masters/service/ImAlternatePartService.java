package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imalternateparts.AddImAlternatePart;
import com.tekclover.wms.api.masters.model.imalternateparts.ImAlternatePart;
import com.tekclover.wms.api.masters.model.imalternateparts.SearchImAlternateParts;
import com.tekclover.wms.api.masters.repository.ImAlternatePartRepository;
import com.tekclover.wms.api.masters.repository.specification.ImAlternatePartSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImAlternatePartService {

    @Autowired
    private ImAlternatePartRepository imAlternatePartRepository;

    public List<ImAlternatePart> getAllImAlternateParts() {
        try {
            List<ImAlternatePart> imAlternatePartList = imAlternatePartRepository.findAll();
//        log.info("imalternateparts : " + imAlternatePartList);
            imAlternatePartList = imAlternatePartList.stream().filter(n -> n.getDeletionIndicator() != null &&
                    n.getDeletionIndicator() == 0).collect(Collectors.toList());

            return imAlternatePartList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param warehouseId
     * @param plantId
     * @param itemCode
     * @return
     */
    public List<ImAlternatePart> getImAlternatePart(String companyCodeId, String languageId, String warehouseId, String plantId, String itemCode) {
        try {
            List<ImAlternatePart> imAlternatePart = imAlternatePartRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                    companyCodeId,
                    languageId,
                    plantId,
                    warehouseId,
                    itemCode,
                    0l
            );
            if (imAlternatePart.isEmpty()) {
                throw new BadRequestException("The given values:" +
                        "companyCodeId" + companyCodeId +
                        "languageId" + languageId +
                        "plantId" + plantId +
                        "warehouseId" + warehouseId +
                        "itemCode" + itemCode +
                        "doesn't exists");
            }
            return imAlternatePart;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newAddImAlternatePart
     * @param loginUserID
     * @return
     */
    public List<ImAlternatePart> createAlternatePart(List<AddImAlternatePart> newAddImAlternatePart, String loginUserID) {
        try {
            List<ImAlternatePart> imAlternatePartList = new ArrayList<>();

            for (AddImAlternatePart newImAlternatePart : newAddImAlternatePart) {

                List<ImAlternatePart> duplicateImAlternatePart =
                        imAlternatePartRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndAltItemCodeAndDeletionIndicator(
                                newImAlternatePart.getCompanyCodeId(),
                                newImAlternatePart.getLanguageId(),
                                newImAlternatePart.getPlantId(),
                                newImAlternatePart.getWarehouseId(),
                                newImAlternatePart.getItemCode(),
                                newImAlternatePart.getAltItemCode(),
                                0L);
                if (!duplicateImAlternatePart.isEmpty()) {
                    throw new EntityNotFoundException("The Record is Getting Duplicate");
                } else {

                    ImAlternatePart dbImAlternatePart = new ImAlternatePart();
                    BeanUtils.copyProperties(newImAlternatePart, dbImAlternatePart, CommonUtils.getNullPropertyNames(newImAlternatePart));
                    dbImAlternatePart.setDeletionIndicator(0L);
                    dbImAlternatePart.setCreatedBy(loginUserID);
                    dbImAlternatePart.setUpdatedBy(loginUserID);
                    dbImAlternatePart.setCreatedOn(new Date());
                    dbImAlternatePart.setUpdatedOn(new Date());
                    ImAlternatePart savedImAlternatePart = imAlternatePartRepository.save(dbImAlternatePart);
                    imAlternatePartList.add(savedImAlternatePart);
                }
            }
            return imAlternatePartList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param updateImAlternatePart
     * @param loginUserID
     * @return
     */
    public List<ImAlternatePart> updateImAlternatePart(String companyCodeId, String languageId, String plantId,
                                                       String warehouseId, String itemCode, List<AddImAlternatePart> updateImAlternatePart, String loginUserID) {

        try {
            List<ImAlternatePart> imAlternatePartList = imAlternatePartRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                    companyCodeId,
                    languageId,
                    plantId,
                    warehouseId,
                    itemCode,
                    0L);

            if (imAlternatePartList != null) {
                for (ImAlternatePart imAlternatePart : imAlternatePartList) {
                    imAlternatePart.setDeletionIndicator(1L);
                    imAlternatePart.setUpdatedBy(loginUserID);
                    imAlternatePart.setUpdatedOn(new Date());
                    imAlternatePartRepository.save(imAlternatePart);
                }
            }

            List<ImAlternatePart> createImAlternatePart = createAlternatePart(updateImAlternatePart, loginUserID);
            return createImAlternatePart;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param loginUserID
     */
    public void deleteImAlternateUom(String companyCodeId, String languageId, String plantId,
                                     String warehouseId, String itemCode, String loginUserID) {

        try {
            List<ImAlternatePart> imAlternatePart = getImAlternatePart(companyCodeId, languageId, warehouseId, plantId, itemCode);
            if (imAlternatePart != null) {
                for (ImAlternatePart newImAlternatePart : imAlternatePart) {
                    newImAlternatePart.setDeletionIndicator(1L);
                    newImAlternatePart.setUpdatedBy(loginUserID);
                    newImAlternatePart.setUpdatedOn(new Date());
                    imAlternatePartRepository.save(newImAlternatePart);
                }
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + itemCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchImAlternateParts
     * @return
     */
    public List<ImAlternatePart> findIAmAlternatePart(SearchImAlternateParts searchImAlternateParts) {
        try {
            ImAlternatePartSpecification spec = new ImAlternatePartSpecification(searchImAlternateParts);
            List<ImAlternatePart> results = imAlternatePartRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

}