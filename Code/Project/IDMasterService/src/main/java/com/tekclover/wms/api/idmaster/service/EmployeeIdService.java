package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.employeeid.*;
import com.tekclover.wms.api.idmaster.repository.EmployeeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
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
public class EmployeeIdService extends BaseService {
	
	@Autowired
	private EmployeeIdRepository employeeIdRepository;
	
	/**
	 * getEmployeeIds
	 * @return
	 */
	public List<EmployeeId> getEmployeeIds () {
		List<EmployeeId> employeeIdList =  employeeIdRepository.findAll();
		employeeIdList = employeeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return employeeIdList;
	}
	
	/**
	 * getEmployeeId
	 * @param employeeId
	 * @return
	 */
	public EmployeeId getEmployeeId (String warehouseId, String employeeId) {
		Optional<EmployeeId> dbEmployeeId = 
				employeeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndEmployeeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								employeeId,
								getLanguageId(),
								0L
								);
		if (dbEmployeeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"employeeId - " + employeeId +
						" doesn't exist.");
			
		} 
		return dbEmployeeId.get();
	}
	
	/**
	 * createEmployeeId
	 * @param newEmployeeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EmployeeId createEmployeeId (AddEmployeeId newEmployeeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		EmployeeId dbEmployeeId = new EmployeeId();
		log.info("newEmployeeId : " + newEmployeeId);
		BeanUtils.copyProperties(newEmployeeId, dbEmployeeId, CommonUtils.getNullPropertyNames(newEmployeeId));
		dbEmployeeId.setCompanyCodeId(getCompanyCode());
		dbEmployeeId.setPlantId(getPlantId());
		dbEmployeeId.setDeletionIndicator(0L);
		dbEmployeeId.setCreatedBy(loginUserID);
		dbEmployeeId.setUpdatedBy(loginUserID);
		dbEmployeeId.setCreatedOn(new Date());
		dbEmployeeId.setUpdatedOn(new Date());
		return employeeIdRepository.save(dbEmployeeId);
	}
	
	/**
	 * updateEmployeeId
	 * @param loginUserID
	 * @param employeeId
	 * @param updateEmployeeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EmployeeId updateEmployeeId (String warehouseId, String employeeId, String loginUserID,
			UpdateEmployeeId updateEmployeeId) 
			throws IllegalAccessException, InvocationTargetException {
		EmployeeId dbEmployeeId = getEmployeeId( warehouseId, employeeId);
		BeanUtils.copyProperties(updateEmployeeId, dbEmployeeId, CommonUtils.getNullPropertyNames(updateEmployeeId));
		dbEmployeeId.setUpdatedBy(loginUserID);
		dbEmployeeId.setUpdatedOn(new Date());
		return employeeIdRepository.save(dbEmployeeId);
	}
	
	/**
	 * deleteEmployeeId
	 * @param loginUserID 
	 * @param employeeId
	 */
	public void deleteEmployeeId (String warehouseId, String employeeId, String loginUserID) {
		EmployeeId dbEmployeeId = getEmployeeId( warehouseId, employeeId);
		if ( dbEmployeeId != null) {
			dbEmployeeId.setDeletionIndicator(1L);
			dbEmployeeId.setUpdatedBy(loginUserID);
			employeeIdRepository.save(dbEmployeeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + employeeId);
		}
	}
}
