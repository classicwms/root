package com.tekclover.wms.api.transaction.model.warehouse.outbound;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tbloborder")
@Data
public class OutboundOrder {

	@Id
	@Column(name = "orderId", nullable = false)
	private String orderId;
	
	private String warehouseID; 			// WH_ID
	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String partnerCode; 			// PARTNER_CODE
	private String partnerName; 			// PARTNER_NM
	private Date requiredDeliveryDate;		// REQ_DEL_DATE

	// Additional Fields
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;
	private Long outboundOrderTypeID;

//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId", nullable = false)
    private Set<OutboundOrderLine> lines;
}


