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

import com.ustorage.api.master.model.customertype.AddCustomerType;
import com.ustorage.api.master.model.customertype.CustomerType;
import com.ustorage.api.master.model.customertype.UpdateCustomerType;
import com.ustorage.api.master.repository.CustomerTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerTypeService {
	
	@Autowired
	private CustomerTypeRepository customerTypeRepository;
	
	public List<CustomerType> getCustomerType () {
		List<CustomerType> customerTypeList =  customerTypeRepository.findAll();
		customerTypeList = customerTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return customerTypeList;
	}
	
	/**
	 * getCustomerType
	 * @param customerTypeId
	 * @return
	 */
	public CustomerType getCustomerType (String customerTypeId) {
		Optional<CustomerType> customerType = customerTypeRepository.findByCodeAndDeletionIndicator(customerTypeId, 0L);
		if (customerType.isEmpty()) {
			return null;
		}
		return customerType.get();
	}
	
	/**
	 * createCustomerType
	 * @param newCustomerType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CustomerType createCustomerType (AddCustomerType newCustomerType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		CustomerType dbCustomerType = new CustomerType();
		BeanUtils.copyProperties(newCustomerType, dbCustomerType, CommonUtils.getNullPropertyNames(newCustomerType));
		dbCustomerType.setDeletionIndicator(0L);
		dbCustomerType.setCreatedBy(loginUserId);
		dbCustomerType.setUpdatedBy(loginUserId);
		dbCustomerType.setCreatedOn(new Date());
		dbCustomerType.setUpdatedOn(new Date());
		return customerTypeRepository.save(dbCustomerType);
	}
	
	/**
	 * updateCustomerType
	 * @param customerTypeId
	 * @param loginUserId 
	 * @param updateCustomerType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CustomerType updateCustomerType (String code, String loginUserId, UpdateCustomerType updateCustomerType)
			throws IllegalAccessException, InvocationTargetException {
		CustomerType dbCustomerType = getCustomerType(code);
		BeanUtils.copyProperties(updateCustomerType, dbCustomerType, CommonUtils.getNullPropertyNames(updateCustomerType));
		dbCustomerType.setUpdatedBy(loginUserId);
		dbCustomerType.setUpdatedOn(new Date());
		return customerTypeRepository.save(dbCustomerType);
	}
	
	/**
	 * deleteCustomerType
	 * @param loginUserID 
	 * @param customertypeCode
	 */
	public void deleteCustomerType (String customertypeModuleId, String loginUserID) {
		CustomerType customertype = getCustomerType(customertypeModuleId);
		if (customertype != null) {
			customertype.setDeletionIndicator(1L);
			customertype.setUpdatedBy(loginUserID);
			customertype.setUpdatedOn(new Date());
			customerTypeRepository.save(customertype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + customertypeModuleId);
		}
	}
}
