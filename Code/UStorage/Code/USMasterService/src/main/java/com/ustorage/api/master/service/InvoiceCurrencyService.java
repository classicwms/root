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

import com.ustorage.api.master.model.invoicecurrency.AddInvoiceCurrency;
import com.ustorage.api.master.model.invoicecurrency.InvoiceCurrency;
import com.ustorage.api.master.model.invoicecurrency.UpdateInvoiceCurrency;
import com.ustorage.api.master.repository.InvoiceCurrencyRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceCurrencyService {
	
	@Autowired
	private InvoiceCurrencyRepository invoiceCurrencyRepository;
	
	public List<InvoiceCurrency> getInvoiceCurrency () {
		List<InvoiceCurrency> invoiceCurrencyList =  invoiceCurrencyRepository.findAll();
		invoiceCurrencyList = invoiceCurrencyList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return invoiceCurrencyList;
	}
	
	/**
	 * getInvoiceCurrency
	 * @param invoiceCurrencyId
	 * @return
	 */
	public InvoiceCurrency getInvoiceCurrency (String invoiceCurrencyId) {
		Optional<InvoiceCurrency> invoiceCurrency = invoiceCurrencyRepository.findByCodeAndDeletionIndicator(invoiceCurrencyId, 0L);
		if (invoiceCurrency.isEmpty()) {
			return null;
		}
		return invoiceCurrency.get();
	}
	
	/**
	 * createInvoiceCurrency
	 * @param newInvoiceCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InvoiceCurrency createInvoiceCurrency (AddInvoiceCurrency newInvoiceCurrency, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		InvoiceCurrency dbInvoiceCurrency = new InvoiceCurrency();
		BeanUtils.copyProperties(newInvoiceCurrency, dbInvoiceCurrency, CommonUtils.getNullPropertyNames(newInvoiceCurrency));
		dbInvoiceCurrency.setDeletionIndicator(0L);
		dbInvoiceCurrency.setCreatedBy(loginUserId);
		dbInvoiceCurrency.setUpdatedBy(loginUserId);
		dbInvoiceCurrency.setCreatedOn(new Date());
		dbInvoiceCurrency.setUpdatedOn(new Date());
		return invoiceCurrencyRepository.save(dbInvoiceCurrency);
	}
	
	/**
	 * updateInvoiceCurrency
	 * @param invoiceCurrencyId
	 * @param loginUserId 
	 * @param updateInvoiceCurrency
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InvoiceCurrency updateInvoiceCurrency (String code, String loginUserId, UpdateInvoiceCurrency updateInvoiceCurrency)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceCurrency dbInvoiceCurrency = getInvoiceCurrency(code);
		BeanUtils.copyProperties(updateInvoiceCurrency, dbInvoiceCurrency, CommonUtils.getNullPropertyNames(updateInvoiceCurrency));
		dbInvoiceCurrency.setUpdatedBy(loginUserId);
		dbInvoiceCurrency.setUpdatedOn(new Date());
		return invoiceCurrencyRepository.save(dbInvoiceCurrency);
	}
	
	/**
	 * deleteInvoiceCurrency
	 * @param loginUserID 
	 * @param invoicecurrencyCode
	 */
	public void deleteInvoiceCurrency (String invoicecurrencyModuleId, String loginUserID) {
		InvoiceCurrency invoicecurrency = getInvoiceCurrency(invoicecurrencyModuleId);
		if (invoicecurrency != null) {
			invoicecurrency.setDeletionIndicator(1L);
			invoicecurrency.setUpdatedBy(loginUserID);
			invoicecurrency.setUpdatedOn(new Date());
			invoiceCurrencyRepository.save(invoicecurrency);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + invoicecurrencyModuleId);
		}
	}
}
