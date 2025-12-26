package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.impl.PutAwayHeaderImpl;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.InboundReversalInput;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.SearchPutAwayHeaderV2;
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

import com.tekclover.wms.api.transaction.model.inbound.putaway.AddPutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayHeader;
import com.tekclover.wms.api.transaction.service.PutAwayHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PutAwayHeader"}, value = "PutAwayHeader  Operations related to PutAwayHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PutAwayHeader ",description = "Operations related to PutAwayHeader ")})
@RequestMapping("/putawayheader")
@RestController
public class PutAwayHeaderController {
	
	@Autowired
	PutAwayHeaderService putawayheaderService;
	
    @ApiOperation(response = PutAwayHeader.class, value = "Get all PutAwayHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PutAwayHeader> putawayheaderList = putawayheaderService.getPutAwayHeaders();
		return new ResponseEntity<>(putawayheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger 
	@GetMapping("/{putAwayNumber}")
	public ResponseEntity<?> getPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String proposedStorageBin) {
    	PutAwayHeader putawayheader = putawayheaderService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
//    	log.info("PutAwayHeader : " + putawayheader);
		return new ResponseEntity<>(putawayheader, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger 
	@GetMapping("/{putAwayNumber}/status")
	public ResponseEntity<?> getPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber) {
    	List<PutAwayHeader> putawayheader = putawayheaderService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, putAwayNumber);
//    	log.info("PutAwayHeader : " + putawayheader);
		return new ResponseEntity<>(putawayheader, HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger 
	@GetMapping("/{refDocNumber}/inboundreversal/asn")
	public ResponseEntity<?> getPutAwayHeaderForASN(@PathVariable String refDocNumber) {
    	List<PutAwayHeader> putawayheader = putawayheaderService.getPutAwayHeader(refDocNumber);
//    	log.info("PutAwayHeader : " + putawayheader);
		return new ResponseEntity<>(putawayheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader") // label for swagger
	@PostMapping("/findPutAwayHeader")
	public List<PutAwayHeader> findPutAwayHeader(@RequestBody SearchPutAwayHeader searchPutAwayHeader)
			throws Exception {
		return putawayheaderService.findPutAwayHeader(searchPutAwayHeader);
	}

	//Stream
	@ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader New") // label for swagger
	@PostMapping("/findPutAwayHeaderNew")
	public Stream<PutAwayHeader> findPutAwayHeaderNew(@RequestBody SearchPutAwayHeader searchPutAwayHeader)
			throws Exception {
		return putawayheaderService.findPutAwayHeaderNew(searchPutAwayHeader);
	}

    @ApiOperation(response = PutAwayHeader.class, value = "Create PutAwayHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPutAwayHeader(@Valid @RequestBody AddPutAwayHeader newPutAwayHeader, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader createdPutAwayHeader = putawayheaderService.createPutAwayHeader(newPutAwayHeader, loginUserID);
		return new ResponseEntity<>(createdPutAwayHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
    @PatchMapping("/{putAwayNumber}")
	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String proposedStorageBin, @Valid @RequestBody UpdatePutAwayHeader updatePutAwayHeader, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader createdPutAwayHeader = 
				putawayheaderService.updatePutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, 
						caseCode, packBarcodes, putAwayNumber, proposedStorageBin, loginUserID, updatePutAwayHeader);
		return new ResponseEntity<>(createdPutAwayHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
    @PatchMapping("/{refDocNumber}/reverse")
	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		putawayheaderService.updatePutAwayHeader(refDocNumber, packBarcodes, loginUserID);
		return new ResponseEntity<>(HttpStatus.OK);
	}
    
    @ApiOperation(response = PutAwayHeader.class, value = "Delete PutAwayHeader") // label for swagger
	@DeleteMapping("/{putAwayNumber}")
	public ResponseEntity<?> deletePutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String proposedStorageBin, @RequestParam String loginUserID) {
    	putawayheaderService.deletePutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
    			packBarcodes, putAwayNumber, proposedStorageBin, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//=============================================================V2==================================================================//

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/v2/{putAwayNumber}")
    public ResponseEntity<?> getPutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                @RequestParam String proposedStorageBin) {
        PutAwayHeaderV2 putawayheader = putawayheaderService.getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
    	log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/{putAwayNumber}/status/v2")
    public ResponseEntity<?> getPutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId,
                                                @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber) {
        List<PutAwayHeaderV2> putawayheader = putawayheaderService.getPutAwayHeaderV2(warehouseId, preInboundNo, refDocNumber,
                putAwayNumber, companyCode, plantId, languageId);
    	log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/{refDocNumber}/inboundreversal/asn/v2")
    public ResponseEntity<?> getPutAwayHeaderForASNV2(@RequestParam String companyCode, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String warehouseId, @PathVariable String refDocNumber) {
        List<PutAwayHeaderV2> putawayheader = putawayheaderService.getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber);
//    	log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Create PutAwayHeader V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPutAwayHeaderV2(@Valid @RequestBody PutAwayHeaderV2 newPutAwayHeader, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayHeaderV2 createdPutAwayHeader = putawayheaderService.createPutAwayHeaderV2(newPutAwayHeader, loginUserID);
        return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
    }

//    @ApiOperation(response = PutAwayHeaderV2.class, value = "Search PutAwayHeader V2") // label for swagger
//    @PostMapping("/findPutAwayHeader/v2")
//    public List<PutAwayHeaderV2> findPutAwayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeader)
//            throws Exception {
//        return putawayheaderService.findPutAwayHeaderV2(searchPutAwayHeader);
//    }
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Search PutAwayHeader V2") // label for swagger
    @PostMapping("/findPutAwayHeader/v2")
    public List<PutAwayHeaderImpl> findPutAwayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeader)
            throws Exception {
        return putawayheaderService.findPutAwayHeaderSQLV2(searchPutAwayHeader);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
    @PatchMapping("/v2/{putAwayNumber}")
    public ResponseEntity<?> patchPutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId,
                                                  @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                  @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                  @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayHeaderV2 updatePutAwayHeader,
                                                  @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayHeaderV2 createdPutAwayHeader =
                putawayheaderService.updatePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                        caseCode, packBarcodes, putAwayNumber, proposedStorageBin, loginUserID, updatePutAwayHeader);
        return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
    @PatchMapping("/{refDocNumber}/reverse/v2")
    public ResponseEntity<?> patchPutAwayHeaderV2(@PathVariable String refDocNumber, @RequestParam String packBarcodes, @RequestParam String warehouseId,
                                                  @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String loginUserID) throws ParseException {
        putawayheaderService.updatePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                refDocNumber, packBarcodes, loginUserID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader Reversal V2") // label for swagger
    @PatchMapping("/reverse/batch/v2")
    public ResponseEntity<?> batchPutAwayHeaderReversalV2(@RequestBody List<InboundReversalInput> inboundReversalInputs, @RequestParam String loginUserID) throws ParseException {
        putawayheaderService.batchPutAwayReversal(inboundReversalInputs, loginUserID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Delete PutAwayHeader V2") // label for swagger
    @DeleteMapping("/v2/{putAwayNumber}")
    public ResponseEntity<?> deletePutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId,
                                                   @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                   @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                   @RequestParam String proposedStorageBin, @RequestParam String loginUserID) {
        putawayheaderService.deletePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                packBarcodes, putAwayNumber, proposedStorageBin, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

	@ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
	@PatchMapping("/v2/putAway")
	public ResponseEntity<?> patchPutAwayHeaderBatchV2(@Valid @RequestBody List <PutAwayHeaderV2> updatePutAwayHeader,
												  @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		List <PutAwayHeaderV2> createdPutAwayHeader =
				putawayheaderService.updatePutAwayHeaderBatchV2(loginUserID, updatePutAwayHeader);
		return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
	}

}