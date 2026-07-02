package com.tekclover.wms.api.idmaster.model.numberrange;

import lombok.Data;

@Data
public class AddNumberRange {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long numberRangeCode;
	private Long fiscalYear;
	private String numberRangeObject;
	private Long numberRangeFrom;
	private Long numberRangeTo;
	private String numberRangeCurrent;
	private Long deletionIndicator;

}
