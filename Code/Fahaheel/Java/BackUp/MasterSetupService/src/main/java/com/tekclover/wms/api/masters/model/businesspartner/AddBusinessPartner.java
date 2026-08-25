package com.tekclover.wms.api.masters.model.businesspartner;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddBusinessPartner {

	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	@NotBlank(message = "Company Code Id is mandatory")
	private String companyCodeId;
	
	@NotBlank(message = "Plant Id is mandatory")
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotNull(message = "Business Partner Type is mandatory")
	private Long businessPartnerType;
	
	@NotBlank(message = "Partner Code is mandatory")
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
