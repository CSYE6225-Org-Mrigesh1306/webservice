package com.example.webapp.Controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.webapp.Config.SecurityConfig;
import com.example.webapp.Model.User;
import com.example.webapp.Service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	SecurityConfig sconfig;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// GET
	@GetMapping(value = "/v1/users/self", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> retrieveAllUsers(@RequestHeader Map<String, String> headers) {

		String[] credentials = sconfig.decodeBasicAuthentication(headers);

		List<User> users = service.retrieveUser(credentials);

		logger.info("User " + users);

		if (users == null || users.size() == 0) {
			
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");	
			
		}

		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	// POST
	@PostMapping(value = "/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> createUser(@RequestBody User user) {

		User newUser = service.createUser(user);

		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}

	// PUT
	@PutMapping(value = "/v1/users/self", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(@RequestBody User user,@RequestHeader Map<String, String> headers) {

		
		String[] credentials = sconfig.decodeBasicAuthentication(headers);
		
		boolean isUpdated = service.updateUser(user,credentials);
		
		if(!isUpdated) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Details Not Updated");
		}
		

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
