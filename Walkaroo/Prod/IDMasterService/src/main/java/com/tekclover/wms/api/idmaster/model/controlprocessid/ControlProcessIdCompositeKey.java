package com.tekclover.wms.api.idmaster.model.controlprocessid;

import lombok.Data;

import java.io.Serializable;

@Data
public class ControlProcessIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `CTRL_PROCESS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String controlProcessId;
	private String languageId;
}
