package com.tekclover.wms.api.enterprise.model.storagebintype;

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
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_TYP_ID`, `ST_BIN_TYP_ID`
 */
@Table(
		name = "tblstoragebintype", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_storagebintype", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ST_TYP_ID", "ST_BIN_TYP_ID"})
				}
		)
@IdClass(StorageBinTypeCompositeKey.class)
public class StorageBinType { 
	
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
	@Column(name = "ST_TYP_ID")
	private Long storageTypeId;
	@Id
	@Column(name = "ST_BIN_TYP_ID") 
	private Long storageBinTypeId;

	@Column(name="ST_CL_ID")
	private Long storageClassId;

	@Column(name = "C_TEXT")
	private String description;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

	@Column(name="ST_CLASS_ID_DESC")
	private String storageClassIdAndDescription;

	@Column(name="ST_TYP_ID_DESC")
	private String storageTypeIdAndDescription;

	@Column(name = "LENGTH")
	private Double length;
	
	@Column(name = "WIDTH") 
	private Double width;
	
	@Column(name = "HEIGHT") 
	private Double height;
	
	@Column(name = "DIM_UOM") 
	private String dimensionUom;
	
	@Column(name = "TOT_VOL") 
	private Double totalVolume;
	
	@Column(name = "VOL_UOM") 
	private String volumeUom;
	
	@Column(name = "ST_BIN_TYP_BLK") 
	private Boolean storageBinTypeBlock;
	
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