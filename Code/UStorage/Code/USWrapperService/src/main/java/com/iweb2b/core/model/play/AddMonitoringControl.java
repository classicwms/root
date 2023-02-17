package com.iweb2b.core.model.play;

import lombok.Data;

import java.util.Date;

@Data
public class AddMonitoringControl {

	private String partnerId;
	private String subcriptionType;
	private String sourceSystem;
	private String targetSystem;
	private String connectivityStatus;
	private Date lastActive;
	private String lastSyncSuccess;
	private String dataLost;
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
