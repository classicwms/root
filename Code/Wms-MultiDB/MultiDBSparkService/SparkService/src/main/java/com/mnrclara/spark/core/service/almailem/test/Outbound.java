package com.mnrclara.spark.core.service.almailem.test;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
public class Outbound {

//    private String refDocNumber;
//    private String referenceDocumentType;
//    private String c_id;
//    private String lang_id;
//    private String plant_id;
//    private String wh_id;
//    private String pre_ob_no;
//    private String partner_code;
//    private double referenceField7;
//    private long referenceField8;
//    private double referenceField9;
//    private long referenceField10;
    private String refDocNumber;
    private String companyCodeId;
    private String languageId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String partnerCode;
    private String createdBy;
    private String updatedBy;
    private Timestamp updatedOn;
    private Timestamp createdOn;
    private String deliveryConfirmedBy;
    private Timestamp deliveryConfirmedOn;
    private String referenceDocumentType;
    private Long outboundOrderTypeId;
    private Timestamp refDocDate;
    private String remarks;
    private Timestamp requiredDeliveryDate;
    private String reversedBy;
    private Timestamp reversedOn;

    private Long statusId;
    private String invoiceNumber;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String salesOrderNumber;
    private String salesInvoiceNumber;
    private String pickListNumber;
    private Timestamp invoiceDate;
    private String deliveryType;
    private String customerId;
    private String customerName;
    private String targetBranchCode;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String tokenNumber;
    private String status;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private Double referenceField7;
    private Long referenceField8;
    private Double referenceField9;
    private Long referenceField10;


//    public Outbound(String refDocNumber, String companyCodeId, String languageId, String plantId, String warehouseId,
//                    String preOutboundNo, String partnerCode, String referenceDocumentType, Long statusId,
//                    String invoiceNumber, String companyDescription, String plantDescription,
//                    String warehouseDescription, String statusDescription, String salesOrderNumber,
//                    String salesInvoiceNumber, String pickListNumber, Timestamp invoiceDate, Double referenceField7,
//                    Long referenceField8, Double referenceField9, Long referenceField10) {
//        this.refDocNumber = refDocNumber;
//        this.companyCodeId = companyCodeId;
//        this.languageId = languageId;
//        this.plantId = plantId;
//        this.warehouseId = warehouseId;
//        this.preOutboundNo = preOutboundNo;
//        this.partnerCode = partnerCode;
//        this.referenceDocumentType = referenceDocumentType;
//        this.statusId = statusId;
//        this.invoiceNumber = invoiceNumber;
//        this.companyDescription = companyDescription;
//        this.plantDescription = plantDescription;
//        this.warehouseDescription = warehouseDescription;
//        this.statusDescription = statusDescription;
//        this.salesOrderNumber = salesOrderNumber;
//        this.salesInvoiceNumber = salesInvoiceNumber;
//        this.pickListNumber = pickListNumber;
//        this.invoiceDate = invoiceDate;
//        this.referenceField7 = referenceField7;
//        this.referenceField8 = referenceField8;
//        this.referenceField9 = referenceField9;
//        this.referenceField10 = referenceField10;
//    }
}
