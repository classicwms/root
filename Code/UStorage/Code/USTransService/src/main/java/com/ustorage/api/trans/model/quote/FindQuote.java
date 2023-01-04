package com.ustorage.api.trans.model.quote;

import lombok.Data;

import java.util.List;

@Data
public class FindQuote {

	private List<String> quoteId;

    private List<String> codeId;

    private List<String> sbu;

    private List<String> enquiryReferenceNumber;

    private String customerName;

    private String mobileNumber;

    private String email;

    private List<String> status;

    private List<String> customerCode;

    private List<String> customerGroup;

    private Boolean isActive;
}
