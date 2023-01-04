package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class EnquiryStatusModel {

	private List<String> requirementType;
	private List<String> enquiryId;
	private List<String> enquiryStatus;
	private List<String> sbu;
}
