package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.dto.FetchImpl;
import com.tekclover.wms.api.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.SearchGrLineV2;
import com.tekclover.wms.api.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.transaction.service.GrLineService;
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
import java.util.Optional;
import java.util.stream.Stream;

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
    DbConfigRepository dbConfigRepository;

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
        List<GrLine> createdGrLine = grlineService.createGrLine(newGrLine, loginUserID);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    @ApiOperation(response = GrLine.class, value = "Update GrLine") // label for swagger
    @PatchMapping("/{lineNo}")
    public ResponseEntity<?> patchGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                         @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                         @RequestParam String itemCode, @Valid @RequestBody UpdateGrLine updateGrLine,
                                         @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        GrLine createdGrLine =
                grlineService.updateGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                                           packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
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
        GrLineV2 grline = grlineService.getGrLineV2(companyCode, languageId, plantId, warehouseId, preInboundNo, refDocNumber,
                                                    goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode);
        log.info("GrLine : " + grline);
        return new ResponseEntity<>(grline, HttpStatus.OK);
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
    public Stream<GrLineV2> findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine)
            throws Exception {
        return grlineService.findGrLineV2(searchGrLine);
    }

    //Grline - Report - to calculate lead time created on fetched from grheader created on using SQL query method
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine V2 SQL") // label for swagger
    @PostMapping("/findGrLineNew/v2")
    public List<GrLineImpl> findGrLineV2SQL(@RequestBody SearchGrLineV2 searchGrLine)
            throws Exception {
        return grlineService.findGrLineSQLV2(searchGrLine);
    }

    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine,
                                          @RequestParam String loginUserID)
            throws Exception {
//		List<GrLineV2> createdGrLine = grlineService.createGrLineV2(newGrLine, loginUserID);
//        List<GrLineV2> createdGrLine = grlineService.createGrLineNonCBMV2(newGrLine, loginUserID);
//        List<GrLineV2> createdGrLine = grlineService.createGrLineNonCBMV4(newGrLine, loginUserID);
        List<GrLineV2> createdGrLine = grLineProcess(newGrLine, loginUserID);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    @ApiOperation(response = GrLineV2.class, value = "Create Putaway Header V2") // label for swagger
    @PostMapping("/v2/create")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody FetchImpl fetch) {
        grlineService.createPutAwayHeaderFromInboundQualityLine(fetch);
        return new ResponseEntity<>(HttpStatus.OK);
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
        GrLineV2 createdGrLine =
                grlineService.updateGrLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                                             goodsReceiptNo, palletCode, caseCode,
                                             packBarcodes, lineNo, itemCode, loginUserID, updateGrLine);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    @ApiOperation(response = GrLineV2.class, value = "Delete GrLine V2") // label for swagger
    @DeleteMapping("/v2/{lineNo}")
    public ResponseEntity<?> deleteGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                            @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode,
                                            @RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode,
                                            @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        grlineService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId,
                                     preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                                     caseCode, packBarcodes, lineNo, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        List<PackBarcode> packBarcodes = grlineService.generatePackBarcodeV2(companyCode, languageId, plantId, acceptQty, damageQty, warehouseId, loginUserID);
        log.info("packBarcodes : " + packBarcodes);
        return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
    }

    //-----------------BATCH-SERIAL-GENERATION----------------------------------------------------------------------------

    @ApiOperation(response = Optional.class, value = "Get a Batch Serial Number") // label for swagger
    @GetMapping("/batchSerialNumber/v2")
    public ResponseEntity<?> getBatchSerialNumber(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                  @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String storageMethod) {
        String batchSerial = grlineService.generateBatchSerial(companyCode, plantId, languageId, warehouseId, storageMethod);
        return new ResponseEntity<>(batchSerial, HttpStatus.OK);
    }

    private List<GrLineV2> grLineProcess(List<AddGrLineV2> grLineV2List, String loginUserID ) throws Exception {
        if(grLineV2List != null && !grLineV2List.isEmpty()) {
            String companyCodeId = grLineV2List.get(0).getCompanyCode();
            String plantId = grLineV2List.get(0).getPlantId();
            String languageId = grLineV2List.get(0).getLanguageId();
            String warehouseId = grLineV2List.get(0).getWarehouseId();
            //            String profile = grlineService.getProfile(companyCodeId, plantId, languageId, warehouseId);

            String profile = dbConfigRepository.getDbName(companyCodeId,plantId,warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}",profile);

            log.info("Profile: " + companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + profile);
            if (profile != null) {
//                profile = profile.strip().toLowerCase();
                switch (profile) {
                    case "WK":
                        return grlineService.createGrLineV3(grLineV2List, loginUserID);
                    case "IMPEX":
                        return grlineService.createGrLineNonCBMV4(grLineV2List, loginUserID);
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}