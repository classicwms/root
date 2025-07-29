package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class EMailDetails {

	private Long emailId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String languageId;
	private String fromAddress;
	private String toAddress;
	private String ccAddress;
	private String subject;
	private String bodyText;
	private String groupBy;
	private Long deletionIndicator;
	private String senderName;
	private Date meetingStartTime;
	private Date meetingEndTime;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}