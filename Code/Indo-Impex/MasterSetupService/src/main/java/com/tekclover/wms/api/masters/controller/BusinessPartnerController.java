package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.businesspartner.AddBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.UpdateBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import com.tekclover.wms.api.masters.service.BusinessPartnerService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"BusinessPartner"}, value = "BusinessPartner  Operations related to BusinessPartnerController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BusinessPartner ", description = "Operations related to BusinessPartner ")})
@RequestMapping("/businesspartner")
@RestController
public class BusinessPartnerController {

    @Autowired
    BusinessPartnerService businesspartnerService;

    @ApiOperation(response = BusinessPartner.class, value = "Get all BusinessPartner details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BusinessPartner> businesspartnerList = businesspartnerService.getBusinessPartners();
        return new ResponseEntity<>(businesspartnerList, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Get a BusinessPartner") // label for swagger 
    @GetMapping("/{partnerCode}")
    public ResponseEntity<?> getBusinessPartner(@PathVariable String partnerCode, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String warehouseId,
                                                @RequestParam String languageId, @RequestParam Long businessPartnerType) {
        BusinessPartner businesspartner =
                businesspartnerService.getBusinessPartner(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType);
        log.info("BusinessPartner : " + businesspartner);
        return new ResponseEntity<>(businesspartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Search BusinessPartner") // label for swagger
    @PostMapping("/findBusinessPartner")
    public List<BusinessPartner> findBusinessPartner(@RequestBody SearchBusinessPartner searchBusinessPartner) {
        return businesspartnerService.findBusinessPartner(searchBusinessPartner);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Create BusinessPartner") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBusinessPartner(@Valid @RequestBody AddBusinessPartner newBusinessPartner, @RequestParam String loginUserID) {
        BusinessPartner createdBusinessPartner = businesspartnerService.createBusinessPartner(newBusinessPartner, loginUserID);
        return new ResponseEntity<>(createdBusinessPartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Update BusinessPartner") // label for swagger
    @PatchMapping("/{partnerCode}")
    public ResponseEntity<?> patchBusinessPartner(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam Long businessPartnerType,
                                                  @Valid @RequestBody UpdateBusinessPartner updateBusinessPartner, @RequestParam String loginUserID) {
        BusinessPartner createdBusinessPartner =
                businesspartnerService.updateBusinessPartner(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType, updateBusinessPartner, loginUserID);
        return new ResponseEntity<>(createdBusinessPartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartnerV2.class, value = "Get a BusinessPartner") // label for swagger
    @GetMapping("/v2/{partnerCode}")
    public ResponseEntity<?> getBusinessPartnerV2(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam Long businessPartnerType) {
        BusinessPartnerV2 businesspartner = businesspartnerService.getBusinessPartnerV2(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType);
        log.info("BusinessPartner : " + businesspartner);
        return new ResponseEntity<>(businesspartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Create BusinessPartner") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postBusinessPartnerV2(@Valid @RequestBody BusinessPartnerV2 newBusinessPartner, @RequestParam String loginUserID) {
        BusinessPartnerV2 createdBusinessPartner = businesspartnerService.createBusinessPartnerV2(newBusinessPartner, loginUserID);
        return new ResponseEntity<>(createdBusinessPartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartnerV2.class, value = "Update BusinessPartner V2") // label for swagger
    @PatchMapping("/v2/{partnerCode}")
    public ResponseEntity<?> patchBusinessPartnerV2(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                    @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam Long businessPartnerType,
                                                    @Valid @RequestBody BusinessPartnerV2 updateBusinessPartner, @RequestParam String loginUserID) {
        BusinessPartnerV2 createdBusinessPartner = businesspartnerService.updateBusinessPartnerV2(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType, updateBusinessPartner, loginUserID);
        return new ResponseEntity<>(createdBusinessPartner, HttpStatus.OK);
    }

    @ApiOperation(response = BusinessPartner.class, value = "Delete BusinessPartner") // label for swagger
    @DeleteMapping("/{partnerCode}")
    public ResponseEntity<?> deleteBusinessPartner(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String warehouseId, @RequestParam String languageId,
                                                   @RequestParam Long businessPartnerType, @RequestParam String loginUserID) {
        businesspartnerService.deleteBusinessPartner(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}