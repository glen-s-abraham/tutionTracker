package com.edore.tutionTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edore.tutionTracker.dto.UserDto;
import com.edore.tutionTracker.service.KeycloakAdminService;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    KeycloakAdminService keycloakAdminService;

    @GetMapping("/dashboard")
    public String getMethodName() {
        return "Welcome to admin dashboard";
    }

    @GetMapping("/user")
    public ResponseEntity<?> getusers() {
        try {
            List<UserRepresentation> users = keycloakAdminService.getAllUsers();
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users", "details", e.getMessage()));
        }

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            UserRepresentation user = keycloakAdminService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user", "details", e.getMessage()));
        }

    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDetails) {
        try {
            return ResponseEntity.ok(keycloakAdminService.createUser(
                    userDetails));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user", "details", e.getMessage()));
        }

    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserDto userDetails) {
        try {
            keycloakAdminService.updateUser(id, userDetails);
            return ResponseEntity.ok("Updated User with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user", "details", e.getMessage()));
        }

    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            keycloakAdminService.deleteUser(id);
            return ResponseEntity.ok("Deleted User with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user", "details", e.getMessage()));
        }

    }

}
