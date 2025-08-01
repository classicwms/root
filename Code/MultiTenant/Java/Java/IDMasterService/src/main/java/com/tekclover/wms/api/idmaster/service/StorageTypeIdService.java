package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.FindStorageTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.StorageTypeIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.storagetypeid.AddStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.UpdateStorageTypeId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageTypeIdService {

	@Autowired
	private StorageTypeIdRepository storageTypeIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	private StorageClassIdRepository storageClassIdRepository;
	@Autowired
	private StorageClassIdService storageClassIdService;

	/**
	 * getStorageTypeIds
	 * @return
	 */
	public List<StorageTypeId> getStorageTypeIds () {
		List<StorageTypeId> storageTypeIdList =  storageTypeIdRepository.findAll();
		storageTypeIdList = storageTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageTypeId> newStorageTypeId=new ArrayList<>();
		for(StorageTypeId dbStorageTypeId:storageTypeIdList) {
			if (dbStorageTypeId.getCompanyIdAndDescription() != null&&dbStorageTypeId.getPlantIdAndDescription()!=null&&dbStorageTypeId.getWarehouseIdAndDescription()!=null&&dbStorageTypeId.getStorageClassIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStorageTypeId.getPlantId(), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageTypeId.getWarehouseId(), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3=storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageTypeId.getStorageClassId()), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getPlantId(), dbStorageTypeId.getWarehouseId());
				if(iKeyValuePair!=null) {
					dbStorageTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if(iKeyValuePair1!=null) {
					dbStorageTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if(iKeyValuePair2!=null) {
					dbStorageTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if(iKeyValuePair3!=null) {
					dbStorageTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}

			}
			newStorageTypeId.add(dbStorageTypeId);
		}
		return newStorageTypeId;
	}

	/**
	 * getStorageTypeId
	 * @param storageTypeId
	 * @return
	 */
	public StorageTypeId getStorageTypeId (String warehouseId,Long storageClassId,Long storageTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<StorageTypeId> dbStorageTypeId =
				storageTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						storageClassId,
						storageTypeId,
						languageId,
						0L
				);
		if (dbStorageTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"storageClassId - " + storageClassId +
					"storageTypeId - " + storageTypeId +
					" doesn't exist.");

		}
		StorageTypeId newStorageTypeId = new StorageTypeId();
		BeanUtils.copyProperties(dbStorageTypeId.get(),newStorageTypeId, CommonUtils.getNullPropertyNames(dbStorageTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3=storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(storageClassId),languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newStorageTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newStorageTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
		}
		return newStorageTypeId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageTypeId dbStorageTypeId = new StorageTypeId();
		Optional<StorageTypeId> duplicateStorageTypeId = storageTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndLanguageIdAndDeletionIndicator(newStorageTypeId.getCompanyCodeId(), newStorageTypeId.getPlantId(), newStorageTypeId.getWarehouseId(),
				newStorageTypeId.getStorageClassId(), newStorageTypeId.getStorageTypeId(), newStorageTypeId.getLanguageId(), 0L);
		if (!duplicateStorageTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			StorageClassId dbStorageClassId=storageClassIdService.getStorageClassId(newStorageTypeId.getWarehouseId(), newStorageTypeId.getStorageClassId(), newStorageTypeId.getCompanyCodeId(), newStorageTypeId.getLanguageId(), newStorageTypeId.getPlantId());
			log.info("newStorageTypeId : " + newStorageTypeId);
			BeanUtils.copyProperties(newStorageTypeId, dbStorageTypeId, CommonUtils.getNullPropertyNames(newStorageTypeId));
			dbStorageTypeId.setDeletionIndicator(0L);
			dbStorageTypeId.setCompanyIdAndDescription(dbStorageClassId.getCompanyIdAndDescription());
			dbStorageTypeId.setPlantIdAndDescription(dbStorageClassId.getPlantIdAndDescription());
			dbStorageTypeId.setWarehouseIdAndDescription(dbStorageClassId.getWarehouseIdAndDescription());
			dbStorageTypeId.setStorageClassIdAndDescription(dbStorageClassId.getStorageClassId()+"-"+dbStorageClassId.getDescription());
			dbStorageTypeId.setCreatedBy(loginUserID);
			dbStorageTypeId.setUpdatedBy(loginUserID);
			dbStorageTypeId.setCreatedOn(new Date());
			dbStorageTypeId.setUpdatedOn(new Date());
			return storageTypeIdRepository.save(dbStorageTypeId);
		}
	}

	/**
	 * updateStorageTypeId
	 * @param loginUserID
	 * @param storageTypeId
	 * @param updateStorageTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageTypeId updateStorageTypeId (String warehouseId, Long storageClassId, Long storageTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
											  UpdateStorageTypeId updateStorageTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageTypeId dbStorageTypeId = getStorageTypeId(warehouseId, storageClassId, storageTypeId,companyCodeId,languageId,plantId);
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
	public void deleteStorageTypeId (String warehouseId, Long storageClassId, Long storageTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		StorageTypeId dbStorageTypeId = getStorageTypeId(warehouseId, storageClassId, storageTypeId,companyCodeId,languageId,plantId);
		if ( dbStorageTypeId != null) {
			dbStorageTypeId.setDeletionIndicator(1L);
			dbStorageTypeId.setUpdatedBy(loginUserID);
			storageTypeIdRepository.save(dbStorageTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageTypeId);
		}
	}
	//Find StorageTypeId

	public List<StorageTypeId> findStorageTypeId(FindStorageTypeId findStorageTypeId) throws ParseException {

		StorageTypeIdSpecification spec = new StorageTypeIdSpecification(findStorageTypeId);
		List<StorageTypeId> results = storageTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StorageTypeId> newStorageTypeId=new ArrayList<>();
		for(StorageTypeId dbStorageTypeId:results) {
			if (dbStorageTypeId.getCompanyIdAndDescription() != null&&dbStorageTypeId.getPlantIdAndDescription()!=null&&dbStorageTypeId.getWarehouseIdAndDescription()!=null&&dbStorageTypeId.getStorageClassIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStorageTypeId.getPlantId(), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageTypeId.getWarehouseId(), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3=storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageTypeId.getStorageClassId()), dbStorageTypeId.getLanguageId(), dbStorageTypeId.getCompanyCodeId(), dbStorageTypeId.getPlantId(), dbStorageTypeId.getWarehouseId());
				if(iKeyValuePair!=null) {
					dbStorageTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if(iKeyValuePair1!=null) {
					dbStorageTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if(iKeyValuePair2!=null) {
					dbStorageTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if(iKeyValuePair3!=null) {
					dbStorageTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newStorageTypeId.add(dbStorageTypeId);
		}
		return newStorageTypeId;
	}
}
