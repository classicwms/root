package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class InvoiceHeader {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String invoiceNumber;
    private String partnerCode;
    private Double invoiceAmount;
    private String billUnit;
    private Double billQuantity;
    private String priceUnit;
    private Date billDateFrom;
    private Date billDateTo;
    private Date invoiceDate;
    private String proformaBillNo;
    private Double paymentAmount;
    private Long statusId;
    private Long deletionIndicator;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
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
    private Double proformaBillAmount;
    private String partnerName;
    private Double totalCbm;
    private String currency;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private Date fromDate;
    private Date toDate;
}
