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

import com.ustorage.api.master.model.paymentmode.AddPaymentMode;
import com.ustorage.api.master.model.paymentmode.PaymentMode;
import com.ustorage.api.master.model.paymentmode.UpdatePaymentMode;
import com.ustorage.api.master.repository.PaymentModeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentModeService {
	
	@Autowired
	private PaymentModeRepository paymentModeRepository;
	
	public List<PaymentMode> getPaymentMode () {
		List<PaymentMode> paymentModeList =  paymentModeRepository.findAll();
		paymentModeList = paymentModeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return paymentModeList;
	}
	
	/**
	 * getPaymentMode
	 * @param paymentModeId
	 * @return
	 */
	public PaymentMode getPaymentMode (String paymentModeId) {
		Optional<PaymentMode> paymentMode = paymentModeRepository.findByCodeAndDeletionIndicator(paymentModeId, 0L);
		if (paymentMode.isEmpty()) {
			return null;
		}
		return paymentMode.get();
	}
	
	/**
	 * createPaymentMode
	 * @param newPaymentMode
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentMode createPaymentMode (AddPaymentMode newPaymentMode, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		PaymentMode dbPaymentMode = new PaymentMode();
		BeanUtils.copyProperties(newPaymentMode, dbPaymentMode, CommonUtils.getNullPropertyNames(newPaymentMode));
		dbPaymentMode.setDeletionIndicator(0L);
		dbPaymentMode.setCreatedBy(loginUserId);
		dbPaymentMode.setUpdatedBy(loginUserId);
		dbPaymentMode.setCreatedOn(new Date());
		dbPaymentMode.setUpdatedOn(new Date());
		return paymentModeRepository.save(dbPaymentMode);
	}
	
	/**
	 * updatePaymentMode
	 * @param paymentModeId
	 * @param loginUserId 
	 * @param updatePaymentMode
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentMode updatePaymentMode (String code, String loginUserId, UpdatePaymentMode updatePaymentMode)
			throws IllegalAccessException, InvocationTargetException {
		PaymentMode dbPaymentMode = getPaymentMode(code);
		BeanUtils.copyProperties(updatePaymentMode, dbPaymentMode, CommonUtils.getNullPropertyNames(updatePaymentMode));
		dbPaymentMode.setUpdatedBy(loginUserId);
		dbPaymentMode.setUpdatedOn(new Date());
		return paymentModeRepository.save(dbPaymentMode);
	}
	
	/**
	 * deletePaymentMode
	 * @param loginUserID 
	 * @param paymentmodeCode
	 */
	public void deletePaymentMode (String paymentmodeModuleId, String loginUserID) {
		PaymentMode paymentmode = getPaymentMode(paymentmodeModuleId);
		if (paymentmode != null) {
			paymentmode.setDeletionIndicator(1L);
			paymentmode.setUpdatedBy(loginUserID);
			paymentmode.setUpdatedOn(new Date());
			paymentModeRepository.save(paymentmode);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymentmodeModuleId);
		}
	}
}
