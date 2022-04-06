package com.example.webapp.Controller;

import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class A01Controller {

	private static final Logger logger = LoggerFactory.getLogger(A01Controller.class);

	@Autowired
	StatsDClient statsd;
	
	@GetMapping(value="/healthz",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getStatus() {

		statsd.incrementCounter("HealthCheck.counter");

		logger.info("Webapp Health Check : Successfull");
		return new ResponseEntity<>("Successfull",HttpStatus.OK);
	}

}
