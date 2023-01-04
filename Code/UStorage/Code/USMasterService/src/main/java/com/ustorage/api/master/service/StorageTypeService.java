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

import com.ustorage.api.master.model.storagetype.AddStorageType;
import com.ustorage.api.master.model.storagetype.StorageType;
import com.ustorage.api.master.model.storagetype.UpdateStorageType;
import com.ustorage.api.master.repository.StorageTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageTypeService {
	
	@Autowired
	private StorageTypeRepository storageTypeRepository;
	
	public List<StorageType> getStorageType () {
		List<StorageType> storageTypeList =  storageTypeRepository.findAll();
		storageTypeList = storageTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageTypeList;
	}
	
	/**
	 * getStorageType
	 * @param storageTypeId
	 * @return
	 */
	public StorageType getStorageType (String storageTypeId) {
		Optional<StorageType> storageType = storageTypeRepository.findByCodeAndDeletionIndicator(storageTypeId, 0L);
		if (storageType.isEmpty()) {
			return null;
		}
		return storageType.get();
	}
	
	/**
	 * createStorageType
	 * @param newStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType createStorageType (AddStorageType newStorageType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		StorageType dbStorageType = new StorageType();
		BeanUtils.copyProperties(newStorageType, dbStorageType, CommonUtils.getNullPropertyNames(newStorageType));
		dbStorageType.setDeletionIndicator(0L);
		dbStorageType.setCreatedBy(loginUserId);
		dbStorageType.setUpdatedBy(loginUserId);
		dbStorageType.setCreatedOn(new Date());
		dbStorageType.setUpdatedOn(new Date());
		return storageTypeRepository.save(dbStorageType);
	}
	
	/**
	 * updateStorageType
	 * @param storageTypeId
	 * @param loginUserId 
	 * @param updateStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType updateStorageType (String code, String loginUserId, UpdateStorageType updateStorageType)
			throws IllegalAccessException, InvocationTargetException {
		StorageType dbStorageType = getStorageType(code);
		BeanUtils.copyProperties(updateStorageType, dbStorageType, CommonUtils.getNullPropertyNames(updateStorageType));
		dbStorageType.setUpdatedBy(loginUserId);
		dbStorageType.setUpdatedOn(new Date());
		return storageTypeRepository.save(dbStorageType);
	}
	
	/**
	 * deleteStorageType
	 * @param loginUserID 
	 * @param storagetypeCode
	 */
	public void deleteStorageType (String storagetypeModuleId, String loginUserID) {
		StorageType storagetype = getStorageType(storagetypeModuleId);
		if (storagetype != null) {
			storagetype.setDeletionIndicator(1L);
			storagetype.setUpdatedBy(loginUserID);
			storagetype.setUpdatedOn(new Date());
			storageTypeRepository.save(storagetype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storagetypeModuleId);
		}
	}
}
