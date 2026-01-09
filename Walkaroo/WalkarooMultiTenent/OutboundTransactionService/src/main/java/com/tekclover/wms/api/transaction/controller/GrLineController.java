package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.dto.GrReversalResponse;
import com.tekclover.wms.api.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.SearchGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.transaction.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.transaction.model.inbound.gr.SearchGrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.UpdateGrLine;
import com.tekclover.wms.api.transaction.service.GrLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"GrLine"}, value = "GrLine  Operations related to GrLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "GrLine ",description = "Operations related to GrLine ")})
@RequestMapping("/grline")
@RestController
public class GrLineController {
	
	@Autowired
	GrLineService grlineService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	BaseService baseService;
	
    @ApiOperation(response = GrLine.class, value = "Get all GrLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<GrLine> grlineList = grlineService.getGrLines();
		return new ResponseEntity<>(grlineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger 
	@GetMapping("/{lineNo}")
	public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String itemCode) {

    	GrLine grline = grlineService.getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode);
    	log.info("GrLine : " + grline);
		return new ResponseEntity<>(grline, HttpStatus.OK);
	}
    
    // PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger 
	@GetMapping("/{lineNo}/putawayline")
	public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String packBarcodes, @RequestParam String itemCode) {
    	List<GrLine> grline = grlineService.getGrLine(preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode);
    	log.info("GrLine : " + grline);
		return new ResponseEntity<>(grline, HttpStatus.OK);
	}
    
    @ApiOperation(response = GrLine.class, value = "Search GrLine") // label for swagger
	@PostMapping("/findGrLine")
	public List<GrLine> findGrLine(@RequestBody SearchGrLine searchGrLine)
			throws Exception {
		return grlineService.findGrLine(searchGrLine);
	}
    
    @ApiOperation(response = GrLine.class, value = "Create GrLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postGrLine(@Valid @RequestBody List<AddGrLine> newGrLine, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(newGrLine.get(0).getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
			List<GrLine> createdGrLine = grlineService.createGrLine(newGrLine, loginUserID);
		return new ResponseEntity<>(createdGrLine , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = GrLine.class, value = "Update GrLine") // label for swagger
    @PatchMapping("/{lineNo}")
	public ResponseEntity<?> patchGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @Valid @RequestBody UpdateGrLine updateGrLine, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(updateGrLine.getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		GrLine createdGrLine = 
				grlineService.updateGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
						packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
		return new ResponseEntity<>(createdGrLine , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = GrLine.class, value = "Delete GrLine") // label for swagger
	@DeleteMapping("/{lineNo}")
	public ResponseEntity<?> deleteGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, 
			@RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
    	grlineService.deleteGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
    			packBarcodes, lineNo, itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    //-----------------PACK_BARCODE-GENERATION----------------------------------------------------------------------------
    @ApiOperation(response = GrLine.class, value = "Get a PackBarcode") // label for swagger 
	@GetMapping("/packBarcode")
	public ResponseEntity<?> getPackBarcode(@RequestParam Long acceptQty, @RequestParam Long damageQty, 
			@RequestParam String warehouseId, @RequestParam String loginUserID) {
    	List<PackBarcode> packBarcodes = grlineService.generatePackBarcode (acceptQty, damageQty, warehouseId, loginUserID);
    	log.info("packBarcodes : " + packBarcodes);
		return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
	}

	//=========================================================V2=================================================================//

	@ApiOperation(response = GrLineV2.class, value = "Get a GrLineV2") // label for swagger
	@GetMapping("/v2/{lineNo}")
	public ResponseEntity<?> getGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
										 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
										 @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
										 @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
										 @RequestParam String itemCode) {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		GrLineV2 grline = grlineService.getGrLineV2(companyCode, languageId, plantId, warehouseId, preInboundNo, refDocNumber,
				goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode );
		log.info("GrLine : " + grline);
		return new ResponseEntity<>(grline, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	// PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
	@ApiOperation(response = GrLineV2.class, value = "Get a GrLine V2") // label for swagger
	@GetMapping("/v2/{lineNo}/putawayline")
	public ResponseEntity<?> getGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
										 @RequestParam String plantId, @RequestParam String languageId,
										 @RequestParam String preInboundNo, @RequestParam String refDocNumber,
										 @RequestParam String packBarcodes, @RequestParam String itemCode) {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		List<GrLineV2> grline = grlineService.getGrLineV2(companyCode, languageId, plantId, preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode );
		log.info("GrLine : " + grline);
		return new ResponseEntity<>(grline, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = GrLineV2.class, value = "Search GrLine V2") // label for swagger
	@PostMapping("/findGrLine/v2")
	public Stream<GrLineV2> findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine)
			throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchGrLine.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		return grlineService.findGrLineV2(searchGrLine);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Grline - Report - to calculate lead time created on fetched from grheader created on using SQL query method
	@ApiOperation(response = GrLineV2.class, value = "Search GrLine V2 SQL") // label for swagger
	@PostMapping("/findGrLineNew/v2")
	public List<GrLineImpl> findGrLineV2SQL(@RequestBody SearchGrLineV2 searchGrLine)
			throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchGrLine.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		return grlineService.findGrLineSQLV2(searchGrLine);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine,
										  @RequestParam String loginUserID)
            throws Exception {
		try {
			String currentDB = baseService.getDataBase(newGrLine.get(0).getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
//		List<GrLineV2> createdGrLine = grlineService.createGrLineV2(newGrLine, loginUserID);
//		List<GrLineV2> createdGrLine = grlineService.createGrLineNonCBMV2(newGrLine, loginUserID);
		List<GrLineV2> createdGrLine = grlineService.createGrLinev3(newGrLine,loginUserID);
		return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = GrLineV2.class, value = "Update GrLine V2") // label for swagger
	@PatchMapping("/v2/{lineNo}")
	public ResponseEntity<?> patchGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
										   @RequestParam String plantId, @RequestParam String languageId,
										   @RequestParam String warehouseId, @RequestParam String preInboundNo,
										   @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
										   @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
										   @RequestParam String itemCode, @Valid @RequestBody GrLineV2 updateGrLine,
										   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		GrLineV2 createdGrLine =
				grlineService.updateGrLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
						goodsReceiptNo, palletCode, caseCode,
						packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
		return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = GrLineV2.class, value = "Delete GrLine V2") // label for swagger
	@DeleteMapping("/v2/{lineNo}")
	public ResponseEntity<?> deleteGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode, @RequestParam String plantId,
											@RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preInboundNo,
											@RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode,
											@RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode,
											@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		grlineService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId,
				preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
				caseCode, packBarcodes, lineNo, itemCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//-----------------PACK_BARCODE-GENERATION----------------------------------------------------------------------------

	/**
	 * @param acceptQty
	 * @param damageQty
	 * @param warehouseId
	 * @param companyCode
	 * @param plantId
	 * @param languageId
	 * @param loginUserID
	 * @return
	 */
	@ApiOperation(response = GrLineV2.class, value = "Get a PackBarcode") // label for swagger
	@GetMapping("/packBarcode/v2")
	public ResponseEntity<?> getPackBarcodeV2(@RequestParam Double acceptQty, @RequestParam Double damageQty,
											  @RequestParam String warehouseId, @RequestParam String companyCode,
											  @RequestParam String plantId, @RequestParam String languageId,
											  @RequestParam String loginUserID) {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		List<PackBarcode> packBarcodes = grlineService.generatePackBarcodeV2(companyCode, languageId, plantId, acceptQty, damageQty, warehouseId, loginUserID);
		log.info("packBarcodes : " + packBarcodes);
		return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	//-----------------BATCH-SERIAL-GENERATION----------------------------------------------------------------------------

	@ApiOperation(response = Optional.class, value = "Get a Batch Serial Number") // label for swagger
	@GetMapping("/batchSerialNumber/v2")
	public ResponseEntity<?> getBatchSerialNumber(@RequestParam String warehouseId, @RequestParam String companyCode,
												  @RequestParam String plantId, @RequestParam String languageId,
												  @RequestParam String storageMethod) {

		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
			String batchSerial = grlineService.generateBatchSerial(companyCode, plantId, languageId, warehouseId, storageMethod);
		return new ResponseEntity<>(batchSerial, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = GrReversalResponse.class, value = "Gr Reversal Updation")
	@PostMapping("/gr/reversal")
	public ResponseEntity<?> grReversalUpdate(@RequestBody List<PreInboundLineEntityV2> preInboundLineEntityV2List, @RequestParam String loginUserID) {
		try {
			String currentDB = baseService.getDataBase(preInboundLineEntityV2List.get(0).getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		GrReversalResponse response = grlineService.grReversalUpdate(preInboundLineEntityV2List, loginUserID);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}
