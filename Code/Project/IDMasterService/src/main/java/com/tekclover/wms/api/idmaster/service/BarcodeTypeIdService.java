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
import com.tekclover.wms.api.idmaster.model.barcodetypeid.AddBarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.UpdateBarcodeTypeId;
import com.tekclover.wms.api.idmaster.repository.BarcodeTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeTypeIdService extends BaseService {
	
	@Autowired
	private BarcodeTypeIdRepository barcodeTypeIdRepository;
	
	/**
	 * getBarcodeTypeIds
	 * @return
	 */
	public List<BarcodeTypeId> getBarcodeTypeIds () {
		List<BarcodeTypeId> barcodeTypeIdList =  barcodeTypeIdRepository.findAll();
		barcodeTypeIdList = barcodeTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return barcodeTypeIdList;
	}
	
	/**
	 * getBarcodeTypeId
	 * @param barcodeTypeId
	 * @return
	 */
	public BarcodeTypeId getBarcodeTypeId (String warehouseId, Long barcodeTypeId) {
		Optional<BarcodeTypeId> dbBarcodeTypeId = 
				barcodeTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								barcodeTypeId,
								getLanguageId(),
								0L
								);
		if (dbBarcodeTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"barcodeTypeId - " + barcodeTypeId +
						" doesn't exist.");
			
		} 
		return dbBarcodeTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchBarcodeTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<BarcodeTypeId> findBarcodeTypeId(SearchBarcodeTypeId searchBarcodeTypeId) 
//			throws ParseException {
//		BarcodeTypeIdSpecification spec = new BarcodeTypeIdSpecification(searchBarcodeTypeId);
//		List<BarcodeTypeId> results = barcodeTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createBarcodeTypeId
	 * @param newBarcodeTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeTypeId createBarcodeTypeId (AddBarcodeTypeId newBarcodeTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId dbBarcodeTypeId = new BarcodeTypeId();
		log.info("newBarcodeTypeId : " + newBarcodeTypeId);
		BeanUtils.copyProperties(newBarcodeTypeId, dbBarcodeTypeId, CommonUtils.getNullPropertyNames(newBarcodeTypeId));
		
		dbBarcodeTypeId.setCompanyCodeId(getCompanyCode());
		dbBarcodeTypeId.setPlantId(getPlantId());
		dbBarcodeTypeId.setDeletionIndicator(0L);
		dbBarcodeTypeId.setCreatedBy(loginUserID);
		dbBarcodeTypeId.setUpdatedBy(loginUserID);
		dbBarcodeTypeId.setCreatedOn(new Date());
		dbBarcodeTypeId.setUpdatedOn(new Date());
		return barcodeTypeIdRepository.save(dbBarcodeTypeId);
	}
	
	/**
	 * updateBarcodeTypeId
	 * @param loginUserId 
	 * @param barcodeTypeId
	 * @param updateBarcodeTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeTypeId updateBarcodeTypeId (String warehouseId, Long barcodeTypeId, String loginUserID, 
			UpdateBarcodeTypeId updateBarcodeTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId dbBarcodeTypeId = getBarcodeTypeId(warehouseId, barcodeTypeId);
		BeanUtils.copyProperties(updateBarcodeTypeId, dbBarcodeTypeId, CommonUtils.getNullPropertyNames(updateBarcodeTypeId));
		dbBarcodeTypeId.setUpdatedBy(loginUserID);
		dbBarcodeTypeId.setUpdatedOn(new Date());
		return barcodeTypeIdRepository.save(dbBarcodeTypeId);
	}
	
	/**
	 * deleteBarcodeTypeId
	 * @param loginUserID 
	 * @param barcodeTypeId
	 */
	public void deleteBarcodeTypeId (String warehouseId, Long barcodeTypeId, String loginUserID) {
		BarcodeTypeId dbBarcodeTypeId = getBarcodeTypeId(warehouseId, barcodeTypeId);
		if ( dbBarcodeTypeId != null) {
			dbBarcodeTypeId.setDeletionIndicator(1L);
			dbBarcodeTypeId.setUpdatedBy(loginUserID);
			barcodeTypeIdRepository.save(dbBarcodeTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + barcodeTypeId);
		}
	}
}
