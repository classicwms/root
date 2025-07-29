package com.tekclover.wms.core.model.transaction;

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
