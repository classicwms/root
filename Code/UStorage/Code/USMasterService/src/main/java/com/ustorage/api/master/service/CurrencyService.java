package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.currency.AddCurrency;
import com.ustorage.api.master.model.currency.Currency;
import com.ustorage.api.master.model.currency.UpdateCurrency;
import com.ustorage.api.master.repository.CurrencyRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CurrencyService {
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	public List<Currency> getCurrency () {
		List<Currency> currencyList =  currencyRepository.findAll();
		currencyList = currencyList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return currencyList;
	}
	
	/**
	 * getCurrency
	 * @param currencyId
	 * @return
	 */
	public Currency getCurrency (String currencyId) {
		Optional<Currency> currency = currencyRepository.findByCodeAndDeletionIndicator(currencyId, 0L);
		if (currency.isEmpty()) {
			return null;
		}
		return currency.get();
	}
	
	/**
	 * createCurrency
	 * @param newCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Currency createCurrency (AddCurrency newCurrency, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Currency dbCurrency = new Currency();
		BeanUtils.copyProperties(newCurrency, dbCurrency, CommonUtils.getNullPropertyNames(newCurrency));
		dbCurrency.setDeletionIndicator(0L);
		dbCurrency.setCreatedBy(loginUserId);
		dbCurrency.setUpdatedBy(loginUserId);
		dbCurrency.setCreatedOn(new Date());
		dbCurrency.setUpdatedOn(new Date());
		return currencyRepository.save(dbCurrency);
	}
	
	/**
	 * updateCurrency
	 * @param currencyId
	 * @param loginUserId 
	 * @param updateCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Currency updateCurrency (String code, String loginUserId, UpdateCurrency updateCurrency)
			throws IllegalAccessException, InvocationTargetException {
		Currency dbCurrency = getCurrency(code);
		BeanUtils.copyProperties(updateCurrency, dbCurrency, CommonUtils.getNullPropertyNames(updateCurrency));
		dbCurrency.setUpdatedBy(loginUserId);
		dbCurrency.setUpdatedOn(new Date());
		return currencyRepository.save(dbCurrency);
	}
	
	/**
	 * deleteCurrency
	 * @param loginUserID 
	 * @param currencyCode
	 */
	public void deleteCurrency (String currencyModuleId, String loginUserID) {
		Currency currency = getCurrency(currencyModuleId);
		if (currency != null) {
			currency.setDeletionIndicator(1L);
			currency.setUpdatedBy(loginUserID);
			currency.setUpdatedOn(new Date());
			currencyRepository.save(currency);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + currencyModuleId);
		}
	}
}
