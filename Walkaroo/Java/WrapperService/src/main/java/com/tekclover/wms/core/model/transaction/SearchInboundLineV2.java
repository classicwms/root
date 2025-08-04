package com.tekclover.wms.core.model.transaction;

import com.tekclover.wms.core.model.transaction.SearchInboundLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
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
}
