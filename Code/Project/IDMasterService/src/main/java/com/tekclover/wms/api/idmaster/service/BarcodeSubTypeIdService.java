package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.AddBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.BarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.UpdateBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.repository.BarcodeSubTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeSubTypeIdService extends BaseService {
	
	@Autowired
	private BarcodeSubTypeIdRepository barcodeSubTypeIdRepository;
	
	/**
	 * getBarcodeSubTypeIds
	 * @return
	 */
	public List<BarcodeSubTypeId> getBarcodeSubTypeIds () {
		List<BarcodeSubTypeId> barcodeSubTypeIdList =  barcodeSubTypeIdRepository.findAll();
		barcodeSubTypeIdList = barcodeSubTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return barcodeSubTypeIdList;
	}
	
	/**
	 * getBarcodeSubTypeId
	 * @param barcodeSubTypeId
	 * @return
	 */
	public BarcodeSubTypeId getBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId) {
		Optional<BarcodeSubTypeId> dbBarcodeSubTypeId = 
				barcodeSubTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndBarcodeSubTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								barcodeTypeId,
								barcodeSubTypeId,
								getLanguageId(),
								0L
								);
		if (dbBarcodeSubTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"barcodeTypeId - " + barcodeTypeId +
						"barcodeSubTypeId - " + barcodeSubTypeId +
						" doesn't exist.");
			
		} 
		return dbBarcodeSubTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchBarcodeSubTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<BarcodeSubTypeId> findBarcodeSubTypeId(SearchBarcodeSubTypeId searchBarcodeSubTypeId) 
//			throws ParseException {
//		BarcodeSubTypeIdSpecification spec = new BarcodeSubTypeIdSpecification(searchBarcodeSubTypeId);
//		List<BarcodeSubTypeId> results = barcodeSubTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createBarcodeSubTypeId
	 * @param newBarcodeSubTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeSubTypeId createBarcodeSubTypeId (AddBarcodeSubTypeId newBarcodeSubTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BarcodeSubTypeId dbBarcodeSubTypeId = new BarcodeSubTypeId();
		log.info("newBarcodeSubTypeId : " + newBarcodeSubTypeId);
		BeanUtils.copyProperties(newBarcodeSubTypeId, dbBarcodeSubTypeId, CommonUtils.getNullPropertyNames(newBarcodeSubTypeId));
		dbBarcodeSubTypeId.setCompanyCodeId(getCompanyCode());
		dbBarcodeSubTypeId.setPlantId(getPlantId());
		dbBarcodeSubTypeId.setDeletionIndicator(0L);
		dbBarcodeSubTypeId.setCreatedBy(loginUserID);
		dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
		dbBarcodeSubTypeId.setCreatedOn(new Date());
		dbBarcodeSubTypeId.setUpdatedOn(new Date());
		return barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
	}
	
	/**
	 * updateBarcodeSubTypeId
	 * @param loginUserId 
	 * @param barcodeSubTypeId
	 * @param updateBarcodeSubTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeSubTypeId updateBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId, String loginUserID, 
			UpdateBarcodeSubTypeId updateBarcodeSubTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		BarcodeSubTypeId dbBarcodeSubTypeId = getBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId);
		BeanUtils.copyProperties(updateBarcodeSubTypeId, dbBarcodeSubTypeId, CommonUtils.getNullPropertyNames(updateBarcodeSubTypeId));
		dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
		dbBarcodeSubTypeId.setUpdatedOn(new Date());
		return barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
	}
	
	/**
	 * deleteBarcodeSubTypeId
	 * @param loginUserID 
	 * @param barcodeSubTypeId
	 */
	public void deleteBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId, String loginUserID) {
		BarcodeSubTypeId dbBarcodeSubTypeId = getBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId);
		if ( dbBarcodeSubTypeId != null) {
			dbBarcodeSubTypeId.setDeletionIndicator(1L);
			dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
			barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + barcodeSubTypeId);
		}
	}
}
