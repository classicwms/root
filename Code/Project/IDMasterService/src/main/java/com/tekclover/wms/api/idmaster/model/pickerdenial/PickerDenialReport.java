package com.tekclover.wms.api.idmaster.model.pickerdenial;

import lombok.Data;

import java.util.List;

@Data
public class PickerDenialReport {

	List<PickerDenialSummary> summaryList;

	List<PickerDenialHeader> headers;
}