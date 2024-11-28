package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderProcess;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.service.OrderProcessingService;
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

@Slf4j
@Validated
@Api(tags = {"OrderProcess"}, value = "OrderProcessing Operations Controller") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OrderProcessing ", description = "Operations related to Order Processing ")})
@RequestMapping("/orderprocess")
@RestController
public class OrderProcessingController {

    @Autowired
    OrderProcessingService orderProcessingService;

    /*----------------------------INBOUND------------------------------------------------------------*/

    // ASN
    @ApiOperation(response = InboundOrderProcess.class, value = "Inbound Order Processing") // label for swagger
    @PostMapping("/inbound")
    public ResponseEntity<?> postInboundOrder(@Valid @RequestBody InboundOrderProcess inboundOrderProcess) throws Exception {
        try {
            InboundHeaderV2 createdInboundOrderReceived = orderProcessingService.postInboundReceived(inboundOrderProcess);
            if (createdInboundOrderReceived != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("InboundOrderReceived Save Error: " + inboundOrderProcess.getRefDocNumber());
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        return null;
    }
}