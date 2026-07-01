package com.tekclover.wms.api.transaction.model.auth;

import lombok.Data;

@Data
public class AXUserAuth {

	private String username;
	private String password;
	
	private String client_Id;
	private String grant_type;
	private String resource;
	private String client_secret;
}