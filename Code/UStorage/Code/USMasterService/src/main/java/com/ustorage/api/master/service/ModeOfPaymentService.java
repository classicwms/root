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

import com.ustorage.api.master.model.modeofpayment.AddModeOfPayment;
import com.ustorage.api.master.model.modeofpayment.ModeOfPayment;
import com.ustorage.api.master.model.modeofpayment.UpdateModeOfPayment;
import com.ustorage.api.master.repository.ModeOfPaymentRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModeOfPaymentService {
	
	@Autowired
	private ModeOfPaymentRepository modeOfPaymentRepository;
	
	public List<ModeOfPayment> getModeOfPayment () {
		List<ModeOfPayment> modeOfPaymentList =  modeOfPaymentRepository.findAll();
		modeOfPaymentList = modeOfPaymentList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return modeOfPaymentList;
	}
	
	/**
	 * getModeOfPayment
	 * @param modeOfPaymentId
	 * @return
	 */
	public ModeOfPayment getModeOfPayment (String modeOfPaymentId) {
		Optional<ModeOfPayment> modeOfPayment = modeOfPaymentRepository.findByCodeAndDeletionIndicator(modeOfPaymentId, 0L);
		if (modeOfPayment.isEmpty()) {
			return null;
		}
		return modeOfPayment.get();
	}
	
	/**
	 * createModeOfPayment
	 * @param newModeOfPayment
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ModeOfPayment createModeOfPayment (AddModeOfPayment newModeOfPayment, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		ModeOfPayment dbModeOfPayment = new ModeOfPayment();
		BeanUtils.copyProperties(newModeOfPayment, dbModeOfPayment, CommonUtils.getNullPropertyNames(newModeOfPayment));
		dbModeOfPayment.setDeletionIndicator(0L);
		dbModeOfPayment.setCreatedBy(loginUserId);
		dbModeOfPayment.setUpdatedBy(loginUserId);
		dbModeOfPayment.setCreatedOn(new Date());
		dbModeOfPayment.setUpdatedOn(new Date());
		return modeOfPaymentRepository.save(dbModeOfPayment);
	}
	
	/**
	 * updateModeOfPayment
	 * @param modeOfPaymentId
	 * @param loginUserId 
	 * @param updateModeOfPayment
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ModeOfPayment updateModeOfPayment (String code, String loginUserId, UpdateModeOfPayment updateModeOfPayment)
			throws IllegalAccessException, InvocationTargetException {
		ModeOfPayment dbModeOfPayment = getModeOfPayment(code);
		BeanUtils.copyProperties(updateModeOfPayment, dbModeOfPayment, CommonUtils.getNullPropertyNames(updateModeOfPayment));
		dbModeOfPayment.setUpdatedBy(loginUserId);
		dbModeOfPayment.setUpdatedOn(new Date());
		return modeOfPaymentRepository.save(dbModeOfPayment);
	}
	
	/**
	 * deleteModeOfPayment
	 * @param loginUserID 
	 * @param modeofpaymentCode
	 */
	public void deleteModeOfPayment (String modeofpaymentModuleId, String loginUserID) {
		ModeOfPayment modeofpayment = getModeOfPayment(modeofpaymentModuleId);
		if (modeofpayment != null) {
			modeofpayment.setDeletionIndicator(1L);
			modeofpayment.setUpdatedBy(loginUserID);
			modeofpayment.setUpdatedOn(new Date());
			modeOfPaymentRepository.save(modeofpayment);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + modeofpaymentModuleId);
		}
	}
}
