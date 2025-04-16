package com.tekclover.wms.api.idmaster.model.movementtypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class MovementTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `MVT_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String movementTypeId;
	private String languageId;
}
