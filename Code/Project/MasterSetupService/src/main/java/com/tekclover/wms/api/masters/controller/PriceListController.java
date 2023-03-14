package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.threepl.pricelist.*;
import com.tekclover.wms.api.masters.service.PriceListService;
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
@Api(tags = {"PriceList"}, value = "PriceList  Operations related to PriceListController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PriceList ",description = "Operations related to PriceList ")})
@RequestMapping("/pricelist")
@RestController
public class PriceListController {

    @Autowired
    PriceListService priceListService;

    @ApiOperation(response = PriceList.class, value = "Get all PriceList details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PriceList> priceListList = priceListService.getPriceLists();
        return new ResponseEntity<>(priceListList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Get a PriceList") // label for swagger
    @GetMapping("/{priceListId}")
    public ResponseEntity<?> getPriceList(@RequestParam Long module, @PathVariable Long priceListId,@RequestParam Long serviceTypeId,
                                              @RequestParam String warehouseId) {
        PriceList PriceList =
                priceListService.getPriceList(warehouseId, module, priceListId,serviceTypeId);
        log.info("PriceList : " + PriceList);
        return new ResponseEntity<>(PriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Create PriceListId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPriceListId(@Valid @RequestBody AddPriceList newPriceListId,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        PriceList createdPriceList = priceListService.createPriceList(newPriceListId, loginUserID);
        return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Update PriceListId") // label for swagger
    @PatchMapping("/{priceListId}")
    public ResponseEntity<?> patchPriceListId(@RequestParam String warehouseId, @PathVariable Long priceListId, @RequestParam Long serviceTypeId, @RequestParam Long module,
                                              @Valid @RequestBody UpdatePriceList updatePriceList, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PriceList createdPriceList =
                priceListService.updatePriceList(warehouseId, module, priceListId,serviceTypeId,loginUserID, updatePriceList);
        return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Delete PriceListId") // label for swagger
    @DeleteMapping("/{priceListId}")
    public ResponseEntity<?> deletePriceListId(@PathVariable Long priceListId,
                                                 @RequestParam String warehouseId, @RequestParam Long module,@RequestParam Long serviceTypeId, @RequestParam String loginUserID) {
        priceListService.deletePriceList(warehouseId, module, priceListId,serviceTypeId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
