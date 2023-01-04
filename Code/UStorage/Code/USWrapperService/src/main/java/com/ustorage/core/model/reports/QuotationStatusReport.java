package com.ustorage.core.model.reports;

import lombok.Data;

@Data
public class QuotationStatusReport {
    String QuotationDate;
    String RequirementType;
    String EnquiryReferenceNumber;

    String Sbu;
    String QuoteId;
    String CustomerCode;
    String CustomerName;
    String MobileNumber;
    String Rent;
    String Status;
    String Notes;

}
