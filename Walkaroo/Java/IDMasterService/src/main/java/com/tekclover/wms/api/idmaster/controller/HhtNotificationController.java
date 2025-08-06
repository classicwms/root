package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AddAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.FindAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.UpdateAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.dockid.AddDockId;
import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.dockid.FindDockId;
import com.tekclover.wms.api.idmaster.model.hhtnotification.FindHhtNotification;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import com.tekclover.wms.api.idmaster.service.AdhocModuleIdService;
import com.tekclover.wms.api.idmaster.service.HhtNotificationService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"HhtNotification"}, value = "HhtNotification  Operations related to HhtNotificationController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HhtNotification ",description = "Operations related to HhtNotification ")})
@RequestMapping("/hhtnotification")
@RestController
public class HhtNotificationController {
	
	@Autowired
	HhtNotificationService hhtNotificationService;


	@ApiOperation(response = HhtNotification.class, value = "Create HhtNotification") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> createHhtNotification(@Valid @RequestBody HhtNotification newHhtNotification,
										@RequestParam String loginUserID) {
		HhtNotification createdHhtNotification = hhtNotificationService.createHhtNotificationV3(newHhtNotification, loginUserID);
		return new ResponseEntity<>(createdHhtNotification , HttpStatus.OK);
	}
	@ApiOperation(response = HhtNotification.class, value = "Get a HhtNotification") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getHhtNotification(@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
												@RequestParam String plantId ,@RequestParam String deviceId,@RequestParam String userId,@RequestParam String tokenId) {
		HhtNotification hhtNotification =
				hhtNotificationService.getHhtNotification(warehouseId,companyCodeId,languageId,plantId,deviceId,userId,tokenId);
		log.info("HhtNotification : " + hhtNotification);
		return new ResponseEntity<>(hhtNotification, HttpStatus.OK);
	}
    
	@ApiOperation(response = HhtNotification.class, value = "Search HhtNotification V3") // label for swagger
	@PostMapping("/v3/find")
	public Stream<HhtNotification> findHhtNotification(@RequestBody FindHhtNotification findHhtNotification) throws Exception {
		return hhtNotificationService.findHhtNotification(findHhtNotification);
	}

}