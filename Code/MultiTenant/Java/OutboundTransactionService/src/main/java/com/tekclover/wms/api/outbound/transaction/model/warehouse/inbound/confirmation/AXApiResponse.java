package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.confirmation;

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
