package com.tekclover.wms.api.idmaster.model.adhocmoduleid;

import java.io.Serializable;

import lombok.Data;

@Data
public class AdhocModuleIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `MOD_ID`,`ADHOC_MOD_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String moduleId;
	private String adhocModuleId;
	private String languageId;
}
