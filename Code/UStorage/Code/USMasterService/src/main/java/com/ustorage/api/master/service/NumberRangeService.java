package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ustorage.api.master.controller.exception.DataNotFoundException;
import com.ustorage.api.master.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.controller.exception.BadRequestException;
import com.ustorage.api.master.model.numberrange.*;
import com.ustorage.api.master.repository.NumberRangeRepository;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
public class NumberRangeService {

	@Autowired
	NumberRangeRepository numberRangeRepository;

	/**
	 *
	 * @return
	 */
	public List<NumberRange> getNumberRanges () {
		List<NumberRange> numberRangeList = numberRangeRepository.findAll();
		return numberRangeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
	}

	/**
	 *
	 * @param numberRangeCode
	 * @return
	 */
	public NumberRange getNumberRange (Long numberRangeCode) {
		NumberRange numberRange = numberRangeRepository.findByNumberRangeCode(numberRangeCode).orElse(null);
		if (numberRange.getDeletionIndicator() == 0) {
			return numberRange;
		} else {
			throw new BadRequestException("The given ActivityCode ID : " + numberRangeCode + " doesn't exist.");
		}
	}

	/**
	 *
	 * @param newNumberRange
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public NumberRange createNumberRange (AddNumberRange newNumberRange, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		Optional<NumberRange> numberRange =
				numberRangeRepository.findByNumberRangeCodeAndDescriptionAndDeletionIndicator (
						newNumberRange.getNumberRangeCode(),
						newNumberRange.getDescription(),
						0L);
		if (!numberRange.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		NumberRange dbNumberRange = new NumberRange();
		BeanUtils.copyProperties(newNumberRange, dbNumberRange);
		dbNumberRange.setDeletionIndicator(0L);
		dbNumberRange.setCreatedBy(loginUserID);
		dbNumberRange.setUpdatedBy(loginUserID);
		dbNumberRange.setCreatedOn(new Date());
		dbNumberRange.setUpdatedOn(new Date());
		return numberRangeRepository.save(dbNumberRange);
	}

	/**
	 *
	 * @param numberRangeCode
	 * @param loginUserID
	 * @param updateNumberRange
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public NumberRange updateNumberRange (Long numberRangeCode, String loginUserID, UpdateNumberRange updateNumberRange)
			throws IllegalAccessException, InvocationTargetException {
		NumberRange dbNumberRange = getNumberRange(numberRangeCode);
		BeanUtils.copyProperties(updateNumberRange, dbNumberRange, CommonUtils.getNullPropertyNames(updateNumberRange));
		dbNumberRange.setUpdatedBy(loginUserID);
		dbNumberRange.setUpdatedOn(new Date());
		return numberRangeRepository.save(dbNumberRange);
	}

	/**
	 *
	 * @param numberRangeModuleId
	 */
	public void deleteNumberRange (Long numberRangeModuleId) {
		NumberRange numberRange = getNumberRange(numberRangeModuleId);
		if (numberRange != null) {
			numberRangeRepository.delete(numberRange);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + numberRangeModuleId);
		}
	}

	/**
	 *
	 * @param numberRangeCode
	 * @param description
	 * @return
	 */
	public String getNextNumberRange (Long numberRangeCode, String description) {
		log.info("numberRangeCode : " + numberRangeCode + ":" + description);
		Optional<NumberRange> numberRangeOpt = numberRangeRepository.findByNumberRangeCodeAndDescription(numberRangeCode, description);
		if (numberRangeOpt.isEmpty()) {
			throw new DataNotFoundException("Record not found for given Range Code and Class: " + numberRangeCode + "," + description);
		}
		NumberRange currentNumberRange = numberRangeOpt.get();

		Optional<NumberRange> optNumberRange =
				numberRangeRepository.findByNumberRangeCodeAndDescriptionAndDeletionIndicator (
						numberRangeCode,
						currentNumberRange.getDescription(),
						0L);
		NumberRange numberRange = optNumberRange.get();
		log.info("Current record: " + numberRange);

		String strCurrentValue = numberRange.getNumberRangeCurrent();
		Long currentValue = 0L;
		if (strCurrentValue.startsWith("AL")) { // Increment logic for AuditLog Insert
			strCurrentValue = strCurrentValue.substring(2); // AL1000002
			currentValue = Long.valueOf(strCurrentValue);
			currentValue ++;
			strCurrentValue = "AL" + String.valueOf(currentValue);
			numberRange.setNumberRangeCurrent(strCurrentValue);
			log.info("currentValue of AL: " + currentValue);
		} else {
			currentValue = Long.valueOf(strCurrentValue);
			currentValue ++;
			log.info("currentValue : " + currentValue);
			numberRange.setNumberRangeCurrent(String.valueOf(currentValue));
			strCurrentValue = String.valueOf(currentValue);
		}
		log.info("New value numberRange: " + numberRange);
		numberRangeRepository.save(numberRange);
		log.info("New value has been updated in NumberRangeCurrent column");
		return strCurrentValue;
	}
}
