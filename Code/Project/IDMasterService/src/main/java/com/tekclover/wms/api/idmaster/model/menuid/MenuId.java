package com.tekclover.wms.api.idmaster.model.menuid;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `MENU_ID`, `SUB_MENU_ID`, `AUT_OBJ_ID`, `AUT_OBJ_VALUE`, `LANG_ID`
 */
@Table(
		name = "tblmenuid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_menuid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "MENU_ID", "SUB_MENU_ID", "AUT_OBJ_ID", "AUT_OBJ_VALUE", "LANG_ID"})
				}
		)
@IdClass(MenuIdCompositeKey.class)
public class MenuId { 
	
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
	@Column(name = "MENU_ID") 
	private Long menuId;
	
	@Id
	@Column(name = "SUB_MENU_ID") 
	private Long subMenuId;
	
	@Id
	@Column(name = "AUT_OBJ_ID") 
	private Long authorizationObjectId;
	
	@Id
	@Column(name = "AUT_OBJ_VALUE")
	private String authorizationObjectValue;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "MENU_TEXT") 
	private String menuText;
	
	@Column(name = "SUB_MENU_TEXT") 
	private String subMenuName;
	
	@Column(name = "AUT_OBJ")
	private String menuName;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
	
	@Column(name = "CTD_BY") 
	private String createdBy;
	
	@Column(name = "CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY") 
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();
}
