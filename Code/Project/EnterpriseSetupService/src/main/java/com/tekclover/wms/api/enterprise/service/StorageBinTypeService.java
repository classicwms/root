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
import com.tekclover.wms.api.enterprise.model.storagebintype.AddStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.UpdateStorageBinType;
import com.tekclover.wms.api.enterprise.repository.StorageBinTypeRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StorageBinTypeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageBinTypeService extends BaseService {
	
	@Autowired
	private StorageBinTypeRepository storagebintypeRepository;
	
	/**
	 * getStorageBinTypes
	 * @return
	 */
	public List<StorageBinType> getStorageBinTypes () {
		List<StorageBinType> storagebintypeList = storagebintypeRepository.findAll();
		log.info("storagebintypeList : " + storagebintypeList);
		storagebintypeList = storagebintypeList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return storagebintypeList;
	}
	
	/**
	 * getStorageBinType
	 * @param warehouseId
	 * @param storageTypeId
	 * @param storageBinTypeId
	 * @return
	 */
	public StorageBinType getStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId) {
		Optional<StorageBinType> storagebintype = 
				storagebintypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageBinTypeIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, storageTypeId, storageBinTypeId, 0L);
		if (storagebintype.isEmpty()) {
			throw new BadRequestException("The given StorageBinType Id : " + storageBinTypeId + " doesn't exist.");
		} 
		return storagebintype.get();
	}
	
	/**
	 * findStorageBinType
	 * @param searchStorageBinType
	 * @return
	 * @throws ParseException
	 */
	public List<StorageBinType> findStorageBinType(SearchStorageBinType searchStorageBinType) throws Exception {
		if (searchStorageBinType.getStartCreatedOn() != null && searchStorageBinType.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageBinType.getStartCreatedOn(), searchStorageBinType.getEndCreatedOn());
			searchStorageBinType.setStartCreatedOn(dates[0]);
			searchStorageBinType.setEndCreatedOn(dates[1]);
		}
		
		StorageBinTypeSpecification spec = new StorageBinTypeSpecification(searchStorageBinType);
		List<StorageBinType> results = storagebintypeRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStorageBinType
	 * @param newStorageBinType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinType createStorageBinType (AddStorageBinType newStorageBinType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<StorageBinType> optStorageBinType = 
				storagebintypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageBinTypeIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newStorageBinType.getWarehouseId(),
						newStorageBinType.getStorageTypeId(),
						newStorageBinType.getStorageBinTypeId(),
						0L);
		if (!optStorageBinType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		StorageBinType dbStorageBinType = new StorageBinType();
		BeanUtils.copyProperties(newStorageBinType, dbStorageBinType, CommonUtils.getNullPropertyNames(newStorageBinType));
		
		dbStorageBinType.setLanguageId(getLanguageId());
		dbStorageBinType.setCompanyId(getCompanyCode());
		dbStorageBinType.setPlantId(getPlantId());
		
		dbStorageBinType.setDeletionIndicator(0L);
		dbStorageBinType.setCreatedBy(loginUserID);
		dbStorageBinType.setUpdatedBy(loginUserID);
		dbStorageBinType.setCreatedOn(new Date());
		dbStorageBinType.setUpdatedOn(new Date());
		return storagebintypeRepository.save(dbStorageBinType);
	}
	
	/**
	 * updateStorageBinType
	 * @param storagebintypeCode
	 * @param updateStorageBinType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinType updateStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId, UpdateStorageBinType updateStorageBinType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBinType dbStorageBinType = getStorageBinType(warehouseId, storageTypeId, storageBinTypeId);
		BeanUtils.copyProperties(updateStorageBinType, dbStorageBinType, CommonUtils.getNullPropertyNames(updateStorageBinType));
		dbStorageBinType.setUpdatedBy(loginUserID);
		dbStorageBinType.setUpdatedOn(new Date());
		return storagebintypeRepository.save(dbStorageBinType);
	}
	
	/**
	 * deleteStorageBinType
	 * @param storagebintypeCode
	 */
	public void deleteStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId, String loginUserID) {
		StorageBinType storagebintype = getStorageBinType(warehouseId, storageTypeId, storageBinTypeId);
		if ( storagebintype != null) {
			storagebintype.setDeletionIndicator (1L);
			storagebintype.setUpdatedBy(loginUserID);
			storagebintype.setUpdatedOn(new Date());
			storagebintypeRepository.save(storagebintype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageBinTypeId);
		}
	}
}
