package com.tekclover.wms.api.masters.model.itemgroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`, `ITM_GRP_ID`, `SUB_ITM_GRP_ID`
 */
@Table(
		name = "tblitemgroup", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_itemgroup", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_TYPE_ID","ITM_GRP_ID","SUB_ITM_GRP_ID"})
				}
		)
@IdClass(ItemGroupCompositeKey.class)
public class ItemGroup { 
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;

	@Id
	@Column(name = "C_ID") 
	private String companyId;
	
	@Id
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
	@Column(name = "ITM_TYPE_ID") 
	private Long itemTypeId;
	
	@Id
	@Column(name = "ITM_GRP_ID")
	private Long itemGroupId;
	
	@Id
	@Column(name = "SUB_ITM_GRP_ID") 
	private Long subItemGroupId;
	
	@Column(name = "ST_CL_ID") 
	private Long storageClassId;
	
	@Column(name = "ST_SEC_ID") 
	private String storageSectionId;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

	@Column(name="ITEM_TYP_DESC")
	private String itemTypeIdAndDescription;

	@Column(name="SUB_ITM_ID_DESC")
	private String subItemGroupIdAndDescription;

	@Column(name = "C_TEXT")
	private String description;
	
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
