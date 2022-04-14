package com.example.webapp.Controller;

import com.example.webapp.Config.SecurityConfig;
import com.example.webapp.Model.Image;
import com.example.webapp.Model.User;
import com.example.webapp.Service.ImageService;
import com.example.webapp.Service.UserService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class ImageController {

    @Autowired
    ImageService imgservice;

    @Autowired
    UserService service;

    @Autowired
    SecurityConfig sconfig;

    @Autowired
    StatsDClient statsd;

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping(value = "/v1/users/self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Image>> getUserImage(@RequestHeader Map<String, String> headers) {

        statsd.incrementCounter("GET.UserPicture.counter");

        String[] credentials = sconfig.decodeBasicAuthentication(headers);

        List<User> user = service.retrieveUser(credentials);

        List<Image> img = imgservice.getUserImage(user);

        if (user.get(0).isIs_Verified()) {

            if (img == null || img.size() == 0) {

                logger.error("Profile Pic Not Found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile Pic Not Found");

            }
        } else {

            logger.error("User " + user.get(0).getUsername() + " not verified");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User " + user.get(0).getUsername() + " not verified");

        }
        return ResponseEntity.status(HttpStatus.OK).body(img);


    }

    @PostMapping(value = "/v1/users/self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Image> storeUserImage(@RequestParam("imagefile") MultipartFile imagefile, @RequestHeader Map<String, String> headers) {

        statsd.incrementCounter("POST.UserPicture.counter");

        System.out.println("headersheaders " + headers);

        String[] credentials = sconfig.decodeBasicAuthentication(headers);

        List<User> user = service.retrieveUser(credentials);
        Image img = null;
        if (user.get(0).isIs_Verified()) {
            img = imgservice.saveImage(imagefile, user);
        } else {
            logger.error("User " + user.get(0).getUsername() + " not verified");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User " + user.get(0).getUsername() + " not verified");

        }

        return ResponseEntity.status(HttpStatus.OK).body(img);

    }


    @DeleteMapping(value = "/v1/users/self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserImage(@RequestHeader Map<String, String> headers) {

        statsd.incrementCounter("DELETE.UserPicture.counter");

        String[] credentials = sconfig.decodeBasicAuthentication(headers);

        List<User> user = service.retrieveUser(credentials);
        if (user.get(0).isIs_Verified()) {
            imgservice.deleteImage(user);
        } else {
            logger.error("User " + user.get(0).getUsername() + " not verified");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User " + user.get(0).getUsername() + " not verified");

        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
