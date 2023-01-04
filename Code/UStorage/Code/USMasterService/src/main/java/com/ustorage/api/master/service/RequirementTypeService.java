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

import com.ustorage.api.master.model.requirementtype.AddRequirementType;
import com.ustorage.api.master.model.requirementtype.RequirementType;
import com.ustorage.api.master.model.requirementtype.UpdateRequirementType;
import com.ustorage.api.master.repository.RequirementTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RequirementTypeService {
	
	@Autowired
	private RequirementTypeRepository requirementTypeRepository;
	
	public List<RequirementType> getRequirementType () {
		List<RequirementType> requirementTypeList =  requirementTypeRepository.findAll();
		requirementTypeList = requirementTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return requirementTypeList;
	}
	
	/**
	 * getRequirementType
	 * @param requirementTypeId
	 * @return
	 */
	public RequirementType getRequirementType (String requirementTypeId) {
		Optional<RequirementType> requirementType = requirementTypeRepository.findByCodeAndDeletionIndicator(requirementTypeId, 0L);
		if (requirementType.isEmpty()) {
			return null;
		}
		return requirementType.get();
	}
	
	/**
	 * createRequirementType
	 * @param newRequirementType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RequirementType createRequirementType (AddRequirementType newRequirementType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		RequirementType dbRequirementType = new RequirementType();
		BeanUtils.copyProperties(newRequirementType, dbRequirementType, CommonUtils.getNullPropertyNames(newRequirementType));
		dbRequirementType.setDeletionIndicator(0L);
		dbRequirementType.setCreatedBy(loginUserId);
		dbRequirementType.setUpdatedBy(loginUserId);
		dbRequirementType.setCreatedOn(new Date());
		dbRequirementType.setUpdatedOn(new Date());
		return requirementTypeRepository.save(dbRequirementType);
	}
	
	/**
	 * updateRequirementType
	 * @param requirementTypeId
	 * @param loginUserId 
	 * @param updateRequirementType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RequirementType updateRequirementType (String code, String loginUserId, UpdateRequirementType updateRequirementType)
			throws IllegalAccessException, InvocationTargetException {
		RequirementType dbRequirementType = getRequirementType(code);
		BeanUtils.copyProperties(updateRequirementType, dbRequirementType, CommonUtils.getNullPropertyNames(updateRequirementType));
		dbRequirementType.setUpdatedBy(loginUserId);
		dbRequirementType.setUpdatedOn(new Date());
		return requirementTypeRepository.save(dbRequirementType);
	}
	
	/**
	 * deleteRequirementType
	 * @param loginUserID 
	 * @param requirementtypeCode
	 */
	public void deleteRequirementType (String requirementtypeModuleId, String loginUserID) {
		RequirementType requirementtype = getRequirementType(requirementtypeModuleId);
		if (requirementtype != null) {
			requirementtype.setDeletionIndicator(1L);
			requirementtype.setUpdatedBy(loginUserID);
			requirementtype.setUpdatedOn(new Date());
			requirementTypeRepository.save(requirementtype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + requirementtypeModuleId);
		}
	}
}
