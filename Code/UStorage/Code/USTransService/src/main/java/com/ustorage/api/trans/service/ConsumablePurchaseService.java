package com.ustorage.api.trans.service;

import com.ustorage.api.trans.controller.exception.BadRequestException;
import com.ustorage.api.trans.model.consumablepurchase.*;

import com.ustorage.api.trans.repository.ConsumablePurchaseRepository;

import com.ustorage.api.trans.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsumablePurchaseService {
	
	@Autowired
	private ConsumablePurchaseRepository consumablePurchaseRepository;

	@Autowired
	private ConsumablePurchaseService consumablePurchaseService;
	
	// GETALL
	public List<ConsumablePurchase> getConsumablePurchase () {
		List<ConsumablePurchase> consumablePurchaseList =  consumablePurchaseRepository.findAll();
		consumablePurchaseList = consumablePurchaseList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return consumablePurchaseList;
	}

	/**
	 * get
	 * @param itemCode
	 * @param receiptNo
	 * @return
	 */
	public ConsumablePurchase getConsumablePurchase (String itemCode, String receiptNo) {
		Optional<ConsumablePurchase> consumablePurchase = consumablePurchaseRepository.findByItemCodeAndReceiptNoAndDeletionIndicator(
				itemCode, receiptNo, 0L);
		if (consumablePurchase != null) {
			return consumablePurchase.get();
		}
		throw new BadRequestException("The given Consumable Purchase ID : " + itemCode + " doesn't exist.");
	}

	/**
	 * get
	 * @param itemCode
	 * @return
	 */
	public List<ConsumablePurchase> getConsumablePurchase (String itemCode) {
		List<ConsumablePurchase> consumablePurchase =
				consumablePurchaseRepository.findByItemCodeAndDeletionIndicator(itemCode, 0L);
		if (consumablePurchase != null) {
			return consumablePurchase;
		}
		throw new BadRequestException("The given Consumable Purchase Id : " + itemCode + " doesn't exist.");
	}

	/**
	 * createConsumablePurchase
	 * @param newConsumablePurchase
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */

	public List<ConsumablePurchase> createConsumablePurchase (List<AddConsumablePurchase> newConsumablePurchase, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<ConsumablePurchase> consumablePurchase = new ArrayList<>();

		for (AddConsumablePurchase addConsumablePurchase : newConsumablePurchase) {
			ConsumablePurchase dbConsumablePurchase = new ConsumablePurchase();

			BeanUtils.copyProperties(addConsumablePurchase, dbConsumablePurchase, CommonUtils.getNullPropertyNames(addConsumablePurchase));
			ConsumablePurchase savedConsumablePurchase = consumablePurchaseRepository.save(dbConsumablePurchase);
			dbConsumablePurchase.setReceiptNo(savedConsumablePurchase.getReceiptNo());

			dbConsumablePurchase.setDeletionIndicator(0L);
			dbConsumablePurchase.setCreatedBy(loginUserID);
			dbConsumablePurchase.setUpdatedBy(loginUserID);
			dbConsumablePurchase.setCreatedOn(new Date());
			dbConsumablePurchase.setUpdatedOn(new Date());
			ConsumablePurchase createdConsumablePurchase = consumablePurchaseRepository.save(dbConsumablePurchase);
			consumablePurchase.add(createdConsumablePurchase);
		}
		return consumablePurchase;
	}

	
	/**
	 * updateConsumablePurchase
	 * @param updateConsumablePurchases
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ConsumablePurchase> updateConsumablePurchase (List<UpdateConsumablePurchase> updateConsumablePurchases, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<AddConsumablePurchase> addConsumablePurchases = new ArrayList<>();
		for (UpdateConsumablePurchase updateConsumablePurchase : updateConsumablePurchases) {
			deleteConsumablePurchase (updateConsumablePurchase.getItemCode(), updateConsumablePurchase.getReceiptNo(),loginUserID);
			AddConsumablePurchase newConsumablePurchase = new AddConsumablePurchase();
			BeanUtils.copyProperties(updateConsumablePurchase, newConsumablePurchase, CommonUtils.getNullPropertyNames(updateConsumablePurchase));
			addConsumablePurchases.add(newConsumablePurchase);
		}
		return createConsumablePurchase (addConsumablePurchases, loginUserID);
	}

	/**
	 *
	 * @param itemCode
	 * @param receiptNo
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void deleteConsumablePurchase (String itemCode, String receiptNo,String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		if (receiptNo != null) {
			ConsumablePurchase dbConsumablePurchase = getConsumablePurchase (itemCode, receiptNo);
			consumablePurchaseRepository.delete(dbConsumablePurchase);
			log.info("Record got deleted");
		} else {
			deleteConsumablePurchase (itemCode);
		}
	}

	/**
	 *
	 * @param itemCode
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void deleteConsumablePurchase (String itemCode)
			throws IllegalAccessException, InvocationTargetException {
		List<ConsumablePurchase> dbConsumablePurchase = getConsumablePurchase (itemCode);
		consumablePurchaseRepository.delete((ConsumablePurchase) dbConsumablePurchase);
		log.info("Record got deleted");
	}
	
	/**
	 * deleteConsumablePurchase
	 * @param consumablepurchaseId
	 * @param loginUserID
	 */
	public void deleteConsumablePurchase (String consumablepurchaseId, String loginUserID) {
		List<ConsumablePurchase> consumablepurchase = getConsumablePurchase(consumablepurchaseId);
		if (consumablepurchase != null) {
			for(ConsumablePurchase consumablepurchases:consumablepurchase){
				consumablepurchases.setDeletionIndicator(1L);
				consumablepurchases.setUpdatedBy(loginUserID);
				consumablepurchases.setUpdatedOn(new Date());
				consumablePurchaseRepository.save(consumablepurchases);
			}

		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + consumablepurchaseId);
		}
	}
}
