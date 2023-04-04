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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `PROCESS_ID`, `SUB_PROCESS_SEQ_ID`, `LANG_ID`, `PROCESS`, `SUBPROCESS`
 */
@Table(
		name = "tblprocesssequenceid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_processsequenceid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "PROCESS_ID", "SUB_PROCESS_SEQ_ID", "LANG_ID", "PROCESS", "SUBPROCESS"})
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
	
	@Id
	@Column(name = "PROCESS")
	private String processDescription;
	
	@Id
	@Column(name = "SUBPROCESS")
	private String subProcessDescription;
	
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
