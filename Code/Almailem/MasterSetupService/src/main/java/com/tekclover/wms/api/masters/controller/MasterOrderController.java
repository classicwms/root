package com.tekclover.wms.api.masters.controller;


import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.masters.*;
import com.tekclover.wms.api.masters.service.MasterOrderService;
import com.tekclover.wms.api.masters.service.MasterService;
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
@Api(tags = {"MasterOrder"}, value = "MasterOrder Operations related to MasterOrderController")
@SwaggerDefinition(tags = {@Tag(name = "MasterOrder", description = "Operations related to MasterOrder")})
@RequestMapping("/masterorder")
@RestController
public class MasterOrderController {


    @Autowired
    MasterOrderService masterOrderService;

    @Autowired
    MasterService masterService;

    //Master/Item
    @ApiOperation(response = Item.class, value = "Create an Item") //label for Swagger
    @PostMapping("/master/item")
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item) throws IllegalAccessException, InvocationTargetException {
        try {
//            ImBasicData1V2 createdItem = masterOrderService.postItem(item);
            Item createdItem = masterService.processItemMaster(item);
            if (createdItem != null) {
                WarehouseApiResponse whApiResponse = new WarehouseApiResponse();
                whApiResponse.setStatusCode("200");
                whApiResponse.setMessage("Success");
                return new ResponseEntity<>(whApiResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("Item Error: " + item);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        return null;
    }

    //Master/Customer
    @ApiOperation(response = Customer.class, value = "Create a Customer") //label for Swagger
    @PostMapping("/master/customer")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer)
            throws IllegalAccessException, InvocationTargetException {
        try {
//            BusinessPartnerV2 createdCustomer = masterOrderService.postCustomer(customer);
            Customer createdCustomer = masterService.processCustomerMaster(customer);
            if (createdCustomer != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("Customer Error: " + customer);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        return null;
    }


}
