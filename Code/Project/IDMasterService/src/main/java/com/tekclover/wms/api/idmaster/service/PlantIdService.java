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
import com.tekclover.wms.api.idmaster.model.plantid.AddPlantId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.plantid.UpdatePlantId;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlantIdService extends BaseService {
	
	@Autowired
	private PlantIdRepository plantIdRepository;
	
	/**
	 * getPlantIds
	 * @return
	 */
	public List<PlantId> getPlantIds () {
		List<PlantId> plantIdList =  plantIdRepository.findAll();
		plantIdList = plantIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return plantIdList;
	}
	
	/**
	 * getPlantId
	 * @param plantId
	 * @return
	 */
	public PlantId getPlantId (String plantId) {
		Optional<PlantId> dbPlantId = 
				plantIdRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(), plantId, getLanguageId(), 0L);
		if (dbPlantId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"plantId - " + plantId +
						" doesn't exist.");
			
		} 
		return dbPlantId.get();
	}
	
//	/**
//	 * 
//	 * @param searchPlantId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<PlantId> findPlantId(SearchPlantId searchPlantId) 
//			throws ParseException {
//		PlantIdSpecification spec = new PlantIdSpecification(searchPlantId);
//		List<PlantId> results = plantIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createPlantId
	 * @param newPlantId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PlantId createPlantId (AddPlantId newPlantId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {

		PlantId dbPlantId = new PlantId();
		log.info("newPlantId : " + newPlantId);
		BeanUtils.copyProperties(newPlantId, dbPlantId, CommonUtils.getNullPropertyNames(newPlantId));
		dbPlantId.setCompanyCodeId(getCompanyCode());
		dbPlantId.setPlantId(getPlantId());
		dbPlantId.setDeletionIndicator(0L);
		dbPlantId.setCreatedBy(loginUserID);
		dbPlantId.setUpdatedBy(loginUserID);
		dbPlantId.setCreatedOn(new Date());
		dbPlantId.setUpdatedOn(new Date());
		return plantIdRepository.save(dbPlantId);
	}
	
	/**
	 * updatePlantId
	 * @param loginUserId 
	 * @param plantId
	 * @param updatePlantId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PlantId updatePlantId (String plantId, String loginUserID, UpdatePlantId updatePlantId) 
			throws IllegalAccessException, InvocationTargetException {
		PlantId dbPlantId = getPlantId(plantId);
		BeanUtils.copyProperties(updatePlantId, dbPlantId, CommonUtils.getNullPropertyNames(updatePlantId));
		dbPlantId.setUpdatedBy(loginUserID);
		dbPlantId.setUpdatedOn(new Date());
		return plantIdRepository.save(dbPlantId);
	}
	
	/**
	 * deletePlantId
	 * @param loginUserID 
	 * @param plantId
	 */
	public void deletePlantId (String plantId, String loginUserID) {
		PlantId dbPlantId = getPlantId(plantId);
		if ( plantId != null) {
			dbPlantId.setDeletionIndicator(1L);
			dbPlantId.setUpdatedBy(loginUserID);
			plantIdRepository.save(dbPlantId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + plantId);
		}
	}
}
