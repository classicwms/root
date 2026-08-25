package com.tekclover.wms.api.idmaster.model.statusmessagesid;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddStatusMessagesId {

	private String messageId;
	private String languageId;
	@NotBlank(message = "Message Type is mandatory")
	private String messageType;
	private String messageText;
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
}
