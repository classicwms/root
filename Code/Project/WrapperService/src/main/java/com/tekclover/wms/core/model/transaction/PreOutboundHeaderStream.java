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
	private Date refDocDate;
	private Date requiredDeliveryDate;
	private String referenceField1;

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
                                   Long statusId,
                                   Date refDocDate,
                                   Date requiredDeliveryDate,
                                   String referenceField1) {
		this.refDocNumber = refDocNumber;
		this.preOutboundNo = preOutboundNo;
		this.partnerCode = partnerCode;
		this.referenceDocumentType = referenceDocumentType;
		this.statusId = statusId;
		this.refDocDate = refDocDate;
		this.requiredDeliveryDate = requiredDeliveryDate;
		this.referenceField1 = referenceField1;
	}
}
