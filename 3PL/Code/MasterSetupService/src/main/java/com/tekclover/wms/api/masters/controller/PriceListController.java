package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.threepl.cbminbound.CbmInbound;
import com.tekclover.wms.api.masters.model.threepl.cbminbound.FindCbmInbound;
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
import java.text.ParseException;
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
    public ResponseEntity<?> getPriceList(@RequestParam String moduleId, @PathVariable Long priceListId,
                                          @RequestParam Long serviceTypeId,@RequestParam Long chargeRangeId,@RequestParam String warehouseId,@RequestParam String companyCodeId,
                                          @RequestParam String languageId,@RequestParam String plantId) {
        PriceList PriceList =
                priceListService.getPriceList(warehouseId, moduleId, priceListId,serviceTypeId,chargeRangeId,companyCodeId,languageId,plantId);
        log.info("PriceList : " + PriceList);
        return new ResponseEntity<>(PriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Create PriceListId") // label for swagger
    @PostMapping("/create/list")
    public ResponseEntity<?> postPriceListId(@Valid @RequestBody List<AddPriceList> newPriceListId,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
       List<PriceList> createdPriceList = priceListService.createPriceLists(newPriceListId, loginUserID);
        return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Update PriceListId") // label for swagger
    @PatchMapping("/update/list")
    public ResponseEntity<?> patchPriceListId(@Valid @RequestBody List<AddPriceList> updatePriceList, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PriceList> updatedPriceList =
                priceListService.updatePriceLists(updatePriceList,loginUserID);
        return new ResponseEntity<>(updatedPriceList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Delete PriceListId") // label for swagger
    @DeleteMapping("/delete/list")
    public ResponseEntity<?> deletePriceListId(@Valid @RequestBody List<PriceList> deletepriceList, @RequestParam String loginUserID) {
        priceListService.deletePriceLists(deletepriceList,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    //Search
    @ApiOperation(response = PriceList.class, value = "Find PriceList") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findPriceList(@Valid @RequestBody FindPriceList findPriceList) throws Exception {
        List<PriceList> createdPriceList = priceListService.findPriceList(findPriceList);
        return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
    }
}
