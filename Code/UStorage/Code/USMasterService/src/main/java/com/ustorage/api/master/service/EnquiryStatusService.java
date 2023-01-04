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

import com.ustorage.api.master.model.enquirystatus.AddEnquiryStatus;
import com.ustorage.api.master.model.enquirystatus.EnquiryStatus;
import com.ustorage.api.master.model.enquirystatus.UpdateEnquiryStatus;
import com.ustorage.api.master.repository.EnquiryStatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnquiryStatusService {
	
	@Autowired
	private EnquiryStatusRepository enquiryStatusRepository;
	
	public List<EnquiryStatus> getEnquiryStatus () {
		List<EnquiryStatus> enquiryStatusList =  enquiryStatusRepository.findAll();
		enquiryStatusList = enquiryStatusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return enquiryStatusList;
	}
	
	/**
	 * getEnquiryStatus
	 * @param enquiryStatusId
	 * @return
	 */
	public EnquiryStatus getEnquiryStatus (String enquiryStatusId) {
		Optional<EnquiryStatus> enquiryStatus = enquiryStatusRepository.findByCodeAndDeletionIndicator(enquiryStatusId, 0L);
		if (enquiryStatus.isEmpty()) {
			return null;
		}
		return enquiryStatus.get();
	}
	
	/**
	 * createEnquiryStatus
	 * @param newEnquiryStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EnquiryStatus createEnquiryStatus (AddEnquiryStatus newEnquiryStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		EnquiryStatus dbEnquiryStatus = new EnquiryStatus();
		BeanUtils.copyProperties(newEnquiryStatus, dbEnquiryStatus, CommonUtils.getNullPropertyNames(newEnquiryStatus));
		dbEnquiryStatus.setDeletionIndicator(0L);
		dbEnquiryStatus.setCreatedBy(loginUserId);
		dbEnquiryStatus.setUpdatedBy(loginUserId);
		dbEnquiryStatus.setCreatedOn(new Date());
		dbEnquiryStatus.setUpdatedOn(new Date());
		return enquiryStatusRepository.save(dbEnquiryStatus);
	}
	
	/**
	 * updateEnquiryStatus
	 * @param enquiryStatusId
	 * @param loginUserId 
	 * @param updateEnquiryStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EnquiryStatus updateEnquiryStatus (String code, String loginUserId, UpdateEnquiryStatus updateEnquiryStatus)
			throws IllegalAccessException, InvocationTargetException {
		EnquiryStatus dbEnquiryStatus = getEnquiryStatus(code);
		BeanUtils.copyProperties(updateEnquiryStatus, dbEnquiryStatus, CommonUtils.getNullPropertyNames(updateEnquiryStatus));
		dbEnquiryStatus.setUpdatedBy(loginUserId);
		dbEnquiryStatus.setUpdatedOn(new Date());
		return enquiryStatusRepository.save(dbEnquiryStatus);
	}
	
	/**
	 * deleteEnquiryStatus
	 * @param loginUserID 
	 * @param enquirystatusCode
	 */
	public void deleteEnquiryStatus (String enquirystatusModuleId, String loginUserID) {
		EnquiryStatus enquirystatus = getEnquiryStatus(enquirystatusModuleId);
		if (enquirystatus != null) {
			enquirystatus.setDeletionIndicator(1L);
			enquirystatus.setUpdatedBy(loginUserID);
			enquirystatus.setUpdatedOn(new Date());
			enquiryStatusRepository.save(enquirystatus);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + enquirystatusModuleId);
		}
	}
}
