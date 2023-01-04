package com.ustorage.api.master.service;

import com.ustorage.api.master.model.sbu.*;

import com.ustorage.api.master.repository.SbuRepository;
import com.ustorage.api.master.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SbuService {
	
	@Autowired
	private SbuRepository sbuRepository;
	
	public List<Sbu> getSbu () {
		List<Sbu> sbuList =  sbuRepository.findAll();
		sbuList = sbuList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return sbuList;
	}
	
	/**
	 * getSbu
	 * @param sbuId
	 * @return
	 */
	public Sbu getSbu (String sbuId) {
		Optional<Sbu> sbu = sbuRepository.findByCodeAndDeletionIndicator(sbuId, 0L);
		if (sbu.isEmpty()) {
			return null;
		}
		return sbu.get();
	}
	
	/**
	 * createSbu
	 * @param newSbu
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Sbu createSbu (AddSbu newSbu, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Sbu dbSbu = new Sbu();
		BeanUtils.copyProperties(newSbu, dbSbu, CommonUtils.getNullPropertyNames(newSbu));
		dbSbu.setDeletionIndicator(0L);
		dbSbu.setCreatedBy(loginUserId);
		dbSbu.setUpdatedBy(loginUserId);
		dbSbu.setCreatedOn(new Date());
		dbSbu.setUpdatedOn(new Date());
		return sbuRepository.save(dbSbu);
	}
	
	/**
	 * updateSbu
	 * @param code
	 * @param loginUserId 
	 * @param updateSbu
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Sbu updateSbu (String code, String loginUserId, UpdateSbu updateSbu)
			throws IllegalAccessException, InvocationTargetException {
		Sbu dbSbu = getSbu(code);
		BeanUtils.copyProperties(updateSbu, dbSbu, CommonUtils.getNullPropertyNames(updateSbu));
		dbSbu.setUpdatedBy(loginUserId);
		dbSbu.setUpdatedOn(new Date());
		return sbuRepository.save(dbSbu);
	}
	
	/**
	 * deleteSbu
	 * @param loginUserID 
	 * @param sbuModuleId
	 */
	public void deleteSbu (String sbuModuleId, String loginUserID) {
		Sbu sbu = getSbu(sbuModuleId);
		if (sbu != null) {
			sbu.setDeletionIndicator(1L);
			sbu.setUpdatedBy(loginUserID);
			sbu.setUpdatedOn(new Date());
			sbuRepository.save(sbu);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + sbuModuleId);
		}
	}
}
