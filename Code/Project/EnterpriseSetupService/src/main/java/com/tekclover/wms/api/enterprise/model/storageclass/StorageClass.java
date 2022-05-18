package com.tekclover.wms.api.enterprise.model.storageclass;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_CL_ID`
 */
@Table(
		name = "tblstorageclass", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_storageclass", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ST_CL_ID"})
				}
		)
@IdClass(StorageClassCompositeKey.class)
public class StorageClass { 
	
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
	@Column(name = "ST_CL_ID")
	private Long storageClassId;
	
	@Column(name = "HAZ_MAT_CLASS")
	private String hazardMaterialClass;
	
	@Column(name = "WAT_POL_CLASS")
	private String waterPollutionClass;
	
	@Column(name = "REMARK") 
	private String remarks;
	
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
