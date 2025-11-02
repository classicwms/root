package com.tekclover.wms.api.inbound.orders.model.numberrange;

import lombok.Data;

import java.io.Serializable;

@Data
public class NumberRangeCompositeKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long numberRangeCode;
//	private Long fiscalYear;
}
