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

import com.ustorage.api.master.model.workorderprocessedby.AddWorkOrderProcessedBy;
import com.ustorage.api.master.model.workorderprocessedby.WorkOrderProcessedBy;
import com.ustorage.api.master.model.workorderprocessedby.UpdateWorkOrderProcessedBy;
import com.ustorage.api.master.repository.WorkOrderProcessedByRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkOrderProcessedByService {
	
	@Autowired
	private WorkOrderProcessedByRepository workOrderProcessedByRepository;
	
	public List<WorkOrderProcessedBy> getWorkOrderProcessedBy () {
		List<WorkOrderProcessedBy> workOrderProcessedByList =  workOrderProcessedByRepository.findAll();
		workOrderProcessedByList = workOrderProcessedByList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return workOrderProcessedByList;
	}
	
	/**
	 * getWorkOrderProcessedBy
	 * @param workOrderProcessedById
	 * @return
	 */
	public WorkOrderProcessedBy getWorkOrderProcessedBy (String workOrderProcessedById) {
		Optional<WorkOrderProcessedBy> workOrderProcessedBy = workOrderProcessedByRepository.findByCodeAndDeletionIndicator(workOrderProcessedById, 0L);
		if (workOrderProcessedBy.isEmpty()) {
			return null;
		}
		return workOrderProcessedBy.get();
	}
	
	/**
	 * createWorkOrderProcessedBy
	 * @param newWorkOrderProcessedBy
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderProcessedBy createWorkOrderProcessedBy (AddWorkOrderProcessedBy newWorkOrderProcessedBy, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		WorkOrderProcessedBy dbWorkOrderProcessedBy = new WorkOrderProcessedBy();
		BeanUtils.copyProperties(newWorkOrderProcessedBy, dbWorkOrderProcessedBy, CommonUtils.getNullPropertyNames(newWorkOrderProcessedBy));
		dbWorkOrderProcessedBy.setDeletionIndicator(0L);
		dbWorkOrderProcessedBy.setCreatedBy(loginUserId);
		dbWorkOrderProcessedBy.setUpdatedBy(loginUserId);
		dbWorkOrderProcessedBy.setCreatedOn(new Date());
		dbWorkOrderProcessedBy.setUpdatedOn(new Date());
		return workOrderProcessedByRepository.save(dbWorkOrderProcessedBy);
	}
	
	/**
	 * updateWorkOrderProcessedBy
	 * @param workOrderProcessedById
	 * @param loginUserId 
	 * @param updateWorkOrderProcessedBy
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderProcessedBy updateWorkOrderProcessedBy (String code, String loginUserId, UpdateWorkOrderProcessedBy updateWorkOrderProcessedBy)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderProcessedBy dbWorkOrderProcessedBy = getWorkOrderProcessedBy(code);
		BeanUtils.copyProperties(updateWorkOrderProcessedBy, dbWorkOrderProcessedBy, CommonUtils.getNullPropertyNames(updateWorkOrderProcessedBy));
		dbWorkOrderProcessedBy.setUpdatedBy(loginUserId);
		dbWorkOrderProcessedBy.setUpdatedOn(new Date());
		return workOrderProcessedByRepository.save(dbWorkOrderProcessedBy);
	}
	
	/**
	 * deleteWorkOrderProcessedBy
	 * @param loginUserID 
	 * @param workorderprocessedbyCode
	 */
	public void deleteWorkOrderProcessedBy (String workorderprocessedbyModuleId, String loginUserID) {
		WorkOrderProcessedBy workorderprocessedby = getWorkOrderProcessedBy(workorderprocessedbyModuleId);
		if (workorderprocessedby != null) {
			workorderprocessedby.setDeletionIndicator(1L);
			workorderprocessedby.setUpdatedBy(loginUserID);
			workorderprocessedby.setUpdatedOn(new Date());
			workOrderProcessedByRepository.save(workorderprocessedby);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workorderprocessedbyModuleId);
		}
	}
}
