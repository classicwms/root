package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.city.FindCity;
import com.tekclover.wms.api.idmaster.model.state.State;
import com.tekclover.wms.api.idmaster.repository.CountryRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.CitySpecification;
import com.tekclover.wms.api.idmaster.repository.StateRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private StateService stateService;

	/**
	 * getCompanies
	 * @return
	 */
	public List<City> getAllCity () {
		List<City> cityList =  cityRepository.findAll();
		cityList = cityList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<City> newCity=new ArrayList<>();
		for(City dbCity:cityList) {
			if (dbCity.getCountryIdAndDescription() != null&&dbCity.getStateIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = countryRepository.getCountryIdAndDescription(dbCity.getCountryId(), dbCity.getLanguageId());
				IKeyValuePair iKeyValuePair1 = stateRepository.getStateIdAndDescription(dbCity.getStateId(), dbCity.getLanguageId(), dbCity.getCountryId());
				if (iKeyValuePair != null) {
					dbCity.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbCity.setStateIdAndDescription(iKeyValuePair1.getStateId() + "-" + iKeyValuePair1.getDescription());
				}
			}
			newCity.add(dbCity);
		}
		return newCity;
	}

	/**
	 *
	 * @param cityId
	 * @param stateId
	 * @param countryId
	 * @param languageId
	 * @return
	 */
	public City getCity (String cityId,String stateId,String countryId,String languageId) {
		log.info("city Id: " + cityId);
		Optional<City> dbCity = cityRepository.findByCityIdAndStateIdAndCountryIdAndLanguageId(cityId,stateId,countryId,languageId);

		if (dbCity.isEmpty()) {
			throw new BadRequestException("The given ID doesn't exist : " +
					" City Id " + cityId +
					" State Id " + stateId +
					"Country Id " + countryId );
		}
		City newCity = new City();
		BeanUtils.copyProperties(dbCity.get(),newCity, CommonUtils.getNullPropertyNames(dbCity));

		IKeyValuePair iKeyValuePair=countryRepository.getCountryIdAndDescription(countryId,languageId);
		IKeyValuePair iKeyValuePair1=stateRepository.getStateIdAndDescription(stateId,languageId,countryId);
		if(iKeyValuePair!=null) {
			newCity.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newCity.setStateIdAndDescription(iKeyValuePair1.getStateId() + "-" + iKeyValuePair1.getDescription());
		}
		return newCity;
	}

	/**
	 * createCity
	 * @param newCity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public City createCity (AddCity newCity,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		City dbCity = new City();
		Optional<City> duplicateCity = cityRepository.findByCityIdAndStateIdAndCountryIdAndLanguageId(newCity.getCityId(),
				newCity.getStateId(), newCity.getCountryId(), newCity.getLanguageId());

		if (!duplicateCity.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {

			State dbState=stateService.getState(newCity.getStateId(), newCity.getCountryId(), newCity.getLanguageId());
			BeanUtils.copyProperties(newCity, dbCity, CommonUtils.getNullPropertyNames(newCity));
			dbCity.setDeletionIndicator(0L);
			dbCity.setStateIdAndDescription(dbState.getStateId()+"-"+dbState.getStateName());
			dbCity.setCountryIdAndDescription(dbState.getCountryIdAndDescription());
			dbCity.setCreatedBy(loginUserID);
			dbCity.setUpdatedBy(loginUserID);
			dbCity.setCreatedOn(new Date());
			dbCity.setUpdatedOn(new Date());
			return cityRepository.save(dbCity);
		}
	}

	/**
	 *
	 * @param cityId
	 * @param stateId
	 * @param countryId
	 * @param zipCode
	 * @param languageId
	 * @param loginUserID
	 * @param updateCity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public City updateCity (String cityId,String stateId,String countryId,String languageId,
							String loginUserID,UpdateCity updateCity)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		City dbCity = getCity(cityId,stateId,countryId,languageId);
		BeanUtils.copyProperties(updateCity, dbCity, CommonUtils.getNullPropertyNames(updateCity));
		dbCity.setDeletionIndicator(0L);
		dbCity.setUpdatedBy(loginUserID);
		dbCity.setUpdatedOn(new Date());
		return cityRepository.save(dbCity);
	}

	/**
	 *
	 * @param cityId
	 * @param stateId
	 * @param countryId
	 * @param languageId
	 */
	public void deleteCity (String cityId,String stateId,String countryId,String languageId) {
		City city = getCity(cityId,stateId,countryId,languageId);
		if ( city != null) {
			cityRepository.delete(city);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cityId);
		}
	}

	/**
	 *
	 * @param findCity
	 * @return
	 * @throws ParseException
	 */
	public List<City> findCity(FindCity findCity) throws ParseException {

		CitySpecification spec = new CitySpecification(findCity);
		List<City> results = cityRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<City> newCity=new ArrayList<>();
		for(City dbCity:results) {
			if (dbCity.getCountryIdAndDescription() != null&&dbCity.getStateIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=countryRepository.getCountryIdAndDescription(dbCity.getCountryId(), dbCity.getLanguageId());
				IKeyValuePair iKeyValuePair1=stateRepository.getStateIdAndDescription(dbCity.getStateId(),dbCity.getLanguageId(), dbCity.getCountryId());
				if (iKeyValuePair != null) {
					dbCity.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbCity.setStateIdAndDescription(iKeyValuePair1.getStateId() + "-" + iKeyValuePair1.getDescription());
				}
			}
			newCity.add(dbCity);
		}
		return newCity;
	}
}
