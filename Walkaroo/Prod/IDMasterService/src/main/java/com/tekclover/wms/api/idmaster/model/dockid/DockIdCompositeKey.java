package com.tekclover.wms.api.idmaster.model.dockid;

import lombok.Data;

import java.io.Serializable;

@Data
public class DockIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `DK_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String dockId;
	private String languageId;
}
