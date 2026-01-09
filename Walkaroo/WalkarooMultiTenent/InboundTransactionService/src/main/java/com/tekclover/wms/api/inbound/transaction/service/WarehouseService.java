package com.tekclover.wms.api.inbound.transaction.service;

import static java.util.stream.Collectors.toList;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.repository.OutboundOrderV2Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.controller.exception.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.periodic.PeriodicHeaderV1;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.periodic.PeriodicLineV1;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.perpetual.PerpetualHeaderV1;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.perpetual.PerpetualLineV1;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASN;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASNHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASNLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferIn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferInHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferInLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SOReturnHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SOReturnLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturnHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturnLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.B2bTransferIn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.B2bTransferInHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.B2bTransferInLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.SOReturnHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.SOReturnLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.SaleOrderReturnV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.StockReceiptHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.StockReceiptLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.transaction.repository.DeliveryConfirmationRepository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundOrderLinesV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService extends BaseService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StockAdjustmentMiddlewareService stockAdjustmentService;

    @Autowired
    InboundOrderLinesV2Repository inboundOrderLinesV2Repository;

    @Autowired
    DeliveryConfirmationRepository deliveryConfirmationRepository;

    @Autowired
    private OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    InboundOrderProcessService inboundOrderProcessService;

    @Autowired
    MastersService mastersService;

    /**
     *
     * @return
     */
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    /**
     *
     * @param asn
     * @return
     */
    public InboundOrder postWarehouseASN (ASN asn) {
        log.info("ASNHeader received from External: " + asn);
        InboundOrder savedAsnHeader = saveASN (asn);							// Without Mongo
        log.info("savedAsnHeader: " + savedAsnHeader);
        return savedAsnHeader;
    }

    /**
     *
     * @param storeReturn
     * @return
     */
    public InboundOrder postStoreReturn(StoreReturn storeReturn) {
        log.info("StoreReturnHeader received from External: " + storeReturn);
        InboundOrder savedStoreReturn = saveStoreReturn (storeReturn);
        log.info("savedStoreReturn: " + savedStoreReturn);
        return savedStoreReturn;
    }

    /**
     *
     * @param soReturn
     * @return
     */
    public InboundOrder postSOReturn(SaleOrderReturn soReturn) {
        log.info("StoreReturnHeader received from External: " + soReturn);
        InboundOrder savedSOReturn = saveSOReturn (soReturn);
        log.info("soReturnHeader: " + savedSOReturn);
        return savedSOReturn;
    }

    /**
     *
     * @param interWarehouseTransferIn
     * @return
     */
    public InboundOrder postInterWarehouseTransfer(InterWarehouseTransferIn interWarehouseTransferIn) {
        log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransferIn);
        InboundOrder savedIWHReturn = saveInterWarehouseTransfer (interWarehouseTransferIn);
        log.info("interWarehouseTransferHeader: " + savedIWHReturn);
        return savedIWHReturn;
    }
    /**
     *
     * @param wareHouseId
     * @return
     */
    private boolean validateWarehouseId(String wareHouseId) {
        log.info("wareHouseId: " + wareHouseId);
        if (wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_100) || wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_200)) {
            log.info("wareHouseId:------------> " + wareHouseId);
            return true;
        } else {
            throw new BadRequestException("Warehouse Id must be either 100 or 200");
        }
    }

    /**
     *
     * @return
     */
    public static synchronized String getUUID() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

    //================================================Moongo=Removed================================================================================
    //------------------------------------------------INBOUND-ORDERS--------------------------------------------------------------------------------
    // POST ASNHeader
    private InboundOrder saveASN (ASN asn) {
        try {
            ASNHeader asnHeader = asn.getAsnHeader();

            // Warehouse ID Validation
            validateWarehouseId (asnHeader.getWareHouseId());

            // Checking for duplicate RefDocNumber
            InboundOrder dbApiHeader = orderService.getOrderById(asnHeader.getAsnNumber());
            if (dbApiHeader != null) {
                throw new InboundOrderRequestException("ASN is already posted and it can't be duplicated.");
            }

            List<ASNLine> asnLines = asn.getAsnLine();
            InboundOrder apiHeader = new InboundOrder();
            apiHeader.setOrderId(asnHeader.getAsnNumber());
            apiHeader.setRefDocumentNo(asnHeader.getAsnNumber());
            apiHeader.setRefDocumentType("ASN");
            apiHeader.setWarehouseID(asnHeader.getWareHouseId());
            apiHeader.setInboundOrderTypeId(1L);
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLines> orderLines = new HashSet<>();
            for (ASNLine asnLine : asnLines) {
                InboundOrderLines apiLine = new InboundOrderLines();
                apiLine.setLineReference(asnLine.getLineReference()); 			// IB_LINE_NO
                apiLine.setItemCode(asnLine.getSku());							// ITM_CODE
                apiLine.setItemText(asnLine.getSkuDescription()); 				// ITEM_TEXT
                apiLine.setInvoiceNumber(asnLine.getInvoiceNumber());			// INV_NO
                apiLine.setContainerNumber(asnLine.getContainerNumber());		// CONT_NO
                apiLine.setSupplierCode(asnLine.getSupplierCode());				// PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
                apiLine.setManufacturerName(asnLine.getManufacturerName());		// BRND_NM
                apiLine.setManufacturerPartNo(asnLine.getManufacturerPartNo());	// MFR_PART
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());

                // EA_DATE
                try {
                    Date reqDelDate = DateUtils.convertStringToDate(asnLine.getExpectedDate());
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setOrderedQty(asnLine.getExpectedQty());				// ORD_QTY
                apiLine.setUom(asnLine.getUom());								// ORD_UOM
                apiLine.setItemCaseQty(asnLine.getPackQty());					// ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLines(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (asn.getAsnLine() != null && !asn.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASN Order Success : " + createdOrder);
                return createdOrder;
            } else if (asn.getAsnLine() == null || asn.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASN Order Failed : " + createdOrder);
                throw new BadRequestException("ASN Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // STORE RETURN
    private InboundOrder saveStoreReturn (StoreReturn storeReturn) {
        try {
            StoreReturnHeader storeReturnHeader = storeReturn.getStoreReturnHeader();

            // Warehouse ID Validation
            validateWarehouseId (storeReturnHeader.getWareHouseId());

            // Checking for duplicate RefDocNumber
            InboundOrder dbApiHeader = orderService.getOrderById(storeReturnHeader.getTransferOrderNumber());
            if (dbApiHeader != null) {
                throw new InboundOrderRequestException("StoreReturn is already posted and it can't be duplicated.");
            }

            List<StoreReturnLine> storeReturnLines = storeReturn.getStoreReturnLine();
            InboundOrder apiHeader = new InboundOrder();
            apiHeader.setOrderId(storeReturnHeader.getTransferOrderNumber());
            apiHeader.setRefDocumentNo(storeReturnHeader.getTransferOrderNumber());
            apiHeader.setWarehouseID(storeReturnHeader.getWareHouseId());
            apiHeader.setRefDocumentType("RETURN");
            apiHeader.setInboundOrderTypeId(2L);
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLines> orderLines = new HashSet<>();
            for (StoreReturnLine storeReturnLine : storeReturnLines) {
                InboundOrderLines apiLine = new InboundOrderLines();
                apiLine.setLineReference(storeReturnLine.getLineReference()); 			// IB_LINE_NO
                apiLine.setItemCode(storeReturnLine.getSku());							// ITM_CODE
                apiLine.setItemText(storeReturnLine.getSkuDescription()); 				// ITEM_TEXT
                apiLine.setInvoiceNumber(storeReturnLine.getInvoiceNumber());			// INV_NO
                apiLine.setContainerNumber(storeReturnLine.getContainerNumber());		// CONT_NO
                apiLine.setSupplierCode(storeReturnLine.getStoreID());					// PARTNER_CODE
                apiLine.setSupplierPartNumber(storeReturnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
                apiLine.setManufacturerName(storeReturnLine.getManufacturerName());		// BRND_NM
                apiLine.setManufacturerPartNo(storeReturnLine.getManufacturerPartNo());	// MFR_PART
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());

                // EA_DATE
                try {
                    Date reqDelDate = DateUtils.convertStringToDate(storeReturnLine.getExpectedDate());
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setOrderedQty(storeReturnLine.getExpectedQty());				// ORD_QTY
                apiLine.setUom(storeReturnLine.getUom());								// ORD_UOM
                apiLine.setItemCaseQty(storeReturnLine.getPackQty());					// ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLines(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (storeReturn.getStoreReturnLine() != null && !storeReturn.getStoreReturnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("StoreReturn Order Success: " + createdOrder);
                return createdOrder;
            } else if (storeReturn.getStoreReturnLine() == null || storeReturn.getStoreReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("StoreReturn Order Failed : " + createdOrder);
                throw new BadRequestException("StoreReturn Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // SOReturn
    private InboundOrder saveSOReturn (SaleOrderReturn soReturn) {
        try {
            SOReturnHeader soReturnHeader = soReturn.getSoReturnHeader();

            // Warehouse ID Validation
            validateWarehouseId (soReturnHeader.getWareHouseId());

            // Checking for duplicate RefDocNumber
            InboundOrder dbApiHeader = orderService.getOrderById(soReturnHeader.getReturnOrderReference());
            if (dbApiHeader != null) {
                throw new InboundOrderRequestException("Return Order Reference is already posted and it can't be duplicated.");
            }

            List<SOReturnLine> storeReturnLines = soReturn.getSoReturnLine();
            InboundOrder apiHeader = new InboundOrder();
            apiHeader.setOrderId(soReturnHeader.getReturnOrderReference());
            apiHeader.setRefDocumentNo(soReturnHeader.getReturnOrderReference());
            apiHeader.setWarehouseID(soReturnHeader.getWareHouseId());
            apiHeader.setRefDocumentType("RETURN");
            apiHeader.setInboundOrderTypeId(4L);										// Hardcoded Value 4
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLines> orderLines = new HashSet<>();
            for (SOReturnLine soReturnLine : storeReturnLines) {
                InboundOrderLines apiLine = new InboundOrderLines();
                apiLine.setLineReference(soReturnLine.getLineReference()); 				// IB_LINE_NO
                apiLine.setItemCode(soReturnLine.getSku());								// ITM_CODE
                apiLine.setItemText(soReturnLine.getSkuDescription()); 					// ITEM_TEXT
                apiLine.setInvoiceNumber(soReturnLine.getInvoiceNumber());				// INV_NO
                apiLine.setContainerNumber(soReturnLine.getContainerNumber());			// CONT_NO
                apiLine.setSupplierCode(soReturnLine.getStoreID());						// PARTNER_CODE
                apiLine.setSupplierPartNumber(soReturnLine.getSupplierPartNumber());	// PARTNER_ITM_CODE
                apiLine.setManufacturerName(soReturnLine.getManufacturerName());		// BRND_NM
                apiLine.setManufacturerPartNo(soReturnLine.getManufacturerPartNo());	// MFR_PART
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());

                // EA_DATE
                try {
                    Date reqDelDate = DateUtils.convertStringToDate(soReturnLine.getExpectedDate());
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setOrderedQty(soReturnLine.getExpectedQty());					// ORD_QTY
                apiLine.setUom(soReturnLine.getUom());									// ORD_UOM
                apiLine.setItemCaseQty(soReturnLine.getPackQty());						// ITM_CASE_QTY
                apiLine.setSalesOrderReference(soReturnLine.getSalesOrderReference());	// REF_FIELD_4
                orderLines.add(apiLine);
            }
            apiHeader.setLines(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (soReturn.getSoReturnLine() != null && !soReturn.getSoReturnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrder);
                return createdOrder;
            } else if (soReturn.getSoReturnLine() == null || soReturn.getSoReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrder);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // InterWarehouseTransfer
    private InboundOrder saveInterWarehouseTransfer (InterWarehouseTransferIn interWarehouseTransferIn) {
        try {
            InterWarehouseTransferInHeader interWarehouseTransferInHeader = interWarehouseTransferIn.getInterWarehouseTransferInHeader();
            // Warehouse ID Validation
            validateWarehouseId (interWarehouseTransferInHeader.getToWhsId());

            // Checking for duplicate RefDocNumber
            InboundOrder dbApiHeader = orderService.getOrderById(interWarehouseTransferInHeader.getTransferOrderNumber());
            if (dbApiHeader != null) {
                throw new InboundOrderRequestException("InterWarehouseTransfer is already posted and it can't be duplicated.");
            }

            List<InterWarehouseTransferInLine> interWarehouseTransferInLines = interWarehouseTransferIn.getInterWarehouseTransferInLine();
            InboundOrder apiHeader = new InboundOrder();
            apiHeader.setOrderId(interWarehouseTransferInHeader.getTransferOrderNumber());
            apiHeader.setRefDocumentNo(interWarehouseTransferInHeader.getTransferOrderNumber());
            apiHeader.setWarehouseID(interWarehouseTransferInHeader.getToWhsId());
            apiHeader.setRefDocumentType("WH2WH");				// Hardcoded Value "WH to WH"
            apiHeader.setInboundOrderTypeId(3L);				// Hardcoded Value 3
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLines> orderLines = new HashSet<>();
            for (InterWarehouseTransferInLine iwhTransferLine : interWarehouseTransferInLines) {
                InboundOrderLines apiLine = new InboundOrderLines();
                apiLine.setLineReference(iwhTransferLine.getLineReference()); 				// IB_LINE_NO
                apiLine.setItemCode(iwhTransferLine.getSku());								// ITM_CODE
                apiLine.setItemText(iwhTransferLine.getSkuDescription()); 					// ITEM_TEXT
                apiLine.setInvoiceNumber(iwhTransferLine.getInvoiceNumber());				// INV_NO
                apiLine.setContainerNumber(iwhTransferLine.getContainerNumber());			// CONT_NO
                apiLine.setSupplierCode(iwhTransferLine.getFromWhsId());					// PARTNER_CODE
                apiLine.setSupplierPartNumber(iwhTransferLine.getSupplierPartNumber());		// PARTNER_ITM_CODE
                apiLine.setManufacturerName(iwhTransferLine.getManufacturerName());			// BRND_NM
                apiLine.setManufacturerPartNo(iwhTransferLine.getManufacturerPartNo());		// MFR_PART
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());

                // EA_DATE
                try {
                    Date reqDelDate = DateUtils.convertStringToDate(iwhTransferLine.getExpectedDate());
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setOrderedQty(iwhTransferLine.getExpectedQty());					// ORD_QTY
                apiLine.setUom(iwhTransferLine.getUom());									// ORD_UOM
                apiLine.setItemCaseQty(iwhTransferLine.getPackQty());						// ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLines(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (interWarehouseTransferIn.getInterWarehouseTransferInLine() != null &&
                    !interWarehouseTransferIn.getInterWarehouseTransferInLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("InterWarehouseTransfer Order Success: " + createdOrder);
                return createdOrder;
            } else if (interWarehouseTransferIn.getInterWarehouseTransferInLine() == null ||
                    interWarehouseTransferIn.getInterWarehouseTransferInLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("InterWarehouseTransfer Order Failed : " + createdOrder);
                throw new BadRequestException("InterWarehouseTransfer Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

//==================================================Inbound V2===========================================================================

    /**
     *
     * @param asnv2List asnList
     * @return return WarehouseApiResponse
     */
    public List<WarehouseApiResponse> createInboundOrder(List<ASNV2> asnv2List) {
        List<WarehouseApiResponse> responseList = new ArrayList<>();
        List<InboundOrderV2> inboundOrderV2List = new ArrayList<>();
        String inboundSetNumber = String.valueOf(System.currentTimeMillis());
        asnv2List.stream().forEach(asnv2 -> {
            asnv2.getAsnHeader().setParentProductionOrderNo(inboundSetNumber);
            InboundOrderV2 inboundOrderV2 = saveASNV4(asnv2);
            inboundOrderV2List.add(inboundOrderV2);
            if (inboundOrderV2 != null) {
                WarehouseApiResponse response = new WarehouseApiResponse();
                response.setStatusCode("200");
                response.setMessage("Success");
                responseList.add(response);
            }
        });

        // createOrderProcess in background
        CompletableFuture.runAsync(() -> {
            try {
                inboundOrderProcessService.createOrderProcess(inboundOrderV2List);
            } catch (Exception e) {
                log.error("Async createOrderProcess failed", e);
            }
        });
        return responseList;
    }

    /**
     *
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV2 (ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV2 (asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }

    // POST ASNV2Header
    private InboundOrderV2 saveASNV2 (ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            //validateBarcodeIds
//			huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));
            apiHeader.setOrderId(asnV2Header.getAsnNumber());
            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
            apiHeader.setBranchCode(asnV2Header.getBranchCode());
            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
            apiHeader.setReversalFlag(asnV2Header.getReversalFlag());

            //-------InboundUpload Vs SAP API------Differentiator----------------------------------------

            apiHeader.setIsSapOrder(asnV2Header.getIsSapOrder());

            if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                asnV2Header.getCompanyCode(),
                                asnV2Header.getBranchCode(),
                                asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }

            if (asnV2Header.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(1L);                                            //Default
            }

            if (asnV2Header.getReversalFlag() != null) {
                if (asnV2Header.getReversalFlag().equalsIgnoreCase("X") || asnV2Header.getReversalFlag().equalsIgnoreCase("x")) {
                    apiHeader.setInboundOrderTypeId(10L);
                }
            }

            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setLineReference(asnLineV2.getLineReference()); 			// IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku().trim());							// ITM_CODE
                apiLine.setBarcodeId(asnLineV2.getBarcodeId());
                apiLine.setItemText(asnLineV2.getSkuDescription()); 				// ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());			// CONT_NO
                apiLine.setSupplierCode(asnLineV2.getSupplierCode());				// PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);		// BRAND_NM
                apiLine.setManufacturerCode(MFR_NAME);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

                if (asnLineV2.getReversalFlag() != null) {
                    if (asnLineV2.getReversalFlag().equalsIgnoreCase("X") || asnLineV2.getReversalFlag().equalsIgnoreCase("x")) {
                        apiLine.setInboundOrderTypeId(10L);
                    }
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());

                if (asnLineV2.getExpectedDate() != null) {
                    if (asnLineV2.getExpectedDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if(asnLineV2.getExpectedDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
                            }
                            if(asnLineV2.getExpectedDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
                            }
                            apiLine.setExpectedDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                    if (asnLineV2.getExpectedDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = asnLineV2.getExpectedDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiLine.setExpectedDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                }

                apiLine.setOrderedQty(asnLineV2.getExpectedQty());// ORD_QTY
                apiLine.setUom(asnLineV2.getUom());								// ORD_UOM
                apiLine.setPackQty(asnLineV2.getPackQty());					// ITM_CASE_QTY
                apiLine.setNoPairs(asnLineV2.getNoPairs());
                apiLine.setMtoNumber(asnLineV2.getMtoNumber());

                //Setting incoming reversalStatus
                apiLine.setReversalFlag(asnLineV2.getReversalFlag());

                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);

                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // POST ASNV4Header
    private InboundOrderV2  saveASNV4 (ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            //validateBarcodeIds
//			huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));
            apiHeader.setOrderId(asnV2Header.getAsnNumber());
            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
            apiHeader.setBranchCode(asnV2Header.getBranchCode());
            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
            apiHeader.setReversalFlag(asnV2Header.getReversalFlag());

            if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                asnV2Header.getCompanyCode(),
                                asnV2Header.getBranchCode(),
                                asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }

            if (asnV2Header.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(1L);                                            //Default
            }

            if (asnV2Header.getReversalFlag() != null) {
                if (asnV2Header.getReversalFlag().equalsIgnoreCase("X") || asnV2Header.getReversalFlag().equalsIgnoreCase("x")) {
                    apiHeader.setInboundOrderTypeId(10L);
                }
            }

            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setLineReference(asnLineV2.getLineReference()); 			// IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku().trim());							// ITM_CODE
                apiLine.setBarcodeId(asnLineV2.getBarcodeId());
                apiLine.setItemText(asnLineV2.getSkuDescription()); 				// ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());			// CONT_NO
                apiLine.setSupplierCode(asnLineV2.getSupplierCode());				// PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);		// BRAND_NM
                apiLine.setManufacturerCode(MFR_NAME);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                }  else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

                if (asnLineV2.getReversalFlag() != null) {
                    if (asnLineV2.getReversalFlag().equalsIgnoreCase("X") || asnLineV2.getReversalFlag().equalsIgnoreCase("x")) {
                        apiLine.setInboundOrderTypeId(10L);
                    }
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());

                if (asnLineV2.getExpectedDate() != null) {
                    if (asnLineV2.getExpectedDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if(asnLineV2.getExpectedDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
                            }
                            if(asnLineV2.getExpectedDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
                            }
                            apiLine.setExpectedDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                    if (asnLineV2.getExpectedDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = asnLineV2.getExpectedDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiLine.setExpectedDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                }

                apiLine.setOrderedQty(asnLineV2.getExpectedQty());// ORD_QTY
                apiLine.setUom(asnLineV2.getUom());								// ORD_UOM
                apiLine.setPackQty(asnLineV2.getPackQty());					// ITM_CASE_QTY
                apiLine.setNoPairs(asnLineV2.getNoPairs());
                apiLine.setMtoNumber(asnLineV2.getMtoNumber());

                //Setting incoming reversalStatus
                apiLine.setReversalFlag(asnLineV2.getReversalFlag());

                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(1L);
                apiHeader.setExecuted(1L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public InboundOrderV2 postWarehouseStockReceipt (StockReceiptHeader stockReceipt) {
        log.info("StockReceipt received from External: " + stockReceipt);
        InboundOrderV2 savedStockReceipt = saveStockReceipt (stockReceipt);
        log.info("savedStockReceipt: " + savedStockReceipt);
        return savedStockReceipt;
    }

    // POST StockReceiptHeader
    private InboundOrderV2 saveStockReceipt (StockReceiptHeader stockReceipt) {
        try {
//			StockReceiptHeader stockReceiptHeader = stockReceipt.getStockReceiptHeader();
            List<StockReceiptLine> stockReceiptLines = stockReceipt.getStockReceiptLines();

            InboundOrderV2 apiHeader = new InboundOrderV2();

            apiHeader.setOrderId(stockReceipt.getReceiptNo());
            apiHeader.setCompanyCode(stockReceipt.getCompanyCode());
            apiHeader.setBranchCode(stockReceipt.getBranchCode());
            apiHeader.setRefDocumentNo(stockReceipt.getReceiptNo());

            apiHeader.setIsCompleted(stockReceipt.getIsCompleted());
            apiHeader.setUpdatedOn(stockReceipt.getUpdatedOn());

            apiHeader.setRefDocumentType("DirectReceipt");
            apiHeader.setInboundOrderTypeId(5L);
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(stockReceipt.getMiddlewareId());
            apiHeader.setMiddlewareTable(stockReceipt.getMiddlewareTable());

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            stockReceipt.getCompanyCode(),
                            stockReceipt.getBranchCode(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (StockReceiptLine stockReceiptLine : stockReceiptLines) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setLineReference(stockReceiptLine.getLineNoForEachItem()); 			// IB_LINE_NO
                apiLine.setItemCode(stockReceiptLine.getItemCode());							// ITM_CODE
                apiLine.setItemText(stockReceiptLine.getItemDescription()); 				// ITEM_TEXT
//				apiLine.setContainerNumber(stockReceiptLine.getContainerNumber());			// CONT_NO
                apiLine.setSupplierCode(stockReceiptLine.getSupplierCode());				// PARTNER_CODE
                apiLine.setSupplierPartNumber(stockReceiptLine.getSupplierPartNo());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);		// BRAND_NM
                apiLine.setManufacturerCode(MFR_NAME);
                apiLine.setSupplierName(stockReceiptLine.getSupplierName());
                apiLine.setExpectedQty(stockReceiptLine.getReceiptQty());
                apiLine.setOrderId(stockReceiptLine.getReceiptNo());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setCompanyCode(stockReceiptLine.getCompanyCode());
                apiLine.setBranchCode(stockReceiptLine.getBranchCode());
                apiLine.setManufacturerFullName(stockReceiptLine.getManufacturerFullName());
                apiLine.setIsCompleted(stockReceiptLine.getIsCompleted());
                apiLine.setMiddlewareHeaderId(stockReceiptLine.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(stockReceiptLine.getMiddlewareId());
                apiLine.setMiddlewareTable(stockReceiptLine.getMiddlewareTable());
                apiLine.setInboundOrderTypeId(5L);

                if (stockReceiptLine.getReceiptDate() != null) {
//						 EA_DATE
                    apiLine.setExpectedDate(stockReceiptLine.getReceiptDate());
                }

                apiLine.setOrderedQty(stockReceiptLine.getReceiptQty());				// ORD_QTY
                apiLine.setUom(stockReceiptLine.getUnitOfMeasure());								// ORD_UOM
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (stockReceipt.getStockReceiptLines() != null && !stockReceipt.getStockReceiptLines().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("stockReceipt Order Success : " + createdOrder);
                return createdOrder;
            } else if (stockReceipt.getStockReceiptLines() == null || stockReceipt.getStockReceiptLines().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("stockReceipt Order Failed : " + createdOrder);
                throw new BadRequestException("stockReceipt Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param soReturnV2
     * @return
     */
    public InboundOrderV2 postSOReturnV2(SaleOrderReturnV2 soReturnV2) {
        log.info("StoreReturnHeader received from External: " + soReturnV2);
        InboundOrderV2 savedSOReturn = saveSOReturnV2(soReturnV2);
        log.info("soReturnHeader: " + savedSOReturn);
        return savedSOReturn;
    }

    // SOReturnV2
    private InboundOrderV2 saveSOReturnV2(SaleOrderReturnV2 soReturnV2) {
        try {
            SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
            List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            apiHeader.setTransferOrderNumber(soReturnHeaderV2.getTransferOrderNumber());
            apiHeader.setOrderId(soReturnHeaderV2.getTransferOrderNumber());
            apiHeader.setRefDocumentNo(soReturnHeaderV2.getTransferOrderNumber());
            apiHeader.setLanguageId("EN");
            apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
            apiHeader.setOrderId(soReturnHeaderV2.getTransferOrderNumber());
            apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
            apiHeader.setRefDocumentType("SalesReturn");
            apiHeader.setInboundOrderTypeId(2L);                                        // Hardcoded Value 2
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
            apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
            apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());

            // Get Warehouse
            Optional<com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            soReturnHeaderV2.getCompanyCode(),
                            soReturnHeaderV2.getBranchCode(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
                apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
                apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
                apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setManufacturerName(MFR_NAME);        // BRND_NM
                apiLine.setStoreID(soReturnLineV2.getStoreID());
                apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
                apiLine.setUom(soReturnLineV2.getUom());
                apiLine.setPackQty(soReturnLineV2.getPackQty());
                apiLine.setOrigin(soReturnLineV2.getOrigin());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
                apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
                apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
                apiLine.setInboundOrderTypeId(2L);
                // EA_DATE
                try {
                    Date reqDelDate = new Date();
//					if (soReturnLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(soReturnLineV2.getExpectedDate());
//					}
                    if (soReturnLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(soReturnLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setManufacturerCode(soReturnLineV2.getManufacturerCode());
                apiLine.setBrand(soReturnLineV2.getBrand());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());

            if (soReturnV2.getSoReturnLine() != null && !soReturnV2.getSoReturnLine().isEmpty()) {

                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (soReturnV2.getSoReturnLine() == null || soReturnV2.getSoReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrderV2);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    /**
     * @param interWarehouseTransferInV2
     * @return
     */
    public InboundOrderV2 postInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
        InboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
        log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
        return savedIWHReturnV2;
    }


    // InterWarehouseTransferInV2
    private InboundOrderV2 saveInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        try {
            InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
            List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
            apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
            apiHeader.setInboundOrderTypeId(4L);                // Hardcoded Value 3
            apiHeader.setRefDocumentType("WMS to WMS");
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
            apiHeader.setTransferOrderDate(interWarehouseTransferInHeaderV2.getTransferOrderDate());
            apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
            apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());
            apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
            apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());

            // Get Warehouse
            Optional<com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            interWarehouseTransferInHeaderV2.getToCompanyCode(),
                            interWarehouseTransferInHeaderV2.getToBranchCode(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
                apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
                apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);            // BRND_NM
                apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
                apiLine.setUom(iwhTransferLineV2.getUom());
                apiLine.setPackQty(iwhTransferLineV2.getPackQty());
                apiLine.setOrigin(iwhTransferLineV2.getOrigin());
                apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
                apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
                apiLine.setBrand(iwhTransferLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setInboundOrderTypeId(4L);

                apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
                apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

                orderLinesV2.add(apiLine);

                // EA_DATE
                try {
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    String sdate = iwhTransferLineV2.getExpectedDate();
                    String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                    String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                    secondHalf = "/20" + secondHalf;
                    sdate = firstHalf + secondHalf;
                    log.info("sdate--------> : " + sdate);

                    LocalDate localDate = DateUtils.dateConv2(sdate);
                    log.info("localDate--------> : " + localDate);
                    Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                    apiLine.setExpectedDate(date);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                }
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());
            if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
                    !interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
                    interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
                throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }



    /**
     * @param
     * @return
     */
    public InboundOrderV2 postB2bTransferIn(B2bTransferIn b2bTransferIn) {
        log.info("B2bTransferIn received from External: " + b2bTransferIn);
        InboundOrderV2 savedB2bTransferIn = saveB2BTransferIn(b2bTransferIn);
        log.info("B2bTransferIn: " + savedB2bTransferIn);
        return savedB2bTransferIn;
    }

    // B2bTransferIn
    private InboundOrderV2 saveB2BTransferIn(B2bTransferIn b2bTransferIn) {
        try {
            B2bTransferInHeader b2BTransferInHeader = b2bTransferIn.getB2bTransferInHeader();
            List<B2bTransferInLine> b2bTransferInLines = b2bTransferIn.getB2bTransferLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            apiHeader.setTransferOrderNumber(b2BTransferInHeader.getTransferOrderNumber());
            apiHeader.setCompanyCode(b2BTransferInHeader.getCompanyCode());
            apiHeader.setBranchCode(b2BTransferInHeader.getBranchCode());
            apiHeader.setOrderId(b2BTransferInHeader.getTransferOrderNumber());
            apiHeader.setRefDocumentNo(b2BTransferInHeader.getTransferOrderNumber());
            apiHeader.setRefDocumentType("Non-WMS to WMS");
            apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(b2BTransferInHeader.getMiddlewareId());
            apiHeader.setMiddlewareTable(b2BTransferInHeader.getMiddlewareTable());
            apiHeader.setSourceBranchCode(b2BTransferInHeader.getSourceBranchCode());
            apiHeader.setSourceCompanyCode(b2BTransferInHeader.getSourceCompanyCode());
            apiHeader.setTransferOrderDate(b2BTransferInHeader.getTransferOrderDate());
            apiHeader.setIsCompleted(b2BTransferInHeader.getIsCompleted());
            apiHeader.setUpdatedOn(b2BTransferInHeader.getUpdatedOn());

            // Get Warehouse
            Optional<com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            b2BTransferInHeader.getCompanyCode(),
                            b2BTransferInHeader.getBranchCode(),
                            "EN",
                            0L
                    );
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (B2bTransferInLine b2bTransferInLine : b2bTransferInLines) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setLineReference(b2bTransferInLine.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(b2bTransferInLine.getSku());                                // ITM_CODE
                apiLine.setItemText(b2bTransferInLine.getSkuDescription());                     // ITEM_TEXT
                apiLine.setSourceBranchCode(b2bTransferInLine.getStoreID());
                apiLine.setSupplierPartNumber(b2bTransferInLine.getSupplierPartNumber());
                apiLine.setManufacturerName(MFR_NAME);
                apiLine.setExpectedQty(b2bTransferInLine.getExpectedQty());
                apiLine.setCountryOfOrigin(b2bTransferInLine.getOrigin());
                apiLine.setManufacturerCode(b2bTransferInLine.getManufacturerCode());
                apiLine.setBrand(b2bTransferInLine.getBrand());
                apiLine.setSupplierName(b2bTransferInLine.getSupplierName());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(b2bTransferInLine.getManufacturerFullName());
                apiLine.setStoreID(b2bTransferInLine.getStoreID());
                apiLine.setInboundOrderTypeId(3L);

                apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());
                apiLine.setMiddlewareId(b2bTransferInLine.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(b2bTransferInLine.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(b2bTransferInLine.getMiddlewareTable());
//				apiLine.setOrigin(b2bTransferInLine.getOrigin());

                apiLine.setIsCompleted(b2bTransferInLine.getIsCompleted());
                apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());

                // EA_DATE
                try {
                    Date reqDelDate = new Date();
//					if (b2bTransferInLine.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(b2bTransferInLine.getExpectedDate());
//					}
                    if (b2bTransferInLine.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(b2bTransferInLine.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                }

                apiLine.setUom(b2bTransferInLine.getUom());                                    // ORD_UOM

                apiLine.setItemCaseQty(Double.valueOf(b2bTransferInLine.getPackQty()));        // ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (b2bTransferIn.getB2bTransferLine() != null && !b2bTransferIn.getB2bTransferLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrder);
                return createdOrder;
            } else if (b2bTransferIn.getB2bTransferLine() == null || b2bTransferIn.getB2bTransferLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrder);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param interWarehouseTransferInV2
     * @return
     */
    public InboundOrderV2 postInterWarehouseTransferInV2(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
        InboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2(interWarehouseTransferInV2);
        log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
        return savedIWHReturnV2;
    }

    // InterWarehouseTransferInV2
    private InboundOrderV2 saveInterWarehouseTransferInV2(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        try {
            InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
            List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
            apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
            apiHeader.setInboundOrderTypeId(4L);                // Hardcoded Value 3
            apiHeader.setRefDocumentType("WMS to WMS");
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
            apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());
            apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
            apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());

            // Get Warehouse
            Optional<com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            interWarehouseTransferInHeaderV2.getToCompanyCode(),
                            interWarehouseTransferInHeaderV2.getToBranchCode(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
                apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
                apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);            // BRND_NM
                apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
                apiLine.setUom(iwhTransferLineV2.getUom());
                apiLine.setPackQty(iwhTransferLineV2.getPackQty());
                apiLine.setOrigin(iwhTransferLineV2.getOrigin());
                apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
                apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
                apiLine.setInboundOrderTypeId(4L);

                apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
                apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

                // EA_DATE
                try {
                    Date reqDelDate = new Date();
//					if (iwhTransferLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(iwhTransferLineV2.getExpectedDate());
//					}
                    if (iwhTransferLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(iwhTransferLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }
                apiLine.setBrand(iwhTransferLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());
            if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
                    !interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
                    interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
                throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------
    // ASN
//	public AXApiResponse postASNConfirmationV2 (com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNV2 asn,
//											  String authToken) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("User-Agent", "AX-API RestTemplate");
//		headers.add("Authorization", "Bearer " + authToken);
//		UriComponentsBuilder builder =
//				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceAsnUrl());
//		HttpEntity<?> entity = new HttpEntity<>(asn, headers);
//		ResponseEntity<AXApiResponse> result =
//				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
//		log.info("result : " + result.getStatusCode());
//		return result.getBody();
//	}

    // StoreReturn
//	public AXApiResponse postStoreReturnConfirmationV2 (
//			com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.StoreReturn storeReturn,
//			String access_token) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("User-Agent", "AX-API Rest service");
//		headers.add("Authorization", "Bearer " + access_token);
//
//		UriComponentsBuilder builder =
//				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
//		HttpEntity<?> entity = new HttpEntity<>(storeReturn, headers);
//		ResponseEntity<AXApiResponse> result =
//				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
//		log.info("result : " + result.getStatusCode());
//		return result.getBody();
//	}

    // Sale Order Returns
//	public AXApiResponse postSOReturnConfirmationV2 (
//			com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.SOReturn soReturn,
//			String access_token) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("User-Agent", "AX-API Rest service");
//		headers.add("Authorization", "Bearer " + access_token);
//
//		UriComponentsBuilder builder =
//				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
//		HttpEntity<?> entity = new HttpEntity<>(soReturn, headers);
//		ResponseEntity<AXApiResponse> result =
//				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
//		log.info("result : " + result.getStatusCode());
//		return result.getBody();
//	}

    /**
     *
     * @param iwhTransfer
     * @param access_token
     * @return
     */
//	public AXApiResponse postInterWarehouseTransferConfirmationV2(InterWarehouseTransferInV2 iwhTransfer,
//																String access_token) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("User-Agent", "AX-API Rest service");
//		headers.add("Authorization", "Bearer " + access_token);
//
//		UriComponentsBuilder builder =
//				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
//		HttpEntity<?> entity = new HttpEntity<>(iwhTransfer, headers);
//		ResponseEntity<AXApiResponse> result =
//				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
//		log.info("result : " + result.getStatusCode());
//		return result.getBody();
//	}

    /*---------------------------------CycleCountOrder------------------------------------------*/

    /*---------------------------------CycleCountOrder------------------------------------------*/

    /**
     * @param perpetual
     * @return
     */
    public CycleCountHeader postPerpetual(Perpetual perpetual) {
        log.info("CycleCountHeaderOrder received from External: " + perpetual);
        CycleCountHeader savedCycleCount = savePerpetual(perpetual);
        log.info("Perpetual: " + perpetual);
        return savedCycleCount;
    }

    // Perpetual
    private CycleCountHeader savePerpetual(Perpetual perpetual) {
        try {
            PerpetualHeaderV1 perpetualHeaderV1 = perpetual.getPerpetualHeaderV1();
            List<PerpetualLineV1> perpetualLineV1List = perpetual.getPerpetualLineV1();
            CycleCountHeader apiHeader = new CycleCountHeader();
            apiHeader.setCompanyCode(perpetualHeaderV1.getCompanyCode());
            apiHeader.setCycleCountNo(perpetualHeaderV1.getCycleCountNo());
            apiHeader.setOrderId(perpetualHeaderV1.getCycleCountNo());
            apiHeader.setBranchCode(perpetualHeaderV1.getBranchCode());
            apiHeader.setBranchName(perpetualHeaderV1.getBranchName());
            apiHeader.setIsNew(perpetualHeaderV1.getIsNew());
            apiHeader.setCycleCountCreationDate(new Date());
            apiHeader.setMiddlewareId(perpetualHeaderV1.getMiddlewareId());
            apiHeader.setMiddlewareTable(perpetualHeaderV1.getMiddlewareTable());
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setStockCountType("PERPETUAL");
            apiHeader.setIsCancelled(perpetualHeaderV1.getIsCancelled());
            apiHeader.setIsCompleted(perpetualHeaderV1.getIsCompleted());
            apiHeader.setUpdatedOn(perpetualHeaderV1.getUpdatedOn());

            Set<CycleCountLine> cycleCountLines = new HashSet<>();
            for (PerpetualLineV1 perpetualLineV1 : perpetualLineV1List) {
                CycleCountLine apiLine = new CycleCountLine();
                apiLine.setCycleCountNo(perpetualLineV1.getCycleCountNo());                                // CC_NO
                apiLine.setLineOfEachItemCode(perpetualLineV1.getLineNoOfEachItemCode());                    // INV_NO
                apiLine.setItemCode(perpetualLineV1.getItemCode());                                        // ITM_CODE
                apiLine.setItemDescription(perpetualLineV1.getItemDescription());                        // ITM_DESC
                apiLine.setUom(perpetualLineV1.getUom());                                                    // UOM
                apiLine.setManufacturerCode(MFR_NAME);                        // MANU_FAC_CODE
                apiLine.setManufacturerName(MFR_NAME);                        // MANU_FAC_NM
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setMiddlewareId(perpetualLineV1.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(perpetualLineV1.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(perpetualLineV1.getMiddlewareTable());
                apiLine.setFrozenQty(perpetualLineV1.getFrozenQty());
                apiLine.setCountedQty(perpetualLineV1.getCountedQty());
                apiLine.setIsCancelled(perpetualLineV1.getIsCancelled());
                apiLine.setIsCompleted(perpetualLineV1.getIsCompleted());
                apiLine.setStockCountType("PERPETUAL");

                cycleCountLines.add(apiLine);
            }
            apiHeader.setLines(cycleCountLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (perpetual.getPerpetualLineV1() != null && !perpetual.getPerpetualLineV1().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                CycleCountHeader cycleCountHeader = orderService.createCycleCountOrder(apiHeader);
                log.info("Perpetual Order Success: " + cycleCountHeader);
                return cycleCountHeader;
            } else if (perpetual.getPerpetualLineV1() == null || perpetual.getPerpetualLineV1().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                CycleCountHeader createOrder = orderService.createCycleCountOrder(apiHeader);
                log.info("Perpetual Order Failed : " + createOrder);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    //Periodic

    /**
     * @param periodic
     * @return
     */
    public CycleCountHeader postPeriodic(Periodic periodic) {
        log.info("Periodic received from External: " + periodic);
        CycleCountHeader savedCycleCount = savePeriodic(periodic);
        log.info("Periodic: " + periodic);
        return savedCycleCount;
    }

    // periodic
    private CycleCountHeader savePeriodic(Periodic periodic) {
        try {
            PeriodicHeaderV1 periodicHeaderV1 = periodic.getPeriodicHeaderV1();
            List<PeriodicLineV1> periodicLineV1List = periodic.getPeriodicLineV1();
            CycleCountHeader apiHeader = new CycleCountHeader();
            apiHeader.setCompanyCode(periodicHeaderV1.getCompanyCode());
            apiHeader.setCycleCountNo(periodicHeaderV1.getCycleCountNo());
            apiHeader.setOrderId(periodicHeaderV1.getCycleCountNo());
            apiHeader.setBranchCode(periodicHeaderV1.getBranchCode());
            apiHeader.setBranchName(periodicHeaderV1.getBranchName());
            apiHeader.setIsNew(periodicHeaderV1.getIsNew());
            apiHeader.setIsCancelled(periodicHeaderV1.getIsCancelled());
            apiHeader.setIsCompleted(periodicHeaderV1.getIsCompleted());
            apiHeader.setUpdatedOn(periodicHeaderV1.getUpdatedOn());
            apiHeader.setCycleCountCreationDate(periodicHeaderV1.getCycleCountCreationDate());
            apiHeader.setStockCountType("PERIODIC");
            apiHeader.setMiddlewareId(periodicHeaderV1.getMiddlewareId());
            apiHeader.setMiddlewareTable(periodicHeaderV1.getMiddlewareTable());

            Set<CycleCountLine> cycleCountLines = new HashSet<>();
            for (PeriodicLineV1 periodicLineV1 : periodicLineV1List) {
                CycleCountLine apiLine = new CycleCountLine();
                apiLine.setCycleCountNo(periodicLineV1.getCycleCountNo());                                // CC_NO
                apiLine.setLineOfEachItemCode(periodicLineV1.getLineNoOfEachItemCode());                    // INV_NO
                apiLine.setItemCode(periodicLineV1.getItemCode());                                        // ITM_CODE
                apiLine.setItemDescription(periodicLineV1.getItemDescription());                        // ITM_DESC
                apiLine.setUom(periodicLineV1.getUom());                                                    // UOM
                apiLine.setManufacturerCode(MFR_NAME);                        // MANU_FAC_CODE
                apiLine.setManufacturerName(MFR_NAME);                        // MANU_FAC_NM
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setCountedQty(periodicLineV1.getCountedQty());
                apiLine.setFrozenQty(periodicLineV1.getFrozenQty());
                apiLine.setIsCancelled(periodicLineV1.getIsCancelled());
                apiLine.setIsCompleted(periodicLineV1.getIsCompleted());
                apiLine.setStockCountType("PERIODIC");
                apiLine.setMiddlewareId(periodicLineV1.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(periodicLineV1.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(periodicLineV1.getMiddlewareTable());

                cycleCountLines.add(apiLine);
            }
            apiHeader.setLines(cycleCountLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (periodic.getPeriodicLineV1() != null && !periodic.getPeriodicLineV1().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                CycleCountHeader cycleCountHeader = orderService.createCycleCountOrder(apiHeader);
                log.info("Periodic Order Success: " + cycleCountHeader);
                return cycleCountHeader;
            } else if (periodic.getPeriodicLineV1() == null || periodic.getPeriodicLineV1().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                CycleCountHeader createOrder = orderService.createCycleCountOrder(apiHeader);
                log.info("Periodic Order Failed : " + createOrder);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     *
     * @param stockAdjustmentList
     * @return
     */
    public List<StockAdjustment> postStockAdjustmentUpload(List<StockAdjustment> stockAdjustmentList) {
        List<StockAdjustment> savedStockAdjustmentList = new ArrayList<>();
        for(StockAdjustment stockAdjustment : stockAdjustmentList) {
            log.info("StockAdjustment received from external: " + stockAdjustment);
            StockAdjustment savedStockAdjustment = saveStockAdjustment(stockAdjustment);
            log.info("Saved StockAdjustment: " + savedStockAdjustment);
            savedStockAdjustmentList.add(savedStockAdjustment);
        }
        return savedStockAdjustmentList;
    }

    /**
     *
     * @param stockAdjustment
     * @return
     */
    public StockAdjustment postStockAdjustment(StockAdjustment stockAdjustment) {
        log.info("StockAdjustment received from external: " + stockAdjustment);
        StockAdjustment savedStockAdjustment = saveStockAdjustment(stockAdjustment);
        log.info("Saved StockAdjustment: " + savedStockAdjustment);
        return savedStockAdjustment;
    }

    /**
     *
     * @param list  Utility method to partition the list into smaller sublists
     * @param size 2000 only at a time process
     * @return
     * @param <T> generic type
     */
    private <T> List<List<T>> partitionList(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    /**
     * deadlockErrorMessage
     *
     * @param errorDesc
     * @return
     */
    private boolean deadLockException(String errorDesc) {
        if ((errorDesc.contains("SQLState: 40001") || errorDesc.contains("SQL Error: 1205")) ||
                errorDesc.contains("was deadlocked on lock") || errorDesc.contains("CannotAcquireLockException") ||
                errorDesc.contains("LockAcquisitionException") || errorDesc.contains("UnexpectedRollbackException")) {
            return true;
        }
        return false;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param referenceField
     * @param error
     * @throws Exception
     */
    private void sendMail(String companyCodeId, String plantId, String languageId, String warehouseId,
                          String refDocNumber, String referenceField, String error) throws Exception {
        try {
            InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
            inboundOrderCancelInput.setCompanyCodeId(companyCodeId);
            inboundOrderCancelInput.setPlantId(plantId);
            inboundOrderCancelInput.setLanguageId(languageId);
            inboundOrderCancelInput.setWarehouseId(warehouseId);
            inboundOrderCancelInput.setRefDocNumber(refDocNumber);
            inboundOrderCancelInput.setReferenceField1(referenceField);
            inboundOrderCancelInput.setRemarks(errorMessageExtraction(error));
            mastersService.sendMail(inboundOrderCancelInput);
        } catch (Exception ex) {
            log.error("Exception occurred while Sending Mail " + ex.toString());
            throw ex;
        }
    }

    /**
     * @param error
     * @return
     */
    private String errorMessageExtraction(String error) throws Exception {
        String errorDesc = error;
        try {
            if (error.contains("message")) {
                errorDesc = error.substring(error.indexOf("message") + 9);
                errorDesc = errorDesc.replaceAll("}]", "");
            }
            if (error.contains("DataIntegrityViolationException") || error.contains("ConstraintViolationException")) {
                errorDesc = "Null Pointer Exception";
            }
            if (error.contains("CannotAcquireLockException") || error.contains("LockAcquisitionException") ||
                    error.contains("SQLServerException") || error.contains("UnexpectedRollbackException")) {
                errorDesc = "SQLServerException";
            }
            if (error.contains("BadRequestException")) {
                errorDesc = error.substring(error.indexOf("BadRequestException:") + 20);
            }
            return errorDesc;
        } catch (Exception ex) {
            throw ex;
        }
    }

    //Save StockAdjustment
    private StockAdjustment saveStockAdjustment(StockAdjustment stockAdjustment) {
        try {

            StockAdjustment dbStockAdjustment = new StockAdjustment();
            dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
            dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());
            dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
            dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
            dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
            dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
            dbStockAdjustment.setItemCode(stockAdjustment.getItemCode());
            dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
            dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
            dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
            dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
            dbStockAdjustment.setManufacturerName(stockAdjustment.getManufacturerName());
            dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
            dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
            dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
            dbStockAdjustment.setUpdatedOn(stockAdjustment.getUpdatedOn());
            dbStockAdjustment.setMiddlewareId(stockAdjustment.getStockAdjustmentId());
            dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(),
                            "EN", 0L);
            log.info("dbWarehouse: " + dbWarehouse);
            validateWarehouseId(dbWarehouse.get().getWarehouseId());
            dbStockAdjustment.setWarehouseId(dbWarehouse.get().getWarehouseId());

            dbStockAdjustment.setRefDocType("Stock Adjustment");
            dbStockAdjustment.setOrderReceivedOn(new Date());
//			dbStockAdjustment.setOrderProcessedOn(new Date());

            IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
                    stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), dbWarehouse.get().getWarehouseId());
            if (iKeyValuePair != null) {
                dbStockAdjustment.setCompanyDescription(iKeyValuePair.getCompanyDesc());
                dbStockAdjustment.setPlantDescription(iKeyValuePair.getPlantDesc());
                dbStockAdjustment.setWarehouseDescription(iKeyValuePair.getWarehouseDesc());
            }

            if (dbStockAdjustment != null) {
                try {
                    dbStockAdjustment.setProcessedStatusId(0L);
                    log.info("stockAdjustment: " + dbStockAdjustment);
                    StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
                    log.info("StockAdjustment Order Success: " + createdOrder);
                    return createdOrder;
                } catch (Exception e) {
                    dbStockAdjustment.setProcessedStatusId(100L);
                    log.info("StockAdjustment: " + dbStockAdjustment);
                    StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
                    log.info("StockAdjustment Order Failed: " + createdOrder);
                    throw e;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     *
     * @param barcodeIdList
     */
//	public void barcodeIdValidation (List<String> barcodeIdList) {
//		boolean result = inboundOrderLinesV2Repository.existsBybarcodeIdIn(barcodeIdList);
//		log.info("Duplicate HU Serial No: " + result);
//		if(result) {
//			throw new BadRequestException("HU-SerialNo/BarcodeId already exists..!");
//		}
//	}

    /**
     *
     * @param barcodeIdList
     */
    public void barcodeIdValidation (List<String> barcodeIdList) {
        List<String> result = inboundOrderLinesV2Repository.findAllByBarcodeIdIn(barcodeIdList);
        log.info("Duplicate HU Serial No: " + result);
        if(result.size() > 0) {
            throw new BadRequestException("HU-SerialNo/BarcodeId already exists..! " + result);
        }
    }

    /**
     *
     * @param barcodeIdList
     */
    public void barcodeIdValidationV3(List<String> barcodeIdList) {
        List<String> duplicateList = new ArrayList<>();
        barcodeIdList.stream().forEach(n -> {
            boolean result = inboundOrderLinesV2Repository.existsByBarcodeId(n);
            log.info("Duplicate HU Serial No: " + result);
            if (result) {
                duplicateList.add(n);
            }
        });
        if (duplicateList.size() > 0) {
            throw new BadRequestException("HU-SerialNo/BarcodeId already exists..! " + duplicateList);
        }
    }

    /**
     *
     * @param asnLines
     */
    public void huSerialValidation (List<ASNLineV2> asnLines, String refDocNumber) {
        if(asnLines != null && !asnLines.isEmpty()) {
            List<String> barcodeIds = asnLines.stream().filter(n->n.getBarcodeId() != null && (!n.getReversalFlag().equalsIgnoreCase("X") || !n.getReversalFlag().equalsIgnoreCase("x"))).map(ASNLineV2::getBarcodeId).collect(Collectors.toList());
            log.info("BarcodeId: " + barcodeIds.size());
            barcodeIdValidation(barcodeIds, refDocNumber);
        }
    }

    /**
     *
     * @param barcodeIdList
     * @param refDocNumber
     */
    public void barcodeIdValidation(List<String> barcodeIdList, String refDocNumber) {
        List<String> result = inboundOrderLinesV2Repository.findAllByBarcodeIdIn(barcodeIdList);
        log.info("Duplicate HU Serial No: " + result);
        if (result.size() > 0) {
            List<String> duplicates = new ArrayList<>(result.size());
            for (String barcode : result) {
                boolean pass = inboundOrderLinesV2Repository.existsByBarcodeIdAndOrderId(barcode, refDocNumber);
                if (pass) {
                    duplicates.add(barcode);
                }
            }
            if (duplicates.size() > 0) {
                throw new BadRequestException("HU-SerialNo/BarcodeId already exists for this order number..! " + result + "|" + refDocNumber);
            }
        }
    }

}