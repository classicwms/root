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

import com.ustorage.api.master.model.unitofmeasure.AddUnitOfMeasure;
import com.ustorage.api.master.model.unitofmeasure.UnitOfMeasure;
import com.ustorage.api.master.model.unitofmeasure.UpdateUnitOfMeasure;
import com.ustorage.api.master.repository.UnitOfMeasureRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UnitOfMeasureService {
	
	@Autowired
	private UnitOfMeasureRepository unitOfMeasureRepository;
	
	public List<UnitOfMeasure> getUnitOfMeasure () {
		List<UnitOfMeasure> unitOfMeasureList =  unitOfMeasureRepository.findAll();
		unitOfMeasureList = unitOfMeasureList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return unitOfMeasureList;
	}
	
	/**
	 * getUnitOfMeasure
	 * @param unitOfMeasureId
	 * @return
	 */
	public UnitOfMeasure getUnitOfMeasure (String unitOfMeasureId) {
		Optional<UnitOfMeasure> unitOfMeasure = unitOfMeasureRepository.findByCodeAndDeletionIndicator(unitOfMeasureId, 0L);
		if (unitOfMeasure.isEmpty()) {
			return null;
		}
		return unitOfMeasure.get();
	}
	
	/**
	 * createUnitOfMeasure
	 * @param newUnitOfMeasure
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UnitOfMeasure createUnitOfMeasure (AddUnitOfMeasure newUnitOfMeasure, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		UnitOfMeasure dbUnitOfMeasure = new UnitOfMeasure();
		BeanUtils.copyProperties(newUnitOfMeasure, dbUnitOfMeasure, CommonUtils.getNullPropertyNames(newUnitOfMeasure));
		dbUnitOfMeasure.setDeletionIndicator(0L);
		dbUnitOfMeasure.setCreatedBy(loginUserId);
		dbUnitOfMeasure.setUpdatedBy(loginUserId);
		dbUnitOfMeasure.setCreatedOn(new Date());
		dbUnitOfMeasure.setUpdatedOn(new Date());
		return unitOfMeasureRepository.save(dbUnitOfMeasure);
	}
	
	/**
	 * updateUnitOfMeasure
	 * @param unitOfMeasureId
	 * @param loginUserId 
	 * @param updateUnitOfMeasure
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UnitOfMeasure updateUnitOfMeasure (String code, String loginUserId, UpdateUnitOfMeasure updateUnitOfMeasure)
			throws IllegalAccessException, InvocationTargetException {
		UnitOfMeasure dbUnitOfMeasure = getUnitOfMeasure(code);
		BeanUtils.copyProperties(updateUnitOfMeasure, dbUnitOfMeasure, CommonUtils.getNullPropertyNames(updateUnitOfMeasure));
		dbUnitOfMeasure.setUpdatedBy(loginUserId);
		dbUnitOfMeasure.setUpdatedOn(new Date());
		return unitOfMeasureRepository.save(dbUnitOfMeasure);
	}
	
	/**
	 * deleteUnitOfMeasure
	 * @param loginUserID 
	 * @param unitofmeasureCode
	 */
	public void deleteUnitOfMeasure (String unitofmeasureModuleId, String loginUserID) {
		UnitOfMeasure unitofmeasure = getUnitOfMeasure(unitofmeasureModuleId);
		if (unitofmeasure != null) {
			unitofmeasure.setDeletionIndicator(1L);
			unitofmeasure.setUpdatedBy(loginUserID);
			unitofmeasure.setUpdatedOn(new Date());
			unitOfMeasureRepository.save(unitofmeasure);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + unitofmeasureModuleId);
		}
	}
}
