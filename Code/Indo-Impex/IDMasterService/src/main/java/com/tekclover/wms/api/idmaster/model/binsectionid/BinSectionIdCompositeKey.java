package com.tekclover.wms.api.idmaster.model.binsectionid;

import lombok.Data;

import java.io.Serializable;

@Data
public class BinSectionIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `BIN_SECTION_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String binSectionId;
	private String languageId;
}
