package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.warehouseid.AddWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.FindWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.UpdateWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.ModuleIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.RoleAccessRepository;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.WarehouseSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService  {
	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private PlantIdRepository plantIdRepository;
	
	@Autowired
	private PlantIdService plantIdService;
	
	@Autowired
	private RoleAccessRepository roleAccessRepository;
	
	@Autowired
	private ModuleIdRepository moduleIdRepository;

	/**
	 * getWarehouses
	 * @return
	 */
	public List<Warehouse> getWarehouses () {
		List<Warehouse> warehouseIdList =  warehouseRepository.findAll();
		warehouseIdList = warehouseIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<Warehouse>newWarehouse=new ArrayList<>();
		for(Warehouse dbWarehouse:warehouseIdList) {
			if (dbWarehouse.getCompanyIdAndDescription() != null&&dbWarehouse.getPlantIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbWarehouse.getCompanyCodeId(), dbWarehouse.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbWarehouse.getPlantId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyCodeId());
				if (iKeyValuePair != null) {
					dbWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
			}
			newWarehouse.add(dbWarehouse);
		}
		return newWarehouse;
	}

	/**
	 * getWarehouse
	 * @param warehouseId
	 * @returnm
	 */
	public Warehouse getWarehouse (String warehouseId,String companyCodeId,String plantId,String languageId) {
		Optional<Warehouse> dbWarehouse =
				warehouseRepository.findByCompanyCodeIdAndWarehouseIdAndLanguageIdAndPlantIdAndDeletionIndicator(
						companyCodeId,
						warehouseId,
						languageId,
						plantId,
						0L
				);
		if (dbWarehouse.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					" doesn't exist.");

		}
		Warehouse newWarehouse = new Warehouse();
		BeanUtils.copyProperties(dbWarehouse.get(),newWarehouse, CommonUtils.getNullPropertyNames(dbWarehouse));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		if(iKeyValuePair!=null) {
			newWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		return newWarehouse;
	}

	/**
	 * 
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @return
	 */
	public Warehouse getWarehouse (String companyCodeId, String plantId, String languageId) {
		Optional<Warehouse> dbWarehouse =
				warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						languageId,
						0L
				);
		if (dbWarehouse.isEmpty()) {
			throw new BadRequestException("The given values : " +
					" companyCodeId - " + companyCodeId +
					" plantId - " + plantId +
					" languageId - " + languageId +
					" doesn't exist.");
		}
		
		Warehouse newWarehouse = new Warehouse();
		BeanUtils.copyProperties(dbWarehouse.get(), newWarehouse, CommonUtils.getNullPropertyNames(dbWarehouse));
		IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		
		if(iKeyValuePair != null) {
			newWarehouse.setCompanyIdAndDescription (iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		
		if(iKeyValuePair1 != null) {
			newWarehouse.setPlantIdAndDescription (iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		return newWarehouse;
	}
	
	/**
	 * createWarehouse
	 * @param newWarehouse
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse createWarehouse (AddWarehouse newWarehouse, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Warehouse dbWarehouse = new Warehouse();
		Optional<Warehouse> duplicateWarehouse=warehouseRepository.findByCompanyCodeIdAndWarehouseIdAndLanguageIdAndPlantIdAndDeletionIndicator(newWarehouse.getCompanyCodeId(), newWarehouse.getWarehouseId(), newWarehouse.getLanguageId(), newWarehouse.getPlantId(), 0L);
		if(!duplicateWarehouse.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			PlantId dbPlantId=plantIdService.getPlantId(newWarehouse.getPlantId(), newWarehouse.getCompanyCodeId(), newWarehouse.getLanguageId());
			log.info("newWarehouse : " + newWarehouse);
			BeanUtils.copyProperties(newWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(newWarehouse));
			dbWarehouse.setDeletionIndicator(0L);
			dbWarehouse.setCompanyIdAndDescription(dbPlantId.getCompanyIdAndDescription());
			dbWarehouse.setPlantIdAndDescription(dbPlantId.getPlantId()+"-"+dbPlantId.getDescription());
			dbWarehouse.setCreatedBy(loginUserID);
			dbWarehouse.setUpdatedBy(loginUserID);
			dbWarehouse.setCreatedOn(new Date());
			dbWarehouse.setUpdatedOn(new Date());
			warehouseRepository.save(dbWarehouse);
		}
		return dbWarehouse;
	}

	/**
	 * updateWarehouse
	 * @param loginUserID
	 * @param warehouseId
	 * @param updateWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse updateWarehouse (String warehouseId,String companyCodeId,String plantId,String languageId, String loginUserID,
									  UpdateWarehouse updateWarehouse)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Warehouse dbWarehouse = getWarehouse(warehouseId,companyCodeId,plantId,languageId);
		BeanUtils.copyProperties(updateWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(updateWarehouse));
		dbWarehouse.setUpdatedBy(loginUserID);
		dbWarehouse.setUpdatedOn(new Date());

		updateRoleAccess(warehouseId, companyCodeId, plantId, languageId, updateWarehouse.getWarehouseDesc());									//Update Description
		updateModuleId(warehouseId, companyCodeId, plantId, languageId, updateWarehouse.getWarehouseDesc());									//Update Description

		return warehouseRepository.save(dbWarehouse);
	}

	/**
	 * deleteWarehouse
	 * @param loginUserID
	 * @param warehouseId
	 */
	public void deleteWarehouse (String warehouseId,String companyCodeId,String plantId,String languageId,String loginUserID) {
		Warehouse dbWarehouse = getWarehouse(warehouseId,companyCodeId,plantId,languageId);
		if ( dbWarehouse != null) {
			dbWarehouse.setDeletionIndicator(1L);
			dbWarehouse.setUpdatedBy(loginUserID);
			warehouseRepository.save(dbWarehouse);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + warehouseId);
		}
	}

	//Find Warehouse
	public List<Warehouse> findWarehouse(FindWarehouse findWarehouse) throws ParseException {

		WarehouseSpecification spec = new WarehouseSpecification(findWarehouse);
		List<Warehouse> results = warehouseRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<Warehouse>newWarehouse=new ArrayList<>();
		for(Warehouse dbWarehouse:results) {
			if (dbWarehouse.getCompanyIdAndDescription() != null&&dbWarehouse.getPlantIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbWarehouse.getCompanyCodeId(), dbWarehouse.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbWarehouse.getPlantId(), dbWarehouse.getLanguageId(), dbWarehouse.getCompanyCodeId());
				if (iKeyValuePair != null) {
					dbWarehouse.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWarehouse.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
			}
			newWarehouse.add(dbWarehouse);
		}
		return newWarehouse;
	}

	//update Description
	public void updateRoleAccess(String warehouseId, String companyCodeId, String plantId, String languageId, String description){
		List<RoleAccess> roleAccessList = roleAccessRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
				languageId, companyCodeId, plantId, warehouseId, 0L	);

		if (roleAccessList != null) {
			roleAccessList.stream().forEach(n -> n.setWarehouseIdAndDescription(warehouseId + "-" + description));
		}
	}
	public void updateModuleId(String warehouseId, String companyCodeId, String plantId, String languageId, String description){
		List<ModuleId> moduleIdList = moduleIdRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
				languageId, companyCodeId, plantId, warehouseId, 0L	);

		if (moduleIdList != null) {
			moduleIdList.stream().forEach(n -> n.setWarehouseIdAndDescription(warehouseId + "-" + description));
		}
	}
}
