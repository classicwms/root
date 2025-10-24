package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import com.tekclover.wms.api.idmaster.model.country.FindCountry;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.repository.Specification.CountrySpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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

	@Autowired
	private LanguageIdService languageIdService;

	/**
	 * getCompanies
	 * @return
	 */
	public List<Country> getCountrys () {
		return countryRepository.findAll();
	}

	/**
	 * getCountry
	 * @param countryId
	 * @return
	 */
	public Country getCountry (String countryId,String languageId) {
		log.info("country Id: " + countryId);
		Country country = countryRepository.findByCountryIdAndLanguageId(countryId,languageId).orElse(null);
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
	public Country createCountry (AddCountry newCountry,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Country dbCountry = new Country();
		Optional<Country> duplicateCountry = countryRepository.findByCountryIdAndLanguageId(newCountry.getCountryId(), newCountry.getLanguageId());
		if (!duplicateCountry.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			LanguageId languageId = languageIdService.getLanguageId(newCountry.getLanguageId());
			BeanUtils.copyProperties(newCountry, dbCountry, CommonUtils.getNullPropertyNames(newCountry));
			dbCountry.setDeletionIndicator(0L);
			dbCountry.setCreatedBy(loginUserID);
			dbCountry.setUpdatedBy(loginUserID);
			dbCountry.setCreatedOn(new Date());
			dbCountry.setUpdatedOn(new Date());
			return countryRepository.save(dbCountry);
		}
	}

	/**
	 * updateCountry
	 * @param countryId
	 * @param updateCountry
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Country updateCountry (String countryId,String languageId,String loginUserID,UpdateCountry updateCountry)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Country dbCountry = getCountry(countryId,languageId);
		BeanUtils.copyProperties(updateCountry, dbCountry, CommonUtils.getNullPropertyNames(updateCountry));
		dbCountry.setUpdatedBy(loginUserID);
		dbCountry.setDeletionIndicator(0L);
		dbCountry.setUpdatedOn(new Date());
		return countryRepository.save(dbCountry);
	}

	/**
	 * deleteCountry
	 * @param countryId
	 */
	public void deleteCountry (String countryId,String languageId) {
		Country country = getCountry(countryId,languageId);
		if ( country != null) {
			countryRepository.delete(country);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + countryId);
		}
	}
	//Find Country

	public List<Country> findCountry(FindCountry findCountry) throws ParseException {

		CountrySpecification spec = new CountrySpecification(findCountry);
		List<Country> results = countryRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
