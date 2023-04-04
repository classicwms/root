package com.tekclover.wms.api.idmaster.model.sublevelid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `LEVEL_ID`, `SUB_LEVEL_ID`, `LANG_ID`
 */
@Table(
		name = "tblsublevelid",
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_sublevelid",
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "LEVEL_ID", "SUB_LEVEL_ID", "LANG_ID"})
				}
		)
@IdClass(SubLevelIdCompositeKey.class)
public class SubLevelId {
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(50)")
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
	private String warehouseId;
	
	@Id
	@Column(name = "LEVEL_ID")
	private String levelId;
	
	@Id
	@Column(name = "SUB_LEVEL_ID")
	private String subLevelId;
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(5)")
	private String languageId;

	@Column(name = "SUB_LEVEL", columnDefinition = "nvarchar(50)")
	private String subLevel;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
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
	
	@Column(name = "CTD_BY") 
	private String createdBy;
	
	@Column(name = "CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY") 
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();
}
