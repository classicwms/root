package com.iweb2b.core.model.play;

import lombok.Data;

import java.util.Date;

@Data
public class AddNotification {

	private String partnerId;
	private String partnerName;
	private String notificationsApiConsoleHealth;
	private String notificationsApiDown;
	private Date notificationsSystemDown;
	private String notificationsDataSyncFailure;
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
