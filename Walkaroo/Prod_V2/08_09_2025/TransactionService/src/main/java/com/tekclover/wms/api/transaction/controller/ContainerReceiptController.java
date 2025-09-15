package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.v2.SearchContainerReceiptV2;
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

import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.AddContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.ContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.SearchContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.UpdateContainerReceipt;
import com.tekclover.wms.api.transaction.service.ContainerReceiptService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ContainerReceipt"}, value = "ContainerReceipt  Operations related to ContainerReceiptController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ContainerReceipt ",description = "Operations related to ContainerReceipt ")})
@RequestMapping("/containerreceipt")
@RestController
public class ContainerReceiptController {
	
	@Autowired
	ContainerReceiptService containerreceiptService;
	
    @ApiOperation(response = ContainerReceipt.class, value = "Get all ContainerReceipt details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ContainerReceipt> containerreceiptList = containerreceiptService.getContainerReceipts();
		return new ResponseEntity<>(containerreceiptList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ContainerReceipt.class, value = "Get a ContainerReceipt") // label for swagger 
	@GetMapping("/{containerReceiptNo}")
	public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo) {
    	ContainerReceipt containerreceipt = 
    			containerreceiptService.getContainerReceipt(containerReceiptNo);
    	log.info("ContainerReceipt : " + containerreceipt);
		return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
	}
    
	@ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt") // label for swagger
	@PostMapping("/findContainerReceipt")
	public List<ContainerReceipt> findContainerReceipt(@RequestBody SearchContainerReceipt searchContainerReceipt)
			throws Exception {
		return containerreceiptService.findContainerReceipt(searchContainerReceipt);
	}

	//Stream
	@ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt New") // label for swagger
	@PostMapping("/findContainerReceiptNew")
	public List<ContainerReceipt> findContainerReceiptNew(@RequestBody SearchContainerReceipt searchContainerReceipt)
			throws Exception {
		return containerreceiptService.findContainerReceiptNew(searchContainerReceipt);
	}

    @ApiOperation(response = ContainerReceipt.class, value = "Create ContainerReceipt") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postContainerReceipt(@Valid @RequestBody AddContainerReceipt newContainerReceipt, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt createdContainerReceipt = containerreceiptService.createContainerReceipt(newContainerReceipt, loginUserID);
		log.info("created container receipt data : " + createdContainerReceipt);
		return new ResponseEntity<>(createdContainerReceipt , HttpStatus.OK);
	}
    
    @ApiOperation(response = ContainerReceipt.class, value = "Update ContainerReceipt") // label for swagger
    @PatchMapping("/{containerReceiptNo}")
	public ResponseEntity<?> patchContainerReceipt(@PathVariable String containerReceiptNo, 
			@Valid @RequestBody UpdateContainerReceipt updateContainerReceipt, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt createdContainerReceipt = 
				containerreceiptService.updateContainerReceipt(containerReceiptNo, updateContainerReceipt, loginUserID);
		log.info("updated container receipt data : " + createdContainerReceipt);
		return new ResponseEntity<>(createdContainerReceipt , HttpStatus.OK);
	}
    
    @ApiOperation(response = ContainerReceipt.class, value = "Delete ContainerReceipt") // label for swagger
	@DeleteMapping("/{containerReceiptNo}")
	public ResponseEntity<?> deleteContainerReceipt(@PathVariable String containerReceiptNo, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String warehouseId,
			@RequestParam String loginUserID) {
    	containerreceiptService.deleteContainerReceipt(preInboundNo, refDocNumber, containerReceiptNo, warehouseId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//--------------------------------------------------------------V2-----------------------------------------------------------------------------//
	@ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
	@GetMapping("/v2/{containerReceiptNo}")
	public ResponseEntity<?> getContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
												   @RequestParam String plantId, @RequestParam String languageId,
												   @RequestParam String warehouseId) {
		ContainerReceiptV2 containerReceipt =
				containerreceiptService.getContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo);
		log.info("ContainerReceipt : " + containerReceipt);
		return new ResponseEntity<>(containerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
	@GetMapping("/{containerReceiptNo}/v2")
	public ResponseEntity<?> getContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
												   @RequestParam String plantId, @RequestParam String languageId,
												   @RequestParam String warehouseId, @RequestParam String preInboundNo,
												   @RequestParam String refDocNumber) {
		ContainerReceiptV2 containerReceipt =
				containerreceiptService.getContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo, preInboundNo, refDocNumber);
		log.info("ContainerReceipt : " + containerReceipt);
		return new ResponseEntity<>(containerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt V2") // label for swagger
	@PostMapping("/findContainerReceipt/v2")
	public List<ContainerReceiptV2> findContainerReceiptV2(@RequestBody SearchContainerReceiptV2 searchContainerReceipt)
			throws Exception {
		return containerreceiptService.findContainerReceiptV2(searchContainerReceipt);
	}

	@ApiOperation(response = ContainerReceiptV2.class, value = "Create ContainerReceipt V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postContainerReceiptV2(@Valid @RequestBody ContainerReceiptV2 newContainerReceipt,
													@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ContainerReceiptV2 createdContainerReceipt = containerreceiptService.createContainerReceiptV2(newContainerReceipt, loginUserID);
		log.info("created container receipt data : " + createdContainerReceipt);
		return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceiptV2.class, value = "Update ContainerReceipt V2") // label for swagger
	@PatchMapping("/v2/{containerReceiptNo}")
	public ResponseEntity<?> patchContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
													 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
													 @Valid @RequestBody ContainerReceiptV2 updateContainerReceipt, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ContainerReceiptV2 createdContainerReceipt =
				containerreceiptService.updateContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo, updateContainerReceipt, loginUserID);
		log.info("updated container receipt data : " + createdContainerReceipt);
		return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceiptV2.class, value = "Delete ContainerReceipt V2") // label for swagger
	@DeleteMapping("/v2/{containerReceiptNo}")
	public ResponseEntity<?> deleteContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
													  @RequestParam String plantId, @RequestParam String languageId,
													  @RequestParam String preInboundNo, @RequestParam String refDocNumber,
													  @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
		containerreceiptService.deleteContainerReceiptV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, containerReceiptNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}