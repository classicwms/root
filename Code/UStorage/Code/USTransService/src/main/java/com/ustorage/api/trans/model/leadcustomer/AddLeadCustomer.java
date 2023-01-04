package com.ustorage.api.trans.model.leadcustomer;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ustorage.api.trans.Enum.LeadCustomerTypeEnum;
import lombok.Data;

@Data
public class AddLeadCustomer {

    @NotNull(message = "Customer name cannot be null")
    @NotBlank(message = "Customer Name is mandatory")
    private String customerName;

    private String codeId;

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

    private String billedAmount;

    private String voucherNumber;

    private String preferedPaymentMode;

    private Date paidDate;

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
