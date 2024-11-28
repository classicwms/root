package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.packing.AddPackingHeader;
import com.tekclover.wms.api.transaction.model.packing.PackingHeader;
import com.tekclover.wms.api.transaction.model.packing.UpdatePackingHeader;
import com.tekclover.wms.api.transaction.service.PackingHeaderService;
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
@Api(tags = {"PackingHeader"}, value = "PackingHeader  Operations related to PackingHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PackingHeader ", description = "Operations related to PackingHeader ")})
@RequestMapping("/packingheader")
@RestController
public class PackingHeaderController {

    @Autowired
    PackingHeaderService packingheaderService;

    @ApiOperation(response = PackingHeader.class, value = "Get all PackingHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PackingHeader> packingheaderList = packingheaderService.getPackingHeaders();
        return new ResponseEntity<>(packingheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PackingHeader.class, value = "Get a PackingHeader") // label for swagger
    @GetMapping("/{packingNo}")
    public ResponseEntity<?> getPackingHeader(@PathVariable String packingNo, @RequestParam String languageId, @RequestParam String companyCodeId,
                                              @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String qualityInspectionNo) {
        PackingHeader packingheader = packingheaderService.getPackingHeader(languageId, companyCodeId, plantId,
                                                                            warehouseId, preOutboundNo, refDocNumber, partnerCode, qualityInspectionNo, packingNo);
        log.info("PackingHeader : " + packingheader);
        return new ResponseEntity<>(packingheader, HttpStatus.OK);
    }

//	@ApiOperation(response = PackingHeader.class, value = "Search PackingHeader") // label for swagger
//	@PostMapping("/findPackingHeader")
//	public List<PackingHeader> findPackingHeader(@RequestBody SearchPackingHeader searchPackingHeader)
//			throws Exception {
//		return packingheaderService.findPackingHeader(searchPackingHeader);
//	}

    @ApiOperation(response = PackingHeader.class, value = "Create PackingHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPackingHeader(@Valid @RequestBody AddPackingHeader newPackingHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PackingHeader createdPackingHeader = packingheaderService.createPackingHeader(newPackingHeader, loginUserID);
        return new ResponseEntity<>(createdPackingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PackingHeader.class, value = "Update PackingHeader") // label for swagger
    @PatchMapping("/{packingNo}")
    public ResponseEntity<?> patchPackingHeader(@PathVariable String packingNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String qualityInspectionNo,
                                                @Valid @RequestBody UpdatePackingHeader updatePackingHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PackingHeader createdPackingHeader =
                packingheaderService.updatePackingHeader(languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, qualityInspectionNo, packingNo, loginUserID, updatePackingHeader);
        return new ResponseEntity<>(createdPackingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PackingHeader.class, value = "Delete PackingHeader") // label for swagger
    @DeleteMapping("/{packingNo}")
    public ResponseEntity<?> deletePackingHeader(@PathVariable String packingNo, @RequestParam String languageId,
                                                 @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam String qualityInspectionNo, @RequestParam String loginUserID) {
        packingheaderService.deletePackingHeader(languageId, companyCodeId, plantId, warehouseId,
                                                 preOutboundNo, refDocNumber, partnerCode, qualityInspectionNo, packingNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}