package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.List;

@Data
public class FindEnquiry {

	private List<String> enquiryId;

    private List<String> codeId;

    private String enquiryName;
    private List<String> sbu;

    private List<String> requirementDetail;

    private String enquiryMobileNumber;

    private String email;

    private List<String> customerGroup;

    private List<String> enquiryStatus;

    private List<String> rentType;

    //private Boolean isActive;
}
