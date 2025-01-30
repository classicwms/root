package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.companyid.FindCompanyId;
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

import com.tekclover.wms.api.idmaster.model.companyid.AddCompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;
import com.tekclover.wms.api.idmaster.model.companyid.UpdateCompanyId;
import com.tekclover.wms.api.idmaster.service.CompanyIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"CompanyId"}, value = "CompanyId  Operations related to CompanyIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CompanyId ",description = "Operations related to CompanyId ")})
@RequestMapping("/companyid")
@RestController
public class CompanyIdController {

	@Autowired
	CompanyIdService companyidService;

	@ApiOperation(response = CompanyId.class, value = "Get all CompanyId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<CompanyId> companyidList = companyidService.getCompanyIds();
		return new ResponseEntity<>(companyidList, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Get a CompanyId") // label for swagger
	@GetMapping("/{companyCodeId}")
	public ResponseEntity<?> getCompanyId(@PathVariable String companyCodeId,@RequestParam String languageId) {
		CompanyId companyid = companyidService.getCompanyId(companyCodeId,languageId);
		log.info("CompanyId : " + companyid);
		return new ResponseEntity<>(companyid, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Create CompanyId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCompanyId(@Valid @RequestBody AddCompanyId newCompanyId,
										   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		CompanyId createdCompanyId = companyidService.createCompanyId(newCompanyId, loginUserID);
		return new ResponseEntity<>(createdCompanyId , HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Update CompanyId") // label for swagger
	@PatchMapping("/{companyCodeId}")
	public ResponseEntity<?> patchCompanyId(@PathVariable String companyCodeId,@RequestParam String languageId, @RequestParam String loginUserID,
											@Valid @RequestBody UpdateCompanyId updateCompanyId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CompanyId createdCompanyId =
				companyidService.updateCompanyId(companyCodeId,languageId,loginUserID,updateCompanyId);
		return new ResponseEntity<>(createdCompanyId , HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Delete CompanyId") // label for swagger
	@DeleteMapping("/{companyCodeId}")
	public ResponseEntity<?> deleteCompanyId(@PathVariable String companyCodeId,@RequestParam String languageId,
											 @RequestParam String loginUserID) {
		companyidService.deleteCompanyId(companyCodeId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = CompanyId.class, value = "Find CompanyId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findCompanyId(@Valid @RequestBody FindCompanyId findCompanyId) throws Exception {
		List<CompanyId> createdCompanyId = companyidService.findCompanyId(findCompanyId);
		return new ResponseEntity<>(createdCompanyId, HttpStatus.OK);
	}
}