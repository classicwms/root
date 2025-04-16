package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.packing.SearchPackingLine;
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

import com.tekclover.wms.api.transaction.model.outbound.packing.AddPackingLine;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingLine;
import com.tekclover.wms.api.transaction.model.outbound.packing.UpdatePackingLine;
import com.tekclover.wms.api.transaction.service.PackingLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PackingLine"}, value = "PackingLine  Operations related to PackingLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PackingLine ",description = "Operations related to PackingLine ")})
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
	public ResponseEntity<?> getPackingLine(@PathVariable String itemCode, @RequestParam String languageId, @RequestParam Long companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String packingNo) {
    	PackingLine packingline = packinglineService.getPackingLine(languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode);
    	log.info("PackingLine : " + packingline);
		return new ResponseEntity<>(packingline, HttpStatus.OK);
	}

	@ApiOperation(response = PackingLine.class, value = "Search PackingLine") // label for swagger
	@PostMapping("/findPackingLine")
	public List<PackingLine> findPackingLine(@RequestBody SearchPackingLine searchPackingLine)
			throws Exception {
		return packinglineService.findPackingLine(searchPackingLine);
	}

    @ApiOperation(response = PackingLine.class, value = "Create PackingLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPackingLine(@Valid @RequestBody List<AddPackingLine> newPackingLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		List<PackingLine> createdPackingLine = packinglineService.createPackingLine(newPackingLine, loginUserID);
		return new ResponseEntity<>(createdPackingLine , HttpStatus.OK);
	}

    @ApiOperation(response = PackingLine.class, value = "Update PackingLine") // label for swagger
    @PatchMapping("/{itemCode}")
	public ResponseEntity<?> patchPackingLine(@PathVariable String itemCode, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String packingNo,
			@Valid @RequestBody UpdatePackingLine updatePackingLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PackingLine createdPackingLine =
				packinglineService.updatePackingLine(languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode, loginUserID, updatePackingLine);
		return new ResponseEntity<>(createdPackingLine , HttpStatus.OK);
	}

    @ApiOperation(response = PackingLine.class, value = "Delete PackingLine") // label for swagger
	@DeleteMapping("/{itemCode}")
	public ResponseEntity<?> deletePackingLine(@PathVariable String itemCode, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String packingNo, @RequestParam String loginUserID) {
    	packinglineService.deletePackingLine(languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, packingNo, itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}