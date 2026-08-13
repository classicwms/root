package com.tekclover.wms.core.model.idmaster;
import lombok.Data;

@Data
public class AddModuleId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String moduleId;
	private String languageId;
	private Long menuId;
	private Long subMenuId;
	private String menuName;
	private String subMenuName;
	private Boolean createUpdate;
	private Boolean delete;
	private Boolean view;
	private Boolean addModule;
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
