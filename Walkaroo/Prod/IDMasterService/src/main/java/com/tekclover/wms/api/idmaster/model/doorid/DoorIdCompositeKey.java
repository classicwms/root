package com.tekclover.wms.api.idmaster.model.doorid;

import java.io.Serializable;

import lombok.Data;

@Data
public class DoorIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `DOOR_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String doorId;
	private String languageId;
}
