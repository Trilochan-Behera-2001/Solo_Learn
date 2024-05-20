package com.solo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		
		return new CustomeUserDetailsServiceImpl();
		
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
		
	}
	
	@Bean
	public AuthenticationSuccessHandler  authenticationSuccessHandler() {
		
		return new CustomeSuccessHandler();
		
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
		
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		
		
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(request ->
										request.requestMatchers("/user/**").hasRole("USER")
										.requestMatchers("/admin/**").hasRole("ADMIN")
										.requestMatchers("/**")
										.permitAll())
		.formLogin(login -> login.loginPage("/signin")
										.loginProcessingUrl("/formLogin")
										.successHandler(authenticationSuccessHandler()))
		.logout(logout -> logout.permitAll());
        
		
		return http.build();
		
	}
	
	

}
