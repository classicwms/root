package com.tekclover.wms.api.idmaster.model.moduleid;

import java.io.Serializable;

import lombok.Data;

@Data
public class ModuleIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `MOD_ID`,`LANG_ID`,`MENU_ID`,`SUB_MENU_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String moduleId;
	private String languageId;
	private Long menuId;
	private Long subMenuId;
}
