package com.tekclover.wms.api.idmaster.model.usertypeid;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `USR_TYP_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long userTypeId;
	private String languageId;
}
