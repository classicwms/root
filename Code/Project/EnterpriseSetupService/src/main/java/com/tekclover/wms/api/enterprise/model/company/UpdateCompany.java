package com.tekclover.wms.api.enterprise.model.company;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateCompany {

	private String languageId;
	private String companyId;
	private String verticalId;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private Long zipCode;
	private Long currencyId;
	private String contactName;
	private String desigination;
	private String phoneNumber;
	private String emailId;
	private String registrationNo;
	private Long noOfPlants;
	private Long noOfOutlets;
	private Long noOfWarehouse;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
