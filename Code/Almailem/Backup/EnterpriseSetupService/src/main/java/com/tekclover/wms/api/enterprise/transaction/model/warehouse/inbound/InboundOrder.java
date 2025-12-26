package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "tbliborder2")
@Data
public class InboundOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INBOUND_ORDER_HEADER_ID")
	private Long inboundOrderHeaderId;

	@Column(name = "order_id", nullable = false)
	private String orderId;

	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String warehouseID; 			// WH_ID
	private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;

	private String purchaseOrderNumber;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "INBOUND_ORDER_HEADER_ID",referencedColumnName = "INBOUND_ORDER_HEADER_ID")
    private Set<InboundOrderLines> lines;
	
}