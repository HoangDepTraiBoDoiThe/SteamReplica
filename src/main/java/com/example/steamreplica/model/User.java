package com.example.steamreplica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private long id;
    
    private String userName;
    private Blob userProfilePicture;
    private String phoneNumber;
    private String Email;
    private String Status;
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "user_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_Id", referencedColumnName = "id"))
    private Set<ApplicationRole> roles = new HashSet<>();
}
