package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AddAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.FindAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.UpdateAdhocModuleId;
import com.tekclover.wms.api.idmaster.model.dockid.AddDockId;
import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.dockid.FindDockId;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.AdhocModuleIdService;
import com.tekclover.wms.api.idmaster.service.HhtNotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.Data;
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

@Slf4j
@Validated
@Api(tags = {"HhtNotification"}, value = "HhtNotification  Operations related to HhtNotificationController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HhtNotification ",description = "Operations related to HhtNotification ")})
@RequestMapping("/hhtnotification")
@RestController
public class HhtNotificationController {
	
	@Autowired
	HhtNotificationService hhtNotificationService;

	@Autowired
	DbConfigRepository dbConfigRepository;


	@ApiOperation(response = HhtNotification.class, value = "Create HhtNotification") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> createHhtNotification(@Valid @RequestBody HhtNotification newHhtNotification,
										@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newHhtNotification.getCompanyId(), newHhtNotification.getPlantId(), newHhtNotification.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			HhtNotification createdHhtNotification = hhtNotificationService.createHhtNotification(newHhtNotification, loginUserID);
			log.info("HhtNotification : " + createdHhtNotification);
			return new ResponseEntity<>(createdHhtNotification, HttpStatus.OK);
		}finally {
			DataBaseContextHolder.clear();
		}
	}
	@ApiOperation(response = HhtNotification.class, value = "Get a HhtNotification") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getHhtNotification(@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
												@RequestParam String plantId ,@RequestParam String deviceId,@RequestParam String userId,@RequestParam String tokenId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		HhtNotification hhtNotification =
				hhtNotificationService.getHhtNotification(warehouseId,companyCodeId,languageId,plantId,deviceId,userId,tokenId);
		log.info("HhtNotification : " + hhtNotification);
		return new ResponseEntity<>(hhtNotification, HttpStatus.OK);
	}
    
finally {
			DataBaseContextHolder.clear();
		}
		}
}