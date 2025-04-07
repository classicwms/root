package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class PickerDenialReport {

	List<PickerDenialSummary> summaryList;

	List<PickerDenialHeader> headers;
}