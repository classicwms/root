package com.tekclover.wms.api.idmaster.model.menuid;

import java.io.Serializable;

import lombok.Data;

@Data
public class MenuIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `MENU_ID`, `SUB_MENU_ID`, `AUT_OBJ_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long menuId;
	private Long subMenuId;
	private Long authorizationObjectId;
	private String languageId;
}
