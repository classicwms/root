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

import com.ustorage.api.master.model.paymentperiod.AddPaymentPeriod;
import com.ustorage.api.master.model.paymentperiod.PaymentPeriod;
import com.ustorage.api.master.model.paymentperiod.UpdatePaymentPeriod;
import com.ustorage.api.master.repository.PaymentPeriodRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentPeriodService {
	
	@Autowired
	private PaymentPeriodRepository paymentPeriodRepository;
	
	public List<PaymentPeriod> getPaymentPeriod () {
		List<PaymentPeriod> paymentPeriodList =  paymentPeriodRepository.findAll();
		paymentPeriodList = paymentPeriodList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return paymentPeriodList;
	}
	
	/**
	 * getPaymentPeriod
	 * @param paymentPeriodId
	 * @return
	 */
	public PaymentPeriod getPaymentPeriod (String paymentPeriodId) {
		Optional<PaymentPeriod> paymentPeriod = paymentPeriodRepository.findByCodeAndDeletionIndicator(paymentPeriodId, 0L);
		if (paymentPeriod.isEmpty()) {
			return null;
		}
		return paymentPeriod.get();
	}
	
	/**
	 * createPaymentPeriod
	 * @param newPaymentPeriod
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentPeriod createPaymentPeriod (AddPaymentPeriod newPaymentPeriod, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		PaymentPeriod dbPaymentPeriod = new PaymentPeriod();
		BeanUtils.copyProperties(newPaymentPeriod, dbPaymentPeriod, CommonUtils.getNullPropertyNames(newPaymentPeriod));
		dbPaymentPeriod.setDeletionIndicator(0L);
		dbPaymentPeriod.setCreatedBy(loginUserId);
		dbPaymentPeriod.setUpdatedBy(loginUserId);
		dbPaymentPeriod.setCreatedOn(new Date());
		dbPaymentPeriod.setUpdatedOn(new Date());
		return paymentPeriodRepository.save(dbPaymentPeriod);
	}
	
	/**
	 * updatePaymentPeriod
	 * @param paymentPeriodId
	 * @param loginUserId 
	 * @param updatePaymentPeriod
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentPeriod updatePaymentPeriod (String code, String loginUserId, UpdatePaymentPeriod updatePaymentPeriod)
			throws IllegalAccessException, InvocationTargetException {
		PaymentPeriod dbPaymentPeriod = getPaymentPeriod(code);
		BeanUtils.copyProperties(updatePaymentPeriod, dbPaymentPeriod, CommonUtils.getNullPropertyNames(updatePaymentPeriod));
		dbPaymentPeriod.setUpdatedBy(loginUserId);
		dbPaymentPeriod.setUpdatedOn(new Date());
		return paymentPeriodRepository.save(dbPaymentPeriod);
	}
	
	/**
	 * deletePaymentPeriod
	 * @param loginUserID 
	 * @param paymentperiodCode
	 */
	public void deletePaymentPeriod (String paymentperiodModuleId, String loginUserID) {
		PaymentPeriod paymentperiod = getPaymentPeriod(paymentperiodModuleId);
		if (paymentperiod != null) {
			paymentperiod.setDeletionIndicator(1L);
			paymentperiod.setUpdatedBy(loginUserID);
			paymentperiod.setUpdatedOn(new Date());
			paymentPeriodRepository.save(paymentperiod);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymentperiodModuleId);
		}
	}
}
