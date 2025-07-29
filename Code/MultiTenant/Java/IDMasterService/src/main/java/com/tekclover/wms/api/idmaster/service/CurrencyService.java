package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import com.tekclover.wms.api.idmaster.model.currency.FindCurrency;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.CurrencySpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.currency.AddCurrency;
import com.tekclover.wms.api.idmaster.model.currency.Currency;
import com.tekclover.wms.api.idmaster.model.currency.UpdateCurrency;
import com.tekclover.wms.api.idmaster.repository.CurrencyRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CurrencyService {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private LanguageIdService languageIdService;

	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getCompanies
	 * @return
	 */
	public List<Currency> getCompanies () {
		return currencyRepository.findAll();
	}

	/**
	 * getCurrency
	 * @param currencyId
	 * @return
	 */
	public Currency getCurrency (Long currencyId,String languageId) {
		log.info("currency Id: " + currencyId);
		Currency currency = currencyRepository.findByCurrencyIdAndLanguageId(currencyId,languageId).orElse(null);
		if (currency == null) {
			throw new BadRequestException("The given ID doesn't exist : " + currencyId);
		}

		return currency;
	}

	/**
	 * createCurrency
	 * @param newCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException//dbVertical.setCreatedBy(loginUserID);
	 * 		dbVertical.setUpdatedBy(loginUserID);
	 */
	public Currency createCurrency (AddCurrency newCurrency,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Currency dbCurrency = new Currency();
		Optional<Currency> duplicateCurrency = currencyRepository.findByCurrencyIdAndLanguageId(newCurrency.getCurrencyId(), newCurrency.getLanguageId());
		if (!duplicateCurrency.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			LanguageId dbLanguageId = languageIdService.getLanguageId(newCurrency.getLanguageId());
			BeanUtils.copyProperties(newCurrency, dbCurrency, CommonUtils.getNullPropertyNames(newCurrency));
			dbCurrency.setDeletionIndicator(0L);
			dbCurrency.setCreatedBy(loginUserID);
			dbCurrency.setUpdatedBy(loginUserID);
			dbCurrency.setCreatedOn(new Date());
			dbCurrency.setUpdatedOn(new Date());
			return currencyRepository.save(dbCurrency);
		}
	}

	/**
	 * updateCurrency
	 * @param currencyId
	 * @param updateCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Currency updateCurrency (Long currencyId,String languageId,String loginUserID,UpdateCurrency updateCurrency)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Currency dbCurrency = getCurrency(currencyId,languageId);
		BeanUtils.copyProperties(updateCurrency, dbCurrency, CommonUtils.getNullPropertyNames(updateCurrency));
		dbCurrency.setDeletionIndicator(0L);
		dbCurrency.setUpdatedBy(loginUserID);
		dbCurrency.setUpdatedOn(new Date());
		return currencyRepository.save(dbCurrency);
	}

	/**
	 * deleteCurrency
	 * @param currencyId
	 */
	public void deleteCurrency (Long currencyId,String languageId) {
		Currency currency = getCurrency(currencyId,languageId);
		if ( currency != null) {
			currencyRepository.delete(currency);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + currencyId);
		}
	}
	//Find Currency

	public List<Currency> findCurrency(FindCurrency findCurrency) throws ParseException {

		CurrencySpecification spec = new CurrencySpecification(findCurrency);
		List<Currency> results = currencyRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
