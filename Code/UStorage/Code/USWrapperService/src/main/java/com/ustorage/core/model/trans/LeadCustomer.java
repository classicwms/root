package com.ustorage.core.model.trans;

import com.ustorage.core.Enum.LeadCustomerTypeEnum;

import lombok.Data;

import java.util.Date;

@Data
public class LeadCustomer {

    private String customerCode;
    private String codeId;
    private String customerName;
    private String type;
    private String sbu;
    private String customerGroup;
    private String currency;
    private String civilId;
    private String nationality;
    private String address;
    private String email;
    private String faxNumber;
    private String mobileNumber;
    private String phoneNumber;
    private String authorizedPerson;
    private String authorizedCivilID;
    private String billedAmountTillDate;
    private String paidAmountTillDate;
    private String balanceToBePaid;
    private String seviceRendered;
    private String status;
    private Boolean isActive = true;
    private String billedAmount;
    private String voucherNumber;
    private String preferedPaymentMode;
    private Date paidDate;
    private Long deletionIndicator = 0L;
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
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
}
