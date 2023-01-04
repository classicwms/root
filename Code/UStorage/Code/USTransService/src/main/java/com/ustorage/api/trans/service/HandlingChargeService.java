package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.handlingcharge.*;

import com.ustorage.api.trans.repository.HandlingChargeRepository;
import com.ustorage.api.trans.repository.Specification.HandlingChargeSpecification;
import com.ustorage.api.trans.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HandlingChargeService {
	
	@Autowired
	private HandlingChargeRepository handlingChargeRepository;
	
	public List<HandlingCharge> getHandlingCharge () {
		List<HandlingCharge> handlingChargeList =  handlingChargeRepository.findAll();
		handlingChargeList = handlingChargeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return handlingChargeList;
	}
	
	/**
	 * getHandlingCharge
	 * @param handlingChargeId
	 * @return
	 */
	public HandlingCharge getHandlingCharge (String handlingChargeId) {
		Optional<HandlingCharge> handlingCharge = handlingChargeRepository.findByItemCodeAndDeletionIndicator(handlingChargeId, 0L);
		if (handlingCharge.isEmpty()) {
			return null;
		}
		return handlingCharge.get();
	}
	
	/**
	 * createHandlingCharge
	 * @param newHandlingCharge
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingCharge createHandlingCharge (AddHandlingCharge newHandlingCharge, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		HandlingCharge dbHandlingCharge = new HandlingCharge();
		BeanUtils.copyProperties(newHandlingCharge, dbHandlingCharge, CommonUtils.getNullPropertyNames(newHandlingCharge));
		dbHandlingCharge.setDeletionIndicator(0L);
		dbHandlingCharge.setCreatedBy(loginUserId);
		dbHandlingCharge.setUpdatedBy(loginUserId);
		dbHandlingCharge.setCreatedOn(new Date());
		dbHandlingCharge.setUpdatedOn(new Date());
		return handlingChargeRepository.save(dbHandlingCharge);
	}
	
	/**
	 * updateHandlingCharge
	 * @param itemCode
	 * @param loginUserId 
	 * @param updateHandlingCharge
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingCharge updateHandlingCharge (String itemCode, String loginUserId, UpdateHandlingCharge updateHandlingCharge)
			throws IllegalAccessException, InvocationTargetException {
		HandlingCharge dbHandlingCharge = getHandlingCharge(itemCode);
		BeanUtils.copyProperties(updateHandlingCharge, dbHandlingCharge, CommonUtils.getNullPropertyNames(updateHandlingCharge));
		dbHandlingCharge.setUpdatedBy(loginUserId);
		dbHandlingCharge.setUpdatedOn(new Date());
		return handlingChargeRepository.save(dbHandlingCharge);
	}
	
	/**
	 * deleteHandlingCharge
	 * @param loginUserID 
	 * @param handlingchargeModuleId
	 */
	public void deleteHandlingCharge (String handlingchargeModuleId, String loginUserID) {
		HandlingCharge handlingcharge = getHandlingCharge(handlingchargeModuleId);
		if (handlingcharge != null) {
			handlingcharge.setDeletionIndicator(1L);
			handlingcharge.setUpdatedBy(loginUserID);
			handlingcharge.setUpdatedOn(new Date());
			handlingChargeRepository.save(handlingcharge);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + handlingchargeModuleId);
		}
	}

	//Find HandlingCharge

	public List<HandlingCharge> findHandlingCharge(FindHandlingCharge findHandlingCharge) throws ParseException {

		HandlingChargeSpecification spec = new HandlingChargeSpecification(findHandlingCharge);
		List<HandlingCharge> results = handlingChargeRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
