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
import com.tekclover.wms.api.idmaster.model.storagebintypeid.AddStorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.StorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.UpdateStorageBinTypeId;
import com.tekclover.wms.api.idmaster.repository.StorageBinTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageBinTypeIdService extends BaseService {
	
	@Autowired
	private StorageBinTypeIdRepository storageBinTypeIdRepository;
	
	/**
	 * getStorageBinTypeIds
	 * @return
	 */
	public List<StorageBinTypeId> getStorageBinTypeIds () {
		List<StorageBinTypeId> storageBinTypeIdList =  storageBinTypeIdRepository.findAll();
		storageBinTypeIdList = storageBinTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageBinTypeIdList;
	}
	
	/**
	 * getStorageBinTypeId
	 * @param storageBinTypeId
	 * @return
	 */
	public StorageBinTypeId getStorageBinTypeId (String warehouseId, Long storageBinTypeId, Long storageClassId, Long storageTypeId) {
		Optional<StorageBinTypeId> dbStorageBinTypeId = 
				storageBinTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndStorageBinTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								storageClassId,
								storageTypeId,
								storageBinTypeId,
								getLanguageId(),
								0L
								);
		if (dbStorageBinTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"storageClassId - " + storageClassId +
						"storageTypeId - " + storageTypeId +
						"storageBinTypeId - " + storageBinTypeId +
						" doesn't exist.");
			
		} 
		return dbStorageBinTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStorageBinTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StorageBinTypeId> findStorageBinTypeId(SearchStorageBinTypeId searchStorageBinTypeId) 
//			throws ParseException {
//		StorageBinTypeIdSpecification spec = new StorageBinTypeIdSpecification(searchStorageBinTypeId);
//		List<StorageBinTypeId> results = storageBinTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStorageBinTypeId
	 * @param newStorageBinTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinTypeId createStorageBinTypeId (AddStorageBinTypeId newStorageBinTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBinTypeId dbStorageBinTypeId = new StorageBinTypeId();
		log.info("newStorageBinTypeId : " + newStorageBinTypeId);
		BeanUtils.copyProperties(newStorageBinTypeId, dbStorageBinTypeId, CommonUtils.getNullPropertyNames(newStorageBinTypeId));
		dbStorageBinTypeId.setCompanyCodeId(getCompanyCode());
		dbStorageBinTypeId.setPlantId(getPlantId());
		dbStorageBinTypeId.setDeletionIndicator(0L);
		dbStorageBinTypeId.setCreatedBy(loginUserID);
		dbStorageBinTypeId.setUpdatedBy(loginUserID);
		dbStorageBinTypeId.setCreatedOn(new Date());
		dbStorageBinTypeId.setUpdatedOn(new Date());
		return storageBinTypeIdRepository.save(dbStorageBinTypeId);
	}
	
	/**
	 * updateStorageBinTypeId
	 * @param loginUserId 
	 * @param storageBinTypeId
	 * @param updateStorageBinTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinTypeId updateStorageBinTypeId (String warehouseId, Long storageClassId, Long storageTypeId, Long storageBinTypeId, String loginUserID, 
			UpdateStorageBinTypeId updateStorageBinTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		StorageBinTypeId dbStorageBinTypeId = getStorageBinTypeId(warehouseId, storageClassId, storageTypeId, storageBinTypeId);
		BeanUtils.copyProperties(updateStorageBinTypeId, dbStorageBinTypeId, CommonUtils.getNullPropertyNames(updateStorageBinTypeId));
		dbStorageBinTypeId.setUpdatedBy(loginUserID);
		dbStorageBinTypeId.setUpdatedOn(new Date());
		return storageBinTypeIdRepository.save(dbStorageBinTypeId);
	}
	
	/**
	 * deleteStorageBinTypeId
	 * @param loginUserID 
	 * @param storageBinTypeId
	 */
	public void deleteStorageBinTypeId (String warehouseId, Long storageClassId, Long storageTypeId, Long storageBinTypeId, String loginUserID) {
		StorageBinTypeId dbStorageBinTypeId = getStorageBinTypeId(warehouseId, storageClassId, storageTypeId, storageBinTypeId);
		if ( dbStorageBinTypeId != null) {
			dbStorageBinTypeId.setDeletionIndicator(1L);
			dbStorageBinTypeId.setUpdatedBy(loginUserID);
			storageBinTypeIdRepository.save(dbStorageBinTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageBinTypeId);
		}
	}
}
