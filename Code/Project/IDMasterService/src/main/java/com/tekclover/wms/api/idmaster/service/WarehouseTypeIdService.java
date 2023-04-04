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
import com.tekclover.wms.api.idmaster.model.warehousetypeid.AddWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.UpdateWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import com.tekclover.wms.api.idmaster.repository.WarehouseTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseTypeIdService extends BaseService {
	
	@Autowired
	private WarehouseTypeIdRepository warehouseTypeIdRepository;
	
	/**
	 * getWarehouseTypeIds
	 * @return
	 */
	public List<WarehouseTypeId> getWarehouseTypeIds () {
		List<WarehouseTypeId> warehouseTypeIdList =  warehouseTypeIdRepository.findAll();
		warehouseTypeIdList = warehouseTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return warehouseTypeIdList;
	}
	
	/**
	 * getWarehouseTypeId
	 * @param warehouseTypeId
	 * @return
	 */
	public WarehouseTypeId getWarehouseTypeId (String warehouseId, Long warehouseTypeId) {
		Optional<WarehouseTypeId> dbWarehouseTypeId = 
				warehouseTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWarehouseTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								warehouseTypeId,
								getLanguageId(),
								0L
								);
		if (dbWarehouseTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"warehouseTypeId - " + warehouseTypeId +
						" doesn't exist.");
			
		} 
		return dbWarehouseTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchWarehouseTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<WarehouseTypeId> findWarehouseTypeId(SearchWarehouseTypeId searchWarehouseTypeId) 
//			throws ParseException {
//		WarehouseTypeIdSpecification spec = new WarehouseTypeIdSpecification(searchWarehouseTypeId);
//		List<WarehouseTypeId> results = warehouseTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createWarehouseTypeId
	 * @param newWarehouseTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WarehouseTypeId createWarehouseTypeId (AddWarehouseTypeId newWarehouseTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		WarehouseTypeId dbWarehouseTypeId = new WarehouseTypeId();
		log.info("newWarehouseTypeId : " + newWarehouseTypeId);
		BeanUtils.copyProperties(newWarehouseTypeId, dbWarehouseTypeId, CommonUtils.getNullPropertyNames(newWarehouseTypeId));
		dbWarehouseTypeId.setCompanyCodeId(getCompanyCode());
		dbWarehouseTypeId.setPlantId(getPlantId());
		dbWarehouseTypeId.setDeletionIndicator(0L);
		dbWarehouseTypeId.setCreatedBy(loginUserID);
		dbWarehouseTypeId.setUpdatedBy(loginUserID);
		dbWarehouseTypeId.setCreatedOn(new Date());
		dbWarehouseTypeId.setUpdatedOn(new Date());
		return warehouseTypeIdRepository.save(dbWarehouseTypeId);
	}
	
	/**
	 * updateWarehouseTypeId
	 * @param loginUserID
	 * @param warehouseTypeId
	 * @param updateWarehouseTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WarehouseTypeId updateWarehouseTypeId (String warehouseId, Long warehouseTypeId, String loginUserID, 
			UpdateWarehouseTypeId updateWarehouseTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		WarehouseTypeId dbWarehouseTypeId = getWarehouseTypeId(warehouseId, warehouseTypeId);
		BeanUtils.copyProperties(updateWarehouseTypeId, dbWarehouseTypeId, CommonUtils.getNullPropertyNames(updateWarehouseTypeId));
		dbWarehouseTypeId.setUpdatedBy(loginUserID);
		dbWarehouseTypeId.setUpdatedOn(new Date());
		return warehouseTypeIdRepository.save(dbWarehouseTypeId);
	}
	
	/**
	 * deleteWarehouseTypeId
	 * @param loginUserID 
	 * @param warehouseTypeId
	 */
	public void deleteWarehouseTypeId (String warehouseId, Long warehouseTypeId, String loginUserID) {
		WarehouseTypeId dbWarehouseTypeId = getWarehouseTypeId(warehouseId, warehouseTypeId);
		if ( dbWarehouseTypeId != null) {
			dbWarehouseTypeId.setDeletionIndicator(1L);
			dbWarehouseTypeId.setUpdatedBy(loginUserID);
			warehouseTypeIdRepository.save(dbWarehouseTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + warehouseTypeId);
		}
	}
}
