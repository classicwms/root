package com.tekclover.wms.api.idmaster.model.processsequenceid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `PROCESS_ID`, `SUB_PROCESS_SEQ_ID`, `LANG_ID`
 */
@Table(
		name = "tblprocesssequenceid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_processsequenceid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "PROCESS_ID", "SUB_PROCESS_SEQ_ID", "LANG_ID"})
				}
		)
@IdClass(ProcessSequenceIdCompositeKey.class)
public class ProcessSequenceId { 
	
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
	@Column(name = "PROCESS_ID")
	private Long processId;
	
	@Id
	@Column(name = "SUB_PROCESS_SEQ_ID") 
	private Long subLevelId;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;

	@Column(name = "PROCESS")
	private String processDescription;

	@Column(name = "SUBPROCESS")
	private String subProcessDescription;
	
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
