package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.Date;
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

	private Date startConfirmedOn;
	private Date endConfirmedOn;

	private String startDate;
	private String endDate;
}