package com.tekclover.wms.core.model.user;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegisteredUser {

	@NotEmpty(message = "Register ID cannot be null or empty.")
	private String registerId;
	
	@NotEmpty(message = "ClientSecret ID cannot be null or empty.")
	private String clientSecretId;
}
