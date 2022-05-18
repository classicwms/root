package com.tekclover.wms.core.model.idmaster;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class RoleAccess {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long userRoleId;
	private Long menuId;
	private Long subMenuId;
	private Long authorizationObjectId;
	private String authorizationObjectValue;
	private String userRoleName;
	private String description;
	private Long statusId;
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
	private Integer create;
	private Integer edit;
	private Integer view;
	private Integer delete;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
