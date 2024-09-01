package com.example.steamreplica.model.userApplication;

import com.example.steamreplica.constants.UserStatus;
import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.purchasedLibrary.PublisherOwnedLibrary;
import com.example.steamreplica.util.StaticHelper;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User extends BaseCacheableModel {
    @NotBlank(message = "User name can not be empty")
    private String userName;

    @Lob
    private Blob userProfilePicture;

    private String phoneNumber;

    @Email
    @NotBlank(message = "Email can not be empty")
    private String email;

    private String Status = UserStatus.ONLINE.name();

    @Size(min = 12, message = "Password must be between 12 characters")
    @NotBlank(message = "Password name can not be empty")
    private String password;

    public User(String userName, String phoneNumber, String email, String password, Blob userProfileBlob) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userProfilePicture = userProfileBlob;
    }
    public User(String userName, String phoneNumber, String email, String password) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userProfilePicture = StaticHelper.convertToBlob("");
    }

    public AuthUserDetail toAuthUserDetail () {
        AuthUserDetail authUserDetail = new AuthUserDetail();
        authUserDetail.setId(super.getId());
        authUserDetail.setUsername(email);
        authUserDetail.setPassword(password);
        authUserDetail.setEmail(email);
        authUserDetail.setPhoneNumber(phoneNumber);
        authUserDetail.setRoles(roles.stream().map(ApplicationRole::getRoleName).collect(Collectors.toSet()));
        return authUserDetail;
    }
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "user_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_Id", referencedColumnName = "id"))
    private Set<ApplicationRole> roles = new HashSet<>();

    // Friends of this user
    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private Set<Friend> friends = new HashSet<>();

    // This user is friend to ...
    @OneToMany(mappedBy = "friend")
    @JsonBackReference
    private Set<Friend> friendOf = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BoughtLibrary boughtLibrary;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private DevOwnedLibrary devOwnedLibrary;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PublisherOwnedLibrary publisherOwnedLibrary;

    public void setBoughtLibrary(BoughtLibrary boughtLibrary) {
        this.boughtLibrary = boughtLibrary;
        if (boughtLibrary != null) this.boughtLibrary.setUser(this);
    }
}
