package com.tekclover.wms.api.transaction.model.warehouse.outbound;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "tbloborderlines2")
@Data
public class OutboundOrderLine { 
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LINE_REFERENCE")
    private Long lineReference;

    @Column(name = "OUTBOUND_ORDER_HEADER_ID")
    private Long outboundOrderHeaderId;

    @Column(name = "ITEM_CODE")
    private String itemCode;

    @Column(name = "ITEM_TEXT")
    private String itemText;

    @Column(name = "ORDERED_QTY")
    private Double orderedQty;

    @Column(name = "UOM")
    private String uom;

    @Column(name = "REF_FIELD1FOR_ORDER_TYPE")
    private String refField1ForOrderType;

    @Column(name = "ORDER_ID")
    private String orderId;


}
