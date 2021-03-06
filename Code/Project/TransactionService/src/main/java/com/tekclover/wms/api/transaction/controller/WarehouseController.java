package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InterWarehouseTransferIn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.StoreReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOut;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPO;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.transaction.service.WarehouseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Warehouse"}, value = "Warehouse Operations related to WarehouseController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ",description = "Operations related to Warehouse ")})
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {
	
	@Autowired
	WarehouseService warehouseService;
	
	/*----------------------------INBOUND------------------------------------------------------------*/
    
	// ASN
	@ApiOperation(response = ASN.class, value = "ASN") // label for swagger
	@PostMapping("/inbound/asn")
	public ResponseEntity<?> postASN(@Valid @RequestBody ASN asn) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundIntegrationHeader createdASNHeader = warehouseService.postWarehouseASN(asn);
			if (createdASNHeader != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	
	/*-----------------StoreReturn---------------------------------------------------------*/
    @ApiOperation(response = StoreReturn.class, value = "Store Return") // label for swagger
	@PostMapping("/inbound/storeReturn")
	public ResponseEntity<?> postStoreReturn(@Valid @RequestBody StoreReturn storeReturn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			StoreReturn createdStoreReturn = warehouseService.postStoreReturn(storeReturn);
			if (createdStoreReturn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*-----------------Sale Order Return---------------------------------------------------------*/
    @ApiOperation(response = SaleOrderReturn.class, value = "Sale Order Return") // label for swagger
	@PostMapping("/inbound/soReturn")
	public ResponseEntity<?> postSOReturn(@Valid @RequestBody SaleOrderReturn soReturn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			SaleOrderReturn createdSOReturn = warehouseService.postSOReturn(soReturn);
			if (createdSOReturn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*-----------------InterWarehouseTransfer-Inbound---------------------------------------------------------*/
    @ApiOperation(response = InterWarehouseTransferIn.class, value = "Inter Warehouse Transfer") // label for swagger
	@PostMapping("/inbound/interWarehouseTransfer")
	public ResponseEntity<?> postInterWarehouseTransfer(@Valid @RequestBody InterWarehouseTransferIn interWarehouseTransferIn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			InterWarehouseTransferIn createdInterWarehouseTransferIn = 
					warehouseService.postInterWarehouseTransfer(interWarehouseTransferIn);
			if (createdInterWarehouseTransferIn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*--------------------------------Outbound-------------------------------------------------------------*/
    /*----------------------------Shipment order------------------------------------------------------------*/
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
	@PostMapping("/outbound/so")
	public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody ShipmentOrder shipmenOrder) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			ShipmentOrder createdSO = warehouseService.postSO(shipmenOrder);
			if (createdSO != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*----------------------------Sale order True Express-------------------------------------------------------*/
    @ApiOperation(response = SalesOrder.class, value = "Sale order True Express") // label for swagger
	@PostMapping("/outbound/salesOrder")
	public ResponseEntity<?> postSalesOrder(@Valid @RequestBody SalesOrder salesOrder) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			SalesOrder createdSalesOrder = warehouseService.postSalesOrder(salesOrder);
			if (createdSalesOrder != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*----------------------------Return PO------------------------------------------------------------*/
    @ApiOperation(response = ReturnPO.class, value = "Return PO") // label for swagger
	@PostMapping("/outbound/returnPO")
	public ResponseEntity<?> postReturnPO(@Valid @RequestBody ReturnPO returnPO) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			ReturnPO createdReturnPO = warehouseService.postReturnPO(returnPO);
			if (createdReturnPO != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
    
    /*----------------------------Interwarehouse SO------------------------------------------------------------*/
    @ApiOperation(response = InterWarehouseTransferOut.class, value = "Inter Warehouse Transfer Out") // label for swagger
   	@PostMapping("/outbound/interWarehouseTransfer")
   	public ResponseEntity<?> postReturnPO(@Valid 
   			@RequestBody InterWarehouseTransferOut interWarehouseTransfer) 
   			throws IllegalAccessException, InvocationTargetException {
   		try {
			InterWarehouseTransferOut createdInterWarehouseTransfer = 
					warehouseService.postInterWarehouseTransferOutbound(interWarehouseTransfer);
			if (createdInterWarehouseTransfer != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		return null;
   	}
}