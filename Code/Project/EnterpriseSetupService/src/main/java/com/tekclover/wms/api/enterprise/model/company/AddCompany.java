package com.tekclover.wms.api.enterprise.model.company;

import javax.validation.constraints.Email;

import lombok.Data;

@Data
public class AddCompany {

    private String companyId;
    
    private String languageId;
    
    private String verticalId;
    
    private String address1;
    
    private String address2;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private Long currency;
    
    private Long zipCode;
    
    private String contactName;
    
    private String designation;
    
    private String phoneNumber;
    
    @Email
    private String eMail;
    
    private String registrationNo;
    
    private Long noOfPlants;
    
    private Long noOfOutlets;
    
    private Long noOfWarehouses;
    
    private String createdBy;
}
