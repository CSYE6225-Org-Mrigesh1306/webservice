package com.example.webapp.Controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.webapp.Config.SecurityConfig;
import com.example.webapp.Model.Image;
import com.example.webapp.Model.User;
import com.example.webapp.Service.ImageService;
import com.example.webapp.Service.UserService;

@RestController
public class ImageController {
	
	@Autowired
	ImageService imgservice;
	
	@Autowired
	UserService service;
	
	@Autowired
	SecurityConfig sconfig;
	
	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

	@GetMapping(value = "/v1/users/self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Image>> getUserImage(@RequestHeader Map<String, String> headers) {
		
		String[] credentials = sconfig.decodeBasicAuthentication(headers);
		
		List<User> user = service.retrieveUser(credentials);
		
		List<Image> img = imgservice.getUserImage(user);
		
		if (img == null || img.size() == 0) {
			
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile Pic Not Found");	
			
		}

		return ResponseEntity.status(HttpStatus.OK).body(img);
		
	 
	}
	
	@PostMapping(value = "/v1/users/self/pic",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Image> storeUserImage(@RequestParam("imagefile") MultipartFile imagefile,@RequestHeader Map<String, String> headers) {
		
		System.out.println("headersheaders "+headers);
		
		String[] credentials = sconfig.decodeBasicAuthentication(headers);
		
		List<User> user = service.retrieveUser(credentials);
		
		System.out.println("User "+user.toString());
		
		Image img = imgservice.saveImage(imagefile,user);
		
		return ResponseEntity.status(HttpStatus.OK).body(img);
		
	}
	
	
	@DeleteMapping(value = "/v1/users/self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteUserImage(@RequestHeader Map<String, String> headers) {
		
		String[] credentials = sconfig.decodeBasicAuthentication(headers);
		
		List<User> user = service.retrieveUser(credentials);
		
		System.out.println("User "+user.toString());
		
		imgservice.deleteImage(user);
		
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
