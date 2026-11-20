package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchBinningProductivityReport {

	private List<String> warehouseId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> refDocNo;
	private List<String> preInboundNo;
	private List<String> binner;
	private List<Long> inboundOrderTypeId;
	private List<Long> statusId;

	private Date startConfirmedOn;
	private Date endConfirmedOn;

	private String startDate;
	private String endDate;
}