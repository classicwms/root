package com.tekclover.wms.api.inbound.transaction.model.notification;

import lombok.Data;

@Data
public class WebSocketNotification {

	private String from;
	private String text;
}
