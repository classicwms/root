package com.tekclover.wms.core.model.transaction;

import lombok.Data;

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

	private String startDate;
	private String endDate;
}