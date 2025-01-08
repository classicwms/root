package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.AddCycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.CycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.SearchCycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.UpdateCycleCountScheduler;
import com.tekclover.wms.api.masters.repository.CycleCountSchedulerRepository;
import com.tekclover.wms.api.masters.repository.specification.CycleCountSchedulerSpecification;
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
public class CycleCountSchedulerService {
    @Autowired
    private CycleCountSchedulerRepository cycleCountSchedulerRepository;

    /**
     * @return
     */
    public List<CycleCountScheduler> getAllCycleCountScheduler() {
        try {
            List<CycleCountScheduler> cycleCountSchedulerList = cycleCountSchedulerRepository.findAll();
            log.info("cycleCountScheduler : " + cycleCountSchedulerList);
            cycleCountSchedulerList = cycleCountSchedulerList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return cycleCountSchedulerList;
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
     * @param levelId
     * @param cycleCountTypeId
     * @param schedulerNumber
     * @return
     */
    public CycleCountScheduler getCycleCountScheduler(String companyCodeId, String languageId, String warehouseId,
                                                      String plantId, Long levelId, Long cycleCountTypeId, String schedulerNumber) {
        try {
            Optional<CycleCountScheduler> cycleCountScheduler =
                    cycleCountSchedulerRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndSchedulerNumberAndLevelIdAndDeletionIndicator(
                            companyCodeId,
                            languageId,
                            plantId,
                            warehouseId,
                            cycleCountTypeId,
                            schedulerNumber,
                            levelId,
                            0L
                    );
            if (cycleCountScheduler.isEmpty()) {
                throw new BadRequestException("The given values:" +
                        "companyCodeId" + companyCodeId +
                        "languageId" + languageId +
                        "plantId" + plantId +
                        "warehouseId" + warehouseId +
                        "levelId" + levelId +
                        "schedulerNumber" + schedulerNumber +
                        "cycleCountTypeId" + cycleCountTypeId + "doesn't exists");
            }
            return cycleCountScheduler.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newCycleCountScheduler
     * @param loginUserID
     * @return
     */
    public CycleCountScheduler createCycleCountScheduler(AddCycleCountScheduler newCycleCountScheduler, String loginUserID) {
        try {
            CycleCountScheduler cycleCountScheduler = new CycleCountScheduler();
            Optional<CycleCountScheduler> duplicateCycleCountScheduler = cycleCountSchedulerRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndSchedulerNumberAndLevelIdAndDeletionIndicator(
                    newCycleCountScheduler.getCompanyCodeId(),
                    newCycleCountScheduler.getPlantId(),
                    newCycleCountScheduler.getLanguageId(),
                    newCycleCountScheduler.getWarehouseId(),
                    newCycleCountScheduler.getCycleCountTypeId(),
                    newCycleCountScheduler.getSchedulerNumber(),
                    newCycleCountScheduler.getLevelId(),
                    0L);
            if (!duplicateCycleCountScheduler.isEmpty()) {
                throw new EntityNotFoundException("The Record is Getting Duplicate");
            } else {
                BeanUtils.copyProperties(newCycleCountScheduler, cycleCountScheduler, CommonUtils.getNullPropertyNames(newCycleCountScheduler));
                cycleCountScheduler.setDeletionIndicator(0L);
                cycleCountScheduler.setCreatedBy(loginUserID);
                cycleCountScheduler.setUpdatedBy(loginUserID);
                cycleCountScheduler.setCreatedOn(new Date());
                cycleCountScheduler.setUpdatedOn(new Date());
                return cycleCountSchedulerRepository.save(cycleCountScheduler);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateCycleCountScheduler
     *
     * @param levelId
     * @param cycleCountTypeId
     * @param schedulerNumber
     * @return
     */
    public CycleCountScheduler updateCycleCountScheduler(String companyCodeId, String languageId, String plantId, String warehouseId, Long levelId,
                                                         Long cycleCountTypeId, String schedulerNumber, UpdateCycleCountScheduler updateCycleCountScheduler, String loginUserID) {
        try {
            CycleCountScheduler dbCycleCountScheduler = getCycleCountScheduler(companyCodeId, languageId, warehouseId, plantId, levelId, cycleCountTypeId, schedulerNumber);
            BeanUtils.copyProperties(updateCycleCountScheduler, dbCycleCountScheduler, CommonUtils.getNullPropertyNames(updateCycleCountScheduler));
            dbCycleCountScheduler.setUpdatedBy(loginUserID);
            dbCycleCountScheduler.setUpdatedOn(new Date());
            return cycleCountSchedulerRepository.save(dbCycleCountScheduler);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteCycleCountScheduler
     *
     * @param cycleCountTypeId
     */
    public void deleteCycleCountScheduler(String companyCodeId, String languageId, String plantId,
                                          String warehouseId, Long levelId, Long cycleCountTypeId,
                                          String schedulerNumber, String loginUserID) {
        try {
            CycleCountScheduler cycleCountScheduler = getCycleCountScheduler(companyCodeId, languageId, warehouseId, plantId, levelId, cycleCountTypeId, schedulerNumber);
            if (cycleCountScheduler != null) {
                cycleCountScheduler.setDeletionIndicator(1L);
                cycleCountScheduler.setUpdatedBy(loginUserID);
                cycleCountScheduler.setUpdatedOn(new Date());
                cycleCountSchedulerRepository.save(cycleCountScheduler);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + cycleCountTypeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchCycleCountScheduler
     * @return
     */
    public List<CycleCountScheduler> findCycleCountScheduler(SearchCycleCountScheduler searchCycleCountScheduler) {
        try {
            CycleCountSchedulerSpecification spec = new CycleCountSchedulerSpecification(searchCycleCountScheduler);
            List<CycleCountScheduler> results = cycleCountSchedulerRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}