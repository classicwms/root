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
import com.tekclover.wms.api.enterprise.model.floor.AddFloor;
import com.tekclover.wms.api.enterprise.model.floor.Floor;
import com.tekclover.wms.api.enterprise.model.floor.SearchFloor;
import com.tekclover.wms.api.enterprise.model.floor.UpdateFloor;
import com.tekclover.wms.api.enterprise.repository.FloorRepository;
import com.tekclover.wms.api.enterprise.repository.specification.FloorSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FloorService extends BaseService {
	
	@Autowired
	private FloorRepository floorRepository;
	
	/**
	 * getFloors
	 * @return
	 */
	public List<Floor> getFloors () {
		List<Floor> floorList = floorRepository.findAll();
		log.info("floorList : " + floorList);
		floorList = floorList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return floorList;
	}
	
	/**
	 * getFloor
	 * @param warehouseId
	 * @param floorId
	 * @return
	 */
	public Floor getFloor (String warehouseId, Long floorId) {
		Optional<Floor> floor = 
				floorRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, floorId, 0L);
		if (floor.isEmpty()) {
			throw new BadRequestException("The given Floor Id : " + floorId + " doesn't exist.");
		} 
		return floor.get();
	}
	
	/**
	 * findFloor
	 * @param searchFloor
	 * @return
	 * @throws ParseException
	 */
	public List<Floor> findFloor(SearchFloor searchFloor) throws Exception {
		if (searchFloor.getStartCreatedOn() != null && searchFloor.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchFloor.getStartCreatedOn(), searchFloor.getEndCreatedOn());
			searchFloor.setStartCreatedOn(dates[0]);
			searchFloor.setEndCreatedOn(dates[1]);
		}
		
		FloorSpecification spec = new FloorSpecification(searchFloor);
		List<Floor> results = floorRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createFloor
	 * @param newFloor
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Floor createFloor (AddFloor newFloor, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<Floor> optFloor = 
				floorRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newFloor.getWarehouseId(),
						newFloor.getFloorId(),
						0L);
		if (!optFloor.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Floor dbFloor = new Floor();
		BeanUtils.copyProperties(newFloor, dbFloor, CommonUtils.getNullPropertyNames(newFloor));
		dbFloor.setLanguageId(getLanguageId());
		dbFloor.setCompanyId(getCompanyCode());
		dbFloor.setPlantId(getPlantId());
		
		dbFloor.setDeletionIndicator(0L);
		dbFloor.setCreatedBy(loginUserID);
		dbFloor.setUpdatedBy(loginUserID);
		dbFloor.setCreatedOn(new Date());
		dbFloor.setUpdatedOn(new Date());
		return floorRepository.save(dbFloor);
	}
	
	/**
	 * updateFloor
	 * @param floorCode
	 * @param updateFloor
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Floor updateFloor (String warehouseId, Long floorId, UpdateFloor updateFloor, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Floor dbFloor = getFloor(warehouseId, floorId);
		BeanUtils.copyProperties(updateFloor, dbFloor, CommonUtils.getNullPropertyNames(updateFloor));
		dbFloor.setUpdatedBy(loginUserID);
		dbFloor.setUpdatedOn(new Date());
		return floorRepository.save(dbFloor);
	}
	
	/**
	 * deleteFloor
	 * @param floorCode
	 */
	public void deleteFloor (String warehouseId, Long floorId, String loginUserID) {
		Floor floor = getFloor(warehouseId, floorId);
		if ( floor != null) {
			floor.setDeletionIndicator (1L);
			floor.setUpdatedBy(loginUserID);
			floor.setUpdatedOn(new Date());
			floorRepository.save(floor);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + floorId);
		}
	}
}
