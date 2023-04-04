package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;


import com.tekclover.wms.api.idmaster.model.doorid.*;

import com.tekclover.wms.api.idmaster.repository.DoorIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoorIdService extends BaseService {
	
	@Autowired
	private DoorIdRepository doorIdRepository;
	
	/**
	 * getDoorIds
	 * @return
	 */
	public List<DoorId> getDoorIds () {
		List<DoorId> doorIdList =  doorIdRepository.findAll();
		doorIdList = doorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return doorIdList;
	}
	
	/**
	 * getDoorId
	 * @param doorId
	 * @return
	 */
	public DoorId getDoorId (String warehouseId, String doorId) {
		Optional<DoorId> dbDoorId = 
				doorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDoorIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								doorId,
								getLanguageId(),
								0L
								);
		if (dbDoorId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"doorId - " + doorId +
						" doesn't exist.");
			
		} 
		return dbDoorId.get();
	}
	
	/**
	 * createDoorId
	 * @param newDoorId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorId createDoorId (AddDoorId newDoorId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		DoorId dbDoorId = new DoorId();
		log.info("newDoorId : " + newDoorId);
		BeanUtils.copyProperties(newDoorId, dbDoorId, CommonUtils.getNullPropertyNames(newDoorId));
		dbDoorId.setCompanyCodeId(getCompanyCode());
		dbDoorId.setPlantId(getPlantId());
		dbDoorId.setDeletionIndicator(0L);
		dbDoorId.setCreatedBy(loginUserID);
		dbDoorId.setUpdatedBy(loginUserID);
		dbDoorId.setCreatedOn(new Date());
		dbDoorId.setUpdatedOn(new Date());
		return doorIdRepository.save(dbDoorId);
	}
	
	/**
	 * updateDoorId
	 * @param loginUserID
	 * @param doorId
	 * @param updateDoorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorId updateDoorId (String warehouseId, String doorId, String loginUserID,
			UpdateDoorId updateDoorId) 
			throws IllegalAccessException, InvocationTargetException {
		DoorId dbDoorId = getDoorId( warehouseId, doorId);
		BeanUtils.copyProperties(updateDoorId, dbDoorId, CommonUtils.getNullPropertyNames(updateDoorId));
		dbDoorId.setUpdatedBy(loginUserID);
		dbDoorId.setUpdatedOn(new Date());
		return doorIdRepository.save(dbDoorId);
	}
	
	/**
	 * deleteDoorId
	 * @param loginUserID 
	 * @param doorId
	 */
	public void deleteDoorId (String warehouseId, String doorId, String loginUserID) {
		DoorId dbDoorId = getDoorId( warehouseId, doorId);
		if ( dbDoorId != null) {
			dbDoorId.setDeletionIndicator(1L);
			dbDoorId.setUpdatedBy(loginUserID);
			doorIdRepository.save(dbDoorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + doorId);
		}
	}
}
