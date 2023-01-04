package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.repository.Specification.ConsumablesSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.consumables.*;

import com.ustorage.api.trans.repository.ConsumablesRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsumablesService {
	
	@Autowired
	private ConsumablesRepository consumablesRepository;
	
	public List<Consumables> getConsumables () {
		List<Consumables> consumablesList =  consumablesRepository.findAll();
		consumablesList = consumablesList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return consumablesList;
	}
	
	/**
	 * getConsumables
	 * @param consumablesId
	 * @return
	 */
	public Consumables getConsumables (String consumablesId) {
		Optional<Consumables> consumables = consumablesRepository.findByItemCodeAndDeletionIndicator(consumablesId, 0L);
		if (consumables.isEmpty()) {
			return null;
		}
		return consumables.get();
	}
	
	/**
	 * createConsumables
	 * @param newConsumables
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Consumables createConsumables (AddConsumables newConsumables, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Consumables dbConsumables = new Consumables();
		BeanUtils.copyProperties(newConsumables, dbConsumables, CommonUtils.getNullPropertyNames(newConsumables));
		dbConsumables.setDeletionIndicator(0L);
		dbConsumables.setCreatedBy(loginUserId);
		dbConsumables.setUpdatedBy(loginUserId);
		dbConsumables.setCreatedOn(new Date());
		dbConsumables.setUpdatedOn(new Date());
		return consumablesRepository.save(dbConsumables);
	}
	
	/**
	 * updateConsumables
	 * @param itemCode
	 * @param loginUserId 
	 * @param updateConsumables
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Consumables updateConsumables (String itemCode, String loginUserId, UpdateConsumables updateConsumables)
			throws IllegalAccessException, InvocationTargetException {
		Consumables dbConsumables = getConsumables(itemCode);
		BeanUtils.copyProperties(updateConsumables, dbConsumables, CommonUtils.getNullPropertyNames(updateConsumables));
		dbConsumables.setUpdatedBy(loginUserId);
		dbConsumables.setUpdatedOn(new Date());
		return consumablesRepository.save(dbConsumables);
	}
	
	/**
	 * deleteConsumables
	 * @param loginUserID 
	 * @param consumablesModuleId
	 */
	public void deleteConsumables (String consumablesModuleId, String loginUserID) {
		Consumables consumables = getConsumables(consumablesModuleId);
		if (consumables != null) {
			consumables.setDeletionIndicator(1L);
			consumables.setUpdatedBy(loginUserID);
			consumables.setUpdatedOn(new Date());
			consumablesRepository.save(consumables);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + consumablesModuleId);
		}
	}

	//Find Consumables

	public List<Consumables> findConsumables(FindConsumables findConsumables) throws ParseException {

		ConsumablesSpecification spec = new ConsumablesSpecification(findConsumables);
		List<Consumables> results = consumablesRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
