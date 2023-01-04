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

import com.ustorage.api.master.model.phase.AddPhase;
import com.ustorage.api.master.model.phase.Phase;
import com.ustorage.api.master.model.phase.UpdatePhase;
import com.ustorage.api.master.repository.PhaseRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PhaseService {
	
	@Autowired
	private PhaseRepository phaseRepository;
	
	public List<Phase> getPhase () {
		List<Phase> phaseList =  phaseRepository.findAll();
		phaseList = phaseList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return phaseList;
	}
	
	/**
	 * getPhase
	 * @param phaseId
	 * @return
	 */
	public Phase getPhase (String phaseId) {
		Optional<Phase> phase = phaseRepository.findByCodeAndDeletionIndicator(phaseId, 0L);
		if (phase.isEmpty()) {
			return null;
		}
		return phase.get();
	}
	
	/**
	 * createPhase
	 * @param newPhase
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Phase createPhase (AddPhase newPhase, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Phase dbPhase = new Phase();
		BeanUtils.copyProperties(newPhase, dbPhase, CommonUtils.getNullPropertyNames(newPhase));
		dbPhase.setDeletionIndicator(0L);
		dbPhase.setCreatedBy(loginUserId);
		dbPhase.setUpdatedBy(loginUserId);
		dbPhase.setCreatedOn(new Date());
		dbPhase.setUpdatedOn(new Date());
		return phaseRepository.save(dbPhase);
	}
	
	/**
	 * updatePhase
	 * @param phaseId
	 * @param loginUserId 
	 * @param updatePhase
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Phase updatePhase (String code, String loginUserId, UpdatePhase updatePhase)
			throws IllegalAccessException, InvocationTargetException {
		Phase dbPhase = getPhase(code);
		BeanUtils.copyProperties(updatePhase, dbPhase, CommonUtils.getNullPropertyNames(updatePhase));
		dbPhase.setUpdatedBy(loginUserId);
		dbPhase.setUpdatedOn(new Date());
		return phaseRepository.save(dbPhase);
	}
	
	/**
	 * deletePhase
	 * @param loginUserID 
	 * @param phaseCode
	 */
	public void deletePhase (String phaseModuleId, String loginUserID) {
		Phase phase = getPhase(phaseModuleId);
		if (phase != null) {
			phase.setDeletionIndicator(1L);
			phase.setUpdatedBy(loginUserID);
			phase.setUpdatedOn(new Date());
			phaseRepository.save(phase);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + phaseModuleId);
		}
	}
}
