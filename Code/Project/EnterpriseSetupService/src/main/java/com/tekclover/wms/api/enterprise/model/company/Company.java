package com.tekclover.wms.api.enterprise.model.company;

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
 * `LANG_ID`, `C_ID`
 */
@Table(
		name = "tblcompany", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_company", 
						columnNames = {"LANG_ID", "C_ID"})
				}
		)
@IdClass(CompanyCompositeKey.class)
public class Company { 
	
	@Id
	@Column(name = "LANG_ID") 	
	private String languageId;
	
	@Id
	@Column(name = "C_ID") 
	private String companyId;
	
	@Column(name = "VERT_ID")
	private String verticalId;
	
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
	
	@Column(name = "CURR_ID") 
	private Long currencyId;
	
	@Column(name = "CNT_NM") 
	private String contactName;
	
	@Column(name = "DESIG")
	private String desigination;
	
	@Column(name = "PH_NO") 
	private String phoneNumber;
	
	@Column(name = "MAIL_ID") 
	private String emailId;
	
	@Column(name = "REG_NO") 
	private String registrationNo;
	
	@Column(name = "NO_PLANTS")
	private Long noOfPlants;
	
	@Column(name = "NO_OUTLETS") 
	private Long noOfOutlets;
	
	@Column(name = "NO_WAREHOUSES")
	private Long noOfWarehouse;
	
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
