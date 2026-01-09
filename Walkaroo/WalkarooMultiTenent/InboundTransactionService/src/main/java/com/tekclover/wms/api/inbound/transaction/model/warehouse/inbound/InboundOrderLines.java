package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "tbliborderlines2")
@Data
public class InboundOrderLines {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "line_reference")
	private Long lineReference;								// IB_LINE_NO
	@Column(name = "item_code")
	private String itemCode; 								// ITM_CODE
	@Column(name = "item_text")
	private String itemText; 								// ITEM_TEXT
	@Column(name = "invoice_number")
	private String invoiceNumber; 							// INV_NO
	@Column(name = "container_number")
	private String containerNumber; 						// CONT_NO
	@Column(name = "supplier_code")
	private String supplierCode; 							// PARTNER_CODE
	@Column(name = "supplier_part_number")
	private String supplierPartNumber;						// PARTNER_ITM_CODE
	@Column(name = "manufacturer_name")
	private String manufacturerName;						// BRND_NM
	@Column(name = "manufacturer_part_no")
	private String manufacturerPartNo;						// MFR_PART
	@Column(name = "expected_date")
	private Date expectedDate;								// EA_DATE
	@Column(name = "ordered_qty")
	private Double orderedQty;								// ORD_QTY
	@Column(name = "uom")
	private String uom;										// ORD_UOM
	@Column(name = "item_case_qty")
	private Double itemCaseQty;								// ITM_CASE_QTY
	@Column(name = "sales")
	private String salesOrderReference;						// REF_FIELD_4
	@Column(name = "order_id")
	private String orderId;

}
