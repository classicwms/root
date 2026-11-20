package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.model.company.AddCompany;
import com.tekclover.wms.api.masters.model.company.Company;
import com.tekclover.wms.api.masters.model.company.SearchCompany;
import com.tekclover.wms.api.masters.model.company.UpdateCompany;
import com.tekclover.wms.api.masters.enterprise.service.CompanyService;
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
import java.text.ParseException;
import java.util.List;

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
	public ResponseEntity<?> getCompany(@PathVariable String companyId,@RequestParam String languageId) {
	   	Company company = companyService.getCompany(companyId,languageId);
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Company createdCompany = companyService.createCompany(newCompany, loginUserID);
		return new ResponseEntity<>(createdCompany , HttpStatus.OK);
	}
    
    @ApiOperation(response = Company.class, value = "Update Company") // label for swagger
    @PatchMapping("/{companyId}")
	public ResponseEntity<?> patchCompany(@PathVariable String companyId,@RequestParam String languageId,
			@Valid @RequestBody UpdateCompany updateCompany, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Company createdCompany = companyService.updateCompany(companyId,languageId, updateCompany, loginUserID);
		return new ResponseEntity<>(createdCompany , HttpStatus.OK);
	}
    
    @ApiOperation(response = Company.class, value = "Delete Company") // label for swagger
	@DeleteMapping("/{companyId}")
	public ResponseEntity<?> deleteCompany(@PathVariable String companyId,@RequestParam String languageId,@RequestParam String loginUserID) throws ParseException {
    	companyService.deleteCompany(companyId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}