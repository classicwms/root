package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import java.util.Date;


@Data
public class InboundOrderLinesV2 {

    private String manufacturerCode;
    private String origin;
    private String supplierName;
    private String brand;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;

    private String sourceBranchCode;
    private String countryOfOrigin;
    private String manufacturerFullName;
    private String purchaseOrderNumber;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    private String transferOrderNumber;
    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;
    private String supplierInvoiceNo;
    private String companyCode;
    private String branchCode;
    private Long id;

    private Long lineReference;								// IB_LINE_NO
    private String itemCode; 								// ITM_CODE
    private String itemText; 								// ITEM_TEXT
    private String invoiceNumber; 							// INV_NO
    private String containerNumber; 						// CONT_NO
    private String supplierCode; 							// PARTNER_CODE
    private String supplierPartNumber;						// PARTNER_ITM_CODE
    private String manufacturerName;						// BRND_NM
    private String manufacturerPartNo;						// MFR_PART
    private Date expectedDate;								// EA_DATE
    private Double orderedQty;								// ORD_QTY
    private String uom;										// ORD_UOM
    private Double itemCaseQty;								// ITM_CASE_QTY
    private String salesOrderReference;						// REF_FIELD_4
    private String orderId;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String barcodeId;
}
