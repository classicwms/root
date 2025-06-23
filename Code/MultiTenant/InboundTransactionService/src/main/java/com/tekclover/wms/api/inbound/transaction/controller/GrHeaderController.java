package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.SearchGrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
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

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.AddGrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.SearchGrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.UpdateGrHeader;
import com.tekclover.wms.api.inbound.transaction.service.GrHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"GrHeader"}, value = "GrHeader  Operations related to GrHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "GrHeader ",description = "Operations related to GrHeader ")})
@RequestMapping("/grheader")
@RestController
public class GrHeaderController {
	
	@Autowired
	GrHeaderService grheaderService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	WarehouseRepository warehouseRepository;
	
    @ApiOperation(response = GrHeader.class, value = "Get all GrHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<GrHeader> grheaderList = grheaderService.getGrHeaders();
		return new ResponseEntity<>(grheaderList, HttpStatus.OK);
	}
    
    @ApiOperation(response = GrHeader.class, value = "Get a GrHeader") // label for swagger 
	@GetMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> getGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode) {
    	GrHeader grheader = grheaderService.getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, 
    			palletCode, caseCode);
    	log.info("GrHeader : " + grheader);
		return new ResponseEntity<>(grheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = GrHeader.class, value = "Search GrHeader") // label for swagger
	@PostMapping("/findGrHeader")
	public List<GrHeader> findGrHeader(@RequestBody SearchGrHeader searchGrHeader)
			throws Exception {
		return grheaderService.findGrHeader(searchGrHeader);
	}
	//Stream
	@ApiOperation(response = GrHeader.class, value = "Search GrHeader New") // label for swagger
	@PostMapping("/findGrHeaderNew")
	public Stream<GrHeader> findGrHeaderNew(@RequestBody SearchGrHeader searchGrHeader)
			throws Exception {
		return grheaderService.findGrHeaderNew(searchGrHeader);
	}
    @ApiOperation(response = GrHeader.class, value = "Create GrHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postGrHeader(@Valid @RequestBody AddGrHeader newGrHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newGrHeader.getCompanyCodeId(), newGrHeader.getPlantId(), newGrHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			GrHeader createdGrHeader = grheaderService.createGrHeader(newGrHeader, loginUserID);
			return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = GrHeader.class, value = "Update GrHeader") // label for swagger
    @PatchMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> patchGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode,
			@Valid @RequestBody UpdateGrHeader updateGrHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(updateGrHeader.getCompanyCodeId(), updateGrHeader.getPlantId(), updateGrHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			GrHeader createdGrHeader =
					grheaderService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, updateGrHeader);
			return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
    
//    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
//    @PatchMapping("/{refDocNumber}/reverse")
//	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes, 
//			@RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String caseCode, 
//			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
//    	grheaderService.updatePutAwayHeader(refDocNumber, packBarcodes, warehouseId, preInboundNo, caseCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
    
    @ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
	@DeleteMapping("/{goodsReceiptNo}")
	public ResponseEntity<?> deleteGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID) {
    	grheaderService.deleteGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
//--------------------------------------------------------------V2-----------------------------------------------------------------------------//

	/**
	 * @param searchGrHeader
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(response = GrHeaderV2.class, value = "Search GrHeader V2") // label for swagger
	@PostMapping("/findGrHeader/v2")
	public List<GrHeaderV2> findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeader)
			throws Exception {
        try {
			log.info("searchGrHeader ------> {}", searchGrHeader);
			String routingDb = null;
			DataBaseContextHolder.setCurrentDb("MT");
			Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchGrHeader.getWarehouseId().get(0), 0L);
			routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
            return grheaderService.findGrHeaderV2(searchGrHeader);
        } finally {
			DataBaseContextHolder.clear();
        }
    }

	@ApiOperation(response = GrHeaderV2.class, value = "Get a GrHeader V2") // label for swagger
	@GetMapping("/v2/{goodsReceiptNo}")
	public ResponseEntity<?> getGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
										   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
										   @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
										   @RequestParam String palletCode, @RequestParam String caseCode) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			GrHeaderV2 grheader = grheaderService.getGrHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
					palletCode, caseCode);
			log.info("GrHeader : " + grheader);
			return new ResponseEntity<>(grheader, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	/**
	 * @param newGrHeader
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@ApiOperation(response = GrHeaderV2.class, value = "Create GrHeader V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postGrHeaderV2(@Valid @RequestBody GrHeaderV2 newGrHeader, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newGrHeader.getCompanyCode(), newGrHeader.getPlantId(), newGrHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			GrHeaderV2 createdGrHeader = grheaderService.createGrHeaderV2(newGrHeader, loginUserID);
			return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = GrHeaderV2.class, value = "Update GrHeader V2") // label for swagger
	@PatchMapping("/v2/{goodsReceiptNo}")
	public ResponseEntity<?> patchGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
											 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
											 @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
											 @RequestParam String palletCode, @RequestParam String caseCode,
											 @Valid @RequestBody GrHeaderV2 updateGrHeader, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			GrHeaderV2 createdGrHeader =
					grheaderService.updateGrHeaderV2(companyCode, languageId, plantId, warehouseId,
							preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, updateGrHeader);
			return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = GrHeaderV2.class, value = "Delete GrHeader V2") // label for swagger
	@DeleteMapping("/v2/{goodsReceiptNo}")
	public ResponseEntity<?> deleteGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
											  @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
											  @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
											  @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			grheaderService.deleteGrHeaderV2(companyCode, languageId, plantId, warehouseId,
					preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
					palletCode, caseCode, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
}