package com.tekclover.wms.api.idmaster.model.rowid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `ST_SEC_ID`, `ROW_ID`, `LANG_ID`
 */
@Table(
		name = "tblrowid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_rowid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "FL_ID", "ST_SEC_ID", "ROW_ID", "LANG_ID"})
				}
		)
@IdClass(RowIdCompositeKey.class)
public class RowId { 
	
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
	@Column(name = "FL_ID")
	private Long floorId;
	
	@Id
	@Column(name = "ST_SEC_ID")
	private String storageSectionId;
	
	@Id
	@Column(name = "ROW_ID") 
	private String rowId;
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Column(name = "ROW_TEXT")
	private String rowNumber;
	
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
