package com.ustorage.api.trans.model.enquiry;

import lombok.Data;

import java.util.List;

@Data
public class FindEnquiry {

	private List<String> enquiryId;

    private List<String> codeId;

    private String enquiryName;

    private String enquiryMobileNumber;

    private List<String> requirementDetail;

    private List<String> sbu;

    private String email;

    private List<String> customerGroup;

    private List<String> enquiryStatus;

    private List<String> rentType;

    private Boolean isActive;
}
