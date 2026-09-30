package com.example.demo.dto;

public class AuthResponse {
    private String token;
    private String tokenType;
    private long expiresIn;



    
    public AuthResponse() {
    }
    
    public AuthResponse(String token, String tokenType, long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }
    public String getTokenType() {
        return tokenType;
    }
    public long getExpiresIn() {
        return expiresIn;
    }

    
}
