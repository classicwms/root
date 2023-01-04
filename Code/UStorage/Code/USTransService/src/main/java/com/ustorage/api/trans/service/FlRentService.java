package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.flrent.*;

import com.ustorage.api.trans.repository.FlRentRepository;
import com.ustorage.api.trans.repository.Specification.FlRentSpecification;
import com.ustorage.api.trans.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlRentService {
	
	@Autowired
	private FlRentRepository flRentRepository;
	
	public List<FlRent> getFlRent () {
		List<FlRent> flRentList =  flRentRepository.findAll();
		flRentList = flRentList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return flRentList;
	}
	
	/**
	 * getFlRent
	 * @param flRentId
	 * @return
	 */
	public FlRent getFlRent (String flRentId) {
		Optional<FlRent> flRent = flRentRepository.findByItemCodeAndDeletionIndicator(flRentId, 0L);
		if (flRent.isEmpty()) {
			return null;
		}
		return flRent.get();
	}
	
	/**
	 * createFlRent
	 * @param newFlRent
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FlRent createFlRent (AddFlRent newFlRent, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		FlRent dbFlRent = new FlRent();
		BeanUtils.copyProperties(newFlRent, dbFlRent, CommonUtils.getNullPropertyNames(newFlRent));
		dbFlRent.setDeletionIndicator(0L);
		dbFlRent.setCreatedBy(loginUserId);
		dbFlRent.setUpdatedBy(loginUserId);
		dbFlRent.setCreatedOn(new Date());
		dbFlRent.setUpdatedOn(new Date());
		return flRentRepository.save(dbFlRent);
	}
	
	/**
	 * updateFlRent
	 * @param itemCode
	 * @param loginUserId 
	 * @param updateFlRent
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FlRent updateFlRent (String itemCode, String loginUserId, UpdateFlRent updateFlRent)
			throws IllegalAccessException, InvocationTargetException {
		FlRent dbFlRent = getFlRent(itemCode);
		BeanUtils.copyProperties(updateFlRent, dbFlRent, CommonUtils.getNullPropertyNames(updateFlRent));
		dbFlRent.setUpdatedBy(loginUserId);
		dbFlRent.setUpdatedOn(new Date());
		return flRentRepository.save(dbFlRent);
	}
	
	/**
	 * deleteFlRent
	 * @param loginUserID 
	 * @param flrentModuleId
	 */
	public void deleteFlRent (String flrentModuleId, String loginUserID) {
		FlRent flrent = getFlRent(flrentModuleId);
		if (flrent != null) {
			flrent.setDeletionIndicator(1L);
			flrent.setUpdatedBy(loginUserID);
			flrent.setUpdatedOn(new Date());
			flRentRepository.save(flrent);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + flrentModuleId);
		}
	}

	//Find FlRent

	public List<FlRent> findFlRent(FindFlRent findFlRent) throws ParseException {

		FlRentSpecification spec = new FlRentSpecification(findFlRent);
		List<FlRent> results = flRentRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}
