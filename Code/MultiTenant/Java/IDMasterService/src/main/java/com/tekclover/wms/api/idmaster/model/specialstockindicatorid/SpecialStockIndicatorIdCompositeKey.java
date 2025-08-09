package com.tekclover.wms.api.idmaster.model.specialstockindicatorid;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecialStockIndicatorIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `STCK_TYP_ID`,`SP_ST_IND_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String stockTypeId;
	private String specialStockIndicatorId;
	private String languageId;
}
