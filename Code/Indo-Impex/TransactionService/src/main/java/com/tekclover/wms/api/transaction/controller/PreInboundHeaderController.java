package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.*;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.SearchPreInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.service.PreInboundHeaderService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PreInboundHeader"}, value = "PreInboundHeader  Operations related to PreInboundHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreInboundHeader ", description = "Operations related to PreInboundHeader ")})
@RequestMapping("/preinboundheader")
@RestController
public class PreInboundHeaderController {

    @Autowired
    PreInboundHeaderService preinboundheaderService;

    /*----------------------PROCESS-INBOUND-RECEIVED----------------------------------------------*/
    /*
     * Process the Preinbound Integraion data
     */
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Process ASN") // label for swagger
    @PostMapping("/{refDocNumber}/processInboundReceived")
    public ResponseEntity<?> processInboundReceived(@PathVariable String refDocNumber,
                                                    @RequestBody InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
        preinboundheaderService.processInboundReceived(refDocNumber, inboundIntegrationHeader);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get all PreInboundHeader details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PreInboundHeader> preinboundheaderList = preinboundheaderService.getPreInboundHeaders();
        return new ResponseEntity<>(preinboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get a PreInboundHeader") // label for swagger 
    @GetMapping("/{preInboundNo}")
    public ResponseEntity<?> getPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId) {
        PreInboundHeader preinboundheader = preinboundheaderService.getPreInboundHeader(preInboundNo, warehouseId);
//    	log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Get a PreInboundHeader") // label for swagger 
    @GetMapping("/inboundconfirm")
    public ResponseEntity<?> getPreInboundHeader(@RequestParam String warehouseId) {
        List<PreInboundHeader> preinboundheader = preinboundheaderService.getPreInboundHeaderWithStatusId(warehouseId);
//       	log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Search PreInboundHeader") // label for swagger
    @PostMapping("/findPreInboundHeader")
    public List<PreInboundHeaderEntity> findPreInboundHeader(@RequestBody SearchPreInboundHeader searchPreInboundHeader) throws Exception {
        return preinboundheaderService.findPreInboundHeader(searchPreInboundHeader);
    }

    //Streaming
    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Search PreInboundHeader New") // label for swagger
    @PostMapping("/findPreInboundHeaderNew")
    public Stream<PreInboundHeaderEntity> findPreInboundHeaderNew(@RequestBody SearchPreInboundHeader searchPreInboundHeader) throws Exception {
        return preinboundheaderService.findPreInboundHeaderNew(searchPreInboundHeader);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Create PreInboundHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPreInboundHeader(@Valid @RequestBody AddPreInboundHeader newPreInboundHeader,
                                                  @RequestParam String loginUserID) throws Exception {
        PreInboundHeaderEntity createdPreInboundHeader =
                preinboundheaderService.createPreInboundHeader(newPreInboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Update PreInboundHeader") // label for swagger
    @PatchMapping("/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                   @Valid @RequestBody UpdatePreInboundHeader updatePreInboundHeader,
                                                   @RequestParam String loginUserID) throws Exception {
        PreInboundHeader createdPreInboundHeader =
                preinboundheaderService.updatePreInboundHeader(preInboundNo, warehouseId, updatePreInboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntity.class, value = "Delete PreInboundHeader") // label for swagger
    @DeleteMapping("/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                    @RequestParam String loginUserID) {
        preinboundheaderService.deletePreInboundHeader(preInboundNo, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Process ASN") // label for swagger
    @PostMapping("/processASN")
    public ResponseEntity<?> processASN(@RequestBody List<AddPreInboundLine> newPreInboundLine,
                                        @RequestParam String loginUserID) throws Exception {
        StagingHeader createdStagingHeader = preinboundheaderService.processASN(newPreInboundLine, loginUserID);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }
//----------------------------------------------------------------V2-----------------------------------------------------------------------------//


    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Process Inbound Received") // label for swagger
    @PostMapping("/{refDocNumber}/processInboundReceived/v2")
    public ResponseEntity<?> processInboundReceivedV2(@PathVariable String refDocNumber,
                                                      @RequestBody InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
        preinboundheaderService.processInboundReceivedV2(refDocNumber, inboundIntegrationHeader);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Process ASN") // label for swagger
    @PostMapping("/processASN/v2")
    public ResponseEntity<?> processASNV2(@RequestBody List<PreInboundLineEntityV2> newPreInboundLine,
                                          @RequestParam String loginUserID) throws Exception {
        StagingHeaderV2 createdStagingHeader = preinboundheaderService.processASNV2(newPreInboundLine, loginUserID);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderV2.class, value = "Get a PreInboundHeader V2") // label for swagger
    @GetMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> getPreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                   @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String languageId) {
        PreInboundHeaderV2 preinboundheader = preinboundheaderService.getPreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId);
//    	log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Get a PreInboundHeader V2") // label for swagger
    @GetMapping("/inboundconfirm/v2")
    public ResponseEntity<?> getPreInboundHeaderV2(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId) {
        List<PreInboundHeaderV2> preinboundheader = preinboundheaderService.getPreInboundHeaderWithStatusIdV2(warehouseId, companyCode, plantId, languageId);
//       	log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Search PreInboundHeader V2") // label for swagger
    @PostMapping("/findPreInboundHeader/v2")
    public Stream<PreInboundHeaderEntityV2> findPreInboundHeaderV2(@RequestBody SearchPreInboundHeaderV2 searchPreInboundHeader)
            throws Exception {
        return preinboundheaderService.findPreInboundHeaderV2(searchPreInboundHeader);
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Create PreInboundHeader V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPreInboundHeaderV2(@Valid @RequestBody PreInboundHeaderV2 newPreInboundHeader,
                                                    @RequestParam String loginUserID) throws Exception {
        PreInboundHeaderEntityV2 createdPreInboundHeader =
                preinboundheaderService.createPreInboundHeaderV2(newPreInboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Update PreInboundHeader V2") // label for swagger
    @PatchMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                     @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                     @Valid @RequestBody PreInboundHeaderV2 updatePreInboundHeader, @RequestParam String loginUserID) throws Exception {
        PreInboundHeaderV2 createdPreInboundHeader =
                preinboundheaderService.updatePreInboundHeaderV2(companyCode, plantId,
                                                                 languageId, preInboundNo, warehouseId, updatePreInboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderEntityV2.class, value = "Delete PreInboundHeader V2") // label for swagger
    @DeleteMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                      @RequestParam String companyCode, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String loginUserID) throws Exception {
        preinboundheaderService.deletePreInboundHeaderV2(companyCode, plantId, languageId, preInboundNo, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}