package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.country.AddCountry;
import com.tekclover.wms.api.idmaster.model.country.Country;
import com.tekclover.wms.api.idmaster.model.country.UpdateCountry;
import com.tekclover.wms.api.idmaster.repository.CountryRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CountryService {
	
	@Autowired
	private CountryRepository countryRepository;
	
	/**
	 * getCompanies
	 * @return
	 */
	public List<Country> getCompanies () {
		return countryRepository.findAll();
	}
	
	/**
	 * getCountry
	 * @param countryId
	 * @return
	 */
	public Country getCountry (String countryId) {
		log.info("country Id: " + countryId);
		Country country = countryRepository.findByCountryId(countryId).orElse(null);
		if (country == null) {
			throw new BadRequestException("The given ID doesn't exist : " + countryId);
		}
		return country;
	}
	
	/**
	 * createCountry
	 * @param newCountry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Country createCountry (AddCountry newCountry) 
			throws IllegalAccessException, InvocationTargetException {
		Country dbCountry = new Country();
		BeanUtils.copyProperties(newCountry, dbCountry, CommonUtils.getNullPropertyNames(newCountry));
		return countryRepository.save(dbCountry);
	}
	
	/**
	 * updateCountry
	 * @param countryId
	 * @param updateCountry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Country updateCountry (String countryId, UpdateCountry updateCountry) 
			throws IllegalAccessException, InvocationTargetException {
		Country dbCountry = getCountry(countryId);
		BeanUtils.copyProperties(updateCountry, dbCountry, CommonUtils.getNullPropertyNames(updateCountry));
		return countryRepository.save(dbCountry);
	}
	
	/**
	 * deleteCountry
	 * @param countryCode
	 */
	public void deleteCountry (String countryId) {
		Country country = getCountry(countryId);
		if ( country != null) {
			countryRepository.delete(country);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + countryId);
		}
	}
}
