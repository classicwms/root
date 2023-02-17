package com.iweb2b.core.model.auth;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AuthTokenRequest {

	@NotEmpty(message = "API Name cannot be null or empty.")
	private String apiName;
	
	@NotEmpty(message = "Client ID cannot be null or empty.")
	private String clientId;
	
	@NotEmpty(message = "Client Secret Key cannot be null or empty.")
	private String clientSecretKey;
	
	@NotEmpty(message = "GrantType cannot be null or empty.")
	private String grantType;
	
	@NotEmpty(message = "OAuth Username cannot be null or empty.")
	private String oauthUserName;
	
	@NotEmpty(message = "OAuth Password cannot be null or empty.")
	private String oauthPassword;
}
