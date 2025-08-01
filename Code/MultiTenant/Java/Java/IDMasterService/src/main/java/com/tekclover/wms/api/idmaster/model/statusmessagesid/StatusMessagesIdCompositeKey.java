package com.tekclover.wms.api.idmaster.model.statusmessagesid;

import lombok.Data;
import java.io.Serializable;

@Data
public class StatusMessagesIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `MESSAGE_ID`, `LANG_ID`,`MESSAGE_TYPE`
	 */
	private String messageId;
	private String languageId;
	private String messageType;
}
