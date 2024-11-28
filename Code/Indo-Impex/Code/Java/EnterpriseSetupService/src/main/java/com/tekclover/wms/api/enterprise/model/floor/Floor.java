package com.tekclover.wms.api.enterprise.model.floor;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`
 */
@Table(
		name = "tblfloor", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_floor", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "FL_ID"})
				}
		)
@IdClass(FloorCompositeKey.class)
public class Floor { 
	
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
	@Column(name = "C_TEXT")
	private String description;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;
	
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