package com.tekclover.wms.core.model.warehouse.inbound;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class WarehouseApiResponse {

	private HttpStatus statusCode;
	private String message;
}
