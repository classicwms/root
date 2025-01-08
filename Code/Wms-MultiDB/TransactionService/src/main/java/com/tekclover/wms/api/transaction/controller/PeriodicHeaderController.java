package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.*;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.SearchPeriodicHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.service.PeriodicHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PeriodicHeader"}, value = "PeriodicHeader  Operations related to PeriodicHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicHeader ", description = "Operations related to PeriodicHeader ")})
@RequestMapping("/periodicheader")
@RestController
public class PeriodicHeaderController {

    @Autowired
    PeriodicHeaderService periodicheaderService;

    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PeriodicHeaderEntity> periodicheaderList = periodicheaderService.getPeriodicHeaders();
        return new ResponseEntity<>(periodicheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger 
    @GetMapping("/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId,
                                               @RequestParam String palntId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId) {
        PeriodicHeader periodicheader =
                periodicheaderService.getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
        return new ResponseEntity<>(periodicheader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Search PeriodicHeader") // label for swagger
    @PostMapping("/findPeriodicHeader")
    public ResponseEntity<?> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader) throws Exception {
        List<PeriodicHeaderEntity> page = periodicheaderService.findPeriodicHeader(searchPeriodicHeader);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = PeriodicHeader.class, value = "Search PeriodicHeader New") // label for swagger
    @PostMapping("/findPeriodicHeaderStream")
    public ResponseEntity<?> findPeriodicHeaderStream(@RequestBody SearchPeriodicHeader searchPeriodicHeader) throws Exception {
        Stream<PeriodicHeader> response = periodicheaderService.findPeriodicHeaderStream(searchPeriodicHeader);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
    @PostMapping("/run/pagination")
    public ResponseEntity<?> findInventory(@RequestParam String warehouseId,
                                           @RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "100") Integer pageSize,
                                           @RequestParam(defaultValue = "itemCode") String sortBy) throws Exception {
        Page<PeriodicLineEntity> periodicLineEntity =
                periodicheaderService.runPeriodicHeader(warehouseId, pageNo, pageSize, sortBy);
        return new ResponseEntity<>(periodicLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader,
                                                @RequestParam String loginUserID) throws Exception {
        PeriodicHeaderEntity createdPeriodicHeader =
                periodicheaderService.createPeriodicHeader(newPeriodicHeader, loginUserID);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                 @RequestParam Long cycleCountTypeId, @Valid @RequestBody UpdatePeriodicHeader updatePeriodicHeader,
                                                 @RequestParam String loginUserID) throws Exception {
        PeriodicHeader createdPeriodicHeader =
                periodicheaderService.updatePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, updatePeriodicHeader);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
    @DeleteMapping("/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId,
                                                  @RequestParam String palntId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
                                                  @RequestParam String loginUserID) {
        periodicheaderService.deletePeriodicHeader(companyCodeId, palntId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //==========================================================V2=======================================================

    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Get all PeriodicHeaderV2 details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPerpetualHeaderV2() {
        List<PeriodicHeaderEntityV2> periodicHeaderEntity = periodicheaderService.getPeriodicHeadersV2();
        return new ResponseEntity<>(periodicHeaderEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Get a PeriodicHeaderV2") // label for swagger
    @GetMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCode, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId,
                                                 @RequestParam Long cycleCountTypeId) {
        PeriodicHeaderEntityV2 periodicHeaderV2 =
                periodicheaderService.getPeriodicHeaderWithLineV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);
        return new ResponseEntity<>(periodicHeaderV2, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderV2.class, value = "Search PeriodicHeader") // label for swagger
    @PostMapping("/v2/findPeriodicHeader")
    public Stream<PeriodicHeaderV2> findPeriodicHeader(@RequestBody SearchPeriodicHeaderV2 searchPeriodicHeader) throws Exception {
        return periodicheaderService.findPeriodicHeaderV2(searchPeriodicHeader);
    }

    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Search PeriodicHeader") // label for swagger
    @PostMapping("/v2/findPeriodicHeaderEntity")
    public List<PeriodicHeaderEntityV2> findPeriodicHeaderEntity(@RequestBody SearchPeriodicHeaderV2 searchPeriodicHeader) throws Exception {
        return periodicheaderService.findPeriodicHeaderEntityV2(searchPeriodicHeader);
    }

    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Create PeriodicHeaderV2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPeriodicHeaderV2(@Valid @RequestBody PeriodicHeaderEntityV2 newPerpetualHeader,
                                                  @RequestParam String loginUserID) throws Exception {
        PeriodicHeaderEntityV2 createdPeriodicHeader =
                periodicheaderService.createPeriodicHeaderV2(newPerpetualHeader, loginUserID);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

//	@ApiOperation(response = PeriodicLineEntity.class, value = "Create PerpetualHeader") // label for swagger
//	@PostMapping("/v2/run")
//	public ResponseEntity<?> postRunPerpetualHeaderV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
//			throws IllegalAccessException, InvocationTargetException, ParseException {
//		Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderNewV2(runPerpetualHeader);
//		return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
//	}

    @ApiOperation(response = PeriodicHeaderV2.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
                                                 @Valid @RequestBody PeriodicHeaderEntityV2 updatePeriodicHeader, @RequestParam String loginUserID)
            throws Exception {
        PeriodicHeaderV2 createPeriodicHeader =
                periodicheaderService.updatePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId,
                                                             cycleCountTypeId, cycleCountNo, loginUserID, updatePeriodicHeader);
        return new ResponseEntity<>(createPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderV2.class, value = "Delete PeriodicHeader") // label for swagger
    @DeleteMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID) throws Exception {
        periodicheaderService.deletePeriodicHeaderV2(
                companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //==========================================================Impex-v4===========================================================
    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Create PeriodicHeaderV4") // label for swagger
    @PostMapping("/v4")
    public ResponseEntity<?> postPeriodicHeaderV4(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @Valid @RequestBody List<PeriodicLineV2> periodicLines,
                                                  @RequestParam String loginUserID) throws Exception {
        PeriodicHeaderEntityV2 createdPeriodicHeader =
                periodicheaderService.processStockV4(companyCodeId, plantId, languageId, warehouseId, loginUserID, periodicLines);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

}