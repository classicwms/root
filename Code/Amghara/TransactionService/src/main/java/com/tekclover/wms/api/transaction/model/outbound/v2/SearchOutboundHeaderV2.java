package com.tekclover.wms.api.transaction.model.outbound.v2;

import java.util.Date;
import java.util.List;

import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundHeader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchOutboundHeaderV2 extends SearchOutboundHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> targetBranchCode;
	private List<String> preOutboundNo;
	
	private Date startOrderDate;
	private Date endOrderDate;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
}