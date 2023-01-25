package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentDueStatusReport {
    String AgreementNumber;
    String CustomerName;
    String CustomerCode;
    String CivilIdNumber;
    String PhoneNumber;
    String SecondaryNumber;
    String Location;
    String StoreNumber;
    String Size;
    String StoreType;
    String Phase;
    String Rent;
    String PaymentTerms;
    String AgreementDiscount;
    String TotalAfterDiscount;
    Date StartDate;
    Date EndDate;
    Date LastPaidDate;
    Date DueDate;
    String RentPeriod;
    String TotalRent;
    String DueDays;
    String TotalPaidVoucherAmount;
    String TotalDueAmount;
    String NextDueAmount;
    String Description;
    String Status;
}
