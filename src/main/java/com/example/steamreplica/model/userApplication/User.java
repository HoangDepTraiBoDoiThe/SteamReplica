package com.example.steamreplica.model.userApplication;

import com.example.steamreplica.constants.UserStatus;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "User name can not be empty")
    private String userName;

    @Lob
    @Size(max = 1048576 * 5, message = "Profile picture must be less than 5MB")
    private Blob userProfilePicture;

    private String phoneNumber;

    @Email
    @NotBlank(message = "Email can not be empty")
    private String email;

    private UserStatus Status;

    @Size(min = 12, max = 50, message = "Password must be between 12 and 50 characters")
    @NotBlank(message = "Password name can not be empty")
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "user_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_Id", referencedColumnName = "id"))
    private Set<ApplicationRole> roles = new HashSet<>();

    // Friends of this user
    @OneToMany(mappedBy = "user")
    private Set<Friend> friends = new HashSet<>();

    // This user is friend to ...
    @OneToMany(mappedBy = "friend")
    private Set<Friend> friendOf = new HashSet<>();
    
    @OneToOne(mappedBy = "user")
    private BoughtLibrary boughtLibrary;
}
