package com.tekclover.wms.api.enterprise.model.storagetype;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_CL_ID`, `ST_TYP_ID`
 */
@Table(
		name = "tblstoragetype", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_storagetype", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ST_CL_ID", "ST_TYP_ID"})
				}
		)
@IdClass(StorageTypeCompositeKey.class)
public class StorageType { 
	
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
	
	@Id
	@Column(name = "ST_TYP_ID") 
	private Long storageTypeId;

	@Column(name = "C_TEXT")
	private String description;
	
	@Column(name = "ST_TEMP_FROM") 
	private String storageTemperatureFrom;
	
	@Column(name = "ST_TEMP_TO")
	private String storageTemperatureTo;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

	@Column(name="STORAGE_CLASS_ID_DESC")
	private String storageClassIdAndDescription;

	@Column(name = "ST_UOM") 
	private String storageUom;
	
	@Column(name = "CAP_CHECK") 
	private Short capacityCheck;
	
	@Column(name = "CAP_BY_WEIGHT") 
	private Short capacityByWeight;
	
	@Column(name = "CAP_BY_QTY") 
	private Short capacityByQty;
	
	@Column(name = "MIX_STOCK") 
	private Short mixToStock;
	
	@Column(name = "ADD_EXT_STOCK")
	private Short addToExistingStock;
	
	@Column(name = "RET_ST_TYP") 
	private Short returnToSameStorageType;
	
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
