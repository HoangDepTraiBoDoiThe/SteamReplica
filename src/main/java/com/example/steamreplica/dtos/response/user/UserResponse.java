package com.example.steamreplica.dtos.response.user;
import com.example.steamreplica.model.userApplication.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String userName;

    public UserResponse(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
    }
}
