package com.iweb2b.core.model.play;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateHealthCheck {

	private String partnerId;
	private String partnerName;
	private String systemsList;
	private String activeSystems;
	private String inactiveSystems;
	private String ipAvailability;
	private String memoryUtilization;
	private String cpuUtilization;
	private String heatMap;
	private String alarms;
	private Date activeSince;
	private Long deletionIndicator;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private String updatedBy;
}
