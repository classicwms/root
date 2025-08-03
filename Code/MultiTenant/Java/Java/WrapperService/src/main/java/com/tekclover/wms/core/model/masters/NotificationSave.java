package com.tekclover.wms.core.model.masters;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
public class NotificationSave {

	private String topic;
	private String message;
	private List<String> userId;
	private String userType;
	private Date createdOn;
	private String createdBy;
}
