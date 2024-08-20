package com.example.steamreplica.dtos.response.user;

import com.example.steamreplica.dtos.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResponse extends BaseResponse {
    private String token;

    public LoginResponse(long id, String message, String token) {
        super(id, message);
        this.token = token;
    }
}
