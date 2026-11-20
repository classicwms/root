package com.tekclover.wms.api.masters.model.company;

import lombok.Data;

@Data
public class UpdateCompany {

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
}
