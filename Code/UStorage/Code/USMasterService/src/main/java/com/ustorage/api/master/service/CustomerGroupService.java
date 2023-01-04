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

import com.ustorage.api.master.model.customergroup.AddCustomerGroup;
import com.ustorage.api.master.model.customergroup.CustomerGroup;
import com.ustorage.api.master.model.customergroup.UpdateCustomerGroup;
import com.ustorage.api.master.repository.CustomerGroupRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerGroupService {
	
	@Autowired
	private CustomerGroupRepository customerGroupRepository;
	
	public List<CustomerGroup> getCustomerGroup () {
		List<CustomerGroup> customerGroupList =  customerGroupRepository.findAll();
		customerGroupList = customerGroupList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return customerGroupList;
	}
	
	/**
	 * getCustomerGroup
	 * @param customerGroupId
	 * @return
	 */
	public CustomerGroup getCustomerGroup (String customerGroupId) {
		Optional<CustomerGroup> customerGroup = customerGroupRepository.findByCodeAndDeletionIndicator(customerGroupId, 0L);
		if (customerGroup.isEmpty()) {
			return null;
		}
		return customerGroup.get();
	}
	
	/**
	 * createCustomerGroup
	 * @param newCustomerGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CustomerGroup createCustomerGroup (AddCustomerGroup newCustomerGroup, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		CustomerGroup dbCustomerGroup = new CustomerGroup();
		BeanUtils.copyProperties(newCustomerGroup, dbCustomerGroup, CommonUtils.getNullPropertyNames(newCustomerGroup));
		dbCustomerGroup.setDeletionIndicator(0L);
		dbCustomerGroup.setCreatedBy(loginUserId);
		dbCustomerGroup.setUpdatedBy(loginUserId);
		dbCustomerGroup.setCreatedOn(new Date());
		dbCustomerGroup.setUpdatedOn(new Date());
		return customerGroupRepository.save(dbCustomerGroup);
	}
	
	/**
	 * updateCustomerGroup
	 * @param customerGroupId
	 * @param loginUserId 
	 * @param updateCustomerGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CustomerGroup updateCustomerGroup (String code, String loginUserId, UpdateCustomerGroup updateCustomerGroup)
			throws IllegalAccessException, InvocationTargetException {
		CustomerGroup dbCustomerGroup = getCustomerGroup(code);
		BeanUtils.copyProperties(updateCustomerGroup, dbCustomerGroup, CommonUtils.getNullPropertyNames(updateCustomerGroup));
		dbCustomerGroup.setUpdatedBy(loginUserId);
		dbCustomerGroup.setUpdatedOn(new Date());
		return customerGroupRepository.save(dbCustomerGroup);
	}
	
	/**
	 * deleteCustomerGroup
	 * @param loginUserID 
	 * @param customergroupCode
	 */
	public void deleteCustomerGroup (String customergroupModuleId, String loginUserID) {
		CustomerGroup customergroup = getCustomerGroup(customergroupModuleId);
		if (customergroup != null) {
			customergroup.setDeletionIndicator(1L);
			customergroup.setUpdatedBy(loginUserID);
			customergroup.setUpdatedOn(new Date());
			customerGroupRepository.save(customergroup);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + customergroupModuleId);
		}
	}
}
