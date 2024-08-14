package com.example.steamreplica.dtos.response.user;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse_Full extends UserResponse {
    private String userProfilePicture;
    private String phoneNumber;
    private String email;
    private String Status;

    public UserResponse_Full(long id, String userName, String userProfilePicture, String phoneNumber, String email, String status) {
        super(id, userName);
        this.userProfilePicture = userProfilePicture;
        this.phoneNumber = phoneNumber;
        this.email = email;
        Status = status;
    }
}
