package com.edore.tutionTracker.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.spi.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${keycloak.auth-server-url}")
    private String authServerurl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public String login(String username, String password) {

        String url = authServerurl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);
        body.add("scope", "openid");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body,
                headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();

    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", clientId);
            requestParams.add("client_secret", clientSecret);
            requestParams.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);

            String logoutUrl = authServerurl + "/realms/" + realm + "/protocol/openid-connect/logout";

            restTemplate.postForEntity(logoutUrl, request, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Error revoking access token");
        }
    }

}
