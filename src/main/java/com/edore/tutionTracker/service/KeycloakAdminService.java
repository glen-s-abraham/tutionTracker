package com.edore.tutionTracker.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edore.tutionTracker.config.KeycloakProperties;
import com.edore.tutionTracker.dto.UserDto;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakAdminService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final Keycloak keycloak;
    private final String realm;

    @Autowired
    public KeycloakAdminService(KeycloakProperties keycloakProperties) {
        this.realm = keycloakProperties.getRealm();

        logger.info("Initializing Keycloak client with server URL: {}", keycloakProperties.getAuthServerUrl());
        logger.info("Using clientId: {}", keycloakProperties.getAdminResource());

        this.keycloak = Keycloak.getInstance(
                keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getAdminRealm(),
                keycloakProperties.getAdminUsername(),
                keycloakProperties.getAdminPassword(),
                keycloakProperties.getAdminResource()
        );
    }

    public String createUser(UserDto userDetails) throws RuntimeException {
        UserRepresentation user = mapToUserRepresentation(userDetails);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDetails.password());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response res = keycloak.realm(realm).users().create(user);
        if (res.getStatus() == 201) {
            return "User created successfully";
        } else {
            String errorMessage = String.format("Error creating user: status=%d, response=%s", res.getStatus(),
                    res.readEntity(String.class));
            logger.error(errorMessage);
            throw new RuntimeException("Failed to create User. " + errorMessage);
        }
    }

    public List<UserRepresentation> getAllUsers() throws RuntimeException {
        try {
            return keycloak.realm(realm).users().list();
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching users", e);
        }
    }

    public UserRepresentation getUser(String id) throws RuntimeException {
        try {
            return keycloak.realm(realm).users().get(id).toRepresentation();
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error fetching user", e);
        }
    }

    public void updateUser(String id, UserDto userDetails) {
        try {
            UserRepresentation user = getUser(id);

            updateIfChanged(user::setEmail, user.getEmail(), userDetails.email());
            updateIfChanged(user::setUsername, user.getUsername(), userDetails.username());
            updateIfChanged(user::setFirstName, user.getFirstName(), userDetails.firstName());
            updateIfChanged(user::setLastName, user.getLastName(), userDetails.lastName());

            keycloak.realm(realm).users().get(id).update(user);
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    private <T> void updateIfChanged(Consumer<T> updater, T currentValue, T newValue) {
        if (!Objects.equals(currentValue, newValue)) {
            updater.accept(newValue);
        }
    }

    public void deleteUser(String id) {
        try {
            keycloak.realm(realm).users().get(id).remove();
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting user", e);
        }
    }

    private UserRepresentation mapToUserRepresentation(UserDto userDetails) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDetails.username());
        user.setEmail(userDetails.email());
        user.setFirstName(userDetails.firstName());
        user.setLastName(userDetails.lastName());
        return user;
    }
}
