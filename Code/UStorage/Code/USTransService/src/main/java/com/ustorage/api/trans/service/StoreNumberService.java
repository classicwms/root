package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;


import com.ustorage.api.trans.repository.AgreementRepository;

import com.ustorage.api.trans.repository.Specification.AgreementSpecification;
import com.ustorage.api.trans.repository.Specification.StoreNumberSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.agreement.*;
import com.ustorage.api.trans.model.storenumber.*;

import com.ustorage.api.trans.repository.StoreNumberRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreNumberService {

	@Autowired
	private AgreementRepository agreementRepository;

	@Autowired
	private StoreNumberRepository storeNumberRepository;


	public List<StoreNumber> getStoreNumber () {
		List<StoreNumber> storeNumberList =  storeNumberRepository.findAll();
		storeNumberList = storeNumberList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storeNumberList;
	}

	/**
	 * getStoreNumber
	 * @param agreementNumber
	 * @return
	 */
	public List<StoreNumber> getStoreNumber (String agreementNumber) {
		List<StoreNumber> storeNumber = storeNumberRepository.findByAgreementNumberAndDeletionIndicator(agreementNumber, 0L);
		if (storeNumber.isEmpty()) {
			return null;
		}
		storeNumber = storeNumber.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storeNumber;
	}

	/**
	 * createStoreNumber
	 * @param newStoreNumber
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StoreNumber createStoreNumber (AddStoreNumber newStoreNumber, String loginUserId)
			throws IllegalAccessException, InvocationTargetException, Exception {
		StoreNumber dbStoreNumber = new StoreNumber();
		Agreement dbAgreement = new Agreement();
		BeanUtils.copyProperties(newStoreNumber, dbStoreNumber, CommonUtils.getNullPropertyNames(newStoreNumber));
		dbStoreNumber.setDeletionIndicator(0L);
		dbStoreNumber.setCreatedBy(loginUserId);
		dbStoreNumber.setUpdatedBy(loginUserId);
		dbStoreNumber.setCreatedOn(new Date());
		dbStoreNumber.setUpdatedOn(new Date());
		dbStoreNumber.setAgreementNumber(dbAgreement.getAgreementNumber());
		//StoreNumber savedStoreNumber = agreementRepository.save(dbStoreNumber);

		//dbStoreNumber.setAgreementNumber(savedStoreNumber.getAgreementNumber());
		//return savedStoreNumber;

		return storeNumberRepository.save(dbStoreNumber);
	}

	/**
	 * updateStoreNumber
	 * @param itemCode
	 * @param loginUserId
	 * @param updateStoreNumber
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*public StoreNumber updateStoreNumber (String itemCode, String loginUserId, UpdateStoreNumber updateStoreNumber)
			throws IllegalAccessException, InvocationTargetException {
		StoreNumber dbStoreNumber = getStoreNumber(itemCode);
		BeanUtils.copyProperties(updateStoreNumber, dbStoreNumber, CommonUtils.getNullPropertyNames(updateStoreNumber));
		dbStoreNumber.setUpdatedBy(loginUserId);
		dbStoreNumber.setUpdatedOn(new Date());
		return storeNumberRepository.save(dbStoreNumber);
	}*/

	/**
	 * deleteStoreNumber
	 */
	public void deleteStoreNumber (String storenumberModuleId, String loginUserID) {
		List<StoreNumber> storenumber = getStoreNumber(storenumberModuleId);
		if (storenumber != null) {
			for(StoreNumber newStoreNumber: storenumber){
				newStoreNumber.setDeletionIndicator(1L);
				newStoreNumber.setUpdatedBy(loginUserID);
				newStoreNumber.setUpdatedOn(new Date());
				storeNumberRepository.save(newStoreNumber);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storenumberModuleId);
		}
	}


	//find
	public List<GAgreement> findStoreNumber(FindStoreNumber findStoreNumber) throws ParseException {

		StoreNumberSpecification spec = new StoreNumberSpecification(findStoreNumber);
		List<StoreNumber> result = storeNumberRepository.findAll(spec);
		result = result.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

		List<Agreement> results = null;
		for (StoreNumber dbStoreNumber : result) {
			results = new ArrayList<>();
			results.add(agreementRepository.getAgreement(dbStoreNumber.getAgreementNumber()));
		}


		List<GAgreement> gAgreement = new ArrayList<>();
		for (Agreement dbAgreement : results) {
			GAgreement newGAgreement = new GAgreement();
			BeanUtils.copyProperties(dbAgreement, newGAgreement, CommonUtils.getNullPropertyNames(dbAgreement));
			newGAgreement.setStoreNumbers(new ArrayList<>());
			for (StoreNumber newstoreNumber : dbAgreement.getStoreNumbers()) {
				newGAgreement.getStoreNumbers().add(newstoreNumber.getStoreNumber());
			}
			gAgreement.add(newGAgreement);
		}
		return gAgreement;
	}

}
