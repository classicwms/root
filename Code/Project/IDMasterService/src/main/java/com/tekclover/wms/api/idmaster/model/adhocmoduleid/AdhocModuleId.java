package com.tekclover.wms.api.idmaster.model.adhocmoduleid;

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
 * `C_ID`,`PLANT_ID`, `WH_ID`, `MOD_ID`,`ADHOC_MOD_ID`,`LANG_ID`
 */
@Table(
		name = "tbladhocmoduleid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_adhocmoduleid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "MOD_ID","ADHOC_MOD_ID", "LANG_ID"})
				}
		)
@IdClass(AdhocModuleIdCompositeKey.class)
public class AdhocModuleId { 
	
	
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
	@Column(name = "MOD_ID", columnDefinition = "nvarchar(25)")
	private String moduleId;

	@Id
	@Column(name = "ADHOC_MOD_ID", columnDefinition = "nvarchar(25)")
	private String adhocModuleId;
		
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(5)") 
	private String languageId;
	
	@Column(name = "ADHOC_MODULE_TEXT", columnDefinition = "nvarchar(500)")	
	private String adhocModuleDescription;	
	
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
