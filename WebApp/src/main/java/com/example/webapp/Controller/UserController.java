package com.example.webapp.Controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.example.webapp.Config.SecurityConfig;
import com.example.webapp.Model.User;
import com.example.webapp.Service.EmailSNSService;
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

import java.math.BigDecimal;
import java.time.Instant;
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

    @Autowired
    EmailSNSService snsService;

    private DynamoDB dynamoDB;

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

        snsService.postToTopic(newUser.getUsername(), "POST");

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping(value = "/v1/users/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestHeader Map<String, String> headers) {

        statsd.incrementCounter("PUT.UserDetails.counter");

        String[] credentials = sconfig.decodeBasicAuthentication(headers);

        boolean isUpdated = service.updateUser(user, credentials);

        if (!isUpdated) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Details Not Updated");
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    //verifyUserEmail?email=user@example.com&token=token
    @GetMapping("/verifyUserEmail")
    public ResponseEntity<String> verifedUserUpdate(@RequestParam("email") String email,
                                                    @RequestParam("token") String token) {
        logger.info("*****/verifyUserEmail***");
        logger.info("email "+email);
        String result = "not verfied get";
        logger.info("not verfied get");
        try {
            //System.out.println("in post");
            //check if token is still valid in EmailID_Data

            // confirm dynamoDB table exists
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
            dynamoDB = new DynamoDB(client);
            logger.info("Get /verifyUserEmail");
            Table userEmailsTable = dynamoDB.getTable("UsernameTokenTable");
            if (userEmailsTable == null) {
                logger.error("Table 'UsernameTokenTable' is not in dynamoDB.");
                return new ResponseEntity<>("Unable to verify User!",HttpStatus.BAD_REQUEST);
            }

            if (email.indexOf(" ", 0) != -1) {
                email = email.replace(" ", "+");
            }
            Item item = userEmailsTable.getItem("emailID", email);
            logger.info("item= " + item);
            if (item == null) {
                result = "token expired !!!";
                logger.error("Token Expired");
                return new ResponseEntity<>("Unique Link has Expired",HttpStatus.BAD_REQUEST);
            } else {
                BigDecimal tokentime = (BigDecimal) item.get("TimeToLive");
                logger.info("item= " + item);
                long now = Instant.now().getEpochSecond(); // unix time
                long timereminsa = now - tokentime.longValue(); // 2 mins in sec
                logger.info("tokentime: " + tokentime);
                logger.info("now: " + now);
                logger.info("remins: " + timereminsa);
                if (timereminsa > 0) {
                    result = "token has expired";
                    logger.error("Token Expired");
                    return new ResponseEntity<>("Unique Link has Expired",HttpStatus.BAD_REQUEST);
                } else {
                    result = "verified successfully!!!";
                    logger.info("verified successfully!!!");
                    service.updateUserToken(email);
                    return new ResponseEntity<>("Congratulations!!!. You have been verified.",HttpStatus.OK);
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
