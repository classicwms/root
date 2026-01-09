package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.FindPalletIdAssignment;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PalletIdAssignment;
import com.tekclover.wms.api.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.transaction.service.BaseService;
import com.tekclover.wms.api.transaction.service.PalletIdAssignmentService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PalletIdAssignment"}, value = "PalletIdAssignment  Operations related to PalletIdAssignmentController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PalletIdAssignment ",description = "Operations related to PalletIdAssignment ")})
@RequestMapping("/palletIdAssignment")
@RestController
public class PalletIdAssignmentController {

    @Autowired
    PalletIdAssignmentService palletIdAssignmentService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

   //Get
    @ApiOperation(response = PalletIdAssignment.class, value = "Get a PalletIdAssignment") // label for swagger
    @GetMapping("/{pId}")
    public ResponseEntity<?> getPalletIdAssignment(@PathVariable Long pId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        PalletIdAssignment palletIdAssignment = palletIdAssignmentService.getPalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, pId);
        log.info("PalletIdAssignment : " + palletIdAssignment);
        return new ResponseEntity<>(palletIdAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    //Get V3
    @ApiOperation(response = PalletIdAssignment.class, value = "Get a PalletIdAssignmentV3") // label for swagger
    @GetMapping("/v3/{palletId}")
    public ResponseEntity<?> getPalletIdAssignmentV3(@PathVariable String palletId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        PalletIdAssignment palletIdAssignment = palletIdAssignmentService.getPalletIdAssignmentV3(companyCodeId, plantId, languageId, warehouseId, palletId);
        log.info("PalletIdAssignment : " + palletIdAssignment);
        return new ResponseEntity<>(palletIdAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PalletIdAssignment.class, value = "Create PalletIdAssignment") // label for swagger
    @PostMapping("/create") //Walkaroo
    public ResponseEntity<?> postPalletIdAssignmentWalkaroo(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                           @Valid @RequestBody List<PalletIdAssignment> palletIdAssignmentList, @RequestParam String loginUserID) throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        List<PalletIdAssignment> createdPalletIdAssignment = palletIdAssignmentService.createPalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, palletIdAssignmentList, loginUserID);
        return new ResponseEntity<>(createdPalletIdAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PalletIdAssignment.class, value = "Update PalletIdAssignment") // label for swagger
    @PatchMapping("/update")
    public ResponseEntity<?> patchPalletIdAssignment(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                     @Valid @RequestBody List<PalletIdAssignment> updatepalletIdAssignmentList, @RequestParam String loginUserID)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        List<PalletIdAssignment> updatedPalletIdAssignment =
                palletIdAssignmentService.updatePalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, updatepalletIdAssignmentList, loginUserID);
        return new ResponseEntity<>(updatedPalletIdAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PalletIdAssignment.class, value = "Update PalletIdAssignment") // label for swagger
    @PatchMapping("/update/PalletIdStatus")
    public ResponseEntity<?> patchPalletIdStatus(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                     @Valid @RequestBody List<PalletIdAssignment> updatepalletIdStatus, @RequestParam String loginUserID)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        List<PalletIdAssignment> updatedPalletIdStatus =
                palletIdAssignmentService.updatePalletIdStatus(companyCodeId, plantId, languageId, warehouseId, updatepalletIdStatus, loginUserID);
        return new ResponseEntity<>(updatedPalletIdStatus, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PalletIdAssignment.class, value = "Delete PalletIdAssignment") // label for swagger
    @DeleteMapping("/delete/{pId}")
    public ResponseEntity<?> deletePalletIdAssignment(@PathVariable Long pId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        palletIdAssignmentService.deletePalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, pId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    //Stream
    @ApiOperation(response = PalletIdAssignment.class, value = "Find PalletIdAssignment New") // label for swagger
    @PostMapping("/findPalletIdAssignment")
    public Stream<PalletIdAssignment> findPalletIdAssignmentNew(@Valid @RequestBody FindPalletIdAssignment findPalletIdAssignment) throws Exception {
        try {
            String currentDB = baseService.getDataBase(findPalletIdAssignment.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
        return palletIdAssignmentService.findPalletIdAssignment(findPalletIdAssignment);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
