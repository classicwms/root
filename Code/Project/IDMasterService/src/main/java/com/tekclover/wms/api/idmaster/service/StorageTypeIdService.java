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
import com.tekclover.wms.api.idmaster.model.storagetypeid.AddStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.UpdateStorageTypeId;
import com.tekclover.wms.api.idmaster.repository.StorageTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageTypeIdService extends BaseService {
	
	@Autowired
	private StorageTypeIdRepository storageTypeIdRepository;
	
	/**
	 * getStorageTypeIds
	 * @return
	 */
	public List<StorageTypeId> getStorageTypeIds () {
		List<StorageTypeId> storageTypeIdList =  storageTypeIdRepository.findAll();
		storageTypeIdList = storageTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageTypeIdList;
	}
	
	/**
	 * getStorageTypeId
	 * @param storageTypeId
	 * @return
	 */
	public StorageTypeId getStorageTypeId (String warehouseId, Long storageClassId, Long storageTypeId) {
		Optional<StorageTypeId> dbStorageTypeId = 
				storageTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								storageClassId,
								storageTypeId,
								getLanguageId(),
								0L
								);
		if (dbStorageTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"storageClassId - " + storageClassId +
						"storageTypeId - " + storageTypeId +
						" doesn't exist.");
			
		} 
		return dbStorageTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStorageTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StorageTypeId> findStorageTypeId(SearchStorageTypeId searchStorageTypeId) 
//			throws ParseException {
//		StorageTypeIdSpecification spec = new StorageTypeIdSpecification(searchStorageTypeId);
//		List<StorageTypeId> results = storageTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStorageTypeId
	 * @param newStorageTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageTypeId createStorageTypeId (AddStorageTypeId newStorageTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageTypeId dbStorageTypeId = new StorageTypeId();
		log.info("newStorageTypeId : " + newStorageTypeId);
		BeanUtils.copyProperties(newStorageTypeId, dbStorageTypeId, CommonUtils.getNullPropertyNames(newStorageTypeId));
		dbStorageTypeId.setCompanyCodeId(getCompanyCode());
		dbStorageTypeId.setPlantId(getPlantId());
		dbStorageTypeId.setDeletionIndicator(0L);
		dbStorageTypeId.setCreatedBy(loginUserID);
		dbStorageTypeId.setUpdatedBy(loginUserID);
		dbStorageTypeId.setCreatedOn(new Date());
		dbStorageTypeId.setUpdatedOn(new Date());
		return storageTypeIdRepository.save(dbStorageTypeId);
	}
	
	/**
	 * updateStorageTypeId
	 * @param loginUserId 
	 * @param storageTypeId
	 * @param updateStorageTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageTypeId updateStorageTypeId (String warehouseId, Long storageClassId, Long storageTypeId, String loginUserID, 
			UpdateStorageTypeId updateStorageTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		StorageTypeId dbStorageTypeId = getStorageTypeId(warehouseId, storageClassId, storageTypeId);
		BeanUtils.copyProperties(updateStorageTypeId, dbStorageTypeId, CommonUtils.getNullPropertyNames(updateStorageTypeId));
		dbStorageTypeId.setUpdatedBy(loginUserID);
		dbStorageTypeId.setUpdatedOn(new Date());
		return storageTypeIdRepository.save(dbStorageTypeId);
	}
	
	/**
	 * deleteStorageTypeId
	 * @param loginUserID 
	 * @param storageTypeId
	 */
	public void deleteStorageTypeId (String warehouseId, Long storageClassId, Long storageTypeId, String loginUserID) {
		StorageTypeId dbStorageTypeId = getStorageTypeId(warehouseId, storageClassId, storageTypeId);
		if ( storageTypeId != null) {
			dbStorageTypeId.setDeletionIndicator(1L);
			dbStorageTypeId.setUpdatedBy(loginUserID);
			storageTypeIdRepository.save(dbStorageTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageTypeId);
		}
	}
}
