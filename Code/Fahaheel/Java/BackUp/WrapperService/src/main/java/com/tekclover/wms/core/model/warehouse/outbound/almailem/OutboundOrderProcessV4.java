package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import java.util.Date;

@Data
public class OutboundOrderProcessV4 {

    private String salesOrderNumber;                        // REF_DOC_NO;
    private String pickListNumber;
    private String requiredDeliveryDate;                    //REQ_DEL_DATE
    private String status;
    private String tokenNumber;
    private String orderType;                                // REF_FIELD_1

    private String companyCode;
    private String branchCode;
    private String languageId;
    private String warehouseId;

    private String customerId;
    private String customerName;
    private String loginUserId;

    private String fromCompanyCode;
    private String fromBranchCode;
    private String toCompanyCode;
    private String toBranchCode;
    private String transferOrderNumber;
    private String poNumber;                            // REF_DOC_NO;
    private String supplierInvoiceNo;
    private String sourceCompanyCode;
    private String sourceBranchCode;

    //Lines
    private Long lineReference;                                // IB_LINE_NO
    private String barcodeId;
    private String sku;                                    // ITM_CODE
    private String skuDescription;                            // ITEM_TEXT
    private Double orderedQty;                                // ORD_QTY
    private Double returnQty;                                // ORD_QTY
    private String uom;                                        // ORD_UOM
    private String manufacturerCode;
    private String manufacturerName;

    private String storageSectionId;
    private String gender;
    private String color;
    private String size;
    private String brand;

    private String origin;
    private String supplierName;
    private Double packQty;
    private Double expectedQty;

    private String itemType;
    private String itemGroup;
    private String batchSerialNumber;
    private String address;
    private Long outboundOrderTypeId;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;

    /*--------------------------Walkaroo--------------------------------------------*/
    private String outbound;
    private Long itm;
    private String customerCode;
    private String customer;
    private String shipToCode;
    private String shipToParty;
    private String materialNo;
    private String priceSegment;
    private Double qty;
    private String specialStock;
    private String mtoNumber;
    private String skuCode;
    /*----------------------REEFERON--------------------------------------------------*/
    private Double qtyInCase;
    private Double qtyInPiece;

    /*----------------------Bharath Package--------------------------------------------------*/
    private Date salerOrderAmendmentDate;
    private String salerOrderAmendmentNo;

    private String fgSku;
    private String fgSkuDescription;
    private String fgUom;
    private String stock;
    private String weight;
    private String department;

}
