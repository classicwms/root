package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class GrHeaderStream {

	private String preInboundNo;
	private String refDocNumber;
	private String caseCode;
	private Long statusId;
	private String createdBy;
	private Date createdOn;

	/**
	 * GrHeader
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param caseCode
	 * @param statusId
	 * @param createdBy
	 * @param createdOn
	 */
	public GrHeaderStream(String preInboundNo,
                          String refDocNumber,
                          String caseCode,
                          Long statusId,
                          String createdBy,
                          Date createdOn) {
		this.preInboundNo = preInboundNo;
		this.refDocNumber = refDocNumber;
		this.caseCode = caseCode;
		this.statusId = statusId;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
