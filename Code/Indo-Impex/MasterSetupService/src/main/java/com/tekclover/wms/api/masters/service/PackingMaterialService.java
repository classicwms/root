package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.packingmaterial.AddPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.SearchPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.UpdatePackingMaterial;
import com.tekclover.wms.api.masters.repository.PackingMaterialRepository;
import com.tekclover.wms.api.masters.repository.specification.PackingMaterialSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
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
public class PackingMaterialService {

    @Autowired
    private PackingMaterialRepository packingmaterialRepository;

    /**
     * getPackingMaterials
     *
     * @return
     */
    public List<PackingMaterial> getPackingMaterials() {
        try {
            List<PackingMaterial> packingmaterialList = packingmaterialRepository.findAll();
            log.info("packingmaterialList : " + packingmaterialList);
            packingmaterialList = packingmaterialList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return packingmaterialList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getPackingMaterial
     *
     * @param packingMaterialNo
     * @return
     */
    public PackingMaterial getPackingMaterial(String packingMaterialNo, String companyCodeId, String plantId, String languageId, String warehouseId) {
        try {
            Optional<PackingMaterial> packingmaterial = packingmaterialRepository.findByPackingMaterialNoAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                    packingMaterialNo,
                    companyCodeId,
                    plantId,
                    warehouseId,
                    languageId,
                    0L);
            if (packingmaterial.isEmpty()) {
                throw new BadRequestException("The given Values : " +
                        "packingMaterialNo -" + packingMaterialNo +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "languageId" + languageId + " doesn't exist.");
            }
            return packingmaterial.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchPackingMaterial
     * @return
     */
    public List<PackingMaterial> findPackingMaterial(SearchPackingMaterial searchPackingMaterial) {
        try {
            if (searchPackingMaterial.getStartCreatedOn() != null && searchPackingMaterial.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchPackingMaterial.getStartCreatedOn(), searchPackingMaterial.getEndCreatedOn());
                searchPackingMaterial.setStartCreatedOn(dates[0]);
                searchPackingMaterial.setEndCreatedOn(dates[1]);
            }

            if (searchPackingMaterial.getStartUpdatedOn() != null && searchPackingMaterial.getEndUpdatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchPackingMaterial.getStartUpdatedOn(), searchPackingMaterial.getEndUpdatedOn());
                searchPackingMaterial.setStartUpdatedOn(dates[0]);
                searchPackingMaterial.setEndUpdatedOn(dates[1]);
            }

            PackingMaterialSpecification spec = new PackingMaterialSpecification(searchPackingMaterial);
            List<PackingMaterial> results = packingmaterialRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createPackingMaterial
     *
     * @param newPackingMaterial
     * @return
     */
    public PackingMaterial createPackingMaterial(AddPackingMaterial newPackingMaterial, String loginUserID) {
        try {
            PackingMaterial dbPackingMaterial = new PackingMaterial();
            Optional<PackingMaterial> duplicatePackingMaterial = packingmaterialRepository.findByPackingMaterialNoAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(newPackingMaterial.getPackingMaterialNo(), newPackingMaterial.getCompanyCodeId(), newPackingMaterial.getPlantId(), newPackingMaterial.getWarehouseId(), newPackingMaterial.getLanguageId(), 0L);
            if (!duplicatePackingMaterial.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting duplicated");
            } else {
                BeanUtils.copyProperties(newPackingMaterial, dbPackingMaterial, CommonUtils.getNullPropertyNames(newPackingMaterial));
                dbPackingMaterial.setDeletionIndicator(0L);
                dbPackingMaterial.setCreatedBy(loginUserID);
                dbPackingMaterial.setUpdatedBy(loginUserID);
                dbPackingMaterial.setCreatedOn(new Date());
                dbPackingMaterial.setUpdatedOn(new Date());
                return packingmaterialRepository.save(dbPackingMaterial);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updatePackingMaterial
     *
     * @param packingMaterialNo
     * @param updatePackingMaterial
     * @return
     */
    public PackingMaterial updatePackingMaterial(String packingMaterialNo, String companyCodeId, String plantId, String warehouseId,
                                                 String languageId, UpdatePackingMaterial updatePackingMaterial, String loginUserID) {
        try {
            PackingMaterial dbPackingMaterial = getPackingMaterial(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId);
            BeanUtils.copyProperties(updatePackingMaterial, dbPackingMaterial, CommonUtils.getNullPropertyNames(updatePackingMaterial));
            dbPackingMaterial.setUpdatedBy(loginUserID);
            dbPackingMaterial.setUpdatedOn(new Date());
            return packingmaterialRepository.save(dbPackingMaterial);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param packingMaterialNo
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param loginUserID
     */
    public void deletePackingMaterial(String packingMaterialNo, String companyCodeId, String plantId,
                                      String warehouseId, String languageId, String loginUserID) {
        try {
            PackingMaterial packingmaterial = getPackingMaterial(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId);
            if (packingmaterial != null) {
                packingmaterial.setDeletionIndicator(1L);
                packingmaterial.setUpdatedBy(loginUserID);
                packingmaterial.setUpdatedOn(new Date());
                packingmaterialRepository.save(packingmaterial);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + packingMaterialNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}