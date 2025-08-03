package com.tekclover.wms.api.outbound.transaction.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.outbound.transaction.model.auth.UserLogin;

@Repository
public class UserAuthRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public UserLogin getUserLoginDetails (String userName) {
		Collection<GrantedAuthority> grantedAuthList = new ArrayList<>();
		
		String userQuery = "Select * from tblusertoken where username = ?";
		@SuppressWarnings("deprecation")
		List<UserLogin> userLoginList = jdbcTemplate.query(userQuery, new String[] {userName},
				(ResultSet rs, int rowNum) -> {
					UserLogin userLogin = new UserLogin();
					userLogin.setUserName(userName);
					userLogin.setPassword(rs.getString("password"));
					return userLogin;
				}
			);
		
		if (userLoginList.size() > 0) {
			GrantedAuthority grantedAutoriy = new SimpleGrantedAuthority("ROLE_SYSTEMADMIN");
			grantedAuthList.add(grantedAutoriy);
			userLoginList.get(0).setGrantedAutoriyList(grantedAuthList);
			return userLoginList.get(0);
		}
		return null;
	}
}