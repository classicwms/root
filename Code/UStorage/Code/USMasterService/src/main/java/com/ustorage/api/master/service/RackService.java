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

import com.ustorage.api.master.model.rack.AddRack;
import com.ustorage.api.master.model.rack.Rack;
import com.ustorage.api.master.model.rack.UpdateRack;
import com.ustorage.api.master.repository.RackRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RackService {
	
	@Autowired
	private RackRepository rackRepository;
	
	public List<Rack> getRack () {
		List<Rack> rackList =  rackRepository.findAll();
		rackList = rackList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return rackList;
	}
	
	/**
	 * getRack
	 * @param rackId
	 * @return
	 */
	public Rack getRack (String rackId) {
		Optional<Rack> rack = rackRepository.findByCodeAndDeletionIndicator(rackId, 0L);
		if (rack.isEmpty()) {
			return null;
		}
		return rack.get();
	}
	
	/**
	 * createRack
	 * @param newRack
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Rack createRack (AddRack newRack, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Rack dbRack = new Rack();
		BeanUtils.copyProperties(newRack, dbRack, CommonUtils.getNullPropertyNames(newRack));
		dbRack.setDeletionIndicator(0L);
		dbRack.setCreatedBy(loginUserId);
		dbRack.setUpdatedBy(loginUserId);
		dbRack.setCreatedOn(new Date());
		dbRack.setUpdatedOn(new Date());
		return rackRepository.save(dbRack);
	}
	
	/**
	 * updateRack
	 * @param rackId
	 * @param loginUserId 
	 * @param updateRack
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Rack updateRack (String code, String loginUserId, UpdateRack updateRack)
			throws IllegalAccessException, InvocationTargetException {
		Rack dbRack = getRack(code);
		BeanUtils.copyProperties(updateRack, dbRack, CommonUtils.getNullPropertyNames(updateRack));
		dbRack.setUpdatedBy(loginUserId);
		dbRack.setUpdatedOn(new Date());
		return rackRepository.save(dbRack);
	}
	
	/**
	 * deleteRack
	 * @param loginUserID 
	 * @param rackCode
	 */
	public void deleteRack (String rackModuleId, String loginUserID) {
		Rack rack = getRack(rackModuleId);
		if (rack != null) {
			rack.setDeletionIndicator(1L);
			rack.setUpdatedBy(loginUserID);
			rack.setUpdatedOn(new Date());
			rackRepository.save(rack);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + rackModuleId);
		}
	}
}
