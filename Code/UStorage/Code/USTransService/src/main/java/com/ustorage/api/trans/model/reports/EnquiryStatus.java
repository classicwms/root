package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class EnquiryStatus {

	private List<String> requirementType;
	private List<String> enquiryId;
	private List<String> enquiryStatus;

	private List<String> sbu;
}
