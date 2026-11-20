package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class OrderManagementLineStream {

	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackBarCode;
	private Long outboundOrderTypeId;
	private Long statusId;
	private String description;
	private Double orderQty;
	private Double inventoryQty;
	private Double allocatedQty;
	private Date requiredDeliveryDate;

	/**
	 * Order Management Line
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackBarCode
	 * @param outboundOrderTypeId
	 * @param statusId
	 * @param description
	 * @param orderQty
	 * @param inventoryQty
	 * @param allocatedQty
	 * @param requiredDeliveryDate
	 */
	public OrderManagementLineStream(String preOutboundNo,
                                     String refDocNumber,
                                     String partnerCode,
                                     Long lineNumber,
                                     String itemCode,
                                     String proposedStorageBin,
                                     String proposedPackBarCode,
                                     Long outboundOrderTypeId,
                                     Long statusId,
                                     String description,
                                     Double orderQty,
                                     Double inventoryQty,
                                     Double allocatedQty,
									 Date requiredDeliveryDate) {
		this.preOutboundNo = preOutboundNo;
		this.refDocNumber = refDocNumber;
		this.partnerCode = partnerCode;
		this.lineNumber = lineNumber;
		this.itemCode = itemCode;
		this.proposedStorageBin = proposedStorageBin;
		this.proposedPackBarCode = proposedPackBarCode;
		this.outboundOrderTypeId = outboundOrderTypeId;
		this.statusId = statusId;
		this.description = description;
		this.orderQty = orderQty;
		this.inventoryQty = inventoryQty;
		this.allocatedQty = allocatedQty;
		this.requiredDeliveryDate = requiredDeliveryDate;
	}
}
