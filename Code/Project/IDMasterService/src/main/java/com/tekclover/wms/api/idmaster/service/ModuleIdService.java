package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.moduleid.*;
import com.tekclover.wms.api.idmaster.repository.ModuleIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModuleIdService extends BaseService {
	
	@Autowired
	private ModuleIdRepository moduleIdRepository;
	
	/**
	 * getModuleIds
	 * @return
	 */
	public List<ModuleId> getModuleIds () {
		List<ModuleId> moduleIdList =  moduleIdRepository.findAll();
		moduleIdList = moduleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return moduleIdList;
	}
	
	/**
	 * getModuleId
	 * @param moduleId
	 * @return
	 */
	public ModuleId getModuleId (String warehouseId, String moduleId) {
		Optional<ModuleId> dbModuleId = 
				moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								moduleId,
								getLanguageId(),
								0L
								);
		if (dbModuleId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"moduleId - " + moduleId +
						" doesn't exist.");
			
		} 
		return dbModuleId.get();
	}
	
	/**
	 * createModuleId
	 * @param newModuleId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ModuleId createModuleId (AddModuleId newModuleId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ModuleId dbModuleId = new ModuleId();
		log.info("newModuleId : " + newModuleId);
		BeanUtils.copyProperties(newModuleId, dbModuleId, CommonUtils.getNullPropertyNames(newModuleId));
		dbModuleId.setCompanyCodeId(getCompanyCode());
		dbModuleId.setPlantId(getPlantId());
		dbModuleId.setDeletionIndicator(0L);
		dbModuleId.setCreatedBy(loginUserID);
		dbModuleId.setUpdatedBy(loginUserID);
		dbModuleId.setCreatedOn(new Date());
		dbModuleId.setUpdatedOn(new Date());
		return moduleIdRepository.save(dbModuleId);
	}
	
	/**
	 * updateModuleId
	 * @param loginUserID
	 * @param moduleId
	 * @param updateModuleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ModuleId updateModuleId (String warehouseId, String moduleId, String loginUserID,
			UpdateModuleId updateModuleId) 
			throws IllegalAccessException, InvocationTargetException {
		ModuleId dbModuleId = getModuleId( warehouseId, moduleId);
		BeanUtils.copyProperties(updateModuleId, dbModuleId, CommonUtils.getNullPropertyNames(updateModuleId));
		dbModuleId.setUpdatedBy(loginUserID);
		dbModuleId.setUpdatedOn(new Date());
		return moduleIdRepository.save(dbModuleId);
	}
	
	/**
	 * deleteModuleId
	 * @param loginUserID 
	 * @param moduleId
	 */
	public void deleteModuleId (String warehouseId, String moduleId, String loginUserID) {
		ModuleId dbModuleId = getModuleId( warehouseId, moduleId);
		if ( dbModuleId != null) {
			dbModuleId.setDeletionIndicator(1L);
			dbModuleId.setUpdatedBy(loginUserID);
			moduleIdRepository.save(dbModuleId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + moduleId);
		}
	}
}
