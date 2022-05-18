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
import com.tekclover.wms.api.idmaster.model.hhtuser.AddHhtUser;
import com.tekclover.wms.api.idmaster.model.hhtuser.HhtUser;
import com.tekclover.wms.api.idmaster.model.hhtuser.UpdateHhtUser;
import com.tekclover.wms.api.idmaster.repository.HhtUserRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HhtUserService extends BaseService {
	
	@Autowired
	private HhtUserRepository hhtUserRepository;
	
	/**
	 * getHhtUsers
	 * @return
	 */
	public List<HhtUser> getHhtUsers () {
		List<HhtUser> hhtUserList =  hhtUserRepository.findAll();
		hhtUserList = hhtUserList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return hhtUserList;
	}
	
	/**
	 * getHhtUser
	 * @param userId
	 * @return
	 */
	public HhtUser getHhtUser (String userId, String warehouseId) {
		HhtUser hhtUser = hhtUserRepository.findByUserIdAndWarehouseIdAndDeletionIndicator(userId, warehouseId, 0L);
		if (hhtUser != null) {
			return hhtUser;
		} else {
			throw new BadRequestException("The given HhtUser ID : " + userId + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<HhtUser> getHhtUser (String warehouseId) {
		List<HhtUser> hhtUser = hhtUserRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L);
		if (hhtUser != null) {
			return hhtUser;
		} else {
			throw new BadRequestException("The given warehouseId ID : " + warehouseId + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchHhtUser
	 * @return
	 * @throws ParseException
	 */
//	public List<HhtUser> findHhtUser(SearchHhtUser searchHhtUser) 
//			throws ParseException {
//		HhtUserSpecification spec = new HhtUserSpecification(searchHhtUser);
//		List<HhtUser> results = hhtUserRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createHhtUser
	 * @param newHhtUser
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HhtUser createHhtUser (AddHhtUser newHhtUser, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HhtUser dbHhtUser = new HhtUser();
		log.info("newHhtUser : " + newHhtUser);
		
		BeanUtils.copyProperties(newHhtUser, dbHhtUser, CommonUtils.getNullPropertyNames(newHhtUser));
		dbHhtUser.setCompanyCodeId(getCompanyCode());
		dbHhtUser.setDeletionIndicator(0L);
		dbHhtUser.setCreatedBy(loginUserID);
		dbHhtUser.setUpdatedBy(loginUserID);
		dbHhtUser.setCreatedOn(new Date());
		dbHhtUser.setUpdatedOn(new Date());
		return hhtUserRepository.save(dbHhtUser);
	}
	
	/**
	 * updateHhtUser
	 * @param loginUserId 
	 * @param userId
	 * @param updateHhtUser
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HhtUser updateHhtUser (String warehouseId, String userId, UpdateHhtUser updateHhtUser, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HhtUser dbHhtUser = getHhtUser(userId, warehouseId);
		BeanUtils.copyProperties(updateHhtUser, dbHhtUser, CommonUtils.getNullPropertyNames(updateHhtUser));
		dbHhtUser.setUpdatedBy(loginUserID);
		dbHhtUser.setUpdatedOn(new Date());
		return hhtUserRepository.save(dbHhtUser);
	}
	
	/**
	 * deleteHhtUser
	 * @param loginUserID 
	 * @param userId
	 */
	public void deleteHhtUser (String warehouseId, String userId, String loginUserID) {
		HhtUser hhtUser = getHhtUser(userId, warehouseId);
		if ( hhtUser != null) {
			hhtUser.setDeletionIndicator(1L);
			hhtUser.setUpdatedBy(loginUserID);
			hhtUserRepository.save(hhtUser);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + userId);
		}
	}
}
