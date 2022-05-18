package com.tekclover.wms.api.enterprise.model.strategies;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `STR_TYP_ID`, `SEQ_IND`, `ST_NO`, `PRIORITY`
 */
@Table(
		name = "tblstrategies", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_strategies", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "STR_TYP_ID", "SEQ_IND", "ST_NO", "PRIORITY"})
				}
		)
@IdClass(StrategiesCompositeKey.class)
public class Strategies { 
	
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
	@Column(name = "STR_TYP_ID") 
	private Long strategyTypeId;
	
	@Id
	@Column(name = "SEQ_IND") 
	private Long sequenceIndicator;
	
	@Id
	@Column(name = "ST_NO") 
	private String strategyNo;
	
	@Id
	@Column(name = "PRIORITY") 
	private Long priority;
	
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
