package com.tekclover.wms.core.model.idmaster;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class UserLogin {

	@NotNull(message = "User name cannot be null")
	private String name;
	
	@NotNull(message = "Password cannot be null")
	private String password;
	
	private Collection<GrantedAuthority> grantedAutoriyList = new ArrayList<>();
}
