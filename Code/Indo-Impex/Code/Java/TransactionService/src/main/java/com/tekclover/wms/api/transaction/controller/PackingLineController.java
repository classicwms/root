package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.packing.AddPackingLine;
import com.tekclover.wms.api.transaction.model.packing.PackingLine;
import com.tekclover.wms.api.transaction.model.packing.UpdatePackingLine;
import com.tekclover.wms.api.transaction.service.PackingLineService;
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
@Api(tags = {"PackingLine"}, value = "PackingLine  Operations related to PackingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PackingLine ", description = "Operations related to PackingLine ")})
@RequestMapping("/packingline")
@RestController
public class PackingLineController {

    @Autowired
    PackingLineService packinglineService;

    @ApiOperation(response = PackingLine.class, value = "Get all PackingLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PackingLine> packinglineList = packinglineService.getPackingLines();
        return new ResponseEntity<>(packinglineList, HttpStatus.OK);
    }

    @ApiOperation(response = PackingLine.class, value = "Get a PackingLine") // label for swagger
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getPackingLine(@PathVariable String itemCode, @RequestParam String languageId,
                                            @RequestParam Long companyCodeId, @RequestParam String plantId,
                                            @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                            @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                            @RequestParam Long lineNumber, @RequestParam String packingNo) {
        PackingLine packingline = packinglineService.getPackingLine(languageId, companyCodeId, plantId, warehouseId,
                                                                    preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode);
        log.info("PackingLine : " + packingline);
        return new ResponseEntity<>(packingline, HttpStatus.OK);
    }

//	@ApiOperation(response = PackingLine.class, value = "Search PackingLine") // label for swagger
//	@PostMapping("/findPackingLine")
//	public List<PackingLine> findPackingLine(@RequestBody SearchPackingLine searchPackingLine)
//			throws Exception {
//		return packinglineService.findPackingLine(searchPackingLine);
//	}

    @ApiOperation(response = PackingLine.class, value = "Create PackingLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPackingLine(@Valid @RequestBody AddPackingLine newPackingLine, @RequestParam String loginUserID)
            throws Exception {
        PackingLine createdPackingLine = packinglineService.createPackingLine(newPackingLine, loginUserID);
        return new ResponseEntity<>(createdPackingLine, HttpStatus.OK);
    }

    @ApiOperation(response = PackingLine.class, value = "Update PackingLine") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchPackingLine(@PathVariable String itemCode, @RequestParam String languageId, @RequestParam Long companyCodeId,
                                              @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                              @RequestParam String packingNo, @Valid @RequestBody UpdatePackingLine updatePackingLine, @RequestParam String loginUserID)
            throws Exception {
        PackingLine createdPackingLine =
                packinglineService.updatePackingLine(languageId, companyCodeId, plantId, warehouseId, preOutboundNo,
                                                     refDocNumber, partnerCode, lineNumber, packingNo, itemCode, loginUserID, updatePackingLine);
        return new ResponseEntity<>(createdPackingLine, HttpStatus.OK);
    }

    @ApiOperation(response = PackingLine.class, value = "Delete PackingLine") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deletePackingLine(@PathVariable String itemCode, @RequestParam String languageId,
                                               @RequestParam Long companyCodeId, @RequestParam String plantId,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String packingNo,
                                               @RequestParam String loginUserID) {
        packinglineService.deletePackingLine(languageId, companyCodeId, plantId, warehouseId, preOutboundNo,
                                             refDocNumber, partnerCode, lineNumber, packingNo, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}