package com.example.steamreplica.dtos.response.user;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.userApplication.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse_Minimal extends BaseResponse {
    private String userName;
    private String status;

    public UserResponse_Minimal(User user) {
        super(user.getId());
        this.userName = user.getUserName();
        this.status = user.getStatus();
    }
}
