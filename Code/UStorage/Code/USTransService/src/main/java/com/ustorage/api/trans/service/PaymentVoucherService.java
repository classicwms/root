package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.repository.Specification.PaymentVoucherSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.paymentvoucher.*;

import com.ustorage.api.trans.repository.PaymentVoucherRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentVoucherService {
	
	@Autowired
	private PaymentVoucherRepository paymentVoucherRepository;
	
	public List<PaymentVoucher> getPaymentVoucher () {
		List<PaymentVoucher> paymentVoucherList =  paymentVoucherRepository.findAll();
		paymentVoucherList = paymentVoucherList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return paymentVoucherList;
	}
	
	/**
	 * getPaymentVoucher
	 * @param paymentVoucherId
	 * @return
	 */
	public PaymentVoucher getPaymentVoucher (String paymentVoucherId) {
		Optional<PaymentVoucher> paymentVoucher = paymentVoucherRepository.findByVoucherIdAndDeletionIndicator(paymentVoucherId, 0L);
		if (paymentVoucher.isEmpty()) {
			return null;
		}
		return paymentVoucher.get();
	}
	
	/**
	 * createPaymentVoucher
	 * @param newPaymentVoucher
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentVoucher createPaymentVoucher (AddPaymentVoucher newPaymentVoucher, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		PaymentVoucher dbPaymentVoucher = new PaymentVoucher();
		BeanUtils.copyProperties(newPaymentVoucher, dbPaymentVoucher, CommonUtils.getNullPropertyNames(newPaymentVoucher));
		dbPaymentVoucher.setDeletionIndicator(0L);
		dbPaymentVoucher.setCreatedBy(loginUserId);
		dbPaymentVoucher.setUpdatedBy(loginUserId);
		dbPaymentVoucher.setCreatedOn(new Date());
		dbPaymentVoucher.setUpdatedOn(new Date());
		return paymentVoucherRepository.save(dbPaymentVoucher);
	}
	
	/**
	 * updatePaymentVoucher
	 * @param voucherId
	 * @param loginUserId 
	 * @param updatePaymentVoucher
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PaymentVoucher updatePaymentVoucher (String voucherId, String loginUserId, UpdatePaymentVoucher updatePaymentVoucher)
			throws IllegalAccessException, InvocationTargetException {
		PaymentVoucher dbPaymentVoucher = getPaymentVoucher(voucherId);
		BeanUtils.copyProperties(updatePaymentVoucher, dbPaymentVoucher, CommonUtils.getNullPropertyNames(updatePaymentVoucher));
		dbPaymentVoucher.setUpdatedBy(loginUserId);
		dbPaymentVoucher.setUpdatedOn(new Date());
		return paymentVoucherRepository.save(dbPaymentVoucher);
	}
	
	/**
	 * deletePaymentVoucher
	 * @param loginUserID 
	 * @param paymentvoucherModuleId
	 */
	public void deletePaymentVoucher (String paymentvoucherModuleId, String loginUserID) {
		PaymentVoucher paymentvoucher = getPaymentVoucher(paymentvoucherModuleId);
		if (paymentvoucher != null) {
			paymentvoucher.setDeletionIndicator(1L);
			paymentvoucher.setUpdatedBy(loginUserID);
			paymentvoucher.setUpdatedOn(new Date());
			paymentVoucherRepository.save(paymentvoucher);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymentvoucherModuleId);
		}
	}

	//Find PaymentVoucher

	public List<PaymentVoucher> findPaymentVoucher(FindPaymentVoucher findPaymentVoucher) throws ParseException {

		PaymentVoucherSpecification spec = new PaymentVoucherSpecification(findPaymentVoucher);
		List<PaymentVoucher> results = paymentVoucherRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
