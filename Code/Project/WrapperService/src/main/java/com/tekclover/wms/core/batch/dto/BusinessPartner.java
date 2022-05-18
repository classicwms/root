package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class BusinessPartner { 
	private String languageId;			// LANG_ID
	private String companyCodeId;		// C_ID
	private String plantId;				// PLANT_ID
	private String warehouseId;			// WH_ID
	private String businessParnterType;	// PARTNER_TYP
	private String partnerCode;			// PARTNER_CODE
	private String partnerName;			// PARTNER_NM
	private String address1;			// ADD_1
	private String address2;			// ADD_2
	private String zone;				// Zone
	private String country;				// COUNTRY
	private String state;				// STATE
	private String phoneNumber;			// PH_NO
	private String faxNumber;			// FX_NO
	private String emailId;				// MAIL_ID
	private String referenceText;		// REF_TXT
	private String location;			// LOCATION
	private String lattitude;			// LATITUDE
	private String longitude;			// LONGITUDE
	private String storageTypeId;			// ST_TYP_ID
	private String storageBin;			// ST_BIN
	private String statusId;				// STATUS_ID
	private String deletionIndicator;		// Is_deleted
	private String createdBy;			// CTD_BY
	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param businessParnterType
	* @param partnerCode
	* @param partnerName
	* @param address1
	* @param address2
	* @param zone
	* @param country
	* @param state
	* @param phoneNumber
	* @param faxNumber
	* @param emailId
	* @param referenceText
	* @param location
	* @param lattitude
	* @param longitude
	* @param storageTypeId
	* @param storageBin
	* @param statusId
	* @param deletionIndicator
	* @param createdBy
	*/
	public BusinessPartner (String languageId, String companyCodeId, String plantId, String warehouseId, String businessParnterType, 
							String partnerCode, String partnerName, String address1, String address2, String zone, String country, 
							String state, String phoneNumber, String faxNumber, String emailId, String referenceText, String location, 
							String lattitude, String longitude, String storageTypeId, String storageBin, String statusId, 
							String deletionIndicator, String createdBy) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.businessParnterType = businessParnterType;
		this.partnerCode = partnerCode;
		this.partnerName = partnerName;
		this.address1 = address1;
		this.address2 = address2;
		this.zone = zone;
		this.country = country;
		this.state = state;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.emailId = emailId;
		this.referenceText = referenceText;
		this.location = location;
		this.lattitude = lattitude;
		this.longitude = longitude;
		this.storageTypeId = storageTypeId;
		this.storageBin = storageBin;
		this.statusId = statusId;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
