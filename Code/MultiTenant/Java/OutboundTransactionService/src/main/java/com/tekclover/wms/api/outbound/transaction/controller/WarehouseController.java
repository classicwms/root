package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.ReturnPOV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.SalesInvoice;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.SalesOrderV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;

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

	@Autowired
	DbConfigRepository dbConfigRepository;
	

	/*--------------------------------Outbound--------------------------------------------------------------*/
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
    
	/*-----------------------------CycleCountOrder---------------------------------------------------*/

	//Perpetual-CycleCount
	@ApiOperation(response = Perpetual.class, value = "Perpetual") // label for swagger
	@PostMapping("/stockcount/perpetual")
	public ResponseEntity<?> postPerpetual(@Valid @RequestBody Perpetual perpetual)
			throws IllegalAccessException, InvocationTargetException {
		try {
			CycleCountHeader createdCycleCount = warehouseService.postPerpetual(perpetual);
			if (createdCycleCount != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("perpetual order Error: " + perpetual);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}


	//Periodic-CycleCount
	@ApiOperation(response = Periodic.class, value = "Periodic") // label for swagger
	@PostMapping("/stockcount/periodic")
	public ResponseEntity<?> postPeriodic(@Valid @RequestBody Periodic periodic)
			throws IllegalAccessException, InvocationTargetException {
		try {
			CycleCountHeader createdCycleCount = warehouseService.postPeriodic(periodic);
			if (createdCycleCount != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("periodic order Error: " + periodic);
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

	//Stock Adjustment
	@ApiOperation(response = StockAdjustment.class, value = "Create StockAdjustment") //label for Swagger
	@PostMapping("/stockAdjustment")
	public ResponseEntity<?> createStockAdjustment(@Valid @RequestBody StockAdjustment stockAdjustment)
			throws IllegalAccessException, InvocationTargetException {
		try {
			StockAdjustment createdStockAdjustment = warehouseService.postStockAdjustment(stockAdjustment);
			if (createdStockAdjustment != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("StockAdjustment order Error: " + stockAdjustment);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	//Stock Adjustment - upload
	@ApiOperation(response = StockAdjustment.class, value = "Create StockAdjustment") //label for Swagger
	@PostMapping("/stockAdjustment/upload")
	public ResponseEntity<?> createStockAdjustmentUpload(@Valid @RequestBody List<StockAdjustment> stockAdjustment) {
		try {
			List<StockAdjustment> createdStockAdjustment = warehouseService.postStockAdjustmentUpload(stockAdjustment);
			if (createdStockAdjustment != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("StockAdjustment order Error: " + stockAdjustment);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
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

	//Upload API
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/upload/salesorderv5")
	public ResponseEntity<?> postSalesOrderV5(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			log.info("------------salesOrders : " + salesOrders);
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (SalesOrderV2 salesOrder : salesOrders) {
				DataBaseContextHolder.setCurrentDb("MT");
				OutboundOrderV2 createdSalesOrder = warehouseService.postSalesOrderV5(salesOrder);
				if (createdSalesOrder != null) {
					response.setStatusCode("200");
					response.setMessage("Success");
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + salesOrders);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Upload API
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/upload/emptycratev5")
	public ResponseEntity<?> emptyCrateOrderV5(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			log.info("------------salesOrders : " + salesOrders);
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (SalesOrderV2 salesOrder : salesOrders) {
				DataBaseContextHolder.setCurrentDb("MT");
				OutboundOrderV2 createdSalesOrder = warehouseService.emptyCrateOrderV5(salesOrder);
				if (createdSalesOrder != null) {
					response.setStatusCode("200");
					response.setMessage("Success");
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + salesOrders);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Upload API
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/upload/salesorderv7")
	public ResponseEntity<?> postSalesOrderV7(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			log.info("------------salesOrders : " + salesOrders);
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (SalesOrderV2 salesOrder : salesOrders) {
				DataBaseContextHolder.setCurrentDb("MT");
				OutboundOrderV2 createdSalesOrder = warehouseService.saveSalesOrderV7(salesOrder);
				if (createdSalesOrder != null) {
					response.setStatusCode("200");
					response.setMessage("Success");
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + salesOrders);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Upload API
	@ApiOperation(response = ReturnPOV2.class, value = "Purchase Return order") // label for swagger
	@PostMapping("/outbound/returnpo/V2")
	public ResponseEntity<?> purchaseReturn(@Valid @RequestBody List<ReturnPOV2> returnPOV2s) {
		try {
			log.info("------------ReturnPOV2 : " + returnPOV2s);
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (ReturnPOV2 returnPOV2 : returnPOV2s) {
				DataBaseContextHolder.setCurrentDb("MT");
				ReturnPOV2 createdPurchaseReturnOrder = warehouseService.postReturnPOV2(returnPOV2);
				if (createdPurchaseReturnOrder != null) {
					response.setStatusCode("200");
					response.setMessage("Success");
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + returnPOV2s);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// WH2WH Transfer Order
	@ApiOperation(response = InterWarehouseTransferInV2.class, value = "Upload Inter Warehouse Transfer V2")
	// label for swagger
	@PostMapping("/outbound/interWarehouseTransferIn/upload/v2")
	public ResponseEntity<?> postInterWarehouseTransferInUploadV2(@RequestBody List<InterWarehouseTransferInV2> interWarehouseTransferInV2List)
			throws IllegalAccessException, InvocationTargetException {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (InterWarehouseTransferInV2 interWarehouseTransferInV2 : interWarehouseTransferInV2List) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceCompanyCode(), interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceBranchCode(), String.valueOf(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getMiddlewareId()));
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
				OutboundOrderV2 createdInterWarehouseTransferInV2 =
						warehouseService.postInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
				if (createdInterWarehouseTransferInV2 != null) {
					response.setStatusCode("200");
					response.setMessage("Success");
					//responseList.add(response);
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("interWarehouseTransfer order Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
}