package com.iweb2b.core.model.play;

import lombok.Data;

import java.util.Date;

@Data
public class SetupConfiguration {

	private String partnerId;
	private String systemType;
	private String adminUser;
	private String syncStatusId;
	private String sourceSystem;
	private String targetSystem;
	private String accessType;
	private String remarks;
	private String userCreation;
	private Long deletionIndicator = 0L;
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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
