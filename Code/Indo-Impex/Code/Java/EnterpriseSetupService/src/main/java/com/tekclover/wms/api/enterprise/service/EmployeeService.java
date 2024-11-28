package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.employee.AddEmployee;
import com.tekclover.wms.api.enterprise.model.employee.Employee;
import com.tekclover.wms.api.enterprise.model.employee.UpdateEmployee;
import com.tekclover.wms.api.enterprise.repository.EmployeeRepository;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService extends BaseService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * getEmployees
     *
     * @return
     */
    public List<Employee> getEmployees() {
        try {
            List<Employee> employeeList = employeeRepository.findAll();
            log.info("employeeList : " + employeeList);
            employeeList = employeeList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return employeeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getEmployee
     *
     * @param employeeId
     * @return
     */
    public Employee getEmployee(String warehouseId, String employeeId, Long processId) {
        try {
            Employee employee = employeeRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIDAndEmployeeIdAndProcessIdAndDeletionIndicator(
                    getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, employeeId, processId, 0L);
            if (employee != null && employee.getDeletionIndicator() != null && employee.getDeletionIndicator() == 0) {
                return employee;
            } else {
                throw new BadRequestException("The given Employee ID : " + employeeId + " doesn't exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newEmployee
     * @param loginUserID
     * @return
     */
    public Employee createEmployee(AddEmployee newEmployee, String loginUserID) {
        try {
            Employee optEmployee =
                    employeeRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIDAndEmployeeIdAndProcessIdAndDeletionIndicator(
                            getLanguageId(), getCompanyCode(), getPlantId(),
                            newEmployee.getWarehouseId(), newEmployee.getEmployeeId(), newEmployee.getProcessId(), 0L);
            if (optEmployee != null) {
                throw new BadRequestException("The given values are getting duplicated.");
            }

            Employee dbEmployee = new Employee();
            BeanUtils.copyProperties(newEmployee, dbEmployee, CommonUtils.getNullPropertyNames(newEmployee));
            dbEmployee.setLanguageId(getLanguageId());
            dbEmployee.setCompanyCode(getCompanyCode());
            dbEmployee.setPlantId(getPlantId());
            dbEmployee.setDeletionIndicator(0L);
            dbEmployee.setCompanyId(getCompanyCode());
            dbEmployee.setCreatedBy(loginUserID);
            dbEmployee.setUpdatedBy(loginUserID);
            dbEmployee.setCreatedOn(new Date());
            dbEmployee.setUpdatedOn(new Date());
            return employeeRepository.save(dbEmployee);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param employeeId
     * @param processId
     * @param updateEmployee
     * @param loginUserID
     * @return
     */
    public Employee updateEmployee(String warehouseId, String employeeId, Long processId,
                                   UpdateEmployee updateEmployee, String loginUserID) {
        try {
            Employee dbEmployee = getEmployee(warehouseId, employeeId, processId);
            BeanUtils.copyProperties(updateEmployee, dbEmployee, CommonUtils.getNullPropertyNames(updateEmployee));
            dbEmployee.setUpdatedBy(loginUserID);
            dbEmployee.setUpdatedOn(new Date());
            return employeeRepository.save(dbEmployee);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param employeeId
     * @param processId
     * @param loginUserID
     */
    public void deleteEmployee(String warehouseId, String employeeId, Long processId, String loginUserID) {
        try {
            Employee employee = getEmployee(warehouseId, employeeId, processId);
            if (employee != null) {
                employee.setDeletionIndicator(1L);
                employee.setUpdatedBy(loginUserID);
                employee.setUpdatedOn(new Date());
                employeeRepository.save(employee);
            } else {
                throw new EntityNotFoundException(String.valueOf(employeeId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}