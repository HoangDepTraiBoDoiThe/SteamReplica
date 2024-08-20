package com.example.steamreplica.dtos.response.user;
import com.example.steamreplica.model.userApplication.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponse_Full extends UserResponse_Basic {
    public UserResponse_Full(User user) {
        super(user);
    }
}
