package com.tekclover.wms.api.idmaster.model.sublevelid;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubLevelIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `LEVEL_ID`, `SUB_LEVEL_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long levelId;
	private String subLevelId;
	private String languageId;
}
