package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.workcenter.AddWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.SearchWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.UpdateWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.WorkCenter;
import com.tekclover.wms.api.masters.repository.WorkCenterRepository;
import com.tekclover.wms.api.masters.repository.specification.WorkCenterSpecification;
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
public class WorkCenterService {
    @Autowired
    private WorkCenterRepository workCenterRepository;

    /**
     * getAllWorkCenter
     *
     * @return
     */
    public List<WorkCenter> getAllWorkCenter() {
        try {
            List<WorkCenter> workCenterList = workCenterRepository.findAll();
//        log.info("workCenterList : " + workCenterList);
            workCenterList = workCenterList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return workCenterList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getWorkCenterId
     *
     * @param workCenterId
     * @return
     */
    public WorkCenter getWorkCenter(Long workCenterId, String companyCodeId, String plantId, String languageId, String warehouseId, String workCenterType) {
        try {
            Optional<WorkCenter> dbWorkCenterId = workCenterRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndWorkCenterTypeAndDeletionIndicator(
                    companyCodeId,
                    languageId,
                    plantId,
                    warehouseId,
                    workCenterId,
                    workCenterType,
                    0L);
            if (dbWorkCenterId.isEmpty()) {
                throw new BadRequestException("The given Values : " +
                        "workCenterId -" + workCenterId +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "languageId" + languageId +
                        "workCenterType -" + workCenterType + " doesn't exist.");
            }
            return dbWorkCenterId.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findWorkCenterId
     *
     * @param searchWorkCenter
     * @return
     */
    public List<WorkCenter> findWorkCenter(SearchWorkCenter searchWorkCenter) {

        try {
            WorkCenterSpecification spec = new WorkCenterSpecification(searchWorkCenter);
            List<WorkCenter> results = workCenterRepository.findAll(spec);
//        log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createWorkCenterId
     *
     * @param newWorkCenter
     * @return
     */
    public WorkCenter createWorkCenter(AddWorkCenter newWorkCenter, String loginUserID) {
        try {
            WorkCenter dbWorkCenter = new WorkCenter();
            Optional<WorkCenter> duplicateWorkCenter =
                    workCenterRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndWorkCenterTypeAndDeletionIndicator(newWorkCenter.getCompanyCodeId(), newWorkCenter.getPlantId(),
                            newWorkCenter.getWarehouseId(), newWorkCenter.getLanguageId(), newWorkCenter.getWorkCenterId(), newWorkCenter.getWorkCenterType(), 0L);

            if (!duplicateWorkCenter.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newWorkCenter, dbWorkCenter, CommonUtils.getNullPropertyNames(newWorkCenter));
                dbWorkCenter.setDeletionIndicator(0L);
                dbWorkCenter.setCreatedBy(loginUserID);
                dbWorkCenter.setUpdatedBy(loginUserID);
                dbWorkCenter.setCreatedOn(new Date());
                dbWorkCenter.setUpdatedOn(new Date());
                return workCenterRepository.save(dbWorkCenter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateWorkCenter
     *
     * @param workCenterId
     * @param updateWorkCenter
     * @return
     */
    public WorkCenter updateWorkCenter(Long workCenterId, String companyCodeId, String plantId, String warehouseId,
                                       String workCenterType, String languageId, UpdateWorkCenter updateWorkCenter, String loginUserID) {
        try {
            WorkCenter dbWorkCenter = getWorkCenter(workCenterId, companyCodeId, plantId, languageId, warehouseId, workCenterType);
            BeanUtils.copyProperties(updateWorkCenter, dbWorkCenter, CommonUtils.getNullPropertyNames(updateWorkCenter));
            dbWorkCenter.setUpdatedBy(loginUserID);
            dbWorkCenter.setUpdatedOn(new Date());
            return workCenterRepository.save(dbWorkCenter);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteWorkCenterId
     *
     * @param workCenterId
     * @param workCenterType
     */
    public void deleteWorkCenterId(Long workCenterId, String companyCodeId, String plantId, String warehouseId,
                                   String workCenterType, String languageId, String loginUserID) {
        try {
            WorkCenter workCenter = getWorkCenter(workCenterId, companyCodeId, plantId, languageId, warehouseId, workCenterType);
            if (workCenter != null) {
                workCenter.setDeletionIndicator(1L);
                workCenter.setUpdatedBy(loginUserID);
                workCenter.setUpdatedOn(new Date());
                workCenterRepository.save(workCenter);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + workCenterId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


}