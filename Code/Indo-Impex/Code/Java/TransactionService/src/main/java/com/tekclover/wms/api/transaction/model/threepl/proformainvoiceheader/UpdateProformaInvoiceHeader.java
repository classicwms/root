package com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProformaInvoiceHeader {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long proformaBillNo;
    private String partnerCode;
    private Double proformaBillAmount;
    private String billUnit;
    private Double billQuantity;
    private String priceUnit;
    private Date billDateFrom;
    private Date billDateTo;
    private Date proformaBillDate;
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
