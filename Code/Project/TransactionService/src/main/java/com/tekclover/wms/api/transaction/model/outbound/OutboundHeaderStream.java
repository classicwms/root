package com.tekclover.wms.api.transaction.model.outbound;

import lombok.Data;

import java.util.Date;

@Data
public class OutboundHeaderStream {

	private String preInboundNo;
	private String refDocNumber;
	private String stagingNo;
	private Long inboundOrderTypeId;
	private Long statusId;
	private String createdBy;
	private Date createdOn;

	/**
	 * StagingHeader
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param inboundOrderTypeId
	 * @param statusId
	 * @param createdBy
	 * @param createdOn
	 */
	public OutboundHeaderStream(String preInboundNo,
                                String refDocNumber,
                                String stagingNo,
                                Long inboundOrderTypeId,
                                Long statusId,
                                String createdBy,
                                Date createdOn) {
		this.preInboundNo = preInboundNo;
		this.refDocNumber = refDocNumber;
		this.stagingNo = stagingNo;
		this.inboundOrderTypeId = inboundOrderTypeId;
		this.statusId = statusId;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
