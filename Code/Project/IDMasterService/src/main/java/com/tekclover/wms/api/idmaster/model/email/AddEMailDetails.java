package com.tekclover.wms.api.idmaster.model.email;

import lombok.Data;

@Data
public class AddEMailDetails {

	private String toAddress;
	private String ccAddress;
	private String fromAddress;
	private String subject;
	private String bodyText;
	private String groupBy;
	private String senderName;
	private Long deletionIndicator=0L;

}
