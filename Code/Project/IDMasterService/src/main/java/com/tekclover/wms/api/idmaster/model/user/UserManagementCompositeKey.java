package com.tekclover.wms.api.idmaster.model.user;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserManagementCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * LANG_ID	C_ID	PLANT_ID	WH_ID	USR_ID	USR_ROLE_ID
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String userId;
	private Long userRoleId;
}
