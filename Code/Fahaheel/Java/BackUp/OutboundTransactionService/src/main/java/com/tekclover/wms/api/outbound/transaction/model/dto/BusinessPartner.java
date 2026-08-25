package com.tekclover.wms.api.outbound.transaction.model.dto;

import lombok.Data;

@Data
public class BusinessPartner {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long businessPartnerType;
	private String partnerCode;
	private String partnerName;
	private String address1;
	private String address2;
	private String zone;
	private String country;
	private String state;
	private String phoneNumber;
	private String faxNumber;
	private String emailId;
	private String referenceText;
	private String location;
	private Double lattitude;
	private Double longitude;
	private Long storageTypeId;
	private String storageBin;
	private Long statusId;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Long deletionIndicator;
	private String createdBy;
}
