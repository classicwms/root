package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.model.invoice.Invoice;
import com.ustorage.api.trans.model.invoice.FindInvoice;
import com.ustorage.api.trans.model.workorder.WoProcessedBy;
import com.ustorage.api.trans.repository.Specification.InvoiceSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.invoice.AddInvoice;
import com.ustorage.api.trans.model.invoice.Invoice;
import com.ustorage.api.trans.model.invoice.UpdateInvoice;
import com.ustorage.api.trans.repository.InvoiceRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceService {
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	public List<Invoice> getInvoice () {
		List<Invoice> invoiceList =  invoiceRepository.findAll();
		invoiceList = invoiceList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return invoiceList;
	}
	
	/**
	 * getInvoice
	 * @param invoiceId
	 * @return
	 */
	public Invoice getInvoice (String invoiceId) {
		Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberAndDeletionIndicator(invoiceId, 0L);
		if (invoice.isEmpty()) {
			return null;
		}
		return invoice.get();
	}
	
	/**
	 * createInvoice
	 * @param newInvoice
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Invoice createInvoice (AddInvoice newInvoice, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Invoice dbInvoice = new Invoice();
		BeanUtils.copyProperties(newInvoice, dbInvoice, CommonUtils.getNullPropertyNames(newInvoice));
		dbInvoice.setDeletionIndicator(0L);
		dbInvoice.setCreatedBy(loginUserId);
		dbInvoice.setUpdatedBy(loginUserId);
		dbInvoice.setCreatedOn(new Date());
		dbInvoice.setUpdatedOn(new Date());
		return invoiceRepository.save(dbInvoice);
	}
	
	/**
	 * updateInvoice
	 * @param invoiceNumber
	 * @param loginUserId 
	 * @param updateInvoice
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Invoice updateInvoice (String invoiceNumber, String loginUserId, UpdateInvoice updateInvoice)
			throws IllegalAccessException, InvocationTargetException {
		Invoice dbInvoice = getInvoice(invoiceNumber);
		BeanUtils.copyProperties(updateInvoice, dbInvoice, CommonUtils.getNullPropertyNames(updateInvoice));
		dbInvoice.setUpdatedBy(loginUserId);
		dbInvoice.setUpdatedOn(new Date());
		return invoiceRepository.save(dbInvoice);
	}
	
	/**
	 * deleteInvoice
	 * @param loginUserID 
	 * @param invoiceModuleId
	 */
	public void deleteInvoice (String invoiceModuleId, String loginUserID) {
		Invoice invoice = getInvoice(invoiceModuleId);
		if (invoice != null) {
			invoice.setDeletionIndicator(1L);
			invoice.setUpdatedBy(loginUserID);
			invoice.setUpdatedOn(new Date());
			invoiceRepository.save(invoice);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + invoiceModuleId);
		}
	}
	//find
	public List<Invoice> findInvoice(FindInvoice findInvoice) throws ParseException {

		InvoiceSpecification spec = new InvoiceSpecification(findInvoice);
		List<Invoice> results = invoiceRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<Invoice> dbInvoice = new ArrayList<>();
		for (Invoice newInvoice : results) {
			newInvoice.setCustomerType(invoiceRepository.getCustomerType(newInvoice.getInvoiceNumber()));
			BeanUtils.copyProperties(newInvoice, dbInvoice, CommonUtils.getNullPropertyNames(newInvoice));
			dbInvoice.add(newInvoice);
		}
		log.info("results: " + results);
		return dbInvoice;
	}
}
