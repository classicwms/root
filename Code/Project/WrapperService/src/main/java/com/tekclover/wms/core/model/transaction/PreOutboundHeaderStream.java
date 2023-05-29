package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PreOutboundHeaderStream {

	private String refDocNumber;
	private String preOutboundNo;
	private String partnerCode;
	private String referenceDocumentType;
	private Long statusId;
	private String referenceField4;
	private Date refDocDate;
	private Date requiredDeliveryDate;
	private String referenceField1;
	private Long outboundOrderTypeId;

	/**
	 * PreOutbound Header
	 * @param refDocNumber
	 * @param preOutboundNo
	 * @param partnerCode
	 * @param referenceDocumentType
	 * @param statusId
	 * @param refDocDate
	 * @param requiredDeliveryDate
	 * @param referenceField1
	 */
	public PreOutboundHeaderStream(String refDocNumber,
                                   String preOutboundNo,
                                   String partnerCode,
                                   String referenceDocumentType,
                                   String referenceField4,
								   Long statusId,
                                   Date refDocDate,
                                   Date requiredDeliveryDate,
                                   String referenceField1,
								   Long outboundOrderTypeId) {
		this.refDocNumber = refDocNumber;
		this.preOutboundNo = preOutboundNo;
		this.partnerCode = partnerCode;
		this.referenceDocumentType = referenceDocumentType;
		this.referenceField4 = referenceField4;
		this.statusId = statusId;
		this.refDocDate = refDocDate;
		this.requiredDeliveryDate = requiredDeliveryDate;
		this.referenceField1 = referenceField1;
		this.outboundOrderTypeId = outboundOrderTypeId;
	}
}
