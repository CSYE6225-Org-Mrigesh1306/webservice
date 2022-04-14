package com.example.webapp.Service;

import com.example.webapp.Controller.UserController;
import com.example.webapp.DAO.UserRepository;
import com.example.webapp.Model.User;
import com.example.webapp.Validations.CustomValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

					logger.info("** User Exists **");
					userdetails.add(u);

				} else {

					logger.error("** Incorrect Username/password **");
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Username/password");

				}

			}

		}

		return userdetails;

	}

	public User createUser(User user) {

		logger.info("*** Create New User ***");

		boolean isValid = customValidator.fieldValidations(user);

		if (isValid) {

			List<User> existingUsers = userrepo.findAll();

			for (User u : existingUsers) {

				if (u.getUsername().equalsIgnoreCase(user.getUsername())) {

					logger.error("User Already exists. Try New username or password");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"User Already exists. Try New username or password");

				}
			}

			if (!customValidator.isEmailValid(user.getUsername())) {

				logger.error("Invalid Email Id Format");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email Id Format");

			}

			logger.info("*** User Crendentials are valid format ***");
			String bcryptpass = passwordEncoder.encode(user.getPassword());
			user.setPassword(bcryptpass);
			user.setAccount_created(java.time.Clock.systemUTC().instant().toString());
			user.setAccount_updated(java.time.Clock.systemUTC().instant().toString());
			Random rand = new Random();
			user.setId(rand.nextLong());

		} else {
			logger.error("All fields are mandatory");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are mandatory");

		}
		logger.info("*** New User has been Created ***");
		return userrepo.save(user);

	}

	public boolean updateUser(User user, String[] creds) {

		logger.info("**Update User Information**");

		boolean isValid = customValidator.fieldValidations(user);

		boolean isUpdated = false;
		boolean isPresent = false;

		if(user.isIs_Verified()) {

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
							logger.info("*** User Information has been Updated ***");
							break;
						} else {

							logger.error("Incorrect Username/password");
							throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Username/password");

						}

					}
				}
				if (!isPresent) {

					logger.error("User Does Not Exist. Please Check username or password");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"User Does Not Exist. Please Check username or password");
				}

			} else {
				logger.error("All fields are mandatory");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are mandatory");
			}
		}else {
			logger.error("User "+user.getUsername()+" not verified");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User "+user.getUsername()+" not verified");
		}

		return isUpdated;

	}
	public void updateUserToken(String email){

		//check if email has space
		if(email.indexOf(' ', 0)!=-1) {
			email.replace(' ', '+');
		}

		List<User> existingUsers = userrepo.findAll();

		for(User u : existingUsers){
			if(u.getUsername().equalsIgnoreCase(email)){
				logger.info("For username: "+u.getUsername()+" before :"+u.isIs_Verified());
				u.setIs_Verified(true);
				logger.info("For username: "+u.getUsername()+" after :"+u.isIs_Verified());
				userrepo.save(u);
				logger.info("For username: "+u.getUsername()+" updated in Table");
			}
		}
	}

}
