package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.doortype.AddDoorType;
import com.ustorage.api.master.model.doortype.DoorType;
import com.ustorage.api.master.model.doortype.UpdateDoorType;
import com.ustorage.api.master.repository.DoorTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoorTypeService {
	
	@Autowired
	private DoorTypeRepository doorTypeRepository;
	
	public List<DoorType> getDoorType () {
		List<DoorType> doorTypeList =  doorTypeRepository.findAll();
		doorTypeList = doorTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return doorTypeList;
	}
	
	/**
	 * getDoorType
	 * @param doorTypeId
	 * @return
	 */
	public DoorType getDoorType (String doorTypeId) {
		Optional<DoorType> doorType = doorTypeRepository.findByCodeAndDeletionIndicator(doorTypeId, 0L);
		if (doorType.isEmpty()) {
			return null;
		}
		return doorType.get();
	}
	
	/**
	 * createDoorType
	 * @param newDoorType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorType createDoorType (AddDoorType newDoorType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		DoorType dbDoorType = new DoorType();
		BeanUtils.copyProperties(newDoorType, dbDoorType, CommonUtils.getNullPropertyNames(newDoorType));
		dbDoorType.setDeletionIndicator(0L);
		dbDoorType.setCreatedBy(loginUserId);
		dbDoorType.setUpdatedBy(loginUserId);
		dbDoorType.setCreatedOn(new Date());
		dbDoorType.setUpdatedOn(new Date());
		return doorTypeRepository.save(dbDoorType);
	}
	
	/**
	 * updateDoorType
	 * @param doorTypeId
	 * @param loginUserId 
	 * @param updateDoorType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorType updateDoorType (String code, String loginUserId, UpdateDoorType updateDoorType)
			throws IllegalAccessException, InvocationTargetException {
		DoorType dbDoorType = getDoorType(code);
		BeanUtils.copyProperties(updateDoorType, dbDoorType, CommonUtils.getNullPropertyNames(updateDoorType));
		dbDoorType.setUpdatedBy(loginUserId);
		dbDoorType.setUpdatedOn(new Date());
		return doorTypeRepository.save(dbDoorType);
	}
	
	/**
	 * deleteDoorType
	 * @param loginUserID 
	 * @param doortypeCode
	 */
	public void deleteDoorType (String doortypeModuleId, String loginUserID) {
		DoorType doortype = getDoorType(doortypeModuleId);
		if (doortype != null) {
			doortype.setDeletionIndicator(1L);
			doortype.setUpdatedBy(loginUserID);
			doortype.setUpdatedOn(new Date());
			doorTypeRepository.save(doortype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + doortypeModuleId);
		}
	}
}
