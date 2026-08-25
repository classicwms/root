package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.idmaster.model.numberrange.AddNumberRange;
import com.tekclover.wms.api.idmaster.model.numberrange.FindNumberRange;
import com.tekclover.wms.api.idmaster.model.numberrange.NumberRange;
import com.tekclover.wms.api.idmaster.model.numberrange.UpdateNumberRange;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.ExceptionLogRepository;
import com.tekclover.wms.api.idmaster.repository.NumberRangeRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.NumberRangeSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NumberRangeService {

	@Autowired
	NumberRangeRepository numberRangeRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ExceptionLogRepository exceptionLogRepo;

	public List<NumberRange> getNumberRanges() {
		List<NumberRange> numberRangeList = numberRangeRepository.findAll();
		numberRangeList = numberRangeList.stream().filter(n -> n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		List<NumberRange> newNumberRange = new ArrayList<>();
		for (NumberRange dbNumberRange : numberRangeList) {
			if (dbNumberRange.getCompanyIdAndDescription() != null && dbNumberRange.getPlantIdAndDescription() != null
					&& dbNumberRange.getWarehouseIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = companyIdRepository
						.getCompanyIdAndDescription(dbNumberRange.getCompanyCodeId(), dbNumberRange.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbNumberRange.getPlantId(),
						dbNumberRange.getLanguageId(), dbNumberRange.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(
						dbNumberRange.getWarehouseId(), dbNumberRange.getLanguageId(), dbNumberRange.getCompanyCodeId(),
						dbNumberRange.getPlantId());
				if (iKeyValuePair != null) {
					dbNumberRange.setCompanyIdAndDescription(
							iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbNumberRange.setPlantIdAndDescription(
							iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbNumberRange.setWarehouseIdAndDescription(
							iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newNumberRange.add(dbNumberRange);
		}
		return newNumberRange;
	}

	/**
	 * getBarcodeTypeId
	 * 
	 * @param numberRangeCode
	 * @param fiscalYear
	 * @return
	 */
	public NumberRange getNumberRange(String warehouseId, String companyCodeId, String languageId, String plantId,
			Long numberRangeCode, Long fiscalYear) {
		Optional<NumberRange> dbNumberRange = numberRangeRepository
				.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndFiscalYearAndLanguageIdAndDeletionIndicator(
						companyCodeId, plantId, warehouseId, numberRangeCode, fiscalYear, languageId, 0L);
		if (dbNumberRange.isEmpty()) {
			// Exception Log
			createNumberRangeLog1(numberRangeCode, fiscalYear, languageId, companyCodeId, plantId, warehouseId,
					"NumberRange with given values and numberRangeCode-" + numberRangeCode + " doesn't exists.");
			throw new BadRequestException("The given values : " + "warehouseId - " + warehouseId + "numberRangeCode - "
					+ numberRangeCode + "fiscalYear - " + fiscalYear + " doesn't exist.");

		}
		NumberRange newNumberRange = new NumberRange();
		BeanUtils.copyProperties(dbNumberRange.get(), newNumberRange, CommonUtils.getNullPropertyNames(dbNumberRange));
		IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(companyCodeId, languageId);
		IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(plantId, languageId, companyCodeId);
		IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId,
				companyCodeId, plantId);
		if (iKeyValuePair != null) {
			newNumberRange.setCompanyIdAndDescription(
					iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if (iKeyValuePair1 != null) {
			newNumberRange
					.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if (iKeyValuePair2 != null) {
			newNumberRange.setWarehouseIdAndDescription(
					iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newNumberRange;
	}

	/**
	 *
	 * @return
	 */
//	public List<NumberRange> getNumberRanges () {
//		List<NumberRange> numberRangeList = numberRangeRepository.findAll();
//		return numberRangeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
//	}
	public NumberRange createNumberRange(AddNumberRange newNumberRange, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		NumberRange dbNumberRange = new NumberRange();
		Optional<NumberRange> duplicateNumberRange = numberRangeRepository
				.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndFiscalYearAndLanguageIdAndDeletionIndicator(
						newNumberRange.getCompanyCodeId(), newNumberRange.getPlantId(), newNumberRange.getWarehouseId(),
						newNumberRange.getNumberRangeCode(), newNumberRange.getFiscalYear(),
						newNumberRange.getLanguageId(), 0L);
		if (!duplicateNumberRange.isEmpty()) {
			// Exception Log
			createNumberRangeLog2(newNumberRange, "Record is getting Duplicated.");
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse = warehouseService.getWarehouse(newNumberRange.getWarehouseId(),
					newNumberRange.getCompanyCodeId(), newNumberRange.getPlantId(), newNumberRange.getLanguageId());
			log.info("newNumberRange : " + newNumberRange);
			BeanUtils.copyProperties(newNumberRange, dbNumberRange, CommonUtils.getNullPropertyNames(newNumberRange));
			dbNumberRange.setDeletionIndicator(0L);
			dbNumberRange.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbNumberRange.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbNumberRange
					.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId() + "-" + dbWarehouse.getWarehouseDesc());
			dbNumberRange.setCreatedBy(loginUserID);
			dbNumberRange.setUpdatedBy(loginUserID);
			dbNumberRange.setCreatedOn(new Date());
			dbNumberRange.setUpdatedOn(new Date());
			return numberRangeRepository.save(dbNumberRange);
		}

	}

	/**
	 * updateBarcodeTypeId
	 * 
	 * @param
	 * @param numberRangeCode
	 * @param updateNumberRange
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public NumberRange updateNumberRange(String warehouseId, String companyCodeId, String languageId, String plantId,
			Long numberRangeCode, Long fiscalYear, String loginUserID, UpdateNumberRange updateNumberRange)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		NumberRange dbNumberRange = getNumberRange(warehouseId, companyCodeId, languageId, plantId, numberRangeCode,
				fiscalYear);
		BeanUtils.copyProperties(updateNumberRange, dbNumberRange, CommonUtils.getNullPropertyNames(updateNumberRange));
		dbNumberRange.setUpdatedBy(loginUserID);
		dbNumberRange.setUpdatedOn(new Date());
		return numberRangeRepository.save(dbNumberRange);
	}

	/**
	 * deleteNumberRange
	 * 
	 * @param loginUserID
	 * @param numberRangeCode
	 */
	public void deleteNumberRange(String warehouseId, String companyCodeId, String languageId, String plantId,
			Long numberRangeCode, Long fiscalYear, String loginUserID) {
		NumberRange dbNumberRange = getNumberRange(warehouseId, companyCodeId, languageId, plantId, numberRangeCode,
				fiscalYear);
		if (dbNumberRange != null) {
			dbNumberRange.setDeletionIndicator(1L);
			dbNumberRange.setUpdatedBy(loginUserID);
			numberRangeRepository.save(dbNumberRange);
		} else {
			// Exception Log
			createNumberRangeLog1(numberRangeCode, fiscalYear, languageId, companyCodeId, plantId, warehouseId,
					"Error in deleting Id - " + numberRangeCode);
			throw new EntityNotFoundException("Error in deleting Id: " + numberRangeCode);
		}
	}
	// Find NumberRange

	public List<NumberRange> findNumberRange(FindNumberRange findNumberRange) throws ParseException {

		NumberRangeSpecification spec = new NumberRangeSpecification(findNumberRange);
		List<NumberRange> results = numberRangeRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<NumberRange> newNumberRange = new ArrayList<>();
		for (NumberRange dbNumberRange : results) {
			if (dbNumberRange.getCompanyIdAndDescription() != null && dbNumberRange.getPlantIdAndDescription() != null
					&& dbNumberRange.getWarehouseIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = companyIdRepository
						.getCompanyIdAndDescription(dbNumberRange.getCompanyCodeId(), dbNumberRange.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbNumberRange.getPlantId(),
						dbNumberRange.getLanguageId(), dbNumberRange.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(
						dbNumberRange.getWarehouseId(), dbNumberRange.getLanguageId(), dbNumberRange.getCompanyCodeId(),
						dbNumberRange.getPlantId());
				if (iKeyValuePair != null) {
					dbNumberRange.setCompanyIdAndDescription(
							iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbNumberRange.setPlantIdAndDescription(
							iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbNumberRange.setWarehouseIdAndDescription(
							iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newNumberRange.add(dbNumberRange);
		}
		return newNumberRange;
	}

	/**
	 *
	 * @param numberRangeCode
	 * @param warehouseId
	 * @return
	 */
	public Long getNumberRange(Long numberRangeCode, Long fiscalYear, String warehouseId) {
		Optional<NumberRange> optNumberRange = numberRangeRepository
				.findByNumberRangeCodeAndFiscalYearAndWarehouseId(numberRangeCode, fiscalYear, warehouseId);
		if (optNumberRange.isEmpty()) {
			// Exception Log
			createNumberRangeLog3(numberRangeCode, fiscalYear, warehouseId,
					"The given NumberRangeCode-" + numberRangeCode + " and warehouseId-" + warehouseId + " doesn't exists.");
			throw new BadRequestException("The given numberRangeCode : " + numberRangeCode + ", warehouseId: "
					+ warehouseId + " doesn't exist.");
		}
		NumberRange numberRange = optNumberRange.get();
		String strCurrentRange = numberRange.getNumberRangeCurrent();
		Long currentRange = Long.valueOf(strCurrentRange);
		currentRange++;
		return currentRange;
	}

	/**
	 *
	 * @param numberRangeCode
	 * @param fiscalYear
	 * @param warehouseId
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @return
	 */
	public String getNextNumberRange(Long numberRangeCode, Long fiscalYear,
									 String warehouseId, String companyCodeId,
									 String plantId, String languageId) {
		Optional<NumberRange> optNumberRange = numberRangeRepository
				.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndFiscalYearAndLanguageIdAndDeletionIndicator(
						companyCodeId, plantId, warehouseId, numberRangeCode, fiscalYear, languageId, 0L);
		log.info("getNextNumberRange---1----> " + numberRangeCode + "," + fiscalYear + "," + warehouseId);
		log.info("getNextNumberRange---2----> " + optNumberRange);

		if (optNumberRange.isEmpty()) {
			optNumberRange = numberRangeRepository
					.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndLanguageIdAndDeletionIndicator(
							companyCodeId, plantId, warehouseId, numberRangeCode, languageId, 0L);
			if (optNumberRange.isEmpty()) {
				// Exception Log
				createNumberRangeLog1(numberRangeCode, fiscalYear, languageId, companyCodeId, plantId, warehouseId,
						"The given NumberRangeCode-" + numberRangeCode + " and warehouseId-" + warehouseId + " doesn't exists.");
				throw new BadRequestException("The given numberRangeCode : " + numberRangeCode + ", warehouseId: "
						+ warehouseId + " doesn't exist.");
			}
		}

		NumberRange numberRange = optNumberRange.get();
		String strCurrentValue = numberRange.getNumberRangeCurrent();
		log.info("New currentValue generated : " + strCurrentValue);
		Long currentValue = 0L;
		if (strCurrentValue != null && strCurrentValue.trim().startsWith("A")) { // Increment logic for AuditLog Insert
			strCurrentValue = strCurrentValue.substring(2); // AL1000002
			currentValue = Long.valueOf(strCurrentValue);
			currentValue ++;
			strCurrentValue = "A" + String.valueOf(currentValue);
			numberRange.setNumberRangeCurrent(strCurrentValue);
			log.info("currentValue of A: " + currentValue);
		} else {
			strCurrentValue = strCurrentValue.trim();
			currentValue = Long.valueOf(strCurrentValue);
			currentValue++;
			log.info("currentValue : " + currentValue);
			numberRange.setNumberRangeCurrent(String.valueOf(currentValue));
			strCurrentValue = String.valueOf(currentValue);
		}

		log.info("New value numberRange: " + numberRange);
		numberRangeRepository.save(numberRange);

		log.info("New value has been updated in NumberRangeCurrent column");
		return strCurrentValue;
	}

	public String getNextNumberRange(Long numberRangeCode, String warehouseId, String companyCodeId,
			String plantId, String languageId) {
		log.info(" | " + companyCodeId + " | " + plantId + " | " + warehouseId + " | ");
		Optional<NumberRange> optNumberRange = numberRangeRepository
				.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndLanguageIdAndDeletionIndicator(
						companyCodeId, plantId, warehouseId, numberRangeCode, languageId, 0L);
		log.info("getNextNumberRange---1----> " + numberRangeCode + "," + warehouseId);
		log.info("getNextNumberRange---2----> " + optNumberRange);

		if (optNumberRange.isEmpty()) {
			log.info(" | " + companyCodeId + " | " + plantId + " | " + warehouseId + " | ");
			optNumberRange = numberRangeRepository
					.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndLanguageIdAndDeletionIndicator(
							companyCodeId, plantId, warehouseId, numberRangeCode, languageId, 0L);
			if (optNumberRange.isEmpty()) {
				// Exception Log
				createNumberRangeLog(numberRangeCode, languageId, companyCodeId, plantId, warehouseId,
						"The given NumberRangeCode-" + numberRangeCode + " and warehouseId-" + warehouseId + " doesn't exists.");
				throw new BadRequestException("The given numberRangeCode : " + numberRangeCode + ", warehouseId: "
						+ warehouseId + " doesn't exist.");
			}
		}

		NumberRange numberRange = optNumberRange.get();
		String strCurrentValue = numberRange.getNumberRangeCurrent();
		log.info("New currentValue generated : " + strCurrentValue);
		Long currentValue = 0L;
		if (strCurrentValue != null && strCurrentValue.trim().startsWith("A")) { // Increment logic for AuditLog Insert
			strCurrentValue = strCurrentValue.substring(2); // AL1000002
			currentValue = Long.valueOf(strCurrentValue);
			currentValue++;
			strCurrentValue = "A" + String.valueOf(currentValue);
			numberRange.setNumberRangeCurrent(strCurrentValue);
			log.info("currentValue of A: " + currentValue);
		} else {
			strCurrentValue = strCurrentValue.trim();
			currentValue = Long.valueOf(strCurrentValue);
			currentValue++;
			log.info("currentValue : " + currentValue);
			numberRange.setNumberRangeCurrent(String.valueOf(currentValue));
			strCurrentValue = String.valueOf(currentValue);
		}

		log.info("New value numberRange: " + numberRange);
		numberRangeRepository.save(numberRange);

		log.info("New value has been updated in NumberRangeCurrent column");
		return strCurrentValue;
	}

	public String getNextNumberRangeV2(Long numberRangeCode, String warehouseId, String companyCodeId,
									 String plantId, String languageId) {
		Optional<NumberRange> optNumberRange = numberRangeRepository
				.getNextNumberRange(
						companyCodeId, plantId, warehouseId, numberRangeCode, languageId);
		log.info("getNextNumberRange---1----> " + numberRangeCode + "," + warehouseId);
		log.info("getNextNumberRange---2----> " + optNumberRange);

		if (optNumberRange.isEmpty()) {
			optNumberRange = numberRangeRepository
					.getNextNumberRange(
							companyCodeId, plantId, warehouseId, numberRangeCode, languageId);
			if (optNumberRange.isEmpty()) {
				// Exception Log
				createNumberRangeLog(numberRangeCode, languageId, companyCodeId, plantId, warehouseId,
						"The given NumberRangeCode-" + numberRangeCode + " and warehouseId-" + warehouseId + " doesn't exists.");
				throw new BadRequestException("The given numberRangeCode : " + numberRangeCode + ", warehouseId: "
						+ warehouseId + " doesn't exist.");
			}
		}

		NumberRange numberRange = optNumberRange.get();
		String strCurrentValue = numberRange.getNumberRangeCurrent();
		log.info("New currentValue generated : " + strCurrentValue);
		Long currentValue = 0L;
		if (strCurrentValue != null && strCurrentValue.trim().startsWith("A")) { // Increment logic for AuditLog Insert
			strCurrentValue = strCurrentValue.substring(2); // AL1000002
			currentValue = Long.valueOf(strCurrentValue);
			currentValue++;
			strCurrentValue = "A" + String.valueOf(currentValue);
			numberRange.setNumberRangeCurrent(strCurrentValue);
			log.info("currentValue of A: " + currentValue);
		} else {
			strCurrentValue = strCurrentValue.trim();
			currentValue = Long.valueOf(strCurrentValue);
			currentValue++;
			log.info("currentValue : " + currentValue);
			numberRange.setNumberRangeCurrent(String.valueOf(currentValue));
			strCurrentValue = String.valueOf(currentValue);
		}

		log.info("New value numberRange: " + numberRange);
		numberRangeRepository.save(numberRange);

		log.info("New value has been updated in NumberRangeCurrent column");
		return strCurrentValue;
	}

	//=========================================NumberRange_ExceptionLog================================================
	private void createNumberRangeLog(Long numberRangeCode, String languageId, String companyCodeId,
									  String plantId, String warehouseId, String error) {

		ExceptionLog exceptionLog = new ExceptionLog();
		exceptionLog.setOrderTypeId(String.valueOf(numberRangeCode));
		exceptionLog.setOrderDate(new Date());
		exceptionLog.setLanguageId(languageId);
		exceptionLog.setCompanyCodeId(companyCodeId);
		exceptionLog.setPlantId(plantId);
		exceptionLog.setWarehouseId(warehouseId);
		exceptionLog.setReferenceField1(String.valueOf(numberRangeCode));
		exceptionLog.setErrorMessage(error);
		exceptionLog.setCreatedBy("MSD_API");
		exceptionLog.setCreatedOn(new Date());
		exceptionLogRepo.save(exceptionLog);
	}

	private void createNumberRangeLog1(Long numberRangeCode, Long fiscalYear, String languageId, String companyCodeId,
									   String plantId, String warehouseId, String error) {

		ExceptionLog exceptionLog = new ExceptionLog();
		exceptionLog.setOrderTypeId(String.valueOf(numberRangeCode));
		exceptionLog.setOrderDate(new Date());
		exceptionLog.setLanguageId(languageId);
		exceptionLog.setCompanyCodeId(companyCodeId);
		exceptionLog.setPlantId(plantId);
		exceptionLog.setWarehouseId(warehouseId);
		exceptionLog.setReferenceField1(String.valueOf(numberRangeCode));
		exceptionLog.setReferenceField2(String.valueOf(fiscalYear));
		exceptionLog.setErrorMessage(error);
		exceptionLog.setCreatedBy("MSD_API");
		exceptionLog.setCreatedOn(new Date());
		exceptionLogRepo.save(exceptionLog);
	}

	private void createNumberRangeLog2(AddNumberRange addNumberRange, String error) {

		ExceptionLog exceptionLog = new ExceptionLog();
		exceptionLog.setOrderTypeId(String.valueOf(addNumberRange.getNumberRangeCode()));
		exceptionLog.setOrderDate(new Date());
		exceptionLog.setLanguageId(addNumberRange.getLanguageId());
		exceptionLog.setCompanyCodeId(addNumberRange.getCompanyCodeId());
		exceptionLog.setPlantId(addNumberRange.getPlantId());
		exceptionLog.setWarehouseId(addNumberRange.getWarehouseId());
		exceptionLog.setReferenceField1(String.valueOf(addNumberRange.getFiscalYear()));
		exceptionLog.setErrorMessage(error);
		exceptionLog.setCreatedBy("MSD_API");
		exceptionLog.setCreatedOn(new Date());
		exceptionLogRepo.save(exceptionLog);
	}

	private void createNumberRangeLog3(Long numberRangeCode, Long fiscalYear, String warehouseId, String error) {

		ExceptionLog exceptionLog = new ExceptionLog();
		exceptionLog.setOrderTypeId(String.valueOf(numberRangeCode));
		exceptionLog.setOrderDate(new Date());
		exceptionLog.setWarehouseId(warehouseId);
		exceptionLog.setReferenceField1(String.valueOf(numberRangeCode));
		exceptionLog.setReferenceField2(String.valueOf(fiscalYear));
		exceptionLog.setErrorMessage(error);
		exceptionLog.setCreatedBy("MSD_API");
		exceptionLog.setCreatedOn(new Date());
		exceptionLogRepo.save(exceptionLog);
	}

}
