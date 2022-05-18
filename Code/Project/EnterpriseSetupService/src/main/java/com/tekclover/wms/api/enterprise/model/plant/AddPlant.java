package com.tekclover.wms.api.enterprise.model.plant;

import javax.validation.constraints.Email;

import lombok.Data;

@Data
public class AddPlant {

    private String companyId;
    private String languageId;
    
    private String plantId;
    
    private String address1;
    
    private String address2;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private Long zipCode;
    
    private String contactName;
    
    private String designation;
    
    private String phoneNumber;
    
    @Email
    private String eMail;
    
    private String createdBy;
}
