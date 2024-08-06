package com.example.steamreplica.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRole {
    @Id
    @GeneratedValue
    private long id;
    
    private String roleName;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
