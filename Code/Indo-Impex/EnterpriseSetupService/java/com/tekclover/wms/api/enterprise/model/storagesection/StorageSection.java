package com.tekclover.wms.api.enterprise.model.storagesection;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `ST_SEC_ID`
 */
@Table(
		name = "tblstoragesection", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_storagesection", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "FL_ID", "ST_SEC_ID"})
				}
		)
@IdClass(StorageSectionCompositeKey.class)
public class StorageSection { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
	private String warehouseId;
	
	@Id
	@Column(name = "FL_ID")
	private Long floorId;
	
	@Id
	@Column(name = "ST_SEC_ID") 
	private String storageSectionId;
	
	@Column(name = "ST_TYP_ID") 
	private Long storageTypeId;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

	@Column(name="FLOOR_ID_DESC")
	private String floorIdAndDescription;
	
	@Column(name = "ROW_CNT") 
	private Long noRows;
	
	@Column(name = "AISLE_CNT") 
	private Long noAisles;
	
	@Column(name = "SPAN_CNT") 
	private Long noSpan;
	
	@Column(name = "SHELF_CNT") 
	private Long noShelves;
	
	@Column(name = "ST_BIN_CNT") 
	private Long noStorageBins;
	
	@Column(name = "ITM_TYPE_ID")
	private Long itemTypeId;
	
	@Column(name = "ITM_GRP_ID") 
	private Long itemGroupId;
	
	@Column(name = "SUB_ITM_GRP_ID") 
	private Long subItemGroup;
	@Column(name = "C_TEXT")
	private String description;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
	
	@Column(name = "CTD_BY", columnDefinition = "nvarchar(100)") 
	private String createdBy;
	
	@Column(name = "CTD_ON")
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY", columnDefinition = "nvarchar(100)") 
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}