package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.purchasereturn.FindPurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.FindPurchaseReturnLine;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnLine;
import com.almailem.ams.api.connector.service.ReturnPOService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ReturnPOV2"}, value = "ReturnPOV2 Operations related to ReturnPOV2Controller")
@SwaggerDefinition(tags = {@Tag(name = "ReturnPOV2", description = "Operations related to ReturnPOV2")})
@RequestMapping("/returnpov2")
@RestController
public class ReturnPOController {

    @Autowired
    ReturnPOService returnPOService;

    //Get All ReturnPOV2 Details
    @ApiOperation(response = PurchaseReturnHeader.class, value = "Get All ReturnPOV2 Details")
    @GetMapping("")
    public ResponseEntity<?> getAllReturnPOV2s() {
        List<PurchaseReturnHeader> purchaseReturns = returnPOService.getAllReturnPoV2Details();
        return new ResponseEntity<>(purchaseReturns, HttpStatus.OK);
    }

    // FInd PurchaseReturnHeader
    @ApiOperation(response = PurchaseReturnHeader.class, value = "Find PurchaseReturnHeader") // label for Swagger
    @PostMapping("/findPurchaseReturnHeader")
    public ResponseEntity<?> searchPurchaseReturnHeader(@RequestBody FindPurchaseReturnHeader findPurchaseReturnHeader) throws ParseException {
        List<PurchaseReturnHeader> purchaseReturnHeaders = returnPOService.findPurchaseReturnHeader(findPurchaseReturnHeader);
        return new ResponseEntity<>(purchaseReturnHeaders, HttpStatus.OK);
    }

    @ApiOperation(response = PurchaseReturnLine.class, value = "Find PurchaseReturnLine") // label for Swagger
    @PostMapping("/findPurchaseReturnLine")
    public ResponseEntity<?> searchPurchaseReturnLine(@RequestBody FindPurchaseReturnLine findPurchaseReturnLine) throws ParseException {
        List<PurchaseReturnLine> purchaseReturnLines = returnPOService.findPurchaseReturnLine(findPurchaseReturnLine);
        return new ResponseEntity<>(purchaseReturnLines, HttpStatus.OK);
    }

}
