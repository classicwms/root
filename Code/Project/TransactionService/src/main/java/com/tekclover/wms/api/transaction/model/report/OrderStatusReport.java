package com.tekclover.wms.api.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class OrderStatusReport {

	private String warehouseId;			// WH_ID
	private Date deliveryConfirmedOn;	// DLV_CNF_ON
	private String soNumber;			// REF_DOC_NO
	private String doNumber;			// DLV_ORD_NO
	private String customerCode;		// PARTNER_CODE
	private String customerName;		// PARTNER_NM
	private String sku;					// ITM_CODE
	private String skuDescription;		// ITEM_TEXT
	private Double orderedQty;			// ORD_QTY
	private Double deliveredQty;		// DLV_QTY
	private Date orderReceivedDate;		// REF_DOC_DATE
	private Date expectedDeliveryDate;	// REQ_DEL_DATE
	private Double percentageOfDelivered;	// (DLV_QTY/ORD_QTY)*100
	private Long statusId;				// STATUS_ID
}

