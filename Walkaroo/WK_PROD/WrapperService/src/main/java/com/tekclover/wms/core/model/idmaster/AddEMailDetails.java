package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
@Data
public class AddEMailDetails {

	private String toAddress;
	private String ccAddress;
	private String fromAddress;
	private String subject;
	private String bodyText;
	private String group;
	private String senderName;
	private Long deletionIndicator=0L;

}
