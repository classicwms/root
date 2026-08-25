package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "tbloborderlines2")
@Data
public class OutboundOrderLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "line_reference")
	private Long lineReference;								// IB_LINE_NO
	@Column(name = "item_code")
	private String itemCode; 								// ITM_CODE
	@Column(name = "item_text")
	private String itemText; 								// ITEM_TEXT
	@Column(name = "ordered_qty")
	private Double orderedQty;								// ORD_QTY
	@Column(name = "uom")
	private String uom;										// ORD_UOM
	@Column(name = "ref_field1for_order_type")
	private String refField1ForOrderType;					// REF_FIELD_1
	@Column(name = "order_id")
	private String orderId;
//	@Column(name = "supplier_part_number")
//	private String supplierPartNumber;
//	@Column(name = "expected_date")
//	private Date expectedDate;
}
