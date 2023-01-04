package com.ustorage.api.master.service;

import com.ustorage.api.master.model.country.*;

import com.ustorage.api.master.repository.CountryRepository;
import com.ustorage.api.master.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CountryService {
	
	@Autowired
	private CountryRepository countryRepository;
	
	public List<Country> getCountry () {
		List<Country> countryList =  countryRepository.findAll();
		countryList = countryList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return countryList;
	}
	
	/**
	 * getCountry
	 * @param countryId
	 * @return
	 */
	public Country getCountry (String countryId) {
		Optional<Country> country = countryRepository.findByCodeAndDeletionIndicator(countryId, 0L);
		if (country.isEmpty()) {
			return null;
		}
		return country.get();
	}
	
	/**
	 * createCountry
	 * @param newCountry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Country createCountry (AddCountry newCountry, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Country dbCountry = new Country();
		BeanUtils.copyProperties(newCountry, dbCountry, CommonUtils.getNullPropertyNames(newCountry));
		dbCountry.setDeletionIndicator(0L);
		dbCountry.setCreatedBy(loginUserId);
		dbCountry.setUpdatedBy(loginUserId);
		dbCountry.setCreatedOn(new Date());
		dbCountry.setUpdatedOn(new Date());
		return countryRepository.save(dbCountry);
	}
	
	/**
	 * updateCountry
	 * @param countryId
	 * @param loginUserId 
	 * @param updateCountry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Country updateCountry (String code, String loginUserId, UpdateCountry updateCountry)
			throws IllegalAccessException, InvocationTargetException {
		Country dbCountry = getCountry(code);
		BeanUtils.copyProperties(updateCountry, dbCountry, CommonUtils.getNullPropertyNames(updateCountry));
		dbCountry.setUpdatedBy(loginUserId);
		dbCountry.setUpdatedOn(new Date());
		return countryRepository.save(dbCountry);
	}
	
	/**
	 * deleteCountry
	 * @param loginUserID 
	 * @param countryCode
	 */
	public void deleteCountry (String countryModuleId, String loginUserID) {
		Country country = getCountry(countryModuleId);
		if (country != null) {
			country.setDeletionIndicator(1L);
			country.setUpdatedBy(loginUserID);
			country.setUpdatedOn(new Date());
			countryRepository.save(country);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + countryModuleId);
		}
	}
}
