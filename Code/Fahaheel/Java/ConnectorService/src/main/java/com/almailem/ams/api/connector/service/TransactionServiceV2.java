package com.almailem.ams.api.connector.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.wms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.PeriodicLine;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.PerpetualLine;
import com.almailem.ams.api.connector.model.picklist.PickListHeader;
import com.almailem.ams.api.connector.model.picklist.PickListLine;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnHeader;
import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnLine;
import com.almailem.ams.api.connector.model.salesinvoice.SalesInvoice;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.salesreturn.SalesReturnLine;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptLine;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceLine;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInLine;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import com.almailem.ams.api.connector.repository.PeriodicHeaderRepository;
import com.almailem.ams.api.connector.repository.PerpetualHeaderRepository;
import com.almailem.ams.api.connector.repository.PickListHeaderRepository;
import com.almailem.ams.api.connector.repository.PurchaseReturnHeaderRepository;
import com.almailem.ams.api.connector.repository.SalesInvoiceRepository;
import com.almailem.ams.api.connector.repository.SalesReturnHeaderRepository;
import com.almailem.ams.api.connector.repository.StockAdjustmentRepository;
import com.almailem.ams.api.connector.repository.StockReceiptHeaderRepository;
import com.almailem.ams.api.connector.repository.SupplierInvoiceHeaderRepository;
import com.almailem.ams.api.connector.repository.TransferInHeaderRepository;
import com.almailem.ams.api.connector.repository.TransferOutHeaderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceV2 {

    @Autowired
    SupplierInvoiceService supplierInvoiceService;

    @Autowired
    StockReceiptService stockReceiptService;

    @Autowired
    SalesReturnService salesReturnService;

    @Autowired
    B2BTransferInService b2BTransferInService;

    @Autowired
    InterWarehouseTransferInService interWarehouseTransferInService;

    @Autowired
    ReturnPOService returnPOService;

    @Autowired
    InterWarehouseTransferOutService interWarehouseTransferOutService;

    @Autowired
    ShipmentOrderService shipmentOrderService;

    @Autowired
    SalesInvoiceService salesInvoiceService;

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    PerpetualService perpetualService;

    @Autowired
    PeriodicService periodicService;

    @Autowired
    MastersService mastersService;

    //-------------------------------------------------------------------------------------------

    @Autowired
    SupplierInvoiceHeaderRepository supplierInvoiceHeaderRepository;

    @Autowired
    StockReceiptHeaderRepository stockReceiptHeaderRepository;

    @Autowired
    SalesReturnHeaderRepository salesReturnHeaderRepository;

    @Autowired
    TransferInHeaderRepository transferInHeaderRepository;

    @Autowired
    PurchaseReturnHeaderRepository purchaseReturnHeaderRepository;

    @Autowired
    TransferOutHeaderRepository transferOutHeaderRepository;

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    SalesInvoiceRepository salesInvoiceRepository;

    @Autowired
    PerpetualHeaderRepository perpetualHeaderRepository;

    @Autowired
    PeriodicHeaderRepository periodicHeaderRepository;

    @Autowired
    StockAdjustmentService stockAdjustmentService;

    @Autowired
    StockAdjustmentRepository stockAdjustmentRepo;

    @Autowired
    IntegrationLogService integrationLogService;


    //-------------------------------------------------------------------------------------------
    List<ASN> inboundList = null;
    List<com.almailem.ams.api.connector.model.wms.StockReceiptHeader> inboundSRList = null;
    List<SaleOrderReturn> inboundSRTList = null;
    List<B2bTransferIn> inboundB2BList = null;
    List<InterWarehouseTransferIn> inboundIWTList = null;
    List<ReturnPO> outboundRPOList = null;
    List<InterWarehouseTransferOut> outboundIWhtList = null;
    List<ShipmentOrder> outboundSOList = null;

    List<SalesOrder> outboundSalesOrderList = null;
    List<com.almailem.ams.api.connector.model.wms.SalesInvoice> outboundSIList = null;

    List<Perpetual> stcPerpetualList = null;
    List<Periodic> stcPeriodicList = null;

    List<StockAdjustment> saList = null;

    //=================================================================================================================
    static CopyOnWriteArrayList<ASN> spList = null;                               // ASN Inbound List
    static CopyOnWriteArrayList<com.almailem.ams.api.connector.model.wms.StockReceiptHeader> spSRList = null;                // StockReceipt Inbound List
    static CopyOnWriteArrayList<SaleOrderReturn> spSRTList = null;                // SaleOrder Inbound List
    static CopyOnWriteArrayList<B2bTransferIn> spB2BList = null;                    // B2B Inbound List
    static CopyOnWriteArrayList<InterWarehouseTransferIn> spIWTList = null;       // InterWarehouse Inbound List

    static CopyOnWriteArrayList<ReturnPO> spRPOList = null;                       // ReturnPO Outbound List
    static CopyOnWriteArrayList<InterWarehouseTransferOut> spIWhtList = null;     // InterWarehouseTransfer Outbound List
    static CopyOnWriteArrayList<ShipmentOrder> spSOList = null;                   // ShipmentOrder Outbound List

    static CopyOnWriteArrayList<SalesOrder> spSalesOrderList = null;                               // ASN Inbound List
    static CopyOnWriteArrayList<com.almailem.ams.api.connector.model.wms.SalesInvoice> spSIList = null;

    static CopyOnWriteArrayList<Perpetual> spPerpetualList = null;                   // Perpetual List
    static CopyOnWriteArrayList<Periodic> spPeriodicList = null;                     // Periodic List
    //==========================================================================================================================

    static CopyOnWriteArrayList<StockAdjustment> spStockAdjustmentList = null;       // Stock Adjustment List

    final List<String> branchCode = List.of("219", "123");

    // SupplierInvoice
    public WarehouseApiResponse processInboundOrder() throws IllegalAccessException, InvocationTargetException {

        List<SupplierInvoiceHeader> supplierInvoiceHeaders = supplierInvoiceHeaderRepository.findBySupplierInvoice(0L, branchCode);
        log.info("Order Received On SupplierInvoiceHeaders: " + supplierInvoiceHeaders);
        long startTime = System.currentTimeMillis();  // Start time
        log.info("Inbound order processing started at: " + new Date(startTime));
        if (!supplierInvoiceHeaders.isEmpty()) {
            supplierInvoiceHeaders.forEach(asn -> {
                supplierInvoiceService.updateProcessedInboundOrder(
                        asn.getSupplierInvoiceHeaderId(),
                        asn.getCompanyCode(),
                        asn.getBranchCode(),
                        asn.getSupplierInvoiceNo(),
                        1L // Starting Process
                );
                log.info("ASN : " + asn.getSupplierInvoiceNo() + " processed successfully.");
            });
        }

        List<ASN> asnList = new ArrayList<>();
        for (SupplierInvoiceHeader dbIBOrder : supplierInvoiceHeaders) {
            ASN asn = new ASN();
            ASNHeader asnHeader = new ASNHeader();
            asnHeader.setAsnNumber(dbIBOrder.getSupplierInvoiceNo());
            asnHeader.setCompanyCode(dbIBOrder.getCompanyCode());
            asnHeader.setBranchCode(dbIBOrder.getBranchCode());
            asnHeader.setIsCancelled(dbIBOrder.getIsCancelled());
            asnHeader.setIsCompleted(dbIBOrder.getIsCompleted());
            asnHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
            asnHeader.setMiddlewareId(dbIBOrder.getSupplierInvoiceHeaderId());
            asnHeader.setMiddlewareTable("IB_SUPPLIER_INVOICE");

            List<ASNLine> asnLineList = new ArrayList<>();
            for (SupplierInvoiceLine line : dbIBOrder.getSupplierInvoiceLines()) {
                ASNLine asnLine = new ASNLine();

                asnLine.setSku(line.getItemCode());
                asnLine.setSkuDescription(line.getItemDescription());
                asnLine.setManufacturerCode(line.getManufacturerCode());
                asnLine.setManufacturerName(line.getManufacturerShortName());
                asnLine.setSupplierPartNumber(line.getSupplierPartNo());
                asnLine.setSupplierName(line.getSupplierName());
                asnLine.setSupplierCode(line.getSupplierCode());
                asnLine.setPackQty(line.getInvoiceQty());
                asnLine.setUom(line.getUnitOfMeasure());
                asnLine.setExpectedQty(line.getInvoiceQty());
                asnLine.setExpectedDate(String.valueOf(line.getInvoiceDate()));
                asnLine.setLineReference(line.getLineNoForEachItem());
                asnLine.setContainerNumber(line.getContainerNo());
                asnLine.setManufacturerFullName(line.getManufacturerFullName());
                asnLine.setPurchaseOrderNumber(line.getPurchaseOrderNo());
                asnLine.setCompanyCode(line.getCompanyCode());
                asnLine.setBranchCode(line.getBranchCode());
                asnLine.setReceivedDate(line.getReceivedDate());
                asnLine.setReceivedQty(line.getReceivedQty());
                asnLine.setReceivedBy(line.getReceivedBy());
                asnLine.setIsCancelled(line.getIsCancelled());
                asnLine.setIsCompleted(line.getIsCompleted());
                asnLine.setManufacturerFullName(line.getManufacturerFullName());
                asnLine.setSupplierInvoiceNo(line.getSupplierInvoiceNo());
                asnLine.setMiddlewareId(line.getSupplierInvoiceLineId());
                asnLine.setMiddlewareHeaderId(dbIBOrder.getSupplierInvoiceHeaderId());
                asnLine.setMiddlewareTable("IB_SUPPLIER_INVOICE");

                asnLineList.add(asnLine);
            }
            asn.setAsnHeader(asnHeader);
            asn.setAsnLine(asnLineList);
            asnList.add(asn);
        }

        if (!asnList.isEmpty()) {
            try {
                ASN[] asnv2 = supplierInvoiceService.postASNV2(asnList);
                if (asnv2 != null) {
                    Arrays.stream(asnv2).forEach(asn -> {
                        // Updating the Processed Status = 10 for successful orders
                        supplierInvoiceService.updateProcessedInboundOrder(
                                asn.getAsnHeader().getMiddlewareId(),
                                asn.getAsnHeader().getCompanyCode(),
                                asn.getAsnHeader().getBranchCode(),
                                asn.getAsnHeader().getAsnNumber(),
                                10L // Success status
                        );
                        log.info("ASN : " + asn.getAsnHeader().getAsnNumber() + " processed successfully.");
                    });
                }
            } catch (Exception e) {
                log.error("Error on inbound processing: ", e);

                // Update the status to 100 for failed orders
                for (ASN asn : asnList) {
                    supplierInvoiceService.updateProcessedInboundOrder(
                            asn.getAsnHeader().getMiddlewareId(),
                            asn.getAsnHeader().getCompanyCode(),
                            asn.getAsnHeader().getBranchCode(),
                            asn.getAsnHeader().getAsnNumber(),
                            100L // Failure status
                    );
                    log.error("ASN : " + asn.getAsnHeader().getAsnNumber() + " failed. Status updated to 100.");
                }

                // Optionally, rethrow the exception or handle it as needed
                throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
            }
        }
        long endTime = System.currentTimeMillis();  // End time
        log.info("Inbound order processing ended at: " + new Date(endTime));
        log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");


        return null;
    }


    //=====================================================StockReceipt============================================================
    public WarehouseApiResponse processInboundOrderSR() throws IllegalAccessException, InvocationTargetException {

        List<StockReceiptHeader> stockReceiptHeaders = stockReceiptHeaderRepository.findByStockReceipt(0L,branchCode);
        log.info("Order Received On stockReceiptHeaders: " + stockReceiptHeaders);
        long startTime = System.currentTimeMillis();  // Start time
        log.info("Inbound order processing started at: " + new Date(startTime));
        if (!stockReceiptHeaders.isEmpty()) {
            stockReceiptHeaders.forEach(ibSR -> {
                stockReceiptService.updateProcessedInboundOrder(
                        ibSR.getStockReceiptHeaderId(),
                        ibSR.getCompanyCode(),
                        ibSR.getBranchCode(),
                        ibSR.getReceiptNo(),
                        1L // Starting Process
                );
                log.info("IBStockReceipt : " + ibSR.getReceiptNo() + " processed successfully.");
            });
        }

        List<StockReceipt> stkList = new ArrayList<>();
        for (StockReceiptHeader dbIBOrder : stockReceiptHeaders) {
            StockReceipt stk = new StockReceipt();
            com.almailem.ams.api.connector.model.wms.StockReceiptHeader stockReceiptHeader = new com.almailem.ams.api.connector.model.wms.StockReceiptHeader();
            stockReceiptHeader.setCompanyCode(dbIBOrder.getCompanyCode());
            stockReceiptHeader.setBranchCode(dbIBOrder.getBranchCode());
            stockReceiptHeader.setReceiptNo(dbIBOrder.getReceiptNo());
            stockReceiptHeader.setIsCompleted(dbIBOrder.getIsCompleted());
            stockReceiptHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
            stockReceiptHeader.setMiddlewareId(dbIBOrder.getStockReceiptHeaderId());
            stockReceiptHeader.setMiddlewareTable("IB_STOCK_RECEIPT");


            List<com.almailem.ams.api.connector.model.wms.StockReceiptLine> stockReceiptLineList = new ArrayList<>();
            for (StockReceiptLine line : dbIBOrder.getStockReceiptLines()) {
                com.almailem.ams.api.connector.model.wms.StockReceiptLine stockReceiptLine = new com.almailem.ams.api.connector.model.wms.StockReceiptLine();

                stockReceiptLine.setItemCode(line.getItemCode());
                stockReceiptLine.setItemDescription(line.getItemDescription());
                stockReceiptLine.setManufacturerCode(line.getManufacturerCode());
                stockReceiptLine.setManufacturerShortName(line.getManufacturerShortName());
                stockReceiptLine.setSupplierPartNo(line.getSupplierPartNo());
                stockReceiptLine.setSupplierName(line.getSupplierName());
                stockReceiptLine.setSupplierCode(line.getSupplierCode());
                stockReceiptLine.setUnitOfMeasure(line.getUnitOfMeasure());
                stockReceiptLine.setReceiptQty(line.getReceiptQty());
                stockReceiptLine.setReceiptDate(line.getReceiptDate());
                stockReceiptLine.setLineNoForEachItem(line.getLineNoForEachItem());
                stockReceiptLine.setManufacturerFullName(line.getManufacturerFullName());

                stockReceiptLine.setReceiptNo(line.getReceiptNo());
                stockReceiptLine.setManufacturerFullName(line.getManufacturerFullName());
                stockReceiptLine.setIsCompleted(line.getIsCompleted());

                stockReceiptLine.setBranchCode(line.getBranchCode());
                stockReceiptLine.setCompanyCode(line.getCompanyCode());
                stockReceiptLine.setMiddlewareId(line.getStockReceiptLineId());
                stockReceiptLine.setMiddlewareHeaderId(dbIBOrder.getStockReceiptHeaderId());
                stockReceiptLine.setMiddlewareTable("IB_STOCK_RECEIPT");

                stockReceiptLineList.add(stockReceiptLine);

            }
            stk.setStockReceiptHeader(stockReceiptHeader);
            stk.setStockReceiptLines(stockReceiptLineList);
            stkList.add(stk);
        }
        if (!stkList.isEmpty()) {
            try {
                StockReceipt[] stkV2 = stockReceiptService.postStockReceipt(stkList);
                if (stkV2 != null) {
                    Arrays.stream(stkV2).forEach(stk -> {
                        // Updating the Processed Status = 10 for successful orders
                        stockReceiptService.updateProcessedInboundOrder(
                                stk.getStockReceiptHeader().getMiddlewareId(),
                                stk.getStockReceiptHeader().getCompanyCode(),
                                stk.getStockReceiptHeader().getBranchCode(),
                                stk.getStockReceiptHeader().getReceiptNo(),
                                10L // Success status
                        );
                        log.info("IBStockReceipt : " + stk.getStockReceiptHeader().getReceiptNo() + " processed successfully.");
                    });
                }
            } catch (Exception e) {
                log.error("Error on inbound processing: ", e);

                // Update the status to 100 for failed orders
                for (StockReceipt stk : stkList) {
                    stockReceiptService.updateProcessedInboundOrder(
                            stk.getStockReceiptHeader().getMiddlewareId(),
                            stk.getStockReceiptHeader().getCompanyCode(),
                            stk.getStockReceiptHeader().getBranchCode(),
                            stk.getStockReceiptHeader().getReceiptNo(),
                            100L // Failure status
                    );
                    log.error("IBStock Receipt : " + stk.getStockReceiptHeader().getReceiptNo() + " failed. Status updated to 100.");
                }

                // Optionally, rethrow the exception or handle it as needed
                throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
            }
        }
        long endTime = System.currentTimeMillis();  // End time
        log.info("Inbound order processing ended at: " + new Date(endTime));
        log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");


        return null;

    }




    //=====================================================SalesReturn============================================================
    public WarehouseApiResponse processInboundOrderSRT() throws IllegalAccessException, InvocationTargetException {

        List<SalesReturnHeader> salesReturnHeaders = salesReturnHeaderRepository.findBySalesReturn(0L,branchCode);
        log.info("Order Received On salesReturnHeaders: " + salesReturnHeaders);

        long startTime = System.currentTimeMillis();  // Start time
        log.info("Inbound order processing started at: " + new Date(startTime));
        if (!salesReturnHeaders.isEmpty()) {
            salesReturnHeaders.forEach(ibSRT -> {
                salesReturnService.updateProcessedInboundOrder(
                        ibSRT.getSalesReturnHeaderId(),
                        ibSRT.getCompanyCode(),
                        ibSRT.getBranchCode(),
                        ibSRT.getReturnOrderNo(),
                        1L // Starting Process
                );
                log.info("IBSales Return : " + ibSRT.getReturnOrderNo() + " processed successfully.");
            });
        }

        List<SaleOrderReturn> salesReturnList = new ArrayList<>();
        for (SalesReturnHeader dbIBOrder : salesReturnHeaders) {
            SaleOrderReturn sor = new SaleOrderReturn();
            com.almailem.ams.api.connector.model.wms.SOReturnHeader salesReturnHeader = new com.almailem.ams.api.connector.model.wms.SOReturnHeader();
            salesReturnHeader.setCompanyCode(dbIBOrder.getCompanyCode());
            salesReturnHeader.setBranchCode(dbIBOrder.getBranchCode());
            salesReturnHeader.setTransferOrderNumber(dbIBOrder.getReturnOrderNo());
            salesReturnHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
            salesReturnHeader.setIsCompleted(dbIBOrder.getIsCompleted());
            salesReturnHeader.setIsCancelled(dbIBOrder.getIsCancelled());
            salesReturnHeader.setMiddlewareId(dbIBOrder.getSalesReturnHeaderId());
            salesReturnHeader.setMiddlewareTable("IB_SALE_RETURN");

            List<com.almailem.ams.api.connector.model.wms.SOReturnLine> sOReturnList = new ArrayList<>();
            for (SalesReturnLine line : dbIBOrder.getSalesReturnLines()) {
                com.almailem.ams.api.connector.model.wms.SOReturnLine salesReturnLine = new com.almailem.ams.api.connector.model.wms.SOReturnLine();

                salesReturnLine.setLineReference(line.getLineNoOfEachItem());
                salesReturnLine.setSku(line.getItemCode());
                salesReturnLine.setSkuDescription(line.getItemDescription());
                salesReturnLine.setInvoiceNumber(line.getReferenceInvoiceNo());
                salesReturnLine.setStoreID(line.getSourceBranchCode());
                salesReturnLine.setSupplierPartNumber(line.getSupplierPartNo());
                salesReturnLine.setManufacturerName(line.getManufacturerShortName());
                salesReturnLine.setExpectedDate(String.valueOf(line.getReturnOrderDate()));
                salesReturnLine.setExpectedQty(line.getReturnQty());
                salesReturnLine.setUom(line.getUnitOfMeasure());
                salesReturnLine.setIsCancelled(line.getIsCancelled());
                salesReturnLine.setIsCompleted(line.getIsCompleted());
                salesReturnLine.setSourceBranchCode(line.getSourceBranchCode());

                if (line.getNoOfPacks() != null) {
                    salesReturnLine.setPackQty(Double.valueOf(line.getNoOfPacks()));
                }
                salesReturnLine.setOrigin(line.getCountryOfOrigin());
                salesReturnLine.setManufacturerCode(line.getManufacturerCode());
                salesReturnLine.setManufacturerFullName(line.getManufacturerFullName());
                salesReturnLine.setMiddlewareId(line.getSalesReturnLineId());
                salesReturnLine.setMiddlewareHeaderId(dbIBOrder.getSalesReturnHeaderId());
                salesReturnLine.setMiddlewareTable("IB_SALE_RETURN");

                sOReturnList.add(salesReturnLine);

            }
            sor.setSoReturnHeader(salesReturnHeader);
            sor.setSoReturnLine(sOReturnList);
            salesReturnList.add(sor);
        }

        if (!salesReturnList.isEmpty()) {
            try {
                SaleOrderReturn[] sorV2 = salesReturnService.postSaleOrderReturn(salesReturnList);
                if (sorV2 != null) {
                    Arrays.stream(sorV2).forEach(sor -> {
                        // Updating the Processed Status = 10 for successful orders
                        salesReturnService.updateProcessedInboundOrder(
                                sor.getSoReturnHeader().getMiddlewareId(),
                                sor.getSoReturnHeader().getCompanyCode(),
                                sor.getSoReturnHeader().getBranchCode(),
                                sor.getSoReturnHeader().getTransferOrderNumber(),
                                10L // Success status
                        );
                        log.info("IBSales Return : " + sor.getSoReturnHeader().getTransferOrderNumber() + " processed successfully.");
                    });
                }
            } catch (Exception e) {
                log.error("Error on inbound processing: ", e);

                // Update the status to 100 for failed orders
                for (SaleOrderReturn sor : salesReturnList) {
                    salesReturnService.updateProcessedInboundOrder(
                            sor.getSoReturnHeader().getMiddlewareId(),
                            sor.getSoReturnHeader().getCompanyCode(),
                            sor.getSoReturnHeader().getBranchCode(),
                            sor.getSoReturnHeader().getTransferOrderNumber(),
                            100L // Failure status
                    );
                    log.error("IBSales Return : " + sor.getSoReturnHeader().getTransferOrderNumber() + " failed. Status updated to 100.");
                }

                // Optionally, rethrow the exception or handle it as needed
                throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
            }
        }
        long endTime = System.currentTimeMillis();  // End time
        log.info("Inbound order processing ended at: " + new Date(endTime));
        log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");


        return null;
    }


    //=====================================================Interwarehouse============================================================
    public WarehouseApiResponse processInboundOrderIWT() throws IllegalAccessException, InvocationTargetException {

        List<TransferInHeader> transferInHeaders = transferInHeaderRepository.findByTransferIn(0L,branchCode);
        log.info("Order Received On transferInHeaders: " + transferInHeaders);

        long startTime = System.currentTimeMillis();  // Start time
        log.info("Inbound order processing started at: " + new Date(startTime));
        if (!transferInHeaders.isEmpty()) {
            transferInHeaders.forEach(ibTransfer -> {
                b2BTransferInService.updateProcessedInboundOrder(
                        ibTransfer.getTransferInHeaderId(),
                        ibTransfer.getSourceCompanyCode(),
                        ibTransfer.getSourceBranchCode(),
                        ibTransfer.getTransferOrderNo(),
                        1L // Success status
                );
                log.info("IBTransfer In : " + ibTransfer.getTransferOrderNo() + " processed successfully.");
            });
        }

        List<B2bTransferIn> b2bTransferInList = new ArrayList<>();
        for (TransferInHeader dbIBOrder : transferInHeaders) {
            String[] branchcode = new String[]{"115", "125", "212", "222", "219", "123"};
            boolean sourceBranchExist = Arrays.stream(branchcode).anyMatch(n -> n.equalsIgnoreCase(dbIBOrder.getSourceBranchCode()));
            boolean targetBranchExist = Arrays.stream(branchcode).anyMatch(n -> n.equalsIgnoreCase(dbIBOrder.getTargetBranchCode()));

            log.info("sourceBranchExist {}, targetBranchExist{} ", sourceBranchExist, targetBranchExist);

            B2bTransferIn b2bTransferIn = null;
            List<B2bTransferInLine> b2bTransferInLineList = null;
            B2bTransferInHeader b2bTransferInHeader = null;
            if (!sourceBranchExist && !targetBranchExist) {
                log.info("IB NON WMS to NON WMS {}, {} ", sourceBranchExist, targetBranchExist);
                b2bTransferIn = new B2bTransferIn();
                b2bTransferInHeader = new B2bTransferInHeader();
                b2bTransferInHeader.setCompanyCode(dbIBOrder.getTargetCompanyCode());
                b2bTransferInHeader.setBranchCode(dbIBOrder.getTargetBranchCode());
                b2bTransferInHeader.setTransferOrderNumber(dbIBOrder.getTransferOrderNo());
                b2bTransferInHeader.setSourceBranchCode(dbIBOrder.getSourceBranchCode());
                b2bTransferInHeader.setSourceCompanyCode(dbIBOrder.getSourceCompanyCode());
                b2bTransferInHeader.setMiddlewareId(dbIBOrder.getTransferInHeaderId());
                b2bTransferInHeader.setMiddlewareTable("IB_NONWMS_TO_NONWMS");
                b2bTransferInHeader.setTransferOrderDate(dbIBOrder.getTransferOrderDate());
                b2bTransferInHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
                b2bTransferInHeader.setIsCompleted(dbIBOrder.getIsCompleted());

                b2bTransferInLineList = new ArrayList<>();
                for (TransferInLine line : dbIBOrder.getTransferInLines()) {
                    com.almailem.ams.api.connector.model.wms.B2bTransferInLine b2bTransferInLine = new com.almailem.ams.api.connector.model.wms.B2bTransferInLine();

                    b2bTransferInLine.setLineReference(line.getLineNoOfEachItem());
                    b2bTransferInLine.setSku(line.getItemCode());
                    b2bTransferInLine.setSkuDescription(line.getItemDescription());
                    b2bTransferInLine.setManufacturerName(line.getManufacturerShortName());
                    b2bTransferInLine.setExpectedQty(line.getTransferQty());
                    b2bTransferInLine.setUom(line.getUnitOfMeasure());
                    b2bTransferInLine.setManufacturerCode(line.getManufacturerCode());
                    b2bTransferInLine.setManufacturerFullName(line.getManufacturerShortName());
                    b2bTransferInLine.setExpectedDate(String.valueOf(dbIBOrder.getTransferOrderDate()));
                    b2bTransferInLine.setStoreID(dbIBOrder.getTargetBranchCode());
                    b2bTransferInLine.setOrigin(dbIBOrder.getSourceCompanyCode());
                    b2bTransferInLine.setBrand(line.getManufacturerShortName());
                    b2bTransferInLine.setTransferOrderNo(line.getTransferOrderNo());
                    b2bTransferInLine.setIsCompleted(line.getIsCompleted());

                    if (line.getTransferQty() != null) {
                        Double newDouble = new Double(line.getTransferQty());
                        Long tfrQty = newDouble.longValue();

                        b2bTransferInLine.setPackQty(tfrQty);
                    }
                    b2bTransferInLine.setMiddlewareId(line.getTransferInLineId());
                    b2bTransferInLine.setMiddlewareHeaderId(dbIBOrder.getTransferInHeaderId());
                    b2bTransferInLine.setMiddlewareTable("IB_NONWMS_TO_NONWMS");

                    b2bTransferInLineList.add(b2bTransferInLine);

                }

                b2bTransferIn.setB2bTransferInHeader(b2bTransferInHeader);
                b2bTransferIn.setB2bTransferLine(b2bTransferInLineList);
                b2bTransferInList.add(b2bTransferIn);

            }
            if (!b2bTransferInList.isEmpty()) {
                try {
                    B2bTransferIn[] b2bTransferv2 = b2BTransferInService.postB2BTransferIn(b2bTransferInList);
                    if (b2bTransferv2 != null) {
                        Arrays.stream(b2bTransferv2).forEach(b2b -> {
                            // Updating the Processed Status = 10 for successful orders
                            b2BTransferInService.updateProcessedInboundOrder(
                                    b2b.getB2bTransferInHeader().getMiddlewareId(),
                                    b2b.getB2bTransferInHeader().getCompanyCode(),
                                    b2b.getB2bTransferInHeader().getBranchCode(),
                                    b2b.getB2bTransferInHeader().getTransferOrderNumber(),
                                    10L // Success status
                            );
                            b2bTransferInList.remove(b2b);
                            log.info("IBTransferIn : " + b2b.getB2bTransferInHeader().getTransferOrderNumber() + " processed successfully.");
                        });
                    }
                } catch (Exception e) {
                    log.error("Error on inbound processing: ", e);

                    // Update the status to 100 for failed orders
                    for (B2bTransferIn b2b : b2bTransferInList) {
                        b2BTransferInService.updateProcessedInboundOrder(
                                b2b.getB2bTransferInHeader().getMiddlewareId(),
                                b2b.getB2bTransferInHeader().getCompanyCode(),
                                b2b.getB2bTransferInHeader().getBranchCode(),
                                b2b.getB2bTransferInHeader().getTransferOrderNumber(),
                                100L // Failure status
                        );
                        b2bTransferInList.remove(b2b);
                        log.error("IBTransferIn : " + b2b.getB2bTransferInHeader().getTransferOrderNumber() + " failed. Status updated to 100.");
                    }

                    // Optionally, rethrow the exception or handle it as needed
                    throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
                }
            }
            long endTime = System.currentTimeMillis();  // End time
            log.info("Inbound order processing ended at: " + new Date(endTime));
            log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");

            if (!sourceBranchExist && targetBranchExist) {
                log.info("IB NON WMS to WMS {}, {} ", sourceBranchExist, targetBranchExist);
                b2bTransferIn = new B2bTransferIn();
                b2bTransferInHeader = new B2bTransferInHeader();
                b2bTransferInHeader.setCompanyCode(dbIBOrder.getTargetCompanyCode());
                b2bTransferInHeader.setBranchCode(dbIBOrder.getTargetBranchCode());
                b2bTransferInHeader.setTransferOrderNumber(dbIBOrder.getTransferOrderNo());
                b2bTransferInHeader.setSourceBranchCode(dbIBOrder.getSourceBranchCode());
                b2bTransferInHeader.setSourceCompanyCode(dbIBOrder.getSourceCompanyCode());
                b2bTransferInHeader.setMiddlewareId(dbIBOrder.getTransferInHeaderId());
                b2bTransferInHeader.setMiddlewareTable("IB_B2B");
                b2bTransferInHeader.setTransferOrderDate(dbIBOrder.getTransferOrderDate());
                b2bTransferInHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
                b2bTransferInHeader.setIsCompleted(dbIBOrder.getIsCompleted());

                b2bTransferInLineList = new ArrayList<>();
                for (TransferInLine line : dbIBOrder.getTransferInLines()) {
                    com.almailem.ams.api.connector.model.wms.B2bTransferInLine b2bTransferInLine = new com.almailem.ams.api.connector.model.wms.B2bTransferInLine();

                    b2bTransferInLine.setLineReference(line.getLineNoOfEachItem());
                    b2bTransferInLine.setSku(line.getItemCode());
                    b2bTransferInLine.setSkuDescription(line.getItemDescription());
                    b2bTransferInLine.setManufacturerName(line.getManufacturerShortName());
                    b2bTransferInLine.setExpectedQty(line.getTransferQty());
                    b2bTransferInLine.setUom(line.getUnitOfMeasure());
                    b2bTransferInLine.setManufacturerCode(line.getManufacturerCode());
                    b2bTransferInLine.setManufacturerFullName(line.getManufacturerShortName());
                    b2bTransferInLine.setExpectedDate(String.valueOf(dbIBOrder.getTransferOrderDate()));
                    b2bTransferInLine.setStoreID(dbIBOrder.getTargetBranchCode());
                    b2bTransferInLine.setOrigin(dbIBOrder.getSourceCompanyCode());
                    b2bTransferInLine.setBrand(line.getManufacturerShortName());
                    b2bTransferInLine.setTransferOrderNo(line.getTransferOrderNo());
                    b2bTransferInLine.setIsCompleted(line.getIsCompleted());

                    if (line.getTransferQty() != null) {
                        Double newDouble = new Double(line.getTransferQty());
                        Long tfrQty = newDouble.longValue();

                        b2bTransferInLine.setPackQty(tfrQty);
                    }
                    b2bTransferInLine.setMiddlewareId(line.getTransferInLineId());
                    b2bTransferInLine.setMiddlewareHeaderId(dbIBOrder.getTransferInHeaderId());
                    b2bTransferInLine.setMiddlewareTable("IB_B2B");
                    b2bTransferInLineList.add(b2bTransferInLine);
                }
                b2bTransferIn.setB2bTransferInHeader(b2bTransferInHeader);
                b2bTransferIn.setB2bTransferLine(b2bTransferInLineList);
                b2bTransferInList.add(b2bTransferIn);
            }
            if (!b2bTransferInList.isEmpty()) {
                try {
                    B2bTransferIn[] b2bTransferv2 = b2BTransferInService.postB2BTransferIn(b2bTransferInList);
                    if (b2bTransferv2 != null) {
                        Arrays.stream(b2bTransferv2).forEach(b2b -> {
                            // Updating the Processed Status = 10 for successful orders
                            b2bTransferInList.remove(b2b);
                            b2BTransferInService.updateProcessedInboundOrder(
                                    b2b.getB2bTransferInHeader().getMiddlewareId(),
                                    b2b.getB2bTransferInHeader().getCompanyCode(),
                                    b2b.getB2bTransferInHeader().getBranchCode(),
                                    b2b.getB2bTransferInHeader().getTransferOrderNumber(),
                                    10L // Success status
                            );
                            log.info("IBTransferIn : " + b2b.getB2bTransferInHeader().getTransferOrderNumber() + " processed successfully.");
                        });
                    }
                } catch (Exception e) {
                    log.error("Error on inbound processing: ", e);

                    // Update the status to 100 for failed orders
                    for (B2bTransferIn b2b : b2bTransferInList) {
                        b2BTransferInService.updateProcessedInboundOrder(
                                b2b.getB2bTransferInHeader().getMiddlewareId(),
                                b2b.getB2bTransferInHeader().getCompanyCode(),
                                b2b.getB2bTransferInHeader().getBranchCode(),
                                b2b.getB2bTransferInHeader().getTransferOrderNumber(),
                                100L // Failure status
                        );
                        b2bTransferInList.remove(b2b);
                        log.error("IBTransferIn : " + b2b.getB2bTransferInHeader().getTransferOrderNumber() + " failed. Status updated to 100.");
                    }

                    // Optionally, rethrow the exception or handle it as needed
                    throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
                }
            }
            endTime = System.currentTimeMillis();
            log.info("Inbound order processing ended at: " + new Date(endTime));
            log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");


            List<InterWarehouseTransferIn> interWarehouseTransferInList = new ArrayList<>();
            if (sourceBranchExist && targetBranchExist) {
                log.info("IB WMS to WMS: {}, {} ", sourceBranchExist, targetBranchExist);
                InterWarehouseTransferIn interWarehouseTransferIn = new InterWarehouseTransferIn();
                InterWarehouseTransferInHeader interWarehouseTransferInHeader = new InterWarehouseTransferInHeader();

                interWarehouseTransferInHeader.setToCompanyCode(dbIBOrder.getTargetCompanyCode());
                interWarehouseTransferInHeader.setToBranchCode(dbIBOrder.getTargetBranchCode());
                interWarehouseTransferInHeader.setSourceCompanyCode(dbIBOrder.getSourceCompanyCode());
                interWarehouseTransferInHeader.setSourceBranchCode(dbIBOrder.getSourceBranchCode());
                interWarehouseTransferInHeader.setIsCompleted(dbIBOrder.getIsCompleted());
                interWarehouseTransferInHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
                interWarehouseTransferInHeader.setTransferOrderNumber(dbIBOrder.getTransferOrderNo());
                interWarehouseTransferInHeader.setTransferOrderDate(dbIBOrder.getTransferOrderDate());
                interWarehouseTransferInHeader.setMiddlewareId(dbIBOrder.getTransferInHeaderId());
                interWarehouseTransferInHeader.setMiddlewareTable("IB_IWT");

                List<InterWarehouseTransferInLine> interWarehouseTransferInLineList = new ArrayList<>();
                for (TransferInLine line : dbIBOrder.getTransferInLines()) {
                    com.almailem.ams.api.connector.model.wms.InterWarehouseTransferInLine interWarehouseTransferInLine = new com.almailem.ams.api.connector.model.wms.InterWarehouseTransferInLine();

                    interWarehouseTransferInLine.setLineReference(line.getLineNoOfEachItem());
                    interWarehouseTransferInLine.setSku(line.getItemCode());
                    interWarehouseTransferInLine.setSkuDescription(line.getItemDescription());
                    interWarehouseTransferInLine.setManufacturerName(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setExpectedQty(line.getTransferQty());
                    interWarehouseTransferInLine.setPackQty(line.getTransferQty());
                    interWarehouseTransferInLine.setUom(line.getUnitOfMeasure());
                    interWarehouseTransferInLine.setManufacturerCode(line.getManufacturerCode());
                    interWarehouseTransferInLine.setManufacturerFullName(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setExpectedDate(String.valueOf(dbIBOrder.getTransferOrderDate()));
                    interWarehouseTransferInLine.setFromBranchCode(dbIBOrder.getSourceBranchCode());
                    interWarehouseTransferInLine.setFromCompanyCode(dbIBOrder.getSourceCompanyCode());
                    interWarehouseTransferInLine.setBrand(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setTransferOrderNo(dbIBOrder.getTransferOrderNo());
                    interWarehouseTransferInLine.setIsCompleted(line.getIsCompleted());
                    interWarehouseTransferInLine.setMiddlewareId(line.getTransferInLineId());
                    interWarehouseTransferInLine.setMiddlewareHeaderId(dbIBOrder.getTransferInHeaderId());
                    interWarehouseTransferInLine.setMiddlewareTable("IB_IWT");

                    interWarehouseTransferInLineList.add(interWarehouseTransferInLine);
                }

                interWarehouseTransferIn.setInterWarehouseTransferInHeader(interWarehouseTransferInHeader);
                interWarehouseTransferIn.setInterWarehouseTransferInLine(interWarehouseTransferInLineList);
                interWarehouseTransferInList.add(interWarehouseTransferIn);
            }

            if (!interWarehouseTransferInList.isEmpty()) {
                try {
                    InterWarehouseTransferIn[] interWarehouseTransferIns = interWarehouseTransferInService.postIWTTransferIn(interWarehouseTransferInList);
                    if (interWarehouseTransferIns != null) {
                        Arrays.stream(interWarehouseTransferIns).forEach(interTransferIn -> {
                            // Updating the Processed Status = 10 for successful orders
                            interWarehouseTransferInService.updateProcessedInboundOrder(
                                    interTransferIn.getInterWarehouseTransferInHeader().getMiddlewareId(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getToCompanyCode(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getToBranchCode(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber(),
                                    10L // Success status
                            );
                            interWarehouseTransferInList.remove(interTransferIn);
                            log.info("Inter warehouse TransferIn : " + interTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber() + " processed successfully.");
                        });
                    }
                } catch (Exception e) {
                    log.error("Error on inbound processing: ", e);

                    // Update the status to 100 for failed orders
                    for (InterWarehouseTransferIn warehouseTransferIn : interWarehouseTransferInList) {
                        interWarehouseTransferInService.updateProcessedInboundOrder(
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getMiddlewareId(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getToCompanyCode(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getToBranchCode(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber(),
                                100L // Failure status
                        );
                        interWarehouseTransferInList.remove(warehouseTransferIn);
                        log.error("Inter warehouse TransferIn : " + warehouseTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber() + " failed. Status updated to 100.");
                    }

                    // Optionally, rethrow the exception or handle it as needed
                    throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
                }
            }
            endTime = System.currentTimeMillis();
            log.info("Inbound order processing ended at: " + new Date(endTime));
            log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");

            if (sourceBranchExist && !targetBranchExist) {
                log.info("IB WMS to NON WMS {}, {} ", sourceBranchExist, targetBranchExist);
                InterWarehouseTransferIn interWarehouseTransferIn = new InterWarehouseTransferIn();
                InterWarehouseTransferInHeader interWarehouseTransferInHeader = new InterWarehouseTransferInHeader();

                interWarehouseTransferInHeader.setToCompanyCode(dbIBOrder.getTargetCompanyCode());
                interWarehouseTransferInHeader.setToBranchCode(dbIBOrder.getTargetBranchCode());
                interWarehouseTransferInHeader.setSourceCompanyCode(dbIBOrder.getSourceCompanyCode());
                interWarehouseTransferInHeader.setSourceBranchCode(dbIBOrder.getSourceBranchCode());
                interWarehouseTransferInHeader.setIsCompleted(dbIBOrder.getIsCompleted());
                interWarehouseTransferInHeader.setUpdatedOn(dbIBOrder.getUpdatedOn());
                interWarehouseTransferInHeader.setTransferOrderNumber(dbIBOrder.getTransferOrderNo());
                interWarehouseTransferInHeader.setTransferOrderDate(dbIBOrder.getTransferOrderDate());
                interWarehouseTransferInHeader.setMiddlewareId(dbIBOrder.getTransferInHeaderId());
                interWarehouseTransferInHeader.setMiddlewareTable("IB_WMS_TO_NONWMS");

                List<InterWarehouseTransferInLine> interWarehouseTransferInLineList = new ArrayList<>();
                for (TransferInLine line : dbIBOrder.getTransferInLines()) {
                    com.almailem.ams.api.connector.model.wms.InterWarehouseTransferInLine interWarehouseTransferInLine = new com.almailem.ams.api.connector.model.wms.InterWarehouseTransferInLine();

                    interWarehouseTransferInLine.setLineReference(line.getLineNoOfEachItem());
                    interWarehouseTransferInLine.setSku(line.getItemCode());
                    interWarehouseTransferInLine.setSkuDescription(line.getItemDescription());
                    interWarehouseTransferInLine.setManufacturerName(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setExpectedQty(line.getTransferQty());
                    interWarehouseTransferInLine.setPackQty(line.getTransferQty());
                    interWarehouseTransferInLine.setUom(line.getUnitOfMeasure());
                    interWarehouseTransferInLine.setManufacturerCode(line.getManufacturerCode());
                    interWarehouseTransferInLine.setManufacturerFullName(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setExpectedDate(String.valueOf(dbIBOrder.getTransferOrderDate()));
                    interWarehouseTransferInLine.setFromBranchCode(dbIBOrder.getSourceBranchCode());
                    interWarehouseTransferInLine.setFromCompanyCode(dbIBOrder.getSourceCompanyCode());
                    interWarehouseTransferInLine.setBrand(line.getManufacturerShortName());
                    interWarehouseTransferInLine.setTransferOrderNo(dbIBOrder.getTransferOrderNo());
                    interWarehouseTransferInLine.setIsCompleted(line.getIsCompleted());
                    interWarehouseTransferInLine.setMiddlewareId(line.getTransferInLineId());
                    interWarehouseTransferInLine.setMiddlewareHeaderId(dbIBOrder.getTransferInHeaderId());
                    interWarehouseTransferInLine.setMiddlewareTable("IB_IWT");
                    interWarehouseTransferInLineList.add(interWarehouseTransferInLine);
                }

                interWarehouseTransferIn.setInterWarehouseTransferInHeader(interWarehouseTransferInHeader);
                interWarehouseTransferIn.setInterWarehouseTransferInLine(interWarehouseTransferInLineList);
                interWarehouseTransferInList.add(interWarehouseTransferIn);
            }

            if (!interWarehouseTransferInList.isEmpty()) {
                try {
                    InterWarehouseTransferIn[] interWarehouseTransferIns = interWarehouseTransferInService.postIWTTransferIn(interWarehouseTransferInList);
                    if (interWarehouseTransferIns != null) {
                        Arrays.stream(interWarehouseTransferIns).forEach(interTransferIn -> {
                            // Updating the Processed Status = 10 for successful orders
                            interWarehouseTransferInService.updateProcessedInboundOrder(
                                    interTransferIn.getInterWarehouseTransferInHeader().getMiddlewareId(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getToCompanyCode(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getToBranchCode(),
                                    interTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber(),
                                    10L // Success status
                            );
                            interWarehouseTransferInList.remove(interTransferIn);
                            log.info("Inter warehouse TransferIn : " + interTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber() + " processed successfully.");
                        });
                    }
                } catch (Exception e) {
                    log.error("Error on inbound processing: ", e);

                    // Update the status to 100 for failed orders
                    for (InterWarehouseTransferIn warehouseTransferIn : interWarehouseTransferInList) {
                        interWarehouseTransferInService.updateProcessedInboundOrder(
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getMiddlewareId(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getToCompanyCode(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getToBranchCode(),
                                warehouseTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber(),
                                100L // Failure status
                        );
                        interWarehouseTransferInList.remove(warehouseTransferIn);
                        log.error("Inter warehouse TransferIn : " + warehouseTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber() + " failed. Status updated to 100.");
                    }

                    // Optionally, rethrow the exception or handle it as needed
                    throw new RuntimeException("Inbound processing failed: " + e.getMessage(), e);
                }
            }
            endTime = System.currentTimeMillis();
            log.info("Inbound order processing ended at: " + new Date(endTime));
            log.info("Total time taken for inbound order processing: " + (endTime - startTime) + " ms");
        }
        return null;
    }

    //===========================================Outbound==============================================================
    //===========================================Purchase_Return=======================================================
    public WarehouseApiResponse processOutboundOrderRPO() throws IllegalAccessException, InvocationTargetException {

        List<PurchaseReturnHeader> purchaseReturns = purchaseReturnHeaderRepository.findByPurchaseReturn(0L,branchCode);
        log.info("Order Received On purchaseReturns: " + purchaseReturns);

        if(purchaseReturns.isEmpty()) {
            log.info("No records found in purchase return headers");
            return null;
        }

        List<ReturnPO> returnPOS = new ArrayList<>();
        // Order_process_starting_Set_value_process_status_id=1
        purchaseReturns.stream().forEach(purchaseReturn -> {
            returnPOService.updateProcessedOutboundOrder(purchaseReturn.getPurchaseReturnHeaderId(),
                    purchaseReturn.getCompanyCode(), purchaseReturn.getBranchCode(),
                    purchaseReturn.getReturnOrderNo(), 1L);
            log.info("Purchase return Order Process Staring Set value is 1");
        });

        ReturnPO returnPO = new ReturnPO();
        for (PurchaseReturnHeader dbObOrder : purchaseReturns) {
            ReturnPOHeader returnPOHeader = new ReturnPOHeader();
            returnPOHeader.setCompanyCode(dbObOrder.getCompanyCode());
            returnPOHeader.setBranchCode(dbObOrder.getBranchCode());
            returnPOHeader.setPoNumber(dbObOrder.getReturnOrderNo());
            returnPOHeader.setStoreID(dbObOrder.getBranchCode());
            returnPOHeader.setRequiredDeliveryDate(String.valueOf(dbObOrder.getReturnOrderDate()));
            returnPOHeader.setIsCancelled(dbObOrder.getIsCancelled());
            returnPOHeader.setIsCompleted(dbObOrder.getIsCompleted());
            returnPOHeader.setUpdatedOn(dbObOrder.getUpdatedOn());
            returnPOHeader.setMiddlewareId(dbObOrder.getPurchaseReturnHeaderId());
            returnPOHeader.setMiddlewareTable("OB_PURCHASE_RETURN_HEADER");

            List<ReturnPOLine> returnPOLineList = new ArrayList<>();
            for (PurchaseReturnLine line : dbObOrder.getPurchaseReturnLines()) {
                ReturnPOLine returnPOLine = new ReturnPOLine();
                returnPOLine.setReturnOrderNo(line.getReturnOrderNo());
                returnPOLine.setLineReference(line.getLineNoOfEachItemCode());
                returnPOLine.setSku(line.getItemCode());
                returnPOLine.setSkuDescription(line.getItemDescription());
                returnPOLine.setReturnQty(line.getReturnOrderQty());
                returnPOLine.setExpectedQty(line.getReturnOrderQty());
                returnPOLine.setUom(line.getUnitOfMeasure());
                returnPOLine.setManufacturerCode(line.getManufacturerCode());
                returnPOLine.setManufacturerName(line.getManufacturerShortName());
                returnPOLine.setBrand(line.getManufacturerFullName());
                returnPOLine.setSupplierInvoiceNo(line.getSupplierInvoiceNo());
                returnPOLine.setIsCancelled(line.getIsCancelled());
                returnPOLine.setIsCompleted(line.getIsCompleted());
                returnPOLine.setMiddlewareId(line.getPurchaseReturnLineId());
                returnPOLine.setMiddlewareHeaderId(dbObOrder.getPurchaseReturnHeaderId());
                returnPOLine.setMiddlewareTable("OB_PURCHASE_RETURN_LINE");
                returnPOLineList.add(returnPOLine);
            }
            returnPO.setReturnPOHeader(returnPOHeader);
            returnPO.setReturnPOLine(returnPOLineList);
            returnPOS.add(returnPO);
        }

        if (!returnPOS.isEmpty()) {
            try {
                ReturnPO[] outboundHeader = returnPOService.postReturnPOV2(returnPOS);
                if (outboundHeader.length > 0) {
                    returnPOS.stream().forEach(returnPO1 -> {
                        // Updating the Processed Status = 10
                        returnPOService.updateProcessedOutboundOrder(returnPO1.getReturnPOHeader().getMiddlewareId(),
                                returnPO1.getReturnPOHeader().getCompanyCode(), returnPO1.getReturnPOHeader().getBranchCode(),
                                returnPO1.getReturnPOHeader().getPoNumber(), 10L);
                        log.info("Purchase Retrun Order Created Successfully {} - ", returnPO1.getReturnPOHeader().getPoNumber());
                    });
                }
            } catch (Exception e) {
                returnPOS.stream().forEach(returnPO1 -> {
                    returnPOService.updateProcessedOutboundOrder(returnPO1.getReturnPOHeader().getMiddlewareId(),
                            returnPO1.getReturnPOHeader().getCompanyCode(), returnPO1.getReturnPOHeader().getBranchCode(),
                            returnPO1.getReturnPOHeader().getPoNumber(), 100L);
                    log.info("Purchase Retrun Order Failed {} - ", returnPO1.getReturnPOHeader().getPoNumber());
                });
            }
        }
        return null;
    }

    //------------------------------------InterWarehouse & ShipmentOrder---------------------------------------------
    public WarehouseApiResponse processOutboundOrderIWT() throws IllegalAccessException, InvocationTargetException {

        List<TransferOutHeader> transferOuts = transferOutHeaderRepository.findByTransferOut(0L,branchCode);
        log.info("TransferOut / Shipment Order Found: " + transferOuts);

        if(transferOuts.isEmpty()) {
            log.info("No records found in transfer out");
            return null;
        }
        List<ShipmentOrder> shipmentOrders = new ArrayList<>();
        // Order_process_starting_Set_value_process_status_id=1
        transferOuts.stream().forEach(transferOut -> {
            interWarehouseTransferOutService.updateProcessedOutboundOrder(transferOut.getTransferOutHeaderId(),
                    transferOut.getSourceCompanyCode(), transferOut.getSourceBranchCode(),
                    transferOut.getTransferOrderNumber(), 1L);
            log.info("PickList Order Process Staring Set value is 1");
        });

        for (TransferOutHeader dbObOrder : transferOuts) {
            String[] branchcode = new String[]{"115", "125", "212", "222", "219"};
            boolean sourceBranchExist = Arrays.stream(branchcode).anyMatch(n -> n.equalsIgnoreCase(dbObOrder.getSourceBranchCode()));
            boolean targetBranchExist = Arrays.stream(branchcode).anyMatch(n -> n.equalsIgnoreCase(dbObOrder.getTargetBranchCode()));

            log.info("sourceBranchExist,targetBranchExist: " + sourceBranchExist, targetBranchExist);

            ShipmentOrder shipmentOrder = null;
            List<SOLine> soLineList = null;
            SOHeader soHeader = null;

            if (sourceBranchExist && !targetBranchExist) {
                log.info("OB WMS to NON WMS: " + sourceBranchExist, targetBranchExist);
                log.info("Shipment Order: " + dbObOrder);
                shipmentOrder = new ShipmentOrder();
                soHeader = new SOHeader();

                soHeader.setCompanyCode(dbObOrder.getSourceCompanyCode());
                soHeader.setBranchCode(dbObOrder.getSourceBranchCode());
                soHeader.setTransferOrderNumber(dbObOrder.getTransferOrderNumber());
                soHeader.setRequiredDeliveryDate(String.valueOf(dbObOrder.getTransferOrderDate()));
                soHeader.setStoreID(dbObOrder.getSourceBranchCode());
                soHeader.setTargetCompanyCode(dbObOrder.getTargetCompanyCode());
                soHeader.setTargetBranchCode(dbObOrder.getTargetBranchCode());
                soHeader.setOrderType(dbObOrder.getFulfilmentMethod());
                soHeader.setMiddlewareId(dbObOrder.getTransferOutHeaderId());
                soHeader.setMiddlewareTable("OB_SHIPMENT_ORDER_HEADER");

                soLineList = new ArrayList<>();
                for (TransferOutLine line : dbObOrder.getTransferOutLines()) {
                    log.info("Shipment Order Lines: " + dbObOrder.getTransferOutLines());
                    SOLine soLine = new SOLine();

                    soLine.setTransferOrderNumber(line.getTransferOrderNumber());
                    soLine.setLineReference(line.getLineNumberOfEachItem());
                    soLine.setSku(line.getItemCode());
                    soLine.setSkuDescription(line.getItemDescription());
                    soLine.setOrderedQty(line.getTransferOrderQty());
                    soLine.setExpectedQty(line.getTransferOrderQty());
                    soLine.setUom(line.getUnitOfMeasure());
                    soLine.setManufacturerCode(line.getManufacturerCode());
                    soLine.setManufacturerName(line.getManufacturerShortName());
                    soLine.setFromCompanyCode(dbObOrder.getSourceCompanyCode());
                    soLine.setOrderType(dbObOrder.getFulfilmentMethod());
                    soLine.setManufacturerFullName(line.getManufacturerFullName());
                    soLine.setMiddlewareId(line.getTransferOutLineId());
                    soLine.setMiddlewareHeaderId(dbObOrder.getTransferOutHeaderId());
                    soLine.setMiddlewareTable("OB_SHIPMENT_ORDER_LINE");
                    soLineList.add(soLine);
                }
                shipmentOrder.setSoHeader(soHeader);
                shipmentOrder.setSoLine(soLineList);
                shipmentOrders.add(shipmentOrder);
            }

            if (!shipmentOrders.isEmpty()) {
                try {
                    ShipmentOrder[] outboundHeader = shipmentOrderService.postShipmentOrder(shipmentOrders);
                    if (outboundHeader.length > 0) {
                        shipmentOrders.stream().forEach(shipment -> {
                            // Updating the Processed Status = 10
                            shipmentOrderService.updateProcessedOutboundOrder(shipment.getSoHeader().getMiddlewareId(),
                                    shipment.getSoHeader().getCompanyCode(), shipment.getSoHeader().getBranchCode(),
                                    shipment.getSoHeader().getTransferOrderNumber(), 10L);
                            log.info("OB Shipment Order Created Successfully {} - ", shipment.getSoHeader().getTransferOrderNumber());
                        });
                    }
                } catch (Exception e) {
                    shipmentOrders.stream().forEach(shipment -> {
                        shipmentOrderService.updateProcessedOutboundOrder(shipment.getSoHeader().getMiddlewareId(),
                                shipment.getSoHeader().getCompanyCode(), shipment.getSoHeader().getBranchCode(),
                                shipment.getSoHeader().getTransferOrderNumber(), 100L);
                        log.info("OB Shipment Order Failed {} - ", shipment.getSoHeader().getTransferOrderNumber());
                    });
                }
            }

            List<InterWarehouseTransferOut> interWarehouseTransferOuts = new ArrayList<>();
            if (!sourceBranchExist && targetBranchExist) {
                log.info("OB NON WMS to WMS: " + sourceBranchExist, targetBranchExist);
                log.info("TransferOut Order: " + dbObOrder);
                InterWarehouseTransferOut interWarehouseTransferOut = new InterWarehouseTransferOut();
                InterWarehouseTransferOutHeader iWhtOutHeader = new InterWarehouseTransferOutHeader();

                iWhtOutHeader.setFromCompanyCode(dbObOrder.getSourceCompanyCode());
                iWhtOutHeader.setToCompanyCode(dbObOrder.getTargetCompanyCode());
                iWhtOutHeader.setTransferOrderNumber(dbObOrder.getTransferOrderNumber());
                iWhtOutHeader.setFromBranchCode(dbObOrder.getSourceBranchCode());
                iWhtOutHeader.setToBranchCode(dbObOrder.getTargetBranchCode());
                iWhtOutHeader.setRequiredDeliveryDate(String.valueOf(dbObOrder.getTransferOrderDate()));
                iWhtOutHeader.setOrderType(dbObOrder.getFulfilmentMethod());
                iWhtOutHeader.setMiddlewareId(dbObOrder.getTransferOutHeaderId());
                iWhtOutHeader.setMiddlewareTable("OB_IWHTRANSFER_OUT_HEADER");

                List<InterWarehouseTransferOutLine> interWarehouseTransferOutLineList = new ArrayList<>();
                for (TransferOutLine line : dbObOrder.getTransferOutLines()) {
                    InterWarehouseTransferOutLine iWhtOutLine = new InterWarehouseTransferOutLine();

                    iWhtOutLine.setTransferOrderNumber(line.getTransferOrderNumber());
                    iWhtOutLine.setLineReference(line.getLineNumberOfEachItem());
                    iWhtOutLine.setSku(line.getItemCode());
                    iWhtOutLine.setSkuDescription(line.getItemDescription());
                    iWhtOutLine.setOrderedQty(line.getTransferOrderQty());
                    iWhtOutLine.setUom(line.getUnitOfMeasure());
                    iWhtOutLine.setManufacturerCode(line.getManufacturerCode());
                    iWhtOutLine.setManufacturerName(line.getManufacturerShortName());
                    iWhtOutLine.setOrderType(dbObOrder.getFulfilmentMethod());
                    iWhtOutLine.setManufacturerFullName(line.getManufacturerFullName());
                    iWhtOutLine.setMiddlewareId(line.getTransferOutLineId());
                    iWhtOutLine.setMiddlewareHeaderId(line.getTransferOutHeaderId());
                    iWhtOutLine.setMiddlewareTable("OB_IWHTRANSFER_OUT_LINE");
                    interWarehouseTransferOutLineList.add(iWhtOutLine);
                }
                interWarehouseTransferOut.setInterWarehouseTransferOutHeader(iWhtOutHeader);
                interWarehouseTransferOut.setInterWarehouseTransferOutLine(interWarehouseTransferOutLineList);
                interWarehouseTransferOuts.add(interWarehouseTransferOut);
            }
//            if (!interWarehouseTransferOuts.isEmpty()) {
//                try {
//                    InterWarehouseTransferOut[] outboundHeader = interWarehouseTransferOutService.postIWhTransferOutV7(interWarehouseTransferOuts);
//                    if (outboundHeader.length > 0) {
//                        interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
//                            // Updating the Processed Status = 10
//                            interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
//                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
//                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 10L);
//                            log.info("OB Inter warehouse transfer Order Created Successfully {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
//                        });
//                    }
//                } catch (Exception e) {
//                    interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
//                        interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
//                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
//                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 100L);
//                        log.info("OB Inter warehouse transfer Order Failed {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
//                    });
//                }
//            }

            if (sourceBranchExist && targetBranchExist) {
                log.info("OB WMS to WMS: " + sourceBranchExist, targetBranchExist);
                log.info("TransferOut Order: " + dbObOrder);
                InterWarehouseTransferOut interWarehouseTransferOut = new InterWarehouseTransferOut();
                InterWarehouseTransferOutHeader iWhtOutHeader = new InterWarehouseTransferOutHeader();
                iWhtOutHeader.setFromCompanyCode(dbObOrder.getSourceCompanyCode());
                iWhtOutHeader.setToCompanyCode(dbObOrder.getTargetCompanyCode());
                iWhtOutHeader.setTransferOrderNumber(dbObOrder.getTransferOrderNumber());
                iWhtOutHeader.setFromBranchCode(dbObOrder.getSourceBranchCode());
                iWhtOutHeader.setToBranchCode(dbObOrder.getTargetBranchCode());
                iWhtOutHeader.setRequiredDeliveryDate(String.valueOf(dbObOrder.getTransferOrderDate()));
                iWhtOutHeader.setOrderType(dbObOrder.getFulfilmentMethod());
                iWhtOutHeader.setMiddlewareId(dbObOrder.getTransferOutHeaderId());
                iWhtOutHeader.setMiddlewareTable("OB_WMS_TO_WMS");

                List<InterWarehouseTransferOutLine> interWarehouseTransferOutLineList = new ArrayList<>();
                for (TransferOutLine line : dbObOrder.getTransferOutLines()) {
                    InterWarehouseTransferOutLine iWhtOutLine = new InterWarehouseTransferOutLine();
                    iWhtOutLine.setTransferOrderNumber(line.getTransferOrderNumber());
                    iWhtOutLine.setLineReference(line.getLineNumberOfEachItem());
                    iWhtOutLine.setSku(line.getItemCode());
                    iWhtOutLine.setSkuDescription(line.getItemDescription());
                    iWhtOutLine.setOrderedQty(line.getTransferOrderQty());
                    iWhtOutLine.setUom(line.getUnitOfMeasure());
                    iWhtOutLine.setManufacturerCode(line.getManufacturerCode());
                    iWhtOutLine.setManufacturerName(line.getManufacturerShortName());
                    iWhtOutLine.setOrderType(dbObOrder.getFulfilmentMethod());
                    iWhtOutLine.setManufacturerFullName(line.getManufacturerFullName());
                    iWhtOutLine.setMiddlewareId(line.getTransferOutLineId());
                    iWhtOutLine.setMiddlewareHeaderId(line.getTransferOutHeaderId());
                    iWhtOutLine.setMiddlewareTable("OB_IWHTRANSFER_OUT_LINE");
                    interWarehouseTransferOutLineList.add(iWhtOutLine);
                }
                interWarehouseTransferOut.setInterWarehouseTransferOutHeader(iWhtOutHeader);
                interWarehouseTransferOut.setInterWarehouseTransferOutLine(interWarehouseTransferOutLineList);
                interWarehouseTransferOuts.add(interWarehouseTransferOut);
            }

            if (!interWarehouseTransferOuts.isEmpty()) {
                try {
                    InterWarehouseTransferOut[] outboundHeader = interWarehouseTransferOutService.postIWhTransferOutV7(interWarehouseTransferOuts);
                    log.info("OutboundHeader -------> {}", outboundHeader);
                    log.info("OutboundHeader length -------> {}", outboundHeader.length);
                    if (outboundHeader.length > 0) {
                        interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
                            // Updating the Processed Status = 10
                            interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 10L);
                            log.info("OB Inter warehouse transfer Order Created Successfully {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
                        });
                    }
                } catch (Exception e) {
                    interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
                        interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 100L);
                        log.info("OB Inter warehouse transfer Order Failed {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
                    });
                }
            }

            if (!sourceBranchExist && !targetBranchExist) {
                log.info("OB NON WMS to NON WMS: " + sourceBranchExist, targetBranchExist);
                log.info("TransferOut Order: " + dbObOrder);

                InterWarehouseTransferOut interWarehouseTransferOut = new InterWarehouseTransferOut();
                InterWarehouseTransferOutHeader iWhtOutHeader = new InterWarehouseTransferOutHeader();
                iWhtOutHeader.setFromCompanyCode(dbObOrder.getSourceCompanyCode());
                iWhtOutHeader.setToCompanyCode(dbObOrder.getTargetCompanyCode());
                iWhtOutHeader.setTransferOrderNumber(dbObOrder.getTransferOrderNumber());
                iWhtOutHeader.setFromBranchCode(dbObOrder.getSourceBranchCode());
                iWhtOutHeader.setToBranchCode(dbObOrder.getTargetBranchCode());
                iWhtOutHeader.setRequiredDeliveryDate(String.valueOf(dbObOrder.getTransferOrderDate()));
                iWhtOutHeader.setOrderType(dbObOrder.getFulfilmentMethod());
                iWhtOutHeader.setMiddlewareId(dbObOrder.getTransferOutHeaderId());
                iWhtOutHeader.setMiddlewareTable("OB_NONWMS_TO_NONWMS");

                List<InterWarehouseTransferOutLine> interWarehouseTransferOutLineList = new ArrayList<>();
                for (TransferOutLine line : dbObOrder.getTransferOutLines()) {
                    InterWarehouseTransferOutLine iWhtOutLine = new InterWarehouseTransferOutLine();

                    iWhtOutLine.setTransferOrderNumber(line.getTransferOrderNumber());
                    iWhtOutLine.setLineReference(line.getLineNumberOfEachItem());
                    iWhtOutLine.setSku(line.getItemCode());
                    iWhtOutLine.setSkuDescription(line.getItemDescription());
                    iWhtOutLine.setOrderedQty(line.getTransferOrderQty());
                    iWhtOutLine.setUom(line.getUnitOfMeasure());
                    iWhtOutLine.setManufacturerCode(line.getManufacturerCode());
                    iWhtOutLine.setManufacturerName(line.getManufacturerShortName());
                    iWhtOutLine.setOrderType(dbObOrder.getFulfilmentMethod());
                    iWhtOutLine.setManufacturerFullName(line.getManufacturerFullName());
                    iWhtOutLine.setMiddlewareId(line.getTransferOutLineId());
                    iWhtOutLine.setMiddlewareHeaderId(line.getTransferOutHeaderId());
                    iWhtOutLine.setMiddlewareTable("OB_IWHTRANSFER_OUT_LINE");
                    interWarehouseTransferOutLineList.add(iWhtOutLine);
                }
                interWarehouseTransferOut.setInterWarehouseTransferOutHeader(iWhtOutHeader);
                interWarehouseTransferOut.setInterWarehouseTransferOutLine(interWarehouseTransferOutLineList);
                interWarehouseTransferOuts.add(interWarehouseTransferOut);
            }
//            if (!interWarehouseTransferOuts.isEmpty()) {
//                try {
//                    InterWarehouseTransferOut[] outboundHeader = interWarehouseTransferOutService.postIWhTransferOutV2(interWarehouseTransferOuts);
//                    if (outboundHeader.length > 0) {
//                        interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
//                            // Updating the Processed Status = 10
//                            interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
//                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
//                                    interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 10L);
//                            log.info("OB Inter warehouse transfer Order Created Successfully {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
//                        });
//                    }
//                } catch (Exception e) {
//                    interWarehouseTransferOuts.stream().forEach(interWarehouseTransferOut -> {
//                        interWarehouseTransferOutService.updateProcessedOutboundOrder(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId(),
//                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode(), interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode(),
//                                interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber(), 100L);
//                        log.info("OB Inter warehouse transfer Order Failed {} - ", interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
//                    });
//                }
//            }

        }

        return null;
    }

    /**
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public WarehouseApiResponse processOutboundOrderPL() throws IllegalAccessException, InvocationTargetException {

        List<PickListHeader> pickListHeaders = pickListHeaderRepository.findByPickList(0L,branchCode);
        log.info("Order Received On pickListHeaders: " + pickListHeaders);

        if(pickListHeaders.isEmpty()) {
            log.info("No records found in pickListHeaders");
            return null;
        }

        List<SalesOrder> salesOrderList = new ArrayList<>();
        // Order_process_starting_Set_value_process_status_id=1
        pickListHeaders.stream().forEach(salesOrder -> {
            salesOrderService.updateProcessedInboundOrder(salesOrder.getPickListHeaderId(),
                    salesOrder.getCompanyCode(), salesOrder.getBranchCode(),
                    salesOrder.getPickListNo(), 1L);
            log.info("PickList Order Process Staring Set value is 1");
        });

        SalesOrder salesOrder = new SalesOrder();
        for (PickListHeader dbOBOrder : pickListHeaders) {
            SalesOrderHeader salesOrderHeader = new SalesOrderHeader();
            salesOrderHeader.setSalesOrderNumber(dbOBOrder.getSalesOrderNo());
            salesOrderHeader.setCompanyCode(dbOBOrder.getCompanyCode());
            salesOrderHeader.setBranchCode(dbOBOrder.getBranchCode());
            salesOrderHeader.setPickListNumber(dbOBOrder.getPickListNo());
            salesOrderHeader.setRequiredDeliveryDate(String.valueOf(dbOBOrder.getPickListdate()));
            salesOrderHeader.setStoreID(dbOBOrder.getBranchCode());
            salesOrderHeader.setStoreName(dbOBOrder.getBranchCode());
            salesOrderHeader.setTokenNumber(dbOBOrder.getTokenNumber());
            salesOrderHeader.setStatus("ACTIVE");
            salesOrderHeader.setMiddlewareId(dbOBOrder.getPickListHeaderId());
            salesOrderHeader.setMiddlewareTable("OB_SalesOrder");

            List<SalesOrderLine> salesOrderLines = new ArrayList<>();
            for (PickListLine line : dbOBOrder.getPickListLines()) {
                SalesOrderLine salesOrderLine = new SalesOrderLine();

                salesOrderLine.setLineReference(line.getLineNumberOfEachItem());
                salesOrderLine.setSku(line.getItemCode());
                salesOrderLine.setSkuDescription(line.getItemDescription());
                salesOrderLine.setManufacturerCode(line.getManufacturerCode());
                salesOrderLine.setManufacturerName(line.getManufacturerShortName());
                salesOrderLine.setManufacturerFullName(line.getManufacturerFullName());
                salesOrderLine.setUom(line.getUnitOfMeasure());
                salesOrderLine.setOrderedQty(line.getPickListQty());
                salesOrderLine.setExpectedQty(line.getPickListQty());
                salesOrderLine.setPackQty(line.getPickedQty());
                salesOrderLine.setPickListNo(line.getPickListNo());
                salesOrderLine.setSalesOrderNo(line.getSalesOrderNo());
                salesOrderLine.setMiddlewareId(line.getPickListLineId());
                salesOrderLine.setMiddlewareHeaderId(dbOBOrder.getPickListHeaderId());
                salesOrderLine.setMiddlewareTable("OB_SalesOrder");
                salesOrderLines.add(salesOrderLine);
            }
            salesOrder.setSalesOrderHeader(salesOrderHeader);
            salesOrder.setSalesOrderLine(salesOrderLines);
        }

        salesOrderList.add(salesOrder);
        // Sales_Order_Record_Transaction_Service
        if (!salesOrderList.isEmpty()) {
            try {
                SalesOrder[] outboundHeader = salesOrderService.postSalesOrder(salesOrderList);
                if (outboundHeader.length > 0) {
                    salesOrderList.stream().forEach(sale -> {
                        // Updating the Processed Status = 10
                        salesOrderService.updateProcessedInboundOrder(sale.getSalesOrderHeader().getMiddlewareId(),
                                sale.getSalesOrderHeader().getCompanyCode(), sale.getSalesOrderHeader().getBranchCode(),
                                sale.getSalesOrderHeader().getPickListNumber(), 10L);
                        log.info("PickList Order Created Successfully {} - ", sale.getSalesOrderHeader().getPickListNumber());
                    });
                }
            } catch (Exception e) {
                salesOrderList.stream().forEach(sale -> {
                    salesOrderService.updateProcessedInboundOrder(sale.getSalesOrderHeader().getMiddlewareId(),
                            sale.getSalesOrderHeader().getCompanyCode(), sale.getSalesOrderHeader().getBranchCode(),
                            sale.getSalesOrderHeader().getPickListNumber(), 100L);
                    log.info("PickList Order Failed {} - ", sale.getSalesOrderHeader().getPickListNumber());
                });
            }
        }
        return null;
    }

    /**
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public WarehouseApiResponse processOutboundOrderSI() throws IllegalAccessException, InvocationTargetException {

        List<SalesInvoice> salesInvoiceList = salesInvoiceRepository.findBySalesInvoice(0L,branchCode);
        log.info("Order Received On salesInvoiceList: " + salesInvoiceList);

        List<com.almailem.ams.api.connector.model.wms.SalesInvoice> salesInvoiceList1 = new ArrayList<>();
        // Order_process_starting_Set_value_process_status_id=1
        salesInvoiceList.stream().forEach(salesInvoice -> {
            salesInvoiceService.updateProcessedOutboundOrder(salesInvoice.getSalesInvoiceId(),
                    salesInvoice.getCompanyCode(), salesInvoice.getBranchCode(),
                    salesInvoice.getSalesInvoiceNumber(), 1L);
            log.info("PickList Order Process Staring Set value is 1");
        });

//        SalesInvoice salesInvoice = new SalesInvoice();
        for (SalesInvoice dbOBOrder : salesInvoiceList) {

            com.almailem.ams.api.connector.model.wms.SalesInvoice salesInvoice = new com.almailem.ams.api.connector.model.wms.SalesInvoice();

            salesInvoice.setCompanyCode(dbOBOrder.getCompanyCode());
            salesInvoice.setBranchCode(dbOBOrder.getBranchCode());
            salesInvoice.setSalesOrderNumber(dbOBOrder.getSalesOrderNumber());
            salesInvoice.setSalesInvoiceNumber(dbOBOrder.getSalesInvoiceNumber());
            salesInvoice.setPickListNumber(dbOBOrder.getPickListNumber());
            salesInvoice.setInvoiceDate(String.valueOf(dbOBOrder.getInvoiceDate()));

            salesInvoice.setAddress(dbOBOrder.getAddress());
            salesInvoice.setStatus(dbOBOrder.getStatus());
            salesInvoice.setAlternateNo(dbOBOrder.getAlternateNo());
            salesInvoice.setCustomerId(dbOBOrder.getCustomerId());
            salesInvoice.setCustomerName(dbOBOrder.getCustomerName());
            salesInvoice.setPhoneNumber(dbOBOrder.getPhoneNumber());
            salesInvoice.setDeliveryType(dbOBOrder.getDeliveryType());

            salesInvoice.setMiddlewareId(dbOBOrder.getSalesInvoiceId());
            salesInvoice.setMiddlewareTable("OBSalesInvoice");
            salesInvoiceList1.add(salesInvoice);
        }

        if (!salesInvoiceList1.isEmpty()) {
            try {
                com.almailem.ams.api.connector.model.wms.SalesInvoice[] outboundHeader = salesInvoiceService.postSalesInvoice(salesInvoiceList1);
                if (outboundHeader.length > 0) {
                    salesInvoiceList1.stream().forEach(sale -> {
                        // Updating the Processed Status = 10
                        salesInvoiceService.updateProcessedOutboundOrder(sale.getMiddlewareId(),
                                sale.getCompanyCode(), sale.getBranchCode(),
                                sale.getSalesInvoiceNumber(), 10L);
                        log.info("Sale Invoice Created Successfully {} - ", sale.getSalesInvoiceNumber());
                    });
                }
            } catch (Exception e) {
                salesInvoiceList1.stream().forEach(sale -> {
                    salesInvoiceService.updateProcessedOutboundOrder(sale.getMiddlewareId(),
                            sale.getCompanyCode(), sale.getBranchCode(),
                            sale.getSalesInvoiceNumber(), 100L);
                    log.info("Sale Invoice Order Failed {} - ", sale.getSalesInvoiceNumber());
                });
            }
        }
        return null;
    }

    //======================================StockCount_Perpetual=======================================================
    public WarehouseApiResponse processPerpetualOrder() throws IllegalAccessException, InvocationTargetException {

        List<PerpetualHeader> perpetualHeaders = perpetualHeaderRepository.findByPerpetual(0L,branchCode);
        log.info("Order Received On perpetualHeaders: " + perpetualHeaders);
        Perpetual perpetual = new Perpetual();

        for (PerpetualHeader dbObOrder : perpetualHeaders) {
            PerpetualHeaderV1 perpetualHeaderV1 = new PerpetualHeaderV1();

            perpetualHeaderV1.setCompanyCode(dbObOrder.getCompanyCode());
            perpetualHeaderV1.setCycleCountNo(dbObOrder.getCycleCountNo());
            perpetualHeaderV1.setBranchCode(dbObOrder.getBranchCode());
            perpetualHeaderV1.setBranchName(dbObOrder.getBranchName());
            perpetualHeaderV1.setIsNew(dbObOrder.getIsNew());
            perpetualHeaderV1.setCycleCountCreationDate(dbObOrder.getCycleCountCreationDate());
            perpetualHeaderV1.setUpdatedOn(dbObOrder.getUpdatedOn());
            perpetualHeaderV1.setIsCancelled(dbObOrder.getIsCancelled());
            perpetualHeaderV1.setIsCompleted(dbObOrder.getIsCompleted());
            perpetualHeaderV1.setMiddlewareId(dbObOrder.getPerpetualHeaderId());
            perpetualHeaderV1.setMiddlewareTable("PERPETUAL_HEADER");

            List<PerpetualLineV1> perpetualLineV1List = new ArrayList<>();
            for (PerpetualLine line : dbObOrder.getPerpetualLines()) {
                PerpetualLineV1 perpetualLineV1 = new PerpetualLineV1();

                perpetualLineV1.setCycleCountNo(line.getCycleCountNo());
                perpetualLineV1.setLineNoOfEachItemCode(line.getLineNoOfEachItemCode());
                perpetualLineV1.setItemCode(line.getItemCode());
                perpetualLineV1.setItemDescription(line.getItemDescription());
                perpetualLineV1.setUom(line.getUnitOfMeasure());
                perpetualLineV1.setManufacturerCode(line.getManufacturerCode());
                perpetualLineV1.setManufacturerName(line.getManufacturerName());
                perpetualLineV1.setFrozenQty(line.getFrozenQty());
                perpetualLineV1.setCountedQty(line.getCountedQty());
                perpetualLineV1.setIsCancelled(line.getIsCancelled());
                perpetualLineV1.setIsCompleted(line.getIsCompleted());
                perpetualLineV1.setMiddlewareId(line.getPerpetualLineId());
                perpetualLineV1.setMiddlewareHeaderId(dbObOrder.getPerpetualHeaderId());
                perpetualLineV1.setMiddlewareTable("PERPETUAL_LINE");
                perpetualLineV1List.add(perpetualLineV1);
            }
            perpetual.setPerpetualLineV1(perpetualLineV1List);
            perpetual.setPerpetualHeaderV1(perpetualHeaderV1);
            try {
                log.info("Perpetual Order Number: " + perpetual.getPerpetualHeaderV1().getCycleCountNo());
                WarehouseApiResponse response = perpetualService.postPerpetualOrderV2(perpetual);
                if (response != null) {

                    //Updating the Processed Status = 10
                    perpetualService.updateProcessedPerpetualOrder(perpetual.getPerpetualHeaderV1().getMiddlewareId(),
                            perpetual.getPerpetualHeaderV1().getCompanyCode(), perpetual.getPerpetualHeaderV1().getBranchCode(),
                            perpetual.getPerpetualHeaderV1().getCycleCountNo(), 10L);
//                        stcPerpetualList.remove(perpetual);
//                        return response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error on StockCount processing: " + e.toString());

                //Integration Log
                integrationLogService.createPerpetualLog(perpetual, e.toString());

                //Updating the Processed Status = 100
                perpetualService.updateProcessedPerpetualOrder(perpetual.getPerpetualHeaderV1().getMiddlewareId(),
                        perpetual.getPerpetualHeaderV1().getCompanyCode(), perpetual.getPerpetualHeaderV1().getBranchCode(),
                        perpetual.getPerpetualHeaderV1().getCycleCountNo(), 100L);
                //============================================================================================
                //Sending Failed Details through Mail
                OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                inboundOrderCancelInput.setCompanyCodeId(perpetualHeaderV1.getCompanyCode());
                inboundOrderCancelInput.setPlantId(perpetualHeaderV1.getBranchCode());
                inboundOrderCancelInput.setRefDocNumber(perpetualHeaderV1.getCycleCountNo());
                inboundOrderCancelInput.setReferenceField1("PERPETUALHEADER");
                String errorDesc = null;
                try {
                    if (e.toString().contains("message")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                        errorDesc = errorDesc.replaceAll("}]", "");
                    }
                    if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                        errorDesc = "Null Pointer Exception";
                    }
                    if (e.toString().contains("BadRequestException")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                    }
                } catch (Exception ex) {
                    throw new BadRequestException("ErrorDesc Extract Error" + ex);
                }
                inboundOrderCancelInput.setRemarks(errorDesc);

                mastersService.sendMail(inboundOrderCancelInput);
                //============================================================================================
                throw new BadRequestException("Exception :" + e);
            }
        }
        log.info("There is no Perpetual record found to process (sql) ...Waiting..");
        return null;
    }

    //======================================StockCount_Periodic=======================================================
    public WarehouseApiResponse processPeriodicOrder() throws IllegalAccessException, InvocationTargetException {

        List<PeriodicHeader> periodicHeaders = periodicHeaderRepository.findByPERIODICHEADER(0L,branchCode);
        log.info("Order Received On periodicHeaders: " + periodicHeaders);
        Periodic periodic = new Periodic();

        for (PeriodicHeader dbOrder : periodicHeaders) {
            PeriodicHeaderV1 periodicHeaderV1 = new PeriodicHeaderV1();

            periodicHeaderV1.setCompanyCode(dbOrder.getCompanyCode());
            periodicHeaderV1.setBranchCode(dbOrder.getBranchCode());
            periodicHeaderV1.setBranchName(dbOrder.getBranchName());
            periodicHeaderV1.setCycleCountNo(dbOrder.getCycleCountNo());
            periodicHeaderV1.setCycleCountCreationDate(dbOrder.getCycleCountCreationDate());
            periodicHeaderV1.setIsNew(dbOrder.getIsNew());
            periodicHeaderV1.setIsCancelled(dbOrder.getIsCancelled());
            periodicHeaderV1.setIsCompleted(dbOrder.getIsCompleted());
            periodicHeaderV1.setUpdatedOn(dbOrder.getUpdatedOn());
            periodicHeaderV1.setMiddlewareId(dbOrder.getPeriodicHeaderId());
            periodicHeaderV1.setMiddlewareTable("PERIODIC_HEADER");

            List<PeriodicLineV1> periodicLineV1List = new ArrayList<>();
            for (PeriodicLine line : dbOrder.getPeriodicLines()) {
                PeriodicLineV1 periodicLineV1 = new PeriodicLineV1();

                periodicLineV1.setCycleCountNo(line.getCycleCountNo());
                periodicLineV1.setLineNoOfEachItemCode(line.getLineNoOfEachItemCode());
                periodicLineV1.setItemCode(line.getItemCode());
                periodicLineV1.setItemDescription(line.getItemDescription());
                periodicLineV1.setItemDescription(line.getItemDescription());
                periodicLineV1.setUom(line.getUnitOfMeasure());
                periodicLineV1.setManufacturerCode(line.getManufacturerCode());
                periodicLineV1.setManufacturerName(line.getManufacturerName());
                periodicLineV1.setCountedQty(line.getCountedQty());
                periodicLineV1.setFrozenQty(line.getFrozenQty());
                periodicLineV1.setIsCompleted(line.getIsCompleted());
                periodicLineV1.setIsCancelled(line.getIsCancelled());
                periodicLineV1.setMiddlewareId(line.getPeriodicLineId());
                periodicLineV1.setMiddlewareHeaderId(dbOrder.getPeriodicHeaderId());
                periodicLineV1.setMiddlewareTable("PERIODIC_LINE");
                periodicLineV1List.add(periodicLineV1);
            }
            periodic.setPeriodicLineV1(periodicLineV1List);
            periodic.setPeriodicHeaderV1(periodicHeaderV1);
            try {
                log.info("Periodic Order Number: " + periodic.getPeriodicHeaderV1().getCycleCountNo());
                WarehouseApiResponse response = periodicService.postPeriodicOrderV2(periodic);
                if (response != null) {

                    //Updating the Processed Status = 10
                    periodicService.updateProcessedPeriodicOrder(periodic.getPeriodicHeaderV1().getMiddlewareId(),
                            periodic.getPeriodicHeaderV1().getCompanyCode(), periodic.getPeriodicHeaderV1().getBranchCode(),
                            periodic.getPeriodicHeaderV1().getCycleCountNo(), 10L);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error on StockCount processing: " + e.toString());

                //Integration Log
                integrationLogService.createPeriodicLog(periodic, e.toString());

                //Updating the Processed Status
                periodicService.updateProcessedPeriodicOrder(periodic.getPeriodicHeaderV1().getMiddlewareId(),
                        periodic.getPeriodicHeaderV1().getCompanyCode(), periodic.getPeriodicHeaderV1().getBranchCode(),
                        periodic.getPeriodicHeaderV1().getCycleCountNo(), 100L);
                //============================================================================================
                //Sending Failed Details through Mail
                OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                inboundOrderCancelInput.setCompanyCodeId(periodicHeaderV1.getCompanyCode());
                inboundOrderCancelInput.setPlantId(periodicHeaderV1.getBranchCode());
                inboundOrderCancelInput.setRefDocNumber(periodicHeaderV1.getCycleCountNo());
                inboundOrderCancelInput.setReferenceField1("PERIODICHEADER");
                String errorDesc = null;
                try {
                    if (e.toString().contains("message")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                        errorDesc = errorDesc.replaceAll("}]", "");
                    }
                    if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                        errorDesc = "Null Pointer Exception";
                    }
                    if (e.toString().contains("BadRequestException")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                    }
                } catch (Exception ex) {
                    throw new BadRequestException("ErrorDesc Extract Error" + ex);
                }
                inboundOrderCancelInput.setRemarks(errorDesc);

                mastersService.sendMail(inboundOrderCancelInput);
                //============================================================================================
                throw new BadRequestException("Exception :" + e);
            }
        }
        return null;
    }

    //=======================================================V2============================================================

    public WarehouseApiResponse processStockAdjustmentOrder() throws IllegalAccessException, InvocationTargetException {

        List<com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment> stockAdjustments = stockAdjustmentRepo.findByStockAdjustment(0L,branchCode);
        log.info("StockAdjustment Found: " + stockAdjustments);
        StockAdjustment stockAdjustment = new StockAdjustment();
        for (com.almailem.ams.api.connector.model.stockadjustment.StockAdjustment dbSA : stockAdjustments) {

            stockAdjustment.setCompanyCode(dbSA.getCompanyCode());
            stockAdjustment.setBranchCode(dbSA.getBranchCode());
            stockAdjustment.setBranchName(dbSA.getBranchName());
            stockAdjustment.setDateOfAdjustment(dbSA.getDateOfAdjustment());
            stockAdjustment.setIsDamage(dbSA.getIsDamage());
            if (dbSA.getIsDamage() != null) {
                if (dbSA.getIsDamage().equalsIgnoreCase("Y")) {
                    stockAdjustment.setIsCycleCount("N");
                }
                if (dbSA.getIsDamage().equalsIgnoreCase("N")) {
                    stockAdjustment.setIsCycleCount("Y");
                }
            }
            stockAdjustment.setItemCode(dbSA.getItemCode());
            stockAdjustment.setItemDescription(dbSA.getItemDescription());
            stockAdjustment.setAdjustmentQty(dbSA.getAdjustmentQty());
            stockAdjustment.setUnitOfMeasure(dbSA.getUnitOfMeasure());
            stockAdjustment.setManufacturerCode(dbSA.getManufacturerCode());
            if (dbSA.getManufacturerName() != null) {
                stockAdjustment.setManufacturerName(dbSA.getManufacturerName());
            }
            if (dbSA.getManufacturerName() == null) {
                stockAdjustment.setManufacturerName(dbSA.getManufacturerCode());
            }
            stockAdjustment.setRemarks(dbSA.getRemarks());
            stockAdjustment.setAmsReferenceNo(dbSA.getAmsReferenceNo());
            stockAdjustment.setIsCompleted(dbSA.getIsCompleted());
            stockAdjustment.setUpdatedOn(dbSA.getUpdatedOn());
            stockAdjustment.setStockAdjustmentId(dbSA.getStockAdjustmentId());
            stockAdjustment.setMiddlewareId(dbSA.getStockAdjustmentId());
            stockAdjustment.setMiddlewareTable("STOCK_ADJUSTMENT");

            try {
                log.info("Item Code: " + stockAdjustment.getItemCode());
                WarehouseApiResponse response = stockAdjustmentService.postStockAdjustmentV2(stockAdjustment);
                if (response != null) {
                    // Updating the Processed Status = 10
                    stockAdjustmentService.updateProcessedStockAdjustment(stockAdjustment.getStockAdjustmentId(),
                            stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), stockAdjustment.getItemCode(), 10L);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error on Stock Adjustment processing : " + e.toString());

                //Integration Log
                integrationLogService.createStockAdjustment(stockAdjustment, e.toString());

                // Updating the Processed Status = 100
                stockAdjustmentService.updateProcessedStockAdjustment(stockAdjustment.getStockAdjustmentId(),
                        stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), stockAdjustment.getItemCode(), 100L);
                //============================================================================================
                //Sending Failed Details through Mail
                OrderCancelInput inboundOrderCancelInput = new OrderCancelInput();
                inboundOrderCancelInput.setCompanyCodeId(stockAdjustment.getCompanyCode());
                inboundOrderCancelInput.setPlantId(stockAdjustment.getBranchCode());
                inboundOrderCancelInput.setRefDocNumber(stockAdjustment.getItemCode());
                inboundOrderCancelInput.setReferenceField2(stockAdjustment.getManufacturerName());
                inboundOrderCancelInput.setReferenceField1("STOCKADJUSTMENT");
                String errorDesc = null;
                try {
                    if (e.toString().contains("message")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
                        errorDesc = errorDesc.replaceAll("}]", "");
                    }
                    if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                        errorDesc = "Null Pointer Exception";
                    }
                    if (e.toString().contains("BadRequestException")) {
                        errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                    }
                } catch (Exception ex) {
                    throw new BadRequestException("ErrorDesc Extract Error" + ex);
                }
                inboundOrderCancelInput.setRemarks(errorDesc);

                mastersService.sendMail(inboundOrderCancelInput);
                //============================================================================================
                throw new BadRequestException("Exception :" + e);
            }
        }
        return null;
    }

}
