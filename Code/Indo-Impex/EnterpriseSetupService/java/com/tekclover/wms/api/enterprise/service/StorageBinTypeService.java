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
import com.tekclover.wms.api.enterprise.model.storagetype.StorageType;
import com.tekclover.wms.api.enterprise.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.storagebintype.AddStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.UpdateStorageBinType;
import com.tekclover.wms.api.enterprise.repository.specification.StorageBinTypeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageBinTypeService {
	@Autowired
	private StorageTypeRepository storageTypeRepository;
	@Autowired
	private StorageClassRepository storageClassRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private StorageBinTypeRepository storagebintypeRepository;

	/**
	 * getStorageBinTypes
	 * @return
	 */
	public List<StorageBinType> getStorageBinTypes () {
		List<StorageBinType> storagebintypeList = storagebintypeRepository.findAll();
		log.info("storagebintypeList : " + storagebintypeList);
		storagebintypeList = storagebintypeList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageBinType> newStorageBinType=new ArrayList<>();
		for(StorageBinType dbStorageBinType:storagebintypeList) {
			if (dbStorageBinType.getCompanyIdAndDescription() != null&&dbStorageBinType.getPlantIdAndDescription()!=null&&dbStorageBinType.getWarehouseIdAndDescription()!=null&&dbStorageBinType.getStorageClassIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageBinType.getCompanyId(), dbStorageBinType.getLanguageId());
				IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageBinType.getPlantId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId());
				IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageBinType.getWarehouseId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId());
				IkeyValuePair iKeyValuePair3 = storageClassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageBinType.getStorageClassId()), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getWarehouseId());
				IkeyValuePair ikeyValuePair4 = storageTypeRepository.getStorageTypeIdAndDescription(dbStorageBinType.getStorageTypeId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getStorageClassId());
				IkeyValuePair ikeyValuePair5 = storagebintypeRepository.getStorageBinTypeIdAndDescription(dbStorageBinType.getStorageBinTypeId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getStorageClassId(), dbStorageBinType.getStorageTypeId());
				if (iKeyValuePair != null) {
					dbStorageBinType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageBinType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageBinType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageBinType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbStorageBinType.setStorageTypeIdAndDescription(ikeyValuePair4.getStorageTypeId() + "-" + ikeyValuePair4.getDescription());
				}
				if (ikeyValuePair5 != null) {
					dbStorageBinType.setDescription(ikeyValuePair5.getDescription());
				}
			}
			newStorageBinType.add(dbStorageBinType);
		}
		return newStorageBinType;
	}

	/**
	 * getStorageBinType
	 * @param warehouseId
	 * @param storageTypeId
	 * @param storageBinTypeId
	 * @return
	 */
	public StorageBinType getStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId,Long storageClassId,String companyId,String languageId,String plantId) {
		Optional<StorageBinType> storagebintype =
				storagebintypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageClassIdAndStorageBinTypeIdAndDeletionIndicator(
						languageId, companyId, plantId, warehouseId, storageTypeId,storageClassId, storageBinTypeId, 0L);
		if (storagebintype.isEmpty()) {
			throw new BadRequestException("The given StorageBinType Id : " + storageBinTypeId + " doesn't exist.");
		}
		StorageBinType newStorageBinTypeId=new StorageBinType();
		BeanUtils.copyProperties(storagebintype.get(),newStorageBinTypeId, CommonUtils.getNullPropertyNames(storagebintype));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(companyId,languageId);
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(plantId,languageId,companyId);
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyId,plantId);
		IkeyValuePair iKeyValuePair3=storageClassRepository.getStorageClassIdAndDescription(String.valueOf(storageClassId),languageId,companyId,plantId,warehouseId);
		IkeyValuePair ikeyValuePair4=storageTypeRepository.getStorageTypeIdAndDescription(storageTypeId,languageId,companyId,plantId,storageClassId);
		IkeyValuePair ikeyValuePair5=storagebintypeRepository.getStorageBinTypeIdAndDescription(storageBinTypeId,languageId,companyId,plantId,storageClassId,storageTypeId);
		if(iKeyValuePair!=null) {
			newStorageBinTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageBinTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageBinTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newStorageBinTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
		}
		if(ikeyValuePair4!=null) {
			newStorageBinTypeId.setStorageTypeIdAndDescription(ikeyValuePair4.getStorageTypeId() + "-" + ikeyValuePair4.getDescription());
		}
		if(ikeyValuePair5!=null) {
			newStorageBinTypeId.setDescription(ikeyValuePair5.getDescription());
		}
		return newStorageBinTypeId;
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
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageBinType> newStorageBinType=new ArrayList<>();
		for(StorageBinType dbStorageBinType:results) {
			if (dbStorageBinType.getCompanyIdAndDescription() != null&&dbStorageBinType.getPlantIdAndDescription()!=null&&dbStorageBinType.getWarehouseIdAndDescription()!=null&&dbStorageBinType.getStorageClassIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(dbStorageBinType.getCompanyId(), dbStorageBinType.getLanguageId());
				IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(dbStorageBinType.getPlantId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId());
				IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageBinType.getWarehouseId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId());
				IkeyValuePair iKeyValuePair3=storageClassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageBinType.getStorageClassId()), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getWarehouseId());
				IkeyValuePair ikeyValuePair4=storageTypeRepository.getStorageTypeIdAndDescription(dbStorageBinType.getStorageTypeId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getStorageClassId());
				IkeyValuePair ikeyValuePair5=storagebintypeRepository.getStorageBinTypeIdAndDescription(dbStorageBinType.getStorageBinTypeId(), dbStorageBinType.getLanguageId(), dbStorageBinType.getCompanyId(), dbStorageBinType.getPlantId(), dbStorageBinType.getStorageClassId(), dbStorageBinType.getStorageTypeId());
				if (iKeyValuePair != null) {
					dbStorageBinType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageBinType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageBinType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageBinType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbStorageBinType.setStorageTypeIdAndDescription(ikeyValuePair4.getStorageTypeId() + "-" + ikeyValuePair4.getDescription());
				}
				if (ikeyValuePair5 != null) {
					dbStorageBinType.setDescription(ikeyValuePair5.getDescription());
				}
			}
			newStorageBinType.add(dbStorageBinType);
		}
		return newStorageBinType;
	}

	/**
	 * createStorageBinType
	 * @param newStorageBinType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinType createStorageBinType (AddStorageBinType newStorageBinType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<StorageBinType> optStorageBinType =
				storagebintypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageClassIdAndStorageBinTypeIdAndDeletionIndicator(
						newStorageBinType.getLanguageId(),
						newStorageBinType.getCompanyId(),
						newStorageBinType.getPlantId(),
						newStorageBinType.getWarehouseId(),
						newStorageBinType.getStorageTypeId(),
						newStorageBinType.getStorageClassId(),
						newStorageBinType.getStorageBinTypeId(),
						0L);
		if (!optStorageBinType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}

		StorageBinType dbStorageBinType = new StorageBinType();
		BeanUtils.copyProperties(newStorageBinType, dbStorageBinType, CommonUtils.getNullPropertyNames(newStorageBinType));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(newStorageBinType.getCompanyId(), newStorageBinType.getLanguageId());
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(newStorageBinType.getPlantId(), newStorageBinType.getLanguageId(), newStorageBinType.getCompanyId());
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(newStorageBinType.getWarehouseId(), newStorageBinType.getLanguageId(), newStorageBinType.getCompanyId(), newStorageBinType.getPlantId());
		IkeyValuePair iKeyValuePair3=storageClassRepository.getStorageClassIdAndDescription(String.valueOf(newStorageBinType.getStorageClassId()), newStorageBinType.getLanguageId(), newStorageBinType.getCompanyId(), newStorageBinType.getPlantId(), newStorageBinType.getWarehouseId());
		IkeyValuePair ikeyValuePair4=storageTypeRepository.getStorageTypeIdAndDescription(newStorageBinType.getStorageTypeId(), newStorageBinType.getLanguageId(), newStorageBinType.getCompanyId(), newStorageBinType.getPlantId(), newStorageBinType.getStorageClassId());
		IkeyValuePair ikeyValuePair5=storagebintypeRepository.getStorageBinTypeIdAndDescription(newStorageBinType.getStorageBinTypeId(), newStorageBinType.getLanguageId(), newStorageBinType.getCompanyId(), newStorageBinType.getPlantId(), newStorageBinType.getStorageClassId(), newStorageBinType.getStorageTypeId());

		if(iKeyValuePair != null && iKeyValuePair1 != null && iKeyValuePair2 != null &&
				iKeyValuePair3 != null && ikeyValuePair4 != null && ikeyValuePair5 != null) {
			dbStorageBinType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
			dbStorageBinType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
			dbStorageBinType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
			dbStorageBinType.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
			dbStorageBinType.setStorageTypeIdAndDescription(ikeyValuePair4.getStorageTypeId() + "-" + ikeyValuePair4.getDescription());
			dbStorageBinType.setDescription(ikeyValuePair5.getDescription());
		}
		else {
			throw new BadRequestException("The given values of Company Id "
			+ newStorageBinType.getCompanyId() + " Plant Id "
			+ newStorageBinType.getPlantId() + " Warehouse Id "
			+ newStorageBinType.getWarehouseId() + " Storage Class Id "
			+ newStorageBinType.getStorageClassId() + " Storage Type Id "
			+ newStorageBinType.getStorageTypeId() + " Storage BinType Id "
			+ newStorageBinType.getStorageBinTypeId() + " doesn't exist ");
		}
		dbStorageBinType.setDeletionIndicator(0L);
		dbStorageBinType.setCreatedBy(loginUserID);
		dbStorageBinType.setUpdatedBy(loginUserID);
		dbStorageBinType.setCreatedOn(new Date());
		dbStorageBinType.setUpdatedOn(new Date());
		return storagebintypeRepository.save(dbStorageBinType);
	}

	/**
	 * updateStorageBinType
	 * @param storageBinTypeId
	 * @param updateStorageBinType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinType updateStorageBinType (String warehouseId, Long storageTypeId, Long storageBinTypeId,String companyId,Long storageClassId,String plantId,String languageId,UpdateStorageBinType updateStorageBinType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageBinType dbStorageBinType = getStorageBinType(warehouseId, storageTypeId, storageBinTypeId,storageClassId,companyId,languageId,plantId);
		BeanUtils.copyProperties(updateStorageBinType, dbStorageBinType, CommonUtils.getNullPropertyNames(updateStorageBinType));
		dbStorageBinType.setUpdatedBy(loginUserID);
		dbStorageBinType.setUpdatedOn(new Date());
		return storagebintypeRepository.save(dbStorageBinType);
	}

	/**
	 * deleteStorageBinType
	 * @param storageBinTypeId
	 */
	public void deleteStorageBinType (String warehouseId, Long storageTypeId,Long storageClassId, Long storageBinTypeId,String companyId,String plantId,String languageId, String loginUserID) throws ParseException {
		StorageBinType storagebintype = getStorageBinType(warehouseId, storageTypeId, storageBinTypeId,storageClassId,companyId,languageId,plantId);
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
