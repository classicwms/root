package com.tekclover.wms.api.idmaster.model.websocketnotification;

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

	private String documentNumber;
	private String referenceNumber;
	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String storageBin;
	private String newCreated;
}
