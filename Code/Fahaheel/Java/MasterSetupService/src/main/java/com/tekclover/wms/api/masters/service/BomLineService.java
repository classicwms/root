package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.model.bom.SearchBomLine;
import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.masters.repository.ExceptionLogRepository;
import com.tekclover.wms.api.masters.repository.specification.BomLineSpecification;
import com.tekclover.wms.api.masters.repository.specification.DriverSpecification;
import com.tekclover.wms.api.masters.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.bom.AddBomLine;
import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.UpdateBomLine;
import com.tekclover.wms.api.masters.repository.BomLineRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BomLineService {

	@Autowired
	private BomLineRepository bomLineRepository;

	@Autowired
	private ExceptionLogRepository exceptionLogRepo;

	/**
	 *
	 * @param warehouseId
	 * @param bomNumber
	 * @return
	 */
	public List<BomLine> getBomLine (String warehouseId, Long bomNumber,String companyCode,String languageId,String plantId) {
		List<BomLine> bomLine = bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumber(
				languageId,
				companyCode,
				plantId,
				warehouseId,
				bomNumber);
		return bomLine;
	}

	/**
	 * getBomLine
	 * @param bomNumber
	 * @return
	 */
	public BomLine getBomLine (String warehouseId, Long bomNumber, String childItemCode,String companyCode,String plantId,String languageId) {
		Optional<BomLine> bomLine =
				bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(
						languageId,
						companyCode,
						plantId,
						warehouseId,
						bomNumber,
						childItemCode,
						0L);
		if (bomLine.isEmpty()) {
			// Exception Log
			createBomLineLog(bomNumber, languageId, companyCode, plantId, warehouseId,
					"Bom Line with given values and bomNumber-" + bomNumber + " doesn't exists.");
			throw new BadRequestException("The given values: warehouseId:" + warehouseId +
					",bomNumber: " + bomNumber +
					",childItemCode: " + childItemCode +
					" doesn't exist.");
		}
		return bomLine.get();
	}

	/**
	 * createBomLine
	 * @param newBomLine
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BomLine createBomLine (BomLine newBomLine, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BomLine dbBomLine = new BomLine();
		Optional<BomLine> duplicateBomLine = bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(newBomLine.getLanguageId(), newBomLine.getCompanyCode(), newBomLine.getPlantId(), newBomLine.getWarehouseId(), newBomLine.getBomNumber(), newBomLine.getChildItemCode(), 0L);
		if (!duplicateBomLine.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting duplicated");
		} else {
			log.info("newBomLine : " + newBomLine);
			BeanUtils.copyProperties(newBomLine, dbBomLine, CommonUtils.getNullPropertyNames(newBomLine));
			dbBomLine.setDeletionIndicator(0L);
			dbBomLine.setCreatedBy(loginUserID);
			dbBomLine.setUpdatedBy(loginUserID);
			dbBomLine.setCreatedOn(new Date());
			dbBomLine.setUpdatedOn(new Date());
			return bomLineRepository.save(dbBomLine);
		}
	}

	/**
	 * updateBomLine
	 * @param loginUserID
	 * @param bomNumber
	 * @param updateBomLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BomLine updateBomLine (String warehouseId, Long bomNumber,String companyCode,String languageId,String plantId, String childItemCode,
								  String loginUserID, BomLine updateBomLine)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BomLine dbBomLine = getBomLine(warehouseId, bomNumber, childItemCode,companyCode,plantId,languageId);
		BeanUtils.copyProperties(updateBomLine, dbBomLine, CommonUtils.getNullPropertyNames(updateBomLine));
		dbBomLine.setUpdatedBy(loginUserID);
		dbBomLine.setUpdatedOn(new Date());
		return bomLineRepository.save(dbBomLine);
	}

	/**
	 * deleteBomLine
	 * @param loginUserID
	 * @param bomNumber
	 */
	public void deleteBomLine (String warehouseId, Long bomNumber, String childItemCode,String companyCode,String languageId,String plantId,String loginUserID) {
		BomLine bomLine = getBomLine(warehouseId,bomNumber, childItemCode,companyCode,plantId,languageId);
		if ( bomLine != null) {
			bomLine.setDeletionIndicator(1L);
			bomLine.setUpdatedBy(loginUserID);
			bomLineRepository.save(bomLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + bomNumber);
		}
	}



	/**
	 *
	 * @param searchBomLine
	 * @return
	 * @throws Exception
	 */
	public List<BomLine> findBomLine(SearchBomLine searchBomLine)
			throws Exception {
		BomLineSpecification spec = new BomLineSpecification(searchBomLine);
		List<BomLine> results = bomLineRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

	//============================================BomLine_ExceptionLog=================================================
	private void createBomLineLog(Long lineNo, String languageId, String companyCodeId,
								  String plantId, String warehouseId, String error) {

		ExceptionLog dbExceptionLog = new ExceptionLog();
		dbExceptionLog.setOrderTypeId(String.valueOf(lineNo));
		dbExceptionLog.setOrderDate(new Date());
		dbExceptionLog.setLanguageId(languageId);
		dbExceptionLog.setCompanyCodeId(companyCodeId);
		dbExceptionLog.setPlantId(plantId);
		dbExceptionLog.setWarehouseId(warehouseId);
		dbExceptionLog.setReferenceField1(String.valueOf(lineNo));
		dbExceptionLog.setErrorMessage(error);
		dbExceptionLog.setCreatedBy("MSD_API");
		dbExceptionLog.setCreatedOn(new Date());
		exceptionLogRepo.save(dbExceptionLog);
	}

}
