package com.tekclover.wms.api.inbound.orders.model.auth;

import lombok.Data;

@Data
public class UserAuth {

	private String userName;
	private String password;
}