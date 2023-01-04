package com.ustorage.api.trans.controller;

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

import com.ustorage.api.trans.model.leadcustomer.*;

import com.ustorage.api.trans.service.LeadCustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = {"LeadCustomer"}, value = "LeadCustomer Operations related to LeadCustomerController")
@SwaggerDefinition(tags = {@Tag(name = "LeadCustomer", description = "Operations related to LeadCustomer")})
@RequestMapping("/leadCustomer")
@RestController
public class LeadCustomerController {

    @Autowired
    LeadCustomerService leadCustomerService;

    @ApiOperation(response = LeadCustomer.class, value = "Get all LeadCustomer details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<LeadCustomer> leadCustomerList = leadCustomerService.getLeadCustomer();
        return new ResponseEntity<>(leadCustomerList, HttpStatus.OK);
    }

    @ApiOperation(response = LeadCustomer.class, value = "Get a LeadCustomer") // label for swagger
    @GetMapping("/{leadCustomerId}")
    public ResponseEntity<?> getLeadCustomer(@PathVariable String leadCustomerId) {
        LeadCustomer dbLeadCustomer = leadCustomerService.getLeadCustomer(leadCustomerId);
        log.info("LeadCustomer : " + dbLeadCustomer);
        return new ResponseEntity<>(dbLeadCustomer, HttpStatus.OK);
    }

    @ApiOperation(response = LeadCustomer.class, value = "Create LeadCustomer") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postLeadCustomer(@Valid @RequestBody AddLeadCustomer newLeadCustomer,
                                              @RequestParam String loginUserID) throws Exception {
        LeadCustomer createdLeadCustomer = leadCustomerService.createLeadCustomer(newLeadCustomer, loginUserID);
        return new ResponseEntity<>(createdLeadCustomer, HttpStatus.OK);
    }
    //Search
    @ApiOperation(response = LeadCustomer.class, value = "Find LeadCustomer") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findLeadCustomer(@Valid @RequestBody FindLeadCustomer findLeadCustomer) throws Exception {
        List<LeadCustomer> createdLeadCustomer = leadCustomerService.findLeadCustomer(findLeadCustomer);
        return new ResponseEntity<>(createdLeadCustomer, HttpStatus.OK);
    }

    @ApiOperation(response = LeadCustomer.class, value = "Update LeadCustomer") // label for swagger
    @PatchMapping("/{leadCustomer}")
    public ResponseEntity<?> patchLeadCustomer(@PathVariable String leadCustomer,
                                               @RequestParam String loginUserID,
                                               @Valid @RequestBody UpdateLeadCustomer updateLeadCustomer)
            throws IllegalAccessException, InvocationTargetException,Exception {
        LeadCustomer updatedLeadCustomer = leadCustomerService.updateLeadCustomer(leadCustomer, loginUserID,
                updateLeadCustomer);
        return new ResponseEntity<>(updatedLeadCustomer, HttpStatus.OK);
    }

    @ApiOperation(response = LeadCustomer.class, value = "Delete LeadCustomer") // label for swagger
    @DeleteMapping("/{leadCustomer}")
    public ResponseEntity<?> deleteLeadCustomer(@PathVariable String leadCustomer, @RequestParam String loginUserID) {
        leadCustomerService.deleteLeadCustomer(leadCustomer, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
