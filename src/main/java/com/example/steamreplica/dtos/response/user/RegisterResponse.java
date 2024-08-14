package com.example.steamreplica.dtos.response.user;

import com.example.steamreplica.dtos.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class RegisterResponse extends BaseResponse {
    public RegisterResponse(String message) {
        super(message);
    }
}
