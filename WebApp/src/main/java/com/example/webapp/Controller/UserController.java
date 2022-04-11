package com.example.webapp.Controller;

import com.example.webapp.Config.SecurityConfig;
import com.example.webapp.Model.User;
import com.example.webapp.Service.UserService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	SecurityConfig sconfig;

	@Autowired
	StatsDClient statsd;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// GET
	@GetMapping(value = "/v2/users/self", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> retrieveAllUsers(@RequestHeader Map<String, String> headers) {

		statsd.incrementCounter("GET.UserDetails.counter");

		String[] credentials = sconfig.decodeBasicAuthentication(headers);

		List<User> users = service.retrieveUser(credentials);

		logger.info("User " + users);

		if (users == null || users.size() == 0) {
			
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");	
			
		}

		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	// POST
	@PostMapping(value = "/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> createUser(@RequestBody User user) {

		statsd.incrementCounter("POST.UserDetails.counter");

		User newUser = service.createUser(user);

		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}

	// PUT
	@PutMapping(value = "/v1/users/self", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(@RequestBody User user,@RequestHeader Map<String, String> headers) {

		statsd.incrementCounter("PUT.UserDetails.counter");
		
		String[] credentials = sconfig.decodeBasicAuthentication(headers);
		
		boolean isUpdated = service.updateUser(user,credentials);
		
		if(!isUpdated) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Details Not Updated");
		}
		

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
