package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.workorderstatus.AddWorkOrderStatus;
import com.ustorage.api.master.model.workorderstatus.WorkOrderStatus;
import com.ustorage.api.master.model.workorderstatus.UpdateWorkOrderStatus;
import com.ustorage.api.master.repository.WorkOrderStatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkOrderStatusService {
	
	@Autowired
	private WorkOrderStatusRepository workOrderStatusRepository;
	
	public List<WorkOrderStatus> getWorkOrderStatus () {
		List<WorkOrderStatus> workOrderStatusList =  workOrderStatusRepository.findAll();
		workOrderStatusList = workOrderStatusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return workOrderStatusList;
	}
	
	/**
	 * getWorkOrderStatus
	 * @param workOrderStatusId
	 * @return
	 */
	public WorkOrderStatus getWorkOrderStatus (String workOrderStatusId) {
		Optional<WorkOrderStatus> workOrderStatus = workOrderStatusRepository.findByCodeAndDeletionIndicator(workOrderStatusId, 0L);
		if (workOrderStatus.isEmpty()) {
			return null;
		}
		return workOrderStatus.get();
	}
	
	/**
	 * createWorkOrderStatus
	 * @param newWorkOrderStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderStatus createWorkOrderStatus (AddWorkOrderStatus newWorkOrderStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		WorkOrderStatus dbWorkOrderStatus = new WorkOrderStatus();
		BeanUtils.copyProperties(newWorkOrderStatus, dbWorkOrderStatus, CommonUtils.getNullPropertyNames(newWorkOrderStatus));
		dbWorkOrderStatus.setDeletionIndicator(0L);
		dbWorkOrderStatus.setCreatedBy(loginUserId);
		dbWorkOrderStatus.setUpdatedBy(loginUserId);
		dbWorkOrderStatus.setCreatedOn(new Date());
		dbWorkOrderStatus.setUpdatedOn(new Date());
		return workOrderStatusRepository.save(dbWorkOrderStatus);
	}
	
	/**
	 * updateWorkOrderStatus
	 * @param workOrderStatusId
	 * @param loginUserId 
	 * @param updateWorkOrderStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderStatus updateWorkOrderStatus (String code, String loginUserId, UpdateWorkOrderStatus updateWorkOrderStatus)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderStatus dbWorkOrderStatus = getWorkOrderStatus(code);
		BeanUtils.copyProperties(updateWorkOrderStatus, dbWorkOrderStatus, CommonUtils.getNullPropertyNames(updateWorkOrderStatus));
		dbWorkOrderStatus.setUpdatedBy(loginUserId);
		dbWorkOrderStatus.setUpdatedOn(new Date());
		return workOrderStatusRepository.save(dbWorkOrderStatus);
	}
	
	/**
	 * deleteWorkOrderStatus
	 * @param loginUserID 
	 * @param workorderstatusCode
	 */
	public void deleteWorkOrderStatus (String workorderstatusModuleId, String loginUserID) {
		WorkOrderStatus workorderstatus = getWorkOrderStatus(workorderstatusModuleId);
		if (workorderstatus != null) {
			workorderstatus.setDeletionIndicator(1L);
			workorderstatus.setUpdatedBy(loginUserID);
			workorderstatus.setUpdatedOn(new Date());
			workOrderStatusRepository.save(workorderstatus);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workorderstatusModuleId);
		}
	}
}
