package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.country.Country;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.spanid.SpanId;
import com.tekclover.wms.api.idmaster.model.state.FindState;
import com.tekclover.wms.api.idmaster.repository.CountryRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.StateSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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

	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private CountryService countryService;

	/**
	 * getCompanies
	 * @return
	 */
	public List<State> getStates () {
		List<State> stateList =  stateRepository.findAll();
		stateList = stateList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<State>newState=new ArrayList<>();
		for(State dbState:stateList) {
			if (dbState.getCountryIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair=countryRepository.getCountryIdAndDescription(dbState.getCountryId(), dbState.getLanguageId());
				if(iKeyValuePair != null) {
					dbState.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
				}
			}
			newState.add(dbState);
		}
		return newState;
	}

	/**
	 * getState
	 * @param stateId
	 * @return
	 */
	public State getState (String stateId,String countryId,String languageId) {
		log.info("state Id: " + stateId);
		Optional<State> dbState = stateRepository.findByStateIdAndCountryIdAndLanguageId(
				stateId,countryId,languageId);
		if (dbState.isEmpty()) {
			throw new BadRequestException("The given ID doesn't exist : " + stateId);
		}
		State newState = new State();
		BeanUtils.copyProperties(dbState.get(),newState, CommonUtils.getNullPropertyNames(dbState));
		IKeyValuePair iKeyValuePair=countryRepository.getCountryIdAndDescription(countryId,languageId);
		if(iKeyValuePair!=null) {
			newState.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
		}
		return newState;
	}

	/**
	 * createState
	 * @param newState
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public State createState (AddState newState,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		State dbState = new State();
		Optional<State> duplicateState = stateRepository.findByStateIdAndCountryIdAndLanguageId(newState.getStateId(), newState.getCountryId(), newState.getLanguageId());
		if (!duplicateState.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Country dbCountry=countryService.getCountry(newState.getCountryId(), newState.getLanguageId());
			BeanUtils.copyProperties(newState, dbState, CommonUtils.getNullPropertyNames(newState));
			dbState.setDeletionIndicator(0L);
			dbState.setCountryIdAndDescription(dbCountry.getCountryId()+"-"+dbCountry.getCountryName());
			dbState.setCreatedBy(loginUserID);
			dbState.setUpdatedBy(loginUserID);
			dbState.setCreatedOn(new Date());
			dbState.setUpdatedOn(new Date());
			return stateRepository.save(dbState);
		}
	}

	/**
	 * updateState
	 * @param stateId
	 * @param updateState
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public State updateState (String stateId,String countryId,String languageId,String loginUserID,UpdateState updateState)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		State dbState = getState(stateId,countryId,languageId);
		BeanUtils.copyProperties(updateState, dbState, CommonUtils.getNullPropertyNames(updateState));
		dbState.setUpdatedBy(loginUserID);
		dbState.setUpdatedOn(new Date());
		return stateRepository.save(dbState);
	}

	/**
	 * deleteState
	 * @param stateId
	 */
	public void deleteState (String stateId,String countryId,String languageId) {
		State state = getState(stateId,countryId,languageId);
		if ( state != null) {
			stateRepository.delete(state);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + stateId);
		}
	}
	//Find State

	public List<State> findState(FindState findState) throws ParseException {

		StateSpecification spec = new StateSpecification(findState);
		List<State> results = stateRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<State>newState=new ArrayList<>();
		for(State dbState:results) {
			if (dbState.getCountryIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = countryRepository.getCountryIdAndDescription(dbState.getCountryId(), dbState.getLanguageId());
				if (iKeyValuePair != null) {
					dbState.setCountryIdAndDescription(iKeyValuePair.getCountryId() + "-" + iKeyValuePair.getDescription());
				}
			}
			newState.add(dbState);
		}
		return newState;
	}
}
