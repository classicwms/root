package com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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
	
	private String warehouseID; 			// WH_ID
	private String refDocumentNo; 			// REF_DOCument_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String partnerCode; 			// PARTNER_CODE
	private String partnerName; 			// PARTNER_NM
	private Date requiredDeliveryDate;		// REQ_DEL_DATE

	// Additional Fields
	private Date orderReceivedOn; 			// order_received_on
	private Date orderProcessedOn;
	private Long processedStatusId;			// processed_status_id
	private Long outboundOrderTypeID;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLine> lines;
}


