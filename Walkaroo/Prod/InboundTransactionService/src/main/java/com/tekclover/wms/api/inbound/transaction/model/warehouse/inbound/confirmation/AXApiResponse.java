package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class AXApiResponse {
	
	/*
	 * 	{
			 "statusCode": <status-code>,
			 "message": <message>
		}
	 */
	private String statusCode;
	private String message;
	
}
