package com.ustorage.api.trans.model.leadcustomer;

import java.util.Date;

import javax.persistence.*;

import com.ustorage.api.trans.Enum.LeadCustomerTypeEnum;
import com.ustorage.api.trans.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblleadcustomer")
@Where(clause = "IS_DELETED='0'")
public class LeadCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lead_customer")
    @GenericGenerator(name = "seq_lead_customer", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
            @Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
            @Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "LC"),
            @Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;

    @Column(name = "CODE_ID")
    private String codeId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SBU")
    private String sbu;

    @Column(name = "CUSTOMER_GROUP")
    private String customerGroup;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "CIVIL_ID")
    private String civilId;

    @Column(name = "NATIONALITY")
    private String nationality;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FAX_NUMBER")
    private String faxNumber;

    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "AUTHORIZED_PERSON")
    private String authorizedPerson;

    @Column(name = "AUTHORIZED_CIVIL_ID")
    private String authorizedCivilID;

    @Column(name = "BILLED_AMOUNT_TILL_DATE")
    private String billedAmountTillDate;

    @Column(name = "PAID_AMOUNT_TILL_DATE")
    private String paidAmountTillDate;

    @Column(name = "BALANCE_TO_BE_PAID")
    private String balanceToBePaid;

    @Column(name = "SEVICE_RENDERED")
    private String seviceRendered;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "BILLED_AMOUNT")
    private String billedAmount;

    @Column(name = "VOUCHER_NUMBER")
    private String voucherNumber;

    @Column(name = "PREFERED_PAYMENT_MODE")
    private String preferedPaymentMode;

    @Column(name = "PAID_DATE")
    private Date paidDate;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "REF_FIELD_1")
    private String referenceField1;

    @Column(name = "REF_FIELD_2")
    private String referenceField2;

    @Column(name = "REF_FIELD_3")
    private String referenceField3;

    @Column(name = "REF_FIELD_4")
    private String referenceField4;

    @Column(name = "REF_FIELD_5")
    private String referenceField5;

    @Column(name = "REF_FIELD_6")
    private String referenceField6;

    @Column(name = "REF_FIELD_7")
    private String referenceField7;

    @Column(name = "REF_FIELD_8")
    private String referenceField8;

    @Column(name = "REF_FIELD_9")
    private String referenceField9;

    @Column(name = "REF_FIELD_10")
    private String referenceField10;

    @Column(name = "CTD_BY")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;
}
