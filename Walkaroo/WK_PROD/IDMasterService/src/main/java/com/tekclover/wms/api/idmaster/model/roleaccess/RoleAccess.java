package com.tekclover.wms.api.idmaster.model.roleaccess;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `USR_ROLE_ID`, `MENU_ID`, `SUB_MENU_ID`
 */
@Table(
		name = "tblroleaccess", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_roleaccess", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "USR_ROLE_ID", "MENU_ID", "SUB_MENU_ID"})
				}
		)
@IdClass(RoleAccessCompositeKey.class)
public class RoleAccess { 
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Id
	@Column(name = "C_ID") 
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID") 
	private String warehouseId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "USR_ROLE_ID")
	private Long userRoleId = 0L;
	
	@Id
	@Column(name = "MENU_ID") 
	private Long menuId;
	
	@Id
	@Column(name = "SUB_MENU_ID") 
	private Long subMenuId;

	@Column(name="ROLE_ID")
	private Long roleId;

	@Column(name = "MOD_ID", columnDefinition = "nvarchar(25)")
	private String moduleId;
	
	@Column(name = "AUT_OBJ_ID") 
	private Long authorizationObjectId;
	
	@Column(name = "AUT_OBJ_VALUE")
	private String authorizationObjectValue;
	
	@Column(name = "USR_ROLE_NM") 
	private String userRoleName;

	@Column(name="MENU_NAME",columnDefinition = "nvarchar(100)")
	private String menuName;

	@Column(name="SUB_MENU_NAME",columnDefinition = "nvarchar(100)")
	private String subMenuName;
	
	@Column(name = "USR_ROLE_TEXT") 
	private String description;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;
	
	@Column(name = "REF_FIELD_1") 
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2")
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3") 
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4") 
	private String referenceField4;
	
	@Column(name = "REF_FIELD_5") 
	private String referenceField5;
	
	@Column(name = "REF_FIELD_6") 
	private String referenceField6;
	
	@Column(name = "REF_FIELD_7") 
	private String referenceField7;
	
	@Column(name = "REF_FIELD_8")
	private String referenceField8;
	
	@Column(name = "REF_FIELD_9") 
	private String referenceField9;
	
	@Column(name = "REF_FIELD_10") 
	private String referenceField10;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "CREATE_ACCESS") 
	private Boolean createUpdate;
	
	@Column(name = "EDIT_ACCESS") 
	private Boolean edit;
	
	@Column(name = "VIEW_ACCESS") 
	private Boolean view;
	
	@Column(name = "DELETE_ACCESS")
	private Boolean delete;
	
	@Column(name = "CTD_BY") 
	private String createdBy;
	
	@Column(name = "CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();

}
