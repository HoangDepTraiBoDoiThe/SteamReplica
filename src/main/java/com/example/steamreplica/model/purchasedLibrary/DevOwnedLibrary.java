package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.game.Game;
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
public class DevOwnedLibrary extends BaseCacheableModel {
    public DevOwnedLibrary(User user) {
        super(user.getId());
        this.user = user;
    }
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @JsonBackReference
    @ManyToMany(mappedBy = "devOwners", fetch = FetchType.EAGER)
    private Set<Game> games = new HashSet<>();
}
