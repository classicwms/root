package com.tekclover.wms.api.idmaster.model.levelid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `LEVEL_ID`, `LANG_ID`, `LEVEL`
 */
@Table(
		name = "tbllevelid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_levelid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "LEVEL_ID", "LANG_ID", "LEVEL"})
				}
		)
@IdClass(LevelIdCompositeKey.class)
public class LevelId { 
	
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
	@Column(name = "LEVEL_ID")
	private Long levelId;
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Id
	@Column(name = "LEVEL") 
	private String level;
	
	@Column(name = "LEVEL_REF") 
	private String levelReference;
	
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
