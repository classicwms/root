package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.SearchPreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.outbound.v2.PickListHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundHeader;
import com.tekclover.wms.api.transaction.service.PreOutboundHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PreOutboundHeader"}, value = "PreOutboundHeader  Operations related to PreOutboundHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreOutboundHeader ", description = "Operations related to PreOutboundHeader ")})
@RequestMapping("/preoutboundheader")
@RestController
public class PreOutboundHeaderController {

    @Autowired
    PreOutboundHeaderService preoutboundheaderService;

    @ApiOperation(response = PreOutboundHeader.class, value = "Get all PreOutboundHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PreOutboundHeader> preoutboundheaderList = preoutboundheaderService.getPreOutboundHeaders();
        return new ResponseEntity<>(preoutboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundHeader.class, value = "Get a PreOutboundHeader") // label for swagger 
    @GetMapping("/{preOutboundNo}")
    public ResponseEntity<?> getPreOutboundHeader(@PathVariable String preOutboundNo,
                                                  @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
        PreOutboundHeader preoutboundheader =
                preoutboundheaderService.getPreOutboundHeader(warehouseId, refDocNumber, preOutboundNo, partnerCode);
        log.info("PreOutboundHeader : " + preoutboundheader);
        return new ResponseEntity<>(preoutboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader") // label for swagger
    @PostMapping("/findPreOutboundHeader")
    public List<PreOutboundHeader> findPreOutboundHeader(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader)
            throws Exception {
        return preoutboundheaderService.findPreOutboundHeader(searchPreOutboundHeader);
    }

    //Stream - JPA
    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
    @PostMapping("/findPreOutboundHeaderNew")
    public Stream<PreOutboundHeader> findPreOutboundHeaderNew(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader)
            throws Exception {
        return preoutboundheaderService.findPreOutboundHeaderNew(searchPreOutboundHeader);
    }

    @ApiOperation(response = PreOutboundHeader.class, value = "Create PreOutboundHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPreOutboundHeader(@Valid @RequestBody AddPreOutboundHeader newPreOutboundHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreOutboundHeader createdPreOutboundHeader = preoutboundheaderService.createPreOutboundHeader(newPreOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreOutboundHeader, HttpStatus.OK);
    }

//    @ApiOperation(response = PreOutboundHeader.class, value = "Update PreOutboundHeader") // label for swagger
//    @PatchMapping("/{preOutboundNo}")
//	public ResponseEntity<?> patchPreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
//			@Valid @RequestBody UpdatePreOutboundHeader updatePreOutboundHeader, @RequestParam String loginUserID) 
//			throws IllegalAccessException, InvocationTargetException {
//		PreOutboundHeader createdPreOutboundHeader = 
//				preoutboundheaderService.updatePreOutboundHeader(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, loginUserID);
//		return new ResponseEntity<>(createdPreOutboundHeader , HttpStatus.OK);
//	}
//    
//    @ApiOperation(response = PreOutboundHeader.class, value = "Delete PreOutboundHeader") // label for swagger
//	@DeleteMapping("/{preOutboundNo}")
//	public ResponseEntity<?> deletePreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID) {
//    	preoutboundheaderService.deletePreOutboundHeader(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}

    //=======================================================V2============================================================
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Get all PreOutboundHeader details")    // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPreOutboundHeaderV2() {
        List<PreOutboundHeaderV2> preoutboundheaderList = preoutboundheaderService.getPreOutboundHeadersV2();
        return new ResponseEntity<>(preoutboundheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Get a PreOutboundHeader") // label for swagger
    @GetMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> getPreOutboundHeaderV2(@PathVariable String preOutboundNo, String companyCodeId, String plantId, String languageId,
                                                    @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode) {
        PreOutboundHeaderV2 preoutboundheader =
                preoutboundheaderService.getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, partnerCode);
        log.info("PreOutboundHeader : " + preoutboundheader);
        return new ResponseEntity<>(preoutboundheader, HttpStatus.OK);
    }

    //Stream - JPA
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Search PreOutboundHeader New") // label for swagger
    @PostMapping("/v2/findPreOutboundHeader")
    public Stream<PreOutboundHeaderV2> findPreOutboundHeaderV2(@RequestBody SearchPreOutboundHeaderV2 searchPreOutboundHeader)
            throws Exception {
        return preoutboundheaderService.findPreOutboundHeaderV2(searchPreOutboundHeader);
    }

    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Create PreOutboundHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPreOutboundHeaderV2(@Valid @RequestBody PreOutboundHeaderV2 newPreOutboundHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreOutboundHeaderV2 createdPreOutboundHeader = preoutboundheaderService.createPreOutboundHeaderV2(newPreOutboundHeader, loginUserID);
        return new ResponseEntity<>(createdPreOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Update PreOutboundHeader") // label for swagger
    @PatchMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> patchPreOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                      @Valid @RequestBody PreOutboundHeaderV2 updatePreOutboundHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreOutboundHeaderV2 createdPreOutboundHeader =
                preoutboundheaderService.updatePreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                        refDocNumber, preOutboundNo, partnerCode, loginUserID, updatePreOutboundHeader);
        return new ResponseEntity<>(createdPreOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Delete PreOutboundHeader") // label for swagger
    @DeleteMapping("/v2/{preOutboundNo}")
    public ResponseEntity<?> deletePreOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                       @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                       @RequestParam String partnerCode, @RequestParam String loginUserID) {
        preoutboundheaderService.deletePreOutboundHeaderV2(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @ApiOperation(response = PickListHeader.class, value = "Cancel Order") // label for swagger
    @PostMapping("/v2/orderCancellation")
    public ResponseEntity<?> orderCancellation(@RequestBody List<OutboundOrderCancelInput> outboundOrderCancelInput) throws Exception {
        List<PreOutboundHeaderV2> orderCancelled = preoutboundheaderService.orderCancellation(outboundOrderCancelInput);
        return new ResponseEntity<>(orderCancelled, HttpStatus.OK);
    }
}