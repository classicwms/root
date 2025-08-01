package com.tekclover.wms.api.masters.model.imalternateuom;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `UOM_ID`, `ALT_UOM`
 */
@Table(
		name = "tblimalternateuom", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_imalternateuom", 
						columnNames = {"ID","LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE", "UOM_ID", "ALT_UOM"})
				}
		)
@IdClass(ImAlternateUomCompositeKey.class)
public class ImAlternateUom {

	@Id
	@Column(name = "ID")
	private Long id;
	
	@Id
	@Column(name = "ALT_UOM") 
	private String alternateUom;
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
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
	@Column(name = "ITM_CODE") 
	private String itemCode;
	
	@Id
	@Column(name = "UOM_ID") 
	private String uomId;

	@Column(name = "S_NO") 
	private Long slNo;
	
	@Column(name = "FRM_UNT") 
	private Long fromUnit;
	
	@Column(name = "TO_UNT") 
	private Long toUnit;
	
	@Column(name = "QPC_01") 
	private Double qpc20Ft;
	
	@Column(name = "QPC_02") 
	private Double qpc40Ft;
	
	@Column(name = "ALT_UOM_BAR") 
	private String alternateUomBarcode;
	
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

	@Column(name = "UOM_QTY")
	private Double uomIdQty;

	@Column(name = "ALT_UOM_QTY")
	private Double alternateUomQty;

}
