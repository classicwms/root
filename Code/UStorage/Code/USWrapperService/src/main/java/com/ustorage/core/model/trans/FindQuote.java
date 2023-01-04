package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.List;

@Data
public class FindQuote {

	private List<String> quoteId;

    private List<String> codeId;

    private List<String> enquiryReferenceNumber;
    private List<String> sbu;

    private String customerName;

    private String mobileNumber;

    private String email;

    private List<String> customerGroup;

    private List<String> status;

    private List<String> customerCode;

    //private Boolean isActive;
}
