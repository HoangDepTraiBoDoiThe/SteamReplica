package com.example.steamreplica.dtos.response.user;

import com.example.steamreplica.dtos.response.BaseResponse;
import lombok.Data;

@Data
public class LoginResponse extends BaseResponse {
    private String token;

    public LoginResponse(String message) {
        super(message);
    }
}
