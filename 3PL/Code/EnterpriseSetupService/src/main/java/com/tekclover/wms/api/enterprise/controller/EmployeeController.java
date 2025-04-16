package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.enterprise.model.employee.AddEmployee;
import com.tekclover.wms.api.enterprise.model.employee.Employee;
import com.tekclover.wms.api.enterprise.model.employee.UpdateEmployee;
import com.tekclover.wms.api.enterprise.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Employee "}, value = "Employee  Operations related to EmployeeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Employee ",description = "Operations related to Employee ")})
@RequestMapping("/employee")
@RestController
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
    @ApiOperation(response = Employee.class, value = "Get all Employee details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Employee> employeeList = employeeService.getEmployees();
		return new ResponseEntity<>(employeeList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = Employee.class, value = "Get a Employee") // label for swagger 
	@GetMapping("/{employeeId}")
	public ResponseEntity<?> getEmployee(@PathVariable String employeeId, @RequestParam String warehouseId, 
			@RequestParam Long processId) {
    	Employee employee = employeeService.getEmployee(warehouseId, employeeId, processId);
    	log.info("Employee : " + employee);
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
    
    @ApiOperation(response = Employee.class, value = "Create Employee") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEmployee(@Valid @RequestBody AddEmployee newEmployee, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Employee createdEmployee = employeeService.createEmployee(newEmployee, loginUserID);
		return new ResponseEntity<>(createdEmployee , HttpStatus.OK);
	}
    
    @ApiOperation(response = Employee.class, value = "Update Employee") // label for swagger
    @PatchMapping("/{employeeId}")
	public ResponseEntity<?> patchEmployee(@PathVariable String employeeId, @RequestParam String warehouseId, 
			@RequestParam Long processId, @Valid @RequestBody UpdateEmployee updateEmployee, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Employee createdEmployee = employeeService.updateEmployee(warehouseId, employeeId, processId, updateEmployee, loginUserID);
		return new ResponseEntity<>(createdEmployee , HttpStatus.OK);
	}
    
    @ApiOperation(response = Employee.class, value = "Delete Employee") // label for swagger
	@DeleteMapping("/{employeeId}")
	public ResponseEntity<?> deleteEmployee(@PathVariable String employeeId, @RequestParam String warehouseId, 
			@RequestParam Long processId, @RequestParam String loginUserID) throws ParseException {
    	employeeService.deleteEmployee(warehouseId, employeeId, processId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}