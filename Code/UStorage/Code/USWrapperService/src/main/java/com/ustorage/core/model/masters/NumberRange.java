package com.ustorage.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class NumberRange {

	private Long numberRangeCode;
	private String description;
	private String documentCode;
	private String documentName;
	private Long numberRangeFrom;
	private Long numberRangeTo;
	private String numberRangeCurrent;
	private Long deletionIndicator = 0L;
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}
