package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.handlingunit.AddHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.HandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.SearchHandlingUnit;
import com.tekclover.wms.api.masters.model.handlingunit.UpdateHandlingUnit;
import com.tekclover.wms.api.masters.repository.HandlingUnitRepository;
import com.tekclover.wms.api.masters.repository.specification.HandlingUnitSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlingUnitService {
	
	@Autowired
	private HandlingUnitRepository handlingunitRepository;
	
	/**
	 * getHandlingUnits
	 * @return
	 */
	public List<HandlingUnit> getHandlingUnits () {
		List<HandlingUnit> handlingunitList = handlingunitRepository.findAll();
		log.info("handlingunitList : " + handlingunitList);
		handlingunitList = handlingunitList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return handlingunitList;
	}
	
	/**
	 * getHandlingUnit
	 * @param handlingUnit
	 * @return
	 */
	public HandlingUnit getHandlingUnit (String handlingUnit) {
		HandlingUnit handlingunit = handlingunitRepository.findByHandlingUnit(handlingUnit).orElse(null);
		if (handlingunit != null && handlingunit.getDeletionIndicator() != null && handlingunit.getDeletionIndicator() == 0) {
			return handlingunit;
		} else {
			throw new BadRequestException("The given HandlingUnit ID : " + handlingUnit + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchHandlingUnit
	 * @return
	 * @throws Exception
	 */
	public List<HandlingUnit> findHandlingUnit(SearchHandlingUnit searchHandlingUnit)
			throws Exception {
		if (searchHandlingUnit.getStartCreatedOn() != null && searchHandlingUnit.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingUnit.getStartCreatedOn(), searchHandlingUnit.getEndCreatedOn());
			searchHandlingUnit.setStartCreatedOn(dates[0]);
			searchHandlingUnit.setEndCreatedOn(dates[1]);
		}
		
		if (searchHandlingUnit.getStartUpdatedOn() != null && searchHandlingUnit.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingUnit.getStartUpdatedOn(), searchHandlingUnit.getEndUpdatedOn());
			searchHandlingUnit.setStartUpdatedOn(dates[0]);
			searchHandlingUnit.setEndUpdatedOn(dates[1]);
		}
		
		HandlingUnitSpecification spec = new HandlingUnitSpecification(searchHandlingUnit);
		List<HandlingUnit> results = handlingunitRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createHandlingUnit
	 * @param newHandlingUnit
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnit createHandlingUnit (AddHandlingUnit newHandlingUnit, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnit dbHandlingUnit = new HandlingUnit();
		BeanUtils.copyProperties(newHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(newHandlingUnit));
		dbHandlingUnit.setDeletionIndicator(0L);
		dbHandlingUnit.setCreatedBy(loginUserID);
		dbHandlingUnit.setUpdatedBy(loginUserID);
		dbHandlingUnit.setCreatedOn(new Date());
		dbHandlingUnit.setUpdatedOn(new Date());
		return handlingunitRepository.save(dbHandlingUnit);
	}
	
	/**
	 * updateHandlingUnit
	 * @param handlingunit
	 * @param updateHandlingUnit
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnit updateHandlingUnit (String handlingUnit, UpdateHandlingUnit updateHandlingUnit, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnit dbHandlingUnit = getHandlingUnit(handlingUnit);
		BeanUtils.copyProperties(updateHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(updateHandlingUnit));
		dbHandlingUnit.setUpdatedBy(loginUserID);
		dbHandlingUnit.setUpdatedOn(new Date());
		return handlingunitRepository.save(dbHandlingUnit);
	}
	
	/**
	 * deleteHandlingUnit
	 * @param handlingunit
	 */
	public void deleteHandlingUnit (String handlingUnit, String loginUserID) {
		HandlingUnit handlingunit = getHandlingUnit(handlingUnit);
		if ( handlingunit != null) {
			handlingunit.setDeletionIndicator (1L);
			handlingunit.setUpdatedBy(loginUserID);
			handlingunit.setUpdatedOn(new Date());
			handlingunitRepository.save(handlingunit);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + handlingUnit);
		}
	}
}
