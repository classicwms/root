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

import com.ustorage.api.master.model.status.AddStatus;
import com.ustorage.api.master.model.status.Status;
import com.ustorage.api.master.model.status.UpdateStatus;
import com.ustorage.api.master.repository.StatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatusService {
	
	@Autowired
	private StatusRepository statusRepository;
	
	public List<Status> getStatus () {
		List<Status> statusList =  statusRepository.findAll();
		statusList = statusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return statusList;
	}
	
	/**
	 * getStatus
	 * @param statusId
	 * @return
	 */
	public Status getStatus (String statusId) {
		Optional<Status> status = statusRepository.findByCodeAndDeletionIndicator(statusId, 0L);
		if (status.isEmpty()) {
			return null;
		}
		return status.get();
	}
	
	/**
	 * createStatus
	 * @param newStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Status createStatus (AddStatus newStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Status dbStatus = new Status();
		BeanUtils.copyProperties(newStatus, dbStatus, CommonUtils.getNullPropertyNames(newStatus));
		dbStatus.setDeletionIndicator(0L);
		dbStatus.setCreatedBy(loginUserId);
		dbStatus.setUpdatedBy(loginUserId);
		dbStatus.setCreatedOn(new Date());
		dbStatus.setUpdatedOn(new Date());
		return statusRepository.save(dbStatus);
	}
	
	/**
	 * updateStatus
	 * @param statusId
	 * @param loginUserId 
	 * @param updateStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Status updateStatus (String code, String loginUserId, UpdateStatus updateStatus)
			throws IllegalAccessException, InvocationTargetException {
		Status dbStatus = getStatus(code);
		BeanUtils.copyProperties(updateStatus, dbStatus, CommonUtils.getNullPropertyNames(updateStatus));
		dbStatus.setUpdatedBy(loginUserId);
		dbStatus.setUpdatedOn(new Date());
		return statusRepository.save(dbStatus);
	}
	
	/**
	 * deleteStatus
	 * @param loginUserID 
	 * @param statusCode
	 */
	public void deleteStatus (String statusModuleId, String loginUserID) {
		Status status = getStatus(statusModuleId);
		if (status != null) {
			status.setDeletionIndicator(1L);
			status.setUpdatedBy(loginUserID);
			status.setUpdatedOn(new Date());
			statusRepository.save(status);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + statusModuleId);
		}
	}
}
