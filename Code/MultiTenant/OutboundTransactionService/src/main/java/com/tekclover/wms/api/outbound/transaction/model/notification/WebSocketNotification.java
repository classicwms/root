package com.tekclover.wms.api.outbound.transaction.model.notification;

import lombok.Data;

@Data
public class WebSocketNotification {

	private String from;
	private String text;
}
