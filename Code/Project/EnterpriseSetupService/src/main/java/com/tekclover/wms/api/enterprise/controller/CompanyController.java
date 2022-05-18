package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
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

import com.tekclover.wms.api.enterprise.model.company.AddCompany;
import com.tekclover.wms.api.enterprise.model.company.Company;
import com.tekclover.wms.api.enterprise.model.company.SearchCompany;
import com.tekclover.wms.api.enterprise.model.company.UpdateCompany;
import com.tekclover.wms.api.enterprise.service.CompanyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Company "}, value = "Company  Operations related to CompanyController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Company ",description = "Operations related to Company ")})
@RequestMapping("/company")
@RestController
public class CompanyController {
	
	@Autowired
	CompanyService companyService;
	
    @ApiOperation(response = Company.class, value = "Get all Company details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Company> companyList = companyService.getCompanys();
		return new ResponseEntity<>(companyList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = Company.class, value = "Get a Company") 
	@GetMapping("/{companyId}")
	public ResponseEntity<?> getCompany(@PathVariable String companyId) {
	   	Company company = companyService.getCompany(companyId);
	   	log.info("Company : " + company);
		return new ResponseEntity<>(company, HttpStatus.OK);
	}
    
    @ApiOperation(response = Company.class, value = "Search Company") // label for swagger
	@PostMapping("/findCompany")
	public List<Company> findCompany(@RequestBody SearchCompany searchCompany)
			throws Exception {
		return companyService.findCompany(searchCompany);
	}
    
    @ApiOperation(response = Company.class, value = "Create Company") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCompany(@Valid @RequestBody AddCompany newCompany, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Company createdCompany = companyService.createCompany(newCompany, loginUserID);
		return new ResponseEntity<>(createdCompany , HttpStatus.OK);
	}
    
    @ApiOperation(response = Company.class, value = "Update Company") // label for swagger
    @PatchMapping("/{companyId}")
	public ResponseEntity<?> patchCompany(@PathVariable String companyId,
			@Valid @RequestBody UpdateCompany updateCompany, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Company createdCompany = companyService.updateCompany(companyId, updateCompany, loginUserID);
		return new ResponseEntity<>(createdCompany , HttpStatus.OK);
	}
    
    @ApiOperation(response = Company.class, value = "Delete Company") // label for swagger
	@DeleteMapping("/{companyId}")
	public ResponseEntity<?> deleteCompany(@PathVariable String companyId, @RequestParam String loginUserID) {
    	companyService.deleteCompany(companyId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}