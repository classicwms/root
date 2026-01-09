package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.email.EMailDetails;
import com.tekclover.wms.api.masters.model.email.FindEmailDetails;
import com.tekclover.wms.api.masters.model.email.OrderCancelInput;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
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
    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = EMailDetails.class, value = "Add Email") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postEmail(@Valid @RequestBody EMailDetails newEmail, @RequestParam String loginUserId) {

        try {
            String currentDB = baseService.getDataBase(newEmail.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            EMailDetails addMail = eMailDetailsService.createEMailDetails(newEmail, loginUserId);
            return new ResponseEntity<>(addMail, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = EMailDetails.class, value = "Update Email") // label for swagger
    @PatchMapping("/{emailId}")
    public ResponseEntity<?> patchEmail(@PathVariable Long emailId, @Valid @RequestBody EMailDetails updateEmail, @RequestParam String loginUserId) {
        try {
            String currentDB = baseService.getDataBase(updateEmail.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            EMailDetails updateMail = eMailDetailsService.updateEMailDetails(emailId, updateEmail, loginUserId);
            return new ResponseEntity<>(updateMail, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
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
        try {
            String currentDB = baseService.getDataBase(findEmailDetails.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Stream<EMailDetails> getAllMail = eMailDetailsService.findEmailDetails(findEmailDetails);
            return new ResponseEntity<>(getAllMail, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
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
        try {
            String currentDB = baseService.getDataBase(orderCancelInput.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            sendMailService.sendMail(orderCancelInput);
            return new ResponseEntity<>(HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Undelete a Email
    @ApiOperation(response = EMailDetails.class, value = "Un Delete Email") // label for swagger
    @GetMapping("/undelete/{emailId}")
    public ResponseEntity<?> unDeleteEmail(@PathVariable Long emailId, @RequestParam String loginUserId) {
        EMailDetails getMail = eMailDetailsService.undeleteEMailDetails(emailId, loginUserId);
        return new ResponseEntity<>(getMail, HttpStatus.OK);
    }
}