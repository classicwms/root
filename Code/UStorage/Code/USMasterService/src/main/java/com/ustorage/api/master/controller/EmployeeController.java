package com.ustorage.api.master.controller;

import com.ustorage.api.master.model.employee.*;
import com.ustorage.api.master.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Employee" }, value = "Employee Operations related to EmployeeController") 
@SwaggerDefinition(tags = { @Tag(name = "Employee", description = "Operations related to Employee") })
@RequestMapping("/employee")
@RestController
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@ApiOperation(response = Employee.class, value = "Get all Employee details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Employee> employeeList = employeeService.getEmployee();
		return new ResponseEntity<>(employeeList, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Get a Employee") // label for swagger
	@GetMapping("/{employeeId}")
	public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
		Employee dbEmployee = employeeService.getEmployee(employeeId);
		log.info("Employee : " + dbEmployee);
		return new ResponseEntity<>(dbEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Create Employee") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEmployee(@Valid @RequestBody AddEmployee newEmployee,
			@RequestParam String loginUserID) throws Exception {
		Employee createdEmployee = employeeService.createEmployee(newEmployee, loginUserID);
		return new ResponseEntity<>(createdEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Update Employee") // label for swagger
	@PatchMapping("/{employeeId}")
	public ResponseEntity<?> patchEmployee(@PathVariable String employeeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateEmployee updateEmployee)
			throws IllegalAccessException, InvocationTargetException {
		Employee updatedEmployee = employeeService.updateEmployee(employeeId, loginUserID,
				updateEmployee);
		return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Delete Employee") // label for swagger
	@DeleteMapping("/{employeeId}")
	public ResponseEntity<?> deleteEmployee(@PathVariable String employeeId, @RequestParam String loginUserID) {
		employeeService.deleteEmployee(employeeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
