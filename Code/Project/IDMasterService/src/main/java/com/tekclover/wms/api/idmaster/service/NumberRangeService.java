package com.tekclover.wms.api.idmaster.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.numberrange.NumberRange;
import com.tekclover.wms.api.idmaster.repository.NumberRangeRepository;

import lombok.extern.slf4j.Slf4j;

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
	 * @param warehouseId
	 * @return
	 */
	public Long getNumberRange (Long numberRangeCode, Long fiscalYear, String warehouseId) {
		Optional<NumberRange> optNumberRange = numberRangeRepository.findByNumberRangeCodeAndFiscalYearAndWarehouseId(
				numberRangeCode, fiscalYear, warehouseId);
		if (optNumberRange.isEmpty()) {
			throw new BadRequestException("The given numberRangeCode : " + numberRangeCode 
												+ ", warehouseId: " + warehouseId + " doesn't exist.");
		}
		NumberRange numberRange = optNumberRange.get();
		String strCurrentRange = numberRange.getNumberRangeCurrent();
		Long currentRange = Long.valueOf(strCurrentRange);
		currentRange ++;
		return currentRange;
	}
	
	/**
	 * 
	 * @param numberRangeCode
	 * @param classId
	 * @return
	 */
	public String getNextNumberRange (Long numberRangeCode, Long fiscalYear, String warehouseId) {
		Optional<NumberRange> optNumberRange = numberRangeRepository.findByNumberRangeCodeAndFiscalYearAndWarehouseId(
				numberRangeCode, fiscalYear, warehouseId);
		log.info("getNextNumberRange---1----> " + numberRangeCode + "," + fiscalYear + "," + warehouseId);
		log.info("getNextNumberRange---2----> " + optNumberRange);
		
		if (optNumberRange.isEmpty()) {
			optNumberRange = numberRangeRepository.findByNumberRangeCodeAndWarehouseId (numberRangeCode, warehouseId);
			if (optNumberRange.isEmpty()) {
				throw new BadRequestException("The given numberRangeCode : " + numberRangeCode 
						+ ", warehouseId: " + warehouseId + " doesn't exist.");
			}
		} 
		
		NumberRange numberRange = optNumberRange.get();
		String strCurrentValue = numberRange.getNumberRangeCurrent();
		log.info("New currentValue generated : " + strCurrentValue);
		Long currentValue = 0L;
		if (strCurrentValue != null && strCurrentValue.trim().startsWith("A")) { 			// Increment logic for AuditLog Insert
			strCurrentValue = strCurrentValue.substring(2); // AL1000002
			currentValue = Long.valueOf(strCurrentValue);
			currentValue ++;
			strCurrentValue = "A" + String.valueOf(currentValue);
			numberRange.setNumberRangeCurrent(strCurrentValue);
			log.info("currentValue of A: " + currentValue);
		} else {
			strCurrentValue = strCurrentValue.trim();
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
