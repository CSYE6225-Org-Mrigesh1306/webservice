package com.example.webapp.Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.example.webapp.Controller.UserController;
import com.example.webapp.DAO.UserRepository;
import com.example.webapp.Model.User;
import com.example.webapp.Validations.CustomValidations;

@Repository
public class UserService {

	@Autowired
	UserRepository userrepo;

	@Autowired
	CustomValidations customValidator;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public List<User> retrieveUser(String[] creds) {

		logger.info("**Retrieve User Information**");

		List<User> userdetails = new ArrayList<>();

		List<User> allusers = userrepo.findAll();

		for (User u : allusers) {

			if (customValidator.isUserExists(creds, u)) {

				boolean isPassMatch = passwordEncoder.matches(creds[1], u.getPassword());
				
				if (isPassMatch) {
					
					userdetails.add(u);

				} else {

					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Username/password");

				}

			}

		}

		return userdetails;

	}

	public User createUser(User user) {

		logger.info("## Create New User ##");

		boolean isValid = customValidator.fieldValidations(user);

		if (isValid) {

			List<User> existingUsers = userrepo.findAll();

			for (User u : existingUsers) {

				if (u.getUsername().equalsIgnoreCase(user.getUsername())) {

					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"User Already exists. Try New username or password");

				}
			}

			if (!customValidator.isEmailValid(user.getUsername())) {

				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email Id Format");

			}

			String bcryptpass = passwordEncoder.encode(user.getPassword());
			user.setPassword(bcryptpass);
			user.setAccount_created(java.time.Clock.systemUTC().instant().toString());
			user.setAccount_updated(java.time.Clock.systemUTC().instant().toString());

		} else {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are mandatory");

		}

		return userrepo.save(user);

	}

	public boolean updateUser(User user, String[] creds) {

		logger.info("**Update User Information**");

		boolean isValid = customValidator.fieldValidations(user);

		boolean isUpdated = false;
		boolean isPresent = false;

		if (isValid) {

			List<User> allusers = userrepo.findAll();

			for (User u : allusers) {

				if (customValidator.isUserExists(creds, u)) {

					boolean isPassMatch = passwordEncoder.matches(creds[1], u.getPassword());

					if (isPassMatch) {
						u.setPassword(passwordEncoder.encode(user.getPassword()));
						u.setFirst_name(user.getFirst_name());
						u.setLast_name(user.getLast_name());
						u.setAccount_updated(java.time.Clock.systemUTC().instant().toString());

						userrepo.save(u);
						isUpdated = true;
						isPresent = true;
						break;
					} else {

						throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Username/password");

					}

				}
			}
			if (!isPresent) {

				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"User Does Not Exist. Please Check username or password");
			}

		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are mandatory");
		}

		return isUpdated;

	}

}
