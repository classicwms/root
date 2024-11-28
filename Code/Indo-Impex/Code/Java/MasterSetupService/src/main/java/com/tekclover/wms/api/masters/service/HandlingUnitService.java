package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.handlingunit.AddHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.HandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.SearchHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.UpdateHandlingUnit;
import com.tekclover.wms.api.masters.repository.HandlingUnitRepository;
import com.tekclover.wms.api.masters.repository.specification.HandlingUnitSpecification;
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
public class HandlingUnitService {

    @Autowired
    private HandlingUnitRepository handlingunitRepository;

    /**
     * getHandlingUnits
     *
     * @return
     */
    public List<HandlingUnit> getHandlingUnits() {
        try {
            List<HandlingUnit> handlingunitList = handlingunitRepository.findAll();
//		log.info("handlingunitList : " + handlingunitList);
            handlingunitList = handlingunitList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return handlingunitList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getHandlingUnit
     *
     * @param handlingUnit
     * @return
     */
    public HandlingUnit getHandlingUnit(String warehouseId, String handlingUnit, String companyCodeId, String languageId, String plantId) {
        try {
            Optional<HandlingUnit> dbHandlingUnitId =
                    handlingunitRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitAndLanguageIdAndDeletionIndicator(
                            companyCodeId,
                            plantId,
                            warehouseId,
                            handlingUnit,
                            languageId,
                            0L
                    );
            if (dbHandlingUnitId.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "warehouseId - " + warehouseId +
                        "handlingUnitId - " + handlingUnit +
                        "doesn't exist.");

            }
            return dbHandlingUnitId.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchHandlingUnit
     * @return
     */
    public List<HandlingUnit> findHandlingUnit(SearchHandlingUnit searchHandlingUnit) {
        try {
            if (searchHandlingUnit.getStartCreatedOn() != null && searchHandlingUnit.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingUnit.getStartCreatedOn(), searchHandlingUnit.getEndCreatedOn());
                searchHandlingUnit.setStartCreatedOn(dates[0]);
                searchHandlingUnit.setEndCreatedOn(dates[1]);
            }

            if (searchHandlingUnit.getStartUpdatedOn() != null && searchHandlingUnit.getEndUpdatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingUnit.getStartUpdatedOn(), searchHandlingUnit.getEndUpdatedOn());
                searchHandlingUnit.setStartUpdatedOn(dates[0]);
                searchHandlingUnit.setEndUpdatedOn(dates[1]);
            }

            HandlingUnitSpecification spec = new HandlingUnitSpecification(searchHandlingUnit);
            List<HandlingUnit> results = handlingunitRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createHandlingUnit
     *
     * @param newHandlingUnit
     * @return
     */
    public HandlingUnit createHandlingUnit(AddHandlingUnit newHandlingUnit, String loginUserID) {
        try {
            HandlingUnit dbHandlingUnit = new HandlingUnit();
            Optional<HandlingUnit> duplicateHandlingUnitId =
                    handlingunitRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitAndLanguageIdAndDeletionIndicator(
                            newHandlingUnit.getCompanyCodeId(), newHandlingUnit.getPlantId(), newHandlingUnit.getWarehouseId(),
                            newHandlingUnit.getHandlingUnit(), newHandlingUnit.getLanguageId(), 0L);
            if (!duplicateHandlingUnitId.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicate");
            } else {
                BeanUtils.copyProperties(newHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(newHandlingUnit));
                dbHandlingUnit.setDeletionIndicator(0L);
                dbHandlingUnit.setCreatedBy(loginUserID);
                dbHandlingUnit.setUpdatedBy(loginUserID);
                dbHandlingUnit.setCreatedOn(new Date());
                dbHandlingUnit.setUpdatedOn(new Date());
                return handlingunitRepository.save(dbHandlingUnit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateHandlingUnit
     *
     * @param handlingUnit
     * @param updateHandlingUnit
     * @return
     */
    public HandlingUnit updateHandlingUnit(String handlingUnit, String companyCodeId, String plantId, String warehouseId,
                                           String languageId, UpdateHandlingUnit updateHandlingUnit, String loginUserID) {
        try {
            HandlingUnit dbHandlingUnit = getHandlingUnit(warehouseId, handlingUnit, companyCodeId, languageId, plantId);
            BeanUtils.copyProperties(updateHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(updateHandlingUnit));
            dbHandlingUnit.setUpdatedBy(loginUserID);
            dbHandlingUnit.setUpdatedOn(new Date());
            return handlingunitRepository.save(dbHandlingUnit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteHandlingUnit
     *
     * @param handlingUnit
     */
    public void deleteHandlingUnit(String handlingUnit, String companyCodeId, String plantId,
                                   String languageId, String warehouseId, String loginUserID) {
        try {
            HandlingUnit handlingunit = getHandlingUnit(warehouseId, handlingUnit, companyCodeId, languageId, plantId);
            if (handlingunit != null) {
                handlingunit.setDeletionIndicator(1L);
                handlingunit.setUpdatedBy(loginUserID);
                handlingunit.setUpdatedOn(new Date());
                handlingunitRepository.save(handlingunit);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + handlingUnit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}