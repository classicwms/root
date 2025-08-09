package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

@Data
public class EMailDetails {

	private Long id;
	private String fromAddress;
	private String toAddress;
	private String ccAddress;
	private String subject;
	private String bodyText;
	private String group;
	private Long deletionIndicator;
	private String senderName;
}
