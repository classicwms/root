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

import com.ustorage.api.master.model.servicerendered.AddServiceRendered;
import com.ustorage.api.master.model.servicerendered.ServiceRendered;
import com.ustorage.api.master.model.servicerendered.UpdateServiceRendered;
import com.ustorage.api.master.repository.ServiceRenderedRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRenderedService {
	
	@Autowired
	private ServiceRenderedRepository serviceRenderedRepository;
	
	public List<ServiceRendered> getServiceRendered () {
		List<ServiceRendered> serviceRenderedList =  serviceRenderedRepository.findAll();
		serviceRenderedList = serviceRenderedList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return serviceRenderedList;
	}
	
	/**
	 * getServiceRendered
	 * @param serviceRenderedId
	 * @return
	 */
	public ServiceRendered getServiceRendered (String serviceRenderedId) {
		Optional<ServiceRendered> serviceRendered = serviceRenderedRepository.findByCodeAndDeletionIndicator(serviceRenderedId, 0L);
		if (serviceRendered.isEmpty()) {
			return null;
		}
		return serviceRendered.get();
	}
	
	/**
	 * createServiceRendered
	 * @param newServiceRendered
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ServiceRendered createServiceRendered (AddServiceRendered newServiceRendered, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		ServiceRendered dbServiceRendered = new ServiceRendered();
		BeanUtils.copyProperties(newServiceRendered, dbServiceRendered, CommonUtils.getNullPropertyNames(newServiceRendered));
		dbServiceRendered.setDeletionIndicator(0L);
		dbServiceRendered.setCreatedBy(loginUserId);
		dbServiceRendered.setUpdatedBy(loginUserId);
		dbServiceRendered.setCreatedOn(new Date());
		dbServiceRendered.setUpdatedOn(new Date());
		return serviceRenderedRepository.save(dbServiceRendered);
	}
	
	/**
	 * updateServiceRendered
	 * @param serviceRenderedId
	 * @param loginUserId 
	 * @param updateServiceRendered
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ServiceRendered updateServiceRendered (String code, String loginUserId, UpdateServiceRendered updateServiceRendered)
			throws IllegalAccessException, InvocationTargetException {
		ServiceRendered dbServiceRendered = getServiceRendered(code);
		BeanUtils.copyProperties(updateServiceRendered, dbServiceRendered, CommonUtils.getNullPropertyNames(updateServiceRendered));
		dbServiceRendered.setUpdatedBy(loginUserId);
		dbServiceRendered.setUpdatedOn(new Date());
		return serviceRenderedRepository.save(dbServiceRendered);
	}
	
	/**
	 * deleteServiceRendered
	 * @param loginUserID 
	 * @param servicerenderedCode
	 */
	public void deleteServiceRendered (String servicerenderedModuleId, String loginUserID) {
		ServiceRendered servicerendered = getServiceRendered(servicerenderedModuleId);
		if (servicerendered != null) {
			servicerendered.setDeletionIndicator(1L);
			servicerendered.setUpdatedBy(loginUserID);
			servicerendered.setUpdatedOn(new Date());
			serviceRenderedRepository.save(servicerendered);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + servicerenderedModuleId);
		}
	}
}
