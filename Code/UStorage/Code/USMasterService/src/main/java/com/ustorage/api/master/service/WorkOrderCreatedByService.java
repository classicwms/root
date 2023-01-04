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

import com.ustorage.api.master.model.workordercreatedby.AddWorkOrderCreatedBy;
import com.ustorage.api.master.model.workordercreatedby.WorkOrderCreatedBy;
import com.ustorage.api.master.model.workordercreatedby.UpdateWorkOrderCreatedBy;
import com.ustorage.api.master.repository.WorkOrderCreatedByRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkOrderCreatedByService {
	
	@Autowired
	private WorkOrderCreatedByRepository workOrderCreatedByRepository;
	
	public List<WorkOrderCreatedBy> getWorkOrderCreatedBy () {
		List<WorkOrderCreatedBy> workOrderCreatedByList =  workOrderCreatedByRepository.findAll();
		workOrderCreatedByList = workOrderCreatedByList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return workOrderCreatedByList;
	}
	
	/**
	 * getWorkOrderCreatedBy
	 * @param workOrderCreatedById
	 * @return
	 */
	public WorkOrderCreatedBy getWorkOrderCreatedBy (String workOrderCreatedById) {
		Optional<WorkOrderCreatedBy> workOrderCreatedBy = workOrderCreatedByRepository.findByCodeAndDeletionIndicator(workOrderCreatedById, 0L);
		if (workOrderCreatedBy.isEmpty()) {
			return null;
		}
		return workOrderCreatedBy.get();
	}
	
	/**
	 * createWorkOrderCreatedBy
	 * @param newWorkOrderCreatedBy
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderCreatedBy createWorkOrderCreatedBy (AddWorkOrderCreatedBy newWorkOrderCreatedBy, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		WorkOrderCreatedBy dbWorkOrderCreatedBy = new WorkOrderCreatedBy();
		BeanUtils.copyProperties(newWorkOrderCreatedBy, dbWorkOrderCreatedBy, CommonUtils.getNullPropertyNames(newWorkOrderCreatedBy));
		dbWorkOrderCreatedBy.setDeletionIndicator(0L);
		dbWorkOrderCreatedBy.setCreatedBy(loginUserId);
		dbWorkOrderCreatedBy.setUpdatedBy(loginUserId);
		dbWorkOrderCreatedBy.setCreatedOn(new Date());
		dbWorkOrderCreatedBy.setUpdatedOn(new Date());
		return workOrderCreatedByRepository.save(dbWorkOrderCreatedBy);
	}
	
	/**
	 * updateWorkOrderCreatedBy
	 * @param workOrderCreatedById
	 * @param loginUserId 
	 * @param updateWorkOrderCreatedBy
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrderCreatedBy updateWorkOrderCreatedBy (String code, String loginUserId, UpdateWorkOrderCreatedBy updateWorkOrderCreatedBy)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderCreatedBy dbWorkOrderCreatedBy = getWorkOrderCreatedBy(code);
		BeanUtils.copyProperties(updateWorkOrderCreatedBy, dbWorkOrderCreatedBy, CommonUtils.getNullPropertyNames(updateWorkOrderCreatedBy));
		dbWorkOrderCreatedBy.setUpdatedBy(loginUserId);
		dbWorkOrderCreatedBy.setUpdatedOn(new Date());
		return workOrderCreatedByRepository.save(dbWorkOrderCreatedBy);
	}
	
	/**
	 * deleteWorkOrderCreatedBy
	 * @param loginUserID 
	 * @param workordercreatedbyCode
	 */
	public void deleteWorkOrderCreatedBy (String workordercreatedbyModuleId, String loginUserID) {
		WorkOrderCreatedBy workordercreatedby = getWorkOrderCreatedBy(workordercreatedbyModuleId);
		if (workordercreatedby != null) {
			workordercreatedby.setDeletionIndicator(1L);
			workordercreatedby.setUpdatedBy(loginUserID);
			workordercreatedby.setUpdatedOn(new Date());
			workOrderCreatedByRepository.save(workordercreatedby);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workordercreatedbyModuleId);
		}
	}
}
