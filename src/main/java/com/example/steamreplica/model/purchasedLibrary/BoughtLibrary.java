package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.userApplication.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BoughtLibrary extends BaseCacheableModel {
    public BoughtLibrary(User user) {
        super(user.getId());
        this.user = user;
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @OneToMany(mappedBy = "boughtLibrary", fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<Purchase> purchases = new HashSet<>();
}
