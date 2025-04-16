package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
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
	public ResponseEntity<?> postASN (@Valid @RequestBody ASN asn) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrder createdASNHeader = warehouseService.postWarehouseASN(asn);
			if (createdASNHeader != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("ASN Order Error: " + asn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
	
	/*-----------------StoreReturn---------------------------------------------------------*/
    @ApiOperation(response = StoreReturn.class, value = "Store Return") // label for swagger
	@PostMapping("/inbound/storeReturn")
	public ResponseEntity<?> postStoreReturn(@Valid @RequestBody StoreReturn storeReturn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			InboundOrder createdStoreReturn = warehouseService.postStoreReturn(storeReturn);
			if (createdStoreReturn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("Store Return Order Error: " + storeReturn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
    
    /*-----------------Sale Order Return---------------------------------------------------------*/
    @ApiOperation(response = SaleOrderReturn.class, value = "Sale Order Return") // label for swagger
	@PostMapping("/inbound/soReturn")
	public ResponseEntity<?> postSOReturn(@Valid @RequestBody SaleOrderReturn soReturn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			InboundOrder createdSOReturn = warehouseService.postSOReturn(soReturn);
			if (createdSOReturn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("soReturn order Error: " + soReturn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
    
    /*-----------------InterWarehouseTransfer-Inbound---------------------------------------------------------*/
    @ApiOperation(response = InterWarehouseTransferIn.class, value = "Inter Warehouse Transfer") // label for swagger
	@PostMapping("/inbound/interWarehouseTransfer")
	public ResponseEntity<?> postInterWarehouseTransfer(@Valid @RequestBody InterWarehouseTransferIn interWarehouseTransferIn) 
			throws IllegalAccessException, InvocationTargetException {
    	try {
			InboundOrder createdInterWarehouseTransferIn = 
					warehouseService.postInterWarehouseTransfer(interWarehouseTransferIn);
			if (createdInterWarehouseTransferIn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("interWarehouseTransfer order Error: " + interWarehouseTransferIn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
    
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

	// ASN V2
	@ApiOperation(response = ASNV2.class, value = "ASN V2") // label for swagger
	@PostMapping("/inbound/asn/v2")
	public ResponseEntity<?> postASNV2 (@Valid @RequestBody ASNV2 asn)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrderV2 createdASNHeader = warehouseService.postWarehouseASNV2(asn);
			if (createdASNHeader != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("ASN Order Error: " + asn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	// StockReceipt
	@ApiOperation(response = StockReceiptHeader.class, value = "StockReceipt") // label for swagger
	@PostMapping("/inbound/stockReceipt")
	public ResponseEntity<?> postStockReceipt (@Valid @RequestBody StockReceiptHeader stockReceipt)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrderV2 createdStockReceipt = warehouseService.postWarehouseStockReceipt(stockReceipt);
			if (createdStockReceipt != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("StockReceipt Order Error: " + stockReceipt);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

// ASNV2 upload
@ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
@PostMapping("/inbound/asn/upload/v2")
public ResponseEntity<?> postAsnUploadV2 (@Valid @RequestBody List<ASNV2> asnv2List)
		throws IllegalAccessException, InvocationTargetException {
	try {
		List<WarehouseApiResponse> responseList = new ArrayList<>();
		for (ASNV2 asnv2 : asnv2List) {
			InboundOrderV2 createdInterWarehouseTransferInV2 =
					warehouseService.postWarehouseASNV2(asnv2);
			if (createdInterWarehouseTransferInV2 != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				responseList.add(response);
			}
		}
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	} catch (Exception e) {
		log.info("interWarehouseTransfer order Error: " + e);
		e.printStackTrace();
		WarehouseApiResponse response = new WarehouseApiResponse();
		response.setStatusCode("1400");
		response.setMessage("Not Success: " + e.getLocalizedMessage());
		return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
	}
}

	//Sale Order Return
	@ApiOperation(response = SaleOrderReturnV2.class, value = "Sale Order ReturnV2") // label for swagger
	@PostMapping("/inbound/soreturn/v2")
	public ResponseEntity<?> postSOReturnV2(@Valid @RequestBody SaleOrderReturnV2 soReturnV2)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrderV2 createdSOReturnV2 = warehouseService.postSOReturnV2(soReturnV2);
			if (createdSOReturnV2 != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("soReturn order Error: " + soReturnV2);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	//InterWarehouseTransferInV2-Inbound
	@ApiOperation(response = InterWarehouseTransferInV2.class, value = "Inter Warehouse Transfer V2") // label for swagger
	@PostMapping("/inbound/interWarehouseTransferIn/v2")
	public ResponseEntity<?> postInterWarehouseTransferInV2(@Valid @RequestBody InterWarehouseTransferInV2 interWarehouseTransferInV2)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrderV2 createdInterWarehouseTransferInV2 =
					warehouseService.postInterWarehouseTransferInV2(interWarehouseTransferInV2);
			if (createdInterWarehouseTransferInV2 != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("interWarehouseTransfer order Error: " + interWarehouseTransferInV2);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	// WH2WH Transfer Order
	@ApiOperation(response = InterWarehouseTransferInV2.class, value = "Upload Inter Warehouse Transfer V2") // label for swagger
	@PostMapping("/inbound/interWarehouseTransferIn/upload/v2")
	public ResponseEntity<?> postInterWarehouseTransferInUploadV2 (@Valid @RequestBody List<InterWarehouseTransferInV2> interWarehouseTransferInV2List)
			throws IllegalAccessException, InvocationTargetException {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			for (InterWarehouseTransferInV2 interWarehouseTransferInV2 : interWarehouseTransferInV2List) {
				InboundOrderV2 createdInterWarehouseTransferInV2 =
						warehouseService.postInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
				if (createdInterWarehouseTransferInV2 != null) {
					WarehouseApiResponse response = new WarehouseApiResponse();
					response.setStatusCode("200");
					response.setMessage("Success");
					responseList.add(response);
				}
			}
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("interWarehouseTransfer order Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}


	/*-----------------B2bTransferIn-Inbound---------------------------------------------------------*/
	@ApiOperation(response = B2bTransferIn.class, value = "B2bTransferIn") // label for swagger
	@PostMapping("/inbound/b2bTransferIn")
	public ResponseEntity<?> postB2bTransferIn(@Valid @RequestBody B2bTransferIn b2bTransferIn)
			throws IllegalAccessException, InvocationTargetException {
		try {
			InboundOrderV2 createdB2bTransferIn =
					warehouseService.postB2bTransferIn(b2bTransferIn);
			if (createdB2bTransferIn != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("interWarehouseTransfer order Error: " + b2bTransferIn);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
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

	/*--------------------------------Outbound V2--------------------------------------------------------------*/
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
	@PostMapping("/outbound/so/upload/v2")
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

	//Upload API
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/upload/salesorderv2")
	public ResponseEntity<?> postSalesOrderV2(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			log.info("------------salesOrders : " + salesOrders);
			WarehouseApiResponse response = new WarehouseApiResponse();
			for (SalesOrderV2 salesOrder : salesOrders) {
				SalesOrderV2 createdSalesOrder = warehouseService.postSalesOrderV2(salesOrder);
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
}