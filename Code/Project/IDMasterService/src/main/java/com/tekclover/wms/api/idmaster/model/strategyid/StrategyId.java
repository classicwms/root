package com.tekclover.wms.api.idmaster.model.strategyid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `STR_TYP_ID`, `ST_NO`, `LANG_ID`
 */
@Table(
		name = "tblstrategyid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_strategyid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "STR_TYP_ID", "ST_NO", "LANG_ID"})
				}
		)
@IdClass(StrategyIdCompositeKey.class)
public class StrategyId { 
	
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
	@Column(name = "STR_TYP_ID") 
	private Long strategyTypeId;
	
	@Id
	@Column(name = "ST_NO")
	private String strategyNo;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "STR_TYP_TEXT")
	private String strategyTypeText;
	
	@Column(name = "ST_NO_TEXT") 
	private String strategyText;
	
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
