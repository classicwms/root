package com.tekclover.wms.api.idmaster.model.palletizationlevelid;

import java.io.Serializable;

import lombok.Data;

@Data
public class PalletizationLevelIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `PAL_LVL_ID`,`PAL_LVL`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String palletizationLevelId;
	private String palletizationLevel;
	private String languageId;
}
