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
import com.tekclover.wms.api.enterprise.model.storageclass.AddStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.SearchStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.UpdateStorageClass;
import com.tekclover.wms.api.enterprise.repository.StorageClassRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StorageClassSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageClassService extends BaseService {
	
	@Autowired
	private StorageClassRepository storageclassRepository;
	
	/**
	 * getStorageClasss
	 * @return
	 */
	public List<StorageClass> getStorageClasss () {
		List<StorageClass> storageclassList = storageclassRepository.findAll();
		log.info("storageclassList : " + storageclassList);
		storageclassList = storageclassList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return storageclassList;
	}
	
	/**
	 * getStorageClass
	 * @param warehouseId
	 * @param storageClassId
	 * @return
	 */
	public StorageClass getStorageClass (String warehouseId, Long storageClassId) {
		Optional<StorageClass> storageclass = 
				storageclassRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, storageClassId, 0L);
		if (storageclass.isEmpty()) {
			throw new BadRequestException("The given StorageClass Id : " + storageClassId + " doesn't exist.");
		} 
		return storageclass.get();
	}
	
	/**
	 * findStorageClass
	 * @param searchStorageClass
	 * @return
	 * @throws ParseException
	 */
	public List<StorageClass> findStorageClass(SearchStorageClass searchStorageClass) throws Exception {
		if (searchStorageClass.getStartCreatedOn() != null && searchStorageClass.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageClass.getStartCreatedOn(), searchStorageClass.getEndCreatedOn());
			searchStorageClass.setStartCreatedOn(dates[0]);
			searchStorageClass.setEndCreatedOn(dates[1]);
		}
		
		StorageClassSpecification spec = new StorageClassSpecification(searchStorageClass);
		List<StorageClass> results = storageclassRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStorageClass
	 * @param newStorageClass
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClass createStorageClass (AddStorageClass newStorageClass, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<StorageClass> optStorageClass = 
				storageclassRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newStorageClass.getWarehouseId(),
						newStorageClass.getStorageClassId(),
						0L);
		if (!optStorageClass.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		StorageClass dbStorageClass = new StorageClass();
		BeanUtils.copyProperties(newStorageClass, dbStorageClass, CommonUtils.getNullPropertyNames(newStorageClass));
		
		dbStorageClass.setLanguageId(getLanguageId());
		dbStorageClass.setCompanyId(getCompanyCode());
		dbStorageClass.setPlantId(getPlantId());
		
		dbStorageClass.setDeletionIndicator(0L);
		dbStorageClass.setCreatedBy(loginUserID);
		dbStorageClass.setUpdatedBy(loginUserID);
		dbStorageClass.setCreatedOn(new Date());
		dbStorageClass.setUpdatedOn(new Date());
		return storageclassRepository.save(dbStorageClass);
	}
	
	/**
	 * updateStorageClass
	 * @param storageclassCode
	 * @param updateStorageClass
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClass updateStorageClass (String warehouseId, Long storageClassId, UpdateStorageClass updateStorageClass, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageClass dbStorageClass = getStorageClass(warehouseId, storageClassId);
		BeanUtils.copyProperties(updateStorageClass, dbStorageClass, CommonUtils.getNullPropertyNames(updateStorageClass));
		dbStorageClass.setUpdatedBy(loginUserID);
		dbStorageClass.setUpdatedOn(new Date());
		return storageclassRepository.save(dbStorageClass);
	}
	
	/**
	 * deleteStorageClass
	 * @param storageclassCode
	 */
	public void deleteStorageClass (String warehouseId, Long storageClassId, String loginUserID) {
		StorageClass storageclass = getStorageClass(warehouseId, storageClassId);
		if ( storageclass != null) {
			storageclass.setDeletionIndicator (1L);
			storageclass.setUpdatedBy(loginUserID);
			storageclass.setUpdatedOn(new Date());
			storageclassRepository.save(storageclass);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageClassId);
		}
	}
}
