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

import com.ustorage.api.master.model.invoicedocumentstatus.AddInvoiceDocumentStatus;
import com.ustorage.api.master.model.invoicedocumentstatus.InvoiceDocumentStatus;
import com.ustorage.api.master.model.invoicedocumentstatus.UpdateInvoiceDocumentStatus;
import com.ustorage.api.master.repository.InvoiceDocumentStatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceDocumentStatusService {
	
	@Autowired
	private InvoiceDocumentStatusRepository invoiceDocumentStatusRepository;
	
	public List<InvoiceDocumentStatus> getInvoiceDocumentStatus () {
		List<InvoiceDocumentStatus> invoiceDocumentStatusList =  invoiceDocumentStatusRepository.findAll();
		invoiceDocumentStatusList = invoiceDocumentStatusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return invoiceDocumentStatusList;
	}
	
	/**
	 * getInvoiceDocumentStatus
	 * @param invoiceDocumentStatusId
	 * @return
	 */
	public InvoiceDocumentStatus getInvoiceDocumentStatus (String invoiceDocumentStatusId) {
		Optional<InvoiceDocumentStatus> invoiceDocumentStatus = invoiceDocumentStatusRepository.findByCodeAndDeletionIndicator(invoiceDocumentStatusId, 0L);
		if (invoiceDocumentStatus.isEmpty()) {
			return null;
		}
		return invoiceDocumentStatus.get();
	}
	
	/**
	 * createInvoiceDocumentStatus
	 * @param newInvoiceDocumentStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InvoiceDocumentStatus createInvoiceDocumentStatus (AddInvoiceDocumentStatus newInvoiceDocumentStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		InvoiceDocumentStatus dbInvoiceDocumentStatus = new InvoiceDocumentStatus();
		BeanUtils.copyProperties(newInvoiceDocumentStatus, dbInvoiceDocumentStatus, CommonUtils.getNullPropertyNames(newInvoiceDocumentStatus));
		dbInvoiceDocumentStatus.setDeletionIndicator(0L);
		dbInvoiceDocumentStatus.setCreatedBy(loginUserId);
		dbInvoiceDocumentStatus.setUpdatedBy(loginUserId);
		dbInvoiceDocumentStatus.setCreatedOn(new Date());
		dbInvoiceDocumentStatus.setUpdatedOn(new Date());
		return invoiceDocumentStatusRepository.save(dbInvoiceDocumentStatus);
	}
	
	/**
	 * updateInvoiceDocumentStatus
	 * @param invoiceDocumentStatusId
	 * @param loginUserId 
	 * @param updateInvoiceDocumentStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InvoiceDocumentStatus updateInvoiceDocumentStatus (String code, String loginUserId, UpdateInvoiceDocumentStatus updateInvoiceDocumentStatus)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceDocumentStatus dbInvoiceDocumentStatus = getInvoiceDocumentStatus(code);
		BeanUtils.copyProperties(updateInvoiceDocumentStatus, dbInvoiceDocumentStatus, CommonUtils.getNullPropertyNames(updateInvoiceDocumentStatus));
		dbInvoiceDocumentStatus.setUpdatedBy(loginUserId);
		dbInvoiceDocumentStatus.setUpdatedOn(new Date());
		return invoiceDocumentStatusRepository.save(dbInvoiceDocumentStatus);
	}
	
	/**
	 * deleteInvoiceDocumentStatus
	 * @param loginUserID 
	 * @param invoicedocumentstatusCode
	 */
	public void deleteInvoiceDocumentStatus (String invoicedocumentstatusModuleId, String loginUserID) {
		InvoiceDocumentStatus invoicedocumentstatus = getInvoiceDocumentStatus(invoicedocumentstatusModuleId);
		if (invoicedocumentstatus != null) {
			invoicedocumentstatus.setDeletionIndicator(1L);
			invoicedocumentstatus.setUpdatedBy(loginUserID);
			invoicedocumentstatus.setUpdatedOn(new Date());
			invoiceDocumentStatusRepository.save(invoicedocumentstatus);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + invoicedocumentstatusModuleId);
		}
	}
}
