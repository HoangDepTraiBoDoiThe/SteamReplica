package com.example.steamreplica.dtos.request;

import com.example.steamreplica.model.userApplication.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String userName;
    private String phoneNumber;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(this.getUserName(), this.getPhoneNumber(), this.getEmail(), passwordEncoder.encode(this.getPassword()));
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof RegisterRequest)) return false;
        final RegisterRequest other = (RegisterRequest) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$username = this.getUserName();
        final Object other$username = other.getUserName();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof RegisterRequest;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $username = this.getUserName();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        return result;
    }

    public String toString() {
        return "RegisterRequest(email=" + this.getEmail() + ", password=" + this.getPassword() + ", username=" + this.getUserName() + ")";
    }
}
