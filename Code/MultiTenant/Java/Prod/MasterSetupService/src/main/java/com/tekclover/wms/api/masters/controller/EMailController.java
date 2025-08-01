package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.email.*;
import com.tekclover.wms.api.masters.service.EMailDetailsService;
import com.tekclover.wms.api.masters.service.SendMailService;
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
import java.io.IOException;
import java.text.ParseException;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"EMail"}, value = "EMail  Operations related to EMailController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EMail", description = "Operations related to EMail ")})
@RequestMapping("/email")
@RestController
public class EMailController {

    @Autowired
    EMailDetailsService eMailDetailsService;
    @Autowired
    SendMailService sendMailService;

    @ApiOperation(response = EMailDetails.class, value = "Add Email") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postEmail(@Valid @RequestBody EMailDetails newEmail, @RequestParam String loginUserId) {
        EMailDetails addMail = eMailDetailsService.createEMailDetails(newEmail, loginUserId);
        return new ResponseEntity<>(addMail, HttpStatus.OK);
    }

    @ApiOperation(response = EMailDetails.class, value = "Update Email") // label for swagger
    @PatchMapping("/{emailId}")
    public ResponseEntity<?> patchEmail(@PathVariable Long emailId, @Valid @RequestBody EMailDetails updateEmail, @RequestParam String loginUserId) {
        EMailDetails updateMail = eMailDetailsService.updateEMailDetails(emailId, updateEmail, loginUserId);
        return new ResponseEntity<>(updateMail, HttpStatus.OK);
    }

    @ApiOperation(response = EMailDetails.class, value = "Get Email") // label for swagger
    @GetMapping("/{emailId}")
    public ResponseEntity<?> getEmail(@PathVariable Long emailId) {
        EMailDetails getMail = eMailDetailsService.getEMailDetails(emailId);
        return new ResponseEntity<>(getMail, HttpStatus.OK);
    }

    @ApiOperation(response = EMailDetails.class, value = "Get all Email") // label for swagger
    @PostMapping("/findEmail")
    public ResponseEntity<?> FindEmailDetails(FindEmailDetails findEmailDetails) throws ParseException {
        Stream<EMailDetails> getAllMail = eMailDetailsService.findEmailDetails(findEmailDetails);
        return new ResponseEntity<>(getAllMail, HttpStatus.OK);
    }

    @ApiOperation(response = EMailDetails.class, value = "Delete Email") // label for swagger
    @DeleteMapping("/{emailId}")
    public ResponseEntity<?> deleteEmail(@PathVariable Long emailId, @RequestParam String loginUserId) {
        eMailDetailsService.deleteEMailDetails(emailId, loginUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //send Mail
    @ApiOperation(response = EMailDetails.class, value = "Send Email") // label for swagger
    @PostMapping("/sendMail")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody OrderCancelInput orderCancelInput) throws IOException, MessagingException {
        sendMailService.sendMail(orderCancelInput);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Undelete a Email
    @ApiOperation(response = EMailDetails.class, value = "Un Delete Email") // label for swagger
    @GetMapping("/undelete/{emailId}")
    public ResponseEntity<?> unDeleteEmail(@PathVariable Long emailId, @RequestParam String loginUserId) {
        EMailDetails getMail = eMailDetailsService.undeleteEMailDetails(emailId, loginUserId);
        return new ResponseEntity<>(getMail, HttpStatus.OK);
    }
}