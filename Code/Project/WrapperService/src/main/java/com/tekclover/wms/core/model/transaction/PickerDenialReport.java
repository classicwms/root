package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PickerDenialReport {

	List<PickerDenialSummary> summaryList;

	List<PickerDenialHeader> headers;
}