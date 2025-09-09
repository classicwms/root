package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
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

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AddOrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementHeader;
import com.tekclover.wms.api.transaction.service.OrderManagementHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OrderManagementHeader"}, value = "OrderManagementHeader  Operations related to OrderManagementHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OrderManagementHeader ", description = "Operations related to OrderManagementHeader ")})
@RequestMapping("/ordermangementheader")
@RestController
public class OrderManagementHeaderController {

    @Autowired
    OrderManagementHeaderService ordermangementheaderService;

    @ApiOperation(response = OrderManagementHeader.class, value = "Get all OrderManagementHeader details")  // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OrderManagementHeader> ordermangementheaderList = ordermangementheaderService.getOrderManagementHeaders();
        return new ResponseEntity<>(ordermangementheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeader.class, value = "Get a OrderManagementHeader") // label for swagger 
    @GetMapping("/{refDocNumber}")
    public ResponseEntity<?> getOrderManagementHeader(@PathVariable String refDocNumber,
                                                      @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String partnerCode) {
        OrderManagementHeader ordermangementheader =
                ordermangementheaderService.getOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        log.info("OrderManagementHeader : " + ordermangementheader);
        return new ResponseEntity<>(ordermangementheader, HttpStatus.OK);
    }

//	@ApiOperation(response = OrderManagementHeader.class, value = "Search OrderManagementHeader") // label for swagger
//	@PostMapping("/findOrderManagementHeader")
//	public List<OrderManagementHeader> findOrderManagementHeader(@RequestBody SearchOrderManagementHeader searchOrderManagementHeader)
//			throws Exception {
//		return ordermangementheaderService.findOrderManagementHeader(searchOrderManagementHeader);
//	}

    @ApiOperation(response = OrderManagementHeader.class, value = "Create OrderManagementHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOrderManagementHeader(@Valid @RequestBody AddOrderManagementHeader newOrderManagementHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementHeader createdOrderManagementHeader = ordermangementheaderService.createOrderManagementHeader(newOrderManagementHeader, loginUserID);
        return new ResponseEntity<>(createdOrderManagementHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeader.class, value = "Update OrderManagementHeader") // label for swagger
    @PatchMapping("/{refDocNumber}")
    public ResponseEntity<?> patchOrderManagementHeader(@PathVariable String refDocNumber,
                                                        @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String partnerCode,
                                                        @Valid @RequestBody UpdateOrderManagementHeader updateOrderManagementHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementHeader createdOrderManagementHeader =
                ordermangementheaderService.updateOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        loginUserID, updateOrderManagementHeader);
        return new ResponseEntity<>(createdOrderManagementHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeader.class, value = "Delete OrderManagementHeader") // label for swagger
    @DeleteMapping("/{refDocNumber}")
    public ResponseEntity<?> deleteOrderManagementHeader(@PathVariable String refDocNumber,
                                                         @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String partnerCode,
                                                         @RequestParam String loginUserID) {
        ordermangementheaderService.deleteOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=======================================================================V2===================================================================================

    @ApiOperation(response = OrderManagementHeaderV2.class, value = "Get all OrderManagementHeader details")
    // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllOrderManagementHeaderV2() {
        List<OrderManagementHeaderV2> ordermangementheaderList = ordermangementheaderService.getOrderManagementHeadersV2();
        return new ResponseEntity<>(ordermangementheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeaderV2.class, value = "Get a OrderManagementHeader") // label for swagger
    @GetMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> getOrderManagementHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                        @RequestParam String partnerCode) {
        OrderManagementHeaderV2 ordermangementheader =
                ordermangementheaderService.getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode);
        log.info("OrderManagementHeader : " + ordermangementheader);
        return new ResponseEntity<>(ordermangementheader, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeaderV2.class, value = "Create OrderManagementHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postOrderManagementHeaderV2(@Valid @RequestBody OrderManagementHeaderV2 newOrderManagementHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OrderManagementHeaderV2 createdOrderManagementHeader = ordermangementheaderService.createOrderManagementHeaderV2(newOrderManagementHeader, loginUserID);
        return new ResponseEntity<>(createdOrderManagementHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeaderV2.class, value = "Update OrderManagementHeader") // label for swagger
    @PatchMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> patchOrderManagementHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                          @RequestParam String partnerCode, @RequestParam String loginUserID,
                                                          @Valid @RequestBody OrderManagementHeaderV2 updateOrderManagementHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OrderManagementHeaderV2 createdOrderManagementHeader =
                ordermangementheaderService.updateOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode, loginUserID, updateOrderManagementHeader);
        return new ResponseEntity<>(createdOrderManagementHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementHeaderV2.class, value = "Delete OrderManagementHeader") // label for swagger
    @DeleteMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> deleteOrderManagementHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                           @RequestParam String partnerCode, @RequestParam String loginUserID) throws ParseException {
        ordermangementheaderService.deleteOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}