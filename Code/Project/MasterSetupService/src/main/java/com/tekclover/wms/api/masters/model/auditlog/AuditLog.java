package com.tekclover.wms.api.masters.model.auditlog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblAuditLog")
public class AuditLog { 
	
	@Id
	@Column(name = "AUD_FILE_NO") 
	private String auditFileNumber;
	
	@Column(name = "LANG_ID")	
	private String languageId;
	
	@Column(name = "C_ID")	
	private String companyCodeId;
	
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_ID") 
	private String warehouseId;	
	
	@Column(name = "FISCAL_YEAR") 
	private Long financialYear;
	
	@Column(name = "OBJ_NM") 
	private String objectName;
	
	@Column(name = "SCREEN_NO") 
	private String screennumber;
	
	@Column(name = "SUB_SCREEN_NO") 
	private String subScreenNumber;
	
	@Column(name = "TABLE_NM") 
	private String modifiedTableName;
	
	@Column(name = "MOD_FIELD") 
	private String modifiedField;
	
	@Column(name = "OLD_VL") 
	private String oldValue;
	
	@Column(name = "NEW_VL") 
	private String newValue;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
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
	
	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "UTD_BY")
	private String UpdatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
