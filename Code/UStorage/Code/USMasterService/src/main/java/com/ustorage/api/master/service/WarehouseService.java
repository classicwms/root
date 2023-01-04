package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.warehouse.AddWarehouse;
import com.ustorage.api.master.model.warehouse.Warehouse;
import com.ustorage.api.master.model.warehouse.UpdateWarehouse;
import com.ustorage.api.master.repository.WarehouseRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService {
	
	@Autowired
	private WarehouseRepository warehouseRepository;
	
	public List<Warehouse> getWarehouse () {
		List<Warehouse> warehouseList =  warehouseRepository.findAll();
		warehouseList = warehouseList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return warehouseList;
	}
	
	/**
	 * getWarehouse
	 * @param warehouseId
	 * @return
	 */
	public Warehouse getWarehouse (String warehouseId) {
		Optional<Warehouse> warehouse = warehouseRepository.findByCodeAndDeletionIndicator(warehouseId, 0L);
		if (warehouse.isEmpty()) {
			return null;
		}
		return warehouse.get();
	}
	
	/**
	 * createWarehouse
	 * @param newWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse createWarehouse (AddWarehouse newWarehouse, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Warehouse dbWarehouse = new Warehouse();
		BeanUtils.copyProperties(newWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(newWarehouse));
		dbWarehouse.setDeletionIndicator(0L);
		dbWarehouse.setCreatedBy(loginUserId);
		dbWarehouse.setUpdatedBy(loginUserId);
		dbWarehouse.setCreatedOn(new Date());
		dbWarehouse.setUpdatedOn(new Date());
		return warehouseRepository.save(dbWarehouse);
	}
	
	/**
	 * updateWarehouse
	 * @param warehouseId
	 * @param loginUserId 
	 * @param updateWarehouse
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Warehouse updateWarehouse (String code, String loginUserId, UpdateWarehouse updateWarehouse)
			throws IllegalAccessException, InvocationTargetException {
		Warehouse dbWarehouse = getWarehouse(code);
		BeanUtils.copyProperties(updateWarehouse, dbWarehouse, CommonUtils.getNullPropertyNames(updateWarehouse));
		dbWarehouse.setUpdatedBy(loginUserId);
		dbWarehouse.setUpdatedOn(new Date());
		return warehouseRepository.save(dbWarehouse);
	}
	
	/**
	 * deleteWarehouse
	 * @param loginUserID 
	 * @param warehouseCode
	 */
	public void deleteWarehouse (String warehouseModuleId, String loginUserID) {
		Warehouse warehouse = getWarehouse(warehouseModuleId);
		if (warehouse != null) {
			warehouse.setDeletionIndicator(1L);
			warehouse.setUpdatedBy(loginUserID);
			warehouse.setUpdatedOn(new Date());
			warehouseRepository.save(warehouse);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + warehouseModuleId);
		}
	}
}
