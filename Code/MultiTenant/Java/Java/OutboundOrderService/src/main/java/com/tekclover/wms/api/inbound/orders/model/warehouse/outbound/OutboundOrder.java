package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "tbloborder2")
@Data
public class OutboundOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OUTBOUND_ORDER_HEADER_ID")
	private Long outboundOrderHeaderId;

	@Column(name = "order_id", nullable = false)
	private String orderId;

	@Column(name = "warehouse_id")
	private String warehouseID;// WH_ID
	@Column(name = "ref_Document_No")
	private String refDocumentNo; 			// REF_DOCument_NO
	@Column(name = "ref_document_type")
	private String refDocumentType; 		// REF_DOC_TYPE
	@Column(name = "partner_code")
	private String partnerCode; 			// PARTNER_CODE
	@Column(name = "partner_name")
	private String partnerName; 			// PARTNER_NM
	@Column(name = "required_delivery_date")
	private Date requiredDeliveryDate;		// REQ_DEL_DATE

	// Additional Fields
	@Column(name = "order_received_on")
	private Date orderReceivedOn; 			// order_received_on
	@Column(name = "order_processed_on")
	private Date orderProcessedOn;
	@Column(name = "processed_status_id")
	private Long processedStatusId;			// processed_status_id
	@Column(name = "outbound_order_typeid")
	private Long outboundOrderTypeID;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLine> lines;

}


