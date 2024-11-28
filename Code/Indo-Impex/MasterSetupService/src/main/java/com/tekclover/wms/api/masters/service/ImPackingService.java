package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.impacking.AddImPacking;
import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import com.tekclover.wms.api.masters.model.impacking.SearchImPacking;
import com.tekclover.wms.api.masters.model.impacking.UpdateImPacking;
import com.tekclover.wms.api.masters.repository.ImPackingRepository;
import com.tekclover.wms.api.masters.repository.specification.ImPackingSpecification;
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
public class ImPackingService {

    @Autowired
    private ImPackingRepository impackingRepository;

    /**
     * getImPackings
     *
     * @return
     */
    public List<ImPacking> getImPackings() {
        try {
            List<ImPacking> impackingList = impackingRepository.findAll();
            log.info("impackingList : " + impackingList);
            impackingList = impackingList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return impackingList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getImPacking
     *
     * @param packingMaterialNo
     * @return
     */
    public ImPacking getImPacking(String packingMaterialNo, String companyCodeId, String plantId,
                                  String languageId, String warehouseId, String itemCode) {
        try {
            Optional<ImPacking> impacking =
                    impackingRepository.findByPackingMaterialNoAndCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                            packingMaterialNo,
                            companyCodeId,
                            languageId,
                            plantId,
                            warehouseId,
                            itemCode,
                            0L);
            if (impacking.isEmpty()) {
                throw new BadRequestException("The given values :" +
                        "packingMaterialNo" + packingMaterialNo +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "itemCode" + itemCode + "doesn't exist.");
            }
            return impacking.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findImPacking
     *
     * @param searchImPacking
     * @return
     */
    public List<ImPacking> findImPacking(SearchImPacking searchImPacking) {

        try {
            ImPackingSpecification spec = new ImPackingSpecification(searchImPacking);
            List<ImPacking> results = impackingRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createImPacking
     *
     * @param newImPacking
     * @return
     */
    public ImPacking createImPacking(AddImPacking newImPacking, String loginUserID) {
        try {
            ImPacking dbImPacking = new ImPacking();
            Optional<ImPacking> duplicateImPacking =
                    impackingRepository.findByPackingMaterialNoAndCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                            newImPacking.getPackingMaterialNo(), newImPacking.getCompanyCodeId(),
                            newImPacking.getLanguageId(), newImPacking.getPlantId(),
                            newImPacking.getWarehouseId(), newImPacking.getItemCode(), 0L);
            if (!duplicateImPacking.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicate");
            } else {
                BeanUtils.copyProperties(newImPacking, dbImPacking, CommonUtils.getNullPropertyNames(newImPacking));
                dbImPacking.setDeletionIndicator(0L);
                dbImPacking.setCreatedBy(loginUserID);
                dbImPacking.setUpdatedBy(loginUserID);
                dbImPacking.setCreatedOn(new Date());
                dbImPacking.setUpdatedOn(new Date());
                return impackingRepository.save(dbImPacking);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateImPacking
     *
     * @param packingMaterialNo
     * @param updateImPacking
     * @return
     */
    public ImPacking updateImPacking(String packingMaterialNo, String companyCodeId, String plantId, String languageId,
                                     String warehouseId, String itemCode, UpdateImPacking updateImPacking, String loginUserID) {
        try {
            ImPacking dbImPacking = getImPacking(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId, itemCode);
            BeanUtils.copyProperties(updateImPacking, dbImPacking, CommonUtils.getNullPropertyNames(updateImPacking));
            dbImPacking.setUpdatedBy(loginUserID);
            dbImPacking.setUpdatedOn(new Date());
            return impackingRepository.save(dbImPacking);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteImPacking
     *
     * @param packingMaterialNo
     */
    public void deleteImPacking(String packingMaterialNo, String companyCodeId, String plantId,
                                String languageId, String warehouseId, String itemCode, String loginUserID) {
        try {
            ImPacking impacking = getImPacking(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId, itemCode);
            if (impacking != null) {
                impacking.setDeletionIndicator(1L);
                impacking.setUpdatedBy(loginUserID);
                impacking.setUpdatedOn(new Date());
                impackingRepository.save(impacking);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + packingMaterialNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}