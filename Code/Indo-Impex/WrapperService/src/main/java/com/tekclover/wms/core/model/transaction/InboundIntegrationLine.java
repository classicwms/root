package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class InboundIntegrationLine {

    private String lineReference;                            // IB_LINE_NO
    private String itemCode;                                // ITM_CODE
    private String itemText;                                // ITEM_TEXT
    private String invoiceNumber;                            // INV_NO
    private String containerNumber;                        // CONT_NO
    private String supplierCode;                            // PARTNER_CODE
    private String supplierPartNumber;                        // PARTNER_ITM_CODE
    private String manufacturerName;                        // BRND_NM
    private String manufacturerPartNo;                        // MFR_PART
    private String expectedDate;                            // EA_DATE
    private Double orderedQty;                                // ORD_QTY
    private String uom;                                        // ORD_UOM
    private Double itemCaseQty;                                // ITM_CASE_QTY
    private String salesOrderReference;                        // REF_FIELD_4

    //---------------------ALM Changes-----------------------------------------
    private String manufacturerCode;                         // MFR_CODE
    private String origin;                                   // ORIGIN
    private String brand;                                    // BRAND
    private String orderType;                                // BRAND
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String purchaseOrderNumber;
    private String supplierName;

    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;
    private String supplierInvoiceNo;

    private String branchCode;
    private String transferOrderNo;
}
