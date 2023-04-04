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
import com.tekclover.wms.api.idmaster.model.warehouseid.AddWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.UpdateWarehouse;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService extends BaseService {
	
	@Autowired
	private WarehouseRepository warehouseRepository;
	
	/**
	 * getWarehouses
	 * @return
	 */
	public List<Warehouse> getWarehouses () {
		List<Warehouse> warehouseIdList =  warehouseRepository.findAll();
		warehouseIdList = warehouseIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return warehouseIdList;
	}
	
	/**
	 * getWarehouse
	 * @param warehouseId
	 * @returnm
	 */
	public Warehouse getWarehouse (String warehouseId) {
		Optional<Warehouse> dbWarehouse = 
				warehouseRepository.findByCompanyCodeAndWarehouseIdAndLanguageIdAndPlantIdAndDeletionIndicator(
								getCompanyCode(),
								warehouseId,
								getLanguageId(),
								getPlantId(),
								0L
								);
		if (dbWarehouse.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						" doesn't exist.");
			
		} 
		return dbWarehouse.get();
	}
	
	/**
	 * createWarehouse
	 * @param newWarehouse
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse createWarehouse (AddWarehouse newWarehouse, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Warehouse dbWarehouse = new Warehouse();
		log.info("newWarehouse : " + newWarehouse);
		BeanUtils.copyProperties(newWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(newWarehouse));
		dbWarehouse.setCompanyCode(getCompanyCode());
		dbWarehouse.setPlantId(getPlantId());
		dbWarehouse.setDeletionIndicator(0L);
		dbWarehouse.setCreatedBy(loginUserID);
		dbWarehouse.setUpdatedBy(loginUserID);
		dbWarehouse.setCreatedOn(new Date());
		dbWarehouse.setUpdatedOn(new Date());
		return warehouseRepository.save(dbWarehouse);
	}
	
	/**
	 * updateWarehouse
	 * @param loginUserID
	 * @param warehouseId
	 * @param updateWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse updateWarehouse (String warehouseId, String loginUserID, 
			UpdateWarehouse updateWarehouse) 
			throws IllegalAccessException, InvocationTargetException {
		Warehouse dbWarehouse = getWarehouse(warehouseId);
		BeanUtils.copyProperties(updateWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(updateWarehouse));
		dbWarehouse.setUpdatedBy(loginUserID);
		dbWarehouse.setUpdatedOn(new Date());
		return warehouseRepository.save(dbWarehouse);
	}
	
	/**
	 * deleteWarehouse
	 * @param loginUserID 
	 * @param warehouseId
	 */
	public void deleteWarehouse (String warehouseId, String loginUserID) {
		Warehouse dbWarehouse = getWarehouse(warehouseId);
		if ( dbWarehouse != null) {
			dbWarehouse.setDeletionIndicator(1L);
			dbWarehouse.setUpdatedBy(loginUserID);
			warehouseRepository.save(dbWarehouse);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + warehouseId);
		}
	}
}
