package com.tekclover.wms.api.idmaster.model.enterprise;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `IMP_MTD`, `WH_TYP_ID`
 */
@Table(
		name = "tblwarehouse", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_warehouse", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "IMP_MTD", "WH_TYP_ID"})
				}
		)
@IdClass(WarehouseCompositeKey.class)
public class WarehouseEnterprise { 
	
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
	@Column(name = "IMP_MTD")
	private String modeOfImplementation;
	
	@Id
	@Column(name = "WH_TYP_ID") 
	private Long warehouseTypeId;
	
	@Column(name = "IB_QA_CHK") 
	private Boolean inboundQaCheck;
	
	@Column(name = "OB_QA_CHK") 
	private Boolean outboundQaCheck;
	
	@Column(name = "ZONE") 
	private String zone;
	
	@Column(name = "ADD_1")
	private String address1;
	
	@Column(name = "ADD_2") 
	private String address2;
	
	@Column(name = "CITY") 
	private String city;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "COUNTRY") 
	private String country;
	
	@Column(name = "ZIP_CODE") 
	private Long zipCode;
	
	@Column(name = "CNT_NM") 
	private String contactName;
	
	@Column(name = "DESIG") 
	private String desigination;
	
	@Column(name = "PH_NO")
	private String phoneNumber;
	
	@Column(name = "MAIL_ID")
	private String emailId;
	
	@Column(name = "LENGTH") 
	private Double length;
	
	@Column(name = "WIDTH") 
	private Double width;
	
	@Column(name = "TOT_AREA")
	private Double totalArea;
	
	@Column(name = "AREA_UOM") 	
	private String uom;
	
	@Column(name = "AISLE_CNT")
	private Long noAisles;
	
	@Column(name = "LATITUDE") 
	private Double lattitude;
	
	@Column(name = "LONGITUDE")
	private Double longitude;
	
	@Column(name = "ST_MTD") 
	private String storageMethod;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
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
	
	@Column(name = "CTD_BY") 
	private String createdBy;
	
	@Column(name = "CTD_ON")	
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();
}
