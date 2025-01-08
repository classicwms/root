package com.tekclover.wms.api.transaction.model.mnc;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `TR_NO`,`TR_TYP_ID`
 */
@Table(
		name = "tblinhousetransferheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_inhousetransferheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "TR_NO", "TR_TYP_ID"})
				}
		)
@IdClass(InhouseTransferHeaderCompositeKey.class)
public class InhouseTransferHeader { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)") 
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
	private String warehouseId;
	
	@Id
	@Column(name = "TR_NO")	
	private String transferNumber;
	
	@Id
	@Column(name = "TR_TYP_ID")	
	private Long transferTypeId;
	
	@Column(name = "TR_MTD") 
	private String transferMethod;	
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "REMARK") 
	private String remarks;	
	
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
	
	@Column(name = "IT_CTD_BY")
	private String createdBy;

	@Column(name = "IT_CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "IT_UTD_BY")
    private String updatedBy;

	@Column(name = "IT_UTD_ON")
	private Date updatedOn = new Date();

	//Almailem Code
	@Column(name = "MFR_NAME")
	private String manufacturerName;

	@Column(name = "STATUS_TEXT")
	private String statusDescription;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;
}