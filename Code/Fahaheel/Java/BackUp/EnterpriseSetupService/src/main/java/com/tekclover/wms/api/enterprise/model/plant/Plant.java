package com.tekclover.wms.api.enterprise.model.plant;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`
 */
@Table(
		name = "tblplant", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_plant", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID"})
				}
		)
@IdClass(PlantCompositeKey.class)
public class Plant { 
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID") 
	private String companyId;
	
	@Id
	@Column(name = "PLANT_ID") 
	private String plantId;
	
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

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;
	
	@Column(name = "DESIG")
	private String designation;
	
	@Column(name = "PH_NO") 
	private String phoneNumber;
	
	@Column(name = "MAIL_ID") 
	private String emailId;
	@Column(name = "C_TEXT")
	private String description;
	
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
