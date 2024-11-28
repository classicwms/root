package com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
public class AddProformaInvoiceLine {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long proformaBillNo;
    private String partnerCode;
    private Long lineNumber;
    private String description;
    private Double proformaBillAmountLine;
    private String billUnit;
    private Double billQuantity;
    private String priceUnit;
    private Date invoiceDate;
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
    private Long proformaHeaderId;
}
