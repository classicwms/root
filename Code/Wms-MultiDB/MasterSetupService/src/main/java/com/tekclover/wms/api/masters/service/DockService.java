package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.dock.AddDock;
import com.tekclover.wms.api.masters.model.dock.Dock;
import com.tekclover.wms.api.masters.model.dock.SearchDock;
import com.tekclover.wms.api.masters.model.dock.UpdateDock;
import com.tekclover.wms.api.masters.repository.DockRepository;
import com.tekclover.wms.api.masters.repository.specification.DockSpecification;
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
public class DockService {

    @Autowired
    private DockRepository dockRepository;

    /**
     * getAllDock
     *
     * @return
     */
    public List<Dock> getAllDock() {
        try {
            List<Dock> dockList = dockRepository.findAll();
            log.info("dockList : " + dockList);
            dockList = dockList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return dockList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getDock
     *
     * @param dockId
     * @param dockType
     * @return
     */
    public Dock getDock(String dockId, String companyCodeId, String plantId, String languageId, String warehouseId, String dockType) {
        try {
            Optional<Dock> dbDock = dockRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDockTypeAndDockIdAndDeletionIndicator(
                    companyCodeId,
                    plantId,
                    languageId,
                    warehouseId,
                    dockType,
                    dockId,
                    0L);
            if (dbDock.isEmpty()) {
                throw new BadRequestException("The given Values : " +
                        "dockId -" + dockId +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId +
                        "languageId" + languageId +
                        "dockType -" + dockType + " doesn't exist.");
            }
            return dbDock.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * @param searchDock
     * @return
     */
    public List<Dock> findDock(SearchDock searchDock) {
        try {
            DockSpecification spec = new DockSpecification(searchDock);
            List<Dock> results = dockRepository.findAll(spec);
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
     * @param newDock
     * @return
     */
    public Dock createDock(AddDock newDock, String loginUserID) {
        try {
            Dock dbDock = new Dock();
            Optional<Dock> duplicateDock = dockRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDockTypeAndDockIdAndDeletionIndicator(
                    newDock.getCompanyCodeId(),
                    newDock.getLanguageId(),
                    newDock.getPlantId(),
                    newDock.getWarehouseId(),
                    newDock.getDockType(),
                    newDock.getDockId(),
                    0L);
            if (!duplicateDock.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newDock, dbDock, CommonUtils.getNullPropertyNames(newDock));
                dbDock.setDeletionIndicator(0L);
                dbDock.setCreatedBy(loginUserID);
                dbDock.setUpdatedBy(loginUserID);
                dbDock.setCreatedOn(new Date());
                dbDock.setUpdatedOn(new Date());
                return dockRepository.save(dbDock);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateDock
     *
     * @param dockId
     * @param dockType
     * @return
     */
    public Dock updateDock(String companyCodeId, String plantId, String warehouseId,
                           String languageId, String dockType, String dockId,
                           UpdateDock updateDock, String loginUserID) {
        try {
            Dock dbDock = getDock(dockId, companyCodeId, plantId, languageId, warehouseId, dockType);
            BeanUtils.copyProperties(updateDock, dbDock, CommonUtils.getNullPropertyNames(updateDock));
            dbDock.setUpdatedBy(loginUserID);
            dbDock.setUpdatedOn(new Date());
            return dockRepository.save(dbDock);
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
     * @param dockId
     * @param dockType
     * @param loginUserID
     */
    public void deleteDock(String companyCodeId, String languageId, String plantId,
                           String warehouseId, String dockId, String dockType, String loginUserID) {
        try {
            Dock dock = getDock(dockId, companyCodeId, plantId, languageId, warehouseId, dockType);
            if (dock != null) {
                dock.setDeletionIndicator(1L);
                dock.setUpdatedBy(loginUserID);
                dock.setUpdatedOn(new Date());
                dockRepository.save(dock);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + dockId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}