package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateRoleAccess {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
//	private Long userRoleId;
	private Long roleId;
	private Long menuId;
	private Long subMenuId;
	private Long authorizationObjectId;
	private String authorizationObjectValue;
	private String userRoleName;
	private String menuName;
	private String subMenuName;
	private String description;
	private Long statusId;
	private String moduleId;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Long deletionIndicator;
	private Boolean createUpdate;
	private Boolean edit;
	private Boolean view;
	private Boolean delete;
}
