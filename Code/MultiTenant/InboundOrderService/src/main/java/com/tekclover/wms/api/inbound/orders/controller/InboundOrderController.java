package com.tekclover.wms.api.inbound.orders.controller;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.orders.service.*;
import com.tekclover.wms.api.inbound.orders.service.namratha.SupplierInvoiceServiceV4;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"InboundOrder"}, value = "InboundOrder Operations related to InboundOrderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InboundOrder", description = "Operations related to InboundOrder ")})
@RequestMapping("/inboundorder")
@RestController
public class InboundOrderController {

    @Autowired
    SupplierInvoiceServiceV4 supplierInvoiceService;

    @Autowired
    StockReceiptService stockReceiptService;

    @Autowired
    SalesReturnService salesReturnService;

    @Autowired
    B2BTransferService b2BTransferService;

    @Autowired
    InterWarehouseService interWarehouseService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseService warehouseService;


    // ASN V2
    @ApiOperation(response = ASNV2.class, value = "ASN V2") // label for swagger
    @PostMapping("/inbound/asn/v2")
    public ResponseEntity<?> postASNV3(@Valid @RequestBody ASNV2 asn)
            throws Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            // OrderProcess Table Save Records
           InboundOrderV2 saveInbound = supplierInvoiceService.saveASNV6(asn);
//            String routingDb = dbConfigRepository.getDbName(asn.getAsnHeader().getCompanyCode(), asn.getAsnHeader().getBranchCode(), asn.getAsnHeader().getWarehouseId());
//            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("NAMRATHA");
             supplierInvoiceService.processInboundReceivedV2(asn);

            WarehouseApiResponse response = null;
            if (saveInbound != null) {
                response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  catch (Exception e){
            WarehouseApiResponse response = new WarehouseApiResponse();
            response.setStatusCode("1400");
            response.setMessage("Not Success: " + e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    // ASN V2
    @ApiOperation(response = ASNV2.class, value = "ASN V8") // label for swagger
    @PostMapping("/inbound/asn/v8")
    public ResponseEntity<?> postASNV3(@Valid @RequestBody List<ASNV2> asn)
            throws Exception {
        try {
            String routingDb = dbConfigRepository.getDbNameWithoutWhId(asn.get(0).getAsnHeader().getCompanyCode(), asn.get(0).getAsnHeader().getBranchCode());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            List<ASNV2> createdASNHeader = supplierInvoiceService.processInboundReceivedV8(asn);
            return new ResponseEntity<>(createdASNHeader, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }


    // StockReceipt V2
    @ApiOperation(response = StockReceipt.class, value = "StockReceipt V2") // label for swagger
    @PostMapping("/inbound/stockReceiptv2")
    public ResponseEntity<?> postStockReceiptv2(@Valid @RequestBody List<StockReceipt> stk)
            throws Exception {
        try {
//            for (StockReceipt stock : stk) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(stk.get(0).getStockReceiptHeader().getCompanyCode(), stk.get(0).getStockReceiptHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
            List<StockReceipt> createdHeader = stockReceiptService.processInboundReceivedV2(stk);
            return new ResponseEntity<>(createdHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // Sales Return V2
    @ApiOperation(response = SaleOrderReturn.class, value = "SalesReturn V2") // label for swagger
    @PostMapping("/inbound/salesReturnv2")
    public ResponseEntity<?> postSalesReturntv2(@Valid @RequestBody List<SaleOrderReturnV2> sor)
            throws Exception {
        try {
            for (SaleOrderReturnV2 saleOrderReturnV2 : sor) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(saleOrderReturnV2.getSoReturnHeader().getCompanyCode(), saleOrderReturnV2.getSoReturnHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<SaleOrderReturnV2> createdHeader = salesReturnService.processInboundReceivedV2(sor);
            return new ResponseEntity<>(createdHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // B2B TransferIn V2
    @ApiOperation(response = B2bTransferIn.class, value = "B2bTransferIn V2") // label for swagger
    @PostMapping("/inbound/b2bTransferInv2")
    public ResponseEntity<?> postB2bTransferInv2(@Valid @RequestBody List<B2bTransferIn> b2bTransferIn) {
        try {
            for (B2bTransferIn b2bTransferIn1 : b2bTransferIn) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(b2bTransferIn1.getB2bTransferInHeader().getCompanyCode(), b2bTransferIn1.getB2bTransferInHeader().getBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<B2bTransferIn> createdHeader = b2BTransferService.processInboundReceivedV2(b2bTransferIn);
            return new ResponseEntity<>(createdHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    //InterWarehouseTransferInV2-Inbound
    @ApiOperation(response = InterWarehouseTransferInV2.class, value = "Inter Warehouse Transfer V2")
    // label for swagger
    @PostMapping("/inbound/interWarehouseTransferInv2")
    public ResponseEntity<?> postInterWarehouseTransferIn(@Valid @RequestBody List<InterWarehouseTransferInV2> inV2s) {
        try {
//            for (InterWarehouseTransferInV2 inV2 : interWarehouseTransferInV2) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbNameWithoutWhId(inV2s.get(0).getInterWarehouseTransferInHeader().getToCompanyCode(), inV2s.get(0).getInterWarehouseTransferInHeader().getToBranchCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
            log.info("db check -->{}", DataBaseContextHolder.getCurrentDb());
            List<InterWarehouseTransferInV2> createdHeader = interWarehouseService.processInboundReceivedV2(inV2s);
            return new ResponseEntity<>(createdHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    // ASNV2 upload
    @ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
    @PostMapping("/inbound/asn/upload/v2")
    public ResponseEntity<?> postAsnUploadV2(@Valid @RequestBody List<ASNV2> asnv2List)
            throws IllegalAccessException, InvocationTargetException {
        try {
            List<WarehouseApiResponse> responseList = new ArrayList<>();
            String inboundSetNumber = String.valueOf(System.currentTimeMillis());
            for (ASNV2 asnv2 : asnv2List) {
                InboundOrderV2 createdInterWarehouseTransferInV2 = null;
                asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
                DataBaseContextHolder.setCurrentDb("MT");
                String profile = dbConfigRepository.getDbName(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
                if (profile != null) {
                    switch (profile) {
                        case "FAHAHEEL":
                        case "AUTO_LAP":
//                            createdInterWarehouseTransferInV2 = supplierInvoiceService.postWarehouseASNV2(asnv2);
                            break;
                        case "NAMRATHA":
                            createdInterWarehouseTransferInV2 = warehouseService.postWarehouseASNV2(asnv2);
                            break;
                        case "REEFERON":
                            createdInterWarehouseTransferInV2 = warehouseService.postWarehouseASNV5(asnv2);
                            break;
                        case "KNOWELL":
                            createdInterWarehouseTransferInV2 = warehouseService.postWarehouseASNV7(asnv2);
                    }
                }
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


    // ASNV2 upload
    @ApiOperation(response = ASNV2.class, value = "Upload Asn V2") // label for swagger
    @PostMapping("/inbound/empty/upload/v2")
    public ResponseEntity<?> postEmptyCrateUploadV2(@Valid @RequestBody List<ASNV2> asnv2List)
            throws IllegalAccessException, InvocationTargetException {
        try {
            List<WarehouseApiResponse> responseList = new ArrayList<>();
            String inboundSetNumber = String.valueOf(System.currentTimeMillis());
            for (ASNV2 asnv2 : asnv2List) {
                InboundOrderV2 createdInterWarehouseTransferInV2 = null;
                asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
                DataBaseContextHolder.setCurrentDb("MT");
                String profile = dbConfigRepository.getDbName(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
                if (profile != null) {
                    createdInterWarehouseTransferInV2 = warehouseService.postWarehouseEmptyCrateV5(asnv2);
                }
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
    @PostMapping("/inbound/soreturn/upload/v2")
    public ResponseEntity<?> postSOReturnV2(@RequestBody List<SaleOrderReturnV2> soReturns)
            throws IllegalAccessException, InvocationTargetException {
        try {
            WarehouseApiResponse response = new WarehouseApiResponse();
            for(SaleOrderReturnV2 soReturnV2 : soReturns) {
                InboundOrderV2 createdInterWarehouseTransferInV2 = null;
                DataBaseContextHolder.setCurrentDb("MT");
                InboundOrderV2 createdSoReturnV2 = null;
                String profile = dbConfigRepository.getDbName(soReturnV2.getSoReturnHeader().getCompanyCode(), soReturnV2.getSoReturnHeader().getBranchCode(), soReturnV2.getSoReturnHeader().getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
                if(profile != null){
                    switch (profile) {
                        case "REEFERON":
                            createdSoReturnV2 = warehouseService.postSOReturnV5(soReturnV2);
                            break;
                        case "KNOWELL":
                            createdSoReturnV2 = warehouseService.postSOReturnV2(soReturnV2);
                            break;
                        default:
                            createdSoReturnV2 = warehouseService.postSOReturnV2(soReturnV2);
                            break;
                    }
                }
                if (createdSoReturnV2 != null) {
                    response.setStatusCode("200");
                    response.setMessage("Success");
                }
            }
        } catch (Exception e) {
            log.info("soReturn order Error: " + soReturns);
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
    @ApiOperation(response = InterWarehouseTransferInV2.class, value = "Upload Inter Warehouse Transfer V2") // label for swagger
    @PostMapping("/inbound/interWarehouseTransferIn/upload/v2")
    public ResponseEntity<?> postInterWarehouseTransferInUploadV2(@RequestBody List<InterWarehouseTransferInV2> interWarehouseTransferInV2List)
            throws Exception {
        try {
            List<WarehouseApiResponse> responseList = new ArrayList<>();
            for (InterWarehouseTransferInV2 interWarehouseTransferInV2 : interWarehouseTransferInV2List) {
                DataBaseContextHolder.setCurrentDb("MT");
                String profile = dbConfigRepository.getDbName(interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceCompanyCode(), interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceBranchCode(), interWarehouseTransferInV2.getInterWarehouseTransferInHeader().getSourceWarehouseCode());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
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

}
