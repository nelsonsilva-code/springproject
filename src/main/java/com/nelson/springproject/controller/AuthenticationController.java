package com.nelson.springproject.controller;


import com.nelson.springproject.dto.*;
import com.nelson.springproject.entity.User;
import com.nelson.springproject.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse jwtAuthResponse = authenticationService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    //This endpoint is only used for creating users for yourself, when starting.
    //Please ignore this endpoint or remove it all together, since it has no security in place
    //if removed, also check SpringSecurityConfig.java to remove the security bypass
    //if removed, delete the service and implementation as well
    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody User request) {
        String password = authenticationService.createUser(request.getUsername());
        return ResponseEntity.ok(password);
    }
}