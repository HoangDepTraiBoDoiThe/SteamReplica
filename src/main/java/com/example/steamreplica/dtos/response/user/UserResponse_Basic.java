package com.example.steamreplica.dtos.response.user;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse_Basic extends UserResponse_Minimal {
    private String userProfilePicture;
    private String phoneNumber;
    private String email;
    private String Status;

    public UserResponse_Basic(User user) {
        super(user);
        this.userProfilePicture = StaticHelper.convertBlobToString(user.getUserProfilePicture());
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        Status = user.getEmail();
    }
}
