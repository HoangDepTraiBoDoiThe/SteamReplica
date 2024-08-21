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
    @GeneratedValue
    private long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @OneToMany(mappedBy = "boughtLibrary")
    private Set<Purchases> purchases = new HashSet<>();

    public BoughtLibrary(User user) {
        this.user = user;
    }
}
