package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.deliverynotification.DeliveryNotification;
import com.tekclover.wms.api.idmaster.service.DeliveryNotificationService;
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

@Slf4j
@Validated
@Api(tags = {"DeliveryNotification"}, value = "DeliveryNotification  Operations related to DeliveryNotificationController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DeliveryNotification ",description = "Operations related to DeliveryNotification ")})
@RequestMapping("/deliverynotification")
@RestController
public class DeliveryNotificationController {

    @Autowired
    DeliveryNotificationService deliveryNotificationService;

    @ApiOperation(response = DeliveryNotificationService.class, value = "Create DeliveryNotification")
    @PostMapping("")
    public ResponseEntity<?> createDeliveryNotification(@Validated @RequestBody DeliveryNotification deliveryNotification,
                                                        @RequestParam String loginUserID) {
        DeliveryNotification notification =
                deliveryNotificationService.createDeliveryNotification(deliveryNotification, loginUserID);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @ApiOperation(response = DeliveryNotification.class, value = "Get DeliveryNotification")
    @GetMapping("")
    public ResponseEntity<?> getDeliveryNotification(@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
                                                    @RequestParam String plantId ,@RequestParam String deviceId,@RequestParam String userId,@RequestParam String tokenId) {
        DeliveryNotification dbDelivery = deliveryNotificationService.getDeliveryNotification(warehouseId, companyCodeId, languageId, plantId, deviceId, userId, tokenId);
        return new ResponseEntity<>(dbDelivery, HttpStatus.OK);
    }


}
