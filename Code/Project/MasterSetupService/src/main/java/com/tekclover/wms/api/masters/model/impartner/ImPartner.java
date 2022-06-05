package com.tekclover.wms.api.masters.model.impartner;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `PARTNER_TYP`, `PARTNER_CODE`
 */
@Table(
		name = "tblimpartner", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_impartner", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE", "PARTNER_TYP", "PARTNER_CODE"})
				}
		)
@IdClass(ImPartnerCompositeKey.class)
public class ImPartner { 
	
	@Id
	@Column(name = "PARTNER_CODE") 
	private String businessPartnerCode;
	
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
	@Column(name = "PARTNER_TYP") 
	private String businessParnterType;
	
	@Column(name = "PARTNER_ITM_CODE") 
	private String vendorItemNo;
	
	@Column(name = "PARTNER_ITM_BAR") 
	private String vendorItemBarcode;
	
	@Column(name = "MFR_BAR") 
	private String mfrBarcode;
	
	@Column(name = "BRND_NM") 
	private String brandName;
	
	@Column(name = "STK") 
	private Double stock;
	
	@Column(name = "STK_UOM") 
	private String stockUom;
	
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
