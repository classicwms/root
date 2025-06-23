package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDock;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDockEntity;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDockGrLineOrderManagementLine;
import com.tekclover.wms.api.inbound.transaction.model.dto.MultiDbInput;
import com.tekclover.wms.api.inbound.transaction.model.dto.StagingLineUpdate;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.inbound.transaction.service.CrossDockService;
import com.tekclover.wms.api.inbound.transaction.service.GrLineService;
import com.tekclover.wms.api.inbound.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.SearchGrLineV2;
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

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.AddGrLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.SearchGrLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.UpdateGrLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"GrLine"}, value = "GrLine  Operations related to GrLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "GrLine ", description = "Operations related to GrLine ")})
@RequestMapping("/grline")
@RestController
public class GrLineController {

    @Autowired
    GrLineService grlineService;

    @Autowired
    CrossDockService crossDockService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

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

//    @ApiOperation(response = GrLine.class, value = "Create GrLine") // label for swagger
//	@PostMapping("")
//	public ResponseEntity<?> postGrLine(@Valid @RequestBody List<AddGrLine> newGrLine,
//			@RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		try {
//			for (AddGrLine grLine : newGrLine) {
//				DataBaseContextHolder.setCurrentDb("IMF");
//				String routingDb = dbConfigRepository.getDbName(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getWarehouseId());
//				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//				DataBaseContextHolder.clear();
//				DataBaseContextHolder.setCurrentDb(routingDb);
//			}
//			List<GrLine> createdGrLine = grlineService.createGrLine(newGrLine, loginUserID);
//			return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
//		} finally {
//			DataBaseContextHolder.clear();
//		}
//	}

    @ApiOperation(response = GrLine.class, value = "Update GrLine") // label for swagger
    @PatchMapping("/{lineNo}")
    public ResponseEntity<?> patchGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                         @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                         @RequestParam String itemCode, @Valid @RequestBody UpdateGrLine updateGrLine,
                                         @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(updateGrLine.getCompanyCodeId(), updateGrLine.getPlantId(), updateGrLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            GrLine createdGrLine =
                    grlineService.updateGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                            packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
            return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
        } finally {
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
        List<PackBarcode> packBarcodes = grlineService.generatePackBarcode(acceptQty, damageQty, warehouseId, loginUserID);
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
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            GrLineV2 grline = grlineService.getGrLineV2(companyCode, languageId, plantId, warehouseId, preInboundNo, refDocNumber,
                    goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode);
            log.info("GrLine : " + grline);
            return new ResponseEntity<>(grline, HttpStatus.OK);
        } finally {
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
        List<GrLineV2> grline = grlineService.getGrLineV2(companyCode, languageId, plantId, preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode);
        log.info("GrLine : " + grline);
        return new ResponseEntity<>(grline, HttpStatus.OK);
    }

    @ApiOperation(response = GrLineV2.class, value = "Search GrLine V2") // label for swagger
    @PostMapping("/findGrLine/v2")
    public List<GrLineV2> findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine)
            throws Exception {
        log.info("SearchGrLineV2 ------> {}", searchGrLine);
        String warehouse = searchGrLine.getWarehouseId().get(0);
        String routingDb = null;
        DataBaseContextHolder.setCurrentDb("IMF");
        Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(warehouse, 0L);
        routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        return grlineService.findGrLineV2(searchGrLine);
    }

    //Grline - Report - to calculate lead time created on fetched from grheader created on using SQL query method
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine V2 SQL") // label for swagger
    @PostMapping("/findGrLineNew/v2")
    public List<GrLineImpl> findGrLineV2SQL(@RequestBody SearchGrLineV2 searchGrLine)
            throws Exception {
        return grlineService.findGrLineSQLV2(searchGrLine);
    }

//	@ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
//	@PostMapping("/v2")
//	public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine,
//										  @RequestParam String loginUserID)
//			throws IllegalAccessException, InvocationTargetException, ParseException {
////		List<GrLineV2> createdGrLine = grlineService.createGrLineV2(newGrLine, loginUserID);
//		try {
//			for (AddGrLineV2 grLineV2 : newGrLine) {
//				DataBaseContextHolder.setCurrentDb("IMF");
//				String routingDb = dbConfigRepository.getDbName(grLineV2.getCompanyCode(), grLineV2.getPlantId(), grLineV2.getWarehouseId());
//				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//				DataBaseContextHolder.clear();
//				DataBaseContextHolder.setCurrentDb(routingDb);
//			}
//
//			List<GrLineV2> createdGrLine = grlineService.createGrLineNonCBMV2(newGrLine, loginUserID);
//			return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
//		} finally {
//			DataBaseContextHolder.clear();
//		}
//	}

    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine,
                                          @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
//		List<GrLineV2> createdGrLine = grlineService.createGrLineV2(newGrLine, loginUserID);
        try {
            log.info("AddGrLineV2 -----> {}", newGrLine);
            DataBaseContextHolder.setCurrentDb("IMF");
            String profile = dbConfigRepository.getDbName(newGrLine.get(0).getCompanyCode(), newGrLine.get(0).getPlantId(), newGrLine.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            List<GrLineV2>  createGrLineResponse = null;
            if (profile != null) {
                switch (profile) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        createGrLineResponse = grlineService.createGrLineNonCBMV2(newGrLine, loginUserID);
                        break;
                    case "NAMRATHA":
                        createGrLineResponse = grlineService.createGrLineNonCBMV4(newGrLine, loginUserID);
                        break;
                    case "REEFERON":
                        createGrLineResponse = grlineService.createGrLineNonCBMV5(newGrLine, loginUserID);
                        break;
                    case "KNOWELL":
                        createGrLineResponse = grlineService.createGrLineNonCBMV7(newGrLine, loginUserID);
                        break;
                }
            }
            return new ResponseEntity<>(createGrLineResponse, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
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
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            GrLineV2 createdGrLine =
                    grlineService.updateGrLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                            goodsReceiptNo, palletCode, caseCode,
                            packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
            return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
        } finally {
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
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            grlineService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId,
                    preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                    caseCode, packBarcodes, lineNo, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
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
    public ResponseEntity<?> getPackBarcodeV2(@RequestParam Long acceptQty, @RequestParam Long damageQty,
                                              @RequestParam String warehouseId, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String languageId,
                                              @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<PackBarcode> packBarcodes = grlineService.generatePackBarcodeV2(companyCode, languageId, plantId, acceptQty, damageQty, warehouseId, loginUserID);
            log.info("packBarcodes : " + packBarcodes);
            return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //--------------------------------CrossDock--------------------------------------------//

    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/v6/crossDock")
    public ResponseEntity<?> postGrLineCrossDockV2(@Valid @RequestBody CrossDockGrLineOrderManagementLine newGrLine,
                                                   @RequestParam String loginUserID)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newGrLine.getGrLineV2List().get(0).getCompanyCode(),
                    newGrLine.getGrLineV2List().get(0).getPlantId(), newGrLine.getGrLineV2List().get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            CrossDockEntity createdGrLine = crossDockService.createGrLineCrossDock(newGrLine, loginUserID);
            return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = CrossDock.class, value = "Search crossDock")
    @PostMapping("/findCrossDock")
    public ResponseEntity<?> crossDock(@RequestBody MultiDbInput input) {
        try {
            log.info("CrossDock find input -----> {}", input);
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(input.getCompanyCode(), input.getPlantId(), input.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            CrossDock crossDock = crossDockService.findCrossDockDetails();
            return new ResponseEntity<>(crossDock, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }
}