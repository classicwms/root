package com.tekclover.wms.api.transaction.model.inbound.v2;

import com.tekclover.wms.api.transaction.model.inbound.SearchInboundLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
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
}
