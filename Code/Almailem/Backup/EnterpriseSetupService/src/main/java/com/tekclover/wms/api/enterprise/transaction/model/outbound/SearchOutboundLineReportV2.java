package com.tekclover.wms.api.enterprise.transaction.model.outbound;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchOutboundLineReportV2 extends SearchOutboundLineReport {
	
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private String preOutboundNo;			// Order Number
}