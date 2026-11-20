package com.tekclover.wms.api.idmaster.model.numberrange;

import lombok.Data;

@Data
public class UpdateNumberRange {

	private Long fiscalYear;
	private String numberRangeObject;
	private Long numberRangeFrom;
	private Long numberRangeTo;
	private String numberRangeCurrent;
	private Long deletionIndicator;
}
