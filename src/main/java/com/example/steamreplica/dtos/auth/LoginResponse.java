package com.example.steamreplica.dtos.auth;

import lombok.Data;

@Data
public class LoginResponse extends BaseResponse {
    private String token;

    public LoginResponse(String message) {
        super(message);
    }
}
