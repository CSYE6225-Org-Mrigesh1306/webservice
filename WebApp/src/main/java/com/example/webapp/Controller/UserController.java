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

    @GetMapping("/verifyUserEmail")
    public ResponseEntity<String> verifedUserUpdate(@RequestParam("email") String email,
                                                    @RequestParam("token") String token) {

        String result = "User Not Verified";
        logger.info("User Not Verified");
        try {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
            dynamoDB = new DynamoDB(client);
            Table userEmailsTable = dynamoDB.getTable("UsernameTokenTable");
            if (userEmailsTable == null) {
                logger.error("Table 'UsernameTokenTable' is not in dynamoDB.");
                return new ResponseEntity<>("Unable to verify User!", HttpStatus.BAD_REQUEST);
            }

            if (email.indexOf(" ", 0) != -1) {
                email = email.replace(" ", "+");
            }
            Item item = userEmailsTable.getItem("emailID", email);
            logger.info("item= " + item);
            if (item == null) {
                result = "TTL expired !!!";
                logger.error("TTL Expired");
                return new ResponseEntity<>("Unique Link has Expired", HttpStatus.BAD_REQUEST);
            } else {
                BigDecimal tokentime = (BigDecimal) item.get("TimeToLive");
                logger.info("item= " + item);
                long current = Instant.now().getEpochSecond(); // unix time
                long timeleft = current - tokentime.longValue(); // 2 mins in sec
                logger.info("Original Token Time: " + tokentime);
                logger.info("Current Time: " + current);
                logger.info("Time Remaining: " + timeleft);
                if (timeleft > 0) {
                    result = "token has expired";
                    logger.error("Token Expired");
                    return new ResponseEntity<>("Unique Link has Expired", HttpStatus.BAD_REQUEST);
                } else {
                    result = "User has been verified";
                    logger.info("verified successfully!!!");
                    service.updateUserToken(email);
                    return new ResponseEntity<>("Congratulations!!! "+email+" You have been verified.", HttpStatus.OK);
                }

            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
