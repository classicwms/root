package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tbliborder")
@Data
public class InboundOrder {
	
	@Id
	@Column(name = "order_id", nullable = false)
	private String orderId;

	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String warehouseID; 			// WH_ID
	private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "orderId",fetch = FetchType.EAGER)
    private Set<InboundOrderLines> lines;
	
}