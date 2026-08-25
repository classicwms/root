package com.tekclover.wms.api.enterprise.transaction.model.notification;

import lombok.Data;

@Data
public class WebSocketNotification {

	private String from;
	private String text;
}