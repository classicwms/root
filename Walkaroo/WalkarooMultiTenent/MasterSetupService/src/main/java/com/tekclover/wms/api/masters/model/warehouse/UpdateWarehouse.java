package com.tekclover.wms.api.masters.model.warehouse;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UpdateWarehouse {
    private String description;
    private Long inboundQACheck;
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
    private String modeOfImplementation;
	
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
    private Double noOfCases;
    private Double plusTolerance;
    private Double minusTolerance;
}
