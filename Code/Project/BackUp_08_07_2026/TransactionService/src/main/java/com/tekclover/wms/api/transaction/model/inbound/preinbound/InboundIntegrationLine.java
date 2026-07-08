package com.tekclover.wms.api.transaction.model.inbound.preinbound;

import java.util.Date;

import lombok.Data;

@Data
public class InboundIntegrationLine { 
	
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
}
