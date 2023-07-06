package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class AddDeliveryLine {

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String deliveryNo;

    private String itemCode;

    private Long lineNo;

    private String invoiceNumber;

    private String refDocNumber;

    private String partnerCode;

    private Long outboundOrderTypeId;

    private String address1;

    private String address2;

    private String zone;

    private String country;

    private String state;

    private String phoneNumber;

    private String eMailId;

    private String description;

    private String manufacturerCode;

    private String deliveryQty;

    private String deliveryUom;

    private Long noOfAttempts;

    private String digitalSignature;

    private String complaintReason;

    private String deliveryFailiureReason;

    private String paymentMode;

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

    private String deliveryConfirmedBy;

    private Date deliveryConfirmedOn;


}
