package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FetchImpl {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private Long lineNo;
	private String itemCode;
	private String preInboundNo;
	private String preOutboundNo;
	private String refDocNumber;
	private String batchSerialNumber;
	private String storageSectionId;
	private String loginUserID;
	private String confirmedStorageBin;
	private Long statusId;
	private Date createdOn;
}