package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound;

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

	@Column(name = "ref_Document_No")
	private String refDocumentNo; 			// REF_DOC_NO
	@Column(name = "ref_document_type")
	private String refDocumentType; 		// REF_DOC_TYPE
	@Column(name = "warehouse_id")
	private String warehouseID;   // WH_ID
	@Column(name = "inbound_order_type_id")
	private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
	@Column(name = "order_received_on")
	private Date orderReceivedOn;
	@Column(name = "order_processed_on")
	private Date orderProcessedOn;
	@Column(name = "processed_status_id")
	private Long processedStatusId;
	@Column(name = "purchase_order_number")
	private String purchaseOrderNumber;
	@Column(name = "language_id")
	private String languageId;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "INBOUND_ORDER_HEADER_ID",referencedColumnName = "INBOUND_ORDER_HEADER_ID")
    private Set<InboundOrderLines> lines;
	
}