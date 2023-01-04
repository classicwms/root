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

import com.ustorage.api.master.model.paymentterm.AddPaymentTerm;
import com.ustorage.api.master.model.paymentterm.PaymentTerm;
import com.ustorage.api.master.model.paymentterm.UpdatePaymentTerm;
import com.ustorage.api.master.repository.PaymentTermRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentTermService {
	
	@Autowired
	private PaymentTermRepository paymentTermRepository;
	
	public List<PaymentTerm> getPaymentTerm () {
		List<PaymentTerm> paymentTermList =  paymentTermRepository.findAll();
		paymentTermList = paymentTermList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return paymentTermList;
	}
	
	/**
	 * getPaymentTerm
	 * @param paymentTermId
	 * @return
	 */
	public PaymentTerm getPaymentTerm (String paymentTermId) {
		Optional<PaymentTerm> paymentTerm = paymentTermRepository.findByCodeAndDeletionIndicator(paymentTermId, 0L);
		if (paymentTerm.isEmpty()) {
			return null;
		}
		return paymentTerm.get();
	}
	
	/**
	 * createPaymentTerm
	 * @param newPaymentTerm
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentTerm createPaymentTerm (AddPaymentTerm newPaymentTerm, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		PaymentTerm dbPaymentTerm = new PaymentTerm();
		BeanUtils.copyProperties(newPaymentTerm, dbPaymentTerm, CommonUtils.getNullPropertyNames(newPaymentTerm));
		dbPaymentTerm.setDeletionIndicator(0L);
		dbPaymentTerm.setCreatedBy(loginUserId);
		dbPaymentTerm.setUpdatedBy(loginUserId);
		dbPaymentTerm.setCreatedOn(new Date());
		dbPaymentTerm.setUpdatedOn(new Date());
		return paymentTermRepository.save(dbPaymentTerm);
	}
	
	/**
	 * updatePaymentTerm
	 * @param paymentTermId
	 * @param loginUserId 
	 * @param updatePaymentTerm
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentTerm updatePaymentTerm (String code, String loginUserId, UpdatePaymentTerm updatePaymentTerm)
			throws IllegalAccessException, InvocationTargetException {
		PaymentTerm dbPaymentTerm = getPaymentTerm(code);
		BeanUtils.copyProperties(updatePaymentTerm, dbPaymentTerm, CommonUtils.getNullPropertyNames(updatePaymentTerm));
		dbPaymentTerm.setUpdatedBy(loginUserId);
		dbPaymentTerm.setUpdatedOn(new Date());
		return paymentTermRepository.save(dbPaymentTerm);
	}
	
	/**
	 * deletePaymentTerm
	 * @param loginUserID 
	 * @param paymenttermCode
	 */
	public void deletePaymentTerm (String paymenttermModuleId, String loginUserID) {
		PaymentTerm paymentterm = getPaymentTerm(paymenttermModuleId);
		if (paymentterm != null) {
			paymentterm.setDeletionIndicator(1L);
			paymentterm.setUpdatedBy(loginUserID);
			paymentterm.setUpdatedOn(new Date());
			paymentTermRepository.save(paymentterm);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymenttermModuleId);
		}
	}
}
