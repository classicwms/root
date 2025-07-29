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
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.FindStorageSectionId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.StorageSectionIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.storagesectionid.AddStorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.UpdateStorageSectionId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageSectionIdService {

	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private FloorIdRepository floorIdRepository;
	@Autowired
	private FloorIdService floorIdService;

	/**
	 * getStorageSectionIds
	 * @return
	 */
	public List<StorageSectionId> getStorageSectionIds () {
		List<StorageSectionId> storageSectionIdList =  storageSectionIdRepository.findAll();
		storageSectionIdList = storageSectionIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageSectionId> newStorageSectionId=new ArrayList<>();
		for(StorageSectionId dbStorageSectionId:storageSectionIdList) {
			if (dbStorageSectionId.getCompanyIdAndDescription() != null&&dbStorageSectionId.getPlantIdAndDescription()!=null&&dbStorageSectionId.getWarehouseIdAndDescription()!=null&&dbStorageSectionId.getFloorIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStorageSectionId.getCompanyCodeId(), dbStorageSectionId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStorageSectionId.getPlantId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyCodeId(), dbStorageSectionId.getPlantId());
				IKeyValuePair iKeyValuePair3 = floorIdRepository.getFloorIdAndDescription(String.valueOf(dbStorageSectionId.getFloorId()), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getCompanyCodeId());
				if (iKeyValuePair != null) {
					dbStorageSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageSectionId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newStorageSectionId.add(dbStorageSectionId);
		}
		return newStorageSectionId;
	}

	/**
	 * getStorageSectionId
	 * @param storageSectionId
	 * @return
	 */
	public StorageSectionId getStorageSectionId (String warehouseId,Long floorId, String storageSectionId,String companyCodeId,String languageId,String plantId) {
		Optional<StorageSectionId> dbStorageSectionId =
				storageSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						floorId,
						storageSectionId,
						languageId,
						0L
				);
		if (dbStorageSectionId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"floorId - " + floorId +
					"storageSectionId - " + storageSectionId +
					" doesn't exist.");

		}
		StorageSectionId newStorageSectionId = new StorageSectionId();
		BeanUtils.copyProperties(dbStorageSectionId.get(),newStorageSectionId, CommonUtils.getNullPropertyNames(dbStorageSectionId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(floorId),languageId,warehouseId,plantId,companyCodeId);
		if(iKeyValuePair!=null) {
			newStorageSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newStorageSectionId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
		}
		return newStorageSectionId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageSectionId dbStorageSectionId = new StorageSectionId();
		Optional<StorageSectionId> duplicateStorageSectionId=storageSectionIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(newStorageSectionId.getCompanyCodeId(), newStorageSectionId.getPlantId(), newStorageSectionId.getWarehouseId(), newStorageSectionId.getFloorId(), newStorageSectionId.getStorageSectionId(), newStorageSectionId.getLanguageId(), 0L);
		if(!duplicateStorageSectionId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		}else {
			FloorId dbFloorId=floorIdService.getFloorId(newStorageSectionId.getWarehouseId(), newStorageSectionId.getFloorId(), newStorageSectionId.getCompanyCodeId(), newStorageSectionId.getLanguageId(), newStorageSectionId.getPlantId());
			log.info("newStorageSectionId : " + newStorageSectionId);
			BeanUtils.copyProperties(newStorageSectionId, dbStorageSectionId, CommonUtils.getNullPropertyNames(newStorageSectionId));
			dbStorageSectionId.setDeletionIndicator(0L);
			dbStorageSectionId.setCompanyIdAndDescription(dbFloorId.getCompanyIdAndDescription());
			dbStorageSectionId.setPlantIdAndDescription(dbFloorId.getPlantIdAndDescription());
			dbStorageSectionId.setWarehouseIdAndDescription(dbFloorId.getWarehouseIdAndDescription());
			dbStorageSectionId.setFloorIdAndDescription(dbFloorId.getFloorId()+"-" + dbFloorId.getDescription());
			dbStorageSectionId.setCreatedBy(loginUserID);
			dbStorageSectionId.setUpdatedBy(loginUserID);
			dbStorageSectionId.setCreatedOn(new Date());
			dbStorageSectionId.setUpdatedOn(new Date());
			storageSectionIdRepository.save(dbStorageSectionId);
		}
		return dbStorageSectionId;
	}

	/**
	 * updateStorageSectionId
	 * @param loginUserID
	 * @param storageSectionId
	 * @param updateStorageSectionId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageSectionId updateStorageSectionId (String warehouseId,Long floorId,String storageSectionId,
													String companyCodeId,String languageId,String plantId,String loginUserID,
													UpdateStorageSectionId updateStorageSectionId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageSectionId dbStorageSectionId = getStorageSectionId(warehouseId,floorId,storageSectionId,companyCodeId,languageId,plantId);
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
	public void deleteStorageSectionId (String warehouseId, Long floorId, String storageSectionId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		StorageSectionId dbStorageSectionId = getStorageSectionId(warehouseId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		if ( dbStorageSectionId != null) {
			dbStorageSectionId.setDeletionIndicator(1L);
			dbStorageSectionId.setUpdatedBy(loginUserID);
			storageSectionIdRepository.save(dbStorageSectionId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageSectionId);
		}
	}

	//Find StorageSectionId
	public List<StorageSectionId> findStorageSectionId(FindStorageSectionId findStorageSectionId) throws ParseException {

		StorageSectionIdSpecification spec = new StorageSectionIdSpecification(findStorageSectionId);
		List<StorageSectionId> results = storageSectionIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StorageSectionId> newStorageSectionId=new ArrayList<>();
		for(StorageSectionId dbStorageSectionId:results) {
			if (dbStorageSectionId.getCompanyIdAndDescription() != null&&dbStorageSectionId.getPlantIdAndDescription()!=null&&dbStorageSectionId.getWarehouseIdAndDescription()!=null&&dbStorageSectionId.getFloorIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStorageSectionId.getCompanyCodeId(), dbStorageSectionId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStorageSectionId.getPlantId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getLanguageId(), dbStorageSectionId.getCompanyCodeId(), dbStorageSectionId.getPlantId());
				IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(dbStorageSectionId.getFloorId()), dbStorageSectionId.getLanguageId(),dbStorageSectionId.getWarehouseId(), dbStorageSectionId.getPlantId(), dbStorageSectionId.getCompanyCodeId());
				if (iKeyValuePair != null) {
					dbStorageSectionId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageSectionId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStorageSectionId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbStorageSectionId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newStorageSectionId.add(dbStorageSectionId);
		}
		return newStorageSectionId;
	}
}
