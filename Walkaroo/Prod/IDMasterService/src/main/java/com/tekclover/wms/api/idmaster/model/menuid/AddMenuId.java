package com.tekclover.wms.api.idmaster.model.menuid;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMenuId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	@NotNull(message = "Menu Id is mandatory")
	private Long menuId;
	@NotNull(message = "Sub Menu Id is mandatory")
	private Long subMenuId;
	@NotNull(message = "Authorization Object Id is mandatory")
	private Long authorizationObjectId;
	private String languageId;
	private String  authorizationObjectValue;;
	private String menuName;
	private String subMenuName;
	private String authorizationObject;
	private Long deletionIndicator;
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

	private Boolean createUpdate;
	private Boolean delete;
	private Boolean view;

	private Boolean addModule;
}
