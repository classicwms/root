package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.service.TransferOrderService;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2.ShipmentOrderV2;
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

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;


@Slf4j
@Validated
@Api(tags = {"OutboundOrder"}, value = "OutboundOrder  Operations related to OrderController")
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ",description = "Operations related to OutboundOrder")})
@RequestMapping("/outbound")
@RestController
public class OutboundOrderController {


    @Autowired
    TransferOrderService transferOrderService;

    @ApiOperation(response = ShipmentOrderV2.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/so/v2")
    public ResponseEntity<?> postShipmenOrderV2(@Valid @RequestBody ShipmentOrderV2 shipmenOrder)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("Transfer Order Async Process Started ------------>");
            ShipmentOrderV2 createdSO = transferOrderService.createShipmentOrderList(shipmenOrder);
            log.info("Transfer Order Async Process Completed ------------>");
//            if (createdSO != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
        } catch (Exception e) {
            log.info("ShipmentOrder order Error: " + shipmenOrder);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
