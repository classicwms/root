package com.tekclover.wms.api.enterprise.service;

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

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.barcode.AddBarcode;
import com.tekclover.wms.api.enterprise.model.barcode.Barcode;
import com.tekclover.wms.api.enterprise.model.barcode.SearchBarcode;
import com.tekclover.wms.api.enterprise.model.barcode.UpdateBarcode;
import com.tekclover.wms.api.enterprise.repository.BarcodeRepository;
import com.tekclover.wms.api.enterprise.repository.specification.BarcodeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeService extends BaseService {
	
	@Autowired
	private BarcodeRepository barcodeRepository;
	
	/**
	 * getBarcodes
	 * @return
	 */
	public List<Barcode> getBarcodes () {
		List<Barcode> barcodeList = barcodeRepository.findAll();
		log.info("barcodeList : " + barcodeList);
		barcodeList = barcodeList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return barcodeList;
	}
	
	/**
	 * getBarcode
	 * @param warehouseId
	 * @param method
	 * @param barcodeTypeId
	 * @param barcodeSubTypeId
	 * @param levelId
	 * @param levelReference
	 * @param processId
	 * @return
	 */
	public Barcode getBarcode (String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId, 
			Long levelId, String levelReference, Long processId) {
		Optional<Barcode> barcode = 
				barcodeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndMethodAndBarcodeTypeIdAndBarcodeSubTypeIdAndLevelIdAndLevelReferenceAndProcessIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(),  warehouseId, method, 
						barcodeTypeId, barcodeSubTypeId, levelId, levelReference, processId, 0L);
		if (barcode.isEmpty()) {
			throw new BadRequestException("The given Barcode Id : " + barcodeTypeId + " doesn't exist.");
		} 
		return barcode.get();
	}
	
	/**
	 * findBarcode
	 * @param searchBarcode
	 * @return
	 * @throws ParseException
	 */
	public List<Barcode> findBarcode(SearchBarcode searchBarcode) throws Exception {
		if (searchBarcode.getStartCreatedOn() != null && searchBarcode.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchBarcode.getStartCreatedOn(), searchBarcode.getEndCreatedOn());
			searchBarcode.setStartCreatedOn(dates[0]);
			searchBarcode.setEndCreatedOn(dates[1]);
		}
		
		BarcodeSpecification spec = new BarcodeSpecification(searchBarcode);
		List<Barcode> results = barcodeRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createBarcode
	 * @param newBarcode
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Barcode createBarcode (AddBarcode newBarcode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<Barcode> optBarcode = 
				barcodeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndMethodAndBarcodeTypeIdAndBarcodeSubTypeIdAndLevelIdAndLevelReferenceAndProcessIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newBarcode.getWarehouseId(),
						newBarcode.getMethod(),
						newBarcode.getBarcodeTypeId(),
						newBarcode.getBarcodeSubTypeId(),
						newBarcode.getLevelId(),
						newBarcode.getLevelReference(),
						newBarcode.getProcessId(),
						0L);
		if (!optBarcode.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Barcode dbBarcode = new Barcode();
		BeanUtils.copyProperties(newBarcode, dbBarcode, CommonUtils.getNullPropertyNames(newBarcode));
		
		dbBarcode.setLanguageId(getLanguageId());
		dbBarcode.setCompanyId(getCompanyCode());
		dbBarcode.setPlantId(getPlantId());
		dbBarcode.setDeletionIndicator(0L);
		dbBarcode.setCompanyId(getCompanyCode());
		dbBarcode.setCreatedBy(loginUserID);
		dbBarcode.setUpdatedBy(loginUserID);
		dbBarcode.setCreatedOn(new Date());
		dbBarcode.setUpdatedOn(new Date());
		return barcodeRepository.save(dbBarcode);
	}
	
	/**
	 * updateBarcode
	 * @param updateBarcode
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Barcode updateBarcode (String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId, 
			Long levelId, String levelReference, Long processId, UpdateBarcode updateBarcode, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Barcode dbBarcode = getBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId, 
	   			levelReference, processId);
		BeanUtils.copyProperties(updateBarcode, dbBarcode, CommonUtils.getNullPropertyNames(updateBarcode));
		dbBarcode.setUpdatedBy(loginUserID);
		dbBarcode.setUpdatedOn(new Date());
		return barcodeRepository.save(dbBarcode);
	}
	
	/**
	 * deleteBarcode
	 */
	public void deleteBarcode (String warehouseId, String method, Long barcodeTypeId, Long barcodeSubTypeId, 
			Long levelId, String levelReference, Long processId, String loginUserID) {
		Barcode barcode = getBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId, 
	   			levelReference, processId);
		if ( barcode != null) {
			barcode.setDeletionIndicator (1L);
			barcode.setUpdatedBy(loginUserID);
			barcode.setUpdatedOn(new Date());
			barcodeRepository.save(barcode);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + barcodeTypeId);
		}
	}
}
