package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.storagetype.AddStorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.SearchStorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.StorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.UpdateStorageType;
import com.tekclover.wms.api.enterprise.repository.StorageTypeRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StorageTypeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageTypeService extends BaseService {
	
	@Autowired
	private StorageTypeRepository storagetypeRepository;
	
	/**
	 * getStorageTypes
	 * @return
	 */
	public List<StorageType> getStorageTypes () {
		List<StorageType> storagetypeList = storagetypeRepository.findAll();
		log.info("storagetypeList : " + storagetypeList);
		storagetypeList = storagetypeList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return storagetypeList;
	}
	
	/**
	 * getStorageType
	 * @param warehouseId
	 * @param storageClassId
	 * @param storageTypeId
	 * @return
	 */
	public StorageType getStorageType (String warehouseId, Long storageClassId, Long storageTypeId) {
		Optional<StorageType> storagetype = 
				storagetypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, storageClassId, storageTypeId, 0L);
		if (storagetype.isEmpty()) {
			throw new BadRequestException("The given StorageType Id : " + storageTypeId + " doesn't exist.");
		} 
		return storagetype.get();
	}
	
	/**
	 * findStorageType
	 * @param searchStorageType
	 * @return
	 * @throws ParseException
	 */
	public List<StorageType> findStorageType(SearchStorageType searchStorageType) throws Exception {
		if (searchStorageType.getStartCreatedOn() != null && searchStorageType.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageType.getStartCreatedOn(), searchStorageType.getEndCreatedOn());
			searchStorageType.setStartCreatedOn(dates[0]);
			searchStorageType.setEndCreatedOn(dates[1]);
		}
		
		StorageTypeSpecification spec = new StorageTypeSpecification(searchStorageType);
		List<StorageType> results = storagetypeRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStorageType
	 * @param newStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType createStorageType (AddStorageType newStorageType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<StorageType> optStorageType = 
				storagetypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newStorageType.getWarehouseId(),
						newStorageType.getStorageClassId(),
						newStorageType.getStorageTypeId(),
						0L);
		if (!optStorageType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		StorageType dbStorageType = new StorageType();
		BeanUtils.copyProperties(newStorageType, dbStorageType, CommonUtils.getNullPropertyNames(newStorageType));
		
		dbStorageType.setLanguageId(getLanguageId());
		dbStorageType.setCompanyId(getCompanyCode());
		dbStorageType.setPlantId(getPlantId());
		
		dbStorageType.setDeletionIndicator(0L);
		dbStorageType.setCreatedBy(loginUserID);
		dbStorageType.setUpdatedBy(loginUserID);
		dbStorageType.setCreatedOn(new Date());
		dbStorageType.setUpdatedOn(new Date());
		return storagetypeRepository.save(dbStorageType);
	}
	
	/**
	 * updateStorageType
	 * @param storagetypeCode
	 * @param updateStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType updateStorageType (String warehouseId, Long storageClassId, Long storageTypeId, UpdateStorageType updateStorageType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageType dbStorageType = getStorageType(warehouseId, storageClassId, storageTypeId);
		BeanUtils.copyProperties(updateStorageType, dbStorageType, CommonUtils.getNullPropertyNames(updateStorageType));
		dbStorageType.setUpdatedBy(loginUserID);
		dbStorageType.setUpdatedOn(new Date());
		return storagetypeRepository.save(dbStorageType);
	}
	
	/**
	 * deleteStorageType
	 * @param storagetypeCode
	 */
	public void deleteStorageType (String warehouseId, Long storageClassId, Long storageTypeId, String loginUserID) {
		StorageType storagetype = getStorageType(warehouseId, storageClassId, storageTypeId);
		if ( storagetype != null) {
			storagetype.setDeletionIndicator (1L);
			storagetype.setUpdatedBy(loginUserID);
			storagetype.setUpdatedOn(new Date());
			storagetypeRepository.save(storagetype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageTypeId);
		}
	}
}
