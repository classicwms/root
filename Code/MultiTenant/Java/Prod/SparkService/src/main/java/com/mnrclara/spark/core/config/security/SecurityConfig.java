package com.mnrclara.spark.core.config.security;

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

//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity (prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	@Autowired
//	UserLoginService userLoginService;
//
//	@Bean
//	public PasswordEncoder encoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userLoginService).passwordEncoder(encoder());
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//			.anyRequest()
//			.authenticated()
//			.and()
//			.sessionManagement()
//			.sessionCreationPolicy(SessionCreationPolicy.NEVER);
//	}
//
//   @Override
//   public void configure(WebSecurity web) throws Exception {
//       web.ignoring().antMatchers(
//    		   "/v2/api-docs",
//    		   "/swagger-resources/**",
//               "/swagger-ui.html",
//               "/webjars/**"
//    		   );
//   }
//
//	@Override
//	@Bean
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//}