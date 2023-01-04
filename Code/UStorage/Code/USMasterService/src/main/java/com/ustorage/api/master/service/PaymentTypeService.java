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

import com.ustorage.api.master.model.paymenttype.AddPaymentType;
import com.ustorage.api.master.model.paymenttype.PaymentType;
import com.ustorage.api.master.model.paymenttype.UpdatePaymentType;
import com.ustorage.api.master.repository.PaymentTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentTypeService {
	
	@Autowired
	private PaymentTypeRepository paymentTypeRepository;
	
	public List<PaymentType> getPaymentType () {
		List<PaymentType> paymentTypeList =  paymentTypeRepository.findAll();
		paymentTypeList = paymentTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return paymentTypeList;
	}
	
	/**
	 * getPaymentType
	 * @param paymentTypeId
	 * @return
	 */
	public PaymentType getPaymentType (String paymentTypeId) {
		Optional<PaymentType> paymentType = paymentTypeRepository.findByCodeAndDeletionIndicator(paymentTypeId, 0L);
		if (paymentType.isEmpty()) {
			return null;
		}
		return paymentType.get();
	}
	
	/**
	 * createPaymentType
	 * @param newPaymentType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentType createPaymentType (AddPaymentType newPaymentType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		PaymentType dbPaymentType = new PaymentType();
		BeanUtils.copyProperties(newPaymentType, dbPaymentType, CommonUtils.getNullPropertyNames(newPaymentType));
		dbPaymentType.setDeletionIndicator(0L);
		dbPaymentType.setCreatedBy(loginUserId);
		dbPaymentType.setUpdatedBy(loginUserId);
		dbPaymentType.setCreatedOn(new Date());
		dbPaymentType.setUpdatedOn(new Date());
		return paymentTypeRepository.save(dbPaymentType);
	}
	
	/**
	 * updatePaymentType
	 * @param paymentTypeId
	 * @param loginUserId 
	 * @param updatePaymentType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentType updatePaymentType (String code, String loginUserId, UpdatePaymentType updatePaymentType)
			throws IllegalAccessException, InvocationTargetException {
		PaymentType dbPaymentType = getPaymentType(code);
		BeanUtils.copyProperties(updatePaymentType, dbPaymentType, CommonUtils.getNullPropertyNames(updatePaymentType));
		dbPaymentType.setUpdatedBy(loginUserId);
		dbPaymentType.setUpdatedOn(new Date());
		return paymentTypeRepository.save(dbPaymentType);
	}
	
	/**
	 * deletePaymentType
	 * @param loginUserID 
	 * @param paymenttypeCode
	 */
	public void deletePaymentType (String paymenttypeModuleId, String loginUserID) {
		PaymentType paymenttype = getPaymentType(paymenttypeModuleId);
		if (paymenttype != null) {
			paymenttype.setDeletionIndicator(1L);
			paymenttype.setUpdatedBy(loginUserID);
			paymenttype.setUpdatedOn(new Date());
			paymentTypeRepository.save(paymenttype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymenttypeModuleId);
		}
	}
}
