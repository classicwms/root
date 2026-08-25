package com.tekclover.wms.api.outbound.transaction.model.auth;

import lombok.Data;

@Data
public class AXAuthToken {

	/*
	 *  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlRlc3RBeFVzZXIiLCJuYmYiOjE2NDgzMDcyMTUsImV4cCI6MTY0ODMxMDgxNSwiaWF0IjoxNjQ4MzA3MjE1fQ.in39zGxCi8GLhvPXabqzltwkiHBkHTOal0UDZaxceuk",
	 *  "token_type": "bearer",
	 *  "expires_in": 3600,
	 *  "username": "TestAxUser",
	 *  "issued": "2022-03-26T15:06:55",
	 *  "expires": "2022-03-26T16:06:55",
	 */
	private String access_token;
	private String token_type;
	private String username;
	private String expires_in;
	private String issued;
	private String expires;
}
