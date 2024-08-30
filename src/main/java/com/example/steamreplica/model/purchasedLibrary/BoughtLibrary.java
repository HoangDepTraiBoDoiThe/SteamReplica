package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.userApplication.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BoughtLibrary {
    @Id
    private long id;

    public BoughtLibrary(User user) {
        this.user = user;
        this.id = user.getId();
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @OneToMany(mappedBy = "boughtLibrary", fetch = FetchType.EAGER)
    private Set<Purchase> purchases = new HashSet<>();
}
