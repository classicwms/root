package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.storageclassid.AddStorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.UpdateStorageClassId;
import com.tekclover.wms.api.idmaster.repository.StorageClassIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageClassIdService extends BaseService {
	
	@Autowired
	private StorageClassIdRepository storageClassIdRepository;
	
	/**
	 * getStorageClassIds
	 * @return
	 */
	public List<StorageClassId> getStorageClassIds () {
		List<StorageClassId> storageClassIdList =  storageClassIdRepository.findAll();
		storageClassIdList = storageClassIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageClassIdList;
	}
	
	/**
	 * getStorageClassId
	 * @param storageClassId
	 * @return
	 */
	public StorageClassId getStorageClassId (String warehouseId, Long storageClassId) {
		Optional<StorageClassId> dbStorageClassId = 
				storageClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								storageClassId,
								getLanguageId(),
								0L
								);
		if (dbStorageClassId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"storageClassId - " + storageClassId +
						" doesn't exist.");
			
		} 
		return dbStorageClassId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStorageClassId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StorageClassId> findStorageClassId(SearchStorageClassId searchStorageClassId) 
//			throws ParseException {
//		StorageClassIdSpecification spec = new StorageClassIdSpecification(searchStorageClassId);
//		List<StorageClassId> results = storageClassIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStorageClassId
	 * @param newStorageClassId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClassId createStorageClassId (AddStorageClassId newStorageClassId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageClassId dbStorageClassId = new StorageClassId();
		log.info("newStorageClassId : " + newStorageClassId);
		BeanUtils.copyProperties(newStorageClassId, dbStorageClassId, CommonUtils.getNullPropertyNames(newStorageClassId));
		dbStorageClassId.setCompanyCodeId(getCompanyCode());
		dbStorageClassId.setPlantId(getPlantId());
		dbStorageClassId.setDeletionIndicator(0L);
		dbStorageClassId.setCreatedBy(loginUserID);
		dbStorageClassId.setUpdatedBy(loginUserID);
		dbStorageClassId.setCreatedOn(new Date());
		dbStorageClassId.setUpdatedOn(new Date());
		return storageClassIdRepository.save(dbStorageClassId);
	}
	
	/**
	 * updateStorageClassId
	 * @param loginUserId 
	 * @param storageClassId
	 * @param updateStorageClassId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClassId updateStorageClassId (String warehouseId, Long storageClassId, String loginUserID, 
			UpdateStorageClassId updateStorageClassId) 
			throws IllegalAccessException, InvocationTargetException {
		StorageClassId dbStorageClassId = getStorageClassId(warehouseId, storageClassId);
		BeanUtils.copyProperties(updateStorageClassId, dbStorageClassId, CommonUtils.getNullPropertyNames(updateStorageClassId));
		dbStorageClassId.setUpdatedBy(loginUserID);
		dbStorageClassId.setUpdatedOn(new Date());
		return storageClassIdRepository.save(dbStorageClassId);
	}
	
	/**
	 * deleteStorageClassId
	 * @param loginUserID 
	 * @param storageClassId
	 */
	public void deleteStorageClassId (String warehouseId, Long storageClassId, String loginUserID) {
		StorageClassId dbStorageClassId = getStorageClassId(warehouseId, storageClassId);
		if ( dbStorageClassId != null) {
			dbStorageClassId.setDeletionIndicator(1L);
			dbStorageClassId.setUpdatedBy(loginUserID);
			storageClassIdRepository.save(dbStorageClassId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageClassId);
		}
	}
}
