package com.iweb2b.core.model.integration.asyad;

import lombok.Data;

@Data
public class ConsignmentResponse {
	/*
	 * {
		  "success": true,
		  "reference_number": "E12345678",
		  "courier_partner": "Shadowfax",
		  "courier_account": "Shadowfax Instant",
		  "courier_partner_reference_number": "",
		  "customer_reference_number": "ABCD"
		}
	 */
	private boolean success;
	private String reference_number;
	private String courier_partner;
	private String courier_account;
	private String courier_partner_reference_number;
	private String customer_reference_number;
}
