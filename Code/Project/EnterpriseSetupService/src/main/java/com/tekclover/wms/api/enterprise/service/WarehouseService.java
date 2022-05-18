package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.warehouse.AddWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.SearchWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.UpdateWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
import com.tekclover.wms.api.enterprise.repository.specification.WarehouseSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

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
		List<Warehouse> warehouseList = warehouseRepository.findAll();
		log.info("warehouseList : " + warehouseList);
		warehouseList = warehouseList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return warehouseList;
	}
	
	/**
	 * getWarehouse
	 * @param warehouseId
	 * @param modeOfImplementation
	 * @param warehouseTypeId
	 * @return
	 */
	public Warehouse getWarehouse (String warehouseId, String modeOfImplementation, Long warehouseTypeId) {
		Optional<Warehouse> warehouse = 
				warehouseRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, modeOfImplementation, warehouseTypeId, 
						0L);
		if (warehouse.isEmpty()) {
			throw new BadRequestException("The given Warehouse ID : " + warehouseId + " doesn't exist.");
			
		} 
		return warehouse.get();
	}
	
	
	/**
	 * getWarehouse
	 * @param warehouseId
	 * @return
	 */
	public Warehouse getWarehouse (String warehouseId) {
		Warehouse warehouse = warehouseRepository.findByWarehouseId(warehouseId).orElse(null);
		if (warehouse != null && warehouse.getDeletionIndicator() != null && warehouse.getDeletionIndicator() == 0) {
			return warehouse;
		} else {
			throw new BadRequestException("The given Warehouse ID : " + warehouseId + " doesn't exist.");
		}
	}
	
	/**
	 * findWarehouse
	 * @param searchWarehouse
	 * @return
	 * @throws ParseException
	 */
	public List<Warehouse> findWarehouse(SearchWarehouse searchWarehouse) throws Exception {
		if (searchWarehouse.getStartCreatedOn() != null && searchWarehouse.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchWarehouse.getStartCreatedOn(), searchWarehouse.getEndCreatedOn());
			searchWarehouse.setStartCreatedOn(dates[0]);
			searchWarehouse.setEndCreatedOn(dates[1]);
		}
		
		WarehouseSpecification spec = new WarehouseSpecification(searchWarehouse);
		List<Warehouse> results = warehouseRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createWarehouse
	 * @param newWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse createWarehouse (AddWarehouse newWarehouse , String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<Warehouse> optWarehouse = 
				warehouseRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator(
				getLanguageId(), getCompanyCode(), getPlantId(), newWarehouse.getWarehouseId(), 
				newWarehouse.getModeOfImplementation(), newWarehouse.getWarehouseTypeId(), 0L);
		if (!optWarehouse.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Warehouse dbWarehouse = new Warehouse();
		BeanUtils.copyProperties(newWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(newWarehouse));
		
		dbWarehouse.setCompanyId(getCompanyCode());
		dbWarehouse.setLanguageId(getLanguageId());
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
	 * @param warehouseCode
	 * @param updateWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse updateWarehouse (String warehouseId, UpdateWarehouse updateWarehouse , String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Warehouse dbWarehouse = getWarehouse(warehouseId);
		BeanUtils.copyProperties(updateWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(updateWarehouse));
		dbWarehouse.setUpdatedBy(loginUserID);
		dbWarehouse.setUpdatedOn(new Date());
		return warehouseRepository.save(dbWarehouse);
	}
	
	/**
	 * deleteWarehouse
	 * @param warehouseCode
	 */
	public void deleteWarehouse (String warehouseId , String loginUserID) {
		Warehouse warehouse = getWarehouse(warehouseId);
		if ( warehouse != null) {
			warehouse.setDeletionIndicator (1L);
			warehouse.setUpdatedBy(loginUserID);
			warehouse.setUpdatedOn(new Date());
			warehouseRepository.save(warehouse);
		} else {
			throw new EntityNotFoundException(String.valueOf(warehouseId));
		}
	}
}
