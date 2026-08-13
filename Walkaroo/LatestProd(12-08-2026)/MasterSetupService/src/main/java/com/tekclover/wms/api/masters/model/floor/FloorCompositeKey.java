package com.tekclover.wms.api.masters.model.floor;

import lombok.Data;

import java.io.Serializable;

@Data
public class FloorCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
}
