package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import com.tekclover.wms.api.enterprise.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.storagetype.AddStorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.SearchStorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.StorageType;
import com.tekclover.wms.api.enterprise.model.storagetype.UpdateStorageType;
import com.tekclover.wms.api.enterprise.repository.specification.StorageTypeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageTypeService {
	@Autowired
	private StorageClassRepository storageClassRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private StorageTypeRepository storagetypeRepository;

	/**
	 * getStorageTypes
	 * @return
	 */
	public List<StorageType> getStorageTypes () {
		List<StorageType> storagetypeList = storagetypeRepository.findAll();
		log.info("storagetypeList : " + storagetypeList);
		storagetypeList = storagetypeList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageType> newStorageType=new ArrayList<>();
		for(StorageType dbStorageType:storagetypeList) {
			if (dbStorageType.getCompanyIdAndDescription() != null&&dbStorageType.getPlantIdAndDescription()!=null&&dbStorageType.getWarehouseIdAndDescription()!=null&&dbStorageType.getStorageClassIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageType.getCompanyId(), dbStorageType.getLanguageId());
				IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageType.getPlantId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId());
				IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageType.getWarehouseId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId());
				IkeyValuePair iKeyValuePair3 = storageClassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageType.getStorageClassId()), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId(), dbStorageType.getWarehouseId());
				IkeyValuePair ikeyValuePair4 = storagetypeRepository.getStorageTypeIdAndDescription(dbStorageType.getStorageTypeId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId(), dbStorageType.getStorageClassId());
				if (iKeyValuePair != null) {
					dbStorageType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbStorageType.setDescription(ikeyValuePair4.getDescription());
				}
			}
			newStorageType.add(dbStorageType);
		}
		return newStorageType;
	}

	/**
	 * getStorageType
	 * @param warehouseId
	 * @param storageClassId
	 * @param storageTypeId
	 * @return
	 */
	public StorageType getStorageType (String warehouseId, Long storageClassId, Long storageTypeId,String companyId,String languageId,String plantId) {
		Optional<StorageType> storagetype =
				storagetypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
						languageId, companyId, plantId, warehouseId, storageClassId, storageTypeId, 0L);
		if (storagetype.isEmpty()) {
			throw new BadRequestException("The given StorageType Id : " + storageTypeId + " doesn't exist.");
		}
		StorageType newStorageType = new StorageType();
		BeanUtils.copyProperties(storagetype.get(),newStorageType, CommonUtils.getNullPropertyNames(storagetype));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(companyId,languageId);
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(plantId,languageId,companyId);
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyId,plantId);
		IkeyValuePair iKeyValuePair3=storageClassRepository.getStorageClassIdAndDescription(String.valueOf(storageClassId),languageId,companyId,plantId,warehouseId);
		IkeyValuePair ikeyValuePair4=storagetypeRepository.getStorageTypeIdAndDescription(storageTypeId,languageId,companyId,plantId,storageClassId);
		if(iKeyValuePair!=null) {
			newStorageType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newStorageType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
		}
		if(ikeyValuePair4!=null) {
			newStorageType.setDescription(ikeyValuePair4.getDescription());
		}
		return newStorageType;

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
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageType> newStorageType=new ArrayList<>();
		for(StorageType dbStorageType:results) {
			if (dbStorageType.getCompanyIdAndDescription() != null&&dbStorageType.getPlantIdAndDescription()!=null&&dbStorageType.getWarehouseIdAndDescription()!=null&&dbStorageType.getStorageClassIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(dbStorageType.getCompanyId(), dbStorageType.getLanguageId());
				IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(dbStorageType.getPlantId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId());
				IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageType.getWarehouseId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId());
				IkeyValuePair iKeyValuePair3=storageClassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageType.getStorageClassId()), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId(), dbStorageType.getWarehouseId());
				IkeyValuePair ikeyValuePair4=storagetypeRepository.getStorageTypeIdAndDescription(dbStorageType.getStorageTypeId(), dbStorageType.getLanguageId(), dbStorageType.getCompanyId(), dbStorageType.getPlantId(), dbStorageType.getStorageClassId());
				if (iKeyValuePair != null) {
					dbStorageType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbStorageType.setDescription(ikeyValuePair4.getDescription());
				}
			}
			newStorageType.add(dbStorageType);
		}
		return newStorageType;
	}

	/**
	 * createStorageType
	 * @param newStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType createStorageType (AddStorageType newStorageType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<StorageType> optStorageType =
				storagetypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
						newStorageType.getLanguageId(),
						newStorageType.getCompanyId(),
						newStorageType.getPlantId(),
						newStorageType.getWarehouseId(),
						newStorageType.getStorageClassId(),
						newStorageType.getStorageTypeId(),
						0L);
		if (!optStorageType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newStorageType.getCompanyId(), newStorageType.getLanguageId());
		IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newStorageType.getPlantId(), newStorageType.getLanguageId(), newStorageType.getCompanyId());
		IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newStorageType.getWarehouseId(), newStorageType.getLanguageId(), newStorageType.getCompanyId(), newStorageType.getPlantId());
		IkeyValuePair ikeyValuePair3 = storageClassRepository.getStorageClassIdAndDescription(String.valueOf(newStorageType.getStorageClassId()), newStorageType.getLanguageId(), newStorageType.getCompanyId(), newStorageType.getPlantId(), newStorageType.getWarehouseId());
		IkeyValuePair ikeyValuePair4=storagetypeRepository.getStorageTypeIdAndDescription(newStorageType.getStorageTypeId(), newStorageType.getLanguageId(), newStorageType.getCompanyId(), newStorageType.getPlantId(), newStorageType.getStorageClassId());
		StorageType dbStorageType = new StorageType();
		BeanUtils.copyProperties(newStorageType, dbStorageType, CommonUtils.getNullPropertyNames(newStorageType));

		if(ikeyValuePair != null && ikeyValuePair1 != null && ikeyValuePair2 != null &&
				ikeyValuePair3 != null && ikeyValuePair4 != null) {
			dbStorageType.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
			dbStorageType.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
			dbStorageType.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
			dbStorageType.setStorageClassIdAndDescription(ikeyValuePair3.getStorageClassId() + "-" + ikeyValuePair3.getDescription());
			dbStorageType.setDescription(ikeyValuePair4.getDescription());
		}
		else {
			throw new BadRequestException("The given values of Company Id "
					+ newStorageType.getCompanyId() + " Plant Id "
					+ newStorageType.getPlantId() + " Warehouse Id "
					+ newStorageType.getWarehouseId() + " Storage Class Id "
					+ newStorageType.getStorageClassId() + " Storage Type Id "
					+ newStorageType.getStorageTypeId() + " doesn't exist ");
		}
		dbStorageType.setDeletionIndicator(0L);
		dbStorageType.setCreatedBy(loginUserID);
		dbStorageType.setUpdatedBy(loginUserID);
		dbStorageType.setCreatedOn(new Date());
		dbStorageType.setUpdatedOn(new Date());
		return storagetypeRepository.save(dbStorageType);
	}

	/**
	 * updateStorageType
	 * @param storageTypeId
	 * @param updateStorageType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageType updateStorageType (String warehouseId, Long storageClassId, Long storageTypeId,String companyId,String languageId,String plantId,UpdateStorageType updateStorageType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageType dbStorageType = getStorageType(warehouseId, storageClassId, storageTypeId,companyId,languageId,plantId);
		BeanUtils.copyProperties(updateStorageType, dbStorageType, CommonUtils.getNullPropertyNames(updateStorageType));
		dbStorageType.setUpdatedBy(loginUserID);
		dbStorageType.setUpdatedOn(new Date());
		return storagetypeRepository.save(dbStorageType);
	}

	/**
	 * deleteStorageType
	 * @param storagetypeCode
	 */
	public void deleteStorageType (String warehouseId, Long storageClassId, Long storageTypeId,String companyId,String plantId,String languageId, String loginUserID) throws ParseException {
		StorageType storagetype = getStorageType(warehouseId, storageClassId, storageTypeId,companyId,languageId,plantId);
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
