package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.transaction.model.deliveryconfirmation.SearchDeliveryConfirmation;
import com.tekclover.wms.api.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.transaction.service.BaseService;
import com.tekclover.wms.api.transaction.service.DeliveryConfirmationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"DeliveryConfirmation"}, value = "DeliveryConfirmation  Operations related to DeliveryConfirmationController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DeliveryConfirmation ",description = "Operations related to DeliveryConfirmation")})
@RequestMapping("/deliveryconfirmation")
@RestController
public class DeliveryConfirmationController {
	
	@Autowired
	DeliveryConfirmationService deliveryConfirmationService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	BaseService baseService;

	/**
	 *
	 * @param searchDeliveryConfirmation
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(response = DeliveryConfirmation.class, value = "Search DeliveryConfirmation V2") // label for swagger
	@PostMapping("/v3/find")
	public ResponseEntity<?> findDeliveryConfirmation(@RequestBody SearchDeliveryConfirmation searchDeliveryConfirmation) throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchDeliveryConfirmation.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
			List<DeliveryConfirmation> delivery =  deliveryConfirmationService.findDeliveryConfirmation(searchDeliveryConfirmation);
			return new ResponseEntity<>(delivery, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
}