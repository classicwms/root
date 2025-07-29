package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.*;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.WarehouseService;
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
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ", description = "Operations related to Warehouse ")})
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    /*----------------------------INBOUND------------------------------------------------------------*/

    // ASN
    @ApiOperation(response = ASN.class, value = "ASN") // label for swagger
    @PostMapping("/inbound/asn")
    public ResponseEntity<?> postASN(@Valid @RequestBody ASN asn)
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
//	@ApiOperation(response = ASNV2.class, value = "ASN V2") // label for swagger
//	@PostMapping("/inbound/asn/v2")
//	public ResponseEntity<?> postASNV2 (@Valid @RequestBody ASNV2 asn)
//			throws IllegalAccessException, InvocationTargetException {
//		try {
//			DataBaseContextHolder.setCurrentDb("MT");
//			String routingDb = dbConfigRepository.getDbName(asn.getAsnHeader().getCompanyCode(), asn.getAsnHeader().getBranchCode(), String.valueOf(asn.getAsnHeader().getMiddlewareId()));
//			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//			DataBaseContextHolder.clear();
//			DataBaseContextHolder.setCurrentDb(routingDb);
//			InboundOrderV2 createdASNHeader = warehouseService.postWarehouseASNV2(asn);
//			warehouseService.postWarehouseASNV6(asn);
//			if (createdASNHeader != null) {
//				WarehouseApiResponse response = new WarehouseApiResponse();
//				response.setStatusCode("200");
//				response.setMessage("Success");
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//		} catch (Exception e) {
//			log.info("ASN Order Error: " + asn);
//			e.printStackTrace();
//			WarehouseApiResponse response = new WarehouseApiResponse();
//			response.setStatusCode("1400");
//			response.setMessage("Not Success: " + e.getLocalizedMessage());
//			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
//		}
//		finally {
//			DataBaseContextHolder.clear();
//		}
//		return null;
//	}

    @ApiOperation(response = ASNV2.class, value = "ASN V2") // label for swagger
    @PostMapping("/inbound/asn/v2")
    public ResponseEntity<?> postASNV2(@Valid @RequestBody ASNV2 asn)
            throws IllegalAccessException, InvocationTargetException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String profile = dbConfigRepository.getDbName(asn.getAsnHeader().getCompanyCode(), asn.getAsnHeader().getBranchCode(), asn.getAsnHeader().getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(profile);
            InboundOrderV2 createdASNHeader = null;
            if (profile != null) {
                switch (profile) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        createdASNHeader = warehouseService.postWarehouseASNV2(asn);
                        break;
                    case "NAMRATHA":
                        createdASNHeader = warehouseService.postWarehouseASNV6(asn);
                        break;
                    case "REEFERON":
                        createdASNHeader = warehouseService.postWarehouseASNV5(asn);
                }
            }
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
        } finally {
            DataBaseContextHolder.clear();
        }
        return null;
    }


    // StockReceipt
    @ApiOperation(response = StockReceiptHeader.class, value = "StockReceipt") // label for swagger
    @PostMapping("/inbound/stockReceipt")
    public ResponseEntity<?> postStockReceipt(@Valid @RequestBody StockReceiptHeader stockReceipt)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(stockReceipt.getCompanyCode(), stockReceipt.getBranchCode(), String.valueOf(stockReceipt.getMiddlewareId()));
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
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
        } finally {
            DataBaseContextHolder.clear();
        }
        return null;
    }

    // ASNV2 upload
    @ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
    @PostMapping("/inbound/asn/upload/v2")
    public ResponseEntity<?> postAsnUploadV2(@Valid @RequestBody List<ASNV2> asnv2List)
            throws IllegalAccessException, InvocationTargetException {
        try {
            List<WarehouseApiResponse> responseList = new ArrayList<>();
            for (ASNV2 asnv2 : asnv2List) {
                InboundOrderV2 inboundOrderV2 = null;
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), String.valueOf(asnv2.getAsnHeader().getMiddlewareId()));
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
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
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Sale Order Return
    @ApiOperation(response = SaleOrderReturnV2.class, value = "Sale Order ReturnV2") // label for swagger
    @PostMapping("/inbound/soreturn/v2")
    public ResponseEntity<?> postSOReturnV2(@RequestBody SaleOrderReturnV2 soReturnV2)
            throws IllegalAccessException, InvocationTargetException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            log.info("soReturn -------> {}", soReturnV2);
            String profile = dbConfigRepository.getDbName2(soReturnV2.getSoReturnHeader().getCompanyCode(), soReturnV2.getSoReturnHeader().getBranchCode());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            InboundOrderV2 createdSOReturnV2 = null;
            if (profile != null) {
                switch (profile) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        createdSOReturnV2 = warehouseService.postSOReturnV2(soReturnV2);
                        break;
                    case "KNOWELL":
                        createdSOReturnV2 = warehouseService.postSOReturnV6(soReturnV2);
                        break;
                    case "NAMRATHA":
                        createdSOReturnV2 = warehouseService.postSOReturnV6(soReturnV2);
                        break;
                }
            }
            if (createdSOReturnV2 != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("SO Return Order Error: " + soReturnV2);
            e.printStackTrace();
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            DataBaseContextHolder.clear();
        }
        return null;
    }

    //InterWarehouseTransferInV2-Inbound
    @ApiOperation(response = InterWarehouseTransferInV2.class, value = "Inter Warehouse Transfer V2")
    // label for swagger
    @PostMapping("/inbound/interWarehouseTransferIn/v2")
    public ResponseEntity<?> postInterWarehouseTransferInV2(@Valid @RequestBody InterWarehouseTransferInV2 interWarehouseTransferInV2)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceCompanyCode(), interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceBranchCode(), String.valueOf(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getMiddlewareId()));
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
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
        } finally {
            DataBaseContextHolder.clear();
        }
        return null;
    }

    // WH2WH Transfer Order
    @ApiOperation(response = InterWarehouseTransferInV2.class, value = "Upload Inter Warehouse Transfer V2")
    // label for swagger
    @PostMapping("/inbound/interWarehouseTransferIn/upload/v2")
    public ResponseEntity<?> postInterWarehouseTransferInUploadV2(@RequestBody List<InterWarehouseTransferInV2> interWarehouseTransferInV2List)
            throws IllegalAccessException, InvocationTargetException {
        try {
            List<WarehouseApiResponse> responseList = new ArrayList<>();
            for (InterWarehouseTransferInV2 interWarehouseTransferInV2 : interWarehouseTransferInV2List) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceCompanyCode(), interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceBranchCode(), String.valueOf(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getMiddlewareId()));
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
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
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    /*-----------------B2bTransferIn-Inbound---------------------------------------------------------*/
    @ApiOperation(response = B2bTransferIn.class, value = "B2bTransferIn") // label for swagger
    @PostMapping("/inbound/b2bTransferIn")
    public ResponseEntity<?> postB2bTransferIn(@Valid @RequestBody B2bTransferIn b2bTransferIn)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(b2bTransferIn.getB2bTransferInHeader().getCompanyCode(), b2bTransferIn.getB2bTransferInHeader().getBranchCode(), String.valueOf(b2bTransferIn.getB2bTransferInHeader().getMiddlewareId()));
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
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
        } finally {
            DataBaseContextHolder.clear();
        }
        return null;
    }
}