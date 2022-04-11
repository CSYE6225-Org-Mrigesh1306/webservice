package com.example.webapp.Config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.webapp.Validations.CustomValidations;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	CustomValidations customValidator;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/*").permitAll().and().authorizeRequests().antMatchers("/h2-console/**")
				.permitAll().and().headers().frameOptions().disable().and().csrf().ignoringAntMatchers("/h2-console/**")
				.disable().authorizeRequests().antMatchers("/*").permitAll().antMatchers(HttpMethod.POST, "/v2/users")
				.permitAll().antMatchers(HttpMethod.GET, "/v2/users/self").permitAll()
				.antMatchers(HttpMethod.PUT, "/v1/users/self").permitAll()
				.antMatchers(HttpMethod.GET, "/v1/users/self/pic").permitAll()
				.antMatchers(HttpMethod.POST, "/v1/users/self/pic").permitAll()
				.antMatchers(HttpMethod.DELETE, "/v1/users/self/pic").permitAll()
				.antMatchers(HttpMethod.GET, "/healthz")
				.permitAll().anyRequest().authenticated().and().cors().disable();

	}

	public String[] decodeBasicAuthentication(Map<String, String> headers) {

		String base64Credentials = "";
		String username = "";
		String password = "";

		if (headers.containsKey("authorization")) {
			
			base64Credentials = headers.get("authorization").substring("Basic".length()).trim();
		
		}
		
		logger.info("Base64 Encoded :"+base64Credentials);
		

		byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
		String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		// credentials = username:password
		final String[] values = credentials.split(":", 2);

		if (values.length == 2) {
			username = values[0];
			password = values[1];
		}
		if (username.isBlank() || password.isBlank()) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter Username or Password");
		
		}
		
		if(!customValidator.isEmailValid(username)) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email format for Username");
		
		}
		
		logger.info("Username : "+username+" Password : "+password);
		
		return values;

	}

}
