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
import com.tekclover.wms.api.enterprise.model.storagesection.AddStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.SearchStorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.StorageSection;
import com.tekclover.wms.api.enterprise.model.storagesection.UpdateStorageSection;
import com.tekclover.wms.api.enterprise.repository.StorageSectionRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StorageSectionSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageSectionService extends BaseService {
	
	@Autowired
	private StorageSectionRepository storagesectionRepository;
	
	/**
	 * getStorageSections
	 * @return
	 */
	public List<StorageSection> getStorageSections () {
		List<StorageSection> storagesectionList = storagesectionRepository.findAll();
		log.info("storagesectionList : " + storagesectionList);
		storagesectionList = storagesectionList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return storagesectionList;
	}
	
	/**
	 * getStorageSection
	 * @param warehouseId
	 * @param floorId
	 * @param storageSectionId
	 * @return
	 */
	public StorageSection getStorageSection (String warehouseId, Long floorId, String storageSectionId) {
		Optional<StorageSection> storagesection = 
				storagesectionRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, floorId, storageSectionId, 0L);
		if (storagesection.isEmpty()) {
			throw new BadRequestException("The given StorageSection Id : " + storageSectionId + " doesn't exist.");
		} 
		return storagesection.get();
	}
	
	/**
	 * findStorageSection
	 * @param searchStorageSection
	 * @return
	 * @throws ParseException
	 */
	public List<StorageSection> findStorageSection(SearchStorageSection searchStorageSection) throws Exception {
		if (searchStorageSection.getStartCreatedOn() != null && searchStorageSection.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageSection.getStartCreatedOn(), searchStorageSection.getEndCreatedOn());
			searchStorageSection.setStartCreatedOn(dates[0]);
			searchStorageSection.setEndCreatedOn(dates[1]);
		}
		
		StorageSectionSpecification spec = new StorageSectionSpecification(searchStorageSection);
		List<StorageSection> results = storagesectionRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStorageSection
	 * @param newStorageSection
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageSection createStorageSection (AddStorageSection newStorageSection, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<StorageSection> optStorageSection = 
				storagesectionRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newStorageSection.getWarehouseId(),
						newStorageSection.getFloorId(),
						newStorageSection.getStorageSectionId(),
						0L);
		if (!optStorageSection.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		StorageSection dbStorageSection = new StorageSection();
		BeanUtils.copyProperties(newStorageSection, dbStorageSection, CommonUtils.getNullPropertyNames(newStorageSection));
		dbStorageSection.setDeletionIndicator(0L);
		
		dbStorageSection.setLanguageId(getLanguageId());
		dbStorageSection.setCompanyId(getCompanyCode());
		dbStorageSection.setPlantId(getPlantId());
		
		dbStorageSection.setCreatedBy(loginUserID);
		dbStorageSection.setUpdatedBy(loginUserID);
		dbStorageSection.setCreatedOn(new Date());
		dbStorageSection.setUpdatedOn(new Date());
		return storagesectionRepository.save(dbStorageSection);
	}
	
	/**
	 * updateStorageSection
	 * @param storagesectionCode
	 * @param updateStorageSection
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageSection updateStorageSection (String warehouseId, Long floorId, String storageSectionId, UpdateStorageSection updateStorageSection, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StorageSection dbStorageSection = getStorageSection(warehouseId, floorId, storageSectionId);
		BeanUtils.copyProperties(updateStorageSection, dbStorageSection, CommonUtils.getNullPropertyNames(updateStorageSection));
		dbStorageSection.setUpdatedBy(loginUserID);
		dbStorageSection.setUpdatedOn(new Date());
		return storagesectionRepository.save(dbStorageSection);
	}
	
	/**
	 * deleteStorageSection
	 * @param storagesectionCode
	 */
	public void deleteStorageSection (String warehouseId, Long floorId, String storageSectionId, String loginUserID) {
		StorageSection storagesection = getStorageSection(warehouseId, floorId, storageSectionId);
		if ( storagesection != null) {
			storagesection.setDeletionIndicator (1L);
			storagesection.setUpdatedBy(loginUserID);
			storagesection.setUpdatedOn(new Date());
			storagesectionRepository.save(storagesection);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageSectionId);
		}
	}
}
