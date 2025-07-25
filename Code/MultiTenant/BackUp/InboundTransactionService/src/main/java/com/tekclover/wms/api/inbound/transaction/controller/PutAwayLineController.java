package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.PutAwayLineService;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.SearchPutAwayLineV2;
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

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.AddPutAwayLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.SearchPutAwayLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.UpdatePutAwayLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PutAwayLine"}, value = "PutAwayLine  Operations related to PutAwayLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PutAwayLine ",description = "Operations related to PutAwayLine ")})
@RequestMapping("/putawayline")
@RestController
public class PutAwayLineController {
	
	@Autowired
    PutAwayLineService putawaylineService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = PutAwayLine.class, value = "Get all PutAwayLine details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PutAwayLine> putawaylineList = putawaylineService.getPutAwayLines();
		return new ResponseEntity<>(putawaylineList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> getPutAwayLine(@PathVariable List<String> confirmedStorageBin, @RequestParam String warehouseId, 
			@RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, 
			@RequestParam String proposedStorageBin) {
    	PutAwayLine putawayline = 
    			putawaylineService.getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, 
    					lineNo, itemCode, proposedStorageBin, confirmedStorageBin);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{lineNo}/inboundline")
	public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			 @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
    	List<PutAwayLine> putawayline = 
    			putawaylineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/{refDocNumber}/inboundreversal/palletId")
	public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable String refDocNumber) {
    	List<PutAwayLine> putawayline = putawaylineService.getPutAwayLine(refDocNumber);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayLine") // label for swagger
	@PostMapping("/findPutAwayLine")
	public List<PutAwayLine> findPutAwayLine(@RequestBody SearchPutAwayLine searchPutAwayLine)
			throws Exception {
		return putawaylineService.findPutAwayLine(searchPutAwayLine);
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPutAwayLine(@Valid @RequestBody AddPutAwayLine newPutAwayLine, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PutAwayLine createdPutAwayLine = putawaylineService.createPutAwayLine(newPutAwayLine, loginUserID);
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
   	@PostMapping("/confirm")
   	public ResponseEntity<?> postPutAwayLineConfirm(@Valid @RequestBody List<AddPutAwayLine> newPutAwayLine, 
   			@RequestParam String loginUserID) 
   			throws IllegalAccessException, InvocationTargetException {
		try {
			for (AddPutAwayLine putAwayLine : newPutAwayLine) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
			log.info("Request for putAwayLines to confirm : " + newPutAwayLine);
			List<PutAwayLine> createdPutAwayLine = putawaylineService.putAwayLineConfirm(newPutAwayLine, loginUserID);
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
   	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Update PutAwayLine") // label for swagger
    @PatchMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> patchPutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String warehouseId,
			@RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, 
			@RequestParam String proposedStorageBin, @Valid @RequestBody UpdatePutAwayLine updatePutAwayLine, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(updatePutAwayLine.getCompanyCodeId(), updatePutAwayLine.getPlantId(), updatePutAwayLine.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PutAwayLine createdPutAwayLine =
					putawaylineService.updatePutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo,
							itemCode, proposedStorageBin, confirmedStorageBin, loginUserID, updatePutAwayLine);
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = PutAwayLine.class, value = "Delete PutAwayLine") // label for swagger
	@DeleteMapping("/{confirmedStorageBin}")
	public ResponseEntity<?> deletePutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			putawaylineService.deletePutAwayLine(languageId, companyCodeId, plantId, warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, proposedStorageBin, confirmedStorageBin, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	//=============================================================V2==================================================================//

	@ApiOperation(response = PutAwayLineV2.class, value = "Get a PutAwayLine V2") // label for swagger
	@GetMapping("/v2/{confirmedStorageBin}")
	public ResponseEntity<?> getPutAwayLineV2(@PathVariable List<String> confirmedStorageBin, @RequestParam String companyCode,
											  @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
											  @RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
											  @RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
											  @RequestParam String proposedStorageBin) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PutAwayLineV2 putawayline =
					putawaylineService.getPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
							goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
							lineNo, itemCode, proposedStorageBin, confirmedStorageBin);
			log.info("PutAwayLine : " + putawayline);
			return new ResponseEntity<>(putawayline, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PutAwayLineV2.class, value = "Get a PutAwayLine V2") // label for swagger
	@GetMapping("/{lineNo}/inboundline/v2")
	public ResponseEntity<?> getPutAwayLineForInboundLineV2(@PathVariable Long lineNo, @RequestParam String warehouseId,
															@RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
															@RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<PutAwayLineV2> putawayline =
					putawaylineService.getPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
			log.info("PutAwayLine : " + putawayline);
			return new ResponseEntity<>(putawayline, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PutAwayLineV2.class, value = "Get a PutAwayLine V2") // label for swagger
	@GetMapping("/{refDocNumber}/inboundreversal/palletId/v2")
	public ResponseEntity<?> getPutAwayLineForInboundLineV2(@PathVariable String refDocNumber, @RequestParam String companyCode,
															@RequestParam String plantId, @RequestParam String languageId) {
		List<PutAwayLineV2> putawayline = putawaylineService.getPutAwayLineV2(companyCode, plantId, languageId, refDocNumber);
		log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
	@PostMapping("/confirm/v2")
	public ResponseEntity<?> postPutAwayLineConfirmV2(@Valid @RequestBody List<PutAwayLineV2> newPutAwayLine,
													  @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String profile = dbConfigRepository.getDbName(newPutAwayLine.get(0).getCompanyCode(), newPutAwayLine.get(0).getPlantId(), newPutAwayLine.get(0).getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(profile);
			List<PutAwayLineV2> createdPutAwayLine = null;
			if (profile != null) {
				switch (profile) {
					case "FAHAHEEL":
 					case "AUTO_LAP":
						createdPutAwayLine = putawaylineService.putAwayLineConfirmV2(newPutAwayLine, loginUserID);
						break;
					case "NAMRATHA":
						createdPutAwayLine = putawaylineService.putAwayLineConfirmNonCBMV4(newPutAwayLine, loginUserID);
						break;
					case "REEFERON":
						createdPutAwayLine = putawaylineService.putAwayLineConfirmNonCBMV5(newPutAwayLine, loginUserID);
						break;
						case "KNOWELL":
						createdPutAwayLine = putawaylineService.putAwayLineConfirmNonCBMV7(newPutAwayLine, loginUserID);
				}
			}
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

//	@ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
//	@PostMapping("/confirm/v2")
//	public ResponseEntity<?> postPutAwayLineConfirmV2(@Valid @RequestBody List<PutAwayLineV2> newPutAwayLine,
//													  @RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException, ParseException {
//
//		try {
//			for(PutAwayLineV2 lineV2 : newPutAwayLine) {
//				DataBaseContextHolder.setCurrentDb("MT");
//				String routingDb = dbConfigRepository.getDbName(lineV2.getCompanyCode(), lineV2.getPlantId(), lineV2.getWarehouseId());
//				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//				DataBaseContextHolder.clear();
//				DataBaseContextHolder.setCurrentDb(routingDb);
//			}
//			log.info("Request for putAwayLines to confirm : " + newPutAwayLine);
//			List<PutAwayLineV2> createdPutAwayLine = putawaylineService.putAwayLineConfirmV2(newPutAwayLine, loginUserID);
//			List<PutAwayLineV2> createdPutAwayLine = putawaylineService.putAwayLineConfirmNonCBMV4(newPutAwayLine, loginUserID);
//			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
//		} finally {
//			DataBaseContextHolder.clear();
//		}
//	}



	@ApiOperation(response = PutAwayLineV2.class, value = "Search PutAwayLine V2") // label for swagger
	@PostMapping("/findPutAwayLine/v2")
	public List<PutAwayLineV2> findPutAwayLineV2(@RequestBody SearchPutAwayLineV2 searchPutAwayLine)
			throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName1(searchPutAwayLine.getCompanyCodeId(), searchPutAwayLine.getPlantId(), searchPutAwayLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return putawaylineService.findPutAwayLineV2(searchPutAwayLine);
        }  finally {
			DataBaseContextHolder.clear();
        }
    }

	@ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postPutAwayLineV2(@Valid @RequestBody PutAwayLineV2 newPutAwayLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PutAwayLineV2 createdPutAwayLine = putawaylineService.createPutAwayLineV2(newPutAwayLine, loginUserID);
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PutAwayLineV2.class, value = "Update PutAwayLine V2") // label for swagger
	@PatchMapping("/v2/{confirmedStorageBin}")
	public ResponseEntity<?> patchPutAwayLineV2(@PathVariable String confirmedStorageBin, @RequestParam String languageId,
												@RequestParam String companyCode, @RequestParam String plantId, @RequestParam String warehouseId,
												@RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
												@RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
												@RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayLineV2 updatePutAwayLine,
												@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PutAwayLineV2 createdPutAwayLine =
					putawaylineService.updatePutAwayLinev2(companyCode, plantId, languageId, warehouseId,
							goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo,
							itemCode, proposedStorageBin, confirmedStorageBin, loginUserID, updatePutAwayLine);
			return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PutAwayLineV2.class, value = "Delete PutAwayLine V2") // label for swagger
	@DeleteMapping("/v2/{confirmedStorageBin}")
	public ResponseEntity<?> deletePutAwayLineV2(@PathVariable String confirmedStorageBin, @RequestParam String languageId,
												 @RequestParam String companyCode, @RequestParam String plantId,
												 @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
												 @RequestParam String preInboundNo, @RequestParam String refDocNumber,
												 @RequestParam String putAwayNumber, @RequestParam Long lineNo,
												 @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			putawaylineService.deletePutAwayLineV2(languageId, companyCode, plantId, warehouseId,
					goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
					lineNo, itemCode, proposedStorageBin, confirmedStorageBin, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	//----------------------------KNOWELL - V7 ----------------------------------//
	@ApiOperation(response = PutAwayLineV2.class, value = "InboundConfirmValidation") // label for swagger
	@GetMapping("/inboundManualConfirmation")
	public ResponseEntity<?> getInboundConfirmV7(@RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
												 @RequestParam String warehouseId, @RequestParam String refDocNumber,@RequestParam String preInboundNo,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			putawaylineService.inboundConfirmValidationV7(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID);
			return new ResponseEntity<>( HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
}