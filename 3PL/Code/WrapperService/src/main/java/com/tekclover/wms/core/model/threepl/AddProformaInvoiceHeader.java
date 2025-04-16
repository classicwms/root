package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AddProformaInvoiceHeader {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String proformaBillNo;
    private String partnerCode;
    private Double proformaBillAmount;
    private String billUnit;
    private Double billQuantity;
    private String priceUnit;
    private Date billDateFrom;
    private Date billDateTo;
    private Date proformaBillDate;
    private Long statusId;
    private Long deletionIndicator;
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
    List<ProformaInvoiceLine> proformaInvoiceLineList = new ArrayList<>();
}
