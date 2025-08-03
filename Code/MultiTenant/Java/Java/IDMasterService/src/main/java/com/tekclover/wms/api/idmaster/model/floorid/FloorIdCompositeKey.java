package com.tekclover.wms.api.idmaster.model.floorid;

import java.io.Serializable;

import lombok.Data;

@Data
public class FloorIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String languageId;
}
