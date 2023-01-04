package com.ustorage.api.master.service;

import com.ustorage.api.master.model.employee.*;

import com.ustorage.api.master.repository.EmployeeRepository;
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
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	public List<Employee> getEmployee () {
		List<Employee> employeeList =  employeeRepository.findAll();
		employeeList = employeeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return employeeList;
	}
	
	/**
	 * getEmployee
	 * @param employeeId
	 * @return
	 */
	public Employee getEmployee (String employeeId) {
		Optional<Employee> employee = employeeRepository.findByEmployeeCodeAndDeletionIndicator(employeeId, 0L);
		if (employee.isEmpty()) {
			return null;
		}
		return employee.get();
	}
	
	/**
	 * createEmployee
	 * @param newEmployee
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Employee createEmployee (AddEmployee newEmployee, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Employee dbEmployee = new Employee();
		BeanUtils.copyProperties(newEmployee, dbEmployee, CommonUtils.getNullPropertyNames(newEmployee));
		dbEmployee.setDeletionIndicator(0L);
		dbEmployee.setCreatedBy(loginUserId);
		dbEmployee.setUpdatedBy(loginUserId);
		dbEmployee.setCreatedOn(new Date());
		dbEmployee.setUpdatedOn(new Date());
		return employeeRepository.save(dbEmployee);
	}
	
	/**
	 * updateEmployee
	 * @param employeeCode
	 * @param loginUserId 
	 * @param updateEmployee
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Employee updateEmployee (String employeeCode, String loginUserId, UpdateEmployee updateEmployee)
			throws IllegalAccessException, InvocationTargetException {
		Employee dbEmployee = getEmployee(employeeCode);
		BeanUtils.copyProperties(updateEmployee, dbEmployee, CommonUtils.getNullPropertyNames(updateEmployee));
		dbEmployee.setUpdatedBy(loginUserId);
		dbEmployee.setUpdatedOn(new Date());
		return employeeRepository.save(dbEmployee);
	}
	
	/**
	 * deleteEmployee
	 * @param loginUserID 
	 * @param employeeModuleId
	 */
	public void deleteEmployee (String employeeModuleId, String loginUserID) {
		Employee employee = getEmployee(employeeModuleId);
		if (employee != null) {
			employee.setDeletionIndicator(1L);
			employee.setUpdatedBy(loginUserID);
			employee.setUpdatedOn(new Date());
			employeeRepository.save(employee);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + employeeModuleId);
		}
	}
}
