package com.tekclover.wms.api.masters.service;


import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.dock.AddDock;
import com.tekclover.wms.api.masters.model.dock.Dock;
import com.tekclover.wms.api.masters.model.dock.SearchDock;
import com.tekclover.wms.api.masters.model.dock.UpdateDock;
import com.tekclover.wms.api.masters.model.imbatchserial.AddImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.SearchImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.UpdateImBatchSerial;
import com.tekclover.wms.api.masters.model.workcenter.WorkCenter;
import com.tekclover.wms.api.masters.repository.DockRepository;
import com.tekclover.wms.api.masters.repository.ImBatchSerialRepository;
import com.tekclover.wms.api.masters.repository.specification.DockSpecification;
import com.tekclover.wms.api.masters.repository.specification.ImBatchSerialSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.print.Doc;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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
     * @return
     */
    public List<Dock> getAllDock () {
        List<Dock> dockList = dockRepository.findAll();
        log.info("dockList : " + dockList);
        dockList = dockList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return dockList;
    }

    /**
     * getDock
     * @param dockId
     * @param dockType
     * @return
     */
    public Dock getDock (String dockId, String companyCodeId, String plantId, String languageId, String warehouseId, String dockType) {
        Optional<Dock> dbDock = dockRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDockTypeAndDockIdAndDeletionIndicator(
               companyCodeId,
                plantId,
                languageId,
                warehouseId,
                dockType,
                dockId,
                0L);
        if(dbDock.isEmpty()) {
            throw new BadRequestException("The given Values : " +
                    "dockId -"+dockId+
                    "companyCodeId" +companyCodeId+
                    "plantId"+plantId+
                    "languageId"+languageId+
                    "dockType -"+ dockType + " doesn't exist.");
        }
        return dbDock.get();
    }


    /**
     *
     * @param searchDock
     * @return
     * @throws Exception
     */
    public List<Dock> findDock(SearchDock searchDock)
            throws Exception {
        DockSpecification spec = new DockSpecification(searchDock);
        List<Dock> results = dockRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * createImBatchSerial
     * @param newDock
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Dock createDock (AddDock newDock, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
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
    }

    /**
     * updateDock
     * @param dockId
     * @param dockType
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Dock updateDock (String companyCodeId, String plantId, String warehouseId, String languageId, String dockType, String dockId, UpdateDock updateDock, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Dock dbDock = getDock(dockId,companyCodeId,plantId,languageId,warehouseId,dockType);
        BeanUtils.copyProperties(updateDock, dbDock, CommonUtils.getNullPropertyNames(updateDock));
        dbDock.setUpdatedBy(loginUserID);
        dbDock.setUpdatedOn(new Date());
        return dockRepository.save(dbDock);
    }

    /**
     * deleteDock
     * @param dockId
     * @param dockType
     */
    public void deleteDock (String companyCodeId,String languageId,String plantId,String warehouseId,String dockId,String dockType,String loginUserID) throws ParseException {
        Dock dock = getDock(dockId,companyCodeId,plantId,languageId,warehouseId,dockType);
        if ( dock != null) {
            dock.setDeletionIndicator (1L);
            dock.setUpdatedBy(loginUserID);
            dock.setUpdatedOn(new Date());
            dockRepository.save(dock);
        } else {
            throw new EntityNotFoundException("Error in deleting Id:" + dockId);
        }
    }
}
