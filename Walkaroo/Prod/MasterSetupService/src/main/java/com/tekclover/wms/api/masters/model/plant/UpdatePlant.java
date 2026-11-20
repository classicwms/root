package com.tekclover.wms.api.masters.model.plant;

import lombok.Data;

@Data
public class UpdatePlant {


    private String languageId;
    private String companyId;
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
    private String emailId;
    private String description;
    private Long deletionIndicator;
}
