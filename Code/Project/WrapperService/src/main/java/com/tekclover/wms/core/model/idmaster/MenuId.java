package com.tekclover.wms.core.model.idmaster;

import java.util.Date;

import lombok.Data;

@Data
public class MenuId { 
	
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long menuId;
	private Long subMenuId;
	private Long authorizationObjectId;
	private String authorizationObjectValue;
	private String languageId;
	private String menuText;
	private String subMenuName;
	private String menuName;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
