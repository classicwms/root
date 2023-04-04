package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.controltypeid.*;
import com.tekclover.wms.api.idmaster.repository.ControlTypeIdRepository;
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
public class ControlTypeIdService extends BaseService {
	
	@Autowired
	private ControlTypeIdRepository controlTypeIdRepository;
	
	/**
	 * getControlTypeIds
	 * @return
	 */
	public List<ControlTypeId> getControlTypeIds () {
		List<ControlTypeId> controlTypeIdList =  controlTypeIdRepository.findAll();
		controlTypeIdList = controlTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return controlTypeIdList;
	}
	
	/**
	 * getControlTypeId
	 * @param controlTypeId
	 * @return
	 */
	public ControlTypeId getControlTypeId (String warehouseId, String controlTypeId) {
		Optional<ControlTypeId> dbControlTypeId = 
				controlTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								controlTypeId,
								getLanguageId(),
								0L
								);
		if (dbControlTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"controlTypeId - " + controlTypeId +
						" doesn't exist.");
			
		} 
		return dbControlTypeId.get();
	}
	
	/**
	 * createControlTypeId
	 * @param newControlTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlTypeId createControlTypeId (AddControlTypeId newControlTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ControlTypeId dbControlTypeId = new ControlTypeId();
		log.info("newControlTypeId : " + newControlTypeId);
		BeanUtils.copyProperties(newControlTypeId, dbControlTypeId, CommonUtils.getNullPropertyNames(newControlTypeId));
		dbControlTypeId.setCompanyCodeId(getCompanyCode());
		dbControlTypeId.setPlantId(getPlantId());
		dbControlTypeId.setDeletionIndicator(0L);
		dbControlTypeId.setCreatedBy(loginUserID);
		dbControlTypeId.setUpdatedBy(loginUserID);
		dbControlTypeId.setCreatedOn(new Date());
		dbControlTypeId.setUpdatedOn(new Date());
		return controlTypeIdRepository.save(dbControlTypeId);
	}
	
	/**
	 * updateControlTypeId
	 * @param loginUserID
	 * @param controlTypeId
	 * @param updateControlTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlTypeId updateControlTypeId (String warehouseId, String controlTypeId, String loginUserID,
			UpdateControlTypeId updateControlTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		ControlTypeId dbControlTypeId = getControlTypeId( warehouseId, controlTypeId);
		BeanUtils.copyProperties(updateControlTypeId, dbControlTypeId, CommonUtils.getNullPropertyNames(updateControlTypeId));
		dbControlTypeId.setUpdatedBy(loginUserID);
		dbControlTypeId.setUpdatedOn(new Date());
		return controlTypeIdRepository.save(dbControlTypeId);
	}
	
	/**
	 * deleteControlTypeId
	 * @param loginUserID 
	 * @param controlTypeId
	 */
	public void deleteControlTypeId (String warehouseId, String controlTypeId, String loginUserID) {
		ControlTypeId dbControlTypeId = getControlTypeId( warehouseId, controlTypeId);
		if ( dbControlTypeId != null) {
			dbControlTypeId.setDeletionIndicator(1L);
			dbControlTypeId.setUpdatedBy(loginUserID);
			controlTypeIdRepository.save(dbControlTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + controlTypeId);
		}
	}
}
