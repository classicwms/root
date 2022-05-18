package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.usertypeid.AddUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UpdateUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;
import com.tekclover.wms.api.idmaster.repository.UserTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserTypeIdService extends BaseService {
	
	@Autowired
	private UserTypeIdRepository userTypeIdRepository;
	
	/**
	 * getUserTypeIds
	 * @return
	 */
	public List<UserTypeId> getUserTypeIds () {
		List<UserTypeId> userTypeIdList =  userTypeIdRepository.findAll();
		userTypeIdList = userTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return userTypeIdList;
	}
	
	/**
	 * getUserTypeId
	 * @param userTypeId
	 * @return
	 */
	public UserTypeId getUserTypeId ( String warehouseId, Long userTypeId) {
		Optional<UserTypeId> dbUserTypeId = 
				userTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndUserTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								userTypeId,
								getLanguageId(),
								0L
								);
		if (dbUserTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"userTypeId - " + userTypeId +
						" doesn't exist.");
			
		} 
		return dbUserTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchUserTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<UserTypeId> findUserTypeId(SearchUserTypeId searchUserTypeId) 
//			throws ParseException {
//		UserTypeIdSpecification spec = new UserTypeIdSpecification(searchUserTypeId);
//		List<UserTypeId> results = userTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createUserTypeId
	 * @param newUserTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserTypeId createUserTypeId (AddUserTypeId newUserTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UserTypeId dbUserTypeId = new UserTypeId();
		log.info("newUserTypeId : " + newUserTypeId);
		BeanUtils.copyProperties(newUserTypeId, dbUserTypeId, CommonUtils.getNullPropertyNames(newUserTypeId));
		dbUserTypeId.setCompanyCodeId(getCompanyCode());
		dbUserTypeId.setPlantId(getPlantId());
		dbUserTypeId.setDeletionIndicator(0L);
		dbUserTypeId.setCreatedBy(loginUserID);
		dbUserTypeId.setUpdatedBy(loginUserID);
		dbUserTypeId.setCreatedOn(new Date());
		dbUserTypeId.setUpdatedOn(new Date());
		return userTypeIdRepository.save(dbUserTypeId);
	}
	
	/**
	 * updateUserTypeId
	 * @param loginUserId 
	 * @param userTypeId
	 * @param updateUserTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserTypeId updateUserTypeId ( String warehouseId, Long userTypeId, String loginUserID, 
			UpdateUserTypeId updateUserTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		UserTypeId dbUserTypeId = getUserTypeId(warehouseId, userTypeId);
		BeanUtils.copyProperties(updateUserTypeId, dbUserTypeId, CommonUtils.getNullPropertyNames(updateUserTypeId));
		dbUserTypeId.setUpdatedBy(loginUserID);
		dbUserTypeId.setUpdatedOn(new Date());
		return userTypeIdRepository.save(dbUserTypeId);
	}
	
	/**
	 * deleteUserTypeId
	 * @param loginUserID 
	 * @param userTypeId
	 */
	public void deleteUserTypeId ( String warehouseId, Long userTypeId, String loginUserID) {
		UserTypeId dbUserTypeId = getUserTypeId(warehouseId, userTypeId);
		if ( userTypeId != null) {
			dbUserTypeId.setDeletionIndicator(1L);
			dbUserTypeId.setUpdatedBy(loginUserID);
			userTypeIdRepository.save(dbUserTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + userTypeId);
		}
	}
}
