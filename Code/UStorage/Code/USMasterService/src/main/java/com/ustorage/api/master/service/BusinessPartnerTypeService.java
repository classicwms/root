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

import com.ustorage.api.master.model.businesspartnertype.AddBusinessPartnerType;
import com.ustorage.api.master.model.businesspartnertype.BusinessPartnerType;
import com.ustorage.api.master.model.businesspartnertype.UpdateBusinessPartnerType;
import com.ustorage.api.master.repository.BusinessPartnerTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BusinessPartnerTypeService {
	
	@Autowired
	private BusinessPartnerTypeRepository businessPartnerTypeRepository;
	
	public List<BusinessPartnerType> getBusinessPartnerType () {
		List<BusinessPartnerType> businessPartnerTypeList =  businessPartnerTypeRepository.findAll();
		businessPartnerTypeList = businessPartnerTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return businessPartnerTypeList;
	}
	
	/**
	 * getBusinessPartnerType
	 * @param businessPartnerTypeId
	 * @return
	 */
	public BusinessPartnerType getBusinessPartnerType (String businessPartnerTypeId) {
		Optional<BusinessPartnerType> businessPartnerType = businessPartnerTypeRepository.findByCodeAndDeletionIndicator(businessPartnerTypeId, 0L);
		if (businessPartnerType.isEmpty()) {
			return null;
		}
		return businessPartnerType.get();
	}
	
	/**
	 * createBusinessPartnerType
	 * @param newBusinessPartnerType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BusinessPartnerType createBusinessPartnerType (AddBusinessPartnerType newBusinessPartnerType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		BusinessPartnerType dbBusinessPartnerType = new BusinessPartnerType();
		BeanUtils.copyProperties(newBusinessPartnerType, dbBusinessPartnerType, CommonUtils.getNullPropertyNames(newBusinessPartnerType));
		dbBusinessPartnerType.setDeletionIndicator(0L);
		dbBusinessPartnerType.setCreatedBy(loginUserId);
		dbBusinessPartnerType.setUpdatedBy(loginUserId);
		dbBusinessPartnerType.setCreatedOn(new Date());
		dbBusinessPartnerType.setUpdatedOn(new Date());
		return businessPartnerTypeRepository.save(dbBusinessPartnerType);
	}
	
	/**
	 * updateBusinessPartnerType
	 * @param businessPartnerTypeId
	 * @param loginUserId 
	 * @param updateBusinessPartnerType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BusinessPartnerType updateBusinessPartnerType (String code, String loginUserId, UpdateBusinessPartnerType updateBusinessPartnerType)
			throws IllegalAccessException, InvocationTargetException {
		BusinessPartnerType dbBusinessPartnerType = getBusinessPartnerType(code);
		BeanUtils.copyProperties(updateBusinessPartnerType, dbBusinessPartnerType, CommonUtils.getNullPropertyNames(updateBusinessPartnerType));
		dbBusinessPartnerType.setUpdatedBy(loginUserId);
		dbBusinessPartnerType.setUpdatedOn(new Date());
		return businessPartnerTypeRepository.save(dbBusinessPartnerType);
	}
	
	/**
	 * deleteBusinessPartnerType
	 * @param loginUserID 
	 * @param businesspartnertypeCode
	 */
	public void deleteBusinessPartnerType (String businesspartnertypeModuleId, String loginUserID) {
		BusinessPartnerType businesspartnertype = getBusinessPartnerType(businesspartnertypeModuleId);
		if (businesspartnertype != null) {
			businesspartnertype.setDeletionIndicator(1L);
			businesspartnertype.setUpdatedBy(loginUserID);
			businesspartnertype.setUpdatedOn(new Date());
			businessPartnerTypeRepository.save(businesspartnertype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + businesspartnertypeModuleId);
		}
	}
}
