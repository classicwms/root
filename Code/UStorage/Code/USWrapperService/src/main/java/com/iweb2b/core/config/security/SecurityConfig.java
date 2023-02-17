package com.iweb2b.core.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated().and().csrf().disable();
    }
	
   @Override
   public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers(
    		   "/v2/api-docs",
    		   "/swagger-resources/**",
               "/swagger-ui.html",
               "/webjars/**"
    		   );
   }
}
