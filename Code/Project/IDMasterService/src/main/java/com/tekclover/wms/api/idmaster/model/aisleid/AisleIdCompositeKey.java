package com.tekclover.wms.api.idmaster.model.aisleid;

import lombok.Data;

import java.io.Serializable;

@Data
public class AisleIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String floorId;
	private String storageSectionId;
	private String aisleId;
	private String languageId;
}
