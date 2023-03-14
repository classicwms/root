package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.threepl.cbminbound.*;
import com.tekclover.wms.api.masters.service.CbmInboundService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"CbmInbound"}, value = "CbmInbound  Operations related to CbmInboundController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CbmInbound ",description = "Operations related to CbmInbound ")})
@RequestMapping("/cbminbound")
@RestController
public class CbmInboundController {

    @Autowired
    CbmInboundService cbmInboundService;

    @ApiOperation(response = CbmInbound.class, value = "Get all CbmInbound details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<CbmInbound> CbmInboundList = cbmInboundService.getCbmInbounds();
        return new ResponseEntity<>(CbmInboundList, HttpStatus.OK);
    }

    @ApiOperation(response = CbmInbound.class, value = "Get a CbmInbound") // label for swagger
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getCbmInbound(@PathVariable String itemCode,
                                        @RequestParam String warehouseId) {
        CbmInbound CbmInboundList =
                cbmInboundService.getCbmInbound(warehouseId, itemCode);
        log.info("CbmInboundList : " + CbmInboundList);
        return new ResponseEntity<>(CbmInboundList, HttpStatus.OK);
    }

    @ApiOperation(response = CbmInbound.class, value = "Create CbmInbound") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postCbmInbound(@Valid @RequestBody AddCbmInbound newCbmInbound,
                                         @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        CbmInbound createdCbmInbound = cbmInboundService.createCbmInbound(newCbmInbound, loginUserID);
        return new ResponseEntity<>(createdCbmInbound, HttpStatus.OK);
    }

    @ApiOperation(response = CbmInbound.class, value = "Update CbmInbound") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchCbmInbound(@RequestParam String warehouseId, @PathVariable String itemCode,
                                          @Valid @RequestBody UpdateCbmInbound updateCbmInbound, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        CbmInbound createdCbmInbound =
                cbmInboundService.updateCbmInbound(warehouseId, itemCode,loginUserID, updateCbmInbound);
        return new ResponseEntity<>(createdCbmInbound, HttpStatus.OK);
    }

    @ApiOperation(response = CbmInbound.class, value = "Delete CbmInbound") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteCbmInbound(@PathVariable String itemCode,
                                           @RequestParam String warehouseId,  @RequestParam String loginUserID) {
        cbmInboundService.deleteCbmInbound(warehouseId, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
