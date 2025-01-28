package com.tekclover.wms.api.idmaster.model.auth;

import lombok.Data;

@Data
public class AuthToken {

	private String access_token;
	private String token_type;
	private String refresh_token;
	private String expires_in;
	private String scope;
	private String jti;
}
