package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class InboundHeaderStream {

	private String refDocNumber;
	private Long statusId;
	private Long inboundOrderTypeId;
	private String containerNo;
	private Date createdOn;
	private String confirmedBy;
	private Date confirmedOn;

	/**
	 * Inbound Header
	 * @param refDocNumber
	 * @param statusId
	 * @param inboundOrderTypeId
	 * @param containerNo
	 * @param createdOn
	 * @param confirmedBy
	 * @param confirmedOn
	 */
	public InboundHeaderStream(String refDocNumber,
                               Long statusId,
							   Long inboundOrderTypeId,
							   String containerNo,
                               Date createdOn,
                               String confirmedBy,
                               Date confirmedOn) {
		this.refDocNumber = refDocNumber;
		this.statusId = statusId;
		this.inboundOrderTypeId = inboundOrderTypeId;
		this.containerNo = containerNo;
		this.createdOn = createdOn;
		this.confirmedBy = confirmedBy;
		this.confirmedOn = confirmedOn;
	}
}
