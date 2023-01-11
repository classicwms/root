package com.ustorage.api.master.model.documentstorage;

import lombok.Data;

import java.util.Date;

@Data
public class AddDocumentStorage {

    private String customerName;

    private String fileDescription;

    private Long deletionIndicator;

    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;

    private String createdBy;

    private Date createdOn;

    private String uploadedBy;

    private Date uploadedOn;

    private String updatedBy;
    private Date updatedOn;

}
