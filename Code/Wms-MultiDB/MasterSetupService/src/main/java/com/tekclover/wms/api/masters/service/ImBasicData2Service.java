package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imbasicdata2.AddImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.SearchImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.UpdateImBasicData2;
import com.tekclover.wms.api.masters.repository.ImBasicData2Repository;
import com.tekclover.wms.api.masters.repository.specification.ImBasicData2Specification;
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
public class ImBasicData2Service {

    @Autowired
    private ImBasicData2Repository imbasicdata2Repository;

    /**
     * getImBasicData2s
     *
     * @return
     */
    public List<ImBasicData2> getImBasicData2s() {
        try {
            List<ImBasicData2> imbasicdata2List = imbasicdata2Repository.findAll();
//		log.info("imbasicdata2List : " + imbasicdata2List);
            imbasicdata2List = imbasicdata2List.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return imbasicdata2List;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getImBasicData2
     *
     * @param itemCode
     * @return
     */
    public ImBasicData2 getImBasicData2(String itemCode, String companyCodeId, String plantId, String warehouseId, String languageId) {
        try {
            Optional<ImBasicData2> imbasicdata2 = imbasicdata2Repository.findByItemCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                    itemCode,
                    companyCodeId,
                    plantId,
                    warehouseId,
                    languageId,
                    0L);
            if (imbasicdata2.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "itemCode" + itemCode +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "doesn't exist.");
            }
            return imbasicdata2.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findImBasicData2
     *
     * @param searchImBasicData2
     * @return
     */
    public List<ImBasicData2> findImBasicData2(SearchImBasicData2 searchImBasicData2) {

        try {
            ImBasicData2Specification spec = new ImBasicData2Specification(searchImBasicData2);
            List<ImBasicData2> results = imbasicdata2Repository.findAll(spec);
//		log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createImBasicData2
     *
     * @param newImBasicData2
     * @return
     */
    public ImBasicData2 createImBasicData2(AddImBasicData2 newImBasicData2, String loginUserID) {
        try {
            ImBasicData2 dbImBasicData2 = new ImBasicData2();
            Optional<ImBasicData2> duplicateImBasicData2 =
                    imbasicdata2Repository.findByItemCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
                            newImBasicData2.getItemCode(), newImBasicData2.getCompanyCodeId(), newImBasicData2.getPlantId(),
                            newImBasicData2.getWarehouseId(), newImBasicData2.getLanguageId(), 0L);
            if (duplicateImBasicData2.isPresent()) {
                throw new EntityNotFoundException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(newImBasicData2));
                dbImBasicData2.setDeletionIndicator(0L);
                dbImBasicData2.setCreatedBy(loginUserID);
                dbImBasicData2.setUpdatedBy(loginUserID);
                dbImBasicData2.setCreatedOn(new Date());
                dbImBasicData2.setUpdatedOn(new Date());
                return imbasicdata2Repository.save(dbImBasicData2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param itemCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param updateImBasicData2
     * @param loginUserID
     * @return
     */
    public ImBasicData2 updateImBasicData2(String itemCode, String companyCodeId, String plantId, String warehouseId,
                                           String languageId, UpdateImBasicData2 updateImBasicData2, String loginUserID) {
        try {
            ImBasicData2 dbImBasicData2 = getImBasicData2(itemCode, companyCodeId, plantId, warehouseId, languageId);
            BeanUtils.copyProperties(updateImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(updateImBasicData2));
            dbImBasicData2.setUpdatedBy(loginUserID);
            dbImBasicData2.setUpdatedOn(new Date());
            return imbasicdata2Repository.save(dbImBasicData2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param itemCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param loginUserID
     */
    public void deleteImBasicData2(String itemCode, String companyCodeId, String plantId,
                                   String warehouseId, String languageId, String loginUserID) {
        try {
            ImBasicData2 imbasicdata2 = getImBasicData2(itemCode, companyCodeId, plantId, warehouseId, languageId);
            if (imbasicdata2 != null) {
                imbasicdata2.setDeletionIndicator(1L);
                imbasicdata2.setUpdatedBy(loginUserID);
                imbasicdata2.setUpdatedOn(new Date());
                imbasicdata2Repository.save(imbasicdata2);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + itemCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}