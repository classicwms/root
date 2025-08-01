package com.tekclover.wms.core.model.transaction;

import com.tekclover.wms.core.model.transaction.SearchInboundLine;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchInboundLineV2 extends SearchInboundLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<Long> inboundOrderTypeId;
	private List<String> sourceBranchCode;
	private List<String> sourceCompanyCode;

	private Date startCreatedOn;
	private Date endCreatedOn;
	private List<String> customerId;

}
