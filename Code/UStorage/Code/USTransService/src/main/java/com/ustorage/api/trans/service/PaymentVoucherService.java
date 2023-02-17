package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.model.itemservice.ItemService;
import com.ustorage.api.trans.model.workorder.GWorkOrder;
import com.ustorage.api.trans.model.workorder.WoProcessedBy;
import com.ustorage.api.trans.model.workorder.WorkOrder;
import com.ustorage.api.trans.repository.ReferenceField3Repository;
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
	@Autowired
	private ReferenceField3Repository referenceField3Repository;
	@Autowired
	private ReferenceField3Service referenceField3Service;
	
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
	public GPaymentVoucher getPaymentVoucher (String paymentVoucherId) {
		Optional<PaymentVoucher> paymentVoucher = paymentVoucherRepository.findByVoucherIdAndDeletionIndicator(paymentVoucherId, 0L);
		if (paymentVoucher.isEmpty()) {
			return null;
		}
		GPaymentVoucher dbPaymentVoucher = new GPaymentVoucher();
		BeanUtils.copyProperties(paymentVoucher.get(),dbPaymentVoucher,CommonUtils.getNullPropertyNames(paymentVoucher.get()));
		if(dbPaymentVoucher.getStoreNumber()!=null){
			dbPaymentVoucher.setStoreName(paymentVoucherRepository.getStoreNumber(dbPaymentVoucher.getStoreNumber()));
		}
		dbPaymentVoucher.setReferenceField3(new ArrayList<>());
		for(ReferenceField3 dbReferenceField3 : paymentVoucher.get().getReferenceField3()){
			dbPaymentVoucher.getReferenceField3().add(dbReferenceField3.getProcessedBy());
		}
		return dbPaymentVoucher;
	}

	public PaymentVoucher getPaymntVoucher (String paymentVoucherId) {
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
		//return paymentVoucherRepository.save(dbPaymentVoucher);

		PaymentVoucher savedPaymentVoucher = paymentVoucherRepository.save(dbPaymentVoucher);

		savedPaymentVoucher.setReferenceField3(new HashSet<>());
		if(newPaymentVoucher.getReferenceField3()!=null){
			for(String newReferenceField3 : newPaymentVoucher.getReferenceField3()){
				ReferenceField3 dbReferenceField3 = new ReferenceField3();
				BeanUtils.copyProperties(newReferenceField3, dbReferenceField3, CommonUtils.getNullPropertyNames(newReferenceField3));
				dbReferenceField3.setDeletionIndicator(0L);
				dbReferenceField3.setCreatedBy(loginUserId);
				dbReferenceField3.setUpdatedBy(loginUserId);
				dbReferenceField3.setCreatedOn(new Date());
				dbReferenceField3.setUpdatedOn(new Date());
				dbReferenceField3.setVoucherId(savedPaymentVoucher.getVoucherId());
				dbReferenceField3.setProcessedBy(newReferenceField3);
				ReferenceField3 savedReferenceField3 = referenceField3Repository.save(dbReferenceField3);
				savedPaymentVoucher.getReferenceField3().add(savedReferenceField3);
			}
		}
		return savedPaymentVoucher;
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
		PaymentVoucher dbPaymentVoucher = getPaymntVoucher(voucherId);
		BeanUtils.copyProperties(updatePaymentVoucher, dbPaymentVoucher, CommonUtils.getNullPropertyNames(updatePaymentVoucher));
		dbPaymentVoucher.setUpdatedBy(loginUserId);
		dbPaymentVoucher.setUpdatedOn(new Date());
		//return paymentVoucherRepository.save(dbPaymentVoucher);

		PaymentVoucher savedPaymentVoucher = paymentVoucherRepository.save(dbPaymentVoucher);

		if(updatePaymentVoucher.getReferenceField3()!=null&&!updatePaymentVoucher.getReferenceField3().isEmpty()){
				if(referenceField3Service.getReferenceField3(voucherId)!=null){
					referenceField3Service.deleteReferenceField3(voucherId,loginUserId);
				}
			for (String newReferenceField3 : updatePaymentVoucher.getReferenceField3()) {
				ReferenceField3 dbReferenceField3 = new ReferenceField3();
				BeanUtils.copyProperties(newReferenceField3, dbReferenceField3, CommonUtils.getNullPropertyNames(newReferenceField3));
				dbReferenceField3.setDeletionIndicator(0L);
				dbReferenceField3.setCreatedBy(loginUserId);
				dbReferenceField3.setCreatedOn(new Date());
				dbReferenceField3.setUpdatedBy(loginUserId);
				dbReferenceField3.setUpdatedOn(new Date());
				dbReferenceField3.setVoucherId(savedPaymentVoucher.getVoucherId());
				dbReferenceField3.setProcessedBy(newReferenceField3);
				ReferenceField3 savedReferenceField3 = referenceField3Repository.save(dbReferenceField3);
				savedPaymentVoucher.getReferenceField3().add(savedReferenceField3);
			}
		}
		return savedPaymentVoucher;
	}
	
	/**
	 * deletePaymentVoucher
	 * @param loginUserID 
	 * @param voucherId
	 */
	public void deletePaymentVoucher (String voucherId, String loginUserID) {
		PaymentVoucher paymentvoucher = getPaymntVoucher(voucherId);
		if (paymentvoucher != null) {
			paymentvoucher.setDeletionIndicator(1L);
			paymentvoucher.setUpdatedBy(loginUserID);
			paymentvoucher.setUpdatedOn(new Date());
			paymentVoucherRepository.save(paymentvoucher);
			if(referenceField3Service.getReferenceField3(voucherId)!=null){
				referenceField3Service.deleteReferenceField3(voucherId,loginUserID);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + voucherId);
		}
	}

	//Find PaymentVoucher

	public List<GPaymentVoucher> findPaymentVoucher(FindPaymentVoucher findPaymentVoucher) throws ParseException {

		PaymentVoucherSpecification spec = new PaymentVoucherSpecification(findPaymentVoucher);
		List<PaymentVoucher> results = paymentVoucherRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		//return results;

		List<GPaymentVoucher> gPaymentVoucher = new ArrayList<>();
		for (PaymentVoucher dbPaymentVoucher : results) {
			GPaymentVoucher newGPaymentVoucher = new GPaymentVoucher();
			BeanUtils.copyProperties(dbPaymentVoucher, newGPaymentVoucher, CommonUtils.getNullPropertyNames(dbPaymentVoucher));
			if(newGPaymentVoucher.getStoreNumber()!=null){
				newGPaymentVoucher.setStoreName(paymentVoucherRepository.getStoreNumber(newGPaymentVoucher.getStoreNumber()));
			}
			newGPaymentVoucher.setReferenceField3(new ArrayList<>());
			for (ReferenceField3 newReferenceField3 : dbPaymentVoucher.getReferenceField3()) {
				newGPaymentVoucher.getReferenceField3().add(newReferenceField3.getProcessedBy());
			}
			gPaymentVoucher.add(newGPaymentVoucher);
		}
		return gPaymentVoucher;
	}
}
