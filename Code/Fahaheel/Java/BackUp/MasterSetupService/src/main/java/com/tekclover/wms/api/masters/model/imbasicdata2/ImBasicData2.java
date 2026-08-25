package com.tekclover.wms.api.masters.model.imbasicdata2;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`
 */
@Table(
		name = "tblimbasicdata2", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_imbasicdata2", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE"})
				}
		)
@IdClass(ImBasicData2CompositeKey.class)
public class ImBasicData2 { 
	
	@Id
	@Column(name = "ITM_CODE") 
	private String itemCode;
	
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
	
	@Column(name = "ITM_BARCODE") 
	private String itemBarcode;
	
	@Column(name = "LENGTH") 
	private Double length;
	
	@Column(name = "WIDTH") 
	private Double width;
	
	@Column(name = "HEIGHT") 
	private Double height;
	
	@Column(name = "DIM_UOM") 
	private String dimensionUom;
	
	@Column(name = "VOLUME") 
	private Double volume;
	
	@Column(name = "VOL_UOM") 
	private String volumeUom;
	
	@Column(name = "QPC_01") 
	private Double qpc20Ft;
	
	@Column(name = "QPC_02") 
	private Double qpc40Ft;
	
	@Column(name = "UNIT_PR") 
	private Double unitPrice;
	
	@Column(name = "CURR_ID") 
	private Long currencyId;
	
	@Column(name = "GR_WT") 
	private Double grossWeight;
	
	@Column(name = "NT_WT") 
	private Double netWeight;
	
	@Column(name = "WT_UOM") 
	private String weightUom;
	
	@Column(name = "PREF_ST_BIN") 
	private String preferredStorageBin;
	
//	@Column(name = "IMAGE") 
//	private Object photo;
	
	@Column(name = "IT_CL") 
	private String itemClass;
	
	@Column(name = "CROSS_DK_IND") 
	private Boolean crossDockingIndicator;
	
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
