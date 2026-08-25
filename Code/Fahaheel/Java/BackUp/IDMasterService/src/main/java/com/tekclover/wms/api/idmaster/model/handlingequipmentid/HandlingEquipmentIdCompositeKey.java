package com.tekclover.wms.api.idmaster.model.handlingequipmentid;

import lombok.Data;

import java.io.Serializable;

@Data
public class HandlingEquipmentIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `HE_ID`,`HE_NO`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long handlingEquipmentNumber;
	private String languageId;
}
