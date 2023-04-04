package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.*;
import com.tekclover.wms.api.idmaster.repository.AdhocModuleIdRepository;
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
public class AdhocModuleIdService extends BaseService {
	
	@Autowired
	private AdhocModuleIdRepository adhocModuleIdRepository;
	
	/**
	 * getAdhocModuleIds
	 * @return
	 */
	public List<AdhocModuleId> getAdhocModuleIds () {
		List<AdhocModuleId> adhocModuleIdList =  adhocModuleIdRepository.findAll();
		adhocModuleIdList = adhocModuleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return adhocModuleIdList;
	}
	
	/**
	 * getAdhocModuleId
	 * @param adhocModuleId
	 * @return
	 */
	public AdhocModuleId getAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId) {
		Optional<AdhocModuleId> dbAdhocModuleId = 
				adhocModuleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndAdhocModuleIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								moduleId,
								adhocModuleId,
								getLanguageId(),
								0L
								);
		if (dbAdhocModuleId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"moduleId - " + moduleId +
						"adhocModuleId - " + adhocModuleId +
						" doesn't exist.");
			
		} 
		return dbAdhocModuleId.get();
	}

	/**
	 * createAdhocModuleId
	 * @param newAdhocModuleId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AdhocModuleId createAdhocModuleId (AddAdhocModuleId newAdhocModuleId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId dbAdhocModuleId = new AdhocModuleId();
		log.info("newAdhocModuleId : " + newAdhocModuleId);
		BeanUtils.copyProperties(newAdhocModuleId, dbAdhocModuleId, CommonUtils.getNullPropertyNames(newAdhocModuleId));
		dbAdhocModuleId.setCompanyCodeId(getCompanyCode());
		dbAdhocModuleId.setPlantId(getPlantId());
		dbAdhocModuleId.setDeletionIndicator(0L);
		dbAdhocModuleId.setCreatedBy(loginUserID);
		dbAdhocModuleId.setUpdatedBy(loginUserID);
		dbAdhocModuleId.setCreatedOn(new Date());
		dbAdhocModuleId.setUpdatedOn(new Date());
		return adhocModuleIdRepository.save(dbAdhocModuleId);
	}
	
	/**
	 * updateAdhocModuleId
	 * @param loginUserID
	 * @param adhocModuleId
	 * @param updateAdhocModuleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AdhocModuleId updateAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId, String loginUserID,
			UpdateAdhocModuleId updateAdhocModuleId) 
			throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId dbAdhocModuleId = getAdhocModuleId(warehouseId, moduleId, adhocModuleId);
		BeanUtils.copyProperties(updateAdhocModuleId, dbAdhocModuleId, CommonUtils.getNullPropertyNames(updateAdhocModuleId));
		dbAdhocModuleId.setUpdatedBy(loginUserID);
		dbAdhocModuleId.setUpdatedOn(new Date());
		return adhocModuleIdRepository.save(dbAdhocModuleId);
	}
	
	/**
	 * deleteAdhocModuleId
	 * @param loginUserID 
	 * @param adhocModuleId
	 */
	public void deleteAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId, String loginUserID) {
		AdhocModuleId dbAdhocModuleId = getAdhocModuleId(warehouseId, moduleId, adhocModuleId);
		if ( dbAdhocModuleId != null) {
			dbAdhocModuleId.setDeletionIndicator(1L);
			dbAdhocModuleId.setUpdatedBy(loginUserID);
			adhocModuleIdRepository.save(dbAdhocModuleId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + adhocModuleId);
		}
	}
}
