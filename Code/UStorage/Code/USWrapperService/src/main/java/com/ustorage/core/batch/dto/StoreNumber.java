package com.ustorage.core.batch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StoreNumber {
    private String storeNumber;
    private String agreementNumber;
    private String description;
    private Long deletionIndicator;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    public StoreNumber(String storeNumber,
                       String agreementNumber,
                       String description,
                       Long deletionIndicator,
                       String createdBy,
                       Date createdOn,
                       String updatedBy,
                       Date updatedOn){
        this.storeNumber = storeNumber;
        this.agreementNumber = agreementNumber;
        this.description = description;
        this.deletionIndicator = deletionIndicator;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.updatedBy = updatedBy;
        this.updatedOn = updatedOn;
    }
}
