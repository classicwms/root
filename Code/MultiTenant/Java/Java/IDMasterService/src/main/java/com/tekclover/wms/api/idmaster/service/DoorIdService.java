package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;


import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.doorid.*;

import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DoorIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.DoorIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoorIdService {

	@Autowired
	private DoorIdRepository doorIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getDoorIds
	 * @return
	 */
	public List<DoorId> getDoorIds () {
		List<DoorId> doorIdList =  doorIdRepository.findAll();
		doorIdList = doorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<DoorId> newDoorId=new ArrayList<>();
		for(DoorId dbDoorId:doorIdList) {
			if (dbDoorId.getCompanyIdAndDescription() != null&&dbDoorId.getPlantIdAndDescription()!=null&&dbDoorId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbDoorId.getCompanyCodeId(), dbDoorId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbDoorId.getPlantId(), dbDoorId.getLanguageId(), dbDoorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbDoorId.getWarehouseId(), dbDoorId.getLanguageId(), dbDoorId.getCompanyCodeId(), dbDoorId.getPlantId());
				if (iKeyValuePair != null) {
					dbDoorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDoorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDoorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDoorId.add(dbDoorId);
		}
		return newDoorId;
	}

	/**
	 * getDoorId
	 * @param doorId
	 * @return
	 */
	public DoorId getDoorId (String warehouseId,String doorId,String companyCodeId,String languageId,String plantId) {
		Optional<DoorId> dbDoorId =
				doorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDoorIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						doorId,
						languageId,
						0L
				);
		if (dbDoorId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"doorId - " + doorId +
					" doesn't exist.");

		}
		DoorId newDoorId = new DoorId();
		BeanUtils.copyProperties(dbDoorId.get(),newDoorId, CommonUtils.getNullPropertyNames(dbDoorId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newDoorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newDoorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newDoorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newDoorId;
	}

	/**
	 * createDoorId
	 * @param newDoorId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorId createDoorId (AddDoorId newDoorId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DoorId dbDoorId = new DoorId();
		Optional<DoorId> duplicateDoorId = doorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDoorIdAndLanguageIdAndDeletionIndicator(newDoorId.getCompanyCodeId(), newDoorId.getPlantId(), newDoorId.getWarehouseId()
				, newDoorId.getDoorId(), newDoorId.getLanguageId(), 0L);
		if (!duplicateDoorId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newDoorId.getWarehouseId(), newDoorId.getCompanyCodeId(), newDoorId.getPlantId(), newDoorId.getLanguageId());
			log.info("newDoorId : " + newDoorId);
			BeanUtils.copyProperties(newDoorId, dbDoorId, CommonUtils.getNullPropertyNames(newDoorId));
			dbDoorId.setDeletionIndicator(0L);
			dbDoorId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbDoorId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbDoorId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbDoorId.setCreatedBy(loginUserID);
			dbDoorId.setUpdatedBy(loginUserID);
			dbDoorId.setCreatedOn(new Date());
			dbDoorId.setUpdatedOn(new Date());
			return doorIdRepository.save(dbDoorId);
		}
	}

	/**
	 * updateDoorId
	 * @param loginUserID
	 * @param doorId
	 * @param updateDoorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DoorId updateDoorId (String warehouseId,String doorId,String companyCodeId,String languageId,String plantId,String loginUserID,
								UpdateDoorId updateDoorId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DoorId dbDoorId = getDoorId( warehouseId,doorId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateDoorId, dbDoorId, CommonUtils.getNullPropertyNames(updateDoorId));
		dbDoorId.setUpdatedBy(loginUserID);
		dbDoorId.setUpdatedOn(new Date());
		return doorIdRepository.save(dbDoorId);
	}

	/**
	 * deleteDoorId
	 * @param loginUserID
	 * @param doorId
	 */
	public void deleteDoorId (String warehouseId, String doorId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		DoorId dbDoorId = getDoorId(warehouseId,doorId,companyCodeId,languageId,plantId);
		if ( dbDoorId != null) {
			dbDoorId.setDeletionIndicator(1L);
			dbDoorId.setUpdatedBy(loginUserID);
			doorIdRepository.save(dbDoorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + doorId);
		}
	}
	//Find DoorId

	public List<DoorId> findDoorId(FindDoorId findDoorId) throws ParseException {

		DoorIdSpecification spec = new DoorIdSpecification(findDoorId);
		List<DoorId> results = doorIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<DoorId> newDoorId=new ArrayList<>();
		for(DoorId dbDoorId:results) {
			if (dbDoorId.getCompanyIdAndDescription() != null&&dbDoorId.getPlantIdAndDescription()!=null&&dbDoorId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbDoorId.getCompanyCodeId(),dbDoorId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbDoorId.getPlantId(), dbDoorId.getLanguageId(), dbDoorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbDoorId.getWarehouseId(), dbDoorId.getLanguageId(), dbDoorId.getCompanyCodeId(), dbDoorId.getPlantId());
				if (iKeyValuePair != null) {
					dbDoorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDoorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDoorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}	}
			newDoorId.add(dbDoorId);
		}
		return newDoorId;
	}
}
