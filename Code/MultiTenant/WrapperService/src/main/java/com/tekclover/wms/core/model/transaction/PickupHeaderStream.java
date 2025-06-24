package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PickupHeaderStream {

	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackBarCode;
	private Long outboundOrderTypeId;
	private Double pickToQty;
	private Long statusId;
	private String assignedPickerId;
	private String referenceField1;
	private Date pickupCreatedOn;

	/**
	 * GrHeader
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackBarCode
	 * @param outboundOrderTypeId
	 * @param pickToQty
	 * @param statusId
	 * @param assignedPickerId
	 * @param referenceField1
	 * @param pickupCreatedOn
	 */
	public PickupHeaderStream(String refDocNumber,
                              String partnerCode,
                              Long lineNumber,
                              String itemCode,
                              String proposedStorageBin,
                              String proposedPackBarCode,
                              Long outboundOrderTypeId,
                              Double pickToQty,
                              Long statusId,
                              String assignedPickerId,
							  String referenceField1,
                              Date pickupCreatedOn) {
		this.refDocNumber = refDocNumber;
		this.partnerCode = partnerCode;
		this.lineNumber = lineNumber;
		this.itemCode = itemCode;
		this.proposedStorageBin = proposedStorageBin;
		this.proposedPackBarCode = proposedPackBarCode;
		this.outboundOrderTypeId = outboundOrderTypeId;
		this.pickToQty = pickToQty;
		this.statusId = statusId;
		this.assignedPickerId = assignedPickerId;
		this.referenceField1 = referenceField1;
		this.pickupCreatedOn = pickupCreatedOn;
	}
}
