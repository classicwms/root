package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.threepl.pricelist.AddPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.FindPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.UpdatePriceList;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
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
@SwaggerDefinition(tags = {@Tag(name = "PriceList ", description = "Operations related to PriceList ")})
@RequestMapping("/pricelist")
@RestController
public class PriceListController {

    @Autowired
    PriceListService priceListService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = PriceList.class, value = "Get all PriceList details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PriceList> priceListList = priceListService.getPriceLists();
        return new ResponseEntity<>(priceListList, HttpStatus.OK);
    }

    @ApiOperation(response = PriceList.class, value = "Get a PriceList") // label for swagger
    @GetMapping("/{priceListId}")
    public ResponseEntity<?> getPriceList(@RequestParam String moduleId, @PathVariable Long priceListId,
                                          @RequestParam Long serviceTypeId, @RequestParam Long chargeRangeId, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                          @RequestParam String languageId, @RequestParam String plantId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PriceList PriceList =
                    priceListService.getPriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId);
            log.info("PriceList : " + PriceList);
            return new ResponseEntity<>(PriceList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PriceList.class, value = "Create PriceListId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPriceListId(@Valid @RequestBody AddPriceList newPriceListId,
                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newPriceListId.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PriceList createdPriceList = priceListService.createPriceList(newPriceListId, loginUserID);
            return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PriceList.class, value = "Update PriceListId") // label for swagger
    @PatchMapping("/{priceListId}")
    public ResponseEntity<?> patchPriceListId(@RequestParam String warehouseId, @PathVariable Long priceListId, @RequestParam Long serviceTypeId,
                                              @RequestParam String moduleId, @RequestParam Long chargeRangeId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                              @Valid @RequestBody UpdatePriceList updatePriceList, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PriceList createdPriceList =
                    priceListService.updatePriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId, loginUserID, updatePriceList);
            return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PriceList.class, value = "Delete PriceListId") // label for swagger
    @DeleteMapping("/{priceListId}")
    public ResponseEntity<?> deletePriceListId(@PathVariable Long priceListId,
                                               @RequestParam String warehouseId, @RequestParam String moduleId, @RequestParam Long serviceTypeId, @RequestParam Long chargeRangeId, @RequestParam String companyCodeId,
                                               @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            priceListService.deletePriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = PriceList.class, value = "Find PriceList") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findPriceList(@Valid @RequestBody FindPriceList findPriceList) throws Exception {
        try {
            String currentDB = baseService.getDataBase(findPriceList.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PriceList> createdPriceList = priceListService.findPriceList(findPriceList);
            return new ResponseEntity<>(createdPriceList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
