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
import com.tekclover.wms.api.idmaster.model.floorid.AddFloorId;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.floorid.UpdateFloorId;
import com.tekclover.wms.api.idmaster.repository.FloorIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FloorIdService extends BaseService {
	
	@Autowired
	private FloorIdRepository floorIdRepository;
	
	/**
	 * getFloorIds
	 * @return
	 */
	public List<FloorId> getFloorIds () {
		List<FloorId> floorIdList =  floorIdRepository.findAll();
		floorIdList = floorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return floorIdList;
	}
	
	/**
	 * getFloorId
	 * @param floorId
	 * @return
	 */
	public FloorId getFloorId (String warehouseId, Long floorId) {
		Optional<FloorId> dbFloorId = 
				floorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								floorId,
								getLanguageId(),
								0L
								);
		if (dbFloorId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"floorId - " + floorId +
						" doesn't exist.");
			
		} 
		return dbFloorId.get();
	}
	
//	/**
//	 * 
//	 * @param searchFloorId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<FloorId> findFloorId(SearchFloorId searchFloorId) 
//			throws ParseException {
//		FloorIdSpecification spec = new FloorIdSpecification(searchFloorId);
//		List<FloorId> results = floorIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createFloorId
	 * @param newFloorId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FloorId createFloorId (AddFloorId newFloorId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		FloorId dbFloorId = new FloorId();
		log.info("newFloorId : " + newFloorId);
		BeanUtils.copyProperties(newFloorId, dbFloorId, CommonUtils.getNullPropertyNames(newFloorId));
		dbFloorId.setCompanyCodeId(getCompanyCode());
		dbFloorId.setPlantId(getPlantId());
		dbFloorId.setDeletionIndicator(0L);
		dbFloorId.setCreatedBy(loginUserID);
		dbFloorId.setUpdatedBy(loginUserID);
		dbFloorId.setCreatedOn(new Date());
		dbFloorId.setUpdatedOn(new Date());
		return floorIdRepository.save(dbFloorId);
	}
	
	/**
	 * updateFloorId
	 * @param loginUserId 
	 * @param floorId
	 * @param updateFloorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FloorId updateFloorId (String warehouseId, Long floorId, String loginUserID, 
			UpdateFloorId updateFloorId) 
			throws IllegalAccessException, InvocationTargetException {
		FloorId dbFloorId = getFloorId( warehouseId, floorId);
		BeanUtils.copyProperties(updateFloorId, dbFloorId, CommonUtils.getNullPropertyNames(updateFloorId));
		dbFloorId.setUpdatedBy(loginUserID);
		dbFloorId.setUpdatedOn(new Date());
		return floorIdRepository.save(dbFloorId);
	}
	
	/**
	 * deleteFloorId
	 * @param loginUserID 
	 * @param floorId
	 */
	public void deleteFloorId (String warehouseId, Long floorId, String loginUserID) {
		FloorId dbFloorId = getFloorId( warehouseId, floorId);
		if ( dbFloorId != null) {
			dbFloorId.setDeletionIndicator(1L);
			dbFloorId.setUpdatedBy(loginUserID);
			floorIdRepository.save(dbFloorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + floorId);
		}
	}
}
