package com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceheader;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateInvoiceHeader {
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
}