package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

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
    
    @ApiOperation(response = ContainerReceipt.class, value = "Create ContainerReceipt") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postContainerReceipt(@Valid @RequestBody AddContainerReceipt newContainerReceipt, 
			@RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt createdContainerReceipt = containerreceiptService.createContainerReceipt(newContainerReceipt, loginUserID);
		return new ResponseEntity<>(createdContainerReceipt , HttpStatus.OK);
	}
    
    @ApiOperation(response = ContainerReceipt.class, value = "Update ContainerReceipt") // label for swagger
    @PatchMapping("/{containerReceiptNo}")
	public ResponseEntity<?> patchContainerReceipt(@PathVariable String containerReceiptNo, 
			@Valid @RequestBody UpdateContainerReceipt updateContainerReceipt, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt createdContainerReceipt = 
				containerreceiptService.updateContainerReceipt(containerReceiptNo, updateContainerReceipt, loginUserID);
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
}