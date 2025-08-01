package com.tekclover.wms.api.idmaster.model.controltypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class ControlTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `CTRL_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String controlTypeId;
	private String languageId;
}
