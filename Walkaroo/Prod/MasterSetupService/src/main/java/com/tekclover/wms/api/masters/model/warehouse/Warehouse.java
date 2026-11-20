package com.tekclover.wms.api.masters.model.warehouse;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `IMP_MTD`, `WH_TYP_ID`
 */
@Table(
		name = "tblwarehouse", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_warehouse", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "WH_TYP_ID"})
				}
		)
@IdClass(WarehouseCompositeKey.class)
public class Warehouse { 
	
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
	
//	@Id
	@Column(name = "IMP_MTD")
	private String modeOfImplementation;
	
	@Id
	@Column(name = "WH_TYP_ID") 
	private Long warehouseTypeId;

	@Column(name = "IB_QA_CHK") 
	private Long inboundQaCheck;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_TYP_ID_DESC")
	private String warehouseTypeIdAndDescription;
	
	@Column(name = "C_TEXT")
	private String description;
	
	@Column(name = "OB_QA_CHK") 
	private Long outboundQaCheck;
	
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
	
	@Column(name = "CTD_BY") 
	private String createdBy;
	
	@Column(name = "CTD_ON")	
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();

	//========Walkaroo======================

	@Column(name = "CAS_NO")
	private Double noOfCases;

	@Column(name = "PL_TOL")
	private Double plusTolerance;

	@Column(name = "MS_TOL")
	private Double minusTolerance;
}
