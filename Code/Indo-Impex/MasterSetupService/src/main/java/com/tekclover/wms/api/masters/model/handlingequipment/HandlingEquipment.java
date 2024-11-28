package com.tekclover.wms.api.masters.model.handlingequipment;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `HE_ID`
 */
@Table(
		name = "tblhandlingequipment", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_handlingequipment", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "HE_ID"})
				}
		)
@IdClass(HandlingEquipmentCompositeKey.class)
public class HandlingEquipment { 
	
	@Id
	@Column(name = "HE_ID") 
	private String handlingEquipmentId;
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)") 
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
	private String warehouseId;	
	
	@Column(name = "CATEGORY") 
	private String category;
	
	@Column(name = "HU_UNIT") 
	private String handlingUnit;
	
	@Column(name = "AQU_DT") 
	private Date acquistionDate;
	
	@Column(name = "AQU_VAL") 
	private Double acquistionValue;
	
	@Column(name = "CURR_ID") 
	private Long currencyId;
	
	@Column(name = "MOD_NO") 
	private String modelNo;
	
	@Column(name = "MFR_PART") 
	private String manufacturerPartNo;
	
	@Column(name = "CTY_ORG") 
	private String countryOfOrigin;
	
	@Column(name = "HE_BAR") 
	private String heBarcode;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "REF_FIELD_1") 
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2") 
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3") 
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4") 
	private String referenceField4;
	
	@Column(name = "REF_FIELD_5") 
	private String referenceField5;
	
	@Column(name = "REF_FIELD_6") 
	private String referenceField6;
	
	@Column(name = "REF_FIELD_7") 
	private String referenceField7;
	
	@Column(name = "REF_FIELD_8") 
	private String referenceField8;
	
	@Column(name = "REF_FIELD_9") 
	private String referenceField9;
	
	@Column(name = "REF_FIELD_10") 
	private String referenceField10;
	
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