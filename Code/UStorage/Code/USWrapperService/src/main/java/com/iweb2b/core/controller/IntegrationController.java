package com.iweb2b.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iweb2b.core.config.PropertiesConfig;
import com.iweb2b.core.model.integration.AddConsignmentTracking;
import com.iweb2b.core.model.integration.AddSoftDataUpload;
import com.iweb2b.core.model.integration.ConsignmentTracking;
import com.iweb2b.core.model.integration.SoftDataUpload;
import com.iweb2b.core.model.integration.UpdateConsignmentTracking;
import com.iweb2b.core.model.integration.UpdateSoftDataUpload;
import com.iweb2b.core.model.integration.asyad.Consignment;
import com.iweb2b.core.model.integration.asyad.ConsignmentResponse;
import com.iweb2b.core.service.AuthTokenService;
import com.iweb2b.core.service.FileStorageService;
import com.iweb2b.core.service.IntegrationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@Api(tags = {"Gateway Service"}, value = "Gateway Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Integration",description = "Operations related to User")})
public class IntegrationController {

	@Autowired
	IntegrationService integrationService;

	@Autowired
	AuthTokenService authTokenService;

	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
    FileStorageService fileStorageService;
	
	//-----------------------------------SoftDataUpload------------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "SoftData Upload") // label for swagger
    @PostMapping("/softdata/upload")
    public ResponseEntity<?> softDataUpload (@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.storeFile(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@ApiOperation(response = Optional.class, value = "SoftData Upload") // label for swagger
    @PostMapping("/softdata/upload")
    public ResponseEntity<?> softDataUpload (@RequestHeader(value="Authorization") String authorization,
    		@Valid @RequestBody Consignment newConsignment) throws Exception {
		log.info("authorization passed: " + authorization);
		log.info("newSoftDataUpload passed: " + newConsignment);
		
		// Lifetoken : Basic $2a$10$qWHNeBhu4FCGoJfuv2XVbO9Yq4QBUwGSvNM0bGpYUVc3iY8jXsJwO
		
		ConsignmentResponse consignmentResponse = integrationService.createConsignment(newConsignment, "ASYAD");
		log.info("Consignment Created: " + consignmentResponse);
		
        return new ResponseEntity<>("Product Created", HttpStatus.OK);
    }
	
	@ApiOperation(response = SoftDataUpload.class, value = "Get a Shipping Label") // label for swagger
	@GetMapping("/{referenceNumber}/shippingLabel")
	public ResponseEntity<?> getShippingLabel(@PathVariable String referenceNumber, @RequestParam String authToken) {
		String dbSoftDataUpload = integrationService.getShippingLabel(referenceNumber, authToken);
		log.info("ShippingLabel : " + dbSoftDataUpload);
		return new ResponseEntity<>(dbSoftDataUpload, HttpStatus.OK);
	}

	//-----------------------------------ConsignmentTracking------------------------------------------------------------
	@ApiOperation(response = ConsignmentTracking.class, value = "Get a ConsignmentTracking") // label for swagger
	@GetMapping("/tracking/{referenceNumber}/shipment")
	public ResponseEntity<?> getConsignmentTracking(@PathVariable String referenceNumber, @RequestParam String authToken) {
		ConsignmentTracking dbConsignmentTracking = integrationService.getConsignmentTrackingByRefNumber(referenceNumber, authToken);
		log.info("ConsignmentTracking : " + dbConsignmentTracking);
		return new ResponseEntity<>(dbConsignmentTracking, HttpStatus.OK);
	}

	@ApiOperation(response = ConsignmentTracking.class, value = "Get all ConsignmentTracking details") // label for swagger
	@GetMapping("/tracking")
	public ResponseEntity<?> getAllConsignmentTracking(@RequestParam String authToken) {
		ConsignmentTracking[] consignmentTrackingList = integrationService.getAllConsignmentTracking(authToken);
		return new ResponseEntity<>(consignmentTrackingList, HttpStatus.OK);
	}

	@ApiOperation(response = ConsignmentTracking.class, value = "Get a ConsignmentTracking") // label for swagger
	@GetMapping("/tracking/{consignmentTrackingId}")
	public ResponseEntity<?> getConsignmentTrackingById(@PathVariable String consignmentTrackingId, @RequestParam String authToken) {
		ConsignmentTracking dbConsignmentTracking = integrationService.getConsignmentTracking(consignmentTrackingId, authToken);
		return new ResponseEntity<>(dbConsignmentTracking, HttpStatus.OK);
	}

	@ApiOperation(response = ConsignmentTracking.class, value = "Create ConsignmentTracking") // label for swagger
	@PostMapping("/tracking")
	public ResponseEntity<?> postConsignmentTracking(@Valid @RequestBody AddConsignmentTracking newConsignmentTracking,
													 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ConsignmentTracking createdConsignmentTracking = integrationService.createConsignmentTracking(newConsignmentTracking, loginUserID, authToken);
		return new ResponseEntity<>(createdConsignmentTracking, HttpStatus.OK);
	}

	@ApiOperation(response = ConsignmentTracking.class, value = "Update ConsignmentTracking") // label for swagger
	@PatchMapping("/tracking/{consignmentTrackingId}")
	public ResponseEntity<?> patchConsignmentTracking(@PathVariable String consignmentTrackingId,
													  @RequestParam String loginUserID,
													  @Valid @RequestBody UpdateConsignmentTracking updateConsignmentTracking,
													  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ConsignmentTracking updatedConsignmentTracking = integrationService.updateConsignmentTracking(consignmentTrackingId, loginUserID,
				updateConsignmentTracking, authToken);
		return new ResponseEntity<>(updatedConsignmentTracking, HttpStatus.OK);
	}

	@ApiOperation(response = ConsignmentTracking.class, value = "Delete ConsignmentTracking") // label for swagger
	@DeleteMapping("/tracking/{consignmentTrackingId}")
	public ResponseEntity<?> deleteConsignmentTracking(@PathVariable String consignmentTrackingId, @RequestParam String loginUserID,
													   @RequestParam String authToken) {
		integrationService.deleteConsignmentTracking(consignmentTrackingId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-------------------CRUD------------------------------------------------------------------------------------
	@ApiOperation(response = SoftDataUpload.class, value = "Get all SoftDataUpload details") // label for swagger
	@GetMapping("/softdata")
	public ResponseEntity<?> getAllSoftDataUpload(@RequestParam String authToken) {
		SoftDataUpload[] softDataUploadList = integrationService.getAllSoftDataUpload(authToken);
		return new ResponseEntity<>(softDataUploadList, HttpStatus.OK);
	}

	@ApiOperation(response = SoftDataUpload.class, value = "Get a SoftDataUpload") // label for swagger
	@GetMapping("/softdata/{softDataUploadId}")
	public ResponseEntity<?> getSoftDataUpload(@PathVariable String softDataUploadId, @RequestParam String authToken) {
		SoftDataUpload dbSoftDataUpload = integrationService.getSoftDataUpload(softDataUploadId, authToken);
		return new ResponseEntity<>(dbSoftDataUpload, HttpStatus.OK);
	}

	@ApiOperation(response = SoftDataUpload.class, value = "Create SoftDataUpload") // label for swagger
	@PostMapping("/softdata")
	public ResponseEntity<?> postSoftDataUpload(@Valid @RequestBody AddSoftDataUpload newSoftDataUpload,
												@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		SoftDataUpload createdSoftDataUpload = integrationService.createSoftDataUpload(newSoftDataUpload, loginUserID);
		return new ResponseEntity<>(createdSoftDataUpload, HttpStatus.OK);
	}

	@ApiOperation(response = SoftDataUpload.class, value = "Update SoftDataUpload") // label for swagger
	@PatchMapping("/softdata/{softDataUploadId}")
	public ResponseEntity<?> patchSoftDataUpload(@PathVariable String softDataUploadId,
												 @RequestParam String loginUserID,
												 @Valid @RequestBody UpdateSoftDataUpload updateSoftDataUpload,
												 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SoftDataUpload updatedSoftDataUpload = integrationService.updateSoftDataUpload(softDataUploadId, loginUserID,
				updateSoftDataUpload, authToken);
		return new ResponseEntity<>(updatedSoftDataUpload, HttpStatus.OK);
	}

	@ApiOperation(response = SoftDataUpload.class, value = "Delete SoftDataUpload") // label for swagger
	@DeleteMapping("/softdata/{softDataUploadId}")
	public ResponseEntity<?> deleteSoftDataUpload(@PathVariable String softDataUploadId, @RequestParam String loginUserID,
												  @RequestParam String authToken) {
		integrationService.deleteSoftDataUpload(softDataUploadId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
