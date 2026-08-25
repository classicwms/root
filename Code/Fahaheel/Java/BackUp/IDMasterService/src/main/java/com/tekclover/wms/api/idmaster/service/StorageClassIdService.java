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
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import com.tekclover.wms.api.idmaster.model.storageclassid.FindStorageClassId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.StorageClassIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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
public class StorageClassIdService {

	@Autowired
	private StorageClassIdRepository storageClassIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getStorageClassIds
	 * @return
	 */
	public List<StorageClassId> getStorageClassIds () {
		List<StorageClassId> storageClassIdList =  storageClassIdRepository.findAll();
		storageClassIdList = storageClassIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageClassId> newStorageClassId=new ArrayList<>();
		for(StorageClassId dbStorageClassId:storageClassIdList) {
			if (dbStorageClassId.getCompanyIdAndDescription() != null&&dbStorageClassId.getPlantIdAndDescription()!=null&&dbStorageClassId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStorageClassId.getCompanyCodeId(), dbStorageClassId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStorageClassId.getPlantId(), dbStorageClassId.getLanguageId(), dbStorageClassId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageClassId.getWarehouseId(), dbStorageClassId.getLanguageId(), dbStorageClassId.getCompanyCodeId(), dbStorageClassId.getPlantId());
				if (iKeyValuePair != null) {
					dbStorageClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStorageClassId.add(dbStorageClassId);
		}
		return newStorageClassId;
	}

	/**
	 * getStorageClassId
	 * @param storageClassId
	 * @return
	 */
	public StorageClassId getStorageClassId (String warehouseId, Long storageClassId,String companyCodeId,String languageId,String plantId) {
		Optional<StorageClassId> dbStorageClassId =
				storageClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						storageClassId,
						languageId,
						0L
				);
		if (dbStorageClassId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"storageClassId - " + storageClassId +
					" doesn't exist.");

		}
		StorageClassId newStorageClassId = new StorageClassId();
		BeanUtils.copyProperties(dbStorageClassId.get(),newStorageClassId, CommonUtils.getNullPropertyNames(dbStorageClassId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newStorageClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newStorageClassId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClassId dbStorageClassId = new StorageClassId();
		Optional<StorageClassId> duplicateStorageClassId = storageClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndLanguageIdAndDeletionIndicator(newStorageClassId.getCompanyCodeId(), newStorageClassId.getPlantId(), newStorageClassId.getWarehouseId(), newStorageClassId.getStorageClassId(), newStorageClassId.getLanguageId(), 0L);
		if (!duplicateStorageClassId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newStorageClassId.getWarehouseId(), newStorageClassId.getCompanyCodeId(), newStorageClassId.getPlantId(), newStorageClassId.getLanguageId());
			log.info("newStorageClassId : " + newStorageClassId);
			BeanUtils.copyProperties(newStorageClassId, dbStorageClassId, CommonUtils.getNullPropertyNames(newStorageClassId));
			dbStorageClassId.setDeletionIndicator(0L);
			dbStorageClassId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbStorageClassId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbStorageClassId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbStorageClassId.setCreatedBy(loginUserID);
			dbStorageClassId.setUpdatedBy(loginUserID);
			dbStorageClassId.setCreatedOn(new Date());
			dbStorageClassId.setUpdatedOn(new Date());
			return storageClassIdRepository.save(dbStorageClassId);
		}
	}

	/**
	 * updateStorageClassId
	 * @param loginUserID
	 * @param storageClassId
	 * @param updateStorageClassId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClassId updateStorageClassId (String warehouseId, Long storageClassId,String companyCodeId,String languageId,String plantId, String loginUserID,
												UpdateStorageClassId updateStorageClassId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClassId dbStorageClassId = getStorageClassId(warehouseId,storageClassId,companyCodeId,languageId,plantId);
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
	public void deleteStorageClassId (String warehouseId, Long storageClassId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		StorageClassId dbStorageClassId = getStorageClassId(warehouseId,storageClassId,companyCodeId,languageId,plantId);
		if ( dbStorageClassId != null) {
			dbStorageClassId.setDeletionIndicator(1L);
			dbStorageClassId.setUpdatedBy(loginUserID);
			storageClassIdRepository.save(dbStorageClassId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageClassId);
		}
	}
	//Find StorageClassId

	public List<StorageClassId> findStorageClassId(FindStorageClassId findStorageClassId) throws ParseException {

		StorageClassIdSpecification spec = new StorageClassIdSpecification(findStorageClassId);
		List<StorageClassId> results = storageClassIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StorageClassId> newStorageClassId=new ArrayList<>();
		for(StorageClassId dbStorageClassId:results) {
			if (dbStorageClassId.getCompanyIdAndDescription() != null&&dbStorageClassId.getPlantIdAndDescription()!=null&&dbStorageClassId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStorageClassId.getCompanyCodeId(), dbStorageClassId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStorageClassId.getPlantId(), dbStorageClassId.getLanguageId(),dbStorageClassId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageClassId.getWarehouseId(), dbStorageClassId.getLanguageId(), dbStorageClassId.getCompanyCodeId(), dbStorageClassId.getPlantId());
				if (iKeyValuePair != null) {
					dbStorageClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStorageClassId.add(dbStorageClassId);
		}
		return newStorageClassId;
	}
}
