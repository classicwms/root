package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class PickerDenialRemarkSummary {

	private String remarks;
	private Long denialCount;

}