package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.doors.AddDoors;
import com.tekclover.wms.api.enterprise.model.doors.Doors;
import com.tekclover.wms.api.enterprise.model.doors.UpdateDoors;
import com.tekclover.wms.api.enterprise.repository.DoorsRepository;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoorsService extends BaseService {

    @Autowired
    private DoorsRepository doorsRepository;

    /**
     * getDoorss
     *
     * @return
     */
    public List<Doors> getDoorss() {
        try {
            List<Doors> doorsList = doorsRepository.findAll();
            log.info("doorsList : " + doorsList);
            doorsList = doorsList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return doorsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param doorId
     * @return
     */
    public Doors getDoors(String doorId) {
        try {
            Doors doors = doorsRepository.findByDoorId(doorId).orElse(null);
            if (doors != null && doors.getDeletionIndicator() != null && doors.getDeletionIndicator() == 0) {
                return doors;
            } else {
                throw new BadRequestException("The given Doors ID : " + doorId + " doesn't exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newDoors
     * @param loginUserID
     * @return
     */
    public Doors createDoors(AddDoors newDoors, String loginUserID) {
        try {
            Doors dbDoors = new Doors();
            BeanUtils.copyProperties(newDoors, dbDoors, CommonUtils.getNullPropertyNames(newDoors));
            dbDoors.setDeletionIndicator(0L);
            dbDoors.setCompanyId(getCompanyCode());
            dbDoors.setCreatedBy(loginUserID);
            dbDoors.setUpdatedBy(loginUserID);
            dbDoors.setCreatedOn(new Date());
            dbDoors.setUpdatedOn(new Date());
            return doorsRepository.save(dbDoors);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param doorNumber
     * @param updateDoors
     * @param loginUserID
     * @return
     */
    public Doors updateDoors(String doorNumber, UpdateDoors updateDoors, String loginUserID) {
        try {
            Doors dbDoors = getDoors(doorNumber);
            BeanUtils.copyProperties(updateDoors, dbDoors, CommonUtils.getNullPropertyNames(updateDoors));
            dbDoors.setUpdatedBy(loginUserID);
            dbDoors.setUpdatedOn(new Date());
            return doorsRepository.save(dbDoors);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param doorNumber
     * @param loginUserID
     */
    public void deleteDoors(String doorNumber, String loginUserID) {
        try {
            Doors doors = getDoors(doorNumber);
            if (doors != null) {
                doors.setDeletionIndicator(1L);
                doors.setUpdatedBy(loginUserID);
                doors.setUpdatedOn(new Date());
                doorsRepository.save(doors);
            } else {
                throw new EntityNotFoundException(String.valueOf(doorNumber));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}