package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.user.AddUserManagement;
import com.tekclover.wms.api.idmaster.model.user.UpdateUserManagement;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.repository.UserManagementRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserManagementService {
	
	@Autowired
	private UserManagementRepository userManagementRepository;
	private PasswordEncoder passwordEncoder = new PasswordEncoder();
	
	/**
	 * 
	 * @return
	 */
	public List<UserManagement> getUserManagements () {
		List<UserManagement> userManagementList = userManagementRepository.findAll();
		userManagementList = userManagementList.stream().filter(a -> a.getDeletionIndicator() != null && 
				a.getDeletionIndicator().longValue() == 0).collect(Collectors.toList());
		return userManagementList;
	}
	
	/**
	 * 
	 * @param emailID
	 * @return
	 */
	public UserManagement getUserManagement (String warehouseId, String userId) {
		UserManagement userManagement = userManagementRepository.findByWarehouseIdAndUserIdAndDeletionIndicator(warehouseId, userId, 0L);
		if (userManagement == null) {
    		throw new BadRequestException("Invalid Username : " + userId);
    	}
		return userManagement;
	}
	
	/**
	 * 
	 * @param userId
	 * @param warehouseId
	 * @return
	 */
	public List<UserManagement> getUserManagement(String userId) {
		List<UserManagement> userManagement = 
				userManagementRepository.findByUserIdAndDeletionIndicator(userId, 0L);
		if (userManagement == null) {
    		throw new BadRequestException("Invalid Username : " + userId);
    	}
		return userManagement;
	}
	
	/**
	 * 
	 * @param emailID
	 * @param loginPassword
	 * @return
	 */
	public UserManagement validateUser (String userId, String loginPassword) {
		List<UserManagement> userManagementList = 
				userManagementRepository.findByUserIdAndDeletionIndicator(userId, 0L);
		if (userManagementList.isEmpty()) {
    		throw new BadRequestException("Invalid Username : " + userId);
    	}
		
		boolean isSuccess = false;
		for (UserManagement userManagement : userManagementList) {
			isSuccess = passwordEncoder.matches(loginPassword, userManagement.getPassword());
			if (isSuccess) {
				return userManagement;
			}
		}
		
		if (!isSuccess) {
			throw new BadRequestException("Password is wrong. Please enter correct password.");
		}
		return null; 
	}
	
	/**
	 * createUserManagement
	 * @param newUserManagement
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserManagement createUserManagement (AddUserManagement newUserManagement, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UserManagement dbUserManagement = new UserManagement();
		BeanUtils.copyProperties(newUserManagement, dbUserManagement, CommonUtils.getNullPropertyNames(newUserManagement));
		
		// Password encryption
		try {
			String encodedPwd = passwordEncoder.encodePassword(newUserManagement.getPassword());
			dbUserManagement.setPassword(encodedPwd);
			dbUserManagement.setCreatedBy(loginUserID);
			dbUserManagement.setCreatedOn(new Date());
			dbUserManagement.setUpdatedBy(loginUserID);
			dbUserManagement.setUpdatedOn(new Date());
			dbUserManagement.setDeletionIndicator(0L);
			return userManagementRepository.save(dbUserManagement);
		} catch (Exception e) {
			log.error("Error : " + e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param userID
	 * @param updateUserManagement
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserManagement updateUserManagement (String userId, String warehouseId, UpdateUserManagement updateUserManagement, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UserManagement dbUserManagement = getUserManagement(warehouseId, userId);
		BeanUtils.copyProperties(updateUserManagement, dbUserManagement, CommonUtils.getNullPropertyNames(updateUserManagement));
		
		if (updateUserManagement.getPassword() != null) {
			// Password encryption
			String encodedPwd = passwordEncoder.encodePassword(updateUserManagement.getPassword());
			dbUserManagement.setPassword(encodedPwd);
		}
		dbUserManagement.setUpdatedBy(loginUserID);
		dbUserManagement.setUpdatedOn(new Date());
		return userManagementRepository.save(dbUserManagement);
	}
	
	/**
	 * deleteUserManagement
	 * @param warehouseCode
	 */
	public void deleteUserManagement (String userId, String warehouseId, String loginUserID) {
		UserManagement dbUserManagement = getUserManagement(warehouseId, userId);
		if ( dbUserManagement != null) {
			dbUserManagement.setUpdatedBy(loginUserID);
			dbUserManagement.setUpdatedOn(new Date());
			dbUserManagement.setDeletionIndicator(1L);
			userManagementRepository.save(dbUserManagement);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + userId);
		}
	}
}
