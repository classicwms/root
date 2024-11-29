package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbliborderlines")
@Data
public class InboundOrderLines {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}