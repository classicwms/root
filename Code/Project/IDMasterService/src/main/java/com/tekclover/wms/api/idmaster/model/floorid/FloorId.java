package com.tekclover.wms.api.idmaster.model.floorid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `LANG_ID`
 */
@Table(
		name = "tblfloorid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_floorid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "FL_ID", "LANG_ID"})
				}
		)
@IdClass(FloorIdCompositeKey.class)
public class FloorId { 
	
	@Id
	@Column(name = "C_ID")
	private String companyCodeId;
	
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_ID") 
	private String warehouseId;
	
	@Column(name = "FL_ID")
	private Long floorId;
	
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Column(name = "FL_TEXT") 
	private String description;
	
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
