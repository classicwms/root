package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InterWarehouseTransferIn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.StoreReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.B2bTransferIn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderProcessV4;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.SaleOrderReturnV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.StockReceiptHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOut;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPO;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.InterWarehouseTransferOutV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.ReturnPOV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.SalesInvoice;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.SalesOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.ShipmentOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationSAP;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationV3;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.OutboundOrderProcessV4;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.ReversalV3;
import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.transaction.service.DeliveryConfirmationAsyncProcessService;
import com.tekclover.wms.api.transaction.service.OrderPreparationService;
import com.tekclover.wms.api.transaction.service.OutboundLineService;
import com.tekclover.wms.api.transaction.service.OutboundOrderProcessingFTService;
import com.tekclover.wms.api.transaction.service.PutAwayHeaderService;
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
	
	@Autowired
	DeliveryConfirmationAsyncProcessService deliveryAsynProcService;

	@Autowired
	OrderPreparationService orderPreparationService;

	@Autowired
	OutboundOrderProcessingFTService obOrderProcessingFTService;

	@Autowired
	PutAwayHeaderService putAwayHeaderService;

	@Autowired
	OutboundLineService outboundLineService;
	
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
//@ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
//@PostMapping("/inbound/asn/upload/v2")
//public ResponseEntity<?> postAsnUploadV2 (@Valid @RequestBody List<ASNV2> asnv2List)
//		throws IllegalAccessException, InvocationTargetException {
//	try {
//		List<WarehouseApiResponse> responseList = new ArrayList<>();
//		String inboundSetNumber = String.valueOf(System.currentTimeMillis());
//		for (ASNV2 asnv2 : asnv2List) {
//			asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
//			InboundOrderV2 createdInterWarehouseTransferInV2 =
//					warehouseService.postWarehouseASNV2(asnv2);
//			if (createdInterWarehouseTransferInV2 != null) {
//				WarehouseApiResponse response = new WarehouseApiResponse();
//				response.setStatusCode("200");
//				response.setMessage("Success");
//				responseList.add(response);
//			}
//		}
//		return new ResponseEntity<>(responseList, HttpStatus.OK);
//	} catch (Exception e) {
//		log.info("interWarehouseTransfer order Error: " + e);
//		e.printStackTrace();
//		WarehouseApiResponse response = new WarehouseApiResponse();
//		response.setStatusCode("1400");
//		response.setMessage("Not Success: " + e.getLocalizedMessage());
//		return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
//	}
//}

	// ASNV2 upload
	@ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
	@PostMapping("/inbound/asn/upload/v2")
	public ResponseEntity<?> postAsnUploadV2(@Valid @RequestBody List<ASNV2> asnv2List) {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			String inboundSetNumber = String.valueOf(System.currentTimeMillis());
			asnv2List.stream().forEach(asnv2 -> {
					asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
					InboundOrderV2 createdInterWarehouseTransferInV2 =
							warehouseService.postWarehouseASNV2(asnv2);
					if (createdInterWarehouseTransferInV2 != null) {
						WarehouseApiResponse response = new WarehouseApiResponse();
						response.setStatusCode("200");
						response.setMessage("Success");
						responseList.add(response);
					}
			});
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("asn order Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(response = ASNV2.class, value = "Upload Asn V4") // label for swagger
	@PostMapping("/inbound/asn/upload/v4")
	public ResponseEntity<?> postAsnUploadV3(@Valid @RequestBody List<ASNV2> asnv2List) {
		try {
			List<WarehouseApiResponse> responseList = warehouseService.createInboundOrder(asnv2List);
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("asn order Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Sale Order Return ExcelUpload
	@ApiOperation(response = SaleOrderReturnV2.class, value = "Sale Order ReturnV2") // label for swagger
	@PostMapping("/inbound/soreturn/excelUpload")
	public ResponseEntity<?> postSOReturnExcelUpload(@RequestBody List<SaleOrderReturnV2> soReturnListV2)
			throws IllegalAccessException, InvocationTargetException {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			String inboundSetNumber = String.valueOf(System.currentTimeMillis());
			soReturnListV2.stream().forEach(soReturn -> {
				InboundOrderV2 createdSOReturnV2 = warehouseService.postSOReturnV2(soReturn);
				if (createdSOReturnV2 != null) {
					WarehouseApiResponse response = new WarehouseApiResponse();
					response.setStatusCode("200");
					response.setMessage("Success");
					responseList.add(response);
				}
			});
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("soReturn order Error: " + soReturnListV2);
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
	
	//Upload API
//	@ApiOperation(response = SalesOrderV2.class, value = "Sales order") // label for swagger
//	@PostMapping("/outbound/upload/salesorderv2")
//	public ResponseEntity<?> postSalesOrderV2(@Valid @RequestBody List<SalesOrderV2> salesOrders)
//			throws IllegalAccessException, InvocationTargetException {
//		try {
//			log.info("------------salesOrders : " + salesOrders);
//			for(SalesOrderV2 salesOrder : salesOrders) {
//				log.info("-------#-----salesOrder----for::::::: : " + salesOrder);
//				SalesOrderV2 createdSalesOrder = warehouseService.postSalesOrderV2(salesOrder);
//				if (createdSalesOrder != null) {
//					WarehouseApiResponse response = new WarehouseApiResponse();
//					response.setStatusCode("200");
//					response.setMessage("Success");
//					return new ResponseEntity<>(response, HttpStatus.OK);
//				}
//			}
//		} catch (Exception e) {
//			log.info("SalesOrder order Error: " + salesOrders);
//			e.printStackTrace();
//			WarehouseApiResponse response = new WarehouseApiResponse();
//			response.setStatusCode("1400");
//			response.setMessage("Not Success: " + e.getLocalizedMessage());
//			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
//		}
//		return null;
//	}

	//Upload API
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order V3") // label for swagger
	@PostMapping("/outbound/upload/salesorderv2")
	public ResponseEntity<?> postSalesOrderV3(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			log.info("------------salesOrders : " + salesOrders);
			salesOrders.stream().forEach(salesOrder -> {
					log.info("-------#-----salesOrder----#----> " + salesOrder);
					OutboundOrderV2 createdSalesOrder = warehouseService.postSalesOrderV3(salesOrder);
					if (createdSalesOrder != null) {
						WarehouseApiResponse response = new WarehouseApiResponse();
						response.setStatusCode("200");
						response.setMessage("Success");
						responseList.add(response);
					}
			});
			return new ResponseEntity<>(responseList.get(0), HttpStatus.OK);
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
	@ApiOperation(response = SalesOrderV2.class, value = "Sales order V3") // label for swagger
	@PostMapping("/outbound/upload/salesorder/V4")
	public ResponseEntity<?> postSalesOrderV4(@Valid @RequestBody List<SalesOrderV2> salesOrders) {
		try {
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			log.info("------------salesOrders : " + salesOrders);
			salesOrders.stream().forEach(salesOrder -> {
				log.info("-------#-----salesOrder----#----> " + salesOrder);
				OutboundOrderV2 createdSalesOrder = warehouseService.postSalesOrderV4(salesOrder);
				if (createdSalesOrder != null) {
					WarehouseApiResponse response = new WarehouseApiResponse();
					response.setStatusCode("200");
					response.setMessage("Success");
					responseList.add(response);
				}
			});
			return new ResponseEntity<>(responseList.get(0), HttpStatus.OK);
		} catch (Exception e) {
			log.info("SalesOrder order Error: " + salesOrders);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}


	// Outbound Order Fullfillment from SAP
	@ApiOperation(response = Optional.class, value = "Outbound Order Fullfillment from SAP") // label for swagger
    @PostMapping("/outbound/order/fullfillment/v3")
    public ResponseEntity<?> postOutboundOrderFullfillmentV3(@RequestBody List<OutboundIntegrationHeaderV2> obIntegrationHeaderV2List) 
    		throws Exception {
		 List<OutboundHeaderV2> outboundHeaderV2 = obOrderProcessingFTService.sapOutboundOrderFullfillment (obIntegrationHeaderV2List);
         return new ResponseEntity<>(outboundHeaderV2, HttpStatus.OK);
    }

	//-----------------------------WALKAROO CHANGES-----------------------------------------------------
	// 
	@ApiOperation(response = DeliveryConfirmationV3.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/upload/deliveryConfirmationV3")
	public ResponseEntity<?> postDeliveryConfirmationV3(@Valid @RequestBody DeliveryConfirmationV3 deliveryConfirmationV3)
			throws IllegalAccessException, InvocationTargetException {
		try {
			DeliveryConfirmationV3 createdSalesOrder = warehouseService.postDeliveryConfirmationV3(deliveryConfirmationV3);
			if (createdSalesOrder != null) {
				WarehouseApiResponse response = new WarehouseApiResponse();
				response.setStatusCode("200");
				response.setMessage("Success");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("DeliveryConfirmationV3 order Error: " + deliveryConfirmationV3);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	/**
	 * Modified for SAP orders - 30/06/2025
	 * Aakash vinayak
	 *
	 * @param deliveryConfirmationV3
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@ApiOperation(response = DeliveryConfirmationV3.class, value = "Sales order") // label for swagger
	@PostMapping("/outbound/SAP/deliveryConfirmationV4")
	public ResponseEntity<?> postDeliveryConfirmationV4(@Valid @RequestBody List<DeliveryConfirmationSAP> deliveryConfirmationSAPList)
			throws IllegalAccessException, InvocationTargetException {
		try {
			warehouseService.validateDeliveryOrders(deliveryConfirmationSAPList);			
			deliveryAsynProcService.postDeliveryConfirmationV4(deliveryConfirmationSAPList);
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("200");
			response.setMessage("Success");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("DeliveryConfirmationV3 order Error: " + deliveryConfirmationSAPList);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
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

	//=======================================================Walakroo===================================================

	// Inbound upload
	@ApiOperation(response = InboundOrderProcessV4.class, value = "Upload InboundOrderProcess V3") // label for swagger
	@PostMapping("/inbound/upload/v3")
	public ResponseEntity<?> postInboundUploadV2(@Valid @RequestBody List<InboundOrderProcessV4> inboundOrderProcessList,
												 @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
												 @RequestParam String warehouseId, @RequestParam String loginUserId) {
		try {
			List<InboundOrderProcessV4> soredList = orderPreparationService.saveInboundProcess(inboundOrderProcessList);
			List<ASNV2> asnv2List = orderPreparationService.prepAsnMultipleDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, soredList);
			postAsnUploadV2(asnv2List);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.info("Inbound order Process Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// Outbound upload
	@ApiOperation(response = OutboundOrderProcessV4.class, value = "Upload OutboundOrderProcess V3") // label for swagger
	@PostMapping("/outbound/upload/v3")
	public ResponseEntity<?> postOutboundUploadV2(@Valid @RequestBody List<OutboundOrderProcessV4> outboundOrderProcessList,
												 @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
												 @RequestParam String warehouseId, @RequestParam String loginUserId) {
		try {
			List<OutboundOrderProcessV4> soredList = orderPreparationService.saveOutboundProcess(outboundOrderProcessList);
			List<SalesOrderV2> salesOrderV2List = orderPreparationService.prepSalesOrderDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, soredList);
			postSalesOrderV3(salesOrderV2List);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.info("Outbound order Process Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// Outbound Reversal
	@ApiOperation(response = ReversalV3.class, value = "Upload OutboundReversal V3") // label for swagger
	@PostMapping("/outbound/upload/outboundReversalV3")
	public ResponseEntity<?> postOutboundReversalUploadV2(@Valid @RequestBody ReversalV3 outboundReversalList) {
		try {
			outboundLineService.batchOuboundReversalV3(outboundReversalList);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.info("Outbound Reversal Process Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// Inbound Reversal
	@ApiOperation(response = ReversalV3.class, value = "Upload InboundReversal V3") // label for swagger
	@PostMapping("/inbound/upload/inboundReversalV3")
	public ResponseEntity<?> postInboundReversalUploadV2(@Valid @RequestBody ReversalV3 inboundReversalList) {
		try {
			putAwayHeaderService.batchPutAwayReversalV3(inboundReversalList);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.info("Outbound Reversal Process Error: " + e);
			e.printStackTrace();
			WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("1400");
			response.setMessage("Not Success: " + e.getLocalizedMessage());
			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(response = WarehouseApiResponse.class, value = "SAP Initiate") // label for swagger
	@PostMapping("/send")
	public ResponseEntity<?> postCheck(@RequestParam String input) {
		    WarehouseApiResponse response = new WarehouseApiResponse();
			response.setStatusCode("200");
			response.setMessage(input);
			return new ResponseEntity<>(response, HttpStatus.OK);

	}


}