package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

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
	public Currency getCurrency (Long currencyId) {
		log.info("currency Id: " + currencyId);
		Currency currency = currencyRepository.findByCurrencyId(currencyId).orElse(null);
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
	 * @throws InvocationTargetException
	 */
	public Currency createCurrency (AddCurrency newCurrency) 
			throws IllegalAccessException, InvocationTargetException {
		Currency dbCurrency = new Currency();
		BeanUtils.copyProperties(newCurrency, dbCurrency, CommonUtils.getNullPropertyNames(newCurrency));
		dbCurrency.setDeletionIndicator(0L);
		dbCurrency.setCreatedOn(new Date());
		dbCurrency.setUpdatedOn(new Date());
		return currencyRepository.save(dbCurrency);
	}
	
	/**
	 * updateCurrency
	 * @param currencyId
	 * @param updateCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Currency updateCurrency (Long currencyId, UpdateCurrency updateCurrency) 
			throws IllegalAccessException, InvocationTargetException {
		Currency dbCurrency = getCurrency(currencyId);
		BeanUtils.copyProperties(updateCurrency, dbCurrency, CommonUtils.getNullPropertyNames(updateCurrency));
		dbCurrency.setDeletionIndicator(0L);
		dbCurrency.setUpdatedOn(new Date());
		return currencyRepository.save(dbCurrency);
	}
	
	/**
	 * deleteCurrency
	 * @param currencyId
	 */
	public void deleteCurrency (Long currencyId) {
		Currency currency = getCurrency(currencyId);
		if ( currency != null) {
			currencyRepository.delete(currency);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + currencyId);
		}
	}
}
