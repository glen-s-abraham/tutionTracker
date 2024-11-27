package com.edore.tutionTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edore.tutionTracker.dto.LoginDto;
import com.edore.tutionTracker.service.AuthService;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> postMethodName(@RequestBody LoginDto credentials) {
        try {
            String result = authService.login(credentials.username(), credentials.password());
            return ResponseEntity.ok(result); // Return the result with HTTP 200 OK
        } catch (Exception e) {
            // Handle errors gracefully
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

}
