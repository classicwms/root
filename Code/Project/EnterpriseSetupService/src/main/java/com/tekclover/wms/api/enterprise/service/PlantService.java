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
import com.tekclover.wms.api.enterprise.model.plant.AddPlant;
import com.tekclover.wms.api.enterprise.model.plant.Plant;
import com.tekclover.wms.api.enterprise.model.plant.SearchPlant;
import com.tekclover.wms.api.enterprise.model.plant.UpdatePlant;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.specification.PlantSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlantService extends BaseService {
	
	@Autowired
	private PlantRepository plantRepository;
	
	/**
	 * getPlants
	 * @return
	 */
	public List<Plant> getPlants () {
		List<Plant> plantList = plantRepository.findAll();
		log.info("plantList : " + plantList);
		plantList = plantList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return plantList;
	}
	
	/**
	 * getPlant
	 * @param plantId
	 * @return
	 */
	public Plant getPlant (String plantId) {
		Optional<Plant> plant = 
				plantRepository.findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), 0L);
		if (plant.isEmpty()) {
			throw new BadRequestException("The given Plant Id : " + plantId + " doesn't exist.");
		} 
		return plant.get();
	}
	
	/**
	 * findPlant
	 * @param searchPlant
	 * @return
	 * @throws ParseException
	 */
	public List<Plant> findPlant(SearchPlant searchPlant) throws Exception {
		if (searchPlant.getStartCreatedOn() != null && searchPlant.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPlant.getStartCreatedOn(), searchPlant.getEndCreatedOn());
			searchPlant.setStartCreatedOn(dates[0]);
			searchPlant.setEndCreatedOn(dates[1]);
		}
		
		PlantSpecification spec = new PlantSpecification(searchPlant);
		List<Plant> results = plantRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPlant
	 * @param newPlant
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Plant createPlant (AddPlant newPlant, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<Plant> optPlant = 
				plantRepository.findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						0L);
		if (!optPlant.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Plant dbPlant = new Plant();
		BeanUtils.copyProperties(newPlant, dbPlant, CommonUtils.getNullPropertyNames(newPlant));
		
		dbPlant.setLanguageId(getLanguageId());
		dbPlant.setCompanyId(getCompanyCode());
		dbPlant.setPlantId(getPlantId());
		
		dbPlant.setDeletionIndicator(0L);
		dbPlant.setCreatedBy(loginUserID);
		dbPlant.setUpdatedBy(loginUserID);
		dbPlant.setCreatedOn(new Date());
		dbPlant.setUpdatedOn(new Date());
		return plantRepository.save(dbPlant);
	}
	
	/**
	 * updatePlant
	 * @param plantCode
	 * @param updatePlant
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Plant updatePlant (String plantId, UpdatePlant updatePlant, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Plant dbPlant = getPlant(plantId);
		BeanUtils.copyProperties(updatePlant, dbPlant, CommonUtils.getNullPropertyNames(updatePlant));
		dbPlant.setUpdatedBy(loginUserID);
		dbPlant.setUpdatedOn(new Date());
		return plantRepository.save(dbPlant);
	}
	
	/**
	 * deletePlant
	 * @param plantCode
	 */
	public void deletePlant (String plantId, String loginUserID) {
		Plant plant = getPlant(plantId);
		if ( plant != null) {
			plant.setDeletionIndicator (1L);
			plant.setUpdatedBy(loginUserID);
			plant.setUpdatedOn(new Date());
			plantRepository.save(plant);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + plantId);
		}
	}
}
