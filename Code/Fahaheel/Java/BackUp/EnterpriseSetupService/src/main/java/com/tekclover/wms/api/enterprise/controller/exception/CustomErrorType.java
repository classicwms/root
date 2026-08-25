package com.tekclover.wms.api.enterprise.controller.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class CustomErrorType {
	
	private String message;
	private HttpStatus status;

	public CustomErrorType (String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}
}
