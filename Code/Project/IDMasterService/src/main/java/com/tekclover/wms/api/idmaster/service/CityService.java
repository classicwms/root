package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.city.AddCity;
import com.tekclover.wms.api.idmaster.model.city.City;
import com.tekclover.wms.api.idmaster.model.city.UpdateCity;
import com.tekclover.wms.api.idmaster.repository.CityRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CityService {
	
	@Autowired
	private CityRepository cityRepository;
	
	/**
	 * getCompanies
	 * @return
	 */
	public List<City> getCompanies () {
		return cityRepository.findAll();
	}
	
	/**
	 * getCity
	 * @param cityId
	 * @return
	 */
	public City getCity (String cityId) {
		log.info("city Id: " + cityId);
		City optCity = cityRepository.findByCityId(cityId).orElse(null);
		if (optCity == null) {
			throw new BadRequestException("The given ID doesn't exist : " + optCity);
		}
		return optCity;
	}
	
	/**
	 * createCity
	 * @param newCity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public City createCity (AddCity newCity) 
			throws IllegalAccessException, InvocationTargetException {
		City dbCity = new City();
		BeanUtils.copyProperties(newCity, dbCity, CommonUtils.getNullPropertyNames(newCity));
		dbCity.setDeletionIndicator(0L);
		dbCity.setCreatedOn(new Date());
		dbCity.setUpdatedOn(new Date());
		return cityRepository.save(dbCity);
	}
	
	/**
	 * updateCity
	 * @param cityId
	 * @param updateCity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public City updateCity (String cityId, UpdateCity updateCity) 
			throws IllegalAccessException, InvocationTargetException {
		City dbCity = getCity(cityId);
		BeanUtils.copyProperties(updateCity, dbCity, CommonUtils.getNullPropertyNames(updateCity));
		dbCity.setDeletionIndicator(0L);
		dbCity.setUpdatedOn(new Date());
		return cityRepository.save(dbCity);
	}
	
	/**
	 * deleteCity
	 * @param cityCode
	 */
	public void deleteCity (String cityId) {
		City city = getCity(cityId);
		if ( city != null) {
			cityRepository.delete(city);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cityId);
		}
	}
}
