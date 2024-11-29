package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbloborderlines")
@Data
public class OutboundOrderLine { 
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private Long lineReference;								// IB_LINE_NO
	private String itemCode; 								// ITM_CODE
	private String itemText; 								// ITEM_TEXT
	private Double orderedQty;								// ORD_QTY
	private String uom;										// ORD_UOM
	private String refField1ForOrderType;					// REF_FIELD_1
	private String orderId;
}