package com.ustorage.api.master.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.api.master.model.accountstatus.AddAccountStatus;
import com.ustorage.api.master.model.accountstatus.AccountStatus;
import com.ustorage.api.master.model.accountstatus.UpdateAccountStatus;
import com.ustorage.api.master.service.AccountStatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "AccountStatus" }, value = "AccountStatus Operations related to AccountStatusController") 
@SwaggerDefinition(tags = { @Tag(name = "AccountStatus", description = "Operations related to AccountStatus") })
@RequestMapping("/accountStatus")
@RestController
public class AccountStatusController {

	@Autowired
	AccountStatusService accountStatusService;

	@ApiOperation(response = AccountStatus.class, value = "Get all AccountStatus details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<AccountStatus> accountStatusList = accountStatusService.getAccountStatus();
		return new ResponseEntity<>(accountStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Get a AccountStatus") // label for swagger
	@GetMapping("/{accountStatusId}")
	public ResponseEntity<?> getAccountStatus(@PathVariable String accountStatusId) {
		AccountStatus dbAccountStatus = accountStatusService.getAccountStatus(accountStatusId);
		log.info("AccountStatus : " + dbAccountStatus);
		return new ResponseEntity<>(dbAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Create AccountStatus") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAccountStatus(@Valid @RequestBody AddAccountStatus newAccountStatus,
			@RequestParam String loginUserID) throws Exception {
		AccountStatus createdAccountStatus = accountStatusService.createAccountStatus(newAccountStatus, loginUserID);
		return new ResponseEntity<>(createdAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Update AccountStatus") // label for swagger
	@PatchMapping("/{accountStatusId}")
	public ResponseEntity<?> patchAccountStatus(@PathVariable String accountStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateAccountStatus updateAccountStatus)
			throws IllegalAccessException, InvocationTargetException {
		AccountStatus updatedAccountStatus = accountStatusService.updateAccountStatus(accountStatusId, loginUserID,
				updateAccountStatus);
		return new ResponseEntity<>(updatedAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Delete AccountStatus") // label for swagger
	@DeleteMapping("/{accountStatusId}")
	public ResponseEntity<?> deleteAccountStatus(@PathVariable String accountStatusId, @RequestParam String loginUserID) {
		accountStatusService.deleteAccountStatus(accountStatusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
