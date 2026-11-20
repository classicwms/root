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
import com.tekclover.wms.api.idmaster.model.storagebintypeid.FindStorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.StorageBinTypeSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.AddStorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.StorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.UpdateStorageBinTypeId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageBinTypeIdService {
	@Autowired
	private StorageTypeIdRepository storageTypeIdRepository;

	@Autowired
	private StorageBinTypeIdRepository storageBinTypeIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private StorageClassIdRepository storageClassIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private StorageTypeIdService storageTypeIdService;

	/**
	 * getStorageBinTypeIds
	 * @return
	 */
	public List<StorageBinTypeId> getStorageBinTypeIds () {
		List<StorageBinTypeId> storageBinTypeIdList =  storageBinTypeIdRepository.findAll();
		storageBinTypeIdList = storageBinTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageBinTypeId> newStorageBinTypeId=new ArrayList<>();
		for(StorageBinTypeId dbStorageBinTypeId:storageBinTypeIdList) {
			if (dbStorageBinTypeId.getCompanyIdAndDescription() != null&&dbStorageBinTypeId.getPlantIdAndDescription()!=null&&dbStorageBinTypeId.getWarehouseIdAndDescription()!=null&&dbStorageBinTypeId.getStorageClassIdAndDescription()!=null&&dbStorageBinTypeId.getStorageTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStorageBinTypeId.getPlantId(), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageBinTypeId.getWarehouseId(), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3 = storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageBinTypeId.getStorageClassId()), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId(), dbStorageBinTypeId.getWarehouseId());
				IKeyValuePair iKeyValuePair4 = storageTypeIdRepository.getStorageTypeIdAndDescription(String.valueOf(dbStorageBinTypeId.getStorageTypeId()), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId(), String.valueOf(dbStorageBinTypeId.getStorageClassId()), dbStorageBinTypeId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbStorageBinTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageBinTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageBinTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageBinTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbStorageBinTypeId.setStorageTypeIdAndDescription(iKeyValuePair4.getStorageTypeId() + "-" + iKeyValuePair4.getDescription());
				}
			}

			newStorageBinTypeId.add(dbStorageBinTypeId);
		}
		return newStorageBinTypeId;
	}

	/**
	 * getStorageBinTypeId
	 * @param storageBinTypeId
	 * @return
	 */
	public StorageBinTypeId getStorageBinTypeId (String warehouseId, Long storageBinTypeId, Long storageClassId, Long storageTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<StorageBinTypeId> dbStorageBinTypeId =
				storageBinTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndStorageBinTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						storageClassId,
						storageTypeId,
						storageBinTypeId,
						languageId,
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
		StorageBinTypeId newStorageBinTypeId = new StorageBinTypeId();
		BeanUtils.copyProperties(dbStorageBinTypeId.get(),newStorageBinTypeId, CommonUtils.getNullPropertyNames(dbStorageBinTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3=storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(storageClassId),languageId,companyCodeId,plantId,warehouseId);
		IKeyValuePair iKeyValuePair4=storageTypeIdRepository.getStorageTypeIdAndDescription(String.valueOf(storageTypeId),languageId,companyCodeId,plantId, String.valueOf(storageClassId),warehouseId);
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
		if(iKeyValuePair4!=null) {
			newStorageBinTypeId.setStorageTypeIdAndDescription(iKeyValuePair4.getStorageTypeId() + "-" + iKeyValuePair4.getDescription());
		}
		return newStorageBinTypeId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageBinTypeId dbStorageBinTypeId = new StorageBinTypeId();
		Optional<StorageBinTypeId>duplicateStorageBinTypeId=storageBinTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndStorageBinTypeIdAndLanguageIdAndDeletionIndicator(newStorageBinTypeId.getCompanyCodeId(), newStorageBinTypeId.getPlantId(), newStorageBinTypeId.getWarehouseId(), newStorageBinTypeId.getStorageClassId(), newStorageBinTypeId.getStorageTypeId(), newStorageBinTypeId.getStorageBinTypeId(), newStorageBinTypeId.getLanguageId(), 0L);
		if(!duplicateStorageBinTypeId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			StorageTypeId dbStorageTypeId=storageTypeIdService.getStorageTypeId(newStorageBinTypeId.getWarehouseId(), newStorageBinTypeId.getStorageClassId(), newStorageBinTypeId.getStorageTypeId(), newStorageBinTypeId.getCompanyCodeId(), newStorageBinTypeId.getLanguageId(), newStorageBinTypeId.getPlantId());
			log.info("newStorageBinTypeId : " + newStorageBinTypeId);
			BeanUtils.copyProperties(newStorageBinTypeId, dbStorageBinTypeId, CommonUtils.getNullPropertyNames(newStorageBinTypeId));
			dbStorageBinTypeId.setDeletionIndicator(0L);
			dbStorageBinTypeId.setCompanyIdAndDescription(dbStorageTypeId.getCompanyIdAndDescription());
			dbStorageBinTypeId.setPlantIdAndDescription(dbStorageTypeId.getPlantIdAndDescription());
			dbStorageBinTypeId.setWarehouseIdAndDescription(dbStorageTypeId.getWarehouseIdAndDescription());
			dbStorageBinTypeId.setStorageClassIdAndDescription(dbStorageTypeId.getStorageClassIdAndDescription());
			dbStorageBinTypeId.setStorageTypeIdAndDescription(dbStorageTypeId.getStorageTypeId()+"-"+dbStorageTypeId.getDescription());
			dbStorageBinTypeId.setCreatedBy(loginUserID);
			dbStorageBinTypeId.setUpdatedBy(loginUserID);
			dbStorageBinTypeId.setCreatedOn(new Date());
			dbStorageBinTypeId.setUpdatedOn(new Date());
			return storageBinTypeIdRepository.save(dbStorageBinTypeId);
		}
	}

	/**
	 * updateStorageBinTypeId
	 * @param loginUserID
	 * @param storageBinTypeId
	 * @param updateStorageBinTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageBinTypeId updateStorageBinTypeId (String warehouseId,Long storageBinTypeId, Long storageClassId,Long storageTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
													UpdateStorageBinTypeId updateStorageBinTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageBinTypeId dbStorageBinTypeId = getStorageBinTypeId(warehouseId,storageBinTypeId,storageClassId,storageTypeId,companyCodeId,languageId,plantId);
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
	public void deleteStorageBinTypeId (String warehouseId,Long storageBinTypeId,Long storageClassId,Long storageTypeId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		StorageBinTypeId dbStorageBinTypeId = getStorageBinTypeId(warehouseId,storageBinTypeId,storageClassId,storageTypeId,companyCodeId,languageId,plantId );
		if ( dbStorageBinTypeId != null) {
			dbStorageBinTypeId.setDeletionIndicator(1L);
			dbStorageBinTypeId.setUpdatedBy(loginUserID);
			storageBinTypeIdRepository.save(dbStorageBinTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageBinTypeId);
		}
	}
	//Find StorageBinTypeId

	public List<StorageBinTypeId> findStorageBinTypeId(FindStorageBinTypeId findStorageBinTypeId) throws ParseException {

		StorageBinTypeSpecification spec = new StorageBinTypeSpecification(findStorageBinTypeId);
		List<StorageBinTypeId> results = storageBinTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StorageBinTypeId> newStorageBinTypeId=new ArrayList<>();
		for(StorageBinTypeId dbStorageBinTypeId:results) {
			if (dbStorageBinTypeId.getCompanyIdAndDescription() != null&&dbStorageBinTypeId.getPlantIdAndDescription()!=null&&dbStorageBinTypeId.getWarehouseIdAndDescription()!=null&&dbStorageBinTypeId.getStorageClassIdAndDescription()!=null&&dbStorageBinTypeId.getStorageTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStorageBinTypeId.getPlantId(), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageBinTypeId.getWarehouseId(), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3=storageClassIdRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageBinTypeId.getStorageClassId()), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId(), dbStorageBinTypeId.getWarehouseId());
				IKeyValuePair iKeyValuePair4=storageTypeIdRepository.getStorageTypeIdAndDescription(String.valueOf(dbStorageBinTypeId.getStorageTypeId()), dbStorageBinTypeId.getLanguageId(), dbStorageBinTypeId.getCompanyCodeId(), dbStorageBinTypeId.getPlantId(), String.valueOf(dbStorageBinTypeId.getStorageClassId()), dbStorageBinTypeId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbStorageBinTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageBinTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageBinTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageBinTypeId.setStorageClassIdAndDescription(iKeyValuePair3.getStorageClassId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbStorageBinTypeId.setStorageTypeIdAndDescription(iKeyValuePair4.getStorageTypeId() + "-" + iKeyValuePair4.getDescription());
				}
			}
			newStorageBinTypeId.add(dbStorageBinTypeId);
		}
		return newStorageBinTypeId;
	}
}
