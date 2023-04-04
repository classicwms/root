package com.tekclover.wms.api.idmaster.model.handlingunitid;

import lombok.Data;

import java.io.Serializable;

@Data
public class HandlingUnitIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `HU_ID`,`HU_NO`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String handlingUnitId;
	private String handlingUnitNumber;
	private String languageId;
}
