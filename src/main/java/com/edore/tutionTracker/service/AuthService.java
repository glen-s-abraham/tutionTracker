package com.edore.tutionTracker.service;

public interface AuthService {
    public String login(String userName, String password);
    public void logout(String accessToken,String refreshToken);
}
