package com.tekclover.wms.api.idmaster.model.moduleid;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddModuleId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;

	private String moduleId;

	private Long menuId;
	private Long subMenuId;
	private String menuName;
	private String subMenuName;
	private Boolean createUpdate;
	private Boolean delete;
	private Boolean view;
	private Boolean addModule;

	private String languageId;
	private String moduleDescription;
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
}
