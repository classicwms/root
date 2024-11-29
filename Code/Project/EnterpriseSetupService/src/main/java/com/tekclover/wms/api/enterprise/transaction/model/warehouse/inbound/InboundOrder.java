package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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