package com.tekclover.wms.api.transaction.model.warehouse.outbound;

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

    @Column(name = "ORDER_ID", nullable = false)
    private String orderId;

    @Column(name = "WAREHOUSEID")
    private String warehouseID;

    @Column(name = "REF_DOCUMENT_NO")
    private String refDocumentNo;

    @Column(name = "REF_DOCUMENT_TYPE")
    private String refDocumentType;

    @Column(name = "PARTNER_CODE")
    private String partnerCode;

    @Column(name = "PARTNER_NAME")
    private String partnerName;

    @Column(name = "REQUIRED_DELIVERY_DATE")
    private Date requiredDeliveryDate;

    // Additional Fields
    @Column(name = "ORDER_RECEIVED_ON")
    private Date orderReceivedOn;

    @Column(name = "ORDER_PROCESSED_ON")
    private Date orderProcessedOn;

    @Column(name = "PROCESSED_STATUS_ID")
    private Long processedStatusId;

    @Column(name = "OUTBOUND_ORDER_TYPEID")
    private Long outboundOrderTypeID;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLine> lines;
}


