package com.ustorage.api.trans.model.impl;

import java.util.Date;

public interface PaymentDueStatusReportImpl {
    String getAgreementNumber();
    String getCustomerName();
    String getCustomerCode();
    String getCivilIdNumber();
    String getPhoneNumber();
    String getSecondaryNumber();
    String getLocation();
    String getStoreNumber();
    String getSize();
    String getStoreType();
    String getPhase();
    String getRent();
    String getPaymentTerms();
    String getAgreementDiscount();
    String getTotalAfterDiscount();
    Date getStartDate();
    Date getEndDate();
    Date getLastPaidDate();
    Date getDueDate();
    String getRentPeriod();
    String getTotalRent();
    String getDueDays();
    String getTotalPaidVoucherAmount();
    String getTotalDueAmount();
    String getNextDueAmount();
}
