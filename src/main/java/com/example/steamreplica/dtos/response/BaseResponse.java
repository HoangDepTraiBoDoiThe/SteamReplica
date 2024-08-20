package com.example.steamreplica.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private long id;
    private String message;

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse(long id) {
        this.id = id;
    }
}
