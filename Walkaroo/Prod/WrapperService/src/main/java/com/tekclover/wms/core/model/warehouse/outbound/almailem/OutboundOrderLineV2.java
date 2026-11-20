package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import java.util.Date;

@Data
public class OutboundOrderLineV2 {
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

    private String manufacturerName;
    private String manufacturerFullName;
    private String fulfilmentMethod;

    private String salesOrderNo;
    private String pickListNo;

    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String transferOrderNumber;
    private String salesInvoiceNo;
    private String supplierInvoiceNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
    private Long id;

    private Long lineReference;								// IB_LINE_NO
    private String itemCode; 								// ITM_CODE
    private String itemText; 								// ITEM_TEXT
    private Double orderedQty;								// ORD_QTY
    private String uom;										// ORD_UOM
    private String refField1ForOrderType;					// REF_FIELD_1
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
