package com.tekclover.wms.api.masters.model.businesspartner;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PARTNER_TYP`, `PARTNER_CODE`
 */
@Table(
		name = "tblBusinessPartner", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_businesspartner", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PARTNER_TYP", "PARTNER_CODE"})
				}
		)
@IdClass(BusinessPartnerCompositeKey.class)
public class BusinessPartner { 
	
	@Id
	@Column(name = "PARTNER_CODE") 
	private String partnerCode;
	
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
	@Column(name = "PARTNER_TYP") 
	private Long businessPartnerType;	
	
	@Column(name = "PARTNER_NM") 
	private String partnerName;
	
	@Column(name = "ADD_1") 
	private String address1;
	
	@Column(name = "ADD_2") 
	private String address2;
	
	@Column(name = "ZONE") 
	private String zone;
	
	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "STATE") 
	private String state;
	
	@Column(name = "PH_NO") 
	private String phoneNumber;
	
	@Column(name = "FX_NO") 
	private String faxNumber;
	
	@Column(name = "MAIL_ID") 
	private String emailId;
	
	@Column(name = "REF_TXT") 
	private String referenceText;
	
	@Column(name = "LOCATION") 
	private String location;
	
	@Column(name = "LATITUDE")
	private Double lattitude;
	
	@Column(name = "LONGITUDE") 
	private Double longitude;
	
	@Column(name = "ST_TYP_ID") 
	private Long storageTypeId;
	
	@Column(name = "ST_BIN") 
	private String storageBin;
	
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
