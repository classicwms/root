package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {

	private Long id = 0L;
	private String topic;
	private String message;
	private String userId;
	private String userType;
	private Boolean status = false;
	private Boolean deletionIndicator = false;
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}