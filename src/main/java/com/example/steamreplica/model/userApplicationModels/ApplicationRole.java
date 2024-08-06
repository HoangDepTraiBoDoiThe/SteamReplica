package com.example.steamreplica.model.userApplicationModels;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRole {
    @Id
    @GeneratedValue
    private long id;
    
    @NotBlank
    private String roleName;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
