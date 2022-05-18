package com.tekclover.wms.api.enterprise.model.warehouse;

import javax.validation.constraints.Email;

import lombok.Data;

@Data
public class UpdateWarehouse {

    private String companyId;
	private String plantId;
    private String warehouseId;
    private String modeOfImpelementation;
    private Long warehouseTypeId;
    private String languageId;    
    private Boolean inboundQACheck;    
    private Boolean outboundQACheck;    
    private String zone;    
    private String address1;    
    private String address2;    
    private String contactName;
    private String designation;
    private String city;
    private String state;	
	private String country;	
	private Long zipCode;	
	private String phoneNumber;
	
    @Email
    private String eMail;    
    private Double length;    
    private Double width;    
    private Double totalArea;    
    private String uom;	
	private Long noAisles;	
	private Double latitude;	
	private Double longitude;	
	private String storageMethod;    
	private String updatedBy;
}
