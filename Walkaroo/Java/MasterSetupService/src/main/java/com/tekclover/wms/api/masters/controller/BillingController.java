package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.threepl.billing.*;
import com.tekclover.wms.api.masters.service.BillingService;
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
@Api(tags = {"Billing"}, value = "Billing  Operations related to BillingController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Billing ",description = "Operations related to Billing ")})
@RequestMapping("/billing")
@RestController
public class BillingController {
    @Autowired
    BillingService billingService;

    @ApiOperation(response = Billing.class, value = "Get all Billing details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Billing> BillingList = billingService.getBillings();
        return new ResponseEntity<>(BillingList, HttpStatus.OK);
    }

    @ApiOperation(response = Billing.class, value = "Get a Billing") // label for swagger
    @GetMapping("/{partnerCode}")
    public ResponseEntity<?> getBilling(@RequestParam String warehouseId,@RequestParam String moduleId, @PathVariable String partnerCode,
                                        @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
        Billing BillingList =
                billingService.getBilling(warehouseId, moduleId, partnerCode,companyCodeId,languageId,plantId);
//        log.info("BillingList : " + BillingList);
        return new ResponseEntity<>(BillingList, HttpStatus.OK);
    }

    @ApiOperation(response = Billing.class, value = "Create Billing") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBilling(@Valid @RequestBody AddBilling newBilling,
                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        Billing createdBilling = billingService.createBilling(newBilling, loginUserID);
        return new ResponseEntity<>(createdBilling, HttpStatus.OK);
    }

    @ApiOperation(response = Billing.class, value = "Update Billing") // label for swagger
    @PatchMapping("/{partnerCode}")
    public ResponseEntity<?> patchBilling(@RequestParam String warehouseId,@RequestParam String moduleId,@PathVariable String partnerCode,@RequestParam String companyCodeId,
                                          @RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID,
                                          @Valid @RequestBody UpdateBilling updateBilling )
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Billing createdBilling =
                billingService.updateBilling(warehouseId, moduleId, partnerCode,companyCodeId,languageId,plantId,loginUserID, updateBilling);
        return new ResponseEntity<>(createdBilling, HttpStatus.OK);
    }

    @ApiOperation(response = Billing.class, value = "Delete Billing") // label for swagger
    @DeleteMapping("/{partnerCode}")
    public ResponseEntity<?> deleteBilling(@RequestParam String warehouseId, @RequestParam String moduleId,@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String languageId,
                                           @RequestParam String plantId,@RequestParam String loginUserID) {
        billingService.deleteBilling(warehouseId, moduleId, partnerCode,companyCodeId,languageId,plantId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    //Search
    @ApiOperation(response = Billing.class, value = "Find Billing") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findBilling(@Valid @RequestBody FindBilling findBilling) throws Exception {
        List<Billing> createdBilling = billingService.findBilling(findBilling);
        return new ResponseEntity<>(createdBilling, HttpStatus.OK);
    }
}
