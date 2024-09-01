package com.example.steamreplica.model.userApplication;

import com.example.steamreplica.model.BaseCacheableModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRole extends BaseCacheableModel {
    @NotBlank
    private String roleName;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    public ApplicationRole(String roleName) {
        this.roleName = roleName;
    }
}
