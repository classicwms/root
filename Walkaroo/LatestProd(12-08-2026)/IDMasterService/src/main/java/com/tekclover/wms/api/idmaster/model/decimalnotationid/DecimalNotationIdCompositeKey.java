package com.tekclover.wms.api.idmaster.model.decimalnotationid;

import lombok.Data;

import java.io.Serializable;

@Data
public class DecimalNotationIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `DEC_NOT_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String decimalNotationId;
	private String languageId;
}
