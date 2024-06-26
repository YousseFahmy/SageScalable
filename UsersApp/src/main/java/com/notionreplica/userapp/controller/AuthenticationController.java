package com.notionreplica.userapp.controller;

import com.notionreplica.userapp.entities.*;
import com.notionreplica.userapp.services.UserService;
import com.notionreplica.userapp.services.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@ConfigurationProperties
@RequestMapping("user")
@PropertySource("classpath:application.yml")
public class AuthenticationController {

    @Autowired
    private UserService authenticationService;
    @Autowired
    private TokenService jwtService;

    @Value("${spring.redis.url}")
    private String redisURL;

    private Jedis jedis;

    @PostConstruct
    public void init() {
        jedis = new Jedis(redisURL);
    }

    Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws Exception{
        User newUser = authenticationService.SignUp(request);
        String jwtToken = jwtService.generateToken(newUser);
        jedis.set(newUser.getUsername(), jwtToken);
        jedis.expire(newUser.getUsername(), 60 * 60 * 24);
        AuthenticationResponse response = new AuthenticationResponse(jwtToken, newUser);
        log.info("User " + newUser.getUsername() + " was registered with ID " + newUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws Exception{
        User myUser = authenticationService.Login(request);
        String jwtToken = jwtService.generateToken(myUser);
        jedis.set(myUser.getUsername(), jwtToken);
        jedis.expire(myUser.getUsername(), 60 * 60 * 24);
        AuthenticationResponse response = new AuthenticationResponse(jwtToken, myUser);
        log.info("User " + myUser.getUsername() + " signed in");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getuser/{username}")
    public ResponseEntity<Map<String, Object>> getUser (@PathVariable("username") String username) throws Exception{
        User user = authenticationService.getUser(username);
        Map<String, Object> response = new HashMap<>();
        response.put("User Details",user);
        log.info("User " + user.getUsername() + " retrieved");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{userID}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteUser (@PathVariable("userID") String UID) throws Exception {
        String responseMessage = authenticationService.deleteUser(UID);
        Map<String, Object> response = new HashMap<>();
        response.put("Response",responseMessage);
        log.info("User with ID " + UID + " was deleted");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changepassword/{userID}")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable("userID") String UID, @RequestBody ChangePasswordRequest changePasswordBody) throws Exception {

        String responseMessage = authenticationService.changePassword(UID, changePasswordBody.getNewPassword(), changePasswordBody.getOldPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("Response", responseMessage);
        log.info("User with ID " + UID + " changed their password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changeemail/{userID}")
    public ResponseEntity<Map<String, Object>> changeEmail(@PathVariable("userID") String UID, @RequestBody Map<String,Object> requesttBody) throws Exception {
        String newEmail = (String) requesttBody.get("newUsername");

        String responseMessage = authenticationService.changeEmail(UID, newEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("Response", responseMessage);
        log.info("User with ID " + UID + " changed their email");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changeusername/{userID}")
    public ResponseEntity<Map<String, Object>> changeUsername(@PathVariable("userID") String UID, @RequestBody Map<String,Object> requesttBody) throws Exception {
        String newUsername = (String) requesttBody.get("newUsername");
        String responseMessage = authenticationService.changeUsername(UID, newUsername);
        Map<String, Object> response = new HashMap<>();
        response.put("Response", responseMessage);
        log.info("User with ID " + UID + " changed their username");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changename/{userID}")
    public ResponseEntity<Map<String, Object>> changeName(@PathVariable("userID") String UID, @RequestBody ChangeNameRequest newName) throws Exception {
        String responseMessage = authenticationService.changeName(UID, newName.getFirstName(), newName.getLastName());
        Map<String, Object> response = new HashMap<>();
        response.put("Response", responseMessage);
        log.info("User with ID " + UID + " changed their name");
        return ResponseEntity.ok(response);
    }

}
