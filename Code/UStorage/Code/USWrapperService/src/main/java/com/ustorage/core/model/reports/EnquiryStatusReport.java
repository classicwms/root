package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class EnquiryStatusReport {
    Date EnquiryDate;
    String RequirementType;
    String EnquiryId;

    String Sbu;
    String CustomerCode;
    String CustomerName;
    String MobileNumber;
    String EnquiryStoreSize;
    String EnquiryName;
    String EnquiryStatus;
    String EnquiryRemarks;

}
