package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.master.CustomerMaster;
import com.almailem.ams.api.connector.model.master.FindCustomerMaster;
import com.almailem.ams.api.connector.service.CustomerMasterService;
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

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"CustomerMaster"}, value = "CustomerMaster Operations related to CustomerMasterController")
@SwaggerDefinition(tags = {@Tag(name = "CustomerMaster", description = "Operations related to CustomerMaster")})
@RequestMapping("/customermaster")
@RestController
public class CustomerMasterController {

    @Autowired
    CustomerMasterService customerMasterService;

    //Get All CustomerMaster Details
    @ApiOperation(response = CustomerMaster.class, value = "Get All CustomerMaster Details")
    @GetMapping("")
    public ResponseEntity<?> getAllCustomerMasters() {
        List<CustomerMaster> customerMasters = customerMasterService.getAllCustomerMasterDetails();
        return new ResponseEntity<>(customerMasters, HttpStatus.OK);
    }

    // Find CustomerMaster
    @ApiOperation(response = CustomerMaster.class, value = "Find CustomerMaster") // label for Swagger
    @PostMapping("/findCustomerMaster")
    public ResponseEntity<?> findCustomerMaster(@RequestBody FindCustomerMaster findCustomerMaster) throws ParseException {
        List<CustomerMaster> customerMaster = customerMasterService.findCustomerMaster(findCustomerMaster);
        return new ResponseEntity<>(customerMaster, HttpStatus.OK);
    }

}
