package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.controlprocessid.*;
import com.tekclover.wms.api.idmaster.repository.ControlProcessIdRepository;
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
public class ControlProcessIdService extends BaseService {
	
	@Autowired
	private ControlProcessIdRepository controlProcessIdRepository;
	
	/**
	 * getControlProcessIds
	 * @return
	 */
	public List<ControlProcessId> getControlProcessIds () {
		List<ControlProcessId> controlProcessIdList =  controlProcessIdRepository.findAll();
		controlProcessIdList = controlProcessIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return controlProcessIdList;
	}
	
	/**
	 * getControlProcessId
	 * @param controlProcessId
	 * @return
	 */
	public ControlProcessId getControlProcessId (String warehouseId, String controlProcessId) {
		Optional<ControlProcessId> dbControlProcessId = 
				controlProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlProcessIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								controlProcessId,
								getLanguageId(),
								0L
								);
		if (dbControlProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"controlProcessId - " + controlProcessId +
						" doesn't exist.");
			
		} 
		return dbControlProcessId.get();
	}
	
	/**
	 * createControlProcessId
	 * @param newControlProcessId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlProcessId createControlProcessId (AddControlProcessId newControlProcessId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ControlProcessId dbControlProcessId = new ControlProcessId();
		log.info("newControlProcessId : " + newControlProcessId);
		BeanUtils.copyProperties(newControlProcessId, dbControlProcessId, CommonUtils.getNullPropertyNames(newControlProcessId));
		dbControlProcessId.setCompanyCodeId(getCompanyCode());
		dbControlProcessId.setPlantId(getPlantId());
		dbControlProcessId.setDeletionIndicator(0L);
		dbControlProcessId.setCreatedBy(loginUserID);
		dbControlProcessId.setUpdatedBy(loginUserID);
		dbControlProcessId.setCreatedOn(new Date());
		dbControlProcessId.setUpdatedOn(new Date());
		return controlProcessIdRepository.save(dbControlProcessId);
	}
	
	/**
	 * updateControlProcessId
	 * @param loginUserID
	 * @param controlProcessId
	 * @param updateControlProcessId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlProcessId updateControlProcessId (String warehouseId, String controlProcessId, String loginUserID,
			UpdateControlProcessId updateControlProcessId) 
			throws IllegalAccessException, InvocationTargetException {
		ControlProcessId dbControlProcessId = getControlProcessId( warehouseId, controlProcessId);
		BeanUtils.copyProperties(updateControlProcessId, dbControlProcessId, CommonUtils.getNullPropertyNames(updateControlProcessId));
		dbControlProcessId.setUpdatedBy(loginUserID);
		dbControlProcessId.setUpdatedOn(new Date());
		return controlProcessIdRepository.save(dbControlProcessId);
	}
	
	/**
	 * deleteControlProcessId
	 * @param loginUserID 
	 * @param controlProcessId
	 */
	public void deleteControlProcessId (String warehouseId, String controlProcessId, String loginUserID) {
		ControlProcessId dbControlProcessId = getControlProcessId( warehouseId, controlProcessId);
		if ( dbControlProcessId != null) {
			dbControlProcessId.setDeletionIndicator(1L);
			dbControlProcessId.setUpdatedBy(loginUserID);
			controlProcessIdRepository.save(dbControlProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + controlProcessId);
		}
	}
}
