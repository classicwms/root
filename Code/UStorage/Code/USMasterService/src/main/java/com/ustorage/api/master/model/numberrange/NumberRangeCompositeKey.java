package com.ustorage.api.master.model.numberrange;

import java.io.Serializable;

import lombok.Data;

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
	private Long fiscalYear;
	private String numberRangeObject;
}
