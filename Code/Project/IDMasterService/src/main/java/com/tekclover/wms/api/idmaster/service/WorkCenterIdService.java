package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.workcenterid.*;
import com.tekclover.wms.api.idmaster.repository.WorkCenterIdRepository;
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
public class WorkCenterIdService extends BaseService {
	
	@Autowired
	private WorkCenterIdRepository workCenterIdRepository;
	
	/**
	 * getWorkCenterIds
	 * @return
	 */
	public List<WorkCenterId> getWorkCenterIds () {
		List<WorkCenterId> workCenterIdList =  workCenterIdRepository.findAll();
		workCenterIdList = workCenterIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return workCenterIdList;
	}
	
	/**
	 * getWorkCenterId
	 * @param workCenterId
	 * @return
	 */
	public WorkCenterId getWorkCenterId (String warehouseId, String workCenterId) {
		Optional<WorkCenterId> dbWorkCenterId = 
				workCenterIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								workCenterId,
								getLanguageId(),
								0L
								);
		if (dbWorkCenterId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"workCenterId - " + workCenterId +
						" doesn't exist.");
			
		} 
		return dbWorkCenterId.get();
	}
	
	/**
	 * createWorkCenterId
	 * @param newWorkCenterId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkCenterId createWorkCenterId (AddWorkCenterId newWorkCenterId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		WorkCenterId dbWorkCenterId = new WorkCenterId();
		log.info("newWorkCenterId : " + newWorkCenterId);
		BeanUtils.copyProperties(newWorkCenterId, dbWorkCenterId, CommonUtils.getNullPropertyNames(newWorkCenterId));
		dbWorkCenterId.setCompanyCodeId(getCompanyCode());
		dbWorkCenterId.setPlantId(getPlantId());
		dbWorkCenterId.setDeletionIndicator(0L);
		dbWorkCenterId.setCreatedBy(loginUserID);
		dbWorkCenterId.setUpdatedBy(loginUserID);
		dbWorkCenterId.setCreatedOn(new Date());
		dbWorkCenterId.setUpdatedOn(new Date());
		return workCenterIdRepository.save(dbWorkCenterId);
	}
	
	/**
	 * updateWorkCenterId
	 * @param loginUserID
	 * @param workCenterId
	 * @param updateWorkCenterId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkCenterId updateWorkCenterId (String warehouseId, String workCenterId, String loginUserID,
			UpdateWorkCenterId updateWorkCenterId) 
			throws IllegalAccessException, InvocationTargetException {
		WorkCenterId dbWorkCenterId = getWorkCenterId( warehouseId, workCenterId);
		BeanUtils.copyProperties(updateWorkCenterId, dbWorkCenterId, CommonUtils.getNullPropertyNames(updateWorkCenterId));
		dbWorkCenterId.setUpdatedBy(loginUserID);
		dbWorkCenterId.setUpdatedOn(new Date());
		return workCenterIdRepository.save(dbWorkCenterId);
	}
	
	/**
	 * deleteWorkCenterId
	 * @param loginUserID 
	 * @param workCenterId
	 */
	public void deleteWorkCenterId (String warehouseId, String workCenterId, String loginUserID) {
		WorkCenterId dbWorkCenterId = getWorkCenterId( warehouseId, workCenterId);
		if ( dbWorkCenterId != null) {
			dbWorkCenterId.setDeletionIndicator(1L);
			dbWorkCenterId.setUpdatedBy(loginUserID);
			workCenterIdRepository.save(dbWorkCenterId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workCenterId);
		}
	}
}
