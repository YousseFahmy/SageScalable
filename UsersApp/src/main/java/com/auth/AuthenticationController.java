package com.auth;

import com.config.JwtService;
import com.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@ConfigurationProperties
@RequestMapping("user")
public class AuthenticationController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    @Value("${spring.redis.url}")
//    private String connectionURL;

    Jedis jedis = new Jedis("redis://default:Tja6txFnJAqKglUil3ubHKEnPcghOmHj@redis-19053.c135.eu-central-1-1.ec2.redns.redis-cloud.com:19053");
    Connection connection = jedis.getConnection();


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        AuthenticationSignUp authenticationSignUp = new AuthenticationSignUp(repository, passwordEncoder, jwtService, request);
        AuthenticationResponse signUpResponse = authenticationSignUp.execute();
        jedis.set(request.getUsername(), signUpResponse.getToken());
        jedis.expire(request.getUsername(), 60*60*24);
        return ResponseEntity.ok(signUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        AuthenticationLogin authenticationLogin = new AuthenticationLogin(authenticationManager, repository, jwtService, request);
        AuthenticationResponse loginResponse = authenticationLogin.execute();
        jedis.set(request.getUsername(), loginResponse.getToken());
        jedis.expire(request.getUsername(), 60*60*24);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/delete/{userID}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteUser (@PathVariable("userID") String UID){
        AuthenticationDeleteUser authenticationDeleteUser= new AuthenticationDeleteUser(UID, repository);
        Map<String, Object> response = new HashMap<>();
        String result = authenticationDeleteUser.execute();
        response.put("message",result);
        log.info(result);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/changepassword/{userID}")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable("userID") String UID, @RequestBody ChangePasswordBody changePasswordBody){
        AuthenticationChangePassword authenticationChangePassword = new AuthenticationChangePassword(UID, repository, passwordEncoder, changePasswordBody.getNewPassword(), changePasswordBody.getOldPassword());
        Map<String, Object> response = new HashMap<>();
        String result = authenticationChangePassword.execute();
        response.put("message", result);
        log.info(result);
        return ResponseEntity.ok(response);
    }


}