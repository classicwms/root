package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString(callSuper = true)
public class SearchOutboundHeaderV2 extends SearchOutboundHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> targetBranchCode;
	
	private Date startOrderDate;
	private Date endOrderDate;

	private Date startCreatedOn;
	private Date endCreatedOn;
}