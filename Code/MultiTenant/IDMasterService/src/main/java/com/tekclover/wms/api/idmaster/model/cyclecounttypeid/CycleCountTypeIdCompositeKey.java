package com.tekclover.wms.api.idmaster.model.cyclecounttypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class CycleCountTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `CC_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String cycleCountTypeId;
	private String languageId;
}
