package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class SearchPickingProductivityReport {

	private List<String> warehouseId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> refDocNo;
	private List<String> preOutboundNo;
	private List<String> assignedPickerId;
	private List<Long> levelId;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;

	private String startDate;
	private String endDate;
}