package com.tekclover.wms.core.model.threepl;

import lombok.Data;
import java.util.Date;
@Data
public class AddInvoiceLine {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long invoiceNumber;
    private String partnerCode;
    private Long lineNumber;
    private String lineDescription;
    private Double lineInvoiceAmount;
    private String billUnit;
    private Double billQuantity;
    private String priceUnit;
    private Date invoiceDate;
    private String proformaBillNo;
    private Double paymentAmount;
    private Long statusId;
    private Long invoiceHeaderId;
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
}
