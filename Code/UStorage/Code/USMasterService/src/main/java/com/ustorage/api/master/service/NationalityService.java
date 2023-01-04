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

import com.ustorage.api.master.model.nationality.AddNationality;
import com.ustorage.api.master.model.nationality.Nationality;
import com.ustorage.api.master.model.nationality.UpdateNationality;
import com.ustorage.api.master.repository.NationalityRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NationalityService {
	
	@Autowired
	private NationalityRepository nationalityRepository;
	
	public List<Nationality> getNationality () {
		List<Nationality> nationalityList =  nationalityRepository.findAll();
		nationalityList = nationalityList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return nationalityList;
	}
	
	/**
	 * getNationality
	 * @param nationalityId
	 * @return
	 */
	public Nationality getNationality (String nationalityId) {
		Optional<Nationality> nationality = nationalityRepository.findByCodeAndDeletionIndicator(nationalityId, 0L);
		if (nationality.isEmpty()) {
			return null;
		}
		return nationality.get();
	}
	
	/**
	 * createNationality
	 * @param newNationality
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Nationality createNationality (AddNationality newNationality, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Nationality dbNationality = new Nationality();
		BeanUtils.copyProperties(newNationality, dbNationality, CommonUtils.getNullPropertyNames(newNationality));
		dbNationality.setDeletionIndicator(0L);
		dbNationality.setCreatedBy(loginUserId);
		dbNationality.setUpdatedBy(loginUserId);
		dbNationality.setCreatedOn(new Date());
		dbNationality.setUpdatedOn(new Date());
		return nationalityRepository.save(dbNationality);
	}
	
	/**
	 * updateNationality
	 * @param nationalityId
	 * @param loginUserId 
	 * @param updateNationality
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Nationality updateNationality (String code, String loginUserId, UpdateNationality updateNationality)
			throws IllegalAccessException, InvocationTargetException {
		Nationality dbNationality = getNationality(code);
		BeanUtils.copyProperties(updateNationality, dbNationality, CommonUtils.getNullPropertyNames(updateNationality));
		dbNationality.setUpdatedBy(loginUserId);
		dbNationality.setUpdatedOn(new Date());
		return nationalityRepository.save(dbNationality);
	}
	
	/**
	 * deleteNationality
	 * @param loginUserID 
	 * @param nationalityCode
	 */
	public void deleteNationality (String nationalityModuleId, String loginUserID) {
		Nationality nationality = getNationality(nationalityModuleId);
		if (nationality != null) {
			nationality.setDeletionIndicator(1L);
			nationality.setUpdatedBy(loginUserID);
			nationality.setUpdatedOn(new Date());
			nationalityRepository.save(nationality);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + nationalityModuleId);
		}
	}
}
