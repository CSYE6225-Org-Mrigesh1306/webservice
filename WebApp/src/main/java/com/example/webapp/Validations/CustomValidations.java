package com.example.webapp.Validations;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.webapp.Model.User;

@Component
public class CustomValidations {

	private static final Logger logger = LoggerFactory.getLogger(CustomValidations.class);

	public boolean isEmailValid(String email) {
		
		logger.info("***Check If Valid Email Format***");
		
		String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

		return Pattern.compile(regexPattern).matcher(email).matches();
	}

	public boolean isUserExists(String[] creds, User user) {
		
		logger.info("***Check If User Exists***");

		boolean isExists = false;

		if (user.getUsername().equalsIgnoreCase(creds[0])) {
			
			isExists = true;
		
		}
		
		return isExists;
	}
	

}
