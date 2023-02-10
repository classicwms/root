package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.controller.exception.BadRequestException;
import com.ustorage.api.trans.repository.Specification.StorageUnitSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.storageunit.*;

import com.ustorage.api.trans.repository.StorageUnitRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageUnitService {
	
	@Autowired
	private StorageUnitRepository storageUnitRepository;
	
	public List<StorageUnit> getStorageUnit () {
		List<StorageUnit> storageUnitList =  storageUnitRepository.findAll();
		storageUnitList = storageUnitList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storageUnitList;
	}
	
	/**
	 * getStorageUnit
	 * @param itemCode
	 * @return
	 */
	public StorageUnit getStorageUnit (String itemCode) {
		Optional<StorageUnit> storageUnit = storageUnitRepository.findByItemCodeAndDeletionIndicator(itemCode, 0L);
		if (storageUnit.isEmpty()) {
			return null;
		}
		return storageUnit.get();
	}
	
	/**
	 * createStorageUnit
	 * @param newStorageUnit
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageUnit createStorageUnit (AddStorageUnit newStorageUnit, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		StorageUnit dbStorageUnit = new StorageUnit();
		Optional<StorageUnit> codeIdExist = storageUnitRepository.findByCodeIdAndDeletionIndicator(newStorageUnit.getCodeId(),0);
		if(codeIdExist!=null && !codeIdExist.isEmpty()){
			throw new BadRequestException("The given Code ID : " + newStorageUnit.getCodeId() + " already exists.");
		}
		else{
			BeanUtils.copyProperties(newStorageUnit, dbStorageUnit, CommonUtils.getNullPropertyNames(newStorageUnit));
			dbStorageUnit.setDeletionIndicator(0L);
			dbStorageUnit.setCreatedBy(loginUserId);
			dbStorageUnit.setUpdatedBy(loginUserId);
			dbStorageUnit.setCreatedOn(new Date());
			dbStorageUnit.setUpdatedOn(new Date());

		}

		return storageUnitRepository.save(dbStorageUnit);
	}
	
	/**
	 * updateStorageUnit
	 * @param itemCode
	 * @param loginUserId 
	 * @param updateStorageUnit
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageUnit updateStorageUnit (String itemCode, String loginUserId, UpdateStorageUnit updateStorageUnit)
			throws IllegalAccessException, InvocationTargetException {
		StorageUnit dbStorageUnit = getStorageUnit(itemCode);

			BeanUtils.copyProperties(updateStorageUnit, dbStorageUnit, CommonUtils.getNullPropertyNames(updateStorageUnit));
			dbStorageUnit.setUpdatedBy(loginUserId);
			dbStorageUnit.setUpdatedOn(new Date());
		return storageUnitRepository.save(dbStorageUnit);
	}
	
	/**
	 * deleteStorageUnit
	 * @param loginUserID 
	 * @param storageunitModuleId
	 */
	public void deleteStorageUnit (String storageunitModuleId, String loginUserID) {
		StorageUnit storageunit = getStorageUnit(storageunitModuleId);
		if (storageunit != null) {
			storageunit.setDeletionIndicator(1L);
			storageunit.setUpdatedBy(loginUserID);
			storageunit.setUpdatedOn(new Date());
			storageUnitRepository.save(storageunit);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageunitModuleId);
		}
	}

	//Find StorageUnit

	public List<StorageUnit> findStorageUnit(FindStorageUnit findStorageUnit) throws ParseException {

		StorageUnitSpecification spec = new StorageUnitSpecification(findStorageUnit);
		List<StorageUnit> results = storageUnitRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
