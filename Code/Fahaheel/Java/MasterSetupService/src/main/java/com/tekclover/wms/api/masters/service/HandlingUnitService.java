package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
	 * getHandlingUnit
	 * @param handlingUnit
	 * @return
	 */
	public HandlingUnit getHandlingUnit (String warehouseId,String handlingUnit,String companyCodeId, String languageId, String plantId) {
		Optional<HandlingUnit> dbHandlingUnitId =
				handlingunitRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						handlingUnit,
						languageId,
						0L
				);
		if (dbHandlingUnitId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"handlingUnitId - "+handlingUnit+
					"doesn't exist.");

		}
		return dbHandlingUnitId.get();
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		HandlingUnit dbHandlingUnit = new HandlingUnit();
		Optional<HandlingUnit> duplicateHandlingUnitId = handlingunitRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitAndLanguageIdAndDeletionIndicator(newHandlingUnit.getCompanyCodeId(), newHandlingUnit.getPlantId(), newHandlingUnit.getWarehouseId(), newHandlingUnit.getHandlingUnit(), newHandlingUnit.getLanguageId(), 0L);
		if (!duplicateHandlingUnitId.isEmpty()) {
			throw new BadRequestException("Record is Getting Duplicate");
		} else {
			BeanUtils.copyProperties(newHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(newHandlingUnit));
			dbHandlingUnit.setDeletionIndicator(0L);
			dbHandlingUnit.setCreatedBy(loginUserID);
			dbHandlingUnit.setUpdatedBy(loginUserID);
			dbHandlingUnit.setCreatedOn(new Date());
			dbHandlingUnit.setUpdatedOn(new Date());
			return handlingunitRepository.save(dbHandlingUnit);
		}
	}

	/**
	 * updateHandlingUnit
	 * @param handlingUnit
	 * @param updateHandlingUnit
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnit updateHandlingUnit (String handlingUnit,String companyCodeId,String plantId,String warehouseId,String languageId,UpdateHandlingUnit updateHandlingUnit, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		HandlingUnit dbHandlingUnit = getHandlingUnit(warehouseId,handlingUnit,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateHandlingUnit, dbHandlingUnit, CommonUtils.getNullPropertyNames(updateHandlingUnit));
		dbHandlingUnit.setUpdatedBy(loginUserID);
		dbHandlingUnit.setUpdatedOn(new Date());
		return handlingunitRepository.save(dbHandlingUnit);
	}

	/**
	 * deleteHandlingUnit
	 * @param handlingUnit
	 */
	public void deleteHandlingUnit (String handlingUnit,String companyCodeId,String plantId,String languageId,String warehouseId,String loginUserID) throws ParseException {
		HandlingUnit handlingunit = getHandlingUnit(warehouseId,handlingUnit,companyCodeId,languageId,plantId);
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
