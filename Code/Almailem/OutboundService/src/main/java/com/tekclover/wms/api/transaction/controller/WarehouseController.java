package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOut;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPO;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.transaction.service.WarehouseService;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Warehouse"}, value = "Warehouse Operations related to WarehouseController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ",description = "Operations related to Warehouse ")})
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {
	
	@Autowired
	WarehouseService warehouseService;
	
    /*--------------------------------Outbound--------------------------------------------------------------*/
    /*----------------------------Shipment order------------------------------------------------------------*/
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
	@PostMapping("/outbound/so")
	public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody ShipmentOrder shipmenOrder) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			ShipmentOrder createdSO = warehouseService.postSO(shipmenOrder, false);
			if (createdSO != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("ShipmentOrder order Error: " + shipmenOrder);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
    
    //----------------------------Bulk Orders---------------------------------------------------------------------
    @ApiOperation(response = ShipmentOrder.class, value = "Create Bulk Shipment Orders") // label for swagger
	@PostMapping("/outbound/so/bulk")
	public ResponseEntity<?> postShipmenOrders(@Valid @RequestBody List<ShipmentOrder> shipmenOrders) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
    		List<WarehouseApiResponse> responseList = new ArrayList<>();
    		for (ShipmentOrder shipmentOrder : shipmenOrders) {
				ShipmentOrder createdSO = warehouseService.postSO(shipmentOrder, false);
				if (createdSO != null) {
					WarehouseApiResponse response = new WarehouseApiResponse();
					response.setStatusCode("200");
					response.setMessage("Success");
					responseList.add(response);
				}
    		}
    		return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}
    
    /*----------------------------Sale order True Express-------------------------------------------------------*/
    @ApiOperation(response = SalesOrder.class, value = "Sales order") // label for swagger
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
			log.info("SalesOrder order Error: " + salesOrder);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
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
			log.info("ReturnPO order Error: " + returnPO);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
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
			log.info("InterWarehouseTransferOut order Error: " + interWarehouseTransfer);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
   	}
//==================================================================================================================
	/*--------------------------------Outbound V2--------------------------------------------------------------*/
	/*----------------------------Shipment order V2------------------------------------------------------------*/
	@ApiOperation(response = ShipmentOrderV2.class, value = "Create Shipment Order") // label for swagger
	@PostMapping("/outbound/so/v2")
	public ResponseEntity<?> postShipmenOrderV2(@Valid @RequestBody ShipmentOrderV2 shipmenOrder)
			throws IllegalAccessException, InvocationTargetException {
		try {
			ShipmentOrderV2 createdSO = warehouseService.postSOV2(shipmenOrder, false);
			if (createdSO != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("ShipmentOrder order Error: " + shipmenOrder);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	//----------------------------Bulk Orders---------------------------------------------------------------------
	@ApiOperation(response = ShipmentOrderV2.class, value = "Create Bulk Shipment Orders") // label for swagger
	@PostMapping("/outbound/so/bulk/v2")
	public ResponseEntity<?> postShipmenOrdersV2(@Valid @RequestBody List<ShipmentOrderV2> shipmenOrders)
			throws IllegalAccessException, InvocationTargetException {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			for (ShipmentOrderV2 shipmentOrder : shipmenOrders) {
				ShipmentOrderV2 createdSO = warehouseService.postSOV2(shipmentOrder, false);
				if (createdSO != null) {
					WarehouseApiResponse response = new WarehouseApiResponse();
					response.setStatusCode("200");
					response.setMessage("Success");
					responseList.add(response);
				}
			}
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}


	/*----------------------------Sale order True Express-------------------------------------------------------*/
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/salesorderv2")
	public ResponseEntity<?> postSalesOrderV2(@Valid @RequestBody SalesOrderV2 salesOrder)
			throws IllegalAccessException, InvocationTargetException {
		try {
			SalesOrderV2 createdSalesOrder = warehouseService.postSalesOrderV2(salesOrder);
			if (createdSalesOrder != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + salesOrder);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}


	/*----------------------------Return PO------------------------------------------------------------*/
	@ApiOperation(response = ReturnPOV2.class, value = "Return PO") // label for swagger
	@PostMapping("/outbound/returnpov2")
	public ResponseEntity<?> postReturnPOV2(@Valid @RequestBody ReturnPOV2 returnPO)
			throws IllegalAccessException, InvocationTargetException {
		try {
			ReturnPOV2 createdReturnPO = warehouseService.postReturnPOV2(returnPO);
			if (createdReturnPO != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("ReturnPO order Error: " + returnPO);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	/*----------------------------Inter-warehouse Transfer_Out------------------------------------------------------------*/
	@ApiOperation(response = InterWarehouseTransferOutV2.class, value = "Inter Warehouse Transfer Out")
	@PostMapping("/outbound/interwarehousetransferoutv2")
	public ResponseEntity<?> postReturnPOV2(@Valid @RequestBody InterWarehouseTransferOutV2 interWarehouseTransfer)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InterWarehouseTransferOutV2 createdInterWarehouseTransfer =
					warehouseService.postInterWarehouseTransferOutboundV2(interWarehouseTransfer);
			if (createdInterWarehouseTransfer != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("InterWarehouseTransferOut order Error: " + interWarehouseTransfer);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	//Sales Invoice
	@ApiOperation(response = SalesInvoice.class, value = "Create SalesInvoice") //label for Swagger
	@PostMapping("/outbound/salesinvoice")
	public ResponseEntity<?> createSalesInvoice(@Valid @RequestBody SalesInvoice salesInvoice)
			throws IllegalAccessException, InvocationTargetException {
		try {
			OutboundOrderV2 createdObOrderV2 = warehouseService.postSalesInvoice(salesInvoice);
			if (createdObOrderV2 != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("SalesInvoice order Error: " + salesInvoice);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
}