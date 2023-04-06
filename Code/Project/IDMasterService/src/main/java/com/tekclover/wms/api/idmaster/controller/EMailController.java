package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.email.*;
import com.tekclover.wms.api.idmaster.service.EMailDetailsService;
import com.tekclover.wms.api.idmaster.service.SendMailService;
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

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"EMail"}, value = "EMail  Operations related to EMailController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EMail",description = "Operations related to EMail ")})
@RequestMapping("/email")
@RestController
public class EMailController {
	
	@Autowired
	EMailDetailsService eMailDetailsService;
	@Autowired
	SendMailService sendMailService;

	@ApiOperation(response = EMailDetails.class, value = "Add Email") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEmail(@Valid @RequestBody AddEMailDetails newEmail)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails addMail = eMailDetailsService.createEMailDetails(newEmail);
		return new ResponseEntity<>(addMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Update Email") // label for swagger
	@PatchMapping("/{id}")
	public ResponseEntity<?> patchEmail(@PathVariable Long id, @Valid @RequestBody AddEMailDetails updateEmail)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails updateMail = eMailDetailsService.updateEMailDetails(id,updateEmail);
		return new ResponseEntity<>(updateMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Get Email") // label for swagger
	@GetMapping("/{id}")
	public ResponseEntity<?> getEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails getMail = eMailDetailsService.getEMailDetails(id);
		return new ResponseEntity<>(getMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Get all Email") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAllEmail()
			throws IllegalAccessException, InvocationTargetException, IOException {
		List<EMailDetails> getAllMail = eMailDetailsService.getEMailDetailsList();
		return new ResponseEntity<>(getAllMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Delete Email") // label for swagger
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		eMailDetailsService.deleteEMailDetails(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//send Mail
	@ApiOperation(response = EMailDetails.class, value = "Send Email") // label for swagger
	@GetMapping("/sendMail")
	public ResponseEntity<?> sendEmail()
			throws IOException, MessagingException {
		sendMailService.sendMail();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Un Delete Email") // label for swagger
	@GetMapping("/undelete/{id}")
	public ResponseEntity<?> unDeleteEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails getMail = eMailDetailsService.undeleteEMailDetails(id);
		return new ResponseEntity<>(getMail,HttpStatus.OK);
	}
}