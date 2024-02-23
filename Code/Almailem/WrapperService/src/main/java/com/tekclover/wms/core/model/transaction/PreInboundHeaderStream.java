package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PreInboundHeaderStream {

	private String preInboundNo;
	private String refDocNumber;
	private Long inboundOrderTypeId;
	private Long statusId;
	private String containerNo;
	private Date refDocDate;
	private String createdBy;

	/**
	 * PreInboundHeader
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param inboundOrderTypeId
	 * @param statusId
	 * @param containerNo
	 * @param refDocDate
	 * @param createdBy
	 */
	public PreInboundHeaderStream(String preInboundNo,
                                  String refDocNumber,
                                  Long inboundOrderTypeId,
                                  Long statusId,
                                  String containerNo,
                                  Date refDocDate,
                                  String createdBy) {
		this.preInboundNo = preInboundNo;
		this.refDocNumber = refDocNumber;
		this.inboundOrderTypeId = inboundOrderTypeId;
		this.statusId = statusId;
		this.containerNo = containerNo;
		this.refDocDate = refDocDate;
		this.createdBy = createdBy;
	}
}
