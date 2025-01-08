package com.tekclover.wms.api.transaction.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `STR_TYP_ID`, `SEQ_IND`, `ST_NO`
 */
@Table(
		name = "tblstrategies", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_strategies", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "STR_TYP_ID", "SEQ_IND"})
				}
		)
@IdClass(StrategiesCompositeKey.class)
public class Strategies { 
	
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
	@Column(name = "STR_TYP_ID") 
	private Long strategyTypeId;

	@Column(name = "C_TEXT")
	private String description;
	
	@Id
	@Column(name = "SEQ_IND") 
	private Long sequenceIndicator;

	@Column(name = "PRIORITY_1")
	private Long priority1;

	@Column(name = "PRIORITY_2")
	private Long priority2;

	@Column(name = "PRIORITY_3")
	private Long priority3;

	@Column(name = "PRIORITY_4")
	private Long priority4;

	@Column(name = "PRIORITY_5")
	private Long priority5;

	@Column(name = "PRIORITY_6")
	private Long priority6;

	@Column(name = "PRIORITY_7")
	private Long priority7;

	@Column(name = "PRIORITY_8")
	private Long priority8;

	@Column(name = "PRIORITY_9")
	private Long priority9;

	@Column(name = "PRIORITY_10")
	private Long priority10;

	@Column(name = "PRIORITY_DESCRIPTION_1", columnDefinition = "nvarchar(255)")
	private String priorityDescription1;

	@Column(name = "PRIORITY_DESCRIPTION_2", columnDefinition = "nvarchar(255)")
	private String priorityDescription2;

	@Column(name = "PRIORITY_DESCRIPTION_3", columnDefinition = "nvarchar(255)")
	private String priorityDescription3;

	@Column(name = "PRIORITY_DESCRIPTION_4", columnDefinition = "nvarchar(255)")
	private String priorityDescription4;

	@Column(name = "PRIORITY_DESCRIPTION_5", columnDefinition = "nvarchar(255)")
	private String priorityDescription5;

	@Column(name = "PRIORITY_DESCRIPTION_6", columnDefinition = "nvarchar(255)")
	private String priorityDescription6;

	@Column(name = "PRIORITY_DESCRIPTION_7", columnDefinition = "nvarchar(255)")
	private String priorityDescription7;

	@Column(name = "PRIORITY_DESCRIPTION_8", columnDefinition = "nvarchar(255)")
	private String priorityDescription8;

	@Column(name = "PRIORITY_DESCRIPTION_9", columnDefinition = "nvarchar(255)")
	private String priorityDescription9;

	@Column(name = "PRIORITY_DESCRIPTION_10", columnDefinition = "nvarchar(255)")
	private String priorityDescription10;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

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