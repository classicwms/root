package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class PickerDenialRemarkSummary {

	private String remarks;
	private Long denialCount;

}