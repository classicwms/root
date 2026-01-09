package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.*;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.B2bTransferIn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderProcessV4;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.SaleOrderReturnV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.StockReceiptHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.transaction.service.OrderPreparationService;
import com.tekclover.wms.api.inbound.transaction.service.PutAwayHeaderService;
import com.tekclover.wms.api.inbound.transaction.service.WarehouseService;

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
	OrderPreparationService orderPreparationService;

	@Autowired
	PutAwayHeaderService putAwayHeaderService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	BaseService baseService;

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

			Map<String, List<ASNV2>> groupBranchList = asnv2List.stream().collect(Collectors.groupingBy(asn -> asn.getAsnHeader().getBranchCode()));
			List<WarehouseApiResponse> responseList = new ArrayList<>();
			for(Map.Entry<String, List<ASNV2>> entry : groupBranchList.entrySet()) {
				String branchCode = entry.getKey();
				log.info("Branch Code is ----> {} ", branchCode);
				List<ASNV2> branchWiseAsnList = entry.getValue();

				String db = baseService.getDataBase(branchCode);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(db);

				log.info("Inbound ASN Current DB ---> Branch: " + branchCode + " | DB: " + db);
				String inboundSetNumber = String.valueOf(System.currentTimeMillis());

				for(ASNV2 asnv2 : branchWiseAsnList) {
					asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
					InboundOrderV2 createdInterWarehouseTransferInV2 =
							warehouseService.postWarehouseASNV2(asnv2);
					if (createdInterWarehouseTransferInV2 != null) {
						WarehouseApiResponse response = new WarehouseApiResponse();
						response.setStatusCode("200");
						response.setMessage("Success");
						responseList.add(response);
					}
				}
			}
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