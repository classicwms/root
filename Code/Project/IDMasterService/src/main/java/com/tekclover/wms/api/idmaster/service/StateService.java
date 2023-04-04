package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.state.AddState;
import com.tekclover.wms.api.idmaster.model.state.State;
import com.tekclover.wms.api.idmaster.model.state.UpdateState;
import com.tekclover.wms.api.idmaster.repository.StateRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StateService {
	
	@Autowired
	private StateRepository stateRepository;
	
	/**
	 * getCompanies
	 * @return
	 */
	public List<State> getCompanies () {
		return stateRepository.findAll();
	}
	
	/**
	 * getState
	 * @param stateId
	 * @return
	 */
	public State getState (String stateId) {
		log.info("state Id: " + stateId);
		State state = stateRepository.findByStateId(stateId).orElse(null);
		if (state == null) {
			throw new BadRequestException("The given ID doesn't exist : " + stateId);
		}
		return state;
	}
	
	/**
	 * createState
	 * @param newState
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public State createState (AddState newState) 
			throws IllegalAccessException, InvocationTargetException {
		State dbState = new State();
		BeanUtils.copyProperties(newState, dbState, CommonUtils.getNullPropertyNames(newState));
		dbState.setDeletionIndicator(0L);
		dbState.setCreatedOn(new Date());
		dbState.setUpdatedOn(new Date());
		return stateRepository.save(dbState);
	}
	
	/**
	 * updateState
	 * @param stateId
	 * @param updateState
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public State updateState (String stateId, UpdateState updateState) 
			throws IllegalAccessException, InvocationTargetException {
		State dbState = getState(stateId);
		BeanUtils.copyProperties(updateState, dbState, CommonUtils.getNullPropertyNames(updateState));
		return stateRepository.save(dbState);
	}
	
	/**
	 * deleteState
	 * @param stateId
	 */
	public void deleteState (String stateId) {
		State state = getState(stateId);
		if ( state != null) {
			stateRepository.delete(state);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + stateId);
		}
	}
}
