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
import com.tekclover.wms.api.idmaster.model.storagesectionid.AddStorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.UpdateStorageSectionId;
import com.tekclover.wms.api.idmaster.repository.StorageSectionIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageSectionIdService extends BaseService {
	
	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	
	/**
	 * getStorageSectionIds
	 * @return
	 */
	public List<StorageSectionId> getStorageSectionIds () {
		List<StorageSectionId> storageSectionIdList =  storageSectionIdRepository.findAll();
		storageSectionIdList = storageSectionIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageSectionIdList;
	}
	
	/**
	 * getStorageSectionId
	 * @param storageSectionId
	 * @return
	 */
	public StorageSectionId getStorageSectionId (String warehouseId, Long floorId, String storageSectionId, String storageSection) {
		Optional<StorageSectionId> dbStorageSectionId = 
				storageSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndStorageSectionAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								floorId,
								storageSectionId,
								storageSection,
								getLanguageId(),
								0L
								);
		if (dbStorageSectionId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"floorId - " + floorId +
						"storageSectionId - " + storageSectionId +
						"storageSection - " + storageSection +
						" doesn't exist.");
			
		} 
		return dbStorageSectionId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStorageSectionId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StorageSectionId> findStorageSectionId(SearchStorageSectionId searchStorageSectionId) 
//			throws ParseException {
//		StorageSectionIdSpecification spec = new StorageSectionIdSpecification(searchStorageSectionId);
//		List<StorageSectionId> results = storageSectionIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStorageSectionId
	 * @param newStorageSectionId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageSectionId createStorageSectionId (AddStorageSectionId newStorageSectionId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageSectionId dbStorageSectionId = new StorageSectionId();
		log.info("newStorageSectionId : " + newStorageSectionId);
		BeanUtils.copyProperties(newStorageSectionId, dbStorageSectionId, CommonUtils.getNullPropertyNames(newStorageSectionId));
		dbStorageSectionId.setCompanyCodeId(getCompanyCode());
		dbStorageSectionId.setPlantId(getPlantId());
		dbStorageSectionId.setDeletionIndicator(0L);
		dbStorageSectionId.setCreatedBy(loginUserID);
		dbStorageSectionId.setUpdatedBy(loginUserID);
		dbStorageSectionId.setCreatedOn(new Date());
		dbStorageSectionId.setUpdatedOn(new Date());
		return storageSectionIdRepository.save(dbStorageSectionId);
	}
	
	/**
	 * updateStorageSectionId
	 * @param loginUserId 
	 * @param storageSectionId
	 * @param updateStorageSectionId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageSectionId updateStorageSectionId (String warehouseId, Long floorId, String storageSectionId, String storageSection, String loginUserID,
			UpdateStorageSectionId updateStorageSectionId) 
			throws IllegalAccessException, InvocationTargetException {
		StorageSectionId dbStorageSectionId = getStorageSectionId(warehouseId, floorId, storageSectionId, storageSection);
		BeanUtils.copyProperties(updateStorageSectionId, dbStorageSectionId, CommonUtils.getNullPropertyNames(updateStorageSectionId));
		dbStorageSectionId.setUpdatedBy(loginUserID);
		dbStorageSectionId.setUpdatedOn(new Date());
		return storageSectionIdRepository.save(dbStorageSectionId);
	}
	
	/**
	 * deleteStorageSectionId
	 * @param loginUserID 
	 * @param storageSectionId
	 */
	public void deleteStorageSectionId (String warehouseId, Long floorId, String storageSectionId, String storageSection, String loginUserID) {
		StorageSectionId dbStorageSectionId = getStorageSectionId(warehouseId, floorId, storageSectionId, storageSection);
		if ( dbStorageSectionId != null) {
			dbStorageSectionId.setDeletionIndicator(1L);
			dbStorageSectionId.setUpdatedBy(loginUserID);
			storageSectionIdRepository.save(dbStorageSectionId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageSectionId);
		}
	}
}
