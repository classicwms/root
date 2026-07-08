package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateInboundOrder {

    private String refDocumentNo; 			               // REF_DOC_NO
    private String refDocumentType; 		               // REF_DOC_TYPE
    private Date orderReceivedOn;
    private Date orderProcessedOn;
    private Long processedStatusId;
    private String purchaseOrderNumber;
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
    private String salesOrderReference;
    private String warehouseID; 			                // WH_ID
    private Long inboundOrderTypeId; 		                // IB_ORD_TYP_ID

}
