package com.example.steamreplica.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse extends BaseResponse {
    public RegisterResponse(String message) {
        super(message);
    }
}
