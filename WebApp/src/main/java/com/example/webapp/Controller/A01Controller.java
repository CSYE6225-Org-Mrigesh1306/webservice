package com.example.webapp.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class A01Controller {
	
	@GetMapping(value="/healthz",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getStatus() {
		return new ResponseEntity<>("Successfull",HttpStatus.OK);
	}

}
